create or replace package wf_timer is

  -- Author  : CODEFAN
  -- Created : 2015/7/16 14:29:23
  -- Purpose : 工作流定时器，用户计算流程运行时间和对应的时间预报警

  procedure consumeLifeTime(consumeTime in number);

  procedure checkTimeWarning;

  procedure checkTaskWarning;
  
  procedure consume5MinsLifeTime;

end wf_timer;
/
create or replace package body wf_timer is

  procedure consumeLifeTime(consumeTime in number) as
    nC number(5);
  begin
    for r_f in (select * from  Wf_Flow_Instance where inststate = 'N' and isTimer='T') loop   
        update  Wf_Node_Instance set timelimit = timelimit - consumeTime 
                where wfinstid= r_f.wfinstid and nodeState in('N','W') and istimer='T';
       
        update WF_STAGE_INSTANCE s set s.timelimit = s.timelimit - consumeTime 
        where  s.wfinstid = r_f.wfinstid and s.stagecode in 
               (select n.stagecode 
                from Wf_Node_Instance i join wf_node n on (i.nodeid=n.nodeid)  
                where i.wfinstid= r_f.wfinstid and i.nodeState in('N','W') and i.istimer='T'
                      and n.isaccounttime='T' and  n.istrunkline ='T');                            
                
        select count(1) into nC 
          from Wf_Node_Instance i join wf_node n on (i.nodeid=n.nodeid)
          where i.wfinstid= r_f.wfinstid and i.nodeState in('N','W') and i.istimer='T'
                and n.isaccounttime='T' and  n.istrunkline ='T';                        
          
        if nC>0 then
          update Wf_Flow_Instance set timelimit = timelimit - consumeTime  where wfinstid= r_f.wfinstid ;
        end if;               
    end loop;
  end consumeLifeTime;

  procedure checkTimeWarning
    as
    begin
      -- 流程超时报警  OBJTYPE = F 报警类别 WARNINGTYPE = A  超时报警 WARNINGCODE = ALTER_EXPIRED 
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, f.wfinstid,null,null,'F',
               'A','N','ALTER_EXPIRED',sysdate,'流程实例超时报警',
               '0',null,null 
      from Wf_Flow_Instance f 
      where f.inststate = 'N' and f.isTimer='T' and f.timelimit < 0
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid and w.OBJTYPE='F' and w.warningcode='ALTER_EXPIRED');  
      -- 流程超时预警                           
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, f.wfinstid,null,null,'F',
               'W','N','WARN_EXPIRED',sysdate,'流程实例超时预警',
               '0',null,null 
      from Wf_Flow_Instance f 
      where f.inststate = 'N' and f.isTimer='T' and f.timelimit > 0 and f.timelimit < 240
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid and w.OBJTYPE='F' and w.warningcode='WARN_EXPIRED');                           
      --节点超时报警
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, n.wfinstid,n.nodeinstid,null,'N',
               'A','N','ALTER_EXPIRED',sysdate,'流程节点实例超时报警',
               '0',null,null 
      from Wf_node_Instance n join Wf_Flow_Instance f on (n.wfinstid=f.wfinstid) 
           join wf_node nd on (n.nodeid = nd.nodeid) 
      where f.inststate = 'N' and n.nodestate='N' and n.isTimer='T' and n.timelimit < 0
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid and  w.nodeinstid = n.nodeinstid
                                and w.OBJTYPE='N' and w.warningcode='ALTER_EXPIRED');  
      
      --节点超时预警
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, n.wfinstid,n.nodeinstid,null,'N',
               'W','N','WARN_EXPIRED',sysdate,'流程节点实例超时预警',
               '0',null,null 
      from Wf_node_Instance n join Wf_Flow_Instance f on (n.wfinstid=f.wfinstid) 
           join wf_node nd on (n.nodeid = nd.nodeid) 
      where f.inststate = 'N' and n.nodestate='N' and n.isTimer='T' and  n.timelimit > 0 and n.timelimit < 240
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid and  w.nodeinstid = n.nodeinstid
                                and w.OBJTYPE='N' and w.warningcode='WARN_EXPIRED');  
      -- 阶段超时报警 
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, f.wfinstid,null,null,'P',
               'A','N','ALTER_EXPIRED',sysdate,'流程阶段超时报警',
               '0',null,null 
      from WF_STAGE_INSTANCE s join Wf_Flow_Instance f on (s.wfinstid=f.wfinstid) 
      where s.stageBegin ='1' and f.inststate = 'N' and s.timelimit < 0
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid  and w.OBJTYPE='P' and w.warningcode='ALTER_EXPIRED');     
       
      
      -- 阶段超时预警
      insert into  WF_RUNTIME_WARNING (WARNINGID ,WFINSTID , NODEINSTID , FLOWSTAGE ,OBJTYPE,
                    WARNINGTYPE ,WARNINGSTATE,WARNINGCODE, WARNINGTIME,WARNINGIDMSG ,
                    NOTICESTATE ,SENDMSGTIME , SENDUSERS)
      select S_WARNING_NO.Nextval, f.wfinstid,null,null,'P',
               'W','N','WARN_EXPIRED',sysdate,'流程阶段超时预警',
               '0',null,null 
      from WF_STAGE_INSTANCE s join Wf_Flow_Instance f on (s.wfinstid=f.wfinstid) 
      where s.stageBegin ='1' and f.inststate = 'N' and s.timelimit > 0 and s.timelimit < 240
            and not exists(select w.* 
                           from WF_RUNTIME_WARNING w 
                           where w.wfinstid = f.wfinstid  and w.OBJTYPE='P' and w.warningcode='WARN_EXPIRED');     
       
                              
    end checkTimeWarning;

  procedure checkTaskWarning
    as
    begin
      null;
    end checkTaskWarning;

    procedure consume5MinsLifeTime
     as
    begin
      consumeLifeTime(5);
    end consume5MinsLifeTime;
begin
  null;
end wf_timer;
/
