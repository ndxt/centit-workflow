package com.centit.workflow.service.impl;


import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.workflow.external.ExtFrameworkContextCacheBean;
import com.centit.workflow.external.JdbcUserUnitFilterCalcContext;
import com.centit.workflow.service.UserUnitFilterCalcContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserUnitFilterCalcContextFactoryImpl implements UserUnitFilterCalcContextFactory {

    @Value("${wf.userunit.engine.type:system}")
    protected String engineType;

    @Resource
    private ExtFrameworkContextCacheBean extFrameworkContextCacheBean;

    @Override
    public UserUnitFilterCalcContext createCalcContext() {

        if ("external".equalsIgnoreCase(engineType)){
            return new JdbcUserUnitFilterCalcContext(extFrameworkContextCacheBean);
        } else { // system
            return new SystemUserUnitFilterCalcContext();
        }
    }
}
