define(function(require) {
	var Page = require('core/page');
	
	// 删除流程定义阶段删除
	var FlowDefineStageRemove = Page.extend(function() {
		
		// @override
		this.submit = function(table, row) {
			var index = table.datagrid('getRowIndex', row);
			table.datagrid('deleteRow', index);
		};
	});
	
	return FlowDefineStageRemove;
});