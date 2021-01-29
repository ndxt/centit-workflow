package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.workflow.po.FlowStage;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class FlowStageDao extends BaseDaoImpl<FlowStage, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

}
