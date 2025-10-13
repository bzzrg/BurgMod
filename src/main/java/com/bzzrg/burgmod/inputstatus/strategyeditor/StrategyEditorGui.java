package com.bzzrg.burgmod.inputstatus.strategyeditor;

import com.bzzrg.burgmod.config.InputStatusConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.helpers.ModHelper.*;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.InputType.*;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyJump.strategyJumps;
import static com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyTick.strategyTicks;

public class StrategyEditorGui extends GuiScreen {

    public static StrategyEditorGui gui = null;
    public List<GuiButton> displayedButtons;
    private boolean smoothListScroll = false;

    public static final Supplier<Integer> LIST_LEFT = () -> scaledX(25);
    public static final Supplier<Integer> LIST_TOP = () -> scaledY(5);
    public static final Supplier<Integer> LIST_RIGHT = () -> scaledX(75);
    public static final Supplier<Integer> LIST_BOTTOM = () -> scaledY(95);
    public static final Supplier<Integer> BUTTON_HEIGHT = () -> scaledY(5);

    public static int listY = scaledY(5) + scaledY(0.5);
    public static int nextButtonId = 10;

    private GuiTextField jumpTextField;

    public static void loadStrategy() {

        strategyTicks.clear();
        strategyJumps.clear();

        for (String tickString : InputStatusConfig.strategy) {

            String[] splitTick = tickString.split(":", -1);

            if (splitTick.length == 1) {
                try {
                    Set<InputType> correctInputs = tickString.isEmpty() ? new HashSet<>() : Arrays.stream(tickString.split(",")).map(InputType::valueOf).collect(Collectors.toSet());
                    StrategyTick.addLoneTick(correctInputs, strategyTicks.size());
                } catch (IllegalArgumentException e) {
                    sendMessage("\u00A74Failed to load strategy array into static fields: Lone tick contains invalid inputs");
                    return;
                }
            } else {

                String[] tickInfo = splitTick[0].split(",", -1);

                // Get jump's jump type and creates a base off of it
                StrategyJump jump;
                try {
                    jump = new StrategyJump(JumpType.valueOf(tickInfo[0]));
                } catch (IllegalArgumentException e) {
                    sendMessage("\u00A74Failed to load strategy array into static fields: Jump contains invalid jump type");
                    return;
                }

                // Get & set jump's run 1t status
                boolean run1T = tickInfo[1].equals("true");
                if (!run1T && !tickInfo[1].equals("false")) {
                    sendMessage("\u00A74Failed to load strategy array into static fields: Jump contains invalid run 1t boolean");
                    return;
                }
                jump.run1T = run1T;
                if (run1T) jump.run1TButton.displayString = "\u00A7aRun 1t";

                // Get & set jump's cut status
                boolean cut = tickInfo[2].equals("true");
                if (!cut && !tickInfo[2].equals("false")) {
                    sendMessage("\u00A74Failed to load strategy array into static fields: Jump contains invalid cut boolean");
                    return;
                }
                jump.cut = cut;
                if (cut) jump.cutButton.displayString = "\u00A7aCut";

                // Get & set jump's direction
                try {
                    Set<InputType> directions = tickInfo[3].isEmpty() ? new HashSet<>() : Arrays.stream(tickInfo[3].split("")).map(InputType::valueOf).collect(Collectors.toSet());
                    if (jump.onDirections != null) {
                        jump.onDirections = directions;
                        jump.directionButtons.get(W).displayString = "\u00A7cW";
                        directions.forEach(inputType -> jump.directionButtons.get(inputType).displayString = "\u00A7a" + inputType);
                    } else if (jump.direction != null) {
                        jump.direction = new ArrayList<>(directions).get(0);
                        jump.directionButton.displayString = jump.direction.name();
                    }

                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    if (e instanceof IllegalArgumentException) {
                        sendMessage("\u00A74Failed to load strategy array into static fields: Jump contains invalid direction(s)");
                        return;
                    }
                }

                // Get & set jump's length
                try {
                    jump.length = Integer.parseInt(tickInfo[4]);
                    jump.lengthSlider.setValue(jump.length);
                    jump.lengthSlider.displayString = jump.length + "t";
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    if (e instanceof NumberFormatException) {
                        sendMessage("\u00A74Failed to load strategy array into static fields: Jump contains invalid length");
                        return;
                    }
                }

                // Get & set jump's ticks
                jump.ticks.forEach(StrategyTick::remove);
                jump.ticks.clear();
                String[] jumpTickStrings = splitTick[1].split(";", -1);

                for (String jumpTickString : jumpTickStrings) {
                    try {
                        Set<InputType> correctInputs = jumpTickString.isEmpty() ? new HashSet<>() : Arrays.stream(jumpTickString.split(",")).map(InputType::valueOf).collect(Collectors.toSet());
                        StrategyTick.addJumpTick(correctInputs, strategyTicks.size(), jump);
                    } catch (IllegalArgumentException e) {
                        sendMessage("\u00A74Failed to load strategy array into static fields: Jump tick '" + jumpTickString + "' contains invalid inputs");
                        return;
                    }
                }

            }
        }
    }

    @Override
    public void initGui() {

        gui = this;
        displayedButtons = buttonList;

        // Initialize gui
        final int buttonY = LIST_TOP.get() - 3;
        final int buttonYDif= BUTTON_HEIGHT.get() + scaledY(1);
        final int buttonWidth = scaledX(20);

        // Add elements on the left side of the list
        final int leftButtonX = LIST_LEFT.get() - scaledX(2) - buttonWidth;

        buttonList.add(new CustomButton(1, leftButtonX, buttonY, buttonWidth, BUTTON_HEIGHT.get(), "Add Tick"));
        buttonList.add(new CustomButton(2, leftButtonX, buttonY + buttonYDif, buttonWidth, BUTTON_HEIGHT.get(), "Mirror Strategy"));
        buttonList.add(new CustomButton(3, leftButtonX, buttonY + buttonYDif * 2, buttonWidth, BUTTON_HEIGHT.get(), "Clear Strategy"));
        buttonList.add(new CustomButton(4, leftButtonX, buttonY + buttonYDif * 3, buttonWidth, BUTTON_HEIGHT.get(), InputStatusConfig.toggleSprintMode ? "\u00A7aToggle Sprint Mode: ON" : "\u00A7cToggle Sprint Mode: OFF"));
        buttonList.add(new CustomButton(5, leftButtonX, buttonY + buttonYDif * 5, buttonWidth, BUTTON_HEIGHT.get(), "Add Jump"));

        jumpTextField = new GuiTextField(0, fontRendererObj, leftButtonX, buttonY + buttonYDif * 4, buttonWidth, BUTTON_HEIGHT.get());
        jumpTextField.setMaxStringLength(20);
        jumpTextField.setFocused(true);

        // Add elements on the right side of the list
        final int rightButtonX = LIST_RIGHT.get() + scaledX(2);

        buttonList.add(new CustomButton(6, rightButtonX, buttonY, buttonWidth, BUTTON_HEIGHT.get(), InputStatusConfig.label ? "\u00A7aLabel: ON" : "\u00A7cLabel: OFF"));

        GuiSlider xSlider = new GuiSlider(7, rightButtonX, buttonY + buttonYDif, buttonWidth, BUTTON_HEIGHT.get(), "Label X: ", "%", 0, 100, InputStatusConfig.labelX, true, true, slider -> {
            InputStatusConfig.labelX = slider.getValue();
            InputStatusConfig.save();
            slider.displayString = "Label X: " + Math.round(InputStatusConfig.labelX * 100d) / 100d + "%";
        });
        xSlider.displayString = "Label X: " + Math.round(InputStatusConfig.labelX * 100d) / 100d + "%";
        buttonList.add(xSlider);

        GuiSlider ySlider = new GuiSlider(8, rightButtonX, buttonY + buttonYDif * 2, buttonWidth, BUTTON_HEIGHT.get(), "Label Y: ", "%", 0, 100, InputStatusConfig.labelY, true, true, slider -> {
            InputStatusConfig.labelY = slider.getValue();
            InputStatusConfig.save();
            slider.displayString = "Label Y: " + Math.round(InputStatusConfig.labelY * 100d) / 100d + "%";
        });
        ySlider.displayString = "Label Y: " + Math.round(InputStatusConfig.labelY * 100d) / 100d + "%";
        buttonList.add(ySlider);

        buttonList.add(new CustomButton(9, rightButtonX, buttonY + buttonYDif * 3, buttonWidth, BUTTON_HEIGHT.get(), InputStatusConfig.shortenLabel ? "\u00A7aShorten Label: ON" : "\u00A7cShorten Label: OFF"));

        nextButtonId = 10;

        loadStrategy();
        clampListHeight();
        syncListY();
    }

    @Override
    public void onGuiClosed() {

        List<String> tickStrings = new ArrayList<>();
        List<StrategyJump> addedJumps = new ArrayList<>();

        for (StrategyTick t : strategyTicks) {
            if (t.jump == null) {
                String tickString = t.correctInputs.stream().map(InputType::name).collect(Collectors.joining(","));
                tickStrings.add(tickString);
            } else if (!addedJumps.contains(t.jump)) {
                StringBuilder jumpString = new StringBuilder(t.jump.jumpType + "," + t.jump.run1T + "," + t.jump.cut + ",");
                if (t.jump.onDirections != null) {
                    jumpString.append(t.jump.onDirections.stream().map(InputType::name).collect(Collectors.joining()));
                } else if (t.jump.direction != null) {
                    jumpString.append(t.jump.direction);
                }
                if (t.jump.length != null) jumpString.append(",").append(t.jump.length);

                jumpString.append(":");

                jumpString.append(t.jump.ticks.stream()
                        .map(jumpTick -> jumpTick.correctInputs.stream().map(InputType::name).collect(Collectors.joining(",")))
                        .collect(Collectors.joining(";")));

                tickStrings.add(jumpString.toString());
                addedJumps.add(t.jump);
            }
        }

        InputStatusConfig.strategy = tickStrings.toArray(new String[0]);
        InputStatusConfig.save();

        displayedButtons = null;

        super.onGuiClosed();
    }

    public void syncListY() {
        final int scrollAmount = scaledY(0.6);
        boolean finished = true;

        // Update logic for jump slots
        for (StrategyJump j : strategyJumps) {

            // Needed info
            List<GuiButton> jumpButtons = j.getButtons();
            int jumpYDest = listY + (BUTTON_HEIGHT.get() + scaledY(0.5)) * j.getListSlot();

            // Tick scroll logic
            if (smoothListScroll) {
                int distance = jumpYDest - j.removeButton.yPosition;
                if (distance != 0) {

                    if (Math.abs(distance) > scrollAmount) {
                        jumpButtons.forEach(b -> b.yPosition += (int) Math.signum(distance) * scrollAmount);
                        finished = false;
                    } else {
                        jumpButtons.forEach(b -> b.yPosition = jumpYDest);
                    }

                }

            } else {
                jumpButtons.forEach(b -> b.yPosition = jumpYDest);
            }

            // Make buttons outside of list zone invisible
            jumpButtons.forEach(button -> button.visible = button.yPosition >= LIST_TOP.get() && button.yPosition + button.height <= LIST_BOTTOM.get());
        }

        // Update logic for ticks
        for (StrategyTick t : strategyTicks) {

            // Needed info
            Integer listSlot = t.getListSlot();
            if (listSlot == null) continue;
            int tickYDest = listY + (BUTTON_HEIGHT.get() + scaledY(0.5)) * listSlot;
            List<GuiButton> tickButtons = t.getButtons();

            // Tick scroll logic
            if (smoothListScroll) {
                int distance = tickYDest - t.inputButtons.get(W).yPosition;
                if (distance != 0) {

                    if (Math.abs(distance) > scrollAmount) {
                        tickButtons.forEach(b -> b.yPosition += (int) Math.signum(distance) * scrollAmount);
                        finished = false;
                    } else {
                        tickButtons.forEach(b -> b.yPosition = tickYDest);
                    }

                }

            } else {
                tickButtons.forEach(b -> b.yPosition = tickYDest);
            }

            // Make buttons outside of list zone invisible
            tickButtons.forEach(button -> button.visible = button.yPosition >= LIST_TOP.get() && button.yPosition + button.height <= LIST_BOTTOM.get());
        }

        if (finished) smoothListScroll = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Draw list border
        final int borderThickness = 3;
        drawRect(LIST_LEFT.get() - borderThickness, LIST_TOP.get() - borderThickness, LIST_RIGHT.get() + borderThickness, LIST_TOP.get(), 0xFFFFFFFF); // Top
        drawRect(LIST_LEFT.get() - borderThickness, LIST_BOTTOM.get(), LIST_RIGHT.get() + borderThickness, LIST_BOTTOM.get() + borderThickness, 0xFFFFFFFF); // Bottom
        drawRect(LIST_LEFT.get() - borderThickness, LIST_TOP.get(), LIST_LEFT.get(), LIST_BOTTOM.get(), 0xFFFFFFFF); // Left
        drawRect(LIST_RIGHT.get(), LIST_TOP.get(), LIST_RIGHT.get() + borderThickness, LIST_BOTTOM.get(), 0xFFFFFFFF); // Right

        jumpTextField.drawTextBox();

        if (smoothListScroll) syncListY();

        for (StrategyJump j : strategyJumps) {
            GuiButton referenceButton = j.removeButton;
            if (referenceButton.visible) {
                drawRect(LIST_LEFT.get(), referenceButton.yPosition - scaledY(0.3), LIST_RIGHT.get(), referenceButton.yPosition + BUTTON_HEIGHT.get() + scaledY(0.3), 0x96000000);
                drawString(fontRendererObj, j.getName(), LIST_LEFT.get() + scaledX(0.5), referenceButton.yPosition + scaledY(1.5), 0xFFFFFFFF);
            }
        }

        for (StrategyTick t : strategyTicks) {
            GuiButton referenceButton = t.inputButtons.get(W);

            if (referenceButton.visible) {
                if (t.jump != null && t.jump.extended) drawRect(LIST_LEFT.get(), referenceButton.yPosition - scaledY(0.3), LIST_RIGHT.get(), referenceButton.yPosition + BUTTON_HEIGHT.get() + scaledY(0.3), 0x96000000); // Draw background behind tick if it is visible & in a jump
                if (t.jump == null || t.jump.extended) drawString(fontRendererObj, "T" + (t.getTickNum() + 1), LIST_LEFT.get() + scaledX(0.5), referenceButton.yPosition + scaledY(1.5), 0xFFFFFFFF); // Draw # next to tick if it is visible
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void clampListHeight() {
        int maxTickSlot = strategyTicks.stream().filter(t -> t.getListSlot() != null).mapToInt(StrategyTick::getListSlot).max().orElse(-1);
        int maxJumpSlot = strategyJumps.stream().mapToInt(StrategyJump::getListSlot).max().orElse(-1);
        int totalHeight = (BUTTON_HEIGHT.get() + scaledY(0.5)) * (Math.max(maxTickSlot, maxJumpSlot) + 1);

        int maxListY = LIST_TOP.get() + scaledY(0.5); // Biggest possible Y coordinate
        int minListY = LIST_BOTTOM.get() - totalHeight; // Smallest possible Y coordinate

        listY = Math.min(maxListY, Math.max(minListY, listY));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        // Check if cursor is inside strategy area & an editor button is invisible (thus scrolling is needed)
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        boolean isButtonInvisible = strategyTicks.stream().anyMatch(t -> !t.inputButtons.get(W).visible && (t.jump == null || t.jump.extended)) || strategyJumps.stream().anyMatch(j -> !j.removeButton.visible);

        if (!(mouseX >= LIST_LEFT.get() && mouseX <= LIST_RIGHT.get() && mouseY >= LIST_TOP.get() && mouseY <= LIST_BOTTOM.get()) || !isButtonInvisible) return;

        // Scroll
        int scrollAmount = Mouse.getEventDWheel();
        if (scrollAmount < 0) listY -= scaledY(2);
        if (scrollAmount > 0) listY += scaledY(2);
        clampListHeight();

        if (scrollAmount != 0) smoothListScroll = true;
    }

    private Object id = new Object();
    private Object lastId = new Object();

    @Override
    protected void actionPerformed(GuiButton button) {

        if (id == lastId) return;
        lastId = id;

        switch (button.id) {
            case 1: { // Add Tick
                StrategyTick.addLoneTick(new HashSet<>(), strategyTicks.size());
                break;
            }
            case 2: { // Mirror Strategy
                for (StrategyTick t : strategyTicks) {
                    List<InputType> correctInputs = new ArrayList<>(t.correctInputs);

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
                    if (j.onDirections != null) {
                        List<InputType> onDirections = new ArrayList<>(j.onDirections);

                        if (j.onDirections.contains(A)) {
                            onDirections.remove(A);
                            onDirections.add(D);
                        }
                        if (j.onDirections.contains(D)) {
                            onDirections.remove(D);
                            onDirections.add(A);
                        }

                        j.onDirections = new HashSet<>(onDirections);

                        j.directionButtons.get(A).displayString = j.onDirections.contains(A) ? "\u00A7aA" : "\u00A7cA";
                        j.directionButtons.get(D).displayString = j.onDirections.contains(D) ? "\u00A7aD" : "\u00A7cD";
                    } else if (j.direction != null) {
                        j.direction = j.direction == A ? D : A;
                        j.directionButton.displayString = j.direction == A ? "A" : "D";
                    }
                }
                break;

            }
            case 3: { // Clear Strategy
                strategyTicks.forEach(t -> buttonList.removeAll(t.getButtons()));
                strategyJumps.forEach(j -> buttonList.removeAll(j.getButtons()));
                strategyTicks.clear();
                strategyJumps.clear();
                break;
            }
            case 4: { // Toggle Sprint Mode ON/OFF
                InputStatusConfig.toggleSprintMode = !InputStatusConfig.toggleSprintMode;
                InputStatusConfig.save();
                button.displayString = InputStatusConfig.toggleSprintMode ? "\u00A7aToggle Sprint Mode: ON" : "\u00A7cToggle Sprint Mode: OFF";
                strategyTicks.forEach(tick -> tick.inputButtons.get(SPR).enabled = !InputStatusConfig.toggleSprintMode);
                break;
            }
            case 5: { // Add Jump
                JumpType jumpType;

                try {
                    jumpType = JumpType.valueOf(jumpTextField.getText().toUpperCase());
                } catch (IllegalArgumentException e) {
                    mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
                    sendMessage("\u00A7cInvalid jump name! List of valid jump names:");

                    for (JumpType t : JumpType.values()) {
                        sendMessage("\u00A77- \u00A7e" + t);
                    }
                    return;
                }

                new StrategyJump(jumpType);
                break;
            }
            case 6: { // Label ON/OFF
                InputStatusConfig.label = !InputStatusConfig.label;
                InputStatusConfig.save();
                button.displayString = InputStatusConfig.label ? "\u00A7aLabel: ON" : "\u00A7cLabel: OFF";
                break;
            }
            case 9: {
                InputStatusConfig.shortenLabel = !InputStatusConfig.shortenLabel;
                InputStatusConfig.save();
                button.displayString = InputStatusConfig.shortenLabel ? "\u00A7aShorten Label: ON" : "\u00A7cShorten Label: OFF";
                break;
            }
            default: { // Strategy Buttons
                StrategyTick tick = strategyTicks.stream().filter(t -> t.getButtons().contains(button)).findFirst().orElse(null);
                StrategyJump jump = strategyJumps.stream().filter(j -> j.getButtons().contains(button)).findFirst().orElse(null);

                if (tick != null) {

                    int tickNum = tick.getTickNum();

                    if (button == tick.duplicateButton) { // Duplicate Tick
                        StrategyTick.addLoneTick(new HashSet<>(tick.correctInputs), tickNum + 1);

                    } else if (button == tick.removeButton) { // Remove Tick
                        tick.remove();

                    } else {
                        InputType inputType = tick.inputButtons.inverse().get(button);

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
                            jump.ticks.forEach(t -> buttonList.addAll(t.getButtons()));
                        } else {
                            jump.ticks.forEach(t -> buttonList.removeAll(t.getButtons()));
                        }
                    } else if (button == jump.run1TButton) { // Run 1t button for jumps
                        jump.run1T = !jump.run1T;
                        jump.run1TButton.displayString = jump.run1T ? "\u00A7aRun 1t" : "\u00A7cRun 1t";

                        if (jump.run1T) {
                            jump.cut = false;
                            jump.cutButton.displayString = "\u00A7cCut";
                        }

                        jump.syncTicks();
                    } else if (button == jump.cutButton) { // Cut button for jumps
                        jump.cut = !jump.cut;
                        jump.cutButton.displayString = jump.cut ? "\u00A7aCut" : "\u00A7cCut";

                        if (jump.cut) {
                            jump.run1T = false;
                            jump.run1TButton.displayString = "\u00A7cRun 1t";
                        }

                        jump.syncTicks();
                    } else if (jump.directionButtons != null && jump.directionButtons.containsValue(button)) { // Direction buttons for jumps
                        InputType inputType = jump.directionButtons.inverse().get(button);

                        if (jump.onDirections.contains(inputType)) {
                            jump.onDirections.remove(inputType);
                            button.displayString = "\u00A7c" + inputType;
                        } else {
                            jump.onDirections.add(inputType);
                            button.displayString = "\u00A7a" + inputType;
                        }

                        jump.syncTicks();

                    } else if (button == jump.directionButton) { // Direction buttons for jumps
                        jump.direction = jump.direction == InputType.A ? InputType.D : InputType.A;
                        button.displayString = jump.direction == InputType.A ? "A" : "D";
                        jump.syncTicks();
                    } else if (button == jump.removeButton) { // Remove button for jumps
                        jump.ticks.forEach(StrategyTick::remove);

                        buttonList.removeAll(jump.getButtons());
                        strategyJumps.remove(jump);

                        for (StrategyTick t : strategyTicks) {
                            Integer listSlot = t.getListSlot();
                            if (listSlot == null) continue;
                            t.getButtons().forEach(b -> b.yPosition = listY + (BUTTON_HEIGHT.get() + scaledY(0.5)) * listSlot);
                        }

                    }
                }
            }
        }
        clampListHeight();
        syncListY();
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
