/**
 * Created by chen_rj on 2017/7/4.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    var AddTeam = require('./addteam');
    var DeleteTeam = require('./deleteteam');
    // 流程节点
    var TeamList = Page.extend(function() {
        var _self = this;


        this.injecte([
            new AddTeam('addteam'),
            new DeleteTeam('deleteteam')
        ]);
        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');
            form.form('load', data);
            Core.ajax(Config.ContextPath+'service/flow/manager/getteamlist/'+data.flowInstId, {
                method: 'get'
            }).then(function(data) {
                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data);
            });
        };
    });

    return TeamList;
});