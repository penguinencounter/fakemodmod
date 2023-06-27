package org.penguinencounter.fakemodmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConfigMessagePicker implements MessagePicker {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("fakemodmod | ConfigMessagePicker");

    public static class PickerFormat {
        public List<List<String>> entries = List.of();
    }

    PickerFormat pickFrom = null;

    @Override
    public List<String> pick() {
        if (pickFrom == null) {
            LOGGER.warn("Failed: pickFrom is null!");
            return List.of("Oh no!",
                    "The configuration file broke",
                    "Why is pickFrom null??");
        }
        if (pickFrom.entries.isEmpty()) {
            LOGGER.info("No entries in configuration file");
            return List.of(
                    "You didn't configure the mod",
                    "Go to your .minecraft folder",
                    "Config, then fmm_picker.json",
                    "Add some clever messages to the entries list",
                    "(add lists of strings, not just strings)",
                    "Then restart the game"
            );
        }
        LOGGER.info("picking mod message, of " + pickFrom.entries.size() + " entries");
        int index = (int) (Math.random() * pickFrom.entries.size());
        return pickFrom.entries.get(index);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void _ready() throws IOException {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("fmm_picker.json");
        Gson gson = new GsonBuilder().create();
        if (!configFile.toFile().exists()) {
            configFile.getParent().toFile().mkdirs();
            configFile.toFile().createNewFile();
            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                gson.toJson(new PickerFormat(), writer);
            }
            pickFrom = new PickerFormat();
            return;
        }
        try (FileReader reader = new FileReader(configFile.toFile())) {
            pickFrom = gson.fromJson(reader, PickerFormat.class);
        }
    }

    @Override
    public void ready() {
        try {
            _ready();
        } catch (IOException e) {
            LOGGER.warn("Failed to read configuration file", e);
        }
    }
}
