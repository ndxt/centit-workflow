package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.OptIdeaInfo;

import java.util.List;
import java.util.Map;

/**
 * @author liu_cc
 * @create 2021-05-08 11:10
 */
public interface OptIdeaInfoService {

    String saveOptIdeaInfo(OptIdeaInfo optIdeaInfo);

    void deleteOptIdeaInfoById(String procId);

    List<OptIdeaInfo> listOptIdeaInfo(Map<String, Object> filterMap, PageDesc pageDesc);

    OptIdeaInfo getOptIdeaInfoById(String taskId);
}
