package com.bzzrg.burgmod.features;

import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.utils.gui.CustomButton;
import com.bzzrg.burgmod.utils.gui.CustomSlider;
import com.bzzrg.burgmod.utils.gui.CustomTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

public class BMConfigGui extends GuiScreen {

    private final List<Setting> settings = new ArrayList<>();
    private final Set<Integer> nextColumnAt = new HashSet<>();
    private boolean addStrategyButton = false;

    protected static final int borderThickness = 3;
    protected static final int borderInline = 15;
    protected static final int buttonHeight = 25;
    protected static final int buttonWidth = 160;
    protected static final int buttonGap = 5;

    private static final int confirmButtonWidth = 20;

    private int backButtonId = -1;
    private int strategyButtonId = -1;

    // ===================== ADD =====================

    public void addBooleanSetting(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
        settings.add(new ButtonSetting(
                () -> get.get() ? "\u00A7a" + name + ": ON" : "\u00A7c" + name + ": OFF",
                () -> set.accept(!get.get())
        ));
    }

    public <T extends Enum<T>> void addEnumSetting(String name, Supplier<T> get, Consumer<T> set) {
        settings.add(new ButtonSetting(
                () -> name + ": " + get.get(),
                () -> {
                    T cur = get.get();
                    T[] vals = cur.getDeclaringClass().getEnumConstants();
                    set.accept(vals[(cur.ordinal() + 1) % vals.length]);
                }
        ));
    }

    // slider versions
    public void addIntSetting(String name, Supplier<Integer> get, Consumer<Integer> set, int min, int max) {
        settings.add(new IntSliderSetting(name, get, set, min, max));
    }

    public void addFloatSetting(String name, Supplier<Float> get, Consumer<Float> set, float min, float max) {
        settings.add(new DecimalSliderSetting<>(name, get, set, min, max, v -> (float) v.doubleValue()));
    }

    public void addDoubleSetting(String name, Supplier<Double> get, Consumer<Double> set, double min, double max) {
        settings.add(new DecimalSliderSetting<>(name, get, set, min, max, v -> v));
    }

    // text-field versions
    public void addIntSetting(String name, Supplier<Integer> get, Consumer<Integer> set, String emptyMsg) {
        settings.add(new NumberTextSetting<>(name, get, set, emptyMsg, Integer::valueOf));
    }

    public void addFloatSetting(String name, Supplier<Float> get, Consumer<Float> set, String emptyMsg) {
        settings.add(new NumberTextSetting<>(name, get, set, emptyMsg, Float::valueOf));
    }

    public void addDoubleSetting(String name, Supplier<Double> get, Consumer<Double> set, String emptyMsg) {
        settings.add(new NumberTextSetting<>(name, get, set, emptyMsg, Double::valueOf));
    }

    public void addStringSetting(String name, Supplier<String> get, Consumer<String> set, String emptyMsg) {
        settings.add(new StringSetting(name, get, set, emptyMsg));
    }

    public void addActionButton(String name, Runnable action) {
        settings.add(new ButtonSetting(() -> name, action));
    }

    public void nextColumn() {
        nextColumnAt.add(settings.size());
    }

    public void addStrategyButton() {
        addStrategyButton = true;
    }

    // ===================== INIT =====================

    @Override
    public void initGui() {
        buttonList.clear();

        int id = 0;
        int startX = borderInline + borderThickness + buttonGap;
        int x = startX;
        int y = borderInline + borderThickness + buttonGap;

        for (int i = 0; i < settings.size(); i++) {
            if (nextColumnAt.contains(i)) {
                y = borderInline + borderThickness + buttonGap;
                x += buttonWidth + buttonGap;
            }

            id = settings.get(i).init(this, id, x, y);
            y += buttonHeight + buttonGap;
        }

        int bottomY = height - borderInline - borderThickness - buttonGap - buttonHeight;

        backButtonId = id;
        buttonList.add(new CustomButton(id++, startX, bottomY, buttonHeight, buttonHeight, "<"));

        if (addStrategyButton) {
            int strategyWidth = 80;
            strategyButtonId = id;
            buttonList.add(new CustomButton(
                    id,
                    width - borderInline - borderThickness - buttonGap - strategyWidth,
                    bottomY,
                    strategyWidth,
                    buttonHeight,
                    "Edit Strategy"
            ));
        }
    }

    // ===================== CLICK =====================

    @Override
    protected void actionPerformed(GuiButton button) {
        for (Setting setting : settings) {
            setting.click(button);
        }

        if (button.id == backButtonId) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        } else if (button.id == strategyButtonId) {
            Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
        }
    }

    // ===================== DRAW =====================

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int c = 0xFFFFFFFF;
        drawRect(borderInline, borderInline, width - borderInline, borderInline + borderThickness, c);
        drawRect(borderInline, height - borderInline - borderThickness, width - borderInline, height - borderInline, c);
        drawRect(borderInline, borderInline, borderInline + borderThickness, height - borderInline, c);
        drawRect(width - borderInline - borderThickness, borderInline, width - borderInline, height - borderInline, c);

        for (Setting setting : settings) {
            setting.draw(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        for (Setting setting : settings) {
            setting.mouse(x, y, button);
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException {
        super.keyTyped(c, keyCode);
        for (Setting setting : settings) {
            setting.key(c, keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        for (Setting s : settings) {
            if (s instanceof NumberTextSetting) {
                ((NumberTextSetting<?>) s).applyFinalValue();
            }
        }

        BasicConfigHandler.updateConfigFile();
        super.onGuiClosed();
    }

    // ===================== BASE =====================

    private abstract static class Setting {
        abstract int init(BMConfigGui gui, int id, int x, int y);
        void click(GuiButton button) {}
        void draw(int mouseX, int mouseY) {}
        void mouse(int x, int y, int button) {}
        void key(char c, int keyCode) {}
    }

    // ===================== BUTTON =====================

    private static class ButtonSetting extends Setting {
        private final Supplier<String> label;
        private final Runnable action;
        private GuiButton button;

        ButtonSetting(Supplier<String> label, Runnable action) {
            this.label = label;
            this.action = action;
        }

        @Override
        int init(BMConfigGui gui, int id, int x, int y) {
            button = new CustomButton(id, x, y, buttonWidth, buttonHeight, label.get());
            gui.buttonList.add(button);
            return id + 1;
        }

        @Override
        void click(GuiButton clicked) {
            if (clicked == button) {
                action.run();
                button.displayString = label.get();
            }
        }
    }

    // ===================== INT SLIDER =====================

    private static class IntSliderSetting extends Setting {
        private final String name;
        private final Supplier<Integer> get;
        private final Consumer<Integer> set;
        private final int min;
        private final int max;

        IntSliderSetting(String name, Supplier<Integer> get, Consumer<Integer> set, int min, int max) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.min = min;
            this.max = max;
        }

        @Override
        int init(BMConfigGui gui, int id, int x, int y) {
            int start = get.get();

            GuiSlider slider = new CustomSlider(
                    id, x, y, buttonWidth, buttonHeight,
                    name + ": ", "", min, max, start, false, true,
                    s -> {
                        int value = s.getValueInt();
                        set.accept(value);
                        s.displayString = name + ": " + value;
                    }
            );

            slider.displayString = name + ": " + start;
            gui.buttonList.add(slider);
            return id + 1;
        }
    }

    // ===================== FLOAT / DOUBLE SLIDER =====================

    private static class DecimalSliderSetting<T extends Number> extends Setting {
        private final String name;
        private final Supplier<T> get;
        private final Consumer<T> set;
        private final double min;
        private final double max;
        private final Function<Double, T> converter;

        DecimalSliderSetting(String name, Supplier<T> get, Consumer<T> set, double min, double max, Function<Double, T> converter) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.min = min;
            this.max = max;
            this.converter = converter;
        }

        @Override
        int init(BMConfigGui gui, int id, int x, int y) {
            double start = get.get().doubleValue();

            GuiSlider slider = new CustomSlider(
                    id, x, y, buttonWidth, buttonHeight,
                    name + ": ", "", min, max, start, true, true,
                    s -> {
                        double value = s.getValue();
                        set.accept(converter.apply(value));
                        s.displayString = format(value);
                    }
            );

            slider.displayString = format(start);
            gui.buttonList.add(slider);
            return id + 1;
        }

        private String format(double value) {
            return name + ": " + String.format(Locale.ROOT, "%.2f", value);
        }
    }

    // ===================== NUMBER TEXT FIELD =====================
    private static class NumberTextSetting<T> extends Setting {
        private final String name;
        private final Supplier<T> get;
        private final Consumer<T> set;
        private final String empty;
        private final Function<String, T> parser;

        private CustomTextField field;

        NumberTextSetting(String name, Supplier<T> get, Consumer<T> set, String empty, Function<String, T> parser) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.empty = empty;
            this.parser = parser;
        }

        @Override
        int init(BMConfigGui gui, int id, int x, int y) {
            field = new CustomTextField(0, x, y, buttonWidth, buttonHeight, name, empty);

            T current = get.get();
            field.field.setText(current == null ? "" : String.valueOf(current));

            return id;
        }

        void applyFinalValue() {
            String text = field.field.getText().trim();

            if (text.isEmpty()) {
                set.accept(null);
                return;
            }

            try {
                T parsed = parser.apply(text);
                set.accept(parsed);
            } catch (Exception ignored) {
                set.accept(null);
                bmChat("\u00A7c\"" + name + "\" is invalid: \"" + text + "\"");
            }
        }

        @Override
        void draw(int mouseX, int mouseY) {
            field.draw(mouseX, mouseY);
        }

        @Override
        void mouse(int x, int y, int button) {
            field.mouseClicked(x, y, button);
        }

        @Override
        void key(char c, int keyCode) {
            field.keyTyped(c, keyCode);
        }
    }

    // ===================== STRING FIELD + CHECK =====================

    private static class StringSetting extends Setting {
        private final String name;
        private final Supplier<String> get;
        private final Consumer<String> set;
        private final String empty;

        private CustomTextField field;
        private GuiButton confirmButton;

        StringSetting(String name, Supplier<String> get, Consumer<String> set, String empty) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.empty = empty;
        }

        @Override
        int init(BMConfigGui gui, int id, int x, int y) {
            int fieldWidth = buttonWidth - confirmButtonWidth - buttonGap;

            field = new CustomTextField(0, x, y, fieldWidth, buttonHeight, name, empty);
            field.field.setText(get.get());

            confirmButton = new CustomButton(
                    id,
                    x + fieldWidth + buttonGap,
                    y,
                    confirmButtonWidth,
                    buttonHeight,
                    "\u00A7a\u2714"
            );
            gui.buttonList.add(confirmButton);

            return id + 1;
        }

        @Override
        void click(GuiButton clicked) {
            if (clicked == confirmButton) {
                set.accept(field.field.getText());
            }
        }

        @Override
        void draw(int mouseX, int mouseY) {
            field.draw(mouseX, mouseY);
        }

        @Override
        void mouse(int x, int y, int button) {
            field.mouseClicked(x, y, button);
        }

        @Override
        void key(char c, int keyCode) {
            field.keyTyped(c, keyCode);
        }
    }
}