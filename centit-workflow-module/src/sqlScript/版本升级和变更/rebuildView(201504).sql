---- 待办视图
create or replace view V_INNER_USER_TASK_LIST as
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, nvl(a.UnitCode,nvl(w.UnitCode,'0000000')) as UnitCode, 
        a.usercode,c.ROLETYPE,c.ROLECODE,'一般任务' as AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.MethodName,
          d.OptUrl,d.optMethod,c.OptParam ,d.OptDesc,a.CREATETIME,a.PromiseTime,a.TIMELIMIT,
          c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
           join WF_NODE c on (a.NODEID=c.NODEID)
           join V_OPTDEF d on (c.OPTCODE=d.OPTCODE)
where /*c.NODETYPE<>'R' and --路由节点不会创建时实例*/ 
    a.NODESTATE='N' and w.INSTSTATE='N' and a.TASKASSIGNED='S'
union all
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, nvl(a.UnitCode,nvl(w.UnitCode,'0000000')) as UnitCode, 
        b.usercode,b.ROLETYPE,b.ROLECODE,b.AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.MethodName,
          d.OptUrl,d.optMethod,c.OptParam ,d.OptDesc,a.CREATETIME,a.PromiseTime,a.TIMELIMIT,
          c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
           join WF_ACTION_TASK b on (a.NODEINSTID=b.NODEINSTID)
           join WF_NODE c on (a.NODEID=c.NODEID)
           join V_OPTDEF d on (c.OPTCODE=d.OPTCODE)
where a.NODESTATE='N' and w.INSTSTATE='N' and a.TASKASSIGNED='T'
    and b.ISVALID='T' and  b.TASKSTATE='A' and (b.EXPIRETIME is null or b.EXPIRETIME>sysdate)
union all
select  a.WFINSTID,w.WFCODE,w.version,w.WfOptName,w.wfOptTag,a.nodeinstid, b.UnitCode ,
         b.usercode,c.ROLETYPE,c.ROLECODE, '系统指定' as AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.MethodName,
          d.OptUrl,d.optMethod,c.OptParam,d.OptDesc,a.CREATETIME,a.PromiseTime,a.timelimit,
           c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
       join WF_NODE c on (a.NODEID=c.NODEID)
       join V_OPTDEF d on (c.OPTCODE=d.OPTCODE) , F_USERUNIT b
where a.NODESTATE='N' and w.INSTSTATE='N'  and a.TASKASSIGNED='D' and
        (a.UNITCODE is null or a.UNITCODE=b.UNITCODE) and
       (   (c.ROLETYPE='gw' and c.ROLECODE=b.UserStation) or
           (c.ROLETYPE='xz' and c.ROLECODE=b.UserRank) );
  

create or replace view V_USER_TASK_LIST as
 select rownum as taskid,t.WFINSTID,t.WFCODE,t.version, t.WFOPTNAME,t.WFOPTTAG,t.NODEINSTID,t.UNITCODE,t.USERCODE,
       t.ROLETYPE,t.ROLECODE,t.AUTHDESC,t.nodecode, t.NODENAME,t.NODETYPE,t.NODEOPTTYPE,t.OPTID,t.OPTNAME,
       t.METHODNAME,t.OPTURL,t.OPTMETHOD,t.OPTPARAM,t.OPTDESC,t.CREATETIME,t.PROMISETIME,
       t.TIMELIMIT,t.OPTCODE,t.EXPIREOPT,t.STAGECODE,t.GRANTOR,t.LASTUPDATEUSER,t.LASTUPDATETIME ,t.inststate
from
   (select a.WFINSTID,a.WFCODE,a.version, a.WfOptName, a.wfOptTag, a.nodeinstid, a.UnitCode, a.usercode, a.ROLETYPE, a.ROLECODE,
     a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
      a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit,  a.OPTCODE, a.ExpireOpt, a.STAGECODE, 
      null as GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
  from V_INNER_USER_TASK_LIST a 
  union select a.WFINSTID,a.WFCODE,a.version, a.WfOptName, a.wfOptTag, a.nodeinstid, a.UnitCode, b.grantee as usercode, a.ROLETYPE, a.ROLECODE, 
    a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod, 
    a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit, a.OPTCODE, a.ExpireOpt, a.STAGECODE, 
    b.GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
    from V_INNER_USER_TASK_LIST a, WF_ROLE_RELEGATE b 
    where b.IsValid = 'T' and b.RELEGATETIME <= sysdate and 
          ( b.EXPIRETIME is null or b.EXPIRETIME >= sysdate) and 
          a.usercode = b.GRANTOR and ( b.UNITCODE is null or b.UNITCODE = a.UnitCode) 
          and ( b.ROLETYPE is null or ( b.ROLETYPE = a.ROLETYPE and ( b.ROLECODE is null or b.ROLECODE = a.ROLECODE) ) )) 
      t;
  
  
drop view F_V_LASTVERTIONFLOW;

create or replace view F_V_LASTVERSIONFLOW as
select
   WFCODE,
   VERSION,
   WFNAME,
   WFCLASS,
   WFSTATE,
   WFDESC,
   WFXMLDESC,
   TimeLimit,
   ExpireOpt,
   OPTID,
   ATPUBLISHDATE,
   WFPublishDate
from
   WF_FLOW_DEFINE
where
   (WFCODE,VERSION)
    IN (SELECT WF.WFCODE,MAX(WF.VERSION) AS VERSION FROM WF_FLOW_DEFINE WF GROUP BY WF.WFCODE);

comment on column F_V_LASTVERSIONFLOW.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column F_V_LASTVERSIONFLOW.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column F_V_LASTVERSIONFLOW.WFCLASS is
'暂未使用';

comment on column F_V_LASTVERSIONFLOW.WFSTATE is
'A 草稿 B 正常 C 过期 D 禁用 E 已发布  (A,E仅对0版本有效)';

comment on column F_V_LASTVERSIONFLOW.WFXMLDESC is
'图形定义界面生成的XML文件';

comment on column F_V_LASTVERSIONFLOW.TimeLimit is
'一小时计，8小时为一天，小数点后面为分钟，比如0.30表示30分钟';

comment on column F_V_LASTVERSIONFLOW.ExpireOpt is
'N：通知， O:不处理 ， S：挂起，E：终止';

comment on column F_V_LASTVERSIONFLOW.WFPublishDate is
'发布时间，也是启用时间';


create or replace view v_node_instdetail as
select f.wfoptname,f.wfopttag,n.nodename,n.roletype,n.rolecode,
d.OptName,d.MethodName,d.OptUrl,d.optMethod,n.optparam,
 t.NODEINSTID, t.WFINSTID, t.NODEID, t.CREATETIME, t.PREVNODEINSTID, t.FINISHTIME, t.NODESTATE,
 t.SUBWFINSTID, t.UNITCODE, t.TRANSID, t.TASKASSIGNED,
 t.RUNTOKEN, t.TIMELIMIT, t.LASTUPDATEUSER, t.LASTUPDATETIME, t.ISTIMER, t.PROMISETIME, t.FlowPhase
  from wf_node_instance t
join wf_node n on t.nodeid =  n.nodeid
join V_OPTDEF d on (n.OPTCODE=d.OPTCODE)
join wf_flow_instance f on t.wfinstid = f.wfinstid;

 comment on table v_node_instdetail is
'包括流程信息、操作信息的视图';

