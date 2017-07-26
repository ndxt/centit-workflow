define(function(require){
	var Page = require('core/page');
	
	var NodeList=require('../ctrl/flowmanager.nodes');
	
	var NoOptNodes = Page.extend(function(){
		this.injecte([
		             	new NodeList('flowmanager_nodeList')
		             ]);
		
		
		this.load = function(panel){
			panel.find("table").cdatagrid({
				controller:this
			});
		};
		
	});
	return NoOptNodes;
});