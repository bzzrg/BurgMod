package com.bzzrg.burgmod.config.files.jsonconfigfiles;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;
import com.bzzrg.burgmod.config.files.utils.ListConvertor;
import com.bzzrg.burgmod.features.stratreminders.StratReminder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StratRemindersConfig extends JsonConfigFile {

    public static List<StratReminder> stratReminders = new ArrayList<>();

    private StratRemindersConfig(File file) {
        super(file);
    }

    @Override
    protected void init() {
        addList("stratReminders", () -> stratReminders, v -> stratReminders = v, convertor);
    }

    private static final ListConvertor<StratReminder> convertor = new ListConvertor<StratReminder>() {

        @Override
        public JsonElement toJson(StratReminder stratReminder) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", stratReminder.name);
            obj.addProperty("description", stratReminder.description);
            return obj;
        }

        @Override
        public StratReminder fromJson(JsonElement element) {
            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String description = obj.get("description").getAsString();
            return new StratReminder(name, description);
        }
    };

    public static final StratRemindersConfig instance = new StratRemindersConfig(new File(BurgMod.modConfigFolder, "strat-reminders.json"));

}