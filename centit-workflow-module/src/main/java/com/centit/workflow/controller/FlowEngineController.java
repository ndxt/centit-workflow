package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.NewFlowInstanceOptions;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.PlatformFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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


    @ApiOperation(value = "创建流程", notes = "创建流程，锁定首节点")
    @WrapUpResponseBody
    @PostMapping(value = "/createInstanceLockFirstNode")
    public FlowInstance createInstanceLockFirstNode(@RequestBody FlowInstance flowInstanceParam) {
        return flowEng.createInstanceLockFirstNode(flowInstanceParam.getFlowCode(), flowInstanceParam.getOptName(), flowInstanceParam.getFlowOptTag(), flowInstanceParam.getUserCode(), flowInstanceParam.getUnitCode());
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
    @WrapUpResponseBody
    @PostMapping(value = "submitOpt")
    public Set<Long> submitOpt(HttpServletRequest httpServletRequest, @RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        long nodeInstId = jsonObject.getLong("nodeInstId");
        String userCode = jsonObject.getString("userCode");
        String unitCode = jsonObject.getString("unitCode");
        String varTrans = jsonObject.getString("varTrans");
        if (StringUtils.isNotBlank(varTrans) && !"null".equals(varTrans)) {
            Map<String, Object> maps = (Map) JSON.parse(varTrans.replaceAll("&quot;", "\""));
            Set<Long> nextNodes = flowEng.submitOpt(nodeInstId, userCode, unitCode, getBusinessVariable(maps), null);
            return nextNodes;
        } else {
            Set<Long> nextNodes = flowEng.submitOpt(nodeInstId, userCode, unitCode, null, null);
            return nextNodes;
        }
    }

    @ApiOperation(value = "保存流程变量", notes = "保存流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/saveFlowVariable")
    public void saveFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        flowEng.saveFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName(), flowVariableParam.getVarValue());
    }

    @ApiOperation(value = "删除流程变量", notes = "删除流程变量")
    @WrapUpResponseBody
    @PostMapping(value = "/deleteFlowVariable")
    public void deleteFlowVariable(@RequestBody FlowVariable flowVariableParam) {
        flowEng.deleteFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getRunToken(), flowVariableParam.getVarName());
    }

    @ApiOperation(value = "查看流程变量", notes = "查看流程变量")
    @WrapUpResponseBody
    @GetMapping(value = "/viewFlowVariablesByVarname")
    public List<FlowVariable> viewFlowVariablesByVarname(FlowVariable flowVariableParam) {
        List<FlowVariable> flowVariables = flowEng.viewFlowVariablesByVarname(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName());
        return flowVariables;
    }

    @ApiOperation(value = "新增办件角色", notes = "新增办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "/assignFlowWorkTeam")
    public void assignFlowWorkTeam(@RequestBody FlowWorkTeam flowWorkTeam) {
        String userCodeList = flowWorkTeam.getUserCode();
        if (userCodeList == null || userCodeList.trim().length() == 0) {
            return;
        }
        String[] userCodeArr = userCodeList.split(",");
        List<String> userCodes = new ArrayList<>(Arrays.asList(userCodeArr));
        flowEng.assignFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode(), userCodes);
    }

    @ApiOperation(value = "删除办件角色", notes = "删除办件角色")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowWorkTeam")
    public void deleteFlowWorkTeam(@RequestBody FlowWorkTeam flowWorkTeam) {
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

    @ApiOperation(value = "新增流程组织机构", notes = "新增流程组织机构")
    @WrapUpResponseBody
    @PostMapping(value = "/assignFlowOrganize")
    public void assignFlowOrganize(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        Long flowInstId = jsonObject.getLong("flowInstId");
        String roleCode = jsonObject.getString("roleCode");
        String orgCodeSet = jsonObject.getString("orgCodeSet");
        String[] orgArr = orgCodeSet.split(",");
        List<String> orgCodes = new ArrayList<>(Arrays.asList(orgArr));
        flowEng.assignFlowOrganize(flowInstId, roleCode, orgCodes);
    }

    @ApiOperation(value = "删除流程机构", notes = "删除流程机构")
    @WrapUpResponseBody
    @PostMapping(value = "deleteFlowOrganize")
    public void deleteFlowOrganize(@RequestBody FlowOrganize flowOrganize) {
        flowEng.deleteFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
    }
}
