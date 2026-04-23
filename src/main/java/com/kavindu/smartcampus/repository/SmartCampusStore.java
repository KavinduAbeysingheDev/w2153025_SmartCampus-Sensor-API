package com.kavindu.smartcampus.repository;

import com.kavindu.smartcampus.exception.DuplicateResourceException;
import com.kavindu.smartcampus.exception.LinkedResourceNotFoundException;
import com.kavindu.smartcampus.exception.ResourceNotFoundException;
import com.kavindu.smartcampus.exception.RoomNotEmptyException;
import com.kavindu.smartcampus.exception.SensorUnavailableException;
import com.kavindu.smartcampus.model.PersistedState;
import com.kavindu.smartcampus.model.Room;
import com.kavindu.smartcampus.model.Sensor;
import com.kavindu.smartcampus.model.SensorReading;
import com.kavindu.smartcampus.model.StatsResponse;
import com.kavindu.smartcampus.util.InputValidator;
import com.kavindu.smartcampus.util.SnapshotManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SmartCampusStore {

    private static final SmartCampusStore INSTANCE = new SmartCampusStore();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readingsBySensor = new ConcurrentHashMap<>();
    private final SnapshotManager snapshotManager = new SnapshotManager();

    private SmartCampusStore() {
        loadOrSeed();
    }

    public static SmartCampusStore getInstance() {
        return INSTANCE;
    }

    public List<Room> getRooms() {
        return rooms.values().stream()
                .map(Room::new)
                .sorted(Comparator.comparing(Room::getId))
                .collect(Collectors.toList());
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room " + roomId + " was not found.");
        }
        return new Room(room);
    }

    public synchronized Room addRoom(Room room) {
        String id = InputValidator.required(room.getId(), "id");
        String name = InputValidator.required(room.getName(), "name");
        InputValidator.positiveCapacity(room.getCapacity());

        if (rooms.containsKey(id)) {
            throw new DuplicateResourceException("Room " + id + " already exists.");
        }

        Room stored = new Room(id, name, room.getCapacity());
        rooms.put(id, stored);
        persistQuietly();
        return new Room(stored);
    }

    public synchronized void deleteRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room " + roomId + " was not found.");
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because sensors are still assigned to it.");
        }

        rooms.remove(roomId);
        persistQuietly();
    }

    public List<Sensor> getSensors(String typeFilter, String roomIdFilter, String statusFilter) {
        return sensors.values().stream()
                .filter(sensor -> typeFilter == null || sensor.getType().equalsIgnoreCase(typeFilter))
                .filter(sensor -> roomIdFilter == null || sensor.getRoomId().equalsIgnoreCase(roomIdFilter))
                .filter(sensor -> statusFilter == null || sensor.getStatus().equalsIgnoreCase(statusFilter))
                .map(Sensor::new)
                .sorted(Comparator.comparing(Sensor::getId))
                .collect(Collectors.toList());
    }

    public Sensor getSensor(String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor " + sensorId + " was not found.");
        }
        return new Sensor(sensor);
    }

    public synchronized Sensor addSensor(Sensor sensor) {
        String id = InputValidator.required(sensor.getId(), "id");
        String type = InputValidator.normalizeType(sensor.getType());
        String status = InputValidator.normalizeStatus(sensor.getStatus());
        String roomId = InputValidator.required(sensor.getRoomId(), "roomId");

        Room room = rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Cannot create sensor because referenced room " + roomId + " does not exist.");
        }

        if (sensors.containsKey(id)) {
            throw new DuplicateResourceException("Sensor " + id + " already exists.");
        }

        Sensor stored = new Sensor(id, type, status, sensor.getCurrentValue(), roomId);
        sensors.put(id, stored);
        readingsBySensor.putIfAbsent(id, new ArrayList<>());
        room.getSensorIds().add(id);

        persistQuietly();
        return new Sensor(stored);
    }

    public List<SensorReading> getReadings(String sensorId) {
        ensureSensorExists(sensorId);

        List<SensorReading> readings = readingsBySensor.getOrDefault(sensorId, new ArrayList<>());
        return readings.stream()
                .map(SensorReading::new)
                .sorted(Comparator.comparingLong(SensorReading::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public synchronized SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor " + sensorId + " was not found.");
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is not available for new readings while status is " + sensor.getStatus() + ".");
        }

        String id = reading.getId() == null || reading.getId().trim().isEmpty()
                ? UUID.randomUUID().toString()
                : reading.getId().trim();

        SensorReading stored = new SensorReading(id, InputValidator.normalizeTimestamp(reading.getTimestamp()), reading.getValue());

        readingsBySensor.computeIfAbsent(sensorId, key -> new ArrayList<>()).add(stored);
        sensor.setCurrentValue(stored.getValue());

        persistQuietly();
        return new SensorReading(stored);
    }

    public synchronized void resetToSeedData() {
        rooms.clear();
        sensors.clear();
        readingsBySensor.clear();
        seedData();
        persistQuietly();
    }

    public StatsResponse getStats() {
        int readingCount = readingsBySensor.values().stream().mapToInt(List::size).sum();
        return new StatsResponse(rooms.size(), sensors.size(), readingCount, System.currentTimeMillis());
    }

    public String getSnapshotPath() {
        return snapshotManager.getStoragePath().toString();
    }

    private void ensureSensorExists(String sensorId) {
        if (!sensors.containsKey(sensorId)) {
            throw new ResourceNotFoundException("Sensor " + sensorId + " was not found.");
        }
    }

    private void loadOrSeed() {
        try {
            PersistedState persistedState = snapshotManager.load();
            if (persistedState == null) {
                seedData();
                persistQuietly();
                return;
            }

            for (Room room : persistedState.getRooms()) {
                rooms.put(room.getId(), new Room(room));
            }
            for (Sensor sensor : persistedState.getSensors()) {
                sensors.put(sensor.getId(), new Sensor(sensor));
            }
            for (PersistedState.ReadingBucket bucket : persistedState.getReadingBuckets()) {
                List<SensorReading> readings = bucket.getReadings() == null ? new ArrayList<>() : bucket.getReadings()
                        .stream()
                        .map(SensorReading::new)
                        .collect(Collectors.toList());
                readingsBySensor.put(bucket.getSensorId(), readings);
            }
        } catch (IOException exception) {
            rooms.clear();
            sensors.clear();
            readingsBySensor.clear();
            seedData();
        }
    }

    private void seedData() {
        Room lib = new Room("LIB-301", "Library Quiet Study", 80);
        Room lab = new Room("LAB-202", "Advanced Networking Lab", 35);

        Sensor temp = new Sensor("TEMP-001", "Temperature", "ACTIVE", 24.5, "LIB-301");
        Sensor co2 = new Sensor("CO2-101", "CO2", "ACTIVE", 420.0, "LIB-301");
        Sensor occ = new Sensor("OCC-050", "Occupancy", "MAINTENANCE", 0.0, "LAB-202");

        lib.getSensorIds().add(temp.getId());
        lib.getSensorIds().add(co2.getId());
        lab.getSensorIds().add(occ.getId());

        rooms.put(lib.getId(), lib);
        rooms.put(lab.getId(), lab);

        sensors.put(temp.getId(), temp);
        sensors.put(co2.getId(), co2);
        sensors.put(occ.getId(), occ);

        List<SensorReading> tempReadings = new ArrayList<>();
        tempReadings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis() - 300000, 24.3));
        tempReadings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis() - 120000, 24.5));

        List<SensorReading> co2Readings = new ArrayList<>();
        co2Readings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis() - 180000, 418.0));
        co2Readings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis() - 60000, 420.0));

        readingsBySensor.put(temp.getId(), tempReadings);
        readingsBySensor.put(co2.getId(), co2Readings);
        readingsBySensor.put(occ.getId(), new ArrayList<>());
    }

    private void persistQuietly() {
        try {
            PersistedState persistedState = new PersistedState();
            persistedState.setRooms(getRooms());
            persistedState.setSensors(getSensors(null, null, null));

            List<PersistedState.ReadingBucket> buckets = new ArrayList<>();
            for (Map.Entry<String, List<SensorReading>> entry : readingsBySensor.entrySet()) {
                List<SensorReading> copies = entry.getValue().stream().map(SensorReading::new).collect(Collectors.toList());
                buckets.add(new PersistedState.ReadingBucket(entry.getKey(), copies));
            }
            persistedState.setReadingBuckets(buckets);

            snapshotManager.save(persistedState);
        } catch (IOException ignored) {
        }
    }
}
