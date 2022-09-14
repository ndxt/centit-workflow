package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ParamName;
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
    public ResponseData listFlowByOpt(@PathVariable String optId) {
        JSONArray jsonArray = flowDefine.listFlowsByOptId(optId);
        if (jsonArray == null) {
            ResponseData.makeErrorMessage("未查询到相关流程。");
            return null;
        }
        return ResponseData.makeResponseData(jsonArray);
    }

    /*
     * 列举系统中的所有流程，只显示最新版本的(包含通用模块中的流程)
     */
    @ApiOperation(value = "列举流程业务相关流程(包含通用模块中的流程)", notes = "列举流程业务相关流程(包含通用模块中的流程)")
    @WrapUpResponseBody
    @GetMapping(value = "/optAllFlow/{optId}")
    public ResponseData listAllFlowByOpt(@PathVariable String optId) {
        JSONArray jsonArray = flowDefine.listAllFlowsByOptId(optId);
        if (jsonArray == null) {
            ResponseData.makeErrorMessage("未查询到相关流程。");
            return null;
        }
        return ResponseData.makeResponseData(jsonArray);
    }

    /**
     * 某个流程的所有版本
     * // field    过滤域
     * @param pageDesc 分页
     * @param flowcode 流程号
     */
    @ApiOperation(value = "根据流程编码列出所有的流程定义列表", notes = "根据流程编码列出所有的流程定义列表")
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
    public FlowInfo getFlowDefineDraft(@PathVariable String flowcode, HttpServletResponse response) {
        FlowInfo flowInfo = flowDefine.getFlowInfo(flowcode, 0);
        if (flowInfo == null) {
            flowInfo = new FlowInfo();
        }
        return flowInfo;
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
        /*if (flowcode.equals("aUISQ66VRaC-RRDArJHVbQ")) {
            flowxmldesc = "{\"nodeList\":[{\"nodeName\":\"开始\",\"icon\":\"StartIcon\",\"x\":3380,\"width\":50,\"y\":3075,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"A\",\"type\":\"start\",\"nodeId\":\"382b004850b7457596a165231db52597\",\"height\":50},{\"nodeName\":\"结束\",\"icon\":\"EndIcon\",\"x\":3365,\"width\":50,\"y\":3860,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"F\",\"type\":\"end\",\"nodeId\":\"00401472eb194c92bbb866496934cb86\",\"height\":50},{\"nodeName\":\"办公室批分\",\"types\":\"commonly\",\"nodeCode\":\"bgspf\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"type\":\"cdcba4aacca\",\"nodeType\":\"C\",\"OptCode\":\"adb4abb\",\"limitType\":\"C\",\"notice_type\":\"N\",\"optType\":\"A\",\"timeLimit\":\"1D\",\"pageType\":\"F\",\"powerExp\":\"D('jssfgw00000000000000000000000100')XZ('7','8')\",\"x\":3380,\"width\":120,\"nodecode\":\"bgspf\",\"y\":3165,\"roletype\":\"EN\",\"warningrule\":\"P\",\"nodeId\":\"a1d27063f63c4c2e99b8271b63c6d84c\",\"expireopt\":\"Y\",\"height\":50},{\"nodeName\":\"主任批阅\",\"rolecode\":\"zrys\",\"types\":\"commonly\",\"nodeCode\":\"zrpy\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"powerExp\":\"D('jssfgw00000000000000000000000000')XZ('1')\",\"x\":3045,\"width\":120,\"nodecode\":\"zrpy\",\"y\":3295,\"roletype\":\"BJ\",\"nodeId\":\"32288c4e69d845b6a06f02139e28ed07\",\"height\":50,\"optRunType\":\"C\"},{\"nodeName\":\"主办分办\",\"rolecode\":\"csfzr\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"nodecode\":\"zbfb\",\"roletype\":\"SF\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50,\"types\":\"commonly\",\"nodeCode\":\"zbfb\",\"isaccounttime\":\"T\",\"nodeType\":\"C\",\"limitType\":\"C\",\"timeLimit\":\"1D\",\"powerExp\":\"\",\"unitexp\":\"D(ZBXB)\",\"x\":3380,\"width\":120,\"y\":3405,\"warningrule\":\"P\",\"nodeId\":\"2623552ec6484e5fb40a6d0720866279\"},{\"nodeName\":\"二次分办\",\"rolecode\":\"slr\",\"types\":\"commonly\",\"nodeCode\":\"ecfb\",\"inheritType\":\"1\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"H\",\"notice_type\":\"N\",\"x\":3380,\"width\":120,\"nodecode\":\"ecfb\",\"y\":3475,\"roletype\":\"BJ\",\"warningrule\":\"P\",\"nodeId\":\"88e00c34a1e140dc8c78796893b829a7\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"受理\",\"rolecode\":\"slr\",\"types\":\"commonly\",\"nodeCode\":\"sl\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"C\",\"notice_type\":\"N\",\"timeLimit\":\"3D\",\"x\":3380,\"width\":120,\"nodecode\":\"sl\",\"y\":3565,\"roletype\":\"BJ\",\"warningrule\":\"P\",\"nodeId\":\"5f533832396b49ee88d77b3c64125b25\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"二级单位二次批分\",\"types\":\"commonly\",\"nodeCode\":\"ejdwecpf\",\"inheritType\":\"1\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"H\",\"notice_type\":\"N\",\"powerExp\":\"D(ZBXB + 'PFBM')XZ('7')\",\"x\":3550,\"width\":120,\"nodecode\":\"ejdwecpf\",\"y\":3345,\"roletype\":\"EN\",\"warningrule\":\"P\",\"nodeId\":\"3e2f20890601476cbfdc2c74e1eedf53\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"并行\",\"types\":\"parallel\",\"routerType\":\"H\",\"icon\":\"ParallelIcon\",\"x\":3525,\"width\":120,\"y\":3485,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"parallel\",\"nodeId\":\"608060129c4c407bb305d1d88b316668\",\"height\":50},{\"nodeName\":\"二次批分主任\",\"rolecode\":\"eczrpf\",\"types\":\"commonly\",\"nodeCode\":\"ecpfzr\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"C\",\"notice_type\":\"N\",\"timeLimit\":\"1D\",\"x\":3825,\"width\":120,\"nodecode\":\"ecpfzr\",\"y\":3500,\"roletype\":\"BJ\",\"warningrule\":\"P\",\"nodeId\":\"7b11eb79ef164ede875c69771fe81e77\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"协办批分\",\"types\":\"commonly\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"powerExp\":\"D(cursor + 'PFBM')XZ('7')\",\"x\":3800,\"width\":120,\"nodecode\":\"xbpf\",\"y\":3085,\"roletype\":\"EN\",\"nodeId\":\"28ef2f7f1b704d26b74874b6da3b4296\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"协办分办\",\"rolecode\":\"csfzr\",\"types\":\"commonly\",\"nodeCode\":\"xbfb\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"powerExp\":\"D(XBDW)XZ('7')\",\"unitexp\":\"D(cursor)\",\"x\":3775,\"width\":120,\"nodecode\":\"xbfb\",\"y\":3205,\"roletype\":\"SF\",\"singleunitexp\":\"D(cursor)\",\"nodeId\":\"d552cd9b2b8d496484a40ca1d3af453c\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"协办二次分办\",\"rolecode\":\"xbecfb\",\"types\":\"commonly\",\"nodeCode\":\"xbecfb\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"x\":4010,\"width\":120,\"nodecode\":\"xbecfb\",\"y\":3350,\"roletype\":\"BJ\",\"nodeId\":\"7d87b80d817947f78a5f2f1d0374d18c\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"协办意见\",\"rolecode\":\"xbyj\",\"types\":\"commonly\",\"nodeCode\":\"xbyj\",\"inheritType\":\"1\",\"warningparam\":\"\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"x\":3775,\"width\":120,\"nodecode\":\"xbfb\",\"y\":3425,\"roletype\":\"BJ\",\"nodeId\":\"5548282ab6224693a532ef594f58fc6a\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3380,\"width\":120,\"y\":3675,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"e6187f40dabd4f49a10081fc846e7bf6\",\"height\":50},{\"nodeName\":\"承办人办理\",\"types\":\"commonly\",\"nodeCode\":\"cbrbl\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"C\",\"notice_type\":\"N\",\"timeLimit\":\"7D\",\"powerExp\":\"S(U(L),U(C))\",\"x\":3535,\"width\":120,\"nodecode\":\"cbrbl\",\"y\":3715,\"roletype\":\"EN\",\"warningrule\":\"P\",\"nodeId\":\"794eb1f3db6340e4be5bb8e20c121e3b\",\"expireopt\":\"Y\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"审核\",\"rolecode\":\"7\",\"types\":\"commonly\",\"nodeCode\":\"sh\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"unitexp\":\"D(C)\",\"x\":3755,\"width\":120,\"nodecode\":\"sh\",\"y\":3730,\"singleunitexp\":\"D(C)\",\"roletype\":\"XZ\",\"nodeId\":\"300fa5c463cf45be92fb9f56878f28c9\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3675,\"width\":120,\"y\":3845,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"9ed5f59f891e4f05848dcdcdc43d010a\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3850,\"width\":120,\"y\":3295,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"d150eefe08924c9b8afd2a854212ae88\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3160,\"width\":120,\"y\":3475,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"df7fe0546b9e47ee84ea166ab536781a\",\"height\":50},{\"nodeName\":\"多实例\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"D\",\"unitexp\":\"D(C)\",\"x\":3610,\"width\":120,\"y\":3290,\"singleunitexp\":\"D(C)\",\"nodeId\":\"2ed85e903a284f79bd75d0d8c8fd777d\",\"height\":50},{\"nodeName\":\"游离\",\"types\":\"youli\",\"routerType\":\"R\",\"icon\":\"DissociativeIcon\",\"x\":3240,\"width\":120,\"y\":3295,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"youli\",\"nodeId\":\"eea275102b774998a34a2a253e8288dd\",\"height\":50},{\"nodeName\":\"游离\",\"types\":\"youli\",\"routerType\":\"R\",\"icon\":\"DissociativeIcon\",\"x\":3515,\"width\":120,\"y\":3250,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"youli\",\"nodeId\":\"7e24a0894f4f4e619030a741dd75f9a4\",\"height\":50},{\"nodeName\":\"游离\",\"types\":\"youli\",\"routerType\":\"R\",\"icon\":\"DissociativeIcon\",\"x\":3705,\"width\":120,\"y\":3385,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"youli\",\"nodeId\":\"76e89817bd524e53abd43ab3a01e72c1\",\"height\":50},{\"nodeName\":\"游离\",\"types\":\"youli\",\"routerType\":\"R\",\"icon\":\"DissociativeIcon\",\"x\":3645,\"width\":120,\"y\":3500,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"youli\",\"nodeId\":\"a8f8176fa65a4257b1fbb6233e183cbc\",\"height\":50},{\"nodeName\":\"申报用户补正\",\"types\":\"auto\",\"nodeCode\":\"sbyhbz\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"D\",\"type\":\"auto\",\"OptCode\":\"ba4ccdaddd\",\"notice_type\":\"N\",\"autoRunType\":\"C\",\"x\":3185,\"width\":120,\"nodecode\":\"sbyhbz\",\"y\":3765,\"nodeId\":\"3c5a3e210d1c4fd99aa155878ca4d500\",\"optRunType\":\"D\",\"height\":50},{\"nodeName\":\"等待补正完成\",\"rolecode\":\"111\",\"types\":\"synchro\",\"nodeSyncType\":\"M\",\"allunitexp\":[],\"nodeCode\":\"ddbzwc\",\"icon\":\"SynchronizationIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"E\",\"type\":\"synchro\",\"unitexp\":\"\",\"x\":3185,\"width\":120,\"nodecode\":\"ddbzwc\",\"y\":3645,\"messageCode\":\"1111\",\"roletype\":\"SF\",\"nodeId\":\"89c0ce9ff0e04c9199ae8753e0ab86bf\",\"optRunType\":\"E\",\"height\":50},{\"nodeName\":\"游离\",\"types\":\"youli\",\"routerType\":\"R\",\"icon\":\"DissociativeIcon\",\"x\":3230,\"width\":120,\"y\":3150,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"youli\",\"nodeId\":\"8e5743e9844648d4875c4e42d7854fd9\",\"height\":50},{\"nodeName\":\"二级巡视员阅\",\"rolecode\":\"ejxsy\",\"types\":\"commonly\",\"nodeCode\":\"ejxsyy\",\"isaccounttime\":\"T\",\"warningparam\":\"2/3\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"limitType\":\"C\",\"notice_type\":\"N\",\"timeLimit\":\"1D\",\"x\":3060,\"width\":120,\"nodecode\":\"ejxsyy\",\"y\":3150,\"roletype\":\"BJ\",\"warningrule\":\"P\",\"nodeId\":\"e0475681a72e4a7b8607fbe62b2eb5bf\",\"expireopt\":\"Y\",\"optRunType\":\"C\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3380,\"width\":120,\"y\":3330,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"102f252461ad47b48a27f61bc32e29d2\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3755,\"width\":120,\"y\":3580,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"cc9f4820d83545d09bd643c747e207c0\",\"height\":50},{\"nodeName\":\"子流程\",\"types\":\"child\",\"nodeCode\":\"zll\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"S\",\"type\":\"child\",\"notice_type\":\"N\",\"subFlowCode\":\"J3JQFRVkQKGrfI2CbHJQog\",\"x\":3910,\"width\":120,\"nodecode\":\"zlc\",\"y\":3595,\"nodeId\":\"7d4a9be739644772b43a0824d6409238\",\"optRunType\":\"S\",\"height\":50},{\"nodeName\":\"并行\",\"types\":\"parallel\",\"routerType\":\"H\",\"icon\":\"ParallelIcon\",\"x\":3380,\"width\":120,\"y\":3255,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"parallel\",\"nodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"height\":50},{\"nodeName\":\"协办分办\",\"rolecode\":\"csfzr\",\"types\":\"commonly\",\"nodeCode\":\"xbfb\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"C\",\"type\":\"commonly\",\"OptCode\":\"adb4abb\",\"notice_type\":\"N\",\"unitexp\":\"D(cursor)\",\"x\":3715,\"width\":120,\"nodecode\":\"xbfb\",\"y\":3275,\"singleunitexp\":\"D(cursor)\",\"roletype\":\"SF\",\"nodeId\":\"d3b3227efe464fa595b3ea03fb09a1ec\",\"optRunType\":\"A\",\"height\":50},{\"nodeName\":\"结束节点\",\"types\":\"end\",\"icon\":\"EndIcon\",\"x\":3910,\"width\":50,\"y\":3735,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"F\",\"type\":\"end\",\"nodeId\":\"d916be4eb11e40e69e04783fdc31b152\",\"height\":50},{\"nodeName\":\"多实例\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"D\",\"unitexp\":\"D(SW_XB)\",\"x\":3515,\"width\":120,\"y\":3125,\"nodeId\":\"d8cadd3b7f4d4ad6893162ae63409c72\",\"height\":50},{\"nodeName\":\"分支\",\"types\":\"branch\",\"routerType\":\"D\",\"icon\":\"BranchIcon\",\"x\":3630,\"width\":120,\"y\":3100,\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"branch\",\"nodeId\":\"c862ab81ee694784a9398168a0d9272d\",\"height\":50},{\"nodeName\":\"多实例\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"D\",\"unitexp\":\"D(ecxbpf)\",\"x\":3915,\"width\":120,\"y\":3155,\"nodeId\":\"919673c13a03484ea1644a730c992349\",\"height\":50},{\"nodeName\":\"多实例\",\"rolecode\":\"ejxsy\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"U\",\"x\":3155,\"width\":120,\"y\":3150,\"roletype\":\"BJ\",\"nodeId\":\"dbce10c1af0c4277831af67250dc9707\",\"height\":50},{\"nodeName\":\"多实例\",\"rolecode\":\"zrys\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"U\",\"x\":3150,\"width\":120,\"y\":3295,\"roletype\":\"BJ\",\"nodeId\":\"5fe8efcfb86942b78df66611fb7635aa\",\"height\":50},{\"nodeName\":\"多实例\",\"rolecode\":\"eczrpf\",\"types\":\"instance\",\"routerType\":\"G\",\"icon\":\"InstanceIcon\",\"osid\":\"14yQA2odTa2b9dqDcGKMMQ\",\"nodeType\":\"R\",\"type\":\"instance\",\"multiinsttype\":\"U\",\"x\":3725,\"width\":120,\"y\":3500,\"roletype\":\"BJ\",\"nodeId\":\"fa05785164bf40f6b8c5666acef44ae5\",\"height\":50}],\"transList\":[{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"382b004850b7457596a165231db52597\",\"endNodeId\":\"a1d27063f63c4c2e99b8271b63c6d84c\",\"transId\":\"c2599d511802480dbd458a8d04796105\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"3e2f20890601476cbfdc2c74e1eedf53\",\"endNodeId\":\"608060129c4c407bb305d1d88b316668\",\"transId\":\"c4c7c82cbb5047679c9169a4cb9631f2\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"608060129c4c407bb305d1d88b316668\",\"endNodeId\":\"2623552ec6484e5fb40a6d0720866279\",\"transId\":\"1518892ad4ea4189b2ed6c751c247620\",\"transName\":\"通过\",\"type\":\"link\",\"transCondition\":\"WN='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"5f533832396b49ee88d77b3c64125b25\",\"endNodeId\":\"e6187f40dabd4f49a10081fc846e7bf6\",\"transId\":\"02fb6a909ef84099b6e6c17001c3905e\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"e6187f40dabd4f49a10081fc846e7bf6\",\"endNodeId\":\"00401472eb194c92bbb866496934cb86\",\"transId\":\"ce62fcd628084da69072ca92612ba7e5\",\"transName\":\"不予受理\",\"type\":\"link\",\"transCondition\":\"BL='bysl'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"e6187f40dabd4f49a10081fc846e7bf6\",\"endNodeId\":\"794eb1f3db6340e4be5bb8e20c121e3b\",\"transId\":\"dd7cf5a695a44b65aad63237cae0f537\",\"transName\":\"准予受理\",\"type\":\"link\",\"transCondition\":\"BL='zysl'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"300fa5c463cf45be92fb9f56878f28c9\",\"endNodeId\":\"9ed5f59f891e4f05848dcdcdc43d010a\",\"transId\":\"c758262b5713435d81adef1e086687df\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"9ed5f59f891e4f05848dcdcdc43d010a\",\"endNodeId\":\"794eb1f3db6340e4be5bb8e20c121e3b\",\"transId\":\"850fe6df8b16465881fe379dd5ae1fc5\",\"transName\":\"驳回\",\"type\":\"link\",\"transCondition\":\"BL='F'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"9ed5f59f891e4f05848dcdcdc43d010a\",\"endNodeId\":\"00401472eb194c92bbb866496934cb86\",\"transId\":\"77ce157abcc94b4c80dd9cc5f4cc8a80\",\"transName\":\"通过\",\"type\":\"link\",\"transCondition\":\"BL='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"d552cd9b2b8d496484a40ca1d3af453c\",\"endNodeId\":\"d150eefe08924c9b8afd2a854212ae88\",\"transId\":\"fc0cf15524df4e85b3b4ac6745246d3d\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"d150eefe08924c9b8afd2a854212ae88\",\"endNodeId\":\"7d87b80d817947f78a5f2f1d0374d18c\",\"transId\":\"ec6559d7213e43c08453987b9e30c1af\",\"transName\":\"交办\",\"type\":\"link\",\"transCondition\":\"BL='F' && ('7' in(userRoles(xbecfb)) || '8' in(userRoles(xbecfb)))\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"d150eefe08924c9b8afd2a854212ae88\",\"endNodeId\":\"5548282ab6224693a532ef594f58fc6a\",\"transId\":\"03f823fb3b9d4c50995ae6aeb05cc018\",\"transName\":\"自办\",\"type\":\"link\",\"transCondition\":\"else\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"2623552ec6484e5fb40a6d0720866279\",\"endNodeId\":\"df7fe0546b9e47ee84ea166ab536781a\",\"transId\":\"cbaa29b688544c3695573bcbcc28840e\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"df7fe0546b9e47ee84ea166ab536781a\",\"endNodeId\":\"5f533832396b49ee88d77b3c64125b25\",\"transId\":\"6774f01c580b4ada848e4e39a1d2b772\",\"transName\":\"自办\",\"type\":\"link\",\"transCondition\":\"else\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"df7fe0546b9e47ee84ea166ab536781a\",\"endNodeId\":\"88e00c34a1e140dc8c78796893b829a7\",\"transId\":\"0d19ee33227f498f8af73a6734070eb4\",\"transName\":\"交办\",\"type\":\"link\",\"transCondition\":\"BL='F' && ('7' in(userRoles(slr)) || '8' in(userRoles(slr)))\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"608060129c4c407bb305d1d88b316668\",\"endNodeId\":\"76e89817bd524e53abd43ab3a01e72c1\",\"transId\":\"dbe7fbccafb1454f9dca806aa7b2c829\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"XB='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"76e89817bd524e53abd43ab3a01e72c1\",\"endNodeId\":\"2ed85e903a284f79bd75d0d8c8fd777d\",\"transId\":\"2572a72a0b3441cb976ec364c313f0e0\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"608060129c4c407bb305d1d88b316668\",\"endNodeId\":\"a8f8176fa65a4257b1fbb6233e183cbc\",\"transId\":\"dd8ef4d558e44d609bbb7f72f61a2498\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"leaderCodes='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"e6187f40dabd4f49a10081fc846e7bf6\",\"endNodeId\":\"3c5a3e210d1c4fd99aa155878ca4d500\",\"transId\":\"26e6ea05e3bf455ebafb569d24f8f1c1\",\"transName\":\"补正材料\",\"type\":\"link\",\"transCondition\":\"BL='bzcl'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"3c5a3e210d1c4fd99aa155878ca4d500\",\"endNodeId\":\"89c0ce9ff0e04c9199ae8753e0ab86bf\",\"transId\":\"f306231c51674d9e90f531b92b90339b\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"89c0ce9ff0e04c9199ae8753e0ab86bf\",\"endNodeId\":\"5f533832396b49ee88d77b3c64125b25\",\"transId\":\"a34453eebfbc4626b267dd73b6b7c1bb\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"102f252461ad47b48a27f61bc32e29d2\",\"endNodeId\":\"2623552ec6484e5fb40a6d0720866279\",\"transId\":\"ce03179d022f4753a2fdd5d2553c2327\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"else\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"102f252461ad47b48a27f61bc32e29d2\",\"endNodeId\":\"3e2f20890601476cbfdc2c74e1eedf53\",\"transId\":\"cfb335743bf640afa31488498d7c069b\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"unitType(ZBXB) == '2'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"794eb1f3db6340e4be5bb8e20c121e3b\",\"endNodeId\":\"cc9f4820d83545d09bd643c747e207c0\",\"transId\":\"b5060070109e472d88d5cd4f8e715d9a\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"cc9f4820d83545d09bd643c747e207c0\",\"endNodeId\":\"300fa5c463cf45be92fb9f56878f28c9\",\"transId\":\"50100f5c20934c6c9e373a6ca3c0c370\",\"transName\":\"办结\",\"type\":\"link\",\"transCondition\":\"BL='end'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"cc9f4820d83545d09bd643c747e207c0\",\"endNodeId\":\"7d4a9be739644772b43a0824d6409238\",\"transId\":\"c330756d291d4b53a09e6afe3d30cf57\",\"transName\":\"形成发文\",\"type\":\"link\",\"transCondition\":\"BL='fw'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"a1d27063f63c4c2e99b8271b63c6d84c\",\"endNodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"transId\":\"14c43743a98d47748a2af8c57a5b1363\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"endNodeId\":\"102f252461ad47b48a27f61bc32e29d2\",\"transId\":\"3fd418d402224d24bdcc6839b08fdea0\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"1\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"endNodeId\":\"7e24a0894f4f4e619030a741dd75f9a4\",\"transId\":\"14c991d94a744d28ae7e8aba309af004\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"XB='T' \"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"endNodeId\":\"eea275102b774998a34a2a253e8288dd\",\"transId\":\"0b5e214a8d43493da2197a99c59ce70b\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"leaderCodes='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"14db1213599c4728a9382b1ac30c66ef\",\"endNodeId\":\"8e5743e9844648d4875c4e42d7854fd9\",\"transId\":\"9de261e070dc4f70af7d3daa5b892c61\",\"transName\":\"\",\"type\":\"link\",\"transCondition\":\"ejxsy='T'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"2ed85e903a284f79bd75d0d8c8fd777d\",\"endNodeId\":\"d3b3227efe464fa595b3ea03fb09a1ec\",\"transId\":\"ab77dbf2a29e464492b7bb82271a7f35\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"d3b3227efe464fa595b3ea03fb09a1ec\",\"endNodeId\":\"d150eefe08924c9b8afd2a854212ae88\",\"transId\":\"50c2ccd0aa5a4cf0976f58a1ba9b41bf\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"7d4a9be739644772b43a0824d6409238\",\"endNodeId\":\"d916be4eb11e40e69e04783fdc31b152\",\"transId\":\"b7b2dfacd4a846fc9b4c4ab9ed8d94c4\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"88e00c34a1e140dc8c78796893b829a7\",\"endNodeId\":\"5f533832396b49ee88d77b3c64125b25\",\"transId\":\"6b1e28047e844e72b055c0e4029330a6\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"7d87b80d817947f78a5f2f1d0374d18c\",\"endNodeId\":\"5548282ab6224693a532ef594f58fc6a\",\"transId\":\"730774311a5e49c490ffeb735b136da4\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"7e24a0894f4f4e619030a741dd75f9a4\",\"endNodeId\":\"d8cadd3b7f4d4ad6893162ae63409c72\",\"transId\":\"301fd902d8fe4cebb7a6e9da1f78470c\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"d8cadd3b7f4d4ad6893162ae63409c72\",\"endNodeId\":\"c862ab81ee694784a9398168a0d9272d\",\"transId\":\"384fdca0774044749ad99e536b10376c\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"c862ab81ee694784a9398168a0d9272d\",\"endNodeId\":\"28ef2f7f1b704d26b74874b6da3b4296\",\"transId\":\"9735b8fa21f14af88197e0bbec73cbc2\",\"transName\":\"二级单位\",\"type\":\"link\",\"transCondition\":\"unitType(cursor) == '2'\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"c862ab81ee694784a9398168a0d9272d\",\"endNodeId\":\"d552cd9b2b8d496484a40ca1d3af453c\",\"transId\":\"0795c6ae209243aeb781606026a68f6d\",\"transName\":\"委内\",\"type\":\"link\",\"transCondition\":\"else\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"28ef2f7f1b704d26b74874b6da3b4296\",\"endNodeId\":\"919673c13a03484ea1644a730c992349\",\"transId\":\"c70255a4b8ac43caaa5e75abaa0b49d0\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"919673c13a03484ea1644a730c992349\",\"endNodeId\":\"d552cd9b2b8d496484a40ca1d3af453c\",\"transId\":\"a4f28b30c02a469383aa51b2e71b7c7d\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"8e5743e9844648d4875c4e42d7854fd9\",\"endNodeId\":\"dbce10c1af0c4277831af67250dc9707\",\"transId\":\"02553ec425304e8d9d0ff0841f0c2039\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"dbce10c1af0c4277831af67250dc9707\",\"endNodeId\":\"e0475681a72e4a7b8607fbe62b2eb5bf\",\"transId\":\"4d83002ab5d04898bb7fb8610ed688dc\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"eea275102b774998a34a2a253e8288dd\",\"endNodeId\":\"5fe8efcfb86942b78df66611fb7635aa\",\"transId\":\"d1f7bc90680b481d899516fc6ade397d\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"5fe8efcfb86942b78df66611fb7635aa\",\"endNodeId\":\"32288c4e69d845b6a06f02139e28ed07\",\"transId\":\"8135b481439b4e26a3322f44617ed72d\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"a8f8176fa65a4257b1fbb6233e183cbc\",\"endNodeId\":\"fa05785164bf40f6b8c5666acef44ae5\",\"transId\":\"a0bd6a59a8bd404295fe794ce82b261b\",\"transName\":\"\",\"type\":\"link\"},{\"cls\":{\"linkThickness\":2,\"linkType\":\"Flowchart\",\"linkColor\":\"#2a2929\"},\"startNodeId\":\"fa05785164bf40f6b8c5666acef44ae5\",\"endNodeId\":\"7b11eb79ef164ede875c69771fe81e77\",\"transId\":\"d94103af4a194e0f9388a8d3718f6184\",\"transName\":\"\",\"type\":\"link\"}],\"attr\":{\"timeLimit\":\"15D\",\"id\":\"flow-5450e22f263b428f836c54\",\"flowName\":\"40收文流程\",\"expireOpt\":\"N\"},\"business\":{\"optName\":\"002测试\",\"optId\":\"951753\"},\"config\":{\"showGridIcon\":\"eye\",\"showGrid\":true,\"showGridText\":\"隐藏网格\"},\"status\":\"1\",\"remarks\":[],\"version\":\"\",\"flowName\":\"\"}";
        }*/
        boolean saveSucced = flowDefine.saveDraftFlowDefJson(flowcode, flowxmldesc);
        if (saveSucced) {
            JsonResultUtils.writeSingleDataJson("流程图草稿草稿保存成功！", response);
        } else {
            JsonResultUtils.writeSingleDataJson("保存出错！", response);
        }
    }

    /**
     * 复制流程草稿为一个新的流程
     * @param flowcode
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
    public ResponseData copyWorkFlow( HttpServletRequest request) {
        Map<String, Object> parameters = collectRequestParameters(request);
        String flowCode = MapUtils.getString(parameters, "flowCode");
        String flowName = MapUtils.getString(parameters, "flowName");
        if (StringUtils.isAnyBlank(flowCode,flowName)){
            return ResponseData.makeErrorMessage("flowCode,flowName不能为空！");
        }
        String newFlowCode = flowDefine.copyWorkFlow(parameters);
        if (StringUtils.isBlank(newFlowCode)){
            return ResponseData.makeErrorMessage("flowCode有误");
        }
        JSONObject json = new JSONObject();
        json.put("flowCode",newFlowCode);
        return ResponseData.makeResponseData(json);
    }

    @ApiOperation(value = "根据流程xml获取流程节点",notes = "根据流程xml获取流程节点")
    @RequestMapping(value = "/getFlowNode/{flowcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getFlowNode(@PathVariable String flowcode) {
        FlowInfo flowInfo = flowDefine.getFlowInfo(flowcode, 0);
        if (flowInfo == null) {
            ResponseData.makeErrorMessage("未获取到流程节点，可能是流程没有保存。");
            return null;
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
     * @param flowcode 流程代码
     */
    @ApiOperation(value = "查看流程图",notes = "查看流程图")
    @RequestMapping(value = "/viewxml/{flowcode}/{version}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String viewXml(@PathVariable Long version, @PathVariable String flowcode) {
        FlowInfo obj = flowDefine.getFlowInfo(flowcode, version);
        String flowXmlDesc = obj == null ? "" : obj.getFlowXmlDesc();
        return flowXmlDesc;
    }

    /**
     * 保存草稿
     * @param flowdefine 流程定义信息
     * @param response HttpServletResponse
     */
    @ApiOperation(value = "新建流程的基本信息",notes = "新建流程的基本信息")
    @RequestMapping(method = RequestMethod.POST)
    public void addFlowDefine(@RequestBody FlowInfo flowdefine, HttpServletResponse response) {
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
        if (FlowInfo.FLOW_STATE_FORBIDDEN.equals(newstate)) {
            flowDefine.disableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经禁用！", response);
        }
        if (FlowInfo.FLOW_STATE_NORMAL.equals(newstate)) {
            flowDefine.enableFlow(flowcode);
            JsonResultUtils.writeSingleDataJson("已经启用！", response);
        }
    }

    /**
     * 批量删除(物理删除)
     * @param flowCodes
     * @return
     */
    @ApiOperation(value = "批量删除流程", notes = "批量删除流程,多个id之间用逗号隔开")
    @WrapUpResponseBody
    @RequestMapping(value = "/batchChangeState", method = RequestMethod.POST)
    public ResponseData batchChangeState(@RequestBody String[] flowCodes){
        if (!StringUtils.isNoneEmpty(flowCodes)){
            return ResponseData.makeErrorMessage("flowCodes不能为空!");
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
    public ResponseData clearRecycle(@RequestBody JSONObject params){
        String osId = params.getString("osId");
        if (!StringUtils.isNoneEmpty(osId)){
            return ResponseData.makeErrorMessage("osId不能为空!");
        }
        try {
            flowDefine.clearRecycle(osId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseData.makeSuccessResponse();
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
    public Map<String, Map<String, String>> getDataMap(@PathVariable String flowcode, HttpServletRequest request){
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Map<String, String>> map = flowDefine.listAllRole(topUnit);
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
    public Map<String, Map<String, String>> listAllRole(HttpServletRequest request){
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
