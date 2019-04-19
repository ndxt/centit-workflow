/**
 * Created by chen_rj on 2017/7/4.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    var AddVariable = require('../flowvariable/addvariable');
    var EditVariable = require('../flowvariable/editvariable');
    // var DelVariable = require('../flowvariable/delvariable');
    // 流程节点
    var VariableList = Page.extend(function() {
        var _self = this;

        this.injecte([
            new AddVariable('addvariable'),
            new EditVariable('editvariable')
            // new DelVariable('delvariable')
        ]);
        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');
            form.form('load', data);
            Core.ajax(Config.ContextPath+'workflow/flow/manager/getvariablelist/'+data.flowInstId, {
                method: 'get'
            }).then(function(data) {
                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data);
            });
        };
    });

    return VariableList;
});
