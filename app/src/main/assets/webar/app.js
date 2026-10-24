/**
 * The Golden Oak — 4D Smart Menu WebAR Controller & 3D Orbital Canvas
 * Supports Google <model-viewer> (Android Scene Viewer .glb + iOS Quick Look .usdz)
 * with floating holographic AR nutrition tags, 3D chef mascot, and binary .glb turntable fallback.
 */
(function () {
  'use strict';

  // 1. Restaurant 4D Menu Catalog (Matching Reference Videos 1, 2, 3 & 4)
  const MENU_ITEMS = [
    {
      id: 'grilled-chicken-salad',
      title: 'Grilled Chicken & Avocado Salad',
      category: 'Salads & Bowls',
      dietary: 'NON_VEG',
      price: '$18.50',
      calories: '🔥 340 kcal • Prep 8m',
      dimensions: 'Ø 24cm × H 8cm',
      description: 'Char-grilled rosemary chicken breast over crisp mixed greens, Haas avocado chunks, San Marzano cherry tomatoes, Persian cucumbers, and cold-pressed extra virgin olive oil.',
      glbSrc: 'models/burger.glb',
      usdzSrc: 'models/burger.usdz',
      posterSrc: 'images/burger.jpg',
      allergens: ['Zero Dairy', 'Gluten-Free', 'Keto-Friendly'],
      cameraOrbit: '15deg 62deg 0.50m',
      hotspots: [
        { name: 'Avocado', benefit1: 'Omega 9 • Good for Heart', emoji: '🥑', pos: '0.08 0.05 0.04' },
        { name: 'Grilled Chicken', benefit1: 'High Protein • Low Carbs', emoji: '🍗', pos: '-0.07 0.07 -0.02' },
        { name: 'Cherry Tomatoes', benefit1: 'Vitamin C • Antioxidants', emoji: '🍅', pos: '0.02 0.04 -0.08' },
        { name: 'Persian Cucumber', benefit1: 'Hydration • Low Calorie', emoji: '🥒', pos: '-0.06 0.03 0.07' },
        { name: 'Kalamata Olives', benefit1: 'Good Fats • Anti-inflammatory', emoji: '🫒', pos: '0.05 0.03 -0.04' }
      ]
    },
    {
      id: 'burrata-truffle-pizza',
      title: 'Italian Supreme Wood-Fired Pizza',
      category: 'Pizza & Italian',
      dietary: 'VEG',
      price: '$28.00',
      calories: '🔥 890 kcal • Prep 7m',
      dimensions: 'Ø 32cm × H 4cm',
      description: '72-hour sourdough crust, San Marzano DOP tomatoes, whole creamy Pugliese burrata cheese, shaved Umbrian black truffle, and sweet basil leaves.',
      glbSrc: 'models/pizza.glb',
      usdzSrc: 'models/pizza.usdz',
      posterSrc: 'images/pizza.jpg',
      allergens: ['Gluten', 'Pugliese Dairy'],
      cameraOrbit: '15deg 55deg 0.55m',
      hotspots: [
        { name: 'Pugliese Burrata', benefit1: 'Rich Calcium & Protein', emoji: '🧀', pos: '0.0 0.06 0.0' },
        { name: 'San Marzano Tomato', benefit1: 'Lycopene & Minerals', emoji: '🍅', pos: '0.10 0.03 0.05' },
        { name: 'Umbrian Truffle', benefit1: 'Earthy Antioxidants', emoji: '🍄', pos: '-0.08 0.04 -0.06' },
        { name: 'Fresh Basil', benefit1: 'Polyphenols & Aroma', emoji: '🌿', pos: '0.04 0.04 -0.09' }
      ]
    },
    {
      id: 'wagyu-truffle-burger',
      title: 'Classic Oak Truffle Burger',
      category: 'Burgers',
      dietary: 'NON_VEG',
      price: '$24.50',
      calories: '🔥 820 kcal • Prep 10m',
      dimensions: 'Ø 14cm × H 12cm',
      description: 'Two seared Wagyu patties, aged Gruyère cheese, black truffle emulsion, caramelized shallots, and toasted Hokkaido milk brioche bun.',
      glbSrc: 'models/burger.glb',
      usdzSrc: 'models/burger.usdz',
      posterSrc: 'images/burger.jpg',
      allergens: ['Gluten', 'Dairy', 'Sesame'],
      cameraOrbit: '25deg 72deg 0.42m',
      hotspots: [
        { name: 'Wagyu Patty', benefit1: 'High B12 & Zinc', emoji: '🥩', pos: '0.0 0.04 0.0' },
        { name: 'Aged Gruyère', benefit1: 'Rich Dairy Calcium', emoji: '🧀', pos: '0.05 0.06 0.02' },
        { name: 'Brioche Bun', benefit1: 'Hand-crafted Daily', emoji: '🍞', pos: '0.0 0.11 0.0' }
      ]
    },
    {
      id: 'burrata-prosciutto',
      title: 'Burrata with Prosciutto di Parma',
      category: 'Starters',
      dietary: 'NON_VEG',
      price: '$21.00',
      calories: '🔥 520 kcal • Prep 6m',
      dimensions: 'Ø 22cm × H 6cm',
      description: 'Imported 24-month aged Prosciutto di Parma draped over fresh whole Italian burrata, heirloom baby tomatoes, aged balsamic reduction, and grilled ciabatta.',
      glbSrc: 'models/pizza.glb',
      usdzSrc: 'models/pizza.usdz',
      posterSrc: 'images/pizza.jpg',
      allergens: ['Dairy', 'Gluten-Optional'],
      cameraOrbit: '10deg 60deg 0.48m',
      hotspots: [
        { name: 'Prosciutto 24m', benefit1: 'Artisanal Cured Protein', emoji: '🥓', pos: '-0.07 0.04 0.04' },
        { name: 'Whole Burrata', benefit1: 'Creamy Stracciatella Center', emoji: '🧀', pos: '0.0 0.05 0.0' }
      ]
    },
    {
      id: 'black-garlic-ramen',
      title: 'Kurobuta Tonkotsu Ramen',
      category: 'Main Course',
      dietary: 'NON_VEG',
      price: '$22.50',
      calories: '🔥 780 kcal • Prep 11m',
      dimensions: 'Ø 22cm × H 10cm',
      description: '18-hour simmered pork bone broth infused with roasted black garlic mayu, torch-seared chashu pork belly, 6-minute ajitsuke egg, and house alkaline noodles.',
      glbSrc: 'models/ramen.glb',
      usdzSrc: 'models/ramen.usdz',
      posterSrc: 'images/ramen.jpg',
      allergens: ['Soy', 'Egg', 'Wheat'],
      cameraOrbit: '0deg 65deg 0.45m',
      hotspots: [
        { name: 'Tonkotsu Collagen', benefit1: 'Rich Bone Marrow Nutrients', emoji: '🍲', pos: '0.0 0.02 0.0' },
        { name: 'Chashu Belly', benefit1: 'Seared Melty Protein', emoji: '🥩', pos: '-0.05 0.05 0.03' },
        { name: 'Ajitsuke Egg', benefit1: 'Jammy 6-min Yolk', emoji: '🥚', pos: '0.05 0.04 -0.04' }
      ]
    },
    {
      id: 'uji-matcha-mille-crepe',
      title: 'Ceremonial Matcha Mille Crêpe',
      category: 'Desserts',
      dietary: 'VEG',
      price: '$16.50',
      calories: '🔥 460 kcal • Chilled',
      dimensions: 'Ø 16cm × H 7.5cm',
      description: 'Twenty paper-thin French crêpe layers brushed with ceremonial-grade Uji matcha diplomate cream, topped with Hokkaido chantilly and edible 24K gold flakes.',
      glbSrc: 'models/matcha_cake.glb',
      usdzSrc: 'models/matcha_cake.usdz',
      posterSrc: 'images/hero_bistro.jpg',
      allergens: ['Dairy', 'Egg', 'Gluten'],
      cameraOrbit: '-20deg 70deg 0.38m',
      hotspots: [
        { name: 'Uji Matcha', benefit1: 'EGCG Antioxidant Boost', emoji: '🍵', pos: '0.0 0.08 0.0' },
        { name: '20 Mille Layers', benefit1: 'Silky Diplomate Cream', emoji: '🍰', pos: '0.04 0.04 0.04' }
      ]
    }
  ];

  const CATEGORIES = ['All Dishes', 'Starters', 'Main Course', 'Burgers', 'Pizza & Italian', 'Salads & Bowls', 'Desserts'];

  // State
  let activeCategory = 'All Dishes';
  let activeDietary = 'ALL';
  let activeDish = MENU_ITEMS[0];
  let autoRotate = true;
  let showHotspots = true;
  let showChefMascot = true;
  let lightingPresetIndex = 0;

  const LIGHTING_PRESETS = [
    { name: 'Warm Bistro', exposure: 1.05 },
    { name: 'Studio Daylight', exposure: 1.25 },
    { name: 'Candlelight', exposure: 0.88 }
  ];

  // DOM References
  const categoryBar = document.getElementById('category-bar');
  const menuGrid = document.getElementById('menu-grid');
  const arModal = document.getElementById('ar-modal');
  const modalBackdrop = document.getElementById('modal-backdrop');
  const modalCloseBtn = document.getElementById('modal-close-btn');
  const modelViewer = document.getElementById('dish-model-viewer');
  const modelHotspotsContainer = document.getElementById('model-hotspots-container');
  const fallbackCanvas = document.getElementById('fallback-3d-canvas');
  const fallbackBanner = document.getElementById('fallback-banner');
  const arCapabilityText = document.getElementById('ar-capability-text');

  // Preload cache for parsed .glb meshes
  const glbMeshCache = new Map();

  // 2. Detect WebAR Capabilities
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
      arCapabilityText.textContent = '4D WebXR & Scene Viewer';
    } else if (isIOS) {
      arCapabilityText.textContent = 'iOS AR Quick Look (.usdz)';
    } else if (isAndroid) {
      arCapabilityText.textContent = 'Android Scene Viewer (.glb)';
    } else {
      arCapabilityText.textContent = '4D Orbital 3D Turntable';
    }
  }

  // 3. Dietary Filter Buttons
  function setupDietaryFilters() {
    const btns = document.querySelectorAll('.dietary-btn');
    btns.forEach((btn) => {
      btn.addEventListener('click', () => {
        btns.forEach((b) => b.classList.remove('active'));
        btn.classList.add('active');
        activeDietary = btn.getAttribute('data-diet') || 'ALL';
        renderMenuGrid();
      });
    });
  }

  // 4. Render Category Tabs
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

  // 5. Render Menu Cards (Matching Golden Oak UI from Video 4)
  function renderMenuGrid() {
    let filtered = MENU_ITEMS;
    if (activeCategory !== 'All Dishes') {
      filtered = filtered.filter((item) => item.category === activeCategory);
    }
    if (activeDietary !== 'ALL') {
      filtered = filtered.filter((item) => item.dietary === activeDietary);
    }

    const frag = document.createDocumentFragment();
    filtered.forEach((item) => {
      const isVeg = item.dietary === 'VEG';
      const card = document.createElement('article');
      card.className = 'dish-card';
      card.innerHTML = `
        <div class="dish-thumb-wrap">
          <img src="${item.posterSrc}" alt="${item.title}" class="dish-thumb" loading="lazy" />
          <span class="dietary-tag ${isVeg ? 'veg' : 'nonveg'}" style="position:absolute; top:12px; left:12px; z-index:4;">
            ${isVeg ? '🟢 VEG' : '🔴 NON-VEG'}
          </span>
          <span class="dish-3d-badge">${item.calories.split('•')[0]}</span>
        </div>
        <div class="dish-body">
          <div class="dish-header-row">
            <h3 class="dish-title">${item.title}</h3>
            <span class="dish-price">${item.price}</span>
          </div>
          <p class="dish-desc">${item.description}</p>
          <div class="dish-meta-tags">
            <span class="meta-tag">🥑 ${item.hotspots ? item.hotspots.length : 3} Holograms</span>
            <span class="meta-tag">${item.dimensions}</span>
          </div>
          <button type="button" class="btn-card-ar" data-dish-id="${item.id}">
            <span>⬡</span> VIEW ON YOUR TABLE (4D AR)
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

  // 6. Binary glTF 2.0 (.glb) Parser & Preloader
  async function preloadGlbAsset(glbUrl) {
    if (glbMeshCache.has(glbUrl)) {
      return glbMeshCache.get(glbUrl);
    }
    try {
      const response = await fetch(glbUrl);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const buffer = await response.arrayBuffer();
      const parsed = parseGlbBinary(buffer);
      glbMeshCache.set(glbUrl, parsed);
      return parsed;
    } catch (err) {
      console.warn('Failed to parse .glb binary, using procedural geometry:', err);
      const fallback = generateProceduralDish(glbUrl);
      glbMeshCache.set(glbUrl, fallback);
      return fallback;
    }
  }

  function parseGlbBinary(arrayBuffer) {
    const dataView = new DataView(arrayBuffer);
    const magic = dataView.getUint32(0, true);
    if (magic !== 0x46546C67) { // 'glTF'
      throw new Error('Not valid glTF binary');
    }
    const jsonChunkLength = dataView.getUint32(12, true);
    const jsonBytes = new Uint8Array(arrayBuffer, 20, jsonChunkLength);
    const jsonString = new TextDecoder('utf-8').decode(jsonBytes);
    const gltfJson = JSON.parse(jsonString);

    const binChunkOffset = 20 + jsonChunkLength;
    const binChunkLength = dataView.getUint32(binChunkOffset, true);
    const binData = new Uint8Array(arrayBuffer, binChunkOffset + 8, binChunkLength);

    const primitivesOut = [];
    if (gltfJson.meshes) {
      gltfJson.meshes.forEach((mesh) => {
        mesh.primitives.forEach((prim) => {
          if (prim.attributes && prim.attributes.POSITION !== undefined) {
            const accessor = gltfJson.accessors[prim.attributes.POSITION];
            const bufferView = gltfJson.bufferViews[accessor.bufferView];
            const byteOffset = (bufferView.byteOffset || 0) + (accessor.byteOffset || 0);
            const count = accessor.count;
            const float32Array = new Float32Array(binData.buffer, binData.byteOffset + byteOffset, count * 3);
            primitivesOut.push({
              positions: float32Array,
              count: count
            });
          }
        });
      });
    }

    if (primitivesOut.length === 0) {
      return generateProceduralDish('parsed');
    }
    return primitivesOut;
  }

  function generateProceduralDish(name) {
    const positions = [];
    const segments = 24;
    for (let i = 0; i < segments; i++) {
      const a1 = (i / segments) * Math.PI * 2;
      const a2 = ((i + 1) / segments) * Math.PI * 2;
      positions.push(0, 0, 0);
      positions.push(Math.cos(a1) * 0.12, 0.02, Math.sin(a1) * 0.12);
      positions.push(Math.cos(a2) * 0.12, 0.02, Math.sin(a2) * 0.12);

      positions.push(Math.cos(a1) * 0.08, 0.06, Math.sin(a1) * 0.08);
      positions.push(Math.cos(a2) * 0.08, 0.06, Math.sin(a2) * 0.08);
      positions.push(0, 0.08, 0);
    }
    return [{ positions: new Float32Array(positions), count: positions.length / 3 }];
  }

  // 7. Open Dish AR Modal & Synchronize Holographic Hotspots
  async function openDishARModal(dish) {
    activeDish = dish;
    const isVeg = dish.dietary === 'VEG';
    const dietTag = document.getElementById('modal-dish-dietary');
    dietTag.className = 'dietary-tag ' + (isVeg ? 'veg' : 'nonveg');
    dietTag.textContent = isVeg ? '🟢 VEG' : '🔴 NON-VEG';

    document.getElementById('modal-dish-category').textContent = dish.category.toUpperCase();
    document.getElementById('modal-dish-title').textContent = dish.title;
    document.getElementById('modal-dish-price').textContent = dish.price;
    document.getElementById('modal-dish-calories').textContent = dish.calories;
    document.getElementById('modal-dish-desc').textContent = dish.description;
    document.getElementById('hud-dimensions').textContent = dish.dimensions;

    // Render summary cards below description
    const previewContainer = document.getElementById('modal-hotspot-cards');
    if (dish.hotspots && dish.hotspots.length > 0) {
      previewContainer.innerHTML = dish.hotspots.map((h) => `
        <div class="hologram-card">
          <div class="card-title">${h.emoji} ${h.name}</div>
          <div class="card-benefit">${h.benefit1}</div>
        </div>
      `).join('');
    } else {
      previewContainer.innerHTML = '';
    }

    // Configure <model-viewer>
    modelViewer.setAttribute('src', dish.glbSrc);
    modelViewer.setAttribute('ios-src', dish.usdzSrc);
    modelViewer.setAttribute('poster', dish.posterSrc);
    modelViewer.setAttribute('alt', `4D model of ${dish.title}`);
    modelViewer.setAttribute('camera-orbit', dish.cameraOrbit);

    // Inject Floating Hotspots into <model-viewer> (Matching Video 4)
    modelHotspotsContainer.innerHTML = '';
    if (showHotspots && dish.hotspots) {
      dish.hotspots.forEach((h, idx) => {
        const btn = document.createElement('button');
        btn.className = 'ar-hotspot-slot';
        btn.setAttribute('slot', `hotspot-${idx}`);
        btn.setAttribute('data-position', h.pos);
        btn.setAttribute('data-normal', '0 1 0');
        btn.innerHTML = `
          <span class="hotspot-title">${h.emoji} ${h.name}</span>
          <span class="hotspot-sub">${h.benefit1}</span>
        `;
        modelHotspotsContainer.appendChild(btn);
      });
    }

    arModal.classList.add('open');
    arModal.setAttribute('aria-hidden', 'false');

    // Launch fallback turntable
    const primitives = await preloadGlbAsset(dish.glbSrc);
    startOrbitalTurntable(primitives);
  }

  function closeDishARModal() {
    arModal.classList.remove('open');
    arModal.setAttribute('aria-hidden', 'true');
    stopOrbitalTurntable();
  }

  // 8. Interactive 3D Turntable Fallback with Chef Mascot
  let animFrameId = null;
  let yaw = 0.5;
  let pitch = 0.42;
  let isDragging = false;
  let lastX = 0;
  let lastY = 0;
  let currentPrimitives = null;

  function startOrbitalTurntable(primitives) {
    currentPrimitives = primitives;
    fallbackBanner.hidden = false;
    fallbackCanvas.style.opacity = '1';

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

      // Draw table plane radar rings
      drawSpatialTableGrid(ctx, w, h, dpr);

      // Draw mesh geometry
      if (currentPrimitives && currentPrimitives.length > 0) {
        drawGlbPrimitives(ctx, w, h, dpr, currentPrimitives);
      }

      // Draw 3D Chef Mascot if enabled (Matching Video 1 & 2)
      if (showChefMascot) {
        drawChefMascot(ctx, w, h, dpr);
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
    const cy = h * 0.65;
    ctx.save();
    ctx.strokeStyle = 'rgba(229, 169, 60, 0.28)';
    ctx.lineWidth = 1.2 * dpr;

    for (let r = 50 * dpr; r <= 150 * dpr; r += 50 * dpr) {
      ctx.beginPath();
      ctx.ellipse(cx, cy, r, r * 0.38, 0, 0, Math.PI * 2);
      ctx.stroke();
    }
    ctx.restore();
  }

  function drawGlbPrimitives(ctx, w, h, dpr, primitives) {
    const cx = w * 0.5;
    const cy = h * 0.62;
    const scale = 850 * dpr;
    const cosY = Math.cos(yaw);
    const sinY = Math.sin(yaw);
    const cosP = Math.cos(pitch);
    const sinP = Math.sin(pitch);

    ctx.save();
    ctx.fillStyle = 'rgba(229, 169, 60, 0.65)';
    ctx.strokeStyle = '#E5A93C';
    ctx.lineWidth = 1 * dpr;

    primitives.forEach((prim) => {
      const pos = prim.positions;
      const step = 9; // Draw sample of triangles
      for (let i = 0; i < pos.length - 8; i += step) {
        const x1 = pos[i], y1 = pos[i + 1], z1 = pos[i + 2];
        const x2 = pos[i + 3], y2 = pos[i + 4], z2 = pos[i + 5];
        const x3 = pos[i + 6], y3 = pos[i + 7], z3 = pos[i + 8];

        function project(x, y, z) {
          const rx = x * cosY - z * sinY;
          const rz = x * sinY + z * cosY;
          const ry = y * cosP - rz * sinP;
          return {
            px: cx + rx * scale,
            py: cy - ry * scale
          };
        }

        const p1 = project(x1, y1, z1);
        const p2 = project(x2, y2, z2);
        const p3 = project(x3, y3, z3);

        ctx.beginPath();
        ctx.moveTo(p1.px, p1.py);
        ctx.lineTo(p2.px, p2.py);
        ctx.lineTo(p3.px, p3.py);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
      }
    });
    ctx.restore();
  }

  function drawChefMascot(ctx, w, h, dpr) {
    const cx = w * 0.5;
    const cy = h * 0.38;

    ctx.save();
    // Mascot Speech Bubble
    ctx.fillStyle = '#1E1712';
    ctx.strokeStyle = '#E5A93C';
    ctx.lineWidth = 1.5 * dpr;
    const bw = 170 * dpr;
    const bh = 34 * dpr;
    const bx = cx - bw * 0.5;
    const by = cy - 70 * dpr;

    ctx.beginPath();
    ctx.roundRect(bx, by, bw, bh, 8 * dpr);
    ctx.fill();
    ctx.stroke();

    ctx.fillStyle = '#FFFFFF';
    ctx.font = `bold ${10 * dpr}px sans-serif`;
    ctx.textAlign = 'center';
    ctx.fillText('👨‍🍳 "Welcome! Enjoy 4D Dining"', cx, by + 21 * dpr);

    // Chef Avatar
    ctx.font = `${32 * dpr}px sans-serif`;
    ctx.fillText('👨‍🍳', cx, cy - 20 * dpr);
    ctx.restore();
  }

  // 9. Input & Control Event Listeners
  function setupControls() {
    // Modal Close
    modalCloseBtn.addEventListener('click', closeDishARModal);
    modalBackdrop.addEventListener('click', closeDishARModal);
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') closeDishARModal();
    });

    // Auto-Rotate Toggle
    const btnRotate = document.getElementById('btn-toggle-rotate');
    btnRotate.addEventListener('click', () => {
      autoRotate = !autoRotate;
      btnRotate.classList.toggle('active', autoRotate);
      if (modelViewer) {
        if (autoRotate) modelViewer.setAttribute('auto-rotate', '');
        else modelViewer.removeAttribute('auto-rotate');
      }
    });

    // Hotspot Toggle
    const btnHotspots = document.getElementById('btn-toggle-hotspots');
    btnHotspots.addEventListener('click', () => {
      showHotspots = !showHotspots;
      btnHotspots.classList.toggle('active', showHotspots);
      modelHotspotsContainer.style.display = showHotspots ? 'block' : 'none';
    });

    // Chef Mascot Toggle
    const btnChef = document.getElementById('btn-toggle-chef');
    btnChef.addEventListener('click', () => {
      showChefMascot = !showChefMascot;
      btnChef.classList.toggle('active', showChefMascot);
      const chefHud = document.getElementById('hud-chef-status');
      chefHud.textContent = showChefMascot ? '👨‍🍳 3D Chef Active' : '👨‍🍳 Chef Hidden';
    });

    // Lighting Toggle
    const btnLighting = document.getElementById('btn-cycle-lighting');
    btnLighting.addEventListener('click', () => {
      lightingPresetIndex = (lightingPresetIndex + 1) % LIGHTING_PRESETS.length;
      const preset = LIGHTING_PRESETS[lightingPresetIndex];
      btnLighting.textContent = `☀ ${preset.name}`;
      if (modelViewer) {
        modelViewer.setAttribute('exposure', String(preset.exposure));
      }
    });

    // Direct Native AR Trigger
    const btnLaunchAr = document.getElementById('btn-launch-ar-direct');
    btnLaunchAr.addEventListener('click', () => {
      if (modelViewer && typeof modelViewer.activateAR === 'function') {
        modelViewer.activateAR();
      } else {
        alert('Launching 1:1 Augmented Reality on table plane.');
      }
    });

    // Add To Order CTA
    const btnAddOrder = document.getElementById('btn-add-order');
    btnAddOrder.addEventListener('click', () => {
      btnAddOrder.textContent = 'ADDED TO ORDER ✓';
      btnAddOrder.style.borderColor = '#4CAF50';
      btnAddOrder.style.color = '#4CAF50';
      setTimeout(() => {
        btnAddOrder.textContent = 'ADD TO ORDER';
        btnAddOrder.style.borderColor = '';
        btnAddOrder.style.color = '';
      }, 1500);
    });

    // Fallback Canvas Touch/Mouse Drag Orbit
    fallbackCanvas.addEventListener('mousedown', (e) => {
      isDragging = true;
      lastX = e.clientX;
      lastY = e.clientY;
    });
    window.addEventListener('mousemove', (e) => {
      if (!isDragging) return;
      const dx = e.clientX - lastX;
      const dy = e.clientY - lastY;
      yaw += dx * 0.01;
      pitch = Math.max(0.1, Math.min(1.4, pitch + dy * 0.01));
      lastX = e.clientX;
      lastY = e.clientY;
    });
    window.addEventListener('mouseup', () => { isDragging = false; });

    fallbackCanvas.addEventListener('touchstart', (e) => {
      if (e.touches.length === 1) {
        isDragging = true;
        lastX = e.touches[0].clientX;
        lastY = e.touches[0].clientY;
      }
    }, { passive: true });

    fallbackCanvas.addEventListener('touchmove', (e) => {
      if (!isDragging || e.touches.length !== 1) return;
      const dx = e.touches[0].clientX - lastX;
      const dy = e.touches[0].clientY - lastY;
      yaw += dx * 0.015;
      pitch = Math.max(0.1, Math.min(1.4, pitch + dy * 0.015));
      lastX = e.touches[0].clientX;
      lastY = e.touches[0].clientY;
    }, { passive: true });

    window.addEventListener('touchend', () => { isDragging = false; });
  }

  // Init
  window.addEventListener('DOMContentLoaded', () => {
    detectARCapabilities();
    setupDietaryFilters();
    renderCategories();
    renderMenuGrid();
    setupControls();
  });
})();
