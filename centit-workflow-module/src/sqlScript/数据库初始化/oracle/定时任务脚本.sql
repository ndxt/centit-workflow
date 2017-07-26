-- oracle 10g

-- 创建工作流定时任务
begin
DBMS_SCHEDULER.CREATE_JOB (JOB_NAME => 'WF_CONSUME_LIFE',
                  JOB_TYPE => 'STORED_PROCEDURE',
                  JOB_ACTION => 'WF_TIMER.CONSUME5MINSLIFETIME',
                  START_DATE => TRUNC(SYSDATE),
                  REPEAT_INTERVAL => 'FREQ=Minutely;Interval=5;ByHour=08, 09, 10, 11, 12, 13, 14, 15, 16, 17, 18',
                  END_DATE => SYSTIMESTAMP+3000,
                  ENABLED => TRUE,
                  AUTO_DROP => TRUE,
                  COMMENTS => '每天早上8点到晚上18点，没5分钟运行一次 ');
end;
/

begin
  DBMS_SCHEDULER.DROP_JOB (JOB_NAME => 'WF_CONSUME_LIFE');
end;
/


/**

select * from user_scheduler_job_log where job_name = 'WF_CONSUME_LIFE';

**/