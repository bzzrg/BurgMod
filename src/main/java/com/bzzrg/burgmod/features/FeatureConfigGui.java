package com.bzzrg.burgmod.features;

import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.utils.gui.CustomButton;
import com.bzzrg.burgmod.utils.gui.CustomSlider;
import com.bzzrg.burgmod.utils.gui.CustomTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FeatureConfigGui extends GuiScreen {

    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean addStrategyButton = false;

    protected static final int borderThickness = 3;
    protected static final int borderInline = 20;
    protected static final int buttonHeight = 25;
    protected static final int buttonWidth = 160;
    protected static final int buttonGap = 5;

    public void addBooleanSetting(String name, Supplier<Boolean> valueGetter, Consumer<Boolean> valueSetter) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, null, null, null));
    }
    public void addIntSetting(String name, Supplier<Integer> valueGetter, Consumer<Integer> valueSetter, int min, int max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max, null));
    }
    public void addFloatSetting(String name, Supplier<Float> valueGetter, Consumer<Float> valueSetter, float min, float max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max, null));
    }
    public void addDoubleSetting(String name, Supplier<Double> valueGetter, Consumer<Double> valueSetter, double min, double max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max, null));
    }

    public void addStringSetting(String name, Supplier<String> valueGetter, Consumer<String> valueSetter, String emptyMsg) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, null, null, emptyMsg));
    }

    // used for seperate xz labels thing and whether u use a or d for 45s
    public <T extends Enum<T>> void addEnumSetting(String name, Supplier<T> valueGetter, Consumer<T> valueSetter) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, null, null, null));
    }

    private final List<Integer> sizesToAdvanceColumn = new ArrayList<>();

    public void nextColumn() {
        sizesToAdvanceColumn.add(settings.size());
    }

    public void addStrategyButton() {
        addStrategyButton = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {

        int latestButtonId = 0;

        int buttonX = borderInline + borderThickness + buttonGap;
        int buttonY = borderInline + borderThickness + buttonGap;

        for (int i = 0; i < settings.size(); i++) {
            Setting<?> setting = settings.get(i);

            if (sizesToAdvanceColumn.contains(i)) {
                buttonY = borderInline + borderThickness + buttonGap;
                buttonX += buttonWidth + buttonGap;
            }

            Object value = setting.valueGetter.get();
            if (value instanceof Boolean) {
                setting.button = new CustomButton(latestButtonId++, buttonX, buttonY, buttonWidth, buttonHeight, ""); // empty name bc the next line sets the name
                setting.button.displayString = (boolean) value ? "\u00A7a" + setting.name + ": ON" : "\u00A7c" + setting.name + ": OFF";
            } else if (value instanceof Integer) {
                Setting<Integer> castedSetting = (Setting<Integer>) setting;

                GuiSlider slider = new CustomSlider(latestButtonId++, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, (int) value, false, true, s -> {
                    int newValue = s.getValueInt();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + newValue;
                });
                slider.displayString = castedSetting.name + ": " + slider.getValueInt();
                setting.button = slider;

            } else if (value instanceof Float) {
                Setting<Float> castedSetting = (Setting<Float>) setting;

                GuiSlider slider = new CustomSlider(latestButtonId++, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, (float) value, true, true, s -> {

                    float newValue = (float) s.getValue();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", newValue);
                });
                slider.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", slider.getValue());
                setting.button = slider;

            } else if (value instanceof Double) {
                Setting<Double> castedSetting = (Setting<Double>) setting;

                GuiSlider slider = new CustomSlider(latestButtonId++, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, (double) value, true, true, s -> {

                    double newValue = s.getValue();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", newValue);
                });
                slider.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", slider.getValue());
                setting.button = slider;

            } else if (value instanceof String) {
                Setting<String> castedSetting = (Setting<String>) setting;

                CustomTextField textField = new CustomTextField(0, buttonX, buttonY, buttonWidth, buttonHeight, castedSetting.name, castedSetting.emptyMsg) {
                    @Override
                    public void keyTyped(char typedChar, int keyCode) {
                        super.keyTyped(typedChar, keyCode);
                        castedSetting.valueSetter.accept(this.field.getText());
                    }
                };
                textField.field.setText((String) value);
                setting.textField = textField;

            } else if (value instanceof Enum<?>) {
                setting.button = new CustomButton(latestButtonId++, buttonX, buttonY, buttonWidth, buttonHeight, ""); // empty name bc the next line sets the name
                setting.button.displayString = setting.name + ": " + value;
            }
            if (setting.button != null) buttonList.add(setting.button);

            buttonY += buttonHeight + buttonGap;

        }

        backButtonId = latestButtonId;
        final int realHeight = height - 1;
        final int backButtonX = borderInline + borderThickness + buttonGap;
        final int bottomButtonY = realHeight - borderInline - borderThickness - buttonGap - buttonHeight;
        buttonList.add(new CustomButton(latestButtonId++, backButtonX, bottomButtonY, buttonHeight, buttonHeight, "<"));


        final int realWidth = width - 1;
        final int strategyButtonWidth = 80;
        final int strategyButtonX = realWidth -borderInline - borderThickness - buttonGap - strategyButtonWidth;

        if (addStrategyButton) {
            strategyButtonId = latestButtonId;
            buttonList.add(new CustomButton(latestButtonId, strategyButtonX, bottomButtonY, strategyButtonWidth, buttonHeight, "Edit Strategy"));
        }

    }

    private int backButtonId = -1;
    private int strategyButtonId = -1;

    @Override
    @SuppressWarnings("unchecked")
    protected void actionPerformed(GuiButton button) {
        for (Setting<?> setting : settings) {

            Object value = setting.valueGetter.get();
            if (setting.button == button) {

                if (value instanceof Boolean) {
                    Setting<Boolean> castedSetting = (Setting<Boolean>) setting;

                    castedSetting.valueSetter.accept(!(boolean) value);
                    setting.button.displayString = !(boolean) value ? "\u00A7a" + setting.name + ": ON" : "\u00A7c" + setting.name + ": OFF";
                    break;
                } else if (value instanceof Enum<?>) {
                    Setting<Enum<?>> castedSetting = (Setting<Enum<?>>) setting;
                    Enum<?>[] values = ((Enum<?>) value).getDeclaringClass().getEnumConstants();
                    Enum<?> next = values[(((Enum<?>) value).ordinal() + 1) % values.length];

                    castedSetting.valueSetter.accept(next);
                    setting.button.displayString = setting.name + ": " + next;
                    break;
                }

            }
        }
        if (button.id == backButtonId) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        } else if (button.id == strategyButtonId) {
            Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int borderColor = 0xFFFFFFFF;
        drawRect(borderInline, borderInline, this.width - borderInline, borderInline + borderThickness, borderColor);
        drawRect(borderInline, this.height - borderInline - borderThickness, this.width - borderInline, this.height - borderInline, borderColor);
        drawRect(borderInline, borderInline, borderInline + borderThickness, this.height - borderInline, borderColor);
        drawRect(this.width - borderInline - borderThickness, borderInline, this.width - borderInline, this.height - borderInline, borderColor);

        for (Setting<?> setting : settings) {
            if (setting.textField != null) {
                setting.textField.draw(mouseX, mouseY);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static class Setting<T> {
        public GuiButton button = null;

        public final String name;
        public final Supplier<T> valueGetter;
        public final Consumer<T> valueSetter;

        // Slider fields
        public final T min;
        public final T max;

        // Text field fields
        public CustomTextField textField = null;
        public final String emptyMsg;

        public Setting(String name, Supplier<T> valueGetter, Consumer<T> valueSetter, T min, T max, String emptyMsg) {
            this.name = name;
            this.valueGetter = valueGetter;
            this.valueSetter = valueSetter;
            this.min = min;
            this.max = max;
            this.emptyMsg = emptyMsg;
        }
    }
    @Override
    public void onGuiClosed() {
        BasicConfigHandler.updateConfigFile();
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (Setting<?> setting : settings) {
            if (setting.textField != null) {
                setting.textField.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Setting<?> setting : settings) {
            if (setting.textField != null) {
                setting.textField.keyTyped(typedChar, keyCode);
            }
        }
    }
}
