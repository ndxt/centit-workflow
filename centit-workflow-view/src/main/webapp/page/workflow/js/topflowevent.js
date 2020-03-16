/**
 * Created by gyr on 2015-4-10.
 */
/**
 * 绑定节点属性属性工具栏
 * @param obj 目标对象
 */
function bindAttr(obj) {
    var i = 0, k;
    if (!$("#argumentTool").is(":visible")) {
        $("#argumentTool").show();
    }
    $("#route-type,.insideDialog").hide();//隐藏路由类别
    $('.insideDialog .content input:checked').prop('checked', false)
    // $("#iosid").show();
    $("#flow-phase").show();//显示流程节点阶段
    $("#is-trunkLine").show();//显示是否主干节点
    //！--公共属性开始
    g("nodename").value = SVG.get(o).attr("title");//节点名称赋值
    //节点类型
    switch (SVG.get(o).attr("nodetype")) {
        case "A":
            $("#argumentTitle").html("开始节点属性") ;
            break;
        case "B":
        case "C":
            $("#argumentTitle").html("业务节点属性") ;
            break;
        case "F":
            $("#argumentTitle").html("结束节点属性") ;
            break;
        case "R":
            $("#argumentTitle").html("路由节点属性") ;
            break;
        default :
            break;
    }
    g("nodecode").value = SVG.get(o).attr("nodecode");//环节代码
    for (i = 0; i < g("isTrunkLine").options.length; i++) {//是否主干节点
        if (g("isTrunkLine").options[i].value == SVG.get(o).attr("isTrunkLine")) {
            g("isTrunkLine").options[i].selected = "selected";
        }
    }

    // 操作类别
    $('#opttype').empty();//清空select
    for (k in Data.OptType) {//重新添加option
      $("#opttype").append("<option  value='" + k + "' >" + Data.OptType[k] + "</option>");
    }

    // 流程节点阶段
    $('#flowphase').empty();//清空select
    for (k in Data.FlowPhase) {//重新添加option
        if (SVG.get(o).attr("flowphase") == k) {//节点阶段
            $("#flowphase").append("<option  value='" + k + "' selected='selected'>" + Data.FlowPhase[k] + "</option>");
        }
        else {
            $("#flowphase").append("<option  value='" + k + "' >" + Data.FlowPhase[k] + "</option>");
        }
    }

    // 机构表达式
    $('.insideUnit').empty();//清空select
    $(".insideUnit").append("<option selected value='' style='display:none;'></option>")
    for (k in Data.InsideUnit) {//重新添加option
      $(".insideUnit").append("<option  value='" + k + "' >" + Data.InsideUnit[k] + "</option>");
    }
    // g("osid").value = SVG.get(o).attr("osid");//osid

    g("nodedesc").value = SVG.get(o).attr("nodedesc");//节点描述
    //--公共属性结束
    if (SVG.get(o).attr("nodetype") == 'C' || SVG.get(o).attr("nodetype") == 'B') {
        $("#argumentTool").find("li").show();//显示所有tab页
        $("#special").hide();//隐藏特殊属性tab页
        $("#constraint").hide();//隐藏目标节点时间约束tab页
        $("li.active").removeClass("active");//移除当前选中
        $("#common").addClass("active");//默认选中公共属性tab页
        $("table").hide();
        $("#commonAttr").show();//默认显示公共属性栏
        //!--操作相关属性开始
        for (i = 0; i < g("opttype").options.length; i++) {
            if (g("opttype").options[i].value == SVG.get(o).attr("opttype")) {
                g("opttype").options[i].selected = "selected";
                //由操作类型引起的判断
                if (SVG.get(o).attr("opttype") == "D") {//自动流程节点
                    $("#business").hide();//隐藏业务操作
                    $("#childNode").hide();//隐藏子流程
                }
                else if (SVG.get(o).attr("opttype") == "S") {//子流程节点
                    $("#business").hide();//隐藏业务操作
                    $("#childNode").show();//显示子流程
                    $("#subwfcode").empty();//清空
                    for (k in Data.SubWfcode) {
                        if (SVG.get(o).attr("subwfcode") == k) {
                            $("#subwfcode").append("<option  value='" + k + "' selected='selected'>" + Data.SubWfcode[k] + "</option>");
                        }
                        else {
                            $("#subwfcode").append("<option  value='" + k + "' >" + Data.SubWfcode[k] + "</option>");
                        }
                    }
                    SVG.get(o).attr({"subwfcode":$("#subwfcode").val()});
                }
                else {//正常的时候
                    $("#business").show();//显示业务操作
                    $("#optcode").empty();//清空
                    for (k in Data.OptCode) {
                      if (k !== '') {
                        if (SVG.get(o).attr("optcode") == k) {
                          $("#optcode").append("<option  value='" + k + "' selected='selected'>" + Data.OptCode[k] + "</option>");
                        }
                        else {
                          $("#optcode").append("<option  value='" + k + "' >" + Data.OptCode[k] + "</option>");
                        }
                      }
                    }
                    SVG.get(o).attr({"optcode":$("#optcode").val()});
                    $("#childNode").hide();//隐藏子流程
                }
            }
        }
        g("optparam").value = SVG.get(o).attr("optparam");//操作参数
        g("optbean").value = SVG.get(o).attr("optbean");//业务注入
//        g("riskinfo").value = SVG.get(o).attr("riskinfo");//风险信息
        //--操作相关属性结束
        //!--权限相关属性开始
        g("unitexp").value = SVG.get(o).attr("unitexp");//机构表达式
        for (i = 0; i < g("roletype").options.length; i++) {//角色类别
            if (g("roletype").options[i].value == SVG.get(o).attr("roletype")) {
                g("roletype").options[i].selected = "selected";
            }
        }
        SVG.get(o).attr({"roletype":$("#roletype").val()});
        //根据角色类别进行判断
        if (SVG.get(o).attr("roletype") == "EN") {//权限引擎
            $("#roleName").hide();//隐藏角色代码
            $("#powerName").show();//显示权限表达式
            g("powerexp").value = SVG.get(o).attr("powerexp");//权限表达式
        }
        else {
            $("#powerName").hide();//隐藏权限表达式
            $("#roleName").show();//显示角色代码
            $("#rolecode").empty();
            var key = SVG.get(o).attr("roletype")

            for (k in Data[key]) {
                if (SVG.get(o).attr("rolecode") == k) {
                    $("#rolecode").append("<option  value='" + k + "' selected='selected' >" + Data[key][k] + "</option>");
                }
                else {
                    $("#rolecode").append("<option  value='" + k + "'>" + Data[key][k] + "</option>");
                }
            }
            SVG.get(o).attr({"rolecode":$("#rolecode").val()});
        }
        //--权限相关属性结束
        //！--时间约束属性开始
        for (i = 0; i < g("isaccounttime").options.length; i++) {//是否计入执行时间
            if (g("isaccounttime").options[i].value == SVG.get(o).attr("isaccounttime")) {
                g("isaccounttime").options[i].selected = "selected";
            }
        }
        for (i = 0; i < g("timeLimitType").options.length; i++) {//期限类别
            if (g("timeLimitType").options[i].value == SVG.get(o).attr("timeLimitType")) {
                g("timeLimitType").options[i].selected = "selected";
            }
        }
        g("timelimit").value = SVG.get(o).attr("timeLimit");//期限设定
        for (i = 0; i < g("inheritType").options.length; i++) {//期限继承类别
            if (g("inheritType").options[i].value == SVG.get(o).attr("inheritType")) {
                g("inheritType").options[i].selected = "selected";
            }
            if (SVG.get(o).attr("inheritType") == '2') {
                $("#inheritCode").show();//显示继承环节代码
                g("inheritNodeCode").value = SVG.get(o).attr("inheritNodeCode");//继承环节代码
            }
            else {
                $("#inheritCode").hide();//隐藏继承环节代码
            }
        }
        for (i = 0; i < g("warningrule").options.length; i++) {//预警规则
            if (g("warningrule").options[i].value == SVG.get(o).attr("warningrule")) {
                g("warningrule").options[i].selected = "selected";
            }
        }
        g("warningparam").value = SVG.get(o).attr("warningparam");//预警时间参数
        //--时间约束属性结束
    }
    else
    {
        $("#argumentTool").find("li.active").removeClass("active");//清除选中
        $("#argumentTool").find("li").hide();
        $("#common").addClass("active").show();//选中公共属性栏
        $("table").hide();
        $("#commonAttr").show();//默认显示公共属性栏
        if(SVG.get(o).attr("nodetype")=="R"){
            $("#route-type").show();//显示路由类别
            // $("#iosid").hide();
        }
        for (i = 0; i < g("routertype").options.length; i++) {//路由类别
            if (g("routertype").options[i].value == SVG.get(o).attr("routertype")) {
                g("routertype").options[i].selected = "selected";
            }
        }
        if (SVG.get(o).attr("shapetype") == "oval-multi" || SVG.get(o).attr("shapetype") == "oval-ju") {
            $("#special").show();//显示特殊属性栏
            if (SVG.get(o).attr("shapetype") == "oval-multi") {
                $(".ju-special").hide();//隐藏汇聚节点属性
                $(".multi-special").show();//显示多实例节点属性
                for (i = 0; i < g("multiinsttype").options.length; i++) {//多实例类别
                    if (g("multiinsttype").options[i].value == SVG.get(o).attr("multiinsttype")) {
                        g("multiinsttype").options[i].selected = "selected";
                    }
                }
                g("route-unitexp").value = SVG.get(o).attr("unitexp");//机构表达式
                //根据角色类别进行判断
                if (SVG.get(o).attr("roletype") == "EN") {//权限引擎
                    $("#route-roleName").hide();//隐藏角色代码
                    $("#route-powerName").show();//显示权限表达式
                    g("route-powerexp").value = SVG.get(o).attr("powerexp");//权限表达式
                }
                else {
                    $("#route-powerName").hide();//隐藏权限表达式
                    $("#route-roleName").show();//显示角色代码
                    $("#route-rolecode").empty();
                    for (k in Data[SVG.get(o).attr("roletype")]) {
                        if (SVG.get(o).attr("rolecode") == k) {
                            $("#route-rolecode").append("<option  value='" + k + "' selected='selected' >" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
                        }
                        else {
                            $("#route-rolecode").append("<option  value='" + k + "'>" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
                        }
                    }
                    SVG.get(o).attr({"roletype":$("#route-roletype").val()});
                    SVG.get(o).attr({"rolecode":$("#route-rolecode").val()});
                }
            }
            else {
                $(".multi-special").hide();//隐藏多实例节点属性
                $(".ju-special").show();//显示汇聚节点属性
                for (i = 0; i < g("convergetype").options.length; i++) {//汇聚条件类别
                    if (g("convergetype").options[i].value == SVG.get(o).attr("convergetype")) {
                        g("convergetype").options[i].selected = "selected";
                    }
                }
                SVG.get(o).attr({"convergetype":$("#convergetype").val()});
                g("convergeparam").value = SVG.get(o).attr("convergeparam");//汇聚参数
                g("route-optbean").value = SVG.get(o).attr("optbean");//汇聚外埠判断bean
            }
        }
        else {
            $("#special").hide();//隐藏特殊属性栏
            //开始和结束节点
            if (SVG.get(o).attr("nodetype") == 'A' || SVG.get(o).attr("nodetype") == 'F') {
                $("#flow-phase").hide();//隐藏流程节点阶段
                $("#is-trunkLine").hide();//隐藏是否主干节点
            }
        }
    }
}
/**
 *绑定线属性属性工具栏
 *@obj param
 */
function bindLineAttr(obj) {
	$("#argumentTitle").html("流转对象属性") ;
	if (!$("#argumentTool").is(":visible")) {//如果工具栏处于隐藏则显示
		$("#argumentTool").show();
	}
	$("#argumentTool li.active").removeClass("active");//清除选中
	$("#argumentTool li").hide();//隐藏其他tab页
	$("#common").show().addClass("active");//显示公共属性tab页
	$("#constraint").show();//显示目标节点时间约束tab页
	$("table").hide();//隐藏其他属性栏
	$("#lineAttr").show();//显示线属性栏
	g("line-name").value = SVG.get(o).attr("title");
	g("cond").value = SVG.get(o).attr("cond");//条件
	for (i = 0; i < g("line-timeLimitType").options.length; i++) {//期限类别
		if (g("line-timeLimitType").options[i].value == SVG.get(o).attr(
				"timeLimitType")) {
			g("line-timeLimitType").options[i].selected = "selected";
		}
	}
	g("line-timeLimit").value = SVG.get(o).attr("timeLimit");//期限设定
	for (i = 0; i < g("line-inheritType").options.length; i++) {//继承期限类别
		if (g("line-inheritType").options[i].value == SVG.get(o).attr(
				"inheritType")) {
			g("line-inheritType").options[i].selected = "selected";
		}
		if (SVG.get(o).attr("inheritType") == "2") {
			$("#line-inheritCode").show();//显示继承环节代码
			g("line-inheritNodeCode").value = SVG.get(o)
					.attr("inheritNodeCode");//继承环节代码
		} else {
			$("#line-inheritCode").hide();//隐藏继承环节代码
		}
	}
	for (i = 0; i < g("line-isaccounttime").options.length; i++) {//是否计时
		if (g("line-isaccounttime").options[i].value == SVG.get(o).attr(
				"isaccounttime")) {
			g("line-isaccounttime").options[i].selected = "selected";
		}
	}
	for (i = 0; i < g("canignore").options.length; i++) {//是否忽略运行
		if (g("canignore").options[i].value == SVG.get(o).attr("canignore")) {
			g("canignore").options[i].selected = "selected";
		}
	}
	g("line-desc").value = SVG.get(o).attr("desc");//说明
}
/**
 * 与SVG交互，事件绑定初始化操作
 * @param event
 * @returns {boolean}
 */
function initEvt(event) {
	var e = window.event || event, target = e.srcElement || e.target;
	if ($(target).closest("#tool").length > 0) {//定位到操作工具栏直接返回
		return;
	}
	if (target.nodeName == "text") {//如果定位到图形上的文本，修改目标对象为其所在的形状
		target = g(target.getAttribute("shapeID"));
	}
	if (target.nodeName == "path") {//如果定位到图形内部的path路径，修改目标对象为其所在的形状
		target = g(target.getAttribute("shapeID"));
	}
	//如果点击对象为SVG本身，或者既不是svg元素也不是线条名称，直接返回
	if (($(target).closest("svg").length < 1 && target.className != "step")
			|| target.nodeName == "svg") {
		return;
	}
	if (g(o) || selectMoreArry.length != 0) {//如果上一个对象处于选中状态，则清除选中
        if(g(o)) {
            SVG.get(o).stroke({color: "#00f"});
        }
        if(target.nodeName != 'ellipse'){
            $('#s4').html('');
        }
	}
	o = target.id;
    if(selectMoreArry.indexOf(o) == -1){
        selectMoreArry.length = 0;
        selectPointsArry.length = 0;
        clearSelected();
        if(image == "selectMore"){
            selectMoreArry.push(o);
        }
    }
    if(image == "default"){
        clearSelected();
    }
	if (target.className == "step") {//定位到线条名称，修改Id目标为线ID
		o = target.id.replace("lab", "");
		moveTip(target, target);
	}
	SVG.get(o).stroke({
		color : "#18b217"
	});//当前点击对象设为选中
    if (g(o).nodeName != "polyline") {
        SVG.get(o).attr("filter", "url(#filter-rect)");
    }
	if (g(o).nodeName == "polyline") {//如果选中对象为线，需变箭头颜色，同时将线条名称也选中
		SVG.get(o).attr({
			"marker-end" : "url(#" + markerGreen.attr("id") + ")"
		});
		if(!g("lab" + o)){
            return;
        }
		g("lab" + o).style.background = "#eee";
	}
	if (SVG.get(o).attr("shapetype") == "polyline") {
		bindLineAttr(g(o));//绑定线属性
		addPointMove(g(o));//拖动线
	} else {
		bindAttr(g(o));//绑定节点属性
        drag(g(o));//拖动节点
	}
}
/**
 * 画矩形
 * @param event
 * @returns {*}
 */
function   drawRect(event) {
	var e = window.event || event, target = e.srcElement || e.target, initP, rect, isMove = 0;
	if(isbrowser()=="IE"){//IE
        if (e.button != 1) {//如果不是鼠标左键则返回
            return;
        }
    }
    else{
        if (e.button != 0) {//如果不是鼠标左键则返回
            return;
        }
    }
	function rectMove(event) {
	    if(image == "selectMore") {
            var e = window.event || event;
            isMove = 1;
            var moveP = [(e.clientX + scrollElement.scrollLeft) / _ZOOM,
                (e.clientY + scrollElement.scrollTop) / _ZOOM];
            var deltaX = parseInt(moveP[0] - initP[0]);
            var deltaY = parseInt(moveP[1] - initP[1]);
            if(deltaX>0 && deltaY>0) {
                rect.attr({
                    "width": deltaX
                });
                rect.attr({
                    "height": deltaY
                });
            }
        }
        if(image != "selectMore") {
            rect.attr({
                "width": 40
            });
            rect.attr({
                "height": 40
            });
        }
	}

	 function rectStop(event) {
         //属性栏影藏
        $("#argumentTool").css("display","none");
	 	var e = window.event || event;
	 	//鼠标弹起时移除事件
		removeEvent(document, "mouseup", rectStop);
	 	removeEvent(document, "mousemove", rectMove);
	 	if (isMove == 0 && image != "selectMore") {//如果没有移动，设置默认宽高
	 		rect.attr({
	 			"width" : 40
			});
	 		rect.attr({
	 			"height" : 40
	 		});
	 	}
	 	if(image == "rect") {
            isMove = 0;//置为未移动
            if (parseInt(rect.attr("width")) < 2) {//宽度小于不创建图形
                rect.remove();
                return false;
            }
            var text = gText.text("过程").x(rect.x()).y(
                rect.y() + parseInt(rect.attr("height")) + 20).attr({
                dx: rect.x() + parseInt(rect.attr("width")) / 2,
                "title": "过程",
                "textWeight": "9pt",
                "strokeWeight": "1",
                "zIndex": "1",
                "shapeID": rect.attr("id")
            }).font({
                size: 14,
                "text-anchor": "middle"
            }).fill("#00f");
            rect.attr({
                "textID": text.attr("id"),
                "title": "过程",
                // "filter": "url(#filter-rect)"
            });
        }
         //多选画圆 找到在圆中的所有节点
         if(image == "selectMore"){
             //找到节点和线的节点,清除所有选中
             clearSelected();
             findPyNode(rect);
             rect.remove();
         }
	 	if ((e.clientY + scrollElement.scrollTop + 20) > scrollElement.scrollHeight) {
	 		$("#canvas").height(scrollElement.scrollHeight + 20 * _ZOOM);
	 	}
	 	if (e.stopPropagation) {//阻止冒泡
	 		e.stopPropagation();
	 	} else {
	 		return false;
	 	}
         changeSaveStatus('save');
	 }

	function rectDisplay() {
		if ((image == "rect" || image == "selectMore")  && target.nodeName == "svg") {
			initP = [ (e.clientX + scrollElement.scrollLeft) / _ZOOM,
					(e.clientY + scrollElement.scrollTop) / _ZOOM ];//按下时坐标
			//赋值默认属性
            if(image == "selectMore") {
                rect = gShape.rect(0, 0).attr('opacity',0.4).stroke({
                    color: "#0000ff",
                    width: 1
                }).fill("#fff").x(initP[0]).y(initP[1]);
            }
            else{
                rect = gShape.image(serviceImg, 0, 0).attr({
                    rx: 2,
                    ry: 2,
                    "shapetype": "roundRect",
                    "flowphase": "",
                    "nodedesc": "",
                    "osid": "",
                    "nodetype": "C",
                    "nodecode": "",
                    "opttype": "A",
                    "optcode": "",
                    "optbean": "",
                    "optparam": "",
                    "subwfcode": "",
                    "roletype": "",
                    "rolecode": "",
                    "isaccounttime": "F",
                    "timeLimitType": "I",
                    "inheritType": "0",
                    "inheritNodeCode": "",
                    "timeLimit": "",
                    "isTrunkLine": "F",
                    "unitexp": "D(P)",
                    "powerexp": "",
                    "expireopt": "",
                    "riskinfo": "",
                    "warningrule": "R",
                    "warningparam": ""
                }).stroke({
                    color: "#0000ff",
                    width: 1
                }).fill("#fff").x(fixXY(initP[0])).y(fixXY(initP[1]));
            }
			addEvent(document, "mousemove", rectMove);//鼠标按下移动时动作
            addEvent(document, "mouseup", rectStop);//鼠标弹起时动作
            //撤回数组入队
            if(image != "selectMore") {
                unreShiftDel(rect,'rect',undoAction);
                redoAction.length = 0;
            }
		}
	}

	return rectDisplay();
}
/**
 * 画线
 * @param event
 * @returns {*}
 */
function drawLine(event) {
	//target为线的起始节点，targetE为线的终止节点
	var e = window.event || event, target = e.srcElement || e.target, pL1, pL2, p1;
	if(isbrowser()=="IE"){//IE
        if (e.button != 1) {//如果不是鼠标左键则返回
            return;
        }
    }
    else{
        if (e.button != 0) {//如果不是鼠标左键则返回
            return;
        }
    }
	if (target.nodeName == "text") {//如果定位到文本，则目标对象为其所在的图形
		target = g(target.getAttribute("shapeID"));
	}
	if (target.nodeName == "path") {//如果定位到path，则目标对象为其所在的图形
		target = g(target.getAttribute("shapeID"));
	}
	function lineMove(event) {
		var e = window.event || event;
		pL2 = [ (e.clientX + scrollElement.scrollLeft) / _ZOOM,
				(e.clientY + scrollElement.scrollTop) / _ZOOM ];
		pl.attr({
			points : pL1[0] + "," + pL1[1] + " " + pL2[0] + "," + pL2[1]
		});//移动过程中不断变换坐标
	}

	function lineStop(event) {
		var e = window.event || event, targetE = e.srcElement || e.target;
		if (targetE.nodeName == "text"||targetE.nodeName == "path") {//如果定位到文本，则目标对象为其所在的图形
			targetE = g(targetE.getAttribute("shapeID"));
		}
		//鼠标弹起时移除绑定事件
		removeEvent(document, "mouseup", lineStop);
		removeEvent(document, "mousemove", lineMove);
		if (target.nodeName != "image" && target.nodeName != "rect" && target.nodeName != "ellipse") {
			alert("错误原因：\n\n起点必须是过程");
			pl.remove();
			pl = null;
			return false;
		}
		if (targetE.nodeName != "image" && targetE.nodeName != "rect" && targetE.nodeName != "ellipse") {
			alert("错误原因：\n\n末点必须是过程");
			pl.remove();
			pl = null;
			return false;
		}
		if (targetE.getAttribute("id") == "begin") {
			alert("错误原因：\n\n开始过程不可以作为回流过程！");
			pl.remove();
			pl = null;
			return false;
		}
		if (target.getAttribute("id") == "begin" && target.getAttribute("from")) {
			alert("错误原因：\n\n此过程已经作为一次起始过程连线！！");
			pl.remove();
			pl = null;
			return false;
		}
		if (target.getAttribute("id") == "end") {
			alert("错误原因：\n\n结束过程不可以作为起始过程！");
			pl.remove();
			pl = null;
			return false;
		}
		if (targetE.id == target.id) {
			alert("错误原因：\n\n起末不能是同一个过程！");
			pl.remove();
			pl = null;
			return false;
		}
		if(target.nodeName=='rect'&&target.getAttribute("from")){
            alert("错误原因：\n\n业务节点只能有一条出度！");
            pl.remove();
            pl=null;
            return false;
        }
		if(target.getAttribute("routertype")=='G'){//多实例节点
            if(target.getAttribute("from")){
                alert("错误原因：\n\n多实例节点只能有一条出度！");
                pl.remove();
                pl=null;
                return false;
            }
            if(targetE.getAttribute("nodetype")!='C'){
                alert("错误原因：\n\n多实例节点只能指向业务节点！");
                pl.remove();
                pl=null;
                return false;
            }
        }
		if (SVG.get(target.id).attr("from")) {//起始节点已经存在出去线，则再加上一条
            if(SVG.get(target.id).attr("nodetype") == 'C'){
                alert('业务节点只能存在一条出线， 请删除上一条线来继续操作')
                pl.remove();
                pl=null;
                return false;
            }else if (SVG.get(target.id).attr("nodetype") == 'R'){
              SVG.get(target.id).attr({
                "from" : SVG.get(target.id).attr("from") + ','+pl.attr("id")
              });
            }
		} else {//不存在则设置
			SVG.get(target.id).attr({
				"from" : pl.attr("id")
			});
		}
		if (SVG.get(targetE.id).attr("to")) {//终止节点存在进入线，则再加上一条
			SVG.get(targetE.id).attr({
				"to" : SVG.get(targetE.id).attr("to") + ',' + pl.attr("id")
			});
		} else {//不存在则设置
			SVG.get(targetE.id).attr({
				"to" : pl.attr("id")
			});
		}
		//根据起始节点和终止节点相对位置重新计算线的坐标
		var FP = formatLine(SVG.get(target.id), SVG.get(targetE.id));
		if (FP["p1"]) {
			pl.attr({
				points : FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0]
						+ "," + FP["p2"][1],
				"from" : target.id,//线的起始节点ID
				"to" : targetE.id
			//线的终止节点ID
			});
		}
		//线条的文字说明
		g("lineCon").innerHTML += "<div class='step' id='lab" + pl.attr("id")
				+ "'>流程step</div>";
		addStyle(g("lab" + pl.attr("id")), {
			"left" : (pL1[0] + pL2[0]) / 2 + "px",
			"top" : (pL1[1] + pL2[1]) / 2 + "px"
		});
		pl.attr({
			"title" : g("lab" + pl.attr("id")).innerHTML,
			"labID" : "lab" + pl.attr("id")
		});
        unreShiftDel(pl,"line",undoAction)
        changeSaveStatus('save');
        redoAction.length = 0;
		if (e.stopPropagation) {//阻止冒泡
			e.stopPropagation();
		} else {
			return false;
		}

	}

	function lineDisplay() {
		if (image == "line") {
			pL1 = [ (e.clientX + scrollElement.scrollLeft) / _ZOOM,
					(e.clientY + scrollElement.scrollTop) / _ZOOM ];
			pl = gLine.polyline().stroke({
				color : "#0000ff",
				width : 1.3
			}).fill("none").attr({
				'transform': "",
                "points": "0,0 0,0",
                "marker-end": "url(#" + marker.attr("id") + ")",
                "title": "",
                "desc": "",
                "cond":"",
                "timeLimit": "",
                "timeLimitType": "I",
                "inheritType": "0",
                "inheritNodeCode": "",
                "shapetype": "polyline",
                "isaccounttime": "F",
                "canignore": "F"
			});
			addEvent(document, "mousemove", lineMove);
			addEvent(document, "mouseup", lineStop);
		}
	}
	return lineDisplay();
}
/**
 * 画圆(路由节点)
 * @param event
 * @returns {*}
 */
function drawCircle(event) {
	var e = window.event || event, target = e.srcElement || e.target, initP;
	if(isbrowser()=="IE"){//IE
        if (e.button != 1) {//如果不是鼠标左键则返回
            return;
        }
    }
    else{
        if (e.button != 0) {//如果不是鼠标左键则返回
            return;
        }
    }
	function circleStop(event) {
		//鼠标弹起时移除事件
		removeEvent(document, "mousedown", drawCircle);
		removeEvent(document, "mouseup", circleStop);
	}

	function circleDisplay() {
		if (image == "circle" && target.nodeName == "svg") {
			initP = [ (e.clientX + scrollElement.scrollLeft) / _ZOOM,
					(e.clientY + scrollElement.scrollTop) / _ZOOM ];//按下时坐标
			//赋值默认属性
			var circle = gShape.image(branchImg,40, 40).cx(fixXY(initP[0])).cy(fixXY(initP[1])).attr(
					{
						"title" : "分支节点",
						"shapetype" : "oval-fen",
						"routertype" : "D",
						"flowphase" : "",
						"osid" : "",
						"nodedesc" : "",
						"nodetype" : "R",
						"nodecode" : "",
						"isTrunkLine" : "F",
						// "filter" : "url(#filter)",
						"width" : 40,
						"height" : 40
					}).fill("#fff").stroke({color:"#00f"});
			addEvent(document, "mouseup", circleStop);//鼠标弹起时动作
            unreShiftDel(circle,"circle",undoAction);
            changeSaveStatus('save');
            redoAction.length = 0;
		}
	}

	return circleDisplay();
}
/**
 * 根据所选进行画图操作
 * @param event
 */
function drawImage(event) {
	var e = window.event || event, target = e.srcElement || e.target;
	switch (image) {
        case "rect":
            if (target.nodeName == "svg") {
                addEvent(target, "mousedown", drawRect);
            }
            break;
        case "selectMore":
            if (target.nodeName == "svg") {
                addEvent(target, "mousedown", drawRect);
            }
            break;
        case "line":
            if(target.nodeName=="image" || target.nodeName=="ellipse"||target.nodeName=="rect"||target.nodeName=="text"||target.nodeName=="path"){
                addEvent(target, "mousedown", drawLine);
            }
            break;
        case "circle":
            if (target.nodeName == "svg") {
                addEvent(target, "mousedown", drawCircle);
            }
            break;
        default:
            break;
	}
}
/**
 * 选择所画图形
 * @param event
 */
function selectImage(event) {
	var e = window.event || event, target = e.srcElement || e.target;
	if (target.nodeName.toLowerCase() == "img"
			&& target.parentNode.className == "opt") {
		if (selectTag) {//如果工具栏选中对象存在
			selectTag.style.backgroundColor = "buttonface";//清除选中
		} else {
			g("default").parentNode.style.backgroundColor = "buttonface";//清除默认选中
		}
		target.parentNode.style.backgroundColor = "#888";//选中点击对象
		selectTag = target.parentNode;//改变当前选中对象为点击对象
		switch (target.id) {
            case "selectMore":
                document.all.canvas.style.cursor = "default";
                image = "selectMore";
                break;
            case "rect":
                document.all.canvas.style.cursor = "crosshair";//鼠标指针变为十字形
                selectMoreArry.length = 0;
                selectPointsArry.length = 0;
                image = "rect";
                break;
            case "line":
                document.all.canvas.style.cursor = "crosshair";
                selectMoreArry.length = 0;
                selectPointsArry.length = 0;
                image = "line";
                break;
            case "circle":
                document.all.canvas.style.cursor = "default";
                selectMoreArry.length = 0;
                selectPointsArry.length = 0;
                image = "circle";
                break;
            case "default":
                document.all.canvas.style.cursor = "default";
                selectMoreArry.length = 0;
                selectPointsArry.length = 0;
                image = "default";
                break;
		default:
			break;
		}
	}
}
/**
 * 删除过程 ==> 当选中每个过程元件的时候可以点击删除按钮进行删除，删除前先要接触连线关系
 * @param o
 * @returns {boolean}
 */
function deletePro(o,operation) {
	if (!o) {//未选中对象则返回
		return false;
	} else {
		var pro = g(o);
	}
	if (pro) {
		switch (pro.nodeName) {
            case "image":
			if (pro.getAttribute("from") || pro.getAttribute("to")) {
		        if((pro.id == 'begin')||(pro.id == 'end')){
		            alert('不能删除开始结束节点');
                    return;
                }
                if(confirm("是否删除节点（包括与节点有关的所有连接线)")){
                    deleteConnectLine(pro);
                    SVG.get(o).remove();
                    if(pro.getAttribute("textID")) {
                        SVG.get(pro.getAttribute("textID")).remove();
                    }
                    if (!operation) {
                        if(pro.getAttribute('nodetype') == 'C'){
                            undoAction.unshift({
                                'oAction': 'draw',
                                rx: 2,
                                ry: 2,
                                "id": pro.getAttribute('id'),
                                "title": pro.getAttribute('title'),
                                "shapetype": "roundRect",
                                "filter": "url(#filter-rect)",
                                "flowphase": pro.getAttribute('flowphase'),
                                "osid": pro.getAttribute('osid'),
                                "nodedesc": pro.getAttribute('nodedesc'),
                                "nodetype": pro.getAttribute('nodetype'),
                                "nodecode": pro.getAttribute('nodecode'),
                                "opttype": pro.getAttribute('opttype'),
                                "optcode": pro.getAttribute('optcode'),
                                "optbean": pro.getAttribute('optbean'),
                                "optparam": pro.getAttribute('optparam'),
                                "subwfcode": pro.getAttribute('subwfcode'),
                                "roletype": pro.getAttribute('roletype'),
                                "rolecode": pro.getAttribute('rolecode'),
                                "isaccounttime": pro.getAttribute('isaccounttime'),
                                "timeLimitType": pro.getAttribute('timeLimitType'),
                                "inheritType": pro.getAttribute('inheritType'),
                                "inheritNodeCode": pro.getAttribute('inheritNodeCode'),
                                "timeLimit": pro.getAttribute('timeLimit'),
                                "isTrunkLine": pro.getAttribute('isTrunkLine'),
                                "unitexp": pro.getAttribute('unitexp'),
                                "powerexp": pro.getAttribute('powerexp'),
                                "expireopt": pro.getAttribute('expireopt'),
                                "riskinfo": pro.getAttribute('riskinfo'),
                                "warningrule": pro.getAttribute('warningrule'),
                                "warningparam": pro.getAttribute('warningparam'),
                                "x": pro.getAttribute('x'),
                                "y": pro.getAttribute('y'),
                            });
                            redoAction.length = 0;
                        }
                        else if(pro.getAttribute('nodetype') == 'R'){
                            stackAction(undoAction,pro);
                        }
                    }
                    changeSaveStatus('save');
                }
			} else {
                if(pro.getAttribute("textID")) {
                    SVG.get(pro.getAttribute("textID")).remove();//删除文本
                }
				SVG.get(o).remove();//删除节点
                //是点击删除时,并没有撤销删除时的操作变量，进行撤销入队
                if (!operation) {
                    if(pro.getAttribute('nodetype') == 'C'){
                        undoAction.unshift({
                            'oAction': 'draw',
                            rx: 2,
                            ry: 2,
                            "id": pro.getAttribute('id'),
                            "title": pro.getAttribute('title'),
                            "shapetype": "roundRect",
                            "filter": "url(#filter-rect)",
                            "flowphase": pro.getAttribute('flowphase'),
                            "osid": pro.getAttribute('osid'),
                            "nodedesc": pro.getAttribute('nodedesc'),
                            "nodetype": pro.getAttribute('nodetype'),
                            "nodecode": pro.getAttribute('nodecode'),
                            "opttype": pro.getAttribute('opttype'),
                            "optcode": pro.getAttribute('optcode'),
                            "optbean": pro.getAttribute('optbean'),
                            "optparam": pro.getAttribute('optparam'),
                            "subwfcode": pro.getAttribute('subwfcode'),
                            "roletype": pro.getAttribute('roletype'),
                            "rolecode": pro.getAttribute('rolecode'),
                            "isaccounttime": pro.getAttribute('isaccounttime'),
                            "timeLimitType": pro.getAttribute('timeLimitType'),
                            "inheritType": pro.getAttribute('inheritType'),
                            "inheritNodeCode": pro.getAttribute('inheritNodeCode'),
                            "timeLimit": pro.getAttribute('timeLimit'),
                            "isTrunkLine": pro.getAttribute('isTrunkLine'),
                            "unitexp": pro.getAttribute('unitexp'),
                            "powerexp": pro.getAttribute('powerexp'),
                            "expireopt": pro.getAttribute('expireopt'),
                            "riskinfo": pro.getAttribute('riskinfo'),
                            "warningrule": pro.getAttribute('warningrule'),
                            "warningparam": pro.getAttribute('warningparam'),
                            "x": pro.getAttribute('x'),
                            "y": pro.getAttribute('y'),
                        });
                        redoAction.length = 0;
                    }
                    else if(pro.getAttribute('nodetype') == 'R'){
                        stackAction(undoAction,pro);
                    }
			    }
            }
			break;
		case "polyline":
			var fromPro = g(pro.getAttribute("from")), //起始节点
			toPro = g(pro.getAttribute("to")), //终止节点
			lenFrom, //起始节点出线集合
			lenTo, //终止节点进线集合
			fromArray = new Array, toArray = new Array, i;
			if (fromPro.getAttribute("from")
					&& fromPro.getAttribute("from") != "") {
				lenFrom = fromPro.getAttribute("from").split(",");
				for (i = 0, len = lenFrom.length; i < len; i++) {
					if (lenFrom[i] != o) {//出线集合中移除当前线
						fromArray.push(lenFrom[i]);
					}
				}
				if (fromArray.length >= 1) {//存在则设置
					fromPro.setAttribute("from", fromArray.join(","));
				} else {
					fromPro.removeAttribute("from");//不存在移除属性
				}
			}
			if (toPro.getAttribute("to") && toPro.getAttribute("to") != "") {
				lenTo = toPro.getAttribute("to").split(",");
				for (i = 0, len = lenTo.length; i < len; i++) {
					if (lenTo[i] != o) {//进线集合移除当前线
						toArray.push(lenTo[i]);
					}
				}
				if (toArray.length >= 1) {//存在则设置
					toPro.setAttribute("to", toArray.join(","));
				} else {
					toPro.removeAttribute("to");//不存在移除属性
				}
			}
			SVG.get(o).remove();//移除线条
            if(!operation){
                undoAction.unshift({
                    'oAction':'draw',
                    "labID":pro.getAttribute('labID'),
                    "id":pro.getAttribute('id'),
                    'from': pro.getAttribute('from'),
                    'to': pro.getAttribute('to'),
                    'transform': pro.getAttribute('transform'),
                    "points": pro.getAttribute('points'),
                    "marker-end": pro.getAttribute('marker-end'),
                    "title": pro.getAttribute('title'),
                    "desc": pro.getAttribute('desc'),
                    "cond":pro.getAttribute('cond'),
                    "timeLimit": pro.getAttribute('timeLimit'),
                    "timeLimitType": pro.getAttribute('timeLimitType'),
                    "inheritType": pro.getAttribute('inheritType'),
                    "inheritNodeCode": pro.getAttribute('inheritNodeCode'),
                    "shapetype": pro.getAttribute('shapetype'),
                    "isaccounttime": pro.getAttribute('isaccounttime'),
                    "canignore": pro.getAttribute('canignore'),
                });
                redoAction.length = 0;
            }
			g("lineCon").removeChild(g("lab" + o));//移除说明
			break;

		case "ellipse":
			if (SVG.get(o).attr("shapetype") == "double-oval") {
				alert("不能删除起始过程！");
			} else {
				if (pro.getAttribute("from") || pro.getAttribute("to")) {
					if(confirm("是否删除节点（包括与节点有关的所有连接线)")){
                        deleteConnectLine(pro);
                        SVG.get(o).remove();
                    }
				} else {
					if(pro.getAttribute("textID")){
                        SVG.get(pro.getAttribute("textID")).remove();//删除文本
                    }
					SVG.get(o).remove();//删除节点
				}
                redoAction.length = 0;
			}
			break;
		default:
			break;
		}
        //多选状态清空
        selectMoreArry.length = 0;
        selectPointsArry.length = 0;
        clearSelected();
        changeSaveStatus('save');
	}
    $('#s4').html("");
}
/**
 * 右键取消当前工具栏选中，选中默认
 * @param event
 * @returns {boolean}
 */
function rightEvent(event) {
	var e = event || window.event;
	if (selectTag) {
		selectTag.style.backgroundColor = "buttonface";
	}
	g("default").parentNode.style.backgroundColor = "#888";
	selectTag = g("default").parentNode;
	document.all.canvas.style.cursor = "default";
	image = "default";
	return false;
}
/**
 * 设置缩放比例
 * @param act
 */
function setZoom(act) {
	var rate = act == "big" ? 0.2 : -0.2;
	var newzoom = _ZOOM + rate;
	if (newzoom > 2)
		return;
	if (newzoom < 0.2)
		return;
	_ZOOM = parseFloat(parseFloat(newzoom).toFixed(2));
	canvas.style.zoom = _ZOOM;
	g("lineCon").style.zoom = _ZOOM;
	if (isbrowser() == "FF") {
		$("svg").css({
			"-moz-transform" : "scale(" + _ZOOM + ")",
			"-moz-transform-origin" : "top left"
		});
		$("#lineCon").css({
			"-moz-transform" : "scale(" + _ZOOM + ")",
			"-moz-transform-origin" : "top left"
		});
	}
	document.all("zoomshow").value = _ZOOM;
}
/**
 * 改变缩放比例
 * @param act
 */
function changeZoom(act) {
	_ZOOM = parseFloat(parseFloat(act).toFixed(2));
	canvas.style.zoom = _ZOOM;
	g("lineCon").style.zoom = _ZOOM;
	if (isbrowser() == "FF") {
		$("svg").css({
			"-moz-transform" : "scale(" + _ZOOM + ")",
			"-moz-transform-origin" : "top left"
		});
		$("#lineCon").css({
			"-moz-transform" : "scale(" + _ZOOM + ")",
			"-moz-transform-origin" : "top left"
		});
	}
	document.all("zoomshow").value = _ZOOM;
}
/**
 * 改变鼠标指针样式，以便进行拉伸
 * @param event
 */
function zoomScale(event) {
	var e = event || window.event, target = e.target || e.srcElement;
	if ( target.nodeName == "image" || target.nodeName == "polyline" || target.nodeName == "ellipse"
			|| target.nodeName == "rect" || target.nodeName == "text") {
		target.style.cursor = "pointer";
	}
}
/**
 * 移除折线点
 * @param event
 */
function removePoint(event) {
	var e = event || window.event;
	var target = e.target || e.srcElement;
	if (target.nodeName != "polyline") {
		return;
	}
    unreShiftMove(target.id,target.getAttribute('points'),undoAction,"changePoint");
	var pointArray = new Array, points = target.getAttribute("points").split(
			" ");

	for ( var i = 0, len = points.length; i < len; i++) {
		var cp = points[i].split(",");
		pointArray.push([ cp[0], cp[1] ]);
	}
	for ( var h = 1, hLen = pointArray.length; h < hLen - 1; h++) {
		if (Math.abs((e.clientX + scrollElement.scrollLeft) / _ZOOM
				- pointArray[h][0]) < 10
				&& Math.abs((e.clientY + scrollElement.scrollTop) / _ZOOM
						- pointArray[h][1]) < 10) {
			deletePoint(target, h);
		}
		$("#s4").empty();
	}
}
//移动操作工具栏
moveTip(g("tool"), g("move"));
moveTip(g("argumentTool"), g("argumentTitle"));
moveTip(g("allUnit"), g("unit-title"));
moveTip(g("allUser"), g("user-title"));
moveTip(g("insideFunc"), g("func-title"));
//删除过程
addEvent(g("delete"), "click", function() {
	deletePro(o);
});
//绑定初始化
addEvent(document.body, "mousedown", initEvt);
//改变鼠标指针样式，以便进行拉伸
addEvent(canvas, "mousemove", zoomScale);
//操作工具栏,点击画线，过程 等等一些具体操作。
addEvent(g("tool"), "mousedown", selectImage);
addEvent(canvas, "mousedown", drawImage);
document.oncontextmenu = rightEvent;//右键动作
//双击折线点，移除折线点
document.ondblclick = removePoint;
