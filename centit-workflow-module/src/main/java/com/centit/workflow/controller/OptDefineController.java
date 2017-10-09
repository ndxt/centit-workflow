package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.workflow.po.OptNode;
import com.centit.workflow.service.impl.OptDefineImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Controller
@RequestMapping("/flow/optdefine")
public class OptDefineController extends BaseController {
    @Resource
    private OptDefineImpl optDefine;

    private ResponseMapData resData = new ResponseMapData();

    @RequestMapping(value = "/listOptNodeByOptId/{optId}")
    public void listOptNodeByOptId(@PathVariable String optId, HttpServletRequest request, HttpServletResponse response){
        List<OptNode> optNodes = optDefine.listOptNodeByOptId(optId);
        resData.addResponseData(OBJLIST,optNodes);
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
}
