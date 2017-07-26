define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 流程图版本编辑
	var VersionEdit = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			var flowCode=data.flowCode;
			Core.ajax(Config.ContextPath+'service/flow/define/editfromthis/'+data.flowCode+'/'+data.version, {
				method: 'POST'
			}).then(function(data) {
				if(data)
					window.open(Config.ViewContextPath+'page/workflow/index.html?flowCode='
							+flowCode+'&contentPath='+window.ContextPath);
				else
					$.messager.alert('错误', '保存出错', 'error');
			});
			
		};
	});
	
	return VersionEdit;
});