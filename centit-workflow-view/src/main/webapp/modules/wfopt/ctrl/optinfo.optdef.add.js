define(function(require) {
	var Page = require('core/page');
    var Core = require('core/core');

	var OptDefAdd = Page.extend(function() {

		// @override
		this.object = {
			optReq : 'R'
		};

		// @override
		this.submit = function(panel, data) {
			var table = this.parent.panel.find('#optdef');

			if (!table.cdatagrid('endEdit')) {
				return;
			}

			// 插入新数据
			table.datagrid('appendRow', $.extend({}, this.object));

			var index = table.datagrid('getRows').length - 1;
			table.datagrid('selectRow', index);

			// 开启编辑
			table.cdatagrid('beginEdit', index, 'dataCode');

			//为optCode和optId赋值
            // Core.ajax(Config.ContextPath+'workflow/flow/opt/createOptDef?optId=' + ioptId, {
            Core.ajax(Config.ContextPath+'workflow/flow/opt/createOptDef?optId=' , {
                method: 'get'
            }).then(function(data) {

				var optCode = data.optCode;
				table.datagrid('updateRow',{
                    index: index,
                    row: {
                        optCode: optCode,
						optId: optId
                    }
                });
            });

		};
	});

	return OptDefAdd;
});
