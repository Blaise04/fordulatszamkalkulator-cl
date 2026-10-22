/* Fordulatszámkalkulátor CL – webes app (PWA) felület. */
(function () {
  'use strict';
  var C = window.Calc;
  var STORE_KEY = 'fkcl-state-v1';
  var THEME_KEY = 'fkcl-theme';
  var INSTALL_KEY = 'fkcl-install-dismissed';

  var defaults = {
    rpm: { tool: 'vago', d: '125', v: '80' },
    spd: { tool: 'vago', d: '125', n: '11000' },
    dia: { tool: 'vago', n: '11000', v: '80' }
  };

  function load(key) { try { return localStorage.getItem(key); } catch (e) { return null; } }
  function save(key, val) { try { localStorage.setItem(key, val); } catch (e) { /* privát mód */ } }

  var state = (function () {
    var s = JSON.parse(JSON.stringify(defaults));
    try {
      var saved = JSON.parse(load(STORE_KEY) || 'null');
      if (saved) ['rpm', 'spd', 'dia'].forEach(function (k) { if (saved[k]) Object.assign(s[k], saved[k]); });
    } catch (e) { /* sérült mentés: alapértékek */ }
    return s;
  })();

  function persist() { save(STORE_KEY, JSON.stringify(state)); }

  function $(sel, root) { return (root || document).querySelector(sel); }
  function $all(sel, root) { return Array.prototype.slice.call((root || document).querySelectorAll(sel)); }
  function out(key, text) { $all('[data-out="' + key + '"]').forEach(function (el) { el.textContent = text; }); }

  // ---------- Szerszámtípus és átmérő gombok ----------
  function buildChips() {
    $all('[data-tools]').forEach(function (box) {
      var screen = box.getAttribute('data-tools');
      C.TOOLS.forEach(function (t) {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'chip';
        b.textContent = t.name;
        b.setAttribute('data-tool', t.id);
        b.addEventListener('click', function () {
          state[screen].tool = t.id;
          // Fordulatszám és átmérő: a sebesség a típus maximumára áll.
          if (screen === 'rpm' || screen === 'dia') {
            state[screen].v = String(t.max);
            $('#' + screen + '-v').value = state[screen].v;
          }
          persist();
          render(screen);
        });
        box.appendChild(b);
      });
    });
    $all('[data-sizes]').forEach(function (box) {
      var screen = box.getAttribute('data-sizes');
      C.QUICK_DIAMETERS.forEach(function (d) {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'chip';
        b.textContent = String(d);
        b.setAttribute('aria-label', d + ' mm');
        b.setAttribute('data-size', String(d));
        b.addEventListener('click', function () {
          state[screen].d = String(d);
          $('#' + screen + '-d').value = state[screen].d;
          persist();
          render(screen);
        });
        box.appendChild(b);
      });
    });
  }

  function markChips(screen, toolId, d) {
    $all('[data-tools="' + screen + '"] .chip').forEach(function (b) {
      b.setAttribute('aria-pressed', String(b.getAttribute('data-tool') === toolId));
    });
    $all('[data-sizes="' + screen + '"] .chip').forEach(function (b) {
      b.setAttribute('aria-pressed', String(d != null && Number(b.getAttribute('data-size')) === d));
    });
  }

  function setShop(screen, t, d) {
    var a = $('[data-shop="' + screen + '"]');
    a.href = C.shopUrl(t, d);
    out(screen + '.shop', C.shopLabel(t, d));
  }

  function setWarn(screen, on, text) {
    var w = $('[data-warn="' + screen + '"]');
    w.hidden = !on;
    if (on) out(screen + '.warn', text);
  }

  // ---------- Számítás és megjelenítés ----------
  function render(screen) {
    var s = state[screen];
    var t = C.tool(s.tool);
    if (screen === 'rpm') {
      var d = C.parse(s.d), v = C.parse(s.v);
      var warn = v != null && v > t.max;
      markChips('rpm', t.id, d);
      out('rpm.max', 'max. ' + t.max + ' m/s');
      out('rpm.result', d != null && v != null ? C.format(C.rpm(d, v), 0) : '–');
      $('#rpm-v').classList.toggle('warn', warn);
      setWarn('rpm', warn, 'A ' + t.lower + ' megengedett maximuma ' + t.max + ' m/s. Válassz kisebb értéket, különben balesetveszélyes!');
      setShop('rpm', t, d != null ? Math.round(d) : null);
    } else if (screen === 'spd') {
      var d2 = C.parse(s.d), n2 = C.parse(s.n);
      var sp = d2 != null && n2 != null ? C.speed(d2, n2) : null;
      var warn2 = sp != null && sp > t.max;
      markChips('spd', t.id, d2);
      out('spd.max', 'max. ' + t.max + ' m/s');
      out('spd.result', sp != null ? C.format(sp, 1) : '–');
      $('[data-result="spd"]').classList.toggle('danger', warn2);
      var limit = d2 != null ? C.formatFloor(C.rpm(d2, t.max)) : '–';
      setWarn('spd', warn2, (sp != null ? C.format(sp, 1) : '') + ' m/s meghaladja a ' + t.lower + ' ' + t.max +
        ' m/s-os maximumát. Ennél az átmérőnél legfeljebb ' + limit + ' 1/min engedélyezett.');
      setShop('spd', t, d2 != null ? Math.round(d2) : null);
    } else if (screen === 'dia') {
      var n3 = C.parse(s.n), v3 = C.parse(s.v);
      var dd = n3 != null && v3 != null ? C.diameter(n3, v3) : null;
      var std = dd != null ? C.standardAtMost(dd) : null;
      var warn3 = v3 != null && v3 > t.max;
      markChips('dia', t.id, null);
      out('dia.max', 'max. ' + t.max + ' m/s');
      out('dia.result', dd != null ? C.format(dd, 1) : '–');
      out('dia.std', std != null ? std + ' mm' : '–');
      $('#dia-v').classList.toggle('warn', warn3);
      setWarn('dia', warn3, 'A ' + t.lower + ' megengedett maximuma ' + t.max + ' m/s. Válassz kisebb értéket, különben balesetveszélyes!');
      setShop('dia', t, std);
    }
  }

  function bindInputs() {
    $all('input[data-key]').forEach(function (input) {
      var parts = input.getAttribute('data-key').split('.');
      input.value = state[parts[0]][parts[1]];
      input.addEventListener('input', function () {
        var cleaned = input.value.replace(/[^\d.,]/g, '').slice(0, 9);
        if (cleaned !== input.value) input.value = cleaned;
        state[parts[0]][parts[1]] = cleaned;
        persist();
        render(parts[0]);
      });
      input.addEventListener('keydown', function (e) { if (e.key === 'Enter') input.blur(); });
    });
  }

  function buildTable() {
    var body = $('#table-body');
    C.TOOLS.forEach(function (t) {
      var tr = document.createElement('tr');
      [t.name, String(t.max), C.formatFloor(C.rpm(125, t.max)), C.formatFloor(C.rpm(230, t.max))].forEach(function (txt, i) {
        var td = document.createElement(i === 0 ? 'th' : 'td');
        if (i === 0) td.setAttribute('scope', 'row');
        if (i === 1) td.className = 'max';
        td.textContent = txt;
        tr.appendChild(td);
      });
      body.appendChild(tr);
    });
  }

  // ---------- Navigáció (hash alapú, a vissza gomb is működik) ----------
  var SCREENS = ['fordulatszam', 'kerulet', 'atmero', 'ajanlott'];
  var lastCalc = 'fordulatszam';

  function route() {
    var id = (location.hash || '').replace('#', '');
    if (SCREENS.indexOf(id) < 0) id = 'fordulatszam';
    if (id !== 'ajanlott') lastCalc = id;
    SCREENS.forEach(function (s) { document.getElementById(s).hidden = s !== id; });
    $all('.nav a').forEach(function (a) {
      if (a.getAttribute('data-tab') === id) a.setAttribute('aria-current', 'page');
      else a.removeAttribute('aria-current');
    });
    $('#back-link').setAttribute('href', '#' + lastCalc);
    $('#main').scrollTop = 0;
  }

  // ---------- Sötét mód ----------
  var media = window.matchMedia ? window.matchMedia('(prefers-color-scheme: dark)') : null;

  function currentDark() {
    var saved = load(THEME_KEY);
    if (saved === 'dark') return true;
    if (saved === 'light') return false;
    return !!(media && media.matches);
  }

  function applyTheme() {
    var dark = currentDark();
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
    $('#theme-icon').setAttribute('href', dark ? '#i-sun' : '#i-moon');
    $('#theme-btn').setAttribute('aria-label', dark ? 'Világos mód' : 'Sötét mód');
    $all('meta[name="theme-color"]').forEach(function (m) { m.setAttribute('content', dark ? '#141416' : '#FFFFFF'); });
  }

  // ---------- Telepítési tipp ----------
  var deferredPrompt = null;

  function isStandalone() {
    return (window.matchMedia && window.matchMedia('(display-mode: standalone)').matches) || window.navigator.standalone === true;
  }

  function isIos() {
    var ua = navigator.userAgent || '';
    return /iPhone|iPad|iPod/.test(ua) || (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1);
  }

  function showInstall(text, withButton) {
    if (isStandalone() || load(INSTALL_KEY) === '1') return;
    $('#install-text').textContent = text;
    $('#install-btn').hidden = !withButton;
    $('#install').hidden = false;
  }

  function setupInstall() {
    $('#install-close').addEventListener('click', function () { $('#install').hidden = true; save(INSTALL_KEY, '1'); });
    $('#install-btn').addEventListener('click', function () {
      if (!deferredPrompt) return;
      deferredPrompt.prompt();
      deferredPrompt.userChoice.then(function () { deferredPrompt = null; $('#install').hidden = true; });
    });
    window.addEventListener('beforeinstallprompt', function (e) {
      e.preventDefault();
      deferredPrompt = e;
      showInstall('Tedd ki a kalkulátort a kezdőképernyőre – internet nélkül is működik.', true);
    });
    if (isIos()) {
      showInstall('Telepítés iPhone-ra: koppints a Megosztás gombra, majd a „Főképernyőhöz adás” pontra.', false);
    }
  }

  // ---------- Indítás ----------
  applyTheme();
  $('#theme-btn').addEventListener('click', function () {
    save(THEME_KEY, currentDark() ? 'light' : 'dark');
    applyTheme();
  });
  if (media && media.addEventListener) media.addEventListener('change', applyTheme);

  buildChips();
  bindInputs();
  buildTable();
  ['rpm', 'spd', 'dia'].forEach(render);
  window.addEventListener('hashchange', route);
  route();
  setupInstall();

  if ('serviceWorker' in navigator) {
    window.addEventListener('load', function () { navigator.serviceWorker.register('sw.js').catch(function () {}); });
  }
})();
