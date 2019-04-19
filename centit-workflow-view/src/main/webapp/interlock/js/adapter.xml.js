(function($, Class, Promise) {
    'use strict';

    /**
     * 工作流数据XML适配器
     *
     * 1、读取服务器工作流xml数据
     * 2、将xml转换成json格式
     * 3、将json格式数据转换成xml
     * 4、保存xml到服务器
     */
    window.AdapterXML = Class.extend(function() {

        var self = this;

        this.constructor = function(options) {
            this.contextPath = options.contextPath || '/';
            this.debug = !!options.debug;
        };

        this.version = '0.0.1';

        // 读取服务器工作流xml数据
        this.load = _load;

        // 保存xml到服务器
        this.save = _save;

        // 将json格式数据转换成xml
        this.encode = _encode;

        // 将xml转换成json格式
        this.parse = _parse;

        // 获取读取链接
        this.getLoadUrl = _getLoadUrl;

        // 获取保存链接
        this.getSaveUrl = _getSaveUrl;

        this.dataType = 'xml';

        this.dataFilter = _dataFilter;

        ////////////////////////////////////////

        /**
         *
         * @param data
         * @returns {*}
         * @private
         */
        function _dataFilter(data) {
            data = JSON.parse(data);

            if (0 == data.code) {
                return data.data;
            }

            self.debug && console.warn('返回数据code不为0，请检查数据：', data);
            return data;
        }

        function _getLoadUrl(code) {
            return this.contextPath + 'workflow/flow/define/viewxml/'+ code +'/000';
        }

        function _getSaveUrl(code) {}

        function _load(code) {
            var url = self.getLoadUrl(code);

            return new Promise(function(resolve, reject) {
                $.ajax(url, {
                    dataType : self.dataType,
                    dataFilter: self.dataFilter,
                    success: function(xml) {
                        resolve(self.parse(xml));
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        reject(errorThrown);
                    }
                });
            });
        }

        function _save(code, json) {
            self.getSaveUrl(code);
            self.encode(json);
        }

        function _encode(json) {}

        function _parse(xml) {
            self.debug && console.log('原始xml：', xml);

            var datas = $.xml2json(xml)['#document']['CommitFlow']['Flow'],

                // 节点
                nodes = datas['Nodes']['Node'].map(function(node) {
                    var BaseProperties = node['BaseProperties']['$'],
                        VMLProperties = node['VMLProperties']['$'],
                        properties = $.extend({}, BaseProperties, VMLProperties);

                    return parseOptionsInt(properties, ['x', 'y', 'cx', 'cy', 'width', 'height']);
                }),

                // 连接线
                edges = datas['Transitions']['Transition'].map(function(edge) {
                    var BaseProperties = edge['BaseProperties']['$'],
                        VMLProperties = edge['VMLProperties']['$'],
                        LabelProperties = edge['LabelProperties']['$'],
                        properties = $.extend({}, BaseProperties, VMLProperties,
                            LabelProperties, { id: BaseProperties.id });

                    return parseOptionsInt(properties, ['x', 'y', 'cx', 'cy', 'width', 'height']);
                });

            self.debug && console.log('原始数据：', datas);
            self.debug && console.log('节点数据：', nodes);
            self.debug && console.log('链接数据：', edges);

            return {
                version: self.version,
                type: 'json',
                nodes: nodes,
                edges: edges
            };
        }

        function parseOptionsInt(options, keys) {
            if (typeof keys == 'string') {
                keys = [keys];
            }

            // 指定转换属性
            if (keys) {
                keys.forEach(function(name) {
                    var value = parseInt(options[name]);

                    if (!isNaN(value)) {
                        options[name] = value;
                    }
                });

                return options;
            }

            // 非指定，转换全部属性
            for (var name in options) {
                var value = parseInt(options[name]);

                if (!isNaN(value)) {
                    options[name] = value;
                }
            }
            return options;
        }
    });

    return AdapterXML;

})(jQuery, Class, Promise);
