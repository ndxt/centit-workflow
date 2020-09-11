package com.centit.workflow.context;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.impl.AbstractUserUnitFilterCalcContext;
import com.centit.framework.model.basedata.IUserRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if(StringUtils.isBlank(unitCode)){
            unitCode = "null";
        }
        return this.extFrameworkBean.subUnitMapCache.getCachedValue(unitCode);
    }

    /*@Override
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
    }*/

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
        return this.extFrameworkBean.userUnitMapCache.getCachedValue(userCode);
    }

    @Override
    public List<? extends IUserRole> listUserRoles(String userCode) {
        return this.extFrameworkBean.userRoleMapCache.getCachedValue(userCode);
    }

    @Override
    public List<? extends IUserRole> listRoleUsers(String roleCode) {
        return this.extFrameworkBean.roleUserMapCache.getCachedValue(roleCode);
    }

    @Override
    public Map<String, String> listAllSystemRole() {
        return this.extFrameworkBean.systemRoleMapCache.getCachedTarget();
    }

    @Override
    public Map<String, String> listAllStation() {
        return this.extFrameworkBean.stationMapCache.getCachedTarget();
    }

    @Override
    public Map<String, String> listAllProjectRole() {
        return this.extFrameworkBean.projectRoleMapCache.getCachedTarget();
    }

    @Override
    public Map<String, String> listAllRank() {
        List<Triple<String, String, Integer>> ranks = this.extFrameworkBean.rankInfoCache.getCachedTarget();
        if(ranks==null){
            return null;
        }
        Map<String, String> rankMap = new HashMap<>();
        for(Triple<String, String, Integer> tri: ranks){
            rankMap.put(tri.getLeft(), tri.getMiddle());
        }
        return rankMap;
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
        List<Triple<String, String, Integer>> ranks =
            this.extFrameworkBean.rankInfoCache.getCachedTarget();//.get(rankCode);
        if(ranks==null){
            return CodeRepositoryUtil.MAXXZRANK;
        }
        for(Triple<String, String, Integer> tri: ranks){
            if(StringUtils.equals(tri.getLeft(),rankCode)){
                return tri.getRight();
            }
        }
        return CodeRepositoryUtil.MAXXZRANK;
    }

}
