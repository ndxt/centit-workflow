define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
	var UserUnits=require('../ctrl/useroptmgr.userunits');
	var DeleteRelegate=require('../ctrl/deleterelegate');
	var AlignRelegate=require('../ctrl/alignrelegate');
	var AddRelegate=require('../ctrl/addrelegate');
	// 编辑流程定义
	var UserOptMgr = Page.extend(function() {
		this.injecte([
		              new UserUnits('useroptmgr_userunits'),
		              new DeleteRelegate('deleteRelegate'),
		              new AlignRelegate('alignrelegate'),
		              new AddRelegate('addrelegate')
	    ]);
		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');

            //选择用户触发事件
            $("#userName").combobox({
                onSelect:function(rec){
                    loadSubTabPage(rec.userCode);
                    loadForm(rec.userCode);
                }

            })

			Core.ajax(Config.ContextPath+'workflow/flow/useroptmgr/loginuser', {
				method: 'get'
			}).then(function(data) {
				form.form("disableValidation");
				formdata=$.extend({}, this.object, data.userInfo,data.primaryUnit/*, {
					unitName: data.primaryUnit.unitCode,
					userStation:data.primaryUnit.userStation,
					userRank:data.primaryUnit.userRank

				}*/);
				form.form('load',formdata);
			});

			panel.find('table').cdatagrid({
				// 必须要加此项!!
				controller: this
			});
		};


	})
    //加载基本信息表单
	function loadForm(userCode){
        Core.ajax(Config.ContextPath+'workflow/flow/useroptmgr/'+userCode, {
            method: 'get'
        }).then(function(data) {
            formdata=$.extend({}, this.object, data.userInfo/*, {
			 unitName: data.primaryUnit.unitCode,
			 userStation:data.primaryUnit.userStation,
			 userRank:data.primaryUnit.userRank

			 }*/);
            $("#baseInfoForm").form('load',formdata);
        });
	}
	//加载标签页
	function loadSubTabPage(userCode){
        //待办
        $('#taskTable').datagrid("options").url = 'workflow/flow/useroptmgr/usertasks/'+userCode;
        $('#taskTable').datagrid('load');
        //已办
        $('#finTaskTable').datagrid("options").url = 'workflow/flow/useroptmgr/usertasksfin/'+userCode;
        $('#finTaskTable').datagrid('load');
        //接受的委托
        $('#relegateTableGet').datagrid("options").url = 'workflow/flow/useroptmgr/getrelegates/'+userCode;
        $('#relegateTableGet').datagrid('load');
        //设置的委托
        $('#relegateTableSet').datagrid("options").url = 'workflow/flow/useroptmgr/setrelegates/'+userCode;
        $('#relegateTableSet').datagrid('load');
        // //关注
        $('#attTable').datagrid("options").url = 'workflow/flow/useroptmgr/getAttentions/'+userCode+'/A';
        $('#attTable').datagrid('load');
    }


	return UserOptMgr;
});
