package com.bzzrg.burgmod.inputstatus.strategyeditor;

import com.bzzrg.burgmod.config.InputStatusConfig;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.helpers.ModHelper.scaledX;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyEditorGui.*;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyJump.strategyJumps;

public class StrategyTick {

    public static final List<StrategyTick> strategyTicks = new ArrayList<>();

    public Set<InputType> correctInputs;

    public final BiMap<InputType, GuiButton> inputButtons = HashBiMap.create();
    public GuiButton duplicateButton = null;
    public GuiButton removeButton = null;

    public StrategyJump jump = null;

    private StrategyTick(Set<InputType> correctInputs, int tickNum) {
        this.correctInputs = correctInputs;
        strategyTicks.add(tickNum, this);

        for (InputType inputType : InputType.values()) {
            final int tickX = LIST_LEFT.get() + scaledX(3) + (BUTTON_HEIGHT.get() + scaledX(0.5)) * (inputType.ordinal());
            inputButtons.put(inputType, new CustomButton(nextButtonId++, tickX, 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), correctInputs.contains(inputType) ? "\u00A7a" + inputType : "\u00A7c" + inputType));
        }

        inputButtons.get(InputType.SPR).enabled = !InputStatusConfig.toggleSprintMode;
    }

    public static void addLoneTick(Set<InputType> correctInputs, int tickNum) {
        StrategyTick tick = new StrategyTick(correctInputs, tickNum);

        final int duplicateX = LIST_LEFT.get() + scaledX(3) + (BUTTON_HEIGHT.get() + scaledX(0.5)) * (InputType.AIR.ordinal() + 1);

        tick.duplicateButton = new CustomButton(nextButtonId++, duplicateX, 0, scaledX(7), BUTTON_HEIGHT.get(), "Duplicate");
        tick.removeButton = new CustomButton(nextButtonId++, duplicateX + scaledX(7) + scaledX(0.5), 0, scaledX(6), BUTTON_HEIGHT.get(), "Remove");

        if (gui != null) gui.displayedButtons.addAll(tick.getButtons());
    }

    public static void addJumpTick(Set<InputType> correctInputs, int tickNum, StrategyJump jump) {
        StrategyTick tick = new StrategyTick(correctInputs, tickNum);
        jump.ticks.add(tick);
        tick.jump = jump;
    }

    public int getTickNum() {
        return strategyTicks.indexOf(this);
    }

    public Integer getListSlot() {
        if (jump != null && !jump.extended) return null;

        int tickNum = getTickNum();
        int listSlot = 0;

        // Add 1 spacing per jump before this tick
        for (StrategyJump j : strategyJumps) {
            if (j.ticks.get(0).getTickNum() <= tickNum) listSlot++;
        }

        // Add 1 spacing per visible tick before this tick
        for (StrategyTick t : strategyTicks) {
            if (t.getTickNum() < tickNum && (t.jump == null || t.jump.extended)) listSlot++;
        }

        return listSlot;
    }

    public void remove() {
        strategyTicks.remove(this);
        if (gui != null) gui.displayedButtons.removeAll(getButtons());
    }

    public List<GuiButton> getButtons() {
        List<GuiButton> buttons = new ArrayList<>(inputButtons.values());
        if (duplicateButton != null) buttons.add(duplicateButton);
        if (removeButton != null) buttons.add(removeButton);
        return buttons;
    }

}
