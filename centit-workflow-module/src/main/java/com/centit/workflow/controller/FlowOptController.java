package com.centit.workflow.controller;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OptInfo;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowOptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "获取业务系统列表", notes = "获取业务系统列表")
    @WrapUpResponseBody
    @RequestMapping(value="/oslist" ,method = RequestMethod.GET)
    public List<OsInfo> listAllOs(HttpServletRequest request){
        return CodeRepositoryUtil.listOsInfo(
            WebOptUtils.getCurrentTopUnit(request));
    }

    @GetMapping("/roles/{optId}")
    @WrapUpResponseBody
    @ApiOperation("根据optId获取角色定义列表")
    public PageQueryResult<OptTeamRole> listOptTeamRolesByOptId(@PathVariable String optId, PageDesc pageDesc) {
        Map<String, Object> filter = CollectionsOpt.createHashMap("optId", optId);
        List<OptTeamRole> optTeamRoles = wfOptService.listOptTeamRolesByFilter(filter, pageDesc);
        return PageQueryResult.createResult(optTeamRoles, pageDesc);
    }

    @GetMapping("/allRoles/{optId}")
    @WrapUpResponseBody
    @ApiOperation("根据optId获取角色定义列表")
    public PageQueryResult<OptTeamRole> listAllOptTeamRolesByOptId(
        @PathVariable String optId, PageDesc pageDesc, HttpServletRequest request) {
        pageDesc.setPageSize(-1);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        List<OptInfo> allOptInfos = platformEnvironment.listAllOptInfo(topUnit);
        List<OptTeamRole> tyRoles = new ArrayList<OptTeamRole>();
        OptInfo optInfo = null;
        String tyOptId = "";
        boolean flag = true;
        for(OptInfo obj : allOptInfos){
            if(obj.getOptId().equals(optId)){
                optInfo = obj;
            }
        }
        if(optInfo != null){
            for(OptInfo obj : allOptInfos){
                if(obj.getTopOptId().equals(optInfo.getTopOptId()) && obj.getOptName().equals(OptInfo.OPT_INFO_FORM_CODE_COMMON_NAME)){
                    tyOptId = obj.getOptId();
                    if(tyOptId.equals(optId)){
                        flag = false;
                    }
                }
            }
        }
        if(flag){
            Map<String, Object> filter = CollectionsOpt.createHashMap("optId", tyOptId);
            tyRoles = wfOptService.listOptTeamRolesByFilter(filter, pageDesc);
        }
        Map<String, Object> filter = CollectionsOpt.createHashMap("optId", optId);
        List<OptTeamRole> optTeamRoles = wfOptService.listOptTeamRolesByFilter(filter, pageDesc);
        optTeamRoles.addAll(tyRoles);
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
    @ApiOperation(value = "批量更新办件角色的业务模块")
    @PutMapping(value = "/batchUpdateTeamByOptId")
    @WrapUpResponseBody
    public JSONObject batchUpdateTeamByOptId(String optId , @RequestBody List<String> optTeamRoleIds) {
        int[] optTeamRoleArr = wfOptService.batchUpdateTeamByOptId(optId, optTeamRoleIds);
        JSONObject result = new JSONObject();
        result.put("optTeamRoleArr",optTeamRoleIds);
        return result;
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

    @GetMapping("/allVariables/{optId}")
    @WrapUpResponseBody
    @ApiOperation("根据optId获取变量定义列表")
    public PageQueryResult<OptVariableDefine> listAllOptVariableDefinesByOptId(
        @PathVariable String optId, PageDesc pageDesc, HttpServletRequest request) {
        pageDesc.setPageSize(-1);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        List<OptInfo> allOptInfos = platformEnvironment.listAllOptInfo(topUnit);
        List<OptVariableDefine> tyVariables = new ArrayList<>();
        OptInfo optInfo = null;
        String tyOptId = "";
        boolean flag = true;
        for(OptInfo obj : allOptInfos){
            if(obj.getOptId().equals(optId)){
                optInfo = (OptInfo) obj;
            }
        }
        if(optInfo != null){
            for(OptInfo obj : allOptInfos){
                if(obj.getTopOptId().equals(optInfo.getTopOptId()) && obj.getOptName().equals(optInfo.OPT_INFO_FORM_CODE_COMMON_NAME)){
                    tyOptId = obj.getOptId();
                    if(tyOptId.equals(optId)){
                        flag = false;
                    }
                }
            }
        }
        if(flag){
            Map<String, Object> filter = CollectionsOpt.createHashMap("optId", tyOptId);
            tyVariables = wfOptService.listOptVariableDefinesByFilter(filter, pageDesc);
        }

        Map<String, Object> filter = CollectionsOpt.createHashMap("optId", optId);
        List<OptVariableDefine> optVariableDefines = wfOptService.listOptVariableDefinesByFilter(filter, pageDesc);
        optVariableDefines.addAll(tyVariables);
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

    @ApiOperation(value = "批量更新流程变量的业务模块")
    @PutMapping(value = "/batchUpdateVariableByOptId")
    @WrapUpResponseBody
    public JSONObject batchUpdateVariableByOptId(String optId , @RequestBody List<String> optVariableIds) {
        int[] optVariableRoleArr = wfOptService.batchUpdateVariableByOptId(optId, optVariableIds);
        JSONObject result = new JSONObject();
        result.put("optVariableRoleArr",optVariableIds);
        return result;
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
