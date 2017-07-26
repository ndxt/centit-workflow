define(function(require) {
	var Config = require('config');
	var Page = require('core/page');
	var Core = require('core/core');
	// 更改机构
	var AlignRelegate = Page.extend(function() {
		
		// @override
		this.load = function(panel, data) {
			var _selt=this;
			var form = panel.find('form');
			_selt.data=data;
			form.form('load', data)
				.form('disableValidation')
				.form('focus');
			
			
		};
		
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			
			form.form('enableValidation');
			
			var value=form.form('value');
			userCode=value.userCode;
			var isValid = form.form('validate');
			
			if (isValid) {
				form.form('ajax', {
					url: Config.ContextPath+'service/flow/useroptmgr/alignrelegate/'+data.relegateno+'/'+userCode,
					method: 'put',
				}).then(closeCallback);
			}
			
			return false;
		};
		
		// @override
		this.onClose = function(table) {
			table.datagrid('reload');
		};
		
		
	});
	
	return AlignRelegate;
});