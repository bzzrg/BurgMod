package com.bzzrg.burgmod.features.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.StrategyListGui.strategyListGui;
import static com.bzzrg.burgmod.modutils.GeneralUtils.getLast;

public class StrategyTick {

    public Set<InputType> correctInputs;
    public StrategyJump jump;
    public StrategyListGui.TickRow row = null;

    public StrategyTick(int index, Set<InputType> correctInputs, StrategyJump jump) {
        this.correctInputs = new HashSet<>(correctInputs);
        this.jump = jump;
        if (jump != null) {

            if (jump.ticks.isEmpty()) {
                jump.ticks.add(0, this);
            } else {
                jump.ticks.add(index - jump.ticks.get(0).getIndex(), this);
            }
        }
        strategyTicks.add(index, this);
    }

    public int getIndex() {
        return strategyTicks.indexOf(this);
    }

    public void remove(boolean removeJumpIfEmpty) {
        strategyTicks.remove(this);
        if (strategyListGui != null && this.row != null) strategyListGui.rows.remove(this.row);
        if (this.jump != null) {
            this.jump.ticks.remove(this);
            if (removeJumpIfEmpty && this.jump.ticks.isEmpty()) this.jump.remove();
        }

    }

    public int getRowIndex() {

        int rowIndex = 0;
        int tickNum = this.getIndex();

        // Tick rows
        for (StrategyTick t : strategyTicks) {
            if (t.getIndex() < tickNum && (t.jump == null || t.jump.extended)) {
                rowIndex++;
            }
        }

        // Jump rows
        for (StrategyJump j : strategyJumps) {
            if (j.ticks.get(0).getIndex() <= tickNum) {
                rowIndex++;
            }
        }

        return rowIndex;
    }



    public static List<Integer> getJumpIndices() {

        List<Integer> jumpIndices = new ArrayList<>();

        for (StrategyTick tick : strategyTicks) {
            if (tick.correctInputs.contains(InputType.JMP)) {
                jumpIndices.add(tick.getIndex());
            }
        }
        return jumpIndices;
    }

    public static Integer getLastJumpIndex() {
        List<Integer> jumpIndices = getJumpIndices();
        return jumpIndices.isEmpty() ? null : getLast(jumpIndices);
    }

}
