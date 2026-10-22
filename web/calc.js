/* Fordulatszámkalkulátor CL – számítási logika (az Android app Calc.kt megfelelője). */
(function (root) {
  'use strict';

  var TOOLS = [
    { id: 'vago', name: 'Vágókorong', lower: 'vágókorong', plural: 'vágókorongok', kw: 'vágókorong', max: 80 },
    { id: 'tisz', name: 'Tisztítókorong', lower: 'tisztítókorong', plural: 'tisztítókorongok', kw: 'tisztítókorong', max: 80 },
    { id: 'lam', name: 'Lamellás tárcsa', lower: 'lamellás tárcsa', plural: 'lamellás tárcsák', kw: 'lamellás', max: 80 },
    { id: 'fib', name: 'Fíbertárcsa', lower: 'fíbertárcsa', plural: 'fíbertárcsák', kw: 'fíbertárcsa', max: 80 },
    { id: 'ker', name: 'Kerámia köszörűkorong', lower: 'kerámia köszörűkorong', plural: 'kerámia köszörűkorongok', kw: 'köszörűkorong', max: 35 },
    { id: 'drot', name: 'Drótkefe', lower: 'drótkefe', plural: 'drótkefék', kw: 'drótkefe', max: 80 }
  ];

  var SHOP_URL = 'https://www.csiszolotarcsawebshop.hu/';
  var QUICK_DIAMETERS = [115, 125, 150, 180, 230];
  var STANDARD_DIAMETERS = [50, 75, 100, 115, 125, 150, 180, 200, 230, 250, 300, 350, 400];

  function tool(id) {
    for (var i = 0; i < TOOLS.length; i++) if (TOOLS[i].id === id) return TOOLS[i];
    return TOOLS[0];
  }

  /** Bevitel számmá alakítása; vessző és szóköz megengedett, csak pozitív szám érvényes. */
  function parse(s) {
    var cleaned = String(s == null ? '' : s).replace(/[\s  ]/g, '').replace(',', '.');
    if (!/^\d*\.?\d+$|^\d+\.$/.test(cleaned)) return null;
    var x = parseFloat(cleaned);
    return isFinite(x) && x > 0 ? x : null;
  }

  function rpm(d, v) { return v * 60000 / (Math.PI * d); }
  function speed(d, n) { return Math.PI * d * n / 60000; }
  function diameter(n, v) { return v * 60000 / (Math.PI * n); }

  function standardAtMost(d) {
    var best = null;
    for (var i = 0; i < STANDARD_DIAMETERS.length; i++) if (STANDARD_DIAMETERS[i] <= d) best = STANDARD_DIAMETERS[i];
    return best;
  }

  function format(x, decimals) {
    return new Intl.NumberFormat('hu-HU', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
      useGrouping: true
    }).format(x);
  }

  function formatFloor(x) { return format(Math.floor(x), 0); }

  function shopUrl(t, d) {
    var q = (d != null ? d + ' ' : '') + t.kw;
    return SHOP_URL + 'shop_search.php?search=' + encodeURIComponent(q).replace(/%20/g, '+');
  }

  function shopLabel(t, d) {
    return d != null ? d + ' mm-es ' + t.plural : t.plural.charAt(0).toUpperCase() + t.plural.slice(1);
  }

  var api = {
    TOOLS: TOOLS, SHOP_URL: SHOP_URL, QUICK_DIAMETERS: QUICK_DIAMETERS,
    tool: tool, parse: parse, rpm: rpm, speed: speed, diameter: diameter,
    standardAtMost: standardAtMost, format: format, formatFloor: formatFloor,
    shopUrl: shopUrl, shopLabel: shopLabel
  };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.Calc = api;
})(this);
