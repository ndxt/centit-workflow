drop view if exists F_V_LASTVERSIONFLOW;

drop view if exists  V_INNER_USER_TASK_LIST;

drop view if exists  V_USER_TASK_LIST;

drop view if exists  v_node_instdetail;

drop table  if exists WF_ACTION_LOG ;

drop table if exists  WF_ACTION_TASK ;

drop table  if exists WF_FLOW_DEFINE ;

drop table if exists  WF_FLOW_INSTANCE ;

drop table  if exists WF_FLOW_STAGE ;

drop table  if exists WF_FLOW_VARIABLE ;

drop table  if exists WF_INST_ATTENTION ;

drop table  if exists WF_MANAGE_ACTION ;

drop table if exists  WF_NODE ;

drop table  if exists WF_NODE_INSTANCE ;

drop table  if exists WF_ROLE_RELEGATE ;

drop table  if exists WF_RUNTIME_WARNING ;

drop table  if exists WF_STAGE_INSTANCE ;

drop table  if exists WF_TEAM ;

drop table if exists  WF_TRANSITION ;

drop table  if exists WF_Task_Move ;

drop table  if exists WF_organize ;


delete from f_mysql_sequence where name='S_FLOWDEFINE' ;

delete from f_mysql_sequence where name='S_ACTIONLOGNO' ;

delete from f_mysql_sequence where name='S_ACTIONTASKNO' ;

delete from f_mysql_sequence where name='S_FLOWDEFNO' ;

delete from f_mysql_sequence where name='S_FLOWINSTNO' ;

delete from f_mysql_sequence where name='S_MANAGERACTIONNO' ;

delete from f_mysql_sequence where name='S_NODEINSTNO' ;

delete from f_mysql_sequence where name='S_RELEGATENO' ;

delete from f_mysql_sequence where name='S_WARNING_NO' ;



INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_FLOWDEFINE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_ACTIONLOGNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_ACTIONTASKNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_FLOWDEFNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_FLOWINSTNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_MANAGERACTIONNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_NODEINSTNO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_RELEGATENO', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_WARNING_NO', 0, 1);


create table WF_ACTION_LOG  (
   ACTIONID             numeric(12)                      not null,
   NODEINSTID           numeric(12),
   ACTIONTYPE           VARCHAR(2)                     not null comment  '创建流程同时创建首节点  W
创建节点 C
更改数据 U
提交节点 S
挂起节点 A
唤醒节点 R
终止节点 E',
   ACTIONTIME           DATE                            not null,
   USERCODE             VARCHAR(8),
   ROLETYPE             VARCHAR(8),
   ROLECODE             VARCHAR(32),
   GRANTOR              VARCHAR(8),
   constraint PK_WF_ACTION_LOG primary key (ACTIONID)
);



create table WF_ACTION_TASK  (
   TASKID               numeric(12)                      not null,
   NODEINSTID           numeric(12),
   ASSIGNTIME           DATE                            not null,
   EXPIRETIME           DATE,
   USERCODE             VARCHAR(8),
   ROLETYPE             VARCHAR(8),
   ROLECODE             VARCHAR(32),
   TASKSTATE            CHAR comment  'A:已分配  C：已完成（提交）W: 已委托给别人',
   ISVALID              char comment 'T  F 可以删除，也可以使失效，根据业务需要设定',
   AUTHDESC             VARCHAR(255) comment '可以是权限引擎自动分配，也可以是管理员指定',
   constraint PK_WF_ACTION_TASK primary key (TASKID)
);



create table WF_FLOW_DEFINE  (
   WFCODE               VARCHAR(8)                     not null comment '同一个代码的流程应该只有一个有效的版本',
   VERSION              numeric(4)                       not null comment '版本号为 0 的为草稿， 有效版本号从 1 开始',
   WFNAME               VARCHAR(120),
   WFCLASS              VARCHAR(4)                     not null comment '暂未使用',
   WFPublishDate        date comment '发布时间，也是启用时间',
   WFSTATE              char comment 'A 草稿 B 正常 C 过期 D 禁用 E 已发布  (A,E仅对0版本有效)',
   WFDESC               VARCHAR(500),
   WFXMLDESC            text comment '图形定义界面生成的XML文件',
   TimeLimit            VARCHAR(20) comment '一小时计，8小时为一天，小数点后面为分钟，比如0.30表示30分钟',
   ExpireOpt            CHAR comment 'N：通知， O:不处理 ， S：挂起，E：终止',
   OptID                VARCHAR(32),
   ATPUBLISHDATE        date,
   constraint PK_WF_FLOW_DEFINE primary key (WFCODE,VERSION)
);

alter table WF_FLOW_DEFINE comment
'现有 业务后有流程，一个流程必需绑定到一个业务，否则，无法找到对应的业务变量来进行流转';


create table WF_FLOW_INSTANCE  (
   WFINSTID             numeric(12)                      not null,
   VERSION              numeric(4),
   WFCODE               VARCHAR(8) comment  '同一个代码的流程应该只有一个有效的版本',
   WfOptName            VARCHAR(100) comment  '这个名称用户 查找流程信息',
   WfOptTag             VARCHAR(100) comment  '这个标记用户 查找流程信息，比如办件代码，有业务系统自己解释',
   CREATETIME           DATE                            not null,
   isTimer              CHAR,
   promiseTime          numeric(10) comment  '承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算',
   timeLimit            numeric(10),
   lastupdateuser        VARCHAR(8),
   lastupdatetime       DATE,
   INSTSTATE            CHAR comment  ' N 正常  C 完成   P 暂停 挂起     F 强行结束
',
   ISSUBINST            CHAR comment  'Y 是的 N  不是',
   PREINSTID            numeric(16),
   PRENODEINSTID        numeric(16),
   UNITCODE             VARCHAR(8),
   USERCODE             VARCHAR(8),
   constraint PK_WF_FLOW_INSTANCE primary key (WFINSTID)
);

create table WF_FLOW_STAGE  (
   STAGEID              numeric(12)                      not null,
   VERSION              numeric(4) comment '版本号为 0 的为草稿， 有效版本号从 1 开始',
   WFCODE               VARCHAR(8) comment '同一个代码的流程应该只有一个有效的版本',
   STAGECODE            varchar(32)                    not null,
   STAGENAME            varchar(60),
   isAccountTime        CHAR  comment '改是否记入（流程）时间 T/F',
   LimitType            CHAR comment '期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、C 固定期限  cycle',
   TimeLimit            VARCHAR(20) comment '5D4H 这样的表达式',
   ExpireOpt            CHAR comment 'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）' ,
   constraint PK_WF_FLOW_STAGE primary key (STAGEID)
);

create table WF_FLOW_VARIABLE  (
   WFINSTID             numeric(12)                      not null,
   RunToken             VARCHAR(20)                    not null,
   VARNAME              VARCHAR(50)                    not null,
   VARVALUE             VARCHAR(256)                   not null,
   VarType              CHAR                            not null,
   constraint PK_WF_FLOW_VARIABLE primary key (WFINSTID, RunToken, VARNAME)
);

create table WF_INST_ATTENTION  (
   WFINSTID             numeric(12)                      not null,
   userCode             varchar(8)                     not null,
   attsettime           DATE,
   attsetuser           varchar(8),
   attsetMemo           varchar(255),
   constraint PK_WF_INST_ATTENTION primary key (userCode, WFINSTID)
);

 alter table WF_INST_ATTENTION comment
'关注有个问题，就是一个关注人如何才可写多条意见，看来还是要写到 OPT_IDEA_INFO 中



这个表暂时不用，用 工作流 的关注';

create table WF_MANAGE_ACTION  (
   ACTIONID             numeric(12)                      not null,
   WFINSTID             numeric(12),
   NODEINSTID           numeric(12) comment '如果是对节点实例的管理，这个就有值',
   ACTIONTYPE           VARCHAR(2)                     not null comment '对流程管理操作用大写字母，对节点管理操作用小写字母
  S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
  c: 创建节点  、创建一个游离节点 创建（任意）指定节点
  R  : 流转管理，包括  强行回退  、强行提交
  T t: 期限管理 、 设置期限
  a: 节点任务管理  分配任务、  删除任务 、  禁用任务
  U u: 变更属性''',
   ACTIONTIME           DATE                            not null,
   USERCODE             VARCHAR(8),
   ROLETYPE             VARCHAR(8),
   ROLECODE             VARCHAR(32),
   ADMINDESC            VARCHAR(1000),
   constraint SYS_C0021281 primary key (ACTIONID)
);

create table WF_NODE  (
   NODEID               numeric(12)                      not null  comment  '有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利'    ,
   WFCODE               VARCHAR(8) comment '同一个代码的流程应该只有一个有效的版本' ,
   VERSION              numeric(4),
   NODETYPE             VARCHAR(1)                     not null comment 'A:开始 B:首节点 C:业务节点  F结束  R: 路由节点 。
这个类别是不可以变更的，在画图页面上可以不显示，首节点不需要设置，在流程发布时和开始节点直接相连的节点就是首节点，这个节点必需是业务节点。' ,
   NODENAME             VARCHAR(120),
   OPTTYPE              VARCHAR(1) comment 'A:一般 B:抢先机制 C:多人操作 D: 自动执行 E哑元（可用于嵌套汇聚） S:子流程 ' ,
   OPTCODE              VARCHAR(16),
   OPTBEAN              VARCHAR(100) comment '自动执行节点需要,或者路由判断bean' ,
   OPTPARAM             VARCHAR(100),
   SUBWFCODE            VARCHAR(8) comment '子流程和业务操作只有一个有效，都是为了指定业务节点的活动' ,
   ROUTERTYPE           VARCHAR(1)
      comment 'D:分支 E:汇聚  G 多实例节点  H并行  R 游离 S：同步' ,
   ROLETYPE             VARCHAR(8) comment 'xz gw bj  en' ,
   ROLECODE             VARCHAR(32),
   UNITEXP              VARCHAR(64),
   POWEREXP             VARCHAR(512),
   multiInstType        CHAR
      comment 'D 机构， U 人员 ， V 变量' ,
   multiInstParam       VARCHAR(512) comment '自定义变量表达，用于多实例节点的分支',
   convergeType         CHAR comment '所有都完成， 至少有X完成，至多有X未完成，完成比率达到X ， 外埠判断' ,
   convergeParam        VARCHAR(64),
   NODEDESC             VARCHAR(500),
   isAccountTime        CHAR comment '改是否记入时间 T/F' ,
   LimitType            CHAR comment '期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
' ,
   TimeLimit            VARCHAR(20) comment '5D4H 这样的表达式',
   inheritType          CHAR comment '0 不集成 1 继承前一个节点 2 继承指定节点' ,
   inheritNodeCode      VARCHAR(20) comment  '继承环节代码'  ,
   ExpireOpt            CHAR comment  'N：通知， O:不处理 ， S：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）' ,
   WarningRule          CHAR
      comment 'R：运行时间  L:剩余时间 P：比率',
   WarningParam         VARCHAR(20) comment '是一个时间字符串 或者 数值' ,
   isTrunkLine          CHAR comment 'T / F',
   STAGECODE            VARCHAR(32),
   NODECODE             VARCHAR(20),
   RISKINFO             VARCHAR(4),
   constraint PK_WF_NODE primary key (NODEID)
);
alter table WF_NODE add   constraint CKC_MULTIINSTTYPE_WF_NODE check (multiInstType is null or (multiInstType in ('D','U','V')));
alter table WF_NODE add   constraint CKC_ROUTERTYPE_WF_NODE check (ROUTERTYPE is null or (ROUTERTYPE in ('D','E','G','H','R','S'))) ;
alter table WF_NODE add constraint CKC_WARNINGRULE_WF_NODE check (WarningRule is null or (WarningRule in ('R','L','P')));

create table WF_NODE_INSTANCE  (
   NODEINSTID           numeric(12)                      not null,
   WFINSTID             numeric(12),
   NODEID               numeric(12)    comment   '有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利',
   CREATETIME           DATE,
   STARTTIME            DATE,
   isTimer              CHAR,
   promiseTime          numeric(10)    comment   '承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算',
   timeLimit            numeric(10),
   PREVNODEINSTID       numeric(12),
   NODESTATE            VARCHAR(2)    comment  '     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成' ,
   SUBWFINSTID          numeric(12),
   UNITCODE             VARCHAR(8),
   STAGECODE            varchar(32),
   ROLETYPE             VARCHAR(8)    comment   'xz gw bj  en',
   ROLECODE             VARCHAR(32),
   USERCODE             VARCHAR(8),
   NODEPARAM            VARCHAR(128),
   TRANSID              numeric(12)    comment  '由哪一条路径创建的' ,
   TASKASSIGNED         VARCHAR(1)                     default 'F'    comment  'T: 通过 tasklist 分配， D：通过 岗位、行政角色 自动匹配 S：静态代办（usercode)' ,
   RunToken             VARCHAR(20)    comment  '令牌： T* 表示正常运行的节点  R* 表示游离节点  L* 表示临时插入的节点' ,
   GRANTOR              VARCHAR(8),
   lastupdateuser       VARCHAR(8),
   lastupdatetime       DATE,
   constraint PK_WF_NODE_INSTANCE primary key (NODEINSTID)
);

create table WF_ROLE_RELEGATE  (
   RELEGATENO           numeric(12)                      not null,
   GRANTOR              VARCHAR(8)                     not null,
   GRANTEE              VARCHAR(8)                     not null,
   ISVALID              CHAR                           default 'T' not null comment 'T:生效 F:无效',
   Recorder             VARCHAR(8),
   RELEGATETIME         DATE                            not null,
   EXPIRETIME           DATE,
   UNITCODE             VARCHAR(8),
   ROLETYPE             VARCHAR(8),
   ROLECODE             VARCHAR(32),
   RecordDate           DATE,
   grantDesc            VARCHAR(256),
   constraint PK_WF_ROLE_RELEGATE primary key (RELEGATENO)
);

create table WF_RUNTIME_WARNING  (
   WARNINGID            numeric(12)                      not null      comment   'sequence : S_WARNING_NO'   ,
   WFINSTID             numeric(12),
   NODEINSTID           numeric(12)                      not null,
   FLOWSTAGE            VARCHAR(4),
   OBJTYPE              CHAR

            comment   'F ： 工作流 N ：节点 P：阶段'   ,
   WARNINGTYPE          CHAR

            comment     'W，预警  A  报警 N 提醒  O 其他'   ,
   WARNINGSTATE         CHAR                           default 'N'

             comment       'D 摘牌 C 纠正 F 督办 N 未处理'  ,
   WARNINGCODE          VARCHAR(16)       comment    'ALTER_EXPIRED  : 时间超期报警
WARN_EXPIRED  : 时间超期预警'    ,
   WARNINGTIME          DATE,
   WARNINGIDMSG         VARCHAR(500),
   NOTICESTATE          CHAR                           default '0'

            comment        '0 待发送 1 已发送 2 发送消息失败',
   SENDMSGTIME          DATE,
   SENDUSERS            VARCHAR(100)       comment   '可以是多个人用逗号隔开'     ,
   constraint PK_WF_RUNTIME_WARNING primary key (WARNINGID)
);
alter table WF_RUNTIME_WARNING add   constraint CKC_OBJTYPE_WF_RUNTI check (OBJTYPE is null or (OBJTYPE in ('F','N','P')));
alter table WF_RUNTIME_WARNING add     constraint CKC_WARNINGTYPE_WF_RUNTI check (WARNINGTYPE is null or (WARNINGTYPE in ('W','A','N','O')));
alter table WF_RUNTIME_WARNING add    constraint CKC_WARNINGSTATE_WF_RUNTI check (WARNINGSTATE is null or (WARNINGSTATE in ('D','C','F','N'))) ;
alter table WF_RUNTIME_WARNING add    constraint CKC_NOTICESTATE_WF_RUNTI check (NOTICESTATE is null or (NOTICESTATE in ('0','1','2')));

create table WF_STAGE_INSTANCE  (
   WFINSTID             numeric(12)                      not null,
   STAGEID              numeric(12)                      not null,
   STAGECODE            varchar(32)                    not null,
   STAGENAME            varchar(60),
   beginTime            date,
   stageBegin           CHAR    comment '0 还没有进入， 1 已经进入',
   promiseTime          numeric(10)    comment '承诺时间 1天8小时，1小时60 分钟 这儿按照分钟计算',
   timeLimit            numeric(10),
   lastupdatetime       DATE,
   constraint PK_WF_STAGE_INSTANCE primary key (WFINSTID, STAGEID)
);

   alter table WF_STAGE_INSTANCE comment
'在流程创建时同时创建这个流程所有的阶段';

create table WF_TEAM  (
   WFINSTID             numeric(12)                      not null,
   USERCODE             VARCHAR(8)                     not null,
   ROLECODE             VARCHAR(32)                    not null,
   AUTHDESC             VARCHAR(255),
   AUTHTIME             Datetime                           default CURRENT_TIMESTAMP not null,
   constraint PK_WF_TEAM primary key (WFINSTID, USERCODE, ROLECODE)
);

   alter table  WF_TEAM comment
'这个表有业务系统写入，流程引擎只负责读取';

create table WF_TRANSITION  (
   TRANSID              numeric(12)                      not null,
   VERSION              numeric(4),
   WFCODE               VARCHAR(8)       comment '同一个代码的流程应该只有一个有效的版本'  ,
   TRANSCLASS           VARCHAR(4)       comment   '暂未使用',
   TRANSNAME            VARCHAR(120),
   STARTNODEID          numeric(12)       comment '有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利'  ,
   ENDNODEID            numeric(12)       comment  '有一个特殊的节点创建节点（000001），它对应的权限用来检验是否有申请的权利' ,
   TRANSCONDITION       VARCHAR(500)       comment  'a>500 && b<300' ,
   TRANSDESC            VARCHAR(500),
   isAccountTime        CHAR       comment '改是否记入时间 T/F  I 忽略'  ,
   LimitType            CHAR       comment '期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、
 F 每实例固定期限 fix 、C 节点固定期限  cycle、
'  ,
   TimeLimit            VARCHAR(20)        comment '5D4H 这样的表达式'  ,
   inheritType          CHAR       comment   '0 不集成 1 继承前一个节点 2 继承指定节点',
   inheritNodeCode      VARCHAR(20)       comment   '继承环节代码',
   canignore            CHAR                           default 'T' not null

             comment  'T可以忽略 F 不可以忽略  是否可以忽略运行' ,
   constraint PK_WF_TRANSITION primary key (TRANSID)
);
alter table WF_TRANSITION add   constraint CKC_CANIGNORE_WF_TRANS check (canignore in ('T','F'));

create table WF_Task_Move  (
   move_no              numeric(12)                      not null,
   from_user            Varchar(8),
   to_user              Varchar(8),
   move_desc            Varchar(1024),
   oper_user            Varchar(8),
   oper_date            date,
   constraint PK_WF_TASK_MOVE primary key (move_no)
);

create table WF_organize  (
   WFINSTID             numeric(12)                      not null,
   UNITCODE             VARCHAR(8)                     not null,
   ROLECODE             VARCHAR(32)                    not null,
   AUTHDESC             VARCHAR(255),
   AUTHTIME             Datetime                           default CURRENT_TIMESTAMP not null,
   constraint PK_WF_ORGANIZE primary key (WFINSTID, UNITCODE, ROLECODE)
);

create or replace view F_V_LASTVERSIONFLOW_temp as
select wfcode, max(version) as version
            from wf_flow_define group by wfcode;


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
from F_V_LASTVERSIONFLOW_temp
    lastVersion
    join wf_flow_define a
       on a.wfcode = lastVersion.wfcode and a.version=0
    join wf_flow_define b
       on lastVersion.wfcode = b.wfcode and lastVersion.version=b.version;

create or replace view f_v_wf_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null
;

create or replace view V_INNER_USER_TASK_LIST as
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, ifnull(a.UnitCode,ifnull(w.UnitCode,'0000000')) as UnitCode,
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
select a.WFINSTID,w.WFCODE,w.version, w.WfOptName,w.wfOptTag,a.nodeinstid, ifnull(a.UnitCode,ifnull(w.UnitCode,'0000000')) as UnitCode,
        b.usercode,b.ROLETYPE,b.ROLECODE,b.AUTHDESC, c.nodecode,
          c.NodeName,c.NodeType,c.OptType as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.OptParam ,d.OptDesc,a.CREATETIME,a.PromiseTime,a.TIMELIMIT,
          c.OPTCODE,c.ExpireOpt,c.STAGECODE,a.lastupdateuser,a.lastupdatetime,w.inststate
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.WFINSTID=w.WFINSTID)
           join WF_ACTION_TASK b on (a.NODEINSTID=b.NODEINSTID)
           join WF_NODE c on (a.NODEID=c.NODEID)
           join f_v_wf_optdef_url_map d on (c.OPTCODE=d.OPTCODE)
where a.NODESTATE='N' and w.INSTSTATE='N' and a.TASKASSIGNED='T'
    and b.ISVALID='T' and  b.TASKSTATE='A' and (b.EXPIRETIME is null or b.EXPIRETIME>NOW())
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
select round(round(rand(),8)*100000000) as taskid,a.WFINSTID,a.WFCODE,a.version, a.WfOptName as WFNAME, a.WfOptName,a.wfOptTag, a.nodeinstid, a.UnitCode, a.usercode, a.ROLETYPE, a.ROLECODE,
     a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
      a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit,  a.OPTCODE, a.ExpireOpt, a.STAGECODE,
      null as GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
  from V_INNER_USER_TASK_LIST a
  union select round(round(rand(),8)*100000000) as taskid,a.WFINSTID,a.WFCODE,a.version, a.WfOptName as WFNAME, a.WfOptName,a.wfOptTag, a.nodeinstid, a.UnitCode, b.grantee as usercode, a.ROLETYPE, a.ROLECODE,
    a.AUTHDESC,a.nodecode, a.NodeName, a.NodeType, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
    a.OptParam, a.OptDesc, a.CREATETIME, a.promisetime, a.timelimit, a.OPTCODE, a.ExpireOpt, a.STAGECODE,
    b.GRANTOR, a.lastupdateuser, a.lastupdatetime ,  a.inststate
    from V_INNER_USER_TASK_LIST a, WF_ROLE_RELEGATE b
    where b.IsValid = 'T' and b.RELEGATETIME <= now() and
          ( b.EXPIRETIME is null or b.EXPIRETIME >= now()) and
          a.usercode = b.GRANTOR and ( b.UNITCODE is null or b.UNITCODE = a.UnitCode)
          and ( b.ROLETYPE is null or ( b.ROLETYPE = a.ROLETYPE and ( b.ROLECODE is null or b.ROLECODE = a.ROLECODE) ) )
      ;

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
;


