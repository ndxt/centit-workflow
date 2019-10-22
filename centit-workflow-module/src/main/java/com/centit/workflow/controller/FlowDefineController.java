package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@Api(value = "流程定义",
    tags = "流程定义接口类")
@RequestMapping("/flow/define")
public class FlowDefineController extends BaseController {
    //public static final Logger logger = LoggerFactory.getLogger(SampleFlowDefineController.class);

    @Autowired
    private com.centit.workflow.service.FlowDefine flowDef;

    /*
     * 列举系统中的所有流程，只显示最新版本的
     */
    @ApiOperation(value = "流程定义列表", notes = "列出所有的流程定义列表")
    @GetMapping(value = "/listFlow")
    @WrapUpResponseBody
    public PageQueryResult<FlowInfo> listFlow(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<FlowInfo> listObjects = flowDef.listLastVersionFlow(searchColumn, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }


    /*
     * 列举系统中的所有流程，只显示最新版本的
     */
    @WrapUpResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public PageQueryResult<FlowInfo> list(PageDesc pageDesc, HttpServletRequest request) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<FlowInfo> listObjects = flowDef.listLastVersionFlow(searchColumn, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    /**
     * 某个流程的所有版本
     * // field    过滤域
     * @param pageDesc 分页
     * @param flowcode 流程号
     */
    @ApiOperation(value = "列出所有的流程定义列表", notes = "列出所有的流程定义列表")
    @WrapUpResponseBody
    @RequestMapping(value = "/allversions/{flowcode}", method = RequestMethod.GET)
    public PageQueryResult<FlowInfo> listAllVersionFlow(PageDesc pageDesc, @PathVariable String flowcode) {
        List<FlowInfo> listObjects = flowDef.getFlowsByCode(flowcode, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    /**
     * 某个流程的最新版本
     *
     * @param flowcode 流程号
     */
    @ApiOperation(value = "获取流程最新版本信息", notes = "获取流程最新版本信息")
    @RequestMapping(value = "/lastversion/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo listLastVersion(@PathVariable String flowcode) {
        return flowDef.getFlowDefObject(flowcode);
    }

    @RequestMapping(value = "/editfromthis/{flowCode}/{version}", method = RequestMethod.POST)
    public void editFromThis(@PathVariable String flowCode, @PathVariable long version, HttpServletRequest request, HttpServletResponse response) {
        FlowInfo flowDefine = flowDef.getFlowDefObject(flowCode, version);
        FlowInfo flowDefine_thisversion = flowDef.getFlowDefObject(flowCode, 0);
        FlowInfo copy = new FlowInfo();
        copy.copyNotNullProperty(flowDefine_thisversion);
        copy.setFlowXmlDesc(flowDefine.getFlowXmlDesc());
        boolean saveSucced = flowDef.saveDraftFlowDef(copy);
        JsonResultUtils.writeSingleDataJson(saveSucced, response);
    }

    /**
     * 查询单个流程草稿
     *
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/draft/{flowcode}", method = RequestMethod.GET)
    public void getFlowDefineDraft(@PathVariable String flowcode, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowDefObject(flowcode, 0);

        JsonResultUtils.writeSingleDataJson(obj, response);
    }

    /**
     * 查询单个流程某个版本
     *
     * @param version
     * @param flowcode
     * @param response
     */
    @ApiOperation(value = "获取流程指定版本信息", notes = "获取流程指定版本信息")
    @RequestMapping(value = "/{version}/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo getFlowDefine(@PathVariable Long version, @PathVariable String flowcode, HttpServletResponse response) {
        if(version >= 0) {
            return flowDef.getFlowDefObject(flowcode, version);
        } else {
            return flowDef.getFlowDefObject(flowcode);
        }
    }


    /**
     * 复制单个流程某个版本
     *
     * @param version
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/copy/{flowcode}/{version}", method = RequestMethod.POST)
    public void copyFlowDefine(@PathVariable String flowcode, @PathVariable Long version, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowDefObject(flowcode, version);
        FlowInfo copy = new FlowInfo();
        copy.copyNotNullProperty(obj);
        copy.setCid(new FlowInfoId(0L, flowDef.getNextPrimarykey()));
        JsonResultUtils.writeSingleDataJson(copy, response);
    }


    /**
     * 编辑流程图
     *
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/savexmldesc/{flowcode}", method = RequestMethod.POST)
    public void editXml(@PathVariable String flowcode, HttpServletRequest request, HttpServletResponse response) {
        String flowxmldesc = request.getParameter("flowxmldesc");
        boolean saveSucced = flowDef.saveDraftFlowDefXML(flowcode, flowxmldesc);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("流程图草稿草稿保存成功！", response);
        else
            JsonResultUtils.writeSingleDataJson("保存出错！", response);
    }

    /**
     * 查看流程图
     *
     * @param version
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/viewxml/{flowcode}/{version}", method = RequestMethod.GET)
    public void viewXml(@PathVariable Long version, @PathVariable String flowcode, HttpServletRequest request, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowDefObject(flowcode, version);
        JsonResultUtils.writeSingleDataJson(obj.getFlowXmlDesc(), response);
    }

    /**
     * 保存草稿
     *
     * @param flowdefine
     * @param response
     */
    @RequestMapping(method = RequestMethod.POST)
    public void addFlowDefine(@Valid FlowInfo flowdefine, HttpServletResponse response) {
        boolean saveSucced = flowDef.saveDraftFlowDef(flowdefine);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeSingleDataJson("保存出错！", response);
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/{flowcode}", method = RequestMethod.POST)
    public void editFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);
        boolean saveSucced = flowDef.saveDraftFlowDef(flowdefine);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
        //新建流程之后，发送流程定义信息到业务系统
        //FlowOptUtils.sendFlowInfo(flowdefine);
    }

    /**
     * 新增流程阶段，默认编辑草稿，版本号为0
     *
     * @param flowdefine
     * @param flowcode
     * @param response
     */
    @PostMapping(value = "/stage/{flowcode}")
    public void editFlowStage(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowStages()) {
            for (FlowStage stage : flowdefine.getFlowStages()) {
                if (null == stage.getStageId()) {
                    stage.setStageId(UuidOpt.getUuidAsString32());
//                    stage.setStageId(flowDef.getNextStageId());
                }
                stage.setFlowDefine(flowdefine);
            }
        }
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDef.saveDraftFlowStage(flowdefine);

        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 编辑流程角色
     *
     * @param flowdefine
     * @param flowcode
     * @param response
     */
    @PostMapping(value = "/role/{flowcode}")
    public void editRole(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowTeamRoles()) {
            for (FlowTeamRole role : flowdefine.getFlowTeamRoles()) {
                if (null == role.getFlowTeamRoleId()) {
                    role.setFlowTeamRoleId(UuidOpt.getUuidAsString32());
//                    role.setFlowTeamRoleId(flowDef.getNextRoleId());
                }
                role.setFlowDefine(flowdefine);
            }
        }
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDef.saveDraftFlowRole(flowdefine);

        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 编辑流程变量
     *
     * @param flowdefine
     * @param flowcode
     * @param response
     */
    @PostMapping(value = "/variableDefine/{flowcode}")
    public void editVariable(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowVariableDefines()) {
            for (FlowVariableDefine variableDefine : flowdefine.getFlowVariableDefines()) {
                if (null == variableDefine.getFlowVariableId()) {
                    variableDefine.setFlowVariableId(UuidOpt.getUuidAsString32());
//                    variableDefine.setFlowVariableId(flowDef.getNextVariableDefId());
                }
                variableDefine.setFlowDefine(flowdefine);
            }
        }
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDef.saveDraftFlowVariableDef(flowdefine);

        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine
     * @param response
     */
    @RequestMapping(value = "/{oldflowcode}/{doCopyXML}", method = RequestMethod.POST)
    public void editCopyFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String oldflowcode, @PathVariable String doCopyXML, HttpServletResponse response) {
        FlowInfo oldFlowDef = flowDef.getFlowDefObject(oldflowcode, 0);
        if ("F".equals(doCopyXML)) {
            flowDef.saveDraftFlowDef(flowdefine);
        } else if ("T".equals(doCopyXML)) {
            flowdefine.setFlowXmlDesc(oldFlowDef.getFlowXmlDesc());
            flowDef.saveDraftFlowDef(flowdefine);
        }
        //新建流程之后，发送流程定义信息到业务系统
        //FlowOptUtils.sendFlowInfo(flowdefine);
    }


    /**
     * 删除流程
     *
     * @param version
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/{flowcode}/{version}", method = RequestMethod.DELETE)
    public void deleteFlowDefine(@PathVariable String flowcode, @PathVariable Long version, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowDefObject(flowcode, version);
        if (null == obj) {
            JsonResultUtils.writeErrorMessageJson("此流程不存在", response);
            return;
        } else {
            flowDef.disableFlow(flowcode);
        }
    }

    /**
     * 物理删除流程定义
     *
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/deleteFlow/{flowcode}", method = RequestMethod.GET)
    public void deleteFlowDefine(@PathVariable String flowcode, HttpServletResponse response) {
        flowDef.deleteFlowDef(flowcode);
        JsonResultUtils.writeSuccessJson(response);
    }


    /**
     * 发布新版本流程
     *
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/publish/{flowcode}", method = RequestMethod.POST)
    public void publishFlow(@PathVariable String flowcode, HttpServletResponse response) throws Exception {
        flowDef.publishFlowDef(flowcode);
        JsonResultUtils.writeSingleDataJson("已发布！", response);
    }


    /**
     * 更新流程状态
     *
     * @param flowcode
     * @param newstate
     * @param response
     */
    @RequestMapping(value = "/changestate/{flowcode}/{newstate}", method = RequestMethod.GET)
    public void changeState(@PathVariable String flowcode, @PathVariable String newstate, HttpServletResponse response) {
        if ("D".equals(newstate)) {
            flowDef.disableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经禁用！", response);
        }
        if ("B".equals(newstate)) {
            flowDef.enableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经启用！", response);
        }
    }


    /**
     * 返回一个带id的空流程
     *
     * @param response
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public void getNextId(HttpServletResponse response) {
        FlowInfo copy = new FlowInfo();
        copy.setCid(new FlowInfoId(0L, flowDef.getNextPrimarykey()));
        JsonResultUtils.writeSingleDataJson(copy, response);
    }

    /**
     * 编辑流程图页面需要的数据字典及相关数据
     *
     * @param flowcode
     * @param response
     */
    @ApiOperation(value = "查询流程图页面需要的数据字典及相关数据")
    @RequestMapping(value = "/getdatamap/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    @Transactional
    public void getDataMap(@PathVariable String flowcode, HttpServletResponse response) {
        Map<String, Map<String, String>> map = flowDef.listAllRole();
        //办件角色重新赋值为当前流程中的办件角色，不再使用系统的
        Map<String, String> bjMap = new LinkedHashMap<>();
        bjMap.put("", "请选择");
        bjMap.putAll(flowDef.listFlowItemRoles(flowcode, 0L));
        map.put(SysUserFilterEngine.ROLE_TYPE_ITEM.toLowerCase() /*"bj"*/, bjMap);

//        Map<String, String> spMap = new LinkedHashMap<>();
//        spMap.put("", "请选择");
//        spMap.putAll(flowDef.getRoleMapByFlowCode);
//        map.put("SP",spMap);
        // 分配机制
        Map<String, String> map2 = flowDef.listAllOptType();
        map.put("OptType", map2);
        // 操作定义
        Map<String, String> map3 = new LinkedHashMap<>();
        map3.put("", "请选择");
        map3.putAll(flowDef.listAllOptCode(flowcode));
        map.put("OptCode", map3);
        // 子流程
        Map<String, String> map4 = flowDef.listAllSubFlow();
        map.put("SubWfcode", map4);
        Map<String, String> stageMap = flowDef.listFlowStages(flowcode, 0L);
        map.put("FlowPhase", stageMap);
        // 流程变量
        Map<String, String> flowVariableDefineMap = flowDef.listFlowVariableDefines(flowcode, 0L);
        map.put("FlowVariableDefine", flowVariableDefineMap);
        JsonResultUtils.writeSingleDataJson(map, response);
    }
    @ApiOperation(value = "查询内置的流程机构表达式")
    @RequestMapping(value = "/listInsideUnitExp", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listInsideUnitExp(){
        return flowDef.listInsideUnitExp();
    }

    @ApiOperation(value = "查询角色类别")
    @RequestMapping(value = "/listRoleType", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listRoleType(){
        return flowDef.listRoleType();
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/listAllRole", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, Map<String, String>> listAllRole(){
        return flowDef.listAllRole();
    }

    @ApiOperation(value = "角色名称和类别对应列表")
    @RequestMapping(value = "/listRoleByType/{roleType}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listAllRole(@PathVariable String roleType){
        return flowDef.listRoleByType(roleType);
    }

    @ApiOperation(value = "列举流程办件角色")
    @RequestMapping(value = "/itemrole/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowItemRoles(@PathVariable String flowCode, @PathVariable Long version){
        return flowDef.listFlowItemRoles(flowCode, version);
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/variable/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowVariable(@PathVariable String flowCode, @PathVariable Long version){
        return flowDef.listFlowVariableDefines(flowCode, version);
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/stage/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowStage(@PathVariable String flowCode, @PathVariable Long version){
        return flowDef.listFlowStages(flowCode, version);
    }

}
