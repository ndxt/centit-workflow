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
    private String optUrl;
    private String stageCode;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeCreateTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeExpireTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date nodeLastUpdateTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date flowExpireTime;
    private Long flowTimeLimit;
    private String osId;

    // Constructors
    /** default constructor */
    public UserTask() {
    }



    public String getPromiseTimeStr() {
        if(promiseTime==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(promiseTime);
        return wts.getTimeSpanDesc();
    }


    public String getTimeLimitStr() {
        if(timeLimit==null)
            return "";
        WorkTimeSpan wts = new WorkTimeSpan();
        wts.fromNumber(timeLimit);
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


    public void copy(UserTask other){

        this.setNodeInstId(other.getNodeInstId());
        this.setUnitCode(other.getUnitCode());
        this.setUserCode(other.getUserCode());
        this.setRoleType(other.getRoleType());
        this.setRoleCode(other.getRoleCode());

        this.flowInstId= other.getFlowInstId();
        this.authDesc= other.getAuthDesc();
        this.flowOptName= other.getFlowOptName();
        this.flowOptTag= other.getFlowOptTag();
        this.nodeName= other.getNodeName();
        this.nodeType= other.getNodeType();
        this.nodeOptType= other.getNodeOptType();
        this.optParam= other.getOptParam();
        this.optCode= other.getOptCode();
        this.createTime= other.getCreateTime();
        this.promiseTime= other.getPromiseTime();
        this.lastUpdateTime= other.getLastUpdateTime();
        this.expireOpt= other.getExpireOpt();
        this.grantor = other.getGrantor();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.optUrl = other.getOptUrl();
        this.stageCode = other.getStageCode();
    }

    public void copyNotNullProperty(UserTask other){

    if( other.getNodeInstId() != null)
        this.setNodeInstId(other.getNodeInstId());
    if( other.getUnitCode() != null)
        this.setUnitCode(other.getUnitCode());
    if( other.getUserCode() != null)
        this.setUserCode(other.getUserCode());
    if( other.getRoleType() != null)
        this.setRoleType(other.getRoleType());
    if( other.getRoleCode() != null)
        this.setRoleCode(other.getRoleCode());

        if( other.getFlowInstId() != null)
            this.flowInstId= other.getFlowInstId();
        if( other.getAuthDesc() != null)
            this.authDesc= other.getAuthDesc();
        if( other.getFlowOptName() != null)
            this.flowOptName= other.getFlowOptName();
        if( other.getFlowOptTag() != null)
            this.flowOptTag= other.getFlowOptTag();
        if( other.getNodeName() != null)
            this.nodeName= other.getNodeName();
        if( other.getNodeType() != null)
            this.nodeType= other.getNodeType();
        if( other.getNodeOptType() != null)
            this.nodeOptType= other.getNodeOptType();
        if( other.getOptParam() != null)
            this.optParam= other.getOptParam();
        if( other.getOptCode() != null)
            this.optCode= other.getOptCode();
        if( other.getCreateTime() != null)
            this.createTime= other.getCreateTime();
        if( other.getPromiseTime() != null)
            this.promiseTime= other.getPromiseTime();
        if( other.getExpireOpt() != null)
            this.expireOpt= other.getExpireOpt();
        if( other.getLastUpdateTime() != null)
            this.lastUpdateTime= other.getLastUpdateTime();
        if(other.getGrantor() != null){
            this.grantor = other.getGrantor();
        }

        if(other.getRoleType() != null){
            this.roleType = other.getRoleType();
        }
        if(other.getRoleCode() != null){
            this.roleCode = other.getRoleCode();
        }
        if(other.getOptUrl() != null){
            this.optUrl = other.getOptUrl();
        }
        if(other.getStageCode() !=null){
            this.stageCode = other.getStageCode();
        }
    }

    public void clearProperties(){

        this.flowInstId= null;
        this.authDesc= null;
        this.flowOptName= null;
        this.flowOptTag= null;
        this.nodeName= null;
        this.nodeType= null;
        this.nodeOptType= null;
        this.optParam= null;
        this.optCode= null;
        this.createTime= null;
        this.promiseTime=null;
        this.expireOpt= null;
        this.grantor = null;
        this.stageCode = null;
    }

}
