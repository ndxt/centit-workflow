package com.centit.workflow.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.ObjectTranslate;
import com.centit.support.compiler.VariableFormula;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.HtmlFormUtils;
import com.centit.workflow.po.RoleFormula;
import com.centit.workflow.service.RoleFormulaService;
import com.centit.workflow.service.impl.FlowOptUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private RoleFormulaService roleFormulaService;
    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @ApiOperation(value = "权限表达式列表", notes = "权限表达式列表")
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public PageQueryResult<RoleFormula> listAllRoleFormula(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        filterMap.put("topUnit", topUnit);
        List<RoleFormula> listObjects = roleFormulaService.listRoleFormulas(filterMap, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    @ApiOperation(value = "查询单个权限表达式", notes = "查询单个权限表达式")
    @WrapUpResponseBody
    @RequestMapping(value = "/{formulaCode}", method = RequestMethod.GET)
    public RoleFormula getRoleFormulaByCode(@PathVariable String formulaCode) {
        return roleFormulaService.getRoleFormulaByCode(formulaCode);
    }

    @ApiOperation(value = "保存权限表达式", notes = "保存权限表达式")
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public RoleFormula saveFlowRole(HttpServletRequest request, @RequestBody RoleFormula roleFormula) {
        String loginUser = WebOptUtils.getCurrentUserCode(request);
        if (StringBaseOpt.isNvl(loginUser)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                getI18nMessage( "error.302.user_not_login", request));
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        roleFormula.setTopUnit(topUnit);
        roleFormulaService.saveRoleFormula(roleFormula);
        return roleFormula;
    }

    @ApiOperation(value = "删除权限表达式", notes = "删除权限表达式")
    @WrapUpResponseBody
    @RequestMapping(value = "/{formulaCode}", method = RequestMethod.DELETE)
    public void deleteFlowRoleByCode(@PathVariable String formulaCode) {
        roleFormulaService.deleteRoleFormulaByCode(formulaCode);
    }

    @ApiOperation(value = "查询权限表达式对应用户", notes = "查询权限表达式对应用户")
    @WrapUpResponseBody
    @RequestMapping(value = "/usersByFormulaCode/{formulaCode}", method = RequestMethod.GET)
    public JSONArray viewRoleFormulaUsers(@PathVariable String formulaCode,
                                          HttpServletRequest request) {
        return roleFormulaService.viewRoleFormulaUsers(
            formulaCode, WebOptUtils.getCurrentUserCode(request),
            WebOptUtils.getCurrentUnitCode(request));
    }

    @ApiOperation(value = "预览权限表达式对应用户", notes =
        "表达式为itemExp ([或| itemExp][与& itemExp][非! itemExp])的形式，itemExp为下列形式\n" +
            "D()P()DT()DL()GW()XZ()R()UT()UL()U()RO()\n" +
            "* D 根据机构代码过滤 D(机构表达式)\n" +
            "* P 根据机构代码过滤主要机构\n" +
            "* DT 根据机构类型过滤 DT(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* DL 根据机构标签过滤 DL(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* GW 根据岗位过滤 GW(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* XZ 根据行政职务过滤 XZ(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* R 根据行政职务等级过滤 R(U) / R(U-) / R(U-1) / R(U--) /R(U-1--)\n" +
            "* U 根据用户代码过滤 U(用户变量|\"用户代码常量\" [,用户变量|\"用户代码常量])\n" +
            "* UT 根据用户类型过滤 UT(\"用户类型常量\" [,\"用户类型常量\"])\n" +
            "* UL 根据用户标签过滤 UL(\"用户标记常量\" [,\"用户标记常量\"])\n" +
            "* RO 根据用户角色过滤 RO(\"系统角色代码常量\" [,\"系统角色代码常量\"])")
    @WrapUpResponseBody
    @RequestMapping(value = "/calcUsers", method = RequestMethod.GET)
    public JSONArray viewFormulaUsers(String formula, HttpServletRequest request) {
        if(StringBaseOpt.isNvl(formula)){
            return null;
        }
        return roleFormulaService.viewFormulaUsers(
            HtmlFormUtils.htmlString(formula),
            WebOptUtils.getCurrentUserCode(request),
            WebOptUtils.getCurrentUnitCode(request));
    }

    @ApiOperation(value = "预览权限表达式对应机构", notes =
        "表达式为itemExp ([或| itemExp][与& itemExp][非! itemExp])的形式，itemExp为下列形式\n" +
            "D()P()DT()DL()\n" +
            "* D 根据机构代码过滤 D(机构表达式)\n" +
            "* P 根据机构代码过滤主要机构\n" +
            "* DT 根据机构类型过滤 DT(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* DL 根据机构标签过滤 DL(\"角色代码常量\" [,\"角色代码常量\"])])")
    @WrapUpResponseBody
    @RequestMapping(value = "/calcUnits", method = RequestMethod.GET)
    public JSONArray viewFormulaUnits(String formula, HttpServletRequest request) {
        return roleFormulaService.viewFormulaUnits(
            HtmlFormUtils.htmlString(formula),
            WebOptUtils.getCurrentUserCode(request),
            WebOptUtils.getCurrentUnitCode(request));
    }

    private static List<UserInfo> truncateUsers(List<UserInfo> allusers, Integer maxSize) {
        if (maxSize == null || maxSize < 1 || allusers == null || allusers.size() <= maxSize) {
            return allusers;
        }
        return allusers.subList(0, maxSize);
    }

    @ApiOperation(value = "列举所有用户", notes = "列举所有用户")
    @WrapUpResponseBody
    @RequestMapping(value = "/allUsers", method = RequestMethod.GET)
    public List<UserInfo> listAllUserInfo(Integer maxUsers, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        return truncateUsers(roleFormulaService.listAllUserInfo(topUnit), maxUsers);
    }

    @ApiOperation(value = "根据前缀或者后缀查询用户", notes = "根据前缀或者后缀查询用户")
    @WrapUpResponseBody
    @RequestMapping(value = "/users/{prefix}", method = RequestMethod.GET)
    public List<UserInfo> listUserInfo(@PathVariable String prefix, Integer maxUsers, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        return truncateUsers(roleFormulaService.listUserInfo(prefix, topUnit), maxUsers);
    }

    @ApiOperation(value = "列举所有机构", notes = "列举所有机构")
    @WrapUpResponseBody
    @RequestMapping(value = "/allUnits", method = RequestMethod.GET)
    public List<UnitInfo> listAllUnitInfo(HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        return roleFormulaService.listAllUnitInfo(topUnit);
    }

    @ApiOperation(value = "列举所有子机构", notes = "列举所有子机构")
    @WrapUpResponseBody
    @RequestMapping(value = "/subUnits/{unitCode}", method = RequestMethod.GET)
    public List<UnitInfo> listSubUnit(@PathVariable String unitCode) {
        if ("null".equalsIgnoreCase(unitCode)) {
            unitCode = "";
        }
        return roleFormulaService.listSubUnit(unitCode);
    }

    @PostMapping(value = "/testformula")
    @ApiOperation(value = "测试表达式")
    @WrapUpResponseBody
    public Object testFormula(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        Object object = jsonObject.getOrDefault("jsonString", "");
        VariableFormula variableFormula = new VariableFormula();
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        variableFormula.setExtendFuncMap(FlowOptUtils.createExtendFuncMap(() ->
            userUnitFilterFactory.createCalcContext(topUnit)));
        variableFormula.setTrans(new ObjectTranslate(object));
        variableFormula.setFormula(jsonObject.containsKey("formula") ? jsonObject.getString("formula") : "");
        return variableFormula.calcFormula();
    }
}
