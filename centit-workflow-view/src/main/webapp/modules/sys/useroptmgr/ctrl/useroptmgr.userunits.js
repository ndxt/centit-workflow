define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');

	// 编辑流程定义
	var UserUnits = Page.extend(function() {
		var _self = this;

		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			form.form('load', data);
			Core.ajax(Config.ContextPath+'workflow/flow/useroptmgr/userunits/'+data.userCode, {
				method: 'get'
			}).then(function(data) {
				table.cdatagrid({
					controller: this
				})
				.datagrid('loadData', data.objList);
			});
		};

		// @override
		this.submit = function(panel, data, closeCallback) {
			var form = panel.find('form');
			var table = this.table;

			if (form.form('validate') && table.cdatagrid('endEdit')) {
				var items = table.datagrid('getData').rows;

				data.wfFlowStages = items;
				data._method = 'PUT';
				Core.ajax(Config.ContextPath+'workflow/flow/define/'+data.version+'/'+data.flowCode, {
					data: data,
					method: 'post'
				}).then(closeCallback);

				return false;
			}
		};


	});

	return UserUnits;
});
