package com.centit.workflow.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.workflow.po.NodeInstance;
@Repository
public class NodeInstanceDao extends BaseDaoImpl<NodeInstance,Long> {
  
	public Map<String, String> getFilterField() {
		if (filterField == null) {
			filterField = new HashMap<String, String>();
			filterField.put("nodeInstId", CodeBook.EQUAL_HQL_ID);
			filterField.put("flowInstId", CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeId", CodeBook.EQUAL_HQL_ID);
            filterField.put("(date)createTime" , "createTime like :createTime");
            filterField.put("(date)lastUpdateTime" , "lastUpdateTime like :createTime");
            filterField.put("lastUpdateUser", CodeBook.EQUAL_HQL_ID);
			filterField.put("startTime", CodeBook.EQUAL_HQL_ID);
			filterField.put("nodeState", CodeBook.EQUAL_HQL_ID);
			filterField.put("subFlowInstId", CodeBook.EQUAL_HQL_ID);
			filterField.put("unitCode", CodeBook.EQUAL_HQL_ID);
			filterField.put("transId", CodeBook.EQUAL_HQL_ID);
			filterField.put("runToken", CodeBook.EQUAL_HQL_ID);
            filterField.put("taskAssigned", CodeBook.EQUAL_HQL_ID);
            filterField.put("userCode", CodeBook.EQUAL_HQL_ID);
			
            filterField.put(CodeBook.ORDER_BY_HQL_ID, "nodeInstId desc");

		}
		return filterField;
	}

    @Transactional(propagation= Propagation.MANDATORY)
    public long getNextNodeInstId() {
		String sNo = DatabaseOptUtils.getNextValueOfSequence(this,"S_NODEINSTNO");
		return Long.valueOf(sNo);
	}

	/**
	 * 根据节点实例编号，更新当前节点运行状态，
	 * @param nodeinstid 节点实例编号
	 * @param state 状态代码  
    */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updtNodeInstState(long nodeinstid, String state) {
		NodeInstance nodeInst = this.getObjectById(nodeinstid);
		nodeInst.setNodeState(state);
		this.updateObject(nodeInst);
	}

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodeInstByState(long flowInstId, String nodeState) {
        return listObjects("From NodeInstance where nodeState= ? and flowInstId= ?",new Object[]{nodeState,flowInstId,});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodesWithoutOpt() {
        return listObjects("From NodeInstance where (nodeState='N' or nodeState='R') and  "
                + " nodeInstId not in (select cid.nodeInstId from VUserTaskList) and "
                + " flowInstId in (select flowInstId from FlowInstance where instState='N' )order by nodeInstId desc");
    }
    //expireOptSign == 0未处理  1 已通知  ,2..6 已通知2..5次（暂时不启动重复通知） 6:不处理    7：已挂起  8 已终止 9 已完成
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNearExpireNodeInstance(long leaveTime) {
        return  this.listObjects("From NodeInstance" +
                " where timeLimit <= ? and nodeState='N' and (isTimer='T' or isTimer='R')",leaveTime);
    }
    

    /**
     * 查询最后更改的节点
     * @param userCode
     * @param state
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listLastUpdateNodeInst(String userCode, String state){
        return  this.listObjects("From NodeInstance" +
                " where lastUpdateUser = ? and nodeState = ? order by lastUpdateTime ",new Object[]{userCode,state});
    }
    
    /**
     * 查询某人操作计时的节点
     * @param userCode
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listNodeInstByTimer(String userCode, String isTimer, PageDesc pageDesc){
        return  this.listObjects("From NodeInstance" +
                " where lastUpdateUser = ? and isTimer = ? order by lastUpdateTime ",
                new Object[]{userCode,isTimer},pageDesc);
    }
    
    /**
     * 更新节点实例时钟状态
     * @param instid 实例编号
     * @param isTimer 不计时N、计时T(有期限)、暂停P  忽略(无期限) F
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public void updateNodeTimerState(long instid,String isTimer,String mangerUserCode){
        NodeInstance nodeInst = this.getObjectById(instid);
        nodeInst.setIsTimer(isTimer);
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        this.updateObject(nodeInst);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listActiveTimerNodeByFlow(long flowInstId){
        return  this.listObjects("From NodeInstance" +
                " where flowInstId = ? and isTimer = 'T' ",
                new Object[]{flowInstId});
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<NodeInstance> listActiveTimerNodeByFlowStage(long flowInstId, String flowStage){
        return  this.listObjects("From NodeInstance" +
                " where flowInstId = ? and flowStage = ? and isTimer = 'T'",
                new Object[]{flowInstId,flowStage});
    }
}
