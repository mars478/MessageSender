(function (root, factory) {
    'use strict';

    if (typeof define === 'function' && define.amd) { // AMD. Register as an anonymous module.
        define([], factory);
    } else if (typeof exports === 'object') { // Node. Does not work with strict CommonJS, but only CommonJS-like environments that support module.exports, like Node.
        module.exports = factory();
    } else { // Browser globals (root is window)
        root.autosize = factory();
    }
}(this, function () {
    function main(ta) {
        if (!ta || !ta.nodeName || ta.nodeName !== 'TEXTAREA' || ta.hasAttribute('data-autosize-on')) {
            return;
        }

        var maxHeight;
        var heightOffset;

        function init() {
            var style = window.getComputedStyle(ta, null);

            if (style.resize === 'vertical') {
                ta.style.resize = 'none';
            } else if (style.resize === 'both') {
                ta.style.resize = 'horizontal';
            }

            ta.style.wordWrap = 'break-word';      // horizontal overflow is hidden, so break-word is necessary for handling words longer than the textarea width

            var width = ta.style.width;  // Chrome/Safari-specific fix: When the textarea y-overflow is hidden, Chrome/Safari doesn't reflow the text to account for the space made available by removing the scrollbar. This workaround will cause the text to reflow.
            ta.style.width = '0px';
            // Force reflow:
            ta.offsetWidth;
            ta.style.width = width;
            maxHeight = style.maxHeight !== 'none' ? parseFloat(style.maxHeight) : false;

            if (style.boxSizing === 'content-box') {
                heightOffset = -(parseFloat(style.paddingTop) + parseFloat(style.paddingBottom));
            } else {
                heightOffset = parseFloat(style.borderTopWidth) + parseFloat(style.borderBottomWidth);
            }

            adjust();
        }

        function adjust() {
            var startHeight = ta.style.height;
            var htmlTop = document.documentElement.scrollTop;
            var bodyTop = document.body.scrollTop;

            ta.style.height = 'auto';

            var endHeight = ta.scrollHeight + heightOffset;

            if (maxHeight !== false && maxHeight < endHeight) {
                endHeight = maxHeight;
                if (ta.style.overflowY !== 'scroll') {
                    ta.style.overflowY = 'scroll';
                }
            } else if (ta.style.overflowY !== 'hidden') {
                ta.style.overflowY = 'hidden';
            }
            ta.style.height = ta.scrollHeight + 'px';
//            console.log('maxHeight:' + maxHeight + ':::::ta.scrollHeight:' + ta.scrollHeight + '::::heightOffset:' + heightOffset + ':::::endHeight:' + endHeight + '::::::ta.style.height:' + ta.style.height);

            document.documentElement.scrollTop = htmlTop;      // prevents scroll-position jumping
            document.body.scrollTop = bodyTop;

            if (startHeight !== ta.style.height) {
                var evt = document.createEvent('Event');
                evt.initEvent('autosize.resized', true, false);
                ta.dispatchEvent(evt);
            }
        }

        function keyListener(e) {
            e = e ? e : window.event;
            if (e.keyCode === 13) {
                var el = $(ta);
                var v = el.val();
                if (!v || v.slice(-2) !== '\n\n') {
                    e.preventDefault();
                    el.val((v ? v : '') + '\n');
                    el.blur().focus();
                    adjust();
                    e.stopPropagation && (e.stopPropagation());
                    e.cancelBubble = true;
                    return false;
                }
            }
        }

        if ('onpropertychange' in ta && 'oninput' in ta) {  // IE9 does not fire onpropertychange or oninput for deletions, so binding to onkeyup to catch most of those events. There is no way that I know of to detect something like 'cut' in IE9.
            ta.addEventListener('keyup', adjust);
        }

        window.addEventListener('resize', adjust);
        ta.addEventListener('keydown', keyListener);
        ta.addEventListener('input', adjust);
        ta.addEventListener('focus', adjust);

        ta.addEventListener('autosize.update', adjust);

        ta.addEventListener('autosize.destroy', function (style) {
            window.removeEventListener('resize', adjust);
            ta.removeEventListener('input', adjust);
            ta.removeEventListener('keydown', keyListener);
            ta.removeEventListener('keyup', adjust);
            ta.removeEventListener('autosize.destroy');

            Object.keys(style).forEach(function (key) {
                ta.style[key] = style[key];
            });

            ta.removeAttribute('data-autosize-on');
        }.bind(ta, {
            height: ta.style.height,
            overflow: ta.style.overflow,
            overflowY: ta.style.overflowY,
            wordWrap: ta.style.wordWrap,
            resize: ta.style.resize
        }));

        ta.setAttribute('data-autosize-on', true);
        ta.style.overflow = 'hidden';
        ta.style.overflowY = 'hidden';

        init();
    }

    // Do nothing in IE8 or lower
    if (typeof window.getComputedStyle !== 'function') {
        return function (elements) {
            return elements;
        };
    } else {
        return function (elements) {
            if (elements && elements.length) {
                Array.prototype.forEach.call(elements, main);
            } else if (elements && elements.nodeName) {
                main(elements);
            }
            return elements;
        };
    }
}));