/**
 * Created by chen_rj on 2018-5-22.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');
    // 工作流定义
    var UserTasks = Page.extend(function() {
        // @override
        this.load = function(panel) {
            panel.find('table').cdatagrid({
                // 必须要加此项!!
                controller: this
            });
        };

    });
    return UserTasks;
});
