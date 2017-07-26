var evt;//修复Firefox event undefined
/**
 * 矩形移动、圆移动、小圆移动改变线折线点位置
 * @param o
 * @returns {*}
 */
function drag(o) {
    var defaultOptions = {
            moveObject: o
        },
        deltaX = 0, deltaY = 0, initX = 0, initY = 0, pointArray1 = new Array, pointArray2 = new Array, p1_1 = new Array, p1_2 = new Array, beginLinePoints, isMove = 0, oldWidth, oldHeight,
        p2_1 = new Array, p2_2 = new Array, lineArray1 = new Array, lineArray2 = new Array, nPoint, line_points, fromArray = new Array, toArray = new Array,
        canpushAction = 1,firstX,firstY,canChange=1,
        action = {
            move: function (event) {
                var e = event || window.event, i,
                    MouseX = e.clientX / _ZOOM,
                    MouseY = e.clientY / _ZOOM,
                    o = defaultOptions.moveObject,
                    width = o.getAttribute("width"),
                    height = o.getAttribute("height"),
                    text;
                switch (o.nodeName) {
                    case "image":
                        firstX = parseInt(o.getAttribute('x'));
                        firstY = parseInt(o.getAttribute('y'));
                        //添加撤销动作数组
                        if (canpushAction){
                            if(image != "selectMore") {
                                canpushAction = 0;
                                unreShiftMove(o.id,[o.getAttribute('x'), o.getAttribute('y')],undoAction,"move");
                                redoAction.length = 0;
                            }
                        }
                        var x = fixXY(MouseX - deltaX,'x');
                        var y = fixXY(MouseY - deltaY,'y');
                        text = o.getAttribute("textID");
                        if(image == "selectMore") {
                            var arrayNode = [];
                            var arrayPoints = [];
                            var shiftX = x - firstX;
                            var shiftY = y - firstY;
                            for (var i = 0; i < selectMoreArry.length; i++) {
                                var node = $('#' + selectMoreArry[i])[0];
                                if(canpushAction) {
                                    arrayNode.unshift({
                                        oNode: node.id,
                                        position: [node.getAttribute('x'), node.getAttribute('y')]
                                    })
                                }
                                changeOtherNode(node, shiftX, shiftY,canpushAction);
                            }
                            for(var i = 0;i<selectPointsArry.length;i++){
                                var selectPoint = selectPointsArry[i].line.getAttribute('points').split(' ');
                                if(canpushAction){
                                    arrayPoints.unshift({
                                        oNode: selectPointsArry[i].line.id,
                                        position: selectPointsArry[i].line.getAttribute('points')
                                    })
                                }
                                var xyArray = selectPoint[selectPointsArry[i].position];
                                xyArray = xyArray.split(',');
                                var pointX = fixXY(parseInt(xyArray[0]) + shiftX,'x');
                                var pointY = fixXY(parseInt(xyArray[1]) + shiftY,'y');
                                var string =pointX+","+pointY;
                                selectPoint[selectPointsArry[i].position] = string;
                                selectPointsArry[i].line.setAttribute('points',selectPoint.join(' '));
                            }
                        }
                        if(canpushAction) {
                            unreShiftMuch(arrayNode,arrayPoints,undoAction,"Muchchange");
                        }
                        o.setAttribute("x", x);//改变X轴坐标
                        o.setAttribute("y", y);//改变Y轴坐标
                        //多选时全部存储完毕才置0
                        canpushAction = 0;
                        if(text) {
                            g(text).setAttribute("x", parseInt(o.getAttribute("x")) + 5);
                            g(text).setAttribute("y", parseInt(o.getAttribute("y")) + (height / 2) + 40);
                            g(text).setAttribute("dx", parseInt(o.getAttribute("x")) + (width / 2));
                        }
                        redoAction.length = 0;
                        break;
                    case "ellipse":
                        o.setAttribute("cx", MouseX - deltaX);
                        o.setAttribute("cy", MouseY - deltaY);
                        if(o.getAttribute("lineID")){//拖动折线点产生的小圆
                            var line = SVG.get(o.getAttribute("lineID"));
                            if(canpushAction) {
                                canpushAction = 0;
                                unreShiftMove(line.node.id,line.attr('points'),undoAction,"changePoint");
                            }
                            dragPolylinePoint(g(o.getAttribute("lineID")),[MouseX-deltaX,MouseY-deltaY],nPoint,line_points);
                        }
                        else{
                            if(o.getAttribute("shapetype")=="double-oval"){//拖动开始节点或结束节点
                                text = o.getAttribute("textID");//文本ID
                                g(text).setAttribute("x", parseInt(o.getAttribute("cx")) + 5);
                                g(text).setAttribute("y", parseInt(o.getAttribute("cy"))+6);
                                g(text).setAttribute("dx", parseInt(o.getAttribute("cx")));
                                drawDoubleCircle(MouseX - deltaX,MouseY - deltaY, o.getAttribute("pathID"));
                            }
                            else{
                                if(o.getAttribute("routertype")=='E'){//汇聚节点
                                    drawJuPath(MouseX - deltaX,MouseY - deltaY+10, o.getAttribute("pathID"));
                                }
                                else if(o.getAttribute("routertype")=='G'){//多实例节点
                                    drawMultiPath(MouseX - deltaX-o.getAttribute("width")/3+6,MouseY - deltaY-4, o.getAttribute("pathID"));
                                }
                                else if(o.getAttribute("routertype")=='H'){//并行节点
                                    drawBingPath(MouseX - deltaX,MouseY - deltaY, o.getAttribute("pathID"));
                                }
                                else if(o.getAttribute("routertype")=='R'){//游离
                                    drawYouPath(MouseX - deltaX,MouseY - deltaY, o.getAttribute("pathID"));
                                }
                                else if(o.getAttribute("routertype")=='S'){//同步节点
                                    drawTongPath(MouseX - deltaX,MouseY - deltaY, {path1:o.getAttribute("path1"),path2:o.getAttribute("path2")});
                                }
                                else{//分支节点
                                    drawFenPath(MouseX - deltaX,MouseY - deltaY, o.getAttribute("pathID"));
                                }
                            }
                        }
                        redoAction.length = 0;
                        break;
                    default: ;
                        break;
                }
                changeSaveStatus('save');
                if (o.getAttribute("from")) {
                    for ( i = 0, len = lineArray1.length; i < len; i++) {
                        pointArray1[i][0] = (p1_1[i] + MouseX - initX) + "," + (p1_2[i] + MouseY - initY);//改变出线第一个点坐标
                        if(image != "selectMore") {
                            changeTextP(lineArray1[i], pointArray1[i]);
                        }
                    }
                }
                if (o.getAttribute("to")) {
                    for ( i = 0, len = lineArray2.length; i < len; i++) {
                        pointArray2[i][pointArray2[i].length - 1] = (p2_1[i] + MouseX - initX) + "," + (p2_2[i] + MouseY - initY);//改变进线最后一个点坐标
                        if(image != "selectMore") {
                            changeTextP(lineArray2[i], pointArray2[i]);
                        }
                    }

                }
                isMove = 1;
                if (e.stopPropagation) {//阻止冒泡
                    e.stopPropagation();
                } else {
                    e.cancleBubble = true;
                }
                if(!o.getAttribute("lineID")) {
                    loadChangeLine();
                }
            },
            stop: function (event) {
                //操作多选变量
                time++;
                var e = event || window.event, i,clen,FP;
                var fromEndArray = new Array, toEndArray = new Array, linePoints;
                //解绑事件
                removeEvent(document, "losecapture", action.stop);
                removeEvent(document, "mouseup", action.stop);
                removeEvent(document, "mousemove", action.move);
                if (document.releaseCapture) document.releaseCapture();//释放捕获
                if (isMove) {//如果移动了
                    canpushAction = 1;
                    if (!o.getAttribute("lineID")) {
                        if (o.getAttribute("from")) {
                            for (i = 0; i < lineArray1.length; i++) {
                                var points = SVG.get(lineArray1[i]).attr("points").split(" ");
                                 clen = SVG.get(lineArray1[i]).attr("points").split(" ").length;

                                if (clen == 2) {//如果是直线需要根据始末节点的位置重新计算坐标
                                    FP = formatLine(SVG.get(SVG.get(lineArray1[i]).attr("from")), SVG.get(SVG.get(lineArray1[i]).attr("to")));
                                    if (FP["p1"]) {
                                        g(lineArray1[i]).setAttribute("points", FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0] + "," + FP["p2"][1]);
                                    }
                                    fromEndArray[i] = SVG.get(lineArray1[i]).attr("points");//改动后数组随之改变
                                }
                                else if (clen > 2) {
                                    points[0] = formatPoint(points[1],o,0).join(',');
                                    g(lineArray1[i]).setAttribute("points", points.join(' '));
                                }
                            }
                        }
                        if (o.getAttribute("to")) {
                            for ( i = 0; i < lineArray2.length; i++) {
                                var points = SVG.get(lineArray2[i]).attr("points").split(" ");
                                clen = SVG.get(lineArray2[i]).attr("points").split(" ").length;
                                if (clen == 2) {//如果是直线需要根据始末节点的位置重新计算坐标
                                    FP = formatLine(SVG.get(SVG.get(lineArray2[i]).attr("from")), SVG.get(SVG.get(lineArray2[i]).attr("to")));
                                    if (FP["p1"]) {
                                        g(lineArray2[i]).setAttribute("points", FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0] + "," + FP["p2"][1]);
                                    }
                                    toEndArray[i] = SVG.get(lineArray2[i]).attr("points");//改动后数组随之改变
                                }
                                else if (clen > 2) {
                                    points[points.length-1] = formatPoint(points[points.length-2],o,1).join(',');
                                    g(lineArray2[i]).setAttribute("points", points.join(' '));
                                }
                            }
                        }
                    }
                    else{
                        //鼠标所在图形
                        var ProcTo = getProcAtXY(e.clientX + scrollElement.scrollLeft / _ZOOM, e.clientY + scrollElement.scrollTop / _ZOOM);
                        var procSource = "";
                        if (ProcTo) {
                            if (ptMoveType == "from") {//移动线的第一个点
                                if (ProcTo.getAttribute("id") != g(o.getAttribute("lineID")).getAttribute("from")) {
                                    procSource = g(g(o.getAttribute("lineID")).getAttribute("from"));
                                    var sourceFrom = procSource.getAttribute("from").split(",");//线原来起始节点的出线数组
                                    if (sourceFrom.length == 1) {//如果只有一个移除属性
                                        procSource.removeAttribute("from");
                                    }
                                    else {//否则从当前数组中弹出当前线ID
                                        for (i = 0; i < sourceFrom.length; i++) {
                                            if (sourceFrom[i] == o.getAttribute("lineID")) {
                                                sourceFrom.splice(i, 1);
                                            }
                                        }
                                        procSource.setAttribute("from", sourceFrom.join(","));//更新出线
                                    }
                                    if(ProcTo.getAttribute("from")){//目标节点存在出线则增加1
                                        ProcTo.setAttribute("from", ProcTo.getAttribute("from") + "," + o.getAttribute("lineID"));
                                    }
                                    else{//否则设置
                                        ProcTo.setAttribute("from", o.getAttribute("lineID"));

                                    }
                                    g(o.getAttribute("lineID")).setAttribute("from", ProcTo.getAttribute("id"));//改变线的起始节点
                                }
                            }
                            if (ptMoveType == "to") {//移动线最后一个节点
                                if (ProcTo.getAttribute("id") != g(o.getAttribute("lineID")).getAttribute("to")) {
                                    procSource = g(g(o.getAttribute("lineID")).getAttribute("to"));
                                    var sourceTo = procSource.getAttribute("to").split(",");//线原来起始节点的入线数组
                                    if (sourceTo.length == 1) {//如果只有一个移除属性
                                        procSource.removeAttribute("to");
                                    }
                                    else {//否则从当前数组中弹出当前线ID
                                        for (i = 0; i < sourceTo.length; i++) {
                                            if (sourceTo[i] == o.getAttribute("lineID")) {
                                                sourceTo.splice(i, 1);
                                            }
                                        }
                                        procSource.setAttribute("to", sourceTo.join(","));//更新入线
                                    }
                                    if(ProcTo.getAttribute("to")){//目标节点存在入线则增加1
                                        ProcTo.setAttribute("to", ProcTo.getAttribute("to") + "," + o.getAttribute("lineID"));
                                    }
                                    else{//否则设置
                                        ProcTo.setAttribute("to",o.getAttribute("lineID"));
                                    }
                                    g(o.getAttribute("lineID")).setAttribute("to", ProcTo.getAttribute("id"));//改变线的终止节点
                                }
                            }
                            correctLinePoints(o.getAttribute("lineID"), ptMoveType, ProcTo, e.clientX + scrollElement.scrollLeft / _ZOOM, e.clientY + scrollElement.scrollTop / _ZOOM);
                            //g(o.getAttribute("lineID")).setAttribute("marker-end", "url(#" + markerGreen.attr("id") + ")");
                        }
                        else {
                        }
                        g(o.getAttribute("lineID")).removeAttribute("circleID");
                        SVG.get(o.id).remove();
                    }
                }
                else {
                }
                if (e.stopPropagation) {
                    e.stopPropagation();
                } else {
                    e.cancleBubble = true;
                }
                canChange = 1;
            }
        };

    function init() {
        if(!canChange){
            return;
        }
        canChange = 0;
        var o = defaultOptions.moveObject, i,np,e;
        if(image!="default" && image!="selectMore"){
            return false;
        }
        if(o.nodeName=="ellipse" && g(o.getAttribute("lineID"))){//如果是拖动折线点添加的小圆
        	e = evt;//修复Firefox event undefined
            initX = e.clientX/_ZOOM;
            initY = e.clientY/_ZOOM;
            if(o.getAttribute("cx")){
                deltaX = e.clientX/_ZOOM - o.getAttribute("cx");
                deltaY = e.clientY/_ZOOM - o.getAttribute("cy");
                //小圆所属线的坐标位置
                line_points = g(o.getAttribute("lineID")).getAttribute("points");
                //拖动第几个点
                nPoint = getcurPoint(g(o.getAttribute("lineID")),[Number(o.getAttribute("cx"))+6,Number(o.getAttribute("cy"))+6],line_points);
            }
            if(document.setCapture) o.setCapture();//鼠标捕获
            if(moveflag){//可以拖动，事件绑定
                addEvent(document,"mousemove",action.move);
                addEvent(document,"mouseup",action.stop);
                addEvent(document,"losecapture",action.stop);
            }
        }
        else{
            o.onmousedown = function (event) {
                e = event || window.event;
                initX = e.clientX / _ZOOM;
                initY = e.clientY / _ZOOM;
                //偏移距离
                deltaX = e.clientX / _ZOOM - o.getAttribute("x");
                deltaY = e.clientY / _ZOOM - o.getAttribute("y");
                if(o.nodeName=="ellipse"){
                    deltaX = e.clientX / _ZOOM - o.getAttribute("cx");
                    deltaY = e.clientY / _ZOOM - o.getAttribute("cy");
                }
                if(o.getAttribute("from") && o.getAttribute("from")!=""){//存在出线
                    lineArray1 = o.getAttribute("from").split(",");//出线ID数组
                    for(i=0,len=lineArray1.length;i<len;i++){
                        pointArray1[i] = g(lineArray1[i]).getAttribute("points").split(" ");//出线每个点坐标数组
                        fromArray[i] = g(lineArray1[i]).getAttribute("points");//出线坐标数组
                        np = pointArray1[i][0].split(",");//出线第一个点
                        p1_1[i] = parseInt(np[0]);//出线第一个点x坐标数组
                        p1_2[i] = parseInt(np[1]);//出线第一个点Y坐标数组
                    }
                }
                if(o.getAttribute("to") && o.getAttribute("to")!=""){//存在进线
                    lineArray2 = o.getAttribute("to").split(",");//进线ID数组
                    for( i=0,len=lineArray2.length;i<len;i++){
                        pointArray2[i] = g(lineArray2[i]).getAttribute("points").split(" ");//进线每个点坐标数组
                        toArray[i] = g(lineArray2[i]).getAttribute("points");//进线坐标数组
                        np = pointArray2[i][pointArray2[i].length-1].split(",");//进线最后一个点
                        p2_1[i] = parseInt(np[0]);//进线最后一个点x坐标数组
                        p2_2[i] = parseInt(np[1]);//进线最后一个点Y坐标数组
                    }
                }
                if (document.setCapture) o.setCapture();//鼠标捕获
                if (moveflag&&(image=="default" || image=="selectMore")) {
                    addEvent(document, "mousemove", action.move);
                    addEvent(document, "mouseup", action.stop);
                    addEvent(document, "losecapture", action.stop);//焦点丢失
                }
                if (e.preventDefault) {//阻止默认事件
                    e.preventDefault();
                } else {
                    return false;
                }
            };
        }
        if (g(o.getAttribute("textID"))) {//如果定位到文本，则将事件交给文本所在图形
            g(o.getAttribute("textID")).onmousedown = o.onmousedown;
        }
        if (g(o.getAttribute("shapeID"))) {//如果定位到圆内path，则将事件交给path所在图形
            g(o.getAttribute("shapeID")).onmousedown = o.onmousedown;
        }
    }

    return init();
}

/**
 * 线条拖动
 * @param o
 * @returns {*}
 */
function addPointMove(o){
    var defaultOptions = {
            moveObject:o
        },
        deltaX = 0, deltaY = 0, initX = 0, initY = 0,MouseX = 0,MouseY = 0,
        nPoint,points,isMove=0,addFlag,canpush=1,
        action = {
            move:function(event){
                var e = event || window.event,
                    o = defaultOptions.moveObject;
                MouseX = (e.clientX+scrollElement.scrollLeft)/_ZOOM;
                MouseY = (e.clientY+scrollElement.scrollTop)/_ZOOM;
                if(Math.abs(MouseX-initX)>5 || Math.abs(MouseY-initY)>5){//在X轴或Y轴移动距离大于5才生效
                    if(addFlag){
                        points = o.getAttribute("points");
                        var newPoints = addPolylinePoint(o,[initX,initY]);//增加一个折线点
                        nPoint = getcurPoint(o,[initX,initY],newPoints);//计算增加的点事第几个点
                        addFlag = false;//标志置为false，之后不再增加点，改为改变增加点的坐标位置
                    }
                    changePolylinePoint(o,[MouseX-deltaX,MouseY-deltaY],nPoint,points);//改变增加点的坐标位置
                    if(nPoint){
                        if(canpush) {
                            canpush = 0;
                            //移动了 进栈
                            unreShiftMove(o.id, points, undoAction, "changePoint");
                        }
                        isMove=1;
                    }
                }
                if(e.stopPropagation){
                    e.stopPropagation();
                }else{
                    e.cancleBubble = true;
                }
            },
            stop:function(event){
                canpush = 1;
                var e = event || window.event;
                removeEvent(document,"losecapture",action.stop);
                removeEvent(document,"mouseup",action.stop);
                removeEvent(document,"mousemove",action.move);
                if(document.releaseCapture) document.releaseCapture();
                isMove=0;
                if(e.stopPropagation){
                    e.stopPropagation();
                }else{
                    e.cancleBubble = true;
                }
                changeSaveStatus('save');
                redoAction.length = 0;
            }
        };
    function init(){
        var	o = defaultOptions.moveObject;
        if(image!="default" && image!="selectMore"){
            return false;
        }
        o.onmousedown = function(event){
            var e = event || window.event;
                initX = (e.clientX+scrollElement.scrollLeft)/_ZOOM;
                initY = (e.clientY+scrollElement.scrollTop)/_ZOOM;
                if(addPointOrNot(o,[initX,initY])){
                    addFlag = true;//增加折线点标志,true代表可增加
                    if(document.setCapture) o.setCapture();
                    addEvent(document,"mousemove",action.move);
                    addEvent(document,"mouseup",action.stop);
                    addEvent(document,"losecapture",action.stop);
                }
                else{
                	evt = e;//修复Firefox event undefined
                    drag(g(o.getAttribute("cricleID")));
                }
            if(e.preventDefault){
                e.preventDefault();
            }else{
                return false;
            }

        };
    }
    return init();
}