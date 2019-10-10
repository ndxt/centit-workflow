package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName FlowRoleDefine/审批角色关联角色表
 * @Date 2019/7/22 15:33
 * @Version 1.0
 */
@Entity
@Table(name = "WF_FLOW_ROLE_DEFINE")
public class FlowTeamDefine implements Serializable {
    private static final long serialVersionUID =  1L;

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "RELATED_TYPE")
    private String relatedType;

    @Column(name = "RELATED_CODE")
    private String relatedCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }

    public String getRelatedCode() {
        return relatedCode;
    }

    public void setRelatedCode(String relatedCode) {
        this.relatedCode = relatedCode;
    }
}
