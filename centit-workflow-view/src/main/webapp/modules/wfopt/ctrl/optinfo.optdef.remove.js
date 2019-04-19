define(function(require) {
	var Page = require('core/page');
    var Core = require('core/core');

	var OptInfoPowerRemove = Page.extend(function() {

		// @override
		this.submit = function(table, row) {

			var index = table.datagrid('getRowIndex', row);
			table.datagrid('deleteRow', index);
            Core.ajax(Config.ContextPath + 'workflow/flow/opt/deleteOptDefByCode?optCode=' + row.optCode, {
                method: 'get'
            }).then(function (data) {

                console.log(data)
            });
		};
	});

	return OptInfoPowerRemove;
});
