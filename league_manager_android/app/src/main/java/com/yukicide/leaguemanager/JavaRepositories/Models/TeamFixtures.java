package com.yukicide.leaguemanager.JavaRepositories.Models;

import java.util.ArrayList;

public class TeamFixtures {
    ArrayList<FixtureModel> fixturesList = new ArrayList<>();
    ArrayList<TeamModel> teamList = new ArrayList<>();

    public void setFixturesList(ArrayList<FixtureModel> fixturesList) {
        this.fixturesList = fixturesList;
    }

    public void setTeamList(ArrayList<TeamModel> teamModelsList) {
        this.teamList = teamModelsList;
    }

    public ArrayList<FixtureModel> getFixturesList() {
        return fixturesList;
    }

    public ArrayList<TeamModel> getTeamList() {
        return teamList;
    }
}
