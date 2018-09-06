/**
 * Created by gyr on 2015-4-9.
 */
//初始化
var canvas = g("canvas");
/**
 * 初始化一些SVG参数
 */
function init() {
    $("table").hide();
    //创建一个阴影过滤器，用于圆形
    var o_filter = document.createElementNS(SVG.ns, "filter"),
        o_feOffset = document.createElementNS(SVG.ns, "feOffset"),
        o_feGaussianBlur = document.createElementNS(SVG.ns, "feGaussianBlur"),
        o_feBlend = document.createElementNS(SVG.ns, "feBlend");
    o_feOffset.setAttribute("result", "offOut");
    o_feOffset.setAttribute("in", "SourceAlpha");
    o_feOffset.setAttribute("dx", 3);
    o_feOffset.setAttribute("dy", 3);

    o_feGaussianBlur.setAttribute("result", "blurOut");
    o_feGaussianBlur.setAttribute("in", "offOut");
    o_feGaussianBlur.setAttribute("stdDeviation", 2);
    o_feBlend.setAttribute("in", "SourceGraphic");
    o_feBlend.setAttribute("in2", "blurOut");
    o_feBlend.setAttribute("mode", "normal");
    o_filter.setAttribute("x", -0.1);
    o_filter.setAttribute("y", -0.1);
    o_filter.setAttribute("width", "200%");
    o_filter.setAttribute("height", "200%");
    o_filter.setAttribute("id", "filter");
    o_filter.appendChild(o_feOffset);
    o_filter.appendChild(o_feGaussianBlur);
    o_filter.appendChild(o_feBlend);
    //创建填充效果
    var radialGradient = document.createElementNS(SVG.ns, "radialGradient"),
        stop1 = document.createElementNS(SVG.ns, "stop"),
        stop2 = document.createElementNS(SVG.ns, "stop");
    radialGradient.setAttribute("id","grad1");
    radialGradient.setAttribute("cx","50%");
    radialGradient.setAttribute("cy","50%");
    radialGradient.setAttribute("fx","50%");
    radialGradient.setAttribute("fy","50%");
    radialGradient.setAttribute("r","50%");
    stop1.setAttribute("offset","0%");
    stop1.style.stopColor = "#ffffff";
    stop1.style.stopOpacity = 0;
    stop2.style.stopColor = "#CCEAD2";
    stop2.style.stopOpacity = 1;
    stop2.setAttribute("offset","100%");
    radialGradient.appendChild(stop1);
    radialGradient.appendChild(stop2);
    g("s1").appendChild(o_filter);//s1为SVG第一个子元素id，这里为<defs>
    g("s1").appendChild(radialGradient);
    //创建另一个阴影过滤器，用于矩形
    o_filter = document.createElementNS(SVG.ns, "filter");
    o_feOffset = document.createElementNS(SVG.ns, "feOffset");
    o_feGaussianBlur = document.createElementNS(SVG.ns, "feGaussianBlur");
    o_feBlend = document.createElementNS(SVG.ns, "feBlend");
    o_feOffset.setAttribute("result", "offOut");
    o_feOffset.setAttribute("in", "SourceAlpha");
    o_feOffset.setAttribute("dx", 4);
    o_feOffset.setAttribute("dy", 4);


    o_feGaussianBlur.setAttribute("result", "blurOut");
    o_feGaussianBlur.setAttribute("in", "offOut");
    o_feGaussianBlur.setAttribute("stdDeviation",2);
    o_feBlend.setAttribute("in", "SourceGraphic");
    o_feBlend.setAttribute("in2", "blurOut");
    o_feBlend.setAttribute("mode", "normal");
    o_filter.setAttribute("x", 0);
    o_filter.setAttribute("y", 0);
    o_filter.setAttribute("width", "200%");
    o_filter.setAttribute("height", "200%");
    o_filter.setAttribute("id", "filter-rect");
    o_filter.appendChild(o_feOffset);
    o_filter.appendChild(o_feGaussianBlur);
    o_filter.appendChild(o_feBlend);
    g("s1").appendChild(o_filter);
    //箭头的三种颜色  可能有更好的方式来做箭头，这个只是一个简单额三角
    marker.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    marker.path().attr({"d": "m 0 0 L 7 4 L 0 7 z"}).fill("#0000ff");
    markerGreen.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    markerGreen.path().attr({"d": "M 0 0 L 7 4 L 0 7 z"}).fill("#18b217");
    markerGrey.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    markerGrey.path().attr({"d": "M 0 0 L 7 4 L 0 7 z"}).fill("#aaa");
    markerBlue.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    markerBlue.path().attr({"d": "m 0 0 L 7 4 L 0 7 z"}).fill("#00e1ff");
}
/**
 * 加载xml文档
 * @param xml
 */
function loadXml(xml) {
	xml = xml.replace("\u0000","");
    var xmldoc;//xml文档对象
    if (window.ActiveXObect) {//IE浏览器
        xmldoc = new ActiveXObject("Microsoft.XMLDOM");
        xmldoc.loadXML(xml);
    }
    else if (window.DOMParser) {
        parser = new DOMParser();
        xmldoc = parser.parseFromString(xml, "text/xml");
    }
    var getShape = xmldoc.getElementsByTagName("Node"),//节点对象
        getLine = xmldoc.getElementsByTagName("Transition"),//流转对象
        CommitFlow = xmldoc.getElementsByTagName("CommitFlow")[0];//根节点
    for (var i = 0, len = getShape.length; i < len; i++) {
        var shapeArgs,nodeType = attrValue(getShape[i], "BaseProperties", "nodetype");//节点类型
        var shapeType = attrValue(getShape[i], "VMLProperties", "shapetype");//节点形状类型
        //--兼容旧数据开始
        if(nodeType == 'C'||nodeType=='B'){
        	shapeType = "roundRect";
        }
        else if(nodeType == 'A'||nodeType=='F'){
        	shapeType = "double-oval";
        }
        else if(nodeType=='D'){
        	shapeType = "oval-fen";
        	nodeType = "R";
        }
        else if(nodeType=='E'){
        	shapeType = "oval-ju";
        	nodeType = "R";
        }
        else if(nodeType=='G'){
        	shapeType = "oval-multi";
        	nodeType = "R";
        }
        else if(nodeType=='H'){
        	shapeType = "oval-bing";
        	nodeType = "R";
        }
        //--兼容旧数据结束
        if(nodeType == 'C'||nodeType=='B'){//业务节点、首节点（首节点其实也是业务节点）
            //修正x
            shapeArgs = {
                shapetype: shapeType,//节点形状类型
                width: 40,
                height: 40,
                id: attrValue(getShape[i], "BaseProperties", "id"),
                x: fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "x")) + attrValue(getShape[i], "VMLProperties", "width")/2 - 20),
                y: fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "y"))),
                title: attrValue(getShape[i], "BaseProperties", "name"),
                flowphase: attrValue(getShape[i], "BaseProperties", "flowphase"),//节点阶段
                nodedesc: attrValue(getShape[i], "BaseProperties", "nodedesc"),//节点描述
                nodetype: nodeType,//节点类型
                nodecode: attrValue(getShape[i], "BaseProperties", "nodecode"),//节点代码
                osid: attrValue(getShape[i], "BaseProperties", "osid"),//osid
                opttype: attrValue(getShape[i], "BaseProperties", "opttype") == 'B'?'A':attrValue(getShape[i], "BaseProperties", "opttype"),//操作类别
                optcode: attrValue(getShape[i], "BaseProperties", "optcode"),//业务代码
                optbean: attrValue(getShape[i], "BaseProperties", "optbean"),//业务注入
                optparam: attrValue(getShape[i], "BaseProperties", "optparam"),//操作参数
                subwfcode: attrValue(getShape[i], "BaseProperties", "subwfcode"),//子流程
                roletype: attrValue(getShape[i], "BaseProperties", "roletype"),//角色类别
                rolecode: attrValue(getShape[i], "BaseProperties", "rolecode"),//角色代码
                isaccounttime: attrValue(getShape[i], "BaseProperties", "isaccounttime"),//是否计时
                timeLimitType: attrValue(getShape[i], "BaseProperties", "timeLimitType"),//期限类别
                inheritType: attrValue(getShape[i], "BaseProperties", "inheritType"),//期限继承类别
                inheritNodeCode: attrValue(getShape[i], "BaseProperties", "inheritNodeCode"),//继承环节代码
                timeLimit: attrValue(getShape[i], "BaseProperties", "timeLimit"),//期限时间
                isTrunkLine: attrValue(getShape[i], "BaseProperties", "isTrunkLine"),//是否为主干节点
                unitexp: attrValue(getShape[i], "BaseProperties", "unitexp"),//机构表达式
                powerexp: attrValue(getShape[i], "BaseProperties", "powerexp"),//权限表达式
                expireopt: attrValue(getShape[i], "BaseProperties", "expireopt"),//预期处理办法
//                riskinfo: attrValue(getShape[i], "BaseProperties", "riskinfo"),//风险信息
                warningrule: attrValue(getShape[i], "BaseProperties", "warningrule"),//预警规则
                warningparam: attrValue(getShape[i], "BaseProperties", "warningparam"),//预警时间参数
                desc: attrValue(getShape[i], "BaseProperties", "desc"),//说明
            };
        }
        else if(nodeType=='R'){//路由节点
        	var cx = attrValue(getShape[i], "VMLProperties", "cx");
        	var cy = attrValue(getShape[i], "VMLProperties", "cy");
        	if (cx == "")
        		cx = parseInt(attrValue(getShape[i], "VMLProperties", "x")) + 50;
        	if (cy == "")
        		cy = parseInt(attrValue(getShape[i], "VMLProperties", "y")) + 20;
            shapeArgs = {
                shapetype: shapeType,//节点形状类型
                width: 40,//parseInt(attrValue(getShape[i], "VMLProperties", "width")),
                height: 40,//parseInt(attrValue(getShape[i], "VMLProperties", "height")),
                id: attrValue(getShape[i], "BaseProperties", "id"),
                cx: parseInt(cx),
                cy: parseInt(cy),
                x: fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "x"))?parseInt(attrValue(getShape[i], "VMLProperties", "x")):cx-20),
                y: fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "y"))?parseInt(attrValue(getShape[i], "VMLProperties", "y")):cy-20),
                title: attrValue(getShape[i], "BaseProperties", "name"),
                flowphase: attrValue(getShape[i], "BaseProperties", "flowphase"),//节点阶段
                nodedesc: attrValue(getShape[i], "BaseProperties", "nodedesc"),//节点描述
                nodetype: nodeType,//节点类型
                nodecode: attrValue(getShape[i], "BaseProperties", "nodecode"),//节点代码
                osid: attrValue(getShape[i], "BaseProperties", "osid"),//osid
                isTrunkLine: attrValue(getShape[i], "BaseProperties", "isTrunkLine"),//是否为主干节点
                routertype: attrValue(getShape[i], "BaseProperties", "routertype"),//路由类别
                // filter: "url(#filter)"//阴影过滤器
            }
        }
        else{
        	var cx = attrValue(getShape[i], "VMLProperties", "cx");
        	var cy = attrValue(getShape[i], "VMLProperties", "cy");
        	if (cx == "")
        		cx = parseInt(attrValue(getShape[i], "VMLProperties", "x")) + 20;
        	if (cy == "")
        		cy = parseInt(attrValue(getShape[i], "VMLProperties", "y")) + 30;
            shapeArgs = {//开始节点、结束节点
                shapetype: shapeType,//节点形状类型
                width: 40,//parseInt(attrValue(getShape[i], "VMLProperties", "width")),
                height: 40,//parseInt(attrValue(getShape[i], "VMLProperties", "height")),
                id: attrValue(getShape[i], "BaseProperties", "id"),
                cx: parseInt(cx),
                cy: parseInt(cy),
                x: parseInt(attrValue(getShape[i], "VMLProperties", "x"))?fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "x"))):fixXY(cx-20),
                y: parseInt(attrValue(getShape[i], "VMLProperties", "y"))?fixXY(parseInt(attrValue(getShape[i], "VMLProperties", "y"))):fixXY(cy-20),
                title: attrValue(getShape[i], "BaseProperties", "name"),
                nodetype: nodeType,
                nodecode:"",
                osid:"",
                nodedesc:"",
                // filter: "url(#filter)"//阴影过滤器
            }
        }
        var t,shape,specialAttr;
        switch (shapeArgs.shapetype) {//根据节点类型分别画不同形状
            case "double-oval" ://开始节点、结束节点
                //画形状
                if(shapeArgs.id == 'begin') {
                    shape = gShape.image(startImg, shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                        .attr(shapeArgs).fill("#fff").stroke({color: "#00f"});
                }
                if(shapeArgs.id == 'end') {
                    shape = gShape.image(endImg, shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                        .attr(shapeArgs).fill("#fff").stroke({color: "#00f"});
                }
                break;
            case "roundRect" ://业务节点
                shape = gShape.image(serviceImg,shapeArgs.width, shapeArgs.height).x(shapeArgs.x).y(shapeArgs.y)
                    .stroke({color: "#00f", width: 1}).fill("#fff").attr(shapeArgs).attr({rx: 2, ry: 2});
                t = gText.text(dealStr(shapeArgs.title, shapeArgs.width)).fill("#00f")
                    .x(shapeArgs.x).y(shapeArgs.y + shapeArgs.height  + 20).font({
                        size: 14,
                        "text-anchor": "middle"
                    })
                    .attr({dx: shapeArgs.x + shapeArgs.width / 2, "shapeID": shape.attr("id")});
                shape.attr({"textID": t.attr("id")});
                break;
            case "oval-fen"://路由分支节点
                shape = gShape.image(branchImg,shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                break;
            case "oval-multi"://路由多实例节点
                shape = gShape.image(multipleImg,shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                specialAttr = {
                    // "pathID": t.attr("id"),
                    "multiinsttype":attrValue(getShape[i], "BaseProperties", "multiinsttype"),//多实例类别
                    "unitexp":attrValue(getShape[i], "BaseProperties", "unitexp"),//机构表达式
                    "powerexp":attrValue(getShape[i], "BaseProperties", "powerexp"),//权限表达式
                    "roletype":attrValue(getShape[i], "BaseProperties", "roletype"),//角色类别
                    "rolecode":attrValue(getShape[i], "BaseProperties", "rolecode")//角色代码
                };
                shape.attr(specialAttr);
                break;
            case "oval-ju"://路由汇聚节点
                shape = gShape.image(convergeImg,shapeArgs.width,  shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                specialAttr = {
                    "pathID": t.attr("id"),
                    "convergetype":attrValue(getShape[i], "BaseProperties", "convergetype"),//汇聚条件类别
                    "convergeparam":attrValue(getShape[i], "BaseProperties", "convergeparam"),//汇聚参数
                    "optbean":attrValue(getShape[i], "BaseProperties", "optbean")//汇聚外埠判断
                };
                shape.attr(specialAttr);
                break;
            case "oval-bing"://路由并行节点
                shape = gShape.image(parallelImg,shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                break;
            case  "oval-tong"://路由同步节点
                shape = gShape.image(synchroImg,shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                break;
            case "oval-you"://路由游离节点
                shape = gShape.image(freeImg,shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                break;
            default :break;
        }

    }
    //画线
    for (var j= 0,jLen = getLine.length;j<jLen;j++){
    	var points = attrValue(getLine[j], "VMLProperties", "points"); // 线轨迹坐标
    	if (points.indexOf("pt") != -1)
    		points = changePt(attrValue(getLine[j], "VMLProperties", "points")); // 线轨迹坐标单位转换
    	var _from = attrValue(getLine[j], "BaseProperties", "from"); // 线初始节点
    	var _to = attrValue(getLine[j], "BaseProperties", "to"); // 线结束节点
        var line = gLine.polyline().stroke({width:1.3,color:"#00f"}).fill("none")
            .attr({'transform':"","marker-end":"url(#"+marker.attr("id")+")",
                "id":attrValue(getLine[j],"BaseProperties","id"),
                "title":attrValue(getLine[j],"BaseProperties","name"),
                "timeLimitType":attrValue(getLine[j],"BaseProperties","timeLimitType"),
                "inheritType":attrValue(getLine[j],"BaseProperties","inheritType"),
                "inheritNodeCode":attrValue(getLine[j],"BaseProperties","inheritNodeCode"),
                "timeLimit":attrValue(getLine[j],"BaseProperties","timeLimit"),
                "isaccounttime":attrValue(getLine[j],"BaseProperties","isaccounttime"),
                "canignore":attrValue(getLine[j],"BaseProperties","canignore"),//是否可以忽略运行
                "desc":attrValue(getLine[j],"BaseProperties","desc"),
                "cond":attrValue(getLine[j],"BaseProperties","cond"),
                "shapetype":"polyline",
                "points" : points,
                "from" : _from,
                "to" : _to,
                "labID":attrValue(getLine[j],"LabelProperties","id")
                //"filter":"url(#filter-rect)"
            });
        var from = SVG.get(attrValue(getLine[j],"BaseProperties","from")),//线的起始节点
            to = SVG.get(attrValue(getLine[j],"BaseProperties","to"));//线的终止节点
        if(from.attr("from")){
            from.attr({"from":from.attr("from")+","+line.attr("id")});
        }
        else{
            from.attr({"from":line.attr("id")});
        }
        if(to.attr("to")){
            to.attr({"to":to.attr("to")+","+line.attr("id")});
        }
        else{
            to.attr({"to":line.attr("id")});
        }
        //流转对象（线条）名称
        $("#lineCon").append("<div class='step' style='left:"+attrValue(getLine[j],"LabelProperties","x")+";top:"+attrValue(getLine[j],"LabelProperties","y")+";' id='"+attrValue(getLine[j],"LabelProperties","id")+"'>"+attrValue(getLine[j],"BaseProperties","name")+"</div>");
    }
    // if(isbrowser()=="FF"){
    //     $("#canvas").height(document.documentElement.scrollHeight +100);//高度适应
    // }
    // else{
    //     $("#canvas").height(document.body.scrollHeight +100);//高度适应
    // }
    $("#canvas").height(99999);
    //保存整个流程图的TYPE、CODE、名称、描述
    SVG.get("s0").attr({"flow-type":attrValue(CommitFlow,"Flow","type"),
        "flow-code":attrValue(CommitFlow,"Flow","code"),
        "flow-name":attrValue(CommitFlow,"Flow","name"),
        "flow-desc":attrValue(CommitFlow,"Flow","desc")});
    loadChangeLine();
    changeLoadPoints();
    var img = $('#s3 image');
    for(var i = 0;i<img.length;i++){
        if(img[i].getAttribute('shapetype') == "roundRect"){
            changeOperateImg(img[i].getAttribute('opttype'),img[i].id);
        }
    }
}
/**
 * 加载viewxml文档
 * @param xml
 */
function loadViewXml(xml){
    xml = xml.replace("\u0000","");
    var xmldoc;//xml文档对象
    if (window.ActiveXObect) {//IE浏览器
        xmldoc = new ActiveXObject("Microsoft.XMLDOM");
        xmldoc.loadXML(xml);
    }
    else if (window.DOMParser) {
        parser = new DOMParser();
        xmldoc = parser.parseFromString(xml, "text/xml");
    }
    var Step = xmldoc.getElementsByTagName("Step");//节点对象
    var Pro = xmldoc.getElementsByTagName("Proc");
    //建立textmap用以改变文本颜色
    var text = $('#s6 text');
    var textMap = [];
    for(var i = 0;i<text.length;i++){
        textMap[text[i].getAttribute('shapeID')] = text[i];
    }

    var end = $('#end')[0],
        from = end.getAttribute('to'),
        lastStep;

    for(var i = 0;i< Step.length;i++) {
        if (from == Step[i].getAttribute('id')) {
            lastStep = Step[i];
        }

        if(Step[i].getAttribute('inststate') == -1){
            $('#'+Step[i].getAttribute('id'))[0].setAttribute('stroke','#777');
            $('#'+Step[i].getAttribute('id'))[0].setAttribute("marker-end","url(#" + markerGrey.attr("id") + ")");
        }
        else{
            $('#'+Step[i].getAttribute('id'))[0].setAttribute("marker-end","url(#" + marker.attr("id") + ")");
            $('#'+Step[i].getAttribute('id'))[0].setAttribute('stroke-width','2');
        }
    }
    for(var i =0;i<Pro.length;i++){
        var node = $('#' + Pro[i].getAttribute('id'))[0];
        //创建div的left,right;
        var left,top,div;
        if(node) {
            if (Pro[i].getAttribute('inststate') == "ready") {
                //未执行节点属性
                node.setAttribute('filter', 'url(#gray)');
                if (textMap['' + Pro[i].getAttribute('id')]) {
                    textMap['' + Pro[i].getAttribute('id')].setAttribute('fill', '#777');
                }
            }
            if (Pro[i].getAttribute('inststate') == "waiting") {
                //正在执行节点属性
                node.setAttribute('filter', 'url(#red)');
                if (textMap['' + Pro[i].getAttribute('id')]) {
                    textMap['' + Pro[i].getAttribute('id')].setAttribute('fill', 'red');
                }
            }
            if(Pro[i].getAttribute('inststate') != "ready") {
                //未执行节点state属性为空，去除点击事件
                node.setAttribute('state', Pro[i].getAttribute('inststate'));
            }
            if(Pro[i].getAttribute('instcount')>0 && node.getAttribute('nodetype') == "C") {
                //创建次数div
                left = parseInt(node.getAttribute('x')) + imgWidth;
                top = parseInt(node.getAttribute('y')) - imgHeight / 2;
                div = document.createElement('div');
                div.setAttribute('class', 'count');
                div.style.left = left + 'px';
                div.style.top = top + 'px';
                div.innerHTML = Pro[i].getAttribute('instcount');
                $("#nodeCount").append(div);
            }
        }
    }
    //将div代替text
    textToDiv();
    // 新版流程图界面，结束标志不管流程走没走完都是红色的，客户认为很不明了，能不能改成流程没走完是灰色的，流程走完了再变成其他颜色。
    if (lastStep && lastStep.getAttribute('inststate') != 1) {
        //未执行节点属性
        end.setAttribute('filter', 'url(#gray)');
    }
}
/**
 * 创建只包含开始节点和结束节点的流程图
 */
function createEmptyXml(){
    var shape,t;
    shape = gShape.image(startImg,40, 40).cx(400).cy(100).attr({
        "title":"开始",
        "shapetype": "double-oval",
        "id":"begin",
        "nodedesc": "",
        "nodetype": "A",
        "nodecode": "",
        "osid": "",
        "filter": "url(#filter)",
        "width": 40,
        "height":40
    }).fill("#fff").stroke({color:"#00f"});
    shape = gShape.image(endImg,40, 40).cx(400).cy(600).attr({
        "title":"结束",
        "shapetype": "double-oval",
        "id":"end",
        "nodedesc": "",
        "nodetype": "F",
        "nodecode": "",
        "osid": "",
        "filter": "url(#filter)",
        "width": 40,
        "height": 40
    }).fill("#fff").stroke({color:"#00f"});
}
/**
 * 改变节点属性值
 * @param obj
 * @param id
 */
function changeValue(obj,id){
    var k;
    if(g(o)){
        if(id=="title"){//更新文本信息
            if(SVG.get(o).attr("nodetype")!='R'){
                setValue(g(SVG.get(o).attr("textID")),dealStr(obj.value,SVG.get(o).attr("width")));
            }
        }
        if(id=="opttype"){//操作类别
            if(obj.value=="D"){//自动流程节点的时候
                $("#business").hide();//隐藏业务操作
                $("#childNode").hide();//隐藏子流程
            }
            else if(obj.value=="S"){//子流程节点的时候
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
            else{//正常的时候
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
            changeOperateImg(obj.value,o);
        }
        if(id=="roletype"){
            if(obj.value=="en"){
                $("#roleName").hide();//隐藏角色代码
                $("#powerName").show();//显示权限表达式
                g("powerexp").value = SVG.get(o).attr("powerexp");//权限表达式
            }
            else{
                $("#powerName").hide();//隐藏权限表达式
                $("#roleName").show();//显示角色代码
                $("#rolecode").empty();
                for (k in Data[obj.value]) {
                    if (SVG.get(o).attr("rolecode") == k) {
                        $("#rolecode").append("<option  value='" + k + "' selected='selected' >" + Data[obj.value][k] + "</option>");
                    }
                    else {
                        $("#rolecode").append("<option  value='" + k + "'>" + Data[obj.value][k] + "</option>");
                    }
                }
            }
        }
        if(id=="inheritType"){
            if(obj.value=='2'){
                $("#inheritCode").show();//显示继承环节代码
                g("inheritNodeCode").value = SVG.get(o).attr("inheritNodeCode");//继承环节代码
            }
            else{
                $("#inheritCode").hide();//隐藏继承环节代码
            }
        }
        if(id=="multiinsttype"){
            if(obj.value=='V'){
                $("#tr-multiinstparam").show();//显示多实例变量
                g("multiinstparam").value = SVG.get(o).attr("multiinstparam");//多实例变量
            }
            else{
                $("#tr-multiinstparam").hide();//隐藏多实例变量
            }
        }
        //是否允许保存
        changeSaveStatus('save');
        //时钟更换图片
        if(id=="isaccounttime"){
            changeClock(obj,o);
        }
        g(o).setAttribute(id,obj.value);
    }
}
/**
 * 设置线条名称
 * @param obj
 */
function setLineName(obj){
    SVG.get(o).attr({"title":obj.value});
    g("lab"+o).innerHTML = obj.value;
}
/**
 * 设置多实例节点角色类别
 * @param obj
 */
function setRoleType(obj){
    SVG.get(o).attr({"roletype":obj.value});
    if(obj.value=="en"){
        $("#route-roleName").hide();//隐藏角色代码
        $("#route-powerName").show();//显示权限表达式
        g("route-powerexp").value = SVG.get(o).attr("powerexp");//权限表达式
    }
    else{
        $("#route-powerName").hide();//隐藏权限表达式
        $("#route-roleName").show();//显示角色代码
        $("#route-rolecode").empty();
        for (var k in Data[SVG.get(o).attr("roletype")]) {
            if (SVG.get(o).attr("rolecode") == k) {
                $("#route-rolecode").append("<option  value='" + k + "' selected='selected' >" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
            }
            else {
                $("#route-rolecode").append("<option  value='" + k + "'>" + Data[SVG.get(o).attr("roletype")][k] + "</option>");
            }
        }
    }
}
/**
 * 设置线继承期限类别
 * @param obj
 */
function setInheritType(obj){
    SVG.get(o).attr({"inheritType":obj.value});
    if(SVG.get(o).attr("inheritType")=="2"){
        $("#line-inheritCode").show();
        g("line-inheritNodeCode").value = SVG.get(o).attr("inheritNodeCode");
    }
    else{
        $("#line-inheritCode").hide();
    }
}
/**
 * 设置路由类别
 * @param obj
 */
function setRouteType(obj){
     var path;
    //重画内部
     switch (obj.value){
         case "D"://分支
             SVG.get(o).attr('xlink:href',branchImg);
             SVG.get(o).attr('shapetype',"oval-fen");
             $("#special").hide();//隐藏特殊属性栏
             break;
         case "E"://汇聚
             SVG.get(o).attr('xlink:href',convergeImg);
             SVG.get(o).attr('shapetype',"oval-ju");
             if($("#special").is(":hidden")){
                 $("#special").show();//显示特殊属性栏
             }
             $(".multi-special").hide();//隐藏多实例节点属性
             $(".ju-special").show();//显示汇聚节点属性
             SVG.get(o).attr({
                 "convergetype":"A",
                 "convergeparam":"",
                 "optbean":""
             });
             for (i = 0; i < g("convergetype").options.length; i++) {//汇聚条件类别
                 if (g("convergetype").options[i].value == SVG.get(o).attr("convergetype")) {
                     g("convergetype").options[i].selected = "selected";
                 }
             }
             g("convergeparam").value = SVG.get(o).attr("convergeparam");//汇聚参数
             g("route-optbean").value = SVG.get(o).attr("optbean");//汇聚外埠判断bean
             break;
         case "G"://多实例
             SVG.get(o).attr('xlink:href',multipleImg);
             SVG.get(o).attr('shapetype',"oval-multi");
             if($("#special").is(":hidden")){
                 $("#special").show();//显示特殊属性栏
             }
             $(".ju-special").hide();//隐藏汇聚节点属性
             $(".multi-special").show();//显示多实例节点属性
             SVG.get(o).attr({
                 "multiinsttype":"D",
                 "unitexp":"",
                 "powerexp":"",
                 "roletype":"",
                 "rolecode":""
             });
             for (i = 0; i < g("multiinsttype").options.length; i++) {//多实例类别
                 if (g("multiinsttype").options[i].value == SVG.get(o).attr("multiinsttype")) {
                     g("multiinsttype").options[i].selected = "selected";
                 }
                 if (SVG.get(o).attr("multiinsttype") == 'V') {//多实例类别为用户自定义变量
                     $("#tr-multiinstparam").show();//显示多实例变量
                     g("multiinstparam").value = SVG.get(o).attr("multiinstparam");//多实例变量
                 }
                 else {
                     $("#tr-multiinstparam").hide();//隐藏多实例变量
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
             break;
         case "H"://并行
             SVG.get(o).attr('xlink:href',parallelImg);
             SVG.get(o).attr('shapetype',"oval-bing");
             $("#special").hide();//隐藏特殊属性栏
             break;
         case "R"://游离
             SVG.get(o).attr('shapetype',"oval-you");
             SVG.get(o).attr('xlink:href',freeImg);
             $("#special").hide();//隐藏特殊属性栏
             break;
         case "S"://同步
             SVG.get(o).attr('xlink:href',synchroImg);
             SVG.get(o).attr('shapetype',"oval-tong");
             $("#special").hide();//隐藏特殊属性栏
             break;
         default:break;
     }
    SVG.get(o).attr({"routertype":obj.value});
    changeSaveStatus('save');
}
function setConvergeparam(obj){
	if($("#convergetype").val())
	if(isNaN(obj.value)){
		alert("必须为数字");
		$("#convergeparam").val("");
		return;
	}
	else{
		if($("#convergetype").val()=='L'||$("#convergetype").val()=='M'){
			if(obj.value.indexOf(".")>0){
				alert("必须为整数");
				$("#convergeparam").val("");
				return;
			}
		}
		if($("#convergetype").val()=='V'){
			if(obj.value.indexOf(".")<0){
				alert("必须为小数");
				$("#convergeparam").val("");
				return;
			}
		}
	}
	SVG.get(o).attr({"convergeparam":obj.value});
}
/**
 * 退出编辑
 */
function closeWindow() {
    if(confirm("确定要退出本次编辑吗？")) {
        window.close();
    }
}
/**
 * 流程图校验
 * @returns {string}
 */
function validate(){
    var obj = g("s3").childNodes;
    var flog=true;
    var myerrors=[];
    var  bxs=0;      //并行个数
    var hjs=0;		//汇聚个数
    var gjs=0;		//多实例个数
    var hd=0;		//首节点个数
    for(var i=0;i<obj.length;i++){
        if(obj[i].getAttribute("nodetype")!='A'&&obj[i].getAttribute("nodetype")!='F'){//开始节点和结束节点除外
            //操作类别为自动执行
            if(obj[i].getAttribute("opttype")=='D')
            {
                if(obj[i].getAttribute("optbean")==''){
                    myerrors.push(obj[i].getAttribute("title")+"为自动执行节点，但是没有配置业务bean，请检查后保存。");
                    flog=false;}
            }
            //操作类别为子流程
            else if(obj[i].getAttribute("opttype")=='S')
            {
                if(obj[i].getAttribute("subwfcode")==''||obj[i].getAttribute("subwfcode")=='0'){
                    myerrors.push(obj[i].getAttribute("title")+"为子流程节点，但是没有配置子流程，请检查后保存。");

                    flog= false;}
            }
            //操作类别不为哑元
            else if(obj[i].getAttribute("opttype")!='E')
            {
                if(obj[i].getAttribute("optcode")==''||obj[i].getAttribute("optcode")=='0'){
                    myerrors.push(obj[i].getAttribute("title")+"没有配置业务代码，请检查后保存");
                    flog=false;
                }
            }
            //角色类型为权限引擎
            if(obj[i].getAttribute("roletype")=='en')	{
                if(obj[i].getAttribute("powerexp")==''){
                    myerrors.push(obj[i].getAttribute("title")+"的角色类别为权限引擎，但是没有设置权限表达式，请检查后保存");
                    flog=false;
                }
            }
            else{
                if(obj[i].getAttribute("rolecode")==''||obj[i].getAttribute("rolecode")=='0'&&obj[i].getAttribute("opttype")!='D'&&obj[i].getAttribute("opttype")!='E'){
                    myerrors.push(obj[i].getAttribute("title")+"没有设置角色代码，请检查后保存");
                    flog=false;
                }
            }
            //计算并行节点的个数
            if(obj[i].getAttribute("routertype")=="H")
                bxs++;
            //计算汇聚节点的个数
            if(obj[i].getAttribute("routertype")=="E")
                hjs++;
            //计算多实例节点的个数
            if(obj[i].getAttribute("routertype")=="G")
                gjs++;
            //计算首节点的个数
            if(obj[i].getAttribute("nodetype")=="B")
                hd++;
            //判断节点的入度
            if(!obj[i].getAttribute("to") && !obj[i].getAttribute("from")) {
                myerrors.push(obj[i].getAttribute("title")+"没有输入路径");
            }
            //判断节点的出度
            if(obj[i].getAttribute("from")){
                if(obj[i].getAttribute("routertype")=="H"&&obj[i].getAttribute("from").split(",").length<2){
                    myerrors.push(obj[i].getAttribute("title")+"为并行节点,应该有二条以上的输出路径");
                }
                if(obj[i].getAttribute("routertype")=="D"&&obj[i].getAttribute("from").split(",").length<2){
                    myerrors.push(obj[i].getAttribute("title")+"为分支节点,应该有二条以上的输出路径");
                }
                if(obj[i].getAttribute("nodetype")=="C"&&obj[i].getAttribute("from").split(",").length>1){
                    myerrors.push(obj[i].getAttribute("title")+"为一般节点,应只有一条输出路径");
                }
            }
        }
    }
    //判断首节点个数
    if(hd>1)
        myerrors.push("首节点应只有一个，实际为"+hd+"个");
    //判断并行、多实例、汇聚个数
    if(hjs!=(bxs+gjs)){
        myerrors.push("汇聚节点个数应该等于并行节点个数和多实例节点个数之和");
    }
    var errors="";
    for(var m=0;m<myerrors.length;m++){
        errors=errors+myerrors[m]+"\n";
    }
    if(errors=="")
        alert("校验完成，这是一个合法的流程图！");
    else{
        alert(errors);
    }
    return errors;
}
/**
 * 生成xml空文档  此方法在getXml()方法中会具体实现，就是实现一个空的xml文档，saveXml()方法实现具体的节点内容
 * @returns {*}
 */
function createNewXml(){
    var xmlDom = null;
    if (window.ActiveXObject){//IE
        xmlDom = new ActiveXObject("Microsoft.XMLDOM");
        xmlDom.async=false;
    }
    else if(document.implementation && document.implementation.createDocument){
        xmlDom = document.implementation.createDocument("", "", null);
    }else{
        xmlDom = null;
    }
    return xmlDom;
}
/**
 * 流程图保存成xml文档
 */
function saveXml(){
    try{
        if(!controlsave){
            return ;
        }
        var xmlDoc = createNewXml(),
            xmlSer=new XMLSerializer(),
            newPI=xmlDoc.createProcessingInstruction("xml","version=\"1.0\" encoding=\"utf-8\""),
            obj = g("s3").childNodes,//节点对象集合
            lineObj = g("s2").childNodes,//线对象集合
            xmlString,commitFlow,flow,nodes,node,baseProperties,VMLProperties,transitions,transition,LabelProperties,LbaseProperties,LVMLProperties;

        commitFlow = xmlDoc.createElement("CommitFlow");//根节点
        flow = xmlDoc.createElement("Flow");//流程开始
        flow.setAttribute("code",SVG.get("s0").attr("flow-code"));//流程code
        flow.setAttribute("name",SVG.get("s0").attr("flow-name"));//流程名称
        flow.setAttribute("type",SVG.get("s0").attr("flow-type"));//流程类型
        flow.setAttribute("desc",SVG.get("s0").attr("flow-desc"));//流程描述
        nodes = xmlDoc.createElement("Nodes");//过程节点集合
        transitions = xmlDoc.createElement("Transitions");//线条集合

        xmlDoc.appendChild(newPI);
        xmlDoc.appendChild(commitFlow);
        commitFlow.appendChild(flow);
        flow.appendChild(nodes);
        flow.appendChild(transitions);
        var s = validate();
        if(s!=""){
            if(!confirm("是否要继续保存该流程？"))
                return;
        }
        for( var i=0,len=obj.length;i<len;i++ ){
            var textID = obj[i].getAttribute("textID");
            node = xmlDoc.createElement("Node");
            baseProperties = xmlDoc.createElement("BaseProperties");
            VMLProperties = xmlDoc.createElement("VMLProperties");
debugger
            switch(obj[i].getAttribute("shapetype")){
                case "double-oval"://开始节点、结束节点
                    addNode(baseProperties,{//属性信息
                        "id":obj[i].getAttribute("id"),//节点ID
                        "name":replaceToXml(obj[i].getAttribute("title")),//节点名称
                        "nodetype":obj[i].getAttribute("nodetype")//节点类型
                    });
                    addNode(VMLProperties,{//位置形状信息
                        "shapetype":obj[i].getAttribute("shapetype"),
                        "width":obj[i].getAttribute("width"),
                        "height":obj[i].getAttribute("height"),
                        "x":obj[i].getAttribute("x"),
                        "y":obj[i].getAttribute("y")
                    });
                    break;

                case "roundRect":
                    addNode(baseProperties,{//属性信息
                        "id":obj[i].getAttribute("id"),//节点ID
                        "name":replaceToXml(obj[i].getAttribute("title")),//节点名称
                        "nodetype":obj[i].getAttribute("nodetype"),//节点类型
                        "flowphase":obj[i].getAttribute("flowphase"),//节点阶段
                        "nodedesc":obj[i].getAttribute("nodedesc"),//节点描述
                        "nodecode":obj[i].getAttribute("nodecode"),//节点代码
                        "osid":obj[i].getAttribute("osid"),//osid
                        "opttype":obj[i].getAttribute("opttype"),//操作类别
                        "optcode":obj[i].getAttribute("optcode"),//业务代码
                        "optbean":obj[i].getAttribute("optbean"),//业务注入
                        "optparam":obj[i].getAttribute("optparam"),//操作参数
                        "subwfcode":obj[i].getAttribute("subwfcode"),//子流程
                        "roletype":obj[i].getAttribute("roletype"),//角色类别
                        "rolecode":obj[i].getAttribute("rolecode"),//角色代码
                        "isaccounttime":obj[i].getAttribute("isaccounttime"),//是否计时
                        "timeLimitType":obj[i].getAttribute("timeLimitType"),//期限类别
                        "inheritType":obj[i].getAttribute("inheritType"),//期限继承类别
                        "inheritNodeCode":obj[i].getAttribute("inheritNodeCode"),//继承环节代码
                        "timeLimit":obj[i].getAttribute("timeLimit"),//期限时间
                        "isTrunkLine":obj[i].getAttribute("isTrunkLine"),//是否为主干节点
                        "unitexp":obj[i].getAttribute("unitexp"),//机构表达式
                        "powerexp":obj[i].getAttribute("powerexp"),//权限表达式
                        "expireopt":obj[i].getAttribute("expireopt"),//预期处理办法
//                        "riskinfo":obj[i].getAttribute("riskinfo"),//风险信息
                        "warningrule":obj[i].getAttribute("warningrule"),//预警规则
                        "warningparam":obj[i].getAttribute("warningparam")//预警时间参数
                    });
                    addNode(VMLProperties,{
                        "shapetype":obj[i].getAttribute("shapetype"),
                        "width":obj[i].getAttribute("width"),
                        "height":obj[i].getAttribute("height"),
                        "x":obj[i].getAttribute("x")+"px",
                        "y":obj[i].getAttribute("y")+"px"
                    });
                    break;

                case "oval-multi":
                    addNode(baseProperties,{//属性信息
                        "id":obj[i].getAttribute("id"),//节点ID
                        "name":replaceToXml(obj[i].getAttribute("title")),//节点名称
                        "nodetype":obj[i].getAttribute("nodetype"),//节点类型
                        "routertype":obj[i].getAttribute("routertype"),//路由类别
                        "flowphase":obj[i].getAttribute("flowphase"),//节点阶段
                        "nodedesc":obj[i].getAttribute("nodedesc"),//节点描述
                        "nodecode":obj[i].getAttribute("nodecode"),//节点代码
                        "osid":obj[i].getAttribute("osid"),//osid
                        "roletype":obj[i].getAttribute("roletype"),//角色类别
                        "rolecode":obj[i].getAttribute("rolecode"),//角色代码
                        "isTrunkLine":obj[i].getAttribute("isTrunkLine"),//是否为主干节点
                        "unitexp":obj[i].getAttribute("unitexp"),//机构表达式
                        "powerexp":obj[i].getAttribute("powerexp"),//权限表达式
                        "multiinsttype":obj[i].getAttribute("multiinsttype"),//多实例类别
                        "multiinstparam":obj[i].getAttribute("multiinstparam")//多实例变量
                    });
                    addNode(VMLProperties,{//位置形状信息
                        "shapetype":obj[i].getAttribute("shapetype"),
                        "width":obj[i].getAttribute("width"),
                        "height":obj[i].getAttribute("height"),
                        "x":obj[i].getAttribute("x"),
                        "y":obj[i].getAttribute("y")
                    });
                    break;
                case "oval-ju":
                    addNode(baseProperties,{//属性信息
                        "id":obj[i].getAttribute("id"),//节点ID
                        "name":replaceToXml(obj[i].getAttribute("title")),//节点名称
                        "nodetype":obj[i].getAttribute("nodetype"),//节点类型
                        "routertype":obj[i].getAttribute("routertype"),//路由类别
                        "flowphase":obj[i].getAttribute("flowphase"),//节点阶段
                        "nodedesc":obj[i].getAttribute("nodedesc"),//节点描述
                        "nodecode":obj[i].getAttribute("nodecode"),//节点代码
                        "osid":obj[i].getAttribute("osid"),//osid
                        "isTrunkLine":obj[i].getAttribute("isTrunkLine"),//是否为主干节点
                        "convergetype":obj[i].getAttribute("convergetype"),//汇聚条件类别
                        "convergeparam":obj[i].getAttribute("convergeparam"),//汇聚参数
                        "optbean":obj[i].getAttribute("optbean")//汇聚外埠判断bean
                    });
                    addNode(VMLProperties,{//位置形状信息
                        "shapetype":obj[i].getAttribute("shapetype"),
                        "width":obj[i].getAttribute("width"),
                        "height":obj[i].getAttribute("height"),
                        "x":obj[i].getAttribute("x"),
                        "y":obj[i].getAttribute("y")
                    });
                    break;
                case "oval-fen":
                case "oval-bing":
                case "oval-you":
                case "oval-tong":
                    addNode(baseProperties,{//属性信息
                        "id":obj[i].getAttribute("id"),//节点ID
                        "name":replaceToXml(obj[i].getAttribute("title")),//节点名称
                        "nodetype":obj[i].getAttribute("nodetype"),//节点类型
                        "routertype":obj[i].getAttribute("routertype"),//路由类别
                        "flowphase":obj[i].getAttribute("flowphase"),//节点阶段
                        "nodedesc":obj[i].getAttribute("nodedesc"),//节点描述
                        "nodecode":obj[i].getAttribute("nodecode"),//节点代码
                        "osid":obj[i].getAttribute("osid"),//osid
                        "isTrunkLine":obj[i].getAttribute("isTrunkLine")//是否为主干节点
                    });
                    addNode(VMLProperties,{//位置形状信息
                        "shapetype":obj[i].getAttribute("shapetype"),
                        "width":obj[i].getAttribute("width"),
                        "height":obj[i].getAttribute("height"),
                        "x":obj[i].getAttribute("x"),
                        "y":obj[i].getAttribute("y")
                    });
                    break;
                default :break;
            }
            nodes.appendChild(node);
            node.appendChild(baseProperties);
            node.appendChild(VMLProperties);
        }

        for(var j=0,jLen=lineObj.length;j<jLen;j++){
                transition = xmlDoc.createElement("Transition");
                LbaseProperties = xmlDoc.createElement("BaseProperties");//基本属性信息
                LVMLProperties = xmlDoc.createElement("VMLProperties");//位置形状信息
                LabelProperties = xmlDoc.createElement("LabelProperties");//标签消息
                addNode(LbaseProperties,{
                    "id":lineObj[j].getAttribute("id"),
                    "name":replaceToXml(lineObj[j].getAttribute("title")),
                    "from":lineObj[j].getAttribute("from"),
                    "to":lineObj[j].getAttribute("to"),
                    "cond":lineObj[j].getAttribute("cond"),
                    /*"cond":replaceToXml(lineObj[j].getAttribute("cond")),*/
                    "desc":replaceToXml(lineObj[j].getAttribute("desc")),
                    "timeLimit":replaceToXml(lineObj[j].getAttribute("timeLimit")),
                    "timeLimitType":lineObj[j].getAttribute("timeLimitType"),
                    "inheritType":lineObj[j].getAttribute("inheritType"),
                    "inheritNodeCode":lineObj[j].getAttribute("inheritNodeCode"),
                    "isaccounttime":lineObj[j].getAttribute("isaccounttime"),//是否计入执行时间
                    "canignore":lineObj[j].getAttribute("canignore")//是否可以忽略运行
                });
                addNode(LVMLProperties,{
                    "points":(lineObj[j].getAttribute("points")),
                    "shapetype":"PolyLine"
                });
                addNode(LabelProperties,{
                    "id":lineObj[j].getAttribute("labID"),
                    "width":g(lineObj[j].getAttribute("labID")).offsetWidth+"px",
                    "height":"24px",
                    "x":g(lineObj[j].getAttribute("labID")).style.left,
                    "y":g(lineObj[j].getAttribute("labID")).style.top
                });
                transitions.appendChild(transition);
                transition.appendChild(LbaseProperties);
                transition.appendChild(LVMLProperties);
                transition.appendChild(LabelProperties);
        }

        if(xmlDoc.xml){
            xmlString = xmlDoc.xml;
        }else{
            xmlString = xmlSer.serializeToString(xmlDoc);
        }
debugger
        $.ajax({
        	type:"post",
        	url: path+saveXmlAdd+flowCode,
        	data:{flowxmldesc:xmlString},
        	success: function (data) {
                alert(data.data);
                changeSaveStatus('unsave');
            },
        	error:function(data){
        		alert("保存出错！");
        	}
        });
    }
    catch(e){
        alert(e);
    }
}
