(function () {
  const SLOT_ANCHORS = {
    FACE: { x: 180, y: 72, side: "right" },
    NERVOUS_SYSTEM: { x: 180, y: 150, side: "left" },
    OPERATING_SYSTEM: { x: 180, y: 202, side: "right" },
    ARMS: { x: 96, y: 244, side: "left" },
    SKELETON: { x: 180, y: 292, side: "right" },
    INTEGUMENTARY_SYSTEM: { x: 264, y: 372, side: "right" }
  };

  const TEMPLATE = [
    '<svg viewBox="0 0 360 620" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">',
    '<style>',
    '.ring{fill:none;transition:opacity .3s ease,stroke .3s ease;}',
    '.ring--base{stroke-dasharray:4 7;}',
    '.figure{fill:var(--ink);stroke:var(--ink);}',
    '.figure__limb{stroke-width:20;stroke-linecap:round;fill:none;}',
    '.figure__leg{stroke-width:26;stroke-linecap:round;fill:none;}',
    '.slot{opacity:0;transition:opacity .35s ease;fill:var(--accent-soft);stroke:var(--accent);stroke-width:2.5;}',
    '.slot.is-active{opacity:1;}',
    '.slot--line{fill:none;}',
    '.slot--outline{fill:none;stroke-width:3;}',
    '.node{fill:var(--accent);}',
    '.node__pulse{fill:none;stroke:var(--accent);stroke-width:2;animation:pulse 2.2s ease-out infinite;}',
    '.node__label{font-family:"IBM Plex Mono",monospace;font-size:9.5px;fill:var(--ink);}',
    '.node__line{stroke:var(--accent);stroke-width:1.2;fill:none;opacity:.7;}',
    '@keyframes pulse{0%{r:6;opacity:.9}100%{r:20;opacity:0}}',
    '</style>',
    '<g id="rings"></g>',
    '<g id="body" class="figure">',
    '<path class="figure__limb" d="M132 142 L108 210 L98 292"/>',
    '<path class="figure__limb" d="M228 142 L252 210 L262 292"/>',
    '<path class="figure__leg" d="M166 300 L158 432 L152 566"/>',
    '<path class="figure__leg" d="M194 300 L202 432 L208 566"/>',
    '<path d="M126 124 Q180 108 234 124 L218 298 Q180 312 142 298 Z"/>',
    '<circle cx="180" cy="72" r="33"/>',
    '</g>',
    '<g id="slots">',
    '<circle class="slot" data-slot="FACE" cx="180" cy="72" r="33"/>',
    '<path class="slot slot--line" data-slot="NERVOUS_SYSTEM" d="M180 116 L180 250"/>',
    '<circle class="slot" data-slot="OPERATING_SYSTEM" cx="180" cy="202" r="18"/>',
    '<circle class="slot" data-slot="ARMS" cx="100" cy="238" r="16"/>',
    '<circle class="slot" data-slot="ARMS" cx="260" cy="238" r="16"/>',
    '<ellipse class="slot" data-slot="SKELETON" cx="180" cy="292" rx="42" ry="24"/>',
    '<path class="slot slot--outline" data-slot="INTEGUMENTARY_SYSTEM" d="M126 124 Q180 108 234 124 L262 292 M126 124 L98 292 M228 142 L252 210 L262 292 M166 300 L152 566 M194 300 L208 566 M218 298 Q180 312 142 298"/>',
    '</g>',
    '<g id="nodes"></g>',
    '</svg>'
  ].join("");

  function mount(container) {
    container.innerHTML = TEMPLATE;
  }

  function render(model) {
    const svg = document.querySelector("#patient-stage svg");
    if (!svg) {
      return;
    }
    const implants = model.implants || [];
    renderRings(svg.querySelector("#rings"), implants.length);
    renderSlots(svg, implants);
    renderNodes(svg.querySelector("#nodes"), implants);
  }

  function renderRings(group, decoratorCount) {
    group.innerHTML = "";
    const layers = decoratorCount + 1;
    for (let index = 0; index < layers; index++) {
      const ring = document.createElementNS("http://www.w3.org/2000/svg", "ellipse");
      ring.setAttribute("cx", "180");
      ring.setAttribute("cy", "300");
      ring.setAttribute("rx", String(96 + index * 11));
      ring.setAttribute("ry", String(150 + index * 15));
      const isBase = index === 0;
      const isOuter = index === layers - 1 && layers > 1;
      ring.setAttribute("class", "ring" + (isBase ? " ring--base" : ""));
      const accent = isOuter ? "var(--accent)" : "var(--muted)";
      ring.setAttribute("stroke", accent);
      ring.setAttribute("stroke-width", isOuter ? "2.4" : "1.4");
      ring.setAttribute("opacity", isBase ? "0.7" : String(0.28 + index * 0.08));
      group.appendChild(ring);
    }
  }

  function renderSlots(svg, implants) {
    const active = {};
    implants.forEach(function (implant) {
      active[implant.slot] = true;
    });
    svg.querySelectorAll(".slot").forEach(function (slot) {
      slot.classList.toggle("is-active", Boolean(active[slot.dataset.slot]));
    });
  }

  function renderNodes(group, implants) {
    group.innerHTML = "";
    const usedArms = { count: 0 };
    implants.forEach(function (implant) {
      const anchor = anchorFor(implant.slot, usedArms);
      const pulse = document.createElementNS("http://www.w3.org/2000/svg", "circle");
      pulse.setAttribute("cx", anchor.x);
      pulse.setAttribute("cy", anchor.y);
      pulse.setAttribute("r", "6");
      pulse.setAttribute("class", "node__pulse");
      group.appendChild(pulse);

      const dot = document.createElementNS("http://www.w3.org/2000/svg", "circle");
      dot.setAttribute("cx", anchor.x);
      dot.setAttribute("cy", anchor.y);
      dot.setAttribute("r", "5");
      dot.setAttribute("class", "node");
      group.appendChild(dot);

      const labelX = anchor.side === "left" ? anchor.x - 12 : anchor.x + 12;
      const anchorAttr = anchor.side === "left" ? "end" : "start";
      const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
      line.setAttribute("x1", anchor.x);
      line.setAttribute("y1", anchor.y);
      line.setAttribute("x2", labelX);
      line.setAttribute("y2", anchor.y - 10);
      line.setAttribute("class", "node__line");
      group.appendChild(line);

      const label = document.createElementNS("http://www.w3.org/2000/svg", "text");
      label.setAttribute("x", labelX);
      label.setAttribute("y", anchor.y - 13);
      label.setAttribute("text-anchor", anchorAttr);
      label.setAttribute("class", "node__label");
      label.textContent = implant.name;
      group.appendChild(label);
    });
  }

  function anchorFor(slot, usedArms) {
    const anchor = SLOT_ANCHORS[slot] || SLOT_ANCHORS.OPERATING_SYSTEM;
    if (slot === "ARMS") {
      const fromRight = usedArms.count % 2 === 1;
      usedArms.count++;
      return {
        x: fromRight ? 260 : 100,
        y: 244,
        side: fromRight ? "right" : "left"
      };
    }
    return { x: anchor.x, y: anchor.y, side: anchor.side };
  }

  window.Silhouette = { mount: mount, render: render };
})();
