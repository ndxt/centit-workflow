/**
 * Created by xu_ts on 2016/10/28.
 */
//事件绑定
$('#undo').on('click',function(){
    undo();
});
$('#redo').on('click',function(){
    redo();
});
//保存撤销动作栈
var undoAction = [];
//保存前进动作栈
var redoAction = [];
//可以入栈
var canPush = 1;
//撤销函数
function undo(){
    if(undoAction.length == 0){
        console.log('没有可以撤销');
        return;
    }
    if(canPush) {
        canPush = 0;
        changeSaveStatus('save');
        switch (undoAction[0].oAction) {
            case "move":
                doMove(0);
                canPush = 1;
                break;
            case "Muchchange":
                muchChange('undo');
                canPush = 1;
                break;
            case "del":
                doDelete(0);
                canPush = 1;
                break;
            case "draw":
                doDraw(0);
                canPush = 1;
                break;
            case "changePoint":
                doChangePoint(0);
                canPush = 1;
                break;
        }
    }
}
//前进撤销入口函数
function redo(){
    if(redoAction.length == 0){
        console.log('无法前进');
        return;
    }
    if(canPush) {
        canPush = 0;
        changeSaveStatus('save');
        switch (redoAction[0].oAction) {
            case "move":
                doMove(1);
                canPush = 1;
                break;
            case "Muchchange":
                muchChange('redo');
                canPush = 1;
                break;
            case "draw":
                doDraw(1);
                canPush = 1;
                break;
            case "del":
                doDelete(1);
                canPush = 1;
                break;
            case "changePoint":
                doChangePoint(1);
                canPush = 1;
                break;
        }
    }
}
//具体执行移动撤销函数
function doMove(type){
    var Action,node;
    //移动后的坐标,保存在unreAction或者redoAction栈中
    var x,y,to;
    //当前动作为撤销时
    if(type == 0){
        Action = undoAction;
        unreShiftMove(Action[0].oNode,[$("#"+undoAction[0].oNode)[0].getAttribute('x'),
            $("#"+undoAction[0].oNode)[0].getAttribute('y')],redoAction,"move");
    }
    //同上
    if(type == 1){
        Action = redoAction;
        unreShiftMove(Action[0].oNode,[$("#"+redoAction[0].oNode)[0].getAttribute('x'), $("#"+redoAction[0].oNode)[0].getAttribute('y')],undoAction,"move");
    }
    //多个节点移动时
    if(type == "unMuchchange"){
        Action = undoAction[0].nodes;
    }
    if(type == "reMuchchange"){
        Action = redoAction[0].nodes;
    }
    //获取当前操作的节点
    node = $('#'+Action[0].oNode)[0];
    if(node) {
        x = parseInt(Action[0].position[0]);
        y = parseInt(Action[0].position[1]);
    //还原图标位置
        node.setAttribute('x', x);
        node.setAttribute('y', y);
    }
    //改变节点对应文字说明的位置
    changeNodeTile(node,x,y);
    //还原图片所在线
    changeLine(node);
    //操作完成后，Action数组移出栈
    outStack(type,"node");
}
//撤销还原点
function doPoint(type){
    var Action,node,points,to;
    if(type == "unMuchchange"){
        Action = undoAction[0].points;
    }
    if(type == "reMuchchange"){
        Action = redoAction[0].points;
    }
    node = $("#"+Action[0].oNode)[0];
    points = Action[0].position;
    //还原图标位置
    if(node) {
        node.setAttribute('points', points);
    }
    //线所对应的文本
    changeTextP(node.id,node.getAttribute('points').split(' '));
    outStack(type,"points")
}
//具体执行删除函数
function doDelete(type){
    var Action,unshiftAction;
    if(type == 0){
        Action = undoAction;
        unshiftAction = redoAction;
    }
    else if(type == 1){
        Action = redoAction;
        unshiftAction = undoAction;
    }
    var node = Action[0].oNode.node;
    var shape = Action[0].nodeName;
    //保存现场
    if(shape=='rect') {
        //保存业务节点数据入向前栈
        unshiftAction.unshift({
                'oAction': 'draw',
                rx: 2,
                ry: 2,
                "id": node.getAttribute('id'),
                "title": node.getAttribute('title'),
                "shapetype": "roundRect",
                "filter": "url(#filter-rect)",
                "flowphase": node.getAttribute('flowphase'),
                "nodedesc": node.getAttribute('nodedesc'),
                "nodetype": node.getAttribute('nodetype'),
                "nodecode": node.getAttribute('nodecode'),
                "opttype": node.getAttribute('opttype'),
                "optcode": node.getAttribute('optcode'),
                "optbean": node.getAttribute('optbean'),
                "optparam": node.getAttribute('optparam'),
                "subwfcode": node.getAttribute('subwfcode'),
                "roletype": node.getAttribute('roletype'),
                "rolecode": node.getAttribute('rolecode'),
                "isaccounttime": node.getAttribute('isaccounttime'),
                "timeLimitType": node.getAttribute('timeLimitType'),
                "inheritType": node.getAttribute('inheritType'),
                "inheritNodeCode": node.getAttribute('inheritNodeCode'),
                "timeLimit": node.getAttribute('timeLimit'),
                "isTrunkLine": node.getAttribute('isTrunkLine'),
                "unitexp": node.getAttribute('unitexp'),
                "powerexp": node.getAttribute('powerexp'),
                "expireopt": node.getAttribute('expireopt'),
                "riskinfo": node.getAttribute('riskinfo'),
                "warningrule": node.getAttribute('warningrule'),
                "warningparam": node.getAttribute('warningparam'),
                "x": node.getAttribute('x'),
                "y": node.getAttribute('y'),
            });
    }
    if(shape=='line'){
            unshiftAction.unshift({
                'oAction': 'draw',
                "labID": node.getAttribute('labID'),
                "id": node.getAttribute('id'),
                'from': node.getAttribute('from'),
                'to': node.getAttribute('to'),
                'transform': node.getAttribute('transform'),
                "points": node.getAttribute('points'),
                "marker-end": node.getAttribute('marker-end'),
                "title": node.getAttribute('title'),
                "desc": node.getAttribute('desc'),
                "cond": node.getAttribute('cond'),
                "timeLimit": node.getAttribute('timeLimit'),
                "timeLimitType": node.getAttribute('timeLimitType'),
                "inheritType": node.getAttribute('inheritType'),
                "inheritNodeCode": node.getAttribute('inheritNodeCode'),
                "shapetype":node.getAttribute('shapetype'),
                "isaccounttime": node.getAttribute('isaccounttime'),
                "canignore": node.getAttribute('canignore')
            });
    }
    if(shape=='circle'){
        //判断结果种类过多，封装成函数
        if(type == 0) {
            stackAction(redoAction,node);
        }
        else if(type == 1){
            stackAction(undoAction,node);
        }
    }
    deletePro(node.id,1);
    //数组出栈
    outStack(type);
}
//前进属性画图
function doDraw(type){
    var data;
    if(type == 1){
    //读取redo栈
        data = redoAction[0];
    }
    else if(type == 0){
        data = undoAction[0];
    }
    if(data.shapetype == "roundRect"){
        drawReServiceNode(data,type);
    }
    if(data.shapetype == "polyline"){
        drawRePolyLine(data,type);
    }
    if(data.nodetype == "R"){
        drawReCircle(data,type);
    }
    outStack(type)
}
//撤销还原中画业务节点
function drawReServiceNode(data,type){
    var rect = gShape.image(serviceImg,0, 0);
    for(var key in data){
        if(key != 'oAction'){
            rect.node.setAttribute(key,data[key]);
        }
    }
    rect.node.setAttribute('width','40');
    rect.node.setAttribute('height','40');
    //创建文字
    var text = gText.text(data["title"]).x(rect.x()).y(
        rect.y() + parseInt(rect.attr("height"))  + 20).attr({
        dx : rect.x() + parseInt(rect.attr("width")) / 2,
        "title" : data["title"],
        "textWeight" : "9pt",
        "strokeWeight" : "1",
        "zIndex" : "1",
        "shapeID" : rect.attr("id")
    }).font({
        size : 14,
        "text-anchor" : "middle"
    }).fill("#00f");
    rect.attr({
        "textID" : text.attr("id"),
        "filter" : "url(#filter-rect)"
    });
    //撤销入栈
    if(type == 1) {
        unreShiftDel(rect,'rect',undoAction);
    }
    else if(type == 0){
        unreShiftDel(rect,'rect',redoAction);
    }
}
//撤销还原中画线
function drawRePolyLine(data,type) {
    var from = $('#'+data.from)[0];
    var to = $('#'+data.to)[0];
    var line;
    var x = ( (parseInt(from.getAttribute('x')))+ (parseInt(to.getAttribute('x'))))/ 2;
    var y = ( (parseInt(from.getAttribute('y')))+ (parseInt(to.getAttribute('y'))))/ 2;
    var pl = gLine.polyline().stroke({
        color: "#0000ff",
        width: 1.3
    }).fill("none");
    var text;
    for (var key in data) {
        if (key != 'oAction') {
            pl.node.setAttribute(key,data[key]);
        }
    }
    if(to.getAttribute('to')) {
        to.setAttribute('to', to.getAttribute('to') + ',' + data['id']);
    }
    else{
        to.setAttribute('to', data['id']);
    }
    if(from.getAttribute('from')) {
        from.setAttribute('from', from.getAttribute('from') + ',' + data['id']);
    }
    else{
        from.setAttribute('from', data['id']);
    }
    //恢复线条文字说明
    line = g("lineCon").innerHTML += "<div class='step' id='lab" + pl.attr("id")
        + "'>流程step</div>";

    addStyle(g("lab" + pl.attr("id")), {
        "left" : x +'px',
        "top" : y + 'px'
    });
    //改变文本
    text = $("#"+data['labID']);
    text.html(data['title']);
    text.css('x',data['title']);
    changeTextP(pl.node.id,pl.node.getAttribute('points').split(' '));
    //type用以区分当前动作是撤销还是前进
    if(type == 1) {
        unreShiftDel(pl,'line',undoAction);
    }
    else if(type == 0){
        unreShiftDel(pl,'line',redoAction);
    }
}
//撤销还原中画判断节点
function drawReCircle(data,type){
    var circle = gShape.image(branchImg,0, 0);
    for(var key in data){
        if(key != 'oAction'){
            circle.node.setAttribute(key,data[key]);
        }
    }
    //撤销入栈
    if(type == 1) {
        unreShiftDel(circle,'circle',undoAction);
    }
    else if(type == 0) {
        unreShiftDel(circle,'circle',redoAction);
    }
}
//寻找与节点有关的所有连线用于改变线的位置
function changeLine(node){
    var allLine = $('#s2 polyline');
    var points;
    var FP;
    var pointArray;
    for(var i = 0;i<allLine.length;i++){
        if(allLine[i].getAttribute('from') == node.id){
            points = allLine[i].getAttribute('points').split(' ');
            if(points.length == 2) {
                FP = formatLine(SVG.get(SVG.get(allLine[i].id).attr("from")), SVG.get(SVG.get(allLine[i].id).attr("to")));
                if (FP["p1"]) {
                    g(allLine[i].id).setAttribute("points", FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0] + "," + FP["p2"][1]);
                    pointArray = ((FP["p1"]).join(',')).split().concat(((FP["p2"]).join(',')).split());
                    //改变线所对应的文本
                    changeTextP(allLine[i].id,pointArray);
                }
            }
            else{
                points[0] = formatPoint(points[1], node, 0);
                points[0] = formatPoint(points[1], node, 1);
                allLine[i].setAttribute("points", points.join(' '));
                changeTextP(allLine[i].id,points.join(' ').split(' '));
            }
        }
        //还原跟随线的文本
        if(allLine[i].getAttribute('to') == node.id){
            points = allLine[i].getAttribute('points').split(' ');
            if(points.length == 2){
                FP = formatLine(SVG.get(SVG.get(allLine[i].id).attr("from")), SVG.get(SVG.get(allLine[i].id).attr("to")));
                if (FP["p1"]) {
                    g(allLine[i].id).setAttribute("points", FP["p1"][0] + "," + FP["p1"][1] + " " + FP["p2"][0] + "," + FP["p2"][1]);
                    pointArray = ((FP["p1"]).join(',')).split().concat(((FP["p2"]).join(',')).split());
                    //改变线所对应的文本
                    changeTextP(allLine[i].id,pointArray);
                }
            }
            else {
                points = allLine[i].getAttribute('points').split(' ');
                points[points.length - 1] = formatPoint(points[points.length - 2], node, 1);
                allLine[i].setAttribute("points", points.join(' '));
                changeTextP(allLine[i].id,points.join(' ').split(' '));
            }
        }
    }
}
//判断节点比较复杂 单独使用一个函数才进行入栈
function stackAction(data,node){
    //汇聚节点有多余属性
    if(node.getAttribute('shapetype') == "oval-ju") {
        data.unshift({
            'oAction': 'draw',
            "id": node.getAttribute('id'),
            "title": node.getAttribute('title'),
            "convergetype":node.getAttribute('convergetype'),
            "convergeparam":node.getAttribute('convergeparam'),
            "optbean":node.getAttribute('optbean'),
            "xlink:href": node.getAttribute('xlink:href'),
            "shapetype": node.getAttribute('shapetype'),
            "routertype": node.getAttribute('routertype'),
            "flowphase": node.getAttribute('flowphase'),
            "nodedesc": node.getAttribute('nodedesc'),
            "nodetype": node.getAttribute('nodetype'),
            "nodecode": node.getAttribute('nodecode'),
            "isTrunkLine": node.getAttribute('isTrunkLine'),
            "filter": node.getAttribute('filter'),
            "width": node.getAttribute('width'),
            "height": node.getAttribute('height'),
            "x": node.getAttribute('x'),
            "y": node.getAttribute('y'),
        });
    }
    //多实例也有特殊属性
    else if(node.getAttribute('shapetype') == "oval-multi"){
        data.unshift({
            'oAction': 'draw',
            "id": node.getAttribute('id'),
            "title": node.getAttribute('title'),
            "xlink:href": node.getAttribute('xlink:href'),
            "shapetype": node.getAttribute('shapetype'),
            "routertype": node.getAttribute('routertype'),
            "flowphase": node.getAttribute('flowphase'),
            "nodedesc": node.getAttribute('nodedesc'),
            "nodetype": node.getAttribute('nodetype'),
            "nodecode": node.getAttribute('nodecode'),
            "isTrunkLine": node.getAttribute('isTrunkLine'),
            "filter": node.getAttribute('filter'),
            "width": node.getAttribute('width'),
            "height": node.getAttribute('height'),
            "x": node.getAttribute('x'),
            "y": node.getAttribute('y'),
            "multiinsttype":node.getAttribute('multiinsttype'),
            "unitexp":node.getAttribute('unitexp'),
            "powerexp":node.getAttribute('powerexp'),
            "roletype":node.getAttribute('roletype'),
            "rolecode":node.getAttribute('rolecode')
        });
    }
    //其他判断属性一直
    else{
        data.unshift({
            'oAction': 'draw',
            "id": node.getAttribute('id'),
            "title": node.getAttribute('title'),
            "xlink:href": node.getAttribute('xlink:href'),
            "shapetype": node.getAttribute('shapetype'),
            "routertype": node.getAttribute('routertype'),
            "flowphase": node.getAttribute('flowphase'),
            "nodedesc": node.getAttribute('nodedesc'),
            "nodetype": node.getAttribute('nodetype'),
            "nodecode": node.getAttribute('nodecode'),
            "isTrunkLine": node.getAttribute('isTrunkLine'),
            "filter": node.getAttribute('filter'),
            "width": node.getAttribute('width'),
            "height": node.getAttribute('height'),
            "x": node.getAttribute('x'),
            "y": node.getAttribute('y')
        });
    }
}
//还原画折现动作
function doChangePoint(type){
    var node,data,to;
    if(type == 0){
        data = undoAction[0];
        unreShiftMove(data.oNode,$("#"+data.oNode)[0].getAttribute('points'),redoAction,"changePoint");
    }
    else if(type == 1){
        data = redoAction[0];
        unreShiftMove(data.oNode,$("#"+data.oNode)[0].getAttribute('points'),undoAction,"changePoint");
    }
    //找到节点
    node =  $("#"+data.oNode)[0];
    //坐标还原
    if(data.passPosition) {
        //因为删除节点后，可以造成将线一起删除，这样会使得再次回撤时，造成找不到线的情况
        //所以再回撤移动时，先将此时节点的to清空
        $('#'+node.getAttribute('to'))[0].setAttribute('to','');
        node.setAttribute('points', data.passPosition);
        //再更新线的to
        node.setAttribute('to', data.to);
        //再更新原来节点的to,将线的末端还原到原来节点上
        $('#'+data.to)[0].setAttribute('to',data.oNode);
        //将有可能存在的折现小点删除
        document.getElementById('s4').innerHTML = "";
        //将选中的颜色还原
        node.setAttribute('stroke', '#0000ff');
        changeTextP(node.id,node.getAttribute('points').split(' '));
    }
    outStack(type);
}
//多个节点改变函数
function muchChange(obj){
    var node;
    if(obj == "undo"){
        node = undoAction;
    }
    else if(obj == "redo"){
        node = redoAction;
    }
    var arraynode = [];
    var arraypoint = [];
    var lengthNode = node[0].nodes.length;
    var lengthPoint = node[0].points.length;
    for(var i = 0;i<lengthPoint;i++){
        arraypoint.unshift({
            oNode: node[0].points[0].oNode,
            position: $("#"+node[0].points[0].oNode)[0].getAttribute('points'),
        })
        if(obj == "undo") {
            doPoint("unMuchchange");
        }
        else if(obj == "redo"){
            doPoint('reMuchchange');
        }
    }
    for(var i = 0;i<lengthNode;i++){
        arraynode.unshift({
            oNode: node[0].nodes[0].oNode,
            position: [$("#"+node[0].nodes[0].oNode)[0].getAttribute('x'),
                $("#"+node[0].nodes[0].oNode)[0].getAttribute('y')]
        });
        if(obj == "undo") {
            doMove('unMuchchange');
        }
        else if(obj == "redo"){
            doMove('reMuchchange');
        }
    }
    if(obj == "undo") {
        outStack(0)
        unreShiftMuch(arraynode,arraypoint,redoAction,"Muchchange");
    }
    if(obj == "redo") {
        outStack(1)
        unreShiftMuch(arraynode,arraypoint,undoAction,"Muchchange");
    }
}
//改变节点对应文字说明的位置函数
function changeNodeTile(node,x,y){
    var texts = document.getElementsByTagName('text');
    var text;
    //找寻图片对应的文本
    for(var i =0;i<texts.length;i++){
        if((texts[i].getAttribute('shapeID'))==(node.id)){
            text = texts[i];
            break;
        }
    }
    //还原图标文字位置
    if(text) {
        text.setAttribute("x", parseInt(x + 5));
        text.setAttribute("y", parseInt(y + (40 / 2) + 40));
        text.setAttribute("dx", parseInt(x + (40 / 2)));
    }
}
//操作完成后，Action数组移出栈
function outStack(type,obj){
    //type有4中状态0,1用来操作单个节点，unMuchange和reMuchchange用来代表多个节点
    if(type == 0) {
        undoAction.shift();
    }
    else if(type == 1){
        redoAction.shift();
    }
    else if(type == "unMuchchange"){
        if(obj == "node") {
            undoAction[0].nodes.shift();
        }
        else if(obj == "points"){
            undoAction[0].points.shift();
        }
    }
    else if(type == "reMuchchange"){
        if(obj == "node") {
            redoAction[0].nodes.shift();
        }
        else if(obj == "points"){
            redoAction[0].points.shift();
        }
    }
}