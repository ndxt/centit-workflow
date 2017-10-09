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
@Table(name = "OPT_VARIABLE")
public class OptVariable {
    @Id
    @NotNull
    @Column(name = "OPT_VARIABLE_ID")
    private Long optVariableId;
    @NotNull
    @Column(name = "OPT_ID")
    private Long optId;
    @NotNull
    @Column(name = "OPT_VARIABLE_NAME")
    private String optVariableName;
    @Column(name = "OPT_VARIABLE_TYPE")
    private String optVariableType;
    @Column(name = "OPT_VARIABLE_ORDER")
    private Integer optVariableOrder;
    @Column(name = "GMT_CREATE_TIME")
    private Date gmtCreateTime;
    @Column(name = "GMT_MODIFY_TIME")
    private Date gmtModifyTime;
}
