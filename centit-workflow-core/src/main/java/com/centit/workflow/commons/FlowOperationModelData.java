package com.centit.workflow.commons;

import java.util.Map;

/**
 * 流程业务操作
 * 模型基础数据接口
 *
 * 这个数据模型用于对应的流程 编辑页面中的各种下拉框
 * Created by codefan on 17-9-11.
 * @author codefan
 */
@SuppressWarnings("unused")
public interface FlowOperationModelData {

	/**
	 * 根据业务类型列举所有业务代码
	 */
	Map<String, String> optMethods();
	/**
	 * 业务流程对应的所有办件角色
	 */
	Map<String, String> teamRoles();

	/**
	 * 业务中所有的流程变量
	 */
	Map<String, String> flowVariables();
	
	/**
	 * 业务对应的流程阶段信息
	 * @return
	 */
	Map<String, String> flowStages();
	
}
