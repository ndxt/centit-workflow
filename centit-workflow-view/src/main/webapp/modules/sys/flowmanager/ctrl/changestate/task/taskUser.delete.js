define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 删除数据字典
	var TaskUserDelete = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			Core.ajax(Config.ContextPath+ 'service/flow/manager/deleteTask/'+ data.taskId,{
            	type: 'json',
                method: 'post'
			}).then(function() {
				table.datagrid('reload');
            });
		};
	});
	
	return TaskUserDelete;
});