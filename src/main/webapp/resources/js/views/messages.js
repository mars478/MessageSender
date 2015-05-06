/* global require */

define([
    "jquery",
    "underscore",
    "backbone",
    "autosize",
    "q",
    "message",
    "bootstrap",
    "growl",
    "datetimepicker",
    "jqueryui",
    "tokenfield"], function ($, _, Backbone, autosize, Q, msg) {
    /* BB view */
    var MessageView = Backbone.View.extend({
        el: $('#msgForm'),
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
                    if ($('#serverInput:visible').size()) {
                        v.refreshLink();
                    } else if ($('textarea[type="taginput"]:visible').size()) {
                        v.refreshServers();
                    }
                    v.decorateFields();
                    v.server && $('textarea[type="taginput"][id^="server"]:visible').val(v.server).change();
                }
            });
        },
        decorateFields: function () {
            var v = this;
            var tagInputDelimiter = "#;";

            _.each($('[type="datepicker"]:visible'), function (e) {
                $(e).datetimepicker();
            });
            _.each($('textarea[type="taginput"]:visible'), function (e) {
                $(e).tokenfield({'createTokensOnBlur': 'true', 'delimiter': [tagInputDelimiter]}); //'itemAdded': tChange, 'itemRemoved': tChange,
            });
            _.each($('textarea:visible'), function (e) {
                autosize($(e));
            });

            _.each($('textarea.dropzone:visible').parent(), function (drop) {
                drop.addEventListener('dragover', function (evt) {
                    evt.stopPropagation();
                    evt.preventDefault();
                    evt.dataTransfer.dropEffect = 'copy';
                }, false);
                drop.addEventListener('drop', function (evt) {
                    evt.stopPropagation();
                    evt.preventDefault();
                    var files = evt.dataTransfer.files;
                    var reader = new FileReader();
                    reader.onload = function (event) {
                        var r = event.target.result.replace(new RegExp('\n', 'g'), tagInputDelimiter);
                        $(drop).find('textarea[type="taginput"]:visible').text(r).change();
                        v.setServers();
                    };
                    reader.readAsText(files[0]);
                }, false);
            });
        },
        events: {
            'click #navPanel li a': 'changeTab',
            'change #serverInput': 'setLink',
            'dblclick #serverInput': 'refreshLink',
            'click .add': 'add'
        },
        setServers: function () {
            var s = $('textarea[type="taginput"]:visible');
            $.post("serverList", {servers: s.text()}, "json").success(function (a) {
                var txt = _.map(a, function (a) {
                    return a.url;
                }).join('#;');
                s.text(txt).change();
            });
        },
        refreshServers: function () {
            $.post("serverList", {servers: 'refresh'}, "json").success(function (a) {
                var txt = _.map(a, function (a) {
                    return a.url;
                }).join('#;');
                $('textarea[type="taginput"]:visible').text(txt).change();
            });
        },
        setLink: function () {
            var s = $('#serverInput');
            $.post("link", {server: s.val()}, "json").success(function (a) {
                s.val(a);
            });
        },
        refreshLink: function () {
            $.post("link", {server: 'refresh'}, "json").success(function (a) {
                $('#serverInput').val(a);
            });
        },
        changeTab: function () {
            this.render();
        },
        initialize: function () {
            this.model = new msg.Messages();
            this.render();
        },
        fillModel: function () {
            var ret = [];
            var info = new msg.Message();
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
                    !block && (block = new msg.Message());
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
            var m = v.fillModel();
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
    mView = new MessageView();

    window.delMsg = function (id) {
        mView.remove(id);
    };

    return mView;
}
);
        