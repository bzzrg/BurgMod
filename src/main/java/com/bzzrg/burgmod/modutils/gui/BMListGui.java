package com.bzzrg.burgmod.modutils.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BMListGui extends BMConfigGui {

    public final List<Row> rows = new ArrayList<>();
    private final List<GuiButton> rowButtons = new ArrayList<>();

    private float scroll, targetScroll;

    private static final float scrollSpeed = 0.1f;
    private static final int scrollStep = 20;
    public static final int rowHeight = buttonHeight + buttonGap * 2;

    protected int listLeft, listRight, listTop, listBottom;

    private Integer customListRight;

    private static boolean consumedClick = false;
    private Row activeSliderRow = null;
    private int builtRowsHash = 0;

    protected void setListWidth(int width) {
        this.customListRight = borderInline + borderThickness + width;
    }

    @Override
    protected void setConstants() {
        int mid = width / 2;

        listLeft = borderInline + borderThickness;
        listRight = customListRight != null
                ? customListRight
                : mid - borderInline / 2 - borderThickness;
        listTop = borderInline + borderThickness;
        listBottom = height - borderInline - borderThickness;

        configLeft = listRight + borderInline + borderThickness * 2;
        configRight = width - borderInline - borderThickness;
        configTop = borderInline + borderThickness;
        configBottom = height - borderInline - borderThickness;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        super.initGui();
        rebuildRows(null);
        scroll = targetScroll;
    }

    private int rowsHash() {
        int hash = 1;
        for (Row row : rows) {
            hash = 31 * hash + System.identityHashCode(row);
        }
        return hash;
    }

    private void rebuildRows(Row preserve) {
        buttonList.removeAll(rowButtons);
        rowButtons.clear();

        for (Row row : rows) {
            if (row == preserve && !row.buttons.isEmpty()) {
                rowButtons.addAll(row.buttons);
                continue;
            }

            row.buttons.clear();
            row.init();
            rowButtons.addAll(row.buttons);
        }

        buttonList.addAll(rowButtons);
        clampScroll();
        builtRowsHash = rowsHash();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        consumedClick = false;

        boolean inside = !outside(mouseX, mouseY);

        if (!inside) {
            for (GuiButton b : rowButtons) {
                b.enabled = false;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!inside) {
            for (GuiButton b : rowButtons) {
                b.enabled = true;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (consumedClick) return;
        consumedClick = true;

        int before = rowsHash();

        super.actionPerformed(button);

        if (rowsHash() != before) {
            rebuildRows(null);
            return;
        }

        int mouseX = Mouse.getX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;

        if (outside(mouseX, mouseY)) return;

        for (Row row : new ArrayList<>(rows)) {
            if (!row.buttons.contains(button)) continue;

            activeSliderRow = button instanceof GuiSlider ? row : null;

            before = rowsHash();
            row.click(button);

            if (rowsHash() != before && !(button instanceof GuiSlider)) {
                rebuildRows(null);
            }

            return;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

        if (outside(mouseX, mouseY)) return;

        int wheel = Mouse.getEventDWheel();
        if (wheel < 0) targetScroll += scrollStep;
        if (wheel > 0) targetScroll -= scrollStep;

        clampScroll();
    }

    private boolean outside(int x, int y) {
        return x < listLeft || x > listRight ||
                y < listTop || y > listBottom;
    }

    private void clampScroll() {
        int total = rows.size() * rowHeight;
        int visible = listBottom - listTop;

        float max = Math.max(0, total - visible);

        targetScroll = Math.max(0, Math.min(targetScroll, max));
        scroll = Math.max(0, Math.min(scroll, max));
    }

    private void updateScroll() {
        float diff = targetScroll - scroll;
        scroll = Math.abs(diff) < 0.5f
                ? targetScroll
                : scroll + diff * scrollSpeed;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (activeSliderRow != null) {
            if (!Mouse.isButtonDown(0)) {
                activeSliderRow = null;
                if (rowsHash() != builtRowsHash) {
                    rebuildRows(null);
                }
            } else if (!rows.contains(activeSliderRow)) {
                activeSliderRow = null;
                rebuildRows(null);
            } else if (rowsHash() != builtRowsHash) {
                rebuildRows(activeSliderRow);
            }
        } else if (rowsHash() != builtRowsHash) {
            rebuildRows(null);
        }

        drawDefaultBackground();

        clampScroll();
        updateScroll();

        drawListBorder();
        drawConfigBorder();

        int startY = listTop - Math.round(scroll);

        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);

            int newTopY = startY + i * rowHeight;
            int deltaY = newTopY - row.topY;

            row.topY = newTopY;

            for (GuiButton button : row.buttons) {
                button.yPosition += deltaY;
            }
        }

        boolean inside = !outside(mouseX, mouseY);

        int rowMouseX = inside ? mouseX : Integer.MIN_VALUE;
        int rowMouseY = inside ? mouseY : Integer.MIN_VALUE;

        enableScissor(listLeft, listTop, listRight, listBottom);

        for (Row row : rows) {
            row.draw();
        }

        for (GuiButton button : rowButtons) {
            button.drawButton(mc, rowMouseX, rowMouseY);
        }

        disableScissor();

        for (Setting setting : settings) {
            setting.draw(mouseX, mouseY);
        }

        for (GuiButton button : buttonList) {
            if (!rowButtons.contains(button)) {
                button.drawButton(mc, mouseX, mouseY);
            }
        }
    }

    private void drawListBorder() {
        int c = 0xFFFFFFFF;

        drawRect(listLeft - borderThickness, listTop - borderThickness, listRight + borderThickness, listTop, c);
        drawRect(listLeft - borderThickness, listBottom, listRight + borderThickness, listBottom + borderThickness, c);
        drawRect(listLeft - borderThickness, listTop - borderThickness, listLeft, listBottom + borderThickness, c);
        drawRect(listRight, listTop - borderThickness, listRight + borderThickness, listBottom + borderThickness, c);
    }

    private void enableScissor(int left, int top, int right, int bottom) {
        int scale = new ScaledResolution(mc).getScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                left * scale,
                mc.displayHeight - bottom * scale,
                (right - left) * scale,
                (bottom - top) * scale
        );
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}