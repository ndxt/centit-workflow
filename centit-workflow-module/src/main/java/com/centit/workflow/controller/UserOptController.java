package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户的权限委托 和任务转移
 */
@Api(value = "任务管理",
    tags = "权限委托和任务转移")
@Controller
@RequestMapping("/flow/relegate")
public class UserOptController extends BaseController {
    @Autowired
    private FlowManager flowManager;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @ApiOperation(value = "获取用户角色", notes = "获取用户角色")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "userCode", value="用户代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "roleType", value="角色类别只能为'GW'或者'XZ'",
        required=true, paramType = "path", dataType= "String"
    )})
    @WrapUpResponseBody
    @GetMapping(value = "/role/{userCode}/{roleType}")
    public Map<String, String> listUserRoles(@PathVariable String userCode,
                                             @PathVariable String roleType){
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        List<? extends IUserUnit> userUnits = context.listUserUnits(userCode);
        boolean isGwRole = SysUserFilterEngine.ROLE_TYPE_GW.equals(roleType);
        Map<String, String> allRoles = isGwRole? context.listAllStation()
            : context.listAllRank();
        Map<String, String> userRoles = new HashMap<>();
        for(IUserUnit uu : userUnits){
            if(isGwRole){
                userRoles.put(uu.getUserStation(), allRoles.get(uu.getUserStation()));
            } else {
                userRoles.put(uu.getUserRank(), allRoles.get(uu.getUserRank()));
            }
        }
        return userRoles;
    }

    /**
     * 获取指定用户委托列表
     *
     * @param userCode
     * @param pageDesc
     * @param request
     */
    @ApiOperation(value = "获取指定用户委托列表", notes = "获取指定用户委托列表")
    @WrapUpResponseBody
    @RequestMapping(value = "/byUser/{userCode}", method = {RequestMethod.GET})
    public PageQueryResult getRelegateListByGrantor(@PathVariable String userCode, PageDesc pageDesc, HttpServletRequest request) {
        if (StringUtils.isBlank(userCode)) {
            CentitUserDetails centitUserDetails = (CentitUserDetails) WebOptUtils.getLoginUser(request);
            if (centitUserDetails != null) {
                userCode = centitUserDetails.getUserCode();
            }
        }
        List<JSONObject> relegateList = flowManager.getListRoleRelegateByGrantor(userCode);

        return PageQueryResult.createResult(relegateList,pageDesc);
    }

    /**
     * 更新委托
     */
    @ApiOperation(value = "保存委托", notes = "保存委托")
    @WrapUpResponseBody
    @PostMapping
    public void saveRelegate(@RequestBody RoleRelegate roleRelegate) {
        flowManager.saveRoleRelegateList(roleRelegate);
    }

    @ApiOperation(value = "修改委托状态", notes = "修改委托状态")
    @RequestMapping(method = RequestMethod.PUT)
    public void updateRelegate(@RequestBody String json) {
        flowManager.changeRelegateValid(json);
    }
    /**
     * 根据id删除委托
     *
     * @param relegateNo
     */
    @ApiOperation(value = "删除委托", notes = "删除委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/relegate/{relegateNo}", method = {RequestMethod.DELETE})
    public void deleteRelegate(@PathVariable String relegateNo) {
        flowManager.deleteRoleRelegate(relegateNo);
    }
    @ApiOperation(value = "通过参数获取委托", notes = "通过参数获取委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public RoleRelegate getTaskDelegateByNo(@RequestBody String json) {
        return flowManager.getRoleRelegateByPara(json);
    }



}
