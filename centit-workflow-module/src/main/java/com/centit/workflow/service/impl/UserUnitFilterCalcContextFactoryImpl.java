package com.centit.workflow.service.impl;


import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.workflow.external.ExtFrameworkContextCacheBean;
import com.centit.workflow.external.JdbcUserUnitFilterCalcContext;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;


public class UserUnitFilterCalcContextFactoryImpl implements UserUnitFilterCalcContextFactory {

    //@Value("${wf.userunit.engine.type:system}")
    protected String engineType;

    private ExtFrameworkContextCacheBean extFrameworkContextCacheBean;

    @Override
    public UserUnitFilterCalcContext createCalcContext() {

        if ("external".equalsIgnoreCase(engineType)){
            return new JdbcUserUnitFilterCalcContext(extFrameworkContextCacheBean);
        } else { // system
            return new SystemUserUnitFilterCalcContext();
        }
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public void setExtFrameworkContextCacheBean(ExtFrameworkContextCacheBean extFrameworkContextCacheBean) {
        this.extFrameworkContextCacheBean = extFrameworkContextCacheBean;
    }
}
