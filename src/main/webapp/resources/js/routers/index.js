define([
    'jquery',
    'underscore',
    'backbone',
    'messages',
    'servers'
], function ($, _, Backbone, MessageView, ServersView) {
    var AppRouter = Backbone.Router.extend({
        routes: {
            "messages": "showMessages",
            "servers": "showServers"
        }
    });

    var initialize = function () {
        var app_router = new AppRouter;
        app_router.on('route:showMessages', function () {
            $('.subView').hide().filter('#msgForm').show();
            MessageView.initialize();
        });
        app_router.on('route:showServers', function (actions) {
            $('.subView').hide().filter('#srvForm').show();
            ServersView.initialize();
        });
        Backbone.history.start();
    };

    return {
        initialize: initialize
    };
});