package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.features.inputstatus.StrategyRecorder;
import com.bzzrg.burgmod.utils.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.*;
import static com.bzzrg.burgmod.features.inputstatus.StrategyRecorder.recordedStrategy;
import static com.bzzrg.burgmod.features.strategy.StrategyTick.InputType.*;
import static com.bzzrg.burgmod.features.strategy.StrategyTick.addLoneTick;
import static com.bzzrg.burgmod.utils.PluginUtils.*;

public class StrategyConfigGui extends GuiScreen {

    public static StrategyConfigGui gui = null;
    public List<GuiButton> displayedButtons;

    public static int nextButtonId = 7; // Should be the last initiated button's button ID in initGui + 1

    public static final int buttonGap = 5;
    public static final int buttonHeight = 23;
    public static final int sideButtonWidth = 135;
    public static final Supplier<Integer> listLeft = () -> (getScaledWidth() - 341) / 2;
    public static final Supplier<Integer> listRight = () -> getScaledWidth() - (getScaledWidth() - 341) / 2;
    public static final int listTop = 25;
    public static final Supplier<Integer> listBottom = () -> getScaledHeight() - 25;
    public static final int borderThickness = 3;

    private static int dynamicListY = listTop + 6;
    private boolean smoothListScroll = false;

    private GuiTextField jumpTextField;

    @Override
    public void initGui() {

        gui = this;
        displayedButtons = buttonList;

        // Initialize gui
        AtomicInteger buttonY = new AtomicInteger(listTop - borderThickness);
        final int buttonYDif= buttonHeight + buttonGap;

        // Add elements on the left side of the list
        final int leftButtonX = listLeft.get() - borderThickness - buttonGap - sideButtonWidth;

        displayedButtons.add(new CustomButton(1, leftButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight, "Add Tick"));

        displayedButtons.add(new CustomButton(2, leftButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight, "Add Jump"));
        jumpTextField = new GuiTextField(0, fontRendererObj, leftButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight);
        jumpTextField.setMaxStringLength(20);
        jumpTextField.setFocused(true);

        buttonY.set(listTop - borderThickness);

        // Add elements on the right side of the list
        final int rightButtonX = listRight.get() + borderThickness + buttonGap;

        displayedButtons.add(new CustomButton(3, rightButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight, "Clear Strategy"));
        displayedButtons.add(new CustomButton(4, rightButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight, "Mirror Strategy"));
        displayedButtons.add(new CustomButton(5, rightButtonX, buttonY.getAndAdd(buttonYDif), sideButtonWidth, buttonHeight, StrategyRecorder.recording ? "\u00A7eStop Recording" : "Record Strategy"));

        // Add back button at bottom left
        final int realHeight = height - 1;
        displayedButtons.add(new CustomButton(6, buttonGap, realHeight - buttonGap - buttonHeight, buttonHeight, buttonHeight, "<"));

        // IF YOU CHANGE THESE BUTTON IDS ABOVE, CHANGE nextButtonID FIELD AT TOP OF CLASS

        updateStrategyFields();
        clampListY();
        updateListY();

    }

    @Override
    public void onGuiClosed() {
        updateStrategyJson();
        gui = null;
        super.onGuiClosed();
    }

    public void updateListY() {
        final int smoothScrollIncrement = 4;
        boolean finished = true;

        // Update logic for jump slots
        for (StrategyJump j : strategyJumps) {

            // Needed info
            List<GuiButton> jumpButtons = j.getButtons();
            int jumpYDest = dynamicListY + (buttonHeight + buttonGap) * j.getListSlot();

            // Tick scroll logic
            if (smoothListScroll) {
                int distance = jumpYDest - j.removeButton.yPosition;
                if (distance != 0) {

                    if (Math.abs(distance) > smoothScrollIncrement) {
                        jumpButtons.forEach(b -> b.yPosition += (int) Math.signum(distance) * smoothScrollIncrement);
                        finished = false;
                    } else {
                        jumpButtons.forEach(b -> b.yPosition = jumpYDest);
                    }

                }

            } else {
                jumpButtons.forEach(b -> b.yPosition = jumpYDest);
            }

            // Make buttons outside of list zone invisible
            jumpButtons.forEach(button -> button.visible = button.yPosition >= listTop && button.yPosition + button.height <= listBottom.get());
        }

        // Update logic for ticks
        for (StrategyTick t : strategyTicks) {

            // Needed info
            Integer listSlot = t.getListSlot();
            if (listSlot == null) continue;
            int tickYDest = dynamicListY + (buttonHeight + buttonGap) * listSlot;
            List<GuiButton> tickButtons = t.getButtons();

            // Tick scroll logic
            if (smoothListScroll) {
                int distance = tickYDest - t.inputButtons.get(W).yPosition;
                if (distance != 0) {

                    if (Math.abs(distance) > smoothScrollIncrement) {
                        tickButtons.forEach(b -> b.yPosition += (int) Math.signum(distance) * smoothScrollIncrement);
                        finished = false;
                    } else {
                        tickButtons.forEach(b -> b.yPosition = tickYDest);
                    }

                }

            } else {
                tickButtons.forEach(b -> b.yPosition = tickYDest);
            }

            // Make buttons outside of list zone invisible
            tickButtons.forEach(button -> button.visible = button.yPosition >= listTop && button.yPosition + button.height <= listBottom.get());
        }

        if (finished) smoothListScroll = false;
    }

    private void clampListY() {
        int maxTickSlot = strategyTicks.stream().filter(t -> t.getListSlot() != null).mapToInt(StrategyTick::getListSlot).max().orElse(-1);
        int maxJumpSlot = strategyJumps.stream().mapToInt(StrategyJump::getListSlot).max().orElse(-1);
        int totalHeight = (buttonHeight + buttonGap) * (Math.max(maxTickSlot, maxJumpSlot) + 1);

        int maxListY = listTop + buttonGap; // Biggest possible Y coordinate
        int minListY = listBottom.get() - totalHeight; // Smallest possible Y coordinate

        dynamicListY = Math.min(maxListY, Math.max(minListY, dynamicListY));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Draw list border
        drawRect(listLeft.get() - borderThickness, listTop - borderThickness, listRight.get() + borderThickness, listTop, 0xFFFFFFFF); // Top
        drawRect(listLeft.get() - borderThickness, listBottom.get(), listRight.get() + borderThickness, listBottom.get() + borderThickness, 0xFFFFFFFF); // Bottom
        drawRect(listLeft.get() - borderThickness, listTop, listLeft.get(), listBottom.get(), 0xFFFFFFFF); // Left
        drawRect(listRight.get(), listTop, listRight.get() + borderThickness, listBottom.get(), 0xFFFFFFFF); // Right

        jumpTextField.drawTextBox();

        if (smoothListScroll) updateListY(); // Run this method every frame if smooth scroll so that its smooth (inside the method, smooth logic triggers instead of instant logic if smoothListScroll = true)

        final int textXGap = 7;
        final int textYOffset = 7;

        for (StrategyJump j : strategyJumps) {
            GuiButton referenceButton = j.removeButton;
            if (referenceButton.visible) {
                drawRect(listLeft.get(), referenceButton.yPosition - borderThickness, listRight.get(), referenceButton.yPosition + buttonHeight + borderThickness, 0x96000000);
                drawString(fontRendererObj, j.getName(), listLeft.get() + textXGap, referenceButton.yPosition + textYOffset, 0xFFFFFFFF);
            }
        }

        for (StrategyTick t : strategyTicks) {
            GuiButton referenceButton = t.inputButtons.get(W);

            if (referenceButton.visible) {
                // Draw background behind tick if it is visible & in a jump
                if (t.jump != null && t.jump.extended) drawRect(listLeft.get(), referenceButton.yPosition - borderThickness, listRight.get(), referenceButton.yPosition + buttonHeight + borderThickness, 0x96000000);
                // Draw # next to tick if it is visible
                if (t.jump == null || t.jump.extended) drawString(fontRendererObj, "T" + (t.getTickNum() + 1), listLeft.get() + textXGap , referenceButton.yPosition + textYOffset, 0xFFFFFFFF);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }



    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        // Check if cursor is inside strategy area & an editor button is invisible (thus scrolling is needed)
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        boolean isButtonInvisible = strategyTicks.stream().anyMatch(t -> !t.inputButtons.get(W).visible && (t.jump == null || t.jump.extended)) || strategyJumps.stream().anyMatch(j -> !j.removeButton.visible);

        if (!(mouseX >= listLeft.get() && mouseX <= listRight.get() && mouseY >= listTop && mouseY <= listBottom.get()) || !isButtonInvisible) return;

        // Scroll
        final int scrollAmount = 20;
        int mouseScroll = Mouse.getEventDWheel();

        if (mouseScroll < 0) dynamicListY -= scrollAmount;
        if (mouseScroll > 0) dynamicListY += scrollAmount;
        clampListY();

        if (mouseScroll != 0) smoothListScroll = true;
    }

    private Object id = new Object();
    private Object lastId = new Object();

    @Override
    protected void actionPerformed(GuiButton button) {

        // Prevent two actionPerformed running from one mouseClicked
        if (id == lastId) return;
        lastId = id;

        switch (button.id) {
            case 1: { // Add Tick
                StrategyTick.addLoneTick(strategyTicks.size(), new HashSet<>());
                break;
            }
            case 2: { // Add Jump
                StrategyJump.JumpType jumpType;

                try {
                    jumpType = StrategyJump.JumpType.valueOf(jumpTextField.getText().toUpperCase());
                } catch (IllegalArgumentException e) {
                    mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
                    sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cInvalid jump name! List of valid jump names:");

                    for (StrategyJump.JumpType t : StrategyJump.JumpType.values()) {
                        sendMessage("\u00A77- \u00A7e" + t);
                    }
                    return;
                }

                new StrategyJump(jumpType);
                break;
            }

            case 3: { // Clear Strategy
                strategyTicks.forEach(t -> displayedButtons.removeAll(t.getButtons()));
                strategyJumps.forEach(j -> displayedButtons.removeAll(j.getButtons()));
                strategyTicks.clear();
                strategyJumps.clear();
                break;
            }
            case 4: { // Mirror Strategy
                for (StrategyTick t : strategyTicks) {
                    List<StrategyTick.InputType> correctInputs = new ArrayList<>(t.correctInputs);

                    if (t.correctInputs.contains(A)) {
                        correctInputs.remove(A);
                        correctInputs.add(D);
                    }
                    if (t.correctInputs.contains(D)) {
                        correctInputs.remove(D);
                        correctInputs.add(A);
                    }

                    t.correctInputs = new HashSet<>(correctInputs);

                    t.inputButtons.get(A).displayString = t.correctInputs.contains(A) ? "\u00A7aA" : "\u00A7cA";
                    t.inputButtons.get(D).displayString = t.correctInputs.contains(D) ? "\u00A7aD" : "\u00A7cD";
                }

                for (StrategyJump j : strategyJumps) {
                    if (j.directions != null) {
                        List<StrategyTick.InputType> onDirections = new ArrayList<>(j.directions);

                        if (j.directions.contains(A)) {
                            onDirections.remove(A);
                            onDirections.add(D);
                        }
                        if (j.directions.contains(D)) {
                            onDirections.remove(D);
                            onDirections.add(A);
                        }

                        j.directions = new HashSet<>(onDirections);

                        j.directionButtons.get(A).displayString = j.directions.contains(A) ? "\u00A7aA" : "\u00A7cA";
                        j.directionButtons.get(D).displayString = j.directions.contains(D) ? "\u00A7aD" : "\u00A7cD";
                    } else if (j.direction != null) {
                        j.direction = j.direction == A ? D : A;
                        j.directionButton.displayString = j.direction == A ? "A" : "D";
                    }
                }
                break;

            }
            case 5: { // Record Strategy

                StrategyRecorder.recording = !StrategyRecorder.recording;
                button.displayString = StrategyRecorder.recording ? "\u00A7eStop Recording" : "Record Strategy";

                if (!StrategyRecorder.recording && !recordedStrategy.isEmpty()) {

                    while (true) {

                        if (recordedStrategy.isEmpty()) {
                            break;
                        }

                        Set<StrategyTick.InputType> inputs = recordedStrategy.get(recordedStrategy.size() - 1);

                        System.out.println("processing inputs: " + inputs);

                        if (inputs.isEmpty()) {
                            recordedStrategy.remove(inputs);
                        } else {
                            break;
                        }

                    }

                    System.out.println(recordedStrategy);

                    // the thing above me processes correctly, probably an issue under this

                    strategyTicks.clear();
                    strategyJumps.clear();


                    for (Set<StrategyTick.InputType> inputs : recordedStrategy) {
                        addLoneTick(strategyTicks.size(), inputs);
                    }

                    recordedStrategy.clear();
                }

                break;
            }
            case 6: { // Back Button
                Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
                break;
            }
            default: { // Strategy Buttons
                StrategyTick tick = strategyTicks.stream().filter(t -> t.getButtons().contains(button)).findFirst().orElse(null);
                StrategyJump jump = strategyJumps.stream().filter(j -> j.getButtons().contains(button)).findFirst().orElse(null);

                if (tick != null) {

                    int tickNum = tick.getTickNum();

                    if (button == tick.duplicateButton) { // Duplicate Tick
                        StrategyTick.addLoneTick(tickNum + 1, new HashSet<>(tick.correctInputs));

                    } else if (button == tick.removeButton) { // Remove Tick
                        tick.remove();

                    } else {
                        StrategyTick.InputType inputType = tick.inputButtons.inverse().get(button);

                        if (inputType == null) return; // Purely for safety, but in practice, inputType will never = null

                        if (tick.correctInputs.contains(inputType)) {
                            tick.correctInputs.remove(inputType);
                            button.displayString = "\u00A7c" + inputType;
                        } else {
                            tick.correctInputs.add(inputType);
                            button.displayString = "\u00A7a" + inputType;
                        }
                    }
                } else if (jump != null) {

                    if (button == jump.extendButton) { // Extend button for jumps
                        jump.extended = !jump.extended;
                        button.displayString = jump.extended ? "\u2228" : "\u2227";

                        if (jump.extended) {
                            jump.ticks.forEach(t -> displayedButtons.addAll(t.getButtons()));
                        } else {
                            jump.ticks.forEach(t -> displayedButtons.removeAll(t.getButtons()));
                        }
                    } else if (button == jump.run1TButton) { // Run 1t button for jumps
                        jump.run1T = !jump.run1T;
                        jump.run1TButton.displayString = jump.run1T ? "\u00A7aRun 1t" : "\u00A7cRun 1t";

                        if (jump.run1T) {
                            jump.cut = false;
                            jump.cutButton.displayString = "\u00A7cCut";
                        }

                        jump.updateTicks();
                    } else if (button == jump.cutButton) { // Cut button for jumps
                        jump.cut = !jump.cut;
                        jump.cutButton.displayString = jump.cut ? "\u00A7aCut" : "\u00A7cCut";

                        if (jump.cut) {
                            jump.run1T = false;
                            jump.run1TButton.displayString = "\u00A7cRun 1t";
                        }

                        jump.updateTicks();
                    } else if (jump.directionButtons != null && jump.directionButtons.containsValue(button)) { // Direction buttons for jumps
                        StrategyTick.InputType inputType = jump.directionButtons.inverse().get(button);

                        if (jump.directions.contains(inputType)) {
                            jump.directions.remove(inputType);
                            button.displayString = "\u00A7c" + inputType;
                        } else {
                            jump.directions.add(inputType);
                            button.displayString = "\u00A7a" + inputType;
                        }

                        jump.updateTicks();

                    } else if (button == jump.directionButton) { // Direction buttons for jumps
                        jump.direction = jump.direction == StrategyTick.InputType.A ? StrategyTick.InputType.D : StrategyTick.InputType.A;
                        button.displayString = jump.direction == StrategyTick.InputType.A ? "A" : "D";
                        jump.updateTicks();
                    } else if (button == jump.removeButton) { // Remove button for jumps

                        jump.removeTicks();
                        displayedButtons.removeAll(jump.getButtons());
                        strategyJumps.remove(jump);

                    }
                }
            }
        }
        clampListY();
        updateListY();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        jumpTextField.mouseClicked(mouseX, mouseY, mouseButton);
        id = new Object();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        jumpTextField.textboxKeyTyped(typedChar, keyCode);
    }

}
