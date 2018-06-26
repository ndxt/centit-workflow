--- 
begin
  for r in ( select fi.wfinstid,fi.wfcode, fi.version, fd.version  as newversion 
        from wf_flow_instance fi join F_V_LASTVERTIONFLOW fd on fi.wfcode=fd.wfcode
        where fi.version <> fd.version and fi.inststate <> 'C' and fi.inststate <> 'F' and fi.inststate <> 'I') loop
             
      update wf_flow_instance set version = r.newversion where wfinstid = r.wfinstid;
      
      update wf_node_instance wfni
             set wfni.nodeid = nvl( (select max(wfn.nodeid) from wf_node wfn where  wfn.wfcode = r.wfcode and wfn.version = r.newversion 
                       and wfn.nodecode = (select wfnn.nodecode from wf_node wfnn where wfnn.nodeid=wfni.nodeid )),nodeid)
      where wfni.wfinstid = r.wfinstid;     
  end loop;  
end;
/

-- 
create table wf_flow_update (  WFINSTID    NUMBER(12)    Not null primary key,
    WFCODE    VARCHAR2(8),
  VERSION    NUMBER(4),
  NEWVERSION    NUMBER(4))
/

create table wf_node_update (  NODEINSTID    NUMBER(12)    Not null primary key,
    NODECODE    VARCHAR2(20),
  NODEID    NUMBER(12),
  NEWNODEID    NUMBER(12))
/

begin
  for r in ( select fi.wfinstid,fi.wfcode, fi.version, fd.version  as newversion 
        from wf_flow_instance fi join F_V_LASTVERTIONFLOW fd on fi.wfcode=fd.wfcode
        where fi.version <> fd.version and fi.inststate <> 'C' and fi.inststate <> 'F' and fi.inststate <> 'I') loop
      
      delete from wf_flow_update where wfinstid = r.wfinstid;
      insert into wf_flow_update(WFINSTID,WFCODE    , VERSION, NEWVERSION)
            values(r.wfinstid,r.wfcode, r.version, r.version);            
      
      update wf_flow_instance set version = r.newversion where wfinstid = r.wfinstid;

      for node in (select wfni.nodeinstid,wfni.nodeid ,n1.nodecode,  
                            n2.nodeid as newnodeid 
                   from wf_node_instance wfni join wf_node n1 on wfni.nodeid = n1.nodeid
                         join wf_node n2 on n2.nodecode = n1.nodecode and n2.wfcode = r.wfcode
                               and n2.version=r.newversion
                   where n2.nodeid is not null and n2.nodeid <> wfni.nodeid
                         /*group by wfni.nodeinstid */) loop         
         
         delete from wf_node_update where  NODEINSTID = node.nodeinstid;
         insert into wf_node_update(NODEINSTID,NODECODE    , NODEID, NEWNODEID)
            values(node.nodeinstid,node.nodecode, node.nodeid,node.newnodeid);
         update wf_node_instance set NODEID = node.newnodeid where NODEINSTID = node.nodeinstid;
      end loop;       
      --commit;  
  end loop;  
end;
/
