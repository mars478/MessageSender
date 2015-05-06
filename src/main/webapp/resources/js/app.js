define([
    'jquery',
    'underscore',
    'backbone',
    'routerIndex', // Request router.js
], function ($, _, Backbone, Router) {
    var initialize = function () {
        Router.initialize();
    }

    return {
        initialize: initialize
    };
});