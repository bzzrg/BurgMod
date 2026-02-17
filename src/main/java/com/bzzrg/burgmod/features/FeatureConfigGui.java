package com.bzzrg.burgmod.features;

import com.bzzrg.burgmod.config.ConfigHandler;
import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.utils.CustomButton;
import com.bzzrg.burgmod.utils.CustomSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FeatureConfigGui extends GuiScreen {

    private final List<Setting<?>> settings = new ArrayList<>();
    public boolean addStrategyButton = false;

    private static final int borderThickness = 3;
    private static final int borderInline = 20;

    public void addBooleanSetting(String name, Supplier<Boolean> valueGetter, Consumer<Boolean> valueSetter) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, null, null));
    }
    public void addIntSetting(String name, Supplier<Integer> valueGetter, Consumer<Integer> valueSetter, int min, int max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max));
    }
    public void addFloatSetting(String name, Supplier<Float> valueGetter, Consumer<Float> valueSetter, float min, float max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max));
    }
    public void addDoubleSetting(String name, Supplier<Double> valueGetter, Consumer<Double> valueSetter, double min, double max) {
        settings.add(new Setting<>(name, valueGetter, valueSetter, min, max));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {

        int buttonHeight = 23;
        int buttonWidth = 120;
        int buttonGap = 5;

        for (int i = 0; i < settings.size(); i++) {
            Setting<?> setting = settings.get(i);
            int buttonX = borderInline + borderThickness + buttonGap + (buttonWidth + buttonGap) * (i / 10);
            int buttonY = borderInline + borderThickness + buttonGap + (buttonHeight + buttonGap) * (i % 10);

            Object value = setting.valueGetter.get();
            if (value instanceof Boolean) {
                setting.button = new CustomButton(i, buttonX, buttonY, buttonWidth, buttonHeight, setting.name);
                setting.button.displayString = (boolean) value ? "\u00A7a" + setting.name + ": ON" : "\u00A7c" + setting.name + ": OFF";

            } else if (value instanceof Integer) {
                Setting<Integer> castedSetting = (Setting<Integer>) setting;

                GuiSlider slider = new CustomSlider(i, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, castedSetting.valueGetter.get(), false, true, s -> {
                    int newValue = s.getValueInt();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + newValue;
                });
                slider.displayString = castedSetting.name + ": " + slider.getValueInt();
                setting.button = slider;

            } else if (value instanceof Float) {
                Setting<Float> castedSetting = (Setting<Float>) setting;

                GuiSlider slider = new CustomSlider(i, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, castedSetting.valueGetter.get(), true, true, s -> {

                    float newValue = (float) s.getValue();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", newValue);
                });
                slider.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", slider.getValue());
                setting.button = slider;

            } else if (value instanceof Double) {
                Setting<Double> castedSetting = (Setting<Double>) setting;

                GuiSlider slider = new CustomSlider(i, buttonX, buttonY, buttonWidth, buttonHeight,
                        setting.name + ": ", "", castedSetting.min, castedSetting.max, castedSetting.valueGetter.get(), true, true, s -> {

                    double newValue = s.getValue();
                    castedSetting.valueSetter.accept(newValue);
                    s.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", newValue);
                });
                slider.displayString = castedSetting.name + ": " + String.format(Locale.ROOT, "%.2f", slider.getValue());
                setting.button = slider;

            }
            buttonList.add(setting.button);

        }

        final int realHeight = height - 1;
        final int backButtonX = borderInline + borderThickness + buttonGap;
        final int bottomButtonY = realHeight - borderInline - borderThickness - buttonGap - buttonHeight;
        buttonList.add(new CustomButton(settings.size(), backButtonX, bottomButtonY, buttonHeight, buttonHeight, "<"));

        final int realWidth = width - 1;
        final int strategyButtonWidth = 80;
        final int strategyButtonX = realWidth -borderInline - borderThickness - buttonGap - strategyButtonWidth;

        if (addStrategyButton) {
            buttonList.add(new CustomButton(settings.size() + 1, strategyButtonX, bottomButtonY, strategyButtonWidth, buttonHeight, "Edit Strategy"));
        }

    }
    @Override
    @SuppressWarnings("unchecked")
    protected void actionPerformed(GuiButton button) {
        for (Setting<?> setting : settings) {

            Object value = setting.valueGetter.get();

            if (setting.button == button && value instanceof Boolean) {
                Setting<Boolean> castedSetting = (Setting<Boolean>) setting;

                castedSetting.valueSetter.accept(!castedSetting.valueGetter.get());
                setting.button.displayString = castedSetting.valueGetter.get() ? "\u00A7a" + setting.name + ": ON" : "\u00A7c" + setting.name + ": OFF";
                break;

            }
        }
        if (button.id == settings.size()) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        } else if (button.id == settings.size() + 1) {
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

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static class Setting<T> {
        public GuiButton button;

        public final String name;
        public final Supplier<T> valueGetter;
        public final Consumer<T> valueSetter;

        // Slider fields
        public final T min;
        public final T max;

        public Setting(String name, Supplier<T> valueGetter, Consumer<T> valueSetter, T min, T max) {
            this.name = name;
            this.valueGetter = valueGetter;
            this.valueSetter = valueSetter;

            this.min = min;
            this.max = max;
        }
    }
    @Override
    public void onGuiClosed() {
        ConfigHandler.updateConfigFromFields();
        super.onGuiClosed();
    }
}
