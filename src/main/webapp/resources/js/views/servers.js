/* global require */

define([
    "jquery",
    "underscore",
    "backbone",
    "autosize",
    "q",
    "server",
    "bootstrap",
    "growl",
    "datetimepicker",
    "jqueryui",
    "tokenfield"], function ($, _, Backbone, autosize, Q, srv) {
    /* BB view */
    var ServerView = Backbone.View.extend({
        el: $('#srvForm'),
        template: _.template($('#srvListTpl').html()),
        render: function () {
            var v = this;
            v.model.fetch({
                error: function (model, response) {
                    $.growl.warning({title: 'Ошибка', message: 'Не удалось синхронизировать серверы'});
                },
                success: function (model, response) {
                    var t = v.template({servers: model.toJSON()});
                    v.$el.html(t);
                }
            });
        },
        events: {
            'dblclick .editableRow': 'editSrv',
            'click .save': 'saveServer',
            'click .add': 'add',
            'click .chngActive': 'changeSrv',
            'keydown .cellInput': 'keyDownCell'
        },
        initialize: function () {
            this.model = new srv.Servers();
            this.render();
        },
        inputToModel: function () {
            var id = $('tr.editableRow').has('input').attr("cid") || false;
            var curSrv = (id)
                    ? this.model.where({id: id})[0] // edit
                    : new srv.Server();  // new

            $('tr.editableRow td[name]:visible').each(function () {
                curSrv.set(this.name, this.value);
            });
            this.add(curSrv, id);
            this.render();
            return curSrv;
        },
        add: function (m, patch) {
            var v = this;
            var opts = {
                success: function (model, response) {
                    $.growl.notice({title: 'Сохранено', message: model.get('header')});
                    v.model.add(m);
                    v.render();
                },
                error: function (model, response) {
                    $.growl.warning({title: 'Ошибка при сохранении', message: model.get('header')});
                    v.render();
                }
            };
            if (patch) {
                opts = $.extend(opts, {patch: true});
            }
            m.save({servers: srv}, opts);
        },
        changeSrv: function (id) {
            var v = this;
            var m = this.model.where({id: id})[0];
            m.set('added', !m.attributes.added);
            m.sync('patch', m, {success: function () {
                    $.growl.notice({title: 'Состояние изменено', message: m.get('link')});
                    v.render();
                },
                error: function () {
                    $.growl.warning({title: 'Ошибка при изменении состояния', message: m.get('link')});
                    v.render();
                }});
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
        },
        editSrv: function (e) {
            var tr = $(e.currentTarget);
            tr.find("td").each(function () {
                var value = this.val();
                this.parent().html();
            });
        },
        keyDownCell: function (e) {
            var code = e.keyCode;
            if (code === 13) { // enter
                e.preventDefault();
                this.inputToModel();
                return false;
            } else if (code === 27) { // esc
                e.preventDefault();
                this.render();
                return false;
            }
        }
    });
    sView = new ServerView();

    window.changeSrv = function (id) {
        debugger;
        sView.changeSrv(id);
    };
    window.delSrv = function (id) {
        sView.remove(id);
    };

    return sView;
});
        