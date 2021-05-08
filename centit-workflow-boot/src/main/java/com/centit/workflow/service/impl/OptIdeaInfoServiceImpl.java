package com.centit.workflow.service.impl;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.OptIdeaInfoDao;
import com.centit.workflow.po.OptIdeaInfo;
import com.centit.workflow.service.OptIdeaInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-08 11:10
 */
@Service
@Slf4j
public class OptIdeaInfoServiceImpl implements OptIdeaInfoService {
    private static final long serialVersionUID = 1L;
    @Autowired
    private OptIdeaInfoDao optIdeaInfoDao;

    @Override
    public String saveOptIdeaInfo(OptIdeaInfo optIdeaInfo) {
        log.info("保存流程办理记录 nodeInstId:{}", optIdeaInfo.getNodeInstId());
        optIdeaInfo.setTransDate(new Date());
        optIdeaInfoDao.mergeObject(optIdeaInfo);
        return optIdeaInfo.getProcId();
    }

    @Override
    public void deleteOptIdeaInfoById(String procId) {
        optIdeaInfoDao.deleteObjectById(procId);
    }

    @Override
    public List<OptIdeaInfo> listOptIdeaInfo(Map<String, Object> filterMap, PageDesc pageDesc) {
        if (pageDesc == null) {
            return optIdeaInfoDao.listObjects(filterMap);
        } else {
            return optIdeaInfoDao.listObjects(filterMap, pageDesc);
        }
    }

    @Override
    public OptIdeaInfo getOptIdeaInfoById(String procId) {
        return optIdeaInfoDao.getObjectById(procId);
    }
}
