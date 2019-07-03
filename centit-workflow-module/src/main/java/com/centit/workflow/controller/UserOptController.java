package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/flow/useroptmgr")
public class UserOptController extends BaseController{

    @Resource
    private FlowEngine flowEng;
    @Resource
    private PlatformEnvironment platformEnvironment;
    @Resource
    FlowManager flowManager;
    private ResponseMapData  resData;
    private  Map<Class<?>, String[]> excludes;

    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    public void listAllUser(HttpServletResponse response){
        Object userList = platformEnvironment.listAllUsers();
        JsonResultUtils.writeSingleDataJson(userList,response);
    }

    /**
     * 获取指定用户任务待办
     * @param usercode
     * @param pageDesc
     * @param response
     */
    @RequestMapping(value="/usertasks/{usercode}",method={RequestMethod.GET})
    public void getUserTasksList(@PathVariable String usercode,PageDesc pageDesc,HttpServletResponse response){
        List<UserTask> taskList=flowEng.listUserTasks(usercode, pageDesc);
        resData = new ResponseMapData();
        resData.addResponseData("objList", taskList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    /**
     * 获取指定用户任务已办
     * @param usercode
     * @param pageDesc
     * @param response
     */
    @RequestMapping(value="/usertasksfin/{usercode}",method={RequestMethod.GET})
    public void getUserTasksFinList(@PathVariable String usercode,PageDesc pageDesc,HttpServletResponse response){
        Map<String,Object> filter = new HashMap<String,Object>();
        filter.put("userCode",usercode);
        List<UserTask> taskList=flowEng.listUserCompleteTasks(filter,pageDesc);
        resData = new ResponseMapData();
        resData.addResponseData("objList", taskList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    /**
     * 获取指定用户委托列表
     * @param userCode
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/getrelegates/{userCode}",method={RequestMethod.GET})
    public void getRelegateList(@PathVariable String userCode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByUser(userCode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    /**
     * 获取指定用户委托列表
     * @param userCode
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/getRelegateSetList/{userCode}",method={RequestMethod.GET})
    public void getRelegateSetList(@PathVariable String userCode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByGrantor(userCode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
    /**
     * 更新委托
     * @param relegateno
     * @param roleRelegate
     * @param request
     * @param response
     */
    @RequestMapping(value="/relegate/{relegateno}",method={RequestMethod.PUT})
    public void saveRelegate(@PathVariable Long relegateno, @Valid RoleRelegate roleRelegate, HttpServletRequest request, HttpServletResponse response){
        flowManager.saveRoleRelegate(roleRelegate);

        JsonResultUtils.writeSingleDataJson("",response);
    }



    /**
     * 根据id删除委托
     * @param relegateno
     * @param request
     * @param response
     */
    @RequestMapping(value="/relegate/{relegateno}",method={RequestMethod.DELETE})
    public void deleteRelegate(@PathVariable Long relegateno,HttpServletRequest request,HttpServletResponse response){
        flowManager.deleteRoleRelegate(relegateno);
        JsonResultUtils.writeSingleDataJson("",response);
    }


    /**
     * 获取当前用户 指定状态的关注流程
     * @param instState
     * @param response
     */
    @RequestMapping(value = "/getAttentions/{instState}/{userCode}",method = RequestMethod.GET)
    public void getAttentionsByLoginUser(@PathVariable String instState,@PathVariable String userCode,HttpServletResponse response,HttpServletRequest request){
        List<FlowInstance> flowInstances = flowEng.viewAttentionFLowInstance(userCode,instState);
        JsonResultUtils.writeSingleDataJson(flowInstances,response);
    }

    /**
     * 获取当前用户 指定状态的关注流程,并且根据业务名称过滤
     * @param flowOptName
     * @param response
     */
    @RequestMapping(value = "/getAttentionsByOptName/{instState}/{userCode}",method = RequestMethod.GET)
    public void getAttentionsByLoginUserAndOptName(@PathVariable String userCode,String flowOptName,@PathVariable String instState,HttpServletRequest request,HttpServletResponse response){
        List<FlowInstance> flowInstances = flowEng.viewAttentionFLowInstanceByOptName(flowOptName,userCode,instState);
        JsonResultUtils.writeSingleDataJson(flowInstances,response);
    }
    /**
     * 获取指定用户 指定状态的关注流程
     * @param userCode
     * @param instState
     * @param response
     */
    @RequestMapping(value = "/getAttentions/{userCode}/{instState}",method = RequestMethod.GET)
    public void getAttentions(@PathVariable String userCode,@PathVariable String instState,HttpServletResponse response){
        List<FlowInstance> flowInstances = flowEng.viewAttentionFLowInstance(userCode,instState);
        JsonResultUtils.writeSingleDataJson(flowInstances,response);
    }
}
