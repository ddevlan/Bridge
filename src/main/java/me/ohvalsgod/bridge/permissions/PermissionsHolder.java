package me.ohvalsgod.bridge.permissions;

import java.util.HashSet;
import java.util.Set;

public interface PermissionsHolder {


    Set<String> getPermissions();

    default void addPermission(String permission) {
        getPermissions().add(permission);
    }

    default void removePermission(String permission) {
        getPermissions().remove(permission);
    }

    default boolean hasPermission(String permission) {
        return permission.contains(permission);
    }

    boolean update();

}
