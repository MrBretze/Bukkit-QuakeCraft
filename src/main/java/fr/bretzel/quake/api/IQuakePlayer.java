package fr.bretzel.quake.api;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by MrBretzel on 13/11/15.
 */

public abstract class IQuakePlayer {

    /**
     * @return Of uuid.
     */
    public UUID getUuid() {
        return null;
    }

    /**
     * @return to bukkit player if online.
     */
    public Player getPlayer(){
        return null;
    }

    public static IQuakePlayer getQuakePlayer(UUID uuid) {
        return null;
    }


    //Coins start !

    public int getCoins() {
        return 0;
    }

    public int setCoins(int i) {
        return 0;
    }

    public int addCoind(int i) {
        return 0;
    }

    public int removeCoins(int i){
        return 0;
    }

    //Coins stop


    //Kill start

    public int getKill() {
        return 0;
    }

    public int setKill(int i) {
        return 0;
    }

    public int addKill(int i) {
        return 0;
    }

    public int removeKill(int i) {
        return 0;
    }

    //Kill stop


    //KillStreak start

    public int getKillStreak() {
        return 0;
    }

    public int setKillStreak(int i) {
        return 0;
    }

    public int addKillStreak(int i) {
        return 0;
    }

    public int removeKillStreak(int i) {
        return 0;
    }

    //KillStreak stop


    //Death start

    public int getDeath() {
        return 0;
    }

    public int setDeath(int i) {
        return 0;
    }

    public int addDeath(int i) {
        return 0;
    }

    public int removeDeath(int i) {
        return 0;
    }
}
