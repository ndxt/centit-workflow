define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	var AddRoleUnits=require('../organize/addroleunits');
	var DelRoleUnits=require('../organize/delroleunits');
	// 编辑流程定义
	var OrgList = Page.extend(function() {
		var _self = this;
		
		this.injecte([
		              new AddRoleUnits('addroleunits'),
            new DelRoleUnits('delroleunits')
		              
	    ]);
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			form.form('load', data);
			Core.ajax(Config.ContextPath+'workflow/flow/manager/getorglist/'+data.flowInstId, {
				method: 'get'
			}).then(function(data) {
				table.cdatagrid({
					controller: _self
				})
				.datagrid('loadData', data);
				
			});
		};
		
		// 扩展数据供form使用
		this.extendData = function(data) {
			this.data = $.extend({}, this.object, data, {
				flowName: data.flowDefine.flowName
				
			});
			
			delete this.data.flowXmlDesc;
			
			return this.data;
		};
		
		
	});
	
	return OrgList;
});
