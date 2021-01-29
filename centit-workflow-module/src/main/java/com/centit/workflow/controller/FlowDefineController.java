package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.algorithm.UuidOpt;
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
public class FlowDefineController extends BaseController {
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
    public List<FlowInfo> listFlowByOpt(@PathVariable String optId) {
        return flowDefine.listFlowsByOptId(optId);
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
    public PageQueryResult<FlowInfo> listFlowAllVisionByCode(PageDesc pageDesc, @PathVariable String flowcode) {
        List<FlowInfo> listObjects = flowDefine.listFlowsByCode(flowcode, pageDesc);
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
        return flowDefine.getFlowInfo(flowcode);
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
     * @param flowcode
     * @param response
     */
    @ApiOperation(value = "查询单个流程草稿", notes = "查询单个流程草稿")
    @RequestMapping(value = "/draft/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public void getFlowDefineDraft(@PathVariable String flowcode, HttpServletResponse response) {
        FlowInfo obj = flowDefine.getFlowInfo(flowcode, 0);
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
            return flowDefine.getFlowInfo(flowcode, version);
        } else {
            return flowDefine.getFlowInfo(flowcode);
        }
    }

    /**
     * 复制单个流程某个版本
     *
     * @param version 版本号
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/copy/{flowcode}/{version}", method = RequestMethod.POST)
    public void copyFlowDefine(@PathVariable String flowcode, @PathVariable Long version, HttpServletResponse response) {
        FlowInfo obj = flowDefine.getFlowInfo(flowcode, version);
        FlowInfo copy = new FlowInfo();
        copy.copyNotNullProperty(obj);
        copy.setCid(new FlowInfoId(0L, UuidOpt.getUuidAsString22()));
        JsonResultUtils.writeSingleDataJson(copy, response);
    }

    /**
     * 编辑流程图
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/savexmldesc/{flowcode}", method = RequestMethod.POST)
    public void editXml(@PathVariable String flowcode, HttpServletRequest request, HttpServletResponse response) {
        String flowxmldesc = request.getParameter("flowxmldesc");
        boolean saveSucced = flowDefine.saveDraftFlowDefJson(flowcode, flowxmldesc);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("流程图草稿草稿保存成功！", response);
        else
            JsonResultUtils.writeSingleDataJson("保存出错！", response);
    }

    /**
     * 查看流程图
     * @param version 版本号
     * @param flowcode 流程代码
     */
    @ApiOperation(value = "查看流程图",notes = "查看流程图")
    @RequestMapping(value = "/viewxml/{flowcode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String viewXml(@PathVariable Long version, @PathVariable String flowcode) {
        FlowInfo obj = flowDefine.getFlowInfo(flowcode, version);
        return obj.getFlowXmlDesc();
    }

    /**
     * 保存草稿
     * @param flowdefine 流程定义信息
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "新建流程的基本信息",notes = "新建流程的基本信息")
    @RequestMapping(method = RequestMethod.POST)
    public void addFlowDefine(@Valid FlowInfo flowdefine, HttpServletResponse response) {
        boolean saveSucced = flowDefine.saveDraftFlowDef(flowdefine);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeSingleDataJson("保存出错！", response);
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine 流程定义信息
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "修改流程的基本信息",notes = "修改流程的基本信息")
    @RequestMapping(value = "/{flowcode}", method = RequestMethod.POST)
    public void editFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);
        boolean saveSucced = flowDefine.saveDraftFlowDef(flowdefine);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
        //新建流程之后，发送流程定义信息到业务系统
        //FlowOptUtils.sendFlowInfo(flowdefine);
    }

    /**
     * 新增流程阶段，默认编辑草稿，版本号为0
     * @param flowdefine 流程定义信息
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "保存流程的阶段信息",notes = "保存流程的阶段信息，阶段信息作为流程信息的属性flowStages封装")
    @WrapUpResponseBody
    @PostMapping(value = "/stage/{flowcode}")
    public ResponseData editFlowStage(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowStages()) {
            for (FlowStage stage : flowdefine.getFlowStages()) {
                if (null == stage.getStageId()) {
                    stage.setStageId(UuidOpt.getUuidAsString32());
//                    stage.setStageId(flowDefine.getNextStageId());
                }
                stage.setFlowDefine(flowdefine);
            }
        }
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDefine.saveDraftFlowStage(flowdefine);

        if (saveSucced) {
            return ResponseData.makeResponseData(flowdefine);
        } else {
            return ResponseData.makeErrorMessage("流程的阶段信息保存失败！");
        }
    }

    /**
     * 编辑流程角色
     * @param flowdefine 流程定义信息
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "编辑流程角色信息",notes = "编辑流程角色信息")
    @PostMapping(value = "/role/{flowcode}")
    public void editRole(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {

        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDefine.saveDraftFlowRole(flowdefine);

        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 编辑流程变量
     * @param flowdefine 流程定义信息
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "编辑流程变量", notes = "编辑流程变量")
    @PostMapping(value = "/variableDefine/{flowcode}")
    @WrapUpResponseBody
    public void editVariable(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);

        boolean saveSucced = flowDefine.saveDraftFlowVariableDef(flowdefine);

        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine 流程定义信息
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "更新流程信息", notes = "更新流程信息doCopyXML为是否保留旧的流程图信息")
    @RequestMapping(value = "/{oldflowcode}/{doCopyXML}", method = RequestMethod.POST)
    public void editCopyFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String oldflowcode, @PathVariable String doCopyXML, HttpServletResponse response) {
        if ("F".equals(doCopyXML)) {
            flowDefine.saveDraftFlowDef(flowdefine);
        } else if ("T".equals(doCopyXML)) {
            FlowInfo oldFlowDef = flowDefine.getFlowInfo(oldflowcode, 0);
            flowdefine.setFlowXmlDesc(oldFlowDef.getFlowXmlDesc());
            flowDefine.saveDraftFlowDef(flowdefine);
        }
        //新建流程之后，发送流程定义信息到业务系统
        //FlowOptUtils.sendFlowInfo(flowdefine);
    }

    /**
     * 删除流程
     * @param version 版本号
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "删除流程", notes = "删除流程")
    @RequestMapping(value = "/{flowcode}/{version}", method = RequestMethod.DELETE)
    public void deleteFlowDefine(@PathVariable String flowcode, @PathVariable Long version, HttpServletResponse response) {
        FlowInfo obj = flowDefine.getFlowInfo(flowcode, version);
        if (null == obj) {
            JsonResultUtils.writeErrorMessageJson("此流程不存在", response);
            return;
        } else {
            flowDefine.disableFlow(flowcode);
        }
    }

    /**
     * 物理删除流程定义
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "物理删除流程定义", notes = "物理删除流程定义")
    @RequestMapping(value = "/deleteFlow/{flowcode}", method = RequestMethod.GET)
    public void deleteFlowDefine(@PathVariable String flowcode, HttpServletResponse response) {
        flowDefine.deleteFlowDef(flowcode);
        JsonResultUtils.writeSuccessJson(response);
    }

    /**
     * 发布新版本流程
     *
     * @param flowcode 流程代码
     * @param response HttpServletResponse
     * @throws Exception 异常
     */
    @ApiOperation(value = "发布流程定义", notes = "发布流程定义")
    @RequestMapping(value = "/publish/{flowcode}", method = RequestMethod.POST)
    public void publishFlow(@PathVariable String flowcode, HttpServletResponse response){
        flowDefine.publishFlowDef(flowcode);
        JsonResultUtils.writeSingleDataJson("已发布！", response);
    }

    /**
     * 更新流程状态
     * @param flowcode 流程代码
     * @param newstate 新的状态
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "更新流程状态", notes = "更新流程状态")
    @RequestMapping(value = "/changestate/{flowcode}/{newstate}", method = RequestMethod.GET)
    public void changeState(@PathVariable String flowcode, @PathVariable String newstate, HttpServletResponse response) {
        if ("D".equals(newstate)) {
            flowDefine.disableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经禁用！", response);
        }
        if ("B".equals(newstate)) {
            flowDefine.enableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经启用！", response);
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
     * @param flowcode 流程代码
     */
    @ApiOperation(value = "查询流程图页面需要的数据字典及相关数据")
    @RequestMapping(value = "/getdatamap/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    @Transactional
    public Map<String, Map<String, String>> getDataMap(@PathVariable String flowcode) {
        Map<String, Map<String, String>> map = flowDefine.listAllRole();
        //办件角色重新赋值为当前流程中的办件角色，不再使用系统的
        Map<String, String> bjMap = new LinkedHashMap<>();
        bjMap.put("", "请选择");
        bjMap.putAll(flowDefine.listFlowItemRoles(flowcode, 0L));
        map.put(SysUserFilterEngine.ROLE_TYPE_ITEM /*"bj"*/, bjMap);

//        Map<String, String> spMap = new LinkedHashMap<>();
//        spMap.put("", "请选择");
//        spMap.putAll(flowDefine.getRoleMapByFlowCode);
//        map.put("SP",spMap);
        // 分配机制
        Map<String, String> map2 = flowDefine.listAllOptType();
        map.put("OptType", map2);
        // 操作定义
        Map<String, String> map3 = new LinkedHashMap<>();
        map3.put("", "请选择");
        map3.putAll(flowDefine.listAllOptCode(flowcode));
        map.put("OptCode", map3);
        // 子流程
        Map<String, String> map4 = flowDefine.listAllSubFlow();
        map.put("SubWfcode", map4);
        Map<String, String> stageMap = flowDefine.listFlowStages(flowcode, 0L);
        map.put("FlowPhase", stageMap);
        // 流程变量
        Map<String, String> flowVariableDefineMap = flowDefine.listFlowVariableDefines(flowcode, 0L);
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
    public Map<String, Map<String, String>> listAllRole(){
        return flowDefine.listAllRole();
    }

    @ApiOperation(value = "角色名称和类别对应列表")
    @RequestMapping(value = "/listRoleByType/{roleType}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> listRoleByType(@PathVariable String roleType){
        return flowDefine.listRoleByType(roleType);
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
    @ApiOperation("根据流程阶段id删除流程阶段")
    public void deleteFlowStageById(@PathVariable String stageId) {
        flowDefine.deleteFlowStageById(stageId);
    }

}
