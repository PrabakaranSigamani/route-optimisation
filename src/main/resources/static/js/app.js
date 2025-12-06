let map;
let routeLayers = [];
const TRUCK_COLORS = [
    '#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231',
    '#911eb4', '#46f0f0', '#f032e6', '#bcf60c', '#fabebe',
    '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000',
    '#aaffc3', '#808000', '#ffd8b1', '#000075', '#808080'
];

document.addEventListener('DOMContentLoaded', () => {
    initMap();
    loadDefaultData();
    document.getElementById('optimizeBtn').addEventListener('click', optimizeRoutes);
});

function initMap() {
    // Initialize map centered on Hyderabad (based on sample data)
    map = L.map('map').setView([17.4717695, 78.3440312], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
}

function loadDefaultData() {
    const defaultData = {
        "depot": { "latitude": 17.4717695, "longitude": 78.3440312 },
        "trucks": [
            {
                "id": "Tata_Ace_1_SHIFT_1_ROUTE_1",
                "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
                "weightCapacity": 600, "lengthCapacity": 200, "widthCapacity": 100, "heightCapacity": 12,
                "costPerKm": 1, "costPerHour": 1, "fixedCost": 100, "speedKmH": 20.0, "maxStops": 0, "availableFrom": 360, "availableTo": 720
            },
            {
                "id": "Tata_Ace_2_SHIFT_1_ROUTE_1",
                "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
                "weightCapacity": 600, "lengthCapacity": 200, "widthCapacity": 100, "heightCapacity": 12,
                "costPerKm": 2, "costPerHour": 2, "fixedCost": 200, "speedKmH": 20.0, "maxStops": 0, "availableFrom": 360, "availableTo": 720
            }
        ],
        "orders": [
            { "id": "769803_24338976", "orderId": "769803_24338976", "location": { "latitude": 17.4960659, "longitude": 78.38883117 }, "serviceTime": 8, "weight": 30, "length": 2, "width": 1, "height": 1 },
            { "id": "93442_24338342", "orderId": "93442_24338342", "location": { "latitude": 17.4494298, "longitude": 78.41358125 }, "serviceTime": 8, "weight": 32, "length": 2, "width": 1, "height": 1 },
            { "id": "68354_24338083", "orderId": "68354_24338083", "location": { "latitude": 17.48833988, "longitude": 78.3792768 }, "serviceTime": 8, "weight": 20, "length": 2, "width": 1, "height": 1 },
            { "id": "171270_24335909", "orderId": "171270_24335909", "location": { "latitude": 17.49027197, "longitude": 78.32435623 }, "serviceTime": 8, "weight": 20, "length": 1, "width": 1, "height": 1 },
            { "id": "390147_24338428", "orderId": "390147_24338428", "location": { "latitude": 17.44973334, "longitude": 78.37032661 }, "serviceTime": 8, "weight": 49, "length": 3, "width": 1, "height": 1 },
            { "id": "769307_24340220", "orderId": "769307_24340220", "location": { "latitude": 17.47850513, "longitude": 78.36319596 }, "serviceTime": 8, "weight": 47, "length": 3, "width": 1, "height": 1 },
            { "id": "40979_24338858", "orderId": "40979_24338858", "location": { "latitude": 17.4536866, "longitude": 78.39376643 }, "serviceTime": 8, "weight": 43, "length": 4, "width": 1, "height": 1 },
            { "id": "771864_24337431", "orderId": "771864_24337431", "location": { "latitude": 17.401345, "longitude": 78.3868657 }, "serviceTime": 8, "weight": 20, "length": 1, "width": 1, "height": 1 },
            { "id": "95765_24338036", "orderId": "95765_24338036", "location": { "latitude": 17.46699817, "longitude": 78.37885503 }, "serviceTime": 8, "weight": 20, "length": 1, "width": 1, "height": 1 },
            { "id": "23061_24339083", "orderId": "23061_24339083", "location": { "latitude": 17.45510221, "longitude": 78.40253122 }, "serviceTime": 8, "weight": 27, "length": 2, "width": 1, "height": 1 },
            { "id": "23322_24340087", "orderId": "23322_24340087", "location": { "latitude": 17.51678743, "longitude": 78.33854612 }, "serviceTime": 8, "weight": 20, "length": 1, "width": 1, "height": 1 },
            { "id": "420124_24338683", "orderId": "420124_24338683", "location": { "latitude": 17.458873, "longitude": 78.4004573 }, "serviceTime": 8, "weight": 22, "length": 1, "width": 1, "height": 1 },
            { "id": "637204_24341046", "orderId": "637204_24341046", "location": { "latitude": 17.45387787, "longitude": 78.39938968 }, "serviceTime": 8, "weight": 22, "length": 2, "width": 1, "height": 1 }
        ]
    };
    document.getElementById('jsonInput').value = JSON.stringify(defaultData, null, 2);
}

async function optimizeRoutes() {
    const btn = document.getElementById('optimizeBtn');
    const input = document.getElementById('jsonInput').value;
    const resultsDiv = document.getElementById('results');

    // Reset
    btn.disabled = true;
    btn.textContent = 'Optimizing...';
    resultsDiv.innerHTML = '';
    clearMap();

    try {
        const payload = JSON.parse(input);

        // Lookup maps for coordinates
        const locationMap = new Map();
        payload.orders.forEach(o => locationMap.set(o.id, o.location));

        const truckStartMap = new Map();
        payload.trucks.forEach(t => truckStartMap.set(t.id, t.startLocation));

        const depot = payload.depot;

        const response = await fetch('/api/optimize', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: input
        });

        if (!response.ok) throw new Error('Network response was not ok');

        const data = await response.json();

        const truckColorMap = new Map();
        let colorIndex = 0;
        data.routes.forEach(r => {
            truckColorMap.set(r.truckId, TRUCK_COLORS[colorIndex++ % TRUCK_COLORS.length]);
        });

        // Display filtered results
        displayResults(data, truckColorMap);

        // Render
        await renderRoutesOnMap(data, locationMap, truckStartMap, depot, truckColorMap);

    } catch (error) {
        resultsDiv.innerHTML = `<div class="error">Error: ${error.message}</div>`;
        console.error(error);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Optimize';
    }
}

function clearMap() {
    routeLayers.forEach(layer => map.removeLayer(layer));
    routeLayers = [];
}

function displayResults(data, truckColorMap) {
    const div = document.getElementById('results');
    div.innerHTML = `
        <h3>Results</h3>
        <div>Total Distance: <span class="metric">${data.totalDistance.toFixed(2)} km</span></div>
        <div>Total Time: <span class="metric">${data.totalTime} mins</span></div>
        <div>Total Cost: <span class="metric">${data.totalCost ? data.totalCost.toFixed(2) : '0.00'}</span></div>
        
        ${data.unassignedOrders && data.unassignedOrders.length > 0 ? `
            <div class="error">
                <strong>Unassigned Orders (${data.unassignedOrders.length}):</strong><br/>
                ${data.unassignedOrders.join(', ')}
            </div>
        ` : ''}
        
        <hr/>
        <div id="route-list">
            ${data.routes.map((r, i) => `
                <div class="route-item" id="route-item-${r.truckId}" 
                     style="border-left-color: ${truckColorMap.get(r.truckId)}; cursor: pointer;"
                     onclick="toggleRoute('${r.truckId}')">
                    <input type="checkbox" checked id="cb-${r.truckId}" onclick="event.stopPropagation(); toggleRoute('${r.truckId}')"/>
                    <strong>${r.truckId}</strong><br/>
                    Stops: ${r.orderSequence.length}<br/>
                    Dist: ${r.distance.toFixed(2)} km<br/>
                    Time: ${r.time} mins<br/>
                    Cost: ${r.cost ? r.cost.toFixed(2) : '0.00'}
                </div>
            `).join('')}
        </div>
    `;
}

// Global map for toggling layers
let truckLayersMap = new Map();

async function renderRoutesOnMap(data, orderLocMap, truckStartMap, depot, truckColorMap) {
    truckLayersMap.clear();

    // Plot Depot
    const depotMarker = L.marker([depot.latitude, depot.longitude]).addTo(map)
        .bindPopup("<strong>Depot</strong>");
    routeLayers.push(depotMarker);

    for (const route of data.routes) {
        if (route.orderSequence.length === 0) continue;

        const color = truckColorMap.get(route.truckId);
        const layersForTruck = L.layerGroup().addTo(map); // Group for this truck
        truckLayersMap.set(route.truckId, layersForTruck);
        routeLayers.push(layersForTruck);

        const startLoc = truckStartMap.get(route.truckId) || depot;

        // --- 1. Markers (Numbered) ---
        let stopNumber = 1;
        route.orderSequence.forEach(orderId => {
            const loc = orderLocMap.get(orderId);
            if (loc) {
                // Numbered Icon
                const numIcon = L.divIcon({
                    className: 'custom-div-icon',
                    html: `<div style="background-color:${color}; width:24px; height:24px; border-radius:12px; border:2px solid white; color:white; text-align:center; font-weight:bold; line-height:20px;">${stopNumber++}</div>`,
                    iconSize: [24, 24],
                    iconAnchor: [12, 12]
                });

                const marker = L.marker([loc.latitude, loc.longitude], { icon: numIcon })
                    .bindPopup(`<strong>Stop ${stopNumber - 1}</strong><br/>Order: ${orderId}<br/>Truck: ${route.truckId}`);
                layersForTruck.addLayer(marker);
            }
        });

        // --- 2. Route Geometry (OSRM) ---
        // Construct coordinates string: lon,lat;lon,lat;...
        // Start -> Orders -> Start (implying return)
        const waypoints = [];
        waypoints.push([startLoc.longitude, startLoc.latitude]); // OSRM uses lon,lat
        route.orderSequence.forEach(orderId => {
            const loc = orderLocMap.get(orderId);
            if (loc) waypoints.push([loc.longitude, loc.latitude]);
        });
        waypoints.push([startLoc.longitude, startLoc.latitude]); // End at start

        // Fetch Geometry
        // OSRM URL: http://router.project-osrm.org/route/v1/driving/{coords}?overview=full&geometries=geojson
        const coordsString = waypoints.map(p => p.join(',')).join(';');
        const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${coordsString}?overview=full&geometries=geojson`;

        try {
            const resp = await fetch(osrmUrl);
            const routeJson = await resp.json();
            if (routeJson.code === 'Ok' && routeJson.routes.length > 0) {
                const geometry = routeJson.routes[0].geometry;
                const polyline = L.geoJSON(geometry, {
                    style: { color: color, weight: 5, opacity: 0.7 }
                });
                layersForTruck.addLayer(polyline);
            } else {
                console.warn('OSRM routing failed, falling back to straight lines for', route.truckId);
                const latlngs = waypoints.map(p => [p[1], p[0]]); // Swap back to lat,lon
                const polyline = L.polyline(latlngs, { color: color, weight: 4, dashArray: '5, 10' });
                layersForTruck.addLayer(polyline);
            }
        } catch (e) {
            console.error('Failed to fetch OSRM geometry', e);
            const latlngs = waypoints.map(p => [p[1], p[0]]); // Swap back to lat,lon
            const polyline = L.polyline(latlngs, { color: color, weight: 4, dashArray: '5, 10' });
            layersForTruck.addLayer(polyline);
        }
    }

    // Plot Unassigned Orders
    if (data.unassignedOrders && data.unassignedOrders.length > 0) {
        data.unassignedOrders.forEach(orderId => {
            const loc = orderLocMap.get(orderId);
            if (loc) {
                const marker = L.circleMarker([loc.latitude, loc.longitude], {
                    radius: 8,
                    fillColor: '#dc3545', // Red
                    color: '#000',
                    weight: 1,
                    opacity: 1,
                    fillOpacity: 0.8
                }).addTo(map).bindPopup(`<strong>Unassigned</strong><br/>Order: ${orderId}`);
                routeLayers.push(marker);
            }
        });
    }
}

function toggleRoute(truckId) {
    const layer = truckLayersMap.get(truckId);
    const cb = document.getElementById(`cb-${truckId}`);
    const row = document.getElementById(`route-item-${truckId}`);

    if (layer) {
        if (map.hasLayer(layer)) {
            map.removeLayer(layer);
            if (cb) cb.checked = false;
            if (row) row.style.opacity = '0.5';
        } else {
            map.addLayer(layer);
            if (cb) cb.checked = true;
            if (row) row.style.opacity = '1';
        }
    }
}
