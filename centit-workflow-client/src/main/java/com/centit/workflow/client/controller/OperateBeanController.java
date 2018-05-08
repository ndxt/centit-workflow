package com.centit.workflow.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.workflow.commons.NodeEventSupport;
import com.centit.workflow.commons.WorkflowException;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.NodeInfo;
import com.centit.workflow.po.NodeInstance;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */

public abstract class OperateBeanController extends BaseController{

    abstract ResponseData doCallBack(Map<String,Object> paramMap );

    abstract ResponseData getFlowVariable();


    @RequestMapping(value = "/callBackFunc", method= RequestMethod.POST)
    @ResponseBody
    public ResponseData callBackFunc(@RequestBody Map<String,Object> paramMap ){
        return  doCallBack(paramMap);
    }

    @RequestMapping(value = "/getFlowVariable", method= RequestMethod.GET)
    @ResponseBody
    public ResponseData callFlowVariable(){
        return  getFlowVariable();
    }
}
