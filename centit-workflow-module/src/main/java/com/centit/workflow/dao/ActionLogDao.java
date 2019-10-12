package com.centit.workflow.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.support.database.utils.PageDesc;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.ActionLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * 流程日志操作类
 *
 * @author ljy, codefan
 * @version $Rev$ <br>
 *          $Id$
 */
@Repository
public class ActionLogDao extends BaseDaoImpl<ActionLog,Long> {
    public Map<String, String> getFilterField() {
        if( filterField == null){
            filterField = new HashMap<String, String>();
            filterField.put("actionId" , CodeBook.EQUAL_HQL_ID);
            filterField.put("nodeInstId" , CodeBook.LIKE_HQL_ID);
            filterField.put("actionType" , CodeBook.LIKE_HQL_ID);
            filterField.put("actionTime" , CodeBook.LIKE_HQL_ID);
            filterField.put("userCode" , CodeBook.LIKE_HQL_ID);
            filterField.put("roleType" , CodeBook.LIKE_HQL_ID);
            filterField.put("roleCode" , CodeBook.LIKE_HQL_ID);

        }
        return filterField;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionLog> listUserActionLogs(String userCode, PageDesc pageDesc, Date lastTime )
    {
        List<ActionLog> list = new ArrayList<>();
        if(lastTime==null){
            list = this.listObjectsByFilter("where USER_CODE = ? order by action_Time desc",
                    new Object[]{userCode});
        }else{
            list = this.listObjectsByFilter("where USER_CODE = ? and ACTION_TIME >= ?" +
                    "order by action_Time desc",new Object[]{userCode,lastTime});
        }
        return list;
    }

    /**
     * 查询受委托的工作记录
     * @param userCode
     * @param pageDesc
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionLog> listGrantedActionLog(String userCode, PageDesc pageDesc){
        return this.listObjectsByFilter("where USER_CODE = ? and  grantor <> null",
                new Object[]{userCode});
    }

    /**
     * 查询委托别人做的工作记录
     * @param userCode
     * @param pageDesc
     * @return
     */
    @Transactional(propagation= Propagation.MANDATORY)
    public List<ActionLog> listGrantorActionLog(String userCode,
                                                PageDesc pageDesc) {
        return this.listObjectsByFilter("where GRANTOR = ?",new Object[]{userCode});
    }

}
