package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/formula")
public class RoleFormulaController extends BaseController {

    private ResponseMapData resData = new ResponseMapData();

    @Resource
    private RoleFormulaService flowRoleService;

    //审批角色列表
    @RequestMapping("/listFlowRole")
    public void listAllFlowRole(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);
        List<RoleFormula> objList = flowRoleService.listRoleFormulas(filterMap, pageDesc);
        resData.addResponseData(OBJLIST,objList);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    @RequestMapping(value = "/getFlowRoleByCode", method = RequestMethod.GET)
    public void getFlowRoleByCode(String roleCode, HttpServletRequest request, HttpServletResponse response){
        RoleFormula flowRole = flowRoleService.getRoleFormulaByCode(roleCode);
        JsonResultUtils.writeSingleDataJson(flowRole,response);
    }

    @RequestMapping(value = "/saveFlowRole", method = RequestMethod.POST)
    public void saveFlowRole(@RequestBody RoleFormula flowRole, HttpServletRequest request, HttpServletResponse response){
        flowRoleService.saveRoleFormula(flowRole);
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping("/deleteFlowRoleByCode")
    public void deleteFlowRoleByCode(String roleCode, HttpServletRequest request, HttpServletResponse response) {
        flowRoleService.deleteRoleFormulaByCode(roleCode);
        JsonResultUtils.writeSuccessJson(response);
    }


}
