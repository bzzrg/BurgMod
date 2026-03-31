package com.bzzrg.burgmod.config.files.utils;

import com.google.gson.*;
import com.bzzrg.burgmod.modutils.GeneralUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class JsonConfigFile {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File file;
    private final List<Property> properties = new ArrayList<>();

    protected JsonConfigFile(File file) {
        this.file = file;
        init();
    }

    protected abstract void init();

    public void updateFields() {
        JsonObject root = readRoot();
        for (Property p : properties) p.updateField(root);
    }

    public void updateFile() {
        JsonObject root = readRoot();
        for (Property p : properties) p.updateConfig(root);
        writeRoot(root);
    }

    protected void addString(String key, Supplier<String> g, Consumer<String> s) {
        properties.add(new StringProperty(key, g, s));
    }

    protected void addInt(String key, Supplier<Integer> g, Consumer<Integer> s) {
        properties.add(new IntProperty(key, g, s));
    }

    protected void addBool(String key, Supplier<Boolean> g, Consumer<Boolean> s) {
        properties.add(new BoolProperty(key, g, s));
    }

    protected void addDouble(String key, Supplier<Double> g, Consumer<Double> s) {
        properties.add(new DoubleProperty(key, g, s));
    }

    protected void addJson(String key, JsonConvertor c) {
        properties.add(new JsonProperty(key, c));
    }

    protected <T> void addList(String key, Supplier<List<T>> g, Consumer<List<T>> s, ListConvertor<T> c) {
        properties.add(new ListProperty<>(key, g, s, c));
    }

    private JsonObject readRoot() {
        try {
            if (!file.exists()) return new JsonObject();

            try (FileReader r = new FileReader(file)) {
                JsonElement e = new JsonParser().parse(r);
                return e != null && e.isJsonObject() ? e.getAsJsonObject() : new JsonObject();
            }
        } catch (Exception ignored) {}

        return new JsonObject();
    }

    private void writeRoot(JsonObject root) {
        GeneralUtils.createDirectory(file.getParentFile());
        try (FileWriter w = new FileWriter(file)) {
            GSON.toJson(root, w);
        } catch (Exception ignored) {}
    }

    private interface Property {
        void updateField(JsonObject root);
        void updateConfig(JsonObject root);
    }

    private static class StringProperty implements Property {
        private final String key;
        private final Supplier<String> g;
        private final Consumer<String> s;

        private StringProperty(String key, Supplier<String> g, Consumer<String> s) {
            this.key = key; this.g = g; this.s = s;
        }

        public void updateField(JsonObject root) {
            if (root.has(key) && root.get(key).isJsonPrimitive()) {
                try { s.accept(root.get(key).getAsString()); } catch (Exception ignored) {}
            }
        }

        public void updateConfig(JsonObject root) {
            root.addProperty(key, g.get());
        }
    }

    private static class IntProperty implements Property {
        private final String key;
        private final Supplier<Integer> g;
        private final Consumer<Integer> s;

        private IntProperty(String key, Supplier<Integer> g, Consumer<Integer> s) {
            this.key = key; this.g = g; this.s = s;
        }

        public void updateField(JsonObject root) {
            if (root.has(key) && root.get(key).isJsonPrimitive()) {
                try { s.accept(root.get(key).getAsInt()); } catch (Exception ignored) {}
            }
        }

        public void updateConfig(JsonObject root) {
            root.addProperty(key, g.get());
        }
    }

    private static class BoolProperty implements Property {
        private final String key;
        private final Supplier<Boolean> g;
        private final Consumer<Boolean> s;

        private BoolProperty(String key, Supplier<Boolean> g, Consumer<Boolean> s) {
            this.key = key; this.g = g; this.s = s;
        }

        public void updateField(JsonObject root) {
            if (root.has(key) && root.get(key).isJsonPrimitive()) {
                try { s.accept(root.get(key).getAsBoolean()); } catch (Exception ignored) {}
            }
        }

        public void updateConfig(JsonObject root) {
            root.addProperty(key, g.get());
        }
    }

    private static class DoubleProperty implements Property {
        private final String key;
        private final Supplier<Double> g;
        private final Consumer<Double> s;

        private DoubleProperty(String key, Supplier<Double> g, Consumer<Double> s) {
            this.key = key; this.g = g; this.s = s;
        }

        public void updateField(JsonObject root) {
            if (root.has(key) && root.get(key).isJsonPrimitive()) {
                try { s.accept(root.get(key).getAsDouble()); } catch (Exception ignored) {}
            }
        }

        public void updateConfig(JsonObject root) {
            root.addProperty(key, g.get());
        }
    }

    private static class JsonProperty implements Property {
        private final String key;
        private final JsonConvertor c;

        private JsonProperty(String key, JsonConvertor c) {
            this.key = key; this.c = c;
        }

        public void updateField(JsonObject root) {
            if (root.has(key)) {
                try { c.setFields(root.get(key)); } catch (Exception ignored) {}
            }
        }

        public void updateConfig(JsonObject root) {
            JsonElement e = null;
            try { e = c.getJson(); } catch (Exception ignored) {}
            root.add(key, e != null ? e : JsonNull.INSTANCE);
        }
    }

    private static class ListProperty<T> implements Property {
        private final String key;
        private final Supplier<List<T>> g;
        private final Consumer<List<T>> s;
        private final ListConvertor<T> c;

        private ListProperty(String key, Supplier<List<T>> g, Consumer<List<T>> s, ListConvertor<T> c) {
            this.key = key; this.g = g; this.s = s; this.c = c;
        }

        public void updateField(JsonObject root) {
            if (!root.has(key) || !root.get(key).isJsonArray()) return;

            List<T> list = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray(key)) {
                try {
                    T obj = c.fromJson(e);
                    if (obj != null) list.add(obj);
                } catch (Exception ignored) {}
            }
            s.accept(list);
        }

        public void updateConfig(JsonObject root) {
            JsonArray arr = new JsonArray();
            for (T obj : g.get()) {
                try {
                    JsonElement e = c.toJson(obj);
                    if (e != null) arr.add(e);
                } catch (Exception ignored) {}
            }
            root.add(key, arr);
        }
    }
}