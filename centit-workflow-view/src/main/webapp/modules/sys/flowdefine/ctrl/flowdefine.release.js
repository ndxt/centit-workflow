define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var FlowDefineRelease = Page.extend(function() {
		
		this.renderButton = function (btn, data) {
		    return data.flowState!='E';
	    };
		// @override
		this.submit = function(table, data,closeCallback) {
			// TODO 发布的逻辑\
			var _self=this;
			Core.ajax(Config.ContextPath+'service/flow/define/publish/'+data.flowCode, {
				method: 'POST'
			}).then(function(data) {
				var table=_self.parent.table;
				table.datagrid('reload');
			});
			return false;
			
		};
	});
	
	return FlowDefineRelease;
});