package com.bzzrg.burgmod.features.turnhelper;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.modutils.gui.BMListGui;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import com.bzzrg.burgmod.modutils.gui.CustomTextField;
import com.bzzrg.burgmod.modutils.gui.Row;
import net.minecraft.client.gui.GuiButton;

import java.util.*;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig.*;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;

public class TurnHelperListGui extends BMListGui {

    private static final int yawFieldWidth = 50;
    private static final int tickNumFieldWidth = 100;

    public TurnHelperListGui() {

        this.setSettingWidth(160);
        this.setListWidth(yawFieldWidth + tickNumFieldWidth + buttonHeight*2 + buttonGap*5);

        yawPoints.forEach(this::addYawPointRow);

        this.addActionButton("Add Yaw Point", b -> {
            int tickNum;
            if (yawPoints.isEmpty()) {
                tickNum = 2;
            } else {
                int lowestTickNum = Collections.max(yawPoints, Comparator.comparingInt(p -> p.tickNum)).tickNum;
                tickNum = Math.max(lowestTickNum + 1, 2);
            }

            YawPoint yawPoint = new YawPoint(tickNum, 0);
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

        this.addEnumSetting("Shape", Shape.class, () -> shape, v -> {
            shape = v;
            thicknessSetting.slider.enabled = "DOT".equals(shape);
            yawPlusMinusSetting.slider.enabled = "LINE".equals(shape);
        });
        this.addBooleanSetting("Delta Yaws", () -> deltaYaws, v -> deltaYaws = v);
        this.addBooleanSetting("Show Turn Accuracy", () -> showTurnAccuracy, v -> showTurnAccuracy = v);
        this.nextColumn();
        this.addFloatSetting("Red", () -> colorRed, v -> colorRed = v, 0, 1);
        this.addFloatSetting("Green", () -> colorGreen, v -> colorGreen = v, 0, 1);
        this.addFloatSetting("Blue", () -> colorBlue, v -> colorBlue = v, 0, 1);
        this.addFloatSetting("Opacity", () -> opacity, v -> opacity = v, 0, 1);
        thicknessSetting = this.addFloatSetting("Thickness (Dot)", () -> thickness, v -> thickness = v, 0, 1);
        yawPlusMinusSetting = this.addFloatSetting("Yaw +- (Line)", () -> yawPlusMinus, v -> yawPlusMinus = v, 0, 10);

    }

    private static DecimalSliderSetting<Float> thicknessSetting;
    private static DecimalSliderSetting<Float> yawPlusMinusSetting;

    @Override
    public void initGui() {
        super.initGui();
        thicknessSetting.slider.enabled = "DOT".equals(shape);
        yawPlusMinusSetting.slider.enabled = "LINE".equals(shape);
    }

    public void addYawPointRow(YawPoint yp) {
        yp.row = new YawPointRow(yp);
        this.rows.add(yawPoints.indexOf(yp), yp.row);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        List<YawPoint> clone = new ArrayList<>(yawPoints);
        Map<Integer, Integer> tickNums = new HashMap<>(); // key = tick num, value = yawpoint num

        for (YawPoint yp : clone) {
            int ypNum = clone.indexOf(yp)+1;

            try {
                Integer.parseInt(yp.row.tickNumField.field.getText());
            } catch (NumberFormatException e) {
                yawPoints.remove(yp);
                bmChat(String.format("\u00A7cDeleted Yaw Point #%d because it had invalid tick #: %s", ypNum, yp.row.tickNumField.field.getText()));
                continue;
            }

            if (yp.tickNum < 2) {
                yawPoints.remove(yp);
                bmChat(String.format("\u00A7cDeleted Yaw Point #%d because its tick # was less than 2! (Tick # must be greater than or equal to 2)", ypNum));
                continue;
            }

            if ("ONE_MOVING_TARGET".equals(mode) || showTurnAccuracy) {
                if (tickNums.containsKey(yp.tickNum)) {
                    yawPoints.remove(yp);
                    bmChat(String.format("\u00A7cDeleted Yaw Point #%d because it had the same tick # as Yaw Point #%d! (Tick # for each Yaw Point must be unique)", ypNum, tickNums.get(yp.tickNum)));
                    continue;
                } else {
                    tickNums.put(yp.tickNum, ypNum);
                }
            }

            try {
                Float.parseFloat(yp.row.yawField.field.getText());
            } catch (NumberFormatException e) {
                yawPoints.remove(yp);
                bmChat(String.format("\u00A7cDeleted Yaw Point #%d because it had invalid yaw: %s", ypNum, yp.row.yawField.field.getText()));
            }



        }

        TurnHelperConfig.instance.updateFile();

    }

    public class YawPointRow extends Row {
        YawPoint yawPoint;

        CustomTextField tickNumField;
        CustomTextField yawField;
        GuiButton duplicateButton;
        GuiButton removeButton;

        public YawPointRow(YawPoint yawPoint) {
            super(TurnHelperListGui.this);
            this.yawPoint = yawPoint;
        }

        @Override
        public void init() {
            int centeredY = getCenteredY(buttonHeight);
            // line below is bc if it is an invalid tick num, like 13e or somehting, it will revert to 13 on init if it just set it from yawPoint.tickNum
            String tickNumFieldText = tickNumField == null ? String.valueOf(yawPoint.tickNum) : tickNumField.field.getText();
            tickNumField = new CustomTextField(0, listLeft + buttonGap, centeredY, tickNumFieldWidth, buttonHeight, "Tick #", "Must be >= 2");
            tickNumField.field.setText(tickNumFieldText);
            fields.add(tickNumField);

            String yawFieldText = yawField == null ? String.valueOf(yawPoint.yaw) : yawField.field.getText();
            yawField = new CustomTextField(0, listLeft + tickNumFieldWidth + buttonGap * 2, centeredY, yawFieldWidth, buttonHeight, "Yaw", "Facing");
            yawField.field.setText(yawFieldText);
            fields.add(yawField);

            duplicateButton = new CustomButton(buttonList.size(), listLeft + tickNumFieldWidth + yawFieldWidth + buttonGap*3, centeredY, buttonHeight, buttonHeight, "\u00A7b\u2ffb");
            buttons.add(duplicateButton);

            removeButton = new CustomButton(buttonList.size(), listLeft + tickNumFieldWidth + yawFieldWidth + buttonHeight + buttonGap*4, centeredY, buttonHeight, buttonHeight, "\u00A74\u2716");
            buttons.add(removeButton);
        }

        @Override
        public void buttonClicked(GuiButton button) {

             if (button == duplicateButton) {
                YawPoint newYp = new YawPoint(yawPoint.tickNum, yawPoint.yaw);
                yawPoints.add(yawPoints.indexOf(yawPoint), newYp);
                addYawPointRow(newYp);
            } else if (button == removeButton) {
                yawPoints.remove(yawPoint);
                rows.remove(this);
            }
        }

        @Override
        public void fieldTextChanged(char c, int keyCode, CustomTextField field) {
            if (field == tickNumField) {
                try {
                    yawPoint.tickNum = Integer.parseInt(tickNumField.field.getText());
                } catch (NumberFormatException ignored) {
                }
            } else if (field == yawField) {
                try {
                    yawPoint.yaw = Float.parseFloat(yawField.field.getText());
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

}
