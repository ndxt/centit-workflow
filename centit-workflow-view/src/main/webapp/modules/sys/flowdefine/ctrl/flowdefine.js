define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 创建流程图定义
	var FlowDefineAdd = require('../ctrl/flowdefine.add');
	
	// 编辑流程图定义
	var FlowDefineEdit = require('../ctrl/flowdefine.edit');
	
	// 编辑流程图阶段
	var FlowDefineStage = require('../ctrl/flowdefine.stage');
	
	// 绘制流程图
	var FlowDefineDraw = require('../ctrl/flowdefine.draw');
	
	// 发布流程图
	var FlowDefineRelease = require('../ctrl/flowdefine.release');
	
	// 流程图版本
	var FlowDefineVersion = require('../ctrl/flowdefine.version');
	
	// 流程图复制
	var FlowDefineCopy = require('../ctrl/flowdefine.copy');
    
	// 工作流定义
	var FlowDefine = Page.extend(function() {
		this.injecte([
		    new FlowDefineAdd('flowdefine_add'),
		    new FlowDefineEdit('flowdefine_edit'),
		    new FlowDefineStage('flowdefine_stage'),
	        new FlowDefineDraw('flowdefine_draw'),
	        new FlowDefineRelease('flowdefine_release'),
	        new FlowDefineVersion('flowdefine_version'),
	        new FlowDefineCopy('flowdefine_copy')
	    ]);
		
		// @override
		this.load = function(panel) {
			var table=this.table=panel.find('table');
			table.cdatagrid({
				// 必须要加此项!!
				controller: this
			});
		};
	});
	
	return FlowDefine;
});