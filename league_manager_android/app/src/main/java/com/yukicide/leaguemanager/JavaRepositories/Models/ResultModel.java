package com.yukicide.leaguemanager.JavaRepositories.Models;

import java.util.ArrayList;

public class ResultModel {
    String _id;
    String fixtureId;
    Integer outcome;
    ArrayList<GoalModel> goals = new ArrayList<>();

    public ResultModel() {
    }

    public ResultModel(String fixtureId, ArrayList<GoalModel> goals) {
        this.fixtureId = fixtureId;
        this.goals = goals;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFixtureId() {
        return fixtureId;
    }

    public void setFixtureId(String fixtureId) {
        this.fixtureId = fixtureId;
    }

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public ArrayList<GoalModel> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<GoalModel> goals) {
        this.goals = goals;
    }
}
