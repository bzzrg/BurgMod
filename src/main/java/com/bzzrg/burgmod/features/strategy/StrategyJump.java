package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.CustomButton;
import com.bzzrg.burgmod.utils.CustomSlider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.StrategyConfigGui.*;
import static com.bzzrg.burgmod.features.strategy.InputType.*;


public class StrategyJump {

    public final JumpType type;
    public final List<StrategyTick> ticks = new ArrayList<>();

    public final GuiButton removeButton;

    public final GuiButton extendButton;
    public boolean extended = false;

    public final GuiButton run1TButton;
    public boolean run1T = false;

    public final GuiButton cutButton;
    public boolean cut = false;

    public BiMap<InputType, GuiButton> directionButtons = null;
    public Set<InputType> directions = null;

    public GuiButton directionButton = null;
    public InputType direction = null;

    public GuiSlider lengthSlider = null;
    public Integer length = null;

    public StrategyJump(JumpType type) {
        this.type = type;

        strategyJumps.add(this);

        final int buttonGap = 5;
        final int run1TButLength = 40;
        final int lengthSliderLength = 40;

        AtomicInteger buttonX = new AtomicInteger(listLeft.get() + 55); // Atomic int so I can use getAndAdd

        // === Base Buttons ===
        extendButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u2227");
        run1TButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(run1TButLength + buttonGap), 0, run1TButLength, buttonHeight, "\u00A7cRun 1t");
        cutButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A7cCut");

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

        // Length slider
        if (type != JumpType.JAM && type != JumpType.WDWA && type != JumpType.BWMM) {
            length = 1;

            lengthSlider = new CustomSlider(nextButtonId++, buttonX.getAndAdd(lengthSliderLength + buttonGap), 0, lengthSliderLength, buttonHeight, "", "t", 1, 11, length, false, true, slider -> {
                length = slider.getValueInt();
                lengthSlider.displayString = length + "t";
                this.updateTicks();
                gui.updateListY();
            });
            lengthSlider.displayString = length + "t";
        }

        // Remove button
        removeButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), 0, buttonHeight, buttonHeight, "\u00A74\u2716");

        // Add new buttons to main button list
        if (gui != null) {
            gui.displayedButtons.addAll(getButtons());
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
        buttons.add(cutButton);
        if (directionButtons != null) buttons.addAll(directionButtons.values());
        if (directionButton != null) buttons.add(directionButton);
        buttons.add(removeButton);
        if (lengthSlider != null) buttons.add(lengthSlider);

        return buttons;
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
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

        int firstTickIndex = ticks.isEmpty() ? strategyTicks.size() : ticks.get(0).getTickNum();

        TriConsumer<InputType[], List<InputType>, Integer> addTick = (constantInputTypes, varyingInputTypes, iterations) -> {
            if (constantInputTypes == null) constantInputTypes = new InputType[]{};
            if (varyingInputTypes == null) varyingInputTypes = new ArrayList<>();

            List<InputType> mergedInputTypes = new ArrayList<>(Arrays.asList(constantInputTypes));
            mergedInputTypes.addAll(varyingInputTypes);

            IntStream.range(0, iterations).forEach(i -> StrategyTick.addJumpTick(this, new HashSet<>(mergedInputTypes), firstTickIndex + ticks.size()));
        };

        this.removeTicks();

        switch (type) {
            case JAM: {
                List<InputType> directions = new ArrayList<>(this.directions);
                if (directions.contains(W)) directions.add(SPR);

                if (cut) {
                    addTick.accept(new InputType[]{AIR}, directions, 1);
                } else {
                    addTick.accept(new InputType[]{AIR}, directions, 11);
                    addTick.accept(null, directions, 1);
                    if (run1T) addTick.accept(null, directions, 1);
                }

                break;
            }
            case HH: {
                List<InputType> directions = new ArrayList<>(this.directions);
                if (directions.contains(W)) directions.add(SPR);

                addTick.accept(null, directions, length);

                if (cut) {
                    addTick.accept(new InputType[]{AIR}, directions, 1);
                } else {
                    addTick.accept(new InputType[]{AIR}, directions, 11);
                    addTick.accept(null, directions, 1);
                    if (run1T) addTick.accept(null, directions, 1);
                }

                break;
            }
            case PESSI: {
                List<InputType> directions = new ArrayList<>(this.directions);
                if (directions.contains(W)) directions.add(SPR);

                addTick.accept(new InputType[]{AIR}, null, length);

                if (cut) {
                    if (length == 11) {
                        addTick.accept(null, directions, 1);
                    } else {
                        addTick.accept(new InputType[]{AIR}, directions, 1);
                    }
                } else {
                    addTick.accept(new InputType[]{AIR}, directions, 11 - length);
                    addTick.accept(null, directions, 1);
                    if (run1T) addTick.accept(null, directions, 1);
                }

                break;
            }
            case FMM: {
                List<InputType> directions = new ArrayList<>(this.directions);

                addTick.accept(new InputType[]{AIR}, directions, length);

                if (cut) {
                    if (length == 11) {
                        addTick.accept(new InputType[]{SPR}, directions, 1);
                    } else {
                        addTick.accept(new InputType[]{SPR, AIR}, directions, 1);
                    }
                } else {
                    addTick.accept(new InputType[]{SPR, AIR}, directions, 11 - length);
                    addTick.accept(new InputType[]{SPR}, directions, 1);
                    if (run1T) addTick.accept(new InputType[]{SPR}, directions, 1);
                }

                break;
            }
            case MARK: {
                List<InputType> direction = Collections.singletonList(this.direction);

                addTick.accept(new InputType[]{AIR}, direction, length);

                if (cut) {
                    if (length == 11) {
                        addTick.accept(new InputType[]{W, SPR}, direction, 1);
                    } else {
                        addTick.accept(new InputType[]{W, SPR, AIR}, direction, 1);
                    }
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, direction, 11 - length);
                    addTick.accept(new InputType[]{W, SPR}, direction, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, direction, 1);
                }

                break;
            }
            case WAD: {
                List<InputType> direction = Collections.singletonList(this.direction);

                addTick.accept(new InputType[]{W, A, D, SPR, AIR}, null, length);

                if (cut) {
                    if (length == 11) {
                        addTick.accept(new InputType[]{W, SPR}, direction, 1);
                    } else {
                        addTick.accept(new InputType[]{W, SPR, AIR}, direction, 1);
                    }
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, direction, 11 - length);
                    addTick.accept(new InputType[]{W, SPR}, direction, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, direction, 1);
                }

                break;
            }
            case WDWA: {
                List<InputType> direction = Collections.singletonList(this.direction);

                if (cut) {
                    addTick.accept(new InputType[]{W, SPR, AIR}, direction, 1);
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, direction, 11);
                    addTick.accept(new InputType[]{W, SPR}, direction, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, direction, 1);
                }

                break;
            }
            case BWMM: {
                addTick.accept(new InputType[]{S, AIR}, null, 11);
                addTick.accept(new InputType[]{S}, null, 2);

                if (cut) {
                    addTick.accept(new InputType[]{W, SPR, AIR}, null, 1);
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, null, 11);
                    addTick.accept(new InputType[]{W, SPR}, null, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, null, 1);
                }

                break;
            }
            case REX: {
                List<InputType> direction = Collections.singletonList(this.direction);

                addTick.accept(new InputType[]{S, AIR}, null, 11);
                addTick.accept(new InputType[]{S}, null, 2);
                addTick.accept(new InputType[]{W, SPR, AIR}, direction, length);

                if (cut) {
                    if (length == 11) {
                        addTick.accept(new InputType[]{W, SPR}, null, 1);
                    } else {
                        addTick.accept(new InputType[]{W, SPR, AIR}, null, 1);
                    }
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, null, 11 - length);
                    addTick.accept(new InputType[]{W, SPR}, null, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, null, 1);
                }

                break;
            }
            case REVERSE_REX: {
                List<InputType> direction = Collections.singletonList(this.direction);

                addTick.accept(new InputType[]{S, AIR}, direction, length);
                addTick.accept(new InputType[]{S, AIR}, null, 11 - length);
                addTick.accept(new InputType[]{S}, null, 2);

                if (cut) {
                    addTick.accept(new InputType[]{W, SPR, AIR}, null, 1);
                } else {
                    addTick.accept(new InputType[]{W, SPR, AIR}, null, 11);
                    addTick.accept(new InputType[]{W, SPR}, null, 1);
                    if (run1T) addTick.accept(new InputType[]{W, SPR}, null, 1);
                }

                break;
            }
        }

        // Since all ticks were just recreated using addJumpTick (based on jump settings), they weren't added to displayed buttons, so this adds them if the jump they are in is in extended view
        if (extended && gui != null) ticks.forEach(t -> gui.displayedButtons.addAll(t.getButtons()));
    }
}
