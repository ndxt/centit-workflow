define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 创建流程定义
    var WfOptAdd = Page.extend(function () {
        var _self = this;

        // @override
        this.load = function (panel) {

            var form = panel.find('form');

            Core.ajax(Config.ContextPath+'service/flow/opt/createOptInfo', {
                method: 'get'
            }).then(function(data) {
                // data = _self.extendData(data);

                form.form('load', data)
                    .form('disableValidation')
                    .form('focus');
            });
        };

        // @override
        this.submit = function (panel, data, closeCallback) {
            $.ajax({
                url: Config.ContextPath + 'service/flow/opt/saveOpt',
                type: "POST",
                data: JSON.stringify($("#optForm").serializeJson()),
                contentType: 'application/json',
                success: function (data) {
                }
            });
        };

        // @override
        this.onClose = function (table) {
            table.datagrid('reload');
        };
    });

    return WfOptAdd;
});