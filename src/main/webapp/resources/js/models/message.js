define([
    'underscore',
    'backbone'
], function (_, Backbone) {
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

    return {
        Message: Message,
        Messages: Messages
    };
});