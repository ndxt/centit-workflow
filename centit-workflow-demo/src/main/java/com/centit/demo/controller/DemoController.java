package com.centit.demo.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by chen_rj on 2018-4-27.
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private FlowManager flowManager;

    @Autowired
    private FlowDefine flowDefineClient;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @RequestMapping(value = "/demoStart")
    public void demoStart(CreateFlowOptions startPo, HttpServletResponse response) throws Exception {
        flowEngine.createInstance(startPo);
        JsonResultUtils.writeMessageJson("流程创建成功",response);
    }

    @RequestMapping(value = "/demoSubmit")
    public void demoSubmit(SubmitOptOptions submitPo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        flowEngine.submitOpt(submitPo);
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping(value = "/listTasks", method = RequestMethod.GET)
    public void listTaskByUserCode(String userCode,HttpServletResponse response,HttpServletRequest request){
        List<UserTask> userTasks = flowEngine.listTasks(
            CollectionsOpt.createHashMap("userCode", userCode), new PageDesc(-1,-1));
        JsonResultUtils.writeSingleDataJson(userTasks,response);
    }

    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    public void listAllUser(HttpServletResponse response){
        Object userList = platformEnvironment.listAllUsers();
        JsonResultUtils.writeSingleDataJson(userList,response);
    }

    @RequestMapping(value = "/listAllFlow", method = RequestMethod.GET)
    public void listAllFlow(HttpServletResponse response){
        List<FlowInfo> flowInfos = flowDefineClient.listLastVersionFlow(
            CollectionsOpt.createHashMap(), new PageDesc(-1,-1)
        );
        JsonResultUtils.writeSingleDataJson(flowInfos,response);
    }
}
