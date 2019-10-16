package com.centit.workflow.client.service;


import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowInstanceGroup;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.NodeInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程运行接口
 */
public interface FlowEngineClient {


    //--------------------创建流程实例接口-----------------------------------
    /**
     * 创建流程实例  返回流程实例
     * @param options CreateFlowOptions 流程创建选项编码
     * @return 流程实例
     */
    FlowInstance createInstance(CreateFlowOptions options) ;


   //--------------------提交流程业务节点-----------------------------------
    /**
     * 返回下一步节点的节点实例ID
     * @param options SubmitOptOptions 提交流程操作选项编码
     * @return  节点实例编号列表
     */
    Map<String, Object> submitOpt(SubmitOptOptions options) ;

    /**
     * 根据业务id获取所有该业务下的流程 一般应该只有一个
     * @param optTag 这个是业务的主键,如果是多个字段生成 a=b&c=d 的浏览器参数形式,并且注意字段顺序
     * @return 流程实例列表
     */
    List<FlowInstance> listAllFlowInstByOptTag(String optTag);

    /**
     * 修改流程的 暂时信息
     * @param flowInstId 流程实例Id
     * @param flowOptName 业务摘要
     * @param flowOptTag 业务主键信息  多个字段生成 a=b&c=d 的浏览器参数形式,并且注意字段顺序
     */
    void updateFlowInstOptInfo(String flowInstId, String flowOptName,String flowOptTag);
    /**
     * 分配办件角色，兼容老业务模块，支持新增多个办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     * 异常
     */
    void assignFlowWorkTeam(String flowInstId, String roleCode,
                            List<String> userCodeSet) ;

    /**
     * 新增单个办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param userCode 用户代码 添加
     * 异常
     */
    void addFlowWorkTeam(String flowInstId, String roleCode, String userCode);

    /**
     * 分配流程机构，兼容老业务模块，支持新增多个流程机构
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param orgCodeSet 机构代码 列表
     * 异常
     */
    void assignFlowOrganize(String flowInstId, String roleCode,
                             List<String> orgCodeSet);

    /**
     * 分配单个流程机构
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param unitCode 机构代码
     */
    void addFlowOrganize(String flowInstId, String roleCode,
                                String unitCode);


    /**
     * 删除工作小组--办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     */
    void deleteFlowWorkTeam(String flowInstId, String roleCode);
    /**
     * 查看工作小组中某个角色的成员
     * @param flowInstId 工作流实例号
     * @param roleCode 办件角色代码
     * @return userCode 列表
     */
    List<String> viewFlowWorkTeam(String flowInstId, String roleCode);
    /**
     * 删除流程机构
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 机构角色 不能为空
     */
    void deleteFlowOrganize(String flowInstId, String roleCode);

    /**
     * 查看工作小组中某个角色的成员
     * @param flowInstId 工作流实例号
     * @param roleCode 结构类别代码
     * @return 机构列表
     */
    List<String> viewFlowOrganize(String flowInstId, String roleCode);

    /**
     * 设置流程全局变量
     * @param flowInstId 工作流实例号
     * @param varName 变量名称
     * @param sValue 变量值
     */
    void saveFlowVariable(String flowInstId, String varName, String sValue);

    /**
     * 设置流程全局变量
     * @param flowInstId 工作流实例号
     * @param varName 变量名称
     * @param varValue 变量值
     */
    void saveFlowVariable(String flowInstId, String varName, Set<String> varValue);

    /**
     * 设置流程节点变量(局部变量)
     * @param nodeInstId 流程节点实例号
     * @param varName 变量名称
     * @param sValue 变量值
     */
    void saveFlowNodeVariable(String nodeInstId, String varName, String sValue);
    /**
     * 设置流程节点变量(局部变量)
     * @param nodeInstId 流程节点实例号
     * @param varName 变量名称
     * @param varValue 变量值
     */
    void saveFlowNodeVariable(String nodeInstId, String varName, Set<String> varValue);
    /**
     * 查询某个流程变量
     * @param flowInstId 流程节点实例号
     * @param varName 变量名称
     * @return 变量列表
     */
    List<FlowVariable> viewFlowVariablesByVarname(String flowInstId, String varName);


    /**
     * 删除流程变量
     * @param flowInstId
     * @param runToken 默认为A
     * @param varName
     *throws Exception
     */
    void deleteFlowVariable(String flowInstId,String runToken,String varName);

    /**
     * 检查后续的节点是否被操作过，包括更新和提交
     * 只有后续节点没有处理的才可以收回。true表示可以撤回，false表示不可以撤回，
     * @param nodeInstId 流程实例id
     * @return 是否可以回收
     */
    boolean nodeCanBeReclaim(String nodeInstId);

    /**
     * 回退节点
     * @param nodeInstId 节点实例
     * @param managerUserCode 回退操作人员
     */
    void rollBackNode(String nodeInstId,String managerUserCode);

    /**
     * 创建孤立节点  知会、关注
     * <p>
     * 用户手动创建一个节点实例，不影响当前节点实例的执行,当前节点实例Id也可以为空
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode       节点 的环节代码
     * @param userCode      指定用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    NodeInstance createIsolatedNodeInst(String flowInstId, String curNodeInstId,
                                               String nodeCode, String createUser,
                                         String userCode, String unitCode);

    /**
     * 加签,并指定到人
     *
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     * @param flowInstId 流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode  节点环节代码，这个节点在这个流程中必需唯一
     * @param createUser  当前创建用户
     * @param userCode  指定操作用户
     * @param unitCode 指定机构
     * @return 节点实例
     */
    NodeInstance createPrepNodeInst(String flowInstId,String curNodeInstId,
                                    String nodeCode, String createUser,
                                    String userCode,String unitCode);
    /**
     * 创建 流程分组
     * @param name 分组名称
     * @param desc 分组描述
     * @return 流程分组
     */
    FlowInstanceGroup createFlowInstGroup(String name, String desc);

    /**
     * 查询流程组信息
     * @param paramMap 查询参数
     * @param pageDesc 分页信息
     * @return 流程分组
     */
    JSONArray/*List<FlowInstanceGroup>*/ listFlowInstGroup(Map<String, Object> paramMap, PageDesc pageDesc);

    /**
     * 根据条件查询待办，包括flowInstId，flowOptTag
     * @param paramMap 查询参数
     * @param pageDesc 分页信息
     * @return 获取待办列表 这里指静态代办
     */
    JSONArray/*List<UserTask>*/ listTasks(Map<String, Object> paramMap, PageDesc pageDesc);

   /* *//**
     * 查看某一个用户所有的待办，并且分页
     * @param userCode 用户代码
     * @param pageDesc 分页信息
     * @return 任务列表
     *//*
    JSONArray*//*List<UserTask>*//* listUserTasks(String userCode, PageDesc pageDesc);*/

    /**
     * 查看某一个节点所有的可以办理的用户
     * @param nodeInstId 节点实例Id
     * @return 用户办件信息
     */
    JSONArray/*List<UserTask>*/ listNodeTaskUsers(String nodeInstId);

    /**
     * 获取动态待办
     * @param searchColumn 包含nodeInstId，unitCode，userStation
     * @param pageDesc 分页信息
     * @return 获取待办列表 这里指动态代办
     * @return
     */
    JSONArray/*List<UserTask>*/ listDynamicTask(Map<String, Object> searchColumn,PageDesc pageDesc);

}
