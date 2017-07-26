/**
 * Created by chen_rj on 2017/7/3.
 */
define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 挂起
    var DeleteTeam = Page.extend(function () {
        var _self = this;
        this.submit = function(table,data) {
            var flowInstId=this.parent.data.flowInstId;
            var userRole=data.roleCode;
            var userCode=data.userCode;
            Core.ajax(Config.ContextPath + 'service/flow/manager/deleteteam/' + flowInstId + '/' + userRole + '/' + userCode, {
                method: 'get'
            }).then(function() {
                Core.ajax(Config.ContextPath+'service/flow/manager/getteamlist/'+flowInstId, {
                    method: 'get'
                }).then(function(data) {
                    table.datagrid('loadData', data);
                });
            })
        };

        // @override
        // this.onClose = function(table) {
        //     var flowInstId=this.parent.data.flowInstId;
        //     Core.ajax(Config.ContextPath+'service/flow/manager/getteamlist/'+flowInstId, {
        //         method: 'get'
        //     }).then(function(data) {
        //         table.cdatagrid({
        //             controller: _self
        //         })
        //             .datagrid('loadData', data);
        //     });
        // };
    });
    return DeleteTeam;
});
