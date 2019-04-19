define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var ReRun = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			if ('C'!=data.nodeState) {
				$.messager.alert('错误', '未完成不可重新运行', 'error');
				return;
			}
			else {
				
				$.messager.confirm("操作提示", "您确定要挂起操作吗？", function (bo) {
					if(bo){
			Core.ajax(Config.ContextPath+'workflow/flow/manager/nodestate/'+data.nodeInstId+'/7', {
				method: 'get'
			}).then(function(data) {
				var index=table.datagrid('getSelectedRowIndex');
				table.datagrid('updateRow',{'index':index,row:data});
			});
					}
				});
			}
			
		};
	});
	
	return ReRun;
});
