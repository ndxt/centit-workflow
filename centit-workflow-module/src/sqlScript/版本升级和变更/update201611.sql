-- v3.0.1-snapshot 升级到  v3.0.2-snapshot
--添加流转路径 
alter table WF_NODE_INSTANCE add transpath VARCHAR2(256);
--迁移老的路径数据
update  WF_NODE_INSTANCE set transpath = to_char(transid);

commit;

--废弃的流转ID不需要
alter table WF_NODE_INSTANCE drop column transid;