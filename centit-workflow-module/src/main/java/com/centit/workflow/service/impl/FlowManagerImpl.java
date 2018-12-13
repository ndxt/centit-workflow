package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.common.WorkTimeSpan;
import com.centit.support.database.utils.QueryUtils;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.NodeMsgSupport;
import com.centit.workflow.dao.*;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.sun.tools.javac.comp.Flow;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
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
    @Resource
    FlowInstanceDao flowInstanceDao;
    @Resource
    NodeInstanceDao nodeInstanceDao;
    @Resource
    NodeInfoDao flowNodeDao;
    @Resource
    FlowTransitionDao flowTransitionDao;
    @Resource
    ManageActionDao manageActionDao;
    @Resource
    ActionTaskDao actionTaskDao;
    @Resource
    ActionLogDao actionLogDao;
    @Resource
    FlowInfoDao flowDefDao;
    @Resource
    StageInstanceDao stageInstanceDao;
    @Resource
    private FlowWorkTeamDao flowTeamDao;
    @Resource
    RoleRelegateDao flowRoleRelegateDao;
    @Resource
    FlowEngine flowEngine;

    /**
     * 查看工作流程实例状态或进度
     *
     * @return XML 描述的流程流转状态图
     */
    @Override
    public String viewFlowInstance(long flowInstId) {
        FlowInstance wfInst = flowInstanceDao.getObjectCascadeById(flowInstId);
        FlowInfoId id = new FlowInfoId(wfInst
            .getVersion(), wfInst.getFlowCode());
        FlowInfo wfDef = flowDefDao.getObjectCascadeById(id);
        Map<Long, String> nodeState = new HashMap<>();
        Map<Long, Integer> nodeInstCount = new HashMap<>();
        Map<Long, NodeInfo> nodeMap = new HashMap<>();
        Map<String, String> transState = new HashMap<>();
        Map<String, FlowTransition> transMap = new HashMap<>();
        Set<NodeInfo> nodeSet = wfDef.getFlowNodes();
        Boolean findTran = true;
        long benginNodeId = -1;
        long endNodeID = -1;
        for (NodeInfo node : nodeSet) {
            nodeState.put(node.getNodeId(), "ready");
            if (node.getNodeType().equals("A")) {
                nodeState.put(node.getNodeId(), "complete");
                benginNodeId = node.getNodeId();
            } else if (node.getNodeType().equals("F")) {
                endNodeID = node.getNodeId();
            }
            nodeInstCount.put(node.getNodeId(), 0);
            nodeMap.put(node.getNodeId(), node);
        }
        //System.out.println(benginNodeId);
        //flowDefDao.fetchObjectReferences(wfDef);
        Set<FlowTransition> transSet = wfDef.getFlowTransitions();
        for (FlowTransition trans : transSet) {
            //首节点必然通过
            if (trans.getStartNodeId().equals(benginNodeId)) {
                transState.put(String.valueOf(trans.getTransId()), "1");
            } else
                transState.put(String.valueOf(trans.getTransId()), "-1");
            transMap.put(String.valueOf(trans.getTransId()), trans);
        }
        //flowInstanceDao.fetchObjectReferences(wfInst);
        List<NodeInstance> nodeInstSet = wfInst.getNodeInstances();
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
                    if (nodeInst.getNodeState().equals("F"))
                        nodeState.put(nodeInst.getNodeId(), "suspend");
                    else if (nodeInst.getNodeState().equals("C")) {
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
                        if (node != null && "R".equals(node.getNodeType())) {
                            nodeState.put(trans.getStartNodeId(), "complete");
                            //nc = nodeInstCount.get(trans.getStartNodeId());
                            //nodeInstCount.put(trans.getStartNodeId(), (nc==null)?1:nc+1);
                        }
                        node = nodeMap.get(trans.getEndNodeId());
                        if (node != null && "R".equals(node.getNodeType())) {
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
                            if ("R".equals(node.getNodeType())) {
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
                                if (trans.getStartNodeId().equals(ni.getNodeId()) && trans.getEndNodeId().equals(no.getNodeId())) {
                                    //如果有已完成的线，查找一下已完成的所有线的开始节点是否有跟我们上面查出来的有重复，有重复的话说明矛盾，并不是我们要的结果
                                    if (!completeTrans.isEmpty()) {
                                        Boolean trueTrans = true;
                                        for (FlowTransition tr : completeTrans) {
                                            if (trans.getStartNodeId().equals(tr.getStartNodeId())) {
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


        Document viewDoc = DocumentHelper.createDocument();
        Element baseEle = viewDoc.addElement("TopFlow");
        Element procsEle = baseEle.addElement("Procs");
        Element stepsEle = baseEle.addElement("Steps");

        for (Map.Entry<Long, String> ns : nodeState.entrySet()) {
            Element procEle = procsEle.addElement("Proc");
            procEle.addAttribute("id", ns.getKey().toString());
            procEle.addAttribute("inststate", ns.getValue());
            procEle.addAttribute("instcount", String.valueOf(nodeInstCount.get(ns.getKey())));
        }

        for (Map.Entry<String, String> ts : transState.entrySet()) {
            Element stepEle = stepsEle.addElement("Step");
            stepEle.addAttribute("id", ts.getKey());
            stepEle.addAttribute("inststate", ts.getValue());
        }

        return viewDoc.asXML();
    }

    @Override
    public String viewFlowNodeInstance(long flowInstId) {
        FlowInstance wfInst = flowInstanceDao.getObjectById(flowInstId);
        Document viewDoc = DocumentHelper.createDocument();
        Element baseEle = viewDoc.addElement("flow");
        Element procsEle = baseEle.addElement("nodes");

        List<NodeInstance> nodeInstSet = wfInst.getNodeInstances();
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
    public List<UserTask> listNodeTasks(long nodeInstId) {
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
    private int updateInstanceState(long instid, String state, String userCode,
                                    String admindesc) {

        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(instid);
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
        else if ("P".equals(state) && "N".equals(wfFlowInst.getInstState()))
            actionDesc = "挂起流程；";
            // 只能唤醒挂起、失效、超时 的流程
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
                if ("P".equals(nodeInst.getNodeState())) {
                    nodeInst.setNodeState("N");
                    nodeInst.setLastUpdateTime(updateTime);
                    nodeInst.setLastUpdateUser(userCode);
                }
            }
        }
        flowInstanceDao.updateObject(wfFlowInst);


        ManageActionLog managerAct = FlowOptUtils.createManagerAction(instid,
            userCode, "S");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc(actionDesc + admindesc);
        manageActionDao.saveNewObject(managerAct);

        return 1;
    }

    public long updateNodeInstState(long nodeInstId, String newState,
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

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(nodeInst.getFlowInstId(),
            nodeInstId, mangerUserCode, "s");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc(actionDesc);
        manageActionDao.saveNewObject(managerAct);
        return 1;
    }


    public long suspendNodeInstance(long nodeInstId, String mangerUserCode) {
        FlowOptUtils.sendMsg(nodeInstId, null, mangerUserCode);
        return updateNodeInstState(nodeInstId, "P", mangerUserCode);
    }

    public long activizeNodeInstance(long nodeInstId, String mangerUserCode) {
        return updateNodeInstState(nodeInstId, "N", mangerUserCode);
    }

    /**
     * 设置流程期限
     *
     * @param nodeInstId     流程节点实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @return
     */
    public long resetNodeTimelimt(long nodeInstId, String timeLimit,
                                  String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null) {
            return 0;
        }

        nodeInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());
        // 设置最后更新时间和更新人
        nodeInstanceDao.updateObject(nodeInst);


        /*        actionType 对流程管理操作用大写字母，对节点管理操作用小写字母
         *             S s: 状态变更， 超时唤醒、 使失效、 使一个正常的节点变为游离状态 、 是游离节点失效
         *               c: 创建节点  、创建一个游离节点 创建（任意）指定节点
         *             R r: 流转管理，包括  强行回退  、强行提交
         *             T t: 期限管理 、 设置期限
         *               a: 节点任务管理  分配任务、  删除任务 、  禁用任务
         *             U u: 变更属性*/

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(nodeInst.getFlowInstId(),
            nodeInstId, mangerUserCode, "t");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("重新设置节点期限：" + new WorkTimeSpan(timeLimit).getTimeSpanDesc());
        manageActionDao.saveNewObject(managerAct);

        return 1;
    }


    public FlowInstance getFlowInstance(long flowInstId) {
        FlowInstance flowInstance = flowInstanceDao.getObjectCascadeById(flowInstId);
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
        if (flowInstance.getNodeInstances() != null) {
            for (NodeInstance nodeInstance : flowInstance.getNodeInstances()) {
                NodeInfo node = flowNodeDao.getObjectById(nodeInstance.getNodeId());
                nodeInstance.setNodeName(node.getNodeName());
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

    @Override
    public List<ManageActionLog> listManageActionLog(long flowInstId, PageDesc pageDesc) {
        List<ManageActionLog> magLogList = manageActionDao.getFlowManageLogs(flowInstId, pageDesc);
        if (null == magLogList) magLogList = new ArrayList<ManageActionLog>();
        return new ArrayList<ManageActionLog>(magLogList);
    }

    /**
     * 强行结束一个流程，同时将其所有的活动节点也强行结束
     */
    @Override
    public int stopInstance(long flowInstId, String mangerUserCode,
                            String admindesc) {
        FlowOptUtils.sendFinishMsg(flowInstId, mangerUserCode);
        return updateInstanceState(flowInstId, "F", mangerUserCode, admindesc);
    }


    /**
     * 终止一个流程
     * 修改其流程id为负数
     * 更新所有节点状态为F
     * F 强行结束
     */
    public int stopAndChangeInstance(long flowInstId, String mangerUserCode, String admindesc) {
        FlowOptUtils.sendFinishMsg(flowInstId, mangerUserCode);
        return stopInstanceState(flowInstId, mangerUserCode, admindesc);
    }

    /**
     * 更新流程实例状态，同时需更新所有节点实例状态
     *
     * @param instid   实例编号
     * @param userCode 操作用户
     */
    private int stopInstanceState(long instid, String userCode,
                                  String admindesc) {

        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(instid);
        if (wfFlowInst == null)
            return 0;
        Date updateTime = DatetimeOpt.currentUtilDate();
        wfFlowInst.setLastUpdateTime(updateTime);
        wfFlowInst.setLastUpdateUser(userCode);
        wfFlowInst.setFlowInstId(-instid);

        String actionDesc = "U";
        // 只能结束未完成的流程
        if (!"C".equals(wfFlowInst.getInstState())
            && !"F".equals(wfFlowInst.getInstState()))
            actionDesc = "强制结束流程；";
        // 只能挂起正常的流程
        // 不正确的操作
        if ("U".equals(actionDesc))
            return -1;

        // 更新流程实例状态
        wfFlowInst.setInstState("F");
        flowInstanceDao.updtFlowInstInfo(wfFlowInst);
        for (NodeInstance nodeInst : wfFlowInst.getFlowNodeInstances()) {
            if ("N".equals(nodeInst.getNodeState()) || "P".equals(nodeInst.getNodeState()) || "S".equals(nodeInst.getNodeState())) {
                nodeInst.setNodeState("F");
                nodeInst.setLastUpdateTime(updateTime);
                nodeInst.setLastUpdateUser(userCode);
                nodeInstanceDao.updateObject(nodeInst);
            }
        }


        ManageActionLog managerAct = FlowOptUtils.createManagerAction(instid,
            userCode, "S");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc(actionDesc + admindesc);
        manageActionDao.saveNewObject(managerAct);

        return 1;
    }

    /**
     * 挂起一个流程，同时将其所有的活动节点也挂起
     */
    @Override
    public int suspendInstance(long flowInstId, String mangerUserCode,
                               String admindesc) {
        return updateInstanceState(flowInstId, "P", mangerUserCode, admindesc);
    }

    /**
     * 激活一个 挂起的或者无效的流程
     */
    @Override
    public int activizeInstance(long flowInstId, String mangerUserCode,
                                String admindesc) {
        return updateInstanceState(flowInstId, "N", mangerUserCode, admindesc);
    }

    @Override
    public void updateFlowInstUnit(long flowInstId, String unitCode, String optUserCode) {
        FlowInstance flowInst = flowInstanceDao.getObjectById(flowInstId);
        if (flowInst == null)
            return;
        flowInst.setUnitCode(unitCode);
        flowInstanceDao.updateObject(flowInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(flowInstId,
            optUserCode, "U");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("更改流程实例机构代码：" + unitCode);
        //managerAct.setNodeInstId(nodeinstid);
        manageActionDao.saveNewObject(managerAct);
    }

    @Override
    public void updateNodeInstUnit(long nodeInstId, String unitCode, String optUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return;
        nodeInst.setUnitCode(unitCode);
        nodeInstanceDao.updateObject(nodeInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(nodeInst.getFlowInstId(),
            optUserCode, "u");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("更改流程节点：" + nodeInstId + "实例机构代码：" + unitCode);
        managerAct.setNodeInstId(nodeInstId);
        manageActionDao.saveNewObject(managerAct);
    }

    /**
     * 更改节点的角色信息
     */
    @Override
    public void updateNodeRoleInfo(long nodeInstId, String roleType, String roleCode, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return;
        nodeInst.setRoleType(roleType);
        nodeInst.setRoleCode(roleCode);
        nodeInstanceDao.updateObject(nodeInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(nodeInst.getFlowInstId(),
            mangerUserCode, "u");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("更改流程节点角色：" + nodeInstId + "角色代码为：" + roleType + ":" + roleCode + "。");
        managerAct.setNodeInstId(nodeInstId);
        manageActionDao.saveNewObject(managerAct);
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
    public long resetFlowTimelimt(long flowInstId, String timeLimit,
                                  String mangerUserCode, String admindesc) {
        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(flowInstId);
        if (wfFlowInst == null)
            return 0;

        wfFlowInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        wfFlowInst.setLastUpdateUser(mangerUserCode);
        wfFlowInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());

        flowInstanceDao.updateObject(wfFlowInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            flowInstId, mangerUserCode, "T");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("更改流程期限：" +
            new WorkTimeSpan(timeLimit).getTimeSpanDesc() + ";" + admindesc);
        manageActionDao.saveNewObject(managerAct);

        return 1;
    }

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     */
    public long resetFlowToThisNode(long nodeInstId, String mangerUserCode) {

        NodeInstance thisnode = nodeInstanceDao.getObjectCascadeById(nodeInstId);
        if (thisnode == null)
            return -1;

        FlowInstance flow = flowInstanceDao.getObjectCascadeById(thisnode
            .getFlowInstId());
        if (flow == null)
            return -2;
        // 当前节点状态必需不能为正常，如果是正常则没有必要重置
        if ("N".equals(thisnode.getNodeState())) {
            //先将流程实例的状态变为正常
            flow.setInstState("N");
            flowInstanceDao.updateObject(flow);
            return -6;
        }
        NodeInfo nodedef = flowNodeDao.getObjectById(thisnode.getNodeId());
        if ("A".equals(nodedef.getNodeType())
            || "F".equals(nodedef.getNodeType())) {
            // 不能设定到开始或者结束节点
            return -5;
        }

        for (NodeInstance nodeInst : flow.getFlowNodeInstances()) {
            if (("N".equals(nodeInst.getNodeState())
                || "W".equals(nodeInst.getNodeState()))
                && (nodeInst.getNodeId().equals(thisnode.getNodeId()))
                && nodeInst.getRunToken().equals(thisnode.getRunToken())) {
                // 已经有一个正在运行的相同节点实例，不能重置到该节点
                return -6;
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
            if (("N".equals(nodeInst.getNodeState())
                || "W".equals(nodeInst.getNodeState()))
                && currToken != null
                && thisToken != null
                && (currToken.equals(thisToken)
                || currToken.startsWith(thisToken + '.')
                || thisToken.startsWith(currToken + '.'))
                || currToken.startsWith("R"+thisToken + '.')) {

                if ("W".equals(nodeInst.getNodeState())) { // 结束子流程
                    FlowInstance subFlowInst = flowInstanceDao
                        .getObjectById(nodeInst.getSubFlowInstId());
                    if (subFlowInst != null) {
                        FlowOptUtils.endInstance(subFlowInst, "F",
                            mangerUserCode, flowInstanceDao);
                        subFlowInst.setLastUpdateUser(mangerUserCode);
                        flowInstanceDao.updateObject(subFlowInst);
                        //更新子流程下的所有消息未已办
                        FlowOptUtils.sendFinishMsg(subFlowInst.getFlowInstId(),mangerUserCode);

                    }
                }
                nodeInst.setNodeState("B");
                // 设置最后更新时间和更新人
                nodeInst.setLastUpdateUser(mangerUserCode);
                nodeInst.setLastUpdateTime(updateTime);
                ActionLog wfactlog = FlowOptUtils.createActionLog("E",
                    mangerUserCode, nodeInstId);
                wfactlog.setActionId(actionLogDao.getNextActionId());
                nodeInst.addWfActionLog(wfactlog);
                nodeInstanceDao.mergeObject(nodeInst);
                //更新消息未已办
                FlowOptUtils.sendMsg(nodeInst.getNodeInstId(),null,mangerUserCode);
            }
        }
        // 创建新节点
        long lastNodeInstId = nodeInstanceDao.getNextNodeInstId();
        NodeInstance nextNodeInst = flow.newWfNodeInstance();
        nextNodeInst.copyNotNullProperty(thisnode);
        nextNodeInst.setNodeInstId(lastNodeInstId);
        nextNodeInst.setNodeState("N");
        nextNodeInst.setTaskAssigned(thisnode.getTaskAssigned());
        nextNodeInst.setLastUpdateUser(mangerUserCode);
        nextNodeInst.setLastUpdateTime(updateTime);

        for (ActionTask task : thisnode.getWfActionTasks()) {
            nextNodeInst.setTaskAssigned("T");
            if ("T".equals(task.getIsValid())) {
                ActionTask newtask = FlowOptUtils.createActionTask(
                    task.getUserCode(), nextNodeInst, nodedef);
                newtask.setTaskId(actionTaskDao.getNextTaskId());
                // 要判断 过期时间的问题
                nextNodeInst.addWfActionTask(newtask);
                actionTaskDao.saveNewObject(newtask);
            }
        }

        flow.addWfNodeInstance(nextNodeInst);
        flowInstanceDao.updateObject(flow);
        nodeInstanceDao.mergeObject(nextNodeInst);
        Set<Long> nextNodeInsts=new HashSet<>();
        nextNodeInsts.add(lastNodeInstId);
        FlowOptUtils.sendMsg(0L,nextNodeInsts,mangerUserCode);


        //执行节点创建后 事件
        NodeEventSupport nodeEventExecutor = NodeEventSupportFactory.getNodeEventSupportBean(nodedef);
        nodeEventExecutor.runAfterCreate(flow, nextNodeInst, nodedef, mangerUserCode);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            flow.getFlowInstId(), lastNodeInstId, mangerUserCode, "R");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("重新运行节点：" + nodeInstId);
        manageActionDao.saveNewObject(managerAct);

        return lastNodeInstId;
    }


    @Override
    public long forceDissociateRuning(long nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null || (!"N".equals(nodeInst.getNodeState())))
            return -1;
        FlowInstance flowinst = flowInstanceDao.getObjectById(nodeInst
            .getFlowInstId());
        if (flowinst == null)
            return -2;
        int otherRunNode = 0;
        for (NodeInstance tNode : flowinst.getFlowNodeInstances()) {
            if (!tNode.getNodeInstId().equals(nodeInstId)
                && "N".equals(tNode.getNodeState()))
                otherRunNode++;
        }
        if (otherRunNode == 0)
            return -3;

        nodeInst.setRunToken("R" + nodeInst.getRunToken());
        // 设置最后更新时间和更新人
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        nodeInstanceDao.updateObject(nodeInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            nodeInst.getFlowInstId(), nodeInstId, mangerUserCode, "R");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("强制节点分支为游离分支:" + nodeInstId);
        manageActionDao.saveNewObject(managerAct);
        return nodeInstId;
    }

    /**
     * 目前只能提交直接向下流转的节点，暂不支持分支节点，也不支持下一个节点是汇聚类型的流转,机构自动继承上个节点的机构 强行流转无法自动运行
     * 机构和权限表达式，所以新的节点可能没有人能够对其进行操作，必需要配合任务管理来指定到具体的人
     */
    @Override
    public long forceCommit(long nodeInstId, String mangerUserCode) {

        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return -1;

        List<FlowTransition> transList = flowTransitionDao.getNodeTrans(nodeInst
            .getNodeId());
        if (transList == null || transList.size() == 0) {
            //if(nodeInst.getRunToken().startsWith("R")){
            //log.info("游离节点:" + nodeInstId);
            //将节点的状态设置为已完成

            Date updateTime = DatetimeOpt.currentUtilDate();
            nodeInst.setLastUpdateTime(updateTime);
            nodeInst.setLastUpdateUser(mangerUserCode);
            //创建节点提交日志 S:提交节点
            ActionLog wfactlog = FlowOptUtils.createActionLog("S", mangerUserCode, nodeInst, null);
            wfactlog.setActionId(actionLogDao.getNextActionId());
            wfactlog.setActionTime(updateTime);

            nodeInst.addWfActionLog(wfactlog);
            nodeInst.setNodeState("F");
            nodeInstanceDao.updateObject(nodeInst);
            return nodeInst.getRunToken().startsWith("R") ? 0 : -2;
        }

        if (transList.size() != 1)
            return -2;

        FlowTransition trans = transList.get(0);
        long nextCode = transList.get(0).getEndNodeId();
        NodeInfo nextNode = flowNodeDao.getObjectById(nextCode);
        if (nextNode == null)
            return -3;
        if (!"C".equals(nextNode.getNodeType()))
            return -4;

        Date commitTime = DatetimeOpt.currentUtilDate();
        FlowInstance flowInst = flowInstanceDao.getObjectById(nodeInst
            .getFlowInstId());
        FlowInfo flowInfo = flowDefDao.getFlowDefineByID(
            flowInst.getFlowCode(), flowInst.getVersion());

        nodeInst.setNodeState("F");
        // 设置最后更新时间和更新人
        nodeInst.setLastUpdateUser(mangerUserCode);
        nodeInst.setLastUpdateTime(commitTime);

        long nextNodeInstId = nodeInstanceDao.getNextNodeInstId();
        NodeInstance nextNodeInst = FlowOptUtils.createNodeInst(
            nodeInst.getUnitCode(), mangerUserCode, null, flowInst, nodeInst, flowInfo,
            nextNode, trans);

        nextNodeInst.setNodeInstId(nextNodeInstId);
        nextNodeInst.setPrevNodeInstId(nodeInst.getNodeId());
        nextNodeInst.setRunToken(nodeInst.getRunToken());// 添加令牌算法
        nextNodeInst.setTransPath(String.valueOf(trans.getTransId()));

        //创建节点提交日志 S:提交节点
        ActionLog wfactlog = FlowOptUtils.createActionLog("S", mangerUserCode, nodeInst, null);
        wfactlog.setActionId(actionLogDao.getNextActionId());
        wfactlog.setActionTime(commitTime);

        nodeInst.addWfActionLog(wfactlog);
        nodeInstanceDao.updateObject(nodeInst);

        nodeInstanceDao.saveNewObject(nextNodeInst);

        flowInst.setLastUpdateUser(mangerUserCode);
        flowInst.setLastUpdateTime(commitTime);
        flowInstanceDao.updateObject(flowInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            nodeInst.getFlowInstId(), mangerUserCode, "R");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("强制提交节点:" + nodeInstId);
        manageActionDao.saveNewObject(managerAct);
        return nextNodeInstId;
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
    public List<ActionLog> listUserActionLogs(String userCode,
                                              PageDesc pageDesc, Date lastTime) {
        List<ActionLog> actionLogs = actionLogDao.listUserActionLogs(
            userCode, pageDesc, lastTime);
        return new ArrayList<ActionLog>(actionLogs);
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

            return new ArrayList<NodeInstance>(tempList);
        }
        return new ArrayList<NodeInstance>();

    }

    public List<NodeInstance> listFlowInstNodes(long wfinstid) {
        List<NodeInstance> nodeInstList = new ArrayList<>();
        FlowInstance flowInst = flowInstanceDao.getObjectCascadeById(wfinstid);
        List<NodeInstance> nodeInstsSet = flowInst.getFlowNodeInstances();
        for (NodeInstance nodeInst : nodeInstsSet) {
            NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
            // 不显示 自动执行的节点 codefan@sina.com 2012-7-8
            if ("D".equals(node.getOptType()) || "E".equals(node.getOptType()))
                continue;

            nodeInst.setNodeName(node.getNodeName());
            if (nodeInst.getNodeState().equals("N")) {
                List<UserTask> taskList = actionTaskDao.listUserTaskByFilter(
                    QueryUtils.createSqlParamsMap("nodeInstId", nodeInst.getNodeInstId()), new PageDesc(-1, -1));

                List<String> trainsUsers = new ArrayList<String>();
                for (UserTask userTask : taskList) {
                    trainsUsers.add(userTask.getUserCode());
                }
                nodeInst.setTrainsUsers(trainsUsers);
            }
            nodeInstList.add(nodeInst);
        }
        return new ArrayList<>(nodeInstList);
    }

    public List<ActionTask> listNodeActionTasks(long nodeinstid) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeinstid);
        if (null == nodeInst)
            nodeInst = new NodeInstance();
        return new ArrayList<ActionTask>(nodeInst.getWfActionTasks());
    }

    public int deleteNodeActionTasks(long nodeinstid, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeinstid);
        int nTasks = nodeInst.getWfActionTasks().size();

        if (nTasks > 0) {
            nodeInst.getWfActionTasks().clear();
            nodeInstanceDao.updateObject(nodeInst);

            ManageActionLog managerAct = FlowOptUtils.createManagerAction(
                nodeInst.getFlowInstId(), nodeinstid, mangerUserCode, "a");
            managerAct.setActionId(manageActionDao.getNextManageId());
            managerAct.setAdminDesc("删除节点任务:"
                + nodeInst.getNodeInstId());
            manageActionDao.saveNewObject(managerAct);
        }

        return nTasks;
    }

    public List<ActionLog> listNodeActionLogs(long nodeinstid) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeinstid);
        return new ArrayList<ActionLog>(nodeInst.getWfActionLogs());
    }

    /**
     * isTimer T 计时、 F 不计时 H仅环节计时 、暂停P
     */
    public int suspendNodeInstTimer(long nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return -1;

        nodeInstanceDao.updateNodeTimerState(nodeInstId, "P", mangerUserCode);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            nodeInst.getFlowInstId(), nodeInstId, mangerUserCode, "t");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("暂停节点计时:"
            + nodeInstId);
        manageActionDao.saveNewObject(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时F 、计时T(有期限)、暂停P H 仅环节计时 唤醒时要根据 wf_node 的计时类别进行修改 T 计时、有期限 F
     * 不计时 H仅环节计时
     */
    public int activizeNodeInstTimer(long nodeInstId, String mangerUserCode) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        if (nodeInst == null)
            return 0;
        NodeInfo node = flowNodeDao.getObjectById(nodeInst.getNodeId());
        nodeInstanceDao.updateNodeTimerState(nodeInstId,
            node.getIsAccountTime() /* T */, mangerUserCode);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            nodeInst.getFlowInstId(), nodeInstId, mangerUserCode, "t");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("恢复节点计时:"
            + nodeInstId);
        manageActionDao.saveNewObject(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时 F、计时T(有期限)、暂停P 忽略(无期限) F
     */
    public int suspendFlowInstTimer(long flowInstId, String mangerUserCode) {
        flowInstanceDao.updateFlowTimerState(flowInstId, "P", mangerUserCode);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            flowInstId, mangerUserCode, "T");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct
            .setAdminDesc("暂停流程计时: " + flowInstId);
        manageActionDao.saveNewObject(managerAct);
        return 1;
    }

    /**
     * isTimer 不计时N、计时T(有期限)、暂停P 忽略(无期限) F
     */
    public int activizeFlowInstTimer(long flowInstId, String mangerUserCode) {
        flowInstanceDao.updateFlowTimerState(flowInstId, "T", mangerUserCode);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            flowInstId, mangerUserCode, "T");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("恢复流程计时: "
            + flowInstId);
        manageActionDao.saveNewObject(managerAct);
        return 1;
    }

    @Override
    public List<NodeInstance> listPauseTimerNodeInst(String userCode, PageDesc pageDesc) {
        List<NodeInstance> tempList = nodeInstanceDao.listNodeInstByTimer(
            userCode, "P", pageDesc);
        return new ArrayList<NodeInstance>(tempList);
    }

    @Override
    public List<FlowInstance> listPauseTimerFlowInst(String userCode, PageDesc pageDesc) {
        List<FlowInstance> tempList = flowInstanceDao.listFlowInstByTimer(
            userCode, "P", pageDesc);
        return new ArrayList<FlowInstance>(tempList);
    }

    @Override
    public List<ActionLog> listGrantorActionLog(String userCode,
                                                PageDesc pageDesc) {
        List<ActionLog> tempList = actionLogDao.listGrantorActionLog(
            userCode, pageDesc);
        return new ArrayList<ActionLog>(tempList);
    }

    @Override
    public List<ActionLog> listGrantdedActionLog(String userCode,
                                                 PageDesc pageDesc) {
        List<ActionLog> tempList = actionLogDao.listGrantedActionLog(
            userCode, pageDesc);
        return new ArrayList<ActionLog>(tempList);
    }

    public void setFlowInstanceDao(FlowInstanceDao flowInstanceDao) {
        this.flowInstanceDao = flowInstanceDao;
    }

    public void setNodeInstanceDao(NodeInstanceDao nodeInstanceDao) {
        this.nodeInstanceDao = nodeInstanceDao;
    }

    public void setFlowNodeDao(NodeInfoDao flowNodeDao) {
        this.flowNodeDao = flowNodeDao;
    }

    public void setFlowTransitionDao(FlowTransitionDao flowTransitionDao) {
        this.flowTransitionDao = flowTransitionDao;
    }

    public void setManageActionDao(ManageActionDao manageActionDao) {
        this.manageActionDao = manageActionDao;
    }

    public void setActionTaskDao(ActionTaskDao actionTaskDao) {
        this.actionTaskDao = actionTaskDao;
    }

    public void setActionLogDao(ActionLogDao actionLogDao) {
        this.actionLogDao = actionLogDao;
    }

    public void setFlowDefDao(FlowInfoDao flowDefDao) {
        this.flowDefDao = flowDefDao;
    }

    public void setFlowTeamDao(FlowWorkTeamDao flowTeamDao) {
        this.flowTeamDao = flowTeamDao;
    }

    public void setStageInstanceDao(StageInstanceDao stageInstanceDao) {
        this.stageInstanceDao = stageInstanceDao;
    }


    @Override
    public List<StageInstance> listStageInstByFlowInstId(long flowInstId) {

        return new ArrayList<StageInstance>(
            stageInstanceDao.listStageInstByFlowInstId(flowInstId)
        );
    }


    @Override
    public long resetStageTimelimt(long flowInstId, long stageId, String timeLimit,
                                   String mangerUserCode, String admindesc) {
        FlowInstance wfFlowInst = flowInstanceDao.getObjectById(flowInstId);
        if (wfFlowInst == null)
            return 0;

        StageInstance stageInst = stageInstanceDao.getObject(flowInstId, stageId);

        stageInst.setLastUpdateTime(new Date(System.currentTimeMillis()));
        stageInst.setTimeLimit(new WorkTimeSpan(timeLimit).toNumber());

        flowInstanceDao.updateObject(wfFlowInst);

        ManageActionLog managerAct = FlowOptUtils.createManagerAction(
            flowInstId, mangerUserCode, "T");
        managerAct.setActionId(manageActionDao.getNextManageId());
        managerAct.setAdminDesc("更改阶段" + stageId + "期限：" +
            new WorkTimeSpan(timeLimit).getTimeSpanDesc() + ";" + admindesc);
        manageActionDao.saveNewObject(managerAct);

        return 1;

    }


    @Override
    public int moveUserTaskTo(String fromUserCode, String toUserCode,
                              String optUserCode, String moveDesc) {
        /**
         *  1, 查出 所有的 fromUserCode 的待办， 可能在 wf_node_instance 也可能在 actiontask 表中
         *      但是不包括 委托的
         *  2, 每个任务 都需要在 actionlog 记录一条 任务转移的记录
         */
        //wf_node_instance 中的待办
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("taskAssigned", "S");
        filterMap.put("userCode", fromUserCode);
        filterMap.put("nodeState", "N");
        List<NodeInstance> nodeInstances = nodeInstanceDao.listObjects(filterMap);
        List<ActionLog> actionLogList = new ArrayList<ActionLog>();
        if (nodeInstances != null && nodeInstances.size() > 0) {
            for (NodeInstance nodeInstance : nodeInstances) {
                //将userCode指向新用户
                nodeInstance.setUserCode(toUserCode);
                nodeInstance.setLastUpdateTime(DatetimeOpt.currentUtilDate());
                nodeInstance.setLastUpdateUser(optUserCode);
                nodeInstanceDao.mergeObject(nodeInstance);
                //日志
                ActionLog wfactlog = FlowOptUtils.createActionLog("A",
                    optUserCode, nodeInstance.getNodeInstId());
                wfactlog.setActionId(actionLogDao.getNextActionId());
                actionLogList.add(wfactlog);
            }
            //nodeInstanceDao.saveNewObjects(nodeInstances);
            DatabaseOptUtils.batchSaveNewObjects(actionLogDao, actionLogList);
        }
        //actiontask 中的待办
        filterMap.clear();
        filterMap.put("isValid", "T");
        //filterMap.put("taskState","A");
        filterMap.put("userCode", fromUserCode);
        List<ActionTask> actionTasks = actionTaskDao.listObjects(filterMap);
        if (actionTasks != null && actionTasks.size() > 0) {
            Iterator<ActionTask> it = actionTasks.iterator();
            while (it.hasNext()) {
                ActionTask actionTask = it.next();
                //任务超时
                if (actionTask.getExpireTime() != null && (actionTask.getExpireTime().getTime() <= new Date().getTime())) {
                    it.remove();
                    continue;
                }
                NodeInstance nodeInstance = nodeInstanceDao.getObjectById(actionTask.getNodeInstId());
                //对应的节点是否正常
                if (nodeInstance != null && !"N".equals(nodeInstance.getNodeState())) {
                    it.remove();
                    continue;
                }
                //节点对应的流程是否正常
                if (nodeInstance != null) {
                    FlowInstance flowInstance = flowInstanceDao.getObjectById(nodeInstance.getFlowInstId());
                    if (flowInstance != null && !"N".equals(flowInstance.getInstState())) {
                        it.remove();
                        continue;
                    }
                }
            }

        }
        filterMap.clear();
        actionLogList.clear();
        if (actionTasks != null && actionTasks.size() > 0) {
            for (ActionTask actionTask : actionTasks) {
                //如果action中只有一条记录，那就写回到nodeinstance里面
                filterMap.put("nodeInstId", actionTask.getNodeInstId());
                List<ActionTask> tasks = actionTaskDao.listObjects(filterMap);
                if (tasks != null && tasks.size() == 1) {
                    NodeInstance nodeInstance = nodeInstanceDao.getObjectById(actionTask.getNodeInstId());
                    nodeInstance.setTaskAssigned("S");
                    nodeInstance.setUserCode(toUserCode);
                    nodeInstanceDao.mergeObject(nodeInstance);
                    actionTaskDao.deleteObjectById(actionTask.getTaskId());
                    continue;
                }
                actionTask.setUserCode(toUserCode);
                //这儿应该还要设置新用户的 roleType 和roleCode，但是不知道怎么获取。
                actionTaskDao.mergeObject(actionTask);
                //日志
                ActionLog wfactlog = FlowOptUtils.createActionLog("A",
                    optUserCode, actionTask.getNodeInstId());
                wfactlog.setActionId(actionLogDao.getNextActionId());
                actionLogList.add(wfactlog);
            }
            //actionTaskDao.saveNewObjects(actionTasks);
            DatabaseOptUtils.batchSaveNewObjects(actionLogDao, actionLogList);
        }
        //wf_task_move这个没有对应的操作
        return 0;
    }


    /**
     * 在任务列表中指定工作人员，这样就屏蔽了按照角色自动查找符合权限的人员
     */
    @Override
    public long assignTask(long nodeInstId, String userCode,
                           String mangerUserCode, Date expiretime, String authDesc) {
        NodeInstance node = nodeInstanceDao.getObjectById(nodeInstId);
        if (node == null)
            return -1;

        Set<ActionTask> taskList = node.getWfActionTasks();
        for (ActionTask task : taskList) {
            if ("T".equals(task.getIsValid())
                && userCode.equals(task.getUserCode()))
                return -2;
        }

        ActionTask task = FlowOptUtils.createActionTask(nodeInstId,
            userCode);
        task.setTaskId(actionTaskDao.getNextTaskId());
        task.setExpireTime(expiretime);
        task.setAuthDesc(authDesc);
        node.addWfActionTask(task);
        node.setTaskAssigned("T");

        nodeInstanceDao.mergeObject(node);
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        ActionLog wfactlog = FlowOptUtils.createActionLog("A",
            mangerUserCode, node.getNodeInstId());
        wfactlog.setActionId(actionLogDao.getNextActionId());
        actionLogDao.mergeObject(wfactlog);
        return 0;
    }


    @Override
    public List<ActionTask> listNodeInstTasks(Long nodeInstId) {
        NodeInstance nodeInst = nodeInstanceDao.getObjectById(nodeInstId);
        return new ArrayList<ActionTask>(nodeInst.getWfActionTasks());
    }


    @Override
    public int disableTask(long taskInstId, String mangerUserCode) {
        ActionTask assignedTask = actionTaskDao.getObjectById(taskInstId);
        if (assignedTask == null)
            return -1;
        NodeInstance node = nodeInstanceDao.getObjectById(assignedTask
            .getNodeInstId());
        if (node == null)
            return -2;
        if ("C".equals(assignedTask.getTaskState()))// 只能禁用没有完成的任务
            return -3;

        Set<ActionTask> taskList = node.getWfActionTasks();
        int atc = 0;
        for (ActionTask task : taskList) {
            if (task.getTaskId().equals(taskInstId))// 只能禁用未完成的任务
                assignedTask = task;
            else if ("T".equals(task.getIsValid())
                && "A".equals(task.getTaskState()))
                atc++;
        }

        assignedTask.setIsValid("F");
        node.setTaskAssigned(atc > 0 ? "T" : "F");
        nodeInstanceDao.mergeObject(node);
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        ActionLog wfactlog = FlowOptUtils.createActionLog("A",
            mangerUserCode, node.getNodeInstId());
        wfactlog.setActionId(actionLogDao.getNextActionId());
        actionLogDao.mergeObject(wfactlog);
        return 0;
    }

    /**
     * 删除节点任务
     */
    @Override
    public int deleteTask(long taskInstId, String mangerUserCode) {
        ActionTask assignedTask = actionTaskDao.getObjectById(taskInstId);
        if (assignedTask == null)
            return -1;
        NodeInstance node = nodeInstanceDao.getObjectById(assignedTask
            .getNodeInstId());
        if (node == null)
            return -2;
        if ("C".equals(assignedTask.getTaskState()))// 只能禁用没有完成的任务
            return -3;

        Set<ActionTask> taskList = node.getWfActionTasks();
        int atc = 0;
        for (ActionTask task : taskList) {
            if (task.getTaskId().equals(taskInstId))// 只能禁用未完成的任务
                assignedTask = task;
            else if ("T".equals(task.getIsValid())
                && "A".equals(task.getTaskState()))
                atc++;
        }

        node.removeWfActionTask(assignedTask);
        node.setTaskAssigned(atc > 0 ? "T" : "F");
        nodeInstanceDao.mergeObject(node);
        node.setLastUpdateUser(mangerUserCode);
        node.setLastUpdateTime(DatetimeOpt.currentUtilDate());
        ActionLog wfactlog = FlowOptUtils.createActionLog("A",
            mangerUserCode, node.getNodeInstId());
        wfactlog.setActionId(actionLogDao.getNextActionId());
        actionLogDao.mergeObject(wfactlog);
        return 0;
    }

    @Override
    public List<ActionLog> listFlowActionLogs(long flowInstId) {
        List<ActionLog> actionLogList = new ArrayList<ActionLog>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        //获取该流程下所有节点
        filterMap.put("flowInstId", flowInstId);
        List<NodeInstance> nodeInstances = nodeInstanceDao.listObjects(filterMap);
        if (nodeInstances == null || nodeInstances.size() == 0) {
            return actionLogList;
        }
        //获取所有节点的操作日志
        for (NodeInstance nodeInstance : nodeInstances) {
            actionLogList.addAll(listNodeActionLogs(nodeInstance.getNodeInstId()));
        }
        //根据时间排序
        if (actionLogList.size() > 0) {
            Collections.sort(actionLogList, new Comparator<ActionLog>() {
                @Override
                public int compare(ActionLog o1, ActionLog o2) {
                    return o1.getActionTime().getTime() > o2.getActionTime().getTime() ? 1 : -1;
                }
            });
        }
        return actionLogList;
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
    public void deleteRoleRelegate(Long relegateno) {
        flowRoleRelegateDao.deleteObjectById(relegateno);
    }
}
