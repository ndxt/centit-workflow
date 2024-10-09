package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.po.FlowStage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class FlowStageDao extends BaseDaoImpl<FlowStage, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    FlowStage getStageByCode(String flowCode, long version, String stageCode){
        if(StringUtils.isBlank(flowCode) || StringUtils.isBlank(stageCode)) return null;
        return this.getObjectByProperties(
            CollectionsOpt.createHashMap("stageCode",stageCode,
                "flowCode", flowCode, "version", version)
        );
    }
}
