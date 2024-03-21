package org.kerix.api.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.utils.Pair;

import java.util.HashMap;
import java.util.UUID;

import static org.kerix.api.MinecraftAPI.getINSTANCE;

public class ActionBar {

    private static final HashMap<Player, CountdownInfo> countdowns = new HashMap<>();
    private static final HashMap<Player , AnimationState> playersWithAnimation = new HashMap<>();
    private static boolean sentCountdowns = false;
    private static boolean sentAnimations = false;




    private static void sendCountdowns() {
        sentCountdowns = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                countdowns.forEach((player, info) -> {
                    if (!player.isOnline()) return;

                    Pair<Integer, Integer> timeRange = info.getMessage().getSecond();
                    int start = timeRange.getFirst();
                    int end = timeRange.getSecond();

                    boolean countDown = true; // Default to counting down

                    if (!info.isCountDownDetermined()) {
                        countDown = start > end; // Determine countdown direction
                        info.setCountDownDetermined(true);
                    }

                    if (countDown) {
                        if (end < start) {
                            countdowns.remove(player);
                        } else {
                            String msg = getString(info.getMessage(), end);
                            send(player, msg);
                            timeRange.setSecond(end - 1);
                        }
                    } else {
                        String msg = getString(info.getMessage(), end);
                        send(player, msg);
                        timeRange.setSecond(end + 1);
                    }
                });
            }

            @NotNull
            private String getString(Pair<String, Pair<Integer, Integer>> message, int end) {
                int hours = end / 3600, minutes = (end % 3600) / 60, seconds = end % 60;
                String formattedCountdown = (hours > 0) ? String.format("%02dh:%02dm:%02ds", hours, minutes, seconds) :
                        (minutes > 0) ? String.format("%02dm:%02ds", minutes, seconds) : String.format("%02ds", seconds);

                return message.getFirst() + " " + formattedCountdown;
            }
        }.runTaskTimer(getINSTANCE(), 0, 20);
    }

    public static void send(Player player, String message) {
        player.sendActionBar(Component.text(message.replace("&", "§")));
    }

    public static void send(Player player, String message, int start, int finish) {
        countdowns.put(player, new CountdownInfo(new Pair<>(message, new Pair<>(start, finish))));

        if (!sentCountdowns) sendCountdowns();
    }

    public static void send(Player player, int times, String @NotNull ... args) {
        playersWithAnimation.put(player , new AnimationState(times , args));

        if (!sentAnimations) sendAnimations();
    }

    private static void sendAnimations() {
        sentAnimations = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                playersWithAnimation.forEach((player, animationState) -> {
                    int i = animationState.getIndex();
                    int stop = animationState.getStop();
                    int times = animationState.getTimes();
                    String[] messages = animationState.getAnimation();

                    if (stop == times || !player.isOnline()) {
                        playersWithAnimation.remove(player);
                        return;
                    }
                    int length = messages.length;

                    if (i >= length) {
                        i = 0;
                        stop++;
                        if (stop != times) {
                            send(player, messages[i]);
                        }
                    } else {
                        send(player, messages[i]);
                        i++;
                    }

                    animationState.setIndex(i);
                    animationState.setStop(stop);
                });
            }
        }.runTaskTimer(getINSTANCE(), 0, 5);
    }


    private static class AnimationState {
        private int index = 0;
        private int stop = 0;
        private final String[] animation;
        private final int times;

        public AnimationState(int times , String[] animation){
            this.animation = animation;
            this.times = times;
        }


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getStop() {
            return stop;
        }

        public void setStop(int stop) {
            this.stop = stop;
        }

        public int getTimes(){
            return times;
        }
        public String[] getAnimation(){
            return animation;
        }
    }
    private static class CountdownInfo {
        private final Pair<String, Pair<Integer, Integer>> message;
        private boolean countDownDetermined;

        public CountdownInfo(Pair<String, Pair<Integer, Integer>> message) {
            this.message = message;
            this.countDownDetermined = false;
        }

        public Pair<String, Pair<Integer, Integer>> getMessage() {
            return message;
        }

        public boolean isCountDownDetermined() {
            return countDownDetermined;
        }

        public void setCountDownDetermined(boolean countDownDetermined) {
            this.countDownDetermined = countDownDetermined;
        }
    }
}


