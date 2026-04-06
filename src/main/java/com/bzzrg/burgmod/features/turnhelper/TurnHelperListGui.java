package com.bzzrg.burgmod.features.turnhelper;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.modutils.gui.*;
import net.minecraft.client.gui.GuiButton;

import java.util.*;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig.*;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;

public class TurnHelperListGui extends BMListGui {

    private static final int yawFieldWidth = 50;
    private static final int tickNumWidth = 150;
    private static final int removeWidth = buttonHeight;

    public TurnHelperListGui() {

        this.setSettingWidth(150);
        this.setListWidth(yawFieldWidth + tickNumWidth + removeWidth + buttonGap * 4);

        yawPoints.forEach(this::addYawPointRow);

        this.addActionButton("Add Yaw Point", b -> {
            int tickNum;
            if (yawPoints.isEmpty()) {
                tickNum = 2;
            } else {
                int lowestTickNum = Collections.max(yawPoints, Comparator.comparingInt(p -> p.tickNum)).tickNum;
                tickNum = Math.min(lowestTickNum + 1, 50);
            }

            YawPoint yawPoint = new YawPoint(0, tickNum);
            yawPoints.add(yawPoint);
            addYawPointRow(yawPoint);
        });
        this.addActionButton("Clear Yaw Points", b -> {
            yawPoints.clear();
            this.rows.clear();
        });
        this.addEnumSetting("Mode", Mode.class, () -> mode, v -> {
            mode = v;
            if ("ALL_TARGETS_ON".equals(mode)) {
                bmChat("\u00A7eNOTE: Tick #s for Yaw Points are not used/needed when using All Targets On mode (Tick #s are only needed for One Moving Target mode and Show Turn Accuracy).");
            }
        });
        this.addBooleanSetting("Delta Yaws", () -> deltaYaws, v -> deltaYaws = v);
        this.addBooleanSetting("Show Turn Accuracy", () -> showTurnAccuracy, v -> showTurnAccuracy = v);
        this.nextColumn();
        this.addFloatSetting("Red", () -> colorRed, v -> colorRed = v, 0, 1);
        this.addFloatSetting("Green", () -> colorGreen, v -> colorGreen = v, 0, 1);
        this.addFloatSetting("Blue", () -> colorBlue, v -> colorBlue = v, 0, 1);
        this.addFloatSetting("Alpha", () -> opacity, v -> opacity = v, 0, 1);
        this.addFloatSetting("Yaw +-", () -> yawPlusMinus, v -> yawPlusMinus = v, 0, 10);

    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public void addYawPointRow(YawPoint yp) {
        yp.row = new YawPointRow(yp);
        this.rows.add(yp.row);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        List<YawPoint> clone = new ArrayList<>(yawPoints);
        Map<Integer, Integer> tickNums = new HashMap<>(); // key = tick num, value = yawpoint num

        for (YawPoint yp : clone) {
            int ypNum = clone.indexOf(yp)+1;
            try {
                yp.row.yawPoint.yaw = Float.parseFloat(yp.row.yawField.field.getText());
            } catch (NumberFormatException e) {
                yawPoints.remove(yp);
                bmChat(String.format("\u00A7cDeleted Yaw Point #%d because it had invalid yaw: %s", ypNum, yp.row.yawField.field.getText()));
            }

            if ("ONE_MOVING_TARGET".equals(mode) || showTurnAccuracy) {
                if (tickNums.containsKey(yp.tickNum)) {
                    yawPoints.remove(yp);
                    bmChat(String.format("\u00A7cDeleted Yaw Point #%d because it had the same tick # as Yaw Point #%d: (Tick #: %d) (Tick # for each Yaw Point must be unique)", ypNum, tickNums.get(yp.tickNum), yp.tickNum));
                } else {
                    tickNums.put(yp.tickNum, ypNum);
                }
            }

        }

        TurnHelperConfig.instance.updateFile();

    }

    public class YawPointRow extends Row {
        YawPoint yawPoint;

        CustomTextField yawField;
        GuiButton removeButton;

        public YawPointRow(YawPoint yawPoint) {
            this.yawPoint = yawPoint;
        }

        @Override
        public void init() {
            String fieldText = yawField == null ? String.valueOf(yawPoint.yaw) : yawField.field.getText();
            yawField = new CustomTextField(0, listLeft + buttonGap, getCenteredY(buttonHeight), yawFieldWidth, buttonHeight, "Yaw", "Facing");
            yawField.field.setText(fieldText);

            fields.add(yawField);

            buttons.add(new CustomSlider(buttonList.size(), listLeft + yawFieldWidth + buttonGap * 2, getCenteredY(buttonHeight), tickNumWidth, buttonHeight, "Tick #: ", "", 2, 50, yawPoint.tickNum, false, true,
                    s -> yawPoint.tickNum = s.getValueInt()));

            removeButton = new CustomButton(buttonList.size(), listLeft + yawFieldWidth + tickNumWidth + buttonGap * 3, getCenteredY(buttonHeight), removeWidth, buttonHeight, "\u00A74\u2716");
            buttons.add(removeButton);
        }

        @Override
        public void click(GuiButton button) {
            if (button == removeButton) {
                yawPoints.remove(yawPoint);
                rows.remove(this);
            }
        }
    }

}
