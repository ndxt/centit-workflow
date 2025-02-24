package com.centit.workflow.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO
 */
@ApiModel(description = "任务转移传输对象DTO")
@Data
public class TaskMove {
    @ApiModelProperty(value = "任务转移来源用户")
    private String formUser;
    @ApiModelProperty(value = "任务转移目标用户")
    private String toUser;
    @ApiModelProperty(value = "操作人员，一般指当前登录用户")
    private String operatorUser;
    @ApiModelProperty(value = "转移描述可以为null")
    private String moveDesc;
    @ApiModelProperty(value = "如果是部分转移请标准转移任务的节点实例id列表")
    private List<String> nodeInstIds;
    @ApiModelProperty(value = "任务转移应用id")
    private String osId;
    @ApiModelProperty(value = "任务分配类型：D：通过 岗位角色 自动匹配 S：静态待办（usercode) ")
    private String taskAssigned;
    @ApiModelProperty(value = "节点归属部门")
    private String unitCode;
    @ApiModelProperty(value = "角色类别")
    private String roleType;
    @ApiModelProperty(value = "角色代码")
    private String roleCode;
}

