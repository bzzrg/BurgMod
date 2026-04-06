package com.bzzrg.burgmod.config.files.utils;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MainConfigSection {

    public static File mainConfigFile;

    private static final List<MainConfigSection> sections = new ArrayList<>();
    private static Configuration config;

    private final List<Property> properties = new ArrayList<>();

    protected MainConfigSection() {
        init();
        sections.add(this);
    }

    protected abstract void init();
    protected abstract String getCategory();

    public static void updateFields() {
        try {
            config = new Configuration(mainConfigFile);
            config.load();

            for (MainConfigSection section : sections) {
                for (Property property : section.properties) {
                    property.updateField(config);
                }
            }

        } catch (Exception ignored) {}
    }

    public static void updateFile() {
        try {
            if (config == null) {
                config = new Configuration(mainConfigFile);
                config.load();
            }

            for (MainConfigSection section : sections) {
                for (Property property : section.properties) {
                    property.updateConfig(config);
                }
            }

            if (config.hasChanged()) {
                config.save();
            }

        } catch (Exception ignored) {}
    }

    // ===== ADD METHODS =====

    protected void addString(String key, Supplier<String> g, Consumer<String> s) {
        properties.add(new StringProperty(getCategory(), key, g, s));
    }

    protected void addInt(String key, Supplier<Integer> g, Consumer<Integer> s) {
        properties.add(new IntProperty(getCategory(), key, g, s));
    }

    protected void addBool(String key, Supplier<Boolean> g, Consumer<Boolean> s) {
        properties.add(new BoolProperty(getCategory(), key, g, s));
    }

    protected void addDouble(String key, Supplier<Double> g, Consumer<Double> s) {
        properties.add(new DoubleProperty(getCategory(), key, g, s));
    }

    // ===== PROPERTY INTERFACE =====

    private interface Property {
        void updateField(Configuration config);
        void updateConfig(Configuration config);
    }

    // ===== IMPLEMENTATIONS =====

    private static class StringProperty implements Property {
        private final String cat, key;
        private final Supplier<String> g;
        private final Consumer<String> s;

        private StringProperty(String cat, String key, Supplier<String> g, Consumer<String> s) {
            this.cat = cat;
            this.key = key;
            this.g = g;
            this.s = s;
        }

        public void updateField(Configuration config) {
            try {
                s.accept(config.get(cat, key, g.get()).getString());
            } catch (Exception ignored) {}
        }

        public void updateConfig(Configuration config) {
            try {
                config.get(cat, key, g.get()).set(g.get());
            } catch (Exception ignored) {}
        }
    }

    private static class IntProperty implements Property {
        private final String cat, key;
        private final Supplier<Integer> g;
        private final Consumer<Integer> s;

        private IntProperty(String cat, String key, Supplier<Integer> g, Consumer<Integer> s) {
            this.cat = cat;
            this.key = key;
            this.g = g;
            this.s = s;
        }

        public void updateField(Configuration config) {
            try {
                s.accept(config.get(cat, key, g.get()).getInt());
            } catch (Exception ignored) {}
        }

        public void updateConfig(Configuration config) {
            try {
                config.get(cat, key, g.get()).set(g.get());
            } catch (Exception ignored) {}
        }
    }

    private static class BoolProperty implements Property {
        private final String cat, key;
        private final Supplier<Boolean> g;
        private final Consumer<Boolean> s;

        private BoolProperty(String cat, String key, Supplier<Boolean> g, Consumer<Boolean> s) {
            this.cat = cat;
            this.key = key;
            this.g = g;
            this.s = s;
        }

        public void updateField(Configuration config) {
            try {
                s.accept(config.get(cat, key, g.get()).getBoolean());
            } catch (Exception ignored) {}
        }

        public void updateConfig(Configuration config) {
            try {
                config.get(cat, key, g.get()).set(g.get());
            } catch (Exception ignored) {}
        }
    }

    private static class DoubleProperty implements Property {
        private final String cat, key;
        private final Supplier<Double> g;
        private final Consumer<Double> s;

        private DoubleProperty(String cat, String key, Supplier<Double> g, Consumer<Double> s) {
            this.cat = cat;
            this.key = key;
            this.g = g;
            this.s = s;
        }

        public void updateField(Configuration config) {
            try {
                s.accept(config.get(cat, key, g.get()).getDouble());
            } catch (Exception ignored) {}
        }

        public void updateConfig(Configuration config) {
            try {
                config.get(cat, key, g.get()).set(g.get());
            } catch (Exception ignored) {}
        }
    }
}