define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	
	var FlowDefineAdd = require('../ctrl/flowdefine.add');
	
	var FlowDefineStageAdd = require('../ctrl/flowdefine.stage.add');
	var FlowDefineStageRemove = require('../ctrl/flowdefine.stage.remove');
	
	// 编辑流程定义
	var FlowDefineStage = FlowDefineAdd.extend(function() {
		var _self = this;
		
		this.injecte([
		              new FlowDefineStageAdd('flowdefine_stage_add'),
		              new FlowDefineStageRemove('flowdefine_stage_remove')
		]);
		
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			
			Core.ajax(Config.ContextPath+'service/flow/define/draft/'+data.flowCode, {
				method: 'get'
			}).then(function(data) {
				data = _self.extendData(data);
				
				form.form('load', data);
				
				table.cdatagrid({
					controller: _self
				})
				.datagrid('loadData', data.flowStages);
			});
		};
		
		// @override
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			var table = this.table;
			
			if (form.form('validate') && table.cdatagrid('endEdit')) {
				var items = table.datagrid('getData').rows;
				
				data.flowStages = items;
				data._method = 'PUT';
				
				Core.ajax(Config.ContextPath+'service/flow/define/stage/'+data.flowCode, {
					data: data,
					method: 'post'
				}).then(closeCallback);
				
				return false;
			}
		};
	});
	
	return FlowDefineStage;
});