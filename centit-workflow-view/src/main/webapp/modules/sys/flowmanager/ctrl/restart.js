/**
 * Created by chen_rj on 2017/7/3.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 挂起
    var ReStart = Page.extend(function() {

        // @override
        this.submit = function(table, data) {
            $.messager.confirm("操作提示", "您确定要重启流程吗？", function (bo) {

                if(bo){
                    Core.ajax(Config.ContextPath+'service/flow/manager/resetToCurrent/'+data.flowInstId, {
                        method: 'get'
                    }).then(function(data) {
                        // var index=table.datagrid('getSelectedRowIndex');
                        // table.datagrid('updateRow',{'index':index,row:data});
                    });
                }

            });
        };

        this.renderButton = function (btn, data) {
            if(data.instState == 'C' || data.instState == 'F')
                return true;
            return false;
        };

    });

    return ReStart;
});