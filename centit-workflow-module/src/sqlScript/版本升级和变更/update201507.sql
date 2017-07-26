-- Add/modify columns 
alter table WF_NODE rename column flowphase to STAGECODE;
alter table WF_NODE modify stagecode VARCHAR2(32);


drop table WF_RUNTIME_WARNING cascade constraints;

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
