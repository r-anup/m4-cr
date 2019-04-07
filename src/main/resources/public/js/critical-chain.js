

var $jscomp = $jscomp || {};
$jscomp.scope = {};
$jscomp.arrayIteratorImpl = function(a) {
    var b = 0;
    return function() {
        return b < a.length ? {
            done: !1,
            value: a[b++]
        } : {
            done: !0
        }
    }
}
;
$jscomp.arrayIterator = function(a) {
    return {
        next: $jscomp.arrayIteratorImpl(a)
    }
}
;
$jscomp.makeIterator = function(a) {
    var b = "undefined" != typeof Symbol && Symbol.iterator && a[Symbol.iterator];
    return b ? b.call(a) : $jscomp.arrayIterator(a)
}
;
$jscomp.arrayFromIterator = function(a) {
    for (var b, c = []; !(b = a.next()).done; )
        c.push(b.value);
    return c
}
;
$jscomp.arrayFromIterable = function(a) {
    return a instanceof Array ? a : $jscomp.arrayFromIterator($jscomp.makeIterator(a))
}
;
$jscomp.ASSUME_ES5 = !1;
$jscomp.ASSUME_NO_NATIVE_MAP = !1;
$jscomp.ASSUME_NO_NATIVE_SET = !1;
$jscomp.SIMPLE_FROUND_POLYFILL = !1;
$jscomp.objectCreate = $jscomp.ASSUME_ES5 || "function" == typeof Object.create ? Object.create : function(a) {
    var b = function() {};
    b.prototype = a;
    return new b
}
;
$jscomp.underscoreProtoCanBeSet = function() {
    var a = {
        a: !0
    }
        , b = {};
    try {
        return b.__proto__ = a,
            b.a
    } catch (c) {}
    return !1
}
;
$jscomp.setPrototypeOf = "function" == typeof Object.setPrototypeOf ? Object.setPrototypeOf : $jscomp.underscoreProtoCanBeSet() ? function(a, b) {
        a.__proto__ = b;
        if (a.__proto__ !== b)
            throw new TypeError(a + " is not extensible");
        return a
    }
    : null;
$jscomp.inherits = function(a, b) {
    a.prototype = $jscomp.objectCreate(b.prototype);
    a.prototype.constructor = a;
    if ($jscomp.setPrototypeOf) {
        var c = $jscomp.setPrototypeOf;
        c(a, b)
    } else
        for (c in b)
            if ("prototype" != c)
                if (Object.defineProperties) {
                    var d = Object.getOwnPropertyDescriptor(b, c);
                    d && Object.defineProperty(a, c, d)
                } else
                    a[c] = b[c];
    a.superClass_ = b.prototype
}
;
$jscomp.getGlobal = function(a) {
    return "undefined" != typeof window && window === a ? a : "undefined" != typeof global && null != global ? global : a
}
;
$jscomp.global = $jscomp.getGlobal(this);
$jscomp.owns = function(a, b) {
    return Object.prototype.hasOwnProperty.call(a, b)
}
;
$jscomp.defineProperty = $jscomp.ASSUME_ES5 || "function" == typeof Object.defineProperties ? Object.defineProperty : function(a, b, c) {
    a != Array.prototype && a != Object.prototype && (a[b] = c.value)
}
;
$jscomp.polyfill = function(a, b) {
    if (b) {
        var c = $jscomp.global;
        a = a.split(".");
        for (var d = 0; d < a.length - 1; d++) {
            var e = a[d];
            e in c || (c[e] = {});
            c = c[e]
        }
        a = a[a.length - 1];
        d = c[a];
        b = b(d);
        b != d && null != b && $jscomp.defineProperty(c, a, {
            configurable: !0,
            writable: !0,
            value: b
        })
    }
}
;
$jscomp.polyfill("Object.values", function(a) {
    return a ? a : a = function(b) {
        var c = [], d;
        for (d in b)
            $jscomp.owns(b, d) && c.push(b[d]);
        return c
    }
}, "es8", "es3");
$jscomp.polyfill("Object.entries", function(a) {
    return a ? a : a = function(b) {
        var c = [], d;
        for (d in b)
            $jscomp.owns(b, d) && c.push([d, b[d]]);
        return c
    }
}, "es8", "es3");
$jscomp.SYMBOL_PREFIX = "jscomp_symbol_";
$jscomp.initSymbol = function() {
    $jscomp.initSymbol = function() {}
    ;
    $jscomp.global.Symbol || ($jscomp.global.Symbol = $jscomp.Symbol)
}
;
$jscomp.SymbolClass = function(a, b) {
    this.$jscomp$symbol$id_ = a;
    $jscomp.defineProperty(this, "description", {
        configurable: !0,
        writable: !0,
        value: b
    })
}
;
$jscomp.SymbolClass.prototype.toString = function() {
    return this.$jscomp$symbol$id_
}
;
$jscomp.Symbol = function() {
    function a(c) {
        if (this instanceof a)
            throw new TypeError("Symbol is not a constructor");
        return new $jscomp.SymbolClass($jscomp.SYMBOL_PREFIX + (c || "") + "_" + b++,c)
    }
    var b = 0;
    return a
}();
$jscomp.initSymbolIterator = function() {
    $jscomp.initSymbol();
    var a = $jscomp.global.Symbol.iterator;
    a || (a = $jscomp.global.Symbol.iterator = $jscomp.global.Symbol("Symbol.iterator"));
    "function" != typeof Array.prototype[a] && $jscomp.defineProperty(Array.prototype, a, {
        configurable: !0,
        writable: !0,
        value: function() {
            return $jscomp.iteratorPrototype($jscomp.arrayIteratorImpl(this))
        }
    });
    $jscomp.initSymbolIterator = function() {}
}
;
$jscomp.initSymbolAsyncIterator = function() {
    $jscomp.initSymbol();
    var a = $jscomp.global.Symbol.asyncIterator;
    a || (a = $jscomp.global.Symbol.asyncIterator = $jscomp.global.Symbol("Symbol.asyncIterator"));
    $jscomp.initSymbolAsyncIterator = function() {}
}
;
$jscomp.iteratorPrototype = function(a) {
    $jscomp.initSymbolIterator();
    a = {
        next: a
    };
    a[$jscomp.global.Symbol.iterator] = function() {
        return this
    }
    ;
    return a
}
;
$jscomp.iteratorFromArray = function(a, b) {
    $jscomp.initSymbolIterator();
    a instanceof String && (a += "");
    var c = 0
        , d = {
        next: function() {
            if (c < a.length) {
                var e = c++;
                return {
                    value: b(e, a[e]),
                    done: !1
                }
            }
            d.next = function() {
                return {
                    done: !0,
                    value: void 0
                }
            }
            ;
            return d.next()
        }
    };
    d[Symbol.iterator] = function() {
        return d
    }
    ;
    return d
}
;
$jscomp.polyfill("Array.prototype.keys", function(a) {
    return a ? a : a = function() {
        return $jscomp.iteratorFromArray(this, function(b) {
            return b
        })
    }
}, "es6", "es3");
$jscomp.polyfill("Object.is", function(a) {
    return a ? a : a = function(b, c) {
        return b === c ? 0 !== b || 1 / b === 1 / c : b !== b && c !== c
    }
}, "es6", "es3");
$jscomp.polyfill("Array.prototype.includes", function(a) {
    return a ? a : a = function(b, c) {
        var d = this;
        d instanceof String && (d = String(d));
        var e = d.length;
        c = c || 0;
        for (0 > c && (c = Math.max(c + e, 0)); c < e; c++) {
            var f = d[c];
            if (f === b || Object.is(f, b))
                return !0
        }
        return !1
    }
}, "es7", "es3");
$jscomp.checkStringArgs = function(a, b, c) {
    if (null == a)
        throw new TypeError("The 'this' value for String.prototype." + c + " must not be null or undefined");
    if (b instanceof RegExp)
        throw new TypeError("First argument to String.prototype." + c + " must not be a regular expression");
    return a + ""
}
;
$jscomp.polyfill("String.prototype.includes", function(a) {
    return a ? a : a = function(b, c) {
        var d = $jscomp.checkStringArgs(this, b, "includes");
        return -1 !== d.indexOf(b, c || 0)
    }
}, "es6", "es3");
$jscomp.polyfill("Array.from", function(a) {
    return a ? a : a = function(b, c, d) {
        c = null != c ? c : function(m) {
            return m
        }
        ;
        var e = []
            , f = "undefined" != typeof Symbol && Symbol.iterator && b[Symbol.iterator];
        if ("function" == typeof f) {
            b = f.call(b);
            for (var g = 0; !(f = b.next()).done; )
                e.push(c.call(d, f.value, g++))
        } else
            for (f = b.length,
                     g = 0; g < f; g++)
                e.push(c.call(d, b[g], g));
        return e
    }
}, "es6", "es3");
$jscomp.polyfill("String.prototype.startsWith", function(a) {
    return a ? a : a = function(b, c) {
        var d = $jscomp.checkStringArgs(this, b, "startsWith");
        b += "";
        var e = d.length
            , f = b.length;
        c = Math.max(0, Math.min(c | 0, d.length));
        for (var g = 0; g < f && c < e; )
            if (d[c++] != b[g++])
                return !1;
        return g >= f
    }
}, "es6", "es3");
$jscomp.findInternal = function(a, b, c) {
    a instanceof String && (a = String(a));
    for (var d = a.length, e = 0; e < d; e++) {
        var f = a[e];
        if (b.call(c, f, e, a))
            return {
                i: e,
                v: f
            }
    }
    return {
        i: -1,
        v: void 0
    }
}
;
$jscomp.polyfill("Array.prototype.find", function(a) {
    return a ? a : a = function(b, c) {
        return $jscomp.findInternal(this, b, c).v
    }
}, "es6", "es3");
$jscomp.checkEs6ConformanceViaProxy = function() {
    try {
        var a = {}
            , b = Object.create(new $jscomp.global.Proxy(a,{
            get: function(c, d, e) {
                return c == a && "q" == d && e == b
            }
        }));
        return !0 === b.q
    } catch (c) {
        return !1
    }
}
;
$jscomp.USE_PROXY_FOR_ES6_CONFORMANCE_CHECKS = !1;
$jscomp.ES6_CONFORMANCE = $jscomp.USE_PROXY_FOR_ES6_CONFORMANCE_CHECKS && $jscomp.checkEs6ConformanceViaProxy();
$jscomp.polyfill("WeakMap", function(a) {
    function b() {
        if (!a || !Object.seal)
            return !1;
        try {
            var h = Object.seal({})
                , l = Object.seal({})
                , k = new a([[h, 2], [l, 3]]);
            if (2 != k.get(h) || 3 != k.get(l))
                return !1;
            k["delete"](h);
            k.set(l, 4);
            return !k.has(h) && 4 == k.get(l)
        } catch (q) {
            return !1
        }
    }
    function c() {}
    function d(h) {
        if (!$jscomp.owns(h, f)) {
            var l = new c;
            $jscomp.defineProperty(h, f, {
                value: l
            })
        }
    }
    function e(h) {
        var l = Object[h];
        l && (Object[h] = function(k) {
                if (k instanceof c)
                    return k;
                d(k);
                return l(k)
            }
        )
    }
    if ($jscomp.USE_PROXY_FOR_ES6_CONFORMANCE_CHECKS) {
        if (a && $jscomp.ES6_CONFORMANCE)
            return a
    } else if (b())
        return a;
    var f = "$jscomp_hidden_" + Math.random();
    e("freeze");
    e("preventExtensions");
    e("seal");
    var g = 0
        , m = function(h) {
        this.id_ = (g += Math.random() + 1).toString();
        if (h) {
            h = $jscomp.makeIterator(h);
            for (var l; !(l = h.next()).done; )
                l = l.value,
                    this.set(l[0], l[1])
        }
    };
    m.prototype.set = function(h, l) {
        d(h);
        if (!$jscomp.owns(h, f))
            throw Error("WeakMap key fail: " + h);
        h[f][this.id_] = l;
        return this
    }
    ;
    m.prototype.get = function(h) {
        return $jscomp.owns(h, f) ? h[f][this.id_] : void 0
    }
    ;
    m.prototype.has = function(h) {
        return $jscomp.owns(h, f) && $jscomp.owns(h[f], this.id_)
    }
    ;
    m.prototype["delete"] = function(h) {
        return $jscomp.owns(h, f) && $jscomp.owns(h[f], this.id_) ? delete h[f][this.id_] : !1
    }
    ;
    return m
}, "es6", "es3");
$jscomp.MapEntry = function() {}
;
$jscomp.polyfill("Map", function(a) {
    function b() {
        if ($jscomp.ASSUME_NO_NATIVE_MAP || !a || "function" != typeof a || !a.prototype.entries || "function" != typeof Object.seal)
            return !1;
        try {
            var h = Object.seal({
                x: 4
            })
                , l = new a($jscomp.makeIterator([[h, "s"]]));
            if ("s" != l.get(h) || 1 != l.size || l.get({
                x: 4
            }) || l.set({
                x: 4
            }, "t") != l || 2 != l.size)
                return !1;
            var k = l.entries()
                , q = k.next();
            if (q.done || q.value[0] != h || "s" != q.value[1])
                return !1;
            q = k.next();
            return q.done || 4 != q.value[0].x || "t" != q.value[1] || !k.next().done ? !1 : !0
        } catch (t) {
            return !1
        }
    }
    if ($jscomp.USE_PROXY_FOR_ES6_CONFORMANCE_CHECKS) {
        if (a && $jscomp.ES6_CONFORMANCE)
            return a
    } else if (b())
        return a;
    $jscomp.initSymbolIterator();
    var c = new WeakMap
        , d = function(h) {
        this.data_ = {};
        this.head_ = g();
        this.size = 0;
        if (h) {
            h = $jscomp.makeIterator(h);
            for (var l; !(l = h.next()).done; )
                l = l.value,
                    this.set(l[0], l[1])
        }
    };
    d.prototype.set = function(h, l) {
        h = 0 === h ? 0 : h;
        var k = e(this, h);
        k.list || (k.list = this.data_[k.id] = []);
        k.entry ? k.entry.value = l : (k.entry = {
            next: this.head_,
            previous: this.head_.previous,
            head: this.head_,
            key: h,
            value: l
        },
            k.list.push(k.entry),
            this.head_.previous.next = k.entry,
            this.head_.previous = k.entry,
            this.size++);
        return this
    }
    ;
    d.prototype["delete"] = function(h) {
        h = e(this, h);
        return h.entry && h.list ? (h.list.splice(h.index, 1),
        h.list.length || delete this.data_[h.id],
            h.entry.previous.next = h.entry.next,
            h.entry.next.previous = h.entry.previous,
            h.entry.head = null,
            this.size--,
            !0) : !1
    }
    ;
    d.prototype.clear = function() {
        this.data_ = {};
        this.head_ = this.head_.previous = g();
        this.size = 0
    }
    ;
    d.prototype.has = function(h) {
        return !!e(this, h).entry
    }
    ;
    d.prototype.get = function(h) {
        return (h = e(this, h).entry) && h.value
    }
    ;
    d.prototype.entries = function() {
        return f(this, function(h) {
            return [h.key, h.value]
        })
    }
    ;
    d.prototype.keys = function() {
        return f(this, function(h) {
            return h.key
        })
    }
    ;
    d.prototype.values = function() {
        return f(this, function(h) {
            return h.value
        })
    }
    ;
    d.prototype.forEach = function(h, l) {
        for (var k = this.entries(), q; !(q = k.next()).done; )
            q = q.value,
                h.call(l, q[1], q[0], this)
    }
    ;
    d.prototype[Symbol.iterator] = d.prototype.entries;
    var e = function(h, l) {
        var k;
        var q = (k = l) && typeof k;
        "object" == q || "function" == q ? c.has(k) ? k = c.get(k) : (q = "" + ++m,
            c.set(k, q),
            k = q) : k = "p_" + k;
        if ((q = h.data_[k]) && $jscomp.owns(h.data_, k))
            for (h = 0; h < q.length; h++) {
                var t = q[h];
                if (l !== l && t.key !== t.key || l === t.key)
                    return {
                        id: k,
                        list: q,
                        index: h,
                        entry: t
                    }
            }
        return {
            id: k,
            list: q,
            index: -1,
            entry: void 0
        }
    }
        , f = function(h, l) {
        var k = h.head_;
        return $jscomp.iteratorPrototype(function() {
            if (k) {
                for (; k.head != h.head_; )
                    k = k.previous;
                for (; k.next != k.head; )
                    return k = k.next,
                        {
                            done: !1,
                            value: l(k)
                        };
                k = null
            }
            return {
                done: !0,
                value: void 0
            }
        })
    }
        , g = function() {
        var h = {};
        return h.previous = h.next = h.head = h
    }
        , m = 0;
    return d
}, "es6", "es3");
$jscomp.polyfill("Set", function(a) {
    function b() {
        if ($jscomp.ASSUME_NO_NATIVE_SET || !a || "function" != typeof a || !a.prototype.entries || "function" != typeof Object.seal)
            return !1;
        try {
            var d = Object.seal({
                x: 4
            })
                , e = new a($jscomp.makeIterator([d]));
            if (!e.has(d) || 1 != e.size || e.add(d) != e || 1 != e.size || e.add({
                x: 4
            }) != e || 2 != e.size)
                return !1;
            var f = e.entries()
                , g = f.next();
            if (g.done || g.value[0] != d || g.value[1] != d)
                return !1;
            g = f.next();
            return g.done || g.value[0] == d || 4 != g.value[0].x || g.value[1] != g.value[0] ? !1 : f.next().done
        } catch (m) {
            return !1
        }
    }
    if ($jscomp.USE_PROXY_FOR_ES6_CONFORMANCE_CHECKS) {
        if (a && $jscomp.ES6_CONFORMANCE)
            return a
    } else if (b())
        return a;
    $jscomp.initSymbolIterator();
    var c = function(d) {
        this.map_ = new Map;
        if (d) {
            d = $jscomp.makeIterator(d);
            for (var e; !(e = d.next()).done; )
                e = e.value,
                    this.add(e)
        }
        this.size = this.map_.size
    };
    c.prototype.add = function(d) {
        d = 0 === d ? 0 : d;
        this.map_.set(d, d);
        this.size = this.map_.size;
        return this
    }
    ;
    c.prototype["delete"] = function(d) {
        d = this.map_["delete"](d);
        this.size = this.map_.size;
        return d
    }
    ;
    c.prototype.clear = function() {
        this.map_.clear();
        this.size = 0
    }
    ;
    c.prototype.has = function(d) {
        return this.map_.has(d)
    }
    ;
    c.prototype.entries = function() {
        return this.map_.entries()
    }
    ;
    c.prototype.values = function() {
        return this.map_.values()
    }
    ;
    c.prototype.keys = c.prototype.values;
    c.prototype[Symbol.iterator] = c.prototype.values;
    c.prototype.forEach = function(d, e) {
        var f = this;
        this.map_.forEach(function(g) {
            return d.call(e, g, g, f)
        })
    }
    ;
    return c
}, "es6", "es3");
(function() {
        function a() {
            (document.body || document.documentElement).appendChild(t);
            var n = t.offsetHeight;
            t.open = !0;
            var p = t.offsetHeight;
            t.parentNode.removeChild(t);
            return n != p
        }
        function b() {
            var n = document.createElement("details").constructor.prototype
                , p = n.setAttribute
                , u = n.removeAttribute
                , x = Object.getOwnPropertyDescriptor(n, "open");
            Object.defineProperties(n, {
                open: {
                    get: function() {
                        if ("DETAILS" == this.tagName)
                            return this.hasAttribute("open");
                        if (x && x.get)
                            return x.get.call(this)
                    },
                    set: function(y) {
                        if ("DETAILS" == this.tagName)
                            return y ? this.setAttribute("open", "") : this.removeAttribute("open");
                        if (x && x.set)
                            return x.set.call(this, y)
                    }
                },
                setAttribute: {
                    value: function(y, A) {
                        var z = this
                            , B = function() {
                            p.call(z, y, A)
                        };
                        return "DETAILS" == this.tagName ? l(this, B) : B()
                    }
                },
                removeAttribute: {
                    value: function(y) {
                        var A = this
                            , z = function() {
                            u.call(A, y)
                        };
                        return "DETAILS" == this.tagName ? l(this, z) : z()
                    }
                }
            })
        }
        function c() {
            m(function(n) {
                n.hasAttribute("open") ? (n.removeAttribute("open"),
                    n.setAttribute("aria-expanded", !1)) : (n.setAttribute("open", ""),
                    n.setAttribute("aria-expanded", !0))
            })
        }
        function d() {
            window.MutationObserver ? (new MutationObserver(function(n) {
                    v.call(n, function(p) {
                        var u = p.target;
                        p = p.attributeName;
                        "DETAILS" == u.tagName && "open" == p && h(u)
                    })
                }
            )).observe(document.documentElement, {
                attributes: !0,
                subtree: !0
            }) : m(function(n) {
                var p = n.getAttribute("open");
                setTimeout(function() {
                    var u = n.getAttribute("open");
                    p != u && h(n)
                }, 1)
            })
        }
        function e() {
            f(document);
            window.MutationObserver ? (new MutationObserver(function(n) {
                    v.call(n, function(p) {
                        v.call(p.addedNodes, f)
                    })
                }
            )).observe(document.documentElement, {
                subtree: !0,
                childList: !0
            }) : document.addEventListener("DOMNodeInserted", function(n) {
                f(n.target)
            })
        }
        function f(n) {
            k(n, "SUMMARY").forEach(function(p) {
                var u = q(p, "DETAILS");
                p.setAttribute("aria-expanded", u.hasAttribute("open"));
                p.hasAttribute("tabindex") || p.setAttribute("tabindex", "0");
                p.hasAttribute("role") || p.setAttribute("role", "button")
            })
        }
        function g(n) {
            return !(n.defaultPrevented || n.ctrlKey || n.metaKey || n.shiftKey || n.target.isContentEditable)
        }
        function m(n) {
            window.addEventListener("click", function(p) {
                g(p) && 1 >= p.which && (p = q(p.target, "SUMMARY")) && p.parentNode && "DETAILS" == p.parentNode.tagName && n(p.parentNode)
            }, !1);
            window.addEventListener("keydown", function(p) {
                if (g(p) && (13 == p.keyCode || 32 == p.keyCode)) {
                    var u = q(p.target, "SUMMARY");
                    u && u.parentNode && "DETAILS" == u.parentNode.tagName && (n(u.parentNode),
                        p.preventDefault())
                }
            }, !1)
        }
        function h(n) {
            var p = document.createEvent("Event");
            p.initEvent("toggle", !0, !1);
            n.dispatchEvent(p)
        }
        function l(n, p) {
            var u = n.getAttribute("open");
            p = p();
            var x = n.getAttribute("open");
            u != x && h(n);
            return p
        }
        function k(n, p) {
            return (n.tagName == p ? [n] : []).concat("function" == typeof n.getElementsByTagName ? C.call(n.getElementsByTagName(p)) : [])
        }
        function q(n, p) {
            if ("function" == typeof n.closest)
                return n.closest(p);
            for (; n; ) {
                if (n.tagName == p)
                    return n;
                n = n.parentNode
            }
        }
        var t = document.createElement("details");
        t.innerHTML = "<summary>a</summary>b";
        t.setAttribute("style", "position: absolute; left: -9999px");
        var w = {
            open: "open"in t && a(),
            toggle: "ontoggle"in t
        }
            , r = []
            , v = r.forEach
            , C = r.slice;
        w.open || (document.head.insertAdjacentHTML("afterbegin", '<style>\ndetails, summary {\n  display: block;\n}\ndetails:not([open]) > *:not(summary) {\n  display: none;\n}\ndetails > summary::before {\n  content: "\u25ba";\n  padding-right: 0.3rem;\n  font-size: 0.6rem;\n  cursor: default;\n}\ndetails[open] > summary::before {\n  content: "\u25bc";\n}\n</style>'),
            b(),
            c(),
            e());
        w.open && !w.toggle && d()
    }
)();

Util = function() {};
Util.prepareReportResult = function(a) {
    var b = JSON.parse(JSON.stringify(a));
    b.configSettings.locale || (b.configSettings.locale = "en");
    Util.setNumberDateLocale(b.configSettings.locale);
    b.i18n && b.i18n.rendererFormattedStrings && Util.updateAllUIStrings(b.i18n.rendererFormattedStrings);
    if ("object" !== typeof b.categories)
        throw Error("No categories provided.");
    b.reportCategories = Object.values(b.categories);
    a = $jscomp.makeIterator(Object.values(b.audits));
    for (var c = a.next(); !c.done; c = a.next())
        if (c = c.value,
        "not_applicable" === c.scoreDisplayMode || "not-applicable" === c.scoreDisplayMode)
            c.scoreDisplayMode = "notApplicable";
    a = $jscomp.makeIterator(b.reportCategories);
    for (c = a.next(); !c.done; c = a.next())
        c = c.value,
            c.auditRefs.forEach(function(d) {
                var e = b.audits[d.id];
                d.result = e
            });
    return b
}
;
Util.updateAllUIStrings = function(a) {
    a = $jscomp.makeIterator(Object.entries(a));
    for (var b = a.next(); !b.done; b = a.next()) {
        b = b.value;
        var c = $jscomp.makeIterator(b);
        b = c.next().value;
        c = c.next().value;
        Util.UIStrings[b] = c
    }
}
;
Util.formatDisplayValue = function(a) {
    if ("string" === typeof a)
        return a;
    if (!a)
        return "";
    var b = /%([0-9]*(\.[0-9]+)?d|s)/
        , c = a[0];
    if ("string" !== typeof c)
        return "UNKNOWN";
    var d = {};
    a = $jscomp.makeIterator(a.slice(1));
    for (var e = a.next(); !e.done; d = {
        $jscomp$loop$prop$replacement$46: d.$jscomp$loop$prop$replacement$46
    },
        e = a.next()) {
        d.$jscomp$loop$prop$replacement$46 = e.value;
        if (!b.test(c)) {
            console.warn("Too many replacements given");
            break
        }
        c = c.replace(b, function(f) {
            return function(g) {
                var m = Number(g.match(/[0-9.]+/)) || 1;
                return "%s" === g ? f.$jscomp$loop$prop$replacement$46.toLocaleString() : (Math.round(Number(f.$jscomp$loop$prop$replacement$46) / m) * m).toLocaleString()
            }
        }(d))
    }
    b.test(c) && console.warn("Not enough replacements given");
    return c
}
;
Util.showAsPassed = function(a) {
    switch (a.scoreDisplayMode) {
        case "manual":
        case "notApplicable":
            return !0;
        case "error":
        case "informative":
            return !1;
        default:
            return Number(a.score) >= RATINGS.PASS.minScore
    }
}
;
Util.calculateRating = function(a, b) {
    if ("manual" === b || "notApplicable" === b)
        return RATINGS.PASS.label;
    if ("error" === b)
        return RATINGS.ERROR.label;
    if (null === a)
        return RATINGS.FAIL.label;
    b = RATINGS.FAIL.label;
    a >= RATINGS.PASS.minScore ? b = RATINGS.PASS.label : a >= RATINGS.AVERAGE.minScore && (b = RATINGS.AVERAGE.label);
    return b
}
;
Util.formatNumber = function(a, b) {
    b = void 0 === b ? .1 : b;
    a = Math.round(a / b) * b;
    return a.toLocaleString(Util.numberDateLocale)
}
;
Util.formatBytesToKB = function(a, b) {
    b = void 0 === b ? .1 : b;
    a = (Math.round(a / 1024 / b) * b).toLocaleString(Util.numberDateLocale);
    return "" + a + "\u00a0KB"
}
;
Util.formatMilliseconds = function(a, b) {
    b = void 0 === b ? 10 : b;
    a = Math.round(a / b) * b;
    return "" + a.toLocaleString(Util.numberDateLocale) + "\u00a0ms"
}
;
Util.formatSeconds = function(a, b) {
    b = void 0 === b ? .1 : b;
    a = Math.round(a / 1E3 / b) * b;
    return "" + a.toLocaleString(Util.numberDateLocale) + "\u00a0s"
}
;
Util.formatDateTime = function(a) {
    var b = {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "numeric",
        minute: "numeric",
        timeZoneName: "short"
    }
        , c = new Intl.DateTimeFormat(Util.numberDateLocale,b)
        , d = c.resolvedOptions().timeZone;
    d && "etc/unknown" !== d.toLowerCase() || (b.timeZone = "UTC",
        c = new Intl.DateTimeFormat(Util.numberDateLocale,b));
    return c.format(new Date(a))
}
;
Util.formatDuration = function(a) {
    var b = a / 1E3;
    if (0 === Math.round(b))
        return "None";
    var c = []
        , d = {
        d: 86400,
        h: 3600,
        m: 60,
        s: 1
    };
    Object.keys(d).forEach(function(e) {
        var f = d[e]
            , g = Math.floor(b / f);
        0 < g && (b -= g * f,
            c.push(g + "\u00a0" + e))
    });
    return c.join(" ")
}
;
Util.getURLDisplayName = function(a, b) {
    b = b || {
        numPathParts: void 0,
        preserveQuery: void 0,
        preserveHost: void 0
    };
    var c = void 0 !== b.numPathParts ? b.numPathParts : 2
        , d = void 0 !== b.preserveQuery ? b.preserveQuery : !0
        , e = b.preserveHost || !1;
    if ("about:" === a.protocol || "data:" === a.protocol)
        b = a.href;
    else {
        b = a.pathname;
        var f = b.split("/").filter(function(g) {
            return g.length
        });
        c && f.length > c && (b = "\u2026" + f.slice(-1 * c).join("/"));
        e && (b = a.host + "/" + b.replace(/^\//, ""));
        d && (b = "" + b + a.search)
    }
    b = b.replace(/([a-f0-9]{7})[a-f0-9]{13}[a-f0-9]*/g, "$1\u2026");
    b = b.replace(/([a-zA-Z0-9-_]{9})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9-_]{10,}/g, "$1\u2026");
    b = b.replace(/(\d{3})\d{6,}/g, "$1\u2026");
    b = b.replace(/\u2026+/g, "\u2026");
    64 < b.length && b.includes("?") && (b = b.replace(/\?([^=]*)(=)?.*/, "?$1$2\u2026"),
    64 < b.length && (b = b.replace(/\?.*/, "?\u2026")));
    64 < b.length && (a = b.lastIndexOf("."),
        b = 0 <= a ? b.slice(0, 63 - (b.length - a)) + ("\u2026" + b.slice(a)) : b.slice(0, 63) + "\u2026");
    return b
}
;
Util.parseURL = function(a) {
    a = new URL(a);
    return {
        file: Util.getURLDisplayName(a),
        hostname: a.hostname,
        origin: a.origin
    }
}
;
Util.getEnvironmentDisplayValues = function(a) {
    a = Util.getEmulationDescriptions(a);
    return [{
        name: "Device",
        description: a.deviceEmulation
    }, {
        name: "Network throttling",
        description: a.networkThrottling
    }, {
        name: "CPU throttling",
        description: a.cpuThrottling
    }]
}
;
Util.getEmulationDescriptions = function(a) {
    var b;
    var c = a.throttling;
    switch (a.throttlingMethod) {
        case "provided":
            c = b = "Provided by environment";
            var d = "No throttling applied";
            break;
        case "devtools":
            d = c;
            b = d.cpuSlowdownMultiplier;
            d = d.requestLatencyMs;
            b = Util.formatNumber(b) + "x slowdown (DevTools)";
            c = "" + Util.formatNumber(d) + "\u00a0ms HTTP RTT, " + ("" + Util.formatNumber(c.downloadThroughputKbps) + "\u00a0Kbps down, ") + ("" + Util.formatNumber(c.uploadThroughputKbps) + "\u00a0Kbps up (DevTools)");
            d = "Throttled Slow 4G network";
            break;
        case "simulate":
            d = c;
            b = d.cpuSlowdownMultiplier;
            c = d.rttMs;
            d = d.throughputKbps;
            b = Util.formatNumber(b) + "x slowdown (Simulated)";
            c = "" + Util.formatNumber(c) + "\u00a0ms TCP RTT, " + ("" + Util.formatNumber(d) + "\u00a0Kbps throughput (Simulated)");
            d = "Simulated Slow 4G network";
            break;
        default:
            d = c = b = "Unknown"
    }
    var e = "No emulation";
    a.disableDeviceEmulation || ("mobile" === a.emulatedFormFactor && (e = "Emulated Nexus 5X"),
    "desktop" === a.emulatedFormFactor && (e = "Emulated Desktop"));
    return {
        deviceEmulation: e,
        cpuThrottling: b,
        networkThrottling: c,
        summary: e + ", " + d
    }
}
;
Util.setNumberDateLocale = function(a) {
    Util.numberDateLocale = a;
    "en-XA" === Util.numberDateLocale && (Util.numberDateLocale = "de")
}
;
Util.numberDateLocale = "en";
Util.UIStrings = {
    varianceDisclaimer: "Values are estimated and may vary.",
    opportunityResourceColumnLabel: "Opportunity",
    opportunitySavingsColumnLabel: "Estimated Savings",
    errorMissingAuditInfo: "Report error: no audit information",
    errorLabel: "Error!",
    warningHeader: "Warnings: ",
    auditGroupExpandTooltip: "Show audits",
    warningAuditsGroupTitle: "Passed audits but with warnings",
    passedAuditsGroupTitle: "Passed audits",
    notApplicableAuditsGroupTitle: "Not applicable",
    manualAuditsGroupTitle: "Additional items to manually check",
    toplevelWarningsMessage: "There were issues affecting this run of Lighthouse:",
    scorescaleLabel: "Score scale:",
    crcInitialNavigation: "Initial Navigation",
    crcLongestDurationLabel: "Maximum critical path latency:",
    lsPerformanceCategoryDescription: "[Lighthouse](https://developers.google.com/web/tools/lighthouse/) analysis of the current page on an emulated mobile network. Values are estimated and may vary.",
    labDataTitle: "Lab Data"
};

"undefined" !== typeof module && module.exports ? module.exports = Util : self.Util = Util;
var DOM = function(a) {
    this._document = a
};

DOM.prototype.createElement = function(a, b, c) {
    c = void 0 === c ? {} : c;
    var d = this._document.createElement(a);
    b && (d.className = b);
    Object.keys(c).forEach(function(e) {
        var f = c[e];
        "undefined" !== typeof f && d.setAttribute(e, f)
    });
    return d
}
;
DOM.prototype.createFragment = function() {
    return this._document.createDocumentFragment()
}
;
DOM.prototype.createChildOf = function(a, b, c, d) {
    b = this.createElement(b, c, d);
    a.appendChild(b);
    return b
}
;
DOM.prototype.cloneTemplate = function(a, b) {
    b = b.querySelector(a);
    if (!b)
        throw Error("Template not found: template" + a);
    a = this._document.importNode(b.content, !0);
    b.hasAttribute("data-stamped") && this.findAll("style", a).forEach(function(c) {
        return c.remove()
    });
    b.setAttribute("data-stamped", "true");
    return a
}
;
DOM.prototype.resetTemplates = function() {
    this.findAll("template[data-stamped]", this._document).forEach(function(a) {
        a.removeAttribute("data-stamped")
    })
}
;
DOM.prototype.convertMarkdownLinkSnippets = function(a) {
    var b = this.createElement("span");
    for (a = a.split(/\[([^\]]*?)\]\((https?:\/\/.*?)\)/g); a.length; ) {
        var c = $jscomp.makeIterator(a.splice(0, 3))
            , d = c.next().value
            , e = c.next().value;
        c = c.next().value;
        b.appendChild(this._document.createTextNode(d));
        e && c && (d = this.createElement("a"),
            d.rel = "noopener",
            d.target = "_blank",
            d.textContent = e,
            d.href = (new URL(c)).href,
            b.appendChild(d))
    }
    return b
}
;
DOM.prototype.convertMarkdownCodeSnippets = function(a) {
    var b = this.createElement("span");
    for (a = a.split(/`(.*?)`/g); a.length; ) {
        var c = $jscomp.makeIterator(a.splice(0, 2))
            , d = c.next().value;
        c = c.next().value;
        b.appendChild(this._document.createTextNode(d));
        c && (d = this.createElement("code"),
            d.textContent = c,
            b.appendChild(d))
    }
    return b
}
;
DOM.prototype.document = function() {
    return this._document
}
;
DOM.prototype.isDevTools = function() {
    return !!this._document.querySelector(".lh-devtools")
}
;
DOM.prototype.find = function(a, b) {
    b = b.querySelector(a);
    if (null === b)
        throw Error("query " + a + " not found");
    return b
}
;
DOM.prototype.findAll = function(a, b) {
    return Array.from(b.querySelectorAll(a))
}
;

"undefined" !== typeof module && module.exports ? module.exports = DOM : self.DOM = DOM;
var URL_PREFIXES = ["http://", "https://", "data:"]
    , DetailsRenderer = function(a) {
    this._dom = a
};
DetailsRenderer.prototype.setTemplateContext = function(a) {
    this._templateContext = a
}
;


DetailsRenderer.prototype.render = function(a) {
    return CriticalRequestChainRenderer.render(this._dom, document, a);
}
;

"undefined" !== typeof module && module.exports ? module.exports = DetailsRenderer : self.DetailsRenderer = DetailsRenderer;
var CriticalRequestChainRenderer = function() {};
CriticalRequestChainRenderer.initTree = function(a) {
    var b = 0
        , c = Object.keys(a);
    0 < c.length && (b = a[c[0]],
        b = b.request.startTime);
    return {
        tree: a,
        startTime: b,
        transferSize: 0
    }
}
;
CriticalRequestChainRenderer.createSegment = function(a, b, c, d, e, f) {
    var g = a[b];
    a = Object.keys(a);
    b = a.indexOf(b) === a.length - 1;
    a = !!g.children && 0 < Object.keys(g.children).length;
    e = Array.isArray(e) ? e.slice(0) : [];
    "undefined" !== typeof f && e.push(!f);
    return {
        node: g,
        isLastChild: b,
        hasChildren: a,
        startTime: c,
        transferSize: d + g.request.transferSize,
        treeMarkers: e
    }
}
;
CriticalRequestChainRenderer.createChainNode = function(a, b, c) {
    b = a.cloneTemplate("#tmpl-lh-crc__chains", b);
    a.find(".crc-node", b).setAttribute("title", c.node.request.url);
    var d = a.find(".crc-node__tree-marker", b);
    c.treeMarkers.forEach(function(h) {
        h ? d.appendChild(a.createElement("span", "tree-marker vert")) : d.appendChild(a.createElement("span", "tree-marker"));
        d.appendChild(a.createElement("span", "tree-marker"))
    });
    c.isLastChild ? d.appendChild(a.createElement("span", "tree-marker up-right")) : d.appendChild(a.createElement("span", "tree-marker vert-right"));
    d.appendChild(a.createElement("span", "tree-marker right"));
    c.hasChildren ? d.appendChild(a.createElement("span", "tree-marker horiz-down")) : d.appendChild(a.createElement("span", "tree-marker right"));
    var e = Util.parseURL(c.node.request.url)
        , f = e.file
        , g = e.hostname;
    e = a.find(".crc-node__tree-value", b);
    a.find(".crc-node__tree-file", e).textContent = "" + f;
    a.find(".crc-node__tree-hostname", e).textContent = g ? "(" + g + ")" : "";
    if (!c.hasChildren) {
        g = c.node.request;
        c = g.startTime;
        f = g.endTime;
        g = g.transferSize;
        var m = a.createElement("span", "crc-node__chain-duration");
        m.textContent = " - " + Util.formatMilliseconds(1E3 * (f - c)) + ", ";
        c = a.createElement("span", "crc-node__chain-duration");
        c.textContent = Util.formatBytesToKB(g, .01);
        e.appendChild(m);
        e.appendChild(c)
    }
    return b
}
;
CriticalRequestChainRenderer.buildTree = function(a, b, c, d, e) {
    d.appendChild(CriticalRequestChainRenderer.createChainNode(a, b, c));
    if (c.node.children)
        for (var f = $jscomp.makeIterator(Object.keys(c.node.children)), g = f.next(); !g.done; g = f.next())
            g = g.value,
                g = CriticalRequestChainRenderer.createSegment(c.node.children, g, c.startTime, c.transferSize, c.treeMarkers, c.isLastChild),
                CriticalRequestChainRenderer.buildTree(a, b, g, d, e)
}
;
CriticalRequestChainRenderer.render = function(a, b, c) {
    b = a.cloneTemplate("#tmpl-lh-crc", b);
    var d = a.find(".lh-crc", b);
    a.find(".crc-initial-nav", b).textContent = Util.UIStrings.crcInitialNavigation;
    a.find(".lh-crc__longest_duration_label", b).textContent = Util.UIStrings.crcLongestDurationLabel;
    a.find(".lh-crc__longest_duration", b).textContent = Util.formatMilliseconds(c.longestChain.duration);
    for (var e = CriticalRequestChainRenderer.initTree(c.chains), f = $jscomp.makeIterator(Object.keys(e.tree)), g = f.next(); !g.done; g = f.next())
        g = g.value,
            g = CriticalRequestChainRenderer.createSegment(e.tree, g, e.startTime, e.transferSize),
            CriticalRequestChainRenderer.buildTree(a, b, g, d, c);
    return a.find(".lh-crc-container", b)
}
;




/* legacy */


function renderTreeMarker(hasChildren) {
    if (globalData.depth == 0) {
        if(hasChildren) {
            return '<span class="tree-marker vert-right"></span><span class="tree-marker right"></span><span class="tree-marker right"></span>';
        } else {
            return '<span class="tree-marker up-right"></span><span class="tree-marker right"></span><span class="tree-marker horiz-down"></span>';
        }
    } else if (globalData.depth == 1){
        return '<span class="tree-marker"></span><span class="tree-marker"></span><span class="tree-marker vert-right"></span><span class="tree-marker right"></span><span class="tree-marker right"></span>';
    } else if (globalData.depth == 2) {
        return '<span class="tree-marker vert"></span><span class="tree-marker"></span><span class="tree-marker vert-right"></span><span class="tree-marker right"></span><span class="tree-marker right"></span>';
    }
}

function renderTreeValue(request) {
    var url = new URL(request.url);
    var parsedURL = getParsedPath(url);
    if (request.transferSize == null) {
        return '<span class="crc-node__tree-value"><span class="crc-node__tree-file">' + parsedURL + '</span><span class="crc-node__tree-hostname">(' + url.hostname + ')</span></span>';
    } else {
        return '<span class="crc-node__tree-value"><span class="crc-node__tree-file">' + parsedURL + '</span>\n' +
            ((url.hostname) ? '<span class="crc-node__tree-hostname">(' + url.hostname + ')</span>\n' : '') +
            '<span class="crc-node__chain-duration"> - ' + timeMiliSecondFormatter(request.endTime - request.startTime) + ', </span>\n' +
            '<span class="crc-node__chain-duration">' + bytesFormatter(request.transferSize) + '</span></span>';
    }
}

function renderNode(data, elem) {
    elem.append('<div class="crc-node" title="' + data.request.url + '">' + renderTreeMarker(data.children != null) + renderTreeValue(data.request) + '</div>');
    if (data.children) {
        globalData.depth++;
        $.each(data.children, function(i, item) {
            renderNode(item, elem);
        });
        globalData.depth--;
    }
}

function plotCriticalRequestChain(data, elem) {
    globalData.depth = 0;
    $.each(data, function(i, item) {
        renderNode(item, $(elem));
    });
}

function getParsedPath(parsedUrl) {
    var ELLIPSIS = '\u2026';
    var numPathParts = 2;
    var name;
    if (parsedUrl.protocol === 'about:' || parsedUrl.protocol === 'data:') {
        name = parsedUrl.href;
    } else {
        name = parsedUrl.pathname;
        var parts = name.split('/').filter(function(g) {
            return g.length;
        });
        if (numPathParts && parts.length > numPathParts) {
            name = ELLIPSIS + parts.slice(-1 * numPathParts).join('/');
        }

        name = "" + name + parsedUrl.search;
    }

    name = name.replace(/([a-f0-9]{7})[a-f0-9]{13}[a-f0-9]*/g, "$1\u2026");
    name = name.replace(/([a-zA-Z0-9-_]{9})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9-_]{10,}/g, "$1\u2026");
    name = name.replace(/(\d{3})\d{6,}/g, "$1\u2026");
    name = name.replace(/\u2026+/g, "\u2026");
    64 < name.length && name.includes("?") && (name = name.replace(/\?([^=]*)(=)?.*/, "?$1$2\u2026"),
    64 < name.length && (name = name.replace(/\?.*/, "?\u2026")));
    64 < name.length && (parsedUrl = name.lastIndexOf("."),
        name = 0 <= parsedUrl ? name.slice(0, 63 - (name.length - parsedUrl)) + ("\u2026" + name.slice(parsedUrl)) : name.slice(0, 63) + "\u2026");
    return name;
}