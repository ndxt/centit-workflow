package com.centit.workflow.service.impl;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;

public class SystemUserUnitCalcContextFactoryImpl implements UserUnitFilterCalcContextFactory {
    @Override
    public UserUnitFilterCalcContext createCalcContext(String topUnit) {
        // HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        // String topUnit = WebOptUtils.getCurrentTopUnit(request);
        SystemUserUnitFilterCalcContext context = new SystemUserUnitFilterCalcContext();
        context.setTopUnit(topUnit);
        return context;
    }
}
