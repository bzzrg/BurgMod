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

    protected void addString(String cat, String key, Supplier<String> g, Consumer<String> s) {
        properties.add(new StringProperty(cat, key, g, s));
    }

    protected void addInt(String cat, String key, Supplier<Integer> g, Consumer<Integer> s) {
        properties.add(new IntProperty(cat, key, g, s));
    }

    protected void addBool(String cat, String key, Supplier<Boolean> g, Consumer<Boolean> s) {
        properties.add(new BoolProperty(cat, key, g, s));
    }

    protected void addDouble(String cat, String key, Supplier<Double> g, Consumer<Double> s) {
        properties.add(new DoubleProperty(cat, key, g, s));
    }

    private interface Property {
        void updateField(Configuration config);
        void updateConfig(Configuration config);
    }

    private static class StringProperty implements Property {
        private final String cat, key;
        private final Supplier<String> g;
        private final Consumer<String> s;

        private StringProperty(String cat, String key, Supplier<String> g, Consumer<String> s) {
            this.cat = cat; this.key = key; this.g = g; this.s = s;
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
            this.cat = cat; this.key = key; this.g = g; this.s = s;
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
            this.cat = cat; this.key = key; this.g = g; this.s = s;
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
            this.cat = cat; this.key = key; this.g = g; this.s = s;
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