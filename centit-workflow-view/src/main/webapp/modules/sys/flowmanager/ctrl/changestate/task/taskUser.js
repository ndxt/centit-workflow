define(function(require) {
	var Config = require('config');
	var Page = require('core/page');
	var Core = require('core/core');
	
	// 发布版本
	var TaskUser = Page.extend(function() {
		var _self = this;
		// @override
		this.load = function(panel,data) {
			_self.data = this.parent.data;
			var form = panel.find('form');
				
			form.form('disableValidation')
				.form('focus');
		};
		
		this.submit = function(panel, data,closeCallback) {
			var form = panel.find('form');
			
			// 开启校验
			form.form('enableValidation');
			var isValid = form.form('validate');
			if (isValid) {
				form.form('ajax', {
					url: Config.ContextPath	+ 'service/flow/manager/assign/'+ _self.data.nodeInstId,
					method: 'post',
					data: data 
				}).then(closeCallback);
			}
			return false;
		};
		
		this.onClose=function(table){
			table.datagrid('reload');
		}
	});
	
	return TaskUser;
});