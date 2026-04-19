package com.bzzrg.burgmod.modutils.gui;

import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.config.files.utils.MainConfigSection;
import com.bzzrg.burgmod.features.strategy.StrategyListGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.getNextEnumValue;

@SuppressWarnings("UnusedReturnValue")
public class BMConfigGui extends GuiScreen {

    protected final List<Setting> settings = new ArrayList<>();
    protected final Set<Integer> nextColumnAt = new HashSet<>();
    protected boolean addStrategyButton = false;

    protected static final int borderThickness = 3;
    protected static final int borderInline = 15;
    protected static final int buttonHeight = 25;
    protected static final int buttonGap = 5;

    protected static final int confirmButtonWidth = 20;
    protected static final int strategyButtonWidth = 100;

    protected GuiButton backButton = null;
    protected GuiButton strategyButton = null;

    protected int settingWidth = 160;


    // ===================== VARIABLE CONSTANTS =====================
    protected int configLeft;
    protected int configRight;
    protected int configTop;
    protected int configBottom;

    // ===================== ADD =====================

    public void setSettingWidth(int settingWidth) {
        this.settingWidth = settingWidth;
    }

    public ButtonSetting addBooleanSetting(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
        ButtonSetting setting = new ButtonSetting(
                () -> get.get() ? "\u00A7a" + name + ": ON" : "\u00A7c" + name + ": OFF",
                b -> set.accept(!get.get())
        );
        settings.add(setting);
        return setting;
    }

    public <T extends Enum<T>> ButtonSetting addEnumSetting(String name, Class<T> enumClass, Supplier<String> get, Consumer<String> set) {
        ButtonSetting setting = new ButtonSetting(
                () -> {
                    try {
                        T val = Enum.valueOf(enumClass, get.get());
                        return name + ": " + val;
                    } catch (Exception e) {
                        return name + ": ?";
                    }
                },
                b -> {
                    try {
                        T cur = Enum.valueOf(enumClass, get.get());
                        T next = getNextEnumValue(cur);
                        set.accept(next.toString());
                    } catch (Exception ignored) {}
                }
        );
        settings.add(setting);
        return setting;
    }

    public IntSliderSetting addIntSetting(String name, Supplier<Integer> get, Consumer<Integer> set, int min, int max) {
        IntSliderSetting setting = new IntSliderSetting(name, get, set, min, max);
        settings.add(setting);
        return setting;
    }

    public DecimalSliderSetting<Float> addFloatSetting(String name, Supplier<Float> get, Consumer<Float> set, float min, float max) {
        DecimalSliderSetting<Float> setting = new DecimalSliderSetting<>(name, get, set, min, max, v -> (float) v.doubleValue());
        settings.add(setting);
        return setting;
    }

    public DecimalSliderSetting<Double> addDoubleSetting(String name, Supplier<Double> get, Consumer<Double> set, double min, double max) {
        DecimalSliderSetting<Double> setting = new DecimalSliderSetting<>(name, get, set, min, max, v -> v);
        settings.add(setting);
        return setting;
    }

    public NumberTextSetting<Integer> addIntSetting(String name, Supplier<Integer> get, Consumer<Integer> set, String emptyMsg) {
        NumberTextSetting<Integer> setting = new NumberTextSetting<>(name, get, set, emptyMsg, Integer::valueOf);
        settings.add(setting);
        return setting;
    }

    public NumberTextSetting<Float> addFloatSetting(String name, Supplier<Float> get, Consumer<Float> set, String emptyMsg) {
        NumberTextSetting<Float> setting = new NumberTextSetting<>(name, get, set, emptyMsg, Float::valueOf);
        settings.add(setting);
        return setting;
    }

    public NumberTextSetting<Double> addDoubleSetting(String name, Supplier<Double> get, Consumer<Double> set, String emptyMsg) {
        NumberTextSetting<Double> setting = new NumberTextSetting<>(name, get, set, emptyMsg, Double::valueOf);
        settings.add(setting);
        return setting;
    }

    public StringSetting addStringSetting(String name, Supplier<String> get, Consumer<String> set, String emptyMsg) {
        StringSetting setting = new StringSetting(name, get, set, emptyMsg);
        settings.add(setting);
        return setting;
    }

    public ButtonSetting addActionButton(String name, Consumer<GuiButton> action) {
        ButtonSetting setting = new ButtonSetting(() -> name, action);
        settings.add(setting);
        return setting;
    }

    public void nextColumn() {
        nextColumnAt.add(settings.size());
    }

    public void addStrategyButton() {
        addStrategyButton = true;
    }

    // ===================== INIT =====================

    protected void setConstants() {
        configLeft = borderInline + borderThickness;
        configRight = width - borderInline - borderThickness;
        configTop = borderInline + borderThickness;
        configBottom = height - borderInline - borderThickness;

        settingWidth = 160;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        setConstants();

        int id = 0;
        int startX = configLeft + buttonGap;
        int startY = configTop + buttonGap;

        int x = startX;
        int y = startY;

        for (int i = 0; i < settings.size(); i++) {
            if (nextColumnAt.contains(i)) {
                y = startY;
                x += settingWidth + buttonGap;
            }

            id = settings.get(i).init(this, id, x, y);
            y += buttonHeight + buttonGap;
        }

        int bottomY = configBottom - buttonGap - buttonHeight;
        backButton = new CustomButton(-1, startX, bottomY, buttonHeight, buttonHeight, "<");
        buttonList.add(backButton);

        if (addStrategyButton) {
            int strategyX = configRight - buttonGap - strategyButtonWidth;
            strategyButton = new CustomButton(-1, strategyX, bottomY, strategyButtonWidth, buttonHeight, "Strategy Editor");
            buttonList.add(strategyButton);
        }
    }

    // ===================== CLICK =====================

    @Override
    protected void actionPerformed(GuiButton button) {
        for (Setting setting : settings) {
            setting.click(button);
        }

        if (button == backButton) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        } else if (button == strategyButton) {
            Minecraft.getMinecraft().displayGuiScreen(new StrategyListGui());
        }
    }

    // ===================== DRAW =====================

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawConfigBorder();

        for (Setting setting : settings) {
            setting.draw(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void drawConfigBorder() {
        int c = 0xFFFFFFFF;

        drawRect(configLeft - borderThickness, configTop - borderThickness, configRight + borderThickness, configTop, c);
        drawRect(configLeft - borderThickness, configBottom, configRight + borderThickness, configBottom + borderThickness, c);
        drawRect(configLeft - borderThickness, configTop - borderThickness, configLeft, configBottom + borderThickness, c);
        drawRect(configRight, configTop - borderThickness, configRight + borderThickness, configBottom + borderThickness, c);
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
        for (Setting setting : settings) {
            if (setting instanceof NumberTextSetting) {
                ((NumberTextSetting<?>) setting).applyFinalValue();
            }
        }

        MainConfigSection.updateFile();
        super.onGuiClosed();
    }

    // ===================== BASE =====================

    protected abstract static class Setting {
        protected abstract int init(BMConfigGui gui, int id, int x, int y);
        protected void click(GuiButton button) {}
        protected void draw(int mouseX, int mouseY) {}
        protected void mouse(int x, int y, int button) {}
        protected void key(char c, int keyCode) {}
    }

    // ===================== BUTTON =====================

    public class ButtonSetting extends Setting {
        public final Supplier<String> label;
        public final Consumer<GuiButton> action;
        public GuiButton button;

        public ButtonSetting(Supplier<String> label, Consumer<GuiButton> action) {
            this.label = label;
            this.action = action;
        }

        @Override
        public int init(BMConfigGui gui, int id, int x, int y) {
            button = new CustomButton(id, x, y, settingWidth, buttonHeight, label.get());
            gui.buttonList.add(button);
            return id + 1;
        }

        @Override
        public void click(GuiButton clicked) {
            if (clicked == button) {

                String before = button.displayString;

                action.accept(clicked);

                // 🔥 only update from label if action DIDN’T override it
                if (button.displayString.equals(before)) {
                    button.displayString = label.get();
                }
            }
        }
    }

    // ===================== INT SLIDER =====================

    public class IntSliderSetting extends Setting {
        public final String name;
        public final Supplier<Integer> get;
        public final Consumer<Integer> set;
        public final int min;
        public final int max;

        public GuiSlider slider; // <-- ADD THIS

        public IntSliderSetting(String name, Supplier<Integer> get, Consumer<Integer> set, int min, int max) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.min = min;
            this.max = max;
        }

        @Override
        public int init(BMConfigGui gui, int id, int x, int y) {
            Integer current = get.get();
            int start = current == null ? min : current;

            slider = new CustomSlider(
                    id, x, y, settingWidth, buttonHeight,
                    name + ": ", "", min, max, start, false, true,
                    s -> {
                        int value = s.getValueInt();
                        set.accept(value);
                    }
            );

            gui.buttonList.add(slider);
            return id + 1;
        }
    }

    // ===================== FLOAT / DOUBLE SLIDER =====================

    public class DecimalSliderSetting<T extends Number> extends Setting {
        public final String name;
        public final Supplier<T> get;
        public final Consumer<T> set;
        public final double min;
        public final double max;
        public final Function<Double, T> converter;

        public GuiSlider slider; // <-- ADD THIS

        public DecimalSliderSetting(String name, Supplier<T> get, Consumer<T> set, double min, double max, Function<Double, T> converter) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.min = min;
            this.max = max;
            this.converter = converter;
        }

        @Override
        public int init(BMConfigGui gui, int id, int x, int y) {
            T current = get.get();
            double start = current == null ? min : current.doubleValue();

            slider = new CustomSlider(
                    id, x, y, settingWidth, buttonHeight,
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

    public class NumberTextSetting<T> extends Setting {
        public final String name;
        public final Supplier<T> get;
        public final Consumer<T> set;
        public final String empty;
        public final Function<String, T> parser;

        public CustomTextField field;

        NumberTextSetting(String name, Supplier<T> get, Consumer<T> set, String empty, Function<String, T> parser) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.empty = empty;
            this.parser = parser;
        }

        @Override
        public int init(BMConfigGui gui, int id, int x, int y) {
            field = new CustomTextField(0, x, y, settingWidth, buttonHeight, name, empty);

            T current = get.get();
            field.field.setText(current == null ? "" : String.valueOf(current));
            return id;
        }

        public void applyFinalValue() {
            String text = field.field.getText().trim();

            if (text.isEmpty()) {
                set.accept(null);
                return;
            }

            try {
                set.accept(parser.apply(text));
            } catch (Exception ignored) {
                set.accept(null);
                bmChat("\u00A7c\"" + name + "\" is invalid: \"" + text + "\"");
            }
        }

        @Override
        public void draw(int mouseX, int mouseY) {
            field.draw(mouseX, mouseY);
        }

        @Override
        public void mouse(int x, int y, int button) {
            field.mouseClicked(x, y, button);
        }

        @Override
        public void key(char c, int keyCode) {
            field.keyTyped(c, keyCode);
        }
    }

    // ===================== STRING FIELD + CHECK =====================

    public class StringSetting extends Setting {
        public final String name;
        public final Supplier<String> get;
        public final Consumer<String> set;
        public final String empty;

        public CustomTextField field;
        public GuiButton confirmButton;

        public StringSetting(String name, Supplier<String> get, Consumer<String> set, String empty) {
            this.name = name;
            this.get = get;
            this.set = set;
            this.empty = empty;
        }

        @Override
        public int init(BMConfigGui gui, int id, int x, int y) {
            int fieldWidth = settingWidth - confirmButtonWidth - buttonGap;

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
        public void click(GuiButton clicked) {
            if (clicked == confirmButton) {
                set.accept(field.field.getText());
            }
        }

        @Override
        public  void draw(int mouseX, int mouseY) {
            field.draw(mouseX, mouseY);
        }

        @Override
        public void mouse(int x, int y, int button) {
            field.mouseClicked(x, y, button);
        }

        @Override
        public void key(char c, int keyCode) {
            field.keyTyped(c, keyCode);
        }
    }
}