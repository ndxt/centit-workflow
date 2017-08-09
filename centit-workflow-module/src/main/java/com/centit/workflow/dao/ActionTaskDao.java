package com.centit.workflow.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.database.DBType;
import com.centit.support.database.QueryAndNamedParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.support.database.QueryUtils;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.po.ActionTask;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 流程任务数据操作类
 * 
 * @author ljy
 * @version $Rev$ <br>
 *          $Id$
 */
@Repository
public class ActionTaskDao extends BaseDaoImpl<ActionTask,Long>
{
 	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();
			filterField.put("taskId" , CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeInstId" , CodeBook.EQUAL_HQL_ID);
			filterField.put("assignTime" , CodeBook.EQUAL_HQL_ID);
			filterField.put("expireTime" , CodeBook.EQUAL_HQL_ID);
			filterField.put("userCode" , CodeBook.EQUAL_HQL_ID);
			filterField.put("roleType" , CodeBook.EQUAL_HQL_ID);
			filterField.put("roleCode" , CodeBook.EQUAL_HQL_ID);
			filterField.put("taskState" , CodeBook.EQUAL_HQL_ID);
		}
		return filterField;
	} 
	
	/**
	 * 生成主键编号
	 * @return
	 */
    @Transactional(propagation= Propagation.MANDATORY)
	public long getNextTaskId(){
		String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_ACTIONTASKNO");
		return Long.valueOf(sNo);
	}

    /**
     *  根据用户编码获取用户已办任务列表
     * @param filter
     * @param pageDesc
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<UserTask> listUserTaskFinJsonByFilter(Map<String,Object> filter, PageDesc pageDesc){
        String baseSQL = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
                "FLOW_OPT_TAG,NODE_INST_ID,UNITCODE,USER_CODE,ROLE_TYPE," +
                "ROLE_CODE,AUTHDESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
                "NODEOPTTYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
                "OPT_CODE,EXPIRE_OPT,STAGE_CODE,GRANTOR,LAST_UPDATE_USER," +
                "LAST_UPDATE_TIME,INST_STATE " +
                "from V_USER_TASK_LIST_FIN " +
                "where 1=1 " +
                "[ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
                "[ :userCode| and USER_CODE = :userCode] " +
                "[ :nodeCode| and NODE_CODE = :nodeCode] " +
                "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
                "[ :flowCode| and FLOW_CODE = :flowCode] " +
                "[ :stageCode| and STAGE_CODE = :stageCode] ";

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(baseSQL,filter);
        JSONArray dataList = DatabaseOptUtils.findObjectsAsJSONBySql(this,
                queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);
        List<UserTask> list = new ArrayList<>();
        if(dataList != null) {
            list = JSONObject.parseArray(dataList.toJSONString(),UserTask.class);
        }
        //List<UserTask>  userTasks = (List<UserTask>)DatabaseOptUtils.findObjectsBySql(this,queryAndNamedParams.getSql(),queryAndNamedParams.getParams(),pageDesc,UserTask.class);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<UserTask> listUserTaskFinByFilter(Map<String,Object> filter, PageDesc pageDesc){
        List<UserTask> userTask = listUserTaskFinJsonByFilter(filter,pageDesc);
        return userTask;
    }





	@SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
	public List<UserTask>  listUserTaskJsonByFilter(Map<String,Object> filter, PageDesc pageDesc){

        /*switch(DBType.mapDialectToDBType(DatabaseOptUtils.getDialectName())){
            case Oracle:

            case DB2:

            case SqlServer:

            case Access:

            case MySql:

            default:

        }*/

		String baseSQL = "select FLOW_INST_ID, FLOW_CODE,VERSION,FLOW_OPT_NAME," +
                "FLOW_OPT_TAG,NODE_INST_ID,UNITCODE,USER_CODE,ROLE_TYPE," +
                "ROLE_CODE,AUTHDESC,NODE_CODE,NODE_NAME,NODE_TYPE," +
                "NODEOPTTYPE,OPT_PARAM,CREATE_TIME,PROMISE_TIME,TIME_LIMIT," +
                "OPT_CODE,EXPIRE_OPT,STAGE_CODE,GRANTOR,LAST_UPDATE_USER," +
                "LAST_UPDATE_TIME,INST_STATE " +
                "from V_USER_TASK_LIST " +
                "where 1=1 " +
                "[ :flowInstId| and FLOW_INST_ID = :flowInstId] " +
                "[ :userCode| and USER_CODE = :userCode] " +
                "[ :nodeCode| and NODE_CODE = :nodeCode] " +
                "[ :nodeInstId| and NODE_INST_ID = :nodeInstId] " +
                "[ :flowCode| and FLOW_CODE = :flowCode] " +
                "[ :stageCode| and STAGE_CODE = :stageCode] ";

        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(baseSQL,filter);
        JSONArray dataList = DatabaseOptUtils.findObjectsAsJSONBySql(this,
                queryAndNamedParams.getQuery(),queryAndNamedParams.getParams(),pageDesc);
        List<UserTask> list = new ArrayList<>();
        if(dataList != null) {
            list = JSONObject.parseArray(dataList.toJSONString(), UserTask.class);
        }
        //List<UserTask>  userTasks = JSONArray.t
        //List<UserTask>  userTasks = (List<UserTask>)DatabaseOptUtils.findObjectsBySql(this,queryAndNamedParams.getSql(),queryAndNamedParams.getParams(),pageDesc,UserTask.class);
        return list;
	}

    @SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<UserTask> listUserTaskByFilter(Map<String,Object> filter, PageDesc pageDesc){
        List<UserTask> userTask = listUserTaskJsonByFilter(filter,pageDesc);
        return userTask;
    }



    @Transactional(propagation= Propagation.MANDATORY)
	public List<ActionTask> getActionTaskByNodeidAndUser(long nodeInstId , String userCode){
		return this.listObjects("From ActionTask where nodeInstId=? and userCode=? and isValid='T'", new Object[]{nodeInstId,userCode});
	}
	
	@SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionTask> listActionTaskByNode(String userCode){
	   // String baseHQL = "from WfActionTask t join WfNodeInstance i on i.nodeinstid = t.nodeinstid where i.nodestate <> 'C' and t.usercode = ?";
	   //return this.listObjects(baseHQL,userCode);
	    String baseSQL = "SELECT * FROM WF_ACTION_TASK T JOIN FLOW_NODE_INSTANCE I ON I.NODE_INST_ID = T.NODE_INST_ID WHERE I.NODE_STATE <> 'C' AND T.USER_CODE = ?";
	    return (List<ActionTask>) DatabaseOptUtils.findObjectsBySql(this,baseSQL,new Object[]{userCode},ActionTask.class);
	}
	
	@SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public String getTaskGrantor(long nodeInstId ,String userCode){
        String baseSQL = "select GRANTOR  from V_USER_TASK_LIST where NODE_INST_ID = ? and USER_CODE =? ";
        List<String> utl = (List<String>)
                DatabaseOptUtils.findObjectsBySql(this,baseSQL,new Object[]{nodeInstId,userCode});
        if(utl==null || utl.size()==0)
            return null;
        /**
         * 优先已自己的身份执行
         */
        String grantor = userCode;
        for(String task : utl){
            if(StringUtils.isBlank(task))
                 return userCode;
            else
                grantor = task;
        }            
        return grantor;
    }
	
	@SuppressWarnings("unchecked")
    @Transactional(propagation= Propagation.MANDATORY)
    public boolean hasOptPower(long nodeInstId ,String userCode,String grantorCode){

        String baseSQL = "select GRANTOR  from V_USER_TASK_LIST where NODE_INST_ID = ? and USER_CODE =? ";
        List<String> utl = (List<String>)
                DatabaseOptUtils.findObjectsBySql(this,baseSQL,new Object[]{nodeInstId,userCode});
        if(utl==null || utl.size()==0)
            return false;
        /**
         * 优先已自己的身份执行
         */
        if(grantorCode !=null && ! grantorCode.equals(userCode)){
            for(String task : utl){
                if(grantorCode.equals(task))
                    return true;

            }
            return false;
        }
        return true;
    }
    	
}
