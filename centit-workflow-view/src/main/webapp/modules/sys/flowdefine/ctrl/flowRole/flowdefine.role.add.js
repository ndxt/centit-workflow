/**
 * Created by chen_rj on 2018-4-25.
 */
define(function(require) {
    var Page = require('core/page');

    var RoleAdd = Page.extend(function() {

        // @override
        this.object = {
            isAccountTime: 'F',
            limitType: 'I',
            expireOpt: 'O'
        };

        // @override
        this.submit = function(panel, data) {
            var table = this.parent.table;

            if (!table.cdatagrid('endEdit')) {
                return;
            }

            // 插入新数据
            table.datagrid('appendRow', $.extend({}, this.object));

            var index = table.datagrid('getRows').length-1;
            table.datagrid('selectRow', index);

            // 开启编辑
            table.cdatagrid('beginEdit', index, 'stageCode');
        };
    });

    return RoleAdd;
});