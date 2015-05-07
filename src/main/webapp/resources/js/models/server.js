define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    /* BB model */
    var Server = Backbone.Model.extend({
        url: function () {
            var suffix = this.id ? ('/' + encodeURIComponent(this.id)) : '';
            return 'server' + suffix;
        },
        defaults: {
            protocol: 'http',
            username: undefined,
            password: undefined,
            link: undefined,
            port: undefined,
            connected: false,
            added: true
        }
    });
    /* BB collection */
    var Servers = Backbone.Collection.extend({
        model: Server,
        url: "server"
    });

    return {
        Server: Server,
        Servers: Servers
    };
});