package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowRole;
import com.centit.workflow.po.FlowTeamDefine;
import com.centit.workflow.service.FlowRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FlowRoleController
 * @Date 2019/7/22 15:37
 * @Version 1.0
 */
@Controller
@RequestMapping("/flowRole")
public class FlowRoleController extends BaseController {

    private ResponseMapData resData = new ResponseMapData();

    @Resource
    private FlowRoleService flowRoleService;

    //审批角色列表
    @RequestMapping("/listFlowRole")
    public void listAllFlowRole(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);
        List<FlowRole> objList = flowRoleService.listFlowRoles(filterMap, pageDesc);
        resData.addResponseData(OBJLIST,objList);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    @RequestMapping(value = "/getFlowRoleByCode", method = RequestMethod.GET)
    public void getFlowRoleByCode(String roleCode, HttpServletRequest request, HttpServletResponse response){
        FlowRole flowRole = flowRoleService.getFlowRoleByCode(roleCode);
        JsonResultUtils.writeSingleDataJson(flowRole,response);
    }

    @RequestMapping(value = "/saveFlowRole", method = RequestMethod.POST)
    public void saveFlowRole(@RequestBody FlowRole flowRole, HttpServletRequest request, HttpServletResponse response){
        flowRoleService.saveFlowRole(flowRole);
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping("/deleteFlowRoleByCode")
    public void deleteFlowRoleByCode(String roleCode, HttpServletRequest request, HttpServletResponse response) {
        flowRoleService.deleteFlowRoleByCode(roleCode);
        JsonResultUtils.writeSuccessJson(response);
    }

    /**
     * 获取审批角色的明细
     */
    @RequestMapping(value = {"/getFlowRoleDefineListByCode/{roleCode}"}, method = {RequestMethod.GET})
    public void getFlowRoleDefineListByCode(@PathVariable String roleCode, HttpServletResponse response) {
        List<FlowTeamDefine> flowRoleDefineList = this.flowRoleService.getFlowRoleDefineListByCode(roleCode);
        FlowRole flowRole = new FlowRole();
        flowRole.setFlowRoleDefineList(flowRoleDefineList);
        JsonResultUtils.writeSingleDataJson(flowRole, response);
    }

    @RequestMapping({"/deleteFlowRoleDefineByCode"})
    public void deleteFlowRoleDefineByCode(String id, HttpServletRequest request, HttpServletResponse response) {
        this.flowRoleService.deleteFlowRoleDefineById(id);
        JsonResultUtils.writeSuccessJson(response);
    }

    @RequestMapping({"/saveFlowRoleDefineList"})
    public void saveFlowRoleDefineList(@RequestBody JSONObject paramData, HttpServletRequest request, HttpServletResponse response) {
        JSONArray flowRoleDefineList = paramData.getJSONArray("flowRoleDefineList");
        for(int i = 0; i < flowRoleDefineList.size(); ++i) {
            FlowTeamDefine flowRoleDefine = flowRoleDefineList.getObject(i, FlowTeamDefine.class);
            this.flowRoleService.saveFlowRoleDefine(flowRoleDefine);
        }
        JsonResultUtils.writeBlankJson(response);
    }
}
