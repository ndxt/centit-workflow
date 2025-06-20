package com.centit.workflow.po;

import com.alibaba.fastjson2.JSON;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.network.UrlOptUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

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
    private String taskAssigned;
    private String authDesc;
    private String flowOptName;
    private String flowOptTag;

    @DictionaryMap(value="flowCode", fieldName="flowName")
    private String flowCode;
    private String version;
    private String nodeCode;
    private String nodeName;
    private String nodeType;

    /**NODE_TYPE == NODE_TYPE_OPT
     * A: 指定到人 唯一执行人 B: 动态分配 C: 多人操作（已废弃）
        * */
    private String nodeOptType;
    private String stageCode;
    private String instState;
    private String osId;

    private String modelId;

    private String optId;
    private String optCode;
    private String optParam;

    @DictionaryMap(value="userCode", fieldName="grantorName")
    private String grantor;
    private String roleType;
    private String roleCode;
    private Date createTime;
    private String expireOpt;
    private Date lastUpdateTime;

    @DictionaryMap(value="userCode", fieldName="lastUpdateUserName")
    private String lastUpdateUser;

    @DictionaryMap(value="userCode", fieldName="creatorName")
    private String creatorCode;

    private String promiseTime;
    private Date nodeExpireTime;
    private Date flowExpireTime;
    private Date nodeWarningTime;
    private Date flowWarningTime;
    private Date flowCreateTime;

    private String prevNodeInstId;
    private String runToken;
    private String preInstId;
    // Constructors
    /** default constructor */
    public UserTask() {
    }

    public String getNodeOptUrl(){
        Map<String, Object> urlParams =
            CollectionsOpt.createHashMap("osId", this.getOsId(),
                "optId", this.getOptId(),
                "optCode", this.getOptCode(),
                "optParam", this.getOptParam(),
                "flowInstId", this.getFlowInstId(),
                "nodeInstId", this.getNodeInstId(),
                "flowOptTag", this.getFlowOptTag(),
                "flowCode", this.getFlowCode(),
                "grantor", this.getGrantor()
            );
        if(StringUtils.isNotBlank(this.getOptParam() )){
            urlParams.putAll(
                UrlOptUtils.splitUrlParamter(this.getOptParam()));
        }
        return JSON.toJSONString(urlParams);
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
        this.optParam = other.getOptParam();
        this.optCode = other.getOptCode();

        this.createTime = other.getCreateTime();
        this.expireOpt = other.getExpireOpt();
        this.modelId = other.getModelId();
        this.lastUpdateTime = other.getLastUpdateTime();

        this.lastUpdateUser = other.getLastUpdateUser();
        this.grantor = other.getGrantor();
        this.roleType = other.getRoleType();
        this.roleCode = other.getRoleCode();
        this.instState = other.getInstState();
        this.osId = other.getOsId();
        this.optId = other.getOptId();
        this.stageCode = other.getStageCode();

        this.creatorCode = other.getCreatorCode();
        this.promiseTime = other.getPromiseTime();
        this.nodeExpireTime = other.getNodeExpireTime();
        this.flowExpireTime = other.getFlowExpireTime();
        this.nodeWarningTime = other.getNodeExpireTime();
        this.flowWarningTime = other.getFlowExpireTime();
        this.flowCreateTime = other.getFlowCreateTime();
        this.prevNodeInstId = other.getPrevNodeInstId();
        this.runToken = other.getRunToken();
        this.preInstId = other.getPreInstId();
    }

}
