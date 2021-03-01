package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.*;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JSONOpt;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@Api(value = "流程控制",
    tags = "流程控制接口类")
@RequestMapping("/flow/manager")
public class FlowManagerController extends BaseController {
    //public static final Logger logger = LoggerFactory.getLogger(SampleFlowManagerController.class);

    @Autowired
    private FlowManager flowManager;
    @Autowired
    private FlowEngine flowEng;
    @Autowired
    private FlowDefine flowDef;

    /**
     * 流程实例检索查询
     *
     * @return
     */
    @ApiOperation(value = "流程实例检索查询", notes = "流程实例检索查询")
    @RequestMapping(method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Object> list(PageDesc pageDesc,
                                        HttpServletRequest request) {
        Map<String, Object> searchColumn = BaseController.collectRequestParameters(request);
        JSONArray listObjects = flowManager.listFlowInstance(searchColumn, pageDesc);
        return PageQueryResult.createJSONArrayResult(listObjects, pageDesc, FlowInstance.class);
    }

    /**
     * 根据id获取流程实例对象
     *
     * @param flowInstId 流程实例ID
     */
    @ApiOperation(value = "根据id获取流程实例对象", notes = "根据id获取流程实例对象")
    @WrapUpResponseBody
    @RequestMapping(value = "/{flowInstId}", method = RequestMethod.GET)
    public Map<String, Object> getFlowInstance(@PathVariable String flowInstId) {
        FlowInstance flowInst = flowManager.getFlowInstance(flowInstId);
        Map<String, Object> result = new HashMap<>();

        List<StageInstance> stageList = flowManager.listStageInstByFlowInstId(flowInstId);
        //String viewFlowInst = flowManager.viewFlowInstance(flowInstId);
        result.put("flowInst", flowInst);
        result.put("viewFlowInst", flowManager.viewFlowInstance(flowInstId));
        result.put("stageList", stageList);
        return result;
        //JsonResultUtils.writeSingleDataJson(result, response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }

    /**
     * 查看流程图
     *
     * @param response
     */
    @ApiOperation(value = "查看流程实例流程图", notes = "查看流程实例流程图")
    @RequestMapping(value = "/viewxml/{flowInstId}", method = RequestMethod.GET)
    public void viewRuntimeXml(@PathVariable String flowInstId, HttpServletResponse response) {
        FlowInstance flowInst = flowManager.getFlowInstance(flowInstId);
        String flowCode = flowInst.getFlowCode();
        Long version = flowInst.getVersion();
        if (StringUtils.isNotBlank(flowCode)) {
            FlowInfo obj = flowDef.getFlowInfo(flowCode, version);
            String wfDefXML = obj.getFlowXmlDesc();

            Map<String, Object> result = new HashMap<>();
            Map<String, Object> flowInstDesc = flowManager.viewFlowInstance(flowInstId);
            result.put("json", wfDefXML);
            result.put("viewJson", flowInstDesc);
            JsonResultUtils.writeSingleDataJson(result, response);
        }
    }

       /**
     * 查看流程图
     *
     * @param response
     */
    @ApiOperation(value = "查看流程图", notes = "查看流程图")
    @RequestMapping(value = "/viewxml/{flowCode}/{version}", method = RequestMethod.GET)
    public void viewRuntimeXml(@PathVariable String flowCode, @PathVariable Long version, HttpServletRequest request, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowInfo(flowCode, version);
        String xml = obj.getFlowXmlDesc();
        HashMap<String, String> result = new HashMap<>();
        result.put("xml", xml);
        JsonResultUtils.writeSingleDataJson(result, response);
    }

    /**
     * 查看流程图
     *
     * @param response
     */
    @ApiOperation(value = "查看流程实例节点流程图", notes = "查看流程实例节点流程图")
    @RequestMapping(value = "/nodesxml/{flowInstId}", method = RequestMethod.GET)
    public void viewNodeInstancesXml(@PathVariable String flowInstId, HttpServletResponse response) {
        String nodesxml = flowManager.viewFlowNodeInstance(flowInstId);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("nodesxml", nodesxml);
        JsonResultUtils.writeSingleDataJson(result, response);
    }

    /**
     * 获取组织机构列表
     * TODO:flowEng.viewFlowOrganize(flowInstId)要是直接返回unitInfo更方便
     *
     * @param flowInstId
     * @param response
     */
    @ApiOperation(value = "获取组织机构列表", notes = "获取组织机构列表")
    @RequestMapping(value = "/getorglist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Map<String, String>> getOrganizeList(@PathVariable String flowInstId, PageDesc pageDesc, HttpServletResponse response) {
        Map<String, List<String>> organizeMap = flowEng.viewFlowOrganize(flowInstId);
        List<Map<String, String>> organizeList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : organizeMap.entrySet()) {
            for (String unitCode : entry.getValue()) {
                HashMap<String, String> unitTempMap = new HashMap<>();
                unitTempMap.put("roleCode", entry.getKey());
                unitTempMap.put("unitCode", unitCode);
                unitTempMap.put("unitName", CodeRepositoryUtil.getValue("unitCode", unitCode));
                organizeList.add(unitTempMap);
            }
        }
        pageDesc.setTotalRows(organizeList.size());
        return PageQueryResult.createResult(organizeList, pageDesc);
    }

    /**
     * 删除指定的流程组织机构
     *
     * @return
     */
    @ApiOperation(value = "删除指定的流程组织机构", notes = "删除指定的流程组织机构")
    @RequestMapping(value = "/deleteorg/{flowInstId}/{roleCode}/{unitCode}", method = RequestMethod.GET)
    public void deleteOrg(@PathVariable String flowInstId, @PathVariable String roleCode, @PathVariable String unitCode, HttpServletResponse response) {
        flowEng.deleteFlowOrganize(flowInstId, roleCode, unitCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }


    /**
     * 删除指定roleCode下的所有流程工作机构
     *
     * @return
     */
    @ApiOperation(value = "删除指定roleCode下的所有流程工作机构", notes = "删除指定roleCode下的所有流程工作机构")
    @RequestMapping(value = "/deleteorg/{flowInstId}/{roleCode}", method = RequestMethod.GET)
    public void deleteOrgAll(@PathVariable String flowInstId, @PathVariable String roleCode, HttpServletResponse response) {
        flowEng.deleteFlowOrganize(flowInstId, roleCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }


    /**
     * 添加流程工作机构
     *
     * @return
     */
    @ApiOperation(value = "添加流程工作机构", notes = "添加流程工作机构")
    @RequestMapping(value = "/saveorg/{flowInstId}/{roleCode}/{unitCode}/{authDesc}", method = RequestMethod.POST)
    public void assignOrganize(
        @PathVariable String flowInstId, @PathVariable String roleCode,
        @PathVariable String unitCode, @PathVariable String authDesc,
        HttpServletRequest request, HttpServletResponse response) {
        flowEng.assignFlowOrganize(flowInstId, roleCode, unitCode, authDesc);
        JsonResultUtils.writeSingleDataJson("", response);

    }

    /**
     * 给一个节点指定任务、用这个代替系统自动分配任务
     */
    @ApiOperation(value = "给一个节点指定任务、用这个代替系统自动分配任务", notes = "给一个节点指定任务、用这个代替系统自动分配任务")
    @RequestMapping(value = "/assign/{nodeInstId}/{userCode}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void assign(@PathVariable String nodeInstId, @PathVariable String userCode, @RequestBody ActionTask actionTask) {
        flowManager.assignNodeTask(nodeInstId,
            actionTask.getUserCode(), StringUtils.isBlank(userCode) ? "admin" : userCode, actionTask.getAuthDesc());
    }

    @ApiOperation(value = "添加节点任务", notes = "添加节点任务")
    @WrapUpResponseBody
    @RequestMapping(value = "/addNodeTask/{nodeInstId}/{mangerUserCode}", method = RequestMethod.POST)
    public void addNodeTask(@PathVariable String nodeInstId, @PathVariable String mangerUserCode, @RequestBody ActionTask actionTask) {
        flowManager.addNodeTask(nodeInstId,
            actionTask.getUserCode(), StringUtils.isBlank(mangerUserCode) ? "admin" : mangerUserCode, actionTask.getAuthDesc());
    }

    @ApiOperation(value = "删除节点任务", notes = "删除节点任务")
    @WrapUpResponseBody
    @RequestMapping(value = "/deleteNodeTask/{nodeInstId}/{mangerUserCode}", method = RequestMethod.POST)
    public void deleteNodeTask(@PathVariable String nodeInstId, @PathVariable String mangerUserCode, @RequestBody ActionTask actionTask) {
        flowManager.deleteNodeTask(nodeInstId,
            actionTask.getUserCode(), StringUtils.isBlank(mangerUserCode) ? "admin" : mangerUserCode);
    }

    /**
     * 删除任务
     *
     * @return
     */
    @ApiOperation(value = "删除任务", notes = "删除任务")
    @RequestMapping(value = "/deleteTask/{taskId}", method = RequestMethod.POST)
    public void deleteTask(@PathVariable String taskId, HttpServletRequest request, HttpServletResponse response) {
        flowManager.deleteNodeTaskById(taskId, "admin");
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /*tab：办件角色管理*/

    /**
     * 查询办件角色列表
     *
     * @param flowInstId
     * @param response
     */
    @ApiOperation(value = "查询办件角色列表", notes = "查询办件角色列表")
    @RequestMapping(value = "/getteamlist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Map<String, String>> getTeamList(@PathVariable String flowInstId, PageDesc pageDesc, HttpServletResponse response) {
        Map<String, List<String>> teamMap = flowEng.viewFlowWorkTeam(flowInstId);
        List<Map<String, String>> teamList = new ArrayList<Map<String, String>>();
        for (Map.Entry<String, List<String>> entry : teamMap.entrySet()) {
            Set<HashMap<String, String>> userMap = new HashSet<HashMap<String, String>>();
            for (String userCode : entry.getValue()) {
                Map<String, String> teamTempMap = new HashMap<String, String>();
                teamTempMap.put("roleCode", entry.getKey());
                teamTempMap.put("userCode", userCode);
                teamTempMap.put("userName", CodeRepositoryUtil.getValue("userCode", userCode));
                teamList.add(teamTempMap);
            }
        }
        pageDesc.setTotalRows(teamList.size());
        return PageQueryResult.createResult(teamList, pageDesc);
    }


    /**
     * 删除指定roleCode下的所有流程工作小组
     *
     * @return
     */
    @ApiOperation(value = "删除指定roleCode下的所有流程工作小组", notes = "删除指定roleCode下的所有流程工作小组")
    @RequestMapping(value = "/deleteteam/{flowInstId}/{roleCode}", method = RequestMethod.GET)
    public void deleteWorkTeam(@PathVariable String flowInstId, @PathVariable String roleCode, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        flowEng.deleteFlowWorkTeam(flowInstId, roleCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /**
     * 删除指定的流程工作小组
     *
     * @return
     */
    @ApiOperation(value = "删除指定的流程工作小组", notes = "删除指定的流程工作小组")
    @RequestMapping(value = "/deleteteam/{flowInstId}/{roleCode}/{userCode}", method = RequestMethod.GET)
    public void deleteWorkTeamUser(@PathVariable String flowInstId, @PathVariable String roleCode, @PathVariable String userCode, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        flowEng.deleteFlowWorkTeam(flowInstId, roleCode, userCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }


    /*tab：流程变量管理*/

    /**
     * 查询变量列表
     *
     * @param flowInstId
     * @param response
     */
    @ApiOperation(value = "查询变量列表", notes = "查询变量列表")
    @RequestMapping(value = "/getvariablelist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<FlowVariable> getVariableList(@PathVariable String flowInstId, PageDesc pageDesc, HttpServletResponse response) {
        List<FlowVariable> variableList = flowEng.listFlowVariables(flowInstId);
        pageDesc.setTotalRows(variableList.size());
        return PageQueryResult.createResult(variableList, pageDesc);
    }


    /**
     * 保存流程变量
     *
     * @return
     */
    @ApiOperation(value = "保存流程变量", notes = "保存流程变量")
    @RequestMapping(value = "/savevariable/{flowInstId}/{varName}/{varValue}", method = RequestMethod.GET)
    public void saveVariable(@PathVariable String flowInstId, @PathVariable String varName, @PathVariable String varValue, HttpServletRequest request, HttpServletResponse response) {
        String runToken = request.getParameter("runToken");
        flowEng.saveFlowNodeVariable(flowInstId, runToken, varName, StringUtils.isBlank(varValue) ? null : varValue);
        JsonResultUtils.writeSingleDataJson("", response);
    }


    /**
     * 新增变量是需要的令牌选择项
     *
     * @param flowInstId 流程实例ID
     */
    @ApiOperation(value = "新增变量时需要的令牌选择项", notes = "新增变量时需要的令牌选择项")
    @RequestMapping(value = "/tokens/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listTokens(@PathVariable String flowInstId) {
        List<FlowVariable> flowVariableList = flowEng.listFlowVariables(flowInstId);
        Set<String> existTokenSet = new HashSet<>();
        for (FlowVariable flowVariable : flowVariableList) {
            existTokenSet.add(flowVariable.getRunToken());
        }
        List<NodeInstance> nodeInstList = flowManager.listFlowInstNodes(flowInstId);
        Set<String> tokenSet = new HashSet<>();
        Map<String, String> tokenLvbList = new HashMap<>();
        tokenLvbList.put(" ", "------请选择------");
        for (NodeInstance nodeInst : nodeInstList) {
            if (!existTokenSet.contains(nodeInst.getRunToken()) && !tokenSet.contains(nodeInst.getRunToken())) {
                tokenSet.add(nodeInst.getRunToken());
                tokenLvbList.put(nodeInst.getRunToken(), nodeInst.getRunToken()); // 获取没有使用过的令牌
            }
        }
        return tokenLvbList;
    }

    /*流程实例管理接口
     * 暂挂一个流程实例
     */
    @ApiOperation(value = "暂挂一个流程实例", notes = "暂挂一个流程实例")
    @WrapUpResponseBody
    @RequestMapping(value = "/suspendinst/{wfinstid}", method = RequestMethod.GET)
    public String suspendInstance(@PathVariable String wfinstid, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        String admindesc = request.getParameter("stopDesc");
        flowManager.suspendInstance(wfinstid, mangerUserCode, admindesc);
        return "已暂挂";
    }

    @ApiOperation(value = "编辑流程实例", notes = "编辑流程实例")
    @WrapUpResponseBody
    @PostMapping
    public void updateFlowInst(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        //流程实例ID
        String flowInstId = jsonObject.getString("flowInstId");
        //流程名称
        String flowOptName = jsonObject.getString("flowOptName");
        //流程业务id
        String flowOptTag = jsonObject.getString("flowOptTag");
        String userCode = jsonObject.getString("userCode");
        String unitCode = jsonObject.getString("unitCode");
        flowManager.updateFlowInstOptInfoAndUser(flowInstId, flowOptName, flowOptTag, userCode, unitCode);
    }

    /*
     * 更改机构
     */
    @ApiOperation(value = "更改机构", notes = "更改机构")
    @PutMapping(value = "/changeunit/{wfinstid}/{unitcode}")
    public void changeUnit(@PathVariable String wfinstid, @PathVariable String unitcode, HttpServletResponse response) {
        flowManager.updateFlowInstUnit(wfinstid, unitcode, "admin");
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /*流程实例状态管理api*/

    /*
     * 终止流程实例
     */
    @ApiOperation(value = "终止流程实例", notes = "终止流程实例")
    @RequestMapping(value = "/stopinst/{flowInstId}", method = RequestMethod.GET)
    public void stopInstance(@PathVariable String flowInstId, HttpServletResponse response) {
        flowManager.stopInstance(flowInstId, "admin", "");
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /**
     * 激活流程实例
     *
     * @param flowInstId
     * @param request
     */
    @ApiOperation(value = "激活流程实例", notes = "激活流程实例")
    @WrapUpResponseBody
    @RequestMapping(value = "/activizeinst/{flowInstId}", method = RequestMethod.GET)
    public void activizeInstance(@PathVariable String flowInstId, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        String admindesc = request.getParameter("stopDesc");
        flowManager.activizeInstance(flowInstId, mangerUserCode, admindesc);
    }

    @ApiOperation(value = "暂停流程的一个节点", notes = "暂停流程的一个节点")
    @WrapUpResponseBody
    @RequestMapping(value = "/suspendNodeInst/{nodeInstId}", method = RequestMethod.GET)
    public void suspendNodeInstance(@PathVariable String nodeInstId, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        flowManager.suspendNodeInstance(nodeInstId, mangerUserCode);
    }

    @ApiOperation(value = "激活流程的一个节点", notes = "激活流程的一个节点")
    @WrapUpResponseBody
    @RequestMapping(value = "/activizeNodeInst/{nodeInstId}", method = RequestMethod.GET)
    public void activizeNodeInstance(@PathVariable String nodeInstId, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        flowManager.activizeNodeInstance(nodeInstId, mangerUserCode);
    }

    @ApiOperation(value = "强制流转到下一结点", notes = "强制流转到下一结点")
    @WrapUpResponseBody
    @RequestMapping(value = "/forceCommit/{nodeInstId}", method = RequestMethod.GET)
    public String forceCommit(@PathVariable String nodeInstId, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        return flowManager.forceCommit(nodeInstId, mangerUserCode);
    }
    /*节点状态管理api
     * 1.回滚一个流程节点到上一节点
     * 2.提交，强制一个流程节点前进到下一个节点
     * 3.对一个正在运行的节点实例强制游离
     * 4.针对一个正在运行且被强制游离的节点实例，结束游离状态
     * 5. 针对一个完成的节点实例，创建游离节点
     * 6.唤醒一个暂挂节点实例
     * 7.从这个节点重新运行该流程，包括已经结束的流程
     * 8.暂挂一个节点实例
     * */

    /**
     * 回滚一个流程节点到上一节点
     */
    @ApiOperation(value = "回滚一个流程节点到上一节点", notes = "1.回滚一个流程节点到上一节点2.提交，强制一个流程节点前进到下一个节点3.对一个正在运行的节点实例强制游离6.唤醒一个暂挂节点实例7.从这个节点重新运行该流程，包括已经结束的流程8.暂挂一个节点实例")
    @RequestMapping(value = "/nodestate/{nodeInstId}/{bo}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public NodeInstance changeFlowInstState(@PathVariable String nodeInstId,
                                            HttpServletRequest request, @PathVariable String bo) {
        switch (bo.charAt(0)) {
            case '1':
                flowEng.rollBackNode(nodeInstId, "admin");
                break;//这儿必须有break，不然会继续往后执行的。
            case '2':
                flowManager.forceCommit(nodeInstId, "admin");
                break;
            case '3':
                flowManager.forceDissociateRuning(nodeInstId, "admin");
                break;
            case '6':
                String mangerUserCode = "admin";
                String timeLimit = request.getParameter("timeLimit");

                if (timeLimit != null) {
                    flowManager.activizeInstance(
                        flowEng.getNodeInstById(nodeInstId).getFlowInstId(), timeLimit,
                        mangerUserCode);
                } else {
                    flowManager.activizeNodeInstance(nodeInstId, mangerUserCode);
                }
                break;
            case '7':
                flowManager.resetFlowToThisNode(nodeInstId, "admin");
                break;
            case '8':
                flowManager.suspendNodeInstance(nodeInstId, "admin");
                break;

        }
        return flowEng.getNodeInstById(nodeInstId);
    }

    /*
     * 从这个节点重新运行该流程，包括已经结束的流程
     */
    @ApiOperation(value = "从这个节点重新运行该流程，包括已经结束的流程", notes = "从这个节点重新运行该流程，包括已经结束的流程")
    @RequestMapping(value = "/resetToCurrent/{nodeInstId}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public NodeInstance resetToCurrent(@PathVariable String nodeInstId, HttpServletRequest request) {
        Map<String, Object> params = BaseController.collectRequestParameters(request);
        String managerUser = StringBaseOpt.castObjectToString(params.get("userCode"));
        if (StringUtils.isBlank(managerUser)) {
            managerUser = WebOptUtils.getCurrentUserCode(request);
        }
        return flowManager.resetFlowToThisNode(nodeInstId, managerUser);
    }

    /**
     * 任务列表查询，查询条件可自助添加
     */
    @ApiOperation(value = "任务列表查询，查询条件可自助添加", notes = "任务列表查询，查询条件可自助添加")
    @RequestMapping(value = "/listNodeOpers/{nodeInstId}", method = RequestMethod.GET)
    public void listNodeOpers(@PathVariable String nodeInstId, HttpServletResponse response) {
        NodeInstance nodeInstance = flowEng.getNodeInstById(nodeInstId);
        List<UserTask> objList = new ArrayList<>();
        List<UserTask> innerTask = flowManager.listNodeTasks(nodeInstId);
        if (innerTask != null)
            objList.addAll(innerTask);
        if ("D".equals(nodeInstance.getTaskAssigned())) {
            Map<String, Object> searchColumn = new HashMap<>();
            searchColumn.put("nodeInstId", nodeInstId);
            searchColumn.put("unitCode", nodeInstance.getUnitCode());
            searchColumn.put("userStation", nodeInstance.getRoleCode());
            PageDesc pageDesc = new PageDesc(1, 100);
            List<UserTask> dynamicTask = flowEng.listDynamicTaskByUnitStation(searchColumn, pageDesc);
            objList.addAll(dynamicTask);
        }
        JsonResultUtils.writeSingleDataJson(objList, response);
    }

    /*
     * 返回节点的操作记录，或者日志
     */
    @ApiOperation(value = "返回节点的操作记录，或者日志", notes = "返回节点的操作记录，或者日志")
    @RequestMapping(value = "/viewnode/{nodeInstId}", method = RequestMethod.GET)
    public void viewNodeInstanceInfo(@PathVariable String nodeInstId, HttpServletResponse response) {
        NodeInstance nodeInst = flowEng.getNodeInstById(nodeInstId);
        NodeInfo nodeInfo = flowDef.getNodeInfoById(nodeInst.getNodeId());
        List<UserTask> tasks = flowManager.listNodeTasks(nodeInstId);
        List<? extends OperationLog> logs = flowManager.listNodeActionLogs(nodeInstId);
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("inst", nodeInst);
        resData.addResponseData("node", nodeInfo);
        resData.addResponseData("tasks", tasks);
        resData.addResponseData("logs", logs);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /*
     * 返回节点的操作记录，或者日志
     */
    @ApiOperation(value = "返回节点的操作记录，或者日志", notes = "返回节点的操作记录，或者日志")
    @RequestMapping(value = "/viewflownode/{flowInstId}/{nodeId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONObject viewFlowNodeInfo(@PathVariable String flowInstId, @PathVariable String nodeId) {
        FlowInstance dbobject = flowManager.getFlowInstance(flowInstId);
        if(dbobject==null) {
            throw new ObjectException("找不到对应的流程实例信息：flowInstId=" + flowInstId);
        }
        NodeInfo nodeInfo = flowDef.getNodeInfoById(nodeId);
        JSONObject nodeOptInfo = new JSONObject();
        nodeOptInfo.put("nodename", nodeInfo.getNodeName());
        int nodeInstInd = 0;
        List<NodeInstance> nodeInsts = dbobject.getFlowNodeInstances();
        int nodeCount = nodeInsts.size();
        for (int i=0; i<nodeCount; i++){
            NodeInstance nodeInst  = nodeInsts.get(i);
            if (nodeInst.getNodeId().equals(nodeId)) {
                //暂时保证一个节点保留一条查看信息
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].createtime",
                    DatetimeOpt.convertDatetimeToString(nodeInst.getCreateTime()));
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].unitcode",
                    nodeInst.getUnitCode());
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].unitname",
                    CodeRepositoryUtil.getValue("unitcode", nodeInst.getUnitCode()));
                if ("N".equals(nodeInst.getNodeState()) || "R".equals(nodeInst.getNodeState())) {
                    List<UserTask> tasks = new ArrayList<>();
                    List<UserTask> innerTasks = flowManager.listNodeTasks(nodeInst.getNodeInstId());
                    if (innerTasks != null)
                        tasks.addAll(innerTasks);
                    //暂时添加一个多余判断，解决相关地方手动修改视图，把岗位待办设置成静态待办的问题
                    if (tasks.isEmpty() && "D".equals(nodeInst.getTaskAssigned())) {
                        int page = 1;
                        int limit = 100;
                        PageDesc pageDesc = new PageDesc(page, limit);
                        Map<String, Object> searchColumn = new HashMap<>();
                        searchColumn.put("nodeInstId", nodeInst.getNodeInstId());
                        searchColumn.put("unitCode", nodeInst.getUnitCode());
                        searchColumn.put("userStation", nodeInfo.getRoleCode());
                        List<UserTask> dynamicTask = flowEng.listDynamicTaskByUnitStation(searchColumn, pageDesc);
                        tasks.addAll(dynamicTask);
                    }
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].state", "办理中");
                    int taskInd = 0;
                    for (UserTask task : tasks) {
                        JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].usercode", task.getUserCode());
                        JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].username",
                            CodeRepositoryUtil.getValue("userCode", task.getUserCode()));
                        IUserInfo user = CodeRepositoryUtil.getUserInfoByCode(task.getUserCode());
                        if (user != null) {
                            JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].order", user.getUserOrder());
                        }
                        taskInd++;
                    }
                } else {
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].state",
                        CodeRepositoryUtil.getValue("WFInstType", nodeInst.getNodeState()));
                    //暂时添加当前节点的最后更新人
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].updateuser",
                        CodeRepositoryUtil.getValue("userCode", nodeInst.getLastUpdateUser()));
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].updatetime",
                        DatetimeOpt.convertDatetimeToString(
                            nodeInst.getLastUpdateTime() == null ? nodeInst.getCreateTime() : nodeInst.getLastUpdateTime()));
                }
                nodeInstInd++;
                //nodeInstId=nodeInst.getNodeInstId();
            }
        }
        nodeOptInfo.put("count", nodeInstInd);
        return nodeOptInfo;
    }

    /**
     * 查找所有没有操作用户的节点
     *
     * @return
     */
    @ApiOperation(value = "查找所有没有操作用户的节点", notes = "查找所有没有操作用户的节点")
    @RequestMapping(value = "/nooptnodes", method = RequestMethod.GET)
    public void listNoOptNodes(HttpServletResponse response) {
        List<NodeInstance> nodeList = flowManager.listNodesWithoutOpt();
        JsonResultUtils.writeSingleDataJson(nodeList, response);
    }

    /**
     * 查询某节点实例下的任务信息
     *
     * @return String
     */
    @ApiOperation(value = "查询某节点实例下的任务信息", notes = "查询某节点实例下的任务信息")
    @RequestMapping(value = "/listusertasks/{nodeInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<ActionTask> listNodeInstTasks(@PathVariable String nodeInstId) {
        return flowManager.listNodeInstTasks(nodeInstId);
    }

    //新增工作组
    @ApiOperation(value = "新增工作组", notes = "新增工作组")
    @RequestMapping(value = "/assignFlowWorkTeam/{flowInstId}/{roleCode}/{userCode}/{authdesc}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void assignFlowWorkTeam(@PathVariable String flowInstId, @PathVariable String roleCode,
                                   @PathVariable String userCode, @PathVariable String authdesc) {
        flowEng.assignFlowWorkTeam(flowInstId, roleCode,
            CollectionsOpt.createList(userCode));
    }

    /**
     * 获取流程实例的关注列表
     *
     * @param flowInstId
     */
    @ApiOperation(value = "获取流程实例的关注列表", notes = "获取流程实例的关注列表")
    @RequestMapping(value = "/getAttByFlowInstId/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<InstAttention> getAttByFlowInstId(@PathVariable String flowInstId) {
        return flowEng.viewFlowAttention(flowInstId);
    }

    /**
     * 新增流程关注
     *
     * @param instAttention
     */
    @ApiOperation(value = "新增流程关注", notes = "新增流程关注")
    @WrapUpResponseBody
    @RequestMapping(value = "/addAttention", method = RequestMethod.POST)
    public void addAttention(@RequestBody InstAttention instAttention) {
        flowEng.saveFlowAttention(instAttention);
    }

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     */
    @ApiOperation(value = "将 fromUserCode 所有任务 迁移 给 toUserCode", notes = "将 fromUserCode 所有任务 迁移 给 toUserCode")
    @WrapUpResponseBody
    @RequestMapping(value = "/moveUserTaskTo", method = RequestMethod.POST)
    public void moveUserTaskTo(@RequestBody TaskMove taskMove) {
        flowManager.moveUserTaskTo(taskMove.getFormUser(), taskMove.getToUser(), taskMove.getOperatorUser(),
            taskMove.getMoveDesc());
    }

    @ApiOperation(value = "将 fromUserCode 所有任务 迁移 给 toUserCode", notes = "将 fromUserCode 所有任务 迁移 给 toUserCode")
    @WrapUpResponseBody
    @RequestMapping(value = "/moveSelectedUserTaskTo", method = RequestMethod.POST)
    public void moveSelectedUserTaskTo(@RequestBody TaskMove taskMove) {
        flowManager.moveUserTaskTo(taskMove.getNodeInstIds(), taskMove.getFormUser(), taskMove.getToUser(), taskMove.getOperatorUser(),
            taskMove.getMoveDesc());
    }

    @ApiOperation(value = "根据id获取流程实例节点", notes = "根据id获取流程实例节点")
    @WrapUpResponseBody
    @RequestMapping(value = "/listFlowInstNodes", method = RequestMethod.GET)
    public List<NodeInstance> listFlowInstNodes(String flowInstId) {
        return flowManager.listFlowInstNodes(flowInstId);
    }

    /**
     * 终止一个流程
     * 修改其流程id为负数
     * 更新所有节点状态为F
     * F 强行结束
     */
    @ApiOperation(value = "终止一个流程，修改其流程id为负数，更新所有节点状态为F", notes = "终止一个流程，修改其流程id为负数，更新所有节点状态为F")
    @PostMapping(value = "/stopAndChangeInstance/{flowInstId}/{userCode}")
    @WrapUpResponseBody
    public void stopAndChangeInstance(String flowInstId, String userCode, String desc) {
        flowManager.stopInstance(flowInstId, userCode, desc);
    }

    /**
     * 终止一个流程
     * 更新所有节点状态为F
     * F 强行结束
     */
    @ApiOperation(value = "终止一个流程，更新所有节点状态为F", notes = "终止一个流程，更新所有节点状态为F")
    @PutMapping(value = "/stopInstance/{flowInstId}/{userCode}")
    public void stopInstance(@PathVariable String flowInstId, @PathVariable String userCode, HttpServletResponse response) {
        try {
            flowManager.stopInstance(flowInstId, userCode, "");
            JsonResultUtils.writeSuccessJson(response);
        } catch (Exception e) {
            JsonResultUtils.writeErrorMessageJson(1, "流程无法强行结束", response);

        }
    }

    /**
     * 流程拉回到首节点
     *
     * @param flowInstId 流程id
     * @param userCode   操作人usercode
     */
    @ApiOperation(value = "流程拉回到首节点", notes = "流程拉回到首节点")
    @PutMapping(value = "/reStartFlow/{flowInstId}/{userCode}")
    public void reStartFlow(@PathVariable String flowInstId, @PathVariable String userCode,
                            @RequestParam(required = false, defaultValue = "false") Boolean force, HttpServletResponse response) {
        NodeInstance startNodeInst = flowManager.reStartFlow(flowInstId, userCode, force);
        if (startNodeInst != null) {
            JsonResultUtils.writeSuccessJson(response);
        } else {
            JsonResultUtils.writeErrorMessageJson(1, "流程已经被审批，无法撤回", response);
        }
    }

    /**
     * 查询所有流程分组
     *
     * @return
     */
    @ApiOperation(value = "查询所有流程分组", notes = "查询所有流程分组")
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<FlowInstanceGroup> listFlowInstGroup(PageDesc pageDesc,
                                                                HttpServletRequest request) {
        Map<String, Object> searchColumn = BaseController.collectRequestParameters(request);
        JSONArray listObjects = flowManager.listFlowInstGroup(searchColumn, pageDesc);
        List<FlowInstanceGroup> flowInstanceGroupList = listObjects.toJavaList(FlowInstanceGroup.class);
        return PageQueryResult.createResult(flowInstanceGroupList, pageDesc);
    }

    /*
     * 根据id获取流程分组对象
     */
    @ApiOperation(value = "根据id获取流程分组对象", notes = "根据id获取流程分组对象")
    @RequestMapping(value = "/group/{flowInstGroupId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInstanceGroup getFlowInstanceGroup(@PathVariable String flowInstGroupId) {
        return flowManager.getFlowInstanceGroup(flowInstGroupId);
    }

    /*
     * 查询某节点实例下的日志信息
     */
    /*@ApiOperation(value = "节点操作日志", notes = "查询某节点实例下的日志信息")
    @RequestMapping(value = "/nodelogs/{nodeInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends OperationLog> listNodeInstLogs(@PathVariable String nodeInstId) {
        return flowManager.listNodeActionLogs(nodeInstId);
    }*/

    @ApiOperation(value = "节点操作日志", notes = "查询某节点实例下的日志信息")
    @RequestMapping(value = "/nodelogs/{flowInstId}/{nodeInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends OperationLog> listNodeInstLogs(@PathVariable String flowInstId, @PathVariable String nodeInstId) {
        return flowManager.listNodeActionLogs(flowInstId, nodeInstId);
    }

    /*
     * 流程操作日志
     */
    @ApiOperation(value = "流程操作日志", notes = "流程操作日志")
    @RequestMapping(value = "/flowlogs/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends OperationLog> listFlowInstLogs(@PathVariable String flowInstId, String withNodeLog) {
        return flowManager.listFlowActionLogs(flowInstId,
            BooleanBaseOpt.castObjectToBoolean(withNodeLog, false));
    }

    @ApiOperation(value = "用户操作日志", notes = "用户操作日志")
    @RequestMapping(value = "/userlogs/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends OperationLog> listUserOptLogs(@PathVariable String userCode,
                                                        String lastTime,
                                                        PageDesc pageDesc) {
        return flowManager.listUserActionLogs(userCode,
            GeneralAlgorithm.nvl(DatetimeOpt.castObjectToDate(lastTime),
                DatetimeOpt.addDays(DatetimeOpt.currentUtilDate(), -30)), pageDesc);
    }
}
