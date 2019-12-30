-- ----------------------------
-- Table structure for wf_action_log
-- ----------------------------
DROP TABLE IF EXISTS `wf_action_log`;
CREATE TABLE `wf_action_log` (
  `ACTION_ID` varchar(32) ,
  `FLOW_INST_ID` varchar(32) ,
  `NODE_INST_ID` varchar(32) ,
  `ACTION_TYPE` varchar(2) ,
  `ACTION_TIME` datetime NOT NULL,
  `USER_CODE` varchar(8) ,
  `ROLE_TYPE` varchar(8) ,
  `ROLE_CODE` varchar(32) ,
  `GRANTOR` varchar(8) ,
  `LOG_DETAIL` varchar(500) ,
  PRIMARY KEY (`ACTION_ID`)
) ;

-- ----------------------------
-- Table structure for wf_action_task
-- ----------------------------
DROP TABLE IF EXISTS `wf_action_task`;
CREATE TABLE `wf_action_task` (
  `TASK_ID` varchar(32) ,
  `NODE_INST_ID` varchar(32) ,
  `ASSIGN_TIME` datetime NOT NULL,
  `USER_CODE` varchar(8) ,
  `TASK_STATE` char(1) ,
  `AUTH_DESC` varchar(255) ,
  PRIMARY KEY (`TASK_ID`)
) ;

-- ----------------------------
-- Table structure for wf_flow_define
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_define`;
CREATE TABLE `wf_flow_define` (
  `FLOW_CODE` varchar(32) ,
  `version` decimal(4,0) NOT NULL DEFAULT '0',
  `FLOW_NAME` varchar(120) ,
  `FLOW_CLASS` varchar(4) ,
  `FLOW_Publish_Date` datetime DEFAULT NULL,
  `FLOW_STATE` char(1) ,
  `FLOW_DESC` varchar(500) ,
  `FLOW_XML_DESC` text,
  `Time_Limit` varchar(20) ,
  `Expire_Opt` char(1) ,
  `Opt_ID` varchar(32) ,
  `AT_PUBLISH_DATE` datetime DEFAULT NULL,
  `OS_ID` varchar(32) ,
  PRIMARY KEY (`version`,`FLOW_CODE`)
) ;

-- ----------------------------
-- Table structure for wf_flow_instance
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_instance`;
CREATE TABLE `wf_flow_instance` (
  `FLOW_INST_ID` varchar(32) ,
  `VERSION` decimal(4,0) DEFAULT NULL,
  `FLOW_CODE` varchar(32) ,
  `FLOW_Opt_Name` varchar(800) ,
  `FLOW_Opt_Tag` varchar(200) ,
  `CREATE_TIME` datetime NOT NULL,
  `is_Timer` char(1) ,
  `promise_Time` decimal(10,0) DEFAULT NULL,
  `time_Limit` decimal(10,0) DEFAULT NULL,
  `last_update_user` varchar(8) ,
  `last_update_time` datetime DEFAULT NULL,
  `INST_STATE` char(1) ,
  `IS_SUB_INST` char(1) ,
  `PRE_INST_ID` varchar(32) ,
  `PRE_NODE_INST_ID` varchar(32) ,
  `UNIT_CODE` varchar(8) ,
  `USER_CODE` varchar(8) ,
  `FLOW_GROUP_ID` varchar(32) ,
  PRIMARY KEY (`FLOW_INST_ID`)
) ;

-- ----------------------------
-- Table structure for wf_flow_instance_group
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_instance_group`;
CREATE TABLE `wf_flow_instance_group` (
  `FLOW_GROUP_ID` varchar(32) ,
  `FLOW_GROUP_NAME` varchar(200) ,
  `FLOW_GROUP_DESC` varchar(1000) ,
  PRIMARY KEY (`FLOW_GROUP_ID`)
) ;

-- ----------------------------
-- Table structure for wf_flow_stage
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_stage`;
CREATE TABLE `wf_flow_stage` (
  `STAGE_ID` varchar(32) ,
  `VERSION` decimal(4,0) DEFAULT NULL,
  `FLOW_CODE` varchar(32) ,
  `STAGE_CODE` varchar(32) ,
  `STAGE_NAME` varchar(60) ,
  `is_Account_Time` char(1) ,
  `Limit_Type` char(1) ,
  `Time_Limit` varchar(20) ,
  `Expire_Opt` char(1) ,
  PRIMARY KEY (`STAGE_ID`)
) ;

-- ----------------------------
-- Table structure for wf_flow_team_role
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_team_role`;
CREATE TABLE `wf_flow_team_role` (
  `FLOW_TEAM_ROLE_ID` varchar(32) ,
  `FLOW_CODE` varchar(32) ,
  `ROLE_CODE` varchar(100) ,
  `ROLE_NAME` varchar(100) ,
  `TEAM_ROLE_ORDER` smallint(6) DEFAULT NULL,
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFY_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `VERSION` decimal(4,0) DEFAULT NULL,
  PRIMARY KEY (`FLOW_TEAM_ROLE_ID`)
) ;

-- ----------------------------
-- Table structure for wf_flow_variable
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_variable`;
CREATE TABLE `wf_flow_variable` (
  `FLOW_INST_ID` varchar(32) ,
  `Run_Token` varchar(20) ,
  `VAR_NAME` varchar(50) ,
  `VAR_VALUE` varchar(256) ,
  `Var_Type` char(1) ,
  PRIMARY KEY (`FLOW_INST_ID`,`Run_Token`,`VAR_NAME`)
) ;

-- ----------------------------
-- Table structure for wf_flow_variable_define
-- ----------------------------
DROP TABLE IF EXISTS `wf_flow_variable_define`;
CREATE TABLE `wf_flow_variable_define` (
  `FLOW_VARIABLE_ID` varchar(32) ,
  `FLOW_CODE` varchar(32) ,
  `VARIABLE_NAME` varchar(100) ,
  `VARIABLE_TYPE` varchar(100) ,
  `MODIFY_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `VERSION` decimal(4,0) DEFAULT NULL,
  `DEFAULT_VALUE` varchar(256) ,
  PRIMARY KEY (`FLOW_VARIABLE_ID`)
) ;

-- ----------------------------
-- Table structure for wf_inst_attention
-- ----------------------------
DROP TABLE IF EXISTS `wf_inst_attention`;
CREATE TABLE `wf_inst_attention` (
  `FLOW_INST_ID` varchar(32) ,
  `user_Code` varchar(8) ,
  `att_set_time` datetime DEFAULT NULL,
  `att_set_user` varchar(8) ,
  `att_set_Memo` varchar(255) ,
  PRIMARY KEY (`user_Code`,`FLOW_INST_ID`)
) ;

-- ----------------------------
-- Table structure for wf_node
-- ----------------------------
DROP TABLE IF EXISTS `wf_node`;
CREATE TABLE `wf_node` (
  `NODE_ID` varchar(32) ,
  `FLOW_CODE` varchar(32) ,
  `VERSION` decimal(4,0) DEFAULT NULL,
  `NODE_TYPE` varchar(1) ,
  `NODE_NAME` varchar(120) ,
  `OPT_TYPE` varchar(1) ,
  `OS_ID` varchar(32)  DEFAULT 'TEST',
  `OPT_ID` varchar(32) ,
  `OPT_CODE` varchar(64) ,
  `OPT_BEAN` varchar(100) ,
  `OPT_PARAM` varchar(100) ,
  `SUB_FLOW_CODE` varchar(32) ,
  `ROUTER_TYPE` varchar(1) ,
  `ROLE_TYPE` varchar(8) ,
  `ROLE_CODE` varchar(32) ,
  `UNIT_EXP` varchar(64) ,
  `POWER_EXP` varchar(512) ,
  `multi_Inst_Type` char(1) ,
  `multi_Inst_Param` varchar(512) ,
  `converge_Type` char(1) ,
  `converge_Param` varchar(64) ,
  `NODE_DESC` varchar(500) ,
  `is_Account_Time` char(1) ,
  `Limit_Type` char(1) ,
  `Time_Limit` varchar(20) ,
  `inherit_Type` char(1) ,
  `inherit_Node_Code` varchar(20) ,
  `Expire_Opt` char(1) ,
  `Warning_Rule` char(1) ,
  `Warning_Param` varchar(20) ,
  `is_Trunk_Line` char(1) ,
  `STAGE_CODE` varchar(32) ,
  `NODE_CODE` varchar(20) ,
  `RISK_INFO` varchar(4) ,
  PRIMARY KEY (`NODE_ID`)
) ;

-- ----------------------------
-- Table structure for wf_node_instance
-- ----------------------------
DROP TABLE IF EXISTS `wf_node_instance`;
CREATE TABLE `wf_node_instance` (
  `NODE_INST_ID` varchar(32) ,
  `FLOW_INST_ID` varchar(32) ,
  `NODE_ID` varchar(32) ,
  `CREATE_TIME` datetime DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `is_Timer` char(1) ,
  `promise_Time` decimal(10,0) DEFAULT NULL,
  `time_Limit` decimal(10,0) DEFAULT NULL,
  `PREV_NODE_INST_ID` varchar(32) ,
  `NODE_STATE` varchar(2) ,
  `SUB_FLOW_INST_ID` varchar(32) ,
  `UNIT_CODE` varchar(8) ,
  `STAGE_CODE` varchar(32) ,
  `ROLE_TYPE` varchar(8) ,
  `ROLE_CODE` varchar(32) ,
  `USER_CODE` varchar(8) ,
  `NODE_PARAM` varchar(128) ,
  `TASK_ASSIGNED` varchar(1)  DEFAULT 'F',
  `Run_Token` varchar(20) ,
  `GRANTOR` varchar(8) ,
  `last_update_user` varchar(8) ,
  `last_update_time` datetime DEFAULT NULL,
  `TRANS_PATH` varchar(256) ,
  PRIMARY KEY (`NODE_INST_ID`),
  KEY `nodeinst_index` (`FLOW_INST_ID`) USING BTREE
) ;

-- ----------------------------
-- Table structure for wf_optinfo
-- ----------------------------
DROP TABLE IF EXISTS `wf_optinfo`;
CREATE TABLE `wf_optinfo` (
  `OPT_ID` varchar(32)  not null,
  `OPT_NAME` varchar(100) not null,
  `OPT_URL` varchar(500) ,
  `OPT_VIEW_URL` varchar(500) ,
  `UPDATE_DATE` date DEFAULT CURRENT_TIMESTAMP,
  `MODEL_ID` varchar(64) ,
  `TITLE_TEMPLATE` varchar(500) ,
  `DEFAULT_FLOW` varchar(32),
  PRIMARY KEY (`OPT_ID`)
) ;

-- ----------------------------
-- Table structure for wf_optpage
-- ----------------------------
DROP TABLE IF EXISTS `wf_optpage`;
CREATE TABLE `wf_optpage` (
  `opt_code` varchar(32)  not null,
  `opt_id` varchar(32) not NULL ,
  `opt_name` varchar(128) not NULL,
  `opt_method` varchar(50) ,
  `update_date` date DEFAULT NULL,
  `page_url` varchar(500) ,
  `page_type` char(1),
  PRIMARY KEY (`opt_code`)
) ;

-- ----------------------------
-- Table structure for wf_organize
-- ----------------------------
DROP TABLE IF EXISTS `wf_organize`;
CREATE TABLE `wf_organize` (
  `FLOW_INST_ID` varchar(32) ,
  `UNIT_CODE` varchar(8) ,
  `ROLE_CODE` varchar(32) ,
  `UNIT_ORDER` decimal(4,0) DEFAULT NULL,
  `AUTH_DESC` varchar(255) ,
  `AUTH_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`FLOW_INST_ID`,`UNIT_CODE`,`ROLE_CODE`)
) ;

-- ----------------------------
-- Table structure for wf_role_formula
-- ----------------------------
DROP TABLE IF EXISTS `wf_role_formula`;
CREATE TABLE `wf_role_formula` (
  `FORMULA_CODE` varchar(64) ,
  `FORMULA_NAME` varchar(200) ,
  `ROLE_FORMULA` varchar(2000) ,
  `ROLE_LEVEL` varchar(20) ,
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`FORMULA_CODE`)
) ;

-- ----------------------------
-- Table structure for wf_role_relegate
-- ----------------------------
DROP TABLE IF EXISTS `wf_role_relegate`;
CREATE TABLE `wf_role_relegate` (
  `RELEGATE_NO` varchar(32) ,
  `GRANTOR` varchar(8) ,
  `GRANTEE` varchar(8) ,
  `IS_VALID` char(1)  DEFAULT 'T',
  `Recorder` varchar(8) ,
  `RELEGATE_TIME` datetime NOT NULL,
  `EXPIRE_TIME` datetime DEFAULT NULL,
  `UNIT_CODE` varchar(8) ,
  `ROLE_TYPE` varchar(8) ,
  `ROLE_CODE` varchar(32) ,
  `Record_Date` datetime DEFAULT CURRENT_TIMESTAMP,
  `grant_Desc` varchar(256) ,
  PRIMARY KEY (`RELEGATE_NO`)
) ;

-- ----------------------------
-- Table structure for wf_runtime_warning
-- ----------------------------
DROP TABLE IF EXISTS `wf_runtime_warning`;
CREATE TABLE `wf_runtime_warning` (
  `WARNING_ID` varchar(32) ,
  `FLOW_INST_ID` varchar(32) ,
  `NODE_INST_ID` varchar(32) ,
  `FLOW_STAGE` varchar(32) ,
  `OBJ_TYPE` char(1) ,
  `WARNING_TYPE` char(1) ,
  `WARNING_STATE` char(1)  DEFAULT 'N',
  `WARNING_CODE` varchar(16) ,
  `WARNING_TIME` datetime DEFAULT NULL,
  `WARNINGID_MSG` varchar(500) ,
  `NOTICE_STATE` char(1)  DEFAULT '0',
  `SEND_MSG_TIME` datetime DEFAULT NULL,
  `SEND_USERS` varchar(100) ,
  PRIMARY KEY (`WARNING_ID`)
) ;

-- ----------------------------
-- Table structure for wf_stage_instance
-- ----------------------------
DROP TABLE IF EXISTS `wf_stage_instance`;
CREATE TABLE `wf_stage_instance` (
  `FLOW_INST_ID` varchar(32) ,
  `STAGE_ID` varchar(32) ,
  `STAGE_CODE` varchar(32) ,
  `begin_Time` datetime DEFAULT NULL,
  `stage_Begin` char(1) ,
  `promise_Time` decimal(10,0) DEFAULT NULL,
  `time_Limit` decimal(10,0) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`FLOW_INST_ID`,`STAGE_ID`)
) ;

-- ----------------------------
-- Table structure for wf_team
-- ----------------------------
DROP TABLE IF EXISTS `wf_team`;
CREATE TABLE `wf_team` (
  `FLOW_INST_ID` varchar(32) ,
  `ROLE_CODE` varchar(32) ,
  `USER_CODE` varchar(8) ,
  `USER_ORDER` decimal(4,0) DEFAULT NULL,
  `AUTH_DESC` varchar(255) ,
  `AUTH_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`FLOW_INST_ID`,`USER_CODE`,`ROLE_CODE`)
) ;

-- ----------------------------
-- Table structure for wf_transition
-- ----------------------------
DROP TABLE IF EXISTS `wf_transition`;
CREATE TABLE `wf_transition` (
  `TRANS_ID` varchar(32) ,
  `VERSION` decimal(4,0) DEFAULT NULL,
  `FLOW_CODE` varchar(32) ,
  `TRANS_CLASS` varchar(4) ,
  `TRANS_NAME` varchar(120) ,
  `START_NODE_ID` varchar(32) ,
  `END_NODE_ID` varchar(32) ,
  `TRANS_CONDITION` varchar(500) ,
  `TRANS_DESC` varchar(500) ,
  `is_Account_Time` char(1) ,
  `Limit_Type` char(1) ,
  `Time_Limit` varchar(20) ,
  `can_ignore` char(1)  DEFAULT 'T',
  PRIMARY KEY (`TRANS_ID`)
) ;

-- ----------------------------
-- View structure for F_V_LASTVERSIONFLOW
-- ----------------------------
CREATE
VIEW `F_V_LASTVERSIONFLOW`AS
select `a`.`FLOW_CODE` AS `FLOW_CODE`,`b`.`version` AS `VERSION`,`a`.`FLOW_NAME` AS `FLOW_NAME`,`a`.`FLOW_CLASS` AS `FLOW_CLASS`,`b`.`FLOW_STATE` AS `FLOW_STATE`,`a`.`FLOW_DESC` AS `FLOW_DESC`,`a`.`FLOW_XML_DESC` AS `FLOW_XML_DESC`,`a`.`Time_Limit` AS `TIME_LIMIT`,`a`.`Expire_Opt` AS `EXPIRE_OPT`,`a`.`Opt_ID` AS `OPT_ID`,`a`.`OS_ID` AS `OS_ID`,`a`.`FLOW_Publish_Date` AS `FLOW_PUBLISH_DATE`,`a`.`AT_PUBLISH_DATE` AS `AT_PUBLISH_DATE` from (((select `wf_flow_define`.`FLOW_CODE` AS `FLOW_CODE`,max(`wf_flow_define`.`version`) AS `version` from `wf_flow_define` group by `wf_flow_define`.`FLOW_CODE`) as `lastversion` join `wf_flow_define` `a` on(((`a`.`FLOW_CODE` = `lastversion`.`FLOW_CODE`) and (`a`.`version` = 0)))) join `wf_flow_define` `b` on(((`lastversion`.`FLOW_CODE` = `b`.`FLOW_CODE`) and (`lastversion`.`version` = `b`.`version`)))) ;
-- ----------------------------
-- View structure for V_INNER_USER_TASK_LIST
-- ----------------------------
CREATE
VIEW `V_INNER_USER_TASK_LIST`AS
select `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,`w`.`FLOW_CODE` AS `FLOW_CODE`,`w`.`VERSION` AS `version`,`w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,`w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,`a`.`NODE_INST_ID` AS `NODE_INST_ID`,ifnull(`a`.`UNIT_CODE`,ifnull(`w`.`UNIT_CODE`,'0000000')) AS `Unit_Code`,`a`.`USER_CODE` AS `user_code`,`c`.`ROLE_TYPE` AS `ROLE_TYPE`,`c`.`ROLE_CODE` AS `ROLE_CODE`,'ϵͳָ��' AS `AUTH_DESC`,`c`.`NODE_CODE` AS `node_code`,`c`.`NODE_NAME` AS `Node_Name`,`c`.`NODE_TYPE` AS `Node_Type`,`c`.`OPT_TYPE` AS `NODE_OPT_TYPE`,`c`.`OPT_PARAM` AS `Opt_Param`,`a`.`CREATE_TIME` AS `CREATE_TIME`,`a`.`promise_Time` AS `Promise_Time`,`a`.`time_Limit` AS `TIME_LIMIT`,`c`.`OPT_CODE` AS `OPT_CODE`,`c`.`Expire_Opt` AS `Expire_Opt`,`c`.`STAGE_CODE` AS `STAGE_CODE`,`a`.`last_update_user` AS `last_update_user`,`a`.`last_update_time` AS `LAST_UPDATE_TIME`,`w`.`INST_STATE` AS `inst_state`,`c`.`OS_ID` AS `OS_ID`,`a`.`NODE_PARAM` AS `NODE_PARAM` from ((`wf_node_instance` `a` join `wf_flow_instance` `w` on((`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`))) join `wf_node` `c` on((`a`.`NODE_ID` = `c`.`NODE_ID`))) where ((`a`.`NODE_STATE` = 'N') and (`w`.`INST_STATE` = 'N') and (`a`.`TASK_ASSIGNED` = 'S')) union all select `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,`w`.`FLOW_CODE` AS `FLOW_CODE`,`w`.`VERSION` AS `version`,`w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,`w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,`a`.`NODE_INST_ID` AS `NODE_INST_ID`,ifnull(`a`.`UNIT_CODE`,ifnull(`w`.`UNIT_CODE`,'0000000')) AS `UnitCode`,`b`.`USER_CODE` AS `user_code`,'' AS `ROLE_TYPE`,'' AS `ROLE_CODE`,`b`.`AUTH_DESC` AS `AUTH_DESC`,`c`.`NODE_CODE` AS `node_code`,`c`.`NODE_NAME` AS `Node_Name`,`c`.`NODE_TYPE` AS `Node_Type`,`c`.`OPT_TYPE` AS `NODE_OPT_TYPE`,`c`.`OPT_PARAM` AS `Opt_Param`,`a`.`CREATE_TIME` AS `CREATE_TIME`,`a`.`promise_Time` AS `Promise_Time`,`a`.`time_Limit` AS `TIME_LIMIT`,`c`.`OPT_CODE` AS `OPT_CODE`,`c`.`Expire_Opt` AS `Expire_Opt`,`c`.`STAGE_CODE` AS `STAGE_CODE`,`a`.`last_update_user` AS `last_update_user`,`a`.`last_update_time` AS `LAST_UPDATE_TIME`,`w`.`INST_STATE` AS `inst_state`,`c`.`OS_ID` AS `OS_ID`,`a`.`NODE_PARAM` AS `NODE_PARAM` from (((`wf_node_instance` `a` join `wf_flow_instance` `w` on((`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`))) join `wf_action_task` `b` on((`a`.`NODE_INST_ID` = `b`.`NODE_INST_ID`))) join `wf_node` `c` on((`a`.`NODE_ID` = `c`.`NODE_ID`))) where ((`a`.`NODE_STATE` = 'N') and (`w`.`INST_STATE` = 'N') and (`a`.`TASK_ASSIGNED` = 'T')  and (`b`.`TASK_STATE` = 'A')) ;

-- ----------------------------
-- View structure for V_USER_TASK_LIST
-- ----------------------------
CREATE
VIEW `V_USER_TASK_LIST`AS
select `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,`a`.`FLOW_CODE` AS `FLOW_CODE`,`a`.`version` AS `version`,`a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,`a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,`a`.`NODE_INST_ID` AS `NODE_INST_ID`,`a`.`Unit_Code` AS `Unit_Code`,`a`.`user_code` AS `user_code`,`a`.`ROLE_TYPE` AS `ROLE_TYPE`,`a`.`ROLE_CODE` AS `ROLE_CODE`,`a`.`AUTH_DESC` AS `AUTH_DESC`,`a`.`node_code` AS `node_code`,`a`.`Node_Name` AS `Node_Name`,`a`.`Node_Type` AS `Node_Type`,`a`.`NODE_OPT_TYPE` AS `NODE_OPT_TYPE`,`a`.`Opt_Param` AS `Opt_Param`,`a`.`CREATE_TIME` AS `CREATE_TIME`,`a`.`Promise_Time` AS `promise_time`,`a`.`TIME_LIMIT` AS `time_limit`,`a`.`OPT_CODE` AS `OPT_CODE`,`a`.`Expire_Opt` AS `Expire_Opt`,`a`.`STAGE_CODE` AS `STAGE_CODE`,'' AS `GRANTOR`,`a`.`last_update_user` AS `last_update_user`,`a`.`LAST_UPDATE_TIME` AS `LAST_UPDATE_TIME`,`a`.`inst_state` AS `inst_state`,`a`.`OPT_CODE` AS `OPT_URL`,`a`.`OS_ID` AS `OS_ID`,`a`.`NODE_PARAM` AS `NODE_PARAM` from `v_inner_user_task_list` `a` union select `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,`a`.`FLOW_CODE` AS `FLOW_CODE`,`a`.`version` AS `version`,`a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,`a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,`a`.`NODE_INST_ID` AS `NODE_INST_ID`,`a`.`Unit_Code` AS `Unit_Code`,`a`.`user_code` AS `user_code`,`a`.`ROLE_TYPE` AS `ROLE_TYPE`,`a`.`ROLE_CODE` AS `ROLE_CODE`,`a`.`AUTH_DESC` AS `AUTH_DESC`,`a`.`node_code` AS `node_code`,`a`.`Node_Name` AS `Node_Name`,`a`.`Node_Type` AS `Node_Type`,`a`.`NODE_OPT_TYPE` AS `NODE_OPT_TYPE`,`a`.`Opt_Param` AS `Opt_Param`,`a`.`CREATE_TIME` AS `CREATE_TIME`,`a`.`Promise_Time` AS `promise_time`,`a`.`TIME_LIMIT` AS `time_limit`,`a`.`OPT_CODE` AS `OPT_CODE`,`a`.`Expire_Opt` AS `Expire_Opt`,`a`.`STAGE_CODE` AS `STAGE_CODE`,`b`.`GRANTOR` AS `GRANTOR`,`a`.`last_update_user` AS `last_update_user`,`a`.`LAST_UPDATE_TIME` AS `last_update_time`,`a`.`inst_state` AS `inst_state`,`a`.`OPT_CODE` AS `OPT_URL`,`a`.`OS_ID` AS `OS_ID`,`a`.`NODE_PARAM` AS `NODE_PARAM` from (`v_inner_user_task_list` `a` join `wf_role_relegate` `b` on((`b`.`UNIT_CODE` = `a`.`Unit_Code`))) where ((`b`.`IS_VALID` = 'T') and (`b`.`RELEGATE_TIME` <= now()) and (`a`.`user_code` = `b`.`GRANTOR`) and (isnull(`b`.`EXPIRE_TIME`) or (`b`.`EXPIRE_TIME` >= now())) and (isnull(`b`.`UNIT_CODE`) or (`b`.`UNIT_CODE` = `a`.`Unit_Code`)) and (isnull(`b`.`ROLE_TYPE`) or (`b`.`ROLE_TYPE` = `a`.`ROLE_TYPE`)) and (isnull(`b`.`ROLE_CODE`) or (`b`.`ROLE_CODE` = `a`.`ROLE_CODE`))) ;


