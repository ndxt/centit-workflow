-- 2024-5-17
alter table WF_NODE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_DEFINE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_STAGE add EXPIRE_CALL_API varchar(32);

ALTER TABLE wf_node MODIFY COLUMN Warning_Param varchar(200);
alter table WF_FLOW_DEFINE add Warning_Param varchar(200);
alter table WF_FLOW_STAGE add Warning_Param varchar(200);

-- alter table WF_NODE_INSTANCE add TIMER_STATUS varchar(1);
ALTER TABLE WF_NODE_INSTANCE CHANGE IS_TIMER TIMER_STATUS varchar(1);

alter table WF_NODE_INSTANCE add warning_time DATETIME;
alter table WF_NODE_INSTANCE add deadline_time DATETIME;
alter table WF_NODE_INSTANCE add pause_time DATETIME;

alter table WF_FLOW_INSTANCE add TIMER_STATUS varchar(1);
alter table WF_FLOW_INSTANCE add warning_time DATETIME;
alter table WF_FLOW_INSTANCE add deadline_time DATETIME;
alter table WF_FLOW_INSTANCE add pause_time DATETIME;

alter table WF_STAGE_INSTANCE add TIMER_STATUS varchar(1);
alter table WF_STAGE_INSTANCE add warning_time DATETIME;
alter table WF_STAGE_INSTANCE add deadline_time DATETIME;
alter table WF_STAGE_INSTANCE add pause_time DATETIME;

alter table WF_RUNTIME_WARNING add WARNING_MSG varchar(500);
ALTER TABLE wf_runtime_warning MODIFY COLUMN SEND_USERS varchar(1000);

-- 添加现有数据转换

alter table wf_flow_stage drop column is_Account_Time;
alter table wf_flow_stage drop column Limit_Type;

-- alter table WF_NODE_INSTANCE drop column IS_TIMER;
alter table WF_NODE_INSTANCE drop column PROMISE_TIME;
alter table WF_NODE_INSTANCE drop column TIME_LIMIT;

alter table WF_STAGE_INSTANCE drop column PROMISE_TIME;
alter table WF_STAGE_INSTANCE drop column TIME_LIMIT;
alter table WF_STAGE_INSTANCE drop column stage_Begin;

alter table WF_FLOW_INSTANCE drop column PROMISE_TIME;
alter table WF_FLOW_INSTANCE drop column TIME_LIMIT;

alter table WF_RUNTIME_WARNING drop column NOTICE_STATE;
alter table WF_RUNTIME_WARNING drop column WARNING_STATE;
alter table WF_RUNTIME_WARNING drop column WARNINGID_MSG;
alter table WF_RUNTIME_WARNING drop column WARNING_CODE;
alter table WF_RUNTIME_WARNING drop column SEND_MSG_TIME;