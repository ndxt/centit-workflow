define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	
	var Page = require('core/page');
	var Edit = require('../ctrl/flowdefine.version.edit');
	var View = require('../ctrl/flowdefine.version.view');
	
	// 流程定义版本
	var FlowDefineVersion = Page.extend(function() {
		var _self = this;
		
		this.injecte([
		    new Edit('flowdefine_version_edit'),
		    new View('flowdefine_version_view'),
	    ]);
		
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			form.form('load', data);
			var table = panel.find('#table_allversion');
			table.cdatagrid({
				controller: _self,
				url:Config.ContextPath+'service/flow/define/allversions/'+data.flowCode
			});
			
			
		};
	});
	
	return FlowDefineVersion;
});