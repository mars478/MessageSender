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
        render: function (newModel) {
            var v = this;
            v.model.fetch({
                error: function (model, response) {
                    $.growl.warning({title: 'Ошибка', message: 'Не удалось синхронизировать серверы'});
                },
                success: function (model, response) {
                    newModel && model.add(new srv.Server());
                    var t = v.template({servers: model.toJSON()});
                    v.$el.html(t);
                    v.decorateFields();
                }
            });
        },
        decorateFields: function () {
            var v = this;
            v.el.ondragover = function (evt) {
                evt.stopPropagation();
                evt.preventDefault();
                evt.dataTransfer.dropEffect = 'copy';
                return false;
            };

            v.el.ondrop = function (evt) {
                evt.stopPropagation();
                evt.preventDefault();

                var results = [];
                var files = evt.dataTransfer.files;
                var size = files.length;
                var fLoad = function () {
                    if (!--size) {
                        v.addList(results.join('\n'));
                    }
                };

                $.each(files, function () {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        var r = e.target.result.replace(new RegExp('\n', 'g'), "#;").replace(new RegExp(' ', 'g'), "#;").replace(new RegExp('\t', 'g'), "#;");
                        results.push(r);
                        fLoad();
                    };
                    reader.onerror = fLoad;
                    reader.readAsBinaryString(this);
                });
            };
        },
        events: {
            'dblclick .editableRow': 'editServer',
            'click .save': 'saveServer',
            'click .newSrv': 'newServer',
            'click .chngActive': 'changeServer',
            'keydown .cellInput': 'keyDownCell'
        },
        initialize: function () {
            this.model = new srv.Servers();
            this.render();
        },
        inputToModel: function () {
            var id = $('tr.editableRow.editNow').has('input').attr("cid") || false;
            var curSrv = (id)
                    ? this.model.where({id: parseInt(id)})[0] // edit
                    : new srv.Server();  // new

            $('tr.editableRow td[name] input:visible').each(function () {
                curSrv.set(this.name.trim(), this.value);
            });
            this.add(curSrv, id);
            this.render();
            return curSrv;
        },
        add: function (m, patch) {
            var v = this;
            var opts = {success: function () {
                    $.growl.notice({title: 'Сохранено', message: m.get('header')});
                    v.model.add(m);
                    v.render();
                },
                error: function () {
                    $.growl.warning({title: 'Ошибка при сохранении', message: m.get('header')});
                    v.render();
                }};

            if (patch) {
                m.sync('patch', m, opts);
            } else {
                m.save({}, opts);
            }
        },
        addList: function (str) {
            var v = this;
            $.post("addServerList", {servers: str}, "json").success(function (a) {
                v.render();
            });
        },
        changeServer: function (id) {
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
        removeServer: function (id) {
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
        newServer: function () {
            if (this.$el.find('td[name] input').size()) {
                return;
            }
            this.render(true);
        },
        editServer: function (e) {
            if (this.$el.find('td[name] input').size()) {
                return;
            }

            var tr = $(e.currentTarget).addClass('editNow');
            tr.find("td[name]").each(function () {
                var el = $(this);
                var width = el.width() - 6;
                var value = el.text();
                var name = el.attr('name');
                el.html("<input name='" + name + " ' value='" + value + "' class='rEditor cellInput' style='width: " + width + "px'  />");
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
        sView.changeServer(id);
    };
    window.delSrv = function (id) {
        sView.removeServer(id);
    };

    return sView;
});
        