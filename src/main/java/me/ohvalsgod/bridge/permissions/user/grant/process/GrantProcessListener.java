package me.ohvalsgod.bridge.permissions.user.grant.process;

import lombok.Getter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.grant.process.menu.GrantProcessMenu;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrantProcessListener implements Listener {

    @Getter private static Map<UUID, GrantProcess> processes;

    public GrantProcessListener(BridgePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        processes = new HashMap<>();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (processes.containsKey(player.getUniqueId())) {
            GrantProcess process = processes.get(player.getUniqueId());

            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                processes.remove(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7Grant process for '&r%s&7' cancelled.", process.getReceiver().getColoredName())));
                return;
            }

            if (process.getProcessState() == GrantProcessState.REASON) {
                process.getBuilder().reason(event.getMessage());
                process.setProcessState(GrantProcessState.DURATION);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlease type a duration for this grant, (\"perm\" for permanent) or type &4cancel &eto cancel."));
            } else if (process.getProcessState() == GrantProcessState.DURATION) {
                //TODO: maybe gui duration?
                if (TimeUtils.parseTime(event.getMessage()) <= 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a valid duration."));
                    return;
                }

                process.getBuilder().duration(TimeUtils.parseTime(event.getMessage()));
                process.setProcessState(GrantProcessState.SCOPE);
                processes.remove(player.getUniqueId());
                new GrantProcessMenu(process).openMenu(player);
            }
        }
    }

}
