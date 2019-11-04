package com.centit.workflow.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.NodeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程定义接口
 */
public interface FlowDefine {

    /**
     * 保存流程定义，内容为JS画的流程描述XML文件
     */
    boolean saveDraftFlowDefXML(String  flowCode,String flowDefXML);

    /**
     * 获取保存的流程定义文件,就是0版本的草稿
     */
    String getDraftFlowDefXML(String  flowCode);

    /**
     * 发布流程，返回当前流程版本号
     */
    long publishFlowDef(String  flowCode) throws Exception;

    /**
     * 获取指定版本流程定义描述文件
     */
    String getFlowDefXML(String  flowCode,long version );

    /**
     * 获取最新版本的流程定义描述文件
     */
    String getFlowDefXML(String  flowCode);

    /**
     * 禁用某个流程
     */
    void disableFlow(String  flowCode);

    /**
     * 启用某个流程
     */
    void enableFlow(String  flowCode);

    /**
     * 获取指定版本流程定义对象
     */
    FlowInfo getFlowDefObject(String flowCode, long version );

    /**
     * 获取流程定义最新版本对象
     */
    FlowInfo getFlowDefObject(String flowCode);

    /**
     * 保存流程定义对象,(版本只能为0),并且只保存基本信息
     */
    boolean saveDraftFlowDef(FlowInfo wfDef);

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程阶段
     */
    boolean saveDraftFlowStage(FlowInfo wfDef);

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程角色
     */
    boolean saveDraftFlowRole(FlowInfo wfDef);

    /**
     * 保存流程定义对象,(版本只能为0),并且只流程变量
     */
    boolean saveDraftFlowVariableDef(FlowInfo wfDef);

    /**
     * 根据节点ID获得节点定义
     * @param nodeId
     * @return
     */
    NodeInfo getNodeInfoById(String nodeId);

    /**
     * 获取全部最新版本流程
     */
    List<FlowInfo> listLastVersionFlow(Map<String, Object> filterMap,
                PageDesc pageDesc);

    /**
     * 获取某个流程全部版本
     */
    List<FlowInfo> getFlowsByCode(String wfCode,PageDesc pageDesc);

    /**
     * 根据已知的流程业务，查询对应的定义流程
     * @param optId 业务id
     * @return 流程列表
     */
    List<FlowInfo> getFlowsByOptId(String optId);

    /**
     * 获取某一流程某一版本号中存在的所有机构表达式
     * @param flowCode 流程代码
     * @param version 版本
     * @return 机构表达式列表
     */
    Set<String> getUnitExp(String flowCode, Long version);



    /**
     * 根据流程code删除相关的流程定义，用于删除多余的测试数据
     * @param flowCode 流程代码
     */
    void deleteFlowDef(String flowCode);

    /**
     * 例举所有节点操作类别
     */
    Map<String,String> listAllOptType();
    /**
     * 例举所有节点类别
     */
    Map<String,String> listAllNoteType();

    /**
     * 根据业务类型列举所有业务代码
     */
    Map<String, String> listAllOptCode(String wfcode);

    /**
     * 列举所有角色类别
     */
    Map<String, String> listRoleType();

    /**
     * 列举所有角色
     */
    Map<String, Map<String, String>> listAllRole();

    /**
     * @param stype 角色类别
     * @return 角色名称和类别对应列表
     */
    Map<String, String> listRoleByType(String stype);

    /**
     * @return 内置的流程结构表达式
     */
    Map<String, String> listInsideUnitExp();
    /**
     * 列举所有的子流程
     */
    Map<String, String> listAllSubFlow();

    /**
     * 获取某一流程某一版本号中存在的所有办件角色
     * @param flowCode 流程代码
     * @param version 版本 0 草稿 -1 最新版本
     * @return 流程的办件角色列表
     */
    Map<String,String> listFlowItemRoles(String flowCode, Long version);
    /**
     * 获取流程阶段信息
     * @param flowCode 流程代码和名称对应表
     * @param version 版本 0 草稿 -1 最新版本
     * @return 流程阶段
     */
    Map<String, String> listFlowStages(String flowCode, Long version);

    /**
     * 根据流程代码获取流程变量信息
     * @param flowCode 流程代码
     * @param version 版本 0 草稿 -1 最新版本
     * @return 流程变量信息
     */
    Map<String, String> listFlowVariableDefines(String flowCode, Long version);

    /**
     * 根据流程代码获取流程变量的默认值
     * @param flowCode 流程代码
     * @param version 版本 0 草稿 -1 最新版本
     * @return 流程变量信息
     */
    Map<String, String> listFlowDefaultVariables(String flowCode, Long version);
}
