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
     * 获取单个用户信息
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{userCode}", method = RequestMethod.GET)
    public void getUserInfo(@PathVariable String userCode, HttpServletResponse response) {
        IUserUnit primaryUnit= CodeRepositoryUtil.getUserPrimaryUnit(userCode);
        IUserInfo userDetails = CodeRepositoryUtil.getUserInfoByCode(userCode);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(IUserInfo.class,new String[]{"userUnits","userRoles"});
        resData = new ResponseMapData();
        resData.addResponseData("userInfo", userDetails);
        resData.addResponseData("primaryUnit", primaryUnit);
        JsonResultUtils.writeResponseDataAsJson(resData,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }


    /**
     * 获取当前登录用户
     * @param request HttpServletReqeust
     * @param response HttpServletResponse
     */
    @RequestMapping(value="/loginuser",method={RequestMethod.GET})
    public void getLoginUserCode(HttpServletResponse response,HttpServletRequest request){
        String userCode=WebOptUtils.getCurrentUserCode(request);
        IUserUnit primaryUnit=CodeRepositoryUtil.getUserPrimaryUnit(userCode);
        IUserInfo userDetails =  CodeRepositoryUtil.getUserInfoByCode(userCode);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(IUserInfo.class,new String[]{"userUnits","userRoles"});
        resData = new ResponseMapData();
        resData.addResponseData("userInfo", userDetails);
        resData.addResponseData("primaryUnit", primaryUnit);
        JsonResultUtils.writeResponseDataAsJson(resData,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }




    /**
     * 获取登陆用户机构列表
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/userunits",method={RequestMethod.GET})
    public void getSysUserUnitsList(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        String usercode=WebOptUtils.getCurrentUserCode(request);
        List<? extends IUserUnit> userUnitList =  CodeRepositoryUtil.listUserUnits(usercode);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(IUserInfo.class,new String[]{"userUnits","userRoles"});
        resData = new ResponseMapData();
        resData.addResponseData("objList", userUnitList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }

    /**
     * 登陆用户任务待办
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/usertasks",method={RequestMethod.GET})
    public void getSysUserTasksList(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        String usercode=WebOptUtils.getCurrentUserCode(request);
        List<UserTask> taskList=flowEng.listUserTasks(usercode, pageDesc);
        resData = new ResponseMapData();
        resData.addResponseData("objList", taskList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
    /**
     * 获取指定用户机构列表
     * @param pageDesc
     * @param userCode
     * @param request
     * @param response
     */
    @RequestMapping(value="/userunits/{userCode}",method={RequestMethod.GET})
    public void getUserUnitsList(PageDesc pageDesc,@PathVariable String userCode,HttpServletRequest request,HttpServletResponse response){
        List<? extends IUserUnit> userUnitList =  CodeRepositoryUtil.listUserUnits(userCode);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(IUserInfo.class,new String[]{"userUnits","userRoles"});
        resData = new ResponseMapData();
        resData.addResponseData("objList", userUnitList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
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
     * @param usercode
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/getrelegates/{usercode}",method={RequestMethod.GET})
    public void getRelegateList(@PathVariable String usercode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByUser(usercode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
    /**
     * 获取当前用户委托列表
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/getrelegates",method={RequestMethod.GET})
    public void getRelegateListByLoginUser(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        String usercode = WebOptUtils.getCurrentUserCode(request);
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByUser(usercode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }

    /**
     * 获取指定用户委托列表
     * @param usercode
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/setrelegates/{usercode}",method={RequestMethod.GET})
    public void getRelegateSetList(@PathVariable String usercode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByGrantor(usercode);
        resData = new ResponseMapData();
        resData.addResponseData("objList", relegateList);
        resData.addResponseData("pageDesc", pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
    /**
     * 获取当前用户委托列表
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping(value="/setrelegates",method={RequestMethod.GET})
    public void getRelegateSetListByLoginUser(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        String usercode = WebOptUtils.getCurrentUserCode(request);
        List<RoleRelegate> relegateList=flowManager.listRoleRelegateByGrantor(usercode);
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
     * 委托转移
     * @param relegateno
     * @param request
     * @param response
     */
    @RequestMapping(value="/alignrelegate/{relegateno}/{userCode}",method={RequestMethod.PUT})
    public void saveRelegate1(@PathVariable Long relegateno,@PathVariable String userCode,HttpServletRequest request,HttpServletResponse response){
        RoleRelegate re=flowManager.getRoleRelegateById(relegateno);
        re.setGrantor(WebOptUtils.getCurrentUserCode(request));
        re.setGrantee(userCode);
        flowManager.saveRoleRelegate(re);
        JsonResultUtils.writeSingleDataJson(re,response);
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
    @RequestMapping(value = "/getAttentions/{instState}",method = RequestMethod.GET)
    public void getAttentionsByLoginUser(@PathVariable String instState,HttpServletResponse response,HttpServletRequest request){
        String userCode=WebOptUtils.getCurrentUserCode(request);
        List<FlowInstance> flowInstances = flowEng.viewAttentionFLowInstance(userCode,instState);
        JsonResultUtils.writeSingleDataJson(flowInstances,response);
    }

    /**
     * 获取当前用户 指定状态的关注流程,并且根据业务名称过滤
     * @param flowOptName
     * @param response
     */
    @RequestMapping(value = "/getAttentionsByOptName/{instState}",method = RequestMethod.GET)
    public void getAttentionsByLoginUserAndOptName(String flowOptName,@PathVariable String instState,HttpServletRequest request,HttpServletResponse response){
        String userCode=WebOptUtils.getCurrentUserCode(request);
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
