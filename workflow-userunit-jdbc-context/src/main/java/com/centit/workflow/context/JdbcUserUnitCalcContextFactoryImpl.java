package com.centit.workflow.context;


import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;


public class JdbcUserUnitCalcContextFactoryImpl implements UserUnitFilterCalcContextFactory {

    private ExtFrameworkContextCacheBean extFrameworkContextCacheBean;

    @Override
    public UserUnitFilterCalcContext createCalcContext(String topUnit) {
        return new JdbcUserUnitFilterCalcContext(extFrameworkContextCacheBean);
    }

    public void setExtFrameworkContextCacheBean(ExtFrameworkContextCacheBean extFrameworkContextCacheBean) {
        this.extFrameworkContextCacheBean = extFrameworkContextCacheBean;
    }
}
