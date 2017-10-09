/**
 * Created by chen_rj on 2017-9-30.
 */
define(function(require) {
    var Config = require('config');
    var Core = require('core/core');
    var Page = require('core/page');

    // 业务节点
    var OptDefineNode = require('../ctrl/optdefine.node');

    // 业务环节
    var OptDefineStage = require('../ctrl/optdefine.stage');

    // 业务办件角色
    var OptDefineTeamRole = require('../ctrl/optdefine.teamrole');

    // 业务变量
    var OptDefineVariable = require('../ctrl/optdefine.variable');

    // 业务定义
    var OptDefine = Page.extend(function() {
        this.injecte([
            new OptDefineNode('optdefine_node'),
            new OptDefineTeamRole('optdefine_teamrole'),
            new OptDefineStage('optdefine_stage'),
            new OptDefineVariable('optdefine_variable')
        ]);

        // @override
        this.load = function(panel) {
            var table=this.table=panel.find('table');
            table.cdatagrid({
                // 必须要加此项!!
                controller: this
            });
        };
    });

    return OptDefine;
});