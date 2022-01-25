package com.yukicide.leaguemanager.JavaRepositories.Models;

public class FixtureModel {
    String _id;
    String leagueId;
    String team1Id;
    String team2Id;
    String venueId;
    long time;
    int outcome;
    GoalModel[] goals;

    public FixtureModel() {
    }

    public FixtureModel(String leagueId, String team1Id, String team2Id, String venueId, int time) {
        this.leagueId = leagueId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.venueId = venueId;
        this.time = time;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getOutcome() {
        return outcome;
    }

    public void setOutcome(int outcome) {
        this.outcome = outcome;
    }

    public GoalModel[] getGoals() {
        return goals;
    }

    public void setGoals(GoalModel[] goals) {
        this.goals = goals;
    }
}
