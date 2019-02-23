define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 发布版本
	var Nodelogs = Page.extend(function() {
		
		
		this.load = function(panel, data) {
			var _self=this;
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			form.form('load', data);
			Core.ajax(Config.ContextPath+'workflow/flow/manager/nodelogs/'+data.nodeInstId, {
				method: 'get'  
			}).then(function(data) {
				
				table.cdatagrid({
					controller: _self
				}).datagrid('loadData', data.objList);;
				
			});
		};
	});
	
	return Nodelogs;
});
