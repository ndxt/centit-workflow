(function() {

    var Interlock = window.Interlock = function(container) {
        var graph = window.graph = this.graph = new Q.Graph(container);
        graph.originAtCenter = false;
        // graph.enableWheelZoom = false;

        this.elements = {};
    };

    Interlock.prototype.draw = _draw;
    Interlock.prototype.editable = _editable;
    Interlock.prototype.exportJSON = function() {
        return {
            nodes: this.nodes,
            edges: this.edges
        }
    };

    ////////////////////////////

    function _draw(json) {
        var graph = this.graph,
            elements = this.elements;

        // 画节点
        if (json.nodes) {
            json.nodes.forEach(function(data) {
                var node = graph.createNode(data.name, data.cx || data.x || 0, data.cy || data.y || 0);
                node.set("data", data);
                node.setStyle(Q.Styles.LABEL_RADIUS, 0);
                node.setStyle(Q.Styles.LABEL_PADDING, new Q.Insets(2, 5));
                node.setStyle(Q.Styles.LABEL_BACKGROUND_COLOR, "#FFFFF5");
                elements[data.id] = node;
            });
            this.nodes = json.nodes;
        }

        // 画连线
        if (json.edges) {
            json.edges.forEach(function(data) {
                var from = elements[data.from];
                var to = elements[data.to];

                if(!from || !to){
                    return;
                }

                var edge = graph.createEdge(data.name, from, to);
                edge.setStyle(Q.Styles.LABEL_RADIUS, 0);
                edge.setStyle(Q.Styles.LABEL_COLOR, "#AAA");
                edge.setStyle(Q.Styles.LABEL_ROTATABLE, false);
                edge.setStyle(Q.Styles.LABEL_BACKGROUND_COLOR, "#FFFFF5");
                edge.setStyle(Q.Styles.LABEL_ANCHOR_POSITION, Q.Position.CENTER_MIDDLE);
                edge.edgeType = Q.Consts.EDGE_TYPE_VERTICAL_HORIZONTAL;
                edge.set("data", data);
            });
            this.edges = json.edges;
        }

        return this;
    }

    function _editable() {

        var graph = this.graph;
        var currentElement;
        var highlightColor = '#FFDB19';

        function unhighlight(element){
            element.setStyle(Q.Styles.BACKGROUND_COLOR, null);
            currentElement.setStyle(Q.Styles.PADDING, null);
        }

        function highlight(element, x, y){

            console.log(x, y);
            element && graph.moveElements(element, 0, 0);

            if(currentElement == element){
                return;
            }
            if(currentElement){
                unhighlight(currentElement);
            }
            currentElement = element;
            if(!currentElement){
                return;
            }
            currentElement.setStyle(Q.Styles.BACKGROUND_COLOR, highlightColor);
            currentElement.setStyle(Q.Styles.PADDING, new Q.Insets(5));
        }

        graph.addCustomInteraction({
            ondrag: function(evt, graph){
                var ui = graph.getUIByMouseEvent(evt);
                if(!ui){
                    graph.cursor = null;
                    highlight(null);
                    return;
                }
                graph.cursor = "move";
                highlight(ui.data, evt.pageX, evt.pageY);
            },

            onrelease: function(evt, graph){
                graph.cursor = null;
                highlight(null);
            }
        });

    }

})();