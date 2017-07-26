define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	var HangUp = require('../ctrl/changestate/hangup');
	var WakeUp =require('../ctrl/changestate/wakeup');
	var Submit = require('../ctrl/changestate/submit');
	var MakeFree = require('../ctrl/changestate/makefree');
	var StopFree = require('../ctrl/changestate/stopfree');
	var Back = require('../ctrl/changestate/back');
	var Operation = require('../ctrl/changestate/operation');
	var Again = require('../ctrl/changestate/again');
	var Task = require('../ctrl/changestate/task');
	var NodeLogs = require('../ctrl/changestate/nodelogs')
	// 流程节点
	var NodeList = Page.extend(function() {
		var _self = this;
		this.injecte([
		  		    new HangUp('hangup'),
		  		    new WakeUp('wakeup'),
		  		    new Submit('submit'),
		  		    new MakeFree('makefree'),
		  		    new StopFree('stopfree'),
		  		    new Back('back'),
		  		    new Operation('operation'),
		  		    new Task('task'),
		  		    new Again('again'),
		  		    new NodeLogs('nodelogs')
		  	    ]);
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			form.form('load', data);
			Core.ajax(Config.ContextPath+'service/flow/manager/'+data.flowInstId, {
				method: 'get'  
			}).then(function(data) {
				data=data.flowInst;
				//data = _self.extendData(data);
				
				
				
				table.cdatagrid({
					controller: _self
				})
				.datagrid('loadData', data.nodeInstances);
			});
		};
        // @override
        this.onClose = function(table) {
            table.datagrid('reload');
        };
	});

	return NodeList;
});