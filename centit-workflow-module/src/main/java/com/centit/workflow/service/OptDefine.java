package com.centit.workflow.service;

import com.centit.workflow.po.OptNode;

import java.util.List;

/**
 * Created by chen_rj on 2017-10-9.
 */
public interface OptDefine {
     List<OptNode> listOptNodeByOptId(String optId);
}
