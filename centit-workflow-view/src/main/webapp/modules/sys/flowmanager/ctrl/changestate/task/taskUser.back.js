define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 删除数据字典
	var TskUserBack = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			Core.ajax(Config.ContextPath + 'workflow/flow/manager/disableTask/'+ data.taskId, {
            	type: 'json',
                method: 'post'
			}).then(function() {
				table.datagrid('reload');
            });
		};
	});
	
	return TskUserBack;
});
