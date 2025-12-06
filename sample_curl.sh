curl -X POST http://localhost:8080/api/optimize \
-H "Content-Type: application/json" \
-d '{
  "depot": {
    "latitude": 17.4717695,
    "longitude": 78.3440312
  },
  "trucks": [
    {
      "id": "Tata_Ace_1_SHIFT_1_ROUTE_1",
      "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
      "weightCapacity": 600,
      "lengthCapacity": 200,
      "widthCapacity": 100,
      "heightCapacity": 5,
      "costPerKm": 1,
      "costPerHour": 1,
      "fixedCost": 100,
      "speedKmH": 20.0,
      "maxStops": 0,
      "availableFrom": 360,
      "availableTo": 720
    },
    {
      "id": "Tata_Ace_2_SHIFT_1_ROUTE_1",
      "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
      "weightCapacity": 600,
      "lengthCapacity": 200,
      "widthCapacity": 100,
      "heightCapacity": 5,
      "costPerKm": 2,
      "costPerHour": 2,
      "fixedCost": 200,
      "speedKmH": 20.0,
      "maxStops": 0,
      "availableFrom": 360,
      "availableTo": 720
    },
    {
      "id": "Tata_Ace_3_SHIFT_1_ROUTE_1",
      "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
      "weightCapacity": 600,
      "lengthCapacity": 200,
      "widthCapacity": 100,
      "heightCapacity": 12,
      "costPerKm": 3,
      "costPerHour": 3,
      "fixedCost": 300,
      "speedKmH": 20.0,
      "maxStops": 0,
      "availableFrom": 360,
      "availableTo": 720
    },
    {
      "id": "Tata_Ace_4_SHIFT_1_ROUTE_1",
      "startLocation": { "latitude": 17.4717695, "longitude": 78.3440312 },
      "weightCapacity": 600,
      "lengthCapacity": 200,
      "widthCapacity": 100,
      "heightCapacity": 12,
      "costPerKm": 4,
      "costPerHour": 4,
      "fixedCost": 400,
      "speedKmH": 20.0,
      "maxStops": 0,
      "availableFrom": 360,
      "availableTo": 720
    }
  ],
  "orders": [
    {
      "id": "769803_24338976",
      "location": { "latitude": 17.4960659, "longitude": 78.38883117 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 2,
      "width": 1,
      "height": 1,
      "weight": 30
    },
    {
      "id": "93442_24338342",
      "location": { "latitude": 17.4494298, "longitude": 78.41358125 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 2,
      "width": 1,
      "height": 1,
      "weight": 32
    },
    {
      "id": "68354_24338083",
      "location": { "latitude": 17.48833988, "longitude": 78.3792768 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 2,
      "width": 1,
      "height": 1,
      "weight": 20
    },
    {
      "id": "171270_24335909",
      "location": { "latitude": 17.49027197, "longitude": 78.32435623 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 570,
      "length": 1,
      "width": 1,
      "height": 1,
      "weight": 20
    },
    {
      "id": "390147_24338428",
      "location": { "latitude": 17.44973334, "longitude": 78.37032661 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 3,
      "width": 1,
      "height": 1,
      "weight": 49
    },
    {
      "id": "769307_24340220",
      "location": { "latitude": 17.47850513, "longitude": 78.36319596 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 3,
      "width": 1,
      "height": 1,
      "weight": 47
    },
    {
      "id": "40979_24338858",
      "location": { "latitude": 17.4536866, "longitude": 78.39376643 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 4,
      "width": 1,
      "height": 1,
      "weight": 43
    },
    {
      "id": "771864_24337431",
      "location": { "latitude": 17.401345, "longitude": 78.3868657 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 570,
      "length": 1,
      "width": 1,
      "height": 1,
      "weight": 20
    },
    {
      "id": "95765_24338036",
      "location": { "latitude": 17.46699817, "longitude": 78.37885503 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 1,
      "width": 1,
      "height": 1,
      "weight": 20
    },
    {
      "id": "23061_24339083",
      "location": { "latitude": 17.45510221, "longitude": 78.40253122 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 2,
      "width": 1,
      "height": 1,
      "weight": 27
    },
    {
      "id": "23322_24340087",
      "location": { "latitude": 17.51678743, "longitude": 78.33854612 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 1,
      "width": 1,
      "height": 1,
      "weight": 20
    },
    {
      "id": "420124_24338683",
      "location": { "latitude": 17.458873, "longitude": 78.4004573 },
      "serviceTime": 8,
      "timeWindowStart": 390,
      "timeWindowEnd": 510,
      "length": 1,
      "width": 1,
      "height": 1,
      "weight": 22
    },
    {
      "id": "637204_24341046",
      "location": { "latitude": 17.45387787, "longitude": 78.39938968 },
      "serviceTime": 8,
      "timeWindowStart": 360,
      "timeWindowEnd": 720,
      "length": 2,
      "width": 1,
      "height": 1,
      "weight": 22
    }
  ]
}'
