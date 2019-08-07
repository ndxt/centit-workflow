package com.centit.workflow.commons;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 */
public class NewFlowInstanceOptions {
    /**
     * 流程代码
     */
    @ApiModelProperty(value = "流程定义Code",required = true)
    private String flowCode;
    /**
     * 流程版本号 如果=0 表示用最新版本，
     */
    @ApiModelProperty("流程版本,可为空")
    private long version;
    /**
     * 流程实例对应的业务名称，用于流程查看式显示
     */
    @ApiModelProperty(value = "流程名称",required = true)
    private String flowOptName;
    /**
     * 流程实例对应的业务主键，用于反向关联
     */
    @ApiModelProperty(value ="流程对应的业务id",required = true)
    private String flowOptTag;
    /**
     * 创建流程用户
     */
    @ApiModelProperty(value ="创建流程用户",required = true)
    private String userCode;
    /**
     * 流程归属单位（机构）
     */
    @ApiModelProperty(value ="流程所属部门，可用创建流程用户的部门",required = true)
    private String unitCode;
    /**
     * 作为子流程创建式，对应的父流程节点
     */
    @ApiModelProperty("不填")
    private long nodeInstId;
    /**
     * 作为子流程创建式，对应的父流程
     */
    @ApiModelProperty("不填")
    private String flowInstId;
    /**
     * 业务变量数据
     */
    @ApiModelProperty(value = "不填")
    private Map<String,Object> variables ;
    /**
     * 流程首节点是否只能有创建人操作（一般 报销、请假的首节点都是只能由发起人修改）
     */
    @ApiModelProperty("流程首节点是否只能有创建人操作，如果首节点是办件角色的话可以为true，其余不填")
    private boolean lockFirstOpt;

    /**
     * 设置流程时限
     */
    @ApiModelProperty("设置流程时限，格式为3D4H30M这样的")
    private String timeLimitStr;

    @ApiModelProperty("传递的用户code列表，用于下一步人员指定")
    private String userList;

    public NewFlowInstanceOptions() {
    }

    public NewFlowInstanceOptions(String flowCode, String flowOptName,
                                  String flowOptTag, String userCode) {
        this.flowCode = flowCode;
        this.flowOptName = flowOptName;
        this.flowOptTag = flowOptTag;
        this.userCode = userCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getFlowOptName() {
        return flowOptName;
    }

    public void setFlowOptName(String flowOptName) {
        this.flowOptName = flowOptName;
    }

    public String getFlowOptTag() {
        return flowOptTag;
    }

    public void setFlowOptTag(String flowOptTag) {
        this.flowOptTag = flowOptTag;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public long getNodeInstId() {
        return nodeInstId;
    }

    public void setNodeInstId(long nodeInstId) {
        this.nodeInstId = nodeInstId;
    }

    public String getFlowInstId() {
        return flowInstId;
    }

    public void setFlowInstid(String flowInstId) {
        this.flowInstId = flowInstId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public boolean isLockFirstOpt() {
        return lockFirstOpt;
    }

    public void setLockFirstOpt(boolean lockFirstOpt) {
        this.lockFirstOpt = lockFirstOpt;
    }

    public String getTimeLimitStr() {
        return timeLimitStr;
    }

    public void setTimeLimitStr(String timeLimitStr) {
        this.timeLimitStr = timeLimitStr;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }
}
