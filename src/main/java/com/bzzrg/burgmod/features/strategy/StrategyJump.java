package com.bzzrg.burgmod.features.strategy;

import java.util.*;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.*;
import static com.bzzrg.burgmod.features.strategy.StrategyListGui.strategyListGui;


public class StrategyJump {

    public final JumpType type;
    public final List<StrategyTick> ticks = new ArrayList<>();
    public StrategyListGui.JumpRow row = null;

    public boolean extended = false;

    public Set<InputType> wasdDirections = null;
    public InputType adDirection = null;

    public boolean run1T = false;

    public Integer length = null;

    public StrategyJump(JumpType type) {
        this.type = type;
        strategyJumps.add(this);

        if (type == JumpType.JAM || type == JumpType.HH || type == JumpType.PESSI || type == JumpType.FMM) {
            wasdDirections = new HashSet<>(Collections.singleton(W));
        } else if (type != JumpType.BWMM) {
            adDirection = A;
        }

        if (type != JumpType.JAM && type != JumpType.WDWA && type != JumpType.BWMM) {
            length = 1;
        }

        this.updateTicks();
    }

    public String getName() {
        switch (type) {
            case JAM: return "Jam";
            case HH: return "HH";
            case PESSI: return "Pessi";
            case FMM: return "FMM";
            case MARK: return "Mark";
            case WAD: return "WAD";
            case WDWA: return "WDWA";
            case BWMM: return "Bwmm";
            case REX: return "Rex";
            case REVERSE_REX: return "R. Rex";
            default: return type.name(); // Never triggers in practice, failsafe
        }
    }

    public void remove() {
        new ArrayList<>(this.ticks).forEach(t -> t.remove(false));
        if (strategyListGui != null && this.row != null) strategyListGui.rows.remove(this.row);
        strategyJumps.remove(this);
    }

    public void fixExtension() {
        if (this.extended) {
            for (StrategyTick tick : this.ticks) {
                if (strategyListGui != null && tick.row == null) strategyListGui.addTickRow(tick);
            }
        } else {
            for (StrategyTick tick : this.ticks) {
                if (tick.row != null) {
                    if (strategyListGui != null) strategyListGui.rows.remove(tick.row);
                    tick.row = null;
                }
            }
        }
    }

    public int getRowIndex() {
        return this.ticks.get(0).getRowIndex() - 1;
    }

    // Update jump to match its fields (called when player updates jump fields via its config buttons)
    private static int firstTickNum = -1;
    public void updateTicks() {

        firstTickNum = ticks.isEmpty() ? strategyTicks.size() : ticks.get(0).getIndex();
        new ArrayList<>(this.ticks).forEach(t -> t.remove(false));

        InputType[] wasd = wasdDirections != null ? wasdDirections.toArray(new InputType[0]) : null;
        InputType[] wasdSpr = wasd != null && Arrays.asList(wasd).contains(W) ? merge(wasd, SPR) : wasd;
        InputType[] ad = adDirection != null ? new InputType[]{adDirection} : null;

        switch (type) {

            case JAM: {
                addTicks(1, merge(wasdSpr, JMP));
                addTicks(11, wasdSpr);
                if (run1T) addTicks(1, wasdSpr);
                break;
            }

            case HH: {
                addTicks(length, wasdSpr);
                addTicks(1, merge(wasdSpr, JMP));
                addTicks(11, wasdSpr);
                if (run1T) addTicks(1, wasdSpr);
                break;
            }

            case PESSI: {
                addTicks(1, JMP);
                addTicks(length - 1);
                addTicks(12 - length, wasdSpr);
                if (run1T) addTicks(1, wasdSpr);
                break;
            }

            case FMM: {
                addTicks(1, merge(wasd, JMP));
                addTicks(length - 1, wasd);
                addTicks(12 - length, merge(wasd, SPR));
                if (run1T) addTicks(1, merge(wasd, SPR));
                break;
            }

            case MARK: {
                addTicks(1, merge(ad, JMP));
                addTicks(length - 1, ad);
                addTicks(12 - length, merge(ad, W, SPR));
                if (run1T) addTicks(1, merge(ad, W, SPR));
                break;
            }

            case WAD: {
                addTicks(1, W, A, D, SPR, JMP);
                addTicks(length - 1, W, A, D, SPR);
                addTicks(12 - length, merge(ad, W, SPR));
                if (run1T) addTicks(1, merge(ad, W, SPR));
                break;
            }

            case WDWA: {
                addTicks(1, merge(ad, W, SPR, JMP));
                addTicks(11, merge(ad, W, SPR));
                if (run1T) addTicks(1, merge(ad, W, SPR));
                break;
            }

            case BWMM: {
                addTicks(1, S, JMP);
                addTicks(12, S);

                addTicks(1, W, SPR, JMP);
                addTicks(11, W, SPR);
                if (run1T) addTicks(1, W, SPR);
                break;
            }

            case REX: {
                addTicks(1, S, JMP);
                addTicks(12, S);

                addTicks(1, merge(ad, W, SPR, JMP));
                addTicks(length - 1, merge(ad, W, SPR));
                addTicks(12 - length, W, SPR);
                if (run1T) addTicks(1, W, SPR);
                break;
            }

            case REVERSE_REX: {
                addTicks(1, merge(ad, S, JMP));
                addTicks(length - 1, merge(ad, S));
                addTicks(13 - length, S);

                addTicks(1, W, SPR, JMP);
                addTicks(11, W, SPR);
                if (run1T) addTicks(1, W, SPR);
                break;
            }
        }

        this.fixExtension();
    }

    private void addTicks(int count, InputType... inputs) {
        Set<InputType> set = new HashSet<>(Arrays.asList(inputs));

        for (int i = 0; i < count; i++) {
            int tickNum = firstTickNum + ticks.size();
            if (this.extended) {
                if (strategyListGui != null) strategyListGui.addTickRow(new StrategyTick(tickNum, set, this));
            } else {
                new StrategyTick(tickNum, set, this);
            }
        }

    }

    private InputType[] merge(InputType[] base, InputType... extra) {
        InputType[] out = Arrays.copyOf(base, base.length + extra.length);
        System.arraycopy(extra, 0, out, base.length, extra.length);
        return out;
    }

}
