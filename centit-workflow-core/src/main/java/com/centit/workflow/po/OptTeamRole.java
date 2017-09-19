package com.centit.workflow.po;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 */
@Entity
@Table(name = "OPT_TEAM_ROLE")
public class OptTeamRole {
    @Id
    @NotNull
    @Column(name = "OPT_TEAM_ROLE_ID")
    private Long optTeamRoleId;
    @NotNull
    @Column(name = "OPT_ID")
    private Long optId;
    @NotNull
    @Column(name = "FLOW_CODE")
    private Long flowCode;
    @NotNull
    @Column(name = "OPT_ROLE_CODE")
    private String optRoleCode;
    @NotNull
    @Column(name = "OPT_ROLE_NAME")
    private String optRoleName;
    @Column(name = "OPT_TEAM_ROLE_ORDER")
    private Integer optTeamRoleOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;
}
