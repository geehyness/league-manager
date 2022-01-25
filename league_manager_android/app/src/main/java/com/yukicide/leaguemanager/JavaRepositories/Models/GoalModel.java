package com.yukicide.leaguemanager.JavaRepositories.Models;

public class GoalModel {
    String playerId;
    int numGoals;

    public GoalModel(String playerId, int numGoals) {
        this.playerId = playerId;
        this.numGoals = numGoals;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getNumGoals() {
        return numGoals;
    }

    public void setNumGoals(int numGoals) {
        this.numGoals = numGoals;
    }
}
