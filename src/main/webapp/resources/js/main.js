/* global require */

require.config({
    baseUrl: 'resources/js',
    paths: {
        // ============ libs =============
        jquery: 'libs/jquery-1.11.2.min',
        jqueryui: 'libs/jquery-ui',
        dropzone: 'libs/dropzone-amd',
        growl: 'libs/jquery.growl',
        inputmask: 'libs/jquery.inputmask',
        datetimepicker: 'libs/jquery.datetimepicker',
        scrollbar: 'libs/perfect-scrollbar',
        backbone: 'libs/backbone-min',
        bootstrap: 'libs/bootstrap.min',
        underscore: 'libs/underscore-min',
        tokenfield: 'libs/bootstrap-tokenfield',
        autosize: 'libs/taResizer',
        q: 'libs/q',
        // =========== components =========
        routerIndex: 'routers/index',
        message: 'models/message',
        server: 'models/server',
        messages: 'views/messages',
        servers: 'views/servers'
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

require([// Load our app module and pass it to our definition function
    'app'
], function (App) {
    App.initialize();

    var stub = function (e) {
        e.preventDefault();
        return false;
    };
    document.body.ondragover = stub;
    document.body.ondrop = stub;
});