package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.OptTeamRole;
import com.centit.workflow.po.OptVariableDefine;

import java.util.List;
import java.util.Map;

public interface FlowOptService {


    String getOptDefSequenceId();

    /**
     * 根据过滤条件查询相关角色定义列表
     * @param filter 过滤条件
     * @param pageDesc 分页参数
     * @return 角色定义列表
     */
    List<OptTeamRole> listOptTeamRolesByFilter(Map<String, Object> filter, PageDesc pageDesc);

    /**
     * 根据主键id获取角色定义
     * @param roleId 主键id
     * @return
     */
    OptTeamRole getOptTeamRoleById(String roleId);

    /**
     * 新增角色定义
     * @param optTeamRole 角色定义
     */
    void saveOptTeamRole(OptTeamRole optTeamRole);
    /**
     * 批量改变角色菜单
     * @param optId 菜单id
     * @param optTeamRoleIds 角色id集合
     */
    int[] batchUpdateTeamByOptId(String optId, List<String> optTeamRoleIds);
    /**
     * 批量改变变量菜单
     * @param optId 菜单id
     * @param optVariableIds 变量id集合
     */
    int[] batchUpdateVariableByOptId(String optId, List<String> optVariableIds);
    /**
     * 更新角色定义
     * @param optTeamRole 角色定义
     */
    void updateOptTeamRole(OptTeamRole optTeamRole);

    /**
     * 根据角色定义id删除角色定义
     * @param roleId 角色定义id
     */
    void deleteOptTeamRoleById(String roleId);

    /**
     * 根据过滤条件查询相关变量定义列表
     * @param filter 过滤条件
     * @param pageDesc 分页参数
     * @return 变量定义列表
     */
    List<OptVariableDefine> listOptVariableDefinesByFilter(Map<String, Object> filter, PageDesc pageDesc);

    /**
     * 根据主键id获取变量定义
     * @param variableId 主键id
     * @return
     */
    OptVariableDefine getOptVariableDefineById(String variableId);

    /**
     * 新增变量定义
     * @param optVariableDefine 变量定义
     */
    void saveOptVariableDefine(OptVariableDefine optVariableDefine);

    /**
     * 更新变量定义
     * @param optVariableDefine 变量定义
     */
    void updateOptVariableDefine(OptVariableDefine optVariableDefine);

    /**
     * 根据变量定义id删除变量定义
     * @param variableId 变量定义id
     */
    void deleteOptVariableDefineById(String variableId);

}
