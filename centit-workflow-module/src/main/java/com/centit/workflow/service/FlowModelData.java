package com.centit.workflow.service;

import java.util.Map;
/**
 * 模型基础数据接口
 */
public interface FlowModelData {
	/**
	 * 例举所有节点操作类别
	 */
	Map<String,String> listAllOptType();
	/**
	 * 例举所有节点类别
	 */
	Map<String,String> listAllNoteType();

	/**
	 * 列举所有可用变量
	 
	Map<String,String> listAllVariable(String optid);
	*/
	/**
	 * 根据业务类型列举所有业务代码
	 */
	Map<String, String> listAllOptCode(String wfcode);
	/**
	 * 列举所有角色
	 */
	Map<String, Map<String, String>> listAllRole();
	/**
	 * 根据工号显示用户名
	 */
	String getUserNameByCode(String userCode);
	/**
	 * 根据机构代码显示机构名
	 */
	String getUnitNameByCode(String unitCode);
	/**
	 * 根据角色代码显示角色名
	 */
	String getRoleNameByCode(String roleCode);
	/*
	 * 根据角色返回符合条件的所有用户代码
	 */
	//List<String> getRightUsers(String unitCode,String roleCode,int instID);
	/**
	 * 列举所有的子流程
	 */
	Map<String, String> listAllSubFlow();
	
	/**
	 * 获取流程阶段信息
	 * @param flowCode 流程代码和名称对应表
	 * @return
	 */
	Map<String, String> listFlowStages(String flowCode);
	
}
