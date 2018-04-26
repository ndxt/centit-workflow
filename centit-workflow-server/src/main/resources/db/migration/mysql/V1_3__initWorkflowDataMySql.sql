
INSERT INTO f_datacatalog(catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WFExpireOpt', '流程预期处理方法', 'S', 'L', 'N：通知， O:不处理 ， S：挂起，E：终止', '流程预期处理方法', null, null, null, '1', null, null);


INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('FlowUnitRole', '机构表达式', 'U', 'L', '用户流程机构', null, null, null, null, null, null, null);
INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('LIMIT_TYPE', '流程期限类别', 'S', 'L', '期限类别 I ： 未设置（ignore 默认 ）、N 无 (无期限 none ) 、 F 每实例固定期限 fix 、C 节点固定期限  cycle、H 继承上一个节点剩余时间 hierarchical。', '流程预期类别', null, null, null, '1', null, null);
INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WfActionType', '流程活动类型', 'S', 'L', '创建流程同时创建首节点  W 创建节点 C 更改数据 U 提交节点 S 挂起节点 A 唤醒节点 R  终止节点 E  唤醒超时节点 X', '流程活动类型', null, null, null, null, null, null);
INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WFFlowState', '流程状态', 'S', 'L', 'A 草稿  E 已发布 (A,E仅对0版本有效) B 正常 C 过期 D 禁用', '编码,数值', null, null, null, '1', null, null);
INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WFInstType', '流程、节点实例状态', 'S', 'L', 'N 正常/ R 运行(保留)/ C 完成/ S 挂起  / X 超时挂起eXpire / D 强制提交  /B 已回退  /E 因为流程完成而结束 / F 因为流程强制结束而被强制结束 / W 等待子流程返回  / I 失效\r\n ', '流程实例状态', null, null, null, '1', null, null);
INSERT INTO f_datacatalog (catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WFRoleType', '流程角色类别', 'S', 'L', '流程角色类别。一共四种 en：引擎，bj：办件 gw：岗位，xz：行政', null, null, null, null, null, null, null);



INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('FlowUnitRole', 'unitexp1', null, null, 'T', '机构表达式1', 'S', null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('LIMIT_TYPE', 'C', null, null, 'T', '节点固定期限  cycle', null, null, null, null, '4');
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('LIMIT_TYPE', 'F', null, null, 'T', '每实例固定期限 fix', null, null, null, null, '3');
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('LIMIT_TYPE', 'H', null, null, 'T', '继承上一个节点剩余时间', null, null, null, null, '5');
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('LIMIT_TYPE', 'I', null, null, 'T', '未设置（ignore 默认 ）', null, null, null, null, '1');
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('LIMIT_TYPE', 'N', null, null, 'T', '无 (无期限 none )', null, null, null, null, '2');
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'A', null, null, 'T', '挂起节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'C', null, null, 'T', '创建节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'E', null, null, 'T', '终止节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'R', null, null, 'T', '唤醒节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'S', null, null, 'T', '提交节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'U', null, null, 'T', '更改数据', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'W', null, null, 'T', '创建首节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WfActionType', 'X', null, null, 'T', '唤醒超时节点', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFFlowState', 'A', null, null, null, '草稿', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFFlowState', 'B', null, null, null, '正常', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFFlowState', 'C', null, null, null, '过期', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFFlowState', 'D', null, null, null, '禁用', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFFlowState', 'E', null, null, null, '已发布', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'B', null, null, 'T', '已退回', null, '流程实例状态', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'C', null, null, 'T', '完成', null, '流程实例状态', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'F', null, null, 'T', '被强制结束', null, ' 因为流程强制结束而被强制结束', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'N', null, null, 'T', '正常', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'P', null, null, null, '暂停', null, '流程实例状态', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'S', null, null, 'T', '等待前置节点完成', null, '流程实例状态', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFInstType', 'W', null, null, null, '等待子流程返回', null, '流程实例状态', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFRoleType', 'bj', null, null, 'T', '办件', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFRoleType', 'en', null, null, 'T', '引擎', null, 'en：引擎', null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFRoleType', 'gw', null, null, 'T', '岗位', null, null, null, null, null);
INSERT INTO f_datadictionary (catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFRoleType', 'xz', null, null, 'T', '行政', null, null, null, null, null);





INSERT INTO f_datadictionary(catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFExpireOpt', 'E', null, null, 'T', '终止', null, null, null, null, '4');
INSERT INTO f_datadictionary(catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFExpireOpt', 'N', null, null, 'T', '通知', null, null, null, null, '1');
INSERT INTO f_datadictionary(catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFExpireOpt', 'O', null, null, 'T', '不处理', null, null, null, null, '2');
INSERT INTO f_datadictionary(catalog_code, data_code, extra_code, extra_code2, data_tag, data_value, data_style, data_desc, last_modify_date, create_date, data_order) VALUES ('WFExpireOpt', 'S', null, null, 'T', '挂起', null, null, null, null, '3');


INSERT INTO f_mysql_sequence VALUES ('S_FLOWDEFINE', '0', '1');
INSERT INTO f_mysql_sequence VALUES ('S_FLOWDEFNO', '0', '1');



INSERT INTO f_optdef(opt_code, opt_id, opt_name, opt_method, opt_url, opt_desc, opt_order, is_in_workflow, update_date, create_date, opt_req, creator, updator) VALUES ('10001', 'WORKFLOW', '查看', 'search', '/changeme', '查看（系统默认）', null, null, null, null, 'CRUD', null, null);
INSERT INTO f_optdef(opt_code, opt_id, opt_name, opt_method, opt_url, opt_desc, opt_order, is_in_workflow, update_date, create_date, opt_req, creator, updator) VALUES ('10002', 'WFDEFINE', '查看', 'search', '/changeme', '查看（系统默认）', null, null, null, null, 'CRUD', null, null);
INSERT INTO f_optdef(opt_code, opt_id, opt_name, opt_method, opt_url, opt_desc, opt_order, is_in_workflow, update_date, create_date, opt_req, creator, updator) VALUES ('10003', 'FLOWMGR', '查看', 'search', '/changeme', '查看（系统默认）', null, null, null, null, 'CRUD', null, null);
INSERT INTO f_optdef(opt_code, opt_id, opt_name, opt_method, opt_url, opt_desc, opt_order, is_in_workflow, update_date, create_date, opt_req, creator, updator) VALUES ('10004', 'USERFLOW', '查看', 'search', '/changeme', '查看（系统默认）', null, null, null, null, 'CRUD', null, null);


INSERT INTO f_optinfo(opt_id, opt_name, pre_opt_id, opt_route, opt_url, form_code, opt_type, msg_no, msg_prm, is_in_toolbar, img_index, top_opt_id, order_ind, flow_code, page_type, icon, height, width, update_date, create_date, creator, updator) VALUES ('bx_lc', '报销流程', '0', '/', '/', null, 'W', null, null, 'N', null, null, null, null, 'D', '', null, null, null, null, null, null);
INSERT INTO f_optinfo(opt_id, opt_name, pre_opt_id, opt_route, opt_url, form_code, opt_type, msg_no, msg_prm, is_in_toolbar, img_index, top_opt_id, order_ind, flow_code, page_type, icon, height, width, update_date, create_date, creator, updator) VALUES ('FLOWMGR', '流程实例管理', 'WORKFLOW', 'modules/sys/flowmanager/flowmanager.html', 'modules/sys/flowmanager/flowmanager.html', null, 'O', null, null, 'Y', null, null, '2', null, 'D', '', null, null, null, null, null, null);
INSERT INTO f_optinfo(opt_id, opt_name, pre_opt_id, opt_route, opt_url, form_code, opt_type, msg_no, msg_prm, is_in_toolbar, img_index, top_opt_id, order_ind, flow_code, page_type, icon, height, width, update_date, create_date, creator, updator) VALUES ('USERFLOW', '用户流程任务管理', 'WORKFLOW', 'modules/sys/useroptmgr/useroptmanager.html', 'modules/sys/useroptmgr/useroptmanager.html', null, 'O', null, null, 'Y', null, null, '3', null, 'D', '', null, null, null, null, null, null);
INSERT INTO f_optinfo(opt_id, opt_name, pre_opt_id, opt_route, opt_url, form_code, opt_type, msg_no, msg_prm, is_in_toolbar, img_index, top_opt_id, order_ind, flow_code, page_type, icon, height, width, update_date, create_date, creator, updator) VALUES ('WFDEFINE', '流程定义', 'WORKFLOW', 'modules/sys/flowdefine/flowdefine.html', 'modules/sys/flowdefine/flowdefine.html', null, 'O', null, null, 'Y', null, null, '1', null, 'D', '', null, null, null, null, null, null);
INSERT INTO f_optinfo(opt_id, opt_name, pre_opt_id, opt_route, opt_url, form_code, opt_type, msg_no, msg_prm, is_in_toolbar, img_index, top_opt_id, order_ind, flow_code, page_type, icon, height, width, update_date, create_date, creator, updator) VALUES ('WORKFLOW', '工作流', '0', '...', '...', null, 'O', null, null, 'Y', null, null, null, null, 'D', '', null, null, null, null, null, null);


-- 工作流菜单
insert into F_ROLEPOWER(role_code,opt_code,update_Date,create_date,opt_scope_codes,CREATOR,UPDATOR)
	select 'sysadmin',opt_code,now(),now(),'',CREATOR,UPDATOR from f_optdef  where opt_code in('10001','10002','10003','10004');



--  框架无该数据
INSERT INTO f_usersetting VALUES ('u0000000', 'LOCAL_LANG', 'zh_CN', 'SYS', '用户默认区域语言', '2018-04-16 16:01:30');

commit;



