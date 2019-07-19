package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class StageInstanceId implements java.io.Serializable {
    private static final long serialVersionUID =  1L;


    @Column(name = "FLOW_INST_ID")
    private Long flowInstId;

    @Column(name = "STAGE_ID")
    private String stageId;

    // Constructors
    /** default constructor */
    public StageInstanceId() {
    }
    /** full constructor */
    public StageInstanceId(Long flowInstId, String stageId) {

        this.flowInstId = flowInstId;
        this.stageId = stageId;
    }

    public Long getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(Long flowInstId) {
        this.flowInstId = flowInstId;
    }

    public String getStageId() {
        return this.stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }


    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof StageInstanceId))
            return false;

        StageInstanceId castOther = (StageInstanceId) other;
        boolean ret = true;

        ret = ret && ( this.getFlowInstId() == castOther.getFlowInstId() ||
                       (this.getFlowInstId() != null && castOther.getFlowInstId() != null
                               && this.getFlowInstId().equals(castOther.getFlowInstId())));

        ret = ret && ( this.getStageId() == castOther.getStageId() ||
                       (this.getStageId() != null && castOther.getStageId() != null
                               && this.getStageId().equals(castOther.getStageId())));

        return ret;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result +
             (this.getFlowInstId() == null ? 0 :this.getFlowInstId().hashCode());

        result = 37 * result +
             (this.getStageId() == null ? 0 :this.getStageId().hashCode());

        return result;
    }
}
