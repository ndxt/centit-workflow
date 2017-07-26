create  procedure consumeLifeTime() 
begin
 DECLARE nC int;
DECLARE consumeTime int DEFAULT 5;

-- 需要定义接收游标数据的变量 
  DECLARE v_wfinstid varCHAR(64);
 -- 遍历数据结束标志
  DECLARE done INT DEFAULT 0;
  -- 游标
  DECLARE r_f CURSOR FOR select wfinstid from  Wf_Flow_Instance where inststate = 'N' and isTimer='T';

  -- 将结束标志绑定到游标
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;  
  -- 打开游标
  OPEN r_f;

  -- 开始循环
REPEAT  
    -- 提取游标里的数据，这里只有一个，多个的话也一样；
    FETCH r_f INTO v_wfinstid;

IF NOT done THEN  
    -- 这里做你想做的循环的事件

        update  Wf_Node_Instance set timelimit = timelimit - consumeTime
                where wfinstid= v_wfinstid and nodeState in('N','W') and istimer='T';

        update WF_STAGE_INSTANCE s set s.timelimit = s.timelimit - consumeTime
        where  s.wfinstid = v_wfinstid and s.stagecode in
               (select n.stagecode
                from Wf_Node_Instance i join wf_node n on (i.nodeid=n.nodeid)
                where i.wfinstid= v_wfinstid and i.nodeState in('N','W') and i.istimer='T'
                      and n.isaccounttime='T' and  n.istrunkline ='T');


        select count(1) into nC
          from Wf_Node_Instance i join wf_node n on (i.nodeid=n.nodeid)
          where i.wfinstid= v_wfinstid and i.nodeState in('N','W') and i.istimer='T'
                and n.isaccounttime='T' and  n.istrunkline ='T';

        if nC>0 then
          update Wf_Flow_Instance set timelimit = timelimit - consumeTime  where wfinstid= v_wfinstid ;
        end if;
END IF;  
 UNTIL done END REPEAT;  
  -- 关闭游标
  CLOSE r_f;
  end;