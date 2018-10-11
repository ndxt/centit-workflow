define(function(require) {
	var Config = require('config');
	var Core = require('core/core');
	var Page = require('core/page');
    var Utils = require('../../../custom/utils');
	
	var OptInfoPowerAdd = require('../ctrl/optinfo.optdef.add');
    var OptInfoPowerRemove = require('../ctrl/optinfo.optdef.remove');

	var OptInfoAll = Page.extend(function() {
		var _self = this;
		
		this.injecte([
              new OptInfoPowerAdd('optinfo_optdef_add'),
              new OptInfoPowerRemove('optinfo_optdef_remove'),
		]);

		// @override
		this.load = function(panel, data) {
			var form = panel.find('form');
			var table = this.table = panel.find('table');
			var table_optdef = panel.find('#optdef');

            var href = panel.panel('options').href;
            var optId2 = Utils.getUrlParam(href,"optId");
            optId = optId2;

			// Core.ajax(Config.ContextPath + 'system/optinfo/' + 'WFDEFINE', {
			Core.ajax(Config.ContextPath + 'service/flow/opt/' + optId2, {
				method: 'get'
			}).then(function(data) {
				_self.data = data;

				form.form('load', data);
				
				// 表格数据
				table_optdef.cdatagrid({
						controller: _self
					})
					.datagrid('loadData', data.wfOptDefs);
			});
		};
		
		// @override
		this.submit = function(panel, data, closeCallback) {
            // var href = panel.panel('options').href;
            // var optId2 = Utils.getUrlParam(href,"optId");

			var form = panel.find('form');
			var table_optdef = panel.find('#optdef');

			if (form.form('validate') && table_optdef.cdatagrid('endEdit')) {
				var formData = form.form('value');
				var optDefs = table_optdef.datagrid('getData').rows;

				$.extend(data, formData);
				data.optDefs = optDefs;
				data._method = 'PUT';

                $.ajax({
                    url: Config.ContextPath + 'service/flow/opt/saveOptDefs',
                    type: "POST",
                    data: JSON.stringify(data),
                    contentType: 'application/json',
                    success: function (data) {
                    	// debugger;
                    }
                });
			}
		};
	});
	
	return OptInfoAll;
});