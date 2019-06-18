package me.ohvalsgod.bridge.permissions.user.grant.process.menu;

import io.github.thatkawaiisam.redstone.shared.RedstoneSharedAPI;
import lombok.AllArgsConstructor;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.grant.process.GrantProcess;
import me.ohvalsgod.bridge.permissions.user.grant.process.GrantProcessListener;
import me.ohvalsgod.bridge.permissions.user.grant.process.GrantProcessState;
import me.ohvalsgod.bukkitlib.menu.Button;
import me.ohvalsgod.bukkitlib.menu.pagination.PaginatedMenu;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import me.ohvalsgod.bukkitlib.util.WoolUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class GrantProcessMenu extends PaginatedMenu {

    private GrantProcess process;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return process.getProcessState().getTitle();
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        if (process.getProcessState() == GrantProcessState.SCOPE) {
            buttons.put(22, new ServerScopeButton("Global"));
            buttons.put(31, new GrantConfirmButton());
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        AtomicInteger i = new AtomicInteger();

        if (process.getProcessState() == GrantProcessState.GROUP) {
            BridgePlugin.getBridgeInstance().getPermissionsHandler().getPermissionsGroups().values().stream().sorted(Comparator.comparingDouble(PermissionsGroup::getWeight).reversed()).forEach(group -> {
                buttons.put(i.get(), new PermissionsGroupButton(group));
                i.getAndIncrement();
            });
        } else if (process.getProcessState() == GrantProcessState.SCOPE) {
            RedstoneSharedAPI.getServerIds().forEach(s -> {
                buttons.put(i.get(), new ServerScopeButton(s));
                i.getAndIncrement();
            });
        }

        return buttons;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @AllArgsConstructor
    private class GrantConfirmButton extends Button {

        @Override
        public String getName(Player player) {
            return "&aConfirm and Grant";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    "&7&m-------------------------------",
                    String.format("&eClick to add the &r%s &egroup to &r%s", process.getBuilder().getPermissionsGroup().getFancyName(), process.getReceiver().getColoredName()),
                    String.format("&eThis grant will apply on: &f[%s]", process.getBuilder().getScope()),
                    String.format("&eReason: &f%s", process.getBuilder().getReason()),
                    String.format("&eDuration: &f%s", (process.getBuilder().getDuration() != Integer.MAX_VALUE ? TimeUtils.formatToLongDetailedString(process.getBuilder().getDuration()):"Permanent")),
                    "&7&m-------------------------------");
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 0;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            process.complete();
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully granted '&r" + process.getReceiver().getColoredName() + "&a' the " + process.getBuilder().getPermissionsGroup().getFancyName() + " &agroup."));
        }
    }

    @AllArgsConstructor
    private class ServerScopeButton extends Button {

        private String server;

        @Override
        public String getName(Player player) {
            return "&6" + server;
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    "&7&m-------------------------------",
                    String.format("&r%s &ewill only have the &r%s &egroup on %s&e.", process.getReceiver().getColoredName(), process.getBuilder().getPermissionsGroup().getFancyName(), server),
                    "&7&m-------------------------------"
            );
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.COMMAND;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 0;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            process.getBuilder().scope(server);
        }
    }

    @AllArgsConstructor
    private class PermissionsGroupButton extends Button {

        private PermissionsGroup group;

        @Override
        public String getName(Player player) {
            return group.getFancyName();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    "&7&m-------------------------------",
                    String.format("&eClick to grant &r%s &ethe &r%s &egroup.", process.getReceiver().getColoredName(), group.getFancyName()),
                    "&7&m-------------------------------"
            );
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.WOOL;
        }

        @Override
        public byte getDamageValue(Player player) {
            return WoolUtils.getData(WoolUtils.fromString(group.getFancyName()));
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            process.getBuilder().permissionsGroup(group);
            player.closeInventory();
            process.setProcessState(GrantProcessState.REASON);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlease type a reason for this grant to be added, or type &4cancel &eto cancel."));
            GrantProcessListener.getProcesses().put(player.getUniqueId(), process);
        }

    }

}
