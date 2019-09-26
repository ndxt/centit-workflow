package com.centit.workflow.external;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.impl.AbstractUserUnitFilterCalcContext;
import com.centit.support.algorithm.CollectionsOpt;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by codefan on 17-9-11.
 * 在这个过滤器中 用户信息只需要用户代码、用户默认机构
 * 机构信息中只需要机构代码、
 * 机构人员信息中需要 用户代码、机构代码、 用户位和职务
 * Rank 获取用户职务等级信息，有一个职务类表
 */
public class JdbcUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext {

    private ExtFrameworkContextCacheBean extFrameworkBean;

    public JdbcUserUnitFilterCalcContext(ExtFrameworkContextCacheBean extFrameworkBean){
        this.extFrameworkBean = extFrameworkBean;
    }


    @Override
    public List<ExtSysUserInfo> listAllUserInfo() {
        return this.extFrameworkBean.allUserInfoCache.getCachedTarget();
    }

    @Override
    public List<ExtSysUnitInfo> listAllUnitInfo() {
        return this.extFrameworkBean.allunitInfoCache.getCachedTarget();
    }

    @Override
    public List<ExtSysUnitInfo> listSubUnit(String unitCode) {
        return this.extFrameworkBean.subUnitMapCache.getCachedValue(unitCode);
    }

    @Override
    public List<ExtSysUnitInfo> listSubUnitAll(String unitCode) {

        if(StringUtils.isBlank(unitCode))
            return null;

        List<ExtSysUnitInfo> units = new ArrayList<>(50);
        List<ExtSysUnitInfo> subunits = listSubUnit(unitCode);
        while( subunits!=null && subunits.size()>0){
            units.addAll(subunits);
            List<ExtSysUnitInfo> subunits1 = new ArrayList<>();
            for(ExtSysUnitInfo u1: subunits){
                List<ExtSysUnitInfo> subunits2 = listSubUnit(u1.getUnitCode());
                if(subunits2!=null)
                    subunits1.addAll(subunits2);
            }
            subunits = subunits1;
        }
        CollectionsOpt.sortAsTree(units, (p, c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));
        return units;
    }

    @Override
    public ExtSysUnitInfo getUnitInfoByCode(String unitCode) {
        return this.extFrameworkBean.codeToUnitMapCache.getCachedTarget().get(unitCode);
    }

    @Override
    public List<ExtSysUserUnit> listAllUserUnits() {
        return this.extFrameworkBean.allUserUnitCache.getCachedTarget();
    }

    @Override
    public List<ExtSysUserUnit> listUnitUsers(String unitCode) {
        return this.extFrameworkBean.unitUserMapCache.getCachedValue(unitCode);
    }

    @Override
    public List<ExtSysUserUnit> listUserUnits(String userCode) {
        return this.extFrameworkBean.unitUserMapCache.getCachedValue(userCode);
    }

    @Override
    public ExtSysUserInfo getUserInfoByCode(String userCode) {
        return this.extFrameworkBean.codeToUserMapCache.getCachedTarget().get(userCode);
    }

    /**
     * 从数据字典中获取 Rank 的等级
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        Integer rank = this.extFrameworkBean.rankMapCache.getCachedTarget().get(rankCode);
        return rank == null ? CodeRepositoryUtil.MAXXZRANK : rank;
    }

}
