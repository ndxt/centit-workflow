define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	
	var FlowDefineAdd = require('../ctrl/flowdefine.add');
	
	// 编辑流程定义,默认编辑草稿，即版本号为0
	var FlowDefineEdit = FlowDefineAdd.extend(function() {
		var _self = this;
		
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			
			Core.ajax(Config.ContextPath+'service/flow/define/draft/'+data.flowCode, {
				method: 'get'
			}).then(function(data) {
				data = _self.extendData(data);
				
				form.form('load', data)
					.form('disableValidation')
					.form('focus');
			});
		};
		
		
		// @override
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			
			form.form('enableValidation');
			var isValid = form.form('validate');
			
			if (isValid) {
				form.form('ajax', {
					url: Config.ContextPath+'service/flow/define/'+data.flowCode,
					method: 'put',
					data: data
				}).then(closeCallback);
			}
			
			return false;
		};
		// @override
		this.onClose = function(table) {
			table.datagrid('reload');
		};
	});
	
	return FlowDefineEdit;
});