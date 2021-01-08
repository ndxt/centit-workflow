package com.centit.demo.service;

import com.centit.demo.po.ApprovalAuditor;
import com.centit.demo.po.ApprovalEvent;
import com.centit.demo.po.ApprovalProcess;
import com.centit.workflow.po.UserTask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by chen_rj on 2017/8/3.
 */
public interface ApprovalService {
    /**
     * 发起申请 需要保存申请的内容，设置所有审批人
     * @param approvalEvent
     * @param approvalAuditors
     */
    String startProcess(HttpServletRequest request, ApprovalEvent approvalEvent, List<ApprovalAuditor> approvalAuditors, int phaseNO, String userCode) throws Exception;

    /**
     * 审批通过或者不通过  需要保存审批意见，审批结果，如果通过还要设置下一步的审批人
     * @param userCodes
     * @param approvalProcess
     */
    void doApproval(List<String> userCodes, ApprovalProcess approvalProcess, String flowInstId, String nodeInstId, String userCode, ServletContext ctx) throws Exception;

    /**
     * 根据userCode获取 待办任务
     * @param userCode
     * @throws Exception
     */
    List<UserTask> getUserTasksByUserCode(String userCode) throws Exception;
}
