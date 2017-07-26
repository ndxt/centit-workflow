define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	
	// 强制游离
	var MakeFree = Page.extend(function() {
		
		// @override
		this.submit = function(table, data) {
			$.messager.confirm("操作提示", "您确定要强制游离吗？", function (bo) {
				if(bo){
					Core.ajax(Config.ContextPath+'service/flow/manager/nodestate/'+data.nodeInstId+'/3', {
						method: 'get'
					}).then(function(data) {
						var index=table.datagrid('getSelectedRowIndex');
						table.datagrid('updateRow',{'index':index,row:data});
					});
				}
			});
		};
		
		this.renderButton = function (btn, data) {
			if(data.nodeState =='N' && this.parent.data.instState != 'F' && this.parent.data.instState != 'C')
				return true;
			return false;
		};
	});
	
	return MakeFree;
});