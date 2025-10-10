package com.bzzrg.burgmod.inputstatus.strategyeditor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.bzzrg.burgmod.helpers.ModHelper.scaledX;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.InputType.*;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyEditorGui.*;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyTick.strategyTicks;

public class StrategyJump {

    public static final List<StrategyJump> strategyJumps = new ArrayList<>();

    public final JumpType jumpType;
    public final List<StrategyTick> ticks = new ArrayList<>();

    public final GuiButton removeButton;

    public final GuiButton extendButton;
    public boolean extended = false;

    public final GuiButton run1TButton;
    public boolean run1T = false;

    public final GuiButton cutButton;
    public boolean cut = false;

    public BiMap<InputType, GuiButton> directionButtons = null;
    public Set<InputType> onDirections = null;

    public GuiButton directionButton = null;
    public InputType direction = null;

    public GuiSlider lengthSlider = null;
    public Integer length = null;

    public StrategyJump(JumpType jumpType) {
        this.jumpType = jumpType;

        strategyJumps.add(this);

        AtomicInteger buttonX = new AtomicInteger(LIST_LEFT.get() + scaledX(10));
        int buttonXGap = scaledX(0.5);

        // === Base Buttons ===
        extendButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u2227");
        run1TButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(scaledX(6) + buttonXGap), 0, scaledX(6), BUTTON_HEIGHT.get(), "\u00A7cRun 1t");
        cutButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u00A7cCut");

        if (jumpType == JumpType.JAM || jumpType == JumpType.HH || jumpType == JumpType.PESSI || jumpType == JumpType.FMM) {
            directionButtons = HashBiMap.create();
            onDirections = new HashSet<>();

            directionButtons.put(W, new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u00A7aW"));
            onDirections.add(W);
            directionButtons.put(A, new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u00A7cA"));
            directionButtons.put(S, new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u00A7cS"));
            directionButtons.put(D, new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "\u00A7cD"));
        } else if (jumpType != JumpType.BWMM) {
            directionButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(BUTTON_HEIGHT.get() + buttonXGap), 0, BUTTON_HEIGHT.get(), BUTTON_HEIGHT.get(), "A");
            direction = A;
        }

        // Length slider
        if (jumpType != JumpType.JAM && jumpType != JumpType.WDWA && jumpType != JumpType.BWMM) {
            length = 1;

            lengthSlider = new GuiSlider(nextButtonId++, buttonX.getAndAdd(scaledX(6) + buttonXGap), 0, scaledX(6), BUTTON_HEIGHT.get(), "", "t", 1, 11, 1, false, true, slider -> {
                length = slider.getValueInt();
                lengthSlider.displayString = length + "t";
                syncTicks();
                gui.syncListY();
            });
            lengthSlider.displayString = length + "t";
        }

        // Remove button
        removeButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(scaledX(6) + buttonXGap), 0, scaledX(6), BUTTON_HEIGHT.get(), "Remove");

        // Add new buttons to main button list
        if (gui != null) gui.displayedButtons.addAll(getButtons());
        syncTicks();
    }

    public String getName() {
        switch (jumpType) {
            case JAM: return "Jam";
            case HH: return "HH";
            case PESSI: return "Pessi";
            case FMM: return "FMM";
            case MARK: return "Mark";
            case WAD: return "WAD";
            case WDWA: return "WDWA";
            case BWMM: return "Bwmm";
            case REX: return "Rex";
            case REVERSE_REX: return "Reverse Rex";
            default: return jumpType.name();
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

    // Update jump to match its fields (called when player updates jump fields via its config buttons)
    public void syncTicks() {

        AtomicInteger tickNum = new AtomicInteger(ticks.isEmpty() ? strategyTicks.size() : ticks.get(0).getTickNum());

        TriConsumer<InputType[], List<InputType>, Integer> addTick = (constantInputTypes, varyingInputTypes, iterations) -> {
            if (constantInputTypes == null) constantInputTypes = new InputType[]{};
            if (varyingInputTypes == null) varyingInputTypes = new ArrayList<>();

            List<InputType> mergedInputTypes = new ArrayList<>(Arrays.asList(constantInputTypes));
            mergedInputTypes.addAll(varyingInputTypes);

            IntStream.range(0, iterations).forEach(i ->
                    StrategyTick.addJumpTick(new HashSet<>(mergedInputTypes), tickNum.getAndIncrement(), this)
            );
        };

        ticks.forEach(StrategyTick::remove);
        ticks.clear();

        switch (jumpType) {
            case JAM: {
                List<InputType> directions = new ArrayList<>(onDirections);
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
                List<InputType> directions = new ArrayList<>(onDirections);
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
                List<InputType> directions = new ArrayList<>(onDirections);
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
                List<InputType> directions = new ArrayList<>(onDirections);

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

        if (extended && gui != null) ticks.forEach(t -> gui.displayedButtons.addAll(t.getButtons()));
    }

}
