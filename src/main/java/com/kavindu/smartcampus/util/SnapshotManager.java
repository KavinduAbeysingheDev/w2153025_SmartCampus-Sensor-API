package com.kavindu.smartcampus.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kavindu.smartcampus.model.PersistedState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SnapshotManager {
    private final ObjectMapper objectMapper;
    private final Path storagePath;

    public SnapshotManager() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String configuredPath = System.getProperty("smartcampus.data.file", "data/smart-campus-state.json");
        this.storagePath = Path.of(configuredPath);
    }

    public synchronized PersistedState load() throws IOException {
        File file = storagePath.toFile();
        if (!file.exists()) {
            return null;
        }
        return objectMapper.readValue(file, PersistedState.class);
    }

    public synchronized void save(PersistedState persistedState) throws IOException {
        Files.createDirectories(storagePath.getParent() == null ? Path.of(".") : storagePath.getParent());
        objectMapper.writeValue(storagePath.toFile(), persistedState);
    }

    public Path getStoragePath() {
        return storagePath;
    }
}
