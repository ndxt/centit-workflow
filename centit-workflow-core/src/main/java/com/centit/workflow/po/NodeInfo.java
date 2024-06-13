package com.centit.workflow.po;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Entity
@Data
@Table(name = "WF_NODE")
public class NodeInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "NODE_ID")
    private String nodeId;

    @Column(name = "FLOW_CODE")
    @NotNull
    @Length(max = 32)
    private String flowCode;

    /**
     * 框架解析 不到ManyToOne的属性 这儿单独 设置
     */
    @Column(name = "VERSION")
    @NotNull
    @Range( max = 9999)
    private Long version;

    public static final String NODE_TYPE_START    = "A";
    @Deprecated
    public static final String NODE_TYPE_FIRST    = "B";
    public static final String NODE_TYPE_OPT      = "C";
    public static final String NODE_TYPE_AUTO     = "D";
    public static final String NODE_TYPE_SYNC     = "E";
    public static final String NODE_TYPE_END      = "F";
    public static final String NODE_TYPE_ROUTE    = "R";
    public static final String NODE_TYPE_SUBFLOW    = "S";
    /**
     * A:开始（这个在数据库中其实不存在）
     * --B:首节点(首节点不能是路由节点，如果是路由节点请设置为 哑元，跳转到后一个节点； B 的处理换个C一样)
     * C:业务节点
     * D:自动运行节点
     * R:路由节点
     * E:同步节点（消息响应节点，或等待时间）
     * F:结束
     * S:子流程
     */
    @Column(name = "NODE_TYPE")
    private String nodeType;

    @Column(name = "NODE_NAME")
    private String nodeName;

    /**NODE_TYPE == NODE_TYPE_OPT
     * optRuntType A: 指定到人 唯一执行人 B: 动态分配 C: 多人操作（已废弃）
     * */
    public static final String OPT_RUN_TYPE_NORMAL   = "A";
    public static final String OPT_RUN_TYPE_DYNAMIC  = "B";
    public static final String OPT_RUN_TYPE_TEAMWORK = "C";

    /**
     * NODE_TYPE == NODE_TYPE_AUTO
     * autoRunType ：
     * N：无操作、哑元
     * B：调用Bean包括远程Bean
     * S：脚本
     * M：发送消息
     * C：接口调用
     * */
    public static final String AUTO_NODE_OPT_CODE_NONE    = "N";
    public static final String AUTO_NODE_OPT_CODE_BEAN    = "B";
    public static final String AUTO_NODE_OPT_CODE_SCRIPT  = "S";
    public static final String AUTO_NODE_OPT_CODE_MESSAGE = "M";
    public static final String AUTO_NODE_OPT_CODE_CALL    = "C";
    /**
     * NODE_TYPE == NODE_TYPE_ROUTE
     * routerType
     * D:分支 E:汇聚  G：多实例节点  H：并行  R：游离
     */
    public static final String ROUTER_TYPE_BRANCH    = "D";
    public static final String ROUTER_TYPE_COLLECT   = "E";
    public static final String ROUTER_TYPE_PARALLEL  = "H";
    public static final String ROUTER_TYPE_MULTI_INST= "G";
    public static final String ROUTER_TYPE_ISOLATED  = "R";
    //public static final String ROUTER_TYPE_SYNC      = "S";

    /**
     * NODE_TYPE == NODE_TYPE_SYNC
     * nodeSyncType
     * T：时间触发
     * M：消息触发
     * */
    public static final String SYNC_NODE_TYPE_TIME    = "T";
    public static final String SYNC_NODE_TYPE_MESSAGE = "M";

    /**
     * optType 为一个多种意义的字段
     */
    @JSONField(serialize = false, deserialize = false)
    @Column(name = "OPT_TYPE")
    private String optType;

    /**
     * 业务ID 关联到 FlowOptInfo
     */
    @Column(name = "OPT_ID")
    private String optId;

    /**
     * 业务页面代码 关联到 FlowOptPage
     **/
    @Column(name = "OPT_CODE")
    private String optCode;

    /**
     * optType = D && optCode = S 时
     * optParam 存放的是代码脚本
     */
    @Length(max = 2048)
    @Column(name = "OPT_PARAM")
    private String optParam;

    @Column(name = "OS_ID")
    private String osId;

    @Column(name = "OPT_BEAN")
    private String optBean;

    @Column(name = "SUB_FLOW_CODE")
    private String subFlowCode;

    /**
     * EN GW XZ BJ RO SF
     * @see com.centit.framework.components.SysUserFilterEngine
     */
    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "UNIT_EXP")
    private String unitExp;

    @Column(name = "POWER_EXP")
    private String powerExp;

    @Column(name = "NODE_DESC")
    private String nodeDesc;

    public static final String TIME_LIMIT_TYPE_IGNORE    = "I";
    public static final String TIME_LIMIT_TYPE_NONE      = "N";
    public static final String TIME_LIMIT_TYPE_FIX       = "F";
    public static final String TIME_LIMIT_TYPE_CYCLE     = "C";
    /**
     * 期限类别 I ： 未设置（ignore 在流转线上默认 ）、
     * N 无 (无期限 none 默认) 、
     * F 每实例固定期限 fix 、
     * C 节点固定期限  cycle、
     * H 继承其他节点期限 hierarchical。
     */
    @Column(name = "LIMIT_TYPE")
    private String limitType;

    /** TODO 添加一些固定的时间 字符串
     * D 当天完成 day
     * W 当周完成 week
     * M 当月完成 month
     * Y 当年完成 year
     */
    @Column(name = "TIME_LIMIT")
    private String timeLimit;

    public static final String TIME_LIMIT_INHERIT_NONE    = "0";
    public static final String TIME_LIMIT_INHERIT_LEAD    = "1";
    public static final String TIME_LIMIT_INHERIT_ASSIGNED = "2";
    /**
     *  0：不继承， 1 ：继承前节点 2 ：继承指定节点；
     */
    @Column(name = "INHERIT_TYPE")
    private String inheritType;

    /**
     * 继承节点环节代码 输入框 ，文本输入； XML 属性名 inheritNodeCode
     * InheritType == '2' 时有效
     */
    @Column(name = "INHERIT_NODE_CODE")
    private String inheritNodeCode;

    public static final String TIME_EXPIRE_OPT_NONE    = "O";
    public static final String TIME_EXPIRE_OPT_NOTIFY  = "N";
    public static final String TIME_EXPIRE_OPT_SUSPEND = "X";
    public static final String TIME_EXPIRE_OPT_END_FLOW= "E";
    public static final String TIME_EXPIRE_OPT_SUBMIT  = "C";
    public static final String TIME_EXPIRE_OPT_CALL_API= "A";
    /**
     * N：仅通知， O:不处理 ，X：挂起，E：终止（流程）， C：完成（强制提交,提交失败就挂起）
     * A ：调用api
     */
    @Column(name = "EXPIRE_OPT")
    private String expireOpt;

    @Column(name = "EXPIRE_CALL_API")
    private String expireCallApi;

    @Column(name = "WARNING_PARAM")
    private String warningParam;

    public static final String NODE_NOTICE_TYPE_NONE  = "N";
    public static final String NODE_NOTICE_TYPE_DEFAULT  = "D";
    /**
     * 通知类别
     */
    @Column(name = "NOTICE_TYPE")
    private String noticeType;

    /**
     * 通知对象
     */
    @Column(name = "NOTICE_USER_EXP")
    private String noticeUserExp;
    /**
     * 通知消息模板
     */
    @Column(name = "NOTICE_MESSAGE")
    private String noticeMessage;
    /**
     * 环节代码
     */
    @Column(name = "NODE_CODE")
    private String nodeCode;
    /**
     * 风险消息模板
     */
    @Column(name = "RISK_INFO")
    private String riskinfo;
    /**
     * 节点所属阶段
     */
    @Column(name = "STAGE_CODE")
    private String stageCode;

    public static final String ROUTER_MULTI_TYPE_UNIT  = "D";
    public static final String ROUTER_MULTI_TYPE_USER  = "U";
    public static final String ROUTER_MULTI_TYPE_VALUE = "V";

    /**
     * 多实例类型
     */
    @Column(name = "MULTI_INST_TYPE")
    private String multiInstType;
    /**
     * 多实例参数
     */
    @Column(name = "MULTI_INST_PARAM")
    private String multiInstParam;

    public static final String ROUTER_COLLECT_TYPE_ALL_COMPLETED  = "A";
    public static final String ROUTER_COLLECT_TYPE_LEAST_COMPLETED  = "R";
    public static final String ROUTER_COLLECT_TYPE_MOST_UNCOMPLETED = "L";
    public static final String ROUTER_COLLECT_TYPE_RATE  = "V";
    public static final String ROUTER_COLLECT_TYPE_EXTRA = "E";

    /**
     * A 所有都完成，R 至少有X完成，L 至多有X未完成， V 完成比率达到X ，E  外埠判断
     */
    @Column(name = "CONVERGE_TYPE")
    private String convergeType;

    @Column(name = "CONVERGE_PARAM")
    private String convergeParam;

    @JSONField(serialize=false)
    private FlowInfo flowDefine;

    @Column(name = "SOURCE_ID")
    @ApiModelProperty(value = "模板来源")
    @JSONField(serialize = false)
    private String sourceId;

    // Constructors
    /** default constructor */
    public NodeInfo() {
        this.inheritType = "0";
    }

    /** minimal constructor */
    public NodeInfo(String nodeid, String nodetype) {
        this.nodeId = nodeid;
        this.nodeType = nodetype;
        this.inheritType = "0";
    }

    public String getOptRunType(){
        return NODE_TYPE_OPT.equals(this.nodeType)?this.optType: null;
    }

    public void setOptRunType(String optRunType){
        // 自动执行节点的流程图json中optRunType为D, 子流程节点的流程图json中optRunType为S
        if (!NODE_TYPE_AUTO.equals(optRunType) && !NODE_TYPE_SUBFLOW.equals(optRunType)) {
            this.nodeType = NODE_TYPE_OPT;
            this.optType = optRunType;
        }
    }

    public String getAutoRunType(){
        return NODE_TYPE_AUTO.equals(this.nodeType)?this.optType: null;
    }

    public void setAutoRunType(String autoRunType){
        this.nodeType = NODE_TYPE_AUTO;
        this.optType = autoRunType;
    }

    public String getRouterType(){
        return NODE_TYPE_ROUTE.equals(this.nodeType)?this.optType: null;
    }

    public void setRouterType(String routerType){
        this.nodeType = NODE_TYPE_ROUTE;
        this.optType = routerType;
    }

    public String getNodeSyncType(){
        return NODE_TYPE_SYNC.equals(this.nodeType)?this.optType: null;
    }

    public void setNodeSyncType(String nodeSyncType){
        this.nodeType = NODE_TYPE_SYNC;
        this.optType = nodeSyncType;
    }

    public String getMessageCode(){
        return this.optCode;
    }

    public void setMessageCode(String messageCode){
        this.optCode = messageCode;
    }

    public String getSyncTimeDesc(){
        return this.timeLimit;
    }

    public void setSyncTimeDesc(String syncTimeDesc){
        this.timeLimit = syncTimeDesc;
    }


    public void copy(NodeInfo other) {
        //this.setNodeId(other.getNodeId());
        this.setFlowDefine(other.getFlowDefine());
        //this.getFlowDefine().setFlowCode(other.getFlowCode());
        this.nodeType = other.getNodeType();
        this.nodeName = other.getNodeName();
        this.optType = other.getOptType();

        this.osId = other.getOsId();
        this.optId = other.getOptId();
        this.optCode = other.getOptCode();
        this.optParam = other.getOptParam();
        this.optBean = other.getOptBean();
        this.subFlowCode = other.getSubFlowCode();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.unitExp = other.getUnitExp();
        this.powerExp = other.getPowerExp();
        this.nodeDesc = other.getNodeDesc();
        this.timeLimit = other.getTimeLimit();
        this.expireOpt = other.getExpireOpt();
        this.stageCode =other.getStageCode();
        //this.routerType=other.getRouterType();
        this.multiInstType=other.getMultiInstType();
        this.multiInstParam=other.getMultiInstParam();
        this.convergeParam=other.getConvergeParam();
        this.convergeType=other.getConvergeType();
        this.warningParam=other.getWarningParam();
    }

    public void copyNotNullProperty(NodeInfo other) {

        /*if (other.getNodeId() != null)
            this.setNodeId(other.getNodeId());*/

        if( other.getFlowDefine() != null)
            this.setFlowDefine(other.getFlowDefine());

        if (other.getNodeType() != null)
            this.nodeType = other.getNodeType();
        if (other.getNodeName() != null)
            this.nodeName = other.getNodeName();
        if (other.getOptType() != null)
            this.optType = other.getOptType();
        if (other.getOptCode() != null)
            this.optCode = other.getOptCode();
        if (other.getOptParam() != null)
            this.optParam = other.getOptParam();
        if (other.getOptBean() != null)
            this.optBean = other.getOptBean();
        if (other.getSubFlowCode() != null)
            this.subFlowCode = other.getSubFlowCode();
        if (other.getRoleType() != null)
            this.roleType = other.getRoleType();
        if (other.getRoleCode() != null)
            this.roleCode = other.getRoleCode();
        if (other.getUnitExp() != null)
            this.unitExp = other.getUnitExp();
        if (other.getPowerExp() != null)
            this.powerExp = other.getPowerExp();
        if (other.getNodeDesc() != null)
            this.nodeDesc = other.getNodeDesc();
        if (other.getTimeLimit() != null)
            this.timeLimit = other.getTimeLimit();
        if (other.getExpireOpt() != null)
            this.expireOpt = other.getExpireOpt();
        if (other.getStageCode()!=null)
            this.stageCode =other.getStageCode();
        if (other.getNoticeMessage()!=null)
            this.noticeMessage=other.getNoticeMessage();
        if (other.getNoticeType()!=null)
            this.noticeType=other.getNoticeType();
        if (other.getOsId()!=null)
            this.osId = other.getOsId();
        if (other.getOptId()!=null)
            this.optId = other.getOptId();

        //if (other.getRouterType()!=null)
        //    this.routerType=other.getRouterType();
        if (other.getMultiInstType()!=null)
            this.multiInstType=other.getMultiInstType();
        if (other.getMultiInstParam()!=null)
            this.multiInstParam=other.getMultiInstParam();
        if (other.getConvergeParam()!=null)
            this.convergeParam=other.getConvergeParam();
        if (other.getConvergeType()!=null)
            this.convergeType=other.getConvergeType();
        if (other.getWarningParam()!=null)
            this.warningParam=other.getWarningParam();
    }

    public void clearProperties()
    {
        this.nodeId = null;
        this.getFlowDefine().setVersion(null);
        this.getFlowDefine().setFlowCode(null);
        this.nodeType =  null;
        this.nodeName =  null;
        this.optType =  null;
        this.optCode =  null;
        this.optParam =  null;
        this.optBean = null;
        this.subFlowCode = null;
        this.roleType = null;
        this.roleCode =  null;
        this.unitExp =  null;
        this.powerExp = null;
        this.nodeDesc = null;
        this.timeLimit = null;
        this.stageCode =null;
        this.expireOpt =  null;
        this.noticeType = null;
        this.noticeMessage = null;
        //this.routerType=null;
        this.multiInstType=null;
        this.multiInstParam=null;
        this.convergeParam=null;
        this.convergeType=null;
        this.warningParam=null;
    }

}
