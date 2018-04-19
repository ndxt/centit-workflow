create sequence S_ADDRESSID;
create sequence S_FLOWDEFINE;
create sequence S_FLOWDEFNO;
create sequence S_MSGCODE;
create sequence S_OPTDEFCODE;
create sequence S_RECIPIENT;
create sequence S_ROLECODE;
create sequence S_SYS_LOG;
create sequence S_UNITCODE;
create sequence S_USERCODE;
create sequence S_USER_UNIT_ID;


CREATE TABLE approval_auditor (
  AUDITOR_ID varchar2(32) NOT NULL ,
  APPROVAL_ID varchar2(32) NOT NULL,
  PHASE_NO number(4,0) DEFAULT NULL ,
  USER_CODE varchar2(32) DEFAULT NULL ,
  IS_PRIMARY_AUDITOR varchar2(1) DEFAULT NULL ,
  PRIMARY KEY (AUDITOR_ID)
) ;




CREATE TABLE approval_event (
  APPROVAL_ID varchar2(32) NOT NULL,
  FLOW_INST_ID number(12,0) DEFAULT NULL,
  EVENT_TITLE varchar2(100) DEFAULT NULL ,
  EVENT_DESC varchar2(500) DEFAULT NULL ,
  REQUEST_TIME date DEFAULT NULL ,
  CURRENT_PHASE number(4,0) DEFAULT NULL ,
  APPROVAL_STATE varchar2(1) DEFAULT NULL ,
  COMPLETE_TIME date DEFAULT NULL ,
  RESULT_DESC varchar2(500) DEFAULT NULL ,
  PRIMARY KEY (APPROVAL_ID)
) ;


CREATE TABLE approval_process (
  PROCESS_ID varchar2(32) NOT NULL ,
  APPROVAL_ID varchar2(32) NOT NULL,
  NODE_INST_ID number(12,0) DEFAULT NULL,
  PHASE_NO number(4,0) DEFAULT NULL ,
  USER_CODE varchar2(32) DEFAULT NULL ,
  AUDIT_RESULT varchar2(1) DEFAULT NULL ,
  RESULT_DESC varchar2(500) DEFAULT NULL ,
  PRIMARY KEY (PROCESS_ID)
) ;


CREATE TABLE f_address_book (
  ADDRBOOKID number(10,0) NOT NULL,
  BodyType varchar2(2) NOT NULL ,
  BodyCode varchar2(16) NOT NULL ,
  representation varchar2(200) DEFAULT NULL,
  UnitName varchar2(200) DEFAULT NULL,
  DeptName varchar2(100) DEFAULT NULL,
  RankName varchar2(50) DEFAULT NULL,
  Email varchar2(60) DEFAULT NULL,
  Email2 varchar2(60) DEFAULT NULL,
  Email3 varchar2(60) DEFAULT NULL,
  HomePage varchar2(100) DEFAULT NULL,
  QQ varchar2(20) DEFAULT NULL,
  MSN varchar2(60) DEFAULT NULL,
  wangwang varchar2(20) DEFAULT NULL,
  buzPhone varchar2(20) DEFAULT NULL,
  buzphone2 varchar2(20) DEFAULT NULL,
  buzfax varchar2(20) DEFAULT NULL,
  assiphone varchar2(20) DEFAULT NULL,
  callbacphone varchar2(20) DEFAULT NULL,
  carphone varchar2(20) DEFAULT NULL,
  unitphone varchar2(20) DEFAULT NULL,
  homephone varchar2(20) DEFAULT NULL,
  homephone2 varchar2(20) DEFAULT NULL,
  homephone3 varchar2(20) DEFAULT NULL,
  homefax varchar2(20) DEFAULT NULL,
  mobilephone varchar2(20) DEFAULT NULL,
  mobilephone2 varchar2(20) DEFAULT NULL,
  mobilephone3 varchar2(20) DEFAULT NULL,
  unitzip varchar2(8) DEFAULT NULL,
  unitProvince varchar2(20) DEFAULT NULL,
  unitCity varchar2(20) DEFAULT NULL,
  unitDistrict varchar2(20) DEFAULT NULL,
  unitStreet varchar2(20) DEFAULT NULL,
  unitAddress varchar2(60) DEFAULT NULL,
  homezip varchar2(8) DEFAULT NULL,
  homeProvince varchar2(20) DEFAULT NULL,
  homeCity varchar2(20) DEFAULT NULL,
  homeDistrict varchar2(20) DEFAULT NULL,
  homeStreet varchar2(20) DEFAULT NULL,
  homeAddress varchar2(60) DEFAULT NULL,
  home2zip varchar2(8) DEFAULT NULL,
  home2Province varchar2(20) DEFAULT NULL,
  home2City varchar2(20) DEFAULT NULL,
  home2District varchar2(20) DEFAULT NULL,
  home2Street varchar2(20) DEFAULT NULL,
  home2Address varchar2(60) DEFAULT NULL,
  inuseAddress varchar2(1) DEFAULT NULL ,
  SearchString varchar2(1000) DEFAULT NULL ,
  memo varchar2(500) DEFAULT NULL,
  LastModifyDate date DEFAULT NULL,
  CreateDate date DEFAULT NULL,
  PRIMARY KEY (ADDRBOOKID)
) ;



CREATE TABLE f_datacatalog (
  CATALOG_CODE varchar2(16) NOT NULL,
  CATALOG_NAME varchar2(64) NOT NULL,
  CATALOG_STYLE char(1) NOT NULL ,
  CATALOG_TYPE char(1) NOT NULL ,
  CATALOG_DESC varchar2(256) DEFAULT NULL,
  Field_Desc varchar2(1024) DEFAULT NULL ,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  opt_ID varchar2(16) DEFAULT NULL ,
  need_Cache char(1) DEFAULT '1',
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (CATALOG_CODE)
)  ;




CREATE TABLE f_datadictionary (
  CATALOG_CODE varchar2(16) NOT NULL,
  DATA_CODE varchar2(16) NOT NULL,
  EXTRA_CODE varchar2(16) DEFAULT NULL ,
  EXTRA_CODE2 varchar2(16) DEFAULT NULL ,
  DATA_TAG char(1) DEFAULT NULL ,
  DATA_VALUE varchar2(2048) DEFAULT NULL,
  DATA_STYLE char(1) DEFAULT NULL ,
  DATA_DESC varchar2(256) DEFAULT NULL,
  Last_Modify_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  DATA_ORDER number(6,0) DEFAULT NULL,
  PRIMARY KEY (CATALOG_CODE,DATA_CODE)
) ;




CREATE TABLE f_optdatascope (
  opt_Scope_Code varchar2(16) NOT NULL,
  Opt_ID varchar2(16) DEFAULT NULL,
  scope_Name varchar2(64) DEFAULT NULL,
  Filter_Condition varchar2(1024) DEFAULT NULL ,
  scope_Memo varchar2(1024) DEFAULT NULL ,
  Filter_Group varchar2(16) DEFAULT 'G',
  PRIMARY KEY (opt_Scope_Code)
) ;



CREATE TABLE f_optdef (
  OPT_CODE varchar2(32) NOT NULL,
  Opt_ID varchar2(32) DEFAULT NULL,
  OPT_NAME varchar2(100) DEFAULT NULL,
  OPT_METHOD varchar2(50) DEFAULT NULL ,
  OPT_URL varchar2(256) DEFAULT NULL,
  OPT_DESC varchar2(256) DEFAULT NULL,
  opt_Order number(4,0) DEFAULT NULL,
  Is_In_Workflow char(1) DEFAULT NULL ,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  OPT_REQ varchar2(8) DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (OPT_CODE)
) ;



CREATE TABLE f_optflownoinfo (
  Owner_Code varchar2(8) NOT NULL,
  Code_Code varchar2(16) NOT NULL,
  Code_Date date DEFAULT sysdate,
  Cur_No number(6,0) DEFAULT '1',
  Last_Code_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  Last_Modify_Date date DEFAULT NULL,
  PRIMARY KEY (Owner_Code,Code_Date,Code_Code)
) ;


CREATE TABLE f_optflownopool (
  Owner_Code varchar2(8) NOT NULL,
  Code_Code varchar2(16) NOT NULL,
  Code_Date date  DEFAULT sysdate,
  Cur_No number(6,0)DEFAULT '1',
  Create_Date date DEFAULT NULL,
  PRIMARY KEY (Owner_Code,Code_Date,Code_Code,Cur_No)
) ;



CREATE TABLE f_optinfo (
  Opt_ID varchar2(32) NOT NULL,
  Opt_Name varchar2(100) NOT NULL,
  Pre_Opt_ID varchar2(32) NOT NULL,
  opt_Route varchar2(256) DEFAULT NULL ,
  opt_url varchar2(256) DEFAULT NULL,
  Form_Code varchar2(4) DEFAULT NULL,
  Opt_Type char(1) DEFAULT NULL ,
  Msg_No number(10,0) DEFAULT NULL,
  Msg_Prm varchar2(256) DEFAULT NULL,
  Is_In_ToolBar char(1) DEFAULT NULL,
  Img_Index number(10,0) DEFAULT NULL,
  Top_Opt_ID varchar2(8) DEFAULT NULL,
  Order_Ind number(4,0) DEFAULT NULL ,
  FLOW_CODE varchar2(8) DEFAULT NULL ,
  Page_Type char(1)  DEFAULT 'I' ,
  Icon varchar2(512) DEFAULT NULL,
  height number(10,0) DEFAULT NULL,
  width number(10,0) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (Opt_ID)
) ;


CREATE TABLE f_opt_log (
  log_Id number(12,0) NOT NULL,
  log_Level varchar2(2) NOT NULL,
  user_code varchar2(8) NOT NULL,
  opt_time date NOT NULL,
  Opt_Content varchar2(1000) NOT NULL ,
  New_Value clob ,
  Old_Value clob ,
  Opt_ID varchar2(64) NOT NULL ,
  OPT_Method varchar2(64) DEFAULT NULL ,
  opt_Tag varchar2(200) DEFAULT NULL ,
  PRIMARY KEY (log_Id)
) ;



CREATE TABLE f_opt_variable (
  OptID varchar2(8) NOT NULL,
  VarName varchar2(32) NOT NULL,
  VarDesc varchar2(200) DEFAULT NULL,
  VarType char(1) DEFAULT NULL ,
  DefaultValue varchar2(200) DEFAULT NULL,
  ISVALID char(1) DEFAULT 'T' ,
  PRIMARY KEY (OptID,VarName)
)  ;


CREATE TABLE f_query_filter_condition (
  CONDITION_NO number(12,0) NOT NULL,
  Table_Class_Name varchar2(64) NOT NULL ,
  Param_Name varchar2(64) NOT NULL ,
  Param_Label varchar2(120) NOT NULL ,
  Param_Type varchar2(8) DEFAULT NULL ,
  Default_Value varchar2(100) DEFAULT NULL,
  Filter_Sql varchar2(200) DEFAULT NULL ,
  Select_Data_type char(1)  DEFAULT 'N' ,
  Select_Data_Catalog varchar2(64) DEFAULT NULL ,
  Select_SQL varchar2(1000) DEFAULT NULL ,
  Select_JSON varchar2(2000) DEFAULT NULL ,
  PRIMARY KEY (CONDITION_NO)
) ;


CREATE TABLE f_rankgrant (
  RANK_grant_ID number(12,0) NOT NULL,
  granter varchar2(8) NOT NULL,
  UNITCODE varchar2(6) NOT NULL,
  UserStation varchar2(4) NOT NULL,
  UserRank varchar2(2) NOT NULL ,
  beginDate date NOT NULL,
  grantee varchar2(8) NOT NULL,
  endDate date DEFAULT NULL,
  grantDesc varchar2(256) DEFAULT NULL,
  LastModifyDate date DEFAULT NULL,
  CreateDate date DEFAULT NULL,
  PRIMARY KEY (RANK_grant_ID,UserRank)
) ;


CREATE TABLE f_roleinfo (
  ROLE_CODE varchar2(32) NOT NULL,
  ROLE_NAME varchar2(64) DEFAULT NULL,
  ROLE_TYPE char(1) NOT NULL ,
  UNIT_CODE varchar2(32) DEFAULT NULL,
  IS_VALID char(1) NOT NULL,
  ROLE_DESC varchar2(256) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (ROLE_CODE)
) ;


CREATE TABLE f_rolepower (
  ROLE_CODE varchar2(32) NOT NULL,
  OPT_CODE varchar2(32) NOT NULL,
  opt_Scope_Codes varchar2(1000) DEFAULT NULL ,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (ROLE_CODE,OPT_CODE)
) ;


CREATE TABLE f_stat_month (
  YEARMONTH varchar2(6) NOT NULL ,
  BeginDay date NOT NULL,
  EendDay date NOT NULL,
  EndSchedule char(1) DEFAULT NULL ,
  BeginSchedule char(1) DEFAULT NULL ,
  PRIMARY KEY (YEARMONTH)
)  ;



CREATE TABLE f_sys_notify (
  Notify_ID number(12,0) NOT NULL,
  Notify_Sender varchar2(100) DEFAULT NULL,
  Notify_Receiver varchar2(100) NOT NULL,
  Msg_Subject varchar2(200) DEFAULT NULL,
  Msg_Content varchar2(2000) NOT NULL,
  notice_Type varchar2(100) DEFAULT NULL,
  Notify_State char(1) DEFAULT NULL ,
  Error_Msg varchar2(500) DEFAULT NULL,
  Notify_Time date DEFAULT NULL,
  opt_Tag varchar2(200) DEFAULT NULL ,
  OPT_Method varchar2(64) DEFAULT NULL ,
  Opt_ID varchar2(64) NOT NULL ,
  PRIMARY KEY (Notify_ID)
) ;



CREATE TABLE f_unitinfo (
  UNIT_CODE varchar2(32) NOT NULL,
  PARENT_UNIT varchar2(32) DEFAULT NULL,
  UNIT_TYPE char(1) DEFAULT NULL ,
  IS_VALID char(1) NOT NULL ,
  UNIT_TAG varchar2(100) DEFAULT NULL ,
  UNIT_NAME varchar2(300) NOT NULL,
  english_Name varchar2(300) DEFAULT NULL,
  dep_no varchar2(100) DEFAULT NULL ,
  UNIT_DESC varchar2(256) DEFAULT NULL,
  ADDRBOOK_ID number(10,0) DEFAULT NULL,
  UNIT_SHORT_NAME varchar2(32) DEFAULT NULL,
  unit_Word varchar2(100) DEFAULT NULL,
  unit_Grade number(4,0) DEFAULT NULL,
  unit_Order number(4,0) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  extJsonInfo varchar2(1000) DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  UNIT_PATH varchar2(1000) DEFAULT NULL,
  UNIT_MANAGER varchar2(32) DEFAULT NULL,
  PRIMARY KEY (UNIT_CODE)
) ;


CREATE TABLE f_unitrole (
  UNIT_CODE varchar2(32) NOT NULL,
  ROLE_CODE varchar2(32) NOT NULL,
  OBTAIN_DATE date NOT NULL,
  SECEDE_DATE date DEFAULT NULL,
  CHANGE_DESC varchar2(256) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (UNIT_CODE,ROLE_CODE)
) ;


CREATE TABLE f_userinfo (
  USER_CODE varchar2(32) NOT NULL,
  USER_PIN varchar2(100) DEFAULT NULL,
  USER_TYPE char(1) DEFAULT 'U' ,
  IS_VALID char(1) NOT NULL ,
  LOGIN_NAME varchar2(100) NOT NULL,
  User_Name varchar2(300) NOT NULL ,
  USER_TAG varchar2(100) DEFAULT NULL ,
  english_Name varchar2(300) DEFAULT NULL,
  USER_DESC varchar2(256) DEFAULT NULL,
  Login_Times number(6,0) DEFAULT NULL,
  Active_Time date DEFAULT NULL,
  Login_IP varchar2(16) DEFAULT NULL,
  ADDRBOOK_ID number(10,0) DEFAULT NULL,
  Reg_Email varchar2(60) DEFAULT NULL ,
  USER_PWD varchar2(20) DEFAULT NULL ,
  pwd_Expired_Time date DEFAULT NULL,
  REG_CELL_PHONE varchar2(15) DEFAULT NULL,
  ID_CARD_NO varchar2(20) DEFAULT NULL,
  primary_Unit varchar2(32) DEFAULT NULL,
  user_Word varchar2(100) DEFAULT NULL ,
  user_Order number(4,0) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  extJsonInfo varchar2(1000) DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (USER_CODE)
) ;


CREATE TABLE f_userrole (
  USER_CODE varchar2(32) NOT NULL,
  ROLE_CODE varchar2(32) NOT NULL,
  OBTAIN_DATE date NOT NULL,
  SECEDE_DATE date DEFAULT NULL,
  CHANGE_DESC varchar2(256) DEFAULT NULL,
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (USER_CODE,ROLE_CODE)
) ;


CREATE TABLE f_usersetting (
  USER_CODE varchar2(32) NOT NULL ,
  Param_Code varchar2(32) NOT NULL,
  Param_Value varchar2(2048) NOT NULL,
  opt_ID varchar2(16) NOT NULL,
  Param_Name varchar2(200) DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  PRIMARY KEY (USER_CODE,Param_Code)
) ;


CREATE TABLE f_userunit (
  USER_UNIT_ID varchar2(32) NOT NULL,
  UNIT_CODE varchar2(32) NOT NULL,
  USER_CODE varchar2(32) NOT NULL,
  Is_Primary char(1)  DEFAULT '1' ,
  User_Station varchar2(16) NOT NULL,
  User_Rank varchar2(16) NOT NULL ,
  Rank_Memo varchar2(256) DEFAULT NULL ,
  USER_ORDER number(8,0) DEFAULT '0',
  update_Date date DEFAULT NULL,
  Create_Date date DEFAULT NULL,
  creator varchar2(32) DEFAULT NULL,
  updator varchar2(32) DEFAULT NULL,
  PRIMARY KEY (USER_UNIT_ID)
)  ;



CREATE TABLE f_user_favorite (
  USERCODE varchar2(8) NOT NULL ,
  OptID varchar2(16) NOT NULL,
  LastModifyDate date DEFAULT NULL,
  CreateDate date DEFAULT NULL,
  PRIMARY KEY (USERCODE,OptID)
) ;



CREATE TABLE f_user_query_filter (
  FILTER_NO number(12,0) NOT NULL,
  user_Code varchar2(8) NOT NULL,
  modle_code varchar2(64) NOT NULL ,
  filter_name varchar2(200) NOT NULL ,
  filter_value varchar2(3200) NOT NULL ,
  PRIMARY KEY (FILTER_NO)
) ;



CREATE TABLE f_work_class (
  CLASS_ID number(12,0) NOT NULL,
  CLASS_NAME varchar2(50) NOT NULL,
  SHORT_NAME varchar2(10) NOT NULL,
  begin_time varchar2(6) DEFAULT NULL ,
  end_time varchar2(6) DEFAULT NULL,
  has_break char(1) DEFAULT NULL,
  break_begin_time varchar2(6) DEFAULT NULL ,
  break_end_time varchar2(6) DEFAULT NULL ,
  class_desc varchar2(500) DEFAULT NULL,
  record_date date DEFAULT NULL,
  recorder varchar2(8) DEFAULT NULL,
  PRIMARY KEY (CLASS_ID)
)  ;



CREATE TABLE f_work_day (
  WorkDay date NOT NULL,
  DayType char(1) NOT NULL ,
  WorkTimeType varchar2(20) DEFAULT NULL,
  WorkDayDesc varchar2(255) DEFAULT NULL,
  PRIMARY KEY (WorkDay)
)  ;



CREATE TABLE m_innermsg (
  Msg_Code varchar2(16) NOT NULL ,
  Sender varchar2(128) DEFAULT NULL,
  Send_Date date DEFAULT NULL,
  Msg_Title varchar2(128) DEFAULT NULL,
  Msg_Type char(1) DEFAULT NULL ,
  Mail_Type char(1) DEFAULT NULL ,
  Mail_UnDel_Type char(1) DEFAULT NULL,
  Receive_Name varchar2(2048) DEFAULT NULL ,
  Hold_Users number(8,0) DEFAULT NULL ,
  msg_State char(1) DEFAULT NULL,
  msg_Content blob,
  Email_Id varchar2(8) DEFAULT NULL ,
  Opt_ID varchar2(64) NOT NULL ,
  OPT_Method varchar2(64) DEFAULT NULL ,
  opt_Tag varchar2(200) DEFAULT NULL ,
  PRIMARY KEY (Msg_Code)
)  ;


CREATE TABLE m_innermsg_recipient (
  Msg_Code varchar2(16) NOT NULL,
  Receive varchar2(8) NOT NULL,
  Reply_Msg_Code int DEFAULT NULL,
  Receive_Type char(1) DEFAULT NULL ,
  Mail_Type char(1) DEFAULT NULL ,
  msg_State char(1) DEFAULT NULL ,
  ID varchar2(16) NOT NULL,
  PRIMARY KEY (ID)
)  ;



CREATE TABLE m_msgannex (
  Msg_Code varchar2(16) NOT NULL,
  Info_Code varchar2(16) NOT NULL,
  Msg_Annex_Id varchar2(16) NOT NULL,
  PRIMARY KEY (Msg_Annex_Id)
) ;



CREATE TABLE opt_node (
  OPT_NODE_ID varchar2(32) NOT NULL,
  OPT_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  OPT_NODE_NAME varchar2(100) NOT NULL,
  OPT_URL varchar2(100) DEFAULT NULL,
  OPT_TYPE varchar2(50) DEFAULT NULL,
  OPT_NODE_ORDER int DEFAULT NULL,
  GMT_CREATE_TIME date  DEFAULT sysdate,
  GMT_MODIFY_TIME date  DEFAULT sysdate,
  PRIMARY KEY (OPT_NODE_ID)
) ;



CREATE TABLE opt_stage (
  OPT_STAGE_ID varchar2(32) NOT NULL,
  OPT_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  OPT_STAGE_NAME varchar2(100) NOT NULL,
  OPT_STAGE_CODE varchar2(100) NOT NULL,
  OPT_STAGE_ORDER int DEFAULT NULL,
  GMT_CREATE_TIME date DEFAULT sysdate,
  GMT_MODIFY_TIME date  DEFAULT sysdate,
  PRIMARY KEY (OPT_STAGE_ID)
) ;



CREATE TABLE opt_team_role (
  OPT_TEAM_ROLE_ID varchar2(32) NOT NULL,
  OPT_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  OPT_ROLE_CODE varchar2(100) NOT NULL,
  OPT_ROLE_NAME varchar2(100) NOT NULL,
  OPT_TEAM_ROLE_ORDER int DEFAULT NULL,
  GMT_CREATE_TIME date  DEFAULT sysdate,
  GMT_MODIFY_TIME date DEFAULT sysdate,
  PRIMARY KEY (OPT_TEAM_ROLE_ID)
) ;



CREATE TABLE opt_variable (
  OPT_VARIABLE_ID varchar2(32) NOT NULL,
  OPT_ID varchar2(32) NOT NULL,
  FLOW_CODE varchar2(32) NOT NULL,
  OPT_VARIABLE_NAME varchar2(100) NOT NULL,
  OPT_VARIABLE_TYPE varchar2(100) DEFAULT NULL,
  OPT_VARIABLE_ORDER int DEFAULT NULL,
  GMT_CREATE_TIME date  DEFAULT sysdate,
  GMT_MODIFY_TIME date  DEFAULT sysdate,
  PRIMARY KEY (OPT_VARIABLE_ID)
) ;



CREATE TABLE p_task_list (
  taskid number(12,0) NOT NULL ,
  taskowner varchar2(8) NOT NULL,
  tasktag varchar2(1) NOT NULL,
  taskrank varchar2(1) NOT NULL ,
  taskstatus varchar2(2) NOT NULL ,
  tasktitle varchar2(256) NOT NULL,
  taskmemo varchar2(1000) DEFAULT NULL ,
  tasktype varchar2(8) NOT NULL ,
  OptID varchar2(64) NOT NULL ,
  OPTMethod varchar2(64) DEFAULT NULL ,
  optTag varchar2(200) DEFAULT NULL,
  creator varchar2(32) NOT NULL,
  created date NOT NULL,
  planbegintime date NOT NULL,
  planendtime date DEFAULT NULL,
  begintime date DEFAULT NULL,
  endtime date DEFAULT NULL,
  finishmemo varchar2(1000) DEFAULT NULL ,
  noticeSign varchar2(1) DEFAULT NULL ,
  lastNoticeTime date DEFAULT NULL ,
  taskdeadline date DEFAULT NULL,
  taskvalue varchar2(2048) DEFAULT NULL ,
  PRIMARY KEY (taskid)
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



CREATE or replace VIEW lastversion AS select wf_flow_define.FLOW_CODE AS FLOW_CODE,max(wf_flow_define.version) AS version from wf_flow_define group by wf_flow_define.FLOW_CODE ;



CREATE  or replace VIEW f_v_lastversionflow AS select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE from ((lastversion join wf_flow_define a on(((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0)))) join wf_flow_define b on(((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version)))) ;




create or replace view F_V_USERROLES as
  select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'D' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
    b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,a.USER_CODE, null as INHERITED_FROM
  from F_USERROLE a join F_ROLEINFO b on (a.ROLE_CODE=b.ROLE_CODE)
  where a.OBTAIN_DATE <=  sysdate and (a.SECEDE_DATE is null or a.SECEDE_DATE > sysdate) and b.IS_VALID='T'
  union
  select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'I' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
    b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,c.USER_CODE, a.UNIT_CODE as INHERITED_FROM
  from F_UNITROLE a join F_ROLEINFO b on (a.ROLE_CODE = b.ROLE_CODE) JOIN F_USERUNIT c on( a.UNIT_CODE = c.UNIT_CODE)
  where a.OBTAIN_DATE <=  sysdate and (a.SECEDE_DATE is null or a.SECEDE_DATE > sysdate) and b.IS_VALID='T';


CREATE   or replace VIEW f_v_optdef_url_map AS select concat(c.opt_url,b.OPT_URL) AS opt_def_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type <> 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;



CREATE  or replace  VIEW f_v_opt_role_map AS select concat(c.opt_url,b.OPT_URL) AS opt_url,b.OPT_REQ AS opt_req,a.ROLE_CODE AS role_code,c.Opt_ID AS opt_id,b.OPT_CODE AS opt_code from ((f_rolepower a join f_optdef b on((a.OPT_CODE = b.OPT_CODE))) join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type <> 'W') and (c.opt_url <> '...')) order by c.opt_url,b.OPT_REQ,a.ROLE_CODE ;



CREATE  or replace  VIEW f_v_useroptdatascopes AS select distinct a.USER_CODE AS User_Code,c.Opt_ID AS OPT_ID,c.OPT_METHOD AS OPT_METHOD,b.opt_Scope_Codes AS opt_Scope_Codes from ((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) ;



CREATE  or replace  VIEW f_v_useroptlist AS select distinct a.USER_CODE AS User_Code,c.OPT_CODE AS OPT_CODE,c.OPT_NAME AS OPT_NAME,c.Opt_ID AS OPT_ID,c.OPT_METHOD AS OPT_METHOD from ((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) ;



CREATE  or replace  VIEW f_v_useroptmoudlelist AS select distinct a.USER_CODE AS User_Code,d.Opt_ID AS Opt_ID,d.Opt_Name AS Opt_Name,d.Pre_Opt_ID AS Pre_Opt_ID,d.Form_Code AS Form_Code,d.opt_url AS opt_url,d.opt_Route AS opt_route,d.Msg_No AS Msg_No,d.Msg_Prm AS Msg_Prm,d.Is_In_ToolBar AS Is_In_ToolBar,d.Img_Index AS Img_Index,d.Top_Opt_ID AS Top_Opt_ID,d.Order_Ind AS Order_Ind,d.Page_Type AS Page_Type,d.Opt_Type AS opt_type from (((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) join f_optinfo d on((c.Opt_ID = d.Opt_ID))) where (d.opt_url <> '...') ;



CREATE  or replace  VIEW f_v_wf_optdef_url_map AS select concat(c.opt_url,b.OPT_URL) AS optdef_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code,b.OPT_DESC AS opt_desc,b.OPT_METHOD AS opt_Method,c.Opt_ID AS opt_id,b.OPT_NAME AS Opt_Name from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type = 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;


CREATE   or replace VIEW v_opt_tree AS select i.Opt_ID AS MENU_ID,i.Pre_Opt_ID AS PARENT_ID,i.Opt_Name AS MENU_NAME,i.Order_Ind AS order_ind from f_optinfo i where (i.Is_In_ToolBar = 'Y') union all select d.OPT_CODE AS MENU_ID,d.Opt_ID AS PARENT_ID,d.OPT_NAME AS MENU_NAME,0 AS order_ind from f_optdef d;


CREATE OR REPLACE VIEW v_hi_unitinfo AS
SELECT a.unit_code AS top_unit_code,  b.unit_code,b.unit_type, b.parent_unit, b.is_valid,     b.unit_name,b.unit_desc,b.unit_short_name,b.addrbook_id,b.unit_order,b.dep_no,
       b.unit_word,b.unit_grade,
       LENGTH(b.Unit_Path)- LENGTH(REPLACE(b.Unit_Path,'/','')) - LENGTH(a.Unit_Path) + LENGTH(REPLACE(a.Unit_Path,'/',''))+1  AS hi_level,
       substr(b.Unit_Path ,  LENGTH(a.Unit_Path)+1) AS Unit_Path
  FROM F_UNITINFO a , F_UNITINFO b
 WHERE b.Unit_Path LIKE CONCAT(a.Unit_Path,'%' );

create or replace view v_optdef as
select a.OPT_CODE, a.OPT_NAME as Method_Name , a.OPT_ID, a.OPT_METHOD, a.OPT_DESC,
       b.Opt_Name, b.opt_url, b.Form_Code, b.Opt_Type, b.Msg_No, b.Msg_Prm,
       b.Is_In_ToolBar, b.Img_Index, b.Pre_Opt_ID, b.Top_Opt_ID, b. Order_Ind ,b.FLOW_CODE
from  F_OPTDEF a join  F_OptInfo b on ( a.opt_id=b.opt_id)
where b.Opt_Type = 'W';

CREATE   or replace VIEW v_inner_user_task_list AS select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,nvl(a.UNIT_CODE,nvl(w.UNIT_CODE,'0000000')) AS Unit_Code,a.USER_CODE AS user_code,c.ROLE_TYPE AS ROLE_TYPE,c.ROLE_CODE AS ROLE_CODE,'系统指定' AS AUTH_DESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODE_OPT_TYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS Method_Name,d.optdef_url AS Opt_Url,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state from (((wf_node_instance a join wf_flow_instance w on((a.FLOW_INST_ID = w.FLOW_INST_ID))) join wf_node c on((a.NODE_ID = c.NODE_ID))) join f_v_wf_optdef_url_map d on((c.OPT_CODE = d.opt_code))) where ((a.NODE_STATE = 'N') and (w.INST_STATE = 'N') and (a.TASK_ASSIGNED = 'S')) union all 
select a.FLOW_INST_ID AS FLOW_INST_ID,w.FLOW_CODE AS FLOW_CODE,w.VERSION AS version,w.FLOW_Opt_Name AS FLOW_OPT_NAME,w.FLOW_Opt_Tag AS FLOW_OPT_TAG,a.NODE_INST_ID AS NODE_INST_ID,nvl(a.UNIT_CODE,nvl(w.UNIT_CODE,'0000000')) AS UnitCode,b.USER_CODE AS user_code,b.ROLE_TYPE AS ROLE_TYPE,b.ROLE_CODE AS ROLE_CODE,b.AUTH_DESC AS AUTH_DESC,c.NODE_CODE AS node_code,c.NODE_NAME AS Node_Name,c.NODE_TYPE AS Node_Type,c.OPT_TYPE AS NODEOPTTYPE,d.opt_id AS opt_id,d.Opt_Name AS Opt_Name,d.Opt_Name AS MethodName,d.optdef_url AS OptUrl,d.opt_Method AS opt_Method,c.OPT_PARAM AS Opt_Param,d.opt_desc AS Opt_Desc,a.CREATE_TIME AS CREATE_TIME,a.promise_Time AS Promise_Time,a.time_Limit AS TIME_LIMIT,c.OPT_CODE AS OPT_CODE,c.Expire_Opt AS Expire_Opt,c.STAGE_CODE AS STAGE_CODE,a.last_update_user AS last_update_user,a.last_update_time AS LAST_UPDATE_TIME,w.INST_STATE AS inst_state from
wf_node_instance a join wf_flow_instance w on a.FLOW_INST_ID = w.FLOW_INST_ID
 join wf_action_task b on a.NODE_INST_ID = b.NODE_INST_ID
 join wf_node c on a.NODE_ID = c.NODE_ID
 join f_v_wf_optdef_url_map d on c.OPT_CODE = d.opt_code
 where a.NODE_STATE = 'N'
 and w.INST_STATE = 'N'
 and a.TASK_ASSIGNED = 'T'
 and b.IS_VALID = 'T'
 and b.TASK_STATE = 'A'
 and (b.EXPIRE_TIME is null or b.EXPIRE_TIME > sysdate)
union all 
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
 and c.ROLE_TYPE = 'gw'
 and (c.ROLE_CODE = b.User_Station or c.ROLE_TYPE = 'xz') 
 and c.ROLE_CODE = b.User_Rank ;



CREATE or replace   VIEW v_user_task_list AS select rownum as taskid,tt.* from (select a.FLOW_INST_ID AS FLOW_INST_ID,a.FLOW_CODE AS FLOW_CODE,a.version 
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
   a.LAST_UPDATE_TIME AS last_update_time,a.inst_state AS inst_state from 
   v_inner_user_task_list a join wf_role_relegate b on b.unit_code=a.UNIT_CODE
   where ((b.IS_VALID = 'T') and (b.RELEGATE_TIME <= sysdate) and (b.EXPIRE_TIME is null or (b.EXPIRE_TIME >= sysdate)) and
    (a.user_code = b.GRANTOR) and (b.UNIT_CODE is null or (b.UNIT_CODE = a.Unit_Code)) and (b.ROLE_TYPE is null or
     ((b.ROLE_TYPE = a.ROLE_TYPE) and (b.ROLE_CODE is null or (b.ROLE_CODE = a.ROLE_CODE))))) ) tt;







