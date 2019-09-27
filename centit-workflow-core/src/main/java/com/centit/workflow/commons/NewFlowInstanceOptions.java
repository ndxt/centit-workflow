package com.centit.workflow.commons;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 */
@Data
public class NewFlowInstanceOptions {
    /**
     * 流程代码
     */
    @ApiModelProperty(value = "流程定义Code",required = true)
    private String flowCode;

    @ApiModelProperty("自定义表单得模板id")
    private String modelId;

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
    @ApiModelProperty("返回值，不填")
    private String nodeInstId;
    /**
     * 作为子流程创建式，对应的父流程
     */
    @ApiModelProperty("返回值，不填")
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

    public NewFlowInstanceOptions flow(String flowCode){
        this.flowCode = flowCode;
        return this;
    }

    public NewFlowInstanceOptions model(String modelId){
        this.modelId = modelId;
        return this;
    }

    public NewFlowInstanceOptions assignVersion(int version){
        this.version = version;
        return this;
    }

    public NewFlowInstanceOptions optName(String flowOptName){
        this.flowOptName = flowOptName;
        return this;
    }

    public NewFlowInstanceOptions optTag(String flowOptTag){
        this.flowOptTag = flowOptTag;
        return this;
    }

    public NewFlowInstanceOptions user(String userCode){
        this.userCode = userCode;
        return this;
    }

    public NewFlowInstanceOptions unit(String unitCode){
        this.unitCode = unitCode;
        return this;
    }

    public NewFlowInstanceOptions addVariable(String name, String value){
        if(this.variables == null){
            this.variables = new HashMap<>();
        }
        this.variables.put(name, value);
        return this;
    }

    public NewFlowInstanceOptions lockFirst(boolean lockFirstOpt){
        this.lockFirstOpt = lockFirstOpt;
        return this;
    }
}
