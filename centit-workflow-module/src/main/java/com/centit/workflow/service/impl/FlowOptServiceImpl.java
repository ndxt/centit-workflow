package com.centit.workflow.service.impl;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.dao.OptTeamRoleDao;
import com.centit.workflow.dao.OptVariableDefineDao;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;
import com.centit.workflow.service.FlowOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 2018年9月12日10:47:39
 */
@Service
public class FlowOptServiceImpl implements FlowOptService {

    @Autowired
    private OptTeamRoleDao optTeamRoleDao;

    @Autowired
    private OptVariableDefineDao optVariableDefineDao;

    @Override
    public String getOptDefSequenceId() {
        return null;
    }

    @Override
    public List<OptTeamRole> listOptTeamRolesByFilter(Map<String, Object> filter, PageDesc pageDesc) {
        return optTeamRoleDao.listObjectsByProperties(filter, pageDesc);
    }

    @Override
    public OptTeamRole getOptTeamRoleById(String roleId) {
        return optTeamRoleDao.getObjectById(roleId);
    }

    @Override
    @Transactional
    public void saveOptTeamRole(OptTeamRole optTeamRole) {
        optTeamRoleDao.saveNewObject(optTeamRole);
    }

    @Override
    public int[] batchUpdateTeamByOptId(String optId, List<String> optTeamRoleIds) {
        String sql = "UPDATE wf_opt_team_role SET opt_id=?  WHERE opt_team_role_id = ? ";
        return optTeamRoleDao.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, optId);
                ps.setString(2, optTeamRoleIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return optTeamRoleIds.size();
            }
        });
    }

    @Override
    public int[] batchUpdateVariableByOptId(String optId, List<String> optVariableIds) {
        String sql = "UPDATE wf_opt_variable_define SET opt_id=?  WHERE opt_variable_id = ? ";
        return optTeamRoleDao.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, optId);
                ps.setString(2, optVariableIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return optVariableIds.size();
            }
        });
    }

    @Override
    @Transactional
    public void updateOptTeamRole(OptTeamRole optTeamRole) {
        optTeamRoleDao.updateObject(optTeamRole);
    }

    @Override
    @Transactional
    public void deleteOptTeamRoleById(String roleId) {
        optTeamRoleDao.deleteObjectById(roleId);
    }

    @Override
    public List<OptVariableDefine> listOptVariableDefinesByFilter(Map<String, Object> filter, PageDesc pageDesc) {
        return optVariableDefineDao.listObjectsByProperties(filter, pageDesc);
    }

    @Override
    public OptVariableDefine getOptVariableDefineById(String variableId) {
        return optVariableDefineDao.getObjectById(variableId);
    }

    @Override
    @Transactional
    public void saveOptVariableDefine(OptVariableDefine optVariableDefine) {
        optVariableDefineDao.saveNewObject(optVariableDefine);
    }

    @Override
    @Transactional
    public void updateOptVariableDefine(OptVariableDefine optVariableDefine) {
        optVariableDefineDao.updateObject(optVariableDefine);
    }

    @Override
    @Transactional
    public void deleteOptVariableDefineById(String variableId) {
        optVariableDefineDao.deleteObjectById(variableId);
    }

}
