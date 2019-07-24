define(function(require) {
  var Config = require('config');
  var Cache = require('core/cache');
  var Page = require('core/page');

  // 绘制流程图
  var FlowDefineDraw = Page.extend(function() {

    // @override
    this.submit = function(table, data) {
      window.open(Config.ViewContextPath+'page/workflow/index.html?flowCode='+data.flowCode+'&version=0&contentPath='+window.ContextPath);
    };
  });

  return FlowDefineDraw;
});
