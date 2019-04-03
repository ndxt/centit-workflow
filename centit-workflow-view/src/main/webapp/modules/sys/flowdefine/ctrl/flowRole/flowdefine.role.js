define(function(require) {
    var Config = require('config');
    var Core = require('core/core');

    var FlowDefineAdd = require('../../ctrl/flowdefine.add');

    var FlowDefineRoleAdd = require('../flowRole/flowdefine.role.add');
    var FlowDefineRoleRemove = require('../flowRole/flowdefine.role.remove');

    // 编辑流程定义
    var FlowDefineRole = FlowDefineAdd.extend(function() {
        var _self = this;

        this.injecte([
            new FlowDefineRoleAdd('flowdefine_role_add'),
            new FlowDefineRoleRemove('flowdefine_role_remove')
        ]);

        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');

            Core.ajax(Config.ContextPath+'workflow/flow/define/draft/'+data.flowCode, {
                method: 'get'
            }).then(function(data) {
                data = _self.extendData(data);

                form.form('load', data);

                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data.flowTeamRoles);
            });
        };

        // @override
        this.submit = function(panel, data, closeCallback) {
            var form = panel.find('form');
            var table = this.table;

            if (form.form('validate') && table.cdatagrid('endEdit')) {
                var items = table.datagrid('getData').rows;

                data.flowTeamRoles = items;
                data.flowRoles = null;
                //data._method = 'PUT';

                Core.ajax(Config.ContextPath+'workflow/flow/define/role/'+data.flowCode, {
                    data: data,
                    method: 'post'
                }).then(closeCallback);

                return false;
            }
        };
    });

    return FlowDefineRole;
});
