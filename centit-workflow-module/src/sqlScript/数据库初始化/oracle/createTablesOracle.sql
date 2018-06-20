drop view F_V_LASTVERSIONFLOW;

drop view V_INNER_USER_TASK_LIST;

drop view V_USER_TASK_LIST;

drop view v_node_instdetail;

drop table WF_ACTION_LOG cascade constraints;

drop table WF_ACTION_TASK cascade constraints;

drop table WF_FLOW_DEFINE cascade constraints;

drop table WF_FLOW_INSTANCE cascade constraints;

drop table WF_FLOW_STAGE cascade constraints;

drop table WF_FLOW_VARIABLE cascade constraints;

drop table WF_INST_ATTENTION cascade constraints;

drop table WF_MANAGE_ACTION cascade constraints;

drop table WF_NODE cascade constraints;

drop table WF_NODE_INSTANCE cascade constraints;

drop table WF_ROLE_RELEGATE cascade constraints;

drop table WF_ROUTER_NODE cascade constraints;

drop table WF_RUNTIME_WARNING cascade constraints;

drop table WF_STAGE_INSTANCE cascade constraints;

drop table WF_TEAM cascade constraints;

drop table WF_TRANSITION cascade constraints;

drop table WF_Task_Move cascade constraints;

drop table WF_organize cascade constraints;

drop sequence S_ACTIONLOGNO;

drop sequence S_ACTIONTASKNO;

drop sequence S_FLOWDEFNO;

drop sequence S_FLOWINSTNO;

drop sequence S_MANAGERACTIONNO;

drop sequence S_NODEINSTNO;

drop sequence S_RELEGATENO;

drop sequence S_WARNING_NO;

create sequence S_FLOWDEFINE
increment by 1
start with 1;

create sequence S_ACTIONLOGNO
increment by 1
start with 1;

create sequence S_ACTIONTASKNO
increment by 1
start with 1;

create sequence S_FLOWDEFNO
increment by 1
start with 1;

create sequence S_FLOWINSTNO
increment by 1
start with 1;

create sequence S_MANAGERACTIONNO
increment by 1
start with 1;

create sequence S_NODEINSTNO
increment by 1
start with 1;

create sequence S_RELEGATENO;

create sequence S_WARNING_NO;

create table WF_ACTION_LOG  (
   ACTIONID             NUMBER(12)                      not null,
   NODEINSTID           NUMBER(12),
   ACTIONTYPE           VARCHAR2(2)                     not null,
   ACTIONTIME           DATE                            not null,
   USERCODE             VARCHAR2(8),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   GRANTOR              VARCHAR2(8),
   constraint PK_WF_ACTION_LOG primary key (ACTIONID)
);

comment on column WF_ACTION_LOG.ACTIONTYPE is
'创建流程同时创建首节点  W
创建节点 C
更改数据 U
提交节点 S
挂起节点 A
唤醒节点 R
终止节点 E';

create table WF_ACTION_TASK  (
   TASKID               NUMBER(12)                      not null,
   NODEINSTID           NUMBER(12),
   ASSIGNTIME           DATE                            not null,
   EXPIRETIME           DATE,
   USERCODE             VARCHAR2(8),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   TASKSTATE            CHAR,
   ISVALID              char,
   AUTHDESC             VARCHAR2(255),
   constraint PK_WF_ACTION_TASK primary key (TASKID)
);

comment on column WF_ACTION_TASK.TASKSTATE is
'A:已分配  C：已完成（提交）W: 已委托给别人';

comment on column WF_ACTION_TASK.ISVALID is
'T  F 可以删除，也可以使失效，根据业务需要设定';

comment on column WF_ACTION_TASK.AUTHDESC is
'可以是权限引擎自动分配，也可以是管理员指定';

create table WF_FLOW_DEFINE  (
   WFCODE               VARCHAR2(8)                     not null,
   VERSION              NUMBER(4)                       not null,
   WFNAME               VARCHAR2(120),
   WFCLASS              VARCHAR2(4)                     not null,
   WFPublishDate        date,
   WFSTATE              char,
   WFDESC               VARCHAR2(500),
   WFXMLDESC            CLOB,
   TimeLimit            VARCHAR2(20),
   ExpireOpt            CHAR,
   OptID                VARCHAR2(32),
   ATPUBLISHDATE        date,
   constraint PK_WF_FLOW_DEFINE primary key (WFCODE,VERSION)
);

comment on table WF_FLOW_DEFINE is
'现有 业务后有流程，一个流程必需绑定到一个业务，否则，无法找到对应的业务变量来进行流转';

comment on column WF_FLOW_DEFINE.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_DEFINE.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column WF_FLOW_DEFINE.WFCLASS is
'暂未使用';

comment on column WF_FLOW_DEFINE.WFPublishDate is
'发布时间，也是启用时间';

comment on column WF_FLOW_DEFINE.WFSTATE is
'A 草稿 B 正常 C 过期 D 禁用 E 已发布  (A,E仅对0版本有效)';

comment on column WF_FLOW_DEFINE.WFXMLDESC is
'图形定义界面生成的XML文件';

comment on column WF_FLOW_DEFINE.TimeLimit is
'一小时计，8小时为一天，小数点后面为分钟，比如0.30表示30分钟';

comment on column WF_FLOW_DEFINE.ExpireOpt is
'N：通知， O:不处理 ， S：挂起，E：终止';

create table WF_FLOW_INSTANCE  (
   WFINSTID             NUMBER(12)                      not null,
   VERSION              NUMBER(4),
   WFCODE               VARCHAR2(8),
   WfOptName            VARCHAR2(100),
   WfOptTag             VARCHAR2(100),
   CREATETIME           DATE                            not null,
   isTimer              CHAR,
   promiseTime          NUMBER(10),
   timeLimit            NUMBER(10),
   lastupdateuser        VARCHAR2(8),
   lastupdatetime       DATE,
   INSTSTATE            CHAR,
   ISSUBINST            CHAR,
   PREINSTID            NUMBER(16),
   PRENODEINSTID        NUMBER(16),
   UNITCODE             VARCHAR2(8),
   USERCODE             VARCHAR2(8),
   constraint PK_WF_FLOW_INSTANCE primary key (WFINSTID)
);

comment on column WF_FLOW_INSTANCE.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_INSTANCE.WfOptName is
'这个名称用户 查找流程信息';

comment on column WF_FLOW_INSTANCE.WfOptTag is
'这个标记用户 查找流程信息，比如办件代码，有业务系统自己解释';

comment on column WF_FLOW_INSTANCE.promiseTime is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

comment on column WF_FLOW_INSTANCE.INSTSTATE is
' N 正常  C 完成   P 暂停 挂起     F 强行结束
';

comment on column WF_FLOW_INSTANCE.ISSUBINST is
'Y 是的 N  不是';

create table WF_FLOW_STAGE  (
   STAGEID              number(12)                      not null,
   VERSION              NUMBER(4),
   WFCODE               VARCHAR2(8),
   STAGECODE            varchar2(32)                    not null,
   STAGENAME            varchar2(60),
   isAccountTime        CHAR,
   LimitType            CHAR,
   TimeLimit            VARCHAR2(20),
   ExpireOpt            CHAR,
   constraint PK_WF_FLOW_STAGE primary key (STAGEID)
);

comment on column WF_FLOW_STAGE.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column WF_FLOW_STAGE.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_FLOW_STAGE.isAccountTime is
'改是否记入（流程）时间 T/F';

comment on column WF_FLOW_STAGE.LimitType is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、C 固定期限  cycle';

comment on column WF_FLOW_STAGE.TimeLimit is
'5D4H 这样的表达式';

comment on column WF_FLOW_STAGE.ExpireOpt is
'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）';

create table WF_FLOW_VARIABLE  (
   WFINSTID             NUMBER(12)                      not null,
   RunToken             VARCHAR2(20)                    not null,
   VARNAME              VARCHAR2(50)                    not null,
   VARVALUE             VARCHAR2(256)                   not null,
   VarType              CHAR                            not null,
   constraint PK_WF_FLOW_VARIABLE primary key (WFINSTID, RunToken, VARNAME)
);

create table WF_INST_ATTENTION  (
   WFINSTID             NUMBER(12)                      not null,
   userCode             varchar2(8)                     not null,
   attsettime           DATE,
   attsetuser           varchar2(8),
   attsetMemo           varchar2(255),
   constraint PK_WF_INST_ATTENTION primary key (userCode, WFINSTID)
);

comment on table WF_INST_ATTENTION is
'关注有个问题，就是一个关注人如何才可写多条意见，看来还是要写到 OPT_IDEA_INFO 中



这个表暂时不用，用 工作流 的关注';

create table WF_MANAGE_ACTION  (
   ACTIONID             NUMBER(12)                      not null,
   WFINSTID             NUMBER(12),
   NODEINSTID           NUMBER(12),
   ACTIONTYPE           VARCHAR2(2)                     not null,
   ACTIONTIME           DATE                            not null,
   USERCODE             VARCHAR2(8),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   ADMINDESC            VARCHAR2(1000),
   constraint SYS_C0021281 primary key (ACTIONID)
);

comment on column WF_MANAGE_ACTION.NODEINSTID is
'如果是对节点实例的管理，这个就有值';

comment on column WF_MANAGE_ACTION.ACTIONTYPE is
'对流程管理操作用大写字母，对节点管理操作用小写字母
  S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
  c: 创建节点  、创建一个游离节点 创建（任意）指定节点
  R  : 流转管理，包括  强行回退  、强行提交   
  T t: 期限管理 、 设置期限
  a: 节点任务管理  分配任务、  删除任务 、  禁用任务
  U u: 变更属性''';

create table WF_NODE  (
   NODEID               NUMBER(12)                      not null,
   WFCODE               VARCHAR2(8),
   VERSION              NUMBER(4),
   NODETYPE             VARCHAR2(1)                     not null,
   NODENAME             VARCHAR2(120),
   OPTTYPE              VARCHAR2(1),
   OPTCODE              VARCHAR2(16),
   OPTBEAN              VARCHAR2(100),
   OPTPARAM             VARCHAR2(100),
   SUBWFCODE            VARCHAR2(8),
   ROUTERTYPE           VARCHAR2(1)                    
      constraint CKC_ROUTERTYPE_WF_NODE check (ROUTERTYPE is null or (ROUTERTYPE in ('D','E','G','H','R','S'))),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   UNITEXP              VARCHAR2(64),
   POWEREXP             VARCHAR2(512),
   multiInstType        CHAR                           
      constraint CKC_MULTIINSTTYPE_WF_NODE check (multiInstType is null or (multiInstType in ('D','U','V'))),
   multiInstParam       VARCHAR2(512),
   convergeType         CHAR,
   convergeParam        VARCHAR2(64),
   NODEDESC             VARCHAR2(500),
   isAccountTime        CHAR,
   LimitType            CHAR,
   TimeLimit            VARCHAR2(20),
   inheritType          CHAR,
   inheritNodeCode      VARCHAR2(20),
   ExpireOpt            CHAR,
   WarningRule          CHAR                           
      constraint CKC_WARNINGRULE_WF_NODE check (WarningRule is null or (WarningRule in ('R','L','P'))),
   WarningParam         VARCHAR2(20),
   isTrunkLine          CHAR,
   STAGECODE            VARCHAR2(32),
   NODECODE             VARCHAR2(20),
   RISKINFO             VARCHAR2(4),
   constraint PK_WF_NODE primary key (NODEID)
);

comment on column WF_NODE.NODEID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_NODE.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_NODE.NODETYPE is
'A:开始 B:首节点 C:业务节点  F结束  R: 路由节点 。
这个类别是不可以变更的，在画图页面上可以不显示，首节点不需要设置，在流程发布时和开始节点直接相连的节点就是首节点，这个节点必需是业务节点。';

comment on column WF_NODE.OPTTYPE is
'A:一般 B:抢先机制 C:多人操作 D: 自动执行 E哑元（可用于嵌套汇聚） S:子流程 ';

comment on column WF_NODE.OPTBEAN is
'自动执行节点需要,或者路由判断bean';

comment on column WF_NODE.SUBWFCODE is
'子流程和业务操作只有一个有效，都是为了指定业务节点的活动';

comment on column WF_NODE.ROUTERTYPE is
'D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步';

comment on column WF_NODE.ROLETYPE is
'xz gw bj  en';

comment on column WF_NODE.multiInstType is
'D 机构， U 人员 ， V 变量';

comment on column WF_NODE.multiInstParam is
'自定义变量表达，用于多实例节点的分支';

comment on column WF_NODE.convergeType is
'所有都完成， 至少有X完成，至多有X未完成，完成比率达到X ， 外埠判断';

comment on column WF_NODE.isAccountTime is
'改是否记入时间 T/F';

comment on column WF_NODE.LimitType is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
';

comment on column WF_NODE.TimeLimit is
'5D4H 这样的表达式';

comment on column WF_NODE.inheritType is
'0 不集成 1 继承前一个节点 2 继承指定节点';

comment on column WF_NODE.inheritNodeCode is
'继承环节代码';

comment on column WF_NODE.ExpireOpt is
'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）';

comment on column WF_NODE.WarningRule is
'R：运行时间  L:剩余时间 P：比率';

comment on column WF_NODE.WarningParam is
'是一个时间字符串 或者 数值';

comment on column WF_NODE.isTrunkLine is
'T / F';


create table WF_NODE_INSTANCE  (
   NODEINSTID           NUMBER(12)                      not null,
   WFINSTID             NUMBER(12),
   NODEID               NUMBER(12),
   CREATETIME           DATE,
   STARTTIME            DATE,
   isTimer              CHAR,
   promiseTime          NUMBER(10),
   timeLimit            NUMBER(10),
   PREVNODEINSTID       NUMBER(12),
   NODESTATE            VARCHAR(2),
   SUBWFINSTID          NUMBER(12),
   UNITCODE             VARCHAR2(8),
   STAGECODE            varchar2(32),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   USERCODE             VARCHAR2(8),
   NODEPARAM            VARCHAR2(128),
   TRANSID              NUMBER(12),
   TASKASSIGNED         VARCHAR(1)                     default 'F',
   RunToken             VARCHAR2(20),
   GRANTOR              VARCHAR2(8),
   lastupdateuser       VARCHAR2(8),
   lastupdatetime       DATE,
   constraint PK_WF_NODE_INSTANCE primary key (NODEINSTID)
);

comment on column WF_NODE_INSTANCE.NODEID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_NODE_INSTANCE.promiseTime is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

comment on column WF_NODE_INSTANCE.NODESTATE is
'     * N 正常  B 已回退    C 完成   F被强制结束 
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成';

comment on column WF_NODE_INSTANCE.ROLETYPE is
'xz gw bj  en';

comment on column WF_NODE_INSTANCE.TRANSID is
'由哪一条路径创建的';

comment on column WF_NODE_INSTANCE.TASKASSIGNED is
'T: 通过 tasklist 分配， D：通过 岗位、行政角色 自动匹配 S：静态代办（usercode)';

comment on column WF_NODE_INSTANCE.RunToken is
'令牌： T* 表示正常运行的节点  R* 表示游离节点  L* 表示临时插入的节点';

create table WF_ROLE_RELEGATE  (
   RELEGATENO           NUMBER(12)                      not null,
   GRANTOR              VARCHAR2(8)                     not null,
   GRANTEE              VARCHAR2(8)                     not null,
   ISVALID              CHAR                           default 'T' not null,
   Recorder             VARCHAR2(8),
   RELEGATETIME         DATE                            not null,
   EXPIRETIME           DATE,
   UNITCODE             VARCHAR2(8),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   RecordDate           DATE,
   grantDesc            VARCHAR2(256),
   constraint PK_WF_ROLE_RELEGATE primary key (RELEGATENO)
);

comment on column WF_ROLE_RELEGATE.ISVALID is
'T:生效 F:无效';

create table WF_ROUTER_NODE  (
   NODEID               NUMBER(12)                      not null,
   WFCODE               VARCHAR2(8),
   VERSION              NUMBER(4),
   ROUTERTYPE           VARCHAR2(1)                     not null,
   NODENAME             VARCHAR2(120),
   NODEDESC             VARCHAR2(500),
   ROLETYPE             VARCHAR2(8),
   ROLECODE             VARCHAR2(32),
   UNITEXP              VARCHAR2(64),
   POWEREXP             VARCHAR2(512),
   SELFDEFPARAM         VARCHAR2(512),
   convergeType         CHAR,
   convergeParam        VARCHAR2(64),
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
   WARNINGID            NUMBER(12)                      not null,
   WFINSTID             NUMBER(12),
   NODEINSTID           NUMBER(12)                      not null,
   FLOWSTAGE            VARCHAR2(4),
   OBJTYPE              CHAR                           
      constraint CKC_OBJTYPE_WF_RUNTI check (OBJTYPE is null or (OBJTYPE in ('F','N','P'))),
   WARNINGTYPE          CHAR                           
      constraint CKC_WARNINGTYPE_WF_RUNTI check (WARNINGTYPE is null or (WARNINGTYPE in ('W','A','N','O'))),
   WARNINGSTATE         CHAR                           default 'N'
      constraint CKC_WARNINGSTATE_WF_RUNTI check (WARNINGSTATE is null or (WARNINGSTATE in ('D','C','F','N'))),
   WARNINGCODE          VARCHAR2(16),
   WARNINGTIME          DATE,
   WARNINGIDMSG         VARCHAR2(500),
   NOTICESTATE          CHAR                           default '0'
      constraint CKC_NOTICESTATE_WF_RUNTI check (NOTICESTATE is null or (NOTICESTATE in ('0','1','2'))),
   SENDMSGTIME          DATE,
   SENDUSERS            VARCHAR2(100),
   constraint PK_WF_RUNTIME_WARNING primary key (WARNINGID)
);

comment on column WF_RUNTIME_WARNING.WARNINGID is
'sequence : S_WARNING_NO';

comment on column WF_RUNTIME_WARNING.OBJTYPE is
'F ： 工作流 N ：节点 P：阶段';

comment on column WF_RUNTIME_WARNING.WARNINGTYPE is
'W，预警  A  报警 N 提醒  O 其他';

comment on column WF_RUNTIME_WARNING.WARNINGSTATE is
'D 摘牌 C 纠正 F 督办 N 未处理';

comment on column WF_RUNTIME_WARNING.WARNINGCODE is
'ALTER_EXPIRED  : 时间超期报警 
WARN_EXPIRED  : 时间超期预警';

comment on column WF_RUNTIME_WARNING.NOTICESTATE is
'0 待发送 1 已发送 2 发送消息失败';

comment on column WF_RUNTIME_WARNING.SENDUSERS is
'可以是多个人用逗号隔开';

create table WF_STAGE_INSTANCE  (
   WFINSTID             NUMBER(12)                      not null,
   STAGEID              number(12)                      not null,
   STAGECODE            varchar2(32)                    not null,
   STAGENAME            varchar2(60),
   beginTime            date,
   stageBegin           CHAR,
   promiseTime          NUMBER(10),
   timeLimit            NUMBER(10),
   lastupdatetime       DATE,
   constraint PK_WF_STAGE_INSTANCE primary key (WFINSTID, STAGEID)
);

comment on table WF_STAGE_INSTANCE is
'在流程创建时同时创建这个流程所有的阶段';

comment on column WF_STAGE_INSTANCE.stageBegin is
'0 还没有进入， 1 已经进入';

comment on column WF_STAGE_INSTANCE.promiseTime is
'承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算';

create table WF_TEAM  (
   WFINSTID             NUMBER(12)                      not null,
   USERCODE             VARCHAR2(8)                     not null,
   ROLECODE             VARCHAR2(32)                    not null,
   AUTHDESC             VARCHAR2(255),
   AUTHTIME             Date                           default sysdate not null,
   constraint PK_WF_TEAM primary key (WFINSTID, USERCODE, ROLECODE)
);

comment on table WF_TEAM is
'这个表有业务系统写入，流程引擎只负责读取';

create table WF_TRANSITION  (
   TRANSID              NUMBER(12)                      not null,
   VERSION              NUMBER(4),
   WFCODE               VARCHAR2(8),
   TRANSCLASS           VARCHAR2(4),
   TRANSNAME            VARCHAR2(120),
   STARTNODEID          NUMBER(12),
   ENDNODEID            NUMBER(12),
   TRANSCONDITION       VARCHAR2(500),
   TRANSDESC            VARCHAR2(500),
   isAccountTime        CHAR,
   LimitType            CHAR,
   TimeLimit            VARCHAR2(20),
   inheritType          CHAR,
   inheritNodeCode      VARCHAR2(20),
   canignore            CHAR                           default 'T' not null
      constraint CKC_CANIGNORE_WF_TRANS check (canignore in ('T','F')),
   constraint PK_WF_TRANSITION primary key (TRANSID)
);

comment on column WF_TRANSITION.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column WF_TRANSITION.TRANSCLASS is
'暂未使用';

comment on column WF_TRANSITION.STARTNODEID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_TRANSITION.ENDNODEID is
'有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利';

comment on column WF_TRANSITION.TRANSCONDITION is
'a>500 && b<300';

comment on column WF_TRANSITION.isAccountTime is
'改是否记入时间 T/F  I 忽略';

comment on column WF_TRANSITION.LimitType is
'期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
';

comment on column WF_TRANSITION.TimeLimit is
'5D4H 这样的表达式';

comment on column WF_TRANSITION.inheritType is
'0 不集成 1 继承前一个节点 2 继承指定节点';

comment on column WF_TRANSITION.inheritNodeCode is
'继承环节代码';

comment on column WF_TRANSITION.canignore is
'T可以忽略 F 不可以忽略  是否可以忽略运行';

create table WF_Task_Move  (
   move_no              number(12)                      not null,
   from_user            Varchar2(8),
   to_user              Varchar2(8),
   move_desc            Varchar2(1024),
   oper_user            Varchar2(8),
   oper_date            date,
   constraint PK_WF_TASK_MOVE primary key (move_no)
);

create table WF_organize  (
   WFINSTID             NUMBER(12)                      not null,
   UNITCODE             VARCHAR2(8)                     not null,
   ROLECODE             VARCHAR2(32)                    not null,
   AUTHDESC             VARCHAR2(255),
   AUTHTIME             Date                           default sysdate not null,
   constraint PK_WF_ORGANIZE primary key (WFINSTID, UNITCODE, ROLECODE)
);

create or replace view F_V_LASTVERSIONFLOW as
select a.WFCODE,
   b.version,
   a.WFNAME,
   a.WFCLASS,
   b.WFSTATE,
   a.WFDESC,
   a.WFXMLDESC,
   a.TimeLimit,
   a.ExpireOpt,
   a.OptID,
   a.WFPublishDate,
   a.ATPUBLISHDATE
from (select wfcode, max(version) as version 
            from wf_flow_define group by wfcode) 
    lastVersion 
    join wf_flow_define a     
       on a.wfcode = lastVersion.wfcode and a.version=0 
    join wf_flow_define b 
       on lastVersion.wfcode = b.wfcode and lastVersion.version=b.version;

comment on column F_V_LASTVERSIONFLOW.WFCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column F_V_LASTVERSIONFLOW.VERSION is
'版本号为 0 的为草稿， 有效版本号从 1 开始';

comment on column F_V_LASTVERSIONFLOW.WFCLASS is
'暂未使用';

comment on column F_V_LASTVERSIONFLOW.WFSTATE is
'A 草稿 B 正常 C 过期 D 禁用 E 已发布  (A,E仅对0版本有效)';

comment on column F_V_LASTVERSIONFLOW.WFXMLDESC is
'图形定义界面生成的XML文件';

comment on column F_V_LASTVERSIONFLOW.TimeLimit is
'一小时计，8小时为一天，小数点后面为分钟，比如0.30表示30分钟';

comment on column F_V_LASTVERSIONFLOW.ExpireOpt is
'N：通知， O:不处理 ， S：挂起，E：终止';

comment on column F_V_LASTVERSIONFLOW.WFPublishDate is
'发布时间，也是启用时间';

create or replace view f_v_wf_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null
;
   
create or replace view V_INNER_USER_TASK_LIST as
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, nvl(a.UnitCode,nvl(w.UnitCode,'0000000')) as UnitCode, 
        a.usercode,c.ROLETYPE,c.ROLECODE,'一般任务' as AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.OptParam ,d.OptDesc,a.CREATETIME,a.PromiseTime,a.TIMELIMIT,
          c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
           join WF_NODE c on (a.NODEID=c.NODEID)
           join f_v_wf_optdef_url_map d on (c.OPTCODE=d.OPTCODE)
where /*c.NODETYPE<>'R' and --游离节点不会创建时实例*/ 
    a.NODESTATE='N' and w.INSTSTATE='N' and a.TASKASSIGNED='S'
union all
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, nvl(a.UnitCode,nvl(w.UnitCode,'0000000')) as UnitCode, 
        b.usercode,b.ROLETYPE,b.ROLECODE,b.AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.OptParam ,d.OptDesc,a.CREATETIME,a.PromiseTime,a.TIMELIMIT,
          c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
           join WF_ACTION_TASK b on (a.NODEINSTID=b.NODEINSTID)
           join WF_NODE c on (a.NODEID=c.NODEID)
           join f_v_wf_optdef_url_map d on (c.OPTCODE=d.OPTCODE)
where a.NODESTATE='N' and w.INSTSTATE='N' and a.TASKASSIGNED='T'
    and b.ISVALID='T' and  b.TASKSTATE='A' and (b.EXPIRETIME is null or b.EXPIRETIME>sysdate)
union all
select  a.WFINSTID,w.WFCODE,w.version,w.WfOptName,w.wfOptTag,a.nodeinstid, b.UnitCode ,
         b.usercode,c.ROLETYPE,c.ROLECODE, '系统指定' as AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.OptParam,d.OptDesc,a.CREATETIME,a.PromiseTime,a.timelimit,
           c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
       join WF_NODE c on (a.NODEID=c.NODEID)
       join f_v_wf_optdef_url_map d on (c.OPTCODE=d.OPTCODE) , F_USERUNIT b
where a.NODESTATE='N' and w.INSTSTATE='N'  and a.TASKASSIGNED='D' and
        (a.UNITCODE is null or a.UNITCODE=b.UNITCODE) and
       (   (c.ROLETYPE='gw' and c.ROLECODE=b.UserStation) or
           (c.ROLETYPE='xz' and c.ROLECODE=b.UserRank) );

create or replace view V_USER_TASK_LIST as
 select rownum as taskid,t.WFINSTID,t.WFCODE,t.version,t.WFOPTNAME as WFNAME, t.WFOPTNAME,t.WFOPTTAG,t.NODEINSTID,t.UNITCODE,t.USERCODE,
       t.ROLETYPE,t.ROLECODE,t.AUTHDESC,t.nodecode, t.NODENAME,t.NODETYPE,t.NODEOPTTYPE,t.OPTID,t.OPTNAME,
       t.METHODNAME,t.OPTURL,t.OPTMETHOD,t.OPTPARAM,t.OPTDESC,t.CREATETIME,t.PROMISETIME,
       t.TIMELIMIT,t.OPTCODE,t.EXPIREOPT,t.STAGECODE,t.GRANTOR,t.LASTUPDATEUSER,t.LASTUPDATETIME ,t.inststate
from
   (select a.WFINSTID,a.WFCODE,a.version, a.WfOptName, a.wfOptTag, a.nodeinstid, a.UnitCode, a.usercode, a.ROLETYPE, a.ROLECODE,
     a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
      a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit,  a.OPTCODE, a.ExpireOpt, a.STAGECODE, 
      null as GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
  from V_INNER_USER_TASK_LIST a 
  union select a.WFINSTID,a.WFCODE,a.version, a.WfOptName, a.wfOptTag, a.nodeinstid, a.UnitCode, b.grantee as usercode, a.ROLETYPE, a.ROLECODE, 
    a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod, 
    a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit, a.OPTCODE, a.ExpireOpt, a.STAGECODE, 
    b.GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
    from V_INNER_USER_TASK_LIST a, WF_ROLE_RELEGATE b 
    where b.IsValid = 'T' and b.RELEGATETIME <= sysdate and 
          ( b.EXPIRETIME is null or b.EXPIRETIME >= sysdate) and 
          a.usercode = b.GRANTOR and ( b.UNITCODE is null or b.UNITCODE = a.UnitCode) 
          and ( b.ROLETYPE is null or ( b.ROLETYPE = a.ROLETYPE and ( b.ROLECODE is null or b.ROLECODE = a.ROLECODE) ) )) 
      t;

create or replace view v_node_instdetail as
select f.wfoptname,f.wfopttag,n.nodename,n.roletype,n.rolecode,
d.OptName,d.OptName as MethodName,d.OptDefUrl as OptUrl,d.optMethod,n.optparam,
 t.NODEINSTID, t.WFINSTID, t.NODEID, t.CREATETIME, t.PREVNODEINSTID, t.NODESTATE,
 t.SUBWFINSTID, t.UNITCODE, t.TRANSID, t.TASKASSIGNED,
 t.RUNTOKEN, t.TIMELIMIT, t.LASTUPDATEUSER, t.LASTUPDATETIME, t.ISTIMER, t.PROMISETIME, n.STAGECODE
  from wf_node_instance t
join wf_node n on t.nodeid =  n.nodeid
join f_v_wf_optdef_url_map d on (n.OPTCODE=d.OPTCODE)
join wf_flow_instance f on t.wfinstid = f.wfinstid
with read only;

 comment on table v_node_instdetail is
'包括流程信息、操作信息的视图';


