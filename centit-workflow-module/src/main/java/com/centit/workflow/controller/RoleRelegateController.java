package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.RoleRelegate;
import com.centit.workflow.service.FlowOptService;
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
    private FlowOptService wfOptService;

    @Autowired
    private UserUnitFilterCalcContextFactory userUnitFilterFactory;

    @ApiOperation(value = "新增委托", notes = "新增委托")
    @WrapUpResponseBody
    @PostMapping
    public void saveRelegate(@RequestBody RoleRelegate roleRelegate) {
        roleRelegateService.saveRelegate(roleRelegate);
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
    public List<? extends IUserUnit> listUserRoles(@PathVariable String userCode) {
        UserUnitFilterCalcContext context = userUnitFilterFactory.createCalcContext();
        return context.listUserUnits(userCode);
    }


}
