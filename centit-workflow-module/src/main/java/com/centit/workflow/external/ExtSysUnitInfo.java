package com.centit.workflow.external;

import com.centit.framework.model.basedata.IUnitInfo;

/**
 * Created by codefan on 17-9-12.
 */
public class ExtSysUnitInfo implements IUnitInfo{

    private String unitCode;
    private String unitName;
    private String parentUnit;
    private Long unitOrder;
    private String unitManager;
    private String unitPath;
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
     * 机构自编代码
     *
     * @return 机构自编代码
     */
    @Override
    public String getDepNo() {
        return getUnitCode();
    }

    /**
     * 机构名称
     *
     * @return 机构名称
     */
    @Override
    public String getUnitName() {
        return this.unitName;
    }

    @Override
    public String getUnitShortName() {
        return this.unitName;
    }

    /**
     * 上级机构代码
     *
     * @return 上级机构代码
     */
    @Override
    public String getParentUnit() {
        return this.parentUnit;
    }

    /**
     * 机构类别
     *
     * @return 机构类别
     */
    @Override
    public String getUnitType() {
        return "U";
    }

    /**
     * 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     *
     * @return 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     */
    @Override
    public String getIsValid() {
        return "T";
    }

    /**
     * 机构路径
     *
     * @return 机构路径
     */
    @Override
    public String getUnitPath() {
        return this.unitPath;
    }

    /**
     * 机构排序
     *
     * @return 机构排序
     */
    @Override
    public Long getUnitOrder() {
        return this.unitOrder;
    }

    /**
     * 分管领导（机构管理员）
     *
     * @return 分管领导（机构管理员）
     */
    @Override
    public String getUnitManager() {
        return this.unitManager;
    }

    @Override
    public String getUnitTag() {
        return null;
    }


    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setParentUnit(String parentUnit) {
        this.parentUnit = parentUnit;
    }


    public void setUnitOrder(Long unitOrder) {
        this.unitOrder = unitOrder;
    }

    public void setUnitManager(String unitManager) {
        this.unitManager = unitManager;
    }

    public void setUnitPath(String unitPath) {
        this.unitPath = unitPath;
    }
}
