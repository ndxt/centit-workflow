package com.centit.workflow.commons;

import com.centit.framework.common.ObjectException;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 */
@SuppressWarnings("unused")
public class WorkflowException extends ObjectException {
    private static final long serialVersionUID = 1L;

    //public enum FlowExceptionType {
    public static final int NodeUserNotFound    = 650; //找不到节点实例
    public static final int NodeInstNotFound    = 651; //找不到节点实例
    public static final int FlowInstNotFound    = 652;  //找不到流程实例
    public static final int FlowDefineError     = 653;  //流程图绘制问题
    public static final int IncorrectNodeState  = 654; //流程节点状态不正确
    public static final int PauseTimerNode      = 655;  // 暂停计时节点不能提交
    public static final int WithoutPermission   = 656;  //没有权限
    public static final int NoValueForMultiInst = 657;  //多实例节点对应的变量为空
    public static final int NotFoundNextNode    = 658;  //找不到符合流转条件的后续节点
    public static final int AutoRunNodeWithoutApplcationContent= 659; //自动运行节点出错，传递的参数application为空
    public static final int AutoRunNodeBeanNotFound= 660; //自动运行节点 出错，可能是设置bean找不到
 ///////////////用户提供的接口异常////////////////////////////////////////////////
    public static final int BizDataCheckError   = 661; //业务数据验证异常
    public static final int BizLogicError       = 662; //业务逻辑异常
    public static final int BizDefinedError     = 663; //业务自定义异常
    //}

    public WorkflowException(String message){
        super(message);
    }

    public WorkflowException(int exceptionType,String message){
        super(exceptionType, message);
    }

    public int getExceptionType() {
        return this.getExceptionCode();
    }

    public void setExceptionType(int exceptionType) {
        this.setExceptionCode(exceptionType);
    }

}
