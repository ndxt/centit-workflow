package com.centit.workflow.service.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.PlatformFlowDao;
import com.centit.workflow.po.UserTask;
import com.centit.workflow.service.PlatformFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PlatformFlowServiceImpl implements PlatformFlowService {
    @Resource
    private PlatformFlowDao platformFlowDao;

    @Override
    @Transactional
    public List<UserTask> queryDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        //动态任务
        //1.找到用户所有机构下的岗位和职务
        List<? extends IUserUnit> iUserUnits = CodeRepositoryUtil.listUserUnits((String)searchColumn.get("userCode"));

        //2.以机构，岗位，职务来查询任务
        if(iUserUnits == null||iUserUnits.size()==0){
            return taskList;
        }
        //TODO 需要做成分页查询
        for(IUserUnit i:iUserUnits){
            searchColumn.put("unitCode",i.getUnitCode());
            searchColumn.put("userStation",i.getUserStation());
            List<UserTask>  dynamicTask = platformFlowDao.queryDynamicTask(searchColumn,pageDesc);
            taskList.addAll(dynamicTask);
        }

        return taskList;
    }

    @Override
    @Transactional
    public List<UserTask> queryTask(Map<String, Object> searchColumn, PageDesc pageDesc) {
        List<UserTask> taskList = new ArrayList<>();
        //静态任务
        List<UserTask> staticTaskList = platformFlowDao.queryStaticTask((String)searchColumn.get("userCode"));
        if(staticTaskList != null){
            taskList.addAll(staticTaskList);
        }
        //动态任务
        //1.找到用户主机构下的岗位和职务
        List<? extends IUserUnit> iUserUnits = CodeRepositoryUtil.listUserUnits((String)searchColumn.get("userCode"));
        IUserUnit userUnit = null;
        if(iUserUnits != null && iUserUnits.size() > 0){
            for(IUserUnit iUserUnit:iUserUnits){
                if("T".equals(iUserUnit.getIsPrimary())){
                    userUnit = iUserUnit;
                    break;
                }
            }
        }
        //2.以机构，岗位，职务来查询任务
        if(userUnit == null){
            return taskList;
        }
        searchColumn.put("unitCode",userUnit.getUnitCode());
        searchColumn.put("userStation",userUnit.getUserStation());
        List<UserTask> dynamicTaskList = platformFlowDao.queryDynamicTask(searchColumn,pageDesc);
        if(dynamicTaskList != null){
            taskList.addAll(dynamicTaskList);
        }
        return taskList;
    }

    @Override
    @Transactional
    public String getNodeOptUrl(String optCode) {//TODO 暂时用不到
        return null;
    }
}
