package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * 流程管理业务实现类
 *
 * @author codefan@sina.com
 * @version 2.0
 */
@Service
@Transactional
public class FlowManagerImpl implements FlowManager, Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(FlowManagerImpl.class);

    @Autowired
    FlowInstanceDao flowInstanceDao;

    @Autowired
    NodeInstanceDao nodeInstanceDao;

    @Autowired
    NodeInfoDao flowNodeDao;

    @Autowired
    FlowTransitionDao flowTransitionDao;

    @Autowired
    ActionTaskDao actionTaskDao;

    @Autowired
    FlowInfoDao flowDefDao;

    @Autowired
    StageInstanceDao stageInstanceDao;

    @Autowired
    RoleRelegateDao flowRoleRelegateDao;

    @Autowired
    FlowEngine flowEngine;

    @Autowired
    FlowInstanceGroupDao flowInstanceGroupDao;

    @Autowired
    private FlowOrganizeDao flowOrganizeDao;

    @Autowired
    private FlowWorkTeamDao flowTeamDao;

    /*@Autowired
    private NotificationCenter notificationCenter;*/

    @Autowired(required = false)
    private OperationLogWriter optLogManager;

    /**
     * 查看工作流程实例状态或进度
     */
    @Override
    public Map<String, Object> viewFlowInstance(String flowInstId) {
        FlowInstance wfInst = flowInstanceDao.getObjectWithReferences(flowInstId);
        FlowInfoId id = new FlowInfoId(wfInst
            .getVersion(), wfInst.getFlowCode());
        FlowInfo wfDef = flowDefDao.getObjectWithReferences(id);
        Map<String, String> nodeState = new HashMap<>();
        Map<String, Integer> nodeInstCount = new HashMap<>();
        Map<String, NodeInfo> nodeMap = new HashMap<>();
        Map<String, String> transState = new HashMap<>();
        Map<String, FlowTransition> transMap = new HashMap<>();
        List<NodeInfo> nodeSet = wfDef.getNodeList();
        Boolean findTran = true;
        String benginNodeId = "";
        String endNodeID = "";
        for (NodeInfo node : nodeSet) {
            nodeState.put(node.getNodeId(), "ready");
            if (node.getNodeType().equals(NodeInfo.NODE_TYPE_START)) {
                nodeState.put(node.getNodeId(), "complete");
                benginNodeId = node.getNodeId();
            } else if (node.getNodeType().equals(NodeInfo.NODE_TYPE_END)) {
                endNodeID = node.getNodeId();
            }
            nodeInstCount.put(node.getNodeId(), 0);
            nodeMap.put(node.getNodeId(), node);
        }
        //System.out.println(benginNodeId);
        //flowDefDao.fetchObjectReferences(wfDef);
        List<FlowTransition> transSet = wfDef.getTransList();
        for (FlowTransition trans : transSet) {
            //首节点必然通过
            if (trans.getStartNodeId().equals(benginNodeId)) {
                transState.put(String.valueOf(trans.getTransId()), "1");
            } else
                transState.put(String.valueOf(trans.getTransId()), "-1");
            transMap.put(String.valueOf(trans.getTransId()), trans);
        }
        //flowInstanceDao.fetchObjectReferences(wfInst);
        List<NodeInstance> nodeInstSet = wfInst.getFlowNodeInstances();
        List<NodeInstance> completeNodeSet = new ArrayList<>();
        List<FlowTransition> completeTrans = new ArrayList<>();
        for (NodeInstance nodeInst : nodeInstSet) {
            if (nodeInst.getNodeState().equals("N")
                || nodeInst.getNodeState().equals("W")) {
                nodeState.put(nodeInst.getNodeId(), "waiting");
            } else {
                String ns = nodeState.get(nodeInst.getNodeId());
                if (nodeInst.getNodeState().equals("P") && !"waiting".equals(ns)) {
                    nodeState.put(nodeInst.getNodeId(), "suspend");
                }
                if ("ready".equals(ns)) {
                    if (nodeInst.getNodeState().equals("F")) {
                        nodeState.put(nodeInst.getNodeId(), "suspend");
                    } else if (nodeInst.getNodeState().equals("C")) {
                        if (nodeInst.getNodeId().equals(endNodeID)) {
                            findTran = false;
                        }
                        nodeState.put(nodeInst.getNodeId(), "complete");
                        completeNodeSet.add(nodeInst);
                    }
                }
            }
            Integer nc = nodeInstCount.get(nodeInst.getNodeId());
            nodeInstCount.put(nodeInst.getNodeId(), (nc == null) ? 1 : nc + 1);

            String transPath = nodeInst.getTransPath();
            if (transPath == null && nodeInstSet.size() > 1)
                continue;
            if (StringUtils.isNotBlank(transPath)) {
                String[] transs = transPath.split(",");
                for (String strTransId : transs) {
                    transState.put(strTransId, "1");
                    FlowTransition trans = transMap.get(strTransId);
                    completeTrans.add(trans);
                    if (trans != null) {
                        NodeInfo node = nodeMap.get(trans.getStartNodeId());
                        if (node != null && NodeInfo.NODE_TYPE_ROUTE.equals(node.getNodeType())) {
                            nodeState.put(trans.getStartNodeId(), "complete");
                            //nc = nodeInstCount.get(trans.getStartNodeId());
                            //nodeInstCount.put(trans.getStartNodeId(), (nc==null)?1:nc+1);
                        }
                        node = nodeMap.get(trans.getEndNodeId());
                        if (node != null && NodeInfo.NODE_TYPE_ROUTE.equals(node.getNodeType())) {
                            nodeState.put(trans.getEndNodeId(), "complete");
                            nc = nodeInstCount.get(trans.getEndNodeId());
                            nodeInstCount.put(trans.getEndNodeId(), (nc == null) ? 1 : nc + 1);
                        }
                    }
                }
            }
            //由于没有办结节点生成，所以最后一条线需要寻找最后一个节点，根据线的startnodeid来判断
            //这里需要判断最后一个节点是路由节点的情况
            if (findTran) {
                //循环所有的线
                for (FlowTransition trans : transSet) {
                    if (nodeInst.getNodeId().equals(trans.getStartNodeId()) && trans.getEndNodeId().equals(endNodeID) && "C".equals(wfInst.getInstState())) {
                        //如果流程结束,那么最后一条线要自动画上
                        transState.put(String.valueOf(trans.getTransId()), "1");
                        findTran = false;
                        break;
                    }
                }
            }
        }
        //判断最后的节点和线是否已经找到
        if (findTran) {
            //如果办结节点之前的是路由节点，找到其节点、线集合
            List<Map<NodeInfo, FlowTransition>> finalTrans = new ArrayList<>();
            for (FlowTransition trans : transSet) {
                if (trans.getEndNodeId().equals(endNodeID)
                    && "C".equals(wfInst.getInstState())) {
                    //第一次循环找到办结节点前面的路由节点
                    //循环所有的节点定义
                    for (NodeInfo node : nodeSet) {
                        if (trans.getStartNodeId().equals(node.getNodeId())) {
                            //如果最后一个节点之前的节点是路由节点
                            if (NodeInfo.NODE_TYPE_ROUTE.equals(node.getNodeType())) {
                                Map<NodeInfo, FlowTransition> map = new HashMap<>();
                                map.put(node, trans);
                                finalTrans.add(map);
                                break;
                            }
                        }
                    }
                }
            }
            //找到路由节点之后，寻找路由节点的上一条线
            if (!finalTrans.isEmpty()) {
                loop:
                //循环路由节点和线
                for (Map<NodeInfo, FlowTransition> map : finalTrans) {
                    //循环节点node
                    for (NodeInfo no : map.keySet()) {
                        //循环已办结的节点
                        for (NodeInstance ni : completeNodeSet) {
                            //判断已办结节点和路由节点的关系,找到已办结节点和路由的连接线，确定哪一个路由是真正完成的
                            for (FlowTransition trans : transSet) {
                                //如果连接线的开始等于已完成节点，结束等于路由节点，判断这个连接线完成，路由节点完成
                                if (trans.getStartNodeId().equals(ni.getNodeId())
                                    && trans.getEndNodeId().equals(no.getNodeId())) {
                                    //如果有已完成的线，查找一下已完成的所有线的开始节点是否有跟我们上面查出来的有重复
                                    //有重复的话说明矛盾，并不是我们要的结果
                                    if (!completeTrans.isEmpty()) {
                                        Boolean trueTrans = true;
                                        for (FlowTransition tr : completeTrans) {
                                            //添加结束节点的判断，如果结束节点不重复
                                            //那么说明并不冲突矛盾，因为存在同一节点路由经过不同流转得情况
                                            //问题在于结束节点没有准确找到，可以根据节点次数来判断
                                            if (trans.getStartNodeId().equals(tr.getStartNodeId())
                                                && (!trans.getEndNodeId().equals(tr.getEndNodeId())
                                                || trans.getEndNodeId().equals(endNodeID))) {
                                                trueTrans = false;
                                            }
                                        }
                                        if (trueTrans) {
                                            transState.put(String.valueOf(trans.getTransId()), "1");
                                            transState.put(String.valueOf(map.get(no).getTransId()), "1");
                                            nodeState.put(no.getNodeId(), "complete");
                                            findTran = false;
                                            break loop;
                                        }
                                    } else {
                                        //如果没有已完成的线，那么找到的这个线就是需要标记完成的线
                                        transState.put(String.valueOf(trans.getTransId()), "1");
                                        transState.put(String.valueOf(map.get(no).getTransId()), "1");
                                        nodeState.put(no.getNodeId(), "complete");
                                        findTran = false;
                                        break loop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Map.Entry<String, String> ns : nodeState.entrySet()) {
            nodes.add(CollectionsOpt.createHashMap("id", ns.getKey(),
                "inststate", ns.getValue(), "instcount", nodeInstCount.get(ns.getKey())));
        }
        List<Map<String, Object>> steps = new ArrayList<>();
        for (Map.Entry<String, String> ts : transState.entrySet()) {
            steps.add(CollectionsOpt.createHashMap("id", ts.getKey(),
                "inststate", ts.getValue()));
        }
        return CollectionsOpt.createHashMap("nodes", nodes, "steps", steps);
    }


    @Override
    public String viewFlowNodeInstance(String flowInstId) {
        FlowInstance wfInst = flowInstanceDao.getObjectById(flowInstId);
        Document viewDoc = DocumentHelper.createDocument();
        Element baseEle = viewDoc.addElement("flow");
        Element procsEle = baseEle.addElement("nodes");

        List<NodeInstance> nodeInstSet = wfInst.getFlowNodeInstances();
        Iterator<NodeInstance> nodeInstIter = nodeInstSet.iterator();
        // 根据节点实例状态刷新流程的节点状态
        while (nodeInstIter.hasNext()) {
            NodeInstance wfInstNodes = nodeInstIter.next();
            Element procEle = procsEle.addElement("node");
            procEle.addAttribute("id", String.valueOf(wfInstNodes.getNodeInstId()));
            procEle.addAttribute("pid", String.valueOf(wfInstNodes.getPrevNodeInstId()));
            procEle.addAttribute("nodeid", String.valueOf(wfInstNodes.getNodeId()));
            procEle.addAttribute("nodeCode", wfInstNodes.getNodeCode());
            procEle.addAttribute("token", wfInstNodes.getRunToken());
            procEle.addAttribute("nodeName", wfInstNodes.getNodeName());
            procEle.addAttribute("nodeState", wfInstNodes.getNodeState());

            procEle.addAttribute("createTime",
                DatetimeOpt.convertDatetimeToString(wfInstNodes.getCreateTime()));
            procEle.addAttribute("lastUpdateTime",
                DatetimeOpt.convertDatetimeToString(wfInstNodes.getLastUpdateTime()));
            procEle.addAttribute("lastUpdateUser", wfInstNodes.getLastUpdateUser());
        }

        return viewDoc.asXML();
    }

    /**
     * 根据节点ID查询能够操作该节点的所有人员，如果为空，则需要分配工作任务单
     */
    @Override
    public List<UserTask> listNodeTasks(String nodeInstId) {
        List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
            QueryUtils.createSqlParamsMap("nodeInstId", nodeInstId), new PageDesc(-1, -1));
        if (taskList == null)
            return null;
        List<UserTask> tempList = new ArrayList<UserTask>();
        tempList.addAll(taskList);
        return tempList;
    }


    /**
     * 更新流程实例状态，同时需更新所有节点实例状态
     *
     * @param instid   实例编号
     * @param state    N 正常/  C 完成/ P 暂停  挂起  / F 因为流程强行结束二结束 /
     * @param userCode 操作用户
     */
    private int updateInstanceState(String instid, String state, String userCode,
                                    String admindesc) {

        FlowInstance wfFlowInst = flowInstanceDao.getObjectWithReferences(instid);
        if (wfFlowInst == null)
            return 0;
        // wfFlowInst.setInststate(state);
        Date updateTime = DatetimeOpt.currentUtilDate();
        wfFlowInst.setLastUpdateTime(updateTime);
        wfFlowInst.setLastUpdateUser(userCode);

        String actionDesc = "U";
        if ("P".equals(state) && "N".equals(wfFlowInst.getInstState())) {
            wfFlowInst.setInstState(state);
            actionDesc = "挂起流程；";
        }
        // 只能结束未完成的流程
        else if ("F".equals(state) && !"C".equals(wfFlowInst.getInstState())
            && !"F".equals(wfFlowInst.getInstState()))
            actionDesc = "强制结束流程；";
            // 只能挂起正常的流程
        else if ("N".equals(state)
            && "P".equals(wfFlowInst.getInstState())) {
            actionDesc = "换新流程；";
        }
        // 不正确的操作
        if ("U".equals(actionDesc))
            return -1;

        // 更新流程实例状态
        wfFlowInst.setInstState(state);
        if ("N".equals(state)) {
            for (NodeInstance nodeInst : wfFlowInst.getFlowNodeInstances()) {
                if ("P".equals(nodeInst.getNodeState()) || "F".equals(nodeInst.getNodeState())) {
                    nodeInst.setNodeState(state);
                    nodeInst.setLastUpdateTime(updateTime);
                    nodeInst.setLastUpdateUser(userCode);
                    nodeInstanceDao.updateObject(nodeInst);
                }
            }
        } else if ("P".equals(state)) { // 更新挂起的节点
            for (NodeInstance nodeInst : wfFlowInst.getFlowNodeInstances()) {
                if ("N".equals(nodeInst.getNodeState()) || "S".equals(nodeInst.getNodeState())) {
                    nodeInst.setNodeState(state);
                    nodeInst.setLastUpdateTime(updateTime);
                    nodeInst.setLastUpdateUser(userCode);
                    nodeInstanceDao.updateObject(nodeInst);
                }
            }
        }
        flowInstanceDao.updateObject(wfFlowInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            userCode, instid, "更改流程状态为" + state + ";" + admindesc);
        managerAct.setNewValue(actionDesc + admindesc);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    public long updateNodeInstState(String nodeInstId, String newState,
                                    String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return 0;
        }

        // 设置最后更新时间和更新人
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        /*
         * N 正常  B 已回退    C 完成   F被强制结束
         * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
         */
        String actionDesc = "U";
        // 超时挂起
        if ("P".equals(newState) && "N".equals(nodeInst.getNodeState())) {
            actionDesc = "挂起节点。";
        }
        // 只能结束未完成的节点
        else if ("F".equals(newState) && !"C".equals(nodeInst.getNodeState())
            && !"F".equals(nodeInst.getNodeState()))
            actionDesc = "强制结束节点。";
            // 只能唤醒挂起、超时挂起、失效的节点
        else if ("N".equals(newState)
            && ("S".equals(nodeInst.getNodeState())
            || "P".equals(nodeInst.getNodeState())))
            actionDesc = "唤醒节点；";
        // 不正确的操作
        if ("U".equals(actionDesc))
            return -1;
        nodeInst.setNodeState(newState);
        nodeInstanceDao.updateObject(nodeInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst,
            "换线流程节点；" + actionDesc, null);
        OperationLogCenter.log(managerAct);
        return 1;
    }


    public long suspendNodeInstance(String nodeInstId, String mangerUserCode) {
        // FlowOptUtils.sendMsg(nodeInstId, null, mangerUserCode);
        return updateNodeInstState(nodeInstId, "P", mangerUserCode);
    }

    public long activizeNodeInstance(String nodeInstId, String mangerUserCode) {
        return updateNodeInstState(nodeInstId, "N", mangerUserCode);
    }

    /**
     * 强制修改流程的节点状态
     *
     * @param nodeInstId
     * @param newState
     */
    @Override
    public void updateNodeState(String nodeInstId, String newState) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return;
        }
        // 设置最后更新时间
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        nodeInst.setNodeState(newState);
        nodeInstanceDao.updateObject(nodeInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            "admin", nodeInst,
            "强制修改流程的节点状态为" + newState + "；", null);
        OperationLogCenter.log(managerAct);
    }

    /**
     * 设置流程期限
     *
     * @param nodeInstId     流程节点实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @return
     */
    public long resetNodeTimelimt(String nodeInstId, String timeLimit,
                                  String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return 0;
        }

        nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
        // 设置最后更新时间和更新人
        nodeInstanceDao.updateObject(nodeInst);
        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst,
            "重置节点期限：" + new WorkTimeSpan(timeLimit).getTimeSpanDesc(), null);
        OperationLogCenter.log(managerAct);

        return 1;
    }


    public FlowInstance getFlowInstance(String flowInstId) {
        FlowInstance flowInstance = flowInstanceDao.getObjectWithReferences(flowInstId);
        /**
         * 初始化节点信息
         */
        if (flowInstance != null) {
            initNodeInstances(flowInstance);
        }
        return flowInstance;
    }

    /**
     * 初始化节点信息
     *
     * @param flowInstance
     */
    public void initNodeInstances(FlowInstance flowInstance) {
        if (flowInstance.getFlowNodeInstances() != null) {
            for (NodeInstance nodeInstance : flowInstance.getFlowNodeInstances()) {
                NodeInfo node = flowNodeDao.getObjectById(nodeInstance.getNodeId());
                if (node != null) {
                    nodeInstance.setNodeName(node.getNodeName());
                }
                nodeInstance.setFlowOptName(flowInstance.getFlowOptName());
                nodeInstance.setFlowOptTag(flowInstance.getFlowOptTag());
            }
        }
    }

    /**
     * 通过flowOptTag获取流程实例
     * @param flowOptTag
     * @return
     */
 /*   @Override
    public FlowInstance getFlowInstance(String flowOptTag) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("flowOptTag",flowOptTag);
        FlowInstance flowInstance = flowInstanceDao.getObjectByProperties(properties);
        */

    /**
     * 初始化节点信息
     *//*
        initNodeInstances(flowInstance);
        return flowInstance;
    }*/
    @Override
    public List<FlowInstance> listUserAttachFlowInstance(String userCode,
                                                         String flowPhase, Map<String, Object> filterMap, PageDesc pageDesc) {
        List<FlowInstance> instList = flowInstanceDao
            .listUserAttachFlowInstance(userCode, flowPhase, filterMap,
                pageDesc);

        for (FlowInstance flowInst : instList) {
            StringBuffer curStep = new StringBuffer(1);
            List<NodeInstance> activeNodeList = new ArrayList<NodeInstance>();
            for (NodeInstance nodeInst : flowInst.getFlowNodeInstances()) {

                if ("N".equals(nodeInst.getNodeState())
                    || "W".equals(nodeInst.getNodeState())) {
                    NodeInfo node = flowNodeDao.getObjectById(nodeInst
                        .getNodeId());
                    nodeInst.setNodeName(node.getNodeName());
                    activeNodeList.add(nodeInst);
                    curStep.append(node.getNodeName());
                    curStep.append(',');
                }
            }
            if (curStep.length() > 1) {
                flowInst.setCurStep(curStep.deleteCharAt(curStep.length() - 1)
                    .toString());
            }
            flowInst.setActiveNodeList(activeNodeList);
        }

        return new ArrayList<FlowInstance>(this.convertor(instList));
    }

    /**
     * 转换流程名称等
     *
     * @param instList
     * @return
     */
    private List<FlowInstance> convertor(List<FlowInstance> instList) {
        //TO： 刘建洋这个为什么不用一个视图，后面可以更新这个效率太低。
        if (instList == null)
            return null;
        for (FlowInstance flowInst : instList) {
            FlowInfo flowDef = flowDefDao.getFlowDefineByID(
                flowInst.getFlowCode(), flowInst.getVersion());
            if (flowDef != null)
                flowInst.setFlowName(flowDef.getFlowName());
        }
        return instList;
    }

    @Override
    public JSONArray listFlowInstance(Map<String, Object> filterMap,
                                      PageDesc pageDesc) {
        return flowInstanceDao.listObjectsAsJson(filterMap,
            pageDesc);
    }


    /**
     * 更新流程实例状态，同时需更新所有节点实例状态
     *
     * @param flowInstId     实例编号
     * @param mangerUserCode 操作用户
     */
    @Override
    public int stopInstance(String flowInstId, String mangerUserCode,
                            String admindesc) {

        FlowInstance wfFlowInst = flowEngine.getFlowInstById(flowInstId);
        if (wfFlowInst == null)
            return 0;

        // 只能结束未完成的流程
        if ("C".equals(wfFlowInst.getInstState()) || "F".equals(wfFlowInst.getInstState()))
            return -1;

        Date updateTime = DatetimeOpt.currentUtilDate();
        wfFlowInst.setLastUpdateTime(updateTime);
        wfFlowInst.setLastUpdateUser(mangerUserCode);
        wfFlowInst.setFlowInstId(flowInstId);

        // 更新流程实例状态
        wfFlowInst.setInstState("F");
        flowInstanceDao.updtFlowInstInfo(wfFlowInst);
        for (NodeInstance nodeInst : wfFlowInst.getFlowNodeInstances()) {
            if ("N".equals(nodeInst.getNodeState()) || "P".equals(nodeInst.getNodeState()) || "S".equals(nodeInst.getNodeState())) {
                nodeInst.setNodeState("F");
                nodeInst.setLastUpdateTime(updateTime);
                nodeInst.setLastUpdateUser(mangerUserCode);
                nodeInstanceDao.updateObject(nodeInst);
            }
        }

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "强制结束流程;" + admindesc);
        OperationLogCenter.log(managerAct);

        return 1;
    }

    /**
     * 挂起一个流程，同时将其所有的活动节点也挂起
     */
    @Override
    public int suspendInstance(String flowInstId, String mangerUserCode,
                               String admindesc) {
        return updateInstanceState(flowInstId, "P", mangerUserCode, admindesc);
    }

    /**
     * 激活一个 挂起的或者无效的流程
     */
    @Override
    public int activizeInstance(String flowInstId, String mangerUserCode,
                                String admindesc) {
        return updateInstanceState(flowInstId, "N", mangerUserCode, admindesc);
    }

    @Override
    public void updateFlowInstUnit(String flowInstId, String unitCode, String optUserCode) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setUnitCode(unitCode);
        flowInstanceDao.updateObject(flowInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            optUserCode, flowInstId, "修改流程机构代码：" + unitCode);
        //managerAct.setNodeInstId(nodeinstid);
        OperationLogCenter.log(managerAct);
    }

    @Override
    public void updateNodeInstUnit(String nodeInstId, String unitCode, String optUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return;
        nodeInst.setUnitCode(unitCode);
        nodeInstanceDao.updateObject(nodeInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            optUserCode, nodeInst, "修改节点：" + nodeInstId + "机构代码：" + unitCode, null);
        OperationLogCenter.log(managerAct);
    }

    /**
     * 更改节点的角色信息
     */
    @Override
    public void updateNodeRoleInfo(String nodeInstId, String roleType, String roleCode, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return;
        nodeInst.setRoleType(roleType);
        nodeInst.setRoleCode(roleCode);
        nodeInstanceDao.updateObject(nodeInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst,
            "修改节点：" + nodeInstId + "角色代码为：" + roleType + ":" + roleCode + "。", null);

        OperationLogCenter.log(managerAct);
    }


    /**
     * 设置流程期限
     *
     * @param flowInstId     流程实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @param admindesc      流程期限更改原因说明
     * @return
     */
    public long resetFlowTimelimt(String flowInstId, String timeLimit,
                                  String mangerUserCode, String admindesc) {
        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(flowInstId);
        if (wfFlowInst == null)
            return 0;

        wfFlowInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        wfFlowInst.setLastUpdateUser(mangerUserCode);
        wfFlowInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());

        flowInstanceDao.updateObject(wfFlowInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId,
            "更改流程期限：" +
                new WorkTimeSpan(timeLimit).getTimeSpanDesc() + ";" + admindesc);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     */
    @Override
    public NodeInstance resetFlowToThisNode(String nodeInstId, String mangerUserCode) {

        NodeInstance thisnode = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (thisnode == null)
            return null;
//            return -1;//d大小于

        FlowInstance flow = flowInstanceDao.getObjectWithReferences(thisnode
            .getFlowInstId());
        if (flow == null)
            return null;
//            return -2;//d大小于
        // 当前节点状态必需不能为正常，如果是正常则没有必要重置
        if ("N".equals(thisnode.getNodeState())) {
            //先将流程实例的状态变为正常
            flow.setInstState("N");
            flowInstanceDao.updateObject(flow);
            return null;
//            return -6;
        }
        NodeInfo nodedef = flowNodeDao.getObjectById(thisnode.getNodeId());
        if (NodeInfo.NODE_TYPE_START.equals(nodedef.getNodeType())
            || NodeInfo.NODE_TYPE_END.equals(nodedef.getNodeType())) {
            // 不能设定到开始或者结束节点
            return null;
//            return -5;//大小于
        }

        for (NodeInstance nodeInst : flow.getFlowNodeInstances()) {
            if (("N".equals(nodeInst.getNodeState())
                || "W".equals(nodeInst.getNodeState()))
                && (nodeInst.getNodeId().equals(thisnode.getNodeId()))
                && nodeInst.getRunToken().equals(thisnode.getRunToken())) {
                // 已经有一个正在运行的相同节点实例，不能重置到该节点
//                return -6;//大小于
                return null;

            }
        }

        flow.setInstState("N");
        //flow.setLastUpdateTime(new Date(System.currentTimeMillis()));
        //flow.setLastUpdateUser(mangerUserCode);
        // 将所有的下层正常节点都设置为 B 已回退
        String thisToken = thisnode.getRunToken();
        Date updateTime = DatetimeOpt.currentUtilDate();
        for (NodeInstance nodeInst : flow.getFlowNodeInstances()) {
            String currToken = nodeInst.getRunToken();
            if ("N,W".contains(nodeInst.getNodeState())
                && currToken != null
                && thisToken != null
                && (currToken.equals(thisToken)
                || currToken.startsWith(thisToken + '.')
                || thisToken.startsWith(currToken + '.')
                || currToken.startsWith("R" + thisToken + '.')
            )) {

                if ("W".equals(nodeInst.getNodeState())) { // 结束子流程
                    FlowInstance subFlowInst = flowInstanceDao
                        .getObjectById(nodeInst.getSubFlowInstId());
                    if (subFlowInst != null) {
                        FlowOptUtils.endInstance(subFlowInst, "F",
                            mangerUserCode, flowInstanceDao);
                        subFlowInst.setLastUpdateUser(mangerUserCode);
                        flowInstanceDao.updateObject(subFlowInst);
                        //更新子流程下的所有消息为已办
                        //FlowOptUtils.sendFinishMsg(subFlowInst.getFlowInstId(), mangerUserCode);
                    }
                }
                nodeInst.setNodeState("B");
                // 设置最后更新时间和更新人
                nodeInst.setLastUpdateUser(mangerUserCode);
                nodeInst.setLastUpdateTime(updateTime);
                nodeInstanceDao.updateObject(nodeInst);
                //更新消息为已办
                // FlowOptUtils.sendMsg(nodeInst.getNodeInstId(), null, mangerUserCode);
            }
        }
        // 创建新节点
        String lastNodeInstId = UuidOpt.getUuidAsString32();
//        String lastNodeInstId = nodeInstanceDao.getNextNodeInstId();
        NodeInstance nextNodeInst = flow.newNodeInstance();
        nextNodeInst.copyNotNullProperty(thisnode);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setNodeState("N");
        nextNodeInst.setTaskAssigned(thisnode.getTaskAssigned());
        nextNodeInst.setLastUpdateUser(mangerUserCode);
        nextNodeInst.setLastUpdateTime(updateTime);

        for (ActionTask task : thisnode.getWfActionTasks()) {
            nextNodeInst.setTaskAssigned("T");
            ActionTask newtask = FlowOptUtils.createActionTask(
                nextNodeInst.getNodeInstId(), task.getUserCode());
            //newtask.setTaskId(actionTaskDao.getNextTaskId());
            // 要判断 过期时间的问题
            nextNodeInst.addWfActionTask(newtask);
            actionTaskDao.saveNewObject(newtask);

        }

        flow.addNodeInstance(nextNodeInst);
        flowInstanceDao.updateObject(flow);
        nodeInstanceDao.saveNewObject(nextNodeInst);
        //FlowOptUtils.sendMsg("", nextNodeInsts, mangerUserCode);
        //执行节点创建后 事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory
            .createNodeEventSupportBean(nodedef, flowEngine);
        nodeEventExecutor.runAfterCreate(flow, nextNodeInst, nodedef, mangerUserCode);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nextNodeInst, "重新运行节点：" + nodeInstId, nodedef);
        OperationLogCenter.log(managerAct);
        return nextNodeInst;
    }


    @Override
    public String forceDissociateRuning(String nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null || (!"N".equals(nodeInst.getNodeState())))
            return null;//大小于
//            return -1;
        FlowInstance flowinst = flowInstanceDao.getObjectWithReferences(nodeInst
            .getFlowInstId());
        if (flowinst == null)
            return null;//大小于
//            return -2;
        int otherRunNode = 0;
        for (NodeInstance tNode : flowinst.getFlowNodeInstances()) {
            if (!tNode.getNodeInstId().equals(nodeInstId)
                && "N".equals(tNode.getNodeState()))
                otherRunNode++;
        }
        if (otherRunNode == 0)
            return null;//大小于
//            return -3;

        nodeInst.setRunToken("R" + nodeInst.getRunToken());
        // 设置最后更新时间和更新人
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        nodeInstanceDao.updateObject(nodeInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst, "强制节点分支为游离分支:" + nodeInstId, null);
        OperationLogCenter.log(managerAct);
        return nodeInstId;
    }

    /**
     * 目前只能提交直接向下流转的节点，暂不支持分支节点，也不支持下一个节点是汇聚类型的流转,机构自动继承上个节点的机构 强行流转无法自动运行
     * 机构和权限表达式，所以新的节点可能没有人能够对其进行操作，必需要配合任务管理来指定到具体的人
     */
    @Override
    public String forceCommit(String nodeInstId, String mangerUserCode) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return null;//大小于
//            return -1;
        Date updateTime = DatetimeOpt.currentUtilDate();
        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(nodeInst
            .getNodeId());
        if (transList == null || transList.size() == 0) {
            //if(nodeInst.getRunToken().startsWith("R")){
            //log.info("游离节点:" + nodeInstId);
            //将节点的状态设置为已完成
            nodeInst.setLastUpdateTime(updateTime);
            nodeInst.setLastUpdateUser(mangerUserCode);
            //创建节点提交日志 S:提交节点
            OperationLog wfactlog = FlowOptUtils.createActionLog(
                mangerUserCode, nodeInst, "强制提交节点:" + nodeInstId, null);
            //wfactlog.setActionId(actionLogDao.getNextActionId());
            wfactlog.time(updateTime);
            OperationLogCenter.log(wfactlog);

            nodeInst.setNodeState("F");
            nodeInstanceDao.updateObject(nodeInst);
            return nodeInst.getRunToken().startsWith("R") ? "" : null;
        }

        if (transList.size() != 1) {
            return null;//大小于
        }

        FlowTransition trans = transList.get(0);
        String nextCode = transList.get(0).getEndNodeId();
        NodeInfo nextNode = flowNodeDao.getObjectById(nextCode);
        if (nextNode == null) {
            return null;//大小于
        }

        if (!NodeInfo.NODE_TYPE_OPT.equals(nextNode.getNodeType())
            && !NodeInfo.NODE_TYPE_FIRST.equals(nextNode.getNodeType())) {
            return null;//大小于
        }

        Date commitTime = DatetimeOpt.currentUtilDate();
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst
            .getFlowInstId());
        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(
            flowInst.getFlowCode(), flowInst.getVersion());

        nodeInst.setNodeState("F");
        // 设置最后更新时间和更新人
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(commitTime);

//        long nextNodeInstId = nodeInstanceDao.getNextNodeInstId();
        String nextNodeInstId = UuidOpt.getUuidAsString32();
        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(
            nodeInst.getUnitCode(), mangerUserCode, flowInst, nodeInst, flowInfo,
            nextNode, trans);

        nextNodeInst.setNodeInstId(nextNodeInstId);
//        nextNodeInst.setPrevNodeInstId(nodeInst.getNodeId());?
        nextNodeInst.setPrevNodeInstId(nodeInst.getNodeInstId());
        nextNodeInst.setRunToken(nodeInst.getRunToken());// 添加令牌算法
        nextNodeInst.setTransPath(String.valueOf(trans.getTransId()));

        // 能计算出下一步操作人时 使用下一步操作人
        Set<String> nextUsers = flowEngine.viewNextNodeOperator(nextCode,
            SubmitOptOptions.create().user(nodeInst.getUserCode()).unit(nodeInst.getUnitCode()).nodeInst(nodeInstId));
        if (null != nextUsers && !nextUsers.isEmpty()) {
            nextNodeInst.setTaskAssigned("T");
            nextUsers.forEach(nextUser -> {
                ActionTask wfactTask = FlowOptUtils.createActionTask(nextNodeInst.getNodeInstId(), nextUser);
                wfactTask.setAssignTime(commitTime);
                actionTaskDao.saveNewObject(wfactTask);
                nextNodeInst.addWfActionTask(wfactTask);
            });
        }

        //创建节点提交日志 S:提交节点
        nodeInstanceDao.updateObject(nodeInst);
        nodeInstanceDao.saveNewObject(nextNodeInst);
        flowInst.setLastUpdateUser(mangerUserCode);
        flowInst.setLastUpdateTime(commitTime);
        flowInstanceDao.updateObject(flowInst);

        OperationLog wfactlog = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst, "强制提交节点:" + nodeInstId, null);
        wfactlog.time(updateTime);
        OperationLogCenter.log(wfactlog);
        return nextNodeInstId;
    }

    /**
     * 这个功能会可能会有严重的性能问题，建议后期用视图修改。
     */
    @Override
    public List<NodeInstance> listNodesWithoutOpt() {
        List<NodeInstance> tempList = nodeInstanceDao.listNodesWithoutOpt();
        if (tempList != null) {
            for (NodeInstance nodeInst : tempList) {
                FlowInstance flowInst = flowInstanceDao
                    .getObjectById(nodeInst.getFlowInstId());
                nodeInst.setFlowOptName(flowInst.getFlowOptName());
                nodeInst.setFlowOptTag(flowInst.getFlowOptTag());

                NodeInfo wfNode = flowNodeDao.getObjectById(nodeInst.getNodeId());
                nodeInst.setNodeName(wfNode.getNodeName());
            }

            return new ArrayList<>(tempList);
        }
        return new ArrayList<>();

    }

    @Override
    public List<NodeInstance> listFlowActiveNodes(String wfinstid) {
        return nodeInstanceDao.listNodeInstByState(wfinstid, "N");
    }

    @Override
    public List<NodeInstance> listFlowInstNodes(String wfinstid) {
        List<NodeInstance> nodeInstList = new ArrayList<>();
        FlowInstance flowInst = flowInstanceDao.getObjectWithReferences(wfinstid);
        List<NodeInstance> nodeInstsSet = flowInst.getFlowNodeInstances();
        for (NodeInstance nodeInst : nodeInstsSet) {
            NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
            if (node == null)
                continue;
            // 不显示 自动执行的节点 和 同步节点 codefan@sina.com 2012-7-8
            /*if (NodeInfo.NODE_TYPE_AUTO.equals(node.getNodeType())
                || NodeInfo.NODE_TYPE_SYNC.equals(node.getNodeType()))
                continue;*/

            nodeInst.setNodeName(node.getNodeName());
            if (nodeInst.checkIsNotCompleted() && NodeInfo.NODE_TYPE_OPT.equals(node.getNodeType())) {
                List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
                    QueryUtils.createSqlParamsMap("nodeInstId", nodeInst.getNodeInstId()), new PageDesc(-1, -1));

                List<String> trainsUsers = new ArrayList<>();
                String optUrl = null;
                for (UserTask userTask : taskList) {
                    trainsUsers.add(userTask.getUserCode());
                    optUrl = userTask.getOptUrl();
                }
                nodeInst.setTrainsUsers(trainsUsers);
                nodeInst.setOptUrl(optUrl);
            }
            nodeInstList.add(nodeInst);
        }
        return new ArrayList<>(nodeInstList);
    }

    public List<ActionTask> listNodeActionTasks(String nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (null == nodeInst)
            nodeInst = new NodeInstance();
        return new ArrayList<>(nodeInst.getWfActionTasks());
    }

    public void deleteNodeActionTasks(String nodeInstId, String flowInstId, String mangerUserCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("nodeInstId", nodeInstId);
        actionTaskDao.deleteObjectsByProperties(map);

        // 删除节点所有任务时，将节点实例taskAssigned改为T
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if ("S".equals(nodeInst.getTaskAssigned())) {
            nodeInst.setTaskAssigned("T");
            nodeInstanceDao.updateObject(nodeInst);
        }

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "删除节点所有任务:"
                + nodeInstId);
        managerAct.method(nodeInstId);
        OperationLogCenter.log(managerAct);

    }


    /**
     * isTimer T 计时、 F 不计时 H仅环节计时 、暂停P
     */
    public int suspendNodeInstTimer(String nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return -1;
        nodeInstanceDao.updateNodeTimerState(nodeInstId, "P", mangerUserCode);
        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst, "暂停节点计时:"
                + nodeInstId, null);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时F 、计时T(有期限)、暂停P H 仅环节计时 唤醒时要根据 wf_node 的计时类别进行修改 T 计时、有期限 F
     * 不计时 H仅环节计时
     */
    public int activizeNodeInstTimer(String nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return 0;
        NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
        nodeInstanceDao.updateNodeTimerState(nodeInstId,
            node.getIsAccountTime() /* T */, mangerUserCode);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, nodeInst, "恢复节点计时:" + nodeInstId, null);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时 F、计时T(有期限)、暂停P 忽略(无期限) F
     */
    public int suspendFlowInstTimer(String flowInstId, String mangerUserCode) {
        flowInstanceDao.updateFlowTimerState(flowInstId, "P", mangerUserCode);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "暂停流程计时: " + flowInstId);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时N、计时T(有期限)、暂停P 忽略(无期限) F
     */
    public int activizeFlowInstTimer(String flowInstId, String mangerUserCode) {
        flowInstanceDao.updateFlowTimerState(flowInstId, "T", mangerUserCode);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "恢复流程计时: " + flowInstId);
        OperationLogCenter.log(managerAct);
        return 1;
    }

    @Override
    public List<NodeInstance> listPauseTimerNodeInst(String userCode, PageDesc pageDesc) {
        List<NodeInstance> tempList = nodeInstanceDao.listNodeInstByTimer(
            userCode, "P", pageDesc);
        return new ArrayList<>(tempList);
    }

    @Override
    public List<FlowInstance> listPauseTimerFlowInst(String userCode, PageDesc pageDesc) {
        List<FlowInstance> tempList = flowInstanceDao.listFlowInstByTimer(
            userCode, "P", pageDesc);
        return new ArrayList<>(tempList);
    }

    @Override
    public List<StageInstance> listStageInstByFlowInstId(String flowInstId) {

        return new ArrayList<>(
            stageInstanceDao.listStageInstByFlowInstId(flowInstId)
        );
    }


    @Override
    public long resetStageTimelimt(String flowInstId, String stageId, String timeLimit,
                                   String mangerUserCode, String admindesc) {
        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(flowInstId);
        if (wfFlowInst == null)
            return 0;

        StageInstance stageInst = stageInstanceDao.getObject(flowInstId, stageId);

        stageInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        stageInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());

        flowInstanceDao.updateObject(wfFlowInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "更改阶段" + stageId + "期限：" +
                new WorkTimeSpan(timeLimit).getTimeSpanDesc() + ";" + admindesc);
        OperationLogCenter.log(managerAct);

        return 1;

    }

    @Override
    public int moveUserTaskTo(String fromUserCode, String toUserCode,
                              String optUserCode, String moveDesc) {
        /** TODO 这个地方 逻辑较乱估计肯定有问题
         *  1, 查出 所有的 fromUserCode 的待办， 可能在 wf_node_instance 也可能在 actiontask 表中
         *      但是不包括 委托的
         *  2, 每个任务 都需要在 actionlog 记录一条 任务转移的记录
         */
        //wf_node_instance 中的待办
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("taskAssigned", "S");
        filterMap.put("userCode", fromUserCode);
        filterMap.put("nodeState", "N");
        List<NodeInstance> nodeInstances = nodeInstanceDao.listObjects(filterMap);
        moveUserTaskTo(nodeInstances, fromUserCode, toUserCode, optUserCode);
        return 0;
    }

    private void moveUserTaskTo(List<NodeInstance> nodeInstIds, String fromUserCode, String toUserCode,
                                String optUserCode) {
        if (nodeInstIds != null && nodeInstIds.size() > 0) {
            for (NodeInstance nodeInstance : nodeInstIds) {
                //将userCode指向新用户
                if (nodeInstance.getTaskAssigned() == "S") {
                    nodeInstance.setUserCode(toUserCode);
                    nodeInstance.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                    nodeInstance.setLastUpdateUser(optUserCode);
                    nodeInstanceDao.updateObject(nodeInstance);
                    //日志
                    OperationLog wfactlog = FlowOptUtils.createActionLog(
                        optUserCode, nodeInstance, "任务从 " + fromUserCode + " 转移到" + toUserCode, null);
                    OperationLogCenter.log(wfactlog);
                } else {
                    Map<String, Object> filterMap = new HashMap<>();
                    filterMap.put("userCode", fromUserCode);
                    filterMap.put("nodeInstId", nodeInstance.getFlowInstId());
                    List<ActionTask> actionTasks = actionTaskDao.listObjects(filterMap);
                    if (actionTasks != null && actionTasks.size() > 0) {
                        for (ActionTask actionTask : actionTasks) {
                            //如果action中只有一条记录，那就写回到nodeinstance里面
                            OperationLog wfactlog = FlowOptUtils.createActionLog(
                                optUserCode, nodeInstance, "任务从 " + fromUserCode + " 转移到" + toUserCode, null);
                            OperationLogCenter.log(wfactlog);
                            actionTask.setUserCode(toUserCode);
                            //这儿应该还要设置新用户的 roleType 和roleCode，但是不知道怎么获取。
                            actionTaskDao.mergeObject(actionTask);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     *
     * @param nodeInstIds  任务节点结合
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param moveDesc     迁移描述
     * @param optUserCode  操作人员
     * @return 返回迁移的任务数
     */
    @Override
    public int moveUserTaskTo(List<String> nodeInstIds, String fromUserCode, String toUserCode,
                              String optUserCode, String moveDesc) {
        List<NodeInstance> nodeInstanceList = new ArrayList<NodeInstance>();
        for (String id : nodeInstIds) {
            NodeInstance nodeInstance = nodeInstanceDao.getObjectById(id);
            nodeInstanceList.add(nodeInstance);
        }
        moveUserTaskTo(nodeInstanceList, fromUserCode, toUserCode, optUserCode);
        return 0;
    }

    /**
     * 在任务列表中指定工作人员，这样就屏蔽了按照角色自动查找符合权限的人员
     */
    @Override
    public int assignNodeTask(String nodeInstId, String userCode,
                              String mangerUserCode, String authDesc) {
        NodeInstance node = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (node == null)
            return -1;

        Set<ActionTask> taskList = node.getWfActionTasks();
        for (ActionTask task : taskList) {
            actionTaskDao.deleteObject(task);
        }
        node.setTaskAssigned("S");
        node.setUserCode(userCode);
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        nodeInstanceDao.updateObject(node);
        OperationLog wfactlog = FlowOptUtils.createActionLog(
            mangerUserCode, node, "添加操作用户" + userCode + ":" + authDesc, null);
        OperationLogCenter.log(wfactlog);
        return 0;
    }


    @Override
    public List<ActionTask> listNodeInstTasks(String nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (null == nodeInst)
            nodeInst = new NodeInstance();
        if (nodeInst.getWfActionTasks().size() == 0) {
            List<ActionTask> actionTasks = new ArrayList<>(1);
            ActionTask actionTask = new ActionTask();
            actionTask.setNodeInstId(nodeInst.getNodeInstId());
            actionTask.setUserCode(nodeInst.getUserCode());
            actionTask.setAssignTime(nodeInst.getLastUpdateTime());
            actionTask.setTaskId(nodeInst.getNodeInstId());
            actionTasks.add(actionTask);
            return actionTasks;
        }
        return new ArrayList<>(nodeInst.getWfActionTasks());
    }


    @Override
    public int deleteNodeTask(String nodeInstId, String userCode,
                              String mangerUserCode) {
//        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId); 20200818 by zhangbin
        NodeInstance node = nodeInstanceDao.getObjectWithReferences(nodeInstId);
        if (node == null)
            return -1;
        Set<ActionTask> taskList = node.getWfActionTasks();
        int leftTaskCount = 0;
        ActionTask leftTask = null;
        for (ActionTask task : taskList) {
            if (userCode.equals(task.getUserCode())) {
                actionTaskDao.deleteObject(task);
            } else {
                leftTaskCount++;
                leftTask = task;
            }
        }
        if (leftTaskCount == 1) {
            node.setTaskAssigned("S");
            node.setUserCode(leftTask.getUserCode());
            actionTaskDao.deleteObject(leftTask);
        }
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        nodeInstanceDao.updateObject(node);

        OperationLog wfactlog = FlowOptUtils.createActionLog(
            mangerUserCode, node, "删除用户：" + userCode + "的任务", null);
        OperationLogCenter.log(wfactlog);
        return 0;
    }

    @Override
    public int deleteNodeTaskById(String taskId,
                                  String mangerUserCode) {
        actionTaskDao.deleteObjectById(taskId);
        return 0;
    }

    /**
     * 删除节点任务
     */
    @Override
    public int addNodeTask(String nodeInstId, String userCode,
                           String mangerUserCode, String authDesc) {
        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;
        if ("T".equals(node.getTaskAssigned())) {
            nodeInstanceDao.fetchObjectReference(node, "wfActionTasks");
            boolean findCount = false;
            ActionTask leftTask = null;
            for (ActionTask task : node.getWfActionTasks()) {
                if (userCode.equals(task.getUserCode())) {
                    findCount = true;
                    break;
                }
            }
            if (!findCount) {
                ActionTask task = FlowOptUtils.createActionTask(nodeInstId,
                    userCode);
                actionTaskDao.saveNewObject(task);
            }
        } else {
            String oldUser = node.getUserCode();
            if (StringUtils.isBlank(oldUser) || StringUtils.equals(oldUser, userCode)) {
                node.setUserCode(userCode);
            } else {
                node.setTaskAssigned("T");
                ActionTask task = FlowOptUtils.createActionTask(nodeInstId,
                    userCode);
                actionTaskDao.saveNewObject(task);
                task = FlowOptUtils.createActionTask(nodeInstId,
                    oldUser);
                actionTaskDao.saveNewObject(task);
            }
        }

        nodeInstanceDao.updateObject(node);
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        OperationLog wfactlog = FlowOptUtils.createActionLog(
            mangerUserCode, node, "添加用户：" + userCode + "的任务，" + authDesc, null);
        //wfactlog.setActionId(actionLogDao.getNextActionId());
        OperationLogCenter.log(wfactlog);
        return 0;
    }


    /**
     * 获取节点实例的操作日志列表
     *
     * @param flowInstId     流程实例id
     * @param withNodeAction 是否包括节点的日志
     * @return List<WfActionLog>
     */
    @Override
    public List<? extends OperationLog> listFlowActionLogs(String flowInstId, boolean withNodeAction) {
        if (optLogManager == null) {
            return null;
        }
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("optTag", flowInstId);
        if (!withNodeAction) {
            filterMap.put("optMethod", "flowOpt");
        }
        return optLogManager.listOptLog("workflow", filterMap, -1, -1);
    }

    /**
     * 获取节点实例的操作日志列表
     *
     * @param flowInstId 流程实例号
     * @param nodeInstId 节点实例好
     * @return List<WfActionLog>
     */
    @Override
    public List<? extends OperationLog> listNodeActionLogs(String flowInstId, String nodeInstId) {
        if (optLogManager == null)
            return null;
        return optLogManager.listOptLog("workflow",
            CollectionsOpt.createHashMap("optTag", flowInstId,
                "optMethod", nodeInstId), -1, -1);
    }

    @Override
    public List<? extends OperationLog> listNodeActionLogs(String nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return null;
        }
        return listNodeActionLogs(nodeInst.getFlowInstId(), nodeInstId);
    }

    /**
     * 获取用户所有的操作记录
     *
     * @param userCode
     * @param pageDesc 和分页机制结合
     * @param lastTime if null return all
     * @return
     */
    @Override
    public List<? extends OperationLog> listUserActionLogs(String userCode, Date lastTime,
                                                           PageDesc pageDesc) {
        if (optLogManager == null)
            return null;
        Map<String, Object> filterMap =
            CollectionsOpt.createHashMap("userCode", userCode,
                "optTime_gt", lastTime);

        List<? extends OperationLog> optLogs =
            optLogManager.listOptLog("workflow", filterMap,
                pageDesc.getRowStart(), pageDesc.getPageSize());
        pageDesc.setTotalRows(optLogManager.countOptLog("workflow", filterMap));
        return optLogs;
    }


    @Override
    public RoleRelegate getRoleRelegateById(Long relegateno) {
        return flowRoleRelegateDao.getObjectById(relegateno);
    }

    @Override
    public void saveRoleRelegate(RoleRelegate roleRelegate) {
        roleRelegate.setRecordDate(DatetimeOpt.currentSqlDate());
        flowRoleRelegateDao.saveObject(roleRelegate);
    }

    @Override
    public List<RoleRelegate> listRoleRelegateByUser(String userCode) {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("grantee", userCode);
        return flowRoleRelegateDao.listObjectsByProperties(filterMap, new PageDesc(-1, -1));
    }

    @Override
    public List<RoleRelegate> listRoleRelegateByGrantor(String grantor) {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("grantor", grantor);
        return flowRoleRelegateDao.listObjectsByProperties(filterMap, new PageDesc(-1, -1));
    }

    @Override
    public void updateFlow(FlowInstance flowInstance) {
        flowInstanceDao.updateObject(flowInstance);
    }

    @Override
    public void updateFlowInstOptInfoAndUser(String flowInstId, String flowOptName, String flowOptTag, String userCode, String unitCode) {
        flowInstanceDao.updateFlowInstOptInfoAndUser(flowInstId, flowOptName, flowOptTag, userCode, unitCode);
    }

    @Override
    public NodeInstance getFirstNodeInst(String flowInstId) {
        return this.getFlowInstance(flowInstId).getFirstNodeInstance();
    }

    @Override
    public boolean deleteFlowInstById(String flowInstId, String userCode) {
        // 判断流程是否可以删除
        HashMap<String, Object> filterMap = new HashMap<>();
        filterMap.put("flowInstId", flowInstId);
        filterMap.put("userCode", userCode);
        FlowInstance flowInstance = flowInstanceDao.getObjectByProperties(filterMap);
        if (flowInstance == null) {
            logger.info("用户 {} 没有权限删除流程 {}", userCode, flowInstId);
            return false;
        }
        flowInstanceDao.deleteObjectById(flowInstId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("flowInstId", flowInstId);
        nodeInstanceDao.deleteObjectsByProperties(map);
        return true;
    }

    /**
     * 办件回收列表，获取用户已办，且下一节点未进行办理的任务(发改委业务需求)
     *
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    @Override
    public List<UserTask> listUserCompleteTasks(Map<String, Object> searchColumn, PageDesc pageDesc) {
        return actionTaskDao.listUserCompleteTasks(searchColumn, pageDesc);
    }

    /**
     * 获取某个节点的用户已办列表(fgw批分回收和批分追加列表)
     *
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    @Override
    public List<UserTask> listUserCompleteFlow(Map<String, Object> searchColumn, PageDesc pageDesc) {
        return actionTaskDao.listUserCompleteFlow(searchColumn, pageDesc);
    }

    @Override
    public NodeInstance reStartFlow(String flowInstId, String managerUserCode, Boolean force) {
        FlowInstance flowInstance = flowInstanceDao.getObjectWithReferences(flowInstId);
        //如果不是强行拉回，需要判断是否流程最后提交人是自己
        if (!force) {
            if (!managerUserCode.equals(flowInstance.getLastUpdateUser())) {
                return null;
            }
        }
        NodeInstance startNodeInst = this.resetFlowToThisNode(flowInstance.getFirstNodeInstance().getNodeInstId(), managerUserCode);
        //退回首节点之后，删除流程变量
        flowEngine.deleteFlowVariable(flowInstId, "", "");
        return startNodeInst;
    }

    @Override
    public List<JSONObject> getListRoleRelegateByGrantor(String grantor) {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("grantor", grantor);
        List<RoleRelegate> roleRelegateList = flowRoleRelegateDao.listObjectsByProperties(filterMap, new PageDesc(-1, -1));
        HashMap<String, Map<String, Object>> hashMap = new HashMap<>();
        for (RoleRelegate roleRelegate : roleRelegateList) {
            String grantee = roleRelegate.getGrantee();
            if (!hashMap.containsKey(grantee)) {
                List<String> roleCodeList = new ArrayList<>();
                roleCodeList.add(roleRelegate.getRoleCode());
                HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                hashMap.put(grantee, stringObjectHashMap);
                stringObjectHashMap.put("roleCodeList", roleCodeList);
                RoleRelegate newRelegate = new RoleRelegate();
                newRelegate.copy(roleRelegate);
                newRelegate.setRoleCode(JSON.toJSONString(roleCodeList));
                stringObjectHashMap.put("newRelegate", newRelegate);
            } else {
                List<String> roleCodeList = (List<String>) hashMap.get(grantee).get("roleCodeList");
                roleCodeList.add(roleRelegate.getRoleCode());
            }
        }
        Set<Map.Entry<String, Map<String, Object>>> entries = hashMap.entrySet();
        ArrayList<Map.Entry<String, Map<String, Object>>> newList = new ArrayList<>(entries);
        ArrayList<JSONObject> roleRelegates = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> mapEntry : newList) {
            RoleRelegate newRelegate = (RoleRelegate) mapEntry.getValue().get("newRelegate");
            List<String> roleCodeList = (List<String>) mapEntry.getValue().get("roleCodeList");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grantor", newRelegate.getGrantor());
            jsonObject.put("grantee", newRelegate.getGrantee());
            jsonObject.put("roleCode", roleCodeList);
            jsonObject.put("unitCode", newRelegate.getUnitCode());
            jsonObject.put("relegateTime", newRelegate.getRelegateTime());
            jsonObject.put("expireTime", newRelegate.getExpireTime());
            jsonObject.put("grantDesc", newRelegate.getGrantDesc());
            roleRelegates.add(jsonObject);
        }
        return roleRelegates;
    }

    @Override
    public JSONArray listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowInstanceGroupDao.listObjectsAsJson(filterMap, pageDesc);
    }

    @Override
    public FlowInstanceGroup getFlowInstanceGroup(String flowInstGroupId) {
        return flowInstanceGroupDao.getObjectById(flowInstGroupId);
    }

    /**
     * 获取节点实例列表
     *
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    @Override
    public List<NodeInstance> listNodeInstance(Map<String, Object> searchColumn, PageDesc pageDesc) {
        return nodeInstanceDao.listObjects(searchColumn, pageDesc);
    }

    /**
     * 强制修改流程状态以及相关节点实例状态
     *
     * @param flowInstId
     * @param mangerUserCode
     * @param instState
     * @param desc
     */
    @Override
    public void updateFlowState(String flowInstId, String mangerUserCode, String instState, String desc) {
        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(flowInstId);
        if (wfFlowInst == null) {
            return;
        }

        // 只能结束未完成的流程
        if ("C".equals(wfFlowInst.getInstState()) || "F".equals(wfFlowInst.getInstState())) {
            return;
        }

        Date updateTime = DatetimeOpt.currentUtilDate();
        // 更新流程实例状态
        wfFlowInst.setInstState(instState);
        wfFlowInst.setLastUpdateTime(updateTime);
        wfFlowInst.setLastUpdateUser(mangerUserCode);
        flowInstanceDao.updtFlowInstInfo(wfFlowInst);
        nodeInstanceDao.updateNodeStateById(wfFlowInst);

        OperationLog managerAct = FlowOptUtils.createActionLog(
            mangerUserCode, flowInstId, "强制修改流程状态为" + instState + ";" + desc);
        OperationLogCenter.log(managerAct);
    }

    /**
     * 获取流程实例列表，并查询流程相关信息(fgw收文办结列表和发文办结列表)
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    @Override
    public JSONArray listFlowInstDetailed(Map<String, Object> searchColumn, PageDesc pageDesc) {
        Object flowInstIds = searchColumn.get("flowInstIds");
        if (flowInstIds != null) {
            searchColumn.put("flowInstIds",flowInstIds.toString().split(","));
        }
        // 获取流程实例数据
        List<FlowInstance> flowInstances = flowInstanceDao.listObjects(searchColumn, pageDesc);
        JSONArray flowInstArray = DictionaryMapUtils.objectsToJSONArray(flowInstances);
        if (flowInstances.isEmpty()) {
            return flowInstArray;
        }
        List<String> flowInstIdList = new ArrayList<>();
        flowInstances.forEach(f -> {
            flowInstIdList.add(f.getFlowInstId());
        });
        // 获取流程机构数据
        List<FlowOrganize> flowOrganizes = flowOrganizeDao.listFlowOrganize(flowInstIdList);
        JSONArray flowOrganizeArray = DictionaryMapUtils.objectsToJSONArray(flowOrganizes);
        // 获取流程办件角色数据
        List<FlowWorkTeam> flowWorkTeams = flowTeamDao.listFlowWorkTeam(flowInstIdList);
        JSONArray flowWorkTeamArray = DictionaryMapUtils.objectsToJSONArray(flowWorkTeams);
        // 数据组装
        for (int i = 0; i < flowInstArray.size(); i++) {
            String flowInstId = flowInstArray.getJSONObject(i).getString("flowInstId");
            // 数据组装 --> 流程机构
            for (int j = 0; j < flowOrganizeArray.size(); j++) {
                if (flowInstId.equals(flowOrganizeArray.getJSONObject(j).getString("flowInstId"))) {
                    String roleCode = flowOrganizeArray.getJSONObject(j).getString("roleCode");
                    if (flowInstArray.getJSONObject(i).getString(roleCode) == null) {
                        flowInstArray.getJSONObject(i).put(roleCode, flowOrganizeArray.getJSONObject(j).getString("unitName"));
                    } else {
                        flowInstArray.getJSONObject(i).put(roleCode, flowInstArray.getJSONObject(i).getString(roleCode) + "," + flowOrganizeArray.getJSONObject(j).getString("unitName"));
                    }
                }
            }
            // 数据组装 --> 办件角色
            for (int j = 0; j < flowWorkTeamArray.size(); j++) {
                if (flowInstId.equals(flowWorkTeamArray.getJSONObject(j).getString("flowInstId"))) {
                    String roleCode = flowWorkTeamArray.getJSONObject(j).getString("roleCode");
                    if (flowInstArray.getJSONObject(i).getString(roleCode) == null) {
                        flowInstArray.getJSONObject(i).put(roleCode, flowWorkTeamArray.getJSONObject(j).getString("userName"));
                    } else {
                        flowInstArray.getJSONObject(i).put(roleCode, flowInstArray.getJSONObject(i).getString(roleCode) + "," + flowWorkTeamArray.getJSONObject(j).getString("userName"));
                    }
                }
            }
        }
        return flowInstArray;
    }
}
