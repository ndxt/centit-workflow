package com.centit.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowOptDef;
import com.centit.workflow.po.FlowOptInfo;

import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:46:54
 */
public interface FlowOptService {
    JSONArray listOptInfo(Map<String, Object> filterMap, PageDesc pageDesc);

     FlowOptInfo getOptById(String optId);

    void deleteOptInfoById(String optId);

    void saveOpt(FlowOptInfo FlowOptInfo);

    void saveOptDef(FlowOptDef FlowOptDef);

    List<FlowOptDef> getListOptDefById(String optId, Map<String, Object> filterMap, PageDesc pageDesc);

    FlowOptDef getOptDefByCode(String optCode);

    void deleteOptDefByCode(String optCode);

    List<FlowOptDef> ListOptDef(Map<String, Object> filterMap, PageDesc pageDesc);

    String getOptInfoSequenceId();

    String getOptDefSequenceId();

    FlowOptInfo getFlowOptInfoById(String optId);

    List<FlowOptInfo> getListOptInfo();

    FlowOptInfo getOptByModelId(String modelId) ;
    }
