package com.centit.workflow.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.support.algorithm.*;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JSONOpt;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
    private FlowEngine flowEngine;
    @Autowired
    private FlowDefine flowDefine;

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

    @ApiOperation(value = "查看流程实例流程图", notes = "查看流程实例流程图")
    @RequestMapping(value = "/viewxml/{flowInstId}", method = RequestMethod.GET)
    public void viewRuntimeXml(@PathVariable String flowInstId, HttpServletResponse response) {
        FlowInstance flowInst = flowManager.getFlowInstance(flowInstId);
        String flowCode = flowInst.getFlowCode();
        Long version = flowInst.getVersion();
        if (StringUtils.isNotBlank(flowCode)) {
            FlowInfo obj = flowDefine.getFlowInfo(flowCode, version);
            String wfDefXML = obj.getFlowXmlDesc();

            Map<String, Object> result = new HashMap<>();
            Map<String, Object> flowInstDesc = flowManager.viewFlowInstance(flowInstId);
            result.put("json", wfDefXML);
            result.put("viewJson", flowInstDesc);
            JsonResultUtils.writeSingleDataJson(result, response);
        }
    }

    @ApiOperation(value = "查看流程图", notes = "查看流程图")
    @RequestMapping(value = "/viewxml/{flowCode}/{version}", method = RequestMethod.GET)
    public void viewRuntimeXml(@PathVariable String flowCode, @PathVariable Long version, HttpServletRequest request, HttpServletResponse response) {
        FlowInfo obj = flowDefine.getFlowInfo(flowCode, version);
        String xml = obj.getFlowXmlDesc();
        HashMap<String, String> result = new HashMap<>();
        result.put("xml", xml);
        JsonResultUtils.writeSingleDataJson(result, response);
    }

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
     * flowEng.viewFlowOrganize(flowInstId)要是直接返回unitInfo更方便
     * @param flowInstId 流程节点实例
     * @param pageDesc 分页参数
     * @return PageQueryResult 分页查询结果
     */
    @ApiOperation(value = "获取组织机构列表", notes = "获取组织机构列表")
    @RequestMapping(value = "/getorglist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Map<String, String>> getOrganizeList(@PathVariable String flowInstId, PageDesc pageDesc) {
        Map<String, List<String>> organizeMap = flowEngine.viewFlowOrganize(flowInstId);
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
    public void deleteOrg(@PathVariable String flowInstId, @PathVariable String roleCode, @PathVariable String unitCode,
                          HttpServletResponse response) {
        flowEngine.deleteFlowOrganize(flowInstId, roleCode, unitCode);
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
        flowEngine.deleteFlowOrganize(flowInstId, roleCode);
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
        flowEngine.assignFlowOrganize(flowInstId, roleCode, unitCode, authDesc);
        JsonResultUtils.writeSingleDataJson("", response);

    }

    /**
     * 给一个节点指定任务、用这个代替系统自动分配任务
     */
    @ApiOperation(value = "给一个节点指定任务、用这个代替系统自动分配任务", notes = "给一个节点指定任务、用这个代替系统自动分配任务")
    @RequestMapping(value = "/assign/{nodeInstId}/{userCode}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void assignNodeUser(@PathVariable String nodeInstId, @PathVariable String userCode,
           @RequestBody UserTask actionTask ) {
        flowManager.assignNodeTask(nodeInstId,
            actionTask.getUserCode(), StringUtils.isBlank(userCode) ? "admin" : userCode, actionTask.getAuthDesc());
    }



    /*tab：办件角色管理*/

    /**
     * 查询办件角色列表
     *
     * @param flowInstId 查询办件角色列表
     * @param pageDesc PageDesc
     */
    @ApiOperation(value = "查询办件角色列表", notes = "查询办件角色列表")
    @RequestMapping(value = "/getteamlist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Map<String, String>> getTeamList(@PathVariable String flowInstId, PageDesc pageDesc) {
        List<FlowWorkTeam> teamMap = flowEngine.viewFlowWorkTeam(flowInstId);
        List<Map<String, String>> teamList = new ArrayList<>();
        for (FlowWorkTeam entry : teamMap) {
            Map<String, String> teamTempMap = new HashMap<>();
            teamTempMap.put("roleCode", entry.getRoleCode());
            teamTempMap.put("userCode", entry.getUserCode());
            teamTempMap.put("runToken", entry.getRunToken());
            teamTempMap.put("userName", CodeRepositoryUtil.getValue("userCode", entry.getUserCode()));
            teamList.add(teamTempMap);
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
    public void deleteWorkTeam(@PathVariable String flowInstId, @PathVariable String roleCode, HttpServletResponse response) {
        flowEngine.deleteFlowWorkTeam(flowInstId, roleCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /**
     * 删除指定的流程工作小组
     *
     * @return
     */
    @ApiOperation(value = "删除指定的流程工作小组", notes = "删除指定的流程工作小组")
    @RequestMapping(value = "/deleteteam/{flowInstId}/{roleCode}/{userCode}", method = RequestMethod.GET)
    public void deleteWorkTeamUser(@PathVariable String flowInstId, @PathVariable String roleCode,
                                   @PathVariable String userCode, HttpServletResponse response) {
        flowEngine.deleteFlowWorkTeam(flowInstId, roleCode, userCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }


    /*tab：流程变量管理*/

    /**
     * 查询变量列表
     *
     * @param flowInstId 查询变量列表
     * @param pageDesc PageDesc
     */
    @ApiOperation(value = "查询变量列表", notes = "查询变量列表")
    @RequestMapping(value = "/getvariablelist/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<FlowVariable> getVariableList(@PathVariable String flowInstId, PageDesc pageDesc) {
        List<FlowVariable> variableList = flowEngine.listFlowVariables(flowInstId);
        pageDesc.setTotalRows(variableList.size());
        return PageQueryResult.createResult(variableList, pageDesc);
    }


    /**
     * 保存流程变量
     */
    @ApiOperation(value = "保存流程变量", notes = "保存流程变量")
    @RequestMapping(value = "/savevariable/{flowInstId}/{varName}/{varValue}", method = RequestMethod.GET)
    public void saveVariable(@PathVariable String flowInstId, @PathVariable String varName, @PathVariable String varValue,
                             HttpServletRequest request, HttpServletResponse response) {
        String runToken = request.getParameter("runToken");
        flowEngine.saveFlowNodeVariable(flowInstId, runToken, varName, StringUtils.isBlank(varValue) ? null : varValue);
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
        List<FlowVariable> flowVariableList = flowEngine.listFlowVariables(flowInstId);
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

    @ApiOperation(value = "更改机构", notes = "更改机构")
    @PutMapping(value = "/changeunit/{wfinstid}/{unitcode}")
    public void changeUnit(@PathVariable String wfinstid, @PathVariable String unitcode, HttpServletResponse response) {
        flowManager.updateFlowInstUnit(wfinstid, unitcode, "admin");
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /*流程实例状态管理api*/
    @ApiOperation(value = "终止流程实例", notes = "终止流程实例")
    @RequestMapping(value = "/stopinst/{flowInstId}", method = RequestMethod.GET)
    public void stopInstance(@PathVariable String flowInstId, HttpServletRequest request, HttpServletResponse response) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        flowManager.stopInstance(flowInstId, mangerUserCode, "");
        JsonResultUtils.writeSingleDataJson("", response);
    }

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

    @ApiOperation(value = "强制修改流程的节点状态", notes = "强制修改流程的节点状态")
    @WrapUpResponseBody
    @PutMapping(value = "/updateNodeState/{nodeInstId}/{newState}")
    public void updateNodeState(@PathVariable String nodeInstId, @PathVariable String newState) {
        flowManager.updateNodeState(nodeInstId, newState);
    }

    @ApiOperation(value = "强制流转到下一节点", notes = "强制流转到下一节点")
    @WrapUpResponseBody
    @RequestMapping(value = "/forceCommit/{nodeInstId}", method = RequestMethod.GET)
    public String forceCommit(@PathVariable String nodeInstId, HttpServletRequest request) {
        String mangerUserCode = request.getParameter("admin");
        if (StringUtils.isBlank(mangerUserCode)) {
            mangerUserCode = "admin";
        }
        return flowManager.forceCommit(nodeInstId, mangerUserCode);
    }

    /**
     * 节点状态管理api
     * 1.回滚一个流程节点到上一节点
     * 2.提交，强制一个流程节点前进到下一个节点
     * 3.对一个正在运行的节点实例强制游离
     * 4.针对一个正在运行且被强制游离的节点实例，结束游离状态
     * 5.针对一个完成的节点实例，创建游离节点
     * 6.唤醒一个暂挂节点实例
     * 7.从这个节点重新运行该流程，包括已经结束的流程
     * 8.暂挂一个节点实例
     */
    @ApiOperation(value = "节点状态管理api", notes = "1.回滚一个流程节点到上一节点2.提交，强制一个流程节点前进到下一个节点3.对一个正在运行的节点实例强制游离6.唤醒一个暂挂节点实例7.从这个节点重新运行该流程，包括已经结束的流程8.暂挂一个节点实例")
    @RequestMapping(value = "/nodestate/{nodeInstId}/{bo}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public NodeInstance changeFlowInstState(@PathVariable String nodeInstId,
                                            HttpServletRequest request, @PathVariable String bo) {
        switch (bo.charAt(0)) {
            case '1':
                flowEngine.rollBackNode(nodeInstId, "admin");
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
                        flowEngine.getNodeInstById(nodeInstId).getFlowInstId(), timeLimit,
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
        return flowEngine.getNodeInstById(nodeInstId);
    }

    /**
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
    public void listNodeOperators(@PathVariable String nodeInstId, HttpServletResponse response) {
        List<UserTask> objList =  flowEngine.listNodeOperators(nodeInstId);
        JsonResultUtils.writeSingleDataJson(objList, response);
    }

    @ApiOperation(value = "返回节点的操作记录，或者日志", notes = "返回节点的操作记录，或者日志")
    @RequestMapping(value = "/viewnode/{nodeInstId}", method = RequestMethod.GET)
    public void viewNodeInstanceInfo(@PathVariable String nodeInstId, HttpServletResponse response) {
        NodeInstance nodeInst = flowEngine.getNodeInstById(nodeInstId);
        NodeInfo nodeInfo = flowDefine.getNodeInfoById(nodeInst.getNodeId());
        List<UserTask> tasks = flowEngine.listNodeOperators(nodeInstId);
        List<? extends OperationLog> logs = flowManager.listNodeActionLogs(nodeInstId);
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("inst", nodeInst);
        resData.addResponseData("node", nodeInfo);
        resData.addResponseData("tasks", tasks);
        resData.addResponseData("logs", logs);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @ApiOperation(value = "返回节点的操作记录，或者日志", notes = "返回节点的操作记录，或者日志")
    @RequestMapping(value = "/viewflownode/{flowInstId}/{nodeId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONObject viewFlowNodeInfo(@PathVariable String flowInstId,
                                       @PathVariable String nodeId, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        String localLang = WebOptUtils.getCurrentLang(request);
        FlowInstance dbobject = flowManager.getFlowInstance(flowInstId);
        if (dbobject == null) {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                    getI18nMessage("error.604.object_not_found", request, "FlowInstance", flowInstId));
                // "找不到对应的流程实例信息：flowInstId=" + flowInstId);
        }
        NodeInfo nodeInfo = flowDefine.getNodeInfoById(nodeId);
        JSONObject nodeOptInfo = new JSONObject();
        nodeOptInfo.put("nodename", nodeInfo.getNodeName());
        int nodeInstInd = 0;
        List<NodeInstance> nodeInsts = dbobject.getFlowNodeInstances();
        int nodeCount = nodeInsts.size();
        for (int i = 0; i < nodeCount; i++) {
            NodeInstance nodeInst = nodeInsts.get(i);
            if (nodeInst.getNodeId().equals(nodeId)) {
                //暂时保证一个节点保留一条查看信息
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].createtime",
                    DatetimeOpt.convertDatetimeToString(nodeInst.getCreateTime()));
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].unitcode",
                    nodeInst.getUnitCode());
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].unitname",
                    CodeRepositoryUtil.getValue(CodeRepositoryUtil.UNIT_CODE, nodeInst.getUnitCode(), topUnit, localLang));
                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].taskAssign", nodeInst.getTaskAssigned());

                if (NodeInstance.NODE_STATE_NORMAL.equals(nodeInst.getNodeState()) || NodeInstance.NODE_STATE_PAUSE.equals(nodeInst.getNodeState())) {
                    List<UserTask> tasks = flowEngine.listNodeOperators(nodeInst.getNodeInstId());
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].state", "办理中");
                    if(NodeInfo.OPT_RUN_TYPE_DYNAMIC.equals(nodeInfo.getOptRunType())) {

                        JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].rolecode", nodeInst.getRoleCode());
                        JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].rolename",
                            CodeRepositoryUtil.getValue(CodeRepositoryUtil.ROLE_CODE, nodeInst.getRoleCode(), topUnit, localLang));

                        if (tasks != null) {
                            StringBuilder sbUsers = new StringBuilder();
                            for (UserTask task : tasks) {
                                UserInfo user = CodeRepositoryUtil.getUserInfoByCode(topUnit, task.getUserCode());
                                if (user != null) {
                                    sbUsers.append(user.getUserName()).append("(").append(user.getLoginName()).append(") ");
                                }
                            }
                            JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].users", sbUsers);
                        }
                    } else{
                        int taskInd = 0;
                        if (tasks != null) {
                            for (UserTask task : tasks) {
                                UserInfo user = CodeRepositoryUtil.getUserInfoByCode(topUnit, task.getUserCode());
                                JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].usercode", task.getUserCode());

                                if (user != null) {
                                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].order", user.getUserOrder());
                                    StringBuilder sbUsers = new StringBuilder();
                                    sbUsers.append(user.getUserName()).append("(").append(user.getLoginName()).append(") ");
                                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].username", sbUsers);
                                } else {
                                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].task[" + taskInd + "].username",
                                        CodeRepositoryUtil.getValue("userCode", task.getUserCode(), topUnit, localLang));
                                }
                                taskInd++;
                            }
                        }
                    }
                } else {
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].state",
                        CodeRepositoryUtil.getValue("WFInstType", nodeInst.getNodeState(), topUnit, localLang));
                    //暂时添加当前节点的最后更新人
                    JSONOpt.setAttribute(nodeOptInfo, "instance[" + nodeInstInd + "].updateuser",
                        CodeRepositoryUtil.getValue("userCode", nodeInst.getLastUpdateUser(), topUnit, localLang));
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


    //新增工作组
    @ApiOperation(value = "新增工作组", notes = "新增工作组")
    @RequestMapping(value = "/assignFlowWorkTeam/{flowInstId}/{roleCode}/{userCode}/{authdesc}",
        method = RequestMethod.POST)
    @WrapUpResponseBody
    public void assignFlowWorkTeam(@PathVariable String flowInstId, @PathVariable String roleCode,
                                   @PathVariable String userCode, @PathVariable String authdesc) {
        flowEngine.assignFlowWorkTeam(flowInstId, roleCode,
            CollectionsOpt.createList(userCode));
    }

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     */
    @ApiOperation(value = "将 fromUserCode 所有任务 迁移 给 toUserCode", notes = "将 fromUserCode 所有任务 迁移 给 toUserCode")
    @WrapUpResponseBody
    @RequestMapping(value = "/moveUserTaskTo", method = RequestMethod.POST)
    public void moveUserTaskTo(@RequestBody TaskMove taskMove, HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        flowManager.moveUserTaskTo( WebOptUtils.getCurrentTopUnit(request),
            taskMove.getFormUser(),
            taskMove.getToUser(),
            userInfo.getUserCode(), //taskMove.getOperatorUser(),
            taskMove.getMoveDesc());
    }

    @ApiOperation(value = "将 fromUserCode 所有任务 迁移 给 toUserCode", notes = "将 fromUserCode 所有任务 迁移 给 toUserCode")
    @WrapUpResponseBody
    @RequestMapping(value = "/moveSelectedUserTaskTo", method = RequestMethod.POST)
    public void moveSelectedUserTaskTo(@RequestBody TaskMove taskMove, HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        flowManager.moveUserTaskTo(taskMove.getNodeInstIds(),
            taskMove.getFormUser(),
            taskMove.getToUser(),
            userInfo.getUserCode(), //taskMove.getOperatorUser(),
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
    @WrapUpResponseBody
    public void stopInstance(@PathVariable String flowInstId, @PathVariable String userCode, HttpServletRequest request) {
        try {
            flowManager.stopInstance(flowInstId, userCode, "");
        } catch (Exception e) {
            throw new ObjectException(WorkflowException.IncorrectNodeState,
                getI18nMessage("flow.654.flow_cant_stop", request));
               // 1, "流程无法强行结束", response);
        }
    }

    @ApiOperation(value = "强制修改流程状态以及相关节点实例状态", notes = "强制修改流程状态以及相关节点实例状态")
    @PostMapping(value = "/updateFlowState")
    @WrapUpResponseBody
    public ResponseData updateFlowState(@RequestBody JSONObject jsonObject) {
        String flowInstId = jsonObject.getString("flowInstId");
        JSONArray flowInstIds = jsonObject.getJSONArray("flowInstIds");
        String userCode = jsonObject.getString("userCode");
        String instState = jsonObject.getString("instState");
        String desc = jsonObject.getString("desc");
        // 判断是否需要批量修改
        if (flowInstIds != null && !flowInstIds.isEmpty()) {
            for (int i = 0; i < flowInstIds.size(); i++) {
                flowManager.updateFlowState(flowInstIds.getString(i), userCode, instState, desc);
            }
            return ResponseData.makeResponseData(flowInstIds);
        } else {
            flowManager.updateFlowState(flowInstId, userCode, instState, desc);
            return ResponseData.makeResponseData(flowInstId);

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
    @WrapUpResponseBody
    public void reStartFlow(@PathVariable String flowInstId, @PathVariable String userCode,
                            @RequestParam(required = false, defaultValue = "false") Boolean force,
                            HttpServletRequest request) {
        NodeInstance startNodeInst = flowManager.reStartFlow(flowInstId, userCode, force);
        if (startNodeInst == null) {
           throw new ObjectException(WorkflowException.WithoutPermission,
                getI18nMessage("flow.656.flow_cant_restart", request));
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

    /**
     * 流程操作日志
     */
    @ApiOperation(value = "流程操作日志", notes = "流程操作日志")
    @RequestMapping(value = "/flowlogs/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData listFlowInstLogs(@PathVariable String flowInstId, String withNodeLog) {
        List<? extends OperationLog> operationLogs = flowManager.listFlowActionLogs(flowInstId,
            BooleanBaseOpt.castObjectToBoolean(withNodeLog, false));
        if (CollectionUtils.sizeIsEmpty(operationLogs)){
            return ResponseData.makeResponseData(Collections.emptyList());
        }
       return ResponseData.makeResponseData(DictionaryMapUtils.objectsToJSONArray(operationLogs));
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

    @ApiOperation(value = "获取流程申请信息", notes = "获取流程申请信息（获取流程创建用户的信息）")
    @RequestMapping(value = "/inst/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData listUserOptLogs(@PathVariable String flowInstId) {
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        FlowInstance flowInstance = flowManager.getFlowInstance(flowInstId);
        UserInfo createFlowUser = CodeRepositoryUtil.getUserInfoByCode(topUnit, flowInstance.getUserCode());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("createFlowUser", createFlowUser);
        resultMap.put("flowOptName", flowInstance.getFlowOptName());
        resultMap.put("flowInstId", flowInstance.getFlowInstId());
        return ResponseData.makeResponseData(resultMap);
    }

    @ApiOperation(value = "删除流程实例数据", notes = "删除流程实例数据")
    @WrapUpResponseBody
    @RequestMapping(
        value = {"/deleteFlowInstById/{flowInstId}/{userCode}"},
        method = {RequestMethod.DELETE}
    )
    public ResponseData deleteFlowInstById(@PathVariable String flowInstId, @PathVariable String userCode) {
        boolean b = flowManager.deleteFlowInstById(flowInstId, userCode);
        return ResponseData.makeResponseData(b);
    }

    @ApiOperation(value = "批量删除流程实例数据", notes = "批量删除流程实例数据,多个id之间用逗号隔开")
    @WrapUpResponseBody
    @RequestMapping(
        value = {"/batchDeleteFlowInst"},
        method = {RequestMethod.DELETE}
    )
    public ResponseData batchDeleteFlowInst(HttpServletRequest request) {
        String flowInstIds = MapUtils.getString(collectRequestParameters(request), "flowInstIds");
        if (StringUtils.isBlank(flowInstIds)){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_FIELD_INPUT_NOT_VALID,
                getI18nMessage("error.701.field_is_blank", request, "flowCode,flowName"));
        }
        flowManager.deleteFlowInstByIds(CollectionsOpt.arrayToList(flowInstIds.split(",")));
        return ResponseData.makeSuccessResponse();
    }

    @ApiOperation(value = "获取流程实例列表，并查询流程相关信息(fgw收文办结列表和发文办结列表)",
        notes = "获取流程实例列表，并查询流程相关信息(fgw收文办结列表和发文办结列表)")
    @WrapUpResponseBody
    @GetMapping(value = "/listFlowInstDetailed")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userCode", value = "流程创建用户"),
        @ApiImplicitParam(name = "flowInstIds", value = "流程实例id，以逗号分割"),
        @ApiImplicitParam(name = "flowCode", value = "流程编码"),
        @ApiImplicitParam(name = "optId", value = "业务编码"),
        @ApiImplicitParam(name = "instState", value = "流程状态 C 已完成  N 办理中"),
        @ApiImplicitParam(name = "flowOptName", value = "流程实例对应的业务名称(like)")
    })
    public PageQueryResult<Object> listFlowInstDetailed(PageDesc pageDesc,
                                                        HttpServletRequest request) {
        Map<String, Object> searchColumn = BaseController.collectRequestParameters(request);
        JSONArray listObjects = flowManager.listFlowInstDetailed(searchColumn, pageDesc);
        return PageQueryResult.createJSONArrayResult(listObjects, pageDesc, FlowInstance.class);
    }

    @ApiOperation(value = "获取节点实例列表", notes = "获取节点实例列表")
    @WrapUpResponseBody
    @GetMapping(value = "/listNodeInstance")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "roleCode", value = "角色代码"),
        @ApiImplicitParam(name = "flowCode", value = "流程编码"),
        @ApiImplicitParam(name = "instState", value = "流程状态 C 已完成  N 办理中"),
        @ApiImplicitParam(name = "flowInstId", value = "流程实例id"),
        @ApiImplicitParam(name = "userCode", value = "用户编码")
    })
    public PageQueryResult<NodeInstance> listNodeInstance(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<NodeInstance> nodeInstanceList = flowManager.listNodeInstance(searchColumn, pageDesc);
        return PageQueryResult.createResultMapDict(nodeInstanceList, pageDesc);
    }

    @ApiOperation(value = "暂停流程计时", notes = "暂停流程计时")
    @RequestMapping(value = "/suspendFlowInstTimer/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public void suspendFlowInstTimer(@PathVariable String flowInstId,HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        String userCode = userInfo.getUserCode();// MapUtils.getString(parameters,"userCode","admin");
        flowManager.suspendFlowInstTimer(flowInstId, userCode);
        //JsonResultUtils.writeSingleDataJson("暂停节点计时成功", response);
    }

    @ApiOperation(value = "唤醒流程计时", notes = "唤醒流程计时")
    @RequestMapping(value = "/activizeFlowInstTimer/{flowInstId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public void activizeFlowInstTimer(@PathVariable String flowInstId,HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        String userCode = userInfo.getUserCode();
        flowManager.activizeFlowInstTimer(flowInstId, userCode);
        //JsonResultUtils.writeSingleDataJson("唤醒流程计时成功", response);
    }

    @ApiOperation(value = "迁移流程版本", notes = "获取流程实例列表")
    @RequestMapping(value = "/upgrade/{flowCode}", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public void upgradeFlowVersion(@PathVariable String flowCode,
                                   @RequestBody JSONObject versionDesc,
                                   HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        long newVersion = -1;
        long oldVersion = -1;
        if(versionDesc!=null){
            newVersion = NumberBaseOpt.castObjectToLong(versionDesc.get("newVersion"), -1l);
            oldVersion = NumberBaseOpt.castObjectToLong(versionDesc.get("oldVersion"), -1l);
        }
        flowManager.upgradeFlowVersion(flowCode, newVersion, oldVersion, userInfo.getUserCode());
    }

}
