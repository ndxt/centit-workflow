package com.centit.workflow.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.OptIdeaInfo;
import com.centit.workflow.service.OptIdeaInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-08 10:50
 */
@Controller
@Api(value = "流程日志(审批记录)接口", tags = "流程日志(审批记录)接口类")
@RequestMapping("/flow/optLog")
@Slf4j
public class OptIdeaInfoController {

    @Autowired
    private OptIdeaInfoService optIdeaInfoService;

    @ApiOperation(value = "保存或修改审批记录", notes = "保存或修改审批记录")
    @WrapUpResponseBody
    @PostMapping
    public String saveOptIdeaInfo(@RequestBody OptIdeaInfo optIdeaInfo) {
        return optIdeaInfoService.saveOptIdeaInfo(optIdeaInfo);
    }

    @ApiOperation(value = "获取审批记录", notes = "获取审批记录")
    @WrapUpResponseBody
    @RequestMapping(value = "/{procId}", method = RequestMethod.GET)
    public OptIdeaInfo getOptIdeaInfoById(@PathVariable String procId) {
        return optIdeaInfoService.getOptIdeaInfoById(procId);
    }

    @ApiOperation(value = "分页获取审批记录", notes = "分页获取审批记录")
    @WrapUpResponseBody
    @GetMapping(value = "/listOptIdeaInfo")
    public PageQueryResult<OptIdeaInfo> listOptIdeaInfo(PageDesc pageDesc, HttpServletRequest request) {
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        List<OptIdeaInfo> optIdeaInfos = optIdeaInfoService.listOptIdeaInfo(filterMap, pageDesc);
        return PageQueryResult.createResultMapDict(optIdeaInfos, pageDesc);
    }

    @ApiOperation(value = "删除审批记录", notes = "删除审批记录")
    @WrapUpResponseBody
    @RequestMapping(value = "/{procId}", method = RequestMethod.DELETE)
    public void deleteOptIdeaInfoById(@PathVariable String procId) {
        optIdeaInfoService.deleteOptIdeaInfoById(procId);
    }

}
