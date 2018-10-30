define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');

	// 删除流程
	var FlowDefineDelete = Page.extend(function() {

		// @override
		this.submit = function(table, data,closeCallback) {
			var _self=this;
			Core.ajax(Config.ContextPath+'service/flow/define/deleteFlow/'+data.flowCode, {
				method: 'GET'
			}).then(function(data) {
				var table=_self.parent.table;
				table.datagrid('reload');
			});
			return false;

		};
	});

	return FlowDefineDelete;
});
