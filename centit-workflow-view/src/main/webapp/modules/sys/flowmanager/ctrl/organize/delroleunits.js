/**
 * Created by chen_rj on 2017/7/3.
 */
define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 挂起
    var DeleteRoleUnits = Page.extend(function () {
        var _self = this;
        this.submit = function(table,data) {
            var flowInstId=this.parent.data.flowInstId;
            var roleCode=data.roleCode;
            var unitCode=data.unitCode;
            Core.ajax(Config.ContextPath + 'workflow/flow/manager/deleteorg/' + flowInstId + '/' + roleCode + '/' + unitCode, {
                method: 'get'
            }).then(function() {
                Core.ajax(Config.ContextPath+'workflow/flow/manager/getorglist/'+flowInstId, {
                    method: 'get'
                }).then(function(data) {
                    table.datagrid('loadData', data);
                });
            })
        };
    });
    return DeleteRoleUnits;
});
