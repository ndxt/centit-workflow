package com.centit.workflow.service;

import com.centit.workflow.po.FlowEventInfo;

import java.util.List;

public interface FlowEventService {
    //获取 所有需要处理的事件
    List<FlowEventInfo> listEventForOpt(int maxRows);

    FlowEventInfo getEventByFlowEvent(String flowInstId, String eventName);

    void saveNewEvent(FlowEventInfo event);

    void updateEvent(FlowEventInfo event);
}
