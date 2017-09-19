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
@Table(name = "OPT_STAGE")
public class OptStage {
    @Id
    @NotNull
    @Column(name = "OPT_STAGE_ID")
    private Long optStageId;
    @NotNull
    @Column(name = "OPT_ID")
    private Long optId;
    @NotNull
    @Column(name = "FLOW_CODE")
    private Long flowCode;
    @NotNull
    @Column(name = "OPT_STAGE_NAME")
    private String optStageName;
    @NotNull
    @Column(name = "OPT_STAGE_CODE")
    private String optStageCode;
    @Column(name = "OPT_STAGE_ORDER")
    private Integer optStageOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;
}
