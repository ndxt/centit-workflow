create sequence S_FLOWDEFINE;
create sequence S_FLOWDEFNO;
create sequence S_ACTIONLOGNO;
create sequence S_FLOWINSTNO;
create sequence S_MANAGERACTIONNO;
create sequence S_NODEINSTNO;



CREATE TABLE wf_flow_variable_define (
  FLOW_VARIABLE_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  VARIABLE_NAME varchar2(100) NOT NULL,
  VARIABLE_TYPE varchar2(100) DEFAULT NULL,
  VARIABLE_ORDER smallint DEFAULT NULL,
  CREATE_TIME date  DEFAULT sysdate,
  MODIFY_TIME date   DEFAULT sysdate,
  VERSION number(4,0) ,
  PRIMARY KEY (FLOW_VARIABLE_ID)
) ;


CREATE TABLE wf_flow_team_role (
  FLOW_TEAM_ROLE_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  ROLE_CODE varchar2(100) NOT NULL,
  ROLE_NAME varchar2(100) NOT NULL,
  TEAM_ROLE_ORDER smallint DEFAULT NULL,
  CREATE_TIME date  DEFAULT sysdate,
  MODIFY_TIME date  DEFAULT sysdate,
  VERSION number(4,0) ,
  PRIMARY KEY (FLOW_TEAM_ROLE_ID)
) ;




CREATE TABLE wf_action_log (
  ACTION_ID number(12,0) NOT NULL,
  NODE_INST_ID number(12,0) DEFAULT NULL,
  ACTION_TYPE varchar2(2) NOT NULL ,
  ACTION_TIME date NOT NULL,
  USER_CODE varchar2(8) DEFAULT NULL,
  ROLE_TYPE varchar2(8) DEFAULT NULL,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  GRANTOR varchar2(8) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;



CREATE TABLE wf_action_task (
  TASK_ID number(12,0) NOT NULL,
  NODE_INST_ID number(12,0) DEFAULT NULL,
  ASSIGN_TIME date NOT NULL,
  EXPIRE_TIME date DEFAULT NULL,
  USER_CODE varchar2(8) DEFAULT NULL,
  ROLE_TYPE varchar2(8) DEFAULT NULL,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  TASK_STATE char(1) DEFAULT NULL ,
  IS_VALID char(1) DEFAULT NULL ,
  AUTH_DESC varchar2(255) DEFAULT NULL ,
  PRIMARY KEY (TASK_ID)
) ;



CREATE TABLE wf_flow_define (
  FLOW_CODE varchar2(32) NOT NULL ,
  version number(20,0) DEFAULT '0',
  FLOW_NAME varchar2(120) DEFAULT NULL,
  FLOW_CLASS varchar2(4) NOT NULL ,
  FLOW_Publish_Date date DEFAULT NULL ,
  FLOW_STATE char(1) DEFAULT NULL ,
  FLOW_DESC varchar2(500) DEFAULT NULL,
  FLOW_XML_DESC clob ,
  Time_Limit varchar2(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  Opt_ID varchar2(32) DEFAULT NULL,
  AT_PUBLISH_DATE date DEFAULT NULL,
  OS_ID varchar2(32) DEFAULT NULL,
  PRIMARY KEY (version,FLOW_CODE)
)  ;


CREATE TABLE wf_flow_instance (
  FLOW_INST_ID number(12,0) NOT NULL,
  VERSION number(4,0) DEFAULT NULL,
  FLOW_CODE varchar2(32) DEFAULT NULL ,
  FLOW_Opt_Name varchar2(100) DEFAULT NULL ,
  FLOW_Opt_Tag varchar2(100) DEFAULT NULL ,
  CREATE_TIME date NOT NULL,
  is_Timer char(1) DEFAULT NULL,
  promise_Time number(10,0) DEFAULT NULL ,
  time_Limit number(10,0) DEFAULT NULL,
  last_update_user varchar2(8) DEFAULT NULL,
  last_update_time date DEFAULT NULL,
  INST_STATE char(1) DEFAULT NULL ,
  IS_SUB_INST char(1) DEFAULT NULL ,
  PRE_INST_ID number(16,0) DEFAULT NULL,
  PRE_NODE_INST_ID number(16,0) DEFAULT NULL,
  UNIT_CODE varchar2(8) DEFAULT NULL,
  USER_CODE varchar2(8) DEFAULT NULL,
  PRIMARY KEY (FLOW_INST_ID)
) ;


CREATE TABLE wf_flow_stage (
  STAGE_ID number(12,0) NOT NULL,
  VERSION number(4,0) DEFAULT NULL ,
  FLOW_CODE varchar2(32) DEFAULT NULL ,
  STAGE_CODE varchar2(32) NOT NULL,
  STAGE_NAME varchar2(60) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar2(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  PRIMARY KEY (STAGE_ID)
) ;



CREATE TABLE wf_flow_variable (
  FLOW_INST_ID number(12,0) NOT NULL,
  Run_Token varchar2(20) NOT NULL,
  VAR_NAME varchar2(50) NOT NULL,
  VAR_VALUE varchar2(256) NOT NULL,
  Var_Type char(1) NOT NULL,
  PRIMARY KEY (FLOW_INST_ID,Run_Token,VAR_NAME)
) ;


CREATE TABLE wf_inst_attention (
  FLOW_INST_ID number(12,0) NOT NULL,
  user_Code varchar2(8) NOT NULL,
  att_set_time date DEFAULT NULL,
  att_set_user varchar2(8) DEFAULT NULL,
  att_set_Memo varchar2(255) DEFAULT NULL,
  PRIMARY KEY (user_Code,FLOW_INST_ID)
)  ;


CREATE TABLE wf_manage_action (
  ACTION_ID number(12,0) NOT NULL,
  FLOW_INST_ID number(12,0) DEFAULT NULL,
  NODE_INST_ID number(12,0) DEFAULT NULL ,
  ACTION_TYPE varchar2(2) NOT NULL ,
  ACTION_TIME date NOT NULL,
  USER_CODE varchar2(8) DEFAULT NULL,
  ROLE_TYPE varchar2(8) DEFAULT NULL,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  ADMIN_DESC varchar2(1000) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;


CREATE TABLE wf_node (
  NODE_ID number(12,0) NOT NULL ,
  FLOW_CODE varchar2(32) DEFAULT NULL ,
  VERSION number(4,0) DEFAULT NULL,
  NODE_TYPE varchar2(1) NOT NULL ,
  NODE_NAME varchar2(120) DEFAULT NULL,
  OPT_TYPE varchar2(1) DEFAULT NULL ,
  OS_ID varchar2(32) DEFAULT 'TEST' ,
  OPT_ID varchar2(32) DEFAULT NULL ,
  OPT_CODE varchar2(32) DEFAULT NULL ,
  OPT_BEAN varchar2(100) DEFAULT NULL ,
  OPT_PARAM varchar2(100) DEFAULT NULL,
  SUB_FLOW_CODE varchar2(8) DEFAULT NULL ,
  ROUTER_TYPE varchar2(1) DEFAULT NULL ,
  ROLE_TYPE varchar2(8) DEFAULT NULL ,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  UNIT_EXP varchar2(64) DEFAULT NULL,
  POWER_EXP varchar2(512) DEFAULT NULL,
  multi_Inst_Type char(1) DEFAULT NULL,
  multi_Inst_Param varchar2(512) DEFAULT NULL,
  converge_Type char(1) DEFAULT NULL ,
  converge_Param varchar2(64) DEFAULT NULL,
  NODE_DESC varchar2(500) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar2(20) DEFAULT NULL ,
  inherit_Type char(1) DEFAULT NULL ,
  inherit_Node_Code varchar2(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  Warning_Rule char(1) DEFAULT NULL ,
  Warning_Param varchar2(20) DEFAULT NULL ,
  is_Trunk_Line char(1) DEFAULT NULL ,
  STAGE_CODE varchar2(32) DEFAULT NULL,
  NODE_CODE varchar2(20) DEFAULT NULL,
  RISK_INFO varchar2(4) DEFAULT NULL,
  PRIMARY KEY (NODE_ID)
)  ;


CREATE TABLE wf_node_instance (
  NODE_INST_ID number(12,0) NOT NULL,
  FLOW_INST_ID number(12,0) DEFAULT NULL,
  NODE_ID number(12,0) DEFAULT NULL ,
  CREATE_TIME date DEFAULT NULL,
  START_TIME date DEFAULT NULL,
  is_Timer char(1) DEFAULT NULL,
  promise_Time number(10,0) DEFAULT NULL ,
  time_Limit number(10,0) DEFAULT NULL,
  PREV_NODE_INST_ID number(12,0) DEFAULT NULL,
  NODE_STATE varchar2(2) DEFAULT NULL ,
  SUB_FLOW_INST_ID number(12,0) DEFAULT NULL,
  UNIT_CODE varchar2(8) DEFAULT NULL,
  STAGE_CODE varchar2(32) DEFAULT NULL,
  ROLE_TYPE varchar2(8) DEFAULT NULL ,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  USER_CODE varchar2(8) DEFAULT NULL,
  NODE_PARAM varchar2(128) DEFAULT NULL,
  TRANS_ID number(12,0) DEFAULT NULL ,
  TASK_ASSIGNED varchar2(1) DEFAULT 'F' ,
  Run_Token varchar2(20) DEFAULT NULL ,
  GRANTOR varchar2(8) DEFAULT NULL,
  last_update_user varchar2(8) DEFAULT NULL,
  last_update_time date DEFAULT NULL,
  TRANS_PATH varchar2(256) DEFAULT NULL,
  PRIMARY KEY (NODE_INST_ID)
) ;


CREATE TABLE wf_organize (
  FLOW_INST_ID number(12,0) NOT NULL,
  UNIT_CODE varchar2(8) NOT NULL,
  ROLE_CODE varchar2(32) NOT NULL,
  UNIT_ORDER number(4,0) DEFAULT NULL ,
  AUTH_DESC varchar2(255) DEFAULT NULL,
  AUTH_TIME date  DEFAULT sysdate,
  PRIMARY KEY (FLOW_INST_ID,UNIT_CODE,ROLE_CODE)
) ;



CREATE TABLE wf_role_relegate (
  RELEGATE_NO number(12,0) NOT NULL,
  GRANTOR varchar2(8) NOT NULL,
  GRANTEE varchar2(8) NOT NULL,
  IS_VALID char(1)  DEFAULT 'T' ,
  Recorder varchar2(8) DEFAULT NULL,
  RELEGATE_TIME date NOT NULL,
  EXPIRE_TIME date DEFAULT NULL,
  UNIT_CODE varchar2(8) DEFAULT NULL,
  ROLE_TYPE varchar2(8) DEFAULT NULL,
  ROLE_CODE varchar2(32) DEFAULT NULL,
  Record_Date date DEFAULT NULL,
  grant_Desc varchar2(256) DEFAULT NULL,
  PRIMARY KEY (RELEGATE_NO)
) ;


CREATE TABLE wf_router_node (
  NODEID number(12,0) NOT NULL ,
  WFCODE varchar2(32) DEFAULT NULL ,
  VERSION number(4,0) DEFAULT NULL,
  ROUTERTYPE varchar2(1) NOT NULL ,
  NODENAME varchar2(120) DEFAULT NULL,
  NODEDESC varchar2(500) DEFAULT NULL,
  ROLETYPE varchar2(8) DEFAULT NULL ,
  ROLECODE varchar2(32) DEFAULT NULL,
  UNITEXP varchar2(64) DEFAULT NULL,
  POWEREXP varchar2(512) DEFAULT NULL,
  SELFDEFPARAM varchar2(512) DEFAULT NULL ,
  convergeType char(1) DEFAULT NULL ,
  convergeParam varchar2(64) DEFAULT NULL,
  OPTBEAN varchar2(100) DEFAULT NULL ,
  PRIMARY KEY (NODEID)
) ;


CREATE TABLE wf_runtime_warning (
  WARNING_ID number(12,0) NOT NULL ,
  FLOW_INST_ID number(12,0) DEFAULT NULL,
  NODE_INST_ID number(12,0) NOT NULL,
  FLOW_STAGE varchar2(4) DEFAULT NULL,
  OBJ_TYPE char(1) DEFAULT NULL ,
  WARNING_TYPE char(1) DEFAULT NULL ,
  WARNING_STATE char(1) DEFAULT 'N' ,
  WARNING_CODE varchar2(16) DEFAULT NULL ,
  WARNING_TIME date DEFAULT NULL,
  WARNINGID_MSG varchar2(500) DEFAULT NULL,
  NOTICE_STATE char(1) DEFAULT '0' ,
  SEND_MSG_TIME date DEFAULT NULL,
  SEND_USERS varchar2(100) DEFAULT NULL ,
  PRIMARY KEY (WARNING_ID)
) ;


CREATE TABLE wf_stage_instance (
  FLOW_INST_ID number(12,0) NOT NULL,
  STAGE_ID number(12,0) NOT NULL,
  STAGE_CODE varchar2(32) NOT NULL,
  STAGE_NAME varchar2(60) DEFAULT NULL,
  begin_Time date DEFAULT NULL,
  stage_Begin char(1) DEFAULT NULL ,
  promise_Time number(10,0) DEFAULT NULL ,
  time_Limit number(10,0) DEFAULT NULL,
  last_update_time date DEFAULT NULL,
  PRIMARY KEY (FLOW_INST_ID,STAGE_ID)
)  ;


CREATE TABLE wf_team (
  FLOW_INST_ID number(12,0) NOT NULL,
  ROLE_CODE varchar2(32) NOT NULL,
  USER_CODE varchar2(8) NOT NULL,
  USER_ORDER number(4,0) DEFAULT NULL ,
  AUTH_DESC varchar2(255) DEFAULT NULL,
  AUTH_TIME date  DEFAULT sysdate,
  PRIMARY KEY (FLOW_INST_ID,USER_CODE,ROLE_CODE)
)  ;


CREATE TABLE wf_transition (
  TRANS_ID number(12,0) NOT NULL,
  VERSION number(4,0) DEFAULT NULL,
  FLOW_CODE varchar2(32) DEFAULT NULL ,
  TRANS_CLASS varchar2(4) DEFAULT NULL ,
  TRANS_NAME varchar2(120) DEFAULT NULL,
  START_NODE_ID number(12,0) DEFAULT NULL ,
  END_NODE_ID number(12,0) DEFAULT NULL ,
  TRANS_CONDITION varchar2(500) DEFAULT NULL ,
  TRANS_DESC varchar2(500) DEFAULT NULL,
  is_Account_Time char(1) DEFAULT NULL ,
  Limit_Type char(1) DEFAULT NULL ,
  Time_Limit varchar2(20) DEFAULT NULL ,
  inherit_Type char(1) DEFAULT NULL ,
  inherit_Node_Code varchar2(20) DEFAULT NULL ,
  can_ignore char(1) DEFAULT 'T' ,
  PRIMARY KEY (TRANS_ID)
) ;


CREATE or replace VIEW lastversion AS
  select wf_flow_define.FLOW_CODE AS FLOW_CODE, max(wf_flow_define.version) AS version
  from wf_flow_define
  group by wf_flow_define.FLOW_CODE ;



CREATE  or replace VIEW f_v_lastversionflow AS
  select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,
    b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,
    a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,
    a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE
  from lastversion join wf_flow_define a on(a.FLOW_CODE = lastversion.FLOW_CODE and a.version = 0)
    join wf_flow_define b on (lastversion.FLOW_CODE = b.FLOW_CODE and lastversion.version = b.version);


/*
CREATE  or replace  VIEW f_v_wf_optdef_url_map AS
  select concat(c.opt_url,b.OPT_URL) AS optdef_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code,b.OPT_DESC AS opt_desc,
    b.OPT_METHOD AS opt_Method,c.Opt_ID AS opt_id,b.OPT_NAME AS Opt_Name
  from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID)))
  where ((c.Opt_Type = 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;

*/


CREATE   or replace VIEW v_inner_user_task_list AS
  select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,
    w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,nvl(a.UNIT_CODE,nvl(w.UNIT_CODE,'0000000')) AS Unit_Code,a.USER_CODE AS user_code,
    c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTH_DESC,c.NODE_CODE AS node_code,
    c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,c.OPT_PARAM AS Opt_Param,
    a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,
    c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,
    w.INST_STATE AS inst_state, a.NODE_PARAM,c.OS_ID
  from wf_node_instance a join wf_flow_instance w on (a.FLOW_INST_ID = w.FLOW_INST_ID)
    join wf_node c on (a.NODE_ID = c.NODE_ID)
  where (a.NODE_STATE = 'N' and w.INST_STATE = 'N' and a.TASK_ASSIGNED = 'S')
  union all
  select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,
    w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,nvl(a.UNIT_CODE,nvl(w.UNIT_CODE,'0000000')) AS UnitCode,b.USER_CODE AS user_code,
    b.ROLE_TYPE AS ROLE_TYPE,b.ROLE_CODE AS ROLE_CODE,b.AUTH_DESC AS AUTH_DESC,c.NODE_CODE AS node_code,
    c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,c.OPT_PARAM AS Opt_Param,
    a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,
    c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,
    w.INST_STATE AS inst_state, a.NODE_PARAM,c.OS_ID
  from wf_node_instance a join wf_flow_instance w on a.FLOW_INST_ID = w.FLOW_INST_ID
    join wf_action_task b on a.NODE_INST_ID = b.NODE_INST_ID
    join wf_node c on a.NODE_ID = c.NODE_ID
  where a.NODE_STATE = 'N' and w.INST_STATE = 'N' and a.TASK_ASSIGNED = 'T' and b.IS_VALID = 'T'
    and b.TASK_STATE = 'A' and (b.EXPIRE_TIME is null or b.EXPIRE_TIME > sysdate);
/*  union all
  select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,b.UNIT_CODE AS Unit_Code,b.USER_CODE AS user_code,c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTHDESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODEOPTTYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS MethodName,d.optdef_url AS OptUrl,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state
from wf_node_instance a
join wf_flow_instance w on a.FLOW_INST_ID = w.FLOW_INST_ID
join wf_node c on a.NODE_ID = c.NODE_ID
join f_v_wf_optdef_url_map d on c.OPT_CODE = d.opt_code
join f_userunit b on b.user_code=a.user_code
where a.NODE_STATE = 'N'
and w.INST_STATE = 'N'
and a.TASK_ASSIGNED = 'D'
and (a.UNIT_CODE is null or a.UNIT_CODE = b.UNIT_CODE)
 and (c.ROLE_CODE = b.User_Station or c.ROLE_TYPE = 'xz')
 and (c.ROLE_CODE = b.User_Rank or c.ROLE_TYPE = 'gw');*/

CREATE or replace   VIEW v_user_task_list AS
  select rownum as taskid,tt.* from (
    select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version AS version,a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
           a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,a.Unit_Code AS Unit_Code,a.user_code AS user_code,
           a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS AUTH_DESC,a.node_code AS node_code,
           a.Node_Name AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE, a.Opt_Param AS Opt_Param,
           a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT AS time_limit,a.OPT_CODE AS OPT_CODE,
           a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,NULL AS GRANTOR,a.last_update_user AS last_update_user,
           a.LAST_UPDATE_TIME AS LAST_UPDATE_TIME,a.inst_state AS inst_state,a.OPT_CODE as opt_url, a.NODE_PARAM,a.os_id
    from v_inner_user_task_list a
  union
    select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version AS version,a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
           a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,a.Unit_Code AS Unit_Code,a.user_code AS user_code,
           a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS AUTH_DESC,a.node_code AS node_code,
           a.Node_Name AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE, a.Opt_Param AS Opt_Param,
           a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT AS time_limit,a.OPT_CODE AS OPT_CODE,
           a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,b.GRANTOR AS GRANTOR,a.last_update_user AS last_update_user,
           a.LAST_UPDATE_TIME AS last_update_time,a.inst_state AS inst_state,a.OPT_CODE as opt_url, a.NODE_PARAM,a.os_id
    from v_inner_user_task_list a join wf_role_relegate b on b.unit_code=a.UNIT_CODE
    where b.IS_VALID = 'T' and b.RELEGATE_TIME <= sysdate and a.user_code = b.GRANTOR
          and (b.EXPIRE_TIME is null or b.EXPIRE_TIME >= sysdate)
          and (b.UNIT_CODE is null or b.UNIT_CODE = a.Unit_Code)
          and (b.ROLE_TYPE is null or b.ROLE_TYPE = a.ROLE_TYPE)
          and (b.ROLE_CODE is null or b.ROLE_CODE = a.ROLE_CODE)
  )tt;







