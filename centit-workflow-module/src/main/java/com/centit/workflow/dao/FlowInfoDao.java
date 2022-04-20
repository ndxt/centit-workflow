package com.centit.workflow.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowInfoId;
import com.centit.workflow.po.LastVersionFlowDefine;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowInfoDao extends BaseDaoImpl<FlowInfo, FlowInfoId> {
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("flowCode" , "flowCode = :flowCode");
        filterField.put("version" , "version = :version");
        filterField.put("flowName" , CodeBook.LIKE_HQL_ID);
        filterField.put("flowState" , CodeBook.LIKE_HQL_ID);
        filterField.put("flowDesc" , CodeBook.LIKE_HQL_ID);
        filterField.put("osId" , CodeBook.EQUAL_HQL_ID);
        filterField.put("optId" , CodeBook.EQUAL_HQL_ID);
        filterField.put(CodeBook.ORDER_BY_HQL_ID, "version DESC, flowPublishDate DESC, flowCode DESC");
        return filterField;
    }

    @Transactional
    public long getLastVersion(String flowCode){
        String sql = "select max(t.VERSION) from WF_FLOW_DEFINE t where t.FLOW_CODE = ?";
        return this.getJdbcTemplate().queryForObject(sql,
                new Object[]{flowCode} ,Long.class);
    }

    @Transactional
    public List<FlowInfo> getAllVersionFlowsByCode(String wfCode, PageDesc pageDesc){
        return this.listObjectsByFilterAsJson("where FLOW_CODE = ? order by version desc",
                new Object[]{wfCode},pageDesc).toJavaList(FlowInfo.class);
    }
    @Transactional
    public FlowInfo getLastVersionFlowByCode(String flowCode){
        long lVer = getLastVersion(flowCode);
        return this.getObjectById(new FlowInfoId(lVer, flowCode));
    }
    @Transactional
    public FlowInfo getFlowDefineByID(String flowCode, Long version)
    {
        return super.getObjectWithReferences(new FlowInfoId(version, flowCode));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<FlowInfo> getFlowsByState(String wfstate)
    {
        String sql="SELECT * FROM F_V_LASTVERSIONFLOW WHERE FLOW_STATE = ? ORDER BY VERSION";
        return  this.getJdbcTemplate().query(sql,
                new Object[]{wfstate} ,new BeanPropertyRowMapper<FlowInfo>(FlowInfo.class));
    }

    @Transactional
    public Map<String, String> listFlowCodeNameMap(){
        String sql = "SELECT FLOW_CODE, FLOW_NAME FROM F_V_LASTVERSIONFLOW" ;
        List<Object[]> codeNameData = DatabaseOptUtils.listObjectsBySql(this, sql);
        Map<String, String> codeNameMap = new HashMap<>();
        if(codeNameData!=null){
            for(Object[] codeName : codeNameData){
                codeNameMap.put(StringBaseOpt.castObjectToString(codeName[0]),
                    StringBaseOpt.castObjectToString(codeName[1]));
            }
        }
        return codeNameMap;
    }

    @Transactional
    public List<FlowInfo> listLastVersionFlowByOptId(String optId){
        String sql =  "SELECT * FROM F_V_LASTVERSIONFLOW WHERE opt_id = ? AND flow_state != 'D'" ;
        return this.listObjectsBySql(sql, new Object[] {optId});
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public List<FlowInfo> listLastVersionFlows(
            Map<String, Object> filterMap, PageDesc pageDesc) {

        String sql =  "select VERSION,FLOW_CODE,FLOW_NAME,FLOW_CLASS,FLOW_STATE,FLOW_DESC,FLOW_XML_DESC," +
                "FLOW_PUBLISH_DATE,OPT_ID,TIME_LIMIT " +
                " from F_V_LASTVERSIONFLOW " +
            " where 1=1 " +
            " [:flowState| and FLOW_STATE = :flowState ] " +
            " [:(like)flowName| and FLOW_NAME like :flowName ] " +
            " [:flowCode| and FLOW_CODE = :flowCode ]" +
            " [:optId| and OPT_ID = :optId ] order by OPT_ID" ;
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql,filterMap);
        JSONArray dataList = DatabaseOptUtils.listObjectsByNamedSqlAsJson(this,
                queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);
        List<LastVersionFlowDefine> ls = new ArrayList<>();
        if(dataList != null) {
            ls = JSONArray.parseArray(dataList.toJSONString(),LastVersionFlowDefine.class);
        }
        List<FlowInfo>all=new ArrayList<FlowInfo>();
        if(ls != null && ls.size() > 0) {
            for (LastVersionFlowDefine s : ls) {
                all.add(s.toWfFlowDefine());
            }
        }
        return all;
    }

    @Transactional
    public void deleteObjectByFlowCode(String flowCode){
        String sql="delete from wf_flow_define where flow_code=?";
        this.getJdbcTemplate().update(sql,flowCode);
    }

}
