package com.kavindu.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

public class PersistedState {
    private List<Room> rooms = new ArrayList<>();
    private List<Sensor> sensors = new ArrayList<>();
    private List<ReadingBucket> readingBuckets = new ArrayList<>();

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<ReadingBucket> getReadingBuckets() {
        return readingBuckets;
    }

    public void setReadingBuckets(List<ReadingBucket> readingBuckets) {
        this.readingBuckets = readingBuckets;
    }

    public static class ReadingBucket {
        private String sensorId;
        private List<SensorReading> readings = new ArrayList<>();

        public ReadingBucket() {
        }

        public ReadingBucket(String sensorId, List<SensorReading> readings) {
            this.sensorId = sensorId;
            this.readings = readings;
        }

        public String getSensorId() {
            return sensorId;
        }

        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        public List<SensorReading> getReadings() {
            return readings;
        }

        public void setReadings(List<SensorReading> readings) {
            this.readings = readings;
        }
    }
}
