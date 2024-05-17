-- 2024-5-17
alter table WF_NODE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_DEFINE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_STAGE add EXPIRE_CALL_API varchar(32);

alter table WF_NODE_INSTANCE add deadline_time DATETIME;
alter table WF_NODE_INSTANCE add pause_time DATETIME;

alter table WF_FLOW_INSTANCE add IS_TIMER varchar(1);
alter table WF_FLOW_INSTANCE add deadline_time DATETIME;
alter table WF_FLOW_INSTANCE add pause_time DATETIME;

alter table WF_STAGE_INSTANCE add IS_TIMER varchar(1);
alter table WF_STAGE_INSTANCE add deadline_time DATETIME;
alter table WF_STAGE_INSTANCE add pause_time DATETIME;

-- 添加现有数据转换

alter table WF_NODE_INSTANCE drop column PROMISE_TIME;
alter table WF_NODE_INSTANCE drop column TIME_LIMIT;

alter table WF_STAGE_INSTANCE drop column PROMISE_TIME;
alter table WF_STAGE_INSTANCE drop column TIME_LIMIT;

alter table WF_FLOW_INSTANCE drop column PROMISE_TIME;
alter table WF_FLOW_INSTANCE drop column TIME_LIMIT;