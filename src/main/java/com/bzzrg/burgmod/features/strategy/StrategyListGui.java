package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusHandler;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler;
import com.bzzrg.burgmod.features.turnhelper.TurnHelperHandler;
import com.bzzrg.burgmod.modutils.gui.BMListGui;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import com.bzzrg.burgmod.modutils.gui.CustomSlider;
import com.bzzrg.burgmod.modutils.gui.Row;
import com.bzzrg.burgmod.modutils.resetting.ResetHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.*;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.features.strategy.InputType.*;
import static com.bzzrg.burgmod.features.strategy.StrategyRecorder.recordedStrategy;
import static com.bzzrg.burgmod.modutils.GeneralUtils.*;

public class StrategyListGui extends BMListGui {

    public static StrategyListGui strategyListGui = null;

    private static final int tickNumGap = 30;
    private static final int jumpTypeGap = 55;

    private static final int run1TButLength = 40;
    private static final int lengthSliderLength = 40;
    private static final int ceilingHeightButLength = 50;

    public static void clearStrategy() {
        strategyTicks.clear();
        strategyJumps.clear();
        if (strategyListGui != null) strategyListGui.rows.clear();
    }


    public void resetRows() {
        strategyTicks.forEach(t -> t.row = null);
        strategyJumps.forEach(j -> j.row = null);
        rows.clear();

        strategyJumps.forEach(j -> j.extended = false);
        for (StrategyTick tick : strategyTicks) {
            if (tick.jump == null) {
                addTickRow(tick);
            } else if (tick == tick.jump.ticks.get(0)) {
                addJumpRow(tick.jump);
            }
        }
    }

    public StrategyListGui() {
        strategyListGui = this;
        this.setSettingWidth(120);
        this.setListWidth(jumpTypeGap + buttonGap*10 + buttonHeight*7 + run1TButLength + lengthSliderLength + ceilingHeightButLength);

        resetRows();

        this.addActionButton("Add Tick", b -> addTickRow(new StrategyTick(strategyTicks.size(), new HashSet<>(), null)));
        this.addStringSetting("Add Jump", () -> "", s -> {
            StrategyJump strategyJump;
            try {
                strategyJump = new StrategyJump(strategyJumps.size(), JumpType.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException e) {
                bmChat("\u00A7cInvalid jump type! List of valid jump types:");
                chat("\u00A7e" + Arrays.stream(JumpType.values()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")));
                playErrorSound();
                return;
            }

            addJumpRow(strategyJump);

        }, "Jump Type");
        this.addActionButton("Clear Strat", b -> StrategyListGui.clearStrategy());
        this.addActionButton("Trim Strat", b -> {
            List<StrategyTick> reversed = new ArrayList<>(strategyTicks);
            Collections.reverse(reversed);

            StrategyTick last = null;

            for (StrategyTick tick : reversed) {
                if (last != null) {

                    Set<InputType> clone = new HashSet<>(last.correctInputs);
                    clone.add(JMP);
                    if (tick.correctInputs.equals(clone) || tick.correctInputs.equals(last.correctInputs)) {
                        last.remove(true);
                    } else {
                        break;
                    }

                }

                last = tick;
            }
        });
        this.addActionButton("Mirror Strat", b -> {
            for (StrategyTick tick : strategyTicks) {
                List<InputType> correctInputs = new ArrayList<>(tick.correctInputs);

                if (tick.correctInputs.contains(A)) {
                    correctInputs.remove(A);
                    correctInputs.add(D);
                }
                if (tick.correctInputs.contains(D)) {
                    correctInputs.remove(D);
                    correctInputs.add(A);
                }

                tick.correctInputs = new HashSet<>(correctInputs);

                if (tick.row != null) {
                    tick.row.inputButtons.get(A).displayString = tick.correctInputs.contains(A) ? "\u00A7aA" : "\u00A7cA";
                    tick.row.inputButtons.get(D).displayString = tick.correctInputs.contains(D) ? "\u00A7aD" : "\u00A7cD";
                }
            }

            for (StrategyJump jump : strategyJumps) {
                if (jump.wasdDirections != null) {
                    List<InputType> onDirections = new ArrayList<>(jump.wasdDirections);

                    if (jump.wasdDirections.contains(A)) {
                        onDirections.remove(A);
                        onDirections.add(D);
                    }
                    if (jump.wasdDirections.contains(D)) {
                        onDirections.remove(D);
                        onDirections.add(A);
                    }

                    jump.wasdDirections = new HashSet<>(onDirections);

                    if (jump.row != null) {
                        jump.row.wasdButtons.get(A).displayString = jump.wasdDirections.contains(A) ? "\u00A7aA" : "\u00A7cA";
                        jump.row.wasdButtons.get(D).displayString = jump.wasdDirections.contains(D) ? "\u00A7aD" : "\u00A7cD";
                    }

                } else if (jump.adDirection != null) {
                    jump.adDirection = jump.adDirection == A ? D : A;
                    if (jump.row != null) {
                        jump.row.adButton.displayString = jump.adDirection == A ? "A" : "D";
                    }

                }
            }
        });

        this.addActionButton(StrategyRecorder.recording ? "\u00A7eStop Recording" : "Record Strat", b -> {
            StrategyRecorder.recording = !StrategyRecorder.recording;
            b.displayString = StrategyRecorder.recording ? "\u00A7eStop Recording" : "Record Strat";

            if (StrategyRecorder.recording) {
                ResetHandler.movedSinceReset = false;

                InputStatusHandler.label = color1 + "Input Status: \u00A7r?";

                P45OffsetHandler.autoLabel = color1 + "Perfect 45 Offset (?): \u00A7r?";
                P45OffsetHandler.xLabel = color1 + "Perfect 45 Offset (X?): \u00A7r?";
                P45OffsetHandler.zLabel = color1 + "Perfect 45 Offset (Z?): \u00A7r?";

                TurnHelperHandler.turnAccuracyLabel = color1 + "Turn Accuracy: \u00A7r?";

            } else {

                List<Set<InputType>> reversed = new ArrayList<>(recordedStrategy);
                Collections.reverse(reversed);

                for (Set<InputType> inputs : reversed) {
                    if (inputs.isEmpty()) {
                        recordedStrategy.remove(recordedStrategy.size()-1);
                    } else {
                        break;
                    }
                }

                clearStrategy();

                for (Set<InputType> inputs : recordedStrategy) {
                    addTickRow(new StrategyTick(strategyTicks.size(), inputs, null));
                }

                recordedStrategy.clear();
            }
        });
        this.addActionButton("Preview Strat", b -> {
            mc.displayGuiScreen(null);
            if (strategyTicks.stream().noneMatch(tick -> tick.correctInputs.contains(SPR) && tick.correctInputs.contains(SNK))) {
                StrategyPreviewer.draw();
            }
        });
        this.addBooleanSetting("Show Tick #", () -> showTickNum, v -> showTickNum = v);
        this.nextColumn();
        StrategySavingHandler.addSavingSettings(this);

    }

    @Override
    public void onGuiClosed() {
        StrategyConfig.instance.updateFile();
        strategyTicks.forEach(t -> t.row = null);
        strategyJumps.forEach(j -> j.row = null);
        strategyListGui = null;

        if (!StrategyRecorder.recording) {
            if (strategyTicks.isEmpty()) {
                bmChat("\u00A7cWARN: Your strategy is empty! Features requiring a strategy will not work as intended.");
                playErrorSound();
            }
            for (StrategyTick tick : strategyTicks) {
                if (tick.correctInputs.contains(SPR) && tick.correctInputs.contains(SNK)) {
                    bmChat("\u00A7cWARN: Tick #" + (tick.getIndex()+1) + " from your strategy has SPR and SNK selected which is invalid! Features requiring a strategy will not work as intended.");
                    playErrorSound();
                }
            }
        }

        super.onGuiClosed();
    }

    public void addTickRow(StrategyTick strategyTick) {
        TickRow row = new TickRow(strategyTick);
        this.rows.add(strategyTick.getRowIndex(), row);
        strategyTick.row = row;
    }

    public void addJumpRow(StrategyJump strategyJump) {
        JumpRow row = new JumpRow(strategyJump);
        this.rows.add(strategyJump.getRowIndex(), row);
        strategyJump.row = row;
    }

    public class TickRow extends Row {
        public final StrategyTick tick;

        public final BiMap<InputType, GuiButton> inputButtons = HashBiMap.create();
        public GuiButton duplicateButton;
        public GuiButton removeButton;

        public TickRow(StrategyTick tick) {
            super(StrategyListGui.this);
            this.tick = tick;
        }

        @Override
        public void init() {

            int nextButtonId = buttonList.size();
            final int centeredY = getCenteredY(buttonHeight);

            for (InputType inputType : InputType.values()) {
                final int tickX = listLeft + tickNumGap + (buttonHeight + buttonGap) * (inputType.ordinal());
                GuiButton inputButton = new CustomButton(nextButtonId++, tickX, centeredY, buttonHeight, buttonHeight,
                        tick.correctInputs.contains(inputType) ? "\u00A7a" + inputType : "\u00A7c" + inputType);
                this.inputButtons.put(inputType, inputButton);
            }

            final int buttonX2 = listLeft + tickNumGap + (buttonHeight + buttonGap) * (JMP.ordinal() + 1);
            this.duplicateButton = new CustomButton(nextButtonId++, buttonX2, centeredY, buttonHeight, buttonHeight, "\u00A7b\u2ffb");
            this.removeButton = new CustomButton(nextButtonId, buttonX2 + buttonHeight + buttonGap, centeredY, buttonHeight, buttonHeight, "\u00A74\u2716");

            buttons.addAll(inputButtons.values());
            buttons.add(duplicateButton);
            buttons.add(removeButton);
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
            if (tick.jump != null) drawRect(listLeft, topY, listRight, topY + rowHeight, 0x96000000);

            int textWidth = fontRendererObj.getStringWidth("T" + (tick.getIndex() + 1));
            int tickNumX = (listLeft + tickNumGap / 2) - textWidth / 2;
            drawString(fontRendererObj, "T" + (tick.getIndex() + 1), tickNumX, getCenteredY(fontRendererObj.FONT_HEIGHT), 0xFFFFFFFF);
        }

        @Override
        public void buttonClicked(GuiButton button) {

            if (button == duplicateButton) { // Duplicate Tick
                addTickRow(new StrategyTick(tick.getIndex(), new HashSet<>(tick.correctInputs), tick.jump));

            } else if (button == removeButton) { // Remove Tick

                tick.remove(true);

            } else {
                InputType inputType = inputButtons.inverse().get(button);
                if (inputType == null) return; // Purely for safety, but in practice, inputType will never = null

                if (tick.correctInputs.contains(inputType)) {
                    tick.correctInputs.remove(inputType);
                    button.displayString = "\u00A7c" + inputType;
                } else {
                    tick.correctInputs.add(inputType);
                    button.displayString = "\u00A7a" + inputType;
                }
            }
        }

    }

    public class JumpRow extends Row {
        public final StrategyJump jump;

        public GuiButton extendButton;

        public BiMap<InputType, GuiButton> wasdButtons = null;
        public GuiButton adButton = null;

        public GuiButton run1TButton;

        public GuiSlider lengthSlider = null;

        public GuiButton ceilingHeightButton;

        public GuiButton duplicateButton;
        public GuiButton removeButton;

        public JumpRow(StrategyJump jump) {
            super(StrategyListGui.this);
            this.jump = jump;
        }

        // call jump.updateTicks() after this, not included in method for consistency, also this method is just meant to change ur jump's ceilingheight constant, thats it
        private void fixCeiling() {
            if (jump.length == null || jump.type == JumpType.HH) return; // if jump has no length option, or if jump is HH, ceiling can't be broken so there's nothing to fix
            while (true) {
                if (jump.ceilingHeight.getJumpLength() <= jump.length) {

                    CeilingHeight next = getNextEnumValue(jump.ceilingHeight);
                    jump.ceilingHeight = next;
                    ceilingHeightButton.displayString = next.toString();

                } else {
                    break;
                }
            }
        }

        @Override
        public void init() {

            int nextButtonId = buttonList.size();
            final int centeredY = getCenteredY(buttonHeight);

            AtomicInteger buttonX = new AtomicInteger(listLeft + jumpTypeGap); // Atomic int so I can use getAndAdd

            extendButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight, jump.extended ? "\u2228" : "\u2227");

            if (jump.type == JumpType.JAM || jump.type == JumpType.HH || jump.type == JumpType.PESSI || jump.type == JumpType.FMM) {
                wasdButtons = HashBiMap.create();

                wasdButtons.put(W, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight,
                        jump.wasdDirections.contains(W) ? "\u00A7aW" : "\u00A7cW"));
                wasdButtons.put(A, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight,
                        jump.wasdDirections.contains(A) ? "\u00A7aA" : "\u00A7cA"));
                wasdButtons.put(S, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight,
                        jump.wasdDirections.contains(S) ? "\u00A7aS" : "\u00A7cS"));
                wasdButtons.put(D, new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight,
                        jump.wasdDirections.contains(D) ? "\u00A7aD" : "\u00A7cD"));

            } else if (jump.type != JumpType.BWMM) {
                adButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight, jump.adDirection.name());
            }

            if (jump.type != JumpType.JAM && jump.type != JumpType.WDWA && jump.type != JumpType.BWMM) {
                lengthSlider = new CustomSlider(nextButtonId++, buttonX.getAndAdd(lengthSliderLength + buttonGap), centeredY, lengthSliderLength, buttonHeight, "", "t", 1, 11, jump.length, false, true, slider -> {
                    jump.length = slider.getValueInt();
                    fixCeiling();
                    jump.updateTicks();
                });
            }

            run1TButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(run1TButLength + buttonGap), centeredY, run1TButLength, buttonHeight, jump.run1T ? "\u00A7aRun 1t" : "\u00A7cRun 1t");

            ceilingHeightButton = new CustomButton(nextButtonId++, buttonX.getAndAdd(ceilingHeightButLength + buttonGap), centeredY, ceilingHeightButLength, buttonHeight, jump.ceilingHeight.toString());

            duplicateButton = new CustomButton(nextButtonId, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight, "\u00A7b\u2ffb");
            removeButton = new CustomButton(nextButtonId, buttonX.getAndAdd(buttonHeight + buttonGap), centeredY, buttonHeight, buttonHeight, "\u00A74\u2716");

            buttons.add(extendButton);
            if (wasdButtons != null) buttons.addAll(wasdButtons.values());
            if (adButton != null) buttons.add(adButton);
            buttons.add(run1TButton);
            buttons.add(ceilingHeightButton);
            buttons.add(duplicateButton);
            buttons.add(removeButton);
            if (lengthSlider != null) buttons.add(lengthSlider);
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
            drawRect(listLeft, topY, listRight, topY + rowHeight, 0x96000000);

            int textWidth = fontRendererObj.getStringWidth(jump.getName());
            int tickNumX = (listLeft + jumpTypeGap / 2) - textWidth / 2;
            drawString(fontRendererObj, jump.getName(), tickNumX, getCenteredY(fontRendererObj.FONT_HEIGHT), 0xFFFFFFFF);
        }

        @Override
        public void buttonClicked(GuiButton button) {

            if (button == extendButton) { // Extend button for jumps
                jump.extended = !jump.extended;
                button.displayString = jump.extended ? "\u2228" : "\u2227";
                jump.fixExtension();

            } else if (wasdButtons != null && wasdButtons.containsValue(button)) { // wasd buttons for jumps
                InputType inputType = wasdButtons.inverse().get(button);

                if (jump.wasdDirections.contains(inputType)) {
                    jump.wasdDirections.remove(inputType);
                    button.displayString = "\u00A7c" + inputType;
                } else {
                    jump.wasdDirections.add(inputType);
                    button.displayString = "\u00A7a" + inputType;
                }

                jump.updateTicks();

            } else if (button == adButton) { // ad button for jumps
                jump.adDirection = jump.adDirection == InputType.A ? InputType.D : InputType.A;
                button.displayString = jump.adDirection == InputType.A ? "A" : "D";
                jump.updateTicks();

            } else if (button == run1TButton) { // Run 1t button for jumps
                jump.run1T = !jump.run1T;
                run1TButton.displayString = jump.run1T ? "\u00A7aRun 1t" : "\u00A7cRun 1t";
                jump.updateTicks();

            } else if (button == ceilingHeightButton) {
                CeilingHeight next = getNextEnumValue(jump.ceilingHeight);
                jump.ceilingHeight = next;
                ceilingHeightButton.displayString = next.toString();
                fixCeiling();
                jump.updateTicks();

            } else if (button == duplicateButton) {
                this.jump.extended = false;
                button.displayString = "\u2227";
                this.jump.fixExtension();
                StrategyJump jump = new StrategyJump(strategyJumps.indexOf(this.jump), this.jump.type);

                jump.wasdDirections = new HashSet<>(this.jump.wasdDirections);
                jump.adDirection = this.jump.adDirection;
                jump.run1T = this.jump.run1T;
                jump.length = this.jump.length;
                jump.ceilingHeight = this.jump.ceilingHeight;

                jump.removeTicks();
                int i = this.jump.ticks.get(0).getIndex();

                for (StrategyTick tick : this.jump.ticks) {
                    new StrategyTick(i, new HashSet<>(tick.correctInputs), jump);
                    i++;
                }

                addJumpRow(jump);
            } else if (button == removeButton) { // Remove button for jumps
                jump.remove();
            }

        }
    }


}
