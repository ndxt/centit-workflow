package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.FlowOptService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@Api(value = "流程引擎",
    tags = "流程引擎接口类")
@RequestMapping("/flow/engine")
public class FlowEngineController extends BaseController {
    @Resource
    private FlowEngine flowEngine;
    @Resource
    private FlowManager flowManager;

    @Resource
    private FlowDefine flowDefine;

    @Resource
    private FlowOptService wfOptService;


    private Map<Class<?>, String[]> excludes;

    @ApiOperation(value = "创建流程并提交", notes = "参数为json格式，包含指定下一步操作人员得list")
    @PostMapping("/createAndSubmitFlow")
    public void createAndSubmitFlow(@RequestBody CreateFlowOptions newFlowInstanceOptions,
                                    HttpServletRequest request, HttpServletResponse response) {
        //List<String> vars = JSON.parseArray(newFlowInstanceOptions.getUserList(), String.class);
        //创建流程
        FlowInstance flowInstance = flowEngine.createInstance(newFlowInstanceOptions,
            new ObjectUserUnitVariableTranslate(BaseController.collectRequestParameters(request)), null);
        //找到创建人得审批角色级别

        //把这个审批角色级别固化到变量createrLevel
        //flowEngine.saveFlowVariable(flowInstance.getFlowInstId(),"createrLevel","");
        //提交节点
        Set<String> nextNodes = flowEngine.submitOpt(
            SubmitOptOptions.create().nodeInst(flowInstance.getFirstNodeInstance().getNodeInstId())
                .user(newFlowInstanceOptions.getUserCode())
                .unit(newFlowInstanceOptions.getUnitCode()));
        //更新操作人
        for (String n : nextNodes) {
            flowManager.deleteNodeActionTasks(n, flowInstance.getFlowInstId(), newFlowInstanceOptions.getUserCode());
            //for (String v : vars) {
            flowManager.assignNodeTask(n, newFlowInstanceOptions.getWorkUserCode(),
                newFlowInstanceOptions.getUserCode(),  "手动指定审批人");
            //}
        }
        JsonResultUtils.writeSingleDataJson(flowInstance, response);

    }


    @ApiOperation(value = "自定义表单创建流程并提交", notes = "参数为json格式，包含指定下一步操作人员得list")
    @PostMapping("/createMetaFormFlowAndSubmit")
    @WrapUpResponseBody
    public FlowInstance createMetaFormFlowAndSubmit(@RequestBody CreateFlowOptions newFlowInstanceOptions, HttpServletRequest request) {
        //暂时这么定义，一个基本业务自定义表单必然只匹配一个流程
        FlowOptInfo flowOptInfo = wfOptService.getOptByModelId(newFlowInstanceOptions.getModelId());
        List<FlowInfo> flowInfos = flowDefine.getFlowsByOptId(flowOptInfo.getOptId());
        FlowInfo flowInfo = flowInfos.get(0);
        //创建流程
        newFlowInstanceOptions.setFlowCode(flowInfo.getFlowCode());
        FlowInstance flowInstance = flowEngine.createInstance(
            newFlowInstanceOptions, new ObjectUserUnitVariableTranslate(
                BaseController.collectRequestParameters(request)), null);
        //提交节点 :: TODO 为什么已创建就提交
        flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(flowInstance.getFirstNodeInstance().getNodeInstId())
            .user(newFlowInstanceOptions.getUserCode())
            .unit(newFlowInstanceOptions.getUnitCode()));
        return flowInstance;
    }

    @WrapUpResponseBody
    @PostMapping(value = "/createInstanceLockFirstNode")
    public FlowInstance createInstanceLockFirstNode(@RequestBody FlowInstance flowInstanceParam) {

        return flowEngine.createInstance(
            CreateFlowOptions.create().flow(flowInstanceParam.getFlowCode())
            .optName(flowInstanceParam.getFlowOptName())
            .optTag(flowInstanceParam.getFlowOptTag())
            .user(flowInstanceParam.getUserCode())
            .unit(flowInstanceParam.getUnitCode())
            .lockOptUser(true));
    }


    @ApiOperation(value = "创建流程", notes = "创建流程，参数为json格式")
    @WrapUpResponseBody
    @PostMapping(value = "/createFlowInstDefault")
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
    public ResponseData submitOpt(@RequestBody String json, HttpServletRequest request) {
        JSONObject jsonObject = JSON.parseObject(json);
        String nodeInstId = jsonObject.getString("nodeInstId");
        String userCode = jsonObject.getString("userCode");
        String unitCode = jsonObject.getString("unitCode");
        String varTrans = jsonObject.getString("varTrans");
        JSONArray users = jsonObject.getJSONArray("userList");

        try {
            Set<String> nextNodes;
            if (StringUtils.isNotBlank(varTrans) && !"null".equals(varTrans)) {
                Map<String, Object> maps = BaseController.collectRequestParameters(request);
                maps.putAll((Map)JSON.parse(varTrans.replaceAll("&quot;", "\"")));
                nextNodes = flowEngine.submitOpt(
                    SubmitOptOptions.create().nodeInst(nodeInstId)
                        .user(userCode)
                        .unit(unitCode),
                    new ObjectUserUnitVariableTranslate(maps), null);
            } else {
                nextNodes = flowEngine.submitOpt(SubmitOptOptions.create().nodeInst(nodeInstId)
                    .user(userCode)
                    .unit(unitCode));
            }
            //更新操作人
            if (users != null && users.size() > 0) {
                for (String n : nextNodes) {
                    flowManager.deleteNodeActionTasks(n, flowEngine.getNodeInstById(nodeInstId).getFlowInstId(), userCode);
                    for (Object v : users) {
                        flowManager.assignNodeTask(n, v.toString(), userCode,  "手动指定审批人");
                    }
                }
            }
            return ResponseData.makeResponseData(nextNodes);
        } catch (WorkflowException e) {
            return ResponseData.makeErrorMessage(e.getExceptionType(), e.getMessage());
        }
    }

    @ApiOperation(value = "保存流程变量", notes = "保存流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/saveFlowVariable")
    public void saveFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        List<String> vars = JSON.parseArray(flowVariableParam.getVarValue(), String.class);
        if (!vars.isEmpty())
            flowEngine.saveFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName(), new HashSet<>(vars));
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
        flowEngine.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode(), flowWorkTeam.getUserCode());
    }

    @ApiOperation(value = "删除办件角色", notes = "删除办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowWorkTeam")
    public void deleteFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        flowEngine.deleteFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
    }


    @ApiOperation(value = "查看流程节点", notes = "查看流程节点")
    @GetMapping(value = "/listFlowInstNodes")
    public void listFlowInstNodes(HttpServletResponse response, String flowInstId) {
        List<NodeInstance> nodeInstList = flowManager.listFlowInstNodes(flowInstId);
        excludes = new HashMap<>();
        excludes.put(NodeInstance.class, new String[]{"wfActionLogs", "wfActionTasks"});
        JsonResultUtils.writeSingleDataJson(nodeInstList, response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }

    @ApiOperation(value = "查询用户待办", notes = "查询用户待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listUserTasks")
    public List<UserTask> listUserTasks(String userCode) {
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = flowEngine.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        return userTasks;
    }

    @ApiOperation(value = "根据条件查询待办", notes = "根据条件查询待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listTasks")
    public List<UserTask> listTasks(HttpServletRequest request) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<UserTask> userTasks = flowEngine.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        return userTasks;
    }

    @ApiOperation(value = "查询节点待办用户", notes = "查询节点待办用户")
    @WrapUpResponseBody
    @GetMapping(value = "/listNodeTaskUsers")
    public List<UserTask> listNodeTaskUsers(String nodeInstId) {
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put("nodeInstId", nodeInstId);
        List<UserTask> userTasks = flowEngine.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        return userTasks;
    }

    @ApiOperation(value = "查询用户岗位待办", notes = "查询用户岗位待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listUserDynamicTasks")
    public List<UserTask> listUserDynamicTasks(String userCode) {
        Map<String, Object> searchColumn = new HashMap<>();
        PageDesc pageDesc = new PageDesc(1, 10);
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = flowEngine.listDynamicTask(searchColumn, pageDesc);
        return userTasks;
    }

    @ApiOperation(value = "业务id关联流程", notes = "根据业务id查询关联流程")
    @WrapUpResponseBody
    @GetMapping(value = "/listAllFlowInstByOptTag")
    public List<FlowInstance> listAllFlowInstByOptTag(@RequestParam(value = "flowOptTag") String flowOptTag) {
        return flowEngine.listAllFlowInstByOptTag(flowOptTag);
    }

    @ApiOperation(value = "更改流程业务信息", notes = "更改流程业务信息程")
    @WrapUpResponseBody
    @PostMapping(value = "/updateFlowInstOptInfo")
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
    @PostMapping(value = "deleteFlowOrganize")
    public void deleteFlowOrganize(@RequestBody FlowOrganizeId flowOrganize) {
        flowEngine.deleteFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }


    @ApiOperation(value = "创建流程节点", notes = "创建流程节点")
    @WrapUpResponseBody
    @PostMapping(value = "createNodeInst")
    public void createNodeInst(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowInstId = jsonObject.getString("flowInstId");
        String curNodeInstId = jsonObject.getString("curNodeInstId");
        String createUser = jsonObject.getString("createUser");
        String userCode = jsonObject.getString("userCode");
        String nodeCode = jsonObject.getString("nodeCode");
        String unitCode = jsonObject.getString("unitCode");
        flowEngine.createIsolatedNodeInst(flowInstId, curNodeInstId, createUser,
            nodeCode, userCode, unitCode);
    }

    @ApiOperation(value = "回退节点", notes = "回退节点")
    @WrapUpResponseBody
    @PostMapping(value = "rollBackNode")
    public void rollBackNode(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String nodeInstId = jsonObject.getString("nodeInstId");
        String managerUserCode = jsonObject.getString("managerUserCode");
        flowEngine.rollbackOpt(nodeInstId, managerUserCode);
    }

    @ApiOperation(value = "创建流程实例分组", notes = "创建流程实例分组")
    @WrapUpResponseBody
    @PostMapping(value = "/createFlowInstGroupDefault")
    public FlowInstanceGroup createFlowInstGroupDefault(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String flowGroupName = jsonObject.getString("flowGroupName");
        String flowGroupDesc = jsonObject.getString("flowGroupDesc");
        FlowInstanceGroup flowInstanceGroup = flowEngine.createFlowInstGroup(flowGroupName, flowGroupDesc);
        return flowInstanceGroup;
    }

}
