/**
 * Created by chen_rj on 2017/7/3.
 */
define(function (require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 挂起
    var AddRoleUnits = Page.extend(function () {
        var _self = this;

        this.submit = function(panel, data, closeCallback) {
            var form = panel.find('form');

            form.form('enableValidation');
            var isValid = form.form('validate');
            var value=form.form('value');
            var flowInstId=this.parent.data.flowInstId;
            var roleCode=value.roleCode;
            var unitCode=value.unitCode;
            var authDesc=value.authDesc;
            if (isValid) {
                form.form('ajax', {
                    url: Config.ContextPath + 'service/flow/manager/saveorg/' + flowInstId + '/'+ roleCode+ '/'+unitCode+'/'+authDesc,
                    method: 'post',
                }).then(closeCallback);
            }

            return false;
        };

        // @override
        this.onClose = function(table) {
            var flowInstId=this.parent.data.flowInstId;
            Core.ajax(Config.ContextPath+'service/flow/manager/getorglist/'+flowInstId, {
                method: 'get'
            }).then(function(data) {
                table.cdatagrid({
                    controller: _self
                })
                    .datagrid('loadData', data);
            });
        };
        // this.renderButton = function (btn, data) {
        //     if(data.instState == 'N')
        //         return true;
        //     return false;
        // };
    });
    return AddRoleUnits;
});
