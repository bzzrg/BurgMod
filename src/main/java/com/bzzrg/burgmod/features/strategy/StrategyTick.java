package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.CustomButton;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.features.strategy.StrategyConfigGui.*;
import static com.bzzrg.burgmod.features.strategy.StrategyJump.strategyJumps;

public class StrategyTick {

    public static final List<StrategyTick> strategyTicks = new ArrayList<>();

    public Set<InputType> correctInputs;

    public final BiMap<InputType, GuiButton> inputButtons = HashBiMap.create();
    public GuiButton duplicateButton = null;
    public GuiButton removeButton = null;

    public StrategyJump jump = null;

    private static final int tickNumGap = 30;

    private StrategyTick(int tickNum, Set<InputType> correctInputs) {
        this.correctInputs = correctInputs;
        strategyTicks.add(tickNum, this);

        for (InputType inputType : InputType.values()) {
            final int tickX = listLeft.get() + tickNumGap + (buttonHeight + buttonGap) * (inputType.ordinal());
            inputButtons.put(inputType, new CustomButton(nextButtonId++, tickX, 0, buttonHeight, buttonHeight, correctInputs.contains(inputType) ? "\u00A7a" + inputType : "\u00A7c" + inputType));
        }

    }

    public static void addLoneTick(int tickNum, Set<InputType> correctInputs) { // Call clampListY and updateListY every time you run this
        StrategyTick tick = new StrategyTick(tickNum, correctInputs);

        final int dupeButX = listLeft.get() + tickNumGap + (buttonHeight + buttonGap) * (InputType.AIR.ordinal() + 1);

        tick.duplicateButton = new CustomButton(nextButtonId++, dupeButX, 0, buttonHeight, buttonHeight, "\u00A7b\u2ffb");
        tick.removeButton = new CustomButton(nextButtonId++, dupeButX + buttonHeight + buttonGap, 0, buttonHeight, buttonHeight, "\u00A74\u2716");

        if (gui != null) {
            gui.displayedButtons.addAll(tick.getButtons());
        }
    }

    public static void addJumpTick(StrategyJump jump, Set<InputType> correctInputs) { // Call clampListY and updateListY every time you run this
        StrategyTick tick = new StrategyTick(strategyTicks.size(), correctInputs);
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
            if (j.ticks.get(0).getTickNum() <= tickNum) {
                listSlot++;
            }
        }

        // Add 1 spacing per visible tick before this tick
        for (StrategyTick t : strategyTicks) {
            if (t.getTickNum() < tickNum && (t.jump == null || t.jump.extended)) {
                listSlot++;
            }
        }

        return listSlot;
    }

    public void remove() {
        strategyTicks.remove(this);
        if (gui != null) {
            gui.displayedButtons.removeAll(getButtons());
        }
    }

    public List<GuiButton> getButtons() {
        List<GuiButton> buttons = new ArrayList<>(inputButtons.values());
        if (duplicateButton != null) buttons.add(duplicateButton);
        if (removeButton != null) buttons.add(removeButton);
        return buttons;
    }

    public enum InputType {
        W,
        A,
        S,
        D,
        SPR,
        SNK,
        AIR
    }
}
