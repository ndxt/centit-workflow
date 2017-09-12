package com.centit.workflow.external;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.impl.AbstractUserUnitFilterCalcContext;

import java.util.List;

/**
 * Created by codefan on 17-9-11.
 * 在这个过滤器中 用户信息只需要用户代码、用户默认机构
 * 机构信息中只需要机构代码、
 * 机构人员信息中需要 用户代码、机构代码、 用户位和职务
 * Rank 获取用户职务等级信息，有一个职务类表
 */
public class JdbcUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext {

    @Override
    public List<ExtSysUserInfo> listAllUserInfo() {
        return ExternalSystemData.allUserInfo;
    }

    @Override
    public List<ExtSysUnitInfo> listAllUnitInfo() {
        return ExternalSystemData.allunitInfo;
    }

    @Override
    public ExtSysUnitInfo getUnitInfoByCode(String unitCode) {
        for(ExtSysUnitInfo unitInfo : ExternalSystemData.allunitInfo){
            if(unitInfo.getUnitCode().equals(unitCode))
                return unitInfo;
        }
        return null;
    }

    @Override
    public List<ExtSysUserUnit> listAllUserUnits() {
        return ExternalSystemData.allUserUnits;
    }

    @Override
    public List<ExtSysUserUnit> listUnitUsers(String unitCode) {
        ExtSysUnitInfo unitInfo = getUnitInfoByCode(unitCode);
        return unitInfo==null?null:unitInfo.getUnitUsers();
    }

    @Override
    public ExtSysUserInfo getUserInfoByCode(String userCode) {
        for(ExtSysUserInfo userInfo : ExternalSystemData.allUserInfo){
            if(userInfo.getUserCode().equals(userCode))
                return userInfo;
        }
        return null;
    }

    /**
     * 从数据字典中获取 Rank 的等级
     *
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        Integer rank = ExternalSystemData.rankMap.get(rankCode);
        return rank == null ? CodeRepositoryUtil.MAXXZRANK : rank;
    }

    /**
     * 读取配置文件中的 JDBC 链接 和 sql语句
     */
    public void loadExternalSystemData(){
        //ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_USERINFO");
    }
}
