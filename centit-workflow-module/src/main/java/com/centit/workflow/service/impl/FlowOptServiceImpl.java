package com.centit.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.FlowOptInfoDao;
import com.centit.workflow.dao.FlowOptPageDao;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.po.FlowOptPage;
import com.centit.workflow.service.FlowOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:47:39
 */
@Service
public class FlowOptServiceImpl implements FlowOptService {
    @Autowired
    private FlowOptInfoDao flowOptInfoDao;

    @Autowired
    private FlowOptPageDao flowOptPageDao;

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
    public void saveOptDef(FlowOptPage flowOptDef) {
        flowOptDef.setUpdateDate(new Date());
        flowOptPageDao.mergeObject(flowOptDef);
    }

    @Override
    @Transactional
    public List<FlowOptPage> listOptPageById(String optId) {
        List<FlowOptPage> wfOptDefs = flowOptPageDao.listObjectsByFilter(
            "where OPT_ID = ? and PAGE_TYPE != 'A'",
            new Object[] {optId});
        return wfOptDefs;
    }

    @Override
    @Transactional
    public List<FlowOptPage> listOptAutoRunById(String optId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optId",optId);
        properties.put("pageType","A");
        List<FlowOptPage> wfOptDefs = flowOptPageDao.listObjectsByProperties(properties);
        return wfOptDefs;
    }

    @Override
    @Transactional
    public FlowOptPage getOptDefByCode(String optCode) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("optCode",optCode);
        FlowOptPage flowOptDef = flowOptPageDao.getObjectByProperties(properties);
        return flowOptDef;
    }

    @Override
    @Transactional
    public void deleteOptDefByCode(String optCode) {
        flowOptPageDao.deleteObjectById(optCode);
    }

    @Override
    @Transactional
    public List<FlowOptPage> listOptDef(Map<String, Object> filterMap, PageDesc pageDesc) {
        return flowOptPageDao.listObjectsByProperties(filterMap,pageDesc);
    }

    @Override
    public String getOptInfoSequenceId() {
        return flowOptInfoDao.getOptInfoSequenceId();
    }

    @Override
    public String getOptDefSequenceId() {
        return flowOptPageDao.getOptDefSequenceId();
    }

     @Override
    public FlowOptInfo getFlowOptInfoById(String optId) {
        FlowOptInfo flowOptInfo = flowOptInfoDao.getObjectById(optId);
        if(flowOptInfo != null){
            List<FlowOptPage> wfOptDefs = this.flowOptPageDao.listObjectsByProperty("optId", optId);
            flowOptInfo.addOptPages(wfOptDefs);
        }
        return flowOptInfo;
    }


}
