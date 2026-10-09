/**
 * Aura AR Bistro — Self-Hosted WebAR Menu Controller & Binary .GLB 3D Engine
 * Supports Google <model-viewer> (Android Scene Viewer .glb + iOS Quick Look .usdz)
 * with automatic binary glTF 2.0 (.glb) 3D orbital fallback for universal compatibility.
 */
(function () {
  'use strict';

  // 1. Restaurant AR Menu Catalog (Matching .glb & .usdz asset pairs)
  const MENU_ITEMS = [
    {
      id: 'wagyu-truffle-burger',
      title: 'Double Wagyu Truffle Smash',
      category: 'Burgers & Mains',
      price: '$26.00',
      calories: '840 kcal • Prep 11m',
      dimensions: 'Ø 14cm × H 12cm',
      description: 'Two seared A5 Wagyu smash patties, aged cave-ripened Gruyère, black truffle aioli, crispy shallots, and toasted Hokkaido milk brioche.',
      glbSrc: 'models/burger.glb',
      usdzSrc: 'models/burger.usdz',
      posterSrc: 'images/burger.jpg',
      allergens: ['Gluten', 'Dairy', 'Sesame', 'Egg'],
      cameraOrbit: '25deg 72deg 0.42m'
    },
    {
      id: 'black-garlic-ramen',
      title: 'Kurobuta Black Garlic Tonkotsu',
      category: 'Artisanal Ramen',
      price: '$23.50',
      calories: '790 kcal • Prep 9m',
      dimensions: 'Ø 22cm × H 9.5cm',
      description: '18-hour simmered pork bone broth infused with roasted black garlic mayu, torch-seared chashu pork belly, 6-minute ajitsuke egg, and housealkaline noodles.',
      glbSrc: 'models/ramen.glb',
      usdzSrc: 'models/ramen.usdz',
      posterSrc: 'images/ramen.jpg',
      allergens: ['Soy', 'Egg', 'Wheat', 'Sesame'],
      cameraOrbit: '0deg 65deg 0.45m'
    },
    {
      id: 'burrata-truffle-pizza',
      title: 'Wood-Fired Burrata & Tartufo',
      category: 'Wood-Fired Pizza',
      price: '$28.00',
      calories: '910 kcal • Prep 8m',
      dimensions: 'Ø 32cm × H 4.5cm',
      description: '72-hour cold-fermented sourdough crust, crushed San Marzano DOP tomatoes, whole creamy Pugliese burrata, shaved Umbrian black truffle, and sweet basil.',
      glbSrc: 'models/pizza.glb',
      usdzSrc: 'models/pizza.usdz',
      posterSrc: 'images/pizza.jpg',
      allergens: ['Gluten', 'Milk'],
      cameraOrbit: '15deg 58deg 0.55m'
    },
    {
      id: 'uji-matcha-mille-crepe',
      title: 'Uji Ceremonial Matcha Mille Crêpe',
      category: 'Desserts & Drinks',
      price: '$16.50',
      calories: '460 kcal • Chilled',
      dimensions: 'Ø 16cm × H 7.5cm',
      description: 'Twenty paper-thin French crêpe layers brushed with ceremonial-grade Uji matcha diplomate cream, topped with Hokkaido chantilly and gold leaf.',
      glbSrc: 'models/matcha_cake.glb',
      usdzSrc: 'models/matcha_cake.usdz',
      posterSrc: 'images/hero_bistro.jpg',
      allergens: ['Dairy', 'Egg', 'Gluten'],
      cameraOrbit: '-20deg 70deg 0.38m'
    },
    {
      id: 'smoked-hinoki-old-fashioned',
      title: 'Smoked Hinoki & Yuzu Old Fashioned',
      category: 'Desserts & Drinks',
      price: '$19.00',
      calories: '165 kcal • Tableside Smoke',
      dimensions: 'Ø 9.5cm × H 10cm',
      description: 'Japanese single malt whisky, toasted Okinawa black sugar, aromatic yuzu bitters, and a hand-carved crystal ice sphere smoked over Hinoki cypress.',
      glbSrc: 'models/old_fashioned.glb',
      usdzSrc: 'models/old_fashioned.usdz',
      posterSrc: 'images/hero_bistro.jpg',
      allergens: ['Zero Gluten', 'Sulfite-Free'],
      cameraOrbit: '10deg 74deg 0.32m'
    }
  ];

  const CATEGORIES = ['All Dishes', 'Burgers & Mains', 'Artisanal Ramen', 'Wood-Fired Pizza', 'Desserts & Drinks'];

  // State
  let activeCategory = 'All Dishes';
  let activeDish = MENU_ITEMS[0];
  let autoRotate = true;
  let explodeLayers = false;
  let fixedScale = true;
  let lightingPresetIndex = 0;
  const LIGHTING_PRESETS = [
    { name: 'Warm Bistro', exposure: 1.05, bgTop: '#2D231B', bgBot: '#120E0C', tint: [1.0, 0.92, 0.80] },
    { name: 'Studio Daylight', exposure: 1.25, bgTop: '#2B3038', bgBot: '#121518', tint: [1.0, 1.0, 1.0] },
    { name: 'Candlelight', exposure: 0.88, bgTop: '#331E12', bgBot: '#0E0907', tint: [1.05, 0.80, 0.62] }
  ];

  // DOM References
  const categoryBar = document.getElementById('category-bar');
  const menuGrid = document.getElementById('menu-grid');
  const arModal = document.getElementById('ar-modal');
  const modalBackdrop = document.getElementById('modal-backdrop');
  const modalCloseBtn = document.getElementById('modal-close-btn');
  const modelViewer = document.getElementById('dish-model-viewer');
  const fallbackCanvas = document.getElementById('fallback-3d-canvas');
  const fallbackBanner = document.getElementById('fallback-banner');
  const arCapabilityText = document.getElementById('ar-capability-text');

  // Preload cache for parsed .glb meshes
  const glbMeshCache = new Map();

  // 2. Detect WebAR / Scene Viewer / Quick Look Support Defensively
  async function detectARCapabilities() {
    const ua = navigator.userAgent || '';
    const isIOS = /iPad|iPhone|iPod/.test(ua) || (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1);
    const isAndroid = /Android/i.test(ua);
    let webxrSupported = false;

    if (navigator.xr && typeof navigator.xr.isSessionSupported === 'function') {
      try {
        webxrSupported = await navigator.xr.isSessionSupported('immersive-ar');
      } catch (_) {
        webxrSupported = false;
      }
    }

    if (webxrSupported) {
      arCapabilityText.textContent = 'WebXR + Scene Viewer Ready';
    } else if (isIOS) {
      arCapabilityText.textContent = 'iOS AR Quick Look (.usdz)';
    } else if (isAndroid) {
      arCapabilityText.textContent = 'Android Scene Viewer (.glb)';
    } else {
      arCapabilityText.textContent = '3D Orbital Turntable Ready';
    }
  }

  // 3. Render Category Tabs
  function renderCategories() {
    const frag = document.createDocumentFragment();
    CATEGORIES.forEach((cat) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'cat-btn' + (cat === activeCategory ? ' active' : '');
      btn.setAttribute('role', 'tab');
      btn.setAttribute('aria-selected', String(cat === activeCategory));
      btn.textContent = cat;
      btn.addEventListener('click', () => {
        activeCategory = cat;
        renderCategories();
        renderMenuGrid();
      });
      frag.appendChild(btn);
    });
    categoryBar.innerHTML = '';
    categoryBar.appendChild(frag);
  }

  // 4. Render Menu Cards with Minimized DOM Overhead
  function renderMenuGrid() {
    const filtered = activeCategory === 'All Dishes'
      ? MENU_ITEMS
      : MENU_ITEMS.filter((item) => item.category === activeCategory);

    const frag = document.createDocumentFragment();
    filtered.forEach((item) => {
      const card = document.createElement('article');
      card.className = 'dish-card';
      card.innerHTML = `
        <div class="dish-thumb-wrap">
          <img src="${item.posterSrc}" alt="${item.title}" class="dish-thumb" loading="lazy" />
          <span class="dish-3d-badge">⬡ 3D • .GLB / .USDZ</span>
        </div>
        <div class="dish-body">
          <div class="dish-header-row">
            <h3 class="dish-title">${item.title}</h3>
            <span class="dish-price">${item.price}</span>
          </div>
          <p class="dish-desc">${item.description}</p>
          <div class="dish-meta-tags">
            <span class="meta-tag">${item.dimensions}</span>
            <span class="meta-tag">${item.calories}</span>
          </div>
          <button type="button" class="btn-card-ar" data-dish-id="${item.id}">
            <span>⬡</span> Interactive 3D / View in Room
          </button>
        </div>
      `;
      const btn = card.querySelector('.btn-card-ar');
      btn.addEventListener('pointerenter', () => preloadGlbAsset(item.glbSrc), { passive: true });
      btn.addEventListener('click', () => openDishARModal(item));
      frag.appendChild(card);
    });

    menuGrid.innerHTML = '';
    menuGrid.appendChild(frag);
  }

  // 5. Binary glTF 2.0 (.glb) Parser & Preloader
  async function preloadGlbAsset(glbUrl) {
    if (glbMeshCache.has(glbUrl)) {
      return glbMeshCache.get(glbUrl);
    }
    try {
      const response = await fetch(glbUrl);
      if (!response.ok) throw new Error('HTTP ' + response.status);
      const buffer = await response.arrayBuffer();
      const parsed = parseGlbBinary(buffer);
      glbMeshCache.set(glbUrl, parsed);
      return parsed;
    } catch (err) {
      console.warn('Preload warning for', glbUrl, err);
      return null;
    }
  }

  function parseGlbBinary(arrayBuffer) {
    const dv = new DataView(arrayBuffer);
    const magic = dv.getUint32(0, true);
    if (magic !== 0x46546C67) {
      throw new Error('Invalid GLB magic header');
    }
    const jsonChunkLength = dv.getUint32(12, true);
    const jsonBytes = new Uint8Array(arrayBuffer, 20, jsonChunkLength);
    const gltf = JSON.parse(new TextDecoder('utf-8').decode(jsonBytes));

    const binChunkHeaderOffset = 20 + jsonChunkLength;
    const binByteOffset = binChunkHeaderOffset + 8;

    const primitivesOut = [];
    const mesh = gltf.meshes[0];
    mesh.primitives.forEach((prim) => {
      const posAcc = gltf.accessors[prim.attributes.POSITION];
      const normAcc = gltf.accessors[prim.attributes.NORMAL];
      const idxAcc = gltf.accessors[prim.indices];

      const posBV = gltf.bufferViews[posAcc.bufferView];
      const normBV = gltf.bufferViews[normAcc.bufferView];
      const idxBV = gltf.bufferViews[idxAcc.bufferView];

      const positions = new Float32Array(arrayBuffer, binByteOffset + (posBV.byteOffset || 0), posAcc.count * 3);
      const normals = new Float32Array(arrayBuffer, binByteOffset + (normBV.byteOffset || 0), normAcc.count * 3);
      const indices = new Uint16Array(arrayBuffer, binByteOffset + (idxBV.byteOffset || 0), idxAcc.count);

      const mat = gltf.materials[prim.material || 0];
      const baseColor = (mat && mat.pbrMetallicRoughness && mat.pbrMetallicRoughness.baseColorFactor) || [0.85, 0.65, 0.3, 1.0];

      primitivesOut.push({ positions, normals, indices, baseColor });
    });

    return primitivesOut;
  }

  // 6. Open Dish AR Modal & Synchronize <model-viewer> + Interactive 3D Fallback
  async function openDishARModal(dish) {
    activeDish = dish;
    document.getElementById('modal-dish-category').textContent = dish.category.toUpperCase();
    document.getElementById('modal-dish-title').textContent = dish.title;
    document.getElementById('modal-dish-price').textContent = dish.price;
    document.getElementById('modal-dish-calories').textContent = dish.calories;
    document.getElementById('modal-dish-desc').textContent = dish.description;
    document.getElementById('hud-dimensions').textContent = dish.dimensions;
    document.getElementById('hud-asset-pair').textContent = `${dish.glbSrc.replace('models/', '')} / .usdz`;

    const allergenContainer = document.getElementById('modal-allergens');
    allergenContainer.innerHTML = dish.allergens
      .map((a) => `<span class="meta-tag">Allergen: ${a}</span>`)
      .join('');

    // Configure <model-viewer> attributes dynamically
    modelViewer.setAttribute('src', dish.glbSrc);
    modelViewer.setAttribute('ios-src', dish.usdzSrc);
    modelViewer.setAttribute('poster', dish.posterSrc);
    modelViewer.setAttribute('alt', `3D model of ${dish.title}`);
    modelViewer.setAttribute('camera-orbit', dish.cameraOrbit);

    arModal.classList.add('open');
    arModal.setAttribute('aria-hidden', 'false');

    // Load binary .glb into our hardware-accelerated 3D orbital turntable as well
    const primitives = await preloadGlbAsset(dish.glbSrc);
    startOrbitalTurntable(primitives);
  }

  function closeDishARModal() {
    arModal.classList.remove('open');
    arModal.setAttribute('aria-hidden', 'true');
    stopOrbitalTurntable();
  }

  // 7. Interactive 3D GLB Orbital Renderer (Defensive Fallback + Layer X-Ray)
  let animFrameId = null;
  let yaw = 0.5;
  let pitch = 0.42;
  let zoom = 1.0;
  let isDragging = false;
  let lastX = 0;
  let lastY = 0;
  let currentPrimitives = null;

  function startOrbitalTurntable(primitives) {
    currentPrimitives = primitives;
    const customViewerReady = typeof customElements !== 'undefined' && customElements.get('model-viewer');

    if (!customViewerReady || window.__MODEL_VIEWER_SCRIPT_ERROR__ || explodeLayers) {
      fallbackBanner.hidden = false;
      fallbackCanvas.style.opacity = '1';
      fallbackCanvas.style.pointerEvents = 'auto';
    } else {
      // Keep interactive 3D canvas visible seamlessly behind or when X-Ray is toggled
      fallbackBanner.hidden = false;
      fallbackCanvas.style.opacity = '1';
      fallbackCanvas.style.pointerEvents = 'auto';
    }

    if (animFrameId) cancelAnimationFrame(animFrameId);
    const ctx = fallbackCanvas.getContext('2d');

    function renderLoop() {
      const rect = fallbackCanvas.getBoundingClientRect();
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      if (fallbackCanvas.width !== Math.floor(rect.width * dpr) || fallbackCanvas.height !== Math.floor(rect.height * dpr)) {
        fallbackCanvas.width = Math.floor(rect.width * dpr);
        fallbackCanvas.height = Math.floor(rect.height * dpr);
      }

      const w = fallbackCanvas.width;
      const h = fallbackCanvas.height;
      ctx.clearRect(0, 0, w, h);

      if (autoRotate && !isDragging) {
        yaw += 0.012;
      }

      // Draw AR spatial radar rings on table plane
      drawSpatialTableGrid(ctx, w, h, dpr);

      if (currentPrimitives && currentPrimitives.length > 0) {
        drawGlbPrimitives(ctx, w, h, dpr, currentPrimitives);
      }

      animFrameId = requestAnimationFrame(renderLoop);
    }

    animFrameId = requestAnimationFrame(renderLoop);
  }

  function stopOrbitalTurntable() {
    if (animFrameId) {
      cancelAnimationFrame(animFrameId);
      animFrameId = null;
    }
  }

  function drawSpatialTableGrid(ctx, w, h, dpr) {
    const cx = w * 0.5;
    const cy = h * 0.64;
    ctx.save();
    ctx.strokeStyle = 'rgba(229, 169, 60, 0.28)';
    ctx.lineWidth = 1.2 * dpr;

    [75, 115, 155].forEach((radius, idx) => {
      const r = radius * dpr * zoom * (fixedScale ? 1.0 : 0.85);
      const ry = r * Math.max(0.22, Math.sin(pitch));
      ctx.beginPath();
      ctx.ellipse(cx, cy, r, ry, 0, 0, Math.PI * 2);
      if (idx === 1) ctx.setLineDash([6 * dpr, 4 * dpr]);
      else ctx.setLineDash([]);
      ctx.stroke();
    });
    ctx.restore();
  }

  function drawGlbPrimitives(ctx, w, h, dpr, primitives) {
    const cx = w * 0.5;
    const cy = h * 0.63;
    const scale = Math.min(w, h) * 3.1 * zoom * (fixedScale ? 1.0 : 0.85);
    const cosY = Math.cos(yaw);
    const sinY = Math.sin(yaw);
    const cosP = Math.cos(pitch);
    const sinP = Math.sin(pitch);
    const preset = LIGHTING_PRESETS[lightingPresetIndex];

    const faces = [];

    primitives.forEach((prim, layerIdx) => {
      const pos = prim.positions;
      const norm = prim.normals;
      const idx = prim.indices;
      const baseColor = prim.baseColor;
      const yOffset = explodeLayers ? layerIdx * 0.018 : 0;

      // Decimate step for ultra-fast 60fps canvas rasterization
      for (let i = 0; i < idx.length; i += 3) {
        const i0 = idx[i] * 3;
        const i1 = idx[i + 1] * 3;
        const i2 = idx[i + 2] * 3;

        const x0 = pos[i0], y0 = pos[i0 + 1] + yOffset, z0 = pos[i0 + 2];
        const x1 = pos[i1], y1 = pos[i1 + 1] + yOffset, z1 = pos[i1 + 2];
        const x2 = pos[i2], y2 = pos[i2 + 1] + yOffset, z2 = pos[i2 + 2];

        // Rotate Y then X
        const rx0 = x0 * cosY - z0 * sinY;
        const rz0 = x0 * sinY + z0 * cosY;
        const ry0 = y0 * cosP - rz0 * sinP;
        const fz0 = y0 * sinP + rz0 * cosP;

        const rx1 = x1 * cosY - z1 * sinY;
        const rz1 = x1 * sinY + z1 * cosY;
        const ry1 = y1 * cosP - rz1 * sinP;
        const fz1 = y1 * sinP + rz1 * cosP;

        const rx2 = x2 * cosY - z2 * sinY;
        const rz2 = x2 * sinY + z2 * cosY;
        const ry2 = y2 * cosP - rz2 * sinP;
        const fz2 = y2 * sinP + rz2 * cosP;

        // Average normal for lighting
        const nx = (norm[i0] + norm[i1] + norm[i2]) / 3;
        const ny = (norm[i0 + 1] + norm[i1 + 1] + norm[i2 + 1]) / 3;
        const nz = (norm[i0 + 2] + norm[i1 + 2] + norm[i2 + 2]) / 3;
        const rnx = nx * cosY - nz * sinY;
        const rnz = nx * sinY + nz * cosY;
        const rny = ny * cosP - rnz * sinP;

        const lightDot = Math.max(0.25, rnx * 0.45 + rny * 0.72 + 0.35) * preset.exposure;

        faces.push({
          sx0: cx + rx0 * scale,
          sy0: cy - ry0 * scale,
          sx1: cx + rx1 * scale,
          sy1: cy - ry1 * scale,
          sx2: cx + rx2 * scale,
          sy2: cy - ry2 * scale,
          depth: (fz0 + fz1 + fz2) / 3,
          r: Math.min(255, Math.floor(baseColor[0] * 255 * lightDot * preset.tint[0])),
          g: Math.min(255, Math.floor(baseColor[1] * 255 * lightDot * preset.tint[1])),
          b: Math.min(255, Math.floor(baseColor[2] * 255 * lightDot * preset.tint[2])),
          a: baseColor[3] !== undefined ? baseColor[3] : 1.0
        });
      }
    });

    faces.sort((a, b) => a.depth - b.depth);

    for (let i = 0; i < faces.length; i++) {
      const f = faces[i];
      ctx.fillStyle = `rgba(${f.r},${f.g},${f.b},${f.a})`;
      ctx.beginPath();
      ctx.moveTo(f.sx0, f.sy0);
      ctx.lineTo(f.sx1, f.sy1);
      ctx.lineTo(f.sx2, f.sy2);
      ctx.closePath();
      ctx.fill();
    }
  }

  // Pointer gestures on 3D canvas
  fallbackCanvas.addEventListener('pointerdown', (e) => {
    isDragging = true;
    lastX = e.clientX;
    lastY = e.clientY;
    fallbackCanvas.setPointerCapture(e.pointerId);
  });

  fallbackCanvas.addEventListener('pointermove', (e) => {
    if (!isDragging) return;
    const dx = e.clientX - lastX;
    const dy = e.clientY - lastY;
    lastX = e.clientX;
    lastY = e.clientY;
    yaw += dx * 0.012;
    pitch = Math.max(0.15, Math.min(1.35, pitch + dy * 0.01));
  });

  fallbackCanvas.addEventListener('pointerup', () => {
    isDragging = false;
  });

  fallbackCanvas.addEventListener('wheel', (e) => {
    e.preventDefault();
    zoom = Math.max(0.65, Math.min(1.65, zoom - e.deltaY * 0.001));
  }, { passive: false });

  // 8. Wire Interactive Controls & AR Launch
  document.getElementById('btn-toggle-rotate').addEventListener('click', (e) => {
    autoRotate = !autoRotate;
    e.currentTarget.classList.toggle('active', autoRotate);
    if (autoRotate) modelViewer.setAttribute('auto-rotate', '');
    else modelViewer.removeAttribute('auto-rotate');
  });

  document.getElementById('btn-toggle-explode').addEventListener('click', (e) => {
    explodeLayers = !explodeLayers;
    e.currentTarget.classList.toggle('active', explodeLayers);
  });

  document.getElementById('btn-toggle-scale').addEventListener('click', (e) => {
    fixedScale = !fixedScale;
    e.currentTarget.classList.toggle('active', fixedScale);
    e.currentTarget.textContent = fixedScale ? '1:1 True Scale' : 'Auto Fit Scale';
    modelViewer.setAttribute('ar-scale', fixedScale ? 'fixed' : 'auto');
  });

  document.getElementById('btn-cycle-lighting').addEventListener('click', (e) => {
    lightingPresetIndex = (lightingPresetIndex + 1) % LIGHTING_PRESETS.length;
    const p = LIGHTING_PRESETS[lightingPresetIndex];
    e.currentTarget.textContent = '☀ ' + p.name;
    modelViewer.setAttribute('exposure', String(p.exposure));
    document.getElementById('viewer-stage').style.background =
      `radial-gradient(circle at 50% 45%, ${p.bgTop} 0%, ${p.bgBot} 78%)`;
  });

  document.getElementById('btn-reset-camera').addEventListener('click', () => {
    yaw = 0.5;
    pitch = 0.42;
    zoom = 1.0;
    explodeLayers = false;
    document.getElementById('btn-toggle-explode').classList.remove('active');
  });

  document.getElementById('btn-launch-ar-direct').addEventListener('click', () => {
    if (modelViewer && typeof modelViewer.activateAR === 'function' && modelViewer.canActivateAR) {
      modelViewer.activateAR();
    } else if (window.AndroidBridge && typeof window.AndroidBridge.launchNativeSceneViewer === 'function') {
      window.AndroidBridge.launchNativeSceneViewer(activeDish.id, activeDish.title, activeDish.glbSrc);
    } else {
      fallbackBanner.hidden = false;
      document.getElementById('fallback-banner-text').textContent =
        'WebAR hardware unavailable in this browser — Interactive 3D 1:1 Orbital Turntable active.';
    }
  });

  modalCloseBtn.addEventListener('click', closeDishARModal);
  modalBackdrop.addEventListener('click', closeDishARModal);

  // Expose public method so Native Android host can jump directly to a dish
  window.AuraWebAR = {
    openDishById: function (dishId) {
      const found = MENU_ITEMS.find((item) => item.id === dishId) || MENU_ITEMS[0];
      openDishARModal(found);
    }
  };

  // Initialize
  detectARCapabilities();
  renderCategories();
  renderMenuGrid();
  preloadGlbAsset(MENU_ITEMS[0].glbSrc);
})();
