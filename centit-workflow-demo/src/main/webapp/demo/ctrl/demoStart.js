/**
 * Created by chen_rj on 2018-4-27.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 工作流定义
    var DemoStart = Page.extend(function() {
        // @override
        this.load = function(panel){

            var form = panel.find("form");
            form.form('ajax', {
                url: Config.ContextPath+'workflow/demo/listAllFlow',
                method: 'get',
            }).then(function (data) {
                    $("#flowCode").combobox('loadData',data);
                }
            )
        }

        // @override
        this.submit = function(panel, data, closeCallback) {
            debugger;
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
    });

    return DemoStart;
});
