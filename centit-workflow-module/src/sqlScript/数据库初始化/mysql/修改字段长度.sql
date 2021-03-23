-- 修改工作流表中的userCode字段长度。
alter table f_opt_log  modify column USER_CODE varchar(32);
alter table wf_action_task  modify column USER_CODE varchar(32);
alter table wf_flow_instance  modify column last_update_time varchar(32);
alter table wf_flow_instance  modify column USER_CODE varchar(32);

alter table wf_node_instance  modify column USER_CODE varchar(32);
alter table wf_node_instance  modify column last_update_user varchar(32);

alter table wf_inst_attention  modify column USER_CODE varchar(32);
alter table wf_inst_attention  modify column att_set_user varchar(32);

alter table wf_team  modify column USER_CODE varchar(32);
alter table wf_role_relegate  modify column Recorder varchar(32);
alter table wf_role_relegate  modify column GRANTOR varchar(32);
alter table wf_role_relegate  modify column GRANTEE varchar(32);

-- 修改工作流表中的unitCode字段长度。
alter table wf_role_relegate  modify column UNIT_CODE varchar(32);
alter table wf_organize  modify column UNIT_CODE varchar(32);
alter table wf_node_instance  modify column UNIT_CODE varchar(32);
alter table wf_flow_instance  modify column UNIT_CODE varchar(32);
