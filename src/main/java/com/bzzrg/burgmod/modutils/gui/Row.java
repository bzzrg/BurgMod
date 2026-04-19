package com.bzzrg.burgmod.modutils.gui;

import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

public abstract class Row {

    private final BMListGui gui;

    protected final List<GuiButton> buttons = new ArrayList<>();
    protected final List<CustomTextField> fields = new ArrayList<>();

    protected int topY;

    public Row(BMListGui gui) {
        this.gui = gui;
    }

    public void init() {}

    public void draw(int mouseX, int mouseY, float partialTicks) {}

    public void buttonClicked(GuiButton button) {}

    public void fieldTextChanged(char c, int keyCode, CustomTextField field) {}

    public final int getCenteredY(int elementHeight) {
        return this.topY + (gui.rowHeight - elementHeight) / 2;
    }
}