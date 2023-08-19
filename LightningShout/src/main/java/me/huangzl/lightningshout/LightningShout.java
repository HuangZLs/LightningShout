package me.huangzl.lightningshout;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LightningShout extends Plugin {
    private Map<ProxiedPlayer, Long> lastShoutTimes = new HashMap<>();
    private TaskScheduler scheduler;

    @Override
    public void onEnable() {
        getLogger().info("\033[36m\n.____    .__       .__     __         .__                _________.__                   __   \n" +
                "|    |   |__| ____ |  |___/  |_  ____ |__| ____    ____ /   _____/|  |__   ____  __ ___/  |_ \n" +
                "|    |   |  |/ ___\\|  |  \\   __\\/    \\|  |/    \\  / ___\\\\_____  \\ |  |  \\ /  _ \\|  |  \\   __\\\n" +
                "|    |___|  / /_/  >   Y  \\  | |   |  \\  |   |  \\/ /_/  >        \\|   Y  (  <_> )  |  /|  |  \n" +
                "|_______ \\__\\___  /|___|  /__| |___|  /__|___|  /\\___  /_______  /|___|  /\\____/|____/ |__|  \n" +
                "        \\/ /_____/      \\/          \\/        \\//_____/        \\/      \\/                    \33[0m");
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerCommand(this, new ShoutCommand());
        scheduler = ProxyServer.getInstance().getScheduler();
    }

    @Override
    public void onDisable() {
        scheduler.cancel(this);
    }

    private class ShoutCommand extends Command {

        public ShoutCommand() {
            super("shout", null, "s");
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                long currentTime = System.currentTimeMillis();
                long lastShoutTime = lastShoutTimes.getOrDefault(player, 0L);
                long elapsedTime = currentTime - lastShoutTime;
                long cooldownTime = 2 * 60 * 1000;

                if (elapsedTime < cooldownTime) {
                    long remainingCooldown = cooldownTime - elapsedTime;
                    player.sendMessage(ChatColor.RED + "每2分钟只能喊一次，请耐心等待" + TimeUnit.MILLISECONDS.toSeconds(remainingCooldown) + "秒哦！");
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "用法: /shout <信息>");
                } else {
                    String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
                    String formattedMessage = ChatColor.BLUE + "[全服喊话] " + ChatColor.YELLOW + player.getName() + ": " + ChatColor.DARK_AQUA + message;
                    for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
                        target.sendMessage(formattedMessage);
                    }
                    lastShoutTimes.put(player, currentTime);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "该命令只能由玩家执行!");
                return;
            }
        }
    }

}