/**
 * Created by gyr on 2015-4-24.
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
    $("#route-type").hide();//隐藏路由类别
    $("#iosid").show();
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
    $('#flowphase').empty();//清空select
    for (k in Data.FlowPhase) {//重新添加option
        if (SVG.get(o).attr("flowphase") == k) {//节点阶段
            $("#flowphase").append("<option  value='" + k + "' selected='selected'>" + Data.FlowPhase[k] + "</option>");
        }
        else {

            $("#flowphase").append("<option  value='" + k + "' >" + Data.FlowPhase[k] + "</option>");
        }
    }
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
                }
                else {//正常的时候
                    $("#business").show();//显示业务操作
                    $("#optcode").empty();//清空
                    for (k in Data.OptCode) {
                        if (SVG.get(o).attr("optcode") == k) {
                            $("#optcode").append("<option  value='" + k + "' selected='selected'>" + Data.OptCode[k] + "</option>");
                        }
                        else {

                            $("#optcode").append("<option  value='" + k + "' >" + Data.OptCode[k] + "</option>");

                        }
                    }
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
        //根据角色类别进行判断
        if (SVG.get(o).attr("roletype") == "en") {//权限引擎
            $("#roleName").hide();//隐藏角色代码
            $("#powerName").show();//显示权限表达式
            g("powerexp").value = SVG.get(o).attr("powerexp");//权限表达式
        }
        else {
            $("#powerName").hide();//隐藏权限表达式
            $("#roleName").show();//显示角色代码
            $("#rolecode").empty();
            for (k in Data[SVG.get(o).attr("roletype")]) {
                if (SVG.get(o).attr("rolecode") == k) {
                    $("#rolecode").append("<option  value='" + k + "' selected='selected' >" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
                }
                else {
                    $("#rolecode").append("<option  value='" + k + "'>" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
                }
            }
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
            $("#iosid").hide();
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
                if (SVG.get(o).attr("roletype") == "en") {//权限引擎
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
    $("li.active").removeClass("active");//清除选中
    $("li").hide();//隐藏其他tab页
    $("#common").show().addClass("active");//显示公共属性tab页
    $("#constraint").show();//显示目标节点时间约束tab页
    $("table").hide();//隐藏其他属性栏
    $("#lineAttr").show();//显示线属性栏
    g("line-name").value = SVG.get(o).attr("title");
    g("cond").value = SVG.get(o).attr("cond");//条件
    for (i = 0; i < g("line-timeLimitType").options.length; i++) {//期限类别
        if (g("line-timeLimitType").options[i].value == SVG.get(o).attr("timeLimitType")) {
            g("line-timeLimitType").options[i].selected = "selected";
        }
    }
    g("line-timeLimit").value = SVG.get(o).attr("timeLimit");//期限设定
    for (i = 0; i < g("line-inheritType").options.length; i++) {//继承期限类别
        if (g("line-inheritType").options[i].value == SVG.get(o).attr("inheritType")) {
            g("line-inheritType").options[i].selected = "selected";
        }
        if (SVG.get(o).attr("inheritType") == "2") {
            $("#line-inheritCode").show();//显示继承环节代码
            g("line-inheritNodeCode").value = SVG.get(o).attr("inheritNodeCode");//继承环节代码
        }
        else {
            $("#line-inheritCode").hide();//隐藏继承环节代码
        }
    }
    for (i = 0; i < g("line-isaccounttime").options.length; i++) {//是否计时
        if (g("line-isaccounttime").options[i].value == SVG.get(o).attr("isaccounttime")) {
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
function sortArr(a, b) {
  return b[0] - a[0];
}
function initEvt(event){
    var e = window.event || event, target = e.srcElement || e.target, left,top,htmlString;
    var objId;//节点ID
    //定位到SVG本身或者开始结束节点直接返回
    if(target.nodeName=="svg"||target.getAttribute("shapetype")=="double-oval"){
        return false;
    }
    if(target.nodeName=="text"||target.nodeName=="path"){//如果定位到文本或path路径，目标换成其所在图形
        target = g(target.getAttribute("shapeID"));
    }
    //如果不是完成和等待状态的节点直接返回
    if(!target.getAttribute("state")){
        return false;
    }
    //完成或等待状态节点
    if(target.getAttribute("state")){
        if(target.getAttribute("nodetype")=="R"){
            left = parseInt(target.getAttribute("cx"));
            top = parseInt(target.getAttribute("cy"))+parseInt(target.getAttribute("height"))/2;
        }
        else{
            left = parseInt(target.getAttribute("x"))+parseInt(target.getAttribute("width"))/2;
            top = parseInt(target.getAttribute("y"))+parseInt(target.getAttribute("height"));
        }
        objId = target.getAttribute("id");
        if(target.getAttribute('nodetype') == "R"){
            return;
        }
        $.ajax({
            type:"GET",
            url: path+"service/flow/manager/viewflownode/"+flowInstId+"/"+objId,
            //url:"package.json",
            dataType:"json",
            async: false,
            success:function(data){
                data = data.data || data;
                $("#nodeInfo").css({"top":top+"px","left":left+"px"}).show();//显示提示框并设置位置
                $("#nodeContent").empty();//清空内容
                $("#nodeContent").css({"overflow":"scroll","height":"200px"});
                if(data.instance!=null){
                    //拼接字符串
                    htmlString = "";
                    //$("#nodeHeading").html(data.nodename);//设置标题
                    var instances=eval(data.instance);
                    for(var i=0;i<instances.length;i++){
                        if(htmlString!="") {
                            htmlString +="<br><hr style='height:1px;border:none;border-top:1px dashed #0066CC;'/>";
                        }
                        htmlString += "环节状态：<span class='red'>"+instances[i].state+"</span><br>创建时间：<span>"
                            +instances[i].createtime+"</span>";
                        if(instances[i].updateuser!=null){
                            htmlString+="<br>办理人："+instances[i].updateuser+"<br>办理时间：<span>"+instances[i].updatetime+"</span>";
                        }
                        if(instances[i].action!=null){
                            htmlString += "<div class='sort'><div class='sort-bg'></div><ul>";
                            var actions=eval(instances[i].action);
                            for(j=0;j<actions.length;j++){
                                htmlString += "<li><a>"+actions[j].username+"于<span class='blue'>"
                                    +actions[j].actiontime+"</span>"+actions[j].actiontype;
                            }
                            htmlString += "</ul></div>"
                        }
                        if (instances[i].task!=null){
                            var tasks=eval(instances[i].task);
                            var j=tasks.length;
                            var arry = new Array();
                            for(var m=0;m<j;m++){
                                arry.push(tasks[m].order+","+tasks[m].username);
                            }
                            //降序排列
                            arry.sort(
                              function(a,b){
                                var a0 = parseInt(a.split(",")[0]);
                                var b0 =  parseInt(b.split(",")[0]);
                                if(b0<a0){
                                  return -1;
                                }
                                if(b0>a0){
                                  return 1;
                                }
                              return 0;
                            });
                          var nameArr = new Array();
                            for(var m=0;m<j;m++){
                              nameArr.push(arry[m].split(",")[1]);
                            }
                            htmlString += "<br>当前办理人："+nameArr.join("，");
                        }
                    }
                    htmlString += "";
                    $("#nodeContent").append(htmlString);
                    $(".first-menu>li").hover(function(){
                        $(this).addClass("hover");
                    },function(){
                        $(this).removeClass("hover");
                    });
                }

            },
            error:function(){
                alert("请求失败");
            }
        });
    }
}
addEvent(g("canvas"), "mousedown", initEvt);
