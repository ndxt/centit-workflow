package com.centit.workflow.service;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.common.ResponseData;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.commons.CreateFlowOptions;
import com.centit.workflow.commons.SubmitOptOptions;
import com.centit.workflow.po.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程运行接口
 */
public interface FlowEngine {

    /**
     * 创建流程实例或子流程实例
     *
     * @param options  NewFlowInstanceOptions 流程创建选项编码
     * @param varTrans UserUnitVariableTranslate 机构执行环境
     * @return FlowInstance
     */
    FlowInstance createInstance(CreateFlowOptions options,
                                UserUnitVariableTranslate varTrans);
    //--------------------创建流程实例接口-----------------------------------

    /**
     * 创建流程实例  返回流程实例
     *
     * @param options NewFlowInstanceOptions 流程创建选项编码
     * @return 流程实例
     */
    FlowInstance createInstance(CreateFlowOptions options);


    //--------------------提交流程业务节点-----------------------------------

    /**
     * 返回下一步节点的节点实例ID
     *
     * @param options  SubmitOptOptions 提交流程操作选项编码
     * @param varTrans UserUnitVariableTranslate 机构执行环境
     * @param varTrans 变量转换器
     * @return 节点实例编号列表
     */
    List<String> submitOpt(SubmitOptOptions options,
                           UserUnitVariableTranslate varTrans);

    /**
     * 返回下一步节点的节点实例ID
     *
     * @param options 当前节点实例编号
     * @return 节点实例编号列表
     */
    List<String> submitOpt(SubmitOptOptions options);

    /**
     * 返回下一步节点的节点实例信息
     *
     * @param options 当前节点实例编号
     * @return 节点实例信息
     */
    Map<String, Object> submitFlowOpt(SubmitOptOptions options);

    //--------------------查看流转信息-----------------------------------

    /**
     * 预判下一步节点的节点编号
     *
     * @param options
     * @return 节点信息列表
     */
    Set<NodeInfo> viewNextNode(SubmitOptOptions options);

    /**
     * 查看下一节点可以操作的人员类表
     *
     * @param nextNodeId 下一个节点编号
     * @param options
     * @return 用户代码
     */
    Set<String> viewNextNodeOperator(String nextNodeId, SubmitOptOptions options);


    //-------------------------用户待办接口---------------------------------------
    //任务类表查看

    /**
     * 获取节点的所有操作人员
     *
     * @param nodeInstId 节点实例id
     * @return 操作人员
     */
    List<UserTask> listNodeOperators(String nodeInstId);


    /**
     * 获取流程所有活动节点的任务列表
     *
     * @param flowInstId 流程实例id
     * @return 操作人员
     */
    List<UserTask> listFlowActiveNodeOperators(String flowInstId);

    /**
     * 获取动态待办
     *
     * @param searchColumn 查询参数，必须包括用户编号userCode
     * @param pageDesc     分页信息
     * @return 获取待办列表 这里指动态代办
     */
    List<UserTask> listUserDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUserDynamicTask(Map<String, Object> searchColumn, PageDesc pageDesc);


    /**
     * 根据条件查询用户静态待办，包括flowInstId，flowOptTag
     *
     * @param searchColumn 查询参数，必须包括用户编号userCode
     * @param pageDesc     分页信息
     * @return 获取待办列表 这里指静态待办
     */
    List<UserTask> listUserStaticTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUserStaticTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUnitTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 根据条件查询用户被授权的代码
     *
     * @param searchColumn 查询参数，必须包括用户编号userCode
     * @param pageDesc     分页信息
     * @return 获取待办列表 这里指静态代办
     */
    List<UserTask> listUserGrantorTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUserGrantorTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 根据条件查询静态待办和被授权的待办
     *
     * @param searchColumn 查询参数，必须包括用户编号userCode
     * @param pageDesc     分页信息
     * @return 获取待办列表 这里指静态待办
     */
    List<UserTask> listUserStaticAndGrantorTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUserStaticAndGrantorTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 查看某一个用户所有的待办，并且分页
     *
     * @param searchColumn 查询参数，必须包括用户编号userCode
     * @param pageDesc     分页信息
     * @return 用户任务列表
     */
    List<UserTask> listUserAllTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboUserAllTask(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 查看某一个用户所有的已办，并且分页
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 用户任务列表
     */
    List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc);

    //-------------------------权限验证---------------------------------------

    /**
     * 判断一个用户是否可以处理指定的节点,可以喝submitOpt结合使用，
     * 判断当前操作人员是否可以访问提交后的下一个节点。
     *
     * @param nodeInstId 节点实例代码
     * @param userCode   用户代码
     * @return 是否有权限
     */
    boolean canAccess(String nodeInstId, String userCode);


    /**
     * 获取任务授权人，如果是用户自己的任务，返回自己，否则返回授权人
     *
     * @param nodeInstId 节点实例id
     * @param userCode   用户代码
     * @return 授权人
     */
    String getTaskGrantor(String nodeInstId, String userCode);

    //-------------------------预报警列表查看---------------------------------------

    /**
     * 自定义预警查询
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 预警列表
     */
    List<FlowWarning> listFlowWarning(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 查询某个流程的预警
     *
     * @param flowInstId 流程实例代码
     * @param pageDesc   分页信息
     * @return 预警列表
     */
    List<FlowWarning> listFlowWarningByInst(String flowInstId, PageDesc pageDesc);

    /**
     * 查询某个节点的预警
     *
     * @param nodeInstId 节点实例代码
     * @param pageDesc   分页信息
     * @return 预警列表
     */
    List<FlowWarning> listFlowWarningByNodeInst(String nodeInstId, PageDesc pageDesc);

    /**
     * 查询某一个类别的预警
     *
     * @param warningCode 预警类别
     * @param pageDesc    分页信息
     * @return 预警列表
     */
    List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc);


    //--------------------特殊操作和记录日志接口-----------------------------------

    /**
     * 根据业务id获取所有该业务下的流程
     *
     * @param optTag 业务标签
     * @return 流程实例列表
     */
    List<FlowInstance> listAllFlowInstByOptTag(String optTag);
    //--------------------更改流程业务相关数据接口-----------------------------------

    /**
     * 更改流程业务信息，flowOptName 用来显示业务办件名称，flowOptTag 给业务系统自己解释可以用于反向关联
     *
     * @param flowInstId  流程实例ID
     * @param flowOptName 这个名称用户 查找流程信息
     * @param flowOptTag  流程业务标签
     */
    void updateFlowInstOptInfo(String flowInstId, String flowOptName, String flowOptTag);

    /**
     * 设置节点实例参数
     *
     * @param nodeInstId 节点实例id
     * @param nodeParam  节点实例参数
     */
    void updateNodeInstanceParam(String nodeInstId, String nodeParam);

    /**
     * 针对 抢先类别的 节点， 锁定任务，这个任务后续只能由 他来做
     *
     * @param nodeInstId 节点实例id
     * @param userCode   用户
     */
    void lockNodeTask(String nodeInstId, String userCode);
    //--------------------人工控制流程流转和任务分配------------------------------------

    /**
     * 回退操作-回退到上一个节点
     */
    String rollBackNode(String nodeInstId, CentitUserDetails managerUser);

    /**
     * 检查后续的节点是否被操作过，包括更新和提交
     * 只有后续节点没有处理的才可以收回。true表示可以撤回，false表示不可以撤回，
     *
     * @param nodeInstId 流程节点实例id
     * @return 是否可以回收
     */
    boolean nodeCanBeReclaim(String nodeInstId);


    /**
     * 撤回（回收）当前节点，将后续的节点终止，重新生成当前节点
     *
     * @param nodeInstId 流程节点实例id
     * @param userCode   操作用户
     * @return 重新生成的新节点
     */
    NodeInstance reclaimNode(String nodeInstId, String userCode);

    /**
     * 加签,并指定到人
     * <p>
     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码，这个节点在这个流程中必需唯一
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    NodeInstance createPrepNodeInst(String flowInstId, String curNodeInstId,
                                    String nodeCode, String createUser,
                                    String userCode, String unitCode);

    /**
     * 创建一个游离节点
     * 知会、关注
     * <p>
     * 用户手动创建一个节点实例，不影响当前节点实例的执行,当前节点实例Id也可以为空
     *
     * @param flowInstId    流程实例号
     * @param curNodeInstId 当前节点实例号
     * @param nodeCode      节点环节代码
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     *                      //* @param unitCode      指定机构
     * @return 节点实例
     */
    NodeInstance createIsolatedNodeInst(String flowInstId, String curNodeInstId,
                                        String nodeCode, String createUser,
                                        String userCode, String unitCode);

    /**
     * 复制一个多实例节点，用于代替以前给一个节点分配多个操作人员
     * <p>
     *
     * @param flowInstId    流程实例号
     * @param multiNodeCode 需要复制的节点必须要指定nodeCode 并且需要在 多实例循环中
     * @param createUser    当前创建用户
     * @param userCode      指定操作用户
     * @param unitCode      指定机构
     * @return 节点实例
     */
    NodeInstance duplicateMultiNodeInst(String flowInstId, String multiNodeCode, String createUser,
                                        String userCode, String unitCode);

    /**
     * 更改流程的父节点，这个函数只是用来手动的将一个流程作为子流程挂到父流程的节点上，一般不会使用。
     *
     * @param flowInstId       子流程实例id
     * @param parentFlowInstId 父流程实例id
     * @param parentNodeInstId 父流程节点实例id
     */
    void updateFlowInstParentNode(String flowInstId, String parentFlowInstId,
                                  String parentNodeInstId);


    /**
     * 关闭本节点分支以外的其他分支的所有节点,特指和本节点平行的分支，就是同一个父类令牌的分支
     *
     * @param nodeInstId  当前活动节点
     * @param optUserCode 操作人员
     */
    void disableOtherBranchNodes(String nodeInstId, String optUserCode);
    //--------------------任务分配和查看相关接口-----------------------------------

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    办件角色 不能为空
     * @param runToken    令牌
     * @param userCodeSet 用户代码列表，添加
     */
    void assignFlowWorkTeam(String flowInstId, String roleCode, String runToken,
                            List<String> userCodeSet);

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     */
    void assignFlowWorkTeam(String flowInstId, String roleCode,
                            List<String> userCodeSet);

    /**
     * 分配角色及变量
     *
     * @param flowInstId  流程实例号 不能为空
     * @param flowVariables  变量
     * @param flowRoleUsers 办件角色
     */
    void updateFlowInstanceTeamAndVar(String flowInstId, String nodeInstId,
                                      Map<String, String> flowVariables,
                                      Map<String, List<String>> flowRoleUsers);

    /**
     * 分配工作小组 --办件角色
     *
     * @param nodeInstId  节点实例号 不能为空
     * @param roleCode    办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     */
    void assignFlowWorkTeamByNode(String nodeInstId, String roleCode,
                                  List<String> userCodeSet);

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   办件角色 不能为空
     * @param userCode   用户代码，添加
     */
    void deleteFlowWorkTeam(String flowInstId, String roleCode, String userCode);

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   办件角色 不能为空
     */
    void deleteFlowWorkTeam(String flowInstId, String roleCode);

    /**
     * 查看流程实例的办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @return Map roleCode,Set userCode
     */
    List<FlowWorkTeam> viewFlowWorkTeam(String flowInstId);

    /**
     * 查看工作小组中某个角色的成员
     *
     * @param flowInstId 工作流实例号
     * @param roleCode   办件角色代码
     * @return Set userCode
     */
    List<FlowWorkTeam> viewFlowWorkTeam(String flowInstId, String roleCode);

    /**
     * 分配流程组织机构
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param unitCode   机构代码，添加
     */
    void assignFlowOrganize(String flowInstId, String roleCode, String unitCode);

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    机构角色 不能为空
     * @param unitCodeSet 机构代码列表，添加
     */
    void assignFlowOrganize(String flowInstId, String roleCode, List<String> unitCodeSet);

    /**
     * 分配流程组织机构
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param unitCode   机构代码，添加
     */
    void assignFlowOrganize(String flowInstId, String roleCode,
                            String unitCode, String authdesc);

    /**
     * 分配工作小组 --办件角色
     *
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode    机构角色 不能为空
     * @param unitCodeSet 机构代码列表，添加
     */
    void assignFlowOrganize(String flowInstId, String roleCode,
                            List<String> unitCodeSet, String authdesc);

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     * @param unitCode   机构代码，添加
     */
    void deleteFlowOrganize(String flowInstId, String roleCode, String unitCode);

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     */
    void deleteFlowOrganize(String flowInstId, String roleCode);

    /**
     * 删除工作小组--办件角色
     *
     * @param flowInstId 流程实例号 不能为空
     * @param roleCode   机构角色 不能为空
     */
    void deleteFlowOrganizeByAuth(String flowInstId, String roleCode,
                                  String authDesc);

    /**
     * 查看工作小组
     *
     * @param flowInstId 流程实例号 不能为空
     * @return Map roleCode,Set unitCode
     */
    Map<String, List<String>> viewFlowOrganize(String flowInstId);

    /**
     * 查看工作小组中某个角色的成员
     *
     * @param flowInstId 工作流实例号
     * @param roleCode   机构角色代码
     * @return Set unitCode
     */
    List<String> viewFlowOrganize(String flowInstId, String roleCode);

    /**
     * @param flowInstId 工作流实例号
     * @param roleCode   机构角色代码
     * @return 流程组织架构
     */
    List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode);

    /**
     * @param flowInstId 工作流实例号
     * @param roleCode   机构角色代码
     * @param authDesc   授权信息
     * @return 流程组织架构
     */
    List<FlowOrganize> viewFlowOrganizeList(String flowInstId, String roleCode,
                                            String authDesc);

    /**
     * 设置流程全局变量
     *
     * @param flowInstId 工作流实例号
     * @param sVar       变量名
     * @param sValue     变量值
     */
    void saveFlowVariable(String flowInstId, String sVar, Object sValue);

    /**
     * 设置流程节点上下文变量
     *
     * @param nodeInstId 节点实例号
     * @param sVar       变量名
     * @param sValue     变量值
     */
    void saveFlowNodeVariable(String nodeInstId, String sVar, Object sValue);


    /**
     * 设置流程节点上下文变量
     *
     * @param flowInstId 工作流实例号
     * @param runToken   令牌值
     * @param sVar       变量名
     * @param sValue     变量值
     */
    void saveFlowNodeVariable(String flowInstId, String runToken, String sVar, Object sValue);

    /**
     * 查询流程变量
     *
     * @param flowInstId 工作流实例号
     * @return 所有流程变量
     */
    List<FlowVariable> listFlowVariables(String flowInstId);

    /**
     * 查询某个流程变量
     *
     * @param flowInstId 工作流实例号
     * @param varname    变量名
     * @return 流程变量
     */
    List<FlowVariable> viewFlowVariablesByVarName(String flowInstId, String varname);


    /**
     * 查询某个流程节点的变量
     *
     * @param flowInstId 工作流实例号
     * @param varname    变量名
     * @param runToken   令牌
     * @return 流程变量
     */
    FlowVariable viewNodeVariable(String flowInstId, String runToken, String varname);

    //--------------------信息查看相关接口-----------------------------------

    /**
     * 根据流程编号获得流程实例
     *
     * @param flowInstId 工作流实例号
     * @return 流程实例信息
     */
    FlowInstance getFlowInstById(String flowInstId);

    /**
     * 获取流程定义信息
     *
     * @param flowInstId 实例id
     * @return 流程定义信息
     */
    FlowInfo getFlowDefine(String flowInstId);


    /**
     * 根据节点实例号 获得节点实例
     *
     * @param nodeInstId 节点实例号
     * @return 节点信息
     */
    NodeInstance getNodeInstById(String nodeInstId);

    /**
     * 获取节点定义信息
     *
     * @param nodeInstId 节点实例id
     * @return 节点实例信息
     */
    NodeInfo getNodeInfo(String nodeInstId);

    /**
     * 获取用户操作节点的Url，if ! canAccess rteurn null
     *
     * @param nodeInstId 节点实例代码
     * @param userCode   用户代码
     * @return optUrl
     */
    String getNodeOptUrl(String nodeInstId, String userCode);

    /**
     * 根据节点实例号 获得节点实例
     *
     * @param flowInstId 流程实例id
     * @param nodeCode   节点代码
     * @return 节点信息列表
     */
    List<NodeInstance> listNodeInstanceByNodeCode(String flowInstId, String nodeCode);

    /**
     * 列举当前流程可以创建的所有节点
     *
     * @param flowInstId 流程实例代码
     * @return Map 节点代码， 节点名称
     */
    Map<String, String> listFlowNodeForCreate(String flowInstId);

    /**
     * 获取节点所在阶段信息
     *
     * @param nodeInstId 节点实例id
     * @return 阶段信息
     */
    StageInstance getStageInstByNodeInstId(String nodeInstId);


    /**
     * 删除流程变量
     *
     * @param flowInstId 必须
     * @param runToken   非必须
     * @param varName    非必须
     */
    void deleteFlowVariable(String flowInstId, String runToken, String varName);


    /**
     * 创建 流程分组
     *
     * @param name 分组名称
     * @param desc 分组描述
     * @return 流程分组
     */
    FlowInstanceGroup createFlowInstGroup(String name, String desc);

    /**
     * @param filterMap 过滤条件
     * @param pageDesc  分页信息
     * @return 用户任务列表
     */
    List<FlowInstanceGroup> listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 获取流程实例的业务节点信息
     *
     * @param flowInstId
     * @return
     */
    JSONArray viewFlowNodes(String flowInstId);

    /**
     * 更新办件角色
     *
     * @param u
     */
    void updateFlowWorkTeam(FlowWorkTeam u);
}
