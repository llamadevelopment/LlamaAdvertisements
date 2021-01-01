package net.lldv.llamaadvertisements.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaadvertisements.LlamaAdvertisements;
import net.lldv.llamaadvertisements.components.language.Language;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.concurrent.CompletableFuture;

/**
 * @author LlamaDevelopment
 * @project MobEarn
 * @website http://llamadevelopment.net/
 */
public class AdvertiseCommand extends PluginCommand<LlamaAdvertisements> {

    public AdvertiseCommand(final LlamaAdvertisements plugin, final ConfigSection section) {
        super(section.getString("Name"), plugin);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[0]));
        if (plugin.isUsePermission()) setPermission(section.getString("Permission"));
        final String params = section.getString("Parameters");
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter(params, CommandParamType.STRING, false),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (sender.isPlayer()) {
                if (!sender.isOp() && this.getPlugin().isUsePermission() && !sender.hasPermission(getPermission())) {
                    sender.sendMessage(Language.get("no-permission"));
                    return;
                }

                if (!sender.isOp() && this.getPlugin().getCooldowns().getOrDefault(sender.getName(), 0L) > System.currentTimeMillis()) {
                    sender.sendMessage(Language.get("cooldown", this.getPlugin().getRemainingTime(this.getPlugin().getCooldowns().get(sender.getName()))));
                    return;
                }

                if (args.length == 0) {
                    sender.sendMessage(Language.get("too-short"));
                    return;
                }

                if (!sender.isOp() && this.getPlugin().isUseMoney()) {
                    final double money = LlamaEconomy.getAPI().getMoney(sender.getName());
                    if (this.getPlugin().getPrice() > money) {
                        sender.sendMessage(Language.get("not-enough-money", this.getPlugin().getPrice()));
                        return;
                    } else LlamaEconomy.getAPI().reduceMoney(sender.getName(), this.getPlugin().getPrice());
                }

                this.getPlugin().getCooldowns().put(sender.getName(), System.currentTimeMillis() + (this.getPlugin().getCooldownTime() * 1000L));

                final String text = String.join(" ", args);

                this.getPlugin().getAdText().forEach((line) ->
                        this.getPlugin().getServer().broadcastMessage(line.replace("%message%", text))
                );

            }
        });
        return false;
    }
}
