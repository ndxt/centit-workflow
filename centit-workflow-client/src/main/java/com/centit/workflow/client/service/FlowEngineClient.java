package com.centit.workflow.client.service;


import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程运行接口
 */
public interface FlowEngineClient {

    public CloseableHttpClient getHttpClient() throws Exception ;

    public void releaseHttpClient(CloseableHttpClient httpClient);

    public void setWorkFlowServerUrl(String workFlowServerUrl) ;

    //--------------------创建流程实例接口-----------------------------------    
    /**
     * 创建流程实例  返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return
     */
    String createInstance(String flowCode, String flowOptName,
                                String flowOptTag, String userCode, String unitCode) throws Exception;

    /**
     * 创建流程实例 返回流程实例
     * @param flowCode 流程编码
     * @param version 指定版本号
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return
     */
    String createInstance(String flowCode, long version, String flowOptName,
                                String flowOptTag, String userCode, String unitCode) throws Exception;

    /**
     * 创建流程实例 返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @param varTrans 变量转换接口，用于表达式计算，可以为null
     * @param application 容器句柄，用于自动执行节点，一般首节点不会为自动执行节点，可以为null
     * @return
     */
    String createInstance(String flowCode, long version, String flowOptName,
                                String flowOptTag, String userCode, String unitCode,
                                Map<String,Object> varTrans, ServletContext application) throws Exception;

//    /**
//     * 创建流程实例  返回流程实例
//     * @param flowCode 流程编码
//     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
//     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
//     * @param userCode 创建用户
//     * @param unitCode 将流程指定一个所属机构
//     * @param varTrans 变量转换接口，用于表达式计算，可以为null
//     * @param application 容器句柄，用于自动执行节点，一般首节点不会为自动执行节点，可以为null
//     * @return
//     */
//    FlowInstance createInstance(String flowCode, String flowOptName,
//                                String flowOptTag, String userCode, String unitCode,
//                                Map<String,Object> varTrans, ServletContext application);
//
//
    /**
     * 创建流程实例并锁定首节点，首节点只能有创建人操作 返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return FlowInstance 流程实例
     */
    FlowInstance createInstanceLockFirstNode(String flowCode, String flowOptName,
                                             String flowOptTag, String userCode, String unitCode) throws Exception;

//
//
//    /**
//     * 创建流程实例并锁定首节点，首节点只能有创建人操作 返回流程实例
//     * @param flowCode 流程编码
//     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
//     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
//     * @param userCode 创建用户
//     * @param unitCode 将流程指定一个所属机构
//     * @return
//     */
//    FlowInstance createInstanceLockFirstNode(String flowCode, long version,
//                                             String flowOptName, String flowOptTag, String userCode, String unitCode);
//
//    /*
//     * 创建子流程实例 返回流程实例号  子流程由内部创建不需要提供接口
//       long createInstance(String  flowCode,long nodeInstId,String userCode,String unitCode);
//    */
//
//
//   //--------------------提交流程业务节点-----------------------------------
    /**
     * 返回下一步节点的节点实例ID
     * @param nodeInstId 当前节点实例编号
     * @param userCode 操作用户编号 对应用户表达式 O operator
     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
     * @param varTrans 变量转换器
     * @return  节点实例编号列表
        */
    Set<Long> submitOpt(long nodeInstId, String userCode,
                        String unitCode, Map<String,Object> varTrans,
                        ServletContext application) throws  Exception;
//
//
//    Set<Long> submitOpt(long nodeInstId, String userCode, String grantorCode,
//                        String unitCode, Map<String,Object> varTrans,
//                        ServletContext application);
//
//    /**
//     * 返回下一步节点的节点实例ID
//     * @param nodeInstId 当前节点实例编号
//     * @param userCode 操作用户编号 对应用户表达式  O operator
//     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
//     * @param varTrans 变量转换器
//     * @param nodeOptUsers 预设的节点操作用户  给程序自行判断用户和机构用的
//     * @param nodeUnits 预设的节点机构 给程序自行判断用户和机构用的
//     * @return  节点实例编号列表
//     */
//    Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode,
//                                             String unitCode, Map<String,Object> varTrans,
//                                             Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
//                                             ServletContext application);
//
//    /**
//     * 返回下一步节点的节点实例ID
//     * @param nodeInstId 当前节点实例编号
//     * @param userCode 操作用户编号 对应用户表达式  O operator
//     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
//     * @param varTrans 变量转换器
//     * @param nodeOptUsers 预设的节点操作用户  给程序自行判断用户和机构用的
//     * @param nodeUnits 预设的节点机构 给程序自行判断用户和机构用的
//     * @return  节点实例编号列表
//     */
//    Set<Long> submitOptWithAssignUnitAndUser(long nodeInstId, String userCode,
//                                             String grantorCode, String unitCode, Map<String,Object> varTrans,
//                                             Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers,
//                                             ServletContext application);
//
//
//  //--------------------查看流转信息-----------------------------------
//    /**
//     * 提交节点工作 是否成功
//     * 预判下一步节点的节点编号
//     * @param nodeInstId 当前节点实例编号
//     * @param userCode 操作用户编号 对应用户表达式 O operator
//     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
//     * @param varTrans 变量转换器
//     * @return 节点信息列表
//     */
//    Set<NodeInfo> viewNextNode(long nodeInstId, String userCode,
//                               String unitCode, Map<String,Object> varTrans);
//
//    /**
//     * 查看下一节点可以操作的人员类表
//     * @param nextNodeId 下一个节点编号
//     * @param curNodeInstId 当前节点实例编号
//     * @param userCode 操作用户编号 对应用户表达式 O operator
//     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
//     * @param varTrans 变量转换器
//     * @return
//     */
//    Set<String> viewNextNodeOperator(long nextNodeId, long curNodeInstId,
//                                     String userCode, String unitCode, Map<String,Object> varTrans);
//
//
//    //-------------------------用户待办接口---------------------------------------
//
//    //任务类表查看
//
    /**
     * 查看某一个用户所有的待办，并且分页
     * @param userCode
     * @param pageDesc
     * @return
     */
    List<UserTask> listUserTasks(String userCode, PageDesc pageDesc);
//
//    /**
//     * 这个查看某个用户对用特定流程的待办
//     * @param userCode
//     * @param flowCode
//     * @param pageDesc
//     * @return
//     */
//    List<UserTask> listUserTasksByFlowCode(String userCode, String flowCode, PageDesc pageDesc);
//    /**
//     * 查看某个用户对用某一类流程的待办
//     * @param userCode
//     * @param flowClass
//     * @param pageDesc
//     * @return
//     */
//    //List<UserTask> listUserTasksByFlowClass(String userCode,String flowClass,PageDesc pageDesc);
//    /**
//     * 查看某一个用户对应某一个阶段的待办
//     * @param userCode
//     * @param flowStage
//     * @param pageDesc
//     * @return
//     */
//    List<UserTask> listUserTasksByFlowStage(String userCode, String flowStage, PageDesc pageDesc);
//    /**
//     * 查询某个用户的对应某一个节点的待办，这个节点可以是多个流程中的节点，只要这些节点的nodecode一致
//     * @param userCode
//     * @param nodeCode
//     * @param pageDesc
//     * @return
//     */
//    List<UserTask> listUserTasksByNodeCode(String userCode, String nodeCode, PageDesc pageDesc);
//
//
//    /**
//     * 列举用户已办事项
//     */
//     /*List<NodeInstance> listUserCompleteTasks(
//            Map<String, Object> filterMap, PageDesc pageDesc, String userCode);
//    */
//
//    /**
//     * 查看某一个用户所有的已办，并且分页
//     * @param filterMap
//     * @param pageDesc
//     * @return
//     */
//    List<UserTask> listUserCompleteTasks(Map<String, Object> filterMap, PageDesc pageDesc);
//
//    /**
//     * 判断一个用户是否可以处理指定的节点,可以喝submitOpt结合使用，
//     * 判断当前操作人员是否可以访问提交后的下一个节点。
//     *
//     * @param nodeInstId 节点实例代码
//     * @param userCode 用户代码
//     * @return
//     */
//    boolean canAccess(long nodeInstId, String userCode);
//
//
//    /**
//     * 获取任务授权人，如果是用户自己的任务，返回自己，否则返回授权人
//     * @param nodeInstId
//     * @param userCode
//     * @return
//     */
//    String getTaskGrantor(long nodeInstId, String userCode);
//
//    //预报警列表查看
//    /**
//     * 自定义预警查询
//     * @param filterMap
//     * @param pageDesc
//     * @return
//     */
//    List<FlowWarning> listFlowWarning(Map<String, Object> filterMap, PageDesc pageDesc);
//
//    /**
//     * 查询某个流程的预警
//     * @param flowInstId
//     * @param pageDesc
//     * @return
//     */
//    List<FlowWarning> listFlowWarningByInst(Long flowInstId, PageDesc pageDesc);
//    /**
//     * 查询某个节点的预警
//     * @param nodeInstId
//     * @param pageDesc
//     * @return
//     */
//    List<FlowWarning> listFlowWarningByNodeInst(Long nodeInstId, PageDesc pageDesc);
//    /**
//     * 查询某一个类别的预警
//     * @param warningCode
//     * @param pageDesc
//     * @return
//     */
//    List<FlowWarning> listFlowWarningByWarningCode(String warningCode, PageDesc pageDesc);
//
//
//    //--------------------特殊操作和记录日志接口-----------------------------------
//    //--------------------更改流程业务相关数据接口-----------------------------------
//    /**
//     * 更改流程业务信息，flowOptName 用来显示业务办件名称，flowOptTag 给业务系统自己解释可以用于反向关联
//     * @param flowInstId 流程实例ID
//     * @param flowOptName 这个名称用户 查找流程信息
//     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，有业务系统自己解释
//     */
//    void updateFlowInstOptInfo(long flowInstId, String flowOptName, String flowOptTag);
//
//    //--------------------人工控制流程流转和任务分配------------------------------------
//
//    /**
//     * 流程节点操作日志
//     * @param nodeInstId 节点实例编号
//     * @param userCode 用户编码
//     * @param actionType
//     *               s: 状态变更，挂起节点、 唤醒超时节点、  唤醒节点 、使失效、 终止节点 、使一个正常的节点变为游离状态 、 是游离节点失效
//     *               c: 创建节点  、创建一个游离节点 创建（任意）指定节点、 创建流程同时创建首节点
//     *               r: 流转管理，包括  强行回退  、强行提交
//     *               t: 期限管理 、 设置期限
//     *               a: 节点任务管理  分配任务、  删除任务 、  禁用任务
//     *               u: 变更属性     *
//     */
//    void recordActionLog(long nodeInstId, String userCode, String actionType);
//
//
//    /**
//     * 回退操作-回退到上一个节点
//     */
//    long rollbackOpt(long nodeInstId, String mangerUserCode);
//    /**
//     * 检查后续的节点是否被操作过，包括更新和提交
//     * 只有后续节点没有处理的才可以收回。true表示可以撤回，false表示不可以撤回，
//     * @param nodeInstId
//     * @return
//     */
//    boolean nodeCanBeReclaim(long nodeInstId);
//
//
//    /**
//     * 加签,并指定到人
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param nodeId    节点号
//     * @param createUser  当前创建用户
//     * @param userCode  指定操作用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId,
//                                            long nodeId, String createUser, String userCode, String unitCode);
//
//
//    /**
//     * 加签,并指定到人
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param nodeCode    节点号
//     * @param createUser  当前创建用户
//     * @param userCode  指定操作用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createPrepNodeInstLockUser(long flowInstId, long curNodeInstId,
//                                            String nodeCode, String createUser, String userCode, String unitCode);
//
//    /**
//     * 加签
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param nodeId    节点号
//     * @param userCode  指定用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId,
//                                    long nodeId, String userCode, String unitCode);
//
//
//    /**
//     *
//     * 加签
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param nodeCode    节点环节代码，这个节点在这个流程中必需唯一
//     * @param userCode  指定操作用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createPrepNodeInst(long flowInstId, long curNodeInstId,
//                                    String nodeCode, String userCode, String unitCode);
//
//    /**
//     * 创建一个游离节点
//     * 知会、关注
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param userCode    节点环节代码，这个节点在这个流程中必需唯一
//     * @param createUser  当前创建用户
//     * @param userCode  指定操作用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId,
//                                        String nodeCode, String createUser, String userCode, String unitCode);
//
//
//    /**
//     * 创建一个游离节点
//     * 知会、关注
//     *
//     * 用户手动创建一个节点实例，当前节点实例挂起，等这个新建的节点实例运行完提交时，当前节点实例继续运行.
//     * 同一个节点可以创建多个前置节点，当所有的前置节点都执行提交后，现有的节点才被唤醒
//     * @param flowInstId 流程实例号
//     * @param curNodeInstId 当前节点实例号
//     * @param nodeId    节点号
//     * @param createUser  当前创建用户
//     * @param userCode  指定操作用户
//     * @param unitCode 指定机构
//     * @return 节点实例
//     */
//    NodeInstance createIsolatedNodeInst(long flowInstId, long curNodeInstId,
//                                        long nodeId, String createUser, String userCode, String unitCode);
//
//
//    /**
//     * 更改流程的父节点，这个函数只是用来手动的将一个流程作为子流程挂到父流程的节点上，一般不会使用。
//     * @param flowInstId   子流程实例id
//     * @param parentFlowInstId 父流程实例id
//     * @param parentNodeInstId 父流程节点实例id
//     */
//    void updateFlowInstParentNode(long flowInstId, long parentFlowInstId,
//                                  long parentNodeInstId);
//
//
//    /**
//     * 关闭本节点分支以外的其他分支的所有节点,特指和本节点平行的分支，就是同一个父类令牌的分支
//     * @param nodeInstId 当前活动节点
//     * @param optUserCode 操作人员
//     */
//    void disableOtherBranchNodes(long nodeInstId, String optUserCode);
//    //--------------------任务分配和查看相关接口-----------------------------------
//
//    /**
//     * 分配工作小组 --办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 办件角色 不能为空
//     * @param userCode 用户代码，添加
//     * @return
//     */
//    void assignFlowWorkTeam(long flowInstId, String roleCode, String userCode);
    /**
     * 分配工作小组 --办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     * @return
     */
    void assignFlowWorkTeam(long flowInstId, String roleCode,
                            List<String> userCodeSet) throws Exception;
//
//
//    /**
//     * 分配工作小组 --办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 办件角色 不能为空
//     * @param userCode 用户代码，添加
//     * @param authdesc 角色描述
//     * @return
//     */
//    void assignFlowWorkTeam(long flowInstId, String roleCode,
//                            String userCode, String authdesc);
//    /**
//     * 分配工作小组 --办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 办件角色 不能为空
//     * @param userCodeSet 用户代码列表，添加
//     * @param authdesc 角色描述
//     * @return
//     */
//    void assignFlowWorkTeam(long flowInstId, String roleCode,
//                            List<String> userCodeSet, String authdesc);
//
//    /**
//     * 删除工作小组--办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 办件角色 不能为空
//     * @param userCode 用户代码，添加
//     */
//    void deleteFlowWorkTeam(long flowInstId, String roleCode, String userCode);
    /**
     * 删除工作小组--办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     */
    void deleteFlowWorkTeam(long flowInstId, String roleCode) throws Exception;
//
//    /**
//     * 查看工作小组
//     * @param flowInstId
//     * @return Map<roleCode,Set<userCode>>
//     */
//    Map<String,List<String>> viewFlowWorkTeam(long flowInstId);
//
//    /**
//     * 查看工作小组中某个角色的成员
//     * @param flowInstId 工作流实例号
//     * @param roleCode 办件角色代码
//     * @return Set<userCode>
//     */
//    List<String> viewFlowWorkTeam(long flowInstId, String roleCode);
//
//    /**
//     * 查看工作小组中某个角色的成员
//     * @param flowInstId
//     * @param roleCode
//     * @return
//     */
//    List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode);
//
//    /**
//     * 查看工作小组中某个角色的成员,并且通过制定的授权说明过滤
//     * @param flowInstId
//     * @param roleCode
//     * @param authdesc 角色描述
//     * @return
//     */
//    List<FlowWorkTeam> viewFlowWorkTeamList(long flowInstId, String roleCode,
//                                            String authdesc);
//
//
//    /**
//     * 分配节点任务
//     *  Task_assigned 设置为 S 如果多于 一个人 放在 ActionTask 表中，并且把  Task_assigned 设置为 T
//     */
//    long assignNodeTask(long nodeInstId, String userCode,
//                        String mangerUserCode, Date expiretime, String authDesc);
//    /**
//     * 取消节点任务
//     */
//    int disableNodeTask(long nodeInstId, String userCode, String mangerUserCode);
//    /**
//     * 删除节点任务
//     */
//    int deleteNodeTask(long nodeInstId, String userCode, String mangerUserCode);
//
//    /**
//     * 删除节点任务
//     */
//    int deleteNodeAllTask(long nodeInstId, String mangerUserCode);
//
//
//    /**
//     * 分配流程组织机构
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     * @param unitCode 机构代码，添加
//     * @return
//     */
//    void assignFlowOrganize(long flowInstId, String roleCode, String unitCode);
//    /**
//     * 分配工作小组 --办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     * @param unitCodeSet 机构代码列表，添加
//     * @return
//     */
//    void assignFlowOrganize(long flowInstId, String roleCode, List<String> unitCodeSet);
//
//    /**
//     * 分配流程组织机构
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     * @param unitCode 机构代码，添加
//     * @return
//     */
//    void assignFlowOrganize(long flowInstId, String roleCode,
//                            String unitCode, String authdesc);
//    /**
//     * 分配工作小组 --办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     * @param unitCodeSet 机构代码列表，添加
//     * @return
//     */
//    void assignFlowOrganize(long flowInstId, String roleCode,
//                            List<String> unitCodeSet, String authdesc);
//
//    /**
//     * 删除工作小组--办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     * @param unitCode 机构代码，添加
//     */
//    void deleteFlowOrganize(long flowInstId, String roleCode, String unitCode);
//    /**
//     * 删除工作小组--办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     */
//    void deleteFlowOrganize(long flowInstId, String roleCode);
//
//    /**
//     * 删除工作小组--办件角色
//     * @param flowInstId  流程实例号 不能为空
//     * @param roleCode 机构角色 不能为空
//     */
//    void deleteFlowOrganizeByAuth(long flowInstId, String roleCode,
//                                  String authDesc);
//
//    /**
//     * 查看工作小组
//     * @param flowInstId
//     * @return Map<roleCode,Set<unitCode>>
//     */
//    Map<String,List<String>> viewFlowOrganize(long flowInstId);
//
//    /**
//     * 查看工作小组中某个角色的成员
//     * @param flowInstId 工作流实例号
//     * @param roleCode 机构角色代码
//     * @return Set<unitCode>
//     */
//    List<String> viewFlowOrganize(long flowInstId, String roleCode);
//
//    /**
//     *
//     * @param flowInstId
//     * @param roleCode
//     * @return
//     */
//    List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode);
//
//    /**
//     *
//     * @param flowInstId
//     * @param roleCode
//     * @return
//     */
//    List<FlowOrganize> viewFlowOrganizeList(long flowInstId, String roleCode,
//                                            String authDesc);
//
    /**
     * 设置流程全局变量
     * @param flowInstId
     * @param sVar
     * @param sValue
     */
    void saveFlowVariable(long flowInstId, String sVar, String sValue) throws Exception;
//
//    /**
//     * 设置流程节点上下文变量
//     * @param nodeInstId
//     * @param sVar
//     * @param sValue
//     */
//    void saveFlowNodeVariable(long nodeInstId, String sVar, String sValue);
//
//
//    /**
//     * 设置流程节点上下文变量
//     * @param flowInstId
//     * @param runToken
//     * @param sVar
//     * @param sValue
//     */
//    void saveFlowNodeVariable(long flowInstId, String runToken, String sVar, String sValue);
//
//    /**
//     * 设置流程全局变量
//     * @param flowInstId
//     * @param sVar
//     * @param sValues Set<String> 中的值不能有 分号 ;
//     */
//    void saveFlowVariable(long flowInstId, String sVar, Set<String> sValues);
//
//    /**
//     * 设置流程节点上下文变量
//     * @param nodeInstId
//     * @param sVar
//     * @param sValues Set<String> 中的值不能有 分号 ;
//     */
//    void saveFlowNodeVariable(long nodeInstId, String sVar, Set<String> sValues);
//
//    /**
//     * 查询流程变量
//     * @param flowInstId
//     * @return
//     */
//    List<FlowVariable> listFlowVariables(long flowInstId);
    /**
     * 查询某个流程变量
     * @param flowInstId
     * @return
     */
    List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varname) throws Exception;
//
//
//    /**
//     * 查询某个流程节点的变量
//     * @param flowInstId
//     * @return
//     */
//    FlowVariable viewNodeVariable(long flowInstId, String runToken, String varname);
//
//    //流程关注设置与清空
//    /**
//     * 设置流程关注人员
//     * @param flowInstId 流程实例id
//     * @param attUser 关注人员
//     * @param optUser 设置人员
//     */
//    void saveFlowAttention(long flowInstId, String attUser, String optUser);
//
//    /**
//     * 删除流程关注人员
//     * @param flowInstId
//     * @param attUser 关注人员
//     */
//    void deleteFlowAttention(long flowInstId, String attUser);
//
//    /**
//     * 删除流程关注人员
//     * @param flowInstId
//     * @param optUser 关注设置人员
//     */
//    void deleteFlowAttentionByOptUser(long flowInstId, String optUser);
//
//    /**
//     * 删除流程所有关注人员
//     * @param flowInstId
//     */
//    void deleteFlowAttention(long flowInstId);
//    /**
//     * 获取流程关注人员
//     * @param flowInstId
//     * @return
//     */
//    List<InstAttention> viewFlowAttention(long flowInstId);
//
//    /**
//     * @param flowInstId
//     * @param userCode 关注人员
//     * @return
//     */
//    InstAttention getFlowAttention(long flowInstId, String userCode);
//
//
//    /**
//     * 返回所有关在的项目
//     * @param userCode 关注人
//     * @param instState  N 正常  C 完成   P 暂停 挂起     F 强行结束  A 所有
//     * @return
//     */
//    List<FlowInstance> viewAttentionFLowInstance(String userCode, String instState);
//    //--------------------信息查看相关接口-----------------------------------
//    /**
//     * 根据流程编号获得流程实例
//     * @param flowInstId
//     * @return
//     */
//    FlowInstance getFlowInstById(long flowInstId);
//    /**
//     * 根据节点实例号 获得节点实例
//     * @param nodeInstId
//     * @return
//     */
//    NodeInstance getNodeInstById(long nodeInstId);
//
//    /**
//     * 获取用户操作节点的Url，if ! canAccess rteurn null
//     *
//     * @param nodeInstId 节点实例代码
//     * @param userCode 用户代码
//     * @return
//     */
//    String getNodeOptUrl(long nodeInstId, String userCode);
//
//    /**
//     * 根据节点实例号 获得节点实例
//     * @param flowInstId  流程实例id
//     * @param nodeCode  节点代码
//     * @return
//     */
//    List<NodeInstance> listNodeInstsByNodecode(long flowInstId, String nodeCode);
//
//    /**
//     * 获取节点所在阶段信息
//     * @param nodeInstId
//     * @return
//     */
//    StageInstance getStageInstByNodeInstId(long nodeInstId);
}
