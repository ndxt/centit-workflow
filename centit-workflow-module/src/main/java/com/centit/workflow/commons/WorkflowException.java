package com.centit.workflow.commons;

public class WorkflowException extends  RuntimeException {
    private static final long serialVersionUID = 1L;
    public static enum FlowExceptionType {
        NodeInstNotFound, //找不到节点实例
        FlowInstNotFound, //找不到流程实例
        FlowDefineError, //流程图绘制问题
        IncorrectNodeState, //流程节点状态不正确
        PauseTimerNode, // 暂停计时节点不能提交
        WithoutPermission, //没有权限
        NoValueForMultiInst, //多实例节点对应的变量为空
        NotFoundNextNode, //找不到符合流转条件的后续节点
        AutoRunNodeWithoutApplcationContent, //自动运行节点出错，传递的参数application为空
        AutoRunNodeBeanNotFound, //自动运行节点 出错，可能是设置bean找不到 
//////////////////////用户提供的接口异常//////////////////////////////////////////////// 
        BizDataCheckError,//业务数据验证异常
        BizLogicError,//业务逻辑异常
        BizDefinedError;//业务自定义异常        
    };
        
    private FlowExceptionType exceptionType;
    
    public WorkflowException(FlowExceptionType exceptionType,String message){
        super(message);
        this.exceptionType = exceptionType;
    }

    public FlowExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(FlowExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

}
