package com.centit.workflow.service.impl;

import com.centit.workflow.dao.OptNodeDao;
import com.centit.workflow.po.OptNode;
import com.centit.workflow.service.OptDefine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Service
public class OptDefineImpl implements OptDefine {
    @Resource
    private OptNodeDao optNodeDao;
    @Override
    @Transactional
    public List<OptNode> listOptNodeByOptId(String optId) {
        return optNodeDao.getOptNodeByOptId(optId);
    }
}
