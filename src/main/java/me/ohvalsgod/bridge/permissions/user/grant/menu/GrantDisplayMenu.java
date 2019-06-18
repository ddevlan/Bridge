package me.ohvalsgod.bridge.permissions.user.grant.menu;

import lombok.val;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.grant.Grant;
import me.ohvalsgod.bukkitlib.menu.Button;
import me.ohvalsgod.bukkitlib.menu.pagination.PaginatedMenu;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class GrantDisplayMenu extends PaginatedMenu {

    private PermissionsUser user;

    public  GrantDisplayMenu(PermissionsUser user) {
        this.user = user;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return String.format("%s&6's Grants&e", user.getColoredName());
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        val buttons = new HashMap<Integer, Button>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return "&6How to use:";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "&7* Left-click to modify a grant.",
                        "&7* Right-click to remove a grant."
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMPTY_MAP;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {

            }
        });

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        val buttons = new HashMap<Integer, Button>();

        buttons.put(0, new GrantButton(user.getActiveGrant()));

        List<Grant> grants = new ArrayList<>(user.getGrants());
        grants.sort(Comparator.comparingLong(Grant::getAddedAt));

        int i = 1;
        for (Grant grant : grants) {
            if (grant != user.getActiveGrant()) {
                buttons.put(i, new GrantButton(grant));
                i++;
            }
        }

        return buttons;
    }

    private class GrantButton extends Button {

        private Grant grant;

        public GrantButton(Grant grant) {
            this.grant = grant;
        }

        @Override
        public String getName(Player player) {
            return String.format("&6Grant for group '%s&6' %s", grant.getPermissionsGroup().getFancyName(), (grant == user.getActiveGrant() ? " &7(&aActive&7)":""));
        }

        @Override
        public List<String> getDescription(Player player) {

            //  todo update to use database instead of cache
            List<String> toReturn = new ArrayList<>(Arrays.asList(
                    "&7&m----------------------------",
                    String.format("&eAdded by: &f%s", BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(UUID.fromString(grant.getIssuerId())).getColoredName()),
                    String.format("&eAdded at: &f%s", TimeUtils.formatIntoCalendarString(new Date(grant.getAddedAt()))),
                    String.format("&eAdded reason: &f%s", grant.getAddedReason()),
                    String.format("&eScope: &f%s", grant.getScope())
            ));

            if (grant.isRemoved()) {
                toReturn.addAll(Arrays.asList(
                        "",
                        String.format("&eRemoved by: &f%s", BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(UUID.fromString(grant.getRemoverId())).getColoredName()),
                        String.format("&eRemoved at: &f%s", TimeUtils.formatIntoCalendarString(new Date(grant.getRemovedAt()))),
                        String.format("&eRemoval reason: &f%s", grant.getRemovalReason())
                ));
            } else {
                toReturn.addAll(Arrays.asList(
                        "",
                        String.format("&eDuration: &f%s", (grant.isPermanent() ? "Permanent":TimeUtils.formatToLongDetailedString(System.currentTimeMillis() - grant.getDuration()))),
                        String.format("&eExpires at: &f%s", (grant.isPermanent() ? "Never":TimeUtils.formatIntoCalendarString(new Date(grant.getAddedAt() + grant.getDuration()))))
                ));
            }

            toReturn.add("&7&m----------------------------");

            return toReturn;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.BOOK;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 0;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType == ClickType.RIGHT) {
                if (grant.isActive()) {
                    grant.setRemoved(true);
                    grant.setRemoverId(player.getUniqueId().toString());
                    grant.setRemovedAt(System.currentTimeMillis());
                    grant.setRemovalReason("debug remove");

                    Player removed = user.toPlayer();
                    if (removed != null) {
                        user.update();
                        removed.sendMessage(ChatColor.GRAY + "One of your grants has been removed.");
                        removed.sendMessage(ChatColor.GRAY + "You have lost some of your permissions.");
                        removed.closeInventory();
                    }
                }
            }
        }
    }

}
