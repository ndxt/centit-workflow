package com.centit.workflow.service;

import com.centit.framework.core.dao.PageDesc;
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
     * 根据节点ID获得节点定义
     * @param nodeId
     * @return
     */
	NodeInfo getNodeInfoById(long nodeId);
	
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
     * 获取新建流程的主键（流程代码）
     */
	String getNextPrimarykey();
	
	/**
	 * 生成一个阶段主键
	 * @return
	 */
	long getNextStageId();
	
	/**
	 * 根据已知的流程业务，查询对应的定义流程
	 * @param optId
	 * @return
	 */
	List<FlowInfo> getFlowsByOptId(String optId);
	
	/**
     * 获取某一流程某一版本号中存在的所有机构表达式
     * @param flowCode
     * @param version
     * @return
     */
    Set<String> getUnitExp(String flowCode, Long version);
   
}
