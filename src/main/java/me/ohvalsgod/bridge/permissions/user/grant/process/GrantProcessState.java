package me.ohvalsgod.bridge.permissions.user.grant.process;

public enum GrantProcessState {

    GROUP("&6&lChoose a Group"),
    REASON("null"),
    DURATION("null"),
    SCOPE("&6&lChoose the Scope");

    String title;

    GrantProcessState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
