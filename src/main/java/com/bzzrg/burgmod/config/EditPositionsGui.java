package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.DistanceOffsetConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.InputStatusConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig;
import com.bzzrg.burgmod.config.files.utils.MainConfigSection;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color2;

public class EditPositionsGui extends GuiScreen {

    private static final int buttonHeight = 25;
    private static final int buttonGap = 5;

    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private Label activeLabel = null;

    private final List<Label> labels = new ArrayList<>();

    @Override
    public void initGui() {

        buttonList.add(new CustomButton(0, buttonGap, this.height - buttonGap - buttonHeight, buttonHeight, buttonHeight, "<"));

        labels.clear();

        labels.add(new Label(
                () -> String.format("%sTick #: %sRelocating...", color1, color2),
                () -> StrategyConfig.tickNumLabelX,
                () -> StrategyConfig.tickNumLabelY,
                v -> StrategyConfig.tickNumLabelX = v,
                v -> StrategyConfig.tickNumLabelY = v,
                () -> StrategyConfig.showTickNum
        ));

        labels.add(new Label(
                () -> String.format("%sInput Status: %sRelocating...", color1, color2),
                () -> InputStatusConfig.labelX,
                () -> InputStatusConfig.labelY,
                v -> InputStatusConfig.labelX = v,
                v -> InputStatusConfig.labelY = v,
                () -> InputStatusConfig.enabled
        ));

        labels.add(new Label(
                () -> String.format("%sPerfect 45 Offset (?): %sRelocating...", color1, color2),
                () -> P45OffsetConfig.autoLabelX,
                () -> P45OffsetConfig.autoLabelY,
                v -> P45OffsetConfig.autoLabelX = v,
                v -> P45OffsetConfig.autoLabelY = v,
                () -> P45OffsetConfig.enabled && P45OffsetConfig.showAutoOffset
        ));

        labels.add(new Label(
                () -> String.format("%sPerfect 45 Offset (X?): %sRelocating...", color1, color2),
                () -> P45OffsetConfig.xLabelX,
                () -> P45OffsetConfig.xLabelY,
                v -> P45OffsetConfig.xLabelX = v,
                v -> P45OffsetConfig.xLabelY = v,
                () -> P45OffsetConfig.enabled && P45OffsetConfig.showXOffset
        ));

        labels.add(new Label(
                () -> String.format("%sPerfect 45 Offset (Z?): %sRelocating...", color1, color2),
                () -> P45OffsetConfig.zLabelX,
                () -> P45OffsetConfig.zLabelY,
                v -> P45OffsetConfig.zLabelX = v,
                v -> P45OffsetConfig.zLabelY = v,
                () -> P45OffsetConfig.enabled && P45OffsetConfig.showZOffset
        ));

        labels.add(new Label(
                () -> String.format("%sTurn Accuracy: %sRelocating...", color1, color2),
                () -> TurnHelperConfig.turnAccuracyLabelX,
                () -> TurnHelperConfig.turnAccuracyLabelY,
                v -> TurnHelperConfig.turnAccuracyLabelX = v,
                v -> TurnHelperConfig.turnAccuracyLabelY = v,
                () -> TurnHelperConfig.enabled && TurnHelperConfig.showTurnAccuracy
        ));

        labels.add(new Label(
                () -> String.format("%sDistance Offset: %sRelocating...", color1, color2),
                () -> DistanceOffsetConfig.labelX,
                () -> DistanceOffsetConfig.labelY,
                v -> DistanceOffsetConfig.labelX = v,
                v -> DistanceOffsetConfig.labelY = v,
                () -> DistanceOffsetConfig.enabled
        ));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();

        // draw labels
        for (Label label : labels) {
            if (!label.visible.get()) continue;

            String text = label.text.get();
            int x = label.getX.getAsInt();
            int y = label.getY.getAsInt();

            int width = fontRendererObj.getStringWidth(text);
            int height = fontRendererObj.FONT_HEIGHT;

            drawRect(x - 2, y - 2, x + width + 2, y + height + 2, 0x40FFFFFF);
            fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
        }

        // mouse pressed
        if (Mouse.isButtonDown(0)) {

            if (!dragging) {
                for (Label label : labels) {
                    if (!label.visible.get()) continue;

                    String text = label.text.get();
                    int x = label.getX.getAsInt();
                    int y = label.getY.getAsInt();

                    int width = fontRendererObj.getStringWidth(text);
                    int height = fontRendererObj.FONT_HEIGHT;

                    if (mouseX >= x && mouseX <= x + width &&
                            mouseY >= y && mouseY <= y + height) {

                        activeLabel = label;
                        dragging = true;

                        dragOffsetX = mouseX - x;
                        dragOffsetY = mouseY - y;
                        break;
                    }
                }
            }

        } else {
            dragging = false;
            activeLabel = null;
        }

        // dragging update
        if (dragging && activeLabel != null) {
            activeLabel.setX.accept(mouseX - dragOffsetX);
            activeLabel.setY.accept(mouseY - dragOffsetY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        }
    }

    @Override
    public void onGuiClosed() {
        MainConfigSection.updateFile();
        super.onGuiClosed();
    }

    private static class Label {
        public final Supplier<String> text;

        public final IntSupplier getX;
        public final IntSupplier getY;

        public final IntConsumer setX;
        public final IntConsumer setY;

        public final Supplier<Boolean> visible;

        public Label(Supplier<String> text,
                     IntSupplier getX,
                     IntSupplier getY,
                     IntConsumer setX,
                     IntConsumer setY,
                     Supplier<Boolean> visible) {

            this.text = text;
            this.getX = getX;
            this.getY = getY;
            this.setX = setX;
            this.setY = setY;
            this.visible = visible;
        }
    }
}