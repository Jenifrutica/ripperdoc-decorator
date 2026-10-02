(function () {
  const SLOT_ORDER = ["OPERATING_SYSTEM", "FACE", "NERVOUS_SYSTEM", "ARMS", "SKELETON", "INTEGUMENTARY_SYSTEM"];
  const STORE_KEY = "ripperdoc.grupo12";
  const STAT_MAX = 150;

  const state = {
    name: "V",
    lifepath: "nomad",
    implants: [],
    family: "civilian",
    lifepaths: [],
    families: [],
    presets: [],
    allImplants: [],
    build: null,
    theme: "light"
  };

  const byId = function (id) {
    return document.getElementById(id);
  };

  const money = function (value) {
    return "€$" + Number(value).toLocaleString("en-US");
  };

  async function get(path) {
    const response = await fetch(path);
    if (!response.ok) {
      throw new Error("HTTP " + response.status);
    }
    return response.json();
  }

  async function init() {
    try {
      await window.Silhouette.mount(byId("patient-stage"));
      const results = await Promise.all([
        get("api/lifepaths"),
        get("api/implants"),
        get("api/families"),
        get("api/presets")
      ]);
      state.lifepaths = results[0];
      state.families = results[2];
      state.presets = results[3];
      state.allImplants = mergeImplants(results[1], results[2]);
      restore();
      applyUrlState();
      bindEvents();
      renderStatic();
      setStatus("ok", "en línea");
      await rebuild();
    } catch (error) {
      setStatus("error", "sin servidor");
      toast("No se pudo conectar con la clínica");
    }
  }

  function mergeImplants(catalog, families) {
    const byId = {};
    catalog.forEach(function (implant) {
      byId[implant.id] = implant;
    });
    families.forEach(function (family) {
      family.products.forEach(function (product) {
        if (!byId[product.id]) {
          byId[product.id] = product;
        }
      });
    });
    return Object.values(byId);
  }

  function restore() {
    let saved = null;
    try {
      saved = JSON.parse(localStorage.getItem(STORE_KEY) || "null");
    } catch (error) {
      saved = null;
    }
    if (!saved) {
      state.theme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
      return;
    }
    if (typeof saved.name === "string") {
      state.name = saved.name;
    }
    if (state.lifepaths.some(function (item) { return item.id === saved.lifepath; })) {
      state.lifepath = saved.lifepath;
    }
    if (Array.isArray(saved.implants)) {
      state.implants = saved.implants.filter(function (id) {
        return state.allImplants.some(function (implant) { return implant.id === id; });
      });
    }
    if (state.families.some(function (item) { return item.id === saved.family; })) {
      state.family = saved.family;
    }
    if (saved.theme === "dark" || saved.theme === "light") {
      state.theme = saved.theme;
    }
  }

  function applyUrlState() {
    const params = new URLSearchParams(window.location.search);
    const name = params.get("name");
    if (name) {
      state.name = name;
    }
    const lifepath = params.get("lifepath");
    if (lifepath && state.lifepaths.some(function (item) { return item.id === lifepath; })) {
      state.lifepath = lifepath;
    }
    const family = params.get("family");
    if (family && state.families.some(function (item) { return item.id === family; })) {
      state.family = family;
    }
    const preset = params.get("preset");
    const presetData = state.presets.find(function (item) { return item.id === preset; });
    if (presetData) {
      state.lifepath = presetData.lifepathId;
      state.implants = presetData.implantIds.slice();
    }
    const implants = params.get("implants");
    if (implants) {
      state.implants = implants.split(",").filter(function (id) {
        return state.allImplants.some(function (implant) { return implant.id === id; });
      });
    }
  }

  function persist() {
    localStorage.setItem(STORE_KEY, JSON.stringify({
      name: state.name,
      lifepath: state.lifepath,
      implants: state.implants,
      family: state.family,
      theme: state.theme
    }));
  }

  function bindEvents() {
    const input = byId("patient-name");
    input.value = state.name;
    let timer = null;
    input.addEventListener("input", function () {
      state.name = input.value;
      clearTimeout(timer);
      timer = setTimeout(function () {
        rebuild();
      }, 320);
    });

    byId("theme-toggle").addEventListener("click", function () {
      state.theme = state.theme === "dark" ? "light" : "dark";
      applyTheme();
      persist();
    });

    byId("install-kit").addEventListener("click", installKit);
  }

  function applyTheme() {
    document.documentElement.setAttribute("data-theme", state.theme);
    byId("theme-icon").textContent = state.theme === "dark" ? "◑" : "◐";
  }

  function renderStatic() {
    applyTheme();
    renderLifepaths();
    renderFamilies();
    renderPresets();
    renderCatalog();
  }

  function renderLifepaths() {
    const container = byId("lifepath-tabs");
    container.innerHTML = "";
    state.lifepaths.forEach(function (lifepath) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "lifepath";
      button.dataset.code = lifepath.id;
      button.setAttribute("role", "radio");
      button.setAttribute("aria-checked", String(lifepath.id === state.lifepath));
      const name = document.createElement("span");
      name.className = "lifepath__name";
      name.textContent = lifepath.name;
      const meta = document.createElement("span");
      meta.className = "lifepath__meta";
      meta.textContent = "STR " + lifepath.stats.strength + " · REF " + lifepath.stats.reflexes + " · HACK " + lifepath.stats.hacking;
      button.append(name, meta);
      button.addEventListener("click", function () {
        state.lifepath = lifepath.id;
        syncChecked(container, state.lifepath);
        rebuild();
      });
      container.appendChild(button);
    });
  }

  function renderFamilies() {
    const container = byId("family-tabs");
    container.innerHTML = "";
    state.families.forEach(function (family) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "family";
      button.dataset.code = family.id;
      button.setAttribute("role", "radio");
      button.setAttribute("aria-checked", String(family.id === state.family));
      const name = document.createElement("span");
      name.className = "family__name";
      name.textContent = family.label;
      const meta = document.createElement("span");
      meta.className = "family__meta";
      meta.textContent = family.products.map(function (product) { return product.name; }).join(" · ");
      button.append(name, meta);
      button.addEventListener("click", function () {
        state.family = family.id;
        syncChecked(container, state.family);
        persist();
      });
      container.appendChild(button);
    });
  }

  function renderPresets() {
    const container = byId("presets");
    container.innerHTML = "";
    state.presets.forEach(function (preset) {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "preset";
      button.textContent = preset.name;
      button.title = preset.description;
      button.addEventListener("click", function () {
        state.lifepath = preset.lifepathId;
        state.implants = preset.implantIds.slice();
        syncChecked(byId("lifepath-tabs"), state.lifepath);
        rebuild();
        toast("Prototipo clonado: " + preset.name);
      });
      container.appendChild(button);
    });
  }

  function renderCatalog() {
    const container = byId("catalog");
    container.innerHTML = "";
    SLOT_ORDER.forEach(function (slot) {
      const items = state.allImplants.filter(function (implant) {
        return implant.slot === slot;
      });
      if (!items.length) {
        return;
      }
      const group = document.createElement("div");
      group.className = "slot-group";
      const title = document.createElement("div");
      title.className = "slot-group__title";
      title.textContent = items[0].slotLabel || slot;
      const list = document.createElement("div");
      list.className = "slot-group__items";
      items.forEach(function (implant) {
        list.appendChild(implantCard(implant));
      });
      group.append(title, list);
      container.appendChild(group);
    });
  }

  function implantCard(implant) {
    const installed = state.implants.indexOf(implant.id) !== -1;
    const card = document.createElement("div");
    card.className = "implant" + (installed ? " is-installed" : "");

    const name = document.createElement("span");
    name.className = "implant__name";
    name.textContent = implant.name;

    const side = document.createElement("span");
    side.className = "implant__side";
    const price = document.createElement("span");
    price.className = "implant__price";
    price.textContent = money(implant.price);
    const button = document.createElement("button");
    button.type = "button";
    button.className = "implant__btn";
    button.textContent = installed ? "Quitar" : "Instalar";
    button.addEventListener("click", function () {
      toggleImplant(implant);
    });
    side.append(price, button);

    const effect = document.createElement("span");
    effect.className = "implant__effect";
    effect.textContent = implant.effect;

    card.append(name, side, effect);
    return card;
  }

  function toggleImplant(implant) {
    const index = state.implants.indexOf(implant.id);
    if (index !== -1) {
      state.implants.splice(index, 1);
    } else {
      const conflicting = state.implants.findIndex(function (id) {
        const other = findImplant(id);
        return other && other.slot === implant.slot;
      });
      if (conflicting !== -1) {
        state.implants[conflicting] = implant.id;
        toast(implant.slotLabel + " reemplazado por " + implant.name);
      } else {
        state.implants.push(implant.id);
      }
    }
    rebuild();
  }

  function installKit() {
    const family = state.families.find(function (item) {
      return item.id === state.family;
    });
    if (!family) {
      return;
    }
    family.products.forEach(function (product) {
      const conflicting = state.implants.findIndex(function (id) {
        const other = findImplant(id);
        return other && other.slot === product.slot;
      });
      if (conflicting !== -1) {
        state.implants[conflicting] = product.id;
      } else {
        state.implants.push(product.id);
      }
    });
    rebuild();
    toast("Kit " + family.label + " instalado (Abstract Factory)");
  }

  function findImplant(id) {
    return state.allImplants.find(function (implant) {
      return implant.id === id;
    });
  }

  function syncChecked(container, code) {
    Array.prototype.forEach.call(container.children, function (child) {
      child.setAttribute("aria-checked", String(child.dataset.code === code));
    });
  }

  async function rebuild() {
    const query = "api/build?name=" + encodeURIComponent(state.name)
      + "&lifepath=" + encodeURIComponent(state.lifepath)
      + (state.implants.length ? "&implants=" + encodeURIComponent(state.implants.join(",")) : "");
    try {
      const build = await get(query);
      state.build = build;
      state.implants = build.layers.slice(1).map(function (layer) {
        return layer.id;
      });
      render();
      persist();
    } catch (error) {
      toast("Error al calcular el build: " + error.message);
    }
  }

  function render() {
    const build = state.build;
    window.Silhouette.render({
      name: state.name,
      lifepathName: findLifepathName(),
      implants: state.implants.map(findImplant).filter(Boolean),
      condition: build.condition,
      humanity: build.stats.humanity
    });
    renderCatalog();
    renderChain(build);
    renderHumanity(build);
    renderEcg(build.condition);
    renderStats(build);
    renderBill(build);
    renderJava(build);
  }

  function findLifepathName() {
    const found = state.lifepaths.find(function (item) {
      return item.id === state.lifepath;
    });
    return found ? found.name : state.lifepath;
  }

  function renderChain(build) {
    const list = byId("chain-list");
    list.innerHTML = "";
    build.layers.forEach(function (layer, index) {
      const isBase = index === 0;
      const isOuter = index === build.layers.length - 1 && !isBase;
      const row = document.createElement("li");
      row.className = "chain__row" + (isBase ? " is-base" : "") + (isOuter ? " is-outer" : "");

      const position = document.createElement("span");
      position.className = "chain__index";
      position.textContent = isBase ? "base" : String(index).padStart(2, "0");

      const name = document.createElement("span");
      name.className = "chain__name";
      name.textContent = layer.name;
      const className = document.createElement("small");
      className.textContent = layer.className;
      name.appendChild(className);

      const delta = document.createElement("span");
      delta.className = "chain__delta";
      delta.textContent = describeDelta(build.layers, index);

      const buttons = document.createElement("span");
      buttons.className = "chain__btns";
      if (!isBase) {
        const up = document.createElement("button");
        up.type = "button";
        up.className = "chain__btn";
        up.textContent = "▲";
        up.addEventListener("click", function () {
          moveImplant(index - 1, -1);
        });
        const down = document.createElement("button");
        down.type = "button";
        down.className = "chain__btn";
        down.textContent = "▼";
        down.addEventListener("click", function () {
          moveImplant(index - 1, 1);
        });
        buttons.append(up, down);
        makeDraggable(row, index - 1);
      }

      row.append(position, name, delta, buttons);
      list.appendChild(row);
    });
    if (build.layers.length <= 1) {
      const empty = document.createElement("li");
      empty.className = "chain__empty";
      empty.textContent = "Solo el humano base. Instala un implante para envolverlo.";
      list.appendChild(empty);
    }
  }

  function describeDelta(layers, index) {
    if (index === 0) {
      const s = layers[0].stats;
      return "STR " + s.strength + " · REF " + s.reflexes + " · HACK " + s.hacking;
    }
    const previous = layers[index - 1].stats;
    const current = layers[index].stats;
    const parts = [];
    pushDelta(parts, "STR", current.strength - previous.strength);
    pushDelta(parts, "REF", current.reflexes - previous.reflexes);
    pushDelta(parts, "HACK", current.hacking - previous.hacking);
    pushDelta(parts, "ARM", current.armor - previous.armor);
    pushDelta(parts, "HUM", current.humanity - previous.humanity);
    return parts.join(" · ");
  }

  function pushDelta(parts, label, value) {
    if (value !== 0) {
      parts.push(label + " " + (value > 0 ? "+" : "") + value);
    }
  }

  function moveImplant(index, direction) {
    const target = index + direction;
    if (target < 0 || target >= state.implants.length) {
      return;
    }
    const moved = state.implants.splice(index, 1)[0];
    state.implants.splice(target, 0, moved);
    rebuild();
  }

  let dragIndex = null;

  function makeDraggable(row, implantIndex) {
    row.draggable = true;
    row.addEventListener("dragstart", function (event) {
      dragIndex = implantIndex;
      row.classList.add("is-dragging");
      event.dataTransfer.effectAllowed = "move";
    });
    row.addEventListener("dragend", function () {
      row.classList.remove("is-dragging");
      dragIndex = null;
    });
    row.addEventListener("dragover", function (event) {
      event.preventDefault();
      row.classList.add("is-over");
    });
    row.addEventListener("dragleave", function () {
      row.classList.remove("is-over");
    });
    row.addEventListener("drop", function (event) {
      event.preventDefault();
      row.classList.remove("is-over");
      if (dragIndex === null || dragIndex === implantIndex) {
        return;
      }
      const moved = state.implants.splice(dragIndex, 1)[0];
      state.implants.splice(implantIndex, 0, moved);
      rebuild();
    });
  }

  function renderHumanity(build) {
    const humanity = build.stats.humanity;
    byId("humanity-value").textContent = humanity;
    const fill = byId("humanity-fill");
    fill.style.width = Math.max(0, Math.min(100, humanity)) + "%";
    const label = byId("condition-label");
    label.textContent = build.condition;
    label.dataset.state = build.condition;
  }

  function renderEcg(condition) {
    const period = condition === "CYBERPSYCHOSIS" ? 22 : condition === "UNSTABLE" ? 32 : 44;
    const spike = condition === "CYBERPSYCHOSIS" ? 12 : condition === "UNSTABLE" ? 18 : 24;
    let path = "M0 30";
    for (let x = 0; x <= 320; x += period) {
      path += " L" + (x + period * 0.4) + " 30"
        + " L" + (x + period * 0.5) + " " + (30 - spike)
        + " L" + (x + period * 0.6) + " " + (30 + spike * 0.8)
        + " L" + (x + period * 0.7) + " 30";
    }
    path += " L320 30";
    byId("ecg-path").setAttribute("d", path);
  }

  function renderStats(build) {
    const container = byId("stats-list");
    container.innerHTML = "";
    const base = build.layers[0].stats;
    const rows = [
      ["Fuerza", "strength"],
      ["Reflejos", "reflexes"],
      ["Hackeo", "hacking"],
      ["Armadura", "armor"]
    ];
    rows.forEach(function (row) {
      const value = build.stats[row[1]];
      const delta = value - base[row[1]];
      const wrapper = document.createElement("div");
      wrapper.className = "stat";
      const label = document.createElement("span");
      label.className = "stat__label";
      label.textContent = row[0];
      const bar = document.createElement("span");
      bar.className = "stat__bar";
      const fill = document.createElement("span");
      fill.className = "stat__fill " + (delta > 0 ? "stat__fill--up" : delta < 0 ? "stat__fill--down" : "");
      fill.style.width = Math.max(0, Math.min(100, (value / STAT_MAX) * 100)) + "%";
      bar.appendChild(fill);
      const valueLabel = document.createElement("span");
      valueLabel.className = "stat__value";
      valueLabel.textContent = value + (delta !== 0 ? " (" + (delta > 0 ? "+" : "") + delta + ")" : "");
      wrapper.append(label, bar, valueLabel);
      container.appendChild(wrapper);
    });
  }

  function renderBill(build) {
    byId("bill-total").textContent = money(build.stats.cost);
  }

  function renderJava(build) {
    const layers = build.layers;
    const baseClass = layers[0].className;
    let expression = baseClass + "(\"" + state.name + "\")";
    for (let i = 1; i < layers.length; i++) {
      expression = layers[i].className + "(" + expression + ")";
    }
    byId("java-code").textContent = "Human patient =\n    " + expression + ";";
    byId("java-description").textContent = "patient.getDescription() → \"" + build.description + "\"";
  }

  function setStatus(statusState, text) {
    const status = byId("status");
    status.dataset.state = statusState;
    status.textContent = text;
  }

  let toastTimer = null;

  function toast(message) {
    const element = byId("toast");
    element.textContent = message;
    element.classList.add("is-visible");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () {
      element.classList.remove("is-visible");
    }, 2600);
  }

  init();
})();
