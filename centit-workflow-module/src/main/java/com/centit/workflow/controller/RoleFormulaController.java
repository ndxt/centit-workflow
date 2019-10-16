package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @ClassName RoleFormulaController
 * @Date 2019/7/22 15:37
 * @Version 1.0
 */
@Controller
@Api(value = "权限表达式",
    tags = "权限表达式接口类")
@RequestMapping("/formula")
public class RoleFormulaController extends BaseController {


    @Resource
    private RoleFormulaService roleFormulaService;

    @ApiOperation(value = "权限表达式列表", notes = "权限表达式列表")
    @WrapUpResponseBody
    @RequestMapping(value="/listRoleFormula",method = RequestMethod.GET)
    public PageQueryResult<RoleFormula> listAllRoleFormula(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);
        List<RoleFormula> listObjects = roleFormulaService.listRoleFormulas(filterMap, pageDesc);

        return PageQueryResult.createResult(listObjects, pageDesc);
    }
    @ApiOperation(value = "查询单个权限表达式", notes = "查询单个权限表达式")
    @WrapUpResponseBody
    @RequestMapping(value = "/getRoleFormulaCode/{formulaCode}", method = RequestMethod.GET)
    public RoleFormula getRoleFormulaByCode(@PathVariable String formulaCode){
        RoleFormula roleFormula = roleFormulaService.getRoleFormulaByCode(formulaCode);
        return roleFormula;
    }
    @ApiOperation(value = "保存权限表达式", notes = "保存权限表达式")
    @WrapUpResponseBody
    @RequestMapping(value = "/saveRoleFormula", method = RequestMethod.POST)
    public RoleFormula saveFlowRole(@RequestBody RoleFormula roleFormula){
        roleFormulaService.saveRoleFormula(roleFormula);
        return roleFormula;
    }
    @ApiOperation(value = "删除权限表达式", notes = "删除权限表达式")
    @WrapUpResponseBody
    @RequestMapping(value="/deleteRoleFormulaByCode/{formulaCode}", method = RequestMethod.POST)
    public void deleteFlowRoleByCode(@PathVariable String formulaCode) {
        roleFormulaService.deleteRoleFormulaByCode(formulaCode);
    }
    @ApiOperation(value = "查询权限表达式对应用户", notes = "查询权限表达式对应用户")
    @WrapUpResponseBody
    @RequestMapping(value="/viewRoleFormulaUsers/{formulaCode}",method = RequestMethod.GET)
    public JSONArray viewRoleFormulaUsers(@PathVariable String formulaCode){
        JSONArray listObjects = roleFormulaService.viewRoleFormulaUsers(formulaCode);
        return listObjects;
    }

}
