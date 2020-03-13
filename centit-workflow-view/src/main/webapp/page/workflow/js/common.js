/**
 * Created by gyr on 2015-4-9.
 * 通用函数
 */
// 获取url参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null) return  decodeURIComponent(r[2]); return null;
}
	
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
    markerGreen = c.marker(),
    markerGrey = c.marker(),
    markerBlue = c.marker();
var o,//当前选中对象id
    selectTag,//工具栏选中对象
    image = "default",//画图标志
    _ZOOM = 1,//缩放比例
    moveflag = 1,//拖动缩放标志
    ptMoveType = "",//拖动点是否是第一个

    // 获取scrollTop有问题，为了减少代码改动加入事件
    scrollElement = getScrollInfo();
    function getScrollInfo() {
        return {
            scrollTop: $(window).scrollTop(),
            scrollLeft: $(window).scrollLeft()
        };
    }
    $(window).on('scroll', function() {
        scrollElement = getScrollInfo();
    });

//放置图片的全局变量
var startImg = "viewimage/ks.png",           //开始节点
    endImg = "viewimage/js.png",            //结束节点
    serviceImg = "viewimage/yb.png",    //业务节点
    branchImg = "viewimage/fz.png",        //分支节点
    convergeImg = "viewimage/hj.png",       //汇聚
    multipleImg = "viewimage/dsl.png",       //多实例节点
    parallelImg = "viewimage/bx.png",       //并行节点
    synchroImg = "viewimage/tb.png",        //同步节点
    freeImg = "viewimage/yl.png",           //游离节点
    saveImg = "images/save.png",            //保存图片
    hassavedImg = "images/has_saved.png",       //已经被保存的图片
    imgHeight = 40,                         //图片长度
    imgWidth = 40;                          //图片宽度
var controlsave = 0;
var selectMoreArry = [];                  //全局多选数组
var selectPointsArry = [];
var time = 1                               //操作多选次数
//业务节点操作图片
var ybImg = "viewimage/yb.png",            //一般
    drzxImg = "viewimage/drzx.png",
    qxjzImg = "viewimage/qxjz.png",
    yyImg = "viewimage/yy.png",
    zdzxImg = "viewimage/zdzx.png",
    zlcImg = "viewimage/zlc.png";
    jbImg = "viewimage/yy-jb.png";
    beanImg = "viewimage/yy-bean.png";
var ybClockImg = "viewimage/yb-clock.png",            //一般
    drzxClockImg = "viewimage/drzx-clock.png",
    qxjzClockImg = "viewimage/qxjz-clock.png",
    yyClockImg = "viewimage/yy-clock.png",
    zdzxClockImg = "viewimage/zdzx-clock.png",
    zlcClockImg = "viewimage/zlc-clock.png";
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
var g = function (id) {
    return document.getElementById(id);
};
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
        length = 2;
    }
    else {
        length = parseInt(wid) / 5;
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
 * pt 转换成 px
 * @param p
 * @returns {*}
 */
function changePt(p) {
    var point = p.split(","), s;

    for (var i = 0; i < point.length; i++) {
        if (i != 0 && i % 2 == 0) {
            s += " " + (Math.round(parseFloat(point[i]) * 1.333));
        } else if (i == 0) {
            s = Math.round(parseFloat(point[i]) * 1.333);
        } else {
            s += "," + Math.round(parseFloat(point[i]) * 1.333);
        }
    }
    return s;
}
/**
 * px 转换成 pt
 * @param p
 * @returns {string}
 */
function changePx(p) {
    var point = p.split(/[ ,]+/), s = "";

    for (var i = 0; i < point.length; i++) {
        if (s == "") {
            s = Math.round(parseFloat(point[i]) / 1.33) + "pt";
        }
        else {
            s = s + "," + Math.round(parseFloat(point[i]) / 1.33) + "pt";
        }
    }
    return s;
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
    if(o) {
        if (o.text) {
            o.text = content;
        } else {
            o.textContent = content;
        }
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
/**
 * 返回菱形的4个点
 * @param point
 * @param width
 * @param height
 * @returns {string}
 */
function calculatePolygon(point, width, height) {
    var p = new Array;
    p[0] = [point[0], point[1] + (height / 2)];
    p[1] = [point[0] + (width / 2), point[1]];
    p[2] = [point[0], point[1] - (height / 2)];
    p[3] = [point[0] - (width / 2), point[1]];
    return p.join(" ");
}
/**
 * 返回齿轮形状5个点
 * @param x
 * @param y
 * @param R
 * @returns {{p1: {x: *, y: *}, p2: {x: *, y: *}, p3: {x: *, y: *}, p4: {x: *, y: *}, p5: {x: *, y: *}}}
 */
function calculateGear(x, y, R) {
    var p = {
        p1: {x: x, y: y + R},
        p2: {x: x + R * Math.sin(72 / 180 * Math.PI), y: y + R * Math.cos(72 / 180 * Math.PI)},
        p3: {x: x + R * Math.sin(144 / 180 * Math.PI), y: y + R * Math.cos(144 / 180 * Math.PI)},
        p4: {x: x + R * Math.sin(216 / 180 * Math.PI), y: y + R * Math.cos(216 / 180 * Math.PI)},
        p5: {x: x + R * Math.sin(288 / 180 * Math.PI), y: y + R * Math.cos(288 / 180 * Math.PI)}
    };
    return p;
}
/**
 * 获取节点的四个坐标
 * @param pro
 * @returns {{L: *, T: *, R: *, B: *}}
 */
function getPoint(pro) {
    var x, y, w, h, left, right, top, bottom, pointArray = new Array, points;
    // if (pro.attr("shapetype") == "roundRect") {//矩形返回其每条边的中点
        x = pro.x();
        y = pro.y();
        w = pro.attr("width");
        h = pro.attr("height");
        left = [x, y + h / 2];
        right = [x + w, y + h / 2];
        top = [x + w / 2, y];
        bottom = [x + w / 2, y + h];
    return {L: left, T: top, R: right, B: bottom};
}
/**
 * 获取节点的四个坐标
 * @param pro
 * @returns {{L: *, T: *, R: *, B: *}}
 */
function getEndPoint(pro) {
    var x, y, w, h, left, right, top, bottom, pointArray = new Array, points;
    // if (pro.attr("shapetype") == "roundRect" ) {//矩形返回其每条边的中点
        x = pro.x();
        y = pro.y();
        w = pro.attr("width");
        h = pro.attr("height");
        left = [x-1, y + h / 2];
        right = [x + w + 2, y + h / 2];
        top = [x + w / 2, y-1];
        bottom = [x + w / 2, y + h+6];
    return {L: left, T: top, R: right, B: bottom};
}
/**
 * 格式化线的位置 此方法适合于只有两个点的直线，如果是多点就不合适
 * @param pocBegin
 * @param pocEnd
 * @returns {{p1: *, p2: *}}
 */
function formatLine(pocBegin, pocEnd) {
    var F1 = getPoint(pocBegin),//起始节点四个点
        F2 = getEndPoint(pocEnd),//终止节点四个点
        w = parseInt(pocBegin.attr("width")),
        h = parseInt(pocBegin.attr("height")),
        p1, p2, x1, x2, y1, y2;//p1、p2为最终返回起止坐标，x1、y1、x2、y2分别起止节点x、y坐标
    //找到线，然后找到线中最接近的点
    x1 = pocBegin.x();
    y1 = pocBegin.y();
    x2 = pocEnd.x();
    y2 = pocEnd.y();
    if (x1 + w +  0 < x2) {//起始节点在终止节点左边，并且距离大于50
        if (y1 + h + 0 < y2) {//起始节点在终止节点上边,并且距离大于50
            p1 = F1.R;//起始节点右边
            p2 = F2.T;//终止节点上边
        } else if (y1 - h - 0 > y2) {//起始节点在终止节点下边，并且距离大于50
            p1 = F1.R;//起始节点右边
            p2 = F2.B;//终止节点底边
        } else {//起始节点与终止节点在垂直方向差距不大
            p1 = F1.R;//起始节点右边
            p2 = F2.L;//终止节点左边
        }
    } else if (x1 - w - 0 > x2) {//起始节点在终止节点右边，并且距离大于50
        if (y1 + h + 0 < y2) {//起始节点在终止节点上边,并且距离大于50
            p1 = F1.L;//起始节点左边
            p2 = F2.T;//终止节点上边
        } else if (y1 - h - 0 > y2) {////起始节点在终止节点下边，并且距离大于50
            p1 = F1.L;//起始节点左边
            p2 = F2.B;//终止节点底边
        } else {//起始节点与终止节点在垂直方向差距不大
            p1 = F1.L;//起始节点左边
            p2 = F2.R;//终止节点右边
        }
    } else {//起始节点与终止节点在水平方向差距不大
        if (y1 + h < y2) {//起始节点在上
            p1 = F1.B;//起始节点底边
            p2 = F2.T;//终止节点上边
        } else if (y1 - h > y2) {//起始节点在下
            p1 = F1.T;//起始节点上边
            p2 = F2.B;//终止节点底边
        }else{
            p1 = F1.R;//起始节点右边
            p2 = F2.L;//终止节点左边
        }
    }
    return {p1: p1, p2: p2};
}
/**
 * 是否增加折线点
 * @param line
 * @param point
 * @returns {boolean}
 */
function addPointOrNot(line, point) {
    var pointArray = new Array,
        points = line.getAttribute("points").split(" ");

    for (var i = 0, len = points.length; i < len; i++) {
        var cp = points[i].split(",");
        pointArray.push([cp[0], cp[1]]);//线每个点x、y轴坐标数组
    }
    for (var h = 0, hLen = pointArray.length; h < hLen; h++) {
        if (Math.abs(point[0] - pointArray[h][0]) < 10 && Math.abs(point[1] - pointArray[h][1]) < 10) {//如果距离线其中一个点的距离小于10，增加一个点
            //小圆略微偏移原来距离以便区分
            var circle = gCircle.circle(8).cx(Number(pointArray[h][0]) - 6).cy(Number(pointArray[h][1]) - 6).fill("blue").attr({"lineID": line.getAttribute("id")});
            if (h == 0) {
                ptMoveType = "from";
            }
            else if(h == (hLen-1)){
                ptMoveType = "to";
            }
            else{
                ptMoveType = "";
            }
            line.setAttribute("cricleID", circle.attr("id"));//记录小圆ID
            return false;
        }
    }
    return true;
}

/**
 * 折线工具 添加节点
 * @param line
 * @param newPoint
 * @returns {string}
 */
function addPolylinePoint(line, newPoint) {
    var pointArray = new Array, points = line.getAttribute("points").split(" "),
        flagX, flagY, newPosition, newPointArray = new Array;

    for (var i = 0, len = points.length; i < len; i++) {
        var cp = points[i].split(",");
        pointArray.push([cp[0], cp[1]]);
    }

    for (var j = 0, jLen = pointArray.length; j < jLen - 1; j++) {
        //介于两点之间，并且距前一个点距离小于5
        var d = GetNearestDistance(
            {x:parseInt(pointArray[j][0]),y:parseInt(pointArray[j][1])},
            {x:parseInt(pointArray[j+1][0]),y:parseInt(pointArray[j+1][1])},
            {x:newPoint[0],y:newPoint[1]}
        )
        if (d<=5) {
            newPosition = j + 1;//前一个点的位置加1
            break;
        }
    }

    for (var h = 0, hLen = pointArray.length; h < hLen; h++) {//新增加点的坐标放入数组
        if (h == newPosition) newPointArray.push(newPoint[0] + "," + newPoint[1]);
        newPointArray.push(pointArray[h][0] + "," + pointArray[h][1]);
    }

    line.setAttribute("points", newPointArray.join(" "));//重新设置线坐标
    return newPointArray.join(" ");

}

/**
 * 返回插入点的位置
 * @param line
 * @param point
 * @param newPoints
 * @returns {number}
 */
function getcurPoint(line,point,newPoints){
    var pointArray = new Array, ps = newPoints.split(" ");

    for( var i=0,len=ps.length;i<len;i++ ){
        var cp = ps[i].split(",");
        pointArray.push([cp[0],cp[1]]);
    }

    for( var h=0,hLen=pointArray.length;h<hLen;h++ ){
        //距离小于2
        if( Math.abs(parseInt(pointArray[h][0])-parseInt(point[0]))<2 && Math.abs(parseInt(pointArray[h][1])-parseInt(point[1]))<2 ){
            return h;
            break;
        }
    }
}

/**
 * 拖动某一节点变化位置 n为变化的第几个节点
 * @param line
 * @param point
 * @param n
 * @param points
 */
function changePolylinePoint(line,point,n,points){
    var pointArray = new Array,newPointArray = new Array;
    var ps = points.split(" ");
    for( var i=0,len=ps.length;i<len;i++ ){
        var cp = ps[i].split(",");
        pointArray.push([cp[0],cp[1]]);
    }
    for( var h=0,hLen=pointArray.length;h<hLen;h++ ){
        if(h==n) newPointArray.push(fixXY(point[0])+","+fixXY(point[1]));//不停变化位置坐标的点
        newPointArray.push(pointArray[h][0]+","+pointArray[h][1]);
    }
    //折线判断点的位置
    if(newPointArray.length >= 3){
        var from = $('#'+line.getAttribute('from'))[0];
        var to = $('#'+line.getAttribute('to'))[0];
        newPointArray[0] = formatPoint(newPointArray[1],from);
        newPointArray[newPointArray.length - 1] = formatPoint(newPointArray[newPointArray.length - 2],to);
    }
    line.setAttribute("points",newPointArray.join(" "));
}

/**
 * 拖动某一节点变化位置 n为变化的第几个节点
 * @param line
 * @param point
 * @param n
 * @param points
 */
function dragPolylinePoint(line,point,n,points){
    var pointArray = new Array;
    var ps = points.split(" ");
    for( var i=0,len=ps.length;i<len;i++ ){
        var cp = ps[i].split(",");
        pointArray.push([cp[0],cp[1]]);
    }
    pointArray[n]=fixXY(point[0])+","+fixXY(point[1]);
    if(n == 1){
        var from = $('#'+line.getAttribute('from'))[0];
        pointArray[0] = formatPoint(pointArray[1],from);
    }
    if(n == (pointArray.length - 2)){
        var to = $('#'+line.getAttribute('to'))[0];
        pointArray[pointArray.length - 1] = formatPoint( pointArray[pointArray.length - 2],to);
    }
    line.setAttribute("points",pointArray.join(" "));
}
/**
 * 删除线上指定节点
 * @param line
 * @param n
 */
function deletePoint(line,n){
    var	pointArray = new Array,
        points = line.getAttribute("points").split(" ");
    for( var i=0,len=points.length;i<len;i++ ){
        var cp = points[i].split(",");
        pointArray.push([cp[0],cp[1]]);
    }
    pointArray.splice(n,1);
    line.setAttribute("points",pointArray.join(" "));
}
/**
 * 根据坐标得到图形
 * @param x
 * @param y
 * @returns {*}
 */
function getProcAtXY(x,y){
    var obj = g("s3").childNodes,x1, y1,s;
    for( var i=0,len=obj.length;i<len;i++ ){
        //首节点或业务节点
        if(obj[i].getAttribute("nodetype")=='B'||obj[i].getAttribute("nodetype")=='C'){
            var bx= obj[i].getAttribute("x"),by = obj[i].getAttribute("y"),bw = obj[i].getAttribute("width"),bh= obj[i].getAttribute("height");
            if(x>=parseInt(bx)&&x<=parseInt(bx)+parseInt(bw)&&y>=parseInt(by)&&y<=parseInt(by)+parseInt(bh)) {
                return obj[i];
            }
        }
        else{//路由节点
            x1 = obj[i].getAttribute("cx");
            y1 = obj[i].getAttribute("cy");
            s = parseInt(Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2)));//两点之间距离
            if(s<parseInt(obj[i].getAttribute("width")/2)){
                return obj[i];
            }
        }
    }
    return null;
}

//修正拖动后线的坐标
function correctLinePoints(lineID,moveType,proc,x,y){
    var correctedPoints,str = g(lineID).getAttribute("points").split(" "),pl = getEndPoint(SVG.get(proc.id)),
        w = proc.getAttribute("width"),h=proc.getAttribute("height"),px,py;
    if(proc.getAttribute("nodetype")=='B'||proc.getAttribute("nodetype")=='C'){//首节点或业务节点
        px = proc.getAttribute("x");
        py = proc.getAttribute("y");
        if(x>=parseInt(px)+parseInt(w)/4&&x<=parseInt(px)+parseInt(w)*0.75&&y<=parseInt(py)+parseInt(h)/2){
            correctedPoints = pl.T;
        }
        else if(x>=parseInt(px)+parseInt(w)/4&&x<=parseInt(px)+parseInt(w)*0.75&&y>parseInt(py)+parseInt(h)/2){
            correctedPoints = pl.B;
        }
        else if(x<parseInt(px)+parseInt(w)/4){
            correctedPoints = pl.L;
        }
        else if(x>parseInt(px)+parseInt(w)*0.75){
            correctedPoints = pl.R;
        }
    }
    else{//路由节点、开始节点、结束节点
        px = proc.getAttribute("cx");
        py = proc.getAttribute("cy");
        if(x<(px-w*0.2)){
            correctedPoints = pl.L;
        }
        else if(x>(px+w*0.2)){
            correctedPoints = pl.R;
        }
        else{
            if(y>=py){
                correctedPoints = pl.B;
            }
            else{
                correctedPoints = pl.T;
            }
        }
    }
    if(moveType=="to"){//替换最后一个点坐标
        str.splice(str.length-1,1,correctedPoints);
        g(lineID).setAttribute("points",str.join(" "));
    }
    else if(moveType=="from"){//替换第一个点坐标
        str.splice(0,1,correctedPoints);
        g(lineID).setAttribute("points",str.join(" "));
    }
}
/**
 * 开始结束节点内圆
 * @param x
 * @param y
 * @param id
 * @returns {*}
 */
function drawDoubleCircle(x, y, id) {
    if (id) {
        SVG.get(id).attr({
            "d": "M " + (x + 25) + " " + y
            + " A 25 25 0 0 0 " + (x - 25) + " " + y
            + " A 25 25 0 0 0 " + (x + 25) + " " + y
        });
        return false;
    } else {
        var path = gPath.path().attr({
            "d": "M " + (x + 25) + " " + y
            + " A 25 25 0 0 0 " + (x - 25) + " " + y
            + " A 25 25 0 0 0 " + (x + 25) + " " + y
        }).fill("none").stroke({color: "#00f"});
        return path;
    }
}

/**
 * 画路由分布节点内部
 * @param x
 * @param y
 * @param id
 * @returns {*}
 */
function drawFenPath(x, y, id) {
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
}

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
function drawMultiPath(x, y, id) {
    if (id) {
        SVG.get(id).attr({
            "d": "M " + (x - 6) + " " + (y + 3) + " h 20 v 10 h -20 z M" + (x - 3) + " " + (y + 4) + " v -5 h 20 v 10 h -3 M "
            + (x + 1) + " " + y + " v -5 h 20 v 10 h -3"
        });
        return false;
    } else {
        var path = gPath.path().attr({
            "d": "M " + (x - 6) + " " + (y + 3) + " h 20 v 10 h -20 z M" + (x - 3) + " " + (y + 4) + " v -5 h 20 v 10 h -3 M "
            + (x + 1) + " " + y + " v -5 h 20 v 10 h -3"
        }).stroke({width: 1.3, color: "#00f"}).fill("none");
        return path;
    }
}

/**
 * 画同步节点内部
 */
function drawTongPath(x, y, ids) {
    if (ids) {
        SVG.get(ids.path1).attr({
            "d": "M " + (x - 13) + " " + (y - 5)
            + " A 20 33 0 0 1 " + (x + 13) + " " + (y - 5)
        });
        SVG.get(ids.path2).attr({
            "d": "M " + (x + 13) + " " + (y + 5)
            + " A 20 33 0 0 1 " + (x - 13) + " " + (y + 5)
        });
        return false;
    } else {
        var path1 = gPath.path().attr({
            "d": "M " + (x - 13) + " " + (y - 5)
            + " A 20 33 0 0 1 " + (x + 13) + " " + (y - 5)
        }).stroke({width: 1.3, color: "#00f"}).fill("none").attr({"marker-end": "url(#" + marker.attr("id") + ")"});
        var path2 = gPath.path().attr({
            "d": "M " + (x + 13) + " " + (y + 5)
            + " A 20 33 0 0 1 " + (x - 13) + " " + (y + 5)
        }).stroke({width: 1.3, color: "#00f"}).fill("none").attr({"marker-end": "url(#" + marker.attr("id") + ")"});
        return {path1: path1, path2: path2};
    }
}

/**
 * 画游离节点内部
 * @param x
 * @param y
 * @returns {*}
 */
function drawYouPath(x, y, id) {
    if (id) {
        SVG.get(id).attr({
            "d": "M " + (x - 7) + " " + (y - 2) + " v -10 h 15 v 10 l -10 -5 z M "
            + (x - 7) + " " + (y + 3) + " v 10 h 15 v -10 l -10 -5 z"
        });
        return false;
    } else {
        var path = gPath.path().attr({
            "d": "M " + (x - 7) + " " + (y - 2) + " v -10 h 15 v 10 l -10 -5 z M "
            + (x - 7) + " " + (y + 3) + " v 10 h 15 v -10 l -10 -5 z"
        }).stroke({color: "#00f", width: 1.3}).fill("none");
        return path;
    }
}
/**
 * 在折现时更改点
 */
function formatPoint(position,node,type){
    //找到左中 上中 右中 下中4个点 计算出距离最短 返回
    var p = position.split(',');
    var left,right,up,down,leftDis,rightDis,upDis,downDis,min;
    if(node.getAttribute('nodetype') == 'R'){
        left = [parseInt(node.getAttribute('x')) + 5, parseInt(node.getAttribute('y')) + 20];
        right = [parseInt(node.getAttribute('x')) + 35, parseInt(node.getAttribute('y')) + 20];
        up = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y')) + 5];
        down = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y')) + 35];
    }
    else {
        left = [parseInt(node.getAttribute('x')), parseInt(node.getAttribute('y')) + 20];
        right = [parseInt(node.getAttribute('x')) + 40, parseInt(node.getAttribute('y')) + 20];
        up = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y'))];
        down = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y')) + 40];
    }
    leftDis = calDistance(p,left);
    rightDis = calDistance(p,right);
    upDis = calDistance(p,up);
    downDis = calDistance(p,down);
    min = [leftDis,rightDis,upDis,downDis].sort(function(a,b){
        return a-b;
    })[0];
    left = [parseInt(node.getAttribute('x')), parseInt(node.getAttribute('y')) + 20];
    right = [parseInt(node.getAttribute('x')) + 40, parseInt(node.getAttribute('y')) + 20];
    up = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y'))];
    down = [parseInt(node.getAttribute('x')) + 20, parseInt(node.getAttribute('y')) + 40];
    return      min == leftDis?left:
                min==rightDis?right:
                    min==upDis?up:down;
}
/**
 * 是否允许保存
 */
function changeSaveStatus(status){
    if(status=='save'){
        controlsave = 1;
        $('#toxml img').attr('src',saveImg);
    }
    else{
        controlsave = 0;
        $('#toxml img').attr('src',hassavedImg);
    }
}
/**
 * 计算两点的距离
 */
function calDistance(a,b){
    return Math.sqrt((a[0]-b[0])*(a[0]-b[0]) + (a[1]-b[1])*(a[1]-b[1]));
}
/**
 * 改变文本位置
 */
function changeTextP(lineArray,pointArray){
    var text = $('#lab'+lineArray);
    var width = parseFloat(text.css('width'));
    var height = parseFloat(text.css('height'));
    if(text) {
        //直线按照2点计算
        if(pointArray.length == 2) {
            var np1 = pointArray[0].split(",");
            var fromX = parseInt(np1[0]);
            var fromY = parseInt(np1[1]);
            var np2 = pointArray[1].split(",");
            var toX = parseInt(np2[0]);
            var toY = parseInt(np2[1]);
            text[0].style.left = parseInt((toX + fromX) / 2) - width / 2 + 'px';
            text[0].style.top = parseInt((toY + fromY) / 2) - height / 2 + 'px';
        }
        else if(pointArray.length > 2){
            //折线时进行中点法线和各个线段的交点
            //计算法线方程
            var x1 =  parseInt(pointArray[0].split(',')[0]);
            var x2 =  parseInt(pointArray[pointArray.length - 1].split(',')[0]);
            var y1 =  parseInt(pointArray[0].split(',')[1]);
            var y2 =  parseInt(pointArray[pointArray.length - 1].split(',')[1]);
            //形式为a1x+b1=y
            var a1 = -1/((y1 - y2 )/(x1 - x2));
            var b1 = -a1*((x1+x2)/2) + (y1+y2)/2;
            for(var i = 1;i<pointArray.length;i++){
                 //计算所有线段
                 x1 = parseInt(pointArray[i].split(',')[0]);
                 x2 = parseInt(pointArray[i-1].split(',')[0]);
                 y1 =  parseInt(pointArray[i].split(',')[1]);
                 y2 =  parseInt(pointArray[i-1].split(',')[1]);
                if(x1 == x2){
                    //如果斜率趋向无穷大，及x1=x2时，直接取x=x1
                    var x = x1;
                    var y = a1 * x + b1;
                    //此时只需要满足y在内部即可
                    if (y < Math.max(y1, y2) && y > Math.min(y1, y2)) {
                        text[0].style.left = parseInt(x) - width / 2 + 'px';
                        text[0].style.top = parseInt(y) - height / 2 + 'px';
                        return;
                    }
                }
                //斜率存在
                else {
                    //计算每条这线段的方程
                    var a2 = (y1 - y2 ) / (x1 - x2);
                    //当平行时直接进行下一条线段对比
                    if (a2 == a1) {
                        continue;
                    }
                    else {
                        //计算线段方程a2x+b2=y
                        var b2 = -a2 * ((x1 + x2) / 2) + (y1 + y2) / 2;
                        //连列a1x+b1=y与a2x+b2=y得到交点
                        var x = (b2 - b1) / (a1 - a2);
                        var y = a1 * x + b1;
                        //当交点满足在线段上时，才赋值（必定可以得到一条满足的）
                        if((y1 == y2)&&(x < Math.max(x1, x2) && x > Math.min(x1, x2))){
                            text[0].style.left = parseInt(x) - width / 2 + 'px';
                            text[0].style.top = parseInt(y) - height / 2 + 'px';
                            return;
                        }
                        else if (x < Math.max(x1, x2) && x > Math.min(x1, x2) && y < Math.max(y1, y2) && y > Math.min(y1, y2)) {
                            text[0].style.left = parseInt(x) - width / 2 + 'px';
                            text[0].style.top = parseInt(y) - height / 2 + 'px';
                            return;
                        }
                    }
                }
            }
        }
    }
}
/**
 * 删除链接线
 */
function deleteConnectLine(pro){
    var lineArray = [];
    if (pro.getAttribute("from")) {
        lineArray = lineArray.concat(pro.getAttribute("from").split(','));
    }
    if (pro.getAttribute("to")){
        lineArray = lineArray.concat(pro.getAttribute("to").split(','));
    }
    for(var i = 0;i<lineArray.length;i++){
        deletePro(lineArray[i]);
    }
}
/**
 * 修正坐标函数
 */
function fixXY(p,type){
    if(!type) {
        return p % 5 > 2 ? p + (5 - p % 5) : p - p % 5;
    }
    else
    {
        var xy = p % 5 > 2 ? p + (5 - p % 5) : p - p % 5;
        if (type == 'y') {
            var height = parseFloat($('#canvas').css('height'));
            if(xy<(height - imgHeight -25)&& xy> 0 ){
                return xy;
            }
            else{
                if(xy<(imgHeight/2)){
                    return xy;
                }
                else{
                    $('#canvas').css('height',parseInt($('#canvas').css('height'))+imgHeight+50+'px');
                    return xy;
                }
            }
        }
        if (type == 'x') {
            var width = parseInt($('#canvas').css('width'));
            if(xy<(width - imgWidth)&& xy> 0 ){
                return xy;
            }
            else{
                if(xy<(imgWidth/2)){
                    return xy;
                }
                else{
                    $('#canvas').css('width',parseInt($('#canvas').css('width'))+imgWidth+50+'px');
                    return xy;
                }
            }
        }
    }
}
/**
 * 读取改变链接线
 */
function loadChangeLine(){
    var line = $('polyline');
    var points,from,to,node,FP;
    for(var i = 0 ;i<line.length;i++){
        points = line[i].getAttribute('points').split(' ');
        if(points.length >2){
            from = line[i].getAttribute('from');
            to = line[i].getAttribute('to');
            if(from){
                node = $('#'+from)[0];
                points[0] = formatPoint(points[1],node,0).join(',');
                line[i].setAttribute('points',points.join(' '));
            }
            if(to){
                node = $('#'+to)[0];
                points[points.length-1] = formatPoint(points[points.length-2],node,1).join(',');
                line[i].setAttribute('points',points.join(' '));
            }
        }
        else if(points.length == 2){
            FP = formatLine(SVG.get(SVG.get(line[i].id).attr("from")), SVG.get(SVG.get(line[i].id).attr("to")));
            if (FP["p1"]) {
                g(line[i].id).setAttribute("points", FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0] + "," + FP["p2"][1]);
            }
        }
    }
}

/**
 * 改变其他节点所有数据
 */
function changeOtherNode(node,shiftX,shiftY){
    var x = fixXY(parseInt(node.getAttribute('x'))+shiftX,'x');
    var y = fixXY(parseInt(node.getAttribute('y'))+shiftY,'y');
    node.setAttribute('x',x);
    node.setAttribute('y',y);
    //文本节点
    var text = node.getAttribute("textID");
    var  width = node.getAttribute("width");
    var  height = node.getAttribute("height");
    if(text) {
        g(text).setAttribute("x", parseInt(node.getAttribute("x")) + 5);
        g(text).setAttribute("y", parseInt(node.getAttribute("y")) + (height / 2) + 40);
        g(text).setAttribute("dx", parseInt(node.getAttribute("x")) + (width / 2));
    }
    var line = $('polyline');
    for(var i = 0;i<line.length;i++){
        if((line[i].getAttribute('from') == node.id)){
            changeTextP(line[i].id,line[i].getAttribute('points').split(' '));
        }
        if((line[i].getAttribute('to') == node.id)){
            changeTextP(line[i].id,line[i].getAttribute('points').split(' '));
        }
    }
}
/**
 * 清除所有选中
 *
 */
function clearSelected(){
    var image = $('image');
    for(var i = 0;i<image.length;i++){
        if(image[i].getAttribute('filter') == "url(#filter-rect)"){
            image[i].setAttribute('filter',"");
        }
    }
    $('#lineCon div').css('background',"");
}
/**
 * 改变操作类别图片
 *
 */
function changeOperateImg(type,o){
    if(g(o).getAttribute('isaccounttime') == "F" && g(o).getAttribute('opttype') == "D") {
        switch (type) {
            case 'N':
                g(o).setAttribute('xlink:href', yyImg);
                break;
            case 'S':
                g(o).setAttribute('xlink:href', jbImg);
                break;
            case 'B':
                g(o).setAttribute('xlink:href', beanImg);
                break;
            default:
                break;
        }
    }else if(g(o).getAttribute('isaccounttime') == "F") {
        switch (type) {
            case 'D':
                g(o).setAttribute('xlink:href', yyImg);
                break;
            case 'E':
                g(o).setAttribute('xlink:href', yyImg);
                break;
            case 'A':
                g(o).setAttribute('xlink:href', ybImg);
                break;
            case 'S':
                g(o).setAttribute('xlink:href', zlcImg);
                break;
            case 'B':
                g(o).setAttribute('xlink:href', qxjzImg);
                break;
            case 'C':
                g(o).setAttribute('xlink:href', drzxImg);
                break;
            default:
                break;
        }
    }
    else{
        switch (type) {
            case 'D':
                g(o).setAttribute('xlink:href', zdzxClockImg);
                break;
            case 'E':
                g(o).setAttribute('xlink:href', yyClockImg);
                break;
            case 'A':
                g(o).setAttribute('xlink:href', ybClockImg);
                break;
            case 'S':
                g(o).setAttribute('xlink:href', zlcClockImg);
                break;
            case 'B':
                g(o).setAttribute('xlink:href', qxjzClockImg);
                break;
            case 'C':
                g(o).setAttribute('xlink:href', drzxClockImg);
                break;
            default:
                break;
        }
    }
}
/**
 * 改变所有polyline上的点（兼容过去数据）
 *
 */
function changeLoadPoints(){
    var line = $('polyline');
    for(var i = 0;i<line.length;i++){
        var points = line[i].getAttribute('points').split(' ');
        if(points.length > 2) {
            var updatepoints = "";
            for (var j = 0; j < points.length; j++) {
                points[j] = points[j].split(',');
                for (var k = 0; k < points[j].length; k++) {
                    if (k == 0) {
                        updatepoints = updatepoints + fixXY(parseInt(points[j][k]));
                    }
                    else {
                        updatepoints = updatepoints + "," + fixXY(parseInt(points[j][k]));
                    }
                }
                if(j!=points.length - 1) {
                    updatepoints = updatepoints + " ";
                }
            }
            line[i].setAttribute('points', updatepoints);
            updatepoints = "";
        }
    }
}
/**
 * 改变所有clock
 *
 */
function changeClock(obj,o){
    if(obj.value == 'H' || obj.value == "T"){
        if(g(o).getAttribute('opttype') == "D") {
            g(o).setAttribute("xlink:href", zdzxClockImg);
        }
        if(g(o).getAttribute('opttype') == "E") {
            g(o).setAttribute("xlink:href", yyClockImg);
        }
        if(g(o).getAttribute('opttype') == "A") {
            g(o).setAttribute("xlink:href", ybClockImg);
        }
        if(g(o).getAttribute('opttype') == "S") {
            g(o).setAttribute("xlink:href", zlcClockImg);
        }
        if(g(o).getAttribute('opttype') == "B") {
            g(o).setAttribute("xlink:href", qxjzClockImg);
        }
        if(g(o).getAttribute('opttype') == "C") {
            g(o).setAttribute("xlink:href", drzxClockImg);
        }
    }
    else if(obj.value == "F"){
        if(g(o).getAttribute('opttype') == "D") {
            g(o).setAttribute("xlink:href", zdzxImg);
        }
        if(g(o).getAttribute('opttype') == "E") {
            g(o).setAttribute("xlink:href", yyImg);
        }
        if(g(o).getAttribute('opttype') == "A") {
            g(o).setAttribute("xlink:href", ybImg);
        }
        if(g(o).getAttribute('opttype') == "S") {
            g(o).setAttribute("xlink:href", zlcImg);
        }
        if(g(o).getAttribute('opttype') == "B") {
            g(o).setAttribute("xlink:href", qxjzImg);
        }
        if(g(o).getAttribute('opttype') == "C") {
            g(o).setAttribute("xlink:href", drzxImg);
        }
    }
}
/**
 * 寻找被框中的点（多选）
 *
 */
function findPyNode(rect){
    var x1 = rect.attr('x');
    var x2 = rect.attr('x') + rect.attr('width');
    var y1 = rect.attr('y');
    var y2 = rect.attr('y') + rect.attr('height');
    var node = $('#s3 image');
    var line = $('polyline');
    selectMoreArry.length = 0;
    selectPointsArry.length = 0;
    //找节点
    for(var i = 0;i<node.length;i++){
        //找到node的4个点坐标，只要有一个在范围内，那么图形就被选中，进行push数组
        var startX = parseInt(node[i].getAttribute('x'));
        var endX = parseInt(node[i].getAttribute('x')) + parseInt(node[i].getAttribute('width'));
        var startY = parseInt(node[i].getAttribute('y'));
        var endY = parseInt(node[i].getAttribute('y')) + parseInt(node[i].getAttribute('height'));
        if((startX<x2 && startX>x1)){
            if(startY<y2 && startY>y1){
                selectMoreArry.push(node[i].id);
                node[i].setAttribute("filter","url(#filter-rect)");
            }
            else if(endY<y2 && endY>y1){
                selectMoreArry.push(node[i].id);
                node[i].setAttribute("filter","url(#filter-rect)");
            }
        }
        else if(startY<y2 && startY>y1){
            if(startX<x2 && startX>x1){
                selectMoreArry.push(node[i].id);
                node[i].setAttribute("filter","url(#filter-rect)");
            }
            else if(endX<x2 && endX>x1){
                selectMoreArry.push(node[i].id);
                node[i].setAttribute("filter","url(#filter-rect)");
            }
        }
    }
    //找线上点
    for(var i = 0;i<line.length;i++){
        var points = line[i].getAttribute('points').split(" ");
        for(var j=1;j<points.length-1;j++){
            var dot = points[j].split(',');
            if(dot[0]>x1&&dot[0]<x2&&dot[1]>y1&&dot[1]<y2){
                selectPointsArry.push({
                    line:line[i],
                    position:j
                });
            }
        }
    }
}
/**
 * 重复操作和前进操作的入栈(包括节点移动 线移动)
 *
 */
function unreShiftMove(node,data,unreArray,type){
    if(type == "move") {
        unreArray.unshift({
            oNode: node,
            oAction: type,
            position: data
        });
    }
    else if(type == "changePoint"){
        //存储此时的属性to，以防改变to后删除节点会造成找不到线的情况
        var to = $('#'+node)[0].getAttribute('to');
        unreArray.unshift({
            oNode:node,
            oAction:type,
            passPosition:data,
            to:to
        });
    }
}
/**
 * 重复操作和前进操作的入栈(删除)
 *
 */
function unreShiftDel(node,nodeName,unreArray){
    unreArray.unshift({
        oNode: node,
        oAction: 'del',
        nodeName: nodeName
    });
}
/**
 * 重复操作和前进操作的入栈(多个节点操作)
 *
 */
function unreShiftMuch(nodes,points,unreArray,type){
    unreArray.unshift({
        oAction:type,
        nodes: nodes,
        points:points
    });
}
function GetPointDistance( p1,  p2)
{
    return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
}
function GetNearestDistance( PA,  PB,  P3)
{
    var a,b,c;
    a=GetPointDistance(PB,P3);
    if(a<=0.00001)
        return 0.0;
    b=GetPointDistance(PA,P3);
    if(b<=0.00001)
        return 0.0;
    c=GetPointDistance(PA,PB);
    if(c<=0.00001)
        return a;
    if(a*a>=b*b+c*c)
        return b;
    if(b*b>=a*a+c*c)
        return a;
    var l=(a+b+c)/2;     //周长的一半
    var s=Math.sqrt(l*(l-a)*(l-b)*(l-c));  //海伦公式求面积
    return 2*s/c;
}

function textToDiv(){
    var text = $('#s6 text');
    var container = $('#textToDiv');
    for(var i = 0;i<text.length;i++){
        var t = text[i];
        var box = t.getBBox();
        var x = box.x;
        var y = box.y;
        var html = text[i].innerHTML;
        var div = document.createElement('div');
        div.style.position = "absolute";
        div.style.left = x + 'px';
        div.style.top = y + 'px';
        div.innerHTML = html;
        container.append(div);
    }
    $('#s6').html("");
}