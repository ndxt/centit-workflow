package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.common.WorkTimeSpan;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * create by scaffold
 * @author codefan@hotmail.com
 */
@Data
public class UserTask implements java.io.Serializable {
    private static final long serialVersionUID =  1L;
    private String nodeInstId;
    @DictionaryMap(value="unitCode", fieldName="unitName")

    private String unitCode;
    @DictionaryMap(value="userCode", fieldName="userName")

    private String userCode;

    private String flowInstId;
    private String authDesc;
    private String flowOptName;
    private String flowOptTag;

    @DictionaryMap(value="flowCode", fieldName="flowName")
    private String flowCode;
    private String version;
    private String nodeCode;
    private String nodeName;
    private String nodeType;
    private String nodeOptType;
    private String nodeParam;
    private String optParam;
    private String optCode;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String expireOpt;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @DictionaryMap(value="userCode", fieldName="lastUpdateUserName")
    private String lastUpdateUser;
    private Long promiseTime;
    private Long timeLimit;

    @DictionaryMap(value="userCode", fieldName="grantorName")
    private String grantor;
    private String roleType;
    private String roleCode;
    private String instState;
    private String osId;
    private String optId;
    private String optUrl;
    private String stageCode;
    @DictionaryMap(value="userCode", fieldName="creatorName")
    private String creatorCode;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeCreateTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeExpireTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeLastUpdateTime;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date flowExpireTime;

    private Long flowTimeLimit;
    private Long flowPromiseTime;

    // Constructors
    /** default constructor */
    public UserTask() {
    }

    public String getPromiseTimeStr() {
        if(promiseTime==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(promiseTime);
        return wts.getTimeSpanDesc();
    }

    public String getTimeLimitStr() {
        if(timeLimit==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(timeLimit);
        return wts.getTimeSpanDesc();
    }

    public String getFlowTimeLimitStr() {
        if(flowTimeLimit==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(flowTimeLimit);
        return wts.getTimeSpanDesc();
    }

    public String getFlowPromiseTimeStr() {
        if(flowPromiseTime==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumberAsMinute(flowPromiseTime);
        return wts.getTimeSpanDesc();
    }

    public String getNodeOptUrl(){
        if(this.optUrl==null) {
            //this.optUrl=this.optCode;
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder(this.optUrl);
        if(!this.optUrl.endsWith("?") && !this.optUrl.endsWith("&")){
            if(this.optUrl.indexOf('?') == -1 )
              urlBuilder.append('?');
            else
              urlBuilder.append('&');
        }
        urlBuilder.append("flowInstId=")
            .append(this.flowInstId)
            .append("&nodeInstId=")
            .append(this.nodeInstId);
        if(StringUtils.isNotBlank(this.flowOptTag)){
            urlBuilder.append("&flowOptTag=")
                .append(this.flowOptTag);
        }
        if(StringUtils.isNotBlank(this.nodeParam)){
            urlBuilder.append("&nodeParam=")
                .append(this.nodeParam);
        }
        if(StringUtils.isNotBlank(this.optParam)){
            urlBuilder.append("&")
            .append(this.optParam);
        }
        if(StringUtils.isNotBlank(this.grantor) &&
          ! StringUtils.equals(this.grantor, this.userCode)){
            urlBuilder.append("&grantor=")
              .append(this.grantor);
        }
        return urlBuilder.toString();
    }

    public void copy(UserTask other) {
        this.nodeInstId = other.getNodeInstId();
        this.unitCode = other.getUnitCode();
        this.userCode = other.getUserCode();

        this.flowInstId = other.getFlowInstId();
        this.authDesc = other.getAuthDesc();
        this.flowOptName = other.getFlowOptName();
        this.flowOptTag = other.getFlowOptTag();
        this.flowCode = other.getFlowCode();
        this.version  = other.getVersion();
        this.nodeCode = other.getNodeCode();
        this.nodeName = other.getNodeName();
        this.nodeType = other.getNodeType();
        this.nodeOptType = other.getNodeOptType();
        this.nodeParam = other.getNodeParam();
        this.optParam = other.getOptParam();
        this.optCode = other.getOptCode();

        this.createTime = other.getCreateTime();
        this.expireOpt = other.getExpireOpt();

        this.lastUpdateTime = other.getLastUpdateTime();

        this.lastUpdateUser = other.getLastUpdateUser();
        this.promiseTime = other.getPromiseTime();
        this.timeLimit = other.getTimeLimit();

        this.grantor = other.getGrantor();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.instState = other.getInstState();
        this.osId = other.getOsId();
        this.optId = other.getOptId();
        this.optUrl = other.getOptUrl();
        this.stageCode = other.getStageCode();

        this.creatorCode = other.getCreatorCode();

        this.nodeCreateTime = other.getNodeCreateTime();
        this.nodeExpireTime = other.getNodeExpireTime();
        this.nodeLastUpdateTime = other.getNodeLastUpdateTime();
        this.flowExpireTime = other.getFlowExpireTime();

        this.flowTimeLimit = other.getFlowTimeLimit();
        this.flowPromiseTime = other.getFlowPromiseTime();
    }

}
