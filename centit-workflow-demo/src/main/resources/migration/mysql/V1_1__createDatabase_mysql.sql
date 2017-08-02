

create table APPROVAL_EVENT
(
  APPROVAL_ID          varchar(32) not null,
  EVENT_TITLE          varchar(100) comment '审批事件标题',
  EVENT_DESC           varchar(500) comment '审批事件说明',
  REQUEST_TIME         datetime comment '申请时间',
  CURRENT_PHASE        numeric(4,0)  comment '当前阶段',
  APPROVAL_STATE       varchar(1) comment '状态 A 申请， B 审核中 C 审核通过 D 审核未通过',
  COMPLETE_TIME        datetime comment '办结时间',
  RESULT_DESC          varchar(500) comment '结果说明',
  primary key (APPROVAL_ID)
);


create table APPROVAL_AUDITOR
(
  AUDITOR_ID           varchar(32) not null comment '主键',
  PHASE_NO             numeric(4,0)  comment '阶段审批人',
  USER_CODE            varchar(32) comment '审批人代码',
  IS_PRIMARY_AUDITOR   varchar(1) comment 'Y / N yes or no',
  primary key (AUDITOR_ID)
);


create table APPROVAL_PROCESS
(
  PROCESS_ID           varchar(32) not null comment '主键',
  PHASE_NO             numeric(4,0)  comment '阶段审批人',
  USER_CODE            varchar(32) comment '审批人代码',
  AUDIT_RESULT         varchar(1) comment '审核是否通过 Y / N yes or no',
  RESULT_DESC          varchar(500) comment '审核说明',
  primary key (PROCESS_ID)
);
