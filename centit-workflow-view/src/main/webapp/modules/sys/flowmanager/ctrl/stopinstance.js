/**
 * Created by chen_rj on 2017/7/3.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 挂起
    var StopInst = Page.extend(function() {

        // @override
        this.submit = function(table, data) {
            $.messager.confirm("操作提示", "您确定要强制停止流程吗？", function (bo) {

                if(bo){
                 Core.ajax(Config.ContextPath+'workflow/flow/manager/stopinst/'+data.flowInstId, {
                 method: 'get'
                 }).then(function(data) {
                 // var index=table.datagrid('getSelectedRowIndex');
                 // table.datagrid('updateRow',{'index':index,row:data});
                     table.datagrid("reload");
                 });
                 }

            });
        };

     this.renderButton = function (btn, data) {
            if(data.instState == 'N')
                return true;
            return false;
        };

    });

    return StopInst;
});
