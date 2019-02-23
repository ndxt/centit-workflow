define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var DeleteRelegate = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			$.messager.confirm("操作提示", "您确定要删除该委托吗？", function (bo) {
		if(bo){
			Core.ajax(Config.ContextPath+'workflow/flow/useroptmgr/relegate/'+data.relegateno, {
				method: 'delete'
			}).then(function(data) {
				table.datagrid('reload');
			});
		}	
			});
			
		};
	});
	
	return DeleteRelegate;
});
