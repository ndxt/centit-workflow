alter table WF_NODE 
  add inheritType          CHAR;
  
alter table WF_NODE 
  add inheritNodeCode      VARCHAR2(20);
  
alter table WF_NODE 
  add isTrunkLine          CHAR;

alter table wf_node drop column isleafnode;
  
alter table WF_NODE 
   add ROUTERTYPE           VARCHAR2(1)                    
      constraint CKC_ROUTERTYPE_WF_NODE check (ROUTERTYPE is null or ROUTERTYPE in ('D','E','G','H','R','S'))
   add multiInstType        CHAR                             
         constraint CKC_MULTIINSTTYPE_WF_NODE check (multiInstType is null or (multiInstType in ('D','U','V','E')))
   add multiInstParam        VARCHAR2(512)
   add convergeType         CHAR
   add convergeParam        VARCHAR2(64)
   add WarningRule          CHAR                           
      constraint CKC_WARNINGRULE_WF_NODE check (WarningRule is null or (WarningRule in ('R','L','P')))
   add WarningParam         VARCHAR2(20);

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
'改是否记入时间 T/F ';

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
   

alter table WF_TRANSITION 
  add inheritType          CHAR
  add inheritNodeCode      VARCHAR2(20)
  add isAccountTime        CHAR;

alter table WF_TRANSITION 
  add canignore   CHAR    default 'T' not null
      constraint CKC_CANIGNORE_WF_TRANS check (canignore in ('T','F'));
      
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
'改是否记入时间 T/F';

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
  

alter table WF_NODE_INSTANCE
   add USERCODE             VARCHAR2(8)
   add NODEPARAM            VARCHAR2(128);

create sequence S_WARNING_NO;
  
create table WF_RUNTIME_WARNING  (
   WARNINGID            NUMBER(12)                      not null,
   NODEINSTID           NUMBER(12)                      not null,
   WFINSTID             NUMBER(12),
   FLOWSTAGE            VARCHAR2(4),
   WARNINGTYPE          CHAR                           
      constraint CKC_WARNINGTYPE_WF_RUNTI check (WARNINGTYPE is null or (WARNINGTYPE in ('F','N','P'))),
   WARNINGCODE          VARCHAR2(16),
   WARNINGTIME          DATE,
   WARNINGSTATE         CHAR                           
      constraint CKC_WARNINGSTATE_WF_RUNTI check (WARNINGSTATE is null or (WARNINGSTATE in ('C','D','E'))),
   WARNINGIDMSG         VARCHAR2(500),
   SENDMSGTIME          DATE,
   SENDUSERS            VARCHAR2(100),
   constraint PK_WF_RUNTIME_WARNING primary key (WARNINGID)
);

comment on column WF_RUNTIME_WARNING.WARNINGID is
'sequence : S_WARNING_NO';

comment on column WF_RUNTIME_WARNING.WARNINGTYPE is
'F ： 工作流 N ：节点 P：阶段';

comment on column WF_RUNTIME_WARNING.WARNINGSTATE is
'C ：预警 D：已处理 E 发送消息失败';

comment on column WF_RUNTIME_WARNING.SENDUSERS is
'可以是多个人用逗号隔开';


alter table WF_MANAGE_ACTION
   add  NODEINSTID           NUMBER(12);
   

comment on column WF_MANAGE_ACTION.NODEINSTID is
'如果是对节点实例的管理，这个就有值';


comment on column WF_MANAGE_ACTION.ACTIONTYPE is
' 对流程管理操作用大写字母，对节点管理操作用小写字母
  S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
    c: 创建节点  、创建一个游离节点 创建（任意）指定节点
  R  : 流转管理，包括  强行回退  、强行提交   
  T t: 期限管理 、 设置期限
    a: 节点任务管理  分配任务、  删除任务 、  禁用任务
  U u: 变更属性';
   
  
comment on column WF_ACTION_LOG.ACTIONTYPE is
's: 状态变更，挂起节点、 唤醒超时节点、  唤醒节点 、使失效、 终止节点 、使一个正常的节点变为游离状态 、 是游离节点失效
c: 创建节点  、创建一个游离节点 创建（任意）指定节点、 创建流程同时创建首节点   
r: 流转管理，包括  强行回退  、强行提交   
t: 期限管理 、 设置期限
a: 节点任务管理  分配任务、  删除任务 、  禁用任务
u: 变更属性';
  

-- Add/modify columns 
--alter table WF_RUNTIME_WARNING rename column flowphase to FLOWSTAGE;
--删除相关的超时处理标记，这些内容统一由预警表代替
alter table WF_NODE_INSTANCE drop column EXPIREOPTSIGN;
alter table WF_NODE_INSTANCE drop column EXPIRETIME;
alter table WF_FLOW_INSTANCE drop column EXPIREOPTSIGN;
alter table WF_STAGE_INSTANCE drop column EXPIREOPTSIGN;

comment on column WF_FLOW_INSTANCE.INSTSTATE is
' N 正常  C 完成   P 暂停 挂起     F 强行结束';

comment on column WF_NODE_INSTANCE.NODESTATE is
'     * N 正常  B 已回退    C 完成   F被强制结束 
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成';
     
     
drop table WF_Task_Move cascade constraints;

create table WF_Task_Move  (
   move_no              number(12)                      not null,
   from_user            Varchar2(8),
   to_user              Varchar2(8),
   move_desc            Varchar2(1024),
   oper_user            Varchar2(8),
   oper_date            date,
   constraint PK_WF_TASK_MOVE primary key (move_no)
);

alter table WF_NODE_INSTANCE 
   add GRANTOR  VARCHAR2(8);
   
 alter table WF_ACTION_LOG 
   add GRANTOR  VARCHAR2(8);
   
alter table WF_STAGE_INSTANCE
  drop column ExpireOpt;
alter table WF_STAGE_INSTANCE 
  add  beginTime    date;
  
alter table WF_STAGE_INSTANCE    
   add stageBegin  CHAR;

   
alter table WF_NODE_INSTANCE 
add   ROLETYPE             VARCHAR2(8)
add   ROLECODE             VARCHAR2(32);

