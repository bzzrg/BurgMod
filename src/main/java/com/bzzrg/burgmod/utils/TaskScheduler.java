package com.bzzrg.burgmod.utils;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class TaskScheduler {

    private static class Task {
        int runTick;
        Runnable runnable;

        Task(int runTick, Runnable runnable) {
            this.runTick = runTick;
            this.runnable = runnable;
        }
    }

    private static final List<Task> tasks = new ArrayList<>();
    public static int currentTick = 0;

    public static void schedule(int ticks, Runnable runnable) {
        tasks.add(new Task(currentTick + ticks, runnable));
    }

    public static void clearTasks() {
        tasks.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // lowest is for the phase.end
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            currentTick++;
        } else if (event.phase == TickEvent.Phase.END) {
            for (Task task : new ArrayList<>(tasks)) {
                if (currentTick >= task.runTick) {
                    task.runnable.run();
                    tasks.remove(task);
                }
            }
        }

    }
}