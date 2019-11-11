package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.service.FlowOptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:46:03
 */
@Controller
@Api(value = "流程业务",
    tags = "流程业务")
@RequestMapping("/flow/opt")
public class FlowOptController extends BaseController {

    @Autowired
    private FlowOptService wfOptService;

    @Autowired
    private IntegrationEnvironment integrationEnvironmen;

    @ApiOperation(value = "获取业务系统列表", notes = "获取业务系统列表")
    @WrapUpResponseBody
    @RequestMapping(value="/oslist" ,method = RequestMethod.GET)
    public List<OsInfo> listAllOs(){
        return integrationEnvironmen.listOsInfos();
    }

    //工作流--流程定义--业务模块取值
    @ApiOperation(value = "获取业务列表", notes = "获取业务列表")
    @WrapUpResponseBody
    @RequestMapping(value="/allOptInfos" ,method = RequestMethod.GET)
    public List<FlowOptInfo> listFlowOptInfo(){
         return wfOptService.getListOptInfo();
    }

    @ApiOperation(value = "分页获取业务列表", notes = "分页获取业务列表")
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public PageQueryResult listOptInfo(PageDesc pageDesc, HttpServletRequest request){
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        JSONArray objList = wfOptService.listOptInfo(filterMap,pageDesc);
        return PageQueryResult.createResult(objList,pageDesc);
    }

    @ApiOperation(value = "获取指定业务", notes = "获取指定业务")
    @WrapUpResponseBody
    @RequestMapping(value = "/{optId}", method = RequestMethod.GET)
    public FlowOptInfo getOptInfoById(@PathVariable String optId) {
        return this.wfOptService.getFlowOptInfoById(optId);
    }

    @ApiOperation(value = "删除业务", notes = "删除业务")
    @WrapUpResponseBody
    @RequestMapping(value="/{optId}",method = RequestMethod.DELETE)
    public void deleteOptInfoById(@PathVariable String optId){
        wfOptService.deleteOptInfoById(optId);
    }

    @ApiOperation(value = "保存新业务", notes = "保存新业务")
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public void saveNewOptInfo(@RequestBody FlowOptInfo FlowOptInfo){
        wfOptService.saveOptInfo(FlowOptInfo);
    }

    @ApiOperation(value = "更改业务", notes = "更改业务")
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public void updateOptInfo(@RequestBody FlowOptInfo FlowOptInfo){
        wfOptService.saveOptInfo(FlowOptInfo);
    }

    //根据optId获取表wf_optdef中数据，分页功能没有加！
    @ApiOperation(value = "根据optId获取流程页面,用于编辑包括交互的和自动运行的",
        notes = "根据optId获取流程页面,用于编辑包括交互的和自动运行的")
    @WrapUpResponseBody
    @RequestMapping(value="/pages/{optId}",method = RequestMethod.GET)
    public List<FlowOptPage> listOptPagesForEdit(@PathVariable String optId){
        return wfOptService.listAllOptPageById(optId);
    }

    @ApiOperation(value = "根据optId获取流程页面,用于流程定义", notes = "根据optId获取流程页面,用于流程定义")
    @WrapUpResponseBody
    @RequestMapping(value="/views/{optId}",method = RequestMethod.GET)
    public List<FlowOptPage> listOptPagesById(@PathVariable String optId){
        return wfOptService.listOptPageById(optId);
    }

    @ApiOperation(value = "根据optId获取业务自动操作业务", notes = "根据optId获取业务自动操作业务")
    @WrapUpResponseBody
    @RequestMapping(value="/autos/{optId}",method = RequestMethod.GET)
    public List<FlowOptPage> listOptAutoRunById(@PathVariable String optId){
        return wfOptService.listOptAutoRunById(optId);
    }

    @ApiOperation(value = "根据optCode获取流程页面", notes = "根据optCode获取流程页面")
    @WrapUpResponseBody
    @RequestMapping(value="/page/{optCode}",method = RequestMethod.GET)
    public FlowOptPage getOptDefByCode(@PathVariable String optCode){
        return wfOptService.getOptPageByCode(optCode);
    }


    @ApiOperation(value = "删除流程页面", notes = "删除流程页面")
    @RequestMapping(value = "/page/{optCode}", method = RequestMethod.DELETE)
    @WrapUpResponseBody
    public void deleteOptDefByCode(@PathVariable String optCode){
        wfOptService.deleteOptPageByCode(optCode);
    }

    @RequestMapping(value = "/newId", method = RequestMethod.GET)
    public void getNextOptInfoId(HttpServletResponse response) {
        String optInfoId = wfOptService.getOptInfoSequenceId();
        FlowOptInfo copy = new FlowOptInfo();
        copy.setOptId(optInfoId);
        copy.setUpdateDate(new Date());
        JsonResultUtils.writeSingleDataJson(copy, response);
    }

    @RequestMapping("/newOptCode")
    public void getNextOptDefId(String optId, HttpServletResponse response) {
        String optDefCode = wfOptService.getOptDefSequenceId();
        FlowOptPage copy = new FlowOptPage();
        copy.setOptCode(optDefCode);
        copy.setOptId(optId);
        copy.setUpdateDate(new Date());
        JsonResultUtils.writeSingleDataJson(copy, response);
    }


    @ApiOperation(value = "保存流程页面", notes = "保存流程页面")
    @WrapUpResponseBody
    @RequestMapping(value="/page", method = RequestMethod.POST)
    public void saveOptDef(@RequestBody FlowOptPage optPage){
        wfOptService.saveOptPage(optPage);
    }

    @ApiOperation(value = "批量保存流程页面", notes = "批量保存流程页面")
    @WrapUpResponseBody
    @RequestMapping(value="/pages",method = RequestMethod.POST)
    public void saveOptPages(@RequestBody List<FlowOptPage> optPages){
        if(optPages==null || optPages.size()==0){
            return;
        }
        for(FlowOptPage optPage : optPages){
            wfOptService.saveOptPage(optPage);
        }
    }
}
