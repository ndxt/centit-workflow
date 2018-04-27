package com.centit.demo.po;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Created by chen_rj on 2018-4-27.
 */
public class DemoSubmitPo {

    private Long nodeInstId;

    private String userCode;

    private String unitCode;

    private Map<String,Object> varTrans;

    public Long getNodeInstId() {
        return nodeInstId;
    }

    public void setNodeInstId(Long nodeInstId) {
        this.nodeInstId = nodeInstId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Map<String, Object> getVarTrans() {
        return varTrans;
    }

    public void setVarTrans(Map<String, Object> varTrans) {
        this.varTrans = varTrans;
    }
}
