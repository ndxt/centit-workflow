package com.centit.workflow.external;

import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.UuidOpt;

/**
 * Created by codefan on 17-9-12.
 */
public class ExtSysUserUnit implements IUserUnit {
    private String userCode;
    private String unitCode;
    private String userUnitId;
    private String isPrimary;
    private String userStation;
    private String userRank;
    private Long userOrder;

    public ExtSysUserUnit(){
        this.userUnitId = UuidOpt.getUuidAsString32();
    }
    /**
     * 关联关系主键
     *
     * @return 关联关系主键
     */
    @Override
    public String getUserUnitId() {
        return this.userUnitId;
    }

    /**
     * 用户编码，是用户的主键
     *
     * @return 用户编码，是用户的主键
     */
    @Override
    public String getUserCode() {
        return this.userCode;
    }

    /**
     * 机构代码 是机构的主键
     *
     * @return 机构代码 是机构的主键
     */
    @Override
    public String getUnitCode() {
        return this.unitCode;
    }

    /**
     * 是否为主机构 T:主机构 F：辅机构
     *
     * @return 是否为主机构 T:主机构 F：辅机构
     */
    @Override
    public String getIsPrimary() {
        return this.isPrimary;
    }

    /**
     * 用户在本机构的岗位
     *
     * @return 用户在本机构的岗位
     */
    @Override
    public String getUserStation() {
        return this.userStation;
    }

    /**
     * 用户在本机构的行政职务
     *
     * @return 用户在本机构的行政职务
     */
    @Override
    public String getUserRank() {
        return this.userRank;
    }

    /**
     * 用户在本单位的排序号
     *
     * @return 排序号
     */
    @Override
    public Long getUserOrder() {
        return this.userOrder;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public void setUserUnitId(String userUnitId) {
        this.userUnitId = userUnitId;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void setUserStation(String userStation) {
        this.userStation = userStation;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public void setUserOrder(Long userOrder) {
        this.userOrder = userOrder;
    }
}
