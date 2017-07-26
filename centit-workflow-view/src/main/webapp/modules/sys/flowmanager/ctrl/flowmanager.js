define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	// 查看流程图
	var FlowManagerView= require('../ctrl/flowmanager.view');
	var NodeList=require('../ctrl/flowmanager.nodes');
	var OrgList=require('../ctrl/organize/flowmanager.orgs');
	var LogList=require('../ctrl/flowmanager.logs');
	var ChangeUnit=require('../ctrl/changeunit');
    var StopInst=require('../ctrl/stopinstance');
    var ReStart=require('../ctrl/restart');
    var Pause=require('../ctrl/pause');
    var Wake=require('../ctrl/wake');
    var FlowVariable=require('../ctrl/flowvariable/flowmanager.variable');
    var FlowTeam=require('../ctrl/flowteam/flowmanager.team');
    var FlowAtt=require('../ctrl/flowattention/flowmanager.att');
    var NewFlowInst=require('../ctrl/newflowinst');
    var OptLogList=require('../ctrl/flowmanager.optlogs');
	// 工作流定义
	var flowmanager = Page.extend(function() {
		this.injecte([
		              new FlowManagerView('flowmanager_view'),
		              new NodeList('flowmanager_nodeList'),
		              new LogList('flowmanager_logList'),
		              new OrgList('flowmanager_orgList'),
		              new ChangeUnit('changeunit'),
                      new StopInst('stopinstance'),
                      new ReStart('restart'),
                      new Pause('pause'),
                      new Wake('wake'),
			          new FlowVariable('flowmanager_variableList'),
                      new FlowTeam('flowmanager_teamList'),
                      new FlowAtt('flowmanager_attList'),
                      new NewFlowInst('flowmanager_newFlowInst'),
			          new OptLogList('flowmanager_optlogList')
	    ]);
		
		// @override
		this.load = function(panel) {
			panel.find('table').cdatagrid({
				// 必须要加此项!!
				controller: this
			});
		};
	});
	
	return flowmanager;
});