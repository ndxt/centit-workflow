define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	var AddRelegate = Page.extend(function() {
		
		// @override
		this.object = {
		    // 默认管理业务
			unitType: 'A',
			
		    // 默认启用
			isValid: 'T'
		};
		
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			
			form.form('load', $.extend({}, this.object, {
				parentUnit: (data ? data.unitCode : "0")
			}))
				.form('disableValidation')
				.form('focus');
		};
		
		// @override
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			
			form.form('enableValidation');
			var isValid = form.form('validate');
			
			if (isValid) {
				this.newObject = form.form('value');
			
				form.form('ajax', {
					url: Config.ContextPath + 'system/unitinfo',
					method: 'post'
				}).then(closeCallback);
			}
			
			return false;
		};
		
		// @override
		this.onClose = function(table, data) {
			var newObject = this.newObject;
			
			if (!newObject) return;
			
			// 展开节点
			if (data) {
				table.treegrid('expand', data.unitCode);
			}
			
			// 添加数据
			table.treegrid('append', {
				parent: newObject.parentUnit,
				data: [newObject]
			});
		};
	});
	
	return AddRelegate;
});