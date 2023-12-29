package com.centit.workflow.service.impl;

import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.dao.FlowEventDao;
import com.centit.workflow.po.FlowEventInfo;
import com.centit.workflow.service.FlowEventService;
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
        return flowEventDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("optState",
                "N"), 0, maxRows);
    }

    /*@Override
    public List<FlowEventInfo> listEventByFlow(String flowInstId) {
        return flowEventDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("OPT_STATE",
                "N", "flowInstId", flowInstId));
    }
    */
    @Override
    public FlowEventInfo getEventByFlowEvent(String flowInstId, String eventName) {
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
