/*==============================================================*/
/* MySql 数据库脚本                                               */
/*==============================================================*/

DROP TABLE IF EXISTS f_mysql_sequence;

CREATE TABLE  f_mysql_sequence (
  name varchar(50) NOT NULL,
  currvalue int(11) NOT NULL,
  increment int(11) NOT NULL DEFAULT '1',
   primary key (name)
) ;

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_MSGCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_RECIPIENT', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_UNITCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_USERCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_USER_UNIT_ID', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_ADDRESSID', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_OPTDEFCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
('S_SYS_LOG', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES
  ('S_ROLECODE', 0, 1);


drop table if exists F_DATACATALOG;

drop table if exists F_DATADICTIONARY;

drop table if exists F_OPTDATASCOPE;

drop table if exists F_OPTDEF;

drop table if exists F_OPT_LOG;

drop table if exists F_OptInfo;

drop table if exists F_QUERY_FILTER_CONDITION;

drop table if exists F_RANKGRANT;

drop table if exists F_ROLEINFO;

drop table if exists F_ROLEPOWER;

drop table if exists F_SYS_NOTIFY;

drop table if exists F_UNITINFO;

drop table if exists F_USERINFO;

drop table if exists F_USERROLE;

drop table if exists F_USERSETTING;

drop table if exists F_USERUNIT;

drop table if exists F_USER_FAVORITE;

drop table if exists F_USER_QUERY_FILTER;

drop table if exists M_InnerMsg;

drop table if exists M_InnerMsg_Recipient;

drop table if exists M_MsgAnnex;

drop table if exists  F_UNITROLE;

/*==============================================================*/
/* Table: F_DATACATALOG                                         */
/*==============================================================*/
create table F_DATACATALOG
(
   CATALOG_CODE         varchar(16) not null,
   CATALOG_NAME         varchar(64) not null,
   CATALOG_STYLE        char(1) not null comment 'F : 框架固有的 U:用户 S：系统  G国标',
   CATALOG_TYPE         char(1) not null comment 'T：树状表格 L:列表
            ',
   CATALOG_DESC         varchar(256),
   Field_Desc           varchar(1024) comment '字段描述，不同字段用分号隔开',
   update_Date          datetime,
   Create_Date          datetime,
   opt_ID               varchar(16) comment '业务分类，使用数据字典DICTIONARYTYPE中数据',
   need_Cache           char(1) default '1',
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_DATACATALOG comment '类别状态	 U:用户 S：系统，G国标
类别形式  T：树状表格 L:列表
';

alter table F_DATACATALOG
   add primary key (CATALOG_CODE);

/*==============================================================*/
/* Table: F_DATADICTIONARY                                      */
/*==============================================================*/
create table F_DATADICTIONARY
(
   CATALOG_CODE         varchar(16) not null,
   DATA_CODE            varchar(16) not null,
   EXTRA_CODE           varchar(16) comment '树型字典的父类代码',
   EXTRA_CODE2          varchar(16) comment '默认的排序字段',
   DATA_TAG             char(1) comment 'N正常，D已停用，用户可以自解释这个字段',
   DATA_VALUE           varchar(2048),
   DATA_STYLE           char(1) comment 'F : 框架固有的 U:用户 S：系统  G国标',
   DATA_DESC            varchar(256),
   Last_Modify_Date     datetime,
   Create_Date          datetime,
   DATA_ORDER           numeric(6,0) comment '排序字段'
);

alter table F_DATADICTIONARY comment '数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等
';

alter table F_DATADICTIONARY
   add primary key (CATALOG_CODE, DATA_CODE);

/*==============================================================*/
/* Table: F_OPTDATASCOPE                                        */
/*==============================================================*/
create table F_OPTDATASCOPE
(
   opt_Scope_Code       varchar(16) not null,
   Opt_ID               varchar(16),
   scope_Name           varchar(64),
   Filter_Condition     varchar(1024) comment '条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码',
   scope_Memo           varchar(1024) comment '数据权限说明',
   Filter_Group         varchar(16) default 'G'
);

alter table F_OPTDATASCOPE
   add primary key (opt_Scope_Code);

/*==============================================================*/
/* Table: F_OPTDEF                                              */
/*==============================================================*/
create table F_OPTDEF
(
   OPT_CODE             varchar(32) not null,
   Opt_ID               varchar(32),
   OPT_NAME             varchar(100),
   OPT_METHOD           varchar(50) comment '操作参数 方法',
   OPT_URL              varchar(256),
   OPT_DESC             varchar(256),
   opt_Order            numeric(4,0),
   Is_In_Workflow       char(1) comment '是否为流程操作方法 F：不是  T ： 是',
   update_Date          datetime,
   Create_Date          datetime,
   OPT_REQ              varchar(8),
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_OPTDEF
   add primary key (OPT_CODE);

/*==============================================================*/
/* Table: F_OPT_LOG                                             */
/*==============================================================*/
create table F_OPT_LOG
(
   log_Id               numeric(12,0) not null,
   log_Level            varchar(2) not null,
   user_code            varchar(8) not null,
   opt_time             datetime not null,
   Opt_Content          varchar(1000) not null comment '操作描述',
   New_Value            text comment '新值',
   Old_Value            text comment '原值',
   Opt_ID               varchar(64) not null comment '模块，或者表',
   OPT_Method           varchar(64) comment '方法，或者字段',
   opt_Tag              varchar(200) comment '一般用于关联到业务主体的标识、表的主键等等'
);

alter table F_OPT_LOG
   add primary key (log_Id);


/*==============================================================*/
/* Table: F_OptInfo                                             */
/*==============================================================*/
create table F_OptInfo
(
   Opt_ID               varchar(32) not null,
   Opt_Name             varchar(100) not null,
   Pre_Opt_ID           varchar(32) not null,
   opt_Route            varchar(256) comment '与angularjs路由匹配',
   opt_url              varchar(256),
   Form_Code            varchar(4),
   Opt_Type             char(1) comment ' S:实施业务, O:普通业务, W:流程业务, I :项目业务',
   Msg_No               numeric(10,0),
   Msg_Prm              varchar(256),
   Is_In_ToolBar        char(1),
   Img_Index            numeric(10,0),
   Top_Opt_ID           varchar(8),
   Order_Ind            numeric(4,0) comment '这个顺序只需在同一个父业务下排序',
   FLOW_CODE            varchar(8) comment '同一个代码的流程应该只有一个有效的版本',
   Page_Type            char(1) not null default 'I' comment 'D : DIV I:iFrame',
   Icon                 varchar(512),
   height               numeric(10,0),
   width                numeric(10,0),
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_OptInfo
   add primary key (Opt_ID);


/*==============================================================*/
/* Table: F_QUERY_FILTER_CONDITION                              */
/*==============================================================*/
create table F_QUERY_FILTER_CONDITION
(
   CONDITION_NO         numeric(12,0) not null,
   Table_Class_Name     varchar(64) not null comment '数据库表代码或者po的类名',
   Param_Name           varchar(64) not null comment '参数名',
   Param_Label          varchar(120) not null comment '参数输入框提示',
   Param_Type           varchar(8) comment '参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月',
   Default_Value        varchar(100),
   Filter_Sql           varchar(200) comment '过滤语句，将会拼装到sql语句中',
   Select_Data_type     char(1) not null default 'N' comment '数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取
            ',
   Select_Data_Catalog  varchar(64) comment '数据字典',
   Select_SQL           varchar(1000) comment '有两个返回字段的sql语句',
   Select_JSON          varchar(2000) comment 'KEY,Value数值对，JSON格式'
);

alter table F_QUERY_FILTER_CONDITION
   add primary key (CONDITION_NO);

/*==============================================================*/
/* Table: F_RANKGRANT                                           */
/*==============================================================*/
create table F_RANKGRANT
(
   RANK_grant_ID        numeric(12,0) not null,
   granter              varchar(8) not null,
   UNITCODE             varchar(6) not null,
   UserStation          varchar(4) not null,
   UserRank             varchar(2) not null comment 'RANK 代码不是 0开头的可以进行授予',
   beginDate            datetime not null,
   grantee              varchar(8) not null,
   endDate              datetime,
   grantDesc            varchar(256),
   LastModifyDate       datetime,
   CreateDate           datetime
);

alter table F_RANKGRANT
   add primary key (RANK_grant_ID, UserRank);

/*==============================================================*/
/* Table: F_ROLEINFO                                            */
/*==============================================================*/
create table F_ROLEINFO
(
   ROLE_CODE            varchar(32) not null,
   ROLE_NAME            varchar(64),
   ROLE_TYPE            char(1) not null comment 'F 为系统 固有的 G 全局的 P 公用的 D 部门的 I 为项目角色 W工作量角色',
   UNIT_CODE            varchar(32),
   IS_VALID             char(1) not null,
   ROLE_DESC            varchar(256),
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_ROLEINFO
   add primary key (ROLE_CODE);

/*==============================================================*/
/* Table: F_ROLEPOWER                                           */
/*==============================================================*/
create table F_ROLEPOWER
(
   ROLE_CODE            varchar(32) not null,
   OPT_CODE             varchar(32) not null,
   opt_Scope_Codes      varchar(1000) comment '用逗号隔开的数据范围结合（空\all 表示全部）',
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_ROLEPOWER
   add primary key (ROLE_CODE, OPT_CODE);

/*==============================================================*/
/* Table: F_SYS_NOTIFY                                          */
/*==============================================================*/
create table F_SYS_NOTIFY
(
   Notify_ID            numeric(12,0) not null,
   Notify_Sender        varchar(100),
   Notify_Receiver      varchar(100) not null,
   Msg_Subject          varchar(200),
   Msg_Content          varchar(2000) not null,
   notice_Type          varchar(100),
   Notify_State         char(1) comment '0 成功， 1 失败 2 部分成功',
   Error_Msg            varchar(500),
   Notify_Time          datetime,
   opt_Tag              varchar(200) comment '一般用于关联到业务主体',
   OPT_Method           varchar(64) comment '方法，或者字段',
   Opt_ID               varchar(64) not null comment '模块，或者表'
);

alter table F_SYS_NOTIFY
   add primary key (Notify_ID);

/*==============================================================*/
/* Table: F_UNITINFO                                            */
/*==============================================================*/
create table F_UNITINFO
(
   UNIT_CODE            varchar(32) not null,
   PARENT_UNIT          varchar(32),
   UNIT_TYPE            char(1) comment '发布任务/ 邮电规划/组队/接收任务',
   IS_VALID             char(1) not null comment 'T:生效 F:无效',
   UNIT_TAG             varchar(100) comment '用户第三方系统管理',
   UNIT_NAME            varchar(300) not null,
   english_Name         varchar(300),
   dep_no               varchar(100) comment '组织机构代码：',
   UNIT_DESC            varchar(256),
   ADDRBOOK_ID          numeric(10,0),
   UNIT_SHORT_NAME      varchar(32),
   unit_Word            varchar(100),
   unit_Grade           numeric(4,0),
   unit_Order           numeric(4,0),
   update_Date          datetime,
   Create_Date          datetime,
   extJsonInfo          varchar(1000),
   creator              varchar(32),
   updator              varchar(32),
   UNIT_PATH            varchar(1000),
   UNIT_MANAGER         varchar(32)
);

alter table F_UNITINFO
   add primary key (UNIT_CODE);

/*==============================================================*/
/* Table: F_USERINFO                                            */
/*==============================================================*/
create table F_USERINFO
(
   USER_CODE            varchar(32) not null,
   USER_PIN             varchar(100),
   USER_TYPE            char(1) default 'U' comment '发布任务/接收任务/系统管理',
   IS_VALID             char(1) not null comment 'T:生效 F:无效',
   LOGIN_NAME           varchar(100) not null,
   User_Name            varchar(300) not null comment '昵称',
   USER_TAG             varchar(100) comment '用于第三方系统关联',
   english_Name         varchar(300),
   USER_DESC            varchar(256),
   Login_Times          numeric(6,0),
   Active_Time          datetime,
   Login_IP             varchar(16),
   ADDRBOOK_ID          numeric(10,0),
   Reg_Email            varchar(60) comment '注册用Email，不能重复',
   USER_PWD             varchar(20) comment '如果需要可以有',
   pwd_Expired_Time     datetime,
   REG_CELL_PHONE       varchar(15),
   ID_CARD_NO           varchar(20),
   primary_Unit         varchar(32),
   user_Word            varchar(100) comment '微信号',
   user_Order           numeric(4,0),
   update_Date          datetime,
   Create_Date          datetime,
   extJsonInfo          varchar(1000),
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_USERINFO
   add primary key (USER_CODE);

/*==============================================================*/
/* Table: F_USERROLE                                            */
/*==============================================================*/
create table F_USERROLE
(
   USER_CODE            varchar(32) not null,
   ROLE_CODE            varchar(32) not null,
   OBTAIN_DATE          datetime not null,
   SECEDE_DATE          datetime,
   CHANGE_DESC          varchar(256),
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_USERROLE
   add primary key (USER_CODE, ROLE_CODE);

/*==============================================================*/
/* Table: F_USERSETTING                                         */
/*==============================================================*/
create table F_USERSETTING
(
   USER_CODE            varchar(32) not null comment 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   Param_Code           varchar(32) not null,
   Param_Value          varchar(2048) not null,
   opt_ID               varchar(16) not null,
   Param_Name           varchar(200),
   Create_Date          datetime
);

alter table F_USERSETTING
   add primary key (USER_CODE, Param_Code);

/*==============================================================*/
/* Table: F_USERUNIT                                            */
/*==============================================================*/
create table F_USERUNIT
(
   USER_UNIT_ID         varchar(32) not null,
   UNIT_CODE            varchar(32) not null,
   USER_CODE            varchar(32) not null,
   Is_Primary           char(1) not null default '1' comment 'T：为主， F：兼职',
   User_Station         varchar(16) not null,
   User_Rank            varchar(16) not null comment 'RANK 代码不是 0开头的可以进行授予',
   Rank_Memo            varchar(256) comment '任职备注',
   USER_ORDER           numeric(8,0) default 0,
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);

alter table F_USERUNIT comment '同一个人可能在多个部门担任不同的职位';

alter table F_USERUNIT
   add primary key (USER_UNIT_ID);

/*==============================================================*/
/* Table: F_USER_FAVORITE                                       */
/*==============================================================*/
create table F_USER_FAVORITE
(
   USERCODE             varchar(8) not null comment 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   OptID                varchar(16) not null,
   LastModifyDate       datetime,
   CreateDate           datetime
);

alter table F_USER_FAVORITE
   add primary key (USERCODE, OptID);

/*==============================================================*/
/* Table: F_USER_QUERY_FILTER                                   */
/*==============================================================*/
create table F_USER_QUERY_FILTER
(
   FILTER_NO            numeric(12,0) not null,
   user_Code            varchar(8) not null,
   modle_code           varchar(64) not null comment '开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法',
   filter_name          varchar(200) not null comment '用户自行定义的名称',
   filter_value         varchar(3200) not null comment '变量值，json格式，对应一个map'
);

alter table F_USER_QUERY_FILTER
   add primary key (FILTER_NO);

/*==============================================================*/
/* Table: M_InnerMsg                                            */
/*==============================================================*/
create table M_InnerMsg
(
   Msg_Code             varchar(16) not null comment '消息主键自定义，通过S_M_INNERMSG序列生成',
   Sender               varchar(128),
   Send_Date            datetime,
   Msg_Title            varchar(128),
   Msg_Type             varchar(16) comment 'P= 个人为消息  A= 机构为公告（通知）
            M=邮件',
   Mail_Type            char(1) comment 'I=收件箱
            O=发件箱
            D=草稿箱
            T=废件箱',
   Mail_UnDel_Type      char(1),
   Receive_Name         varchar(2048) comment '使用部门，个人中文名，中间使用英文分号分割',
   Hold_Users           numeric(8,0) comment '总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录

            消息类型为邮件时不需要设置',
   msg_State            char(1) comment '未读/已读/删除',
   msg_Content          longblob,
   Email_Id             varchar(8) comment '用户配置多邮箱时使用',
   Opt_ID               varchar(64) not null comment '模块，或者表',
   OPT_Method           varchar(64) comment '方法，或者字段',
   opt_Tag              varchar(200) comment '一般用于关联到业务主体'
);

alter table M_InnerMsg comment '内部消息与公告
接受代码,  其实可以独立出来, 因为他 和发送人 是 一对多的关系

                               -&#';

alter table M_InnerMsg
   add primary key (Msg_Code);

/*==============================================================*/
/* Table: M_InnerMsg_Recipient                                  */
/*==============================================================*/
create table M_InnerMsg_Recipient
(
   Msg_Code             varchar(16) not null,
   Receive              varchar(8) not null,
   Reply_Msg_Code       int,
   Receive_Type         char(1) comment 'P=个人为消息
            A=机构为公告
            M=邮件',
   Mail_Type            char(1) comment 'T=收件人
            C=抄送
            B=密送',
   msg_State            char(1) comment '未读/已读/删除，收件人在线时弹出提示

            U=未读
            R=已读
            D=删除',
   ID                   varchar(16) not null
);

alter table M_InnerMsg_Recipient comment '内部消息（邮件）与公告收件人及消息信息';

alter table M_InnerMsg_Recipient
   add primary key (ID);

/*==============================================================*/
/* Table: M_MsgAnnex                                            */
/*==============================================================*/
create table M_MsgAnnex
(
   Msg_Code             varchar(16) not null,
   Info_Code            varchar(16) not null,
   Msg_Annex_Id         varchar(16) not null
);

alter table M_MsgAnnex
   add primary key (Msg_Annex_Id);


create table F_UNITROLE
(
   UNIT_CODE            varchar(32) not null,
   ROLE_CODE            varchar(32) not null,
   OBTAIN_DATE          datetime not null,
   SECEDE_DATE          datetime,
   CHANGE_DESC          varchar(256),
   update_Date          datetime,
   Create_Date          datetime,
   creator              varchar(32),
   updator              varchar(32)
);
alter table F_UNITROLE
   add primary key (UNIT_CODE, ROLE_CODE);

--  函数


DROP FUNCTION IF EXISTS sequence_currval;

DELIMITER //

CREATE  FUNCTION sequence_currval(seq_name VARCHAR(50)) RETURNS int(11)

    READS SQL DATA

    DETERMINISTIC

BEGIN

DECLARE cur_value INTEGER;

SET cur_value = 0;

SELECT currvalue INTO cur_value FROM f_mysql_sequence WHERE NAME = seq_name;

RETURN cur_value;

END//

DELIMITER ;


DROP FUNCTION IF EXISTS sequence_nextval;

DELIMITER //

CREATE  FUNCTION sequence_nextval(seq_name VARCHAR(50)) RETURNS int(11)

    DETERMINISTIC

BEGIN
DECLARE cur_value INTEGER;

UPDATE f_mysql_sequence SET currvalue = currvalue + increment WHERE NAME = seq_name;

SELECT currvalue INTO cur_value FROM f_mysql_sequence WHERE NAME = seq_name;

RETURN cur_value;

END//

DELIMITER ;


DROP FUNCTION IF EXISTS sequence_setval;

DELIMITER //

CREATE  FUNCTION sequence_setval(seq_name VARCHAR(50),seq_value int(11)) RETURNS int(11)

    DETERMINISTIC

BEGIN

UPDATE f_mysql_sequence SET currvalue = seq_value WHERE NAME = seq_name;
RETURN seq_value;
END//
DELIMITER ;
DROP FUNCTION IF EXISTS calcUnitPath;
DELIMITER $$


CREATE FUNCTION calcUnitPath (chrId varchar(32))
	RETURNS varchar(1000)
BEGIN
   DECLARE sTemp VARCHAR(32);
   DECLARE sPreTemp VARCHAR(32);
   DECLARE path VARCHAR(1000);
   DECLARE rs VARCHAR(1000);
   SET  sTemp = trim(chrId);
   SET  path = '';
   REPEAT
   	  SET  path = concat('/',sTemp, path);
   	  set sPreTemp = sTemp;
      SELECT unit_code INTO sTemp
         FROM f_unitinfo
         where unit_code =
         		(select parent_unit FROM f_unitinfo where unit_code = sTemp);
      until sTemp is null or sTemp='' or sPreTemp = sTemp
   END REPEAT;

   RETURN path;
END$$

DELIMITER ;


-- v_hi_unitinfo视图脚本

CREATE OR REPLACE VIEW v_hi_unitinfo AS
SELECT a.unit_code AS top_unit_code,  b.unit_code,b.unit_type, b.parent_unit, b.is_valid,     b.unit_name,b.unit_desc,b.unit_short_name,b.addrbook_id,b.unit_order,b.dep_no,
       b.unit_word,b.unit_grade,
       LENGTH(b.Unit_Path)- LENGTH(REPLACE(b.Unit_Path,'/','')) - LENGTH(a.Unit_Path) + LENGTH(REPLACE(a.Unit_Path,'/',''))+1  AS hi_level,
       substr(b.Unit_Path ,  LENGTH(a.Unit_Path)+1) AS Unit_Path
  FROM F_UNITINFO a , F_UNITINFO b
 WHERE b.Unit_Path LIKE CONCAT(a.Unit_Path,'%' );

create or replace view F_V_USERROLES as
  select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'D' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
    b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,a.USER_CODE, NULL as INHERITED_FROM
  from F_USERROLE a join F_ROLEINFO b on (a.ROLE_CODE=b.ROLE_CODE)
  where a.OBTAIN_DATE <=  now() and (a.SECEDE_DATE is null or a.SECEDE_DATE > now()) and b.IS_VALID='T'
  union
  select b.ROLE_CODE, b.ROLE_NAME, b.IS_VALID, 'I' as OBTAIN_TYPE, b.ROLE_TYPE, b.UNIT_CODE,
    b.ROLE_DESC, b.CREATE_DATE, b.UPDATE_DATE ,c.USER_CODE, a.UNIT_CODE as INHERITED_FROM
  from F_UNITROLE a join F_ROLEINFO b on (a.ROLE_CODE = b.ROLE_CODE) JOIN F_USERUNIT c on( a.UNIT_CODE = c.UNIT_CODE)
  where a.OBTAIN_DATE <=  now() and (a.SECEDE_DATE is null or a.SECEDE_DATE > now()) and b.IS_VALID='T';


 create or replace view F_V_Opt_Role_Map as
select concat(`c`.`opt_url`,`b`.`OPT_URL`) as opt_url, b.opt_req, a.role_code, c.opt_id, b.opt_code
  from F_ROLEPOWER a
  join F_OPTDEF b
    on (a.opt_code = b.opt_code)
  join F_OptInfo c
    on (b.opt_id = c.opt_id)
 where c.Opt_Type <> 'W'
   and c.opt_url <> '...'
 order by c.opt_url, b.opt_req, a.role_code;


/*==============================================================*/
/* View: F_V_UserOptDataScopes                                  */
/*==============================================================*/
create or replace view F_V_UserOptDataScopes as
select  distinct a.User_Code, c. OPT_ID ,  c.OPT_METHOD , b.opt_Scope_Codes
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.Role_Code=b.Role_Code)
         join F_OPTDEF  c on(b.OPT_CODE=c.OPT_CODE);
/*==============================================================*/
/* View: F_V_UserOptList                                        */
/*==============================================================*/
create or replace view F_V_UserOptList as
select  distinct a.User_Code,  c.OPT_CODE,  c.OPT_NAME  ,  c. OPT_ID ,  c.OPT_METHOD
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.Role_Code=b.Role_Code)
         join F_OPTDEF  c on(b.OPT_CODE=c.OPT_CODE);

/*==============================================================*/
/* View: F_V_UserOptMoudleList                                  */
/*==============================================================*/

create or replace view f_v_useroptmoudlelist as
select  distinct a.User_Code,d.Opt_ID, d.Opt_Name , d.Pre_Opt_ID  ,
            d.Form_Code  , d.opt_url, d.opt_route, d.Msg_No , d.Msg_Prm, d.Is_In_ToolBar ,
            d.Img_Index,d.Top_Opt_ID ,d.Order_Ind,d.Page_Type,d.opt_type
from F_V_USERROLES a  join F_ROLEPOWER b on (a.Role_Code=b.Role_Code)
         join F_OPTDEF  c on(b.OPT_CODE=c.OPT_CODE)
        join F_OptInfo d on(c.OPT_ID=d.Opt_ID)
where d.opt_url<>'...';

/*==============================================================*/
/* View: f_v_optdef_url_map                                     */
/*==============================================================*/
create or replace view f_v_optdef_url_map as
select concat(`c`.`opt_url`,`b`.`OPT_URL`) as opt_def_url, b.opt_req, b.opt_code
from F_OPTDEF b join F_OptInfo c
    on (b.opt_id = c.opt_id)
 where c.Opt_Type <> 'W'
   and c.opt_url <> '...' and b.opt_req is not null;

/*==============================================================*/
/* View: v_opt_tree                                             */
/*==============================================================*/
create or replace view v_opt_tree as
   select i.opt_id as MENU_ID,i.pre_opt_id as PARENT_ID,i.opt_name as MENU_NAME,i.order_ind
   from F_OptInfo i where i.is_in_toolbar ='Y'
   union all
   select d.opt_code as MENU_ID,d.opt_id as PARENT_ID,d.opt_name as MENU_NAME,0 as order_ind
   from F_OPTDEF d
;
