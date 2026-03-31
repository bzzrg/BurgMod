package com.bzzrg.burgmod.modutils.gui;

import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

public abstract class Row {

    protected final List<GuiButton> buttons = new ArrayList<>();
    protected int topY;

    public void init() {}

    public void draw() {}

    public void click(GuiButton button) {}

    public final int getCenteredY(int elementHeight) {
        return this.topY + (BMListGui.rowHeight - elementHeight) / 2;
    }
}