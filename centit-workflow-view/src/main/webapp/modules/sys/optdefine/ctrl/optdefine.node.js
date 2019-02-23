/**
 * Created by chen_rj on 2017-9-30.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');


    var OptDefineNode = Page.extend(function() {
        var _self = this;

        // @override
        this.load = function(panel) {
            var form = panel.find('form');

            Core.ajax(Config.ContextPath+'workflow/flow/define/create', {
                method: 'get'
            }).then(function(data) {
                data = _self.extendData(data);

                form.form('load', data)
                    .form('disableValidation')
                    .form('focus');
            });
        };

        // @override
        this.submit = function(panel, data, closeCallback) {
            var form = panel.find('form');

            form.form('enableValidation');
            var isValid = form.form('validate');

            if (isValid) {
                form.form('ajax', {
                    url: Config.ContextPath+'workflow/flow/define/'+data.flowCode,
                    method: 'put',
                    data: data
                }).then(closeCallback);
            }

            return false;
        };

        // @override
        this.onClose = function(table) {
            table.datagrid('reload');
        };

        // 扩展数据供form使用
        this.extendData = function(data) {
            this.data = $.extend({}, this.object, data, {
                flowCode: data.cid.flowCode,
                version: data.cid.version+""
            });

            return this.data;
        };
    });

    return OptDefineNode;
});
