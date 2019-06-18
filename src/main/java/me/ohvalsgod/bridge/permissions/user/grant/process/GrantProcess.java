package me.ohvalsgod.bridge.permissions.user.grant.process;

import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.grant.Grant;
import me.ohvalsgod.bridge.permissions.user.grant.GrantBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
public class GrantProcess {

    private PermissionsUser issuer;
    private PermissionsUser receiver;
    private GrantBuilder builder;
    @Setter private GrantProcessState processState;

    public GrantProcess(PermissionsUser issuer, PermissionsUser receiver) {
        this.issuer = issuer;
        this.receiver = receiver;
        this.builder = new GrantBuilder(issuer.getUUID());
        this.processState = GrantProcessState.GROUP;
    }

    public void complete() {
        Grant grant = builder.get();
        receiver.getGrants().add(grant);

        if (receiver.toPlayer() != null) {
            receiver.update();
            Player onlinePlayer = receiver.toPlayer();
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You now have access to the &r" + grant.getPermissionsGroup().getFancyName() + " &7group."));
        }

        BridgePlugin.getBridgeInstance().getMongo().getPermissionsUserDAO().saveUser(receiver);
    }

}
