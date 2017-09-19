package com.centit.workflow.po;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by chen_rj on 2017-9-19.
 */
public class OptNode {
    @Id
    @NotNull
    @Column(name = "OPT_NODE_ID")
    private Long optNodeId;
    @Column(name = "OPT_ID")
    private Long optId;
    @Column(name = "FLOW_CODE")
    private Long flowCode;
    @Column(name = "OPT_NAME")
    private String optName;
    @Column(name = "OPT_URL")
    private String optUrl;
    @Column(name = "OPT_TYPE")
    private String optType;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;
}
