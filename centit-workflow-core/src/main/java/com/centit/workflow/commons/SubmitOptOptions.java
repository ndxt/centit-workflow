package com.centit.workflow.commons;

import com.centit.support.algorithm.CollectionsOpt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 *
 * String nodeInstId, String userCode, String grantorCode,
 *                                       String unitCode,
 *                                       Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers
 */
@Data
public class SubmitOptOptions implements FlowOptParamOptions{
    /**
     * 流程代码
     */
    @ApiModelProperty(value = "流程节点实例ID，这个变量是必须的", required = true)
    private String nodeInstId;

    /**
     * 授权用户，在作为被授权人时需要
     */
    @ApiModelProperty(value ="授权用户，在作为被授权人时需要")
    private String grantorCode;


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
    @ApiModelProperty("指定后续节点机构")
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

    private SubmitOptOptions() {
        this.lockOptUser = false;
    }

    public static SubmitOptOptions create(){
        return new SubmitOptOptions();
    }

    public SubmitOptOptions nodeInst(String nodeInstId){
        this.nodeInstId = nodeInstId;
        return this;
    }

    public SubmitOptOptions grantor(String grantorCode){
        this.grantorCode = grantorCode;
        return this;
    }

    public SubmitOptOptions user(String userCode){
        this.userCode = userCode;
        return this;
    }

    public SubmitOptOptions unit(String unitCode){
        this.unitCode = unitCode;
        return this;
    }

    public SubmitOptOptions addVariable(String name, String value){
        if(this.variables == null){
            this.variables = new HashMap<>();
        }
        this.variables.put(name, value);
        return this;
    }

    public SubmitOptOptions addGlobalVariable(String name, String value){
        if(this.globalVariables == null){
            this.globalVariables = new HashMap<>();
        }
        this.globalVariables.put(name, value);
        return this;
    }

    public SubmitOptOptions addFlowRoleUsers(String role, List<String> users){
        if(this.flowRoleUsers == null){
            this.flowRoleUsers = new HashMap<>();
        }
        this.flowRoleUsers.put(role, users);
        return this;
    }

    public SubmitOptOptions addFlowRoleUser(String role, String user){
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

    public SubmitOptOptions addFlowOrganizes(String role, List<String> units){
        if(this.flowOrganizes == null){
            this.flowOrganizes = new HashMap<>();
        }
        this.flowOrganizes.put(role, units);
        return this;
    }

    public SubmitOptOptions addFlowOrganize(String role, String unitCode){
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

    public SubmitOptOptions setNextNodeUnit(String nextNode, String unitCode){
        if(this.nodeUnits == null){
            this.nodeUnits = new HashMap<>();
        }
        this.nodeUnits.put(nextNode, unitCode);
        return this;
    }

    public SubmitOptOptions setNextNodeUsers(String nextNode, String ... userCodes){
        if(this.nodeOptUsers == null){
            this.nodeOptUsers = new HashMap<>();
        }
        this.nodeOptUsers.put(nextNode, CollectionsOpt.createHashSet(userCodes));
        return this;
    }

    public SubmitOptOptions lockOptUser(boolean lockFirstOpt){
        this.lockOptUser = lockFirstOpt;
        return this;
    }

    public SubmitOptOptions workUser(String workUserCode){
        if(StringUtils.isNotBlank(workUserCode)) {
            this.lockOptUser = true;
            this.workUserCode = workUserCode;
        }
        return this;
    }

    public SubmitOptOptions copy(FlowOptParamOptions options){

        this.setVariables(CollectionsOpt.cloneHashMap(options.getVariables()));
        this.setFlowRoleUsers(CollectionsOpt.cloneHashMap(options.getFlowRoleUsers()));
        this.setGlobalVariables(CollectionsOpt.cloneHashMap(options.getGlobalVariables()));
        this.setNodeUnits(CollectionsOpt.cloneHashMap(options.getNodeUnits()));
        this.setNodeOptUsers(CollectionsOpt.cloneHashMap(options.getNodeOptUsers()));
        this.setFlowOrganizes(CollectionsOpt.cloneHashMap(options.getFlowOrganizes()));

        this.user(options.getUserCode())
            .unit(options.getUnitCode());

        return this;
    }

    public SubmitOptOptions clone(){
        SubmitOptOptions newObj = new SubmitOptOptions();
        newObj.setVariables(CollectionsOpt.cloneHashMap(this.variables));
        newObj.setGlobalVariables(CollectionsOpt.cloneHashMap(this.globalVariables));
        newObj.setNodeUnits(CollectionsOpt.cloneHashMap(this.nodeUnits));
        newObj.setFlowRoleUsers(CollectionsOpt.cloneHashMap(this.flowRoleUsers));
        newObj.setNodeOptUsers(CollectionsOpt.cloneHashMap(this.nodeOptUsers));
        newObj.setFlowOrganizes(CollectionsOpt.cloneHashMap(this.flowOrganizes));

        return newObj.user(this.userCode)
            .unit(this.unitCode).workUser(this.workUserCode)
            .lockOptUser(this.lockOptUser)
            .nodeInst(this.nodeInstId).grantor(this.grantorCode);
    }
}
