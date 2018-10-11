define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    var WfOptAdd = require('../ctrl/wfOpt.add');
    var WfOptEdit = require('../ctrl/wfOpt.edit');
    var WfOptDelete = require('../ctrl/wfOpt.delete');
    // var WfOptDef = require('../ctrl/wfOptDef');
    var OptInfoAll = require('../ctrl/wfOptinfo.all');

    // 工作流定义
    var WfOpt = Page.extend(function () {
        this.injecte([
            new WfOptAdd('wfOpt_add'),
            new WfOptEdit('wfOpt_edit'),
            new WfOptDelete('wfOpt_delete'),
            // new WfOptDef('wfOpt_def'),
            new OptInfoAll('optinfo_all')
        ]);

        // @override
        this.load = function (panel) {
            var table = this.table = panel.find('table');
            table.cdatagrid({
                // 必须要加此项!!
                controller: this
            });
        };
    });

    return WfOpt;
});