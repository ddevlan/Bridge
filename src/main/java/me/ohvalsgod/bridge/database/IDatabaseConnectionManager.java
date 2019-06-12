package me.ohvalsgod.bridge.database;

public interface IDatabaseConnectionManager {

    boolean connect();
    void close();
    boolean connected();

}
