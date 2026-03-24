package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.gui.CustomButton;
import com.bzzrg.burgmod.utils.gui.CustomSlider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.*;
import static com.bzzrg.burgmod.features.strategy.StrategyConfigGui.*;


public class StrategyJump {

    public final JumpType type;
    public final List<StrategyTick> ticks = new ArrayList<>();

    public final GuiButton extendButton;
    public boolean extended = false;

    public BiMap<InputType, GuiButton> directionButtons = null;
    public Set<InputType> directions = null;

    public GuiButton directionButton = null;
    public InputType direction = null;

    public final GuiButton run1TButton;
    public boolean run1T = false;

    public GuiSlider lengthSlider = null;
    public Integer length = null;

    public final GuiButton removeButton;

    public StrategyJump(JumpType type) {
        this.type = type;

        strategyJumps.add(this);

        final int buttonGap = 5;
        final int run1TButLength = 40;
        final int lengthSliderLength = 40;

        AtomicInteger buttonX = new AtomicInteger(listLeft.get() + 55); // Atomic int so I can use getAndAdd

        // === Base Buttons ===
        extendButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u2227");

        if (type == JumpType.JAM || type == JumpType.HH || type == JumpType.PESSI || type == JumpType.FMM) {
            directionButtons = HashBiMap.create();
            directions = new HashSet<>();

            directionButtons.put(W, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A7aW"));
            directions.add(W);
            directionButtons.put(A, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A7cA"));
            directionButtons.put(S, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A7cS"));
            directionButtons.put(D, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A7cD"));
        } else if (type != JumpType.BWMM) {
            directionButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "A");
            direction = A;
        }

        run1TButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(run1TButLength + buttonGap), 0, run1TButLength, buttonHeight, "\u00A7cRun 1t");

        // Length slider
        if (type != JumpType.JAM && type != JumpType.WDWA && type != JumpType.BWMM) {
            length = 1;

            lengthSlider = new CustomSlider(nextButtonId++, buttonX.getAndAdd(lengthSliderLength + buttonGap), 0, lengthSliderLength, buttonHeight, "", "t", 1, 11, length, false, true, slider -> {
                length = slider.getValueInt();
                lengthSlider.displayString = length + "t";
                this.updateTicks();
                gui.clampListY();
                gui.updateListY();
            });
            lengthSlider.displayString = length + "t";
        }

        // Remove button
        removeButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A74\u2716");

        // Add new buttons to main button list
        if (gui != null) {
            gui.displayedButtons.addAll(this.getButtons());
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

    public int getListSlot() {
        int listSlot = 0;

        // Add 1 spacing per jump before this jump
        for (StrategyJump jump : strategyJumps) {
            if (strategyJumps.indexOf(jump) < strategyJumps.indexOf(this)) listSlot++;
        }

        // Add 1 spacing per visible tick before this jump
        for (StrategyTick tick : strategyTicks) {
            if (tick.getTickNum() < ticks.get(0).getTickNum() && (tick.jump == null || tick.jump.extended)) listSlot++;
        }

        return listSlot;
    }

    // Gets all buttons for this jump (not including the buttons for the ticks within the jump)
    public List<GuiButton> getButtons() {
        List<GuiButton> buttons = new ArrayList<>();

        buttons.add(extendButton);
        buttons.add(run1TButton);
        if (directionButtons != null) buttons.addAll(directionButtons.values());
        if (directionButton != null) buttons.add(directionButton);
        buttons.add(removeButton);
        if (lengthSlider != null) buttons.add(lengthSlider);

        return buttons;
    }

    public void removeTicks() {
        for (StrategyTick tick : new ArrayList<>(ticks)) {
            tick.remove();
        }
        ticks.clear();

    }

    public void remove() {
        this.removeTicks();
        if (gui != null) gui.displayedButtons.removeAll(this.getButtons());
        strategyJumps.remove(this);
    }

    // Update jump to match its fields (called when player updates jump fields via its config buttons)
    public void updateTicks() {

        this.removeTicks();

        switch (type) {
            case JAM: {
                InputType[] movement = directionsWithSprint();

                addTicks(1, merge(movement, JMP));
                addTicks(10, movement);

                addTicks(1, movement);
                if (run1T) addTicks(1, movement);

                break;
            }
            case HH: {
                InputType[] movement = directionsWithSprint();

                addTicks(length, movement);

                addTicks(1, merge(movement, JMP));
                addTicks(10, movement);

                addTicks(1, movement);
                if (run1T) addTicks(1, movement);

                break;
            }
            case PESSI: {
                InputType[] movement = directionsWithSprint();

                addTicks(1, JMP);
                addTicks(length - 1);

                addTicks(1, merge(movement, JMP));
                addTicks(11 - length - 1, movement);

                addTicks(1, movement);
                if (run1T) addTicks(1, movement);

                break;
            }
            case FMM: {
                InputType[] movement = directionsArray();

                addTicks(1, merge(movement, JMP));
                addTicks(length - 1, movement);

                addTicks(1, merge(movement, SPR, JMP));
                addTicks(11 - length - 1, merge(movement, SPR));

                addTicks(1, merge(movement, SPR));
                if (run1T) addTicks(1, merge(movement, SPR));

                break;
            }
            case MARK: {
                InputType[] strafe = singleDirectionArray();

                addTicks(1, merge(strafe, JMP));
                addTicks(length - 1, strafe);

                addTicks(1, merge(strafe, W, SPR, JMP));
                addTicks(11 - length - 1, merge(strafe, W, SPR));

                addTicks(1, merge(strafe, W, SPR));
                if (run1T) addTicks(1, merge(strafe, W, SPR));

                break;
            }
            case WAD: {
                InputType[] strafe = singleDirectionArray();

                addTicks(1, W, A, D, SPR, JMP);
                addTicks(length - 1, W, A, D, SPR);

                addTicks(1, merge(strafe, W, SPR, JMP));
                addTicks(11 - length - 1, merge(strafe, W, SPR));

                addTicks(1, merge(strafe, W, SPR));
                if (run1T) addTicks(1, merge(strafe, W, SPR));

                break;
            }
            case WDWA: {
                InputType[] strafe = singleDirectionArray();

                addTicks(1, merge(strafe, W, SPR, JMP));

                addTicks(1, merge(strafe, W, SPR, JMP));
                addTicks(10, merge(strafe, W, SPR));

                addTicks(1, merge(strafe, W, SPR));
                if (run1T) addTicks(1, merge(strafe, W, SPR));

                break;
            }
            case BWMM: {
                addTicks(1, S, JMP);
                addTicks(10, S);

                addTicks(2, S);

                addTicks(1, W, SPR, JMP);
                addTicks(10, W, SPR);

                addTicks(1, W, SPR);
                if (run1T) addTicks(1, W, SPR);

                break;
            }
            case REX: {
                InputType[] strafe = singleDirectionArray();

                addTicks(1, S, JMP);
                addTicks(10, S);

                addTicks(2, S);

                addTicks(1, merge(strafe, W, SPR, JMP));
                addTicks(length - 1, merge(strafe, W, SPR));

                addTicks(1, W, SPR, JMP);
                addTicks(11 - length - 1, W, SPR);

                addTicks(1, W, SPR);
                if (run1T) addTicks(1, W, SPR);

                break;
            }
            case REVERSE_REX: {
                InputType[] strafe = singleDirectionArray();

                addTicks(1, merge(strafe, S, JMP));
                addTicks(length - 1, merge(strafe, S));

                addTicks(1, S, JMP);
                addTicks(11 - length - 1, S);

                addTicks(2, S);

                addTicks(1, W, SPR, JMP);
                addTicks(10, W, SPR);

                addTicks(1, W, SPR);
                if (run1T) addTicks(1, W, SPR);

                break;
            }
        }

        // Since all ticks were just recreated using addJumpTick (based on jump settings), they weren't added to displayed buttons, so this adds them if the jump they are in is in extended view
        if (extended && gui != null) ticks.forEach(t -> gui.displayedButtons.addAll(t.getButtons()));
    }

    private void addTicks(int count, InputType... inputs) {
        for (int i = 0; i < count; i++) {
            Set<InputType> set = new HashSet<>(Arrays.asList(inputs));
            int tickNum = ticks.isEmpty() ? strategyTicks.size() : ticks.get(0).getTickNum() + ticks.size();
            StrategyTick.addJumpTick(tickNum, set, this);
        }
    }

    private InputType[] directionsArray() {
        return directions.toArray(new InputType[0]);
    }

    private InputType[] directionsWithSprint() {
        List<InputType> result = new ArrayList<>(directions);
        if (result.contains(W)) result.add(SPR);
        return result.toArray(new InputType[0]);
    }

    private InputType[] singleDirectionArray() {
        return new InputType[]{direction};
    }

    private InputType[] merge(InputType[] base, InputType... extra) {
        InputType[] out = Arrays.copyOf(base, base.length + extra.length);
        System.arraycopy(extra, 0, out, base.length, extra.length);
        return out;
    }

}
