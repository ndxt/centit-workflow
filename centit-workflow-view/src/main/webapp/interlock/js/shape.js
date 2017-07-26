(function(Interlock) {
    'use strict';
    if (!Interlock) return;

    var Shape = Interlock.Shape = Class.extend(function() {
        this.constructor = function (options) {

            for (var key in options) {
                var value = parseInt(options[key]);

                if (!isNaN(value)) {
                    options[key] = value;
                }
            }

            this.options = options;
            this.id = options.id;
            this.name = options.name;

            return options;
        };

        this.background = "#fff";

        this.color = "#00f";

        this.font = {size: 14, "text-anchor": "middle"};

        this.border = {color:"#00f"};

        this.draw = function() {

        }
    });

    // 业务流程
    Interlock.Rect = Shape.extend(function() {
        this.constructor = function (options) {

            options = this._super(options);

            this.x = options.x;
            this.y = options.y;
            this.width = options.width;
            this.height = options.height;
        };

        this.shapeType = 'roundRect';

        this.draw = function(group) {
            group.rect(this.width, this.height)
                .attr({
                    id: this.id
                })
                .x(this.x)
                .y(this.y)
                .fill(this.background)
                .stroke(this.border);

            group.text(this.name)
                .x(this.x + this.width / 2)
                .y(this.y + this.height / 2 - 7)
                .fill(this.color)
                .font(this.font);
        }
    });

    // 路由节点
    var Route = Interlock.Route = Shape.extend(function() {
        this.constructor = function (options) {
            options = this._super(options);

            this.cx = options.cx;
            this.cy = options.cy;
        };

        this.shapeType = 'double-oval';
        this.width = 40;
        this.height = 40;

        this.drawText = function (group) {
            group.text(this.name)
                .x(this.cx)
                .y(this.cy - 7)
                .fill(this.color)
                .font(this.font);
        };

        this.drawPath = null;

        this.draw = function(group) {

            group.ellipse(this.width, this.height)
                .attr({
                    id: this.id
                })
                .cx(this.cx)
                .cy(this.cy)
                .fill(this.background)
                .stroke(this.border);

            if (this.drawText) {
                this.drawText(group);
            }

            if (this.drawPath) {
                this.drawPath(group);
            }
        };
    });

    // 分支
    Interlock.RouteFen = Route.extend(function() {
        this.constructor = function(options) {
            this._super(options);
        };

        this.shapeType = 'oval-fen';

        this.drawText = null;

        this.drawPath = function(group) {
            var x = this.cx - 15,
                y = this.cy;

            group.path()
                .fill('none')
                .stroke({
                    width: 1.3,
                    color: '#00f'
                })
                .M(x, y)
                .l(15, -15)
                .l(15, 15)
                .l(-15, 15)
                .Z();
        };
    });

    // 多实例
    Interlock.RouteMulti = Route.extend(function() {
        this.constructor = function(options) {
            this._super(options);
        };

        this.shapeType = 'oval-multi';

        this.drawText = null;

        this.drawPath = function(group) {

            var x = this.cx - this.width / 3 + 6,
                y = this.cy - 4;

            group.path()
                .fill('none')
                .stroke({
                    width: 1.3,
                    color: '#00f'
                })
                .M(x - 6, y + 3)
                .h(20)
                .v(10)
                .h(-20)
                .Z()

                .M(x - 3, y + 4)
                .v(-5)
                .h(20)
                .v(10)
                .h(-3)

                .M(x + 1, y)
                .v(-5)
                .h(20)
                .v(10)
                .h(-3);
        };
    });

    // 汇聚
    Interlock.RouteJu = Route.extend(function() {
        this.constructor = function(options) {
            this._super(options);
        };

        this.shapeType = 'oval-ju';

        this.drawText = null;

        this.drawPath = function(group) {

            var x = this.cx,
                y = this.cy + 10;

            group.path()
                .fill('none')
                .stroke({
                    width: 1.3,
                    color: '#00f'
                })
                .M(x - 10, y - 20)
                .L(x, y - 10)
                .L(x + 10, y - 20)

                .M(x - 10, y - 12)
                .L(x, y - 3)
                .L(x + 10, y - 12)

                .M(x - 10, y - 4)
                .L(x, y + 5)
                .L(x + 10, y - 4)
        };
    });

    // 并行
    Interlock.RouteBing = Route.extend(function() {
        this.constructor = function(options) {
            this._super(options);
        };

        this.shapeType = 'oval-bing';

        this.drawText = null;

        this.drawPath = function(group) {

            var x = this.cx,
                y = this.cy;

            group.path()
                .fill('none')
                .stroke({
                    width: 1.3,
                    color: '#00f'
                })
                .M(x - 11, y - 8)
                .h(23)

                .M(x - 11, y)
                .h(23)

                .M(x - 11, y + 8)
                .h(23)
        };
    });
})(window.Interlock);