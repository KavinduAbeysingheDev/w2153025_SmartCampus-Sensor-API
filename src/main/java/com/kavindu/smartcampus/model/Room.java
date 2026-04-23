package com.kavindu.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();

    public Room() {
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = new ArrayList<>();
    }

    public Room(Room other) {
        this.id = other.id;
        this.name = other.name;
        this.capacity = other.capacity;
        this.sensorIds = new ArrayList<>(other.sensorIds);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = safe(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = safe(name);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds == null ? new ArrayList<>() : new ArrayList<>(sensorIds);
    }

    private String safe(String value) {
        return value == null ? null : value.trim();
    }
}
