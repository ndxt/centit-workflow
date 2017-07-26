

create table F_Opt_Variable  (
   OptID              VARCHAR2(8)                     not null,
   VarName            VARCHAR2(32)                    not null,
   VarDesc            VARCHAR2(200),
   VarType            CHAR(1),
   DefaultValue       VARCHAR2(200),
   ISVALID              CHAR(1)                        default 'T' not null,
   constraint PK_F_OPT_VARIABLE primary key (OptID, VarName)
);

comment on table F_Opt_Variable is
'业务变量为流程引擎和业务进行数据访问的接口：
由流程引擎 以只读的方式访问';

comment on column F_Opt_Variable.VarType is
'只有3种类型 N：数据 S:字符串 D:日期';

comment on column F_Opt_Variable.ISVALID is
'T:生效 F:无效';

create table WF_ACTION_LOG  (
   ACTION_ID            NUMBER(12,0)                    not null,
   NODE_INST_ID         NUMBER(12,0),
   ACTION_TYPE          VARCHAR2(2)                     not null,
   ACTION_TIME          DATE                            not null,
   USER_CODE            VARCHAR2(8),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   GRANTOR              VARCHAR2(8),
   constraint PK_WF_ACTION_LOG primary key (ACTION_ID)
);

comment on column WF_ACTION_LOG.ACTION_TYPE is
'创建流程同时创建首节点  W
创建节点 C
更改数据 U
提交节点 S
挂起节点 A
唤醒节点 R
终止节点 E';

create table WF_ACTION_TASK  (
   TASK_ID              NUMBER(12,0)                    not null,
   NODE_INST_ID         NUMBER(12,0),
   ASSIGN_TIME          DATE                            not null,
   EXPIRE_TIME          DATE,
   USER_CODE            VARCHAR2(8),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   TASK_STATE           CHAR(1),
   IS_VALID             CHAR(1),
   AUTH_DESC            VARCHAR2(255),
   constraint PK_WF_ACTION_TASK primary key (TASK_ID)
);

comment on column WF_ACTION_TASK.TASK_STATE is
'A:已分配  C：已完成（提交）W: 已委托给别人';

comment on column WF_ACTION_TASK.IS_VALID is
'T  F 可以删除，也可以使失效，根据业务需要设定';

comment on column WF_ACTION_TASK.AUTH_DESC is
'可以是权限引擎自动分配，也可以是管理员指定';

create table WF_FLOW_DEFINE  (
   FLOW_CODE            VARCHAR2(32)                    not null,
   VERSION              NUMBER(4,0)                     not null,
   FLOW_NAME            VARCHAR2(120),
   FLOW_CLASS           VARCHAR2(4)                     not null,
   FLOW_Publish_Date  DATE,
   FLOW_STATE           CHAR(1),
   FLOW_DESC            VARCHAR2(500),
   FLOW_XML_DESC        CLOB,
   Time_Limit         VARCHAR2(20),
   Expire_Opt         CHAR(1),
   Opt_ID             VARCHAR2(32),
   AT_PUBLISH_DATE      DATE,
   constraint PK_WF_FLOW_DEFINE primary key (VERSION, FLOW_CODE)
);

comment on table WF_FLOW_DEFINE is
'现有 业务后有流程，一个流程必需绑定到一个业务，否则，无法找到对应的业务变量来进行流转';

comment on column WF_FLOW_DEFINE.FLOW_CODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_DEFINE.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column WF_FLOW_DEFINE.FLOW_CLASS is
'暂未使用';

comment on column WF_FLOW_DEFINE.FLOW_Publish_Date is
'发布时间，也是启用时间';

comment on column WF_FLOW_DEFINE.FLOW_STATE is
'A 草稿 B 正常 C 过期 D 禁用 E 已发布  (A,E仅对0版本有效)';

comment on column WF_FLOW_DEFINE.FLOW_XML_DESC is
'图形定义界面生成的XML文件';

comment on column WF_FLOW_DEFINE.Time_Limit is
'一小时计，8小时为一天，小数点后面为分钟，比如0.30表示30分钟';

comment on column WF_FLOW_DEFINE.Expire_Opt is
'N：通知， O:不处理 ， S：挂起，E：终止';

create table WF_FLOW_INSTANCE  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   VERSION              NUMBER(4,0),
   FLOW_CODE            VARCHAR2(32),
   FLOW_Opt_Name      VARCHAR2(100),
   FLOW_Opt_Tag       VARCHAR2(100),
   CREATE_TIME          DATE                            not null,
   is_Timer           CHAR(1),
   promise_Time       NUMBER(10,0),
   time_Limit         NUMBER(10,0),
   last_update_user   VARCHAR2(8),
   last_update_time   DATE,
   INST_STATE           CHAR(1),
   IS_SUB_INST          CHAR(1),
   PRE_INST_ID          NUMBER(16,0),
   PRE_NODE_INST_ID     NUMBER(16,0),
   UNIT_CODE            VARCHAR2(8),
   USER_CODE            VARCHAR2(8),
   constraint PK_WF_FLOW_INSTANCE primary key (FLOW_INST_ID)
);

comment on column WF_FLOW_INSTANCE.FLOW_CODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_INSTANCE.FLOW_Opt_Name is
'这个名称用户 查找流程信息';

comment on column WF_FLOW_INSTANCE.FLOW_Opt_Tag is
'这个标记用户 查找流程信息，比如办件代码，有业务系统自己解释';

comment on column WF_FLOW_INSTANCE.promise_Time is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

comment on column WF_FLOW_INSTANCE.INST_STATE is
' N 正常  C 完成   P 暂停 挂起     F 强行结束
';

comment on column WF_FLOW_INSTANCE.IS_SUB_INST is
'Y 是的 N  不是';

create table WF_FLOW_STAGE  (
   STAGE_ID             NUMBER(12,0)                    not null,
   VERSION              NUMBER(4,0),
   FLOW_CODE            VARCHAR2(32),
   STAGE_CODE           VARCHAR2(32)                    not null,
   STAGE_NAME           VARCHAR2(60),
   is_Account_Time    CHAR(1),
   Limit_Type         CHAR(1),
   Time_Limit         VARCHAR2(20),
   Expire_Opt         CHAR(1),
   constraint PK_WF_FLOW_STAGE primary key (STAGE_ID)
);

comment on column WF_FLOW_STAGE.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column WF_FLOW_STAGE.FLOW_CODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_STAGE.is_Account_Time is
'改是否记入（流程）时间 T/F';

comment on column WF_FLOW_STAGE.Limit_Type is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、C 固定期限  cycle';

comment on column WF_FLOW_STAGE.Time_Limit is
'5D4H 这样的表达式';

comment on column WF_FLOW_STAGE.Expire_Opt is
'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）';

create table WF_FLOW_VARIABLE  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   Run_Token          VARCHAR2(20)                    not null,
   VAR_NAME             VARCHAR2(50)                    not null,
   VAR_VALUE            VARCHAR2(256)                   not null,
   Var_Type           CHAR(1)                         not null,
   constraint PK_WF_FLOW_VARIABLE primary key (FLOW_INST_ID, Run_Token, VAR_NAME)
);

create table WF_INST_ATTENTION  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   user_Code          VARCHAR2(8)                     not null,
   att_set_time       DATE,
   att_set_user       VARCHAR2(8),
   att_set_Memo       VARCHAR2(255),
   constraint PK_WF_INST_ATTENTION primary key (user_Code, FLOW_INST_ID)
);

comment on table WF_INST_ATTENTION is
'关注有个问题，就是一个关注人如何才可写多条意见，看来还是要写到 OPT_IDEA_INFO 中



这个表暂时不用，用 工作流 的关注';

create table WF_MANAGE_ACTION  (
   ACTION_ID            NUMBER(12,0)                    not null,
   FLOW_INST_ID         NUMBER(12,0),
   NODE_INST_ID         NUMBER(12,0),
   ACTION_TYPE          VARCHAR2(2)                     not null,
   ACTION_TIME          DATE                            not null,
   USER_CODE            VARCHAR2(8),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   ADMIN_DESC           VARCHAR2(1000),
   constraint SYS_C0021281 primary key (ACTION_ID)
);

comment on column WF_MANAGE_ACTION.NODE_INST_ID is
'如果是对节点实例的管理，这个就有值';

comment on column WF_MANAGE_ACTION.ACTION_TYPE is
'对流程管理操作用大写字母，对节点管理操作用小写字母
  S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
  c: 创建节点  、创建一个游离节点 创建（任意）指定节点
  R  : 流转管理，包括  强行回退  、强行提交   
  T t: 期限管理 、 设置期限
  a: 节点任务管理  分配任务、  删除任务 、  禁用任务
  U u: 变更属性''';

create table WF_NODE  (
   NODE_ID              NUMBER(12,0)                    not null,
   FLOW_CODE            VARCHAR2(32),
   VERSION              NUMBER(4,0),
   NODE_TYPE            VARCHAR2(1)                     not null,
   NODE_NAME            VARCHAR2(120),
   OPT_TYPE             VARCHAR2(1),
   OS_ID                VARCHAR2(32),
   OPT_ID               VARCHAR2(32),
   OPT_CODE             VARCHAR2(32),
   OPT_BEAN             VARCHAR2(100),
   OPT_PARAM            VARCHAR2(100),
   SUB_FLOW_CODE        VARCHAR2(8),
   ROUTER_TYPE          VARCHAR2(1)                    
      constraint CKC_ROUTER_TYPE_WF_NODE check (ROUTER_TYPE is null or (ROUTER_TYPE in ('D','E','G','H','R','S'))),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   UNIT_EXP             VARCHAR2(64),
   POWER_EXP            VARCHAR2(512),
   multiInst_Type     CHAR(1)                        
      constraint CKC_MULTIINST_TYPE_WF_NODE check (multiInst_Type is null or (multiInst_Type in ('D','U','V'))),
   multiInst_Param    VARCHAR2(512),
   converge_Type      CHAR(1),
   converge_Param     VARCHAR2(64),
   NODE_DESC            VARCHAR2(500),
   is_Account_Time    CHAR(1),
   Limit_Type         CHAR(1),
   Time_Limit         VARCHAR2(20),
   inherit_Type       CHAR(1),
   inherit_Node_Code  VARCHAR2(20),
   Expire_Opt         CHAR(1),
   Warning_Rule       CHAR(1)                        
      constraint CKC_WARNING_RULE_WF_NODE check (Warning_Rule is null or (Warning_Rule in ('R','L','P'))),
   Warning_Param      VARCHAR2(20),
   is_Trunk_Line      CHAR(1),
   STAGE_CODE           VARCHAR2(32),
   NODE_CODE            VARCHAR2(20),
   RISK_INFO            VARCHAR2(4),
   constraint PK_WF_NODE primary key (NODE_ID)
);

comment on table WF_NODE is
'2017-6-13 添加了 osid  optid 修改了 OPTcode 为 varchar2(32)';

comment on column WF_NODE.NODE_ID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_NODE.FLOW_CODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_NODE.NODE_TYPE is
'A:开始 B:首节点 C:业务节点  F结束  R: 路由节点 。
这个类别是不可以变更的，在画图页面上可以不显示，首节点不需要设置，在流程发布时和开始节点直接相连的节点就是首节点，这个节点必需是业务节点。';

comment on column WF_NODE.OPT_TYPE is
'A:一般 B:抢先机制 C:多人操作 D: 自动执行 E哑元（可用于嵌套汇聚） S:子流程 ';

comment on column WF_NODE.OS_ID is
'业务系统 跨业务的流程需要';

comment on column WF_NODE.OPT_ID is
'业务系统  OPTINFO的OPTID';

comment on column WF_NODE.OPT_CODE is
'对应 OPT_DEF 的 OPTCODE ，OPTMETHOD 操作方法';

comment on column WF_NODE.OPT_BEAN is
'自动执行节点需要,或者路由判断bean';

comment on column WF_NODE.SUB_FLOW_CODE is
'子流程和业务操作只有一个有效，都是为了指定业务节点的活动';

comment on column WF_NODE.ROUTER_TYPE is
'D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步';

comment on column WF_NODE.ROLE_TYPE is
'xz gw bj  en';

comment on column WF_NODE.multiInst_Type is
'D 机构， U 人员 ， V 变量';

comment on column WF_NODE.multiInst_Param is
'自定义变量表达，用于多实例节点的分支';

comment on column WF_NODE.converge_Type is
'所有都完成， 至少有X完成，至多有X未完成，完成比率达到X ， 外埠判断';

comment on column WF_NODE.is_Account_Time is
'改是否记入时间 T/F';

comment on column WF_NODE.Limit_Type is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
';

comment on column WF_NODE.Time_Limit is
'5D4H 这样的表达式';

comment on column WF_NODE.inherit_Type is
'0 不集成 1 继承前一个节点 2 继承指定节点';

comment on column WF_NODE.inherit_Node_Code is
'继承环节代码';

comment on column WF_NODE.Expire_Opt is
'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）';

comment on column WF_NODE.Warning_Rule is
'R：运行时间  L:剩余时间 P：比率';

comment on column WF_NODE.Warning_Param is
'是一个时间字符串 或者 数值';

comment on column WF_NODE.is_Trunk_Line is
'T / F';

create table WF_NODE_INSTANCE  (
   NODE_INST_ID         NUMBER(12,0)                    not null,
   FLOW_INST_ID         NUMBER(12,0),
   NODE_ID              NUMBER(12,0),
   CREATE_TIME          DATE,
   START_TIME           DATE,
   is_Timer           CHAR(1),
   promise_Time       NUMBER(10,0),
   time_Limit         NUMBER(10,0),
   PREV_NODE_INST_ID    NUMBER(12,0),
   NODE_STATE           VARCHAR2(2),
   SUB_FLOW_INST_ID     NUMBER(12,0),
   UNIT_CODE            VARCHAR2(8),
   STAGE_CODE           VARCHAR2(32),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   USER_CODE            VARCHAR2(8),
   NODE_PARAM           VARCHAR2(128),
   TRANS_ID             NUMBER(12,0),
   TASK_ASSIGNED        VARCHAR2(1)                    default 'F',
   Run_Token          VARCHAR2(20),
   GRANTOR              VARCHAR2(8),
   last_update_user   VARCHAR2(8),
   last_update_time   DATE,
   constraint PK_WF_NODE_INSTANCE primary key (NODE_INST_ID)
);

comment on column WF_NODE_INSTANCE.NODE_ID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_NODE_INSTANCE.promise_Time is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

comment on column WF_NODE_INSTANCE.NODE_STATE is
'     * N 正常  B 已回退    C 完成   F被强制结束 
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成';

comment on column WF_NODE_INSTANCE.ROLE_TYPE is
'xz gw bj  en';

comment on column WF_NODE_INSTANCE.TRANS_ID is
'由哪一条路径创建的';

comment on column WF_NODE_INSTANCE.TASK_ASSIGNED is
'T: 通过 tasklist 分配， D：通过 岗位、行政角色 自动匹配 S：静态代办（usercode)';

comment on column WF_NODE_INSTANCE.Run_Token is
'令牌： T* 表示正常运行的节点  R* 表示游离节点  L* 表示临时插入的节点';

create table WF_ROLE_RELEGATE  (
   RELEGATE_NO          NUMBER(12,0)                    not null,
   GRANTOR              VARCHAR2(8)                     not null,
   GRANTEE              VARCHAR2(8)                     not null,
   IS_VALID             CHAR(1)                        default 'T' not null,
   Recorder           VARCHAR2(8),
   RELEGATE_TIME        DATE                            not null,
   EXPIRE_TIME          DATE,
   UNIT_CODE            VARCHAR2(8),
   ROLE_TYPE            VARCHAR2(8),
   ROLE_CODE            VARCHAR2(32),
   Record_Date        DATE,
   grant_Desc         VARCHAR2(256),
   constraint PK_WF_ROLE_RELEGATE primary key (RELEGATE_NO)
);

comment on column WF_ROLE_RELEGATE.IS_VALID is
'T:生效 F:无效';

create table WF_ROUTER_NODE  (
   NODEID               NUMBER(12,0)                    not null,
   WFCODE               VARCHAR2(32),
   VERSION              NUMBER(4,0),
   ROUTERTYPE           VARCHAR2(1)                     not null,
   NODENAME             VARCHAR2(120),
   NODEDESC             VARCHAR2(500),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   UNITEXP              VARCHAR2(64),
   POWEREXP             VARCHAR2(512),
   SELFDEFPARAM         VARCHAR2(512),
   convergeType       CHAR(1),
   convergeParam      VARCHAR2(64),
   OPTBEAN              VARCHAR2(100),
   constraint PK_WF_ROUTER_NODE primary key (NODEID)
);

comment on column WF_ROUTER_NODE.NODEID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_ROUTER_NODE.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_ROUTER_NODE.ROUTERTYPE is
'D:分支 E:汇聚  G 多实例节点  H并行  R 游离';

comment on column WF_ROUTER_NODE.ROLETYPE is
'xz gw bj  en';

comment on column WF_ROUTER_NODE.SELFDEFPARAM is
'自定义变量表达，用于多实例节点的分支';

comment on column WF_ROUTER_NODE.convergeType is
'所有都完成， 至少有X完成，至多有X未完成，完成比率达到X ， 外埠判断';

comment on column WF_ROUTER_NODE.OPTBEAN is
'自动执行节点需要';

create table WF_RUNTIME_WARNING  (
   WARNING_ID           NUMBER(12,0)                    not null,
   FLOW_INST_ID         NUMBER(12,0),
   NODE_INST_ID         NUMBER(12,0)                    not null,
   FLOW_STAGE           VARCHAR2(4),
   OBJ_TYPE             CHAR(1)                        
      constraint CKC_OBJ_TYPE_WF_RUNTI check (OBJ_TYPE is null or (OBJ_TYPE in ('F','N','P'))),
   WARNING_TYPE         CHAR(1)                        
      constraint CKC_WARNING_TYPE_WF_RUNTI check (WARNING_TYPE is null or (WARNING_TYPE in ('W','A','N','O'))),
   WARNING_STATE        CHAR(1)                        default 'N'
      constraint CKC_WARNING_STATE_WF_RUNTI check (WARNING_STATE is null or (WARNING_STATE in ('D','C','F','N'))),
   WARNING_CODE         VARCHAR2(16),
   WARNING_TIME         DATE,
   WARNINGID_MSG        VARCHAR2(500),
   NOTICE_STATE         CHAR(1)                        default '0'
      constraint CKC_NOTICE_STATE_WF_RUNTI check (NOTICE_STATE is null or (NOTICE_STATE in ('0','1','2'))),
   SEND_MSG_TIME        DATE,
   SEND_USERS           VARCHAR2(100),
   constraint PK_WF_RUNTIME_WARNING primary key (WARNING_ID)
);

comment on column WF_RUNTIME_WARNING.WARNING_ID is
'sequence : S_WARNING_NO';

comment on column WF_RUNTIME_WARNING.OBJ_TYPE is
'F ： 工作流 N ：节点 P：阶段';

comment on column WF_RUNTIME_WARNING.WARNING_TYPE is
'W，预警  A  报警 N 提醒  O 其他';

comment on column WF_RUNTIME_WARNING.WARNING_STATE is
'D 摘牌 C 纠正 F 督办 N 未处理';

comment on column WF_RUNTIME_WARNING.WARNING_CODE is
'ALTER_EXPIRED  : 时间超期报警 
WARN_EXPIRED  : 时间超期预警';

comment on column WF_RUNTIME_WARNING.NOTICE_STATE is
'0 待发送 1 已发送 2 发送消息失败';

comment on column WF_RUNTIME_WARNING.SEND_USERS is
'可以是多个人用逗号隔开';

create table WF_STAGE_INSTANCE  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   STAGE_ID             NUMBER(12,0)                    not null,
   STAGE_CODE           VARCHAR2(32)                    not null,
   STAGE_NAME           VARCHAR2(60),
   begin_Time         DATE,
   stage_Begin        CHAR(1),
   promise_Time       NUMBER(10,0),
   time_Limit         NUMBER(10,0),
   last_update_time   DATE,
   constraint PK_WF_STAGE_INSTANCE primary key (FLOW_INST_ID, STAGE_ID)
);

comment on table WF_STAGE_INSTANCE is
'在流程创建时同时创建这个流程所有的阶段';

comment on column WF_STAGE_INSTANCE.stage_Begin is
'0 还没有进入， 1 已经进入';

comment on column WF_STAGE_INSTANCE.promise_Time is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

create table WF_TEAM  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   ROLE_CODE            VARCHAR2(32)                    not null,
   USER_CODE            VARCHAR2(8)                     not null,
   USER_ORDER           NUMBER(4,0),
   AUTH_DESC            VARCHAR2(255),
   AUTH_TIME            DATE                           default sysdate not null,
   constraint PK_WF_TEAM primary key (FLOW_INST_ID, USER_CODE, ROLE_CODE)
);

comment on table WF_TEAM is
'这个表有业务系统写入，流程引擎只负责读取';

comment on column WF_TEAM.USER_ORDER is
'同一个角色多个用户排序号';

create table WF_TRANSITION  (
   TRANS_ID             NUMBER(12,0)                    not null,
   VERSION              NUMBER(4,0),
   FLOW_CODE            VARCHAR2(32),
   TRANS_CLASS          VARCHAR2(4),
   TRANS_NAME           VARCHAR2(120),
   START_NODE_ID        NUMBER(12,0),
   END_NODE_ID          NUMBER(12,0),
   TRANS_CONDITION      VARCHAR2(500),
   TRANS_DESC           VARCHAR2(500),
   is_Account_Time    CHAR(1),
   Limit_Type         CHAR(1),
   Time_Limit         VARCHAR2(20),
   inherit_Type       CHAR(1),
   inherit_Node_Code  VARCHAR2(20),
   can_ignore         CHAR(1)                        default 'T' not null
      constraint CKC_CAN_IGNORE_WF_TRANS check (can_ignore in ('T','F')),
   constraint PK_WF_TRANSITION primary key (TRANS_ID)
);

comment on column WF_TRANSITION.FLOW_CODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_TRANSITION.TRANS_CLASS is
'暂未使用';

comment on column WF_TRANSITION.START_NODE_ID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_TRANSITION.END_NODE_ID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_TRANSITION.TRANS_CONDITION is
'a>500 && b<300';

comment on column WF_TRANSITION.is_Account_Time is
'改是否记入时间 T/F  I 忽略';

comment on column WF_TRANSITION.Limit_Type is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
';

comment on column WF_TRANSITION.Time_Limit is
'5D4H 这样的表达式';

comment on column WF_TRANSITION.inherit_Type is
'0 不集成 1 继承前一个节点 2 继承指定节点';

comment on column WF_TRANSITION.inherit_Node_Code is
'继承环节代码';

comment on column WF_TRANSITION.can_ignore is
'T可以忽略 F 不可以忽略  是否可以忽略运行';

create table WF_Task_Move  (
   move_no            NUMBER(12,0)                    not null,
   from_user          VARCHAR2(8),
   to_user            VARCHAR2(8),
   move_desc          VARCHAR2(1024),
   oper_user          VARCHAR2(8),
   oper_date          DATE,
   constraint PK_WF_TASK_MOVE primary key (move_no)
);

create table WF_organize  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   UNIT_CODE            VARCHAR2(8)                     not null,
   ROLE_CODE            VARCHAR2(32)                    not null,
   UNIT_ORDER           NUMBER(4,0),
   AUTH_DESC            VARCHAR2(255),
   AUTH_TIME            DATE                           default sysdate not null,
   constraint PK_WF_ORGANIZE primary key (FLOW_INST_ID, UNIT_CODE, ROLE_CODE)
);

comment on column WF_organize.UNIT_ORDER is
'同一个角色多个机构排序号';

--依赖的框架部分表
create table F_OPTDEF  (
  OPTCODE              VARCHAR2(32)                    not null,
  OptID                VARCHAR2(32),
  OPTNAME              VARCHAR2(100),
  OPTMETHOD            VARCHAR2(50),
  OPTURL               VARCHAR2(256),
  OPTDESC              VARCHAR2(256),
  IsInWorkflow         CHAR(1),
  updateDate           DATE,
  CreateDate           DATE,
  OPTREQ               VARCHAR2(8),
  optOrder 			number(4),
  creator              VARCHAR2(32),
  updator              VARCHAR2(32)
);
comment on column F_OPTDEF.OPTMETHOD is
'操作参数 方法';
comment on column F_OPTDEF.IsInWorkflow is
'是否为流程操作方法 F：不是  T ： 是';
alter table F_OPTDEF
  add constraint PK_F_OPTDEF primary key (OPTCODE);

create table F_OptInfo  (
  OptID                VARCHAR2(32)                    not null,
  OptName              VARCHAR2(100)                   not null,
  PreOptID             VARCHAR2(32)                    not null,
  optRoute             VARCHAR2(256),
  opturl               VARCHAR2(256),
  FormCode             VARCHAR2(4),
  OptType              CHAR(1),
  MsgNo                NUMBER(10,0),
  MsgPrm               VARCHAR2(256),
  IsInToolBar          CHAR(1),
  ImgIndex             NUMBER(10,0),
  TopOptID             VARCHAR2(8),
  OrderInd             NUMBER(4,0),
  FLOWCODE             VARCHAR2(8),
  PageType             CHAR(1)                        default 'I' not null,
  Icon                 VARCHAR2(512),
  height               NUMBER(10,0),
  width                NUMBER(10,0),
  updateDate           DATE,
  CreateDate           DATE,
  creator              VARCHAR2(32),
  updator              VARCHAR2(32)
);
comment on column F_OptInfo.optRoute is
'与angularjs路由匹配';
comment on column F_OptInfo.OptType is
' S:实施业务, O:普通业务, W:流程业务, I :项目业务';
comment on column F_OptInfo.OrderInd is
'这个顺序只需在同一个父业务下排序';
comment on column F_OptInfo.FLOWCODE is
'同一个代码的流程应该只有一个有效的版本';
comment on column F_OptInfo.PageType is
'D : DIV I:iFrame';
alter table F_OptInfo add constraint PK_F_OPTINFO primary key (OptID);

create table F_USERUNIT  (
  USERUNITID           VARCHAR2(16)                    not null,
  UNITCODE             VARCHAR2(6)                     not null,
  USERCODE             VARCHAR2(8)                     not null,
  IsPrimary            CHAR(1)                        default '1' not null,
  UserStation          VARCHAR2(16)                    not null,
  UserRank             VARCHAR2(2)                     not null,
  RankMemo             VARCHAR2(256),
  USERORDER            number(8)                      default 0,
  updateDate           DATE,
  CreateDate           DATE,
  creator              VARCHAR2(32),
  updator              VARCHAR2(32)
);
comment on table F_USERUNIT is
'同一个人可能在多个部门担任不同的职位';
comment on column F_USERUNIT.IsPrimary is
'T：为主， F：兼职';
comment on column F_USERUNIT.UserRank is
'RANK 代码不是 0开头的可以进行授予';
comment on column F_USERUNIT.RankMemo is
'任职备注';
alter table F_USERUNIT
  add constraint PK_F_USERUNIT primary key (USERUNITID);

--依赖的框架视图
create or replace view f_v_wf_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null;
        
--工作流视图        
create or replace view F_V_LASTVERSIONFLOW(FLOW_CODE, VERSION, FLOW_NAME, FLOW_CLASS, FLOW_STATE, FLOW_DESC, FLOW_XML_DESC, TIME_LIMIT, EXPIRE_OPT, OPT_ID, FLOW_PUBLISH_DATE, AT_PUBLISH_DATE) as
select a.FLOW_CODE,
   b.version,
   a.FLOW_NAME,
   a.FLOW_CLASS,
   b.FLOW_STATE,
   a.FLOW_DESC,
   a.FLOW_XML_DESC,
   a.TIME_LIMIT,
   a.EXPIRE_OPT,
   a.OPT_ID,
   a.FLOW_PUBLISH_DATE,
   a.AT_PUBLISH_DATE
from (select FLOW_CODE, max(version) as version 
            from wf_flow_define group by FLOW_CODE) 
    lastVersion 
    join wf_flow_define a     
       on a.FLOW_CODE = lastVersion.FLOW_CODE and a.version=0 
    join wf_flow_define b 
       on lastVersion.FLOW_CODE = b.FLOW_CODE and lastVersion.version=b.version;

create or replace view V_INNER_USER_TASK_LIST as
select a.FLOW_INST_ID,w.FLOW_CODE,w.version, w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, nvl(a.Unit_Code,nvl(w.Unit_Code,'0000000')) as UnitCode, 
        a.user_code,c.ROLE_TYPE,c.ROLE_CODE,'一般任务' as AUTHDESC, c.node_code,
          c.Node_Name,c.Node_Type,c.Opt_Type as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.Opt_Param ,d.OptDesc,a.CREATE_TIME,a.Promise_Time,a.TIME_LIMIT,
          c.OPT_CODE,c.Expire_Opt,c.STAGE_CODE,a.last_update_user,a.LAST_UPDATE_TIME,w.inst_state
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.FLOW_INST_ID=w.FLOW_INST_ID)
           join WF_NODE c on (a.NODE_ID=c.NODE_ID)
           join f_v_wf_optdef_url_map d on (c.OPT_CODE=d.OPTCODE)
where /*c.NODETYPE<>'R' and --游离节点不会创建时实例*/ 
    a.NODE_STATE='N' and w.INST_STATE='N' and a.TASK_ASSIGNED='S'
union all
select a.FLOW_INST_ID,w.FLOW_CODE,w.version, w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, nvl(a.Unit_Code,nvl(w.Unit_Code,'0000000')) as UnitCode, 
        b.user_code,b.ROLE_TYPE,b.ROLE_CODE,b.AUTH_DESC, c.node_code,
          c.Node_Name,c.Node_Type,c.Opt_Type as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.Opt_Param ,d.OptDesc,a.CREATE_TIME,a.Promise_Time,a.TIME_LIMIT,
          c.OPT_CODE,c.Expire_Opt,c.STAGE_CODE,a.last_update_user,a.LAST_UPDATE_TIME,w.inst_state
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.FLOW_INST_ID=w.FLOW_INST_ID)
           join WF_ACTION_TASK b on (a.NODE_INST_ID=b.NODE_INST_ID)
           join WF_NODE c on (a.NODE_ID=c.NODE_ID)
           join f_v_wf_optdef_url_map d on (c.OPT_CODE=d.OPTCODE)
where a.NODE_STATE='N' and w.INST_STATE='N' and a.TASK_ASSIGNED='T'
    and b.IS_VALID='T' and  b.TASK_STATE='A' and (b.EXPIRE_TIME is null or b.EXPIRE_TIME>sysdate)
union all
select  a.FLOW_INST_ID,w.FLOW_CODE,w.version,w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, b.UnitCode ,
         b.usercode,c.ROLE_TYPE,c.ROLE_CODE, '系统指定' as AUTHDESC, c.node_code,
          c.Node_Name,c.Node_Type,c.Opt_Type as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.Opt_Param ,d.OptDesc,a.CREATE_TIME,a.Promise_Time,a.TIME_LIMIT,
          c.OPT_CODE,c.Expire_Opt,c.STAGE_CODE,a.last_update_user,a.LAST_UPDATE_TIME,w.inst_state
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.FLOW_INST_ID=w.FLOW_INST_ID)
       join WF_NODE c on (a.NODE_ID=c.NODE_ID)
       join f_v_wf_optdef_url_map d on (c.OPT_CODE=d.OPTCODE) , F_USERUNIT b
where a.NODE_STATE='N' and w.INST_STATE='N'  and a.TASK_ASSIGNED='D' and
        (a.UNIT_CODE is null or a.UNIT_CODE=b.UNITCODE) and
       (   (c.ROLE_TYPE='gw' and c.ROLE_CODE=b.UserStation) or
           (c.ROLE_TYPE='xz' and c.ROLE_CODE=b.UserRank) );


create or replace view V_USER_TASK_LIST(TASK_ID, FLOW_INST_ID, FLOW_CODE, VERSION, FLOW_NAME, FLOW_OPT_NAME, FLOW_OPT_TAG, NODE_INST_ID, UNIT_CODE, USER_CODE, ROLE_TYPE, ROLE_CODE, AUTH_DESC, NODE_CODE, NODE_NAME, NODE_TYPE, NODE_OPT_TYPE, OPT_ID, OPT_NAME, METHOD_NAME, OPT_URL, OPT_METHOD, OPT_PARAM, OPT_DESC, CREATE_TIME, PROMISE_TIME, TIME_LIMIT, OPT_CODE, EXPIRE_OPT, STAGE_CODE, GRANTOR, LAST_UPDATE_USER, LAST_UPDATE_TIME, INST_STATE) as
select rownum as taskid,t.FLOW_INST_ID,t.FLOW_CODE,t.version,t.FLOW_OPT_NAME as WFNAME, t.FLOW_OPT_NAME,t.FLOW_OPT_TAG,t.NODE_INST_ID,t.UNITCODE,t.USER_CODE,
       t.ROLE_TYPE,t.ROLE_CODE,t.AUTHDESC,t.node_code, t.NODE_NAME,t.NODE_TYPE,t.NODEOPTTYPE,t.OPTID,t.OPTNAME,
       t.METHODNAME,t.OPTURL,t.OPTMETHOD,t.OPT_PARAM,t.OPTDESC,t.CREATE_TIME,t.PROMISE_TIME,
       t.TIME_LIMIT,t.OPT_CODE,t.EXPIRE_OPT,t.STAGE_CODE,t.GRANTOR,t.LAST_UPDATE_USER,t.LAST_UPDATE_TIME ,t.inst_state
from
   (select a.FLOW_INST_ID,a.FLOW_CODE,a.version, a.FLOW_OPT_NAME, a.FLOW_OPT_TAG, a.NODE_INST_ID, a.UnitCode, a.user_code, a.ROLE_TYPE, a.ROLE_CODE,
     a.AUTHDESC,a.node_code, a.Node_Name, a.Node_Type, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
      a.Opt_Param, a.OptDesc, a.CREATE_TIME, a.promise_time, a.time_limit,  a.OPT_CODE, a.Expire_Opt, a.STAGE_CODE, 
      null as GRANTOR, a.last_update_user, a.LAST_UPDATE_TIME ,  a.inst_state
  from V_INNER_USER_TASK_LIST a 
  union select a.FLOW_INST_ID,a.FLOW_CODE,a.version, a.FLOW_OPT_NAME, a.FLOW_OPT_TAG, a.node_inst_id, a.UnitCode, b.grantee as user_code, a.ROLE_TYPE, a.ROLE_CODE, 
    a.AUTHDESC,a.node_code, a.Node_Name, a.Node_Type, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod, 
    a.Opt_Param, a.OptDesc, a.CREATE_TIME, a.promise_time, a.time_limit, a.OPT_CODE, a.Expire_Opt, a.STAGE_CODE, 
    b.GRANTOR, a.last_update_user, a.last_update_time ,  a.inst_state
    from V_INNER_USER_TASK_LIST a, WF_ROLE_RELEGATE b 
    where b.Is_Valid = 'T' and b.RELEGATE_TIME <= sysdate and 
          ( b.EXPIRE_TIME is null or b.EXPIRE_TIME >= sysdate) and 
          a.user_code = b.GRANTOR and ( b.UNIT_CODE is null or b.UNIT_CODE = a.UnitCode) 
          and ( b.ROLE_TYPE is null or ( b.ROLE_TYPE = a.ROLE_TYPE and ( b.ROLE_CODE is null or b.ROLE_CODE = a.ROLE_CODE) ) )) 
    t;

create or replace view v_node_instdetail as
select f.FLOW_OPT_NAME,f.FLOW_OPT_TAG,n.node_name,n.role_type,n.role_code,
d.OptName,d.OptName as MethodName,d.OptDefUrl as OptUrl,d.optMethod,n.opt_param,
 t.NODE_INST_ID, t.FLOW_INST_ID, t.NODE_ID, t.CREATE_TIME, t.PREV_NODE_INST_ID, t.NODE_STATE,
 t.SUB_FLOW_INST_ID, t.UNIT_CODE, t.TRANS_ID, t.TASK_ASSIGNED,
 t.RUN_TOKEN, t.TIME_LIMIT, t.LAST_UPDATE_USER, t.LAST_UPDATE_TIME, t.IS_TIMER, t.PROMISE_TIME, n.STAGE_CODE
  from wf_node_instance t
join wf_node n on t.node_id =  n.node_id
join f_v_wf_optdef_url_map d on (n.OPT_CODE=d.OPTCODE)
join wf_flow_instance f on t.FLOW_INST_ID = f.FLOW_INST_ID
with read only;
 comment on table v_node_instdetail is
'包括流程信息、操作信息的视图';
