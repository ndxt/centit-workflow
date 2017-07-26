define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 绘制流程图
	var FlowManagerView = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
 			if(data.flowInstId==undefined){
				data.flowInstId =data.flowinstid;
 			}
			window.open(Config.ViewContextPath+'page/workflow/flowView.html?flowInstId='
					+data.flowInstId+'&contentPath='+window.ContextPath);
		};
	});
	
	return FlowManagerView;
})
