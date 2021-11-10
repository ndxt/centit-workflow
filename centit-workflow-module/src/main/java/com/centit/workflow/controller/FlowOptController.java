package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowOptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "获取业务系统列表", notes = "获取业务系统列表")
    @WrapUpResponseBody
    @RequestMapping(value="/oslist" ,method = RequestMethod.GET)
    public List<? extends IOsInfo> listAllOs(HttpServletRequest request){
        return CodeRepositoryUtil.listOsInfo(
            WebOptUtils.getCurrentTopUnit(request));
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


    @GetMapping("/roles/{optId}")
    @WrapUpResponseBody
    @ApiOperation("根据optId获取角色定义列表")
    public PageQueryResult<OptTeamRole> listOptTeamRolesByOptId(@PathVariable String optId, PageDesc pageDesc) {
        Map<String, Object> filter = CollectionsOpt.createHashMap("optId", optId);
        List<OptTeamRole> optTeamRoles = wfOptService.listOptTeamRolesByFilter(filter, pageDesc);
        return PageQueryResult.createResult(optTeamRoles, pageDesc);
    }

    @GetMapping("/role/{roleId}")
    @WrapUpResponseBody
    @ApiOperation("根据主键id获取角色定义")
    public OptTeamRole getOptTeamRoleById(@PathVariable String roleId) {
        return wfOptService.getOptTeamRoleById(roleId);
    }

    @PostMapping("/role")
    @WrapUpResponseBody
    @ApiOperation("保存角色定义")
    public void saveOptTeamRole(@RequestBody OptTeamRole optTeamRole) {
        wfOptService.saveOptTeamRole(optTeamRole);
    }

    @PutMapping("/role")
    @WrapUpResponseBody
    @ApiOperation("更新角色定义")
    public void updateOptTeamRole(@RequestBody OptTeamRole optTeamRole) {
        wfOptService.updateOptTeamRole(optTeamRole);
    }

    @DeleteMapping("/role/{roleId}")
    @WrapUpResponseBody
    @ApiOperation("删除角色定义")
    public void deleteOptTeamRole(@PathVariable String roleId) {
        wfOptService.deleteOptTeamRoleById(roleId);
    }

    @GetMapping("/variables/{optId}")
    @WrapUpResponseBody
    @ApiOperation("根据optId获取变量定义列表")
    public PageQueryResult<OptVariableDefine> listOptVariableDefinesByOptId(@PathVariable String optId, PageDesc pageDesc) {
        Map<String, Object> filter = CollectionsOpt.createHashMap("optId", optId);
        List<OptVariableDefine> optVariableDefines = wfOptService.listOptVariableDefinesByFilter(filter, pageDesc);
        return PageQueryResult.createResult(optVariableDefines, pageDesc);
    }

    @GetMapping("/variable/{variableId}")
    @WrapUpResponseBody
    @ApiOperation("根据主键id获取变量定义")
    public OptVariableDefine getOptVariableDefineById(@PathVariable String variableId) {
        return wfOptService.getOptVariableDefineById(variableId);
    }

    @PostMapping("/variable")
    @WrapUpResponseBody
    @ApiOperation("保存变量定义")
    public void saveOptVariableDefine(@RequestBody OptVariableDefine optVariableDefine) {
        wfOptService.saveOptVariableDefine(optVariableDefine);
    }

    @PutMapping("/variable")
    @WrapUpResponseBody
    @ApiOperation("更新变量定义")
    public void updateOptVariableDefine(@RequestBody OptVariableDefine optVariableDefine) {
        wfOptService.updateOptVariableDefine(optVariableDefine);
    }

    @DeleteMapping("/variable/{variableId}")
    @WrapUpResponseBody
    @ApiOperation("删除变量定义")
    public void deleteOptVariableDefine(@PathVariable String variableId) {
        wfOptService.deleteOptVariableDefineById(variableId);
    }
}
