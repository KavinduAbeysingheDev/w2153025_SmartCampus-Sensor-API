package com.kavindu.smartcampus.model;

public class StatsResponse {
    private int roomCount;
    private int sensorCount;
    private int readingCount;
    private long generatedAt;

    public StatsResponse() {
    }

    public StatsResponse(int roomCount, int sensorCount, int readingCount, long generatedAt) {
        this.roomCount = roomCount;
        this.sensorCount = sensorCount;
        this.readingCount = readingCount;
        this.generatedAt = generatedAt;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public int getReadingCount() {
        return readingCount;
    }

    public void setReadingCount(int readingCount) {
        this.readingCount = readingCount;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }
}
