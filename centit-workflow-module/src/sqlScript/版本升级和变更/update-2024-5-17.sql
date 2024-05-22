-- 2024-5-17
alter table WF_NODE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_DEFINE add EXPIRE_CALL_API varchar(32);
alter table WF_FLOW_STAGE add EXPIRE_CALL_API varchar(32);

ALTER TABLE wf_node MODIFY COLUMN Warning_Param varchar(200);
alter table WF_FLOW_DEFINE add Warning_Param varchar(200);
alter table WF_FLOW_STAGE add Warning_Param varchar(200);


ALTER TABLE WF_NODE_INSTANCE CHANGE IS_TIMER TIMER_STATUS varchar(1);
-- alter table WF_NODE_INSTANCE add TIMER_STATUS varchar(1);
-- update WF_NODE_INSTANCE set TIMER_STATUS = IS_TIMER;

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

ALTER TABLE WF_RUNTIME_WARNING CHANGE WARNINGID_MSG WARNING_MSG varchar(1);
-- alter table WF_RUNTIME_WARNING add WARNING_MSG varchar(500);
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
-- alter table WF_RUNTIME_WARNING drop column WARNINGID_MSG;
alter table WF_RUNTIME_WARNING drop column WARNING_CODE;
alter table WF_RUNTIME_WARNING drop column SEND_MSG_TIME;

-- locodedata.v_inner_user_task_list source

create or replace
algorithm = UNDEFINED view `v_inner_user_task_list` as
select
    `a`.`FLOW_INST_ID` as `FLOW_INST_ID`,
    `w`.`FLOW_CODE` as `FLOW_CODE`,
    `w`.`VERSION` as `version`,
    `w`.`FLOW_Opt_Name` as `FLOW_OPT_NAME`,
    `w`.`FLOW_Opt_Tag` as `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` as `NODE_INST_ID`,
    ifnull(`a`.`UNIT_CODE`, ifnull(`w`.`UNIT_CODE`, '0000000')) as `Unit_Code`,
    `a`.`USER_CODE` as `user_code`,
    `c`.`ROLE_TYPE` as `ROLE_TYPE`,
    `c`.`ROLE_CODE` as `ROLE_CODE`,
    '系统指定' as `AUTH_DESC`,
    `c`.`NODE_CODE` as `node_code`,
    `c`.`NODE_NAME` as `Node_Name`,
    `c`.`NODE_TYPE` as `Node_Type`,
    `c`.`OPT_TYPE` as `NODE_OPT_TYPE`,
    `c`.`OPT_PARAM` as `Opt_Param`,
    `a`.`CREATE_TIME` as `CREATE_TIME`,
    `a`.`deadline_time` as `node_Promise_Time`,
    `a`.`warning_time` as `node_warning_time`,
    `f`.`OS_ID` as `OS_ID`,
    `f`.`Opt_ID` as `OPT_ID`,
    `c`.`OPT_CODE` as `OPT_CODE`,
    `c`.`Expire_Opt` as `Expire_Opt`,
    `c`.`STAGE_CODE` as `STAGE_CODE`,
    `a`.`last_update_user` as `last_update_user`,
    `a`.`last_update_time` as `LAST_UPDATE_TIME`,
    `w`.`INST_STATE` as `inst_state`,
    `a`.`NODE_PARAM` as `NODE_PARAM`,
    `p`.`page_url` as `opt_url`,
    `w`.`USER_CODE` as `creator_code`,
    `w`.`deadline_time` as `FLOW_PROMISE_TIME`,
    `w`.`warning_time` as `FLOW_warning_time`
from
    ((((`wf_node_instance` `a`
        join `wf_flow_instance` `w` on
        ((`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`)))
        join `wf_node` `c` on
        ((`a`.`NODE_ID` = `c`.`NODE_ID`)))
        join `wf_flow_define` `f` on
        (((`w`.`FLOW_CODE` = `f`.`FLOW_CODE`)
            and (`w`.`VERSION` = `f`.`version`))))
        left join `wf_optpage` `p` on
        ((`c`.`OPT_CODE` = `p`.`opt_code`)))
where
    ((`c`.`NODE_TYPE` = 'C')
        and (`a`.`NODE_STATE` = 'N')
        and (`w`.`INST_STATE` = 'N')
        and (`a`.`TASK_ASSIGNED` = 'S'))
union all
select
    `a`.`FLOW_INST_ID` as `FLOW_INST_ID`,
    `w`.`FLOW_CODE` as `FLOW_CODE`,
    `w`.`VERSION` as `version`,
    `w`.`FLOW_Opt_Name` as `FLOW_OPT_NAME`,
    `w`.`FLOW_Opt_Tag` as `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` as `NODE_INST_ID`,
    ifnull(`a`.`UNIT_CODE`, ifnull(`w`.`UNIT_CODE`, '0000000')) as `UnitCode`,
    `b`.`USER_CODE` as `user_code`,
    `c`.`ROLE_TYPE` as `ROLE_TYPE`,
    `c`.`ROLE_CODE` as `ROLE_CODE`,
    `b`.`AUTH_DESC` as `AUTH_DESC`,
    `c`.`NODE_CODE` as `node_code`,
    `c`.`NODE_NAME` as `Node_Name`,
    `c`.`NODE_TYPE` as `Node_Type`,
    `c`.`OPT_TYPE` as `NODE_OPT_TYPE`,
    `c`.`OPT_PARAM` as `Opt_Param`,
    `a`.`CREATE_TIME` as `CREATE_TIME`,
    `a`.`deadline_time` as `node_Promise_Time`,
    `a`.`warning_time` as `node_warning_time`,
    `f`.`OS_ID` as `OS_ID`,
    `f`.`Opt_ID` as `OPT_ID`,
    `c`.`OPT_CODE` as `OPT_CODE`,
    `c`.`Expire_Opt` as `Expire_Opt`,
    `c`.`STAGE_CODE` as `STAGE_CODE`,
    `a`.`last_update_user` as `last_update_user`,
    `a`.`last_update_time` as `LAST_UPDATE_TIME`,
    `w`.`INST_STATE` as `inst_state`,
    `a`.`NODE_PARAM` as `NODE_PARAM`,
    `p`.`page_url` as `opt_url`,
    `w`.`USER_CODE` as `creator_code`,
    `w`.`deadline_time` as `FLOW_PROMISE_TIME`,
    `w`.`warning_time` as `FLOW_warning_time`
from
    (((((`wf_node_instance` `a`
        join `wf_flow_instance` `w` on
        ((`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`)))
        join `wf_action_task` `b` on
        ((`a`.`NODE_INST_ID` = `b`.`NODE_INST_ID`)))
        join `wf_node` `c` on
        ((`a`.`NODE_ID` = `c`.`NODE_ID`)))
        join `wf_flow_define` `f` on
        (((`w`.`FLOW_CODE` = `f`.`FLOW_CODE`)
            and (`w`.`VERSION` = `f`.`version`))))
        left join `wf_optpage` `p` on
        ((`c`.`OPT_CODE` = `p`.`opt_code`)))
where
    ((`c`.`NODE_TYPE` = 'C')
        and (`a`.`NODE_STATE` = 'N')
        and (`w`.`INST_STATE` = 'N')
        and (`a`.`TASK_ASSIGNED` = 'T')
        and (`b`.`TASK_STATE` = 'A'));

-- locodedata.v_user_task_list source

create or replace
algorithm = UNDEFINED view `v_user_task_list` as
select
    `a`.`FLOW_INST_ID` as `FLOW_INST_ID`,
    `a`.`FLOW_CODE` as `FLOW_CODE`,
    `a`.`version` as `version`,
    `a`.`FLOW_OPT_NAME` as `FLOW_OPT_NAME`,
    `a`.`FLOW_OPT_TAG` as `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` as `NODE_INST_ID`,
    `a`.`Unit_Code` as `Unit_Code`,
    `a`.`user_code` as `user_code`,
    `a`.`ROLE_TYPE` as `ROLE_TYPE`,
    `a`.`ROLE_CODE` as `ROLE_CODE`,
    `a`.`AUTH_DESC` as `AUTH_DESC`,
    `a`.`node_code` as `node_code`,
    `a`.`Node_Name` as `Node_Name`,
    `a`.`Node_Type` as `Node_Type`,
    `a`.`NODE_OPT_TYPE` as `NODE_OPT_TYPE`,
    `a`.`Opt_Param` as `Opt_Param`,
    `a`.`CREATE_TIME` as `CREATE_TIME`,
    `a`.`node_Promise_Time` as `NODE_promise_time`,
    `a`.`node_warning_time` as `NODE_warning_time`,
    `a`.`OPT_CODE` as `OPT_CODE`,
    `a`.`Expire_Opt` as `Expire_Opt`,
    `a`.`STAGE_CODE` as `STAGE_CODE`,
    null as `GRANTOR`,
    `a`.`last_update_user` as `last_update_user`,
    `a`.`LAST_UPDATE_TIME` as `LAST_UPDATE_TIME`,
    `a`.`inst_state` as `inst_state`,
    `a`.`opt_url` as `OPT_URL`,
    `a`.`NODE_PARAM` as `NODE_PARAM`,
    `a`.`OS_ID` as `os_id`,
    `a`.`OPT_ID` as `opt_id`,
    `a`.`creator_code` as `creator_code`,
    `a`.`FLOW_PROMISE_TIME` as `flow_promise_time`,
    `a`.`FLOW_warning_time` as `FLOW_warning_time`
from
    `v_inner_user_task_list` `a`
union
select
    `a`.`FLOW_INST_ID` as `FLOW_INST_ID`,
    `a`.`FLOW_CODE` as `FLOW_CODE`,
    `a`.`version` as `version`,
    `a`.`FLOW_OPT_NAME` as `FLOW_OPT_NAME`,
    `a`.`FLOW_OPT_TAG` as `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` as `NODE_INST_ID`,
    `a`.`Unit_Code` as `Unit_Code`,
    `a`.`user_code` as `user_code`,
    `a`.`ROLE_TYPE` as `ROLE_TYPE`,
    `a`.`ROLE_CODE` as `ROLE_CODE`,
    `a`.`AUTH_DESC` as `AUTH_DESC`,
    `a`.`node_code` as `node_code`,
    `a`.`Node_Name` as `Node_Name`,
    `a`.`Node_Type` as `Node_Type`,
    `a`.`NODE_OPT_TYPE` as `NODE_OPT_TYPE`,
    `a`.`Opt_Param` as `Opt_Param`,
    `a`.`CREATE_TIME` as `CREATE_TIME`,
    `a`.`node_Promise_Time` as `NODE_promise_time`,
    `a`.`node_warning_time` as `NODE_warning_time`,
    `a`.`OPT_CODE` as `OPT_CODE`,
    `a`.`Expire_Opt` as `Expire_Opt`,
    `a`.`STAGE_CODE` as `STAGE_CODE`,
    `b`.`GRANTOR` as `GRANTOR`,
    `a`.`last_update_user` as `last_update_user`,
    `a`.`LAST_UPDATE_TIME` as `last_update_time`,
    `a`.`inst_state` as `inst_state`,
    `a`.`opt_url` as `OPT_URL`,
    `a`.`NODE_PARAM` as `NODE_PARAM`,
    `a`.`OS_ID` as `os_id`,
    `a`.`OPT_ID` as `opt_id`,
    `a`.`creator_code` as `creator_code`,
    `a`.`FLOW_PROMISE_TIME` as `flow_promise_time`,
    `a`.`FLOW_warning_time` as `FLOW_warning_time`
from
    (`v_inner_user_task_list` `a`
        join `wf_role_relegate` `b` on
        ((`b`.`UNIT_CODE` = `a`.`Unit_Code`)))
where
    ((`b`.`IS_VALID` = 'T')
        and (`b`.`RELEGATE_TIME` <= now())
        and (`a`.`user_code` = `b`.`GRANTOR`)
        and ((`b`.`EXPIRE_TIME` is null)
            or (`b`.`EXPIRE_TIME` >= now()))
        and ((`b`.`UNIT_CODE` is null)
            or (`b`.`UNIT_CODE` = `a`.`Unit_Code`))
        and ((`b`.`ROLE_TYPE` is null)
            or (`b`.`ROLE_TYPE` = `a`.`ROLE_TYPE`))
        and ((`b`.`ROLE_CODE` is null)
            or (`b`.`ROLE_CODE` = `a`.`ROLE_CODE`)));
