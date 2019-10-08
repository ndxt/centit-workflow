package com.centit.workflow.service;

import com.centit.framework.model.adapter.UserUnitFilterCalcContext;

/**
 * 用于构建计算权限引擎的上下文环境
 *
 */
public interface UserUnitFilterCalcContextFactory {
    UserUnitFilterCalcContext createCalcContext();
}
