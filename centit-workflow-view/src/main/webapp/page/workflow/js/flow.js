/**
 * Created by gyr on 2015-4-27.
 */
/**
 * 全局对象
 */
var c = SVG("canvas"),
    gLine = c.group(),//线条组对象
    gShape = c.group(),//矩形、菱形、圆形等节点形状组对象
    gCircle = c.group(),//圆形组对象
    gPath = c.group(),//路径组对象
    gText = c.group(),//文字组对象
    marker = c.marker(),//创建一个标记对象，这里为箭头标记
    markerRed = c.marker(),
    markerBlue = c.marker(),
    g = function (id) {
        return document.getElementById(id);
    },
    canvas = g("canvas"),_ZOOM = 1;
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
    o_feOffset.setAttribute("dx", 1);
    o_feOffset.setAttribute("dy", 1);
    o_feGaussianBlur.setAttribute("result", "blurOut");
    o_feGaussianBlur.setAttribute("in", "offOut");
    o_feGaussianBlur.setAttribute("stdDeviation", 0);
    o_feBlend.setAttribute("in", "SourceGraphic");
    o_feBlend.setAttribute("in2", "blurOut");
    o_feBlend.setAttribute("mode", "normal");
    o_filter.setAttribute("x", 0);
    o_filter.setAttribute("y", 0);
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
    marker.path().attr({"d": "m 0 0 L 7 4 L 0 7 z"}).fill("#000000");
    markerRed.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    markerRed.path().attr({"d": "M 0 0 L 7 4 L 0 7 z"}).fill("#FF0000");
    markerBlue.attr({
        "viewBox": "0 0 8 8",
        "refX": 6,
        "refY": 4,
        "markerUnits": "strokeWidth",
        "markerWidth": 5,
        "markerHeight": 5,
        "orient": "auto"
    });
    markerBlue.path().attr({"d": "M 0 0 L 7 4 L 0 7 z"}).fill("#0000FF");
}
/**
 * 加载xml文档
 * @param xml
 */
function loadXml(xml,viewXml) {
    var xmlDoc,viewXmlDoc;//xml文档对象
    if (window.ActiveXObect) {//IE浏览器
        xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.loadXML(xml);
        viewXmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        viewXmlDoc.loadXML(viewXml);
    }
    else if (window.DOMParser) {
        parser = new DOMParser();
        xmlDoc = parser.parseFromString(xml, "text/xml");
        viewXmlDoc = parser.parseFromString(viewXml, "text/xml");
    }
    var getShape = xmlDoc.getElementsByTagName("Node"),//节点对象
        getLine = xmlDoc.getElementsByTagName("Transition"),//流转对象
        xmlStep = viewXmlDoc.getElementsByTagName("Step"),
        xmlProc = viewXmlDoc.getElementsByTagName("Proc"),
        number_Proc = xmlProc.length;
    for (var i = 0, len = getShape.length; i < len; i++) {
        var shapeArgs,nodeType = attrValue(getShape[i], "BaseProperties", "nodetype");//节点类型
        if(nodeType == 'C'||nodeType=='B'){//业务节点、首节点（首节点其实也是业务节点）
            shapeArgs = {
                shapetype: attrValue(getShape[i], "VMLProperties", "shapetype"),//节点形状类型
                width: parseInt(attrValue(getShape[i], "VMLProperties", "width")),
                height: parseInt(attrValue(getShape[i], "VMLProperties", "height")),
                id: attrValue(getShape[i], "BaseProperties", "id"),
                x: parseInt(attrValue(getShape[i], "VMLProperties", "x")),
                y: parseInt(attrValue(getShape[i], "VMLProperties", "y")),
                title: attrValue(getShape[i], "BaseProperties", "name"),
                nodetype: nodeType,//节点类型
                filter: "url(#filter-rect)"//阴影过滤器
            };
        }
        else if(nodeType=='R'){//路由节点
            shapeArgs = {
                shapetype: attrValue(getShape[i], "VMLProperties", "shapetype"),//节点形状类型
                width: parseInt(attrValue(getShape[i], "VMLProperties", "width")),
                height: parseInt(attrValue(getShape[i], "VMLProperties", "height")),
                id: attrValue(getShape[i], "BaseProperties", "id"),
                cx: parseInt(attrValue(getShape[i], "VMLProperties", "cx")),
                cy: parseInt(attrValue(getShape[i], "VMLProperties", "cy")),
                nodetype: nodeType,//节点类型
                routertype: attrValue(getShape[i], "BaseProperties", "routertype"),//路由类别
                filter: "url(#filter)"//阴影过滤器
            }
        }
        else{
            shapeArgs = {//开始节点、结束节点
                shapetype: attrValue(getShape[i], "VMLProperties", "shapetype"),//节点形状类型
                width: parseInt(attrValue(getShape[i], "VMLProperties", "width")),
                height: parseInt(attrValue(getShape[i], "VMLProperties", "height")),
                id: attrValue(getShape[i], "BaseProperties", "id"),
                cx: parseInt(attrValue(getShape[i], "VMLProperties", "cx")),
                cy: parseInt(attrValue(getShape[i], "VMLProperties", "cy")),
                title: attrValue(getShape[i], "BaseProperties", "name"),
                nodetype: nodeType,
                nodecode:"",
                nodedesc:"",
                filter: "url(#filter)"//阴影过滤器
            }
        }
        var t,shape;
        switch (shapeArgs.shapetype) {//根据节点类型分别画不同形状
            case "double-oval" ://开始节点、结束节点
            	 //画形状
            	shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx ).cy(shapeArgs.cy)
                .attr(shapeArgs).fill("#fff").stroke({color:"#00f"}) ;
                //画文字
                t = gText.text(dealStr(shapeArgs.title, shapeArgs.width)).fill("#000")
                    .x(shapeArgs.cx).y(shapeArgs.cy+ 6).font({
                        size: 14,
                        "text-anchor": "middle"
                    })
                    .attr({dx: shapeArgs.cx, "shapeID": shape.attr("id")});
                shape.attr({"textID": t.attr("id")});
                break;
            case "roundRect" ://业务节点
                shape = gShape.rect(shapeArgs.width, shapeArgs.height).x(shapeArgs.x).y(shapeArgs.y)
                    .stroke({color: "#000", width: 1}).fill("#fff").attr(shapeArgs).attr({rx: 2, ry: 2});
                t = gText.text(dealStr(shapeArgs.title, shapeArgs.width)).fill("#000")
                    .x(shapeArgs.x).y(shapeArgs.y + shapeArgs.height / 2 + 5).font({
                        size: 14,
                        "text-anchor": "middle"
                    })
                    .attr({dx: shapeArgs.x + shapeArgs.width / 2, "shapeID": shape.attr("id")});
                shape.attr({"textID": t.attr("id")});
                break;
            case "oval-fen"://路由分支节点
//                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
//                    .fill("url(#grad1)").attr(shapeArgs);
            	shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
                t = drawFenPath(shapeArgs.cx,shapeArgs.cy).attr({"shapeID":shape.attr("id")});
                shape.attr({"pathID": t.attr("id")});
                break;
            case "oval-multi"://路由多实例节点
                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("url(#grad1)").attr(shapeArgs);
                t = drawMultiPath(shapeArgs.cx-shapeArgs.width/3+6,shapeArgs.cy-4).attr({"shapeID":shape.attr("id")});
                shape.attr({"pathID": t.attr("id")});
                break;
            case "oval-ju"://路由汇聚节点
//                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
//                    .fill("url(#grad1)").attr(shapeArgs);
//                t = drawJuPath(shapeArgs.cx,shapeArgs.cy+10).attr({"shapeID":shape.attr("id")});
//                shape.attr({"pathID": t.attr("id")});
//                break;
            	shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
	            t = drawJuPath(shapeArgs.cx,shapeArgs.cy+10).attr({"shapeID":shape.attr("id")});
	            specialAttr = {
	                "pathID": t.attr("id"),
	                "convergetype":attrValue(getShape[i], "BaseProperties", "convergetype"),//汇聚条件类别
	                "convergeparam":attrValue(getShape[i], "BaseProperties", "convergeparam"),//汇聚参数
	                "optbean":attrValue(getShape[i], "BaseProperties", "optbean")//汇聚外埠判断
	            };
	            shape.attr(specialAttr);
	            break;
            case "oval-bing"://路由并行节点
//                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
//                    .fill("url(#grad1)").attr(shapeArgs);
//                t = drawBingPath(shapeArgs.cx,shapeArgs.cy).attr({"shapeID":shape.attr("id")});
//                shape.attr({"pathID": t.attr("id")});
//                break;
                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                .fill("#fff").attr(shapeArgs).stroke({color:"#00f"});
	            t = drawBingPath(shapeArgs.cx,shapeArgs.cy).attr({"shapeID":shape.attr("id")});
	            shape.attr({"pathID": t.attr("id")});
	            break;
            case  "oval-tong"://路由同步节点
                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("url(#grad1)").attr(shapeArgs);
                var pt = drawTongPath(shapeArgs.cx,shapeArgs.cy);
                pt.path1.attr({"shapeID":shape.attr("id")});pt.path2.attr({"shapeID":shape.attr("id")});
                shape.attr({"path1":pt.path1.attr("id"),"path2":pt.path2.attr("id")});
                break;
            case "oval-you"://路由游离节点
                shape = gShape.ellipse(shapeArgs.width, shapeArgs.height).cx(shapeArgs.cx).cy(shapeArgs.cy)
                    .fill("url(#grad1)").attr(shapeArgs);
                t = drawYouPath(shapeArgs.cx,shapeArgs.cy).attr({"shapeID":shape.attr("id")});
                shape.attr({"pathID": t.attr("id")});
                break;
            default :break;
        }

    }
    //画线
    for (var j= 0,jLen = getLine.length;j<jLen;j++){
        var line = gLine.polyline().stroke({width:1.3,color:"#00f"}).fill("none")
            .attr({'transform':"","marker-end":"url(#"+marker.attr("id")+")",
                "id":attrValue(getLine[j],"BaseProperties","id"),
                "title":attrValue(getLine[j],"BaseProperties","name"),
                "shapetype":"polyline",
                "points":(attrValue(getLine[j],"VMLProperties","points")),
                "from":attrValue(getLine[j],"BaseProperties","from"),
                "to":attrValue(getLine[j],"BaseProperties","to"),
                "labID":attrValue(getLine[j],"LabelProperties","id")
            });
        //流转对象（线条）名称
        $("#lineCon").append("<div class='step' style='left:"+attrValue(getLine[j],"LabelProperties","x")+";top:"+attrValue(getLine[j],"LabelProperties","y")+";' id='"+attrValue(getLine[j],"LabelProperties","id")+"'>"+attrValue(getLine[j],"BaseProperties","name")+"</div>");
    }
    //对已完成的流程进行颜色变化
    for(var k=0; k<xmlStep.length;k++){
        var step = xmlStep[k];
        redrawLine({
            id:step.getAttribute("id"),
            inststate:step.getAttribute("inststate")
        });
    }
    for(k=0; k<number_Proc;k++){
        var proc = xmlProc[k];
        redrawProc({
            id:proc.getAttribute("id"),
            inststate:proc.getAttribute("inststate")
        });
    }
    if(isbrowser()=="FF"){
        $("#canvas").height(document.documentElement.scrollHeight +100);//高度适应
    }
    else{
        $("#canvas").height(document.body.scrollHeight +100);//高度适应
    }
}
/**
 * 将线变红色
 * @param option
 */
function redrawLine(option){
    var options = option || {},
        id = options.id,
        inststate = parseInt(options.inststate);
    if(g(id)&&inststate>0){
        g(id).setAttribute("marker-end","url(#"+markerRed.attr("id")+")");
        g(id).setAttribute("stroke", "#FF0000");
    }
}
/**
 * 根据过程状态设置颜色
 * @param option
 */
function redrawProc(option){
    var options = option || {},
        id = options.id,
        inststate = options.inststate;
    switch(inststate){
        case "complete":
            if(g(id)){
                if(g(id).getAttribute("nodetype")=="R"){
                    if(g(id).getAttribute("routertype")=="S"){//同步节点
                        g(g(id).getAttribute("path1")).setAttribute("stroke","#0000ff");
                        g(g(id).getAttribute("path1")).setAttribute("marker-end","url(#"+markerBlue.attr("id")+")");
                        g(g(id).getAttribute("path2")).setAttribute("stroke","#0000ff");
                        g(g(id).getAttribute("path2")).setAttribute("marker-end","url(#"+markerBlue.attr("id")+")");
                    }
                    else{
                        g(g(id).getAttribute("pathID")).setAttribute("stroke","#0000ff");
                    }
                }
                else{
                    g(id).setAttribute("stroke", "#0000ff");
                    g(g(id).getAttribute("textID")).setAttribute("fill","#0000ff");
                }
                g(id).setAttribute("state", "complete");//设置状态完成
            }
            break;
        case "waiting":
            if(g(id)){
                if(g(id).getAttribute("nodetype")=="R"){
                    if(g(id).getAttribute("routertype")=="S"){//同步节点
                        g(g(id).getAttribute("path1")).setAttribute("stroke","#FF0000");
                        g(g(id).getAttribute("path1")).setAttribute("marker-end","url(#"+markerRed.attr("id")+")");
                        g(g(id).getAttribute("path2")).setAttribute("stroke","#FF0000");
                        g(g(id).getAttribute("path2")).setAttribute("marker-end","url(#"+markerRed.attr("id")+")");
                    }
                    else{
                        g(g(id).getAttribute("pathID")).setAttribute("stroke","#FF0000");
                    }
                }
                else{
                    g(id).setAttribute("stroke", "#FF0000");
                    g(g(id).getAttribute("textID")).setAttribute("fill","#FF0000");
                }
                g(id).setAttribute("state", "waiting");//设置状态处理中
            }
            break;
        case "ready":
            break;
        default:
            break;
    }
}
/**
 * 判断浏览器
 * @returns {string}
 */
function isbrowser() {
    var userAgent = navigator.userAgent,
        isOpera = userAgent.indexOf("Opera") > -1;

    if (userAgent.indexOf("Opera") > -1) {
        return "Opera";
    }
    if (userAgent.indexOf("Firefox") > -1) {
        return "FF";
    }
    if (userAgent.indexOf("Safari") > -1) {
        return "Safari";
    }
    if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
        return "IE";
    }
}
//绑定事件
var addEvent = function (o, eventType, fn) {
    if (document.addEventListener) {
        o.addEventListener(eventType, fn, true);
    } else if (document.attachEvent) {
        o.attachEvent('on' + eventType, fn);
    } else {
        o['on' + eventType] = fn;
    }
};
//移除绑定
var removeEvent = function (o, eventType, fn) {
    if (document.removeEventListener) {
        document.removeEventListener(eventType, fn, true);
    } else if (document.detachEvent) {
        o.detachEvent('on' + eventType, fn);
    }
};
//处理字串方法
String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
};
/**
 * 截取多余的字串
 * @param str
 * @param wid
 * @returns {*}
 */
function dealStr(str, wid) {
    var length;
    if (!wid) {
        length = 5;
    }
    else {
        length = parseInt(wid) / 15;
    }
    if (str.length == 0) {
        return "";
    }
    else {
        if (str.trim().length > length) {
            return str.substr(0, length) + "...";
        } else {
            return str;
        }
    }
}
/**
 * 特殊字符转义
 * @param str
 * @returns {string}
 */
function replaceToXml(str)
{
    if (str){
        var str2;
        str2=str.replace(/&/g,"&amp;");
        str2=str2.replace(/</g,"&lt;");
        str2=str2.replace(/>/g,"&gt;");
        str2=str2.replace(/"/g,"&quot;");
        str2=str2.replace(/'/g,"&apos;");
        return str2;
    }
    else{
        return "";
    }
}
/**
 * 特殊字符转义
 * @param str
 * @returns {string}
 */
function escapeXml(str){
    if (str){
        var str2;
        str2=str.replace(/&amp;/g,"&");
        str2=str2.replace(/&lt;/g,"<");
        str2=str2.replace(/&gt;/g,">");
        str2=str2.replace(/&quot;/g,"\"");
        str2=str2.replace(/&apos;/g,"\'");
        return str2;
    }
    else{
        return "";
    }
}
/**
 * 获取节点内容
 * @param o
 * @param nodeName
 * @returns {*}
 */
function getValue(o, nodeName) {
    if (o.text != undefined) {
        return o.text;
    } else {
        return o.textContent;
    }
}
/**
 * 获取节点属性内容
 * @param o
 * @param nodeName
 * @param attrName
 * @returns {string}
 */
function attrValue(o, nodeName, attrName) {
    return o.getElementsByTagName(nodeName)[0].getAttribute(attrName) ? o.getElementsByTagName(nodeName)[0].getAttribute(attrName) : "";
}
/**
 * 获取节点内容
 * @param o
 * @param nodeName
 * @returns {*}
 */
function nodeValue(o, nodeName) {
    if (o.getElementsByTagName(nodeName)[0]) {
        if (o.getElementsByTagName(nodeName)[0].textContent) {
            return o.getElementsByTagName(nodeName)[0].textContent;
        } else {
            return o.getElementsByTagName(nodeName)[0].text;
        }
    }
}
//添加样式集合
var addStyle = function (o, obj) {
    if (!o) return false;
    if ((typeof obj) != "object") return false;
    for (var able in obj) {
        o.style[able] = obj[able];
    }
};

/**
 * 给节点添加内容，这里兼容了IE9  和chrome
 * @param o
 * @param content
 */
function setValue(o, content) {
    if (o.text) {
        o.text = content;
    } else {
        o.textContent = content;
    }
}
//给节点添加属性
var addNode = function (o, obj) {
    if (!o) return false;
    if ((typeof obj) != "object") return false;
    for (var able in obj) {
        o.setAttribute(able, obj[able]);
    }
};
///**
// * 画路由汇聚节点内部
// * @param x
// * @param y
// * @returns {*}
// */
//function drawJuPath(x,y){
//    var path;
//    path = gPath.path().attr({"d":"M "+(x-15)+" "+(y-10-15)+" L "+x+" "+(y-10)+" L "+(x+15)+" "+(y-10-15)
//    +"M "+(x-15)+" "+(y-15)+" L "+x+" "+y+" L "+(x+15)+" "+(y-15)
//    +"M "+(x-15)+" "+(y+10-15)+" L "+x+" "+(y+10)+" L "+(x+15)+" "+(y+10-15)}).stroke({width:1.3,color:"#000"}).fill("none");
//    return path;
//}

/**
 * 画路由汇聚节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawJuPath(x, y, id) {
    if (id) {
        SVG.get(id).attr({
            "d": "M " + (x - 10) + " " + (y - 20) + " L " + x + " " + (y - 10) + " L " + (x + 10) + " " + (y - 20)
            + "M " + (x - 10) + " " + (y - 12) + " L " + x + " " + (y - 3) + " L " + (x + 10) + " " + (y - 12)
            + "M " + (x - 10) + " " + (y - 5) + " L " + x + " " + (y + 4) + " L " + (x + 10) + " " + (y - 4)
        });
        return false;
    } else {
        var path = gPath.path().attr({
            "d": "M " + (x - 10) + " " + (y - 20) + " L " + x + " " + (y - 10) + " L " + (x + 10) + " " + (y - 20)
            + "M " + (x - 10) + " " + (y - 12) + " L " + x + " " + (y - 3) + " L " + (x + 10) + " " + (y - 12)
            + "M " + (x - 10) + " " + (y - 5) + " L " + x + " " + (y + 4) + " L " + (x + 10) + " " + (y - 4)
        }).stroke({width: 1.3, color: "#00f"}).fill("none");
        return path;
    }
}



///**
// * 画路由并行节点内部
// * @param x
// * @param y
// * @returns {*}
// */
//function drawBingPath(x,y){
//    var path;
//    path = gPath.path().attr({"d":"M "+(x-20)+" "+(y-10)+" h 40"
//    +"M "+(x-20)+" "+y+" h 40"
//    +"M "+(x-20)+" "+(y+10)+" h 40"}).stroke({width:1.3,color:"#000"}).fill("none");
//    return path;
//}
/**
 * 画路由并行节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawBingPath(x, y, id) {
    if (id) {
        SVG.get(id).attr({
            "d": "M " + (x - 11) + " " + (y - 8) + " h 23"
            + "M " + (x - 11) + " " + y + " h 23"
            + "M " + (x - 11) + " " + (y + 8) + " h 23"
        });
        return false;
    } else {
        var path = gPath.path().attr({
            "d": "M " + (x - 11) + " " + (y - 8) + " h 23"
            + "M " + (x - 11) + " " + y + " h 23"
            + "M " + (x - 11) + " " + (y + 8) + " h 23"
        }).stroke({width: 1.3, color: "#00f"}).fill("none");
        return path;
    }
}



/**
 * 画多实例节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawMultiPath(x,y){
    var path;
    path = gPath.path().attr({"d":"M "+x+" "+y+" h 40 v 20 h -40 v -20 M"
    +(x+6)+" "+y+" v -6 h 40 v 20 h -6 M "
    +(x+12)+" "+(y-6)+" v -6 h 40 v 20 h -6"}).stroke({width:1.3,color:"#000"}).fill("none");
    return path;
}
/**
 * 画同步节点内部
 * @param x
 * @param y
 */
function drawTongPath(x,y){
    var R = 20,ros = 15,path1,path2;
    path1 = gPath.path().attr({"d":"M "+(x+R*Math.cos(ros/180*Math.PI))+" "+(y-R*Math.sin(ros/180*Math.PI))
    +" A "+R+" "+R+" 0 0 0 "+(x+R*Math.cos((180-ros)/180*Math.PI))+" "+(y-R*Math.sin((180-ros)/180*Math.PI))})
        .stroke({width:1.3,color:"#000"}).fill("none").attr({"marker-end":"url(#"+marker.attr("id")+")"});
    path2 = gPath.path().attr({"d":"M "+(x-R*Math.cos(ros/180*Math.PI))+" "+(y+R*Math.sin(ros/180*Math.PI))
    +" A "+R+" "+R+" 0 0 0 "+(x-R*Math.cos((180-ros)/180*Math.PI))+" "+(y+R*Math.sin((180-ros)/180*Math.PI))})
        .stroke({width:1.3,color:"#000"}).fill("none").attr({"marker-end":"url(#"+marker.attr("id")+")"});
    return {path1:path1,path2:path2};
}
/**
 * 画游离节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawYouPath(x,y){
    var path;
    path = gPath.path().attr({"d":"M "+(x-10)+" "+y+" v -20 h 20 v 20 l -15 -10 l -5 10 M "
    +(x-10)+" "+(y+5)+" v 15 h 20 v -15 l -15 -5 l -5 5"}).stroke({color:"#000",width:1.3}).fill("none");
    return path;
}
/**
 * 画分支节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawFenPath(x,y,id){
	if (id) {
        SVG.get(id).attr({"d": "M " + (x - 15) + " " + y + " l 15 -15 l 15 15 l -15 15 z"});
        return false;
    } else {
        var path = gPath.path().attr({"d": "M " + (x - 15) + " " + y + " l 15 -15 l 15 15 l -15 15 z"}).fill("none").stroke({
            color: "#00f",
            width: 1.3
        });
        return path;
    }
	
//    var path;
//    path = gPath.path().attr({"d":"M "+(x-30)+" "+y+" l 30 -30 l 30 30 l -30 30 l -30 -30"})
//        .fill("none").stroke({color:"#000",width:1.3});
//    return path;
}
/**
 * 关闭节点信息查看窗口
 */
function closeModal(){
    $("#nodeInfo").hide();
}
/**
 * 点击查询节点状态及操作日志
 * @param event
 * @returns {boolean}
 */
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
                if(data.instance!=null){
                    //拼接字符串
                    htmlString = "<ul class='first-menu'>";
                    $("#nodeHeading").html(data.nodename);//设置标题
                    var instances=eval(data.instance);
                    for(var i=0;i<instances.length;i++){
                        htmlString += "<li><a>当前状态为<span class='red'>"+instances[i].state+"</span>,创建于<span class='blue'>"
                        +instances[i].createtime+"</span><i></i></a>";
                        if(instances[i].action!=null){
                            htmlString += "<div class='sort'><div class='sort-bg'></div><ul>";
                            var actions=eval(instances[i].action);
                            for(j=0;j<actions.length;j++){
                                htmlString += "<li><a>"+actions[j].username+"于<span class='blue'>"
                                +actions[j].actiontime+"</span>"+actions[j].actiontype;
                            }
                            htmlString += "</ul></div>"
                        }
                        else if (instances[i].task!=null){
                            var tasks=eval(instances[i].task);
                            var j=tasks.length;
                            var arry = new Array();
                            for(var m=0;m<j;m++){
                                arry.push(tasks[m].username);
                            }
                            htmlString += "<div class='sort'><ul><li><a>"+arry.join("，")+"</a></li></ul></div>";
                        }
                    }
                    htmlString += "</ul>";
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
moveTip( g("nodeInfo"),g("nodeHeading") );
addEvent(g("canvas"), "mousedown", initEvt);