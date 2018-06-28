package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.NewFlowInstanceOptions;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.PlatformFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
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
public class FlowEngineController  extends BaseController {
    @Resource
    private FlowEngine flowEng;
    @Resource
    private FlowManager flowManager;
    @Resource
    private PlatformFlowService platformFlowService;


    private Map<Class<?>, String[]> excludes;
    @RequestMapping(value = "/createFlowInstDefault")
    public void createInstance(String flowCode, String flowOptName,String flowOptTag,String userCode,String unitCode, HttpServletResponse httpResponse) {
        NewFlowInstanceOptions newFlowInstanceOptions = new NewFlowInstanceOptions();
        newFlowInstanceOptions.setFlowCode(flowCode);
        newFlowInstanceOptions.setFlowOptName(flowOptName);
        newFlowInstanceOptions.setFlowOptTag(flowOptTag);
        newFlowInstanceOptions.setUserCode(userCode);
        newFlowInstanceOptions.setUnitCode(unitCode);
        FlowInstance flowInstance = flowEng.createInstanceWithDefaultVersion(newFlowInstanceOptions);
        JsonResultUtils.writeSingleDataJson(flowInstance,httpResponse);
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
        JsonResultUtils.writeSingleDataJson(flowInstance,httpResponse);
    }

    @RequestMapping(value = "/createInstanceLockFirstNode",method = RequestMethod.POST)
    public void createInstanceLockFirstNode(HttpServletResponse httpResponse,FlowInstance flowInstanceParam){
        FlowInstance flowInstance = flowEng.createInstanceLockFirstNode(flowInstanceParam.getFlowCode(),flowInstanceParam.getOptName(),flowInstanceParam.getFlowOptTag(),flowInstanceParam.getUserCode(),flowInstanceParam.getUnitCode());
        JsonResultUtils.writeSingleDataJson(flowInstance,httpResponse);
    }
    @RequestMapping(value="submitOpt",method = RequestMethod.POST)
    public void submitOpt(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse, Long nodeInstId, String userCode, String unitCode){
        flowEng.submitOpt(nodeInstId,userCode,unitCode,null,httpServletRequest.getServletContext());
    }

    @RequestMapping(value = "/saveFlowVariable",method = {RequestMethod.POST,RequestMethod.PUT})
    public void saveFlowVariable(HttpServletResponse httpServletResponse, FlowVariable flowVariableParam){
        flowEng.saveFlowVariable(flowVariableParam.getFlowInstId(),flowVariableParam.getVarName(),flowVariableParam.getVarValue());
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }
    @RequestMapping(value="/viewFlowVariablesByVarname",method = RequestMethod.GET)
    public void viewFlowVariablesByVarname(HttpServletResponse httpServletResponse,Long flowInstId,String varName){
        List<FlowVariable> flowVariables = flowEng.viewFlowVariablesByVarname(flowInstId,varName);
        JsonResultUtils.writeSingleDataJson(flowVariables,httpServletResponse);
    }
    @RequestMapping(value = "/assignFlowWorkTeam",method = RequestMethod.POST)
    public void assignFlowWorkTeam(HttpServletResponse httpServletResponse,Long flowInstId,String roleCode,String userCodeList){
        if(userCodeList == null || userCodeList.trim().length() == 0){
            return;
        }
        String[] userCodeArr = userCodeList.split(",");
        List<String> userCodes = new ArrayList<>(Arrays.asList(userCodeArr));
        flowEng.assignFlowWorkTeam(flowInstId,roleCode,userCodes);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }
    @RequestMapping(value = "deleteFlowWorkTeam",method = RequestMethod.GET)
    public void deleteFlowWorkTeam(HttpServletResponse httpServletResponse,Long flowInstId,String roleCode){
        flowEng.deleteFlowWorkTeam(flowInstId,roleCode);
        JsonResultUtils.writeBlankJson(httpServletResponse);
    }
    @RequestMapping(value="/listFlowInstNodes",method = RequestMethod.GET)
    public void  listFlowInstNodes(HttpServletResponse response,Long flowInstId){
        List<NodeInstance> nodeInstList = flowManager.listFlowInstNodes(flowInstId);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(NodeInstance.class,new String[]{"wfActionLogs","wfActionTasks"});
        JsonResultUtils.writeSingleDataJson(nodeInstList,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }
    @RequestMapping(value="/listUserTasks",method = RequestMethod.GET)
    public void  listUserTasks(HttpServletRequest request,HttpServletResponse response,String userCode){
        Map<String, Object> searchColumn = convertSearchColumn(request);
        if(StringUtils.isBlank(userCode)){
            userCode = super.getLoginUserCode(request);
        }
        searchColumn.put("userCode",userCode);
        List<UserTask> userTasks = flowEng.listUserTasksByFilter(searchColumn,new PageDesc(-1,-1));
        JsonResultUtils.writeSingleDataJson(userTasks,response);
    }

    /**
     * 获取用户动态待办
     * @param request
     * @param response
     * @param userCode
     */
    @RequestMapping(value="/listUserDynamicTasks",method = RequestMethod.GET)
    public void  listUserDynamicTasks(HttpServletRequest request,HttpServletResponse response,String userCode){
        Map<String, Object> searchColumn = convertSearchColumn(request);
        if(StringUtils.isBlank(userCode)){
            userCode = super.getLoginUserCode(request);
        }
        searchColumn.put("userCode",userCode);
        List<UserTask> userTasks = platformFlowService.queryDynamicTask(userCode);
        JsonResultUtils.writeSingleDataJson(userTasks,response);
    }
}
