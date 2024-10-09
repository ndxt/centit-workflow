package com.centit.workflow.service.impl;

import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.dao.FlowEventDao;
import com.centit.workflow.po.FlowEventInfo;
import com.centit.workflow.service.FlowEventService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service

public class FlowEventServiceImpl implements FlowEventService {

    @Autowired
    private FlowEventDao flowEventDao;

    @Override
    public List<FlowEventInfo> listEventForOpt(int maxRows) {
        return flowEventDao.listEventForOpt(maxRows);
    }

    @Override
    public FlowEventInfo getEventByFlowEvent(String flowInstId, String eventName) {
        if(StringUtils.isBlank(flowInstId) || StringUtils.isBlank(eventName)) return null;
        return flowEventDao.getObjectByProperties(
            CollectionsOpt.createHashMap("optState",
                "N", "flowInstId", flowInstId, "eventName", eventName));
    }

    @Override
    @Transactional
    public void saveNewEvent(FlowEventInfo event) {
        flowEventDao.saveNewObject(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateEvent(FlowEventInfo event) {
        flowEventDao.updateObject(event);
    }
}
