define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');

	// 重新运行
	var Again = Page.extend(function() {
		var _self=this;

		// @override
		this.submit = function(table, data) {
			$.messager.confirm("操作提示", "您确定要重新运行吗？", function(bo) {
				if (bo) {
					Core.ajax(
							Config.ContextPath
									+ 'workflow/flow/manager/resetToCurrent/'+ data.nodeInstId, {
								method : 'get'
							}).then(function(data) {
//						var index = table.datagrid('getSelectedRowIndex');
						table.datagrid('reload');
					});
				}
			});
		};

		this.renderButton = function (btn, data) {
			if(data.nodeState =='C' || this.parent.data.instState == 'F')
				return true;
			return false;
		};
	});

	return Again;
});
