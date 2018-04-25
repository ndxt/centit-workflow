
INSERT INTO f_datacatalog(catalog_code, catalog_name, catalog_style, catalog_type, catalog_desc, field_desc, update_date, create_date, opt_id, need_cache, creator, updator) VALUES ('WFExpireOpt', '流程预期处理方法', 'S', 'L', 'N：通知， O:不处理 ， S：挂起，E：终止', '流程预期处理方法', null, null, null, '1', null, null);

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



