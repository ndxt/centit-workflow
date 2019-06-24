package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.NewFlowInstanceOptions;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.PlatformFlowService;
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
    private FlowEngine flowEng;
    @Resource
    private FlowManager flowManager;
    @Resource
    private PlatformFlowService platformFlowService;


    private Map<Class<?>, String[]> excludes;


    @WrapUpResponseBody
    @PostMapping(value = "/createInstanceLockFirstNode")
    public FlowInstance createInstanceLockFirstNode(@RequestBody FlowInstance flowInstanceParam) {
        return flowEng.createInstanceLockFirstNode(flowInstanceParam.getFlowCode(), flowInstanceParam.getFlowOptName(), flowInstanceParam.getFlowOptTag(), flowInstanceParam.getUserCode(), flowInstanceParam.getUnitCode());
    }

    //加载通用po到流程流转中
    private ObjectUserUnitVariableTranslate getBusinessVariable(Map<String, Object> varTrans) {
        ObjectUserUnitVariableTranslate<Map<String, Object>> bo = new ObjectUserUnitVariableTranslate<>();
        bo.setModuleObject(varTrans);
        return bo;
    }

    @ApiOperation(value = "创建流程", notes = "创建流程，参数为json格式")
    @WrapUpResponseBody
    @PostMapping(value = "/createFlowInstDefault")
    public FlowInstance createFlowInstDefault(@RequestBody NewFlowInstanceOptions newFlowInstanceOptions) {
        FlowInstance flowInstance = flowEng.createInstanceWithDefaultVersion(newFlowInstanceOptions);
        return flowInstance;
    }

    @ApiOperation(value = "提交节点", notes = "提交节点")
    @ApiImplicitParam(name = "json", value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}", paramType = "body", examples = @Example({
        @ExampleProperty(value = "{'nodeInstId':10,'userCode':'u1','unitCode':'d1','varTrans':'jsonString,可不填'}", mediaType = "application/json")
    }))
    @WrapUpResponseBody
    @PostMapping(value = "submitOpt")
    public ResponseData submitOpt(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        long nodeInstId = jsonObject.getLong("nodeInstId");
        String userCode = jsonObject.getString("userCode");
        String unitCode = jsonObject.getString("unitCode");
        String varTrans = jsonObject.getString("varTrans");
        try {
            Set<Long> nextNodes ;
            if (StringUtils.isNotBlank(varTrans) && !"null".equals(varTrans)) {
                Map<String, Object> maps = (Map) JSON.parse(varTrans.replaceAll("&quot;", "\""));
                nextNodes = flowEng.submitOpt(nodeInstId, userCode, unitCode, getBusinessVariable(maps), null);
            } else {
                nextNodes = flowEng.submitOpt(nodeInstId, userCode, unitCode, null, null);
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
            flowEng.saveFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName(), new HashSet<>(vars));
    }

    @ApiOperation(value = "删除流程变量", notes = "删除流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/deleteFlowVariable")
    public void deleteFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        flowEng.deleteFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getRunToken(), flowVariableParam.getVarName());
    }

    @ApiOperation(value = "保存流程节点变量", notes = "保存流程节点变量")
    @WrapUpResponseBody
    @PostMapping(value = "/saveFlowNodeVariable")
    public void saveFlowNodeVariable(@RequestBody String FlowNodeVariable) {
        JSONObject jsonObject = JSON.parseObject(FlowNodeVariable);
        long nodeInstId = jsonObject.getLong("nodeInstId");
        String varName = jsonObject.getString("varName");
        String varValue = jsonObject.getString("varValue");
        List<String> vars = JSON.parseArray(varValue, String.class);
        flowEng.saveFlowNodeVariable(nodeInstId, varName, new HashSet<>(vars));
    }

    @ApiOperation(value = "查看流程变量", notes = "查看流程变量")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowVariablesByVarname")
    public List<FlowVariable> viewFlowVariablesByVarname(FlowVariable flowVariableParam) {
        List<FlowVariable> flowVariables = flowEng.viewFlowVariablesByVarname(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName());
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
        flowEng.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode(), userCodes);
    }

    @ApiOperation(value = "新增单个办件角色", notes = "新增单个办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "/addFlowWorkTeam")
    public void addFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        flowEng.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode(), flowWorkTeam.getUserCode());
    }

    @ApiOperation(value = "删除办件角色", notes = "删除办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowWorkTeam")
    public void deleteFlowWorkTeam(@RequestBody FlowWorkTeamId flowWorkTeam) {
        flowEng.deleteFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
    }


    @ApiOperation(value = "查看流程节点", notes = "查看流程节点")
    @GetMapping(value = "/listFlowInstNodes")
    public void listFlowInstNodes(HttpServletResponse response, Long flowInstId) {
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
        List<UserTask> userTasks = flowEng.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        return userTasks;
    }

    @ApiOperation(value = "查询节点待办用户", notes = "查询节点待办用户")
    @WrapUpResponseBody
    @GetMapping(value = "/listNodeTaskUsers")
    public List<UserTask> listNodeTaskUsers(Long nodeInstId) {
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put("nodeInstId", nodeInstId);
        List<UserTask> userTasks = flowEng.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        return userTasks;
    }

    @ApiOperation(value = "查询用户岗位待办", notes = "查询用户岗位待办")
    @WrapUpResponseBody
    @GetMapping(value = "/listUserDynamicTasks")
    public List<UserTask> listUserDynamicTasks(String userCode) {
        Map<String, Object> searchColumn = new HashMap<>();
        PageDesc pageDesc = new PageDesc(1, 10);
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = platformFlowService.queryDynamicTask(searchColumn, pageDesc);
        return userTasks;
    }

    @ApiOperation(value = "业务id关联流程", notes = "根据业务id查询关联流程")
    @WrapUpResponseBody
    @GetMapping(value = "/listAllFlowInstByOptTag")
    public List<FlowInstance> listAllFlowInstByOptTag(@RequestParam(value = "flowOptTag") String flowOptTag) {
        return flowEng.listAllFlowInstByOptTag(flowOptTag);
    }

    @ApiOperation(value = "更改流程业务信息", notes = "更改流程业务信息程")
    @WrapUpResponseBody
    @PostMapping(value = "/updateFlowInstOptInfo")
    public void updateFlowInstOptInfo(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        //流程实例ID
        long flowInstId = jsonObject.getLong("flowInstId");
        //流程名称
        String flowOptName = jsonObject.getString("flowOptName");
        //流程业务id
        String flowOptTag = jsonObject.getString("flowOptTag");
        flowEng.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
    }

    @ApiOperation(value = "查看办件角色", notes = "查看办件角色")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowWorkTeam")
    public List<String> viewFlowWorkTeam(FlowWorkTeam flowWorkTeam) {
        return flowEng.viewFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
    }

    @ApiOperation(value = "查看流程组织机构", notes = "查看流程组织机构")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowOrganize")
    public List<String> viewFlowOrganize(FlowOrganize flowOrganize) {
        return flowEng.viewFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }

    /**
     * @param json
     * @ApiOperation(value = "新增多个流程机构", notes = "新增流程组织机构，多个组织orgCodeSet传一个数组或者一个list，格式为userCode:[\"1\",\"2\"]")
     */
    @WrapUpResponseBody
    @PostMapping(value = "/assignFlowOrganize")
    public void assignFlowOrganize(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Long flowInstId = jsonObject.getLong("flowInstId");
        String roleCode = jsonObject.getString("roleCode");
        String orgCodeSet = jsonObject.getString("orgCodeSet");
        List<String> orgCodes = JSON.parseArray(orgCodeSet, String.class);
        flowEng.assignFlowOrganize(flowInstId, roleCode, orgCodes);
    }

    @ApiOperation(value = "新增单个流程机构", notes = "新增流程组织机构")
    @WrapUpResponseBody
    @PostMapping(value = "/addFlowOrganize")
    public void addFlowOrganize(@RequestBody FlowOrganizeId flowOrganize) {
        flowEng.assignFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode(), flowOrganize.getUnitCode());
    }

    @ApiOperation(value = "删除流程机构", notes = "删除流程机构")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowOrganize")
    public void deleteFlowOrganize(@RequestBody FlowOrganizeId flowOrganize) {
        flowEng.deleteFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }


    @ApiOperation(value = "创建流程节点", notes = "创建流程节点")
    @WrapUpResponseBody
    @PostMapping(value = "createNodeInst")
    public void createNodeInst(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        long flowInstId = jsonObject.getLong("flowInstId");
        String createUser = jsonObject.getString("createUser");
        String userCodes = jsonObject.getString("userCodes");
        long nodeId = jsonObject.getLong("nodeId");
        String unitCode = jsonObject.getString("unitCode");
        List<String> userList = JSON.parseArray(userCodes, String.class);
        flowEng.createNodeInst(flowInstId, createUser, nodeId, userList, unitCode);
    }
}
