define(function(require) {
	var Config = require('config');
//	var Core = require('core/core');
	var Page = require('core/page');
	
	var TaskUser = require('../changestate/task/taskUser');
	var TaskUserBack = require('../changestate/task/taskUser.back');
	var TaskUserDelete = require('../changestate/task/taskUser.delete');
	// 发布版本
	var Task = Page.extend(function() {
		var _self = this;
		this.injecte([
			  		    new TaskUser('taskUser'),
			  		    new TaskUserBack('taskUser_back'),
			  		    new TaskUserDelete('taskUser_delete')
			  	    ]);
		
		// @override
		this.load = function(panel, data) {
			_self.data=data;
			var url = Config.ContextPath+'service/flow/manager/listusertasks/'+data.nodeInstId;
			panel.find('#usertask').cdatagrid({
				controller: this,
				url:url
			});
		};
		
		this.renderButton = function (btn, data) {
			if(data.nodeState =='N'&& this.parent.data.instState != 'F' && this.parent.data.instState != 'C')
				return true;
			return false;
		};
	});
	
	return Task;
});