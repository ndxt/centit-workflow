/**
 * Created by chen_rj on 2017/7/12.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    var newFlowInst = Page.extend(function() {
        var _self = this;
        this.load = function(panel){

            var form = panel.find("form");
          /*form.form('ajax', {
            url: Config.ContextPath+'workflow/flow/define?_search=true&field=flowCode&field=flowName',
            method: 'get',
          })*/
          Core.ajax(Config.ContextPath+'workflow/flow/define?_search=true&field=flowCode&field=flowName&rows=100', {
            method: 'get'
          }).then(function (data) {
                    $("#flowCode").combobox('loadData',data.objList);
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
                    url: Config.ContextPath+'workflow/flow/demo/createFlowInstance',
                    method: 'put',
                    data: data
                }).then(closeCallback);
            }

            return false;
        };
    });

    return newFlowInst;
});
