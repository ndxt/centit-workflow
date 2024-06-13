package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.workflow.po.FlowEventInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class FlowEventDao extends BaseDaoImpl<FlowEventInfo, String> {
    //public static final Logger logger = LoggerFactory.getLogger(FlowEventDao.class);

    public Map<String, String> getFilterField() {

        return null;
    }

    public List<FlowEventInfo> listEventForOpt(int maxRows) {
        return this.listObjectsByProperties(
            CollectionsOpt.createHashMap("optState",
                "N"), 0, maxRows);
    }
}
