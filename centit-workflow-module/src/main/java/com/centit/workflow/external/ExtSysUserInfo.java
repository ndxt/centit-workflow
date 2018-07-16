package com.centit.workflow.external;

import com.centit.framework.model.basedata.IUserInfo;
import com.centit.support.algorithm.DatetimeOpt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by codefan on 17-9-12.
 */
public class ExtSysUserInfo implements IUserInfo {
    private String userCode;
    private String userName;
    private String primaryUnit;
    private Long userOrder;
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
     * String getUserPin()
     *
     * @return getUserPin
     */
    @Override
    public String getUserPin() {
        return "password";
    }

    @Override
    public Date getPwdExpiredTime() {
        return DatetimeOpt.addDays(DatetimeOpt.currentUtilDate(),100);
    }

    /**
     * 用户是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     *
     * @return 用户是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     */
    @Override
    public String getIsValid() {
        return "T";
    }

    /**
     * 用户名称  ，和 getUsername()不同后者返回的是用户登录名称
     *
     * @return 用户名称
     */
    @Override
    public String getUserName() {
        return this.userName;
    }

    /**
     * 用户登录名 同 getUsername
     *
     * @return 用户登录名
     */
    @Override
    public String getLoginName() {
        return getUserName();
    }

    /**
     * 用户默认机构（主机构）代码
     *
     * @return 用户默认机构（主机构）代码
     */
    @Override
    public String getPrimaryUnit() {
        return this.primaryUnit;
    }

    /**
     * 用户类别，各个业务系统自定义类别信息return null;
     *
     * @return 用户类别，各个业务系统自定义类别信息
     */
    @Override
    public String getUserType() {
        return "U";
    }

    /**
     * 用户注册邮箱
     *
     * @return 用户注册邮箱
     */
    @Override
    public String getRegEmail() {
        return "null";
    }

    /**
     * 用户注册手机号码
     *
     * @return 用户注册手机号码
     */
    @Override
    public String getRegCellPhone() {
        return "null";
    }

    /**
     * 用户排序号
     *
     * @return 用户排序号
     */
    @Override
    public Long getUserOrder() {
        return this.userOrder;
    }

    @Override
    public String getIdCardNo() {
        return null;
    }

    @Override
    public String getUserTag() {
        return null;
    }

    @Override
    public String getEnglishName() {
      return this.userName;
    }

    @Override
    public String getUserDesc() {
      return this.userName;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPrimaryUnit(String primaryUnit) {
        this.primaryUnit = primaryUnit;
    }

    public void setUserOrder(Long userOrder) {
        this.userOrder = userOrder;
    }

}
