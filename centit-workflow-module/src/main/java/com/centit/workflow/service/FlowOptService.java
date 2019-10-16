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

    FlowOptInfo getOptById(String optId);

    void deleteOptInfoById(String optId);

    void saveOpt(FlowOptInfo FlowOptInfo);

    void saveOptDef(FlowOptPage FlowOptDef);

    List<FlowOptPage> getListOptDefById(String optId, Map<String, Object> filterMap, PageDesc pageDesc);

    FlowOptPage getOptDefByCode(String optCode);

    void deleteOptDefByCode(String optCode);

    List<FlowOptPage> ListOptDef(Map<String, Object> filterMap, PageDesc pageDesc);

    String getOptInfoSequenceId();

    String getOptDefSequenceId();

    FlowOptInfo getFlowOptInfoById(String optId);

    List<FlowOptInfo> getListOptInfo();

    FlowOptInfo getOptByModelId(String modelId);

}
