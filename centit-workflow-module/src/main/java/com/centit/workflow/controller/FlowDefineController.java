package com.centit.workflow.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.product.oa.team.utils.ResourceBaseController;
import com.centit.product.oa.team.utils.ResourceLock;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.VariableFormula;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowInfoId;
import com.centit.workflow.po.FlowStage;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.RoleFormulaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
public class FlowDefineController extends ResourceBaseController {
    //public static final Logger logger = LoggerFactory.getLogger(SampleFlowDefineController.class);

    @Autowired
    private FlowDefine flowDefine;

    @Autowired
    private RoleFormulaService roleFormulaService;

    /*
     * 列举系统中的所有流程，只显示最新版本的
     */
    @ApiOperation(value = "流程定义列表", notes = "列出所有的流程定义列表")
    @GetMapping(value = "/listFlow")
    @WrapUpResponseBody
    public PageQueryResult<FlowInfo> listFlow(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<FlowInfo> listObjects = flowDefine.listLastVersionFlow(searchColumn, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    /*
     * 列举系统中的所有流程，只显示最新版本的
     */
    @ApiOperation(value = "列举流程业务相关流程", notes = "列举流程业务相关流程")
    @WrapUpResponseBody
    @GetMapping(value = "/optFlow/{optId}")
    public ResponseData listFlowByOpt(@PathVariable String optId, HttpServletRequest request) {
        JSONArray jsonArray = flowDefine.listFlowsByOptId(optId);
        if (jsonArray == null) {
            return ResponseData.makeErrorMessage(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.data_not_found", request));
        }
        return ResponseData.makeResponseData(jsonArray);
    }

    /*
     * 列举应用中的所有流程，只显示最新版本的
     */
    @ApiOperation(value = "列举流程业务相关流程", notes = "列举流程业务相关流程")
    @WrapUpResponseBody
    @GetMapping(value = "/osFlow/{osId}")
    public ResponseData listFlowByOs(@PathVariable String osId, HttpServletRequest request) {
        JSONArray jsonArray = flowDefine.listFlowByOsId(osId);
        if (jsonArray == null) {
            return ResponseData.makeErrorMessage(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.data_not_found", request));
        }
        return ResponseData.makeResponseData(jsonArray);
    }

    /*
     * 列举系统中的所有流程，只显示最新版本的(包含通用模块中的流程)
     */
    @ApiOperation(value = "列举流程业务相关流程(包含通用模块中的流程)", notes = "列举流程业务相关流程(包含通用模块中的流程)")
    @WrapUpResponseBody
    @GetMapping(value = "/optAllFlow/{optId}")
    public ResponseData listAllFlowByOpt(@PathVariable String optId, HttpServletRequest request) {
        JSONArray jsonArray = flowDefine.listAllFlowsByOptId(optId);
        if (jsonArray == null) {
            return ResponseData.makeErrorMessage(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.data_not_found", request));
        }
        return ResponseData.makeResponseData(jsonArray);
    }

    /**
     * 某个流程的所有版本
     * // field    过滤域
     * @param pageDesc 分页
     * @param flowCode 流程号
     */
    @ApiOperation(value = "根据流程编码列出所有的流程定义列表", notes = "根据流程编码列出所有的流程定义列表")
    @WrapUpResponseBody
    @RequestMapping(value = "/allversions/{flowCode}", method = RequestMethod.GET)
    public PageQueryResult<FlowInfo> listFlowAllVisionByCode(PageDesc pageDesc, @PathVariable String flowCode) {
        List<FlowInfo> listObjects = flowDefine.listFlowsByCode(flowCode, pageDesc);
        return PageQueryResult.createResult(listObjects, pageDesc);
    }

    /**
     * 某个流程的最新版本
     *
     * @param flowCode 流程号
     */
    @ApiOperation(value = "获取流程最新版本信息", notes = "获取流程最新版本信息")
    @RequestMapping(value = "/lastversion/{flowCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo listLastVersion(@PathVariable String flowCode) {
        return flowDefine.getFlowInfo(flowCode);
    }

    @RequestMapping(value = "/editfromthis/{flowCode}/{version}", method = RequestMethod.POST)
    public void editFromThis(@PathVariable String flowCode, @PathVariable long version, HttpServletRequest request, HttpServletResponse response) {
        FlowInfo flowDefine = this.flowDefine.getFlowInfo(flowCode, version);
        FlowInfo flowDefine_thisversion = this.flowDefine.getFlowInfo(flowCode, 0);
        FlowInfo copy = new FlowInfo();
        copy.copyNotNullProperty(flowDefine_thisversion);
        copy.setFlowXmlDesc(flowDefine.getFlowXmlDesc());
        boolean saveSucced = this.flowDefine.saveDraftFlowDef(copy);
        JsonResultUtils.writeSingleDataJson(saveSucced, response);
    }

    /**
     * 查询单个流程草稿
     *
     * @param flowCode
     * @param response
     */
    @ApiOperation(value = "查询单个流程草稿", notes = "查询单个流程草稿")
    @RequestMapping(value = "/draft/{flowCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo getFlowDefineDraft(@PathVariable String flowCode, HttpServletResponse response) {
        FlowInfo flowInfo = flowDefine.getFlowInfo(flowCode, 0);
        if (flowInfo == null) {
            flowInfo = new FlowInfo();
        }
        return flowInfo;
    }

    /**
     * 查询单个流程某个版本
     *
     * @param version
     * @param flowCode
     * @param response
     */
    @ApiOperation(value = "获取流程指定版本信息", notes = "获取流程指定版本信息")
    @RequestMapping(value = "/{version}/{flowCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo getFlowDefine(@PathVariable Long version, @PathVariable String flowCode, HttpServletResponse response) {
        if(version >= 0) {
            return flowDefine.getFlowInfo(flowCode, version);
        } else {
            return flowDefine.getFlowInfo(flowCode);
        }
    }

    /**
     * 复制单个流程某个版本
     *
     * @param version 版本号
     * @param flowCode 流程代码
     */
    @RequestMapping(value = "/copy/{flowCode}/{version}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public FlowInfo copyFlowDefine(@PathVariable String flowCode, @PathVariable Long version) {
        FlowInfo obj = flowDefine.getFlowInfo(flowCode, version);
        FlowInfo copy = new FlowInfo();
        copy.copyNotNullProperty(obj);
        copy.setCid(new FlowInfoId(0L, UuidOpt.getUuidAsString22()));
        return copy;// .writeSingleDataJson(copy, response);
    }

    /**
     * 编辑流程图
     * @param flowCode 流程代码
     * @param request HttpServletResponse
     */
    @RequestMapping(value = "/savexmldesc/{flowCode}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void saveXmlDesc(@PathVariable String flowCode, HttpServletRequest request) {
        String flowxmldesc = request.getParameter("flowxmldesc");
        FlowInfo wfDefine = new FlowInfo();
        wfDefine.setFlowCode(flowCode);
        wfDefine.setFlowXmlDesc(flowxmldesc);
        wfDefine.setFlowName(request.getParameter("flowName"));
        wfDefine.setFlowDesc(request.getParameter("flowDesc"));
        wfDefine.setExpireOpt(request.getParameter("expireOpt"));
        wfDefine.setExpireCallApi(request.getParameter("expireCallApi"));
        wfDefine.setWarningParam(request.getParameter("warningParam"));
        wfDefine.setTimeLimit(request.getParameter("timeLimit"));

        flowDefine.saveDraftFlowDef(wfDefine);
       /* if (saveSucced) {
            return ResponseData.successResponse;
            //JsonResultUtils.writeSingleDataJson("流程图草稿草稿保存成功！", response);
        } else {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, ""));
            //JsonResultUtils.writeSingleDataJson("保存出错！", response);
        }*/
    }

    /**
     * 复制流程草稿为一个新的流程
     * @param flowCode
     * @param request
     * @param response
     */
    /**
     * 复制流程草稿或版本为一个新的流程
     * @param request
     * @return
     */
    @ApiOperation(value = "复制流程草稿为一个新的流程")
    @ApiImplicitParams({@ApiImplicitParam(name = "flowCode",value = "流程code",paramType = "String",dataTypeClass = String.class,required = true),
        @ApiImplicitParam( name = "version",value = "版本号，不传默认为0", paramType = "String", dataTypeClass = String.class),
        @ApiImplicitParam(name = "flowName",value = "新的流程名",paramType = "String",dataTypeClass = String.class,required = true)})
    @RequestMapping(value = "/copyWorkFlow", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData copyWorkFlow(HttpServletRequest request) {
        Map<String, Object> parameters = collectRequestParameters(request);
        String flowCode = MapUtils.getString(parameters, "flowCode");
        String flowName = MapUtils.getString(parameters, "flowName");
        if (StringUtils.isAnyBlank(flowCode,flowName)){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_FIELD_INPUT_NOT_VALID,
                getI18nMessage("error.701.field_is_blank", request, "flowCode,flowName"));
        }
        String newflowCode = flowDefine.copyWorkFlow(parameters);
        if (StringUtils.isBlank(newflowCode)){
            return ResponseData.makeErrorMessage(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.object_not_found", request, "FlowInfo","flowCode"));

        }
        JSONObject json = new JSONObject();
        json.put("flowCode",newflowCode);
        return ResponseData.makeResponseData(json);
    }

    @ApiOperation(value = "根据流程xml获取流程节点",notes = "根据流程xml获取流程节点")
    @RequestMapping(value = "/getFlowNode/{flowCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getFlowNode(@PathVariable String flowCode, HttpServletRequest request) {
        FlowInfo flowInfo = flowDefine.getFlowInfo(flowCode, 0);
        if (flowInfo == null) {
            return ResponseData.makeErrorMessage(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.data_not_found", request));
            //ResponseData.makeErrorMessage("未获取到流程节点，可能是流程没有保存。");
            //return null;
        }
        JSONArray nodeList = JSONObject.parseObject(new String(flowInfo.getFlowXmlDesc())).getJSONArray("nodeList");
        nodeList.removeIf(
            node -> !"C".equals(((JSONObject) node).getString("nodeType")) ||
                ((JSONObject) node).getString("nodecode") == null ||
                ((JSONObject) node).getString("nodecode").isEmpty()
        );
        return ResponseData.makeResponseData(nodeList);
    }

    /**
     * 查看流程图
     * @param version 版本号
     * @param flowCode 流程代码
     */
    @ApiOperation(value = "查看流程图",notes = "查看流程图")
    @RequestMapping(value = "/viewxml/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String viewXml(@PathVariable Long version, @PathVariable String flowCode) {
        FlowInfo obj = flowDefine.getFlowInfo(flowCode, version);
        String flowXmlDesc = obj == null ? "" : obj.getFlowXmlDesc();
        return flowXmlDesc;
    }

    /**
     * 保存草稿
     * @param flowdefine 流程定义信息
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "新建流程的基本信息",notes = "新建流程的基本信息")
    @RequestMapping(method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData addFlowDefine(@RequestBody FlowInfo flowdefine, HttpServletRequest request) {
        boolean saveSucced = flowDefine.saveDraftFlowDef(flowdefine);
        if (saveSucced) {
            return ResponseData.makeSuccessResponse(
                getI18nMessage("flow.200.save_draft_success", request));
        } else { // saveSucced always true
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "no way"));
        }
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine 流程定义信息
     * @param flowCode 流程代码
     * @param request HttpServletRequest
     */
    @ApiOperation(value = "修改流程的基本信息",notes = "修改流程的基本信息")
    @RequestMapping(value = "/{flowCode}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData editFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String flowCode,
                              HttpServletRequest request) {
        //检查资源
        ResourceLock.lockResource(flowCode, WebOptUtils.getCurrentUserCode(request), request);

        flowdefine.setFlowCode(flowCode);
        flowdefine.setVersion(0l);
        boolean saveSucced = flowDefine.saveDraftFlowDef(flowdefine);
        if (saveSucced) {
            return ResponseData.makeSuccessResponse(
                getI18nMessage("flow.200.save_draft_success", request));
        } else { // saveSucced always true
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "no way"));
        }
    }

    /**
     * 新增流程阶段，默认编辑草稿，版本号为0
     * @param flowdefine 流程定义信息
     * @param flowCode 流程代码
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "保存流程的阶段信息",notes = "保存流程的阶段信息，阶段信息作为流程信息的属性flowStages封装")
    @PostMapping(value = "/stage/{flowCode}")
    @WrapUpResponseBody
    public ResponseData editFlowStage(@Valid FlowInfo flowdefine, @PathVariable String flowCode,
                                      HttpServletRequest request) {
        if (null != flowdefine.getFlowStages()) {
            for (FlowStage stage : flowdefine.getFlowStages()) {
                if (null == stage.getStageId()) {
                    stage.setStageId(UuidOpt.getUuidAsString32());
//                    stage.setStageId(flowDefine.getNextStageId());
                }
                stage.setFlowDefine(flowdefine);
            }
        }
        flowdefine.setFlowCode(flowCode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDefine.saveDraftFlowStage(flowdefine);
        if (saveSucced) {
            return ResponseData.makeResponseData(flowdefine);
        } else {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "flow not found"));
        }
    }

    /**
     * 编辑流程角色
     * @param flowdefine 流程定义信息
     * @param flowCode 流程代码
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "编辑流程角色信息",notes = "编辑流程角色信息")
    @PostMapping(value = "/role/{flowCode}")
    @WrapUpResponseBody
    public ResponseData editRole(@Valid FlowInfo flowdefine, @PathVariable String flowCode,
                                 HttpServletRequest request) {
        flowdefine.setFlowCode(flowCode);
        flowdefine.setVersion(0l);
        boolean saveSucced = flowDefine.saveDraftFlowRole(flowdefine);
        if (saveSucced) {
            return ResponseData.makeSuccessResponse(
                getI18nMessage("flow.200.save_draft_success", request));
        } else {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "flow not found"));
        }
    }

    /**
     * 编辑流程变量
     * @param flowdefine 流程定义信息
     * @param flowCode 流程代码
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "编辑流程变量", notes = "编辑流程变量")
    @PostMapping(value = "/variableDefine/{flowCode}")
    @WrapUpResponseBody
    public ResponseData editVariable(@Valid FlowInfo flowdefine, @PathVariable String flowCode,
                             HttpServletRequest request) {
        flowdefine.setFlowCode(flowCode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDefine.saveDraftFlowVariableDef(flowdefine);
        if (saveSucced) {
            return ResponseData.successResponse;
        } else {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "flow not found"));
        }
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine 流程定义信息
     * @param oldflowCode HttpServletResponse
     * @param doCopyXML HttpServletResponse
     */
    @ApiOperation(value = "更新流程信息", notes = "更新流程信息doCopyXML为是否保留旧的流程图信息")
    @RequestMapping(value = "/{oldflowCode}/{doCopyXML}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void editCopyFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String oldflowCode, @PathVariable String doCopyXML) {
        if ("F".equals(doCopyXML)) {
            flowDefine.saveDraftFlowDef(flowdefine);
        } else if ("T".equals(doCopyXML)) {
            FlowInfo oldFlowDef = flowDefine.getFlowInfo(oldflowCode, 0);
            flowdefine.setFlowXmlDesc(oldFlowDef.getFlowXmlDesc());
            flowDefine.saveDraftFlowDef(flowdefine);
        }
    }

    /**
     * 禁用流程
     * @param version 版本号
     * @param flowCode 流程代码
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "禁用流程", notes = "禁用流程")
    @RequestMapping(value = "/{flowCode}/{version}", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public void deleteFlowDefine(@PathVariable String flowCode, @PathVariable Long version, HttpServletRequest request) {
        FlowInfo obj = flowDefine.getFlowInfo(flowCode, version);
        if (null == obj) {
            throw new ObjectException(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, "flow not found"));
            //JsonResultUtils.writeErrorMessageJson("此流程不存在", response);
        } else {
            flowDefine.disableFlow(flowCode);
        }
    }

    /**
     * 物理删除流程定义
     * @param flowCode 流程代码
     */
    @ApiOperation(value = "物理删除流程定义", notes = "物理删除流程定义")
    @RequestMapping(value = "/deleteFlow/{flowCode}", method = RequestMethod.DELETE)
    @WrapUpResponseBody
    public void deleteFlowDefine(@PathVariable String flowCode) {
        flowDefine.deleteFlowDef(flowCode);
    }

    /**
     * 发布新版本流程
     *
     * @param flowCode 流程代码
     * @param response HttpServletResponse
     * @throws Exception 异常
     */
    @ApiOperation(value = "发布流程定义", notes = "发布流程定义")
    @RequestMapping(value = "/publish/{flowCode}", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void publishFlow(@PathVariable String flowCode, HttpServletResponse response){
        flowDefine.publishFlowDef(flowCode);
        //JsonResultUtils.writeSingleDataJson("已发布！", response);
    }

    /**
     * 更新流程状态
     * @param flowCode 流程代码
     * @param newState 新的状态
     * @param request HttpServletResponse
     */
    @ApiOperation(value = "更新流程状态", notes = "更新流程状态")
    @RequestMapping(value = "/changestate/{flowCode}/{newState}", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData changeState(@PathVariable String flowCode, @PathVariable String newState, HttpServletRequest request) {
        WebOptUtils.assertUserLogin(request);
        if (FlowInfo.FLOW_STATE_FORBIDDEN.equals(newState)) {
            flowDefine.disableFlow(flowCode);
        }
        if (FlowInfo.FLOW_STATE_NORMAL.equals(newState)) {
            flowDefine.enableFlow(flowCode);
        }
        return ResponseData.makeSuccessResponse(getI18nMessage("info.200.success", request));
    }

    /**
     * 批量删除(物理删除)
     * @param flowCodes
     * @return
     */
    @ApiOperation(value = "批量删除流程", notes = "批量删除流程,多个id之间用逗号隔开")
    @RequestMapping(value = "/batchChangeState", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData batchChangeState(@RequestBody String[] flowCodes, HttpServletRequest request){
        if (StringUtils.isAnyEmpty(flowCodes)){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_FIELD_INPUT_NOT_VALID,
                getI18nMessage("error.701.field_is_blank", request, "flowCodes"));
        }
        flowDefine.deleteByCodes(CollectionsOpt.arrayToList(flowCodes));
        return ResponseData.makeSuccessResponse();
    }

    /**
     * 清空回收站
     */
    @ApiOperation(value = "清空回收站", notes = "清空回收站")
    @PostMapping("/clear")
    @WrapUpResponseBody
    public ResponseData clearRecycle(@RequestBody JSONObject params, HttpServletRequest request){
        String osId = params.getString("osId");
        if (StringUtils.isAnyEmpty(osId)){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_FIELD_INPUT_NOT_VALID,
                getI18nMessage("error.701.field_is_blank", request, "osId"));
        }
        try {
            flowDefine.clearRecycle(osId);
            return ResponseData.makeSuccessResponse();
        }catch (Exception e){
            //e.printStackTrace();
            return ResponseData.makeErrorMessage(ResponseData.ERROR_PROCESS_FAILED,
                getI18nMessage("error.704.process_failed", request, e.getMessage()));
        }

    }

    /**
     * 返回一个带id的空流程
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @WrapUpResponseBody
    public FlowInfo getNextId() {
        return new FlowInfo(new FlowInfoId(0L, UuidOpt.getUuidAsString22()), "N");
    }

    /**
     * 编辑流程图页面需要的数据字典及相关数据
     * @param flowCode 流程代码
     */
    @ApiOperation(value = "查询流程图页面需要的数据字典及相关数据")
    @RequestMapping(value = "/getdatamap/{flowCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    @Transactional
    public Map<String, Map<String, String>> getDataMap(@PathVariable String flowCode, HttpServletRequest request){
        WebOptUtils.assertUserLogin(request);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Map<String, String>> map = flowDefine.listAllRole(topUnit);
        //办件角色重新赋值为当前流程中的办件角色，不再使用系统的
        FlowInfo flowInfo = flowDefine.getFlowInfo(flowCode, 0l);
        Map<String, String> bjMap = new LinkedHashMap<>();
        bjMap.put("", "请选择");
        bjMap.putAll(flowDefine.listFlowItemRoles(flowCode, 0L));
        map.put(SysUserFilterEngine.ROLE_TYPE_ITEM /*"bj"*/, bjMap);

//        Map<String, String> spMap = new LinkedHashMap<>();
//        spMap.put("", "请选择");
//        spMap.putAll(flowDefine.getRoleMapByflowCode);
//        map.put("SP",spMap);
        // 分配机制
        Map<String, String> map2 = flowDefine.listAllOptType();
        map.put("OptType", map2);

        // 子流程
        Map<String, String> map4 = flowDefine.listAllSubFlow(flowInfo.getOsId());
        map.put("SubWfcode", map4);
        Map<String, String> stageMap = flowDefine.listFlowStages(flowCode, 0L);
        map.put("FlowPhase", stageMap);
        // 流程变量
        Map<String, String> flowVariableDefineMap = flowDefine.listFlowVariableDefines(flowCode, 0L);
        map.put("FlowVariableDefine", flowVariableDefineMap);
        return map;
    }

    @ApiOperation(value = "查询内置的流程机构表达式")
    @RequestMapping(value = "/listInsideUnitExp", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listInsideUnitExp(){
        return flowDefine.listInsideUnitExp();
    }

    @ApiOperation(value = "查询角色类别")
    @RequestMapping(value = "/listRoleType", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listRoleType(){
        return flowDefine.listRoleType();
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/listAllRole", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, Map<String, String>> listAllRole(HttpServletRequest request){
        WebOptUtils.assertUserLogin(request);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        return flowDefine.listAllRole(topUnit);
    }

    @ApiOperation(value = "角色名称和类别对应列表")
    @RequestMapping(value = "/listRoleByType/{roleType}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listRoleByType(@PathVariable String roleType, HttpServletRequest request){
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        return flowDefine.listRoleByType(roleType, topUnit);
    }

    @ApiOperation(value = "列举流程办件角色")
    @RequestMapping(value = "/itemrole/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowItemRoles(@PathVariable String flowCode, @PathVariable Long version){
        return flowDefine.listFlowItemRoles(flowCode, version);
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/variable/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowVariableDefines(@PathVariable String flowCode, @PathVariable Long version){
        return flowDefine.listFlowVariableDefines(flowCode, version);
    }

    @ApiOperation(value = "列举所有角色")
    @RequestMapping(value = "/stage/{flowCode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listFlowStages(@PathVariable String flowCode, @PathVariable Long version){
        return flowDefine.listFlowStages(flowCode, version);
    }

    @ApiOperation(value = "检查表达式是否符合语法,返回错误位置，0表示无错误")
    @WrapUpResponseBody
    @GetMapping("/checkFormula")
    public Integer checkFormula(String formula){
        return VariableFormula.checkFormula(formula);
    }

    @ApiOperation(value = "测试运行结果")
    @WrapUpResponseBody
    @GetMapping("/testFormula/{flowCode}")
    public Object testFormula(@PathVariable String flowCode,  String formula){
        return VariableFormula.calculate(formula,
            flowDefine.listFlowDefaultVariables(flowCode,0L));
    }

    @ApiOperation(value = "查询流程办件角色对应的用户范围，返回空表示可以选择任意人员", notes = "查询流程办件角色对应的用户范围")
    @WrapUpResponseBody
    @ApiImplicitParams({@ApiImplicitParam(
        name = "flowCode", value="流程代码",
        required = true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "version", value="流程版本号，<1 表示最新版本",
        required= true, paramType = "path", dataType= "Long"
    ),@ApiImplicitParam(
        name = "itemRoleCode", value="办件角色代码",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value="/itemRoleFilter/{flowCode}/{version}/{itemRoleCode}", method = RequestMethod.GET)
    public JSONArray viewRoleFormulaUsers(@PathVariable String flowCode,
                                          @PathVariable Long version,
                                          @PathVariable String itemRoleCode,
                                          HttpServletRequest request){
        if(version == null || version < 1){
            version = flowDefine.getFlowLastVersion(flowCode);
        }
        OptTeamRole itemRole = flowDefine.getFlowItemRole(flowCode, version, itemRoleCode);
        if(StringUtils.isBlank(itemRole.getFormulaCode())){
            return null;
        }
        return roleFormulaService.viewRoleFormulaUsers(
            itemRole.getFormulaCode(),
            WebOptUtils.getCurrentUserCode(request),
            WebOptUtils.getCurrentUnitCode(request));
    }

    @DeleteMapping("/stage/{stageId}")
    @WrapUpResponseBody
    @ApiOperation(value = "根据流程阶段id删除流程阶段")
    public void deleteFlowStageById(@PathVariable String stageId) {
        flowDefine.deleteFlowStageById(stageId);
    }

    @PostMapping("/stage")
    @WrapUpResponseBody
    @ApiOperation(value = "保存流程阶段")
    public void saveFlowStage(@RequestBody FlowStage flowStage) {
        flowDefine.saveFlowStage(flowStage);
    }
    @ApiOperation(value = "修改表单所属业务模块")
    @PutMapping(value = "/batchUpdateOptId")
    @WrapUpResponseBody
    public JSONObject batchUpdateOptId(String optId , @RequestBody List<String> flowCodes) {
        int[] flowDefineArr = flowDefine.batchUpdateOptId(optId, flowCodes);
        JSONObject result = new JSONObject();
        result.put("flowDefineArr",flowDefineArr);
        return result;
    }
}
