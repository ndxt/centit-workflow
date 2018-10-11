define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 删除流程定义阶段删除
    var WfOptDelete = Page.extend(function () {

        // @override
        this.submit = function (table, row) {
            var index = table.datagrid('getRowIndex', row);
            table.datagrid('deleteRow', index);
            Core.ajax(Config.ContextPath + 'service/flow/opt/deleteOptInfoById?optId=' + row.optId, {
                method: 'get'
            }).then(function (data) {
                console.log(data)
            });
        };
    });

    return WfOptDelete;
});
