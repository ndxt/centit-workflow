define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 流程图版本查看
	var VersionView = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			window.open(Config.ViewContextPath+'page/workflow/indexView.html?flowCode='
					 +data.flowCode+'&version='+data.version+'&contentPath='+window.ContextPath);
		};
	});
	
	return VersionView;
});