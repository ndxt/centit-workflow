package com.centit.workflow.external;

import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.DbcpConnectPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by codefan on 17-9-12.
 */
public abstract class ExternalSystemData {

    protected static final Logger logger = LoggerFactory.getLogger(ExternalSystemData.class);

    public static List<ExtSysUserInfo> allUserInfo = new ArrayList<>();
    public static List<ExtSysUnitInfo> allunitInfo = new ArrayList<>();
    public static List<ExtSysUserUnit> allUserUnits = new ArrayList<>();
    public static Map<String, Integer > rankMap = new HashMap<>();
    private static Date lastLoadDataTime = null;

    public static ExtSysUnitInfo getUnitInfoByCode(String unitCode) {
        for(ExtSysUnitInfo unitInfo : ExternalSystemData.allunitInfo){
            if(unitInfo.getUnitCode().equals(unitCode))
                return unitInfo;
        }
        return null;
    }

    public static ExtSysUserInfo getUserInfoByCode(String userCode) {
        for(ExtSysUserInfo userInfo : ExternalSystemData.allUserInfo){
            if(userInfo.getUserCode().equals(userCode))
                return userInfo;
        }
        return null;
    }
    /**
     *#工作流引擎中引用外部业务系统用户表
     wf.external.system.jdbc.driver = com.mysql.jdbc.Driver
     wf.external.system.jdbc.user =workflow
     wf.external.system.jdbc.password =workflow
     wf.external.system.jdbc.url=jdbc:mysql://192.168.128.32:3306/workflow?characterEncoding=UTF-8
     */
    public static void loadExternalSystemData(){
        //每小时更新一次
        if(lastLoadDataTime!=null &&
                DatetimeOpt.currentUtilDate().before(DatetimeOpt.addHours(lastLoadDataTime,1 ) ))
            return ;
        lastLoadDataTime = DatetimeOpt.currentUtilDate();

        DataSourceDescription dataSourceDesc = new DataSourceDescription();
        dataSourceDesc.setConnUrl(SysParametersUtils.getStringValue("wf.external.system.jdbc.url"));
        dataSourceDesc.setUsername(SysParametersUtils.getStringValue("wf.external.system.jdbc.user"));
        dataSourceDesc.setPassword(SysParametersUtils.getStringValue("wf.external.system.jdbc.password"));

        try(Connection conn = DbcpConnectPools.getDbcpConnect(dataSourceDesc)){
            List<Object[]>  users = DatabaseAccess.findObjectsBySql(conn,
                    ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_USERINFO") );
            if(users == null)
                return;
            allUserInfo.clear();
            for(Object[] user : users){
                ExtSysUserInfo userInfo = new ExtSysUserInfo();
                userInfo.setUserCode(StringBaseOpt.objectToString(user[0]));
                userInfo.setUserName(StringBaseOpt.objectToString(user[1]));
                userInfo.setPrimaryUnit(StringBaseOpt.objectToString(user[2]));
                userInfo.setUserOrder(NumberBaseOpt.castObjectToLong(user[3]));

                allUserInfo.add(userInfo);
            }
            List<Object[]> units =DatabaseAccess.findObjectsBySql(conn,
                    ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_UNITINFO") );

            if(units == null)
                return;
            allunitInfo.clear();
            for(Object[] unit : units){
                ExtSysUnitInfo unitInfo = new ExtSysUnitInfo();
                unitInfo.setUnitCode(StringBaseOpt.objectToString(unit[0]));
                unitInfo.setParentUnit(StringBaseOpt.objectToString(unit[1]));
                unitInfo.setUnitName(StringBaseOpt.objectToString(unit[2]));
                unitInfo.setUnitManager(StringBaseOpt.objectToString(unit[3]));
                unitInfo.setUnitOrder(NumberBaseOpt.castObjectToLong(unit[4]));
                unitInfo.setUnitPath(StringBaseOpt.objectToString(unit[5]));

                allunitInfo.add(unitInfo);
            }

            for (ExtSysUnitInfo ui : allunitInfo) {
                ExtSysUnitInfo unit = getUnitInfoByCode(ui.getParentUnit());
                if (unit != null)
                    unit.addSubUnit(ui);
            }

            List<Object[]> userUnits = DatabaseAccess.findObjectsBySql(conn,
                    ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_USERUNIT") );

            if(userUnits == null)
                return;
            allUserUnits.clear();
            for(Object[] uu: userUnits){
                ExtSysUserUnit userUnit = new ExtSysUserUnit();
                userUnit.setUnitCode(StringBaseOpt.objectToString(uu[0]));
                userUnit.setUserCode(StringBaseOpt.objectToString(uu[1]));
                userUnit.setUserStation(StringBaseOpt.objectToString(uu[2]));
                userUnit.setUserRank(StringBaseOpt.objectToString(uu[3]));
                userUnit.setIsPrimary(StringBaseOpt.objectToString(uu[4]));
                userUnit.setUserOrder(NumberBaseOpt.castObjectToLong(uu[5]));

                allUserUnits.add(userUnit);

                ExtSysUserInfo userInfo = getUserInfoByCode(userUnit.getUserCode());
                if(userInfo!=null){
                    userInfo.addUserUnit(userUnit);
                }

                ExtSysUnitInfo unitInfo = getUnitInfoByCode(userUnit.getUnitCode());
                if(unitInfo!=null){
                    unitInfo.addUnitUser(userUnit);
                }

            }

            List<Object[]> ranks = DatabaseAccess.findObjectsBySql(conn,
                    ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_RANKMAP") );

            if(ranks == null)
                return;
            rankMap.clear();
            for(Object[] rank: ranks){
                rankMap.put(StringBaseOpt.objectToString(rank[0]),
                        NumberBaseOpt.castObjectToInteger(rank[1]) );
            }

        } catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
