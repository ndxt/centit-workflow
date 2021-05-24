package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.RoleRelegateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户的权限委托 和任务转移
 */
@Api(value = "任务管理",
    tags = "权限委托和任务转移")
@Controller
@RequestMapping("/flow/relegate")
public class RoleRelegateController extends BaseController {
    @Autowired
    private FlowManager flowManager;

    @Autowired
    private RoleRelegateService roleRelegateService;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @ApiOperation(value = "获取用户角色", notes = "获取用户角色")
    @ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String"
    )
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @GetMapping(value = "/role/{userCode}")
    public List<? extends IUserUnit> listUserRoles(@PathVariable String userCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        return context.listUserUnits(userCode);

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

        return PageQueryResult.createResult(relegateList, pageDesc);
    }

    @ApiOperation(value = "新增委托", notes = "新增委托")
    @WrapUpResponseBody
    @PostMapping
    public RoleRelegate saveRelegate(@RequestBody RoleRelegate roleRelegate) {
        return roleRelegateService.saveRelegate(roleRelegate);
    }

    @ApiOperation(value = "修改委托", notes = "修改委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/updateRelegate", method = RequestMethod.POST)
    public RoleRelegate updateRelegate(@RequestBody RoleRelegate roleRelegate) {
        return roleRelegateService.updateRelegate(roleRelegate);
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
    @RequestMapping(value = "/{relegateNo}", method = {RequestMethod.DELETE})
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
