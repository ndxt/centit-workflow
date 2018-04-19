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


DROP TABLE IF EXISTS approval_auditor;
DROP TABLE IF EXISTS approval_event;
DROP TABLE IF EXISTS approval_process;
DROP TABLE IF EXISTS f_address_book;
DROP TABLE IF EXISTS f_datacatalog;
DROP TABLE IF EXISTS f_datadictionary;
DROP TABLE IF EXISTS f_optdatascope;
DROP TABLE IF EXISTS f_optdef;
DROP TABLE IF EXISTS f_optflownoinfo;
DROP TABLE IF EXISTS f_optflownopool;
DROP TABLE IF EXISTS f_optinfo;
DROP TABLE IF EXISTS f_opt_log;
DROP TABLE IF EXISTS f_opt_variable;
DROP TABLE IF EXISTS f_query_filter_condition;
DROP TABLE IF EXISTS f_rankgrant;
DROP TABLE IF EXISTS f_roleinfo;
DROP TABLE IF EXISTS f_rolepower;
DROP TABLE IF EXISTS f_stat_month;
DROP TABLE IF EXISTS f_sys_notify;
DROP TABLE IF EXISTS f_unitinfo;
DROP TABLE IF EXISTS f_unitrole;
DROP TABLE IF EXISTS f_userinfo;
DROP TABLE IF EXISTS f_userrole;
DROP TABLE IF EXISTS f_usersetting;
DROP TABLE IF EXISTS f_userunit;
DROP TABLE IF EXISTS f_user_favorite;
DROP TABLE IF EXISTS f_user_query_filter;
DROP TABLE IF EXISTS f_work_class;
DROP TABLE IF EXISTS f_work_day;
DROP TABLE IF EXISTS m_innermsg;
DROP TABLE IF EXISTS m_innermsg_recipient;
DROP TABLE IF EXISTS m_msgannex;
DROP TABLE IF EXISTS opt_node;
DROP TABLE IF EXISTS opt_stage;
DROP TABLE IF EXISTS opt_team_role;
DROP TABLE IF EXISTS opt_variable;
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

CREATE TABLE approval_auditor (
  AUDITOR_ID varchar(32) NOT NULL COMMENT '主键',
  APPROVAL_ID varchar(32) NOT NULL,
  PHASE_NO decimal(4,0) DEFAULT NULL COMMENT '阶段审批人',
  USER_CODE varchar(32) DEFAULT NULL COMMENT '审批人代码',
  IS_PRIMARY_AUDITOR varchar(1) DEFAULT NULL COMMENT 'Y / N yes or no',
  PRIMARY KEY (AUDITOR_ID)
) ;




CREATE TABLE approval_event (
  APPROVAL_ID varchar(32) NOT NULL,
  FLOW_INST_ID decimal(12,0) DEFAULT NULL,
  EVENT_TITLE varchar(100) DEFAULT NULL COMMENT '审批事件标题',
  EVENT_DESC varchar(500) DEFAULT NULL COMMENT '审批事件说明',
  REQUEST_TIME datetime DEFAULT NULL COMMENT '申请时间',
  CURRENT_PHASE decimal(4,0) DEFAULT NULL COMMENT '当前阶段',
  APPROVAL_STATE varchar(1) DEFAULT NULL COMMENT '状态 A 申请， B 审核中 C 审核通过 D 审核未通过',
  COMPLETE_TIME datetime DEFAULT NULL COMMENT '办结时间',
  RESULT_DESC varchar(500) DEFAULT NULL COMMENT '结果说明',
  PRIMARY KEY (APPROVAL_ID)
) ;


CREATE TABLE approval_process (
  PROCESS_ID varchar(32) NOT NULL COMMENT '主键',
  APPROVAL_ID varchar(32) NOT NULL,
  NODE_INST_ID decimal(12,0) DEFAULT NULL,
  PHASE_NO decimal(4,0) DEFAULT NULL COMMENT '阶段审批人',
  USER_CODE varchar(32) DEFAULT NULL COMMENT '审批人代码',
  AUDIT_RESULT varchar(1) DEFAULT NULL COMMENT '审核是否通过 Y / N yes or no',
  RESULT_DESC varchar(500) DEFAULT NULL COMMENT '审核说明',
  PRIMARY KEY (PROCESS_ID)
) ;


CREATE TABLE f_address_book (
  ADDRBOOKID decimal(10,0) NOT NULL,
  BodyType varchar(2) NOT NULL COMMENT '用户/个人/单位',
  BodyCode varchar(16) NOT NULL COMMENT '用户/个人/单位 编号',
  representation varchar(200) DEFAULT NULL,
  UnitName varchar(200) DEFAULT NULL,
  DeptName varchar(100) DEFAULT NULL,
  RankName varchar(50) DEFAULT NULL,
  Email varchar(60) DEFAULT NULL,
  Email2 varchar(60) DEFAULT NULL,
  Email3 varchar(60) DEFAULT NULL,
  HomePage varchar(100) DEFAULT NULL,
  QQ varchar(20) DEFAULT NULL,
  MSN varchar(60) DEFAULT NULL,
  wangwang varchar(20) DEFAULT NULL,
  buzPhone varchar(20) DEFAULT NULL,
  buzphone2 varchar(20) DEFAULT NULL,
  buzfax varchar(20) DEFAULT NULL,
  assiphone varchar(20) DEFAULT NULL,
  callbacphone varchar(20) DEFAULT NULL,
  carphone varchar(20) DEFAULT NULL,
  unitphone varchar(20) DEFAULT NULL,
  homephone varchar(20) DEFAULT NULL,
  homephone2 varchar(20) DEFAULT NULL,
  homephone3 varchar(20) DEFAULT NULL,
  homefax varchar(20) DEFAULT NULL,
  mobilephone varchar(20) DEFAULT NULL,
  mobilephone2 varchar(20) DEFAULT NULL,
  mobilephone3 varchar(20) DEFAULT NULL,
  unitzip varchar(8) DEFAULT NULL,
  unitProvince varchar(20) DEFAULT NULL,
  unitCity varchar(20) DEFAULT NULL,
  unitDistrict varchar(20) DEFAULT NULL,
  unitStreet varchar(20) DEFAULT NULL,
  unitAddress varchar(60) DEFAULT NULL,
  homezip varchar(8) DEFAULT NULL,
  homeProvince varchar(20) DEFAULT NULL,
  homeCity varchar(20) DEFAULT NULL,
  homeDistrict varchar(20) DEFAULT NULL,
  homeStreet varchar(20) DEFAULT NULL,
  homeAddress varchar(60) DEFAULT NULL,
  home2zip varchar(8) DEFAULT NULL,
  home2Province varchar(20) DEFAULT NULL,
  home2City varchar(20) DEFAULT NULL,
  home2District varchar(20) DEFAULT NULL,
  home2Street varchar(20) DEFAULT NULL,
  home2Address varchar(60) DEFAULT NULL,
  inuseAddress varchar(1) DEFAULT NULL COMMENT '单位/住宅/住宅2',
  SearchString varchar(1000) DEFAULT NULL COMMENT '前面各个字段的中文首字母，数字 连接的串',
  memo varchar(500) DEFAULT NULL,
  LastModifyDate datetime DEFAULT NULL,
  CreateDate datetime DEFAULT NULL,
  PRIMARY KEY (ADDRBOOKID)
) ;



CREATE TABLE f_datacatalog (
  CATALOG_CODE varchar(16) NOT NULL,
  CATALOG_NAME varchar(64) NOT NULL,
  CATALOG_STYLE char(1) NOT NULL COMMENT 'F : 框架固有的 U:用户 S：系统  G国标',
  CATALOG_TYPE char(1) NOT NULL COMMENT 'T：树状表格 L:列表\r\n            ',
  CATALOG_DESC varchar(256) DEFAULT NULL,
  Field_Desc varchar(1024) DEFAULT NULL COMMENT '字段描述，不同字段用分号隔开',
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  opt_ID varchar(16) DEFAULT NULL COMMENT '业务分类，使用数据字典DICTIONARYTYPE中数据',
  need_Cache char(1) DEFAULT '1',
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (CATALOG_CODE)
)  ;




CREATE TABLE f_datadictionary (
  CATALOG_CODE varchar(16) NOT NULL,
  DATA_CODE varchar(16) NOT NULL,
  EXTRA_CODE varchar(16) DEFAULT NULL COMMENT '树型字典的父类代码',
  EXTRA_CODE2 varchar(16) DEFAULT NULL COMMENT '默认的排序字段',
  DATA_TAG char(1) DEFAULT NULL COMMENT 'N正常，D已停用，用户可以自解释这个字段',
  DATA_VALUE varchar(2048) DEFAULT NULL,
  DATA_STYLE char(1) DEFAULT NULL COMMENT 'F : 框架固有的 U:用户 S：系统  G国标',
  DATA_DESC varchar(256) DEFAULT NULL,
  Last_Modify_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  DATA_ORDER decimal(6,0) DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (CATALOG_CODE,DATA_CODE)
) ;





CREATE TABLE f_optdatascope (
  opt_Scope_Code varchar(16) NOT NULL,
  Opt_ID varchar(16) DEFAULT NULL,
  scope_Name varchar(64) DEFAULT NULL,
  Filter_Condition varchar(1024) DEFAULT NULL COMMENT '条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码',
  scope_Memo varchar(1024) DEFAULT NULL COMMENT '数据权限说明',
  Filter_Group varchar(16) DEFAULT 'G',
  PRIMARY KEY (opt_Scope_Code)
) ;



CREATE TABLE f_optdef (
  OPT_CODE varchar(32) NOT NULL,
  Opt_ID varchar(32) DEFAULT NULL,
  OPT_NAME varchar(100) DEFAULT NULL,
  OPT_METHOD varchar(50) DEFAULT NULL COMMENT '操作参数 方法',
  OPT_URL varchar(256) DEFAULT NULL,
  OPT_DESC varchar(256) DEFAULT NULL,
  opt_Order decimal(4,0) DEFAULT NULL,
  Is_In_Workflow char(1) DEFAULT NULL COMMENT '是否为流程操作方法 F：不是  T ： 是',
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  OPT_REQ varchar(8) DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (OPT_CODE)
) ;



CREATE TABLE f_optflownoinfo (
  Owner_Code varchar(8) NOT NULL,
  Code_Code varchar(16) NOT NULL,
  Code_Date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Cur_No decimal(6,0) NOT NULL DEFAULT '1',
  Last_Code_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  Last_Modify_Date datetime DEFAULT NULL,
  PRIMARY KEY (Owner_Code,Code_Date,Code_Code)
) ;


CREATE TABLE f_optflownopool (
  Owner_Code varchar(8) NOT NULL,
  Code_Code varchar(16) NOT NULL,
  Code_Date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Cur_No decimal(6,0) NOT NULL DEFAULT '1',
  Create_Date datetime DEFAULT NULL,
  PRIMARY KEY (Owner_Code,Code_Date,Code_Code,Cur_No)
) ;



CREATE TABLE f_optinfo (
  Opt_ID varchar(32) NOT NULL,
  Opt_Name varchar(100) NOT NULL,
  Pre_Opt_ID varchar(32) NOT NULL,
  opt_Route varchar(256) DEFAULT NULL COMMENT '与angularjs路由匹配',
  opt_url varchar(256) DEFAULT NULL,
  Form_Code varchar(4) DEFAULT NULL,
  Opt_Type char(1) DEFAULT NULL COMMENT ' S:实施业务, O:普通业务, W:流程业务, I :项目业务',
  Msg_No decimal(10,0) DEFAULT NULL,
  Msg_Prm varchar(256) DEFAULT NULL,
  Is_In_ToolBar char(1) DEFAULT NULL,
  Img_Index decimal(10,0) DEFAULT NULL,
  Top_Opt_ID varchar(8) DEFAULT NULL,
  Order_Ind decimal(4,0) DEFAULT NULL COMMENT '这个顺序只需在同一个父业务下排序',
  FLOW_CODE varchar(8) DEFAULT NULL COMMENT '同一个代码的流程应该只有一个有效的版本',
  Page_Type char(1) NOT NULL DEFAULT 'I' COMMENT 'D : DIV I:iFrame',
  Icon varchar(512) DEFAULT NULL,
  height decimal(10,0) DEFAULT NULL,
  width decimal(10,0) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (Opt_ID)
) ;


CREATE TABLE f_opt_log (
  log_Id decimal(12,0) NOT NULL,
  log_Level varchar(2) NOT NULL,
  user_code varchar(8) NOT NULL,
  opt_time datetime NOT NULL,
  Opt_Content varchar(1000) NOT NULL COMMENT '操作描述',
  New_Value text COMMENT '新值',
  Old_Value text COMMENT '原值',
  Opt_ID varchar(64) NOT NULL COMMENT '模块，或者表',
  OPT_Method varchar(64) DEFAULT NULL COMMENT '方法，或者字段',
  opt_Tag varchar(200) DEFAULT NULL COMMENT '一般用于关联到业务主体的标识、表的主键等等',
  PRIMARY KEY (log_Id)
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


CREATE TABLE f_query_filter_condition (
  CONDITION_NO decimal(12,0) NOT NULL,
  Table_Class_Name varchar(64) NOT NULL COMMENT '数据库表代码或者po的类名',
  Param_Name varchar(64) NOT NULL COMMENT '参数名',
  Param_Label varchar(120) NOT NULL COMMENT '参数输入框提示',
  Param_Type varchar(8) DEFAULT NULL COMMENT '参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月',
  Default_Value varchar(100) DEFAULT NULL,
  Filter_Sql varchar(200) DEFAULT NULL COMMENT '过滤语句，将会拼装到sql语句中',
  Select_Data_type char(1) NOT NULL DEFAULT 'N' COMMENT '数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取\r\n            ',
  Select_Data_Catalog varchar(64) DEFAULT NULL COMMENT '数据字典',
  Select_SQL varchar(1000) DEFAULT NULL COMMENT '有两个返回字段的sql语句',
  Select_JSON varchar(2000) DEFAULT NULL COMMENT 'KEY,Value数值对，JSON格式',
  PRIMARY KEY (CONDITION_NO)
) ;


CREATE TABLE f_rankgrant (
  RANK_grant_ID decimal(12,0) NOT NULL,
  granter varchar(8) NOT NULL,
  UNITCODE varchar(6) NOT NULL,
  UserStation varchar(4) NOT NULL,
  UserRank varchar(2) NOT NULL COMMENT 'RANK 代码不是 0开头的可以进行授予',
  beginDate datetime NOT NULL,
  grantee varchar(8) NOT NULL,
  endDate datetime DEFAULT NULL,
  grantDesc varchar(256) DEFAULT NULL,
  LastModifyDate datetime DEFAULT NULL,
  CreateDate datetime DEFAULT NULL,
  PRIMARY KEY (RANK_grant_ID,UserRank)
) ;


CREATE TABLE f_roleinfo (
  ROLE_CODE varchar(32) NOT NULL,
  ROLE_NAME varchar(64) DEFAULT NULL,
  ROLE_TYPE char(1) NOT NULL COMMENT 'F 为系统 固有的 G 全局的 P 公用的 D 部门的 I 为项目角色 W工作量角色',
  UNIT_CODE varchar(32) DEFAULT NULL,
  IS_VALID char(1) NOT NULL,
  ROLE_DESC varchar(256) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (ROLE_CODE)
) ;


CREATE TABLE f_rolepower (
  ROLE_CODE varchar(32) NOT NULL,
  OPT_CODE varchar(32) NOT NULL,
  opt_Scope_Codes varchar(1000) DEFAULT NULL COMMENT '用逗号隔开的数据范围结合（空all 表示全部）',
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (ROLE_CODE,OPT_CODE)
) ;


CREATE TABLE f_stat_month (
  YEARMONTH varchar(6) NOT NULL COMMENT 'YYYYMM',
  BeginDay datetime NOT NULL,
  EendDay datetime NOT NULL,
  EndSchedule char(1) DEFAULT NULL COMMENT '这个字段忽略',
  BeginSchedule char(1) DEFAULT NULL COMMENT '这个字段忽略',
  PRIMARY KEY (YEARMONTH)
)  ;



CREATE TABLE f_sys_notify (
  Notify_ID decimal(12,0) NOT NULL,
  Notify_Sender varchar(100) DEFAULT NULL,
  Notify_Receiver varchar(100) NOT NULL,
  Msg_Subject varchar(200) DEFAULT NULL,
  Msg_Content varchar(2000) NOT NULL,
  notice_Type varchar(100) DEFAULT NULL,
  Notify_State char(1) DEFAULT NULL COMMENT '0 成功， 1 失败 2 部分成功',
  Error_Msg varchar(500) DEFAULT NULL,
  Notify_Time datetime DEFAULT NULL,
  opt_Tag varchar(200) DEFAULT NULL COMMENT '一般用于关联到业务主体',
  OPT_Method varchar(64) DEFAULT NULL COMMENT '方法，或者字段',
  Opt_ID varchar(64) NOT NULL COMMENT '模块，或者表',
  PRIMARY KEY (Notify_ID)
) ;



CREATE TABLE f_unitinfo (
  UNIT_CODE varchar(32) NOT NULL,
  PARENT_UNIT varchar(32) DEFAULT NULL,
  UNIT_TYPE char(1) DEFAULT NULL COMMENT '发布任务/ 邮电规划/组队/接收任务',
  IS_VALID char(1) NOT NULL COMMENT 'T:生效 F:无效',
  UNIT_TAG varchar(100) DEFAULT NULL COMMENT '用户第三方系统管理',
  UNIT_NAME varchar(300) NOT NULL,
  english_Name varchar(300) DEFAULT NULL,
  dep_no varchar(100) DEFAULT NULL COMMENT '组织机构代码：',
  UNIT_DESC varchar(256) DEFAULT NULL,
  ADDRBOOK_ID decimal(10,0) DEFAULT NULL,
  UNIT_SHORT_NAME varchar(32) DEFAULT NULL,
  unit_Word varchar(100) DEFAULT NULL,
  unit_Grade decimal(4,0) DEFAULT NULL,
  unit_Order decimal(4,0) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  extJsonInfo varchar(1000) DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  UNIT_PATH varchar(1000) DEFAULT NULL,
  UNIT_MANAGER varchar(32) DEFAULT NULL,
  PRIMARY KEY (UNIT_CODE)
) ;


CREATE TABLE f_unitrole (
  UNIT_CODE varchar(32) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  OBTAIN_DATE datetime NOT NULL,
  SECEDE_DATE datetime DEFAULT NULL,
  CHANGE_DESC varchar(256) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (UNIT_CODE,ROLE_CODE)
) ;


CREATE TABLE f_userinfo (
  USER_CODE varchar(32) NOT NULL,
  USER_PIN varchar(100) DEFAULT NULL,
  USER_TYPE char(1) DEFAULT 'U' COMMENT '发布任务/接收任务/系统管理',
  IS_VALID char(1) NOT NULL COMMENT 'T:生效 F:无效',
  LOGIN_NAME varchar(100) NOT NULL,
  User_Name varchar(300) NOT NULL COMMENT '昵称',
  USER_TAG varchar(100) DEFAULT NULL COMMENT '用于第三方系统关联',
  english_Name varchar(300) DEFAULT NULL,
  USER_DESC varchar(256) DEFAULT NULL,
  Login_Times decimal(6,0) DEFAULT NULL,
  Active_Time datetime DEFAULT NULL,
  Login_IP varchar(16) DEFAULT NULL,
  ADDRBOOK_ID decimal(10,0) DEFAULT NULL,
  Reg_Email varchar(60) DEFAULT NULL COMMENT '注册用Email，不能重复',
  USER_PWD varchar(20) DEFAULT NULL COMMENT '如果需要可以有',
  pwd_Expired_Time datetime DEFAULT NULL,
  REG_CELL_PHONE varchar(15) DEFAULT NULL,
  ID_CARD_NO varchar(20) DEFAULT NULL,
  primary_Unit varchar(32) DEFAULT NULL,
  user_Word varchar(100) DEFAULT NULL COMMENT '微信号',
  user_Order decimal(4,0) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  extJsonInfo varchar(1000) DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (USER_CODE)
) ;


CREATE TABLE f_userrole (
  USER_CODE varchar(32) NOT NULL,
  ROLE_CODE varchar(32) NOT NULL,
  OBTAIN_DATE datetime NOT NULL,
  SECEDE_DATE datetime DEFAULT NULL,
  CHANGE_DESC varchar(256) DEFAULT NULL,
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (USER_CODE,ROLE_CODE)
) ;


CREATE TABLE f_usersetting (
  USER_CODE varchar(32) NOT NULL COMMENT 'DEFAULT:为默认设置\r\n            SYS001~SYS999: 为系统设置方案\r\n            是一个用户号,或者是系统的一个设置方案',
  Param_Code varchar(32) NOT NULL,
  Param_Value varchar(2048) NOT NULL,
  opt_ID varchar(16) NOT NULL,
  Param_Name varchar(200) DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  PRIMARY KEY (USER_CODE,Param_Code)
) ;


CREATE TABLE f_userunit (
  USER_UNIT_ID varchar(32) NOT NULL,
  UNIT_CODE varchar(32) NOT NULL,
  USER_CODE varchar(32) NOT NULL,
  Is_Primary char(1) NOT NULL DEFAULT '1' COMMENT 'T：为主， F：兼职',
  User_Station varchar(16) NOT NULL,
  User_Rank varchar(16) NOT NULL COMMENT 'RANK 代码不是 0开头的可以进行授予',
  Rank_Memo varchar(256) DEFAULT NULL COMMENT '任职备注',
  USER_ORDER decimal(8,0) DEFAULT '0',
  update_Date datetime DEFAULT NULL,
  Create_Date datetime DEFAULT NULL,
  creator varchar(32) DEFAULT NULL,
  updator varchar(32) DEFAULT NULL,
  PRIMARY KEY (USER_UNIT_ID)
)  ;



CREATE TABLE f_user_favorite (
  USERCODE varchar(8) NOT NULL COMMENT 'DEFAULT:为默认设置\r\n            SYS001~SYS999: 为系统设置方案\r\n            是一个用户号,或者是系统的一个设置方案',
  OptID varchar(16) NOT NULL,
  LastModifyDate datetime DEFAULT NULL,
  CreateDate datetime DEFAULT NULL,
  PRIMARY KEY (USERCODE,OptID)
) ;



CREATE TABLE f_user_query_filter (
  FILTER_NO decimal(12,0) NOT NULL,
  user_Code varchar(8) NOT NULL,
  modle_code varchar(64) NOT NULL COMMENT '开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法',
  filter_name varchar(200) NOT NULL COMMENT '用户自行定义的名称',
  filter_value varchar(3200) NOT NULL COMMENT '变量值，json格式，对应一个map',
  PRIMARY KEY (FILTER_NO)
) ;



CREATE TABLE f_work_class (
  CLASS_ID decimal(12,0) NOT NULL,
  CLASS_NAME varchar(50) NOT NULL,
  SHORT_NAME varchar(10) NOT NULL,
  begin_time varchar(6) DEFAULT NULL COMMENT '9:00',
  end_time varchar(6) DEFAULT NULL COMMENT '+4:00 ''+''表示第二天',
  has_break char(1) DEFAULT NULL,
  break_begin_time varchar(6) DEFAULT NULL COMMENT '9:00',
  break_end_time varchar(6) DEFAULT NULL COMMENT '+4:00 ''+''表示第二天',
  class_desc varchar(500) DEFAULT NULL,
  record_date datetime DEFAULT NULL,
  recorder varchar(8) DEFAULT NULL,
  PRIMARY KEY (CLASS_ID)
)  ;



CREATE TABLE f_work_day (
  WorkDay datetime NOT NULL,
  DayType char(1) NOT NULL COMMENT 'A:工作日放假，B:周末调休成工作时间 C 正常上班 D正常休假',
  WorkTimeType varchar(20) DEFAULT NULL,
  WorkDayDesc varchar(255) DEFAULT NULL,
  PRIMARY KEY (WorkDay)
)  ;



CREATE TABLE m_innermsg (
  Msg_Code varchar(16) NOT NULL COMMENT '消息主键自定义，通过S_M_INNERMSG序列生成',
  Sender varchar(128) DEFAULT NULL,
  Send_Date datetime DEFAULT NULL,
  Msg_Title varchar(128) DEFAULT NULL,
  Msg_Type char(1) DEFAULT NULL COMMENT 'P= 个人为消息  A= 机构为公告（通知）\r\n            M=邮件',
  Mail_Type char(1) DEFAULT NULL COMMENT 'I=收件箱\r\n            O=发件箱\r\n            D=草稿箱\r\n            T=废件箱\r\n\r\n\r\n            ',
  Mail_UnDel_Type char(1) DEFAULT NULL,
  Receive_Name varchar(2048) DEFAULT NULL COMMENT '使用部门，个人中文名，中间使用英文分号分割',
  Hold_Users decimal(8,0) DEFAULT NULL COMMENT '总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录\r\n\r\n            消息类型为邮件时不需要设置',
  msg_State char(1) DEFAULT NULL COMMENT '未读/已读/删除',
  msg_Content longblob,
  Email_Id varchar(8) DEFAULT NULL COMMENT '用户配置多邮箱时使用',
  Opt_ID varchar(64) NOT NULL COMMENT '模块，或者表',
  OPT_Method varchar(64) DEFAULT NULL COMMENT '方法，或者字段',
  opt_Tag varchar(200) DEFAULT NULL COMMENT '一般用于关联到业务主体',
  PRIMARY KEY (Msg_Code)
)  ;


CREATE TABLE m_innermsg_recipient (
  Msg_Code varchar(16) NOT NULL,
  Receive varchar(8) NOT NULL,
  Reply_Msg_Code int(11) DEFAULT NULL,
  Receive_Type char(1) DEFAULT NULL COMMENT 'P=个人为消息\r\n            A=机构为公告\r\n            M=邮件',
  Mail_Type char(1) DEFAULT NULL COMMENT 'T=收件人\r\n            C=抄送\r\n            B=密送',
  msg_State char(1) DEFAULT NULL COMMENT '未读/已读/删除，收件人在线时弹出提示\r\n\r\n            U=未读\r\n            R=已读\r\n            D=删除',
  ID varchar(16) NOT NULL,
  PRIMARY KEY (ID)
)  ;



CREATE TABLE m_msgannex (
  Msg_Code varchar(16) NOT NULL,
  Info_Code varchar(16) NOT NULL,
  Msg_Annex_Id varchar(16) NOT NULL,
  PRIMARY KEY (Msg_Annex_Id)
) ;



CREATE TABLE opt_node (
  OPT_NODE_ID varchar(32) NOT NULL,
  OPT_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  OPT_NODE_NAME varchar(100) NOT NULL,
  OPT_URL varchar(100) DEFAULT NULL,
  OPT_TYPE varchar(50) DEFAULT NULL,
  OPT_NODE_ORDER smallint(6) DEFAULT NULL,
  GMT_CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  GMT_MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OPT_NODE_ID)
) ;



CREATE TABLE opt_stage (
  OPT_STAGE_ID varchar(32) NOT NULL,
  OPT_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  OPT_STAGE_NAME varchar(100) NOT NULL,
  OPT_STAGE_CODE varchar(100) NOT NULL,
  OPT_STAGE_ORDER smallint(6) DEFAULT NULL,
  GMT_CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  GMT_MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OPT_STAGE_ID)
) ;



CREATE TABLE opt_team_role (
  OPT_TEAM_ROLE_ID varchar(32) NOT NULL,
  OPT_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  OPT_ROLE_CODE varchar(100) NOT NULL,
  OPT_ROLE_NAME varchar(100) NOT NULL,
  OPT_TEAM_ROLE_ORDER smallint(6) DEFAULT NULL,
  GMT_CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  GMT_MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OPT_TEAM_ROLE_ID)
) ;



CREATE TABLE opt_variable (
  OPT_VARIABLE_ID varchar(32) NOT NULL,
  OPT_ID varchar(32) NOT NULL,
  FLOW_CODE varchar(32) NOT NULL,
  OPT_VARIABLE_NAME varchar(100) NOT NULL,
  OPT_VARIABLE_TYPE varchar(100) DEFAULT NULL,
  OPT_VARIABLE_ORDER smallint(6) DEFAULT NULL,
  GMT_CREATE_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  GMT_MODIFY_TIME datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OPT_VARIABLE_ID)
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



CREATE or replace VIEW lastversion AS select wf_flow_define.FLOW_CODE AS FLOW_CODE,max(wf_flow_define.version) AS version from wf_flow_define group by wf_flow_define.FLOW_CODE ;



CREATE or replace  VIEW f_v_lastversionflow AS select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE from ((lastversion join wf_flow_define a on(((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0)))) join wf_flow_define b on(((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version)))) ;



create or replace view F_V_USERROLES as
select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'D' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
      b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,a.USER_CODE, NULL as INHERITED_FROM
    from F_USERROLE a join F_ROLEINFO b on (a.ROLE_CODE=b.ROLE_CODE)
    where a.OBTAIN_DATE <=  now() and (a.SECEDE_DATE is null or a.SECEDE_DATE > now()) and b.IS_VALID='T'
union
  select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'I' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
        b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,c.USER_CODE, a.UNIT_CODE as INHERITED_FROM
    from F_UNITROLE a join F_ROLEINFO b on (a.ROLE_CODE = b.ROLE_CODE) JOIN F_USERUNIT c on( a.UNIT_CODE = c.UNIT_CODE)
    where a.OBTAIN_DATE <=  now() and (a.SECEDE_DATE is null or a.SECEDE_DATE > now()) and b.IS_VALID='T';


CREATE  or replace  VIEW f_v_optdef_url_map AS select concat(c.opt_url,b.OPT_URL) AS opt_def_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type <> 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;



CREATE  or replace  VIEW f_v_opt_role_map AS select concat(c.opt_url,b.OPT_URL) AS opt_url,b.OPT_REQ AS opt_req,a.ROLE_CODE AS role_code,c.Opt_ID AS opt_id,b.OPT_CODE AS opt_code from ((f_rolepower a join f_optdef b on((a.OPT_CODE = b.OPT_CODE))) join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type <> 'W') and (c.opt_url <> '...')) order by c.opt_url,b.OPT_REQ,a.ROLE_CODE ;



CREATE  or replace  VIEW f_v_useroptdatascopes AS select distinct a.USER_CODE AS User_Code,c.Opt_ID AS OPT_ID,c.OPT_METHOD AS OPT_METHOD,b.opt_Scope_Codes AS opt_Scope_Codes from ((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) ;



CREATE  or replace  VIEW f_v_useroptlist AS select distinct a.USER_CODE AS User_Code,c.OPT_CODE AS OPT_CODE,c.OPT_NAME AS OPT_NAME,c.Opt_ID AS OPT_ID,c.OPT_METHOD AS OPT_METHOD from ((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) ;



CREATE   or replace VIEW f_v_useroptmoudlelist AS select distinct a.USER_CODE AS User_Code,d.Opt_ID AS Opt_ID,d.Opt_Name AS Opt_Name,d.Pre_Opt_ID AS Pre_Opt_ID,d.Form_Code AS Form_Code,d.opt_url AS opt_url,d.opt_Route AS opt_route,d.Msg_No AS Msg_No,d.Msg_Prm AS Msg_Prm,d.Is_In_ToolBar AS Is_In_ToolBar,d.Img_Index AS Img_Index,d.Top_Opt_ID AS Top_Opt_ID,d.Order_Ind AS Order_Ind,d.Page_Type AS Page_Type,d.Opt_Type AS opt_type from (((f_v_userroles a join f_rolepower b on((a.ROLE_CODE = b.ROLE_CODE))) join f_optdef c on((b.OPT_CODE = c.OPT_CODE))) join f_optinfo d on((c.Opt_ID = d.Opt_ID))) where (d.opt_url <> '...') ;



CREATE   or replace VIEW f_v_wf_optdef_url_map AS select concat(c.opt_url,b.OPT_URL) AS optdef_url,b.OPT_REQ AS opt_req,b.OPT_CODE AS opt_code,b.OPT_DESC AS opt_desc,b.OPT_METHOD AS opt_Method,c.Opt_ID AS opt_id,b.OPT_NAME AS Opt_Name from (f_optdef b join f_optinfo c on((b.Opt_ID = c.Opt_ID))) where ((c.Opt_Type = 'W') and (c.opt_url <> '...') and (b.OPT_REQ is not null)) ;



CREATE  or replace  VIEW v_hi_unitinfo AS select a.UNIT_CODE AS top_unit_code,b.UNIT_CODE AS unit_code,b.UNIT_TYPE AS unit_type,b.PARENT_UNIT AS parent_unit,b.IS_VALID AS is_valid,b.UNIT_NAME AS unit_name,b.UNIT_DESC AS unit_desc,b.UNIT_SHORT_NAME AS unit_short_name,b.ADDRBOOK_ID AS addrbook_id,b.unit_Order AS unit_order,b.dep_no AS dep_no,b.unit_Word AS unit_word,b.unit_Grade AS unit_grade,((((length(b.UNIT_PATH) - length(replace(b.UNIT_PATH,'/',''))) - length(a.UNIT_PATH)) + length(replace(a.UNIT_PATH,'/',''))) + 1) AS hi_level,substr(b.UNIT_PATH,(length(a.UNIT_PATH) + 1)) AS Unit_Path from (f_unitinfo a join f_unitinfo b) where (b.UNIT_PATH like concat(a.UNIT_PATH,'%')) ;



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



CREATE  or replace  VIEW v_opt_tree AS select i.Opt_ID AS MENU_ID,i.Pre_Opt_ID AS PARENT_ID,i.Opt_Name AS MENU_NAME,i.Order_Ind AS order_ind from f_optinfo i where (i.Is_In_ToolBar = 'Y') union all select d.OPT_CODE AS MENU_ID,d.Opt_ID AS PARENT_ID,d.OPT_NAME AS MENU_NAME,0 AS order_ind from f_optdef d;





