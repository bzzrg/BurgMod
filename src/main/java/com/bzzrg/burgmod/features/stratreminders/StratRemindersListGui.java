package com.bzzrg.burgmod.features.stratreminders;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.StratRemindersConfig;
import com.bzzrg.burgmod.modutils.gui.BMListGui;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import com.bzzrg.burgmod.modutils.gui.CustomTextField;
import com.bzzrg.burgmod.modutils.gui.Row;
import net.minecraft.client.gui.GuiButton;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StratRemindersConfig.stratReminders;

public class StratRemindersListGui extends BMListGui {

    private static final int fieldWidth = 200;
    private static final int fieldHeight = 15;

    private static final int thisButtonWidth = 20;
    private static final int thisButtonHeight = fieldHeight*2 + buttonGap;

    public StratRemindersListGui() {
        this.setListWidth(fieldWidth + thisButtonWidth * 2 + buttonGap * 4);
        this.setRowHeight(fieldHeight * 2 + buttonGap * 5);

        stratReminders.forEach(this::addReminderRow);

        this.addActionButton("Add Reminder", b -> {
            StratReminder reminder = new StratReminder("", "");
            stratReminders.add(reminder);
            addReminderRow(reminder);
        });
    }

    public void addReminderRow(StratReminder sr) {
        this.rows.add(stratReminders.indexOf(sr), new Row(this) {
            final StratReminder stratReminder = sr;

            CustomTextField nameField;
            CustomTextField descriptionField;

            GuiButton duplicateButton;
            GuiButton removeButton;

            @Override
            public void init() {
                nameField = new CustomTextField(0, listLeft + buttonGap, topY + buttonGap * 2, fieldWidth, fieldHeight, null, "Name");
                nameField.field.setText(stratReminder.name);
                fields.add(nameField);

                descriptionField = new CustomTextField(0, listLeft + buttonGap, topY + fieldHeight + buttonGap * 3, fieldWidth, fieldHeight, null, "Description");
                descriptionField.field.setText(stratReminder.description);
                fields.add(descriptionField);

                duplicateButton = new CustomButton(buttonList.size(), listLeft + fieldWidth + buttonGap * 2, getCenteredY(thisButtonHeight), thisButtonWidth, thisButtonHeight, "\u00A7b\u2ffb");
                buttons.add(duplicateButton);

                removeButton = new CustomButton(buttonList.size(), listLeft + fieldWidth + thisButtonWidth + buttonGap * 3, getCenteredY(thisButtonHeight), thisButtonWidth, thisButtonHeight, "\u00A74\u2716");
                buttons.add(removeButton);
            }

            @Override
            public void buttonClicked(GuiButton button) {
                if (button == duplicateButton) {
                    StratReminder newSr = new StratReminder(stratReminder.name, stratReminder.description);
                    stratReminders.add(stratReminders.indexOf(stratReminder), newSr);
                    addReminderRow(newSr);
                } else if (button == removeButton) {
                    stratReminders.remove(stratReminder);
                    rows.remove(this);
                }
            }

            @Override
            public void fieldTextChanged(char c, int keyCode, CustomTextField field) {
                if (field == nameField) {
                    stratReminder.name = nameField.field.getText();
                } else if (field == descriptionField) {
                    stratReminder.description = descriptionField.field.getText();
                }
            }
        });
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        StratRemindersConfig.instance.updateFile();
    }

}