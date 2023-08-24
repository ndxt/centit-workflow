package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowWarning;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FlowWarningDao extends BaseDaoImpl<FlowWarning, String> {

    //public static final Logger logger = LoggerFactory.getLogger(WfRuntimeWarningDao.class);

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<String, String>();
        filterField.put("warningId", CodeBook.EQUAL_HQL_ID);
        filterField.put("nodeInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("flowInstId", CodeBook.EQUAL_HQL_ID);
        filterField.put("flowStage", CodeBook.EQUAL_HQL_ID);
        filterField.put("warningType", CodeBook.EQUAL_HQL_ID);
        filterField.put("warningCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("warningTime", CodeBook.LIKE_HQL_ID);
        filterField.put("warningState", CodeBook.EQUAL_HQL_ID);
        filterField.put("warningidMsg", CodeBook.LIKE_HQL_ID);
        filterField.put("sendMsgTime", CodeBook.LIKE_HQL_ID);
        filterField.put("sendUsers", CodeBook.LIKE_HQL_ID);
        return filterField;
    }

    @Transactional
    public List<FlowWarning> listFlowWarningByInst(String flowInstId, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where flow_Inst_Id = ?",
            new Object[]{flowInstId}, pageDesc).toJavaList(FlowWarning.class);
    }

    @Transactional
    public List<FlowWarning> listFlowWarning(String flowInstId, String nodeInstId, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where flow_Inst_Id = ? and node_Inst_Id = ?",
            new Object[]{flowInstId, nodeInstId}, pageDesc).toJavaList(FlowWarning.class);
    }

    @Transactional
    public List<FlowWarning> listFlowWarning(String flowInstId, String nodeInstId,String warningType,String objType, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where flow_Inst_Id = ? and node_Inst_Id = ? and OBJ_TYPE = ? and warning_type = ?",
            new Object[]{flowInstId, nodeInstId, objType, warningType}, pageDesc).toJavaList(FlowWarning.class);
    }

    @Transactional
    public List<FlowWarning> listFlowWarning(String flowInstId, String warningType,String objType, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where flow_Inst_Id = ? and (node_Inst_Id = '0' or node_Inst_Id = null)" +
                " and OBJ_TYPE = ? and warning_type = ?",
            new Object[]{flowInstId, objType, warningType}, pageDesc).toJavaList(FlowWarning.class);
    }

    @Transactional
    public List<FlowWarning> listFlowWarningByNodeInst(String nodeInstId, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where node_Inst_Id = ?",
            new Object[]{nodeInstId}, pageDesc).toJavaList(FlowWarning.class);
    }


    public List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc) {
        return this.listObjectsByFilterAsJson("where warning_Code = ?",
            new Object[]{warningCode}, pageDesc).toJavaList(FlowWarning.class);
    }

    public List<FlowWarning> listNeedNotifyWarning() {
        return this.listObjectsByFilter("where notice_State = '0'", new Object[]{});
    }

}
