/**
 * Created by chen_rj on 2017/7/4.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');
    var AddAtt = require('../flowattention/addatt');
    // 流程节点
    var AttList = Page.extend(function() {
        var _self = this;
        this.injecte([
            new AddAtt('addatt')
        ]);
        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            var table = this.table = panel.find('table');
            form.form('load', data);
            Core.ajax(Config.ContextPath+'workflow/flow/manager/getAttByFlowInstId/'+data.flowInstId, {
                method: 'get'
            }).then(function(data) {
                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data);
            });
        };
    });

    return AttList;
});
