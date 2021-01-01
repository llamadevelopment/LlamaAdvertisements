package net.lldv.llamaadvertisements;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import net.lldv.llamaadvertisements.commands.AdvertiseCommand;
import net.lldv.llamaadvertisements.components.language.Language;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author LlamaDevelopment
 * @project MobEarn
 * @website http://llamadevelopment.net/
 */
public class LlamaAdvertisements extends PluginBase {

    @Getter
    private boolean useMoney, usePermission;

    @Getter
    private final LinkedList<String> adText = new LinkedList<>();

    @Getter
    private double price;

    @Getter
    private int cooldownTime;

    @Getter
    private final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        Language.init(this);
        this.saveDefaultConfig();
        final Config c = this.getConfig();
        this.useMoney = c.getBoolean("UseMoney");
        this.usePermission = c.getBoolean("UsePermission");
        this.price = c.getDouble("PricePerAd");
        this.cooldownTime = c.getInt("Cooldown");
        this.adText.addAll(c.getStringList("Advertisement"));
        this.getServer().getCommandMap().register("advertise", new AdvertiseCommand(this, c.getSection("Commands.Advertise")));
    }

    public String getRemainingTime(long duration) {
        if (duration == -1L) {
            return Language.getNP("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = Language.getNP("days");
            if (days == 1) {
                day = Language.getNP("day");
            }

            String hour = Language.getNP("hours");
            if (hours == 1) {
                hour = Language.getNP("hour");
            }

            String minute = Language.getNP("minutes");
            if (minutes == 1) {
                minute = Language.getNP("minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Language.getNP("seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }
}
