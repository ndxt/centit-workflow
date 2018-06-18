create sequence S_FLOWDEFINE;
create sequence S_FLOWDEFNO;
create sequence S_ACTIONLOGNO;
create sequence S_FLOWINSTNO;
create sequence S_MANAGERACTIONNO;
create sequence S_NODEINSTNO;




DROP TABLE IF EXISTS wf_flow_variable_define;
DROP TABLE IF EXISTS wf_flow_team_role;
DROP TABLE IF EXISTS p_task_list;
DROP TABLE IF EXISTS wf_action_log;
DROP TABLE IF EXISTS wf_action_task;
DROP TABLE IF EXISTS wf_flow_define;
DROP TABLE IF EXISTS wf_flow_instance;
DROP TABLE IF EXISTS wf_flow_stage;
DROP TABLE IF EXISTS wf_flow_variable;
DROP TABLE IF EXISTS wf_inst_attention;
DROP TABLE IF EXISTS wf_manage_action;
DROP TABLE IF EXISTS wf_node;
DROP TABLE IF EXISTS wf_node_instance;
DROP TABLE IF EXISTS wf_organize;
DROP TABLE IF EXISTS wf_role_relegate;
DROP TABLE IF EXISTS wf_router_node;
DROP TABLE IF EXISTS wf_runtime_warning;
DROP TABLE IF EXISTS wf_stage_instance;
DROP TABLE IF EXISTS wf_team;
DROP TABLE IF EXISTS wf_transition;
DROP TABLE IF EXISTS f_opt_variable;



CREATE TABLE wf_flow_variable_define (
  FLOW_VARIABLE_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  VARIABLE_NAME varchar(100) NOT NULL,
  VARIABLE_TYPE varchar(100) DEFAULT NULL,
  VARIABLE_ORDER smallint(6) DEFAULT NULL,
  CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  VERSION decimal(4,0) ,
  PRIMARY KEY (FLOW_VARIABLE_ID)
) ;


CREATE TABLE wf_flow_team_role (
  FLOW_TEAM_ROLE_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  ROLE_CODE varchar(100) NOT NULL,
  ROLE_NAME varchar(100) NOT NULL,
  TEAM_ROLE_ORDER smallint(6) DEFAULT NULL,
  CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  VERSION decimal(4,0) ,
  PRIMARY KEY (FLOW_TEAM_ROLE_ID)
) ;




CREATE TABLE p_task_list (
  taskid decimal(12,0) NOT NULL COMMENT '自动生成的主键，需要一个序列来配合',
  taskowner varchar(8) NOT NULL COMMENT '谁的任务',
  tasktag varchar(1) NOT NULL COMMENT '类似与outlook中的邮件标记，可以用不同的颜色的旗子图表标识',
  taskrank varchar(1) NOT NULL COMMENT '任务的优先级',
  taskstatus varchar(2) NOT NULL COMMENT '处理中、完成、取消、终止',
  tasktitle varchar(256) NOT NULL,
  taskmemo varchar(1000) DEFAULT NULL COMMENT '简要描述任务的具体内容',
  tasktype varchar(8) NOT NULL COMMENT '个人、组织活动、领导委派 等等',
  OptID varchar(64) NOT NULL COMMENT '模块，或者表',
  OPTMethod varchar(64) DEFAULT NULL COMMENT '方法，或者字段',
  optTag varchar(200) DEFAULT NULL COMMENT '一般用于关联到业务主体',
  creator varchar(32) NOT NULL,
  created datetime NOT NULL,
  planbegintime datetime NOT NULL,
  planendtime datetime DEFAULT NULL,
  begintime datetime DEFAULT NULL,
  endtime datetime DEFAULT NULL,
  finishmemo varchar(1000) DEFAULT NULL COMMENT '简要记录任务的执行过程和结果',
  noticeSign varchar(1) DEFAULT NULL COMMENT '提醒标志为：禁止提醒、未提醒、已提醒',
  lastNoticeTime datetime DEFAULT NULL COMMENT '最后一次提醒时间，根据提醒策略可以提醒多次',
  taskdeadline datetime DEFAULT NULL,
  taskvalue varchar(2048) DEFAULT NULL COMMENT '备用，字段不够时使用',
  PRIMARY KEY (taskid)
) ;



CREATE TABLE wf_action_log (
  ACTION_ID decimal(12,0) NOT NULL,
  NODE_INST_ID decimal(12,0) DEFAULT NULL,
  ACTION_TYPE varchar(2) NOT NULL ,
  ACTION_TIME datetime NOT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  GRANTOR varchar(8) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;



CREATE TABLE wf_action_task (
  TASK_ID decimal(12,0) NOT NULL,
  NODE_INST_ID decimal(12,0) DEFAULT NULL,
  ASSIGN_TIME datetime NOT NULL,
  EXPIRE_TIME datetime DEFAULT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  TASK_STATE char(1) DEFAULT NULL ,
  IS_VALID char(1) DEFAULT NULL ,
  AUTH_DESC varchar(255) DEFAULT NULL ,
  PRIMARY KEY (TASK_ID)
) ;



CREATE TABLE wf_flow_define (
  FLOW_CODE varchar(32) NOT NULL ,
  version decimal(20,0) NOT NULL DEFAULT '0',
  FLOW_NAME varchar(120) DEFAULT NULL,
  FLOW_CLASS varchar(4) NOT NULL ,
  FLOW_Publish_Date datetime DEFAULT NULL ,
  FLOW_STATE char(1) DEFAULT NULL ,
  FLOW_DESC varchar(500) DEFAULT NULL,
  FLOW_XML_DESC text ,
  Time_Limit varchar(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  Opt_ID varchar(32) DEFAULT NULL,
  AT_PUBLISH_DATE datetime DEFAULT NULL,
  OS_ID varchar(32) DEFAULT NULL,
  PRIMARY KEY (version,FLOW_CODE)
)  ;


CREATE TABLE wf_flow_instance (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  VERSION decimal(4,0) DEFAULT NULL,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  FLOW_Opt_Name varchar(100) DEFAULT NULL ,
  FLOW_Opt_Tag varchar(100) DEFAULT NULL ,
  CREATE_TIME datetime NOT NULL,
  is_Timer char(1) DEFAULT NULL,
  promise_Time decimal(10,0) DEFAULT NULL ,
  time_Limit decimal(10,0) DEFAULT NULL,
  last_update_user varchar(8) DEFAULT NULL,
  last_update_time datetime DEFAULT NULL,
  INST_STATE char(1) DEFAULT NULL ,
  IS_SUB_INST char(1) DEFAULT NULL ,
  PRE_INST_ID decimal(16,0) DEFAULT NULL,
  PRE_NODE_INST_ID decimal(16,0) DEFAULT NULL,
  UNIT_CODE varchar(8) DEFAULT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  PRIMARY KEY (FLOW_INST_ID)
) ;


CREATE TABLE wf_flow_stage (
  STAGE_ID decimal(12,0) NOT NULL,
  VERSION decimal(4,0) DEFAULT NULL ,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  STAGE_CODE varchar(32) NOT NULL,
  STAGE_NAME varchar(60) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  PRIMARY KEY (STAGE_ID)
) ;



CREATE TABLE wf_flow_variable (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  Run_Token varchar(20) NOT NULL,
  VAR_NAME varchar(50) NOT NULL,
  VAR_VALUE varchar(256) NOT NULL,
  Var_Type char(1) NOT NULL,
  PRIMARY KEY (FLOW_INST_ID,Run_Token,VAR_NAME)
) ;


CREATE TABLE wf_inst_attention (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  user_Code varchar(8) NOT NULL,
  att_set_time datetime DEFAULT NULL,
  att_set_user varchar(8) DEFAULT NULL,
  att_set_Memo varchar(255) DEFAULT NULL,
  PRIMARY KEY (user_Code,FLOW_INST_ID)
)  ;


CREATE TABLE wf_manage_action (
  ACTION_ID decimal(12,0) NOT NULL,
  FLOW_INST_ID decimal(12,0) DEFAULT NULL,
  NODE_INST_ID decimal(12,0) DEFAULT NULL ,
  ACTION_TYPE varchar(2) NOT NULL ,
  ACTION_TIME datetime NOT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  ADMIN_DESC varchar(1000) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;


CREATE TABLE wf_node (
  NODE_ID decimal(12,0) NOT NULL ,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  VERSION decimal(4,0) DEFAULT NULL,
  NODE_TYPE varchar(1) NOT NULL ,
  NODE_NAME varchar(120) DEFAULT NULL,
  OPT_TYPE varchar(1) DEFAULT NULL ,
  OS_ID varchar(32) DEFAULT 'TEST' ,
  OPT_ID varchar(32) DEFAULT NULL ,
  OPT_CODE varchar(32) DEFAULT NULL ,
  OPT_BEAN varchar(100) DEFAULT NULL ,
  OPT_PARAM varchar(100) DEFAULT NULL,
  SUB_FLOW_CODE varchar(8) DEFAULT NULL ,
  ROUTER_TYPE varchar(1) DEFAULT NULL ,
  ROLE_TYPE varchar(8) DEFAULT NULL ,
  ROLE_CODE varchar(32) DEFAULT NULL,
  UNIT_EXP varchar(64) DEFAULT NULL,
  POWER_EXP varchar(512) DEFAULT NULL,
  multi_Inst_Type char(1) DEFAULT NULL,
  multi_Inst_Param varchar(512) DEFAULT NULL,
  converge_Type char(1) DEFAULT NULL ,
  converge_Param varchar(64) DEFAULT NULL,
  NODE_DESC varchar(500) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar(20) DEFAULT NULL ,
  inherit_Type char(1) DEFAULT NULL ,
  inherit_Node_Code varchar(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  Warning_Rule char(1) DEFAULT NULL ,
  Warning_Param varchar(20) DEFAULT NULL ,
  is_Trunk_Line char(1) DEFAULT NULL ,
  STAGE_CODE varchar(32) DEFAULT NULL,
  NODE_CODE varchar(20) DEFAULT NULL,
  RISK_INFO varchar(4) DEFAULT NULL,
  PRIMARY KEY (NODE_ID)
)  ;


CREATE TABLE wf_node_instance (
  NODE_INST_ID decimal(12,0) NOT NULL,
  FLOW_INST_ID decimal(12,0) DEFAULT NULL,
  NODE_ID decimal(12,0) DEFAULT NULL ,
  CREATE_TIME datetime DEFAULT NULL,
  START_TIME datetime DEFAULT NULL,
  is_Timer char(1) DEFAULT NULL,
  promise_Time decimal(10,0) DEFAULT NULL ,
  time_Limit decimal(10,0) DEFAULT NULL,
  PREV_NODE_INST_ID decimal(12,0) DEFAULT NULL,
  NODE_STATE varchar(2) DEFAULT NULL ,
  SUB_FLOW_INST_ID decimal(12,0) DEFAULT NULL,
  UNIT_CODE varchar(8) DEFAULT NULL,
  STAGE_CODE varchar(32) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL ,
  ROLE_CODE varchar(32) DEFAULT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  NODE_PARAM varchar(128) DEFAULT NULL,
  TRANS_ID decimal(12,0) DEFAULT NULL ,
  TASK_ASSIGNED varchar(1) DEFAULT 'F' ,
  Run_Token varchar(20) DEFAULT NULL ,
  GRANTOR varchar(8) DEFAULT NULL,
  last_update_user varchar(8) DEFAULT NULL,
  last_update_time datetime DEFAULT NULL,
  TRANS_PATH varchar(256) DEFAULT NULL,
  PRIMARY KEY (NODE_INST_ID)
) ;


CREATE TABLE wf_organize (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  UNIT_CODE varchar(8) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  UNIT_ORDER decimal(4,0) DEFAULT NULL ,
  AUTH_DESC varchar(255) DEFAULT NULL,
  AUTH_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (FLOW_INST_ID,UNIT_CODE,ROLE_CODE)
) ;



CREATE TABLE wf_role_relegate (
  RELEGATE_NO decimal(12,0) NOT NULL,
  GRANTOR varchar(8) NOT NULL,
  GRANTEE varchar(8) NOT NULL,
  IS_VALID char(1) NOT NULL DEFAULT 'T' ,
  Recorder varchar(8) DEFAULT NULL,
  RELEGATE_TIME datetime NOT NULL,
  EXPIRE_TIME datetime DEFAULT NULL,
  UNIT_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  Record_Date datetime DEFAULT NULL,
  grant_Desc varchar(256) DEFAULT NULL,
  PRIMARY KEY (RELEGATE_NO)
) ;


CREATE TABLE wf_router_node (
  NODEID decimal(12,0) NOT NULL ,
  WFCODE varchar(32) DEFAULT NULL ,
  VERSION decimal(4,0) DEFAULT NULL,
  ROUTERTYPE varchar(1) NOT NULL ,
  NODENAME varchar(120) DEFAULT NULL,
  NODEDESC varchar(500) DEFAULT NULL,
  ROLETYPE varchar(8) DEFAULT NULL ,
  ROLECODE varchar(32) DEFAULT NULL,
  UNITEXP varchar(64) DEFAULT NULL,
  POWEREXP varchar(512) DEFAULT NULL,
  SELFDEFPARAM varchar(512) DEFAULT NULL ,
  convergeType char(1) DEFAULT NULL ,
  convergeParam varchar(64) DEFAULT NULL,
  OPTBEAN varchar(100) DEFAULT NULL ,
  PRIMARY KEY (NODEID)
) ;


CREATE TABLE wf_runtime_warning (
  WARNING_ID decimal(12,0) NOT NULL ,
  FLOW_INST_ID decimal(12,0) DEFAULT NULL,
  NODE_INST_ID decimal(12,0) NOT NULL,
  FLOW_STAGE varchar(4) DEFAULT NULL,
  OBJ_TYPE char(1) DEFAULT NULL ,
  WARNING_TYPE char(1) DEFAULT NULL ,
  WARNING_STATE char(1) DEFAULT 'N' ,
  WARNING_CODE varchar(16) DEFAULT NULL ,
  WARNING_TIME datetime DEFAULT NULL,
  WARNINGID_MSG varchar(500) DEFAULT NULL,
  NOTICE_STATE char(1) DEFAULT '0' ,
  SEND_MSG_TIME datetime DEFAULT NULL,
  SEND_USERS varchar(100) DEFAULT NULL ,
  PRIMARY KEY (WARNING_ID)
) ;


CREATE TABLE wf_stage_instance (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  STAGE_ID decimal(12,0) NOT NULL,
  STAGE_CODE varchar(32) NOT NULL,
  STAGE_NAME varchar(60) DEFAULT NULL,
  begin_Time datetime DEFAULT NULL,
  stage_Begin char(1) DEFAULT NULL ,
  promise_Time decimal(10,0) DEFAULT NULL ,
  time_Limit decimal(10,0) DEFAULT NULL,
  last_update_time datetime DEFAULT NULL,
  PRIMARY KEY (FLOW_INST_ID,STAGE_ID)
)  ;


CREATE TABLE wf_team (
  FLOW_INST_ID decimal(12,0) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  USER_CODE varchar(8) NOT NULL,
  USER_ORDER decimal(4,0) DEFAULT NULL ,
  AUTH_DESC varchar(255) DEFAULT NULL,
  AUTH_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (FLOW_INST_ID,USER_CODE,ROLE_CODE)
)  ;


CREATE TABLE wf_transition (
  TRANS_ID decimal(12,0) NOT NULL,
  VERSION decimal(4,0) DEFAULT NULL,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  TRANS_CLASS varchar(4) DEFAULT NULL ,
  TRANS_NAME varchar(120) DEFAULT NULL,
  START_NODE_ID decimal(12,0) DEFAULT NULL ,
  END_NODE_ID decimal(12,0) DEFAULT NULL ,
  TRANS_CONDITION varchar(500) DEFAULT NULL ,
  TRANS_DESC varchar(500) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar(20) DEFAULT NULL ,
  inherit_Type char(1) DEFAULT NULL ,
  inherit_Node_Code varchar(20) DEFAULT NULL ,
  can_ignore char(1) NOT NULL DEFAULT 'T' ,
  PRIMARY KEY (TRANS_ID)
) ;

CREATE TABLE f_opt_variable (
  OptID varchar(8) NOT NULL,
  VarName varchar(32) NOT NULL,
  VarDesc varchar(200) DEFAULT NULL,
  VarType char(1) DEFAULT NULL ,
  DefaultValue varchar(200) DEFAULT NULL,
  ISVALID char(1) NOT NULL DEFAULT 'T' ,
  PRIMARY KEY (OptID,VarName)
)  ;


CREATE or replace VIEW lastversion AS select wf_flow_define.FLOW_CODE AS FLOW_CODE,max(wf_flow_define.version) AS version from wf_flow_define group by wf_flow_define.FLOW_CODE ;



CREATE or replace  VIEW f_v_lastversionflow AS select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE from ((lastversion join wf_flow_define a on(((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0)))) join wf_flow_define b on(((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version)))) ;



CREATE   or replace VIEW f_v_wf_optdef_url_map AS select concat(c.opt_url,b.OPT_URL) AS optdef_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code,b.OPT_DESC AS opt_desc,b.OPT_METHOD AS opt_Method,c.Opt_ID AS opt_id,b.OPT_NAME AS Opt_Name from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type = 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;



CREATE   or replace VIEW v_inner_user_task_list AS select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,ifnull(a.UNIT_CODE,ifnull(w.UNIT_CODE,'0000000')) AS Unit_Code,a.USER_CODE AS user_code,c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTH_DESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS Method_Name,d.optdef_url AS Opt_Url,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state from (((wf_node_instance a join wf_flow_instance w on((a.FLOW_INST_ID = w.FLOW_INST_ID))) join wf_node c on((a.NODE_ID = c.NODE_ID))) join f_v_wf_optdef_url_map d on((c.OPT_CODE = d.opt_code))) where ((a.NODE_STATE = 'N') and (w.INST_STATE = 'N') and (a.TASK_ASSIGNED = 'S')) union all select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,ifnull(a.UNIT_CODE,ifnull(w.UNIT_CODE,'0000000')) AS UnitCode,b.USER_CODE AS user_code,b.ROLE_TYPE AS ROLE_TYPE,b.ROLE_CODE AS ROLE_CODE,b.AUTH_DESC AS AUTH_DESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODEOPTTYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS MethodName,d.optdef_url AS OptUrl,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state from ((((wf_node_instance a join wf_flow_instance w on((a.FLOW_INST_ID = w.FLOW_INST_ID))) join wf_action_task b on((a.NODE_INST_ID = b.NODE_INST_ID))) join wf_node c on((a.NODE_ID = c.NODE_ID))) join f_v_wf_optdef_url_map d on((c.OPT_CODE = d.opt_code))) where ((a.NODE_STATE = 'N') and (w.INST_STATE = 'N') and (a.TASK_ASSIGNED = 'T') and (b.IS_VALID = 'T') and (b.TASK_STATE = 'A') and (b.EXPIRE_TIME is null or (b.EXPIRE_TIME > now()))) union all select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,b.UNIT_CODE AS Unit_Code,b.USER_CODE AS user_code,c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTHDESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODEOPTTYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS MethodName,d.optdef_url AS OptUrl,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state from ((((wf_node_instance a join wf_flow_instance w on((a.FLOW_INST_ID = w.FLOW_INST_ID))) join wf_node c on((a.NODE_ID = c.NODE_ID))) join f_v_wf_optdef_url_map d on((c.OPT_CODE = d.opt_code))) join f_userunit b) where ((a.NODE_STATE = 'N') and (w.INST_STATE = 'N') and (a.TASK_ASSIGNED = 'D') and (a.UNIT_CODE is null or (a.UNIT_CODE = b.UNIT_CODE)) and (((c.ROLE_TYPE = 'gw') and (c.ROLE_CODE = b.User_Station)) or ((c.ROLE_TYPE = 'xz') and (c.ROLE_CODE = b.User_Rank)))) ;


CREATE or replace   VIEW v_user_task_list_temp AS select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version 
AS version,a.FLOW_OPT_NAME AS FLOW_OPT_NAME,a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,
a.Unit_Code AS Unit_Code,a.user_code AS user_code,a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS
 AUTH_DESC,a.node_code AS node_code,a.Node_Name AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE,
 a.opt_id AS opt_id,a.Opt_Name AS Opt_Name,a.Method_Name AS Method_Name,a.Opt_Url AS Opt_Url,a.opt_Method AS opt_Method,
 a.Opt_Param AS Opt_Param,a.Opt_Desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT 
 AS time_limit,a.OPT_CODE AS OPT_CODE,a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,NULL AS GRANTOR,a.last_update_user
  AS last_update_user,a.LAST_UPDATE_TIME AS LAST_UPDATE_TIME,a.inst_state AS inst_state from v_inner_user_task_list a
   union select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version AS version,a.FLOW_OPT_NAME 
   AS FLOW_OPT_NAME,a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS node_inst_id,a.Unit_Code AS Unit_Code,b.GRANTEE AS 
   user_code,a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS AUTH_DESC,a.node_code AS node_code,a.Node_Name 
   AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE,a.opt_id AS opt_id,a.Opt_Name AS Opt_Name,
   a.Method_Name AS Method_Name,a.Opt_Url AS Opt_Url,a.opt_Method AS opt_Method,a.Opt_Param AS Opt_Param,a.Opt_Desc AS 
   Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT AS time_limit,a.OPT_CODE AS OPT_CODE
   ,a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,b.GRANTOR AS GRANTOR,a.last_update_user AS last_update_user,
   a.LAST_UPDATE_TIME AS last_update_time,a.inst_state AS inst_state from (v_inner_user_task_list a join wf_role_relegate b) 
   where ((b.IS_VALID = 'T') and (b.RELEGATE_TIME <= now()) and (b.EXPIRE_TIME is null or (b.EXPIRE_TIME >= now())) and
    (a.user_code = b.GRANTOR) and (b.UNIT_CODE is null or (b.UNIT_CODE = a.Unit_Code)) and (b.ROLE_TYPE is null or
     ((b.ROLE_TYPE = a.ROLE_TYPE) and (b.ROLE_CODE is null or (b.ROLE_CODE = a.ROLE_CODE)))));


CREATE   or replace VIEW v_user_task_list AS select (select count(0) from v_user_task_list_temp where (v_user_task_list_temp.FLOW_INST_ID <= t.FLOW_INST_ID)) AS TASK_ID,t.FLOW_INST_ID AS FLOW_INST_ID,t.FLOW_CODE AS FLOW_CODE,t.version AS VERSION,t.FLOW_OPT_NAME AS FLOW_NAME,t.FLOW_OPT_NAME AS FLOW_OPT_NAME,t.FLOW_OPT_TAG AS FLOW_OPT_TAG,t.NODE_INST_ID AS NODE_INST_ID,t.Unit_Code AS UNIT_CODE,t.user_code AS USER_CODE,t.ROLE_TYPE AS ROLE_TYPE,t.ROLE_CODE AS ROLE_CODE,t.AUTH_DESC AS AUTH_DESC,t.node_code AS NODE_CODE,t.Node_Name AS NODE_NAME,t.Node_Type AS NODE_TYPE,t.NODE_OPT_TYPE AS NODE_OPT_TYPE,t.opt_id AS OPT_ID,t.Opt_Name AS OPT_NAME,t.Method_Name AS METHOD_NAME,t.Opt_Url AS OPT_URL,t.opt_Method AS OPT_METHOD,t.Opt_Param AS OPT_PARAM,t.Opt_Desc AS OPT_DESC,t.CREATE_TIME AS CREATE_TIME,t.promise_time AS PROMISE_TIME,t.time_limit AS TIME_LIMIT,t.OPT_CODE AS OPT_CODE,t.Expire_Opt AS EXPIRE_OPT,t.STAGE_CODE AS STAGE_CODE,t.GRANTOR AS GRANTOR,t.last_update_user AS LAST_UPDATE_USER,t.LAST_UPDATE_TIME AS LAST_UPDATE_TIME,t.inst_state AS INST_STATE from v_user_task_list_temp t;






