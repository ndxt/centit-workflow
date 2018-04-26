define(function(require) {
    var Config = require('config');
    var Core = require('core/core');

    var FlowDefineAdd = require('../../ctrl/flowdefine.add');

    var FlowDefineVariableAdd = require('../flowVariable/flowdefine.variable.add');
    var FlowDefineVariableRemove = require('../flowVariable/flowdefine.variable.remove');

    // 编辑流程定义
    var FlowDefineVariable = FlowDefineAdd.extend(function() {
        var _self = this;

        this.injecte([
            new FlowDefineVariableAdd('flowdefine_variable_add'),
            new FlowDefineVariableRemove('flowdefine_variable_remove')
        ]);

        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');

            Core.ajax(Config.ContextPath+'service/flow/define/draft/'+data.flowCode, {
                method: 'get'
            }).then(function(data) {
                data = _self.extendData(data);

                form.form('load', data);

                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data.flowVariableDefines);
            });
        };

        // @override
        this.submit = function(panel, data, closeCallback) {
            var form = panel.find('form');
            var table = this.table;

            if (form.form('validate') && table.cdatagrid('endEdit')) {
                var items = table.datagrid('getData').rows;

                data.flowVariableDefines = items;
                data.flowVariableDefs = null;
                data._method = 'PUT';

                Core.ajax(Config.ContextPath+'service/flow/define/variableDefine/'+data.flowCode, {
                    data: data,
                    method: 'post'
                }).then(closeCallback);

                return false;
            }
        };
    });

    return FlowDefineVariable;
});