package com.centit.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.po.FlowOptPage;

import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:46:54
 * 流程业务服务 —— 业务数据接口
 *
 */
public interface FlowOptService {

    JSONArray listOptInfo(Map<String, Object> filterMap, PageDesc pageDesc);

    FlowOptInfo getFlowOptInfoById(String optId);

    void deleteOptInfoById(String optId);

    void saveOptInfo(FlowOptInfo FlowOptInfo);

    void saveOptPage(FlowOptPage FlowOptDef);

    FlowOptPage getOptPageByCode(String optCode);

    void deleteOptPageByCode(String optCode);

    List<FlowOptPage> listOptPage(Map<String, Object> filterMap, PageDesc pageDesc);

    List<FlowOptPage> listAllOptPageById(String optId);

    List<FlowOptPage> listOptPageById(String optId);

    List<FlowOptPage> listOptAutoRunById(String optId);

    String getOptInfoSequenceId();

    String getOptDefSequenceId();

    List<FlowOptInfo> getListOptInfo();

    FlowOptInfo getOptByModelId(String modelId);

}
