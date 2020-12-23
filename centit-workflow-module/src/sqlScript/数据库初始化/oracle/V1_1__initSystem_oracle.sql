
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
'��������ͬʱ�����׽ڵ�  W
�����ڵ� C
�������� U
�ύ�ڵ� S
����ڵ� A
���ѽڵ� R
��ֹ�ڵ� E';

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
'A:�ѷ���  C������ɣ��ύ��W: ��ί�и�����';

comment on column WF_ACTION_TASK.IS_VALID is
'T  F ����ɾ����Ҳ����ʹʧЧ������ҵ����Ҫ�趨';

comment on column WF_ACTION_TASK.AUTH_DESC is
'������Ȩ�������Զ����䣬Ҳ�����ǹ���Աָ��';

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
'���� ҵ��������̣�һ�����̱���󶨵�һ��ҵ�񣬷����޷��ҵ���Ӧ��ҵ�������������ת';

comment on column WF_FLOW_DEFINE.FLOW_CODE is
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';

comment on column WF_FLOW_DEFINE.VERSION is
'�汾��Ϊ 0 ��Ϊ�ݸ壬 ��Ч�汾�Ŵ� 1 ��ʼ';

comment on column WF_FLOW_DEFINE.FLOW_CLASS is
'��δʹ��';

comment on column WF_FLOW_DEFINE.FLOW_Publish_Date is
'����ʱ�䣬Ҳ������ʱ��';

comment on column WF_FLOW_DEFINE.FLOW_STATE is
'A �ݸ� B ���� C ���� D ���� E �ѷ���  (A,E����0�汾��Ч)';

comment on column WF_FLOW_DEFINE.FLOW_XML_DESC is
'ͼ�ζ���������ɵ�XML�ļ�';

comment on column WF_FLOW_DEFINE.Time_Limit is
'һСʱ�ƣ�8СʱΪһ�죬С�������Ϊ���ӣ�����0.30��ʾ30����';

comment on column WF_FLOW_DEFINE.Expire_Opt is
'N��֪ͨ�� O:������ �� S������E����ֹ';

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
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';

comment on column WF_FLOW_INSTANCE.FLOW_Opt_Name is
'��������û� ����������Ϣ';

comment on column WF_FLOW_INSTANCE.FLOW_Opt_Tag is
'�������û� ����������Ϣ�����������룬��ҵ��ϵͳ�Լ�����';

comment on column WF_FLOW_INSTANCE.promise_Time is
'��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���';

comment on column WF_FLOW_INSTANCE.INST_STATE is
' N ����  C ���   P ��ͣ ����     F ǿ�н���
';

comment on column WF_FLOW_INSTANCE.IS_SUB_INST is
'Y �ǵ� N  ����';

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
'�汾��Ϊ 0 ��Ϊ�ݸ壬 ��Ч�汾�Ŵ� 1 ��ʼ';

comment on column WF_FLOW_STAGE.FLOW_CODE is
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';

comment on column WF_FLOW_STAGE.is_Account_Time is
'���Ƿ���루���̣�ʱ�� T/F';

comment on column WF_FLOW_STAGE.Limit_Type is
'������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��C �̶�����  cycle';

comment on column WF_FLOW_STAGE.Time_Limit is
'5D4H �����ı��ʽ';

comment on column WF_FLOW_STAGE.Expire_Opt is
'N��֪ͨ�� O:������ �� S������E����ֹ�����̣��� C����ɣ�ǿ���ύ,�ύʧ�ܾ͹���';

create table WF_FLOW_VARIABLE  (
   FLOW_INST_ID         NUMBER(12,0)                    not null,
   Run_Token          VARCHAR2(20)                    not null,
   VAR_NAME             VARCHAR2(50)                    not null,
   VAR_VALUE            VARCHAR2(1024)                   not null,
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
'��ע�и����⣬����һ����ע����βſ�д�����������������Ҫд�� OPT_IDEA_INFO ��



�������ʱ���ã��� ������ �Ĺ�ע';

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
'����ǶԽڵ�ʵ���Ĺ����������ֵ';

comment on column WF_MANAGE_ACTION.ACTION_TYPE is
'�����̹�������ô�д��ĸ���Խڵ���������Сд��ĸ
  S s: ״̬����� ��ʱ���ѡ� ʹʧЧ�� ʹһ�������Ľڵ��Ϊ����״̬ �� ������ڵ�ʧЧ
  c: �����ڵ�  ������һ������ڵ� ���������⣩ָ���ڵ�
  R  : ��ת��������  ǿ�л���  ��ǿ���ύ
  T t: ���޹��� �� ��������
  a: �ڵ��������  ��������  ɾ������ ��  ��������
  U u: �������''';

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
'2017-6-13 ����� osid  optid �޸��� OPTcode Ϊ varchar2(32)';

comment on column WF_NODE.NODE_ID is
'��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��';

comment on column WF_NODE.FLOW_CODE is
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';

comment on column WF_NODE.NODE_TYPE is
'A:��ʼ B:�׽ڵ� C:ҵ��ڵ�  F����  R: ·�ɽڵ� ��
�������ǲ����Ա���ģ��ڻ�ͼҳ���Ͽ��Բ���ʾ���׽ڵ㲻��Ҫ���ã������̷���ʱ�Ϳ�ʼ�ڵ�ֱ�������Ľڵ�����׽ڵ㣬����ڵ������ҵ��ڵ㡣';

comment on column WF_NODE.OPT_TYPE is
'A:һ�� B:���Ȼ��� C:���˲��� D: �Զ�ִ�� E��Ԫ��������Ƕ�׻�ۣ� S:������ ';

comment on column WF_NODE.OS_ID is
'ҵ��ϵͳ ��ҵ���������Ҫ';

comment on column WF_NODE.OPT_ID is
'ҵ��ϵͳ  OPTINFO��OPTID';

comment on column WF_NODE.OPT_CODE is
'��Ӧ OPT_DEF �� OPTCODE ��OPTMETHOD ��������';

comment on column WF_NODE.OPT_BEAN is
'�Զ�ִ�нڵ���Ҫ,����·���ж�bean';

comment on column WF_NODE.SUB_FLOW_CODE is
'�����̺�ҵ�����ֻ��һ����Ч������Ϊ��ָ��ҵ��ڵ�Ļ';

comment on column WF_NODE.ROUTER_TYPE is
'D:��֧ E:���  G ��ʵ���ڵ�  H����  R ���� S��ͬ��';

comment on column WF_NODE.ROLE_TYPE is
'xz gw bj  en';

comment on column WF_NODE.multiInst_Type is
'D ������ U ��Ա �� V ����';

comment on column WF_NODE.multiInst_Param is
'�Զ�����������ڶ�ʵ���ڵ�ķ�֧';

comment on column WF_NODE.converge_Type is
'���ж���ɣ� ������X��ɣ�������Xδ��ɣ���ɱ��ʴﵽX �� �Ⲻ�ж�';

comment on column WF_NODE.is_Account_Time is
'���Ƿ����ʱ�� T/F';

comment on column WF_NODE.Limit_Type is
'������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��
 F ÿʵ���̶����� fix ��C �ڵ�̶�����  cycle��
';

comment on column WF_NODE.Time_Limit is
'5D4H �����ı��ʽ';

comment on column WF_NODE.inherit_Type is
'0 ������ 1 �̳�ǰһ���ڵ� 2 �̳�ָ���ڵ�';

comment on column WF_NODE.inherit_Node_Code is
'�̳л��ڴ���';

comment on column WF_NODE.Expire_Opt is
'N��֪ͨ�� O:������ �� S������E����ֹ�����̣��� C����ɣ�ǿ���ύ,�ύʧ�ܾ͹���';

comment on column WF_NODE.Warning_Rule is
'R������ʱ��  L:ʣ��ʱ�� P������';

comment on column WF_NODE.Warning_Param is
'��һ��ʱ���ַ��� ���� ��ֵ';

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
'��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��';

comment on column WF_NODE_INSTANCE.promise_Time is
'��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���';

comment on column WF_NODE_INSTANCE.NODE_STATE is
'     * N ����  B �ѻ���    C ���   F��ǿ�ƽ���
     * P ��ͣ   W �ȴ������̷���   S �ȵ�ǰ�ýڵ㣨�����Ƕ�������';

comment on column WF_NODE_INSTANCE.ROLE_TYPE is
'xz gw bj  en';

comment on column WF_NODE_INSTANCE.TRANS_ID is
'����һ��·��������';

comment on column WF_NODE_INSTANCE.TASK_ASSIGNED is
'T: ͨ�� tasklist ���䣬 D��ͨ�� ��λ��������ɫ �Զ�ƥ�� S����̬���죨usercode)';

comment on column WF_NODE_INSTANCE.Run_Token is
'���ƣ� T* ��ʾ�������еĽڵ�  R* ��ʾ����ڵ�  L* ��ʾ��ʱ����Ľڵ�';

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
'T:��Ч F:��Ч';

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
'F �� ������ N ���ڵ� P���׶�';

comment on column WF_RUNTIME_WARNING.WARNING_TYPE is
'W��Ԥ��  A  ���� N ����  O ����';

comment on column WF_RUNTIME_WARNING.WARNING_STATE is
'D ժ�� C ���� F ���� N δ����';

comment on column WF_RUNTIME_WARNING.WARNING_CODE is
'ALTER_EXPIRED  : ʱ�䳬�ڱ���
WARN_EXPIRED  : ʱ�䳬��Ԥ��';

comment on column WF_RUNTIME_WARNING.NOTICE_STATE is
'0 ������ 1 �ѷ��� 2 ������Ϣʧ��';

comment on column WF_RUNTIME_WARNING.SEND_USERS is
'�����Ƕ�����ö��Ÿ���';

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
'�����̴���ʱͬʱ��������������еĽ׶�';

comment on column WF_STAGE_INSTANCE.stage_Begin is
'0 ��û�н��룬 1 �Ѿ�����';

comment on column WF_STAGE_INSTANCE.promise_Time is
'��ŵʱ�� 1��8Сʱ��1Сʱ60 ���� ������շ��Ӽ���';

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
'�������ҵ��ϵͳд�룬��������ֻ�����ȡ';

comment on column WF_TEAM.USER_ORDER is
'ͬһ����ɫ����û������';

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
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';

comment on column WF_TRANSITION.TRANS_CLASS is
'��δʹ��';

comment on column WF_TRANSITION.START_NODE_ID is
'��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��';

comment on column WF_TRANSITION.END_NODE_ID is
'��һ������Ľڵ㴴���ڵ㣨000001��������Ӧ��Ȩ�����������Ƿ��������Ȩ��';

comment on column WF_TRANSITION.TRANS_CONDITION is
'a>500 && b<300';

comment on column WF_TRANSITION.is_Account_Time is
'���Ƿ����ʱ�� T/F  I ����';

comment on column WF_TRANSITION.Limit_Type is
'������� I �� δ���ã�ignore Ĭ�� ����N �� (������ none ) ��
 F ÿʵ���̶����� fix ��C �ڵ�̶�����  cycle��
';

comment on column WF_TRANSITION.Time_Limit is
'5D4H �����ı��ʽ';

comment on column WF_TRANSITION.inherit_Type is
'0 ������ 1 �̳�ǰһ���ڵ� 2 �̳�ָ���ڵ�';

comment on column WF_TRANSITION.inherit_Node_Code is
'�̳л��ڴ���';

comment on column WF_TRANSITION.can_ignore is
'T���Ժ��� F �����Ժ���  �Ƿ���Ժ�������';

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
'ͬһ����ɫ������������';

--�����Ŀ�ܲ��ֱ�
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
  optOrder             number(4),
  creator              VARCHAR2(32),
  updator              VARCHAR2(32)
);
comment on column F_OPTDEF.OPTMETHOD is
'�������� ����';
comment on column F_OPTDEF.IsInWorkflow is
'�Ƿ�Ϊ���̲������� F������  T �� ��';
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
'��angularjs·��ƥ��';
comment on column F_OptInfo.OptType is
' S:ʵʩҵ��, O:��ͨҵ��, W:����ҵ��, I :��Ŀҵ��';
comment on column F_OptInfo.OrderInd is
'���˳��ֻ����ͬһ����ҵ��������';
comment on column F_OptInfo.FLOWCODE is
'ͬһ�����������Ӧ��ֻ��һ����Ч�İ汾';
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
'ͬһ���˿����ڶ�����ŵ��β�ͬ��ְλ';
comment on column F_USERUNIT.IsPrimary is
'T��Ϊ���� F����ְ';
comment on column F_USERUNIT.UserRank is
'RANK ���벻�� 0��ͷ�Ŀ��Խ�������';
comment on column F_USERUNIT.RankMemo is
'��ְ��ע';
alter table F_USERUNIT
  add constraint PK_F_USERUNIT primary key (USERUNITID);

--�����Ŀ����ͼ
create or replace view f_v_wf_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null;

--��������ͼ
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
'����������Ϣ��������Ϣ����ͼ';
