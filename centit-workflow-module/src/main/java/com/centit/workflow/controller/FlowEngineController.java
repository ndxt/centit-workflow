package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.components.impl.ObjectUserUnitVariableTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.NewFlowInstanceOptions;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.PlatformFlowService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by chen_rj on 2017/7/28.
 */
@Controller
@RequestMapping("/flow/engine")
public class FlowEngineController extends BaseController {
    @Resource
    private FlowEngine flowEng;
    @Resource
    private FlowManager flowManager;
    @Resource
    private PlatformFlowService platformFlowService;


    private Map<Class<?>, String[]> excludes;

    @RequestMapping(value = "/createFlowInstDefault")
    public void createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, HttpServletResponse httpResponse) {
        NewFlowInstanceOptions newFlowInstanceOptions = new NewFlowInstanceOptions();
        newFlowInstanceOptions.setFlowCode(flowCode);
        newFlowInstanceOptions.setFlowOptName(flowOptName);
        newFlowInstanceOptions.setFlowOptTag(flowOptTag);
        newFlowInstanceOptions.setUserCode(userCode);
        newFlowInstanceOptions.setUnitCode(unitCode);
        FlowInstance flowInstance = flowEng.createInstanceWithDefaultVersion(newFlowInstanceOptions);
        JsonResultUtils.writeSingleDataJson(flowInstance, httpResponse);
    }

    @RequestMapping(value = "/createTimeLimitFlowInstDefault")
    public void createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode, String timeLimitStr, HttpServletResponse httpResponse) {
        NewFlowInstanceOptions newFlowInstanceOptions = new NewFlowInstanceOptions();
        newFlowInstanceOptions.setFlowCode(flowCode);
        newFlowInstanceOptions.setFlowOptName(flowOptName);
        newFlowInstanceOptions.setFlowOptTag(flowOptTag);
        newFlowInstanceOptions.setUserCode(userCode);
        newFlowInstanceOptions.setUnitCode(unitCode);
        newFlowInstanceOptions.setTimeLimitStr(timeLimitStr);
        FlowInstance flowInstance = flowEng.createInstanceWithDefaultVersion(newFlowInstanceOptions);
        JsonResultUtils.writeSingleDataJson(flowInstance, httpResponse);
    }

    /**
     * 新增一个以json作为参数的创建流程接口
     *
     * @param json
     * @param httpResponse
     */
    @RequestMapping(value = "/createFlowInstDefaultByJson")
    public void createFlowInstDefaultByJson(@RequestBody String json, HttpServletResponse httpResponse) {
        NewFlowInstanceOptions newFlowInstanceOptions = JSON.parseObject(json, NewFlowInstanceOptions.class);
        FlowInstance flowInstance = flowEng.createInstanceWithDefaultVersion(newFlowInstanceOptions);
        JsonResultUtils.writeSingleDataJson(flowInstance, httpResponse);
    }


    @RequestMapping(value = "/createFlowInstWithVersion")
    public void createInstance(String flowCode, long version, String flowOptName, String flowOptTag, String userCode, String unitCode, HttpServletResponse httpResponse) {
        NewFlowInstanceOptions newFlowInstanceOptions = new NewFlowInstanceOptions();
        newFlowInstanceOptions.setFlowCode(flowCode);
        newFlowInstanceOptions.setVersion(version);
        newFlowInstanceOptions.setFlowOptName(flowOptName);
        newFlowInstanceOptions.setFlowOptTag(flowOptTag);
        newFlowInstanceOptions.setUserCode(userCode);
        newFlowInstanceOptions.setUnitCode(unitCode);
        FlowInstance flowInstance = flowEng.createInstanceWithSpecifiedVersion(newFlowInstanceOptions);
        JsonResultUtils.writeSingleDataJson(flowInstance, httpResponse);
    }

    @RequestMapping(value = "/createInstanceLockFirstNode", method = RequestMethod.POST)
    public void createInstanceLockFirstNode(HttpServletResponse httpResponse, FlowInstance flowInstanceParam) {
        FlowInstance flowInstance = flowEng.createInstanceLockFirstNode(flowInstanceParam.getFlowCode(), flowInstanceParam.getOptName(), flowInstanceParam.getFlowOptTag(), flowInstanceParam.getUserCode(), flowInstanceParam.getUnitCode());
        JsonResultUtils.writeSingleDataJson(flowInstance, httpResponse);
    }

    @RequestMapping(value = "submitOpt", method = RequestMethod.POST)
    public Set<Long> submitOpt(HttpServletRequest httpServletRequest, Long nodeInstId, String userCode, String unitCode, String varTrans) {
        if (StringUtils.isNotBlank(varTrans) && !"null".equals(varTrans)) {
            Map<String, Object> maps = (Map) JSON.parse(varTrans.replaceAll("&quot;", "\""));
            return flowEng.submitOpt(nodeInstId, userCode, unitCode, getBusinessVariable(maps), httpServletRequest.getServletContext());
        } else {
            return flowEng.submitOpt(nodeInstId, userCode, unitCode, null, httpServletRequest.getServletContext());
        }
    }

    //加载通用po到流程流转中
    private ObjectUserUnitVariableTranslate getBusinessVariable(Map<String, Object> varTrans) {
        ObjectUserUnitVariableTranslate<Map<String, Object>> bo = new ObjectUserUnitVariableTranslate<>();
        bo.setModuleObject(varTrans);
        return bo;
    }

    @RequestMapping(value = "/saveFlowVariable", method = {RequestMethod.POST, RequestMethod.PUT})
    public void saveFlowVariable(HttpServletResponse httpServletResponse, FlowVariable flowVariableParam) {
        flowEng.saveFlowVariable(flowVariableParam.getFlowInstId(), flowVariableParam.getVarName(), flowVariableParam.getVarValue());
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }

    @RequestMapping(value = "/viewFlowVariablesByVarname", method = RequestMethod.GET)
    public void viewFlowVariablesByVarname(HttpServletResponse httpServletResponse, Long flowInstId, String varName) {
        List<FlowVariable> flowVariables = flowEng.viewFlowVariablesByVarname(flowInstId, varName);
        JsonResultUtils.writeSingleDataJson(flowVariables, httpServletResponse);
    }

    @RequestMapping(value = "/assignFlowWorkTeam", method = RequestMethod.POST)
    public void assignFlowWorkTeam(HttpServletResponse httpServletResponse, Long flowInstId, String roleCode, String userCodeList) {
        if (userCodeList == null || userCodeList.trim().length() == 0) {
            return;
        }
        String[] userCodeArr = userCodeList.split(",");
        List<String> userCodes = new ArrayList<>(Arrays.asList(userCodeArr));
        flowEng.assignFlowWorkTeam(flowInstId, roleCode, userCodes);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }

    @RequestMapping(value = "deleteFlowWorkTeam", method = RequestMethod.POST)
    public void deleteFlowWorkTeam(HttpServletResponse httpServletResponse, Long flowInstId, String roleCode) {
        flowEng.deleteFlowWorkTeam(flowInstId, roleCode);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }

    @RequestMapping(value = "deleteFlowOrganize", method = RequestMethod.POST)
    public void deleteFlowOrganize(HttpServletResponse httpServletResponse, Long flowInstId, String roleCode) {
        flowEng.deleteFlowOrganize(flowInstId, roleCode);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }

    @RequestMapping(value = "/listFlowInstNodes", method = RequestMethod.GET)
    public void listFlowInstNodes(HttpServletResponse response, Long flowInstId) {
        List<NodeInstance> nodeInstList = flowManager.listFlowInstNodes(flowInstId);
        excludes = new HashMap<Class<?>, String[]>();
        excludes.put(NodeInstance.class, new String[]{"wfActionLogs", "wfActionTasks"});
        JsonResultUtils.writeSingleDataJson(nodeInstList, response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }

    @RequestMapping(value = "/listUserTasks", method = RequestMethod.GET)
    public void listUserTasks(HttpServletRequest request, HttpServletResponse response, String userCode) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        if (StringUtils.isBlank(userCode)) {
            userCode = super.getLoginUserCode(request);
        }
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = flowEng.listUserTasksByFilter(searchColumn, new PageDesc(-1, -1));
        JsonResultUtils.writeSingleDataJson(userTasks, response);
    }

    /**
     * 获取用户动态待办
     *
     * @param request
     * @param response
     * @param userCode
     */
    @RequestMapping(value = "/listUserDynamicTasks", method = RequestMethod.GET)
    public void listUserDynamicTasks(HttpServletRequest request, HttpServletResponse response, String userCode) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        if (StringUtils.isBlank(userCode)) {
            userCode = super.getLoginUserCode(request);
        }
        PageDesc pageDesc = new PageDesc(1, 10);
        searchColumn.put("userCode", userCode);
        List<UserTask> userTasks = platformFlowService.queryDynamicTask(searchColumn, pageDesc);
        JsonResultUtils.writeSingleDataJson(userTasks, response);
    }

/*    @RequestMapping(value = "/saveOptIdeaForAutoSubmit",method = {RequestMethod.POST})
    public void saveOptIdeaForAutoSubmit(HttpServletResponse httpServletResponse, Map<String,Object> paraMap){
        flowEng.saveOptIdeaForAutoSubmit(paraMap);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }*/

    /**
     * 根据业务id获取所有该业务下的流程
     *
     * @param flowOptTag
     * @return
     */
    @RequestMapping(value = "/listAllFlowInstByOptTag", method = RequestMethod.GET)
    void listAllFlowInstByOptTag(HttpServletRequest request, HttpServletResponse response, String flowOptTag) {
        JsonResultUtils.writeSingleDataJson(flowEng.listAllFlowInstByOptTag(flowOptTag), response);
    }

    /**
     * 更改流程业务信息，flowOptName 用来显示业务办件名称，flowOptTag 给业务系统自己解释可以用于反向关联
     *
     * @param flowInstId  流程实例ID
     * @param flowOptName 这个名称用户 查找流程信息
     */
    @RequestMapping(value = "/updateFlowInstOptInfo", method = RequestMethod.POST)
    void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag,
                               HttpServletRequest request, HttpServletResponse response) {
        flowEng.updateFlowInstOptInfo(flowInstId, flowOptName, flowOptTag);
        JsonResultUtils.writeSuccessJson(response);
    }

    @RequestMapping(value = "/viewFlowWorkTeam", method = {RequestMethod.POST, RequestMethod.GET})
    public void viewFlowWorkTeam(HttpServletResponse httpServletResponse, FlowWorkTeam flowWorkTeam) {
        List<String> flowWorkTeams = flowEng.viewFlowWorkTeam(flowWorkTeam.getFlowInstId(), flowWorkTeam.getRoleCode());
        JsonResultUtils.writeSingleDataJson(flowWorkTeams, httpServletResponse);
    }

    @RequestMapping(value = "/viewFlowOrganize", method = {RequestMethod.POST, RequestMethod.GET})
    public void viewFlowOrganize(HttpServletResponse httpServletResponse, FlowOrganize flowOrganize) {
        List<String> orgnaizes = flowEng.viewFlowOrganize(flowOrganize.getFlowInstId(), flowOrganize.getRoleCode());
        JsonResultUtils.writeSingleDataJson(orgnaizes, httpServletResponse);
    }

    @RequestMapping(value = "/assignFlowOrganize", method = {RequestMethod.POST, RequestMethod.GET})
    public void assignFlowOrganize(HttpServletResponse httpServletResponse, Long flowInstId, String roleCode, String orgCodeSet) {
        String[] orgArr = orgCodeSet.split(",");
        List<String> orgCodes = new ArrayList<>(Arrays.asList(orgArr));
        flowEng.assignFlowOrganize(flowInstId, roleCode, orgCodes);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }
}
