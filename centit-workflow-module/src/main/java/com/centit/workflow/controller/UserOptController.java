package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.RoleFormulaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户的权限委托 和任务转移
 */
@Api(value = "任务管理",
    tags = "权限委托和任务转移")
@Controller
@RequestMapping("/flow/userOpt")
public class UserOptController extends BaseController {
    @Resource
    private FlowManager flowManager;
    @Resource
    private RoleFormulaService flowRoleService;
    //private ResponseMapData resData;


    /**
     * 获取指定用户委托列表
     *
     * @param userCode
     * @param pageDesc
     * @param request
     */
    @ApiOperation(value = "获取指定用户委托列表", notes = "获取指定用户委托列表")
    @WrapUpResponseBody
    @RequestMapping(value = "/getRelegateList/{userCode}", method = {RequestMethod.GET})
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
    @PostMapping(value = "/saveRelegate")
    public void saveRelegate(@RequestBody RoleRelegate roleRelegate) {
        flowManager.saveRoleRelegateList(roleRelegate);
    }

    /**
     * 根据id删除委托
     *
     * @param relegateNo
     */
    @ApiOperation(value = "删除委托", notes = "删除委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/deleteRelegate/{relegateNo}", method = {RequestMethod.DELETE})
    public void deleteRelegate(@PathVariable Long relegateNo) {
        flowManager.deleteRoleRelegate(relegateNo);
    }
    @ApiOperation(value = "通过参数获取委托", notes = "通过参数获取委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/getTaskDelegateByNo", method = RequestMethod.POST)
    public RoleRelegate getTaskDelegateByNo(@RequestBody String json) {
        return flowManager.getRoleRelegateByPara(json);
    }

    @RequestMapping(value = "/resetRelegate", method = RequestMethod.PUT)
    public void resetRelegate(@RequestBody String json) {
        flowManager.changeRelegateValid(json);
    }

}
