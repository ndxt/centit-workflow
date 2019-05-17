package com.centit.workflow.client.service;


import com.centit.workflow.po.NodeInstance;

import java.util.List;

/**
 * 流程管理业务接口类
 *   <流程终止，暂停，唤醒，回退等操作>
 * @author codefan@sina.com
 * @version 2.0 <br>
 *
 */
public interface FlowManagerClient {
    /**
     * 获取流程实例下的节点实例列表
     * @param wfinstid 流程实例编号
     * @return List<NodeInstance>
     */
    List<NodeInstance> listFlowInstNodes(Long wfinstid) throws Exception;

    /**
     * 终止一个流程
     * 修改其流程id为负数
     * 更新所有节点状态为F
     * F 强行结束
     */
    void stopAndChangeInstance(long flowInstId, String mangerUserCode, String admindesc) throws Exception;
  }
