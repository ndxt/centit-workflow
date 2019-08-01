package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowRole;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowManager;
import com.centit.workflow.service.FlowRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("/flow/userOpt")
public class UserOptController extends BaseController {
    @Resource
    private FlowManager flowManager;
    @Resource
    private FlowRoleService flowRoleService;
    //private ResponseMapData resData;


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
            CentitUserDetails centitUserDetails = (CentitUserDetails) WebOptUtils.getLoginUser(request);
            if (centitUserDetails != null) {
                userCode = centitUserDetails.getUserCode();
            }
        }
        List<JSONObject> relegateList = flowManager.getListRoleRelegateByGrantor(userCode);

        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping(value = "/getUserFlowRole/{userCode}", method = {RequestMethod.GET})
    public void getUserFlowRole(@PathVariable String userCode, HttpServletResponse response) {
        List<FlowRole> flowRoles = flowRoleService.listFlowRoles(new HashMap<>(), new PageDesc());
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("flowRoleList", flowRoles);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 更新委托
     *
     * @param json
     * @param response
     */
    @PostMapping(value = "/saveRelegate")
    public void saveRelegate(@RequestBody String json, HttpServletResponse response) {
        RoleRelegate roleRelegate = JSONObject.parseObject(json, RoleRelegate.class);
        flowManager.saveRoleRelegateList(roleRelegate);
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

    @RequestMapping(value = "/getTaskDelegateByNo", method = RequestMethod.POST)
    public void getTaskDelegateByNo(@RequestBody String json, HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(flowManager.getRoleRelegateByPara(json), response);
    }

    @RequestMapping(value = "/resetRelegate", method = RequestMethod.PUT)
    public void resetRelegate(@RequestBody String json, HttpServletResponse response) {
        Boolean flag = flowManager.changeRelegateValid(json);
        if (flag) {
            JsonResultUtils.writeSingleDataJson("", response);
        } else {
            JsonResultUtils.writeErrorMessageJson("更新信息失败", response);
        }
    }

}
