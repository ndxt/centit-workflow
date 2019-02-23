define(function(require) {
	var Config = require('config');
	var Page = require('core/page');
	var Core = require('core/core');
	// 更改机构
	var ChangeUnit = Page.extend(function() {
		
		// @override
		this.load = function(panel, data) {
			var _selt=this;
			var form = panel.find('form');
			Core.ajax(Config.ContextPath+'workflow/flow/manager/'+data.flowInstId, {
				method: 'get'
			}).then(function(data) {
				this.data=data.flowInst;
				_selt.data=this.data;
				form.form('load', this.data)
					.form('disableValidation')
					.form('focus');
			});
		};
		
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			
			form.form('enableValidation');
			
			var value=form.form('value');
			unitCode=value.unitCode;
			var isValid = form.form('validate');
			
			if (isValid) {
				
				this.isSubmit = true;
				
				form.form('ajax', {
					url: Config.ContextPath+'workflow/flow/manager/changeunit/'+data.flowInstId+'/'+unitCode,
					method: 'put',
				}).then(closeCallback);
			}
			
			return false;
		};
		
		// @override
		this.onClose = function(table) {
			this.isSubmit && table.datagrid('reload');
			this.isSubmit = false;
		};
		
		
	});
	
	return ChangeUnit;
});
