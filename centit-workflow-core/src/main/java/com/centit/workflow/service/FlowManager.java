package com.centit.workflow.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程管理业务接口类
 * <流程终止，暂停，唤醒，回退等操作>
 *
 * @author codefan@sina.com
 * @version 2.0 <br>
 */
public interface FlowManager {
    //------------查看流程运行状态图---------------------------------

    /**
     * 获取系统中所有的流程实例
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页描述
     * @return
     */
    JSONArray listFlowInstance(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 根据 示例flowOptTag获得实例
     * @param flowOptTag
     * @return
     */
    //FlowInstance getFlowInstance(String flowOptTag );


    /**
     * 根据 示例ID获得实例
     *
     * @param flowInstId 流程 实例id
     * @return 流程实例信息
     */
    FlowInstance getFlowInstance(String flowInstId);

    /**
     * 查看工作流程实例状态或进度
     *
     * @param flowInstId 流程 实例id
     * @return XML 描述的流程流转状态图
     */
    Map<String, Object> viewFlowInstance(String flowInstId);

    /**
     * 查看工作流程节点示例图
     *
     * @param flowInstId 流程 实例id
     * @return 工作流程节点示例图
     */
    String viewFlowNodeInstance(String flowInstId);
    //流程实例管理-------------------------------
    //-----------流程状态变更-----N 正常  C 完成   P 暂停 挂起     F 强行结束---------------------------

    /**
     * 终止一个流程  F 强行结束
     * 修改其流程id为负数
     * 更新所有节点状态为F
     * F 强行结束
     */
    int stopInstance(String flowInstId, CentitUserDetails managerUser, String admindesc);

    /**
     * 暂停一个流程    P 暂停 挂起
     *
     * @param flowInstId     流程实例id
     * @param managerUser 管理人员代码
     * @param admindesc      管理原因
     */
    int suspendInstance(String flowInstId, CentitUserDetails managerUser, String admindesc);

    /**
     * 激活一个 挂起的或者无效的流程  N 正常
     *
     * @param flowInstId     流程实例id
     * @param managerUser 管理人员代码
     * @param admindesc      管理原因
     */
    int activizeInstance(String flowInstId, CentitUserDetails managerUser, String admindesc);

    /**
     * 查询某人暂停计时的流程
     *
     * @param userCode
     * @param pageDesc
     * @return
     */
    List<FlowInstance> listPauseTimerFlowInst(String userCode, PageDesc pageDesc);

    /**
     * 暂停流程计时
     *
     * @param flowInstId
     * @param managerUser
     */
    int suspendFlowInstTimer(String flowInstId, CentitUserDetails managerUser);


    /**
     * 唤醒流程计时
     *
     * @param flowInstId
     * @param managerUser
     */
    int activizeFlowInstTimer(String flowInstId, CentitUserDetails managerUser);

    /**
     * 设置流程期限
     *
     * @param flowInstId     流程实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param managerUser 管理人员代码
     * @param admindesc      流程期限更改原因说明
     * @return
     */
    long resetFlowTimelimt(String flowInstId, String timeLimit, CentitUserDetails managerUser, String admindesc);

    //------------流程属性修改----------------------------------

    /**
     * 更改流程所属机构
     *
     * @param flowInstId 流程实例ID
     * @param unitCode   机构代码
     */
    void updateFlowInstUnit(String flowInstId, String unitCode, CentitUserDetails managerUser);


    //------日志信息查看-----------------

    /**
     * 获取流程实例下的节点实例列表
     *
     * @param wfinstid 流程实例编号
     * @return List<NodeInstance>
     */
    List<NodeInstance> listFlowInstNodes(String wfinstid);


    List<NodeInstance> listFlowActiveNodes(String wfinstid);
    //-----------节点状态变更-------------------------------
    /*
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */

    /**
     * 暂停流程的一个节点  P 暂停
     */
    long suspendNodeInstance(String nodeInstId, CentitUserDetails managerUser);

    /**
     * 使流程的 挂起和失效的节点 正常运行 N 正常
     */
    long activizeNodeInstance(String nodeInstId, CentitUserDetails managerUser);


    /**
     * 强制修改流程的节点状态
     *
     * @param nodeInstId
     * @param newState
     */
    void updateNodeState(String nodeInstId, String newState, CentitUserDetails managerUser);

    /**
     * 强制流转到下一结点，这个好像不好搞，主要是无法获得业务数据，只能提交没有分支的节点
     */
    String forceCommit(String nodeInstId, CentitUserDetails managerUser);


    /**
     * 查询某人暂定计时的节点
     *
     * @param userCode
     * @param pageDesc
     * @return
     */
    List<NodeInstance> listPauseTimerNodeInst(String userCode, PageDesc pageDesc);

    /**
     * 暂停节点定时
     *
     * @param nodeInstId
     * @param managerUser
     */
    int suspendNodeInstTimer(String nodeInstId, CentitUserDetails managerUser);


    /**
     * 唤醒节点定时
     *
     * @param nodeInstId
     * @param managerUser
     */
    int activizeNodeInstTimer(String nodeInstId, CentitUserDetails managerUser);


    //----------流程分支管理-------------------------------

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     *
     * @param nodeInstId     节点实例id
     * @param managerUser 管理人员代码
     * @return 新的节点实例
     */
    NodeInstance resetFlowToThisNode(String nodeInstId, CentitUserDetails managerUser);

    /**
     * 强制一个并行分支的节点为游离状态，在提交其他并行分支前调用
     */
    String forceDissociateRuning(String nodeInstId, CentitUserDetails managerUser);


    //------------节点属性修改----------------------------------

    /**
     * 更改节点所属机构
     *
     * @param nodeInstId 节点实例ID
     * @param unitCode   机构代码
     */
    void updateNodeInstUnit(String nodeInstId, String unitCode, CentitUserDetails managerUser);


    /**
     * 更改节点的角色信息
     */
    void updateNodeRoleInfo(String nodeInstId, String roleType, String roleCode, CentitUserDetails managerUser);

    /**
     * 设置流程期限
     *
     * @param nodeInstId     流程节点实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param managerUser 管理人员代码
     * @return
     */
    long resetNodeTimelimt(String nodeInstId, String timeLimit, CentitUserDetails managerUser, String topUnit);


    //------------流程阶段管理-------------------------

    /**
     * 获取节点所在阶段信息
     *
     * @param flowInstId
     * @return
     */
    List<StageInstance> listStageInstByFlowInstId(String flowInstId);


    /**
     * 设置流程期限
     *
     * @param flowInstId     流程实例编号
     * @param timeLimit      新的流程期限 5D3h
     * @param managerUser 管理人员代码
     * @param admindesc      流程期限更改原因说明
     * @return
     */
    long resetStageTimelimt(String flowInstId, String stageId,
                            String timeLimit, CentitUserDetails managerUser, String admindesc);

    //------------流程角色管理-----接口参见flowEngine--------------------
    //------------流程机构管理------接口参见flowEngine--------------------
    //------------流程变量管理------接口参见flowEngine---------------------
    //------------流程预警管理------接口参见flowEngine---------------------
    //------查看节点信息-------------------------

    /**
     * 获取节点实例的操作日志列表
     *
     * @param flowInstId 流程实例号
     * @param nodeInstId 节点实例好
     * @return List<WfActionLog>
     */
    List<? extends OperationLog> listNodeActionLogs(String flowInstId, String nodeInstId);

    List<? extends OperationLog> listNodeActionLogs(String nodeInstId);

    /**
     * 获取节点实例的操作日志列表
     *
     * @param flowInstId     流程实例id
     * @param topUnit 归宿租户
     * @return List<WfActionLog>
     */
    List<? extends OperationLog> listFlowActionLogs(String flowInstId, String topUnit);

    /**
     * 获取用户所有的操作记录
     *
     * @param userCode
     * @param pageDesc 和分页机制结合
     * @param lastTime if null return all
     * @return
     */
    List<? extends OperationLog> listUserActionLogs(String userCode, Date lastTime, PageDesc pageDesc);

    // ---------节点异常管理--------------------

    /**
     * 查找所有没有操作用户的节点
     *
     * @return List<NodeInstance>
     */
    List<NodeInstance> listNodesWithoutOpt();


    /**
     * 分配节点任务
     * Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
     */
    int assignNodeTask(String nodeInstId, String userCode,
                       CentitUserDetails managerUser, String authDesc);

    // ---------节点任务委托--------------------


    /**
     * @param relegateno
     * @return
     */
    RoleRelegate getRoleRelegateById(String relegateno);

    /**
     * @param roleRelegate
     */
    void saveRoleRelegate(RoleRelegate roleRelegate);

    /**
     * 根据委托与受委托人删除委托
     *
     * @param grantor
     * @param grantee
     */
    void deleteRoleRelegateByUserCode(String grantor, String grantee);

    /**
     * 查询别人委托给我的
     *
     * @param userCode
     * @return
     */
    List<RoleRelegate> listRoleRelegateByUser(String userCode);

    /**
     * 查询我委托给别人的
     *
     * @param grantor
     * @return
     */
    List<RoleRelegate> listRoleRelegateByGrantor(String grantor);

    // ---------节点任务迁移--------------------

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     * @param topUnit  所属租户
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param moveDesc     迁移描述
     * @param managerUser  操作人员
     * @return 返回迁移的任务数
     */
    int moveUserTaskTo(String topUnit, String fromUserCode, String toUserCode,
                       CentitUserDetails managerUser, String moveDesc);
    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     * @param osId  所属租户
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param moveDesc     迁移描述
     * @param managerUser  操作人员
     * @return 返回迁移的任务数
     */
    int moveUserTaskToByOs(String osId, String fromUserCode, String toUserCode,
                           CentitUserDetails managerUser, String moveDesc);

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     *
     * @param nodeInstIds  任务节点结合
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param moveDesc     迁移描述
     * @param managerUser  操作人员
     * @return 返回迁移的任务数
     */
    int moveUserTaskTo(List<String> nodeInstIds, String fromUserCode, String toUserCode,
                       CentitUserDetails managerUser, String moveDesc);

    /**
     * 将 fromUserCode 所有任务 迁移 给 toUserCode
     *
     * @param params  过滤条件
     * @param fromUserCode 任务属主
     * @param toUserCode   新的属主
     * @param moveDesc     迁移描述
     * @param managerUser  操作人员
     */
    void moveUserTaskTo(Map<String, Object> params,String fromUserCode, String toUserCode,CentitUserDetails managerUser, String moveDesc);

    void updateFlow(FlowInstance flowInstance);

    int changeTaskAssignScheme(TaskMove taskMove, CentitUserDetails managerUser);
    /**
     * 流程拉回到首节点
     *
     * @param flowInstId
     * @param managerUser
     * @param force           是否强制，否的话 需要判断流程最后提交人是否是自己
     */
    NodeInstance reStartFlow(String flowInstId, CentitUserDetails managerUser, Boolean force);

    List<JSONObject> getListRoleRelegateByGrantor(String grantor);

    /**
     * 获取所有流程分组
     *
     * @param filterMap
     * @param pageDesc
     * @return
     */
    JSONArray listFlowInstGroup(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 根据ID获得流程分组
     *
     * @param flowInstGroupId
     * @return
     */
    FlowInstanceGroup getFlowInstanceGroup(String flowInstGroupId);

    void updateFlowInstOptInfoAndUser(String flowInstId, String flowOptName, String flowOptTag,
                                      String userCode, String unitCode);

    NodeInstance getFirstNodeInst(String flowInstId);

    /**
     * 删除流程实例数据
     *
     * @param flowInstId
     * @param userCode
     * @return
     */
    boolean deleteFlowInstById(String flowInstId, String userCode);

    /**
     * 获取节点实例列表
     *
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    List<NodeInstance> listNodeInstance(Map<String, Object> searchColumn, PageDesc pageDesc);

    ResponseData dubboNodeInstance(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 强制修改流程状态以及相关节点实例状态
     *
     * @param flowInstId
     * @param managerUser
     * @param instState
     * @param desc
     */
    void updateFlowState(String flowInstId, CentitUserDetails managerUser, String instState, String desc);

    /**
     * 获取流程实例列表，并查询流程相关信息(fgw收文办结列表和发文办结列表)
     *
     * @param searchColumn
     * @param pageDesc
     * @return
     */
    JSONArray listFlowInstDetailed(Map<String, Object> searchColumn, PageDesc pageDesc);

    /**
     * 批量删除工作流实例
     *
     * @param flowInstIds
     */
    void deleteFlowInstByIds(List<String> flowInstIds);

    /** 流程定义时 必须用 nodeCode 进行节点标注
     * 将正在执行中的流程从一个版本迁移到另一个版本（只能在同一个流程的不通版本间迁移）
     * @param flowCode 流程代码
     * @param newVersion 新的版本 如果 <=0 则替换为 最新版本
     * @param oldVersion 旧的版本， 如果 <=0 则将所有的版本 都迁移过过来
     * @return 返回迁移的流程实例数量
     */
    void upgradeFlowVersion(String flowCode, long newVersion, long oldVersion,
                            String topUnit, CentitUserDetails managerUser);

}
