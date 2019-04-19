define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var HangUp = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			if ('C'!=data.nodeState) {
				$.messager.alert('错误', '未完成不可创建游离', 'error');
				return;
			}
		
			/*Core.ajax(Config.ContextPath+'workflow/flow/manager/nodestate/'+data.nodeInstId+'/8', {
				method: 'get'
			}).then(function(data) {
				alert("节点已挂起");
			});
			*/
			
			
		};
	});
	
	return HangUp;
});
