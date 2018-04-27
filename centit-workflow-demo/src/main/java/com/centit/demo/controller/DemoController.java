package com.centit.demo.controller;

import com.centit.demo.po.DemoStartPo;
import com.centit.demo.po.DemoSubmitPo;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.client.service.FlowEngineClient;
import com.centit.workflow.client.service.impl.FlowManagerClientImpl;
import com.centit.workflow.po.UserTask;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by chen_rj on 2018-4-27.
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private FlowEngineClient flowEngine;
    @Resource
    private FlowManagerClientImpl flowManager;

    @Resource
    private PlatformEnvironment platformEnvironment;

    @RequestMapping(value = "/demoStart", method = RequestMethod.PUT)
    public void demoStart(DemoStartPo startPo, HttpServletResponse response) throws Exception {
        flowEngine.createInstance(startPo.getFlowCode(),startPo.getFlowOptName(),startPo.getFlowOptTag(),
                startPo.getUserCode(),startPo.getUnitCode());
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping(value = "/demoSubmit")
    public void demoSubmit(DemoSubmitPo submitPo,HttpServletResponse response,HttpServletRequest request) throws Exception {
        flowEngine.submitOpt(submitPo.getNodeInstId(),submitPo.getUserCode(),submitPo.getUnitCode(),
                submitPo.getVarTrans(),request.getServletContext());
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping(value = "/listTasks", method = RequestMethod.GET)
    public void listTaskByUserCode(String userCode,HttpServletResponse response,HttpServletRequest request){
        List<UserTask> userTasks = flowEngine.listUserTasks(userCode,new PageDesc(-1,-1));
        JsonResultUtils.writeSingleDataJson(userTasks,response);
    }

    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    public void listAllUser(HttpServletResponse response){
        Object userList = platformEnvironment.listAllUsers();
        JsonResultUtils.writeSingleDataJson(userList,response);
    }
}
