/**
 * Created by chen_rj on 2018-4-27.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');
    // 编辑流程定义
    var DemoSubmit = Page.extend(function() {
        // @override
        this.load = function(panel, data) {
            var form = panel.find('form');
            panel.find('table').cdatagrid({
                // 必须要加此项!!
                controller: this
            });
        };
    })
    return DemoSubmit;
});
