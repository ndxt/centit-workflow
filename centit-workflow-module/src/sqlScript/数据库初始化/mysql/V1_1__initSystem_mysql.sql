drop table if exists WF_ACTION_LOG;

drop table if exists WF_ACTION_TASK;

drop table if exists WF_FLOW_DEFINE;

drop table if exists WF_FLOW_INSTANCE;

drop table if exists WF_FLOW_STAGE;

drop table if exists WF_FLOW_VARIABLE;

drop table if exists WF_INST_ATTENTION;

drop table if exists WF_MANAGE_ACTION;

drop table if exists WF_NODE;

drop table if exists WF_NODE_INSTANCE;

drop table if exists WF_ROLE_RELEGATE;

drop table if exists WF_ROUTER_NODE;

drop table if exists WF_RUNTIME_WARNING;

drop table if exists WF_STAGE_INSTANCE;

drop table if exists WF_TEAM;

drop table if exists WF_TRANSITION;

drop table if exists WF_Task_Move;

drop table if exists WF_organize;

create table WF_ACTION_LOG
(
   ACTION_ID            numeric(12,0) not null,
   NODE_INST_ID         numeric(12,0),
   ACTION_TYPE          varchar(2) not null ,
   ACTION_TIME          datetime not null,
   USER_CODE            varchar(8),
   ROLE_TYPE            varchar(8),
   ROLE_CODE            varchar(32),
   GRANTOR              varchar(8),
   primary key (ACTION_ID)
);

create table WF_ACTION_TASK
(
   TASK_ID              numeric(12,0) not null,
   NODE_INST_ID         numeric(12,0),
   ASSIGN_TIME          datetime not null,
   EXPIRE_TIME          datetime,
   USER_CODE            varchar(8),
   ROLE_TYPE            varchar(8),
   ROLE_CODE            varchar(32),
   TASK_STATE           char(1) comment 'A:�ѷ���  C������ɣ��ύ��W: ��ί�и�����',
   IS_VALID             char(1) comment 'T  F ����ɾ����Ҳ����ʹʧЧ������ҵ����Ҫ�趨',
   AUTH_DESC            varchar(255) comment '������Ȩ�������Զ����䣬Ҳ�����ǹ���Աָ��',
   primary key (TASK_ID)
);

create table WF_FLOW_DEFINE
(
   FLOW_CODE            varchar(32) not null comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   VERSION              numeric(4,0) not null comment '�汾��Ϊ 0 ��Ϊ�ݸ壬 ��Ч�汾�Ŵ� 1 ��ʼ',
   FLOW_NAME            varchar(120),
   FLOW_CLASS           varchar(4) not null comment '��δʹ��',
   FLOW_Publish_Date    datetime comment '����ʱ�䣬Ҳ������ʱ��',
   FLOW_STATE           char(1) comment 'A �ݸ� B ���� C ���� D ���� E �ѷ���  (A,E����0�汾��Ч)',
   FLOW_DESC            varchar(500),
   FLOW_XML_DESC        text comment 'ͼ�ζ���������ɵ�XML�ļ�',
   Time_Limit           varchar(20) comment 'һСʱ�ƣ�8СʱΪһ�죬С�������Ϊ���ӣ�����0.30��ʾ30����',
   Expire_Opt           char(1) comment 'N��֪ͨ�� O:������ �� S������E����ֹ',
   Opt_ID               varchar(32),
   AT_PUBLISH_DATE      datetime,
   primary key (VERSION, FLOW_CODE)
);

alter table WF_FLOW_DEFINE comment '���� ҵ��������̣�һ�����̱���󶨵�һ��ҵ�񣬷����޷��ҵ���Ӧ��ҵ�������������ת';

create table WF_FLOW_INSTANCE
(
   FLOW_INST_ID         numeric(12,0) not null,
   VERSION              numeric(4,0),
   FLOW_CODE            varchar(32) comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   FLOW_Opt_Name        varchar(100) comment '��������û� ����������Ϣ',
   FLOW_Opt_Tag         varchar(100) comment '�������û� ����������Ϣ�����������룬��ҵ��ϵͳ�Լ�����',
   CREATE_TIME          datetime not null,
   is_Timer             char(1),
   promise_Time         numeric(10,0) comment '��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���',
   time_Limit           numeric(10,0),
   last_update_user     varchar(8),
   last_update_time     datetime,
   INST_STATE           char(1) comment ' N ����  C ���   P ��ͣ ����     F ǿ�н���
            ',
   IS_SUB_INST          char(1) comment 'Y �ǵ� N  ����',
   PRE_INST_ID          numeric(16,0),
   PRE_NODE_INST_ID     numeric(16,0),
   UNIT_CODE            varchar(8),
   USER_CODE            varchar(8),
   primary key (FLOW_INST_ID)
);

create table WF_FLOW_STAGE
(
   STAGE_ID             numeric(12,0) not null,
   VERSION              numeric(4,0) comment '�汾��Ϊ 0 ��Ϊ�ݸ壬 ��Ч�汾�Ŵ� 1 ��ʼ',
   FLOW_CODE            varchar(32) comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   STAGE_CODE           varchar(32) not null,
   STAGE_NAME           varchar(60),
   is_Account_Time      char(1) comment '���Ƿ���루���̣�ʱ�� T/F',
   Limit_Type           char(1) comment '������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��C �̶�����  cycle',
   Time_Limit           varchar(20) comment '5D4H �����ı��ʽ',
   Expire_Opt           char(1) comment 'N��֪ͨ�� O:������ �� S������E����ֹ�����̣��� C����ɣ�ǿ���ύ,�ύʧ�ܾ͹���',
   primary key (STAGE_ID)
);

create table WF_FLOW_VARIABLE
(
   FLOW_INST_ID         numeric(12,0) not null,
   Run_Token            varchar(20) not null,
   VAR_NAME             varchar(50) not null,
   VAR_VALUE            varchar(256) not null,
   Var_Type             char(1) not null,
   primary key (FLOW_INST_ID, Run_Token, VAR_NAME)
);

create table WF_INST_ATTENTION
(
   FLOW_INST_ID         numeric(12,0) not null,
   user_Code            varchar(8) not null,
   att_set_time         datetime,
   att_set_user         varchar(8),
   att_set_Memo         varchar(255),
   primary key (user_Code, FLOW_INST_ID)
);

alter table WF_INST_ATTENTION comment '��ע�и����⣬����һ����ע����βſ�д�����������������Ҫд�� OPT_IDEA_INFO ��

                                      -&#&';

create table WF_MANAGE_ACTION
(
   ACTION_ID            numeric(12,0) not null,
   FLOW_INST_ID         numeric(12,0),
   NODE_INST_ID         numeric(12,0) comment '����ǶԽڵ�ʵ���Ĺ����������ֵ',
   ACTION_TYPE          varchar(2) not null comment '�����̹�������ô�д��ĸ���Խڵ���������Сд��ĸ
              S s: ״̬����� ��ʱ���ѡ� ʹʧЧ�� ʹһ�������Ľڵ��Ϊ����״̬ �� ������ڵ�ʧЧ
              c: �����ڵ�  ������һ������ڵ� ���������⣩ָ���ڵ�
              R  : ��ת��������  ǿ�л���  ��ǿ���ύ   
              T t: ���޹��� �� ��������
              a: �ڵ��������  ��������  ɾ������ ��  ��������
              U u: �������''',
   ACTION_TIME          datetime not null,
   USER_CODE            varchar(8),
   ROLE_TYPE            varchar(8),
   ROLE_CODE            varchar(32),
   ADMIN_DESC           varchar(1000),
   primary key (ACTION_ID)
);

create table WF_NODE
(
   NODE_ID              numeric(12,0) not null comment '��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��',
   FLOW_CODE            varchar(32) comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   VERSION              numeric(4,0),
   NODE_TYPE            varchar(1) not null comment 'A:��ʼ B:�׽ڵ� C:ҵ��ڵ�  F����  R: ·�ɽڵ� ��
            �������ǲ����Ա���ģ��ڻ�ͼҳ���Ͽ��Բ���ʾ���׽ڵ㲻��Ҫ���ã������̷���ʱ�Ϳ�ʼ�ڵ�ֱ�������Ľڵ�����׽ڵ㣬����ڵ������ҵ��ڵ㡣',
   NODE_NAME            varchar(120),
   OPT_TYPE             varchar(1) comment 'A:һ�� B:���Ȼ��� C:���˲��� D: �Զ�ִ�� E��Ԫ��������Ƕ�׻�ۣ� S:������ ',
   OS_ID                varchar(32) comment 'ҵ��ϵͳ ��ҵ���������Ҫ',
   OPT_ID               varchar(32) comment 'ҵ��ϵͳ  OPTINFO��OPTID',
   OPT_CODE             varchar(32) comment '��Ӧ OPT_DEF �� OPTCODE ��OPTMETHOD ��������',
   OPT_BEAN             varchar(100) comment '�Զ�ִ�нڵ���Ҫ,����·���ж�bean',
   OPT_PARAM            varchar(100),
   SUB_FLOW_CODE        varchar(8) comment '�����̺�ҵ�����ֻ��һ����Ч������Ϊ��ָ��ҵ��ڵ�Ļ',
   ROUTER_TYPE          varchar(1) comment 'D:��֧ E:���  G ��ʵ���ڵ�  H����  R ���� S��ͬ��',
   ROLE_TYPE            varchar(8) comment 'xz gw bj  en',
   ROLE_CODE            varchar(32),
   UNIT_EXP             varchar(64),
   POWER_EXP            varchar(512),
   multiInst_Type       char(1) comment 'D ������ U ��Ա �� V ����',
   multiInst_Param      varchar(512) comment '�Զ�����������ڶ�ʵ���ڵ�ķ�֧',
   converge_Type        char(1) comment '���ж���ɣ� ������X��ɣ�������Xδ��ɣ���ɱ��ʴﵽX �� �Ⲻ�ж�',
   converge_Param       varchar(64),
   NODE_DESC            varchar(500),
   is_Account_Time      char(1) comment '���Ƿ����ʱ�� T/F',
   Limit_Type           char(1) comment '������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��
             F ÿʵ���̶����� fix ��C �ڵ�̶�����  cycle��
            ',
   Time_Limit           varchar(20) comment '5D4H �����ı��ʽ',
   inherit_Type         char(1) comment '0 ������ 1 �̳�ǰһ���ڵ� 2 �̳�ָ���ڵ�',
   inherit_Node_Code    varchar(20) comment '�̳л��ڴ���',
   Expire_Opt           char(1) comment 'N��֪ͨ�� O:������ �� S������E����ֹ�����̣��� C����ɣ�ǿ���ύ,�ύʧ�ܾ͹���',
   Warning_Rule         char(1) comment 'R������ʱ��  L:ʣ��ʱ�� P������',
   Warning_Param        varchar(20) comment '��һ��ʱ���ַ��� ���� ��ֵ',
   is_Trunk_Line        char(1) comment 'T / F',
   STAGE_CODE           varchar(32),
   NODE_CODE            varchar(20),
   RISK_INFO            varchar(4),
   primary key (NODE_ID)
);

alter table WF_NODE comment '2017-6-13 ����� osid  optid �޸��� OPTcode Ϊ varchar2(32)';

create table WF_NODE_INSTANCE
(
   NODE_INST_ID         numeric(12,0) not null,
   FLOW_INST_ID         numeric(12,0),
   NODE_ID              numeric(12,0) comment '��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��',
   CREATE_TIME          datetime,
   START_TIME           datetime,
   is_Timer             char(1),
   promise_Time         numeric(10,0) comment '��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���',
   time_Limit           numeric(10,0),
   PREV_NODE_INST_ID    numeric(12,0),
   NODE_STATE           varchar(2) comment '     * N ����  B �ѻ���    C ���   F��ǿ�ƽ��� 
                 * P ��ͣ   W �ȴ������̷���   S �ȵ�ǰ�ýڵ㣨�����Ƕ�������',
   SUB_FLOW_INST_ID     numeric(12,0),
   UNIT_CODE            varchar(8),
   STAGE_CODE           varchar(32),
   ROLE_TYPE            varchar(8) comment 'xz gw bj  en',
   ROLE_CODE            varchar(32),
   USER_CODE            varchar(8),
   NODE_PARAM           varchar(128),
   TRANS_ID             numeric(12,0) comment '����һ��·��������',
   TASK_ASSIGNED        varchar(1) default 'F' comment 'T: ͨ�� tasklist ���䣬 D��ͨ�� ��λ��������ɫ �Զ�ƥ�� S����̬���죨usercode)',
   Run_Token            varchar(20) comment '���ƣ� T* ��ʾ�������еĽڵ�  R* ��ʾ����ڵ�  L* ��ʾ��ʱ����Ľڵ�',
   GRANTOR              varchar(8),
   last_update_user     varchar(8),
   last_update_time     datetime,
   primary key (NODE_INST_ID)
);

create table WF_ROLE_RELEGATE
(
   RELEGATE_NO          numeric(12,0) not null,
   GRANTOR              varchar(8) not null,
   GRANTEE              varchar(8) not null,
   IS_VALID             char(1) not null default 'T' comment 'T:��Ч F:��Ч',
   Recorder             varchar(8),
   RELEGATE_TIME        datetime not null,
   EXPIRE_TIME          datetime,
   UNIT_CODE            varchar(8),
   ROLE_TYPE            varchar(8),
   ROLE_CODE            varchar(32),
   Record_Date          datetime,
   grant_Desc           varchar(256),
   primary key (RELEGATE_NO)
);

create table WF_ROUTER_NODE
(
   NODEID               numeric(12,0) not null comment '��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��',
   WFCODE               varchar(32) comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   VERSION              numeric(4,0),
   ROUTERTYPE           varchar(1) not null comment 'D:��֧ E:���  G ��ʵ���ڵ�  H����  R ����',
   NODENAME             varchar(120),
   NODEDESC             varchar(500),
   ROLETYPE             varchar(8) comment 'xz gw bj  en',
   ROLECODE             varchar(32),
   UNITEXP              varchar(64),
   POWEREXP             varchar(512),
   SELFDEFPARAM         varchar(512) comment '�Զ�����������ڶ�ʵ���ڵ�ķ�֧',
   convergeType         char(1) comment '���ж���ɣ� ������X��ɣ�������Xδ��ɣ���ɱ��ʴﵽX �� �Ⲻ�ж�',
   convergeParam        varchar(64),
   OPTBEAN              varchar(100) comment '�Զ�ִ�нڵ���Ҫ',
   primary key (NODEID)
);

create table WF_RUNTIME_WARNING
(
   WARNING_ID           numeric(12,0) not null comment 'sequence : S_WARNING_NO',
   FLOW_INST_ID         numeric(12,0),
   NODE_INST_ID         numeric(12,0) not null,
   FLOW_STAGE           varchar(4),
   OBJ_TYPE             char(1) comment 'F �� ������ N ���ڵ� P���׶�',
   WARNING_TYPE         char(1) comment 'W��Ԥ��  A  ���� N ����  O ����',
   WARNING_STATE        char(1) default 'N' comment 'D ժ�� C ���� F ���� N δ����',
   WARNING_CODE         varchar(16) comment 'ALTER_EXPIRED  : ʱ�䳬�ڱ��� 
            WARN_EXPIRED  : ʱ�䳬��Ԥ��',
   WARNING_TIME         datetime,
   WARNINGID_MSG        varchar(500),
   NOTICE_STATE         char(1) default '0' comment '0 ������ 1 �ѷ��� 2 ������Ϣʧ��',
   SEND_MSG_TIME        datetime,
   SEND_USERS           varchar(100) comment '�����Ƕ�����ö��Ÿ���',
   primary key (WARNING_ID)
);

create table WF_STAGE_INSTANCE
(
   FLOW_INST_ID         numeric(12,0) not null,
   STAGE_ID             numeric(12,0) not null,
   STAGE_CODE           varchar(32) not null,
   STAGE_NAME           varchar(60),
   begin_Time           datetime,
   stage_Begin          char(1) comment '0 ��û�н��룬 1 �Ѿ�����',
   promise_Time         numeric(10,0) comment '��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���',
   time_Limit           numeric(10,0),
   last_update_time     datetime,
   primary key (FLOW_INST_ID, STAGE_ID)
);

alter table WF_STAGE_INSTANCE comment '�����̴���ʱͬʱ��������������еĽ׶�';

create table WF_TEAM
(
   FLOW_INST_ID         numeric(12,0) not null,
   ROLE_CODE            varchar(32) not null,
   USER_CODE            varchar(8) not null,
   USER_ORDER           numeric(4,0) comment 'ͬһ����ɫ����û������',
   AUTH_DESC            varchar(255),
   AUTH_TIME            datetime not null default now(),
   primary key (FLOW_INST_ID, USER_CODE, ROLE_CODE)
);

alter table WF_TEAM comment '�������ҵ��ϵͳд�룬��������ֻ�����ȡ';

create table WF_TRANSITION
(
   TRANS_ID             numeric(12,0) not null,
   VERSION              numeric(4,0),
   FLOW_CODE            varchar(32) comment 'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾',
   TRANS_CLASS          varchar(4) comment '��δʹ��',
   TRANS_NAME           varchar(120),
   START_NODE_ID        numeric(12,0) comment '��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��',
   END_NODE_ID          numeric(12,0) comment '��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��',
   TRANS_CONDITION      varchar(500) comment 'a>500 && b<300',
   TRANS_DESC           varchar(500),
   is_Account_Time      char(1) comment '���Ƿ����ʱ�� T/F  I ����',
   Limit_Type           char(1) comment '������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��
             F ÿʵ���̶����� fix ��C �ڵ�̶�����  cycle��
            ',
   Time_Limit           varchar(20) comment '5D4H �����ı��ʽ',
   inherit_Type         char(1) comment '0 ������ 1 �̳�ǰһ���ڵ� 2 �̳�ָ���ڵ�',
   inherit_Node_Code    varchar(20) comment '�̳л��ڴ���',
   can_ignore           char(1) not null default 'T' comment 'T���Ժ��� F �����Ժ���  �Ƿ���Ժ�������',
   primary key (TRANS_ID)
);

create table WF_Task_Move
(
   move_no              numeric(12,0) not null,
   from_user            varchar(8),
   to_user              varchar(8),
   move_desc            varchar(1024),
   oper_user            varchar(8),
   oper_date            datetime,
   primary key (move_no)
);

create table WF_organize
(
   FLOW_INST_ID         numeric(12,0) not null,
   UNIT_CODE            varchar(8) not null,
   ROLE_CODE            varchar(32) not null,
   UNIT_ORDER           numeric(4,0) comment 'ͬһ����ɫ������������',
   AUTH_DESC            varchar(255),
   AUTH_TIME            datetime not null default now(),
   primary key (FLOW_INST_ID, UNIT_CODE, ROLE_CODE)
);


-- �����Ŀ�ܲ��ֱ�
create table F_OPTDEF  (
  OPTCODE              VARCHAR(32)                    not null,
  OptID                VARCHAR(32),
  OPTNAME              VARCHAR(100),
  OPTMETHOD            VARCHAR(50),
  OPTURL               VARCHAR(256),
  OPTDESC              VARCHAR(256),
  IsInWorkflow         CHAR(1),
  updateDate           DATETIME,
  CreateDate           DATETIME,
  OPTREQ               VARCHAR(8),
  optOrder 			numeric(4),
  creator              VARCHAR(32),
  updator              VARCHAR(32)
);
alter table F_OPTDEF
  add constraint PK_F_OPTDEF primary key (OPTCODE);

create table F_OptInfo  (
  OptID                VARCHAR(32)                    not null,
  OptName              VARCHAR(100)                   not null,
  PreOptID             VARCHAR(32)                    not null,
  optRoute             VARCHAR(256),
  opturl               VARCHAR(256),
  FormCode             VARCHAR(4),
  OptType              CHAR(1),
  MsgNo                numeric(10,0),
  MsgPrm               VARCHAR(256),
  IsInToolBar          CHAR(1),
  ImgIndex             numeric(10,0),
  TopOptID             VARCHAR(8),
  OrderInd             numeric(4,0),
  FLOWCODE             VARCHAR(8),
  PageType             CHAR(1)                        default 'I' not null,
  Icon                 VARCHAR(512),
  height               numeric(10,0),
  width                numeric(10,0),
  updateDate           DATETIME,
  CreateDate           DATETIME,
  creator              VARCHAR(32),
  updator              VARCHAR(32)
);
alter table F_OptInfo add constraint PK_F_OPTINFO primary key (OptID);

create table F_USERUNIT  (
  USERUNITID           VARCHAR(16)                    not null,
  UNITCODE             VARCHAR(6)                     not null,
  USERCODE             VARCHAR(8)                     not null,
  IsPrimary            CHAR(1)                        default '1' not null,
  UserStation          VARCHAR(16)                    not null,
  UserRank             VARCHAR(2)                     not null,
  RankMemo             VARCHAR(256),
  USERORDER            numeric(8)                      default 0,
  updateDate           DATETIME,
  CreateDate           DATETIME,
  creator              VARCHAR(32),
  updator              VARCHAR(32)
);
alter table F_USERUNIT
  add constraint PK_F_USERUNIT primary key (USERUNITID);

-- �����Ŀ����ͼ
create or replace view f_v_wf_optdef_url_map as
select CONCAT(c.opturl , b.opturl) as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null;
        
-- ��������ͼ   
create or replace view lastVersion as
select FLOW_CODE, max(version) as version 
            from wf_flow_define group by FLOW_CODE;        
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
   a.OS_ID,
   a.FLOW_PUBLISH_DATE,
   a.AT_PUBLISH_DATE
from 
    lastVersion 
    join wf_flow_define a     
       on a.FLOW_CODE = lastVersion.FLOW_CODE and a.version=0 
    join wf_flow_define b 
       on lastVersion.FLOW_CODE = b.FLOW_CODE and lastVersion.version=b.version;


create or replace view V_INNER_USER_TASK_LIST as
select a.FLOW_INST_ID,w.FLOW_CODE,w.version, w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, IFNULL(a.Unit_Code,IFNULL(w.Unit_Code,'0000000')) as UnitCode, 
        a.user_code,c.ROLE_TYPE,c.ROLE_CODE,'һ������' as AUTHDESC, c.node_code,
          c.Node_Name,c.Node_Type,c.Opt_Type as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.Opt_Param ,d.OptDesc,a.CREATE_TIME,a.Promise_Time,a.TIME_LIMIT,
          c.OPT_CODE,c.Expire_Opt,c.STAGE_CODE,a.last_update_user,a.LAST_UPDATE_TIME,w.inst_state
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.FLOW_INST_ID=w.FLOW_INST_ID)
           join WF_NODE c on (a.NODE_ID=c.NODE_ID)
           join f_v_wf_optdef_url_map d on (c.OPT_CODE=d.OPTCODE)
where /*c.NODETYPE<>'R' and --����ڵ㲻�ᴴ��ʱʵ��*/ 
    a.NODE_STATE='N' and w.INST_STATE='N' and a.TASK_ASSIGNED='S'
union all
select a.FLOW_INST_ID,w.FLOW_CODE,w.version, w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, IFNULL(a.Unit_Code,IFNULL(w.Unit_Code,'0000000')) as UnitCode, 
        b.user_code,b.ROLE_TYPE,b.ROLE_CODE,b.AUTH_DESC, c.node_code,
          c.Node_Name,c.Node_Type,c.Opt_Type as NODEOPTTYPE,d.optid,d.OptName,d.OptName as MethodName,
          d.optdefurl as OptUrl,d.optMethod,c.Opt_Param ,d.OptDesc,a.CREATE_TIME,a.Promise_Time,a.TIME_LIMIT,
          c.OPT_CODE,c.Expire_Opt,c.STAGE_CODE,a.last_update_user,a.LAST_UPDATE_TIME,w.inst_state
from WF_NODE_INSTANCE a join WF_FLOW_INSTANCE w on (a.FLOW_INST_ID=w.FLOW_INST_ID)
           join WF_ACTION_TASK b on (a.NODE_INST_ID=b.NODE_INST_ID)
           join WF_NODE c on (a.NODE_ID=c.NODE_ID)
           join f_v_wf_optdef_url_map d on (c.OPT_CODE=d.OPTCODE)
where a.NODE_STATE='N' and w.INST_STATE='N' and a.TASK_ASSIGNED='T'
    and b.IS_VALID='T' and  b.TASK_STATE='A' and (b.EXPIRE_TIME is null or b.EXPIRE_TIME>NOW())
union all
select  a.FLOW_INST_ID,w.FLOW_CODE,w.version,w.FLOW_OPT_NAME,w.FLOW_OPT_TAG,a.NODE_INST_ID, b.UnitCode ,
         b.usercode,c.ROLE_TYPE,c.ROLE_CODE, 'ϵͳָ��' as AUTHDESC, c.node_code,
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

create or replace view V_USER_TASK_LIST_temp as
select a.FLOW_INST_ID,a.FLOW_CODE,a.version, a.FLOW_OPT_NAME, a.FLOW_OPT_TAG, a.NODE_INST_ID, a.UnitCode, a.user_code, a.ROLE_TYPE, a.ROLE_CODE,
     a.AUTHDESC,a.node_code, a.Node_Name, a.Node_Type, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod,
      a.Opt_Param, a.OptDesc, a.CREATE_TIME, a.promise_time, a.time_limit,  a.OPT_CODE, a.Expire_Opt, a.STAGE_CODE, 
      null as GRANTOR, a.last_update_user, a.LAST_UPDATE_TIME ,  a.inst_state
  from V_INNER_USER_TASK_LIST a 
  union select a.FLOW_INST_ID,a.FLOW_CODE,a.version, a.FLOW_OPT_NAME, a.FLOW_OPT_TAG, a.node_inst_id, a.UnitCode, b.grantee as user_code, a.ROLE_TYPE, a.ROLE_CODE, 
    a.AUTHDESC,a.node_code, a.Node_Name, a.Node_Type, a.NODEOPTTYPE, a.optid, a.OptName, a.MethodName, a.OptUrl, a.optMethod, 
    a.Opt_Param, a.OptDesc, a.CREATE_TIME, a.promise_time, a.time_limit, a.OPT_CODE, a.Expire_Opt, a.STAGE_CODE, 
    b.GRANTOR, a.last_update_user, a.last_update_time ,  a.inst_state
    from V_INNER_USER_TASK_LIST a, WF_ROLE_RELEGATE b 
    where b.Is_Valid = 'T' and b.RELEGATE_TIME <= NOW() and 
          ( b.EXPIRE_TIME is null or b.EXPIRE_TIME >= NOW()) and 
          a.user_code = b.GRANTOR and ( b.UNIT_CODE is null or b.UNIT_CODE = a.UnitCode) 
          and ( b.ROLE_TYPE is null or ( b.ROLE_TYPE = a.ROLE_TYPE and ( b.ROLE_CODE is null or b.ROLE_CODE = a.ROLE_CODE) ) );

create or replace view V_USER_TASK_LIST(TASK_ID, FLOW_INST_ID, FLOW_CODE, VERSION, FLOW_NAME, FLOW_OPT_NAME, FLOW_OPT_TAG, NODE_INST_ID, UNIT_CODE, USER_CODE, ROLE_TYPE, ROLE_CODE, AUTH_DESC, NODE_CODE, NODE_NAME, NODE_TYPE, NODE_OPT_TYPE, OPT_ID, OPT_NAME, METHOD_NAME, OPT_URL, OPT_METHOD, OPT_PARAM, OPT_DESC, CREATE_TIME, PROMISE_TIME, TIME_LIMIT, OPT_CODE, EXPIRE_OPT, STAGE_CODE, GRANTOR, LAST_UPDATE_USER, LAST_UPDATE_TIME, INST_STATE) as
select (select count(*) from V_USER_TASK_LIST_temp where FLOW_INST_ID<=t.FLOW_INST_ID)as taskid,t.FLOW_INST_ID,t.FLOW_CODE,t.version,t.FLOW_OPT_NAME as WFNAME, t.FLOW_OPT_NAME,t.FLOW_OPT_TAG,t.NODE_INST_ID,t.UNITCODE,t.USER_CODE,
       t.ROLE_TYPE,t.ROLE_CODE,t.AUTHDESC,t.node_code, t.NODE_NAME,t.NODE_TYPE,t.NODEOPTTYPE,t.OPTID,t.OPTNAME,
       t.METHODNAME,t.OPTURL,t.OPTMETHOD,t.OPT_PARAM,t.OPTDESC,t.CREATE_TIME,t.PROMISE_TIME,
       t.TIME_LIMIT,t.OPT_CODE,t.EXPIRE_OPT,t.STAGE_CODE,t.GRANTOR,t.LAST_UPDATE_USER,t.LAST_UPDATE_TIME ,t.inst_state
from
  V_USER_TASK_LIST_temp t;

create or replace view v_node_instdetail as
select f.FLOW_OPT_NAME,f.FLOW_OPT_TAG,n.node_name,n.role_type,n.role_code,
d.OptName,d.OptName as MethodName,d.OptDefUrl as OptUrl,d.optMethod,n.opt_param,
 t.NODE_INST_ID, t.FLOW_INST_ID, t.NODE_ID, t.CREATE_TIME, t.PREV_NODE_INST_ID, t.NODE_STATE,
 t.SUB_FLOW_INST_ID, t.UNIT_CODE, t.TRANS_ID, t.TASK_ASSIGNED,
 t.RUN_TOKEN, t.TIME_LIMIT, t.LAST_UPDATE_USER, t.LAST_UPDATE_TIME, t.IS_TIMER, t.PROMISE_TIME, n.STAGE_CODE
  from wf_node_instance t
join wf_node n on t.node_id =  n.node_id
join f_v_wf_optdef_url_map d on (n.OPT_CODE=d.OPTCODE)
join wf_flow_instance f on t.FLOW_INST_ID = f.FLOW_INST_ID;



alter table wf_node change column multiInst_Type multi_Inst_Type char;
alter table wf_node change column multiInst_Param multi_Inst_Param VARCHAR(512);

alter table wf_node change column multiInst_Type multi_Inst_Type char;
alter table wf_node change column multiInst_Param multi_Inst_Param VARCHAR(512);
