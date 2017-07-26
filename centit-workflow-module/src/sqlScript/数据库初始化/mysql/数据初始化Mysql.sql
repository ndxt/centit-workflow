

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('FlowUnitRole', '机构表达式', 'U', 'L', '用户流程机构', null, null, null, null, null);

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('FlowUserRole', '办件角色', 'U', 'L', '用户流程工作小组', null, null, null, null, '1');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('WfActionType', '流程活动类型', 'S', 'L', '创建流程同时创建首节点  W 创建节点 C 更改数据 U 提交节点 S 挂起节点 A 唤醒节点 R  终止节点 E  唤醒超时节点 X', '流程活动类型', null, null, null, null);

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('WFFlowState', '流程状态', 'S', 'L', 'A 草稿  E 已发布 (A,E仅对0版本有效) B 正常 C 过期 D 禁用', '编码,数值', null, null, null, '1');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('WFInstType', '流程、节点实例状态', 'S', 'L', 'N 正常/ R 运行(保留)/ C 完成/ S 挂起  / X 超时挂起eXpire / D 强制提交  /B 已回退  /E 因为流程完成而结束 / F 因为流程强制结束而被强制结束 / W 等待子流程返回  / I 失效
 ', '流程实例状态', null, null, null, '1');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('WFRoleType', '流程角色类别', 'S', 'L', '流程角色类别。一共四种 en：引擎，bj：办件 gw：岗位，xz：行政', null, null, null, null, null);

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('WFExpireOpt', '流程预期处理方法', 'S', 'L', 'N：通知， O:不处理 ， S：挂起，E：终止', '流程预期处理方法', null, null, null, '1');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('LIMIT_TYPE', '流程期限类别', 'S', 'L', '期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle、H 继承上一个节点剩余时间 hierarchical。', '流程预期类别', null, null, null, '1');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, OPTID, CREATEDATE, UpdateDate, NEEDCACHE)
values ('YesOrNo', '是否标记', 'S', 'L', 'T/F是否标记', '是否标记', null, null, null, '1');

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('YesOrNo', 'T', null, null, 'T', '是(T)', null, null, null, null, 1);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('YesOrNo', 'F', null, null, 'T', '否(F)', null, null, null, null, 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LIMIT_TYPE', 'I', null, null, 'T', '未设置（ignore 默认 ）', null, null, null, null, 1);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LIMIT_TYPE', 'N', null, null, 'T', '无 (无期限 none )', null, null, null, null, 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LIMIT_TYPE', 'F', null, null, 'T', '每实例固定期限 fix', null, null, null, null, 3);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LIMIT_TYPE', 'C', null, null, 'T', '节点固定期限  cycle', null, null, null, null, 4);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LIMIT_TYPE', 'H', null, null, 'T', '继承上一个节点剩余时间', null, null, null, null, 5);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFExpireOpt', 'N', null, null, 'T', '通知', null, null, null, null, 1);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFExpireOpt', 'O', null, null, 'T', '不处理', null, null, null, null, 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFExpireOpt', 'S', null, null, 'T', '挂起', null, null, null, null, 3);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFExpireOpt', 'E', null, null, 'T', '终止', null, null, null, null, 4);


insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'N', null, null, 'T', '正常', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'S', null, null, 'T', '等待前置节点完成', null, '流程实例状态', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'F', null, null, 'T', '被强制结束', null, ' 因为流程强制结束而被强制结束', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'C', null, null, 'T', '完成', null, '流程实例状态', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'B', null, null, 'T', '已退回', null, '流程实例状态', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'A', null, null, 'T', '挂起节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'C', null, null, 'T', '创建节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'E', null, null, 'T', '终止节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'R', null, null, 'T', '唤醒节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'S', null, null, 'T', '提交节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'U', null, null, 'T', '更改数据', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'W', null, null, 'T', '创建首节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WfActionType', 'X', null, null, 'T', '唤醒超时节点', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'wbzrys', null, null, 'T', '委办主任阅示', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'ajjbr', null, null, 'T', '案件经办人', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'csnw', null, null, 'T', '处室拟文', 'S', '处室拟文', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'fgwldqf', null, null, 'T', '分管委领导签发', 'S', '分管委领导签发，多人操作角色', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'jbr', null, null, 'T', '经办人', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'ldys', null, null, 'T', '领导阅示', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'leader', null, null, 'T', '项目负责人', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'wldqf', null, null, 'T', '委领导签发', 'S', '委领导签发，多人操作角色', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'xbcbr', null, null, 'T', '协办承办人', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUserRole', 'zbcbr', null, null, 'T', '主办承办人', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'P', null, null, null, '暂停', null, '流程实例状态', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFInstType', 'W', null, null, null, '等待子流程返回', null, '流程实例状态', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFFlowState', 'A', null, null, null, '草稿', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFFlowState', 'E', null, null, null, '已发布', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFFlowState', 'B', null, null, null, '正常', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFFlowState', 'C', null, null, null, '过期', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFFlowState', 'D', null, null, null, '禁用', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFRoleType', 'en', null, null, 'T', '引擎', null, 'en：引擎', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFRoleType', 'gw', null, null, 'T', '岗位', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFRoleType', 'xz', null, null, 'T', '行政', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('WFRoleType', 'bj', null, null, 'T', '办件', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('FlowUnitRole', 'unitexp1', null, null, 'T', '机构表达式1', 'S', null, null, null, null);


insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE, CREATOR, UPDATOR)
values ('WORKFLOW', '工作流程业务', '0', '...', '...', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, now(), null, null, null);

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE, CREATOR, UPDATOR)
values ('FLOWDEF', '流程定义', 'WORKFLOW', 'modules/sys/flowdefine/flowdefine.html', 'service/flow/define', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, now(), null, null, null);

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE, CREATOR, UPDATOR)
values ('FLOWMGR', '流程实例管理', 'WORKFLOW', 'modules/sys/flowmanager/flowmanager.html', 'service/flow/useroptmgr', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, now(), null, null, null);

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE, CREATOR, UPDATOR)
values ('USERFLOW', '用户流程任务管理', 'WORKFLOW', 'modules/sys/useroptmgr/useroptmanager.html', 'service/flow/manager', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, now(), null, null, null);

--
insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTURL, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTREQ, CREATOR, UPDATOR)
values ('1000101', 'FLOWDEF', '新建', null, '/', '新建（系统默认）', null, now(), null, 'C', null, null);

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTURL, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTREQ, CREATOR, UPDATOR)
values ('1000102', 'FLOWMGR', '新建', null, '/', '新建（系统默认）', null, now(), null, 'C', null, null);

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTURL, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTREQ, CREATOR, UPDATOR)
values ('1000103', 'USERFLOW', '新建', null, '/', '新建（系统默认）', null, now(), null, 'C', null, null);

insert into f_rolepower (ROLECODE, OPTCODE, UPDATEDATE, CREATEDATE, OPTSCOPECODES,CREATOR,UPDATOR)
values ('G-SYSADMIN', '1000101', now(), now(), null,'u0000000','u0000000');

insert into f_rolepower (ROLECODE, OPTCODE, UPDATEDATE, CREATEDATE, OPTSCOPECODES,CREATOR,UPDATOR)
values ('G-SYSADMIN', '1000102', now(), now(), null,'u0000000','u0000000');

insert into f_rolepower (ROLECODE, OPTCODE, UPDATEDATE, CREATEDATE, OPTSCOPECODES,CREATOR,UPDATOR)
values ('G-SYSADMIN', '1000103', now(), now(), null,'u0000000','u0000000');

