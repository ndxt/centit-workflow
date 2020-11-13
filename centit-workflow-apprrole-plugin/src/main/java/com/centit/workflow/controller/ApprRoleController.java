package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.IRoleInfo;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.ApprRole;
import com.centit.workflow.po.ApprRoleDefine;
import com.centit.workflow.service.ApprRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@Api(value = "审批角色",
    tags = "审批角色接口类")
@RequestMapping("/appr/role")
public class ApprRoleController extends BaseController {

    @Autowired
    private ApprRoleService apprRoleService;

    @ApiOperation(value = "审批角色列表", notes = "审批角色列表")
    @GetMapping()
    @WrapUpResponseBody
    public PageQueryResult<ApprRole> listAllApprRole(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> filterMap = collectRequestParameters(request);
        List<ApprRole> objList = apprRoleService.listApprRoles(filterMap, pageDesc);
        return PageQueryResult.createResult(objList, pageDesc);
    }

    @ApiOperation(value = "查询单个审批角色", notes = "查询单个审批角色")
    @GetMapping("/{roleCode}")
    public void getApprRoleByCode(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        ApprRole apprRole = apprRoleService.getApprRoleByCode(roleCode);
        JsonResultUtils.writeSingleDataJson(apprRole, response);
    }

    @ApiOperation(value = "保存审批角色", notes = "保存审批角色")
    @PostMapping()
    public void saveApprRole(@RequestBody ApprRole apprRole, HttpServletRequest request, HttpServletResponse response){
        apprRoleService.saveApprRole(apprRole);
        JsonResultUtils.writeBlankJson(response);
    }

    @ApiOperation(value = "删除审批角色", notes = "删除审批角色")
    @DeleteMapping("/{roleCode}")
    public void deleteApprRoleByCode(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response) {
        apprRoleService.deleteApprRoleByCode(roleCode);
        JsonResultUtils.writeSuccessJson(response);
    }

    @ApiOperation(value = "更新审批角色", notes = "更新审批角色。")
    @PutMapping()
    @WrapUpResponseBody
    public void updateModule(@RequestBody ApprRole apprRole){
        apprRoleService.updateApprRole(apprRole);
    }


    @ApiOperation(value = "获取审批角色的明细", notes = "获取审批角色的明细")
    @GetMapping("/define/{roleCode}")
    public void getApprRoleDefineListByCode(@PathVariable String roleCode, HttpServletResponse response) {
        List<ApprRoleDefine> apprRoleDefineList = this.apprRoleService.getApprRoleDefineListByCode(roleCode);
        changeRoleCodeToRoleName(apprRoleDefineList);
        ApprRole apprRole = new ApprRole();
        apprRole.setApprRoleDefineList(apprRoleDefineList);
        JsonResultUtils.writeSingleDataJson(apprRole, response);
    }

    @ApiOperation(value = "根据ID删除审批角色明细", notes = "根据ID删除审批角色明细")
    @DeleteMapping({"/define/{id}"})
    public void deleteApprRoleDefineByCode(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        this.apprRoleService.deleteApprRoleDefineById(id);
        JsonResultUtils.writeSuccessJson(response);
    }

    @ApiOperation(value = "保存审批角色明细列表", notes = "保存审批角色明细列表")
    @PutMapping({"/define/list"})
    public void saveApprRoleDefineList(@RequestBody JSONObject paramData, HttpServletRequest request, HttpServletResponse response) {
        JSONArray apprRoleDefineList = paramData.getJSONArray("apprRoleDefineList");
        String roleCode = null;
        for(int i = 0; i < apprRoleDefineList.size(); ++i) {
            ApprRoleDefine apprRoleDefine = apprRoleDefineList.getObject(i, ApprRoleDefine.class);
            if (null == roleCode) {
                roleCode = apprRoleDefine.getRoleCode();
            }
            this.apprRoleService.saveApprRoleDefine(apprRoleDefine);
        }
        this.apprRoleService.updateFormula(roleCode);
        JsonResultUtils.writeBlankJson(response);
    }

    @ApiOperation(value = "同步审批角色到权限表达式", notes = "同步审批角色到权限表达式。")
    @GetMapping("/sync")
    @WrapUpResponseBody
    public Boolean syncApprRoleToFormula() {
        return this.apprRoleService.syncApprRoleToFormula();
    }

    private void changeRoleCodeToRoleName(List<ApprRoleDefine> apprRoleDefineList) {
        for (ApprRoleDefine apprRoleDefine : apprRoleDefineList) {
            if ("xz".equals(apprRoleDefine.getRelatedType())) {
                apprRoleDefine.setRelatedType("行政职务");
                apprRoleDefine.setRelatedName(CodeRepositoryUtil.getValue("RankType", apprRoleDefine.getRelatedCode()));
            } else if ("js".equals(apprRoleDefine.getRelatedType())) {
                apprRoleDefine.setRelatedType("用户角色");
                IRoleInfo roleInfo = CodeRepositoryUtil.getRoleByRoleCode(apprRoleDefine.getRelatedCode());
                if (null != roleInfo) {
                    apprRoleDefine.setRelatedName(roleInfo.getRoleName());
                }
            }
        }
    }
}
