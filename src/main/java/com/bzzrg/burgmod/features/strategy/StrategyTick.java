package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.gui.CustomButton;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.StrategyConfigGui.*;

public class StrategyTick {

    public Set<InputType> correctInputs;

    public final BiMap<InputType, GuiButton> inputButtons = HashBiMap.create();
    public GuiButton duplicateButton;
    public GuiButton removeButton;

    public StrategyJump jump = null;

    private static final int tickNumGap = 30;

    private StrategyTick(int tickNum, Set<InputType> correctInputs) {
        this.correctInputs = correctInputs;
        strategyTicks.add(tickNum, this);

        for (InputType inputType : InputType.values()) {
            final int tickX = listLeft.get() + tickNumGap + (buttonHeight + buttonGap) * (inputType.ordinal());
            this.inputButtons.put(inputType, new CustomButton(nextButtonId++, tickX, 0, buttonHeight, buttonHeight, correctInputs.contains(inputType) ? "\u00A7a" + inputType : "\u00A7c" + inputType));
        }

        final int dupeButX = listLeft.get() + tickNumGap + (buttonHeight + buttonGap) * (InputType.AIR.ordinal() + 1);
        this.duplicateButton = new CustomButton(nextButtonId++, dupeButX, 0, buttonHeight, buttonHeight, "\u00A7b\u2ffb");
        this.removeButton = new CustomButton(nextButtonId++, dupeButX + buttonHeight + buttonGap, 0, buttonHeight, buttonHeight, "\u00A74\u2716");


    }

    public static void addLoneTick(int tickNum, Set<InputType> correctInputs) { // Call clampListY and updateListY every time you run this
        StrategyTick tick = new StrategyTick(tickNum, correctInputs);
        if (gui != null) {
            gui.displayedButtons.addAll(tick.getButtons());
        }
    }

    // Call clampListY and updateListY every time you run this
    public static void addJumpTick(int tickNum, Set<InputType> correctInputs, StrategyJump jump) {  // tickNum needed for tick insertion if jump isn’t the latest thing in strategy and it's refreshed.

        Integer firstTickNum = jump.ticks.isEmpty() ? null : jump.ticks.get(0).getTickNum();

        StrategyTick tick = new StrategyTick(tickNum, correctInputs);

        if (firstTickNum == null) {
            jump.ticks.add(tick);
        } else {
            jump.ticks.add(tickNum - firstTickNum, tick);
        }

        tick.jump = jump;

        if (gui != null && jump.extended) {
            gui.displayedButtons.addAll(tick.getButtons());
        }

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
        if (this.jump != null) {
            this.jump.ticks.remove(this);
        }
        strategyTicks.remove(this);
        if (gui != null) gui.displayedButtons.removeAll(getButtons());

    }

    public List<GuiButton> getButtons() {
        List<GuiButton> buttons = new ArrayList<>(inputButtons.values());
        if (duplicateButton != null) buttons.add(duplicateButton);
        if (removeButton != null) buttons.add(removeButton);
        return buttons;
    }

    public static StrategyTick getJumpTick(int distanceFromLast) {

        List<StrategyTick> reversedTicks = new ArrayList<>(strategyTicks);
        Collections.reverse(reversedTicks);

        int jumpCount = -1;
        boolean foundAir = false;

        for (StrategyTick tick : reversedTicks) {

            if (tick.correctInputs.contains(InputType.AIR)) {

                // Different logic for very first tick if its a jump since there is no tick right before it to check if it has no air
                if (tick.getTickNum() == 0) {
                    jumpCount++;
                    if (jumpCount == distanceFromLast) {
                        return tick;
                    }
                }

                foundAir = true;
            }

            if (foundAir && !tick.correctInputs.contains(InputType.AIR)) {
                jumpCount++;
                foundAir = false;

                if (jumpCount == distanceFromLast) {
                    return strategyTicks.get(tick.getTickNum() + 1);
                }
            }
        }

        return null;
    }
}
