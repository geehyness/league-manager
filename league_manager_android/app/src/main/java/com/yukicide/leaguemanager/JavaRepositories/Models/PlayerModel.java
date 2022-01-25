package com.yukicide.leaguemanager.JavaRepositories.Models;

public class PlayerModel {
    String _id;
    String name;
    int number;
    String teamId;
    int goals;

    public PlayerModel(String name, int number, String teamId) {
        this.name = name;
        this.number = number;
        this.teamId = teamId;
    }

    public PlayerModel() {
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
