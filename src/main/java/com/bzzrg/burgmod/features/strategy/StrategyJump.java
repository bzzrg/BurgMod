package com.bzzrg.burgmod.features.strategy;

import java.util.*;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.*;
import static com.bzzrg.burgmod.features.strategy.StrategyListGui.strategyListGui;


public class StrategyJump {

    public final JumpType type;


    public Set<InputType> wasdDirections = null;
    public InputType adDirection = null;

    public boolean run1T = false;

    public Integer length = null;

    public CeilingHeight ceilingHeight = CeilingHeight.NO_CEIL;

    public final List<StrategyTick> ticks = new ArrayList<>();

    public boolean extended = false;

    public StrategyListGui.JumpRow row = null;

    public StrategyJump(int index, JumpType type) {
        this.type = type;
        strategyJumps.add(index, this);

        if (type == JumpType.JAM || type == JumpType.HH || type == JumpType.PESSI || type == JumpType.FMM) {
            wasdDirections = new HashSet<>();
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

    public void removeTicks() {
        new ArrayList<>(this.ticks).forEach(t -> t.remove(false));
    }

    public void remove() {
        this.removeTicks();
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
        this.removeTicks();
        InputType[] wasd = wasdDirections != null ? wasdDirections.toArray(new InputType[0]) : arr();
        InputType[] wasdSpr = Arrays.asList(wasd).contains(W) ? merge(wasd, SPR) : wasd;
        InputType[] ad = adDirection != null ? new InputType[]{adDirection} : arr();

        switch (type) {
            case JAM:
                addJump(1, wasdSpr, wasdSpr, run1T);
                break;
            case HH:
                addTicks(length, wasdSpr);
                addJump(1, wasdSpr, wasdSpr, run1T);
                break;
            case PESSI:
                addJump(length, arr(), wasdSpr, run1T);
                break;
            case FMM:
                addJump(length, wasd, merge(wasd, SPR), run1T);
                break;
            case MARK:
                addJump(length, ad, merge(ad, W, SPR), run1T);
                break;
            case WAD: {
                addJump(length, arr(W, A, D, SPR), merge(ad, W, SPR), run1T);
                break;
            }
            case WDWA:
                addJump(1, merge(ad, W, SPR), merge(ad, W, SPR), run1T);
                break;
            case BWMM:
                addJump(1, arr(S), arr(S), true);
                addJump(1, arr(W, SPR), arr(W, SPR), run1T);
                break;
            case REX:
                addJump(1, arr(S), arr(S), true);
                addJump(length, merge(ad, W, SPR), arr(W, SPR), run1T);
                break;
            case REVERSE_REX:
                addJump(length, merge(ad, S), arr(S), true);
                addJump(1, arr(W, SPR), arr(W, SPR), run1T);
                break;
        }
        this.fixExtension();
    }

    // 1 jump tick (jumpInputs + JMP), firstLength-1 first ticks, jumpLength-firstLength second ticks, optional run1T (ex: firstLength = 2t for 2t pessi, ex: second length = 8t for 3bc 3t mark)
    private void addJump(int firstLength, InputType[] firstInputs, InputType[] secondInputs, boolean run1T) {

        addTicks(1, merge(firstInputs, JMP));
        addTicks(firstLength - 1, firstInputs);
        addTicks(ceilingHeight.getJumpLength() - firstLength, secondInputs);
        if (run1T) addTicks(1, secondInputs);
    }

    private InputType[] arr(InputType... inputs) { return inputs; }

    private InputType[] merge(InputType[] base, InputType... extra) {
        if (base == null || base.length == 0) return extra;
        InputType[] out = Arrays.copyOf(base, base.length + extra.length);
        System.arraycopy(extra, 0, out, base.length, extra.length);
        return out;
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

}
