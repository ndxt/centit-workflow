define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');
    var Utils = require('../../../custom/utils');

    var WfOptEdit = Page.extend(function () {
        var _self = this;
        // @override
        this.load = function (panel, data) {

            var form = panel.find('form');
            var _self = this;
            Core.ajax(Config.ContextPath + 'service/flow/opt/getOptById?optId=' + data.optId, {
                method: 'get'
            }).then(function (data) {

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

    return WfOptEdit;
});