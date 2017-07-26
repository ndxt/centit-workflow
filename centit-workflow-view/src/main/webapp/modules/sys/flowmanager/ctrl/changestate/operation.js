define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var Operation = Page.extend(function() {
		
		// @override
		this.load = function(panel, data) {
			var url = Config.ContextPath+'service/flow/manager/listNodeOpers/'+data.nodeInstId;
			panel.find('#operation').cdatagrid({
				// 必须要加此项!!
				controller: this,
				url:url
			});
		};
		
		this.renderButton = function (btn, data) {
			if(data.nodeState =='N' && this.parent.data.instState != 'F' && this.parent.data.instState != 'C')
				return true;
			return false;
		};
	});
	
	return Operation;
});