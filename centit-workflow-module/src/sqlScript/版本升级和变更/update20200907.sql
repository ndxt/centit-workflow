-- 将版本号升级到4.7 修改
-- WF_FLOW_TEAM_ROLE 修改这表为 WF_OPT_TEAM_ROLE
-- 可以新建这个表， 数据库中的表可以不动，代码中废弃这个表
drop table if exists WF_OPT_TEAM_ROLE;
create table WF_OPT_TEAM_ROLE (
  OPT_TEAM_ROLE_ID  varchar(32) comment '主键id',
  OPT_ID            varchar(32) comment '关联的流程业务id',
  ROLE_CODE         varchar(100) comment '办件角色code',
  ROLE_NAME         varchar(100) comment '办件角色名称',
  FORMULA_CODE      varchar(100) comment '办件角色的约束范围',
  TEAM_ROLE_ORDER   numeric(4,0) DEFAULT NULL comment '办件角色排序',
  MODIFY_TIME       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '修改时间',
  primary key (OPT_TEAM_ROLE_ID)
);

-- WF_FLOW_VARIABLE_DEFINE 修改这表为 WF_OPT_VARIABLE_DEFINE
-- 同样可以新建这个表， 数据库中的表可以不动，代码中废弃这个表
drop table if exists WF_OPT_VARIABLE_DEFINE;
create table WF_OPT_VARIABLE_DEFINE (
  OPT_VARIABLE_ID   varchar(32) comment '主键id',
  OPT_ID            varchar(32) comment '关联的流程业务id',
  VARIABLE_NAME     varchar(100) comment '变量名',
  VARIABLE_DESC     varchar(100) comment '变量中文描述',
  VARIABLE_TYPE     varchar(100) comment '变量类型：E:集合 S:单值',
  DEFAULT_VALUE     varchar(256) comment '变量默认值',
  MODIFY_TIME       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '修改时间',
  primary key (OPT_VARIABLE_ID)
);

-- 流程阶段添加排序字段STAGE_ORDER
alter table WF_FLOW_STAGE add STAGE_ORDER numeric(4,0);
