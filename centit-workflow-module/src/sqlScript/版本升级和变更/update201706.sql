-- v3.0.1-snapshot 升级到  v4.0.0-snapshot
--添加流转路径 
alter table WF_NODE add OSID VARCHAR2(32);
alter table WF_NODE add OPTID VARCHAR2(32);
alter table WF_NODE modify optcode VARCHAR2(32);
