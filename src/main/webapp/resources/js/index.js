/* global require */

require.config({
    baseUrl: 'resources/js/libs',
    paths: {
        jquery: 'jquery-1.11.2.min',
        jqueryui: 'jquery-ui',
        growl: 'jquery.growl',
        inputmask: 'jquery.inputmask',
        datetimepicker: 'jquery.datetimepicker',
        scrollbar: 'perfect-scrollbar',
        backbone: 'backbone-min',
        bootstrap: 'bootstrap.min',
        underscore: 'underscore-min',
        tokenfield: 'bootstrap-tokenfield',
        autosize: 'taResizer'
    },
    shim: {// Use shim for plugins that does not support ADM
        "bootstrap": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },
        "growl": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },
        "scrollbar": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },
        "jqueryui": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },
        "inputmask": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },
        "datetimepicker": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        }
    },
    enforceDefine: true
});
require(["jquery", "underscore", "backbone", "autosize", "bootstrap", "growl", "datetimepicker", "jqueryui", "tokenfield"], function ($, _, Backbone, autosize) {
    /* BB model */
    var Message = Backbone.Model.extend({
        url: function () {
            var suffix = this.id ? ('/' + encodeURIComponent(this.id)) : '';
            return 'message' + suffix;
        },
        defaults: {
            repeats: 1,
            interval: 15, // minutes
            header: null,
            body: null,
            action: null,
            startDate: null,
            expireDate: null
        }
    });
    /* BB collection */
    var Messages = Backbone.Collection.extend({
        model: Message,
        url: "message"
    });
    /* BB view */
    var IndexView = Backbone.View.extend({
        el: $('#msgForm'),
        server: null,
        templates: [
            _.template($('#msgListTpl').html()),
            _.template($('#newMsg').html()),
            _.template($('#newBlock').html())
        ],
        render: function () {
            var v = this;
            v.model.fetch({
                error: function (model, response) {
                    $.growl.warning({title: 'Ошибка', message: 'Не удалось синхронизировать сообщения'});
                },
                success: function (model, response) {
                    var li = $("#navPanel").find("li");
                    var i = li.index(li.filter('.active'));
                    var template = v.templates[i]({messages: model.toJSON()});
                    v.$el.find('div.active').html(template);
                    v.refreshServer();
                    v.decorateFields();
                    debugger;
                    v.server && $('textarea[type="taginput"][id^="server"]:visible').val(v.server).change();
                }
            });
        },
        decorateFields: function () {
            _.each($('[type="datepicker"]:visible'), function (e) {
                $(e).datetimepicker();
            });
            _.each($('textarea[type="taginput"]:visible'), function (e) {
                $(e).tokenfield({'createTokensOnBlur': 'true', 'delimiter': ['#;']}); //'itemAdded': tChange, 'itemRemoved': tChange,
            });
            _.each($('textarea:visible'), function (e) {
                autosize($(e));
            });
        },
        events: {
            'add': 'add',
            'click #navPanel li a': 'changeTab',
            'change #serverInput': 'setServer',
            'dblclick #serverInput': 'refreshServer'
        },
        setServer: function () {
            var s = $('#serverInput');
            $.post("server", {server: s.val()}, function (data) {
                s.val(data);
            }, "json");
        },
        refreshServer: function () {
            var s = $('#serverInput');
            var v = this;
            $.post("server", {server: 'refresh'}, "json").success(function (a) {
                a && (v.server = a);
                s.val(a);
            });
        },
        changeTab: function () {
            this.render();
        },
        initialize: function () {
            this.model = new Messages();
            this.render();
        },
        fillModel: function () {
            var ret = [];
            var info = new Message();
            var block = null;
            var inputs = this.$el.find('input[name]:visible');
            var textAreas = this.$el.find('textarea[name]:visible');
            inputs.add(textAreas).each(function () {
                var name = this.name;
                var value = this.value;
                if (name.indexOf('Date') > -1) {
                    try {
                        value = new Date(value);
                    } catch (e) {
                        value = null;
                    }
                }
                if (name.indexOf('block.') === 0) {
                    !block && (block = new Message());
                    name = name.replace('block.', '');
                    block.set(name, value);
                } else {
                    info.set(name, value);
                }
            });
            if (block) {
                block.set('startDate', info.get('expireDate'));
                block.set('action', 'LOGOUT');
                info.set('action', 'LOGOUT_WARN');
                ret.push(block);
            }
            ret.push(info);
            return ret;
        },
        add: function () {
            var v = this;
            var srvString = $('textarea[type="taginput"]:visible').val();
            var srv = srvString.split('#;');
            if (!srvString.trim()) {
                $.growl.warning({title: 'Ошибка при сохранении', message: 'Не заданы серверы'});
                v.render();
                return;
            }
            var m = this.fillModel();
            _.each(m, function (el) {
                el.save({servers: srv}, {
                    success: function (model, response) {
                        $.growl.notice({title: 'Сохранено', message: model.get('header')});
                        v.model.add(m);
                        v.render();
                    },
                    error: function (model, response) {
                        $.growl.warning({title: 'Ошибка при сохранении', message: model.get('header')});
                        v.render();
                    }
                });
            });
        },
        remove: function (id) {
            var v = this;
            var m = this.model.where({id: id})[0];
            m.destroy({
                success: function (model, response) {
                    $.growl.notice({title: "Удалено", message: model.get('header')});
                    v.render();
                },
                error: function (model, response) {
                    $.growl.warning({title: 'Ошибка при удалении', message: model.get('header')});
                    v.render();
                }
            });
        }
    });
    view = new IndexView();

    window.delMsg = function (id) {
        view.remove(id);
    };
});