define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 编辑流程定义
    var OptLogList = Page.extend(function() {
        var _self = this;

        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');
            form.form('load', data);
            Core.ajax(Config.ContextPath+'service/flow/manager/getOptLogList/'+data.flowInstId, {
                method: 'get'
            }).then(function(data) {
                //data=data;
                //data = _self.extendData(data);

                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data);
            });
        };




    });

    return OptLogList;
});