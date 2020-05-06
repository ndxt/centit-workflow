package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.*;
import com.centit.workflow.service.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@Api(value = "流程引擎",
    tags = "流程引擎接口类")
@RequestMapping("/flow/engine")
public class FlowEngineController extends BaseController {
    @Autowired
    private FlowEngine flowEngine;
    @Autowired
    private FlowManager flowManager;

    @Autowired
    private FlowDefine flowDefine;

    @Autowired
    private FlowOptService flowOptService;

    @Autowired
    private RoleFormulaService roleFormulaService;

    @ApiOperation(value = "创建流程", notes = "创建流程，参数为json格式")
    @WrapUpResponseBody
    @PostMapping(value = "/createInstance")
    public FlowInstance createFlowInstDefault(@RequestBody CreateFlowOptions newFlowInstanceOptions, HttpServletRequest request) {
        FlowInstance flowInstance =
            flowEngine.createInstance(newFlowInstanceOptions,
                new ObjectUserUnitVariableTranslate(
                    BaseController.collectRequestParameters(request)),null);
        return flowInstance;
    }

    @ApiOperation(value = "提交节点", notes = "提交节点")
    @ApiImplicitParam(name = "json", value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}", paramType = "body", examples = @Example({
        @ExampleProperty(value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}", mediaType = "application/json")
    }))
    @WrapUpResponseBody
    @PostMapping(value = "/submitOpt")
    public List<String> submitOpt(@RequestBody SubmitOptOptions options, HttpServletRequest request) {
        return flowEngine.submitOpt(options, new ObjectUserUnitVariableTranslate(
            BaseController.collectRequestParameters(request)),null);
    }

    @ApiOperation(value = "保存流程变量", notes = "保存流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/saveFlowVariable")
    public void saveFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        List<String> vars = JSON.parseArray(flowVariableParam.getVarValue(), String.class);
        if (!vars.isEmpty())
            flowEngine.saveFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName(), vars);
    }

    @ApiOperation(value = "删除流程变量", notes = "删除流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/deleteFlowVariable")
    public void deleteFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        flowEngine.deleteFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getRunToken(), flowVariableParam.getVarName());
    }

    @ApiOperation(value = "保存流程节点变量", notes = "保存流程节点变量")
    @WrapUpResponseBody
    @PostMapping(value = "/saveFlowNodeVariable")
    public void saveFlowNodeVariable(@RequestBody String FlowNodeVariable) {
        JSONObject jsonObject = JSON.parseObject(FlowNodeVariable);
        String nodeInstId = jsonObject.getString("nodeInstId");
        String varName = jsonObject.getString("varName");
        String varValue = jsonObject.getString("varValue");
        List<String> vars = JSON.parseArray(varValue, String.class);
        flowEngine.saveFlowNodeVariable(nodeInstId, varName, new HashSet<>(vars));
    }

    @ApiOperation(value = "查看流程变量", notes = "查看流程变量")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowVariablesByVarname")
    public List<FlowVariable> viewFlowVariablesByVarname(FlowVariable flowVariableParam) {
        List<FlowVariable> flowVariables = flowEngine.viewFlowVariablesByVarname(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName());
        return flowVariables;
    }

    /**
     * @param flowWorkTeam
     * @ApiOperation(value = "新增多个办件角色", notes = "新增办件角色,userCode传一个Stringlist，格式为userCode:[\"1\",\"2\"]")
     */
    @WrapUpResponseBody
    @PostMapping(value = "/assignFlowWorkTeam")
    public void assignFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        String userCodeList = flowWorkTeam.getUserCode();
        if (StringUtils.isBlank(userCodeList)) {
            return;
        }
        List<String> userCodes = JSON.parseArray(userCodeList, String.class);
        flowEngine.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode(), userCodes);
    }

    @ApiOperation(value = "新增单个办件角色", notes = "新增单个办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "/addFlowWorkTeam")
    public void addFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        flowEngine.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(),
              flowWorkTeam.getRoleCode(), flowWorkTeam.getUserCode());
    }

    @ApiOperation(value = "删除办件角色", notes = "删除办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowWorkTeam")
    public void deleteFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        flowEngine.deleteFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
    }


    @ApiOperation(value = "查看流程节点", notes = "查看流程节点")
    @GetMapping(value = "/listFlowInstNodes")
    @WrapUpResponseBody
    public List<NodeInstance> listFlowInstNodes(String flowInstId) {
        return flowManager.listFlowInstNodes(flowInstId);
    }

    @ApiOperation(value = "查看流程活动节点", notes = "查看流程活动节点")
    @GetMapping(value = "/activeNodes")
    @WrapUpResponseBody
    public List<NodeInstance> listFlowActiveNodes(String flowInstId) {
        return flowManager.listFlowActiveNodes(flowInstId);
    }

    @ApiOperation(value = "查询用户待办", notes = "查询用户待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listUserTasks")
    public PageQueryResult<UserTask> listUserTasks(@RequestParam(value = "userCode") String userCode, PageDesc pageDesc) {
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = flowEngine.listUserTasksByFilter(searchColumn, pageDesc);
        return PageQueryResult.createResultMapDict(userTasks, pageDesc);
    }

    @ApiOperation(value = "根据条件查询待办", notes = "根据条件查询待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listTasks")
    public PageQueryResult<UserTask> listTasks(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<UserTask> userTasks = flowEngine.listUserTasksByFilter(searchColumn, pageDesc);
        return PageQueryResult.createResultMapDict(userTasks, pageDesc);
    }

    @ApiOperation(value = "查询节点待办用户", notes = "查询节点待办用户")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @GetMapping(value = "/nodeTaskUsers")
    public List<UserTask> listNodeTaskUsers(String nodeInstId) {
        return flowEngine.listNodeOperator(nodeInstId);
    }

    @ApiOperation(value = "查询用户岗位待办", notes = "查询用户岗位待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listDynamicTasks")
    public PageQueryResult<UserTask> listUserDynamicTasks(HttpServletRequest request, PageDesc pageDesc ) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<UserTask> userTasks =  flowEngine.listDynamicTask(searchColumn, pageDesc);
        return PageQueryResult.createResultMapDict(userTasks, pageDesc);
    }

    @ApiOperation(value = "业务id关联流程", notes = "根据业务id查询关联流程")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @GetMapping(value = "/listAllFlowInstByOptTag")
    public List<FlowInstance> listAllFlowInstByOptTag(@RequestParam(value = "flowOptTag") String flowOptTag) {
        return flowEngine.listAllFlowInstByOptTag(flowOptTag);
    }

    @ApiOperation(value = "更改流程业务信息", notes = "更改流程业务信息程")
    @WrapUpResponseBody
    @PostMapping(value = "/updateFlowOptInfo")
    public void updateFlowInstOptInfo(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        //流程实例ID
        String flowInstId = jsonObject.getString("flowInstId");
        //流程名称
        String flowOptName = jsonObject.getString("flowOptName");
        //流程业务id
        String flowOptTag = jsonObject.getString("flowOptTag");
        flowEngine.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
    }


    @ApiOperation(value = "更改流程节点参数", notes = "更改流程业务信息程")
    @WrapUpResponseBody
    @PostMapping(value = "/updateNodeParam")
    public void updateNodeInstanceParam(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        //流程实例ID
        String nodeInstId = jsonObject.getString("nodeInstId");
        //流程名称
        String nodeParam = jsonObject.getString("nodeParam");
        flowEngine.updateNodeInstanceParam(nodeInstId, nodeParam);
    }

    @ApiOperation(value = "更改流程节点参数", notes = "更改流程业务信息程")
    @WrapUpResponseBody
    @PostMapping(value = "/lockTask")
    public void lockNodeTask(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String nodeInstId = jsonObject.getString("nodeInstId");
        String userCode = jsonObject.getString("userCode");
        flowEngine.lockNodeTask(nodeInstId, userCode);
    }

    @ApiOperation(value = "查看办件角色", notes = "查看办件角色")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowWorkTeam")
    public List<String> viewFlowWorkTeam(FlowWorkTeam flowWorkTeam) {
        return flowEngine.viewFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
    }

    @ApiOperation(value = "查看流程组织机构", notes = "查看流程组织机构")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowOrganize")
    public List<String> viewFlowOrganize(FlowOrganize flowOrganize) {
        return flowEngine.viewFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }

    /**
     * @param json
     * @ApiOperation(value = "新增多个流程机构", notes = "新增流程组织机构，多个组织orgCodeSet传一个数组或者一个list，格式为userCode:[\"1\",\"2\"]")
     */
    @WrapUpResponseBody
    @PostMapping(value = "/assignFlowOrganize")
    public void assignFlowOrganize(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowInstId = jsonObject.getString("flowInstId");
        String roleCode = jsonObject.getString("roleCode");
        String orgCodeSet = jsonObject.getString("orgCodeSet");
        List<String> orgCodes = JSON.parseArray(orgCodeSet, String.class);
        flowEngine.assignFlowOrganize(flowInstId, roleCode, orgCodes);
    }

    @ApiOperation(value = "新增单个流程机构", notes = "新增流程组织机构")
    @WrapUpResponseBody
    @PostMapping(value = "/addFlowOrganize")
    public void addFlowOrganize(@RequestBody FlowOrganizeId flowOrganize) {
        flowEngine.assignFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode(), flowOrganize.getUnitCode());
    }

    @ApiOperation(value = "删除流程机构", notes = "删除流程机构")
    @WrapUpResponseBody
    @PostMapping(value = "/deleteFlowOrganize")
    public void deleteFlowOrganize(@RequestBody FlowOrganizeId flowOrganize) {
        flowEngine.deleteFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }

    @ApiOperation(value = "获取流程中可以创建的节点", notes = "获取流程中可以创建的节点")
    @WrapUpResponseBody
    @GetMapping(value = "/nodeForCreate/{flowInstId}")
    public Map<String, String> listFlowNodeForCreate(@PathVariable String flowInstId) {
        return flowEngine.listFlowNodeForCreate(flowInstId);
    }

    @ApiOperation(value = "创建流程节点", notes = "创建流程节点")
    @WrapUpResponseBody
    @PostMapping(value = "/isolatedNode")
    public NodeInstance createIsolatedNodeInst(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowInstId = jsonObject.getString("flowInstId");
        String curNodeInstId = jsonObject.getString("curNodeInstId");
        String createUser = jsonObject.getString("createUser");
        String userCode = jsonObject.getString("userCode");
        String nodeCode = jsonObject.getString("nodeCode");
        String unitCode = jsonObject.getString("unitCode");
        return /*NodeInstance =*/ flowEngine.createIsolatedNodeInst(flowInstId, curNodeInstId, createUser,
            nodeCode, userCode, unitCode);
    }

    @ApiOperation(value = "创建流程节点", notes = "创建流程节点")
    @WrapUpResponseBody
    @PostMapping(value = "/prepNode")
    public NodeInstance createPrepNodeInst(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowInstId = jsonObject.getString("flowInstId");
        String curNodeInstId = jsonObject.getString("curNodeInstId");
        String createUser = jsonObject.getString("createUser");
        String userCode = jsonObject.getString("userCode");
        String nodeCode = jsonObject.getString("nodeCode");
        String unitCode = jsonObject.getString("unitCode");
        return /*NodeInstance =*/ flowEngine.createPrepNodeInst(flowInstId, curNodeInstId, createUser,
            nodeCode, userCode, unitCode);
    }

    @ApiOperation(value = "回退节点", notes = "回退节点")
    @WrapUpResponseBody
    @PostMapping(value = "/rollBackNode")
    public String rollBackNode(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String nodeInstId = jsonObject.getString("nodeInstId");
        String managerUserCode = jsonObject.getString("managerUserCode");
        return flowEngine.rollBackNode(nodeInstId, managerUserCode);
    }

    @ApiOperation(value = "回退节点", notes = "回退节点")
    @WrapUpResponseBody(contentType = WrapUpContentType.RAW)
    @GetMapping(value = "nodeCanBeReclaim")
    public Boolean checkNodeCanBeReclaim(@PathVariable String nodeInstId) {
        return flowEngine.nodeCanBeReclaim(nodeInstId);
    }

    @ApiOperation(value = "创建流程实例分组", notes = "创建流程实例分组")
    @WrapUpResponseBody
    @PostMapping(value = "/flowGroup")
    public FlowInstanceGroup createFlowInstGroup(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowGroupName = jsonObject.getString("flowGroupName");
        String flowGroupDesc = jsonObject.getString("flowGroupDesc");
        FlowInstanceGroup flowInstanceGroup = flowEngine.createFlowInstGroup(flowGroupName, flowGroupDesc);
        return flowInstanceGroup;
    }

    @ApiOperation(value = "查询流程实例分组", notes = "查询流程实例分组")
    @WrapUpResponseBody
    @GetMapping(value = "/flowGroup")
    public PageQueryResult<FlowInstanceGroup> listFlowInstGroup(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<FlowInstanceGroup> listObjects = flowEngine.listFlowInstGroup(searchColumn, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    /**
     * 获取流程实例信息
     *
     * @param flowInstId 实例id
     * @return 实例信息
     */
    @ApiOperation(value = "获取流程实例信息", notes = "获取流程实例信息")
    @WrapUpResponseBody
    @GetMapping(value = "/inst/{flowInstId}")
    public FlowInstance getFlowInstance(String flowInstId) {
        return flowEngine.getFlowInstById(flowInstId);
    }

    /**
     * 获取流程定义信息
     *
     * @param flowInstId 实例id
     * @return 流程定义信息
     */
    @ApiOperation(value = "获取流程定义信息", notes = "获取流程定义信息")
    @WrapUpResponseBody
    @GetMapping(value = "/instDef/{flowInstId}")
    public FlowInfo getFlowDefine(String flowInstId) {
        FlowInstance instance = flowEngine.getFlowInstById(flowInstId);
        if(instance==null){
            return null;
        }
        return flowDefine.getFlowInfo(instance.getFlowCode(),
            instance.getVersion());
    }

    /**
     * 获取流程业务信息
     * @param flowInstId 实例id
     * @return 流程业务信息
     */
    @ApiOperation(value = "获取流程定义信息", notes = "获取流程定义信息")
    @WrapUpResponseBody
    @GetMapping(value = "/optInfo/{flowInstId}")
    public FlowOptInfo getFlowOptInfo(String flowInstId) {
        FlowInfo flowInfo = getFlowDefine(flowInstId);
        if(flowInfo==null){
            return null;
        }
        return flowOptService.getFlowOptInfoById(flowInfo.getOptId());
    }

    /**
     * 获取节点实例信息
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    @ApiOperation(value = "获取节点实例信息", notes = "获取节点实例信息")
    @WrapUpResponseBody
    @GetMapping(value = "/nodeInst/{nodeInstId}")
    public NodeInstance getNodeInstance(String nodeInstId) {
        return flowEngine.getNodeInstById(nodeInstId);
    }

    /**
     * 获取节点定义信息
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    @ApiOperation(value = "获取节点实例信息", notes = "获取节点实例信息")
    @WrapUpResponseBody
    @GetMapping(value = "/nodeDef/{nodeInstId}")
    public NodeInfo getNodeInfo(String nodeInstId) {
        NodeInstance inst = flowEngine.getNodeInstById(nodeInstId);
        if(inst==null){
            return null;
        }
        return flowDefine.getNodeInfoById(inst.getNodeId());
    }

    @ApiOperation(value = "查询流程办件角色对应的用户范围，返回空表示可以选择任意人员", notes = "查询流程办件角色对应的用户范围")
    @WrapUpResponseBody
    @ApiImplicitParams({@ApiImplicitParam(
        name = "flowInstId", value="流程实例id",
        required = true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "itemRoleCode", value="办件角色代码",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value="/itemRoleFilter/{flowInstId}/{itemRoleCode}",method = RequestMethod.GET)
    public JSONArray viewRoleFormulaUsers(@PathVariable String flowInstId,
                                          @PathVariable String itemRoleCode,
                                          HttpServletRequest request){

        FlowInstance instance = flowEngine.getFlowInstById(flowInstId);
        if(instance==null){
            return null;
        }
        FlowTeamRole itemRole = flowDefine.getFlowItemRole(instance.getFlowCode(),
            instance.getVersion(), itemRoleCode);
        if(StringUtils.isBlank(itemRole.getFormulaCode())){
            return null;
        }
        return roleFormulaService.viewRoleFormulaUsers(
            itemRole.getFormulaCode(),
            WebOptUtils.getCurrentUserCode(request),
            WebOptUtils.getCurrentUnitCode(request));
    }

    @ApiOperation(value = "根据条件查询已办", notes = "根据条件查询已办")
    @WrapUpResponseBody
    @GetMapping(value = "/listCompleteTasks")
    public PageQueryResult<UserTask> listCompleteTasks(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<UserTask> userTasks = flowEngine.listUserCompleteTasks(searchColumn, pageDesc);
        return PageQueryResult.createResultMapDict(userTasks, pageDesc);
    }
}
