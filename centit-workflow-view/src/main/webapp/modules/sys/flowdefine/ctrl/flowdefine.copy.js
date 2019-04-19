define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	var FlowDefineAdd = require('../ctrl/flowdefine.add');
	
	// 编辑流程定义
	var FlowDefineEdit = Page.extend(function() {
		var _self = this;
		
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var formData={oldFlowCode:data.flowCode,doCopyXML:"F"}
			Core.ajax(Config.ContextPath+'workflow/flow/define/copy/'+data.flowCode+"/"+data.version, {
				method: 'post'
			}).then(function(data) {
				//data = _self.extendData(data);
				
				form.form('load', data)
					.form('load',formData)
					.form('disableValidation')
					.form('focus');
			});
			
		};
	
		// @override
		this.submit = function(panel, data, closeCallback) {
						var form = panel.find('form');
		
						form.form('enableValidation');
						var isValid = form.form('validate');
						var value=form.form('value');
						if (isValid) {
							form.form('ajax', {
								url: Config.ContextPath+'workflow/flow/define/'+value.oldFlowCode+'/'+value.doCopyXML,
								method: 'post',
								data: value
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
