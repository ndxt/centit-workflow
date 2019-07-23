package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/flow/userOpt")
public class UserOptController extends BaseController {
    @Resource
    private FlowManager flowManager;
    private ResponseMapData resData;

    /**
     * 获取指定用户委托列表
     *
     * @param userCode
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getRelegateList/{userCode}", method = {RequestMethod.GET})
    public void getRelegateListByGrantor(@PathVariable String userCode, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userCode)) {
            CentitUserDetails centitUserDetails =(CentitUserDetails) WebOptUtils.getLoginUser(request);
            if (centitUserDetails != null) {
                userCode = centitUserDetails.getUserCode();
            }
        }
        List<RoleRelegate> relegateList = flowManager.listRoleRelegateByGrantor(userCode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 更新委托
     *
     * @param roleRelegate
     * @param response
     */
    @PostMapping(value = "/saveRelegate")
    public void saveRelegate(@RequestBody RoleRelegate roleRelegate, HttpServletResponse response) {
        flowManager.saveRoleRelegate(roleRelegate);
        JsonResultUtils.writeSuccessJson(response);
    }

    /**
     * 根据id删除委托
     *
     * @param relegateNo
     * @param response
     */
    @RequestMapping(value = "/deleteRelegate/{relegateNo}", method = {RequestMethod.DELETE})
    public void deleteRelegate(@PathVariable Long relegateNo, HttpServletResponse response) {
        flowManager.deleteRoleRelegate(relegateNo);
        JsonResultUtils.writeSuccessJson(response);
    }

    @GetMapping(value = "/getTaskDelegateByNo/{relegateNo}")
    public void getTaskDelegateByNo(@PathVariable Long relegateNo, HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(flowManager.getRoleRelegateById(relegateNo), response);
    }

}
