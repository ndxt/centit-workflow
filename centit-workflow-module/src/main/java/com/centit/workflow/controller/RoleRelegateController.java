package com.centit.workflow.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.UserUnit;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.RoleRelegateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户的权限委托 和任务转移
 */
@Api(value = "任务管理",
    tags = "权限委托和任务转移")
@Controller
@RequestMapping("/flow/relegate")
public class RoleRelegateController extends BaseController {

    @Autowired
    private RoleRelegateService roleRelegateService;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @ApiOperation(value = "新增委托", notes = "新增委托")
    @WrapUpResponseBody
    @PostMapping
    public void saveRelegate(@RequestBody RoleRelegate roleRelegate) {
        roleRelegateService.saveRelegate(roleRelegate);
    }

    @ApiOperation(value = "根据业务id数组进行委托", notes = "relateType:'batch（按照业务列表委托）/all(表示全部）', optIds :['设置为数组']")
    @WrapUpResponseBody
    @PostMapping("/batchRelegateByOp")
    public void batchRelegate(@RequestBody String relegateString) {
        JSONObject json = JSONObject.parseObject(relegateString);
        RoleRelegate roleRelegate = json.toJavaObject(RoleRelegate.class);
        roleRelegate.setRoleType("OP");
        String relateType = json.getString("relateType");
        //JSONArray optIds = json.getJSONArray("optIds");
        List<String> optIds=  StringBaseOpt.objectToStringList(json.get("optIds"));
        roleRelegateService.batchRelegateByOp(roleRelegate, relateType, optIds);
    }

    @ApiOperation(value = "修改委托", notes = "修改委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/updateRelegate", method = RequestMethod.POST)
    public void updateRelegate(@RequestBody RoleRelegate roleRelegate) {
        roleRelegateService.updateRelegate(roleRelegate);
    }

    @ApiOperation(value = "修改委托状态", notes = "修改委托状态")
    @WrapUpResponseBody
    @RequestMapping(value = "/changeRelegateValid", method = RequestMethod.PUT)
    public void changeRelegateValid(@RequestBody RoleRelegate roleRelegate) {
        roleRelegateService.changeRelegateValid(roleRelegate);
    }

    @ApiOperation(value = "获取指定用户委托列表", notes = "获取指定用户委托列表")
    @WrapUpResponseBody
    @RequestMapping(value = "/byUser/{grantor}", method = {RequestMethod.GET})
    public PageQueryResult<Object> getRelegateListByGrantor(@PathVariable String grantor, HttpServletRequest request,
                                                            PageDesc pageDesc) {
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        filterMap.put("grantor", grantor);
        List<RoleRelegate> relegateList = roleRelegateService.getRelegateListByGrantor(filterMap, pageDesc);

        JSONArray relegates = DictionaryMapUtils.objectsToJSONArray(relegateList);
//        List<FlowOptInfo> listOptInfo = wfOptService.getListOptInfo();
//        Map<String, String> optInfoMap = listOptInfo.stream().collect(Collectors.toMap(FlowOptInfo::getOptId, FlowOptInfo::getOptName));
//        for (Object relegate : relegates) {
//            ((JSONObject) relegate).put("optName", optInfoMap.get(((JSONObject) relegate).getString("optId")));
//        }

//        List<JSONObject> relegateList2 = flowManager.getListRoleRelegateByGrantor(grantor);
//        return PageQueryResult.createResultMapDict(relegateList, pageDesc);
        return PageQueryResult.createJSONArrayResult(relegates, pageDesc);
    }


    @ApiOperation(value = "委托列表查询", notes = "委托列表查询")
    @WrapUpResponseBody
    @RequestMapping(value = "/listRoleRelegates", method = {RequestMethod.GET})
    public PageQueryResult<RoleRelegate> listRoleRelegates(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        List<RoleRelegate> relegateList = roleRelegateService.listRoleRelegates(filterMap, pageDesc);

        return PageQueryResult.createResultMapDict(relegateList, pageDesc);
    }

    @ApiOperation(value = "根据id删除委托", notes = "根据id删除委托")
    @WrapUpResponseBody
    @RequestMapping(value = "/{relegateNo}", method = {RequestMethod.DELETE})
    public void deleteRelegate(@PathVariable String relegateNo) {
        roleRelegateService.deleteRoleRelegate(relegateNo);
    }

    @ApiOperation(value = "获取用户职务", notes = "获取用户职务")
    @ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String"
    )
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @GetMapping(value = "/role/{userCode}")
    public List<UserUnit> listUserRoles(@PathVariable String userCode, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext(topUnit);
        return context.listUserUnits(userCode);
    }


}
