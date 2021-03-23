
-- ----------------------------
-- Table structure for f_opt_log
-- ----------------------------
DROP TABLE IF EXISTS f_opt_log;
CREATE TABLE f_opt_log  (
  LOG_ID varchar(32)  NOT NULL COMMENT '日志编码',
  LOG_LEVEL varchar(2)  NOT NULL COMMENT '日志级别',
  USER_CODE varchar(32)  DEFAULT NULL COMMENT '操作人',
  OPT_TIME date NOT NULL COMMENT '操作时间',
  OPT_CONTENT varchar(1000)  NOT NULL COMMENT '操作内容',
  NEW_VALUE text COMMENT '新的内容',
  OLD_VALUE text COMMENT '旧内容',
  OPT_ID varchar(64)  NOT NULL COMMENT '操作菜单',
  OPT_METHOD varchar(64)  DEFAULT NULL COMMENT '操作方法',
  OPT_TAG varchar(200)  DEFAULT NULL COMMENT '操作标签',
  CORRELATION_ID varchar(32)  DEFAULT NULL COMMENT ' ',
  UNIT_CODE varchar(32)  DEFAULT NULL COMMENT '所属机构',
  PRIMARY KEY (LOG_ID) 
) ;

-- ----------------------------
-- Table structure for wf_action_log
-- ----------------------------
DROP TABLE IF EXISTS wf_action_log;
CREATE TABLE wf_action_log  (
  ACTION_ID varchar(32)  NOT NULL COMMENT '活动编号',
  FLOW_INST_ID varchar(32)  DEFAULT NULL COMMENT '流程实例编号',
  NODE_INST_ID varchar(32)  DEFAULT NULL COMMENT '节点实例编号',
  ACTION_TYPE varchar(2)  NOT NULL COMMENT '活动类别',
  ACTION_TIME datetime(0) NOT NULL COMMENT '活动时间',
  USER_CODE varchar(32)  DEFAULT NULL COMMENT '操作用户',
  ROLE_TYPE varchar(8)  DEFAULT NULL COMMENT '角色类别',
  ROLE_CODE varchar(32)  DEFAULT NULL COMMENT '角色代码',
  GRANTOR varchar(8)  DEFAULT NULL COMMENT '委托人',
  LOG_DETAIL varchar(500)  DEFAULT NULL COMMENT '日志详情',
  PRIMARY KEY (ACTION_ID) 
) ;

-- ----------------------------
-- Table structure for wf_action_task
-- ----------------------------
DROP TABLE IF EXISTS wf_action_task;
CREATE TABLE wf_action_task  (
  TASK_ID varchar(32)  NOT NULL COMMENT '活动编号',
  NODE_INST_ID varchar(32)  DEFAULT NULL COMMENT '节点实例编号',
  ASSIGN_TIME datetime(0) NOT NULL COMMENT '分配时间',
  USER_CODE varchar(32)  DEFAULT NULL COMMENT '操作用户',
  TASK_STATE char(1)  DEFAULT NULL COMMENT '活动状态 A:已分配 C:已完成 F:已委托给别人',
  AUTH_DESC varchar(255)  DEFAULT NULL COMMENT '授权说明',
  PRIMARY KEY (TASK_ID) 
) ;

-- ----------------------------
-- Table structure for wf_event_info
-- ----------------------------
DROP TABLE IF EXISTS wf_event_info;
CREATE TABLE wf_event_info  (
  FLOW_EVENT_ID varchar(32)  NOT NULL COMMENT '流程事件编号',
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例编号',
  SENDER_USER varchar(32)  DEFAULT NULL COMMENT '发送用户',
  EVENT_NAME varchar(64)  DEFAULT NULL COMMENT '事件名称',
  EVENT_PARAM varchar(2000)  DEFAULT NULL COMMENT '事件参数',
  RECEIVE_TIME datetime(0) DEFAULT NULL COMMENT '接收时间',
  OPT_TIME datetime(0) DEFAULT NULL COMMENT '处理时间',
  OPT_STATE varchar(1)  DEFAULT 'N' COMMENT 'N:未处理 S：处理成功 F：处理失败 P：需要再次执行 E： 消息失效',
  OPT_RESULT varchar(2000)  DEFAULT NULL COMMENT '结果',
  PRIMARY KEY (FLOW_EVENT_ID) 
) ;

-- ----------------------------
-- Table structure for wf_flow_define
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_define;
CREATE TABLE wf_flow_define  (
  FLOW_CODE varchar(32)  NOT NULL COMMENT '流程代码',
  version decimal(4, 0) NOT NULL DEFAULT 0 COMMENT '流程版本号',
  FLOW_NAME varchar(120)  DEFAULT NULL COMMENT '流程名称',
  FLOW_CLASS varchar(4)  NOT NULL COMMENT '流程类别 N 普通流程，F 自由流程',
  FLOW_Publish_Date datetime(0) DEFAULT NULL COMMENT '发布时间',
  FLOW_STATE char(1)  DEFAULT NULL COMMENT '流程状态 A草稿 E已发布(A,E仅对0版本有效) B正常 C过期 D禁用',
  FLOW_DESC varchar(500)  DEFAULT NULL COMMENT '流程描述',
  FLOW_XML_DESC text COMMENT '流程定义JSON',
  Time_Limit varchar(20)  DEFAULT NULL COMMENT '预期时间',
  Expire_Opt char(1)  DEFAULT NULL COMMENT '逾期处理办法',
  Opt_ID varchar(32)  DEFAULT NULL COMMENT '业务代码',
  AT_PUBLISH_DATE datetime(0) DEFAULT NULL COMMENT '计划发布时间',
  OS_ID varchar(32)  DEFAULT NULL COMMENT '外部业务系统id 等同于 wf_opt_info中的 APPLICATION_ID',
  FIRST_NODE_ID varchar(32)  DEFAULT NULL COMMENT '首节点',
  PRIMARY KEY (version, FLOW_CODE) 
) ;

-- ----------------------------
-- Table structure for wf_flow_instance
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_instance;
CREATE TABLE wf_flow_instance  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '流程版本号',
  FLOW_CODE varchar(32)  DEFAULT NULL COMMENT '流程代码',
  FLOW_Opt_Name varchar(800)  DEFAULT NULL COMMENT '流程业务名称',
  FLOW_Opt_Tag varchar(200)  DEFAULT NULL COMMENT '流程业务标记(业务主键)',
  CREATE_TIME datetime(0) NOT NULL COMMENT '创建时间',
  is_Timer char(1)  DEFAULT NULL COMMENT '是否计时',
  promise_Time decimal(10, 0) DEFAULT NULL COMMENT '承诺完成时间',
  time_Limit decimal(10, 0) DEFAULT NULL COMMENT '剩余时间',
  last_update_user varchar(8)  DEFAULT NULL COMMENT '剩余时间',
  last_update_time varchar(32)  DEFAULT NULL COMMENT '最后更新时间',
  INST_STATE char(1)  DEFAULT NULL COMMENT '流程状态 N:正常 C:完成 F:强制结束 P:暂停(挂起)',
  IS_SUB_INST char(1)  DEFAULT NULL COMMENT '是否是子流程',
  PRE_INST_ID varchar(32)  DEFAULT NULL COMMENT '是否是子流程',
  PRE_NODE_INST_ID varchar(32)  DEFAULT NULL COMMENT '是否是子流程',
  UNIT_CODE varchar(32)  DEFAULT NULL COMMENT '所属机构',
  USER_CODE varchar(32)  DEFAULT NULL COMMENT '创建人',
  FLOW_GROUP_ID varchar(32)  DEFAULT NULL COMMENT '流程组id',
  OS_ID varchar(32)  DEFAULT NULL COMMENT '外部业务系统id',
  OPT_ID varchar(32)  DEFAULT NULL COMMENT '业务编码',
  PRIMARY KEY (FLOW_INST_ID) 
) ;

-- ----------------------------
-- Table structure for wf_flow_instance_group
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_instance_group;
CREATE TABLE wf_flow_instance_group  (
  FLOW_GROUP_ID varchar(32)  NOT NULL COMMENT '流程组id',
  FLOW_GROUP_NAME varchar(200)  DEFAULT NULL COMMENT '流程组名称',
  FLOW_GROUP_DESC varchar(1000)  DEFAULT NULL COMMENT '流程组描述',
  PRIMARY KEY (FLOW_GROUP_ID) 
) ;

-- ----------------------------
-- Table structure for wf_flow_role
-- ----------------------------
-- DROP TABLE IF EXISTS wf_flow_role;
-- CREATE TABLE wf_flow_role  (
--   ROLE_CODE varchar(32)  NOT NULL COMMENT '角色code',
--   ROLE_NAME varchar(100)  DEFAULT NULL COMMENT '角色名称',
--   ROLE_STATE char(1)  DEFAULT NULL COMMENT '角色状态',
--   PRIMARY KEY (ROLE_CODE)
-- ) ;

-- ----------------------------
-- Table structure for wf_flow_role_define
-- ----------------------------
-- DROP TABLE IF EXISTS wf_flow_role_define;
-- CREATE TABLE wf_flow_role_define  (
--   ID varchar(32)  NOT NULL COMMENT '角色code',
--   ROLE_CODE varchar(32)  NOT NULL COMMENT '对应wf_flow_role中得角色code',
--   RELATED_TYPE varchar(100)  DEFAULT NULL COMMENT '关联类型，gw：岗位，xz：职务，js：角色',
--   RELATED_CODE varchar(100)  DEFAULT NULL COMMENT '关联得角色code',
--   PRIMARY KEY (ID)
-- ) ;

-- ----------------------------
-- Table structure for wf_flow_stage
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_stage;
CREATE TABLE wf_flow_stage  (
  STAGE_ID varchar(32)  NOT NULL COMMENT '阶段编号',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '流程版本号',
  FLOW_CODE varchar(32)  DEFAULT NULL COMMENT '流程代码',
  STAGE_CODE varchar(32)  DEFAULT NULL COMMENT '阶段代码',
  STAGE_NAME varchar(60)  DEFAULT NULL COMMENT '阶段名称',
  is_Account_Time char(1)  DEFAULT NULL COMMENT '是否记入执行时间',
  Limit_Type char(1)  DEFAULT NULL COMMENT '期限类别',
  Time_Limit varchar(20)  DEFAULT NULL COMMENT '期限时间',
  Expire_Opt char(1)  DEFAULT NULL COMMENT '逾期处理办法',
  STAGE_ORDER decimal(4, 0) DEFAULT NULL COMMENT '阶段排序',
  PRIMARY KEY (STAGE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_flow_team_role
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_team_role;
CREATE TABLE wf_flow_team_role  (
  FLOW_TEAM_ROLE_ID varchar(32)  NOT NULL COMMENT '定义id',
  FLOW_CODE varchar(32)  NOT NULL COMMENT '流程编码',
  ROLE_CODE varchar(100)  NOT NULL COMMENT '角色编码',
  ROLE_NAME varchar(100)  NOT NULL COMMENT '角色名称',
  TEAM_ROLE_ORDER smallint(6) DEFAULT NULL COMMENT '排序',
  CREATE_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  MODIFY_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '版本号',
  FORMULA_CODE varchar(64)  DEFAULT NULL COMMENT '表达式编码',
  PRIMARY KEY (FLOW_TEAM_ROLE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_flow_variable
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_variable;
CREATE TABLE wf_flow_variable  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  Run_Token varchar(32)  NOT NULL COMMENT '运行令牌',
  VAR_NAME varchar(50)  NOT NULL COMMENT '变量名',
  VAR_VALUE varchar(1024)  NOT NULL COMMENT '变量值',
  Var_Type char(1)  NOT NULL COMMENT '变量类型',
  PRIMARY KEY (FLOW_INST_ID, Run_Token, VAR_NAME) 
) ;

-- ----------------------------
-- Table structure for wf_flow_variable_define
-- ----------------------------
DROP TABLE IF EXISTS wf_flow_variable_define;
CREATE TABLE wf_flow_variable_define  (
  FLOW_VARIABLE_ID varchar(32)  NOT NULL COMMENT '变量id',
  FLOW_CODE varchar(32)  NOT NULL COMMENT '流程编码',
  VARIABLE_NAME varchar(100)  NOT NULL COMMENT '变量名称',
  VARIABLE_TYPE varchar(100)  DEFAULT NULL COMMENT '变量类型',
  MODIFY_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '版本号',
  DEFAULT_VALUE varchar(256)  DEFAULT NULL COMMENT '默认值',
  VARIABLE_DESC varchar(100)  DEFAULT NULL COMMENT '变量中文描述',
  PRIMARY KEY (FLOW_VARIABLE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_inst_attention
-- ----------------------------
DROP TABLE IF EXISTS wf_inst_attention;
CREATE TABLE wf_inst_attention  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  USER_CODE varchar(32)  NOT NULL COMMENT '关注人',
  att_set_time datetime(0) DEFAULT NULL COMMENT '关注设置时间',
  att_set_user varchar(32)  DEFAULT NULL COMMENT '关注设置人员',
  att_set_Memo varchar(255)  DEFAULT NULL COMMENT '设置备注',
  PRIMARY KEY (USER_CODE, FLOW_INST_ID) 
) ;

-- ----------------------------
-- Table structure for wf_node
-- ----------------------------
DROP TABLE IF EXISTS wf_node;
CREATE TABLE wf_node  (
  NODE_ID varchar(32)  NOT NULL COMMENT '节点编号',
  FLOW_CODE varchar(32)  DEFAULT NULL COMMENT '流程代码',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '流程版本号',
  NODE_TYPE varchar(1)  NOT NULL COMMENT '节点类别 C:业务节点  D:自动运行节点  R:路由节点  E:消息相应节点（同步节点）  F:结束  S:子流程',
  NODE_NAME varchar(120)  DEFAULT NULL COMMENT '节点名',
  OPT_TYPE varchar(1)  DEFAULT NULL COMMENT '操作类别 A: 唯一执行人 B: 抢先机制 C: 多人操作 D: 自动执行 E: 哑元（可用于嵌套汇聚，等同于自动执行无动作） S:子流程',
  OS_ID varchar(32)  DEFAULT 'TEST' COMMENT '外部业务系统id',
  OPT_ID varchar(32)  DEFAULT NULL COMMENT '业务编码',
  OPT_CODE varchar(64)  DEFAULT NULL COMMENT '业务操作',
  OPT_BEAN varchar(100)  DEFAULT NULL COMMENT '业务Bean',
  OPT_PARAM varchar(100)  DEFAULT NULL COMMENT '操作参数',
  SUB_FLOW_CODE varchar(64)  DEFAULT NULL COMMENT '子流程',
  NOTICE_TYPE varchar(16)  DEFAULT NULL COMMENT '通知类别',
  ROLE_TYPE varchar(8)  DEFAULT NULL COMMENT '角色类别',
  ROLE_CODE varchar(32)  DEFAULT NULL COMMENT '角色代码',
  UNIT_EXP varchar(128)  DEFAULT NULL COMMENT '机构表达式',
  POWER_EXP varchar(512)  DEFAULT NULL COMMENT '权限表达式',
  multi_Inst_Type char(1)  DEFAULT NULL COMMENT '多实例类型',
  multi_Inst_Param varchar(512)  DEFAULT NULL COMMENT '多实例参数',
  converge_Type char(1)  DEFAULT NULL COMMENT '汇聚条件类别** A 所有都完成，R 至少有X完成，L 至多有X未完成， V 完成比率达到X E 外埠判断',
  converge_Param varchar(64)  DEFAULT NULL COMMENT '汇聚参数',
  NODE_DESC varchar(500)  DEFAULT NULL COMMENT '节点描述',
  is_Account_Time char(1)  DEFAULT NULL COMMENT '是否记入执行时间',
  Limit_Type char(1)  DEFAULT NULL COMMENT '期限类别  I ： 未设置（ignore 在流转线上默认 ）、 N 无 (无期限 none 默认) 、F 每实例固定期限 fix 、C 节点固定期限  cycle、H 继承其他节点期限 hierarchical。',
  Time_Limit varchar(20)  DEFAULT NULL COMMENT '期限时间',
  inherit_Type char(1)  DEFAULT NULL COMMENT '期限继承类别',
  inherit_Node_Code varchar(20)  DEFAULT NULL COMMENT '继承环节代码',
  Expire_Opt char(1)  DEFAULT NULL COMMENT '逾期处理办法',
  Warning_Rule char(1)  DEFAULT NULL COMMENT '预警规则 R：运行时间  L:剩余时间 P：比率',
  Warning_Param varchar(20)  DEFAULT NULL COMMENT '预警参数',
  NOTICE_MESSAGE varchar(1000)  DEFAULT NULL COMMENT '通知消息模板',
  STAGE_CODE varchar(32)  DEFAULT NULL COMMENT '节点阶段',
  NODE_CODE varchar(20)  DEFAULT NULL COMMENT '环节代码',
  RISK_INFO varchar(4)  DEFAULT NULL COMMENT '风险信息',
  NOTICE_USER_EXP varchar(512)  DEFAULT NULL COMMENT ' 通知对象',
  PRIMARY KEY (NODE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_node_instance
-- ----------------------------
DROP TABLE IF EXISTS wf_node_instance;
CREATE TABLE wf_node_instance  (
  NODE_INST_ID varchar(32)  NOT NULL COMMENT '节点实例编号',
  FLOW_INST_ID varchar(32)  DEFAULT NULL COMMENT '流程实例id',
  NODE_ID varchar(32)  DEFAULT NULL COMMENT '节点编号',
  CREATE_TIME datetime(0) DEFAULT NULL COMMENT '创建时间',
  START_TIME datetime(0) DEFAULT NULL COMMENT '开始时间',
  is_Timer char(1)  DEFAULT NULL COMMENT '是否计时',
  promise_Time decimal(10, 0) DEFAULT NULL COMMENT '承诺完成时间',
  time_Limit decimal(10, 0) DEFAULT NULL COMMENT '剩余时间',
  PREV_NODE_INST_ID varchar(32)  DEFAULT NULL COMMENT '上一个节点实例',
  NODE_STATE varchar(2)  DEFAULT NULL COMMENT ' 节点状态 N:正常 B:回退 F:被强制结束 P:暂停 W:等待子流程返回 S:等待前置节点完成',
  SUB_FLOW_INST_ID varchar(32)  DEFAULT NULL COMMENT '子流程实例ID',
  UNIT_CODE varchar(32)  DEFAULT NULL COMMENT '所属机构',
  STAGE_CODE varchar(32)  DEFAULT NULL COMMENT '阶段代码',
  ROLE_TYPE varchar(8)  DEFAULT NULL COMMENT '角色类别',
  ROLE_CODE varchar(32)  DEFAULT NULL COMMENT '角色代码 ',
  USER_CODE varchar(32)  DEFAULT NULL COMMENT '所属人员',
  NODE_PARAM varchar(128)  DEFAULT NULL COMMENT '节点自定义变量',
  TASK_ASSIGNED varchar(1)  DEFAULT 'F' COMMENT '任务分配方式',
  Run_Token varchar(20)  DEFAULT NULL COMMENT '运行令牌',
  GRANTOR varchar(8)  DEFAULT NULL COMMENT '委托人',
  last_update_user varchar(32)  DEFAULT NULL COMMENT '最后更新人',
  last_update_time datetime(0) DEFAULT NULL COMMENT '最后更新时间',
  TRANS_PATH varchar(256)  DEFAULT NULL COMMENT '应用路径',
  PRIMARY KEY (NODE_INST_ID)
) ;

-- ----------------------------
-- Table structure for wf_opt_team_role
-- ----------------------------
DROP TABLE IF EXISTS wf_opt_team_role;
CREATE TABLE wf_opt_team_role  (
  OPT_TEAM_ROLE_ID varchar(32)  NOT NULL COMMENT '主键id',
  OPT_ID varchar(32)  DEFAULT NULL COMMENT '关联的流程业务id',
  ROLE_CODE varchar(100)  DEFAULT NULL COMMENT '办件角色code',
  ROLE_NAME varchar(100)  DEFAULT NULL COMMENT '办件角色名称',
  FORMULA_CODE varchar(100)  DEFAULT NULL COMMENT '办件角色的约束范围',
  TEAM_ROLE_ORDER decimal(4, 0) DEFAULT NULL COMMENT '办件角色排序',
  MODIFY_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (OPT_TEAM_ROLE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_opt_variable_define
-- ----------------------------
DROP TABLE IF EXISTS wf_opt_variable_define;
CREATE TABLE wf_opt_variable_define  (
  OPT_VARIABLE_ID varchar(32)  NOT NULL COMMENT '主键id',
  OPT_ID varchar(32)  DEFAULT NULL COMMENT '关联的流程业务id',
  VARIABLE_NAME varchar(100)  DEFAULT NULL COMMENT '变量名',
  VARIABLE_DESC varchar(100)  DEFAULT NULL COMMENT '变量中文描述',
  VARIABLE_TYPE varchar(100)  DEFAULT NULL COMMENT '变量类型：E:集合 S:单值',
  DEFAULT_VALUE varchar(256)  DEFAULT NULL COMMENT '变量默认值',
  MODIFY_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (OPT_VARIABLE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_optinfo
-- ----------------------------
DROP TABLE IF EXISTS wf_optinfo;
CREATE TABLE wf_optinfo  (
  OPT_ID varchar(32)  NOT NULL COMMENT '业务id',
  OPT_NAME varchar(100)  NOT NULL COMMENT '业务名称',
  OPT_URL varchar(500)  DEFAULT NULL COMMENT '业务地址',
  OPT_VIEW_URL varchar(500)  DEFAULT NULL COMMENT '业务url',
  UPDATE_DATE date DEFAULT NULL COMMENT '更新时间',
  MODEL_ID varchar(64)  DEFAULT NULL COMMENT '自定义表单的模板id',
  TITLE_TEMPLATE varchar(500)  DEFAULT NULL COMMENT '业务标题模板',
  DEFAULT_FLOW varchar(32)  DEFAULT NULL COMMENT '默认流程',
  APPLICATION_ID varchar(32)  DEFAULT NULL COMMENT '对应应用的application_id用于应用隔离',
  OWNER_UNIT varchar(32)  DEFAULT NULL COMMENT '对应于租户id，用户应用隔离',
  PRIMARY KEY (OPT_ID) 
) ;

-- ----------------------------
-- Table structure for wf_optpage
-- ----------------------------
DROP TABLE IF EXISTS wf_optpage;
CREATE TABLE wf_optpage  (
  opt_code varchar(32)  NOT NULL COMMENT '编码',
  opt_id varchar(32)  NOT NULL COMMENT '和flowOptInfo关联',
  opt_name varchar(128)  NOT NULL COMMENT '业务名称',
  opt_method varchar(50)  DEFAULT NULL COMMENT 'pageType=A时生效 C:create-post R:read-get U:update-put D:delete',
  update_date date DEFAULT NULL COMMENT '最后更新时间',
  page_url varchar(500)  DEFAULT NULL COMMENT '页面url',
  page_type char(1)  DEFAULT NULL COMMENT 'C-公司开发的业务，E-外部 需要iframe嵌入， F-自定义表单， A-auto 自行执行http调用， S-执行JavaScript',
  request_params varchar(2000)  DEFAULT NULL COMMENT ' ',
  request_body varchar(2000)  DEFAULT NULL COMMENT ' ',
  PRIMARY KEY (opt_code) 
) ;

-- ----------------------------
-- Table structure for wf_organize
-- ----------------------------
DROP TABLE IF EXISTS wf_organize;
CREATE TABLE wf_organize  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  UNIT_CODE varchar(32)  NOT NULL COMMENT '机构代码',
  ROLE_CODE varchar(32)  NOT NULL COMMENT '机构角色',
  UNIT_ORDER decimal(4, 0) DEFAULT NULL COMMENT '排序号',
  AUTH_DESC varchar(255)  DEFAULT NULL COMMENT '授权说明',
  AUTH_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (FLOW_INST_ID, UNIT_CODE, ROLE_CODE) 
) ;

-- ----------------------------
-- Table structure for wf_role_formula
-- ----------------------------
DROP TABLE IF EXISTS wf_role_formula;
CREATE TABLE wf_role_formula  (
  FORMULA_CODE varchar(64)  NOT NULL COMMENT '表达式编码',
  FORMULA_NAME varchar(200)  DEFAULT NULL COMMENT '表达式名称',
  ROLE_FORMULA varchar(500)  DEFAULT NULL COMMENT '权限表达式',
  ROLE_LEVEL varchar(20)  DEFAULT NULL COMMENT '表达式等级',
  CREATE_TIME datetime(0) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (FORMULA_CODE) 
) ;

-- ----------------------------
-- Table structure for wf_role_relegate
-- ----------------------------
DROP TABLE IF EXISTS wf_role_relegate;
CREATE TABLE wf_role_relegate  (
  RELEGATE_NO varchar(32)  NOT NULL COMMENT '委托编号',
  GRANTOR varchar(32)  DEFAULT NULL COMMENT '委托人',
  GRANTEE varchar(32)  DEFAULT NULL COMMENT '委托人',
  IS_VALID char(1)  NOT NULL DEFAULT 'T' COMMENT '状态',
  Recorder varchar(32)  DEFAULT NULL COMMENT '录入人员',
  RELEGATE_TIME datetime(0) NOT NULL COMMENT '委托开始时间',
  EXPIRE_TIME datetime(0) DEFAULT NULL COMMENT '截止时间',
  UNIT_CODE varchar(32)  DEFAULT NULL COMMENT '委托机构',
  ROLE_TYPE varchar(8)  DEFAULT NULL COMMENT '委托角色类别',
  ROLE_CODE varchar(32)  DEFAULT NULL COMMENT '委托角色',
  Record_Date datetime(0) DEFAULT NULL COMMENT '录入时间',
  grant_Desc varchar(256)  DEFAULT NULL COMMENT '授予说明',
  PRIMARY KEY (RELEGATE_NO) 
) ;

-- ----------------------------
-- Table structure for wf_runtime_warning
-- ----------------------------
DROP TABLE IF EXISTS wf_runtime_warning;
CREATE TABLE wf_runtime_warning  (
  WARNING_ID varchar(32)  NOT NULL COMMENT '预警ID',
  FLOW_INST_ID varchar(32)  DEFAULT NULL COMMENT '流程实例ID',
  NODE_INST_ID varchar(32)  NOT NULL COMMENT '节点实例编号',
  FLOW_STAGE varchar(32)  DEFAULT NULL COMMENT '节点阶段',
  OBJ_TYPE char(1)  DEFAULT NULL COMMENT '预警对象',
  WARNING_TYPE char(1)  DEFAULT NULL COMMENT '预警类别',
  WARNING_STATE char(1)  DEFAULT 'N' COMMENT '预警状态',
  WARNING_CODE varchar(16)  DEFAULT NULL COMMENT '预警代码',
  WARNING_TIME datetime(0) DEFAULT NULL COMMENT '预警时间',
  WARNINGID_MSG varchar(500)  DEFAULT NULL COMMENT '预警内容',
  NOTICE_STATE char(1)  DEFAULT '0' COMMENT '通知状态',
  SEND_MSG_TIME datetime(0) DEFAULT NULL COMMENT '通知消息时间',
  SEND_USERS varchar(100)  DEFAULT NULL COMMENT '通知人员',
  PRIMARY KEY (WARNING_ID) 
) ;

-- ----------------------------
-- Table structure for wf_stage_instance
-- ----------------------------
DROP TABLE IF EXISTS wf_stage_instance;
CREATE TABLE wf_stage_instance  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  STAGE_ID varchar(32)  NOT NULL COMMENT '阶段编号',
  STAGE_CODE varchar(32)  NOT NULL COMMENT '阶段代码',
  begin_Time datetime(0) DEFAULT NULL COMMENT '阶段进入时间',
  stage_Begin char(1)  DEFAULT NULL COMMENT ' 阶段已经进入',
  promise_Time decimal(10, 0) DEFAULT NULL COMMENT '承诺完成时间',
  time_Limit decimal(10, 0) DEFAULT NULL COMMENT '剩余时间',
  last_update_time datetime(0) DEFAULT NULL COMMENT '剩余时间',
  PRIMARY KEY (FLOW_INST_ID, STAGE_ID) 
) ;

-- ----------------------------
-- Table structure for wf_team
-- ----------------------------
DROP TABLE IF EXISTS wf_team;
CREATE TABLE wf_team  (
  FLOW_INST_ID varchar(32)  NOT NULL COMMENT '流程实例ID',
  ROLE_CODE varchar(32)  NOT NULL COMMENT '角色代码',
  USER_CODE varchar(32)  NOT NULL COMMENT '用户代码',
  USER_ORDER decimal(4, 0) DEFAULT NULL COMMENT '排序号',
  AUTH_DESC varchar(255)  DEFAULT NULL COMMENT '授权说明',
  AUTH_TIME datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  RUN_TOKEN varchar(32)  NOT NULL COMMENT '运行令牌',
  PRIMARY KEY (FLOW_INST_ID, USER_CODE, ROLE_CODE, RUN_TOKEN) 
) ;


-- ----------------------------
-- Table structure for wf_transition
-- ----------------------------
DROP TABLE IF EXISTS wf_transition;
CREATE TABLE wf_transition  (
  TRANS_ID varchar(32)  NOT NULL COMMENT '流转编号',
  VERSION decimal(4, 0) DEFAULT NULL COMMENT '流程版本号',
  FLOW_CODE varchar(32)  DEFAULT NULL COMMENT '流程代码',
  TRANS_CLASS varchar(4)  DEFAULT NULL COMMENT '流转类型',
  TRANS_NAME varchar(120)  DEFAULT NULL COMMENT '流转名称',
  START_NODE_ID varchar(32)  DEFAULT NULL COMMENT '源节点编号',
  END_NODE_ID varchar(32)  DEFAULT NULL COMMENT '目标节点编号',
  TRANS_CONDITION varchar(500)  DEFAULT NULL COMMENT '流转条件',
  TRANS_DESC varchar(500)  DEFAULT NULL COMMENT '流转描述',
  is_Account_Time char(1)  DEFAULT NULL COMMENT '是否记入执行时间',
  Limit_Type char(1)  DEFAULT NULL COMMENT '期限类别',
  Time_Limit varchar(20)  DEFAULT NULL COMMENT '期限时间',
  can_ignore char(1)  NOT NULL DEFAULT 'T' COMMENT '是否可以忽略运行',
  PRIMARY KEY (TRANS_ID) 
) ;



ALTER TABLE f_opt_log              comment '操作日志表';
ALTER TABLE wf_action_log          comment '流程操作日志表';
ALTER TABLE wf_action_task         comment '流程人员待办定义表';
ALTER TABLE wf_flow_define         comment '流程定义表';
ALTER TABLE wf_flow_instance       comment '流程实例表';
ALTER TABLE wf_flow_instance_group comment '流程实例分组表';
-- ALTER TABLE wf_flow_role           comment '流程角色(审批中心)表';
-- ALTER TABLE wf_flow_role_define    comment '流程角色定义(审批中心)表';
ALTER TABLE wf_flow_stage          comment '流程阶段表';
ALTER TABLE wf_flow_team_role      comment '流程角色表';
ALTER TABLE wf_flow_variable       comment '流程变量表';
ALTER TABLE wf_flow_variable_define comment '流程变量定义表';
ALTER TABLE wf_inst_attention      comment '流程关注表';
ALTER TABLE wf_node                comment '节点定义表';
ALTER TABLE wf_node_instance       comment '节点实例表';
ALTER TABLE wf_optinfo             comment '流程业务信息表';
ALTER TABLE wf_optpage             comment '流程页面定义表';
ALTER TABLE wf_organize            comment '流程组织结构表';
ALTER TABLE wf_role_formula        comment '权限表达式资源库表';
ALTER TABLE wf_role_relegate       comment '委托表';
ALTER TABLE wf_runtime_warning     comment '流程告警表';
ALTER TABLE wf_stage_instance      comment '流程阶段实例表';
ALTER TABLE wf_team                comment '办件角色表';
ALTER TABLE wf_transition          comment '流程扭转条件定义表';
ALTER TABLE wf_event_info          comment '流程事件信息表';
ALTER TABLE wf_opt_variable_define comment '流程变量定义表(和流程业务关联)';
ALTER TABLE wf_opt_team_role       comment '办件角色表(和流程业务关联)';



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


DROP VIEW IF EXISTS f_v_lastversionflow;
CREATE VIEW f_v_lastversionflow AS
select a.FLOW_CODE AS FLOW_CODE,b.version AS VERSION,a.FLOW_NAME AS FLOW_NAME,a.FLOW_CLASS AS FLOW_CLASS,
       b.FLOW_STATE AS FLOW_STATE,a.FLOW_DESC AS FLOW_DESC,a.FLOW_XML_DESC AS FLOW_XML_DESC,a.Time_Limit AS TIME_LIMIT,
       a.Expire_Opt AS EXPIRE_OPT,a.Opt_ID AS OPT_ID,a.OS_ID AS OS_ID,a.FLOW_Publish_Date AS FLOW_PUBLISH_DATE,
       a.AT_PUBLISH_DATE AS AT_PUBLISH_DATE
from ((((select wf_flow_define.FLOW_CODE AS FLOW_CODE,max(wf_flow_define.version) AS version
    from wf_flow_define group by wf_flow_define.FLOW_CODE)) lastversion
    join wf_flow_define a on (((a.FLOW_CODE = lastversion.FLOW_CODE) and (a.version = 0))))
    join wf_flow_define b on (((lastversion.FLOW_CODE = b.FLOW_CODE) and (lastversion.version = b.version))))
;

