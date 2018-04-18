drop table if exists OPT_NODE;

drop table if exists OPT_STAGE;

drop table if exists OPT_TEAM_ROLE;

drop table if exists OPT_VARIABLE;

create table OPT_NODE
(
  OPT_NODE_ID          varchar(32) not null,
  OPT_ID               varchar(32) not null,
  FLOW_CODE            varchar(32) not null,
  OPT_NODE_NAME             varchar(100) not null,
  OPT_URL              varchar(100),
  OPT_TYPE             varchar(50),
  OPT_NODE_ORDER       smallint,
  GMT_CREATE_TIME      datetime not null default now(),
  GMT_MODIFY_TIME      datetime not null default now(),
  primary key (OPT_NODE_ID)
);

create table OPT_STAGE
(
  OPT_STAGE_ID          varchar(32) not null,
  OPT_ID               varchar(32) not null,
  FLOW_CODE            varchar(32) not null,
  OPT_STAGE_NAME       varchar(100) not null,
  OPT_STAGE_CODE       varchar(100) not null,
  OPT_STAGE_ORDER      smallint,
  GMT_CREATE_TIME      datetime not null default now(),
  GMT_MODIFY_TIME      datetime not null default now(),
  primary key (OPT_STAGE_ID)
);

create table OPT_TEAM_ROLE
(
  OPT_TEAM_ROLE_ID          varchar(32) not null,
  OPT_ID               varchar(32) not null,
  FLOW_CODE            varchar(32) not null,
  OPT_ROLE_CODE        varchar(100) not null,
  OPT_ROLE_NAME        varchar(100) not null,
  OPT_TEAM_ROLE_ORDER      smallint,
  GMT_CREATE_TIME      datetime not null default now(),
  GMT_MODIFY_TIME      datetime not null default now(),
  primary key (OPT_TEAM_ROLE_ID)
);

create table OPT_VARIABLE
(
  OPT_VARIABLE_ID          varchar(32) not null,
  OPT_ID               varchar(32) not null,
  FLOW_CODE            varchar(32) not null,
  OPT_VARIABLE_NAME    varchar(100) not null,
  OPT_VARIABLE_TYPE    varchar(100),
  OPT_VARIABLE_ORDER      smallint,
  GMT_CREATE_TIME      datetime not null default now(),
  GMT_MODIFY_TIME      datetime not null default now(),
  primary key (OPT_VARIABLE_ID)
);