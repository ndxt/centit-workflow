package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.FlowOptDefDao;
import com.centit.workflow.dao.FlowOptInfoDao;
import com.centit.workflow.po.FlowOptDef;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.service.FlowOptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:47:39
 */
@Service
public class FlowOptServiceImpl implements FlowOptService {
    @Resource
    private FlowOptInfoDao flowOptInfoDao;

    @Resource
    private FlowOptDefDao wfOptDefDao;

    @Override
    public List<FlowOptInfo> getListOptInfo() {
        List<FlowOptInfo> flowOptInfos = flowOptInfoDao.listObjects();
        return flowOptInfos;
    }

    @Override
    @Transactional
    public JSONArray listOptInfo(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowOptInfoDao.listObjectsAsJson(filterMap,pageDesc);
    }

    @Override
    public FlowOptInfo getOptByModelId(String modelId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("modelId",modelId);
        FlowOptInfo optInfo = flowOptInfoDao.getObjectByProperties(properties);
        return optInfo;
    }

    @Override
    @Transactional
    public FlowOptInfo getOptById(String optId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optId",optId);
        FlowOptInfo optInfo = flowOptInfoDao.getObjectByProperties(properties);
        return optInfo;
    }

    @Override
    @Transactional
    public void deleteOptInfoById(String optId) {
        flowOptInfoDao.deleteObjectById(optId);
    }

    @Override
    @Transactional
    public void saveOpt(FlowOptInfo FlowOptInfo) {
        FlowOptInfo.setUpdateDate(new Date());
        flowOptInfoDao.mergeObject(FlowOptInfo);
    }

    @Override
    @Transactional
    public void saveOptDef(FlowOptDef flowOptDef) {
        flowOptDef.setUpdateDate(new Date());
        wfOptDefDao.mergeObject(flowOptDef);
    }

    @Override
    @Transactional
    public List<FlowOptDef> getListOptDefById(String optId, Map<String, Object> filterMap, PageDesc pageDesc) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optId",optId);
        List<FlowOptDef> wfOptDefs = wfOptDefDao.listObjectsByProperties(properties);
        return wfOptDefs;
    }

    @Override
    @Transactional
    public FlowOptDef getOptDefByCode(String optCode) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optCode",optCode);
        FlowOptDef flowOptDef = wfOptDefDao.getObjectByProperties(properties);
        return flowOptDef;
    }

    @Override
    @Transactional
    public void deleteOptDefByCode(String optCode) {
        wfOptDefDao.deleteObjectById(optCode);
    }

    @Override
    @Transactional
    public List<FlowOptDef> ListOptDef(Map<String, Object> filterMap, PageDesc pageDesc) {
        return wfOptDefDao.listObjectsByProperties(filterMap,pageDesc);
    }

    @Override
    public String getOptInfoSequenceId() {
        return flowOptInfoDao.getOptInfoSequenceId();
    }

    @Override
    public String getOptDefSequenceId() {
        return wfOptDefDao.getOptDefSequenceId();
    }

     @Override
    public FlowOptInfo getFlowOptInfoById(String optId) {
        FlowOptInfo flowOptInfo = flowOptInfoDao.getObjectById(optId);
        if(flowOptInfo != null){
            List<FlowOptDef> wfOptDefs = this.wfOptDefDao.listObjectsByProperty("optId", optId);
            flowOptInfo.addAllWfOptDefs(wfOptDefs);
        }
        return flowOptInfo;
    }


}
