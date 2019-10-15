package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.service.FlowOptService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:46:03
 */
@Controller
//@RequestMapping("/wfOpt")
@RequestMapping("/flow/opt")
public class FlowOptController extends BaseController {

    private ResponseMapData resData=new ResponseMapData();

    @Resource
    private FlowOptService wfOptService;

    //工作流--流程定义--业务模块取值
    @RequestMapping("flow_listFlowOptInfo")
    public void getListFlowOptInfo(HttpServletRequest request, HttpServletResponse response){
        List<FlowOptInfo> listFlowOptInfo = wfOptService.getListOptInfo();
        JsonResultUtils.writeSingleDataJson(listFlowOptInfo, response);
    }

    @RequestMapping("/listOptInfo")
    public void listOptInfo(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);

        JSONArray objList = wfOptService.listOptInfo(filterMap,pageDesc);
        resData.addResponseData(OBJLIST, objList);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping("/getOptById")
    public void getOptById(String optId, HttpServletRequest request, HttpServletResponse response){
        FlowOptInfo FlowOptInfo = wfOptService.getOptById(optId);
        JsonResultUtils.writeSingleDataJson(FlowOptInfo,response);
    }

    @RequestMapping("/deleteOptInfoById")
    public void deleteOptInfoById(String optId, HttpServletRequest request, HttpServletResponse response){
        wfOptService.deleteOptInfoById(optId);
        JsonResultUtils.writeSuccessJson(response);
    }

    @RequestMapping("/saveOpt")
    public void saveOpt(@RequestBody FlowOptInfo FlowOptInfo, HttpServletRequest request, HttpServletResponse response){
        wfOptService.saveOpt(FlowOptInfo);
        JsonResultUtils.writeBlankJson(response);
    }

    //根据optId获取表wf_optdef中数据，分页功能没有加！
    @RequestMapping("/getListOptDefById")
    public void getListOptDefById(String optId, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);

//        List<FlowOptDef> objList = wfOptService.getListOptDefById(optId, filterMap,pageDesc);
        List<FlowOptPage> objList = wfOptService.ListOptDef(filterMap,pageDesc);
        resData.addResponseData(OBJLIST, objList);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping("/getOptDefByCode")
    public void getOptDefByCode(String optCode, HttpServletRequest request, HttpServletResponse response){
        FlowOptPage FlowOptDef = wfOptService.getOptDefByCode(optCode);
        JsonResultUtils.writeSingleDataJson(FlowOptDef,response);
    }

    @RequestMapping("/saveOptDef")
    public void saveOptDef(@RequestBody FlowOptPage FlowOptDef, HttpServletRequest request, HttpServletResponse response){
        wfOptService.saveOptDef(FlowOptDef);
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping("/deleteOptDefByCode")
    public void deleteOptDefByCode(String optCode, HttpServletRequest request, HttpServletResponse response){
        wfOptService.deleteOptDefByCode(optCode);
        JsonResultUtils.writeSuccessJson(response);
    }

    @RequestMapping(
            value = {"/createOptInfo"},
            method = {RequestMethod.GET}
    )
    public void getNextOptInfoId(HttpServletResponse response) {
        String optInfoId = wfOptService.getOptInfoSequenceId();
        FlowOptInfo copy = new FlowOptInfo();
        copy.setOptId(optInfoId);
        copy.setUpdateDate(new Date());
        JsonResultUtils.writeSingleDataJson(copy, response);
    }

    @RequestMapping("createOptDef")
    public void getNextOptDefId(String optId, HttpServletResponse response) {
        String optDefCode = wfOptService.getOptDefSequenceId();
        FlowOptPage copy = new FlowOptPage();
        copy.setOptCode(optDefCode);
        copy.setOptId(optId);
        copy.setUpdateDate(new Date());
        JsonResultUtils.writeSingleDataJson(copy, response);
    }

    @RequestMapping(
            value = {"/{optId}"},
            method = {RequestMethod.GET}
    )
    public void getOptInfoById(@PathVariable String optId, HttpServletResponse response) {
        FlowOptInfo FlowOptInfo = this.wfOptService.getFlowOptInfoById(optId);
        JsonResultUtils.writeSingleDataJson(FlowOptInfo, response);
    }

    @RequestMapping("/saveOptDefs")
    public void saveOptDefs(@RequestBody JSONObject paramData, HttpServletRequest request, HttpServletResponse response){
        JSONArray optDefs = paramData.getJSONArray("optDefs");
        for (int i=0; i<optDefs.size(); i++){
            FlowOptPage flowOptDef = optDefs.getObject(i, FlowOptPage.class);
            wfOptService.saveOptDef(flowOptDef);
        }

        JsonResultUtils.writeBlankJson(response);
    }
}
