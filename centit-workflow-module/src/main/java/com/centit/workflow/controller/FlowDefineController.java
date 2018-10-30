package com.centit.workflow.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.core.controller.BaseController;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowModelData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/flow/define")
public class FlowDefineController extends BaseController {
    //public static final Logger logger = LoggerFactory.getLogger(SampleFlowDefineController.class);

    @Resource
    private com.centit.workflow.service.FlowDefine flowDef;
    @Resource
    private FlowModelData modelData;

    ResponseMapData resData = new ResponseMapData();


    /**
     * 列举系统中的所有流程，只显示最新版本的
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc,
                     HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        List<FlowInfo> listObjects = flowDef.listLastVersionFlow(searchColumn, pageDesc);
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response, JsonPropertyUtils.getIncludePropPreFilter(FlowInfo.class, field));
    }

    /**
     * 某个流程的所有版本
     *
     * @param field    过滤域
     * @param pageDesc 分页
     * @param flowcode 流程号
     * @param request
     * @param response
     */
    @RequestMapping(value = "/allversions/{flowcode}", method = RequestMethod.GET)
    public void listAllVersionFlow(String[] field, PageDesc pageDesc, @PathVariable String flowcode, HttpServletRequest request, HttpServletResponse response) {
        List<FlowInfo> listObjects = flowDef.getFlowsByCode(flowcode, pageDesc);
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response, JsonPropertyUtils.getIncludePropPreFilter(FlowInfo.class, field));
    }


    /**
     * 某个流程的最新版本
     *
     * @param flowcode 流程号
     * @param response
     */
    @RequestMapping(value = "/lastversion/{flowcode}", method = RequestMethod.GET)
    public void listLastVersion(@PathVariable String flowcode, HttpServletResponse response) {
        FlowInfo lastVerOne = flowDef.getFlowDefObject(flowcode);
        JsonResultUtils.writeSingleDataJson(lastVerOne, response);
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
    @RequestMapping(value = "/{version}/{flowcode}", method = RequestMethod.GET)
    public void getFlowDefine(@PathVariable Long version, @PathVariable String flowcode, HttpServletResponse response) {
        FlowInfo obj = flowDef.getFlowDefObject(flowcode, version);
        JsonResultUtils.writeSingleDataJson(obj, response);
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
    @RequestMapping(value = "/{flowcode}", method = RequestMethod.PUT)
    public void editFlowDefine(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        flowdefine.setFlowCode(flowcode);
        flowdefine.setVersion(0l);
        boolean saveSucced = flowDef.saveDraftFlowDef(flowdefine);
        if (saveSucced)
            JsonResultUtils.writeSingleDataJson("工作流定义草稿保存成功！", response);
        else
            JsonResultUtils.writeErrorMessageJson("工作流定义草稿保存失败！", response);
    }

    /**
     * 更新流程，默认编辑草稿，版本号为0
     *
     * @param flowdefine
     * @param flowcode
     * @param response
     */
    @RequestMapping(value = "/stage/{flowcode}", method = RequestMethod.PUT)
    public void editFlowStage(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowStages()) {
            for (FlowStage stage : flowdefine.getFlowStages()) {
                if (null == stage.getStageId()) {
                    stage.setStageId(flowDef.getNextStageId());
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
    @RequestMapping(value = "/role/{flowcode}", method = RequestMethod.PUT)
    public void editRole(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowTeamRoles()) {
            for (FlowTeamRole role : flowdefine.getFlowTeamRoles()) {
                if (null == role.getFlowTeamRoleId()) {
                    role.setFlowTeamRoleId(flowDef.getNextRoleId());
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
    @RequestMapping(value = "/variableDefine/{flowcode}", method = RequestMethod.PUT)
    public void editVariable(@Valid FlowInfo flowdefine, @PathVariable String flowcode, HttpServletResponse response) {
        if (null != flowdefine.getFlowVariableDefines()) {
            for (FlowVariableDefine variableDefine : flowdefine.getFlowVariableDefines()) {
                if (null == variableDefine.getFlowVariableId()) {
                    variableDefine.setFlowVariableId(flowDef.getNextVariableDefId());
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
    @RequestMapping(value = "/getdatamap/{flowcode}", method = RequestMethod.GET)
    public void getDataMap(@PathVariable String flowcode, HttpServletResponse response) {
        Map<String, Map<String, String>> map = modelData.listAllRole();
        //办件角色重新赋值为当前流程中的办件角色，不再使用系统的
        map.put(SysUserFilterEngine.ROLE_TYPE_ITEM.toLowerCase() /*"bj"*/, flowDef.getRoleMapByFlowCode(flowcode, 0L));
        // 分配机制
        Map<String, String> map2 = modelData.listAllOptType();
        map.put("OptType", map2);
        // 操作定义
        Map<String, String> map3 = modelData.listAllOptCode(flowcode);
        map.put("OptCode", map3);
        // 子流程
        Map<String, String> map4 = modelData.listAllSubFlow();
        map.put("SubWfcode", map4);
        Map<String, String> stageMap = modelData.listFlowStages(flowcode);
        ;
        map.put("FlowPhase", stageMap);
        JsonResultUtils.writeSingleDataJson(map, response);
    }

}
