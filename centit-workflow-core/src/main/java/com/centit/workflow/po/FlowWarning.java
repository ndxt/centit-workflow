package com.centit.workflow.po;

import javax.persistence.*;
import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */ 

@Entity
@Table(name="WF_RUNTIME_WARNING")
public class FlowWarning implements java.io.Serializable {
	private static final long serialVersionUID =  1L;
	@Id
	@Column(name = "WARNING_ID")
	@GeneratedValue(generator = "assignedGenerator")
	//@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
	private Long warningId;
	@Column(name="NODE_INST_ID")
	private Long  nodeInstId;
	@Column(name="FLOW_INST_ID")
	private Long  flowInstId;
	@Column(name="FLOW_STAGE")
	private String  flowStage;
	@Column(name="OBJ_TYPE")
	private String  objType;
	@Column(name="WARNING_TYPE")
	private String  warningType;
	@Column(name="WARNING_CODE")
	private String  warningCode;
	@Column(name="WARNING_TIME")
	private Date  warningTime;
	@Column(name="WARNING_STATE")
	private String  warningState;
	@Column(name="NOTICE_STATE")
	private String noticeState;
	@Column(name="WARNINGID_MSG")
    private String  warningidMsg;
    @Column(name="SEND_MSG_TIME")
	private Date  sendMsgTime;
    @Column(name="SEND_USERS")
	private String  sendUsers;

	// Constructors
	/** default constructor */
	public FlowWarning() {
	}
	/** minimal constructor */
	public FlowWarning(
		Long warningid		
		,Long  flowInstId) {
	
	
		this.warningId = warningid;
	
		this.flowInstId= flowInstId; 		
	}

/** full constructor */
	public FlowWarning(
	 Long warningid		
	,Long  nodeInstId,Long  flowInstId,String  flowPhase,String  warningType,String  warningCode,Date  warningTime,String  warningState,String  warningidMsg,Date  sendMsgTime,String  sendUsers) {
	
		this.warningId = warningid;
	
		this.nodeInstId= nodeInstId;
		this.flowInstId= flowInstId;
		this.flowStage= flowPhase;
		this.warningType= warningType;
		this.warningCode= warningCode;
		this.warningTime= warningTime;
		this.warningState= warningState;
		this.warningidMsg= warningidMsg;
		this.sendMsgTime= sendMsgTime;
		this.sendUsers= sendUsers;		
	}
	
	/**
     * F ： 工作流 N ：节点 P：阶段
     * @return
     */
    public String getObjType() {
        return objType;
    }
    /**
     * F ： 工作流 N ：节点 P：阶段
     * @param objType
     */
    public void setObjType(String objType) {
        this.objType = objType;
    }
    /**
     * 0 待发送 1 已发送 2 发送消息失败
     * @return
     */
    public String getNoticeState() {
        return noticeState;
    }
    /**
     * 0 待发送 1 已发送 2 发送消息失败
     * @param noticeState
     */
    public void setNoticeState(String noticeState) {
        this.noticeState = noticeState;
    }
  
	public Long getWarningid() {
		return this.warningId;
	}

	public void setWarningid(Long warningid) {
		this.warningId = warningid;
	}
	// Property accessors
  
	public Long getNodeInstId() {
		return this.nodeInstId;
	}
	
	public void setNodeInstId(Long nodeInstId) {
		this.nodeInstId = nodeInstId;
	}
  
	public Long getFlowInstId() {
		return this.flowInstId;
	}
	
	public void setFlowInstId(Long flowInstId) {
		this.flowInstId = flowInstId;
	}
  
    public String getFlowStage() {
        return this.flowStage;
    }

    public void setFlowStage(String stageCode) {
        this.flowStage = stageCode;
    }

	/**
	 * @return W，预警  A  报警 N 提醒  O 其他
	 */
	public String getWarningType() {
		return this.warningType;
	}
	/**
	 * 
	 * @param warningType W，预警  A  报警 N 提醒  O 其他
	 */
	public void setWarningType(String warningType) {
		this.warningType = warningType;
	}
  
	public String getWarningCode() {
		return this.warningCode;
	}
	
	public void setWarningCode(String warningCode) {
		this.warningCode = warningCode;
	}
  
	public Date getWarningTime() {
		return this.warningTime;
	}
	
	public void setWarningTime(Date warningTime) {
		this.warningTime = warningTime;
	}
  
	/**
	 * D 摘牌 C 纠正 F 督办 N 未处理
	 */
	public String getWarningState() {
		return this.warningState;
	}
	
	/**
	 * D 摘牌 C 纠正 F 督办 N 未处理
	 * @param warningState
	 */
	public void setWarningState(String warningState) {
		this.warningState = warningState;
	}
  
	public String getWarningidMsg() {
		return this.warningidMsg;
	}
	
	public void setWarningidMsg(String warningidMsg) {
		this.warningidMsg = warningidMsg;
	}
  
	public Date getSendMsgTime() {
		return this.sendMsgTime;
	}
	
	public void setSendMsgTime(Date sendMsgTime) {
		this.sendMsgTime = sendMsgTime;
	}
  
	public String getSendUsers() {
		return this.sendUsers;
	}
	
	public void setSendUsers(String sendUsers) {
		this.sendUsers = sendUsers;
	}



	public void copy(FlowWarning other){
  
		this.setWarningid(other.getWarningid());
  
		this.nodeInstId= other.getNodeInstId();  
		this.flowInstId= other.getFlowInstId();  
		this.flowStage= other.getFlowStage();  
		this.warningType= other.getWarningType();  
		this.warningCode= other.getWarningCode();  
		this.warningTime= other.getWarningTime();  
		this.warningState= other.getWarningState();  
		this.warningidMsg= other.getWarningidMsg();  
		this.sendMsgTime= other.getSendMsgTime();  
		this.sendUsers= other.getSendUsers();

	}
	
	public void copyNotNullProperty(FlowWarning other){
  
	if( other.getWarningid() != null)
		this.setWarningid(other.getWarningid());
  
		if( other.getNodeInstId() != null)
			this.nodeInstId= other.getNodeInstId();  
		if( other.getFlowInstId() != null)
			this.flowInstId= other.getFlowInstId();  
		if( other.getFlowStage() != null)
			this.flowStage= other.getFlowStage();  
		if( other.getWarningType() != null)
			this.warningType= other.getWarningType();  
		if( other.getWarningCode() != null)
			this.warningCode= other.getWarningCode();  
		if( other.getWarningTime() != null)
			this.warningTime= other.getWarningTime();  
		if( other.getWarningState() != null)
			this.warningState= other.getWarningState();  
		if( other.getWarningidMsg() != null)
			this.warningidMsg= other.getWarningidMsg();  
		if( other.getSendMsgTime() != null)
			this.sendMsgTime= other.getSendMsgTime();  
		if( other.getSendUsers() != null)
			this.sendUsers= other.getSendUsers();

	}
	
	public void clearProperties(){
  
		this.nodeInstId= null;  
		this.flowInstId= null;  
		this.flowStage= null;  
		this.warningType= null;  
		this.warningCode= null;  
		this.warningTime= null;  
		this.warningState= null;  
		this.warningidMsg= null;  
		this.sendMsgTime= null;  
		this.sendUsers= null;

	}
  
}
