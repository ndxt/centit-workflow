DROP TABLE IF EXISTS wf_flow_variable_define;
DROP TABLE IF EXISTS wf_flow_team_role;
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
DROP TABLE IF EXISTS wf_runtime_warning;
DROP TABLE IF EXISTS wf_stage_instance;
DROP TABLE IF EXISTS wf_team;
DROP TABLE IF EXISTS wf_transition;


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


CREATE TABLE wf_action_log (
  ACTION_ID varchar(32) NOT NULL,
  NODE_INST_ID varchar(32) DEFAULT NULL,
  ACTION_TYPE varchar(2) NOT NULL ,
  ACTION_TIME datetime NOT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  GRANTOR varchar(8) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;

-- ----------------------------
-- Table structure for wf_optinfo
-- ----------------------------
DROP TABLE IF EXISTS wf_optinfo;
CREATE TABLE wf_optinfo (
  OPT_ID varchar(32)  not null,
  APPLICATION_ID VARCHAR(32),
  OWNER_UNIT VARCHAR(32),
  OPT_NAME varchar(100) not null,
  OPT_URL varchar(500) ,
  OPT_VIEW_URL varchar(500) ,
  UPDATE_DATE date DEFAULT CURRENT_TIMESTAMP,
  TITLE_TEMPLATE varchar(500) ,
  DEFAULT_FLOW varchar(32),
  PRIMARY KEY (OPT_ID)
) ;

-- ----------------------------
-- Table structure for wf_optpage
-- ----------------------------
DROP TABLE IF EXISTS WF_OPTPAGE;
CREATE TABLE WF_OPTPAGE (
  OPT_CODE VARCHAR(32)  NOT NULL,
  OPT_ID VARCHAR(32) NOT NULL ,
  OPT_NAME VARCHAR(128) NOT NULL,
  PAGE_TYPE CHAR(1),
  OPT_METHOD VARCHAR(50) ,
  PAGE_URL VARCHAR(500) ,
  REQUEST_PARAMS VARCHAR(2000) ,
  REQUEST_BODY VARCHAR(2000) ,
  UPDATE_DATE DATE DEFAULT NULL,
  PRIMARY KEY (OPT_CODE)
) ;


CREATE TABLE wf_action_task (
  TASK_ID varchar(32) NOT NULL,
  NODE_INST_ID varchar(32) DEFAULT NULL,
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
  FIRST_NODE_ID varchar(32),
  Time_Limit varchar(20) DEFAULT NULL ,
  Expire_Opt char(1) DEFAULT NULL ,
  Opt_ID varchar(32) DEFAULT NULL,
  AT_PUBLISH_DATE datetime DEFAULT NULL,
  OS_ID varchar(32) DEFAULT NULL,
  PRIMARY KEY (version,FLOW_CODE)
)  ;


CREATE TABLE wf_flow_instance (
  FLOW_INST_ID varchar(32) NOT NULL,
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
  STAGE_ID varchar(32) NOT NULL,
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
  FLOW_INST_ID varchar(32) NOT NULL,
  Run_Token varchar(20) NOT NULL,
  VAR_NAME varchar(50) NOT NULL,
  VAR_VALUE varchar(1024) NOT NULL,
  Var_Type char(1) NOT NULL,
  PRIMARY KEY (FLOW_INST_ID,Run_Token,VAR_NAME)
) ;


CREATE TABLE wf_inst_attention (
  FLOW_INST_ID varchar(32) NOT NULL,
  user_Code varchar(8) NOT NULL,
  att_set_time datetime DEFAULT NULL,
  att_set_user varchar(8) DEFAULT NULL,
  att_set_Memo varchar(255) DEFAULT NULL,
  PRIMARY KEY (user_Code,FLOW_INST_ID)
)  ;


CREATE TABLE wf_manage_action (
  ACTION_ID varchar(32) NOT NULL,
  FLOW_INST_ID varchar(32) DEFAULT NULL,
  NODE_INST_ID varchar(32) DEFAULT NULL ,
  ACTION_TYPE varchar(2) NOT NULL ,
  ACTION_TIME datetime NOT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL,
  ROLE_CODE varchar(32) DEFAULT NULL,
  ADMIN_DESC varchar(1000) DEFAULT NULL,
  PRIMARY KEY (ACTION_ID)
) ;


CREATE TABLE wf_node (
  NODE_ID varchar(32) NOT NULL ,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  VERSION decimal(4,0) DEFAULT NULL,
  NODE_TYPE varchar(1) NOT NULL ,
  NODE_NAME varchar(120) DEFAULT NULL,
  OPT_TYPE varchar(1) DEFAULT NULL ,
  OS_ID varchar(32) DEFAULT 'TEST' ,
  OPT_ID varchar(32) DEFAULT NULL ,
  OPT_CODE varchar(32) DEFAULT NULL ,
  OPT_BEAN varchar(100) DEFAULT NULL ,
  OPT_PARAM varchar(2048) DEFAULT NULL,
  SUB_FLOW_CODE varchar(8) DEFAULT NULL ,
  NOTICE_TYPE varchar(16) DEFAULT NULL ,
  NOTICE_USER_EXP varchar(512) DEFAULT NULL ,
  NOTICE_MESSAGE varchar(1000) DEFAULT NULL ,
  ROLE_TYPE varchar(8) DEFAULT NULL ,
  ROLE_CODE varchar(32) DEFAULT NULL,
  UNIT_EXP varchar(128) DEFAULT NULL,
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
  STAGE_CODE varchar(32) DEFAULT NULL,
  NODE_CODE varchar(20) DEFAULT NULL,
  RISK_INFO varchar(4) DEFAULT NULL,
  PRIMARY KEY (NODE_ID)
)  ;


CREATE TABLE wf_node_instance (
  NODE_INST_ID varchar(32) NOT NULL,
  FLOW_INST_ID varchar(32) DEFAULT NULL,
  NODE_ID varchar(32) DEFAULT NULL ,
  CREATE_TIME datetime DEFAULT NULL,
  START_TIME datetime DEFAULT NULL,
  is_Timer char(1) DEFAULT NULL,
  promise_Time decimal(10,0) DEFAULT NULL ,
  time_Limit decimal(10,0) DEFAULT NULL,
  PREV_NODE_INST_ID varchar(32) DEFAULT NULL,
  NODE_STATE varchar(2) DEFAULT NULL ,
  SUB_FLOW_INST_ID varchar(32) DEFAULT NULL,
  UNIT_CODE varchar(8) DEFAULT NULL,
  STAGE_CODE varchar(32) DEFAULT NULL,
  ROLE_TYPE varchar(8) DEFAULT NULL ,
  ROLE_CODE varchar(32) DEFAULT NULL,
  USER_CODE varchar(8) DEFAULT NULL,
  NODE_PARAM varchar(128) DEFAULT NULL,
  TRANS_ID varchar(32) DEFAULT NULL ,
  TASK_ASSIGNED varchar(1) DEFAULT 'F' ,
  Run_Token varchar(20) DEFAULT NULL ,
  GRANTOR varchar(8) DEFAULT NULL,
  last_update_user varchar(8) DEFAULT NULL,
  last_update_time datetime DEFAULT NULL,
  TRANS_PATH varchar(256) DEFAULT NULL,
  PRIMARY KEY (NODE_INST_ID)
) ;


CREATE TABLE wf_organize (
  FLOW_INST_ID varchar(32) NOT NULL,
  UNIT_CODE varchar(8) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  UNIT_ORDER decimal(4,0) DEFAULT NULL ,
  AUTH_DESC varchar(255) DEFAULT NULL,
  AUTH_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (FLOW_INST_ID,UNIT_CODE,ROLE_CODE)
) ;



CREATE TABLE wf_role_relegate (
  RELEGATE_NO varchar(32) NOT NULL,
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

CREATE TABLE wf_runtime_warning (
  WARNING_ID varchar(32) NOT NULL ,
  FLOW_INST_ID varchar(32) DEFAULT NULL,
  NODE_INST_ID varchar(32) NOT NULL,
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
  FLOW_INST_ID varchar(32) NOT NULL,
  STAGE_ID varchar(32) NOT NULL,
  STAGE_CODE varchar(32) NOT NULL,
  STAGE_NAME varchar(60) DEFAULT NULL,
  begin_Time datetime DEFAULT NULL,
  stage_Begin char(1) DEFAULT NULL ,
  promise_Time decimal(10,0) DEFAULT NULL ,
  time_Limit decimal(10,0) DEFAULT NULL,
  last_update_time datetime DEFAULT NULL,
  PRIMARY KEY (FLOW_INST_ID,STAGE_ID)
);


CREATE TABLE WF_EVENT_INFO (
   FLOW_EVENT_ID varchar(32) NOT NULL,
   FLOW_INST_ID varchar(32) NOT NULL,
   SENDER_USER varchar(32),
   EVENT_NAME varchar(64) DEFAULT NULL,
   EVENT_PARAM varchar(2000) DEFAULT NULL,
   RECEIVE_TIME datetime,
   OPT_TIME datetime,
   OPT_STATE varchar(1) DEFAULT 'N',
   OPT_RESULT varchar(2000) DEFAULT NULL,
   PRIMARY KEY (FLOW_EVENT_ID)
);


CREATE TABLE wf_team (
  FLOW_INST_ID varchar(32) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  USER_CODE varchar(8) NOT NULL,
  RUN_TOKEN varchar(32) NOT NULL,
  USER_ORDER decimal(4,0) DEFAULT NULL ,
  AUTH_DESC varchar(255) DEFAULT NULL,
  AUTH_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (FLOW_INST_ID,USER_CODE,ROLE_CODE,RUN_TOKEN)
)  ;


CREATE TABLE wf_transition (
  TRANS_ID varchar(32) NOT NULL,
  VERSION decimal(4,0) DEFAULT NULL,
  FLOW_CODE varchar(32) DEFAULT NULL ,
  TRANS_CLASS varchar(4) DEFAULT NULL ,
  TRANS_NAME varchar(120) DEFAULT NULL,
  START_NODE_ID varchar(32) DEFAULT NULL ,
  END_NODE_ID varchar(32) DEFAULT NULL ,
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

DROP VIEW IF EXISTS f_v_lastversionflow;
CREATE VIEW f_v_lastversionflow AS
select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,
       b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,
       a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,
       a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE
from ((((select stat.wf_flow_define.FLOW_CODE AS FLOW_CODE,max(stat.wf_flow_define.version) AS version
    from stat.wf_flow_define group by stat.wf_flow_define.FLOW_CODE)) lastversion
    join stat.wf_flow_define a on (((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0))))
    join stat.wf_flow_define b on (((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version))))
;

/*
DROP VIEW IF EXISTS lastversion;
CREATE  VIEW lastversion AS select wf_flow_define.FLOW_CODE AS FLOW_CODE,max(wf_flow_define.version) AS version
from wf_flow_define group by wf_flow_define.FLOW_CODE ;


DROP VIEW IF EXISTS f_v_lastversionflow;
CREATE VIEW f_v_lastversionflow AS
    select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,
           b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,
           a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,
           a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE
    from ((lastversion join wf_flow_define a on(((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0))))
     join wf_flow_define b on(((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version)))) ;

*/

DROP VIEW IF EXISTS v_inner_user_task_list;
CREATE    VIEW v_inner_user_task_list AS
  select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,
    w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,ifnull(a.UNIT_CODE,ifnull(w.UNIT_CODE,'0000000')) AS Unit_Code,a.USER_CODE AS user_code,
    c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTH_DESC,c.NODE_CODE AS node_code,
    c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,c.OPT_PARAM AS Opt_Param,
    a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT, f.OS_ID,
    c.OPT_ID, c.OPT_CODE AS OPT_CODE, c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,
    a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME, w.INST_STATE AS inst_state, a.NODE_PARAM,
    p.PAGE_URL as opt_url, w.USER_CODE as creator_code
  from wf_node_instance a join wf_flow_instance w on (a.FLOW_INST_ID = w.FLOW_INST_ID)
    join wf_node c on (a.NODE_ID = c.NODE_ID)
    join wf_flow_define f on(w.FLOW_CODE = f.FLOW_CODE and w.VERSION = f.version)
    left join wf_optpage p on (c.OPT_CODE = p.OPT_CODE)
  where c.Node_TYPE= 'C' and a.NODE_STATE = 'N' and w.INST_STATE = 'N' and a.TASK_ASSIGNED = 'S'
  union all
  select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,
    w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,ifnull(a.UNIT_CODE,ifnull(w.UNIT_CODE,'0000000')) AS UnitCode,b.USER_CODE AS user_code,
    c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE, b.AUTH_DESC AS AUTH_DESC,c.NODE_CODE AS node_code,
    c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,c.OPT_PARAM AS Opt_Param,
    a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,f.OS_ID,
    c.OPT_ID, c.OPT_CODE AS OPT_CODE, c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,
    a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state, a.NODE_PARAM,
    p.PAGE_URL as opt_url, w.USER_CODE as creator_code
  from wf_node_instance a join wf_flow_instance w on a.FLOW_INST_ID = w.FLOW_INST_ID
    join wf_action_task b on a.NODE_INST_ID = b.NODE_INST_ID
    join wf_node c on a.NODE_ID = c.NODE_ID
    join wf_flow_define f on(w.FLOW_CODE = f.FLOW_CODE and w.VERSION = f.version)
    left join wf_optpage p on (c.OPT_CODE = p.OPT_CODE)
  where c.Node_TYPE= 'C' and a.NODE_STATE = 'N' and w.INST_STATE = 'N'
    and a.TASK_ASSIGNED = 'T' and b.TASK_STATE = 'A';

-- and (b.EXPIRE_TIME is null or b.EXPIRE_TIME > NOW());

DROP VIEW IF EXISTS v_user_task_list;
CREATE  VIEW v_user_task_list AS
    select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version AS version,a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
           a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,a.Unit_Code AS Unit_Code,a.user_code AS user_code,
           a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS AUTH_DESC,a.node_code AS node_code,
           a.Node_Name AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE, a.Opt_Param AS Opt_Param,
           a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT AS time_limit,a.OPT_CODE AS OPT_CODE,
           a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,NULL AS GRANTOR,a.last_update_user AS last_update_user,
           a.LAST_UPDATE_TIME AS LAST_UPDATE_TIME,a.inst_state AS inst_state,	a.OPT_URL AS OPT_URL, a.NODE_PARAM,a.os_id,
           a.opt_id, a.creator_code
    from v_inner_user_task_list a
  union
    select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version AS version,a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
           a.FLOW_OPT_TAG AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,a.Unit_Code AS Unit_Code,a.user_code AS user_code,
           a.ROLE_TYPE AS ROLE_TYPE,a.ROLE_CODE AS ROLE_CODE,a.AUTH_DESC AS AUTH_DESC,a.node_code AS node_code,
           a.Node_Name AS Node_Name,a.Node_Type AS Node_Type,a.NODE_OPT_TYPE AS NODE_OPT_TYPE, a.Opt_Param AS Opt_Param,
           a.CREATE_TIME AS CREATE_TIME,a.Promise_Time AS promise_time,a.TIME_LIMIT AS time_limit,a.OPT_CODE AS OPT_CODE,
           a.Expire_Opt AS Expire_Opt,a.STAGE_CODE AS STAGE_CODE,b.GRANTOR AS GRANTOR,a.last_update_user AS last_update_user,
           a.LAST_UPDATE_TIME AS last_update_time,a.inst_state AS inst_state,a.OPT_URL AS OPT_URL, a.NODE_PARAM,a.os_id,
           a.opt_id, a.creator_code
    from v_inner_user_task_list a join wf_role_relegate b on b.unit_code=a.UNIT_CODE
    where b.IS_VALID = 'T' and b.RELEGATE_TIME <= now() and a.user_code = b.GRANTOR
          and (b.EXPIRE_TIME is null or b.EXPIRE_TIME >= now())
          and (b.UNIT_CODE is null or b.UNIT_CODE = a.Unit_Code)
          and (b.ROLE_TYPE is null or b.ROLE_TYPE = a.ROLE_TYPE)
          and (b.ROLE_CODE is null or b.ROLE_CODE = a.ROLE_CODE);
/*
DROP VIEW IF EXISTS v_user_task_list;
CREATE VIEW v_user_task_list AS SELECT
    (
        SELECT
            count(0)
        FROM
            v_user_task_list_temp
        WHERE
            (
                v_user_task_list_temp.FLOW_INST_ID <= t.FLOW_INST_ID
            )
    ) AS TASK_ID,
    t.*
FROM
    v_user_task_list_temp t;*/


DROP VIEW IF EXISTS v_inner_user_task_list_fin;
CREATE  VIEW v_inner_user_task_list_fin AS
SELECT a.FLOW_INST_ID AS FLOW_INST_ID, w.FLOW_CODE AS FLOW_CODE, w.VERSION AS version,
	w.FLOW_Opt_Name AS FLOW_OPT_NAME, w.FLOW_Opt_Tag AS FLOW_OPT_TAG, a.NODE_INST_ID AS NODE_INST_ID,
	ifnull( a.UNIT_CODE, ifnull(w.UNIT_CODE, '0000000') ) AS UnitCode,
	a.USER_CODE AS user_code, c.ROLE_TYPE AS ROLE_TYPE, c.ROLE_CODE AS ROLE_CODE,
	'一般任务' AS AUTHDESC, c.NODE_CODE AS node_code, c.NODE_NAME AS Node_Name,
	c.NODE_TYPE AS Node_Type, c.OPT_TYPE AS NODEOPTTYPE, c.OPT_PARAM AS Opt_Param,
	d.optdef_url AS Opt_Url, a.CREATE_TIME AS CREATE_TIME, a.promise_Time AS Promise_Time,
	a.time_Limit AS TIME_LIMIT,
	c.OPT_CODE AS OPT_CODE,
	c.Expire_Opt AS Expire_Opt,
	c.STAGE_CODE AS STAGE_CODE,
	a.last_update_user AS last_update_user,
	a.last_update_time AS LAST_UPDATE_TIME,
	w.INST_STATE AS inst_state
FROM
	(
		(
			(
				wf_node_instance a
				JOIN wf_flow_instance w ON (
					(
						a.FLOW_INST_ID = w.FLOW_INST_ID
					)
				)
			)
			JOIN wf_node c ON (
				(
					a.NODE_ID = c.NODE_ID
				)
			)
		)
		JOIN f_v_wf_optdef_url_map d ON (
			(
				c.OPT_CODE = d.opt_code
			)
		)
	)
WHERE
	(
		(a.NODE_STATE = 'C')
		AND (a.TASK_ASSIGNED = 'S')
	)
UNION ALL
	SELECT
		a.FLOW_INST_ID AS FLOW_INST_ID,
		w.FLOW_CODE AS FLOW_CODE,
		w.VERSION AS version,
		w.FLOW_Opt_Name AS FLOW_OPT_NAME,
		w.FLOW_Opt_Tag AS FLOW_OPT_TAG,
		a.NODE_INST_ID AS NODE_INST_ID,
		ifnull(
			a.UNIT_CODE,
			ifnull(w.UNIT_CODE, '0000000')
		) AS UnitCode,
		b.USER_CODE AS user_code,
		b.ROLE_TYPE AS ROLE_TYPE,
		b.ROLE_CODE AS ROLE_CODE,
		b.AUTH_DESC AS AUTH_DESC,
		c.NODE_CODE AS node_code,
		c.NODE_NAME AS Node_Name,
		c.NODE_TYPE AS Node_Type,
		c.OPT_TYPE AS NODEOPTTYPE,
		c.OPT_PARAM AS Opt_Param,
		d.optdef_url AS Opt_Url,
		a.CREATE_TIME AS CREATE_TIME,
		a.promise_Time AS Promise_Time,
		a.time_Limit AS TIME_LIMIT,
		c.OPT_CODE AS OPT_CODE,
		c.Expire_Opt AS Expire_Opt,
		c.STAGE_CODE AS STAGE_CODE,
		a.last_update_user AS last_update_user,
		a.last_update_time AS LAST_UPDATE_TIME,
		w.INST_STATE AS inst_state
	FROM
		(
			(
				(
					(
						wf_node_instance a
						JOIN wf_flow_instance w ON (
							(
								a.FLOW_INST_ID = w.FLOW_INST_ID
							)
						)
					)
					JOIN wf_action_task b ON (
						(
							a.NODE_INST_ID = b.NODE_INST_ID
						)
					)
				)
				JOIN wf_node c ON (
					(
						a.NODE_ID = c.NODE_ID
					)
				)
			)
			JOIN f_v_wf_optdef_url_map d ON (
				(
					c.OPT_CODE = d.opt_code
				)
			)
		)
	WHERE
		(
			(a.NODE_STATE = 'C')
			AND (a.TASK_ASSIGNED = 'T')
			AND (b.IS_VALID = 'T')
			AND (b.TASK_STATE = 'A')
			AND (
				isnull(b.EXPIRE_TIME)
				OR (b.EXPIRE_TIME > now())
			)
		)
	UNION ALL
		SELECT
			a.FLOW_INST_ID AS FLOW_INST_ID,
			w.FLOW_CODE AS FLOW_CODE,
			w.VERSION AS version,
			w.FLOW_Opt_Name AS FLOW_OPT_NAME,
			w.FLOW_Opt_Tag AS FLOW_OPT_TAG,
			a.NODE_INST_ID AS NODE_INST_ID,
			b.UNIT_CODE AS UnitCode,
			b.USER_CODE AS usercode,
			c.ROLE_TYPE AS ROLE_TYPE,
			c.ROLE_CODE AS ROLE_CODE,
			'系统指定' AS AUTHDESC,
			c.NODE_CODE AS node_code,
			c.NODE_NAME AS Node_Name,
			c.NODE_TYPE AS Node_Type,
			c.OPT_TYPE AS NODEOPTTYPE,
			c.OPT_PARAM AS Opt_Param,
			d.optdef_url AS Opt_Url,
			a.CREATE_TIME AS CREATE_TIME,
			a.promise_Time AS Promise_Time,
			a.time_Limit AS TIME_LIMIT,
			c.OPT_CODE AS OPT_CODE,
			c.Expire_Opt AS Expire_Opt,
			c.STAGE_CODE AS STAGE_CODE,
			a.last_update_user AS last_update_user,
			a.last_update_time AS LAST_UPDATE_TIME,
			w.INST_STATE AS inst_state
		FROM
			(
				(
					(
						(
							wf_node_instance a
							JOIN wf_flow_instance w ON (
								(
									a.FLOW_INST_ID = w.FLOW_INST_ID
								)
							)
						)
						JOIN wf_node c ON (
							(
								a.NODE_ID = c.NODE_ID
							)
						)
					)
					JOIN f_v_wf_optdef_url_map d ON (
						(
							c.OPT_CODE = d.opt_code
						)
					)
				)
				JOIN f_userunit b
			)
		WHERE
			(
				(a.NODE_STATE = 'C')
				AND (a.TASK_ASSIGNED = 'D')
				AND (
					isnull(a.UNIT_CODE)
					OR (
						a.UNIT_CODE = b.UNIT_CODE
					)
				)
				AND (
					(
						(c.ROLE_TYPE = 'gw')
						AND (
							c.ROLE_CODE = b.User_Station
						)
					)
					OR (
						(c.ROLE_TYPE = 'xz')
						AND (
							c.ROLE_CODE = b.User_Rank
						)
					)
				)
			)
;

DROP VIEW IF EXISTS v_user_task_list_fin;
CREATE  VIEW v_user_task_list_fin AS
SELECT
	a.FLOW_INST_ID AS FLOW_INST_ID,
	a.FLOW_CODE AS FLOW_CODE,
	a.version AS version,
	a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
	a.FLOW_OPT_TAG AS FLOW_OPT_TAG,
	a.NODE_INST_ID AS NODE_INST_ID,
	a.UnitCode AS Unit_Code,
	a.user_code AS user_code,
	a.ROLE_TYPE AS ROLE_TYPE,
	a.ROLE_CODE AS ROLE_CODE,
	a.AUTHDESC AS AUTH_DESC,
	a.node_code AS node_code,
	a.Node_Name AS Node_Name,
	a.Node_Type AS Node_Type,
	a.NODEOPTTYPE AS NODE_OPT_TYPE,
	a.Opt_Param AS Opt_Param,
	a.Opt_Url AS Opt_url,
	a.CREATE_TIME AS CREATE_TIME,
	a.Promise_Time AS promise_time,
	a.TIME_LIMIT AS time_limit,
	a.OPT_CODE AS OPT_CODE,
	a.Expire_Opt AS Expire_Opt,
	a.STAGE_CODE AS STAGE_CODE,
	NULL AS GRANTOR,
	a.last_update_user AS last_update_user,
	a.LAST_UPDATE_TIME AS LAST_UPDATE_TIME,
	a.inst_state AS inst_state
FROM
	v_inner_user_task_list_fin a
UNION
	SELECT
		a.FLOW_INST_ID AS FLOW_INST_ID,
		a.FLOW_CODE AS FLOW_CODE,
		a.version AS version,
		a.FLOW_OPT_NAME AS FLOW_OPT_NAME,
		a.FLOW_OPT_TAG AS FLOW_OPT_TAG,
		a.NODE_INST_ID AS node_inst_id,
		a.UnitCode AS Unit_Code,
		b.GRANTEE AS user_code,
		a.ROLE_TYPE AS ROLE_TYPE,
		a.ROLE_CODE AS ROLE_CODE,
		a.AUTHDESC AS AUTH_DESC,
		a.node_code AS node_code,
		a.Node_Name AS Node_Name,
		a.Node_Type AS Node_Type,
		a.NODEOPTTYPE AS NODE_OPT_TYPE,
		a.Opt_Param AS Opt_Param,
		a.Opt_Url AS Opt_url,
		a.CREATE_TIME AS CREATE_TIME,
		a.Promise_Time AS promise_time,
		a.TIME_LIMIT AS time_limit,
		a.OPT_CODE AS OPT_CODE,
		a.Expire_Opt AS Expire_Opt,
		a.STAGE_CODE AS STAGE_CODE,
		b.GRANTOR AS GRANTOR,
		a.last_update_user AS last_update_user,
		a.LAST_UPDATE_TIME AS last_update_time,
		a.inst_state AS inst_state
	FROM
		(
			v_inner_user_task_list_fin a
			JOIN wf_role_relegate b
		)
	WHERE
		(
			(b.IS_VALID = 'T')
			AND (b.RELEGATE_TIME <= now())
			AND (
				isnull(b.EXPIRE_TIME)
				OR (b.EXPIRE_TIME >= now())
			)
			AND (
				a.user_code = b.GRANTOR
			)
			AND (
				isnull(b.UNIT_CODE)
				OR (
					b.UNIT_CODE = a.UnitCode
				)
			)
			AND (
				isnull(b.ROLE_TYPE)
				OR (
					(
						b.ROLE_TYPE = a.ROLE_TYPE
					)
					AND (
						isnull(b.ROLE_CODE)
						OR (
							b.ROLE_CODE = a.ROLE_CODE
						)
					)
				)
			)
		)
;

