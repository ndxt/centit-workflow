package com.centit.workflow.commons;

import com.centit.support.algorithm.CollectionsOpt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 */
@Data
public class CreateFlowOptions implements FlowOptParamOptions, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程代码
     */
    @ApiModelProperty(value = "流程定义Code", required = true)
    private String flowCode;

    /**
     * 业务的OptId 或者 自定义表单得模板id
     */
    @ApiModelProperty("业务的OptId 或者 自定义表单得模板id")
    private String modelId;

    /**
     * 流程版本号 如果=0 表示用最新版本，
     */
    @ApiModelProperty("流程版本,可为空")
    private long flowVersion;
    /**
     * 流程实例对应的业务名称，用于流程查看式显示
     */
    @ApiModelProperty(value = "流程名称", required = true)
    private String flowOptName;
    /**
     * 流程实例对应的业务主键，用于反向关联
     */
    @ApiModelProperty(value ="流程对应的业务id")
    private String flowOptTag;

    /**
     * 作为子流程创建式，对应的父流程节点
     */
    @ApiModelProperty("父节点ID，作为子流程时有效")
    private String parentNodeInstId;

    /**
     * 流程实例ID，指定创建流程的实例编号，UUID要保证不能冲突
     */
    @ApiModelProperty("流程实例ID，指定创建流程的实例编号")
    private String flowInstId;

    /**
     * 作为子流程创建式，对应的父流程
     */
    @ApiModelProperty("父流程ID，作为子流程时有效")
    private String parentFlowInstId;

    @ApiModelProperty("流程组ID")
    private String flowGroupId;

    /**
     * 设置流程时限
     */
    @ApiModelProperty("设置流程时限，格式为3D4H30M这样的")
    private String timeLimitStr;

    /**
     * 提交（操作）用户
     */
    @ApiModelProperty(value ="提交（操作）用户", required = true)
    private String userCode;

    /**
     * 提交（操作）用户当前单位（机构）
     */
    @ApiModelProperty(value ="提交（操作）用户当前单位（机构）", required = true)
    private String unitCode;


    /**
     * 租户代码，可以为空，如果是空系统会根据 unitCode 获取
     */
    @ApiModelProperty(value ="当前应用的租户代码，可以为空，如果是空系统会根据 unitCode 获取。")
    private String topUnit;


    @ApiModelProperty("是否跳过第一步，默认为false，如果第一步为路由节点无效")
    private boolean skipFirstNode;

    /**
     * 提交后的节点强行指定用户
     */
    @ApiModelProperty("提交后的节点强行指定用户")
    private boolean lockOptUser;

    @ApiModelProperty("传递的用户code，用于下一步人员指定, " +
        "和 lockFirstOpt 配合使用，如果lockFirstOpt为true并且workUserCode为空则指定为userCode")
    private String workUserCode;

    /**
     * 业务变量数据
     */
    @ApiModelProperty("流程变量")
    private Map<String,Object> variables;

    /**
     * 业务变量数据
     */
    @ApiModelProperty("流程全局变量")
    private Map<String,Object> globalVariables;
    /**
     * 流程办件角色
     */
    @ApiModelProperty("流程办件角色")
    private Map<String, List<String>> flowRoleUsers;

    /**
     * 流程机构
     * Map String 机构角色 String（机构代码）
     */
    @ApiModelProperty("流程办件机构，类似于办件角色")
    private Map<String, List<String>> flowOrganizes;

    /**
     * 后续节点机构
     * Map String (节点的环节代码或者节点代码) String（机构代码）
     */
    @ApiModelProperty("指定后续节点机构")
    private Map<String, String> nodeUnits;

    /**
     * 后续节点操作人员
     * Map String (节点的环节代码或者节点代码) String（人员代码可以是多个）
     */
    @ApiModelProperty("指定后续节点操作人员")
    private Map<String, Set<String>> nodeOptUsers;


    private Locale clientLocale;

    private String loginIp;

    private CreateFlowOptions() {
        this.lockOptUser = false;
        this.skipFirstNode = false;
        this.flowVersion = -1;
    }

    public static CreateFlowOptions create(){
        return new CreateFlowOptions();
    }

    public CreateFlowOptions flowInst(String flowInstId){
        this.flowInstId = flowInstId;
        return this;
    }

    public CreateFlowOptions user(String userCode){
        this.userCode = userCode;
        return this;
    }

    public CreateFlowOptions unit(String unitCode){
        this.unitCode = unitCode;
        return this;
    }

    public CreateFlowOptions tenant(String topUnit){
        this.topUnit = topUnit;
        return this;
    }

    public CreateFlowOptions addVariable(String name, String value){
        if(this.variables == null){
            this.variables = new HashMap<>();
        }
        this.variables.put(name, value);
        return this;
    }

    public CreateFlowOptions addGlobalVariable(String name, String value){
        if(this.globalVariables == null){
            this.globalVariables = new HashMap<>();
        }
        this.globalVariables.put(name, value);
        return this;
    }

    public CreateFlowOptions addFlowRoleUsers(String role, List<String> users){
        if(this.flowRoleUsers == null){
            this.flowRoleUsers = new HashMap<>();
        }
        this.flowRoleUsers.put(role, users);
        return this;
    }

    public CreateFlowOptions addFlowRoleUser(String role, String user){
        if(this.flowRoleUsers == null){
            this.flowRoleUsers = new HashMap<>();
        }
        List<String> users = this.flowRoleUsers.get(role);
        if(users == null){
            this.flowRoleUsers.put(role, CollectionsOpt.createList(user));
        } else {
            users.add(user);
        }
        return this;
    }

    public CreateFlowOptions addFlowOrganizes(String role, List<String> units){
        if(this.flowOrganizes == null){
            this.flowOrganizes = new HashMap<>();
        }
        this.flowOrganizes.put(role, units);
        return this;
    }

    public CreateFlowOptions addFlowOrganize(String role, String unitCode){
        if(this.flowOrganizes == null){
            this.flowOrganizes = new HashMap<>();
        }
        List<String> units = this.flowOrganizes.get(role);
        if(units == null){
            this.flowOrganizes.put(role, CollectionsOpt.createList(unitCode));
        } else {
            units.add(unitCode);
        }
        return this;
    }

    public CreateFlowOptions setNextNodeUnit(String nextNode, String unitCode){
        if(this.nodeUnits == null){
            this.nodeUnits = new HashMap<>();
        }
        this.nodeUnits.put(nextNode, unitCode);
        return this;
    }

    public CreateFlowOptions setNextNodeUsers(String nextNode, String ... userCodes){
        if(this.nodeOptUsers == null){
            this.nodeOptUsers = new HashMap<>();
        }
        this.nodeOptUsers.put(nextNode, CollectionsOpt.createHashSet(userCodes));
        return this;
    }

    public CreateFlowOptions lockOptUser(boolean lockFirstOpt){
        this.lockOptUser = lockFirstOpt;
        return this;
    }

    public CreateFlowOptions skipFirstNode(boolean skipFirstNode){
        this.skipFirstNode = skipFirstNode;
        return this;
    }


    public CreateFlowOptions workUser(String workUserCode){
        if(StringUtils.isNotBlank(workUserCode)) {
            this.lockOptUser = true;
            this.workUserCode = workUserCode;
        }
        return this;
    }


    public CreateFlowOptions flow(String flowCode){
        this.flowCode = flowCode;
        return this;
    }

    public CreateFlowOptions parentFlow(String flowInstId, String nodeInstId){
        this.parentFlowInstId = flowInstId;
        this.parentNodeInstId = nodeInstId;
        return this;
    }

    public CreateFlowOptions group(String flowGroupId){
        this.flowGroupId = flowGroupId;
        return this;
    }

    public CreateFlowOptions model(String modelId){
        this.modelId = modelId;
        return this;
    }

    public CreateFlowOptions version(long version){
        this.flowVersion = version;
        return this;
    }

    public CreateFlowOptions optName(String flowOptName){
        this.flowOptName = flowOptName;
        return this;
    }

    public CreateFlowOptions optTag(String flowOptTag){
        this.flowOptTag = flowOptTag;
        return this;
    }

    public CreateFlowOptions timeLimit(String timeLimitStr) {
        this.timeLimitStr = timeLimitStr;
        return this;
    }

    public CreateFlowOptions copy(FlowOptParamOptions options){
        this.setVariables(CollectionsOpt.cloneHashMap(options.getVariables()));
        this.setFlowRoleUsers(CollectionsOpt.cloneHashMap(options.getFlowRoleUsers()));
        this.setGlobalVariables(CollectionsOpt.cloneHashMap(options.getGlobalVariables()));
        this.setNodeUnits(CollectionsOpt.cloneHashMap(options.getNodeUnits()));
        this.setNodeOptUsers(CollectionsOpt.cloneHashMap(options.getNodeOptUsers()));
        this.setFlowOrganizes(CollectionsOpt.cloneHashMap(options.getFlowOrganizes()));
        this.setLoginIp(options.getLoginIp());
        this.setClientLocale(options.getClientLocale());
        this.user(options.getUserCode())
            .unit(options.getUnitCode());
        return this;
    }

}
