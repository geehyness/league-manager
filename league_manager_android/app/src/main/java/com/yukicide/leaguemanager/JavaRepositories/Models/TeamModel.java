package com.yukicide.leaguemanager.JavaRepositories.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TeamModel implements Parcelable {
    private String _id;
    private String name;
    private String leagueId;
    private int played, wins, losses, draws, goalsScored, goalsAgainst, goalDifference, points;

    public TeamModel() {
    }

    public TeamModel(String name, String leagueId) {
        this.name = name;
        this.leagueId = leagueId;
    }

    protected TeamModel(Parcel in) {
        _id = in.readString();
        name = in.readString();
        leagueId = in.readString();
        played = in.readInt();
        wins = in.readInt();
        losses = in.readInt();
        draws = in.readInt();
        goalsScored = in.readInt();
        goalsAgainst = in.readInt();
        goalDifference = in.readInt();
        points = in.readInt();
    }

    public static final Creator<TeamModel> CREATOR = new Creator<TeamModel>() {
        @Override
        public TeamModel createFromParcel(Parcel in) {
            return new TeamModel(in);
        }

        @Override
        public TeamModel[] newArray(int size) {
            return new TeamModel[size];
        }
    };

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

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(leagueId);
        dest.writeInt(played);
        dest.writeInt(wins);
        dest.writeInt(losses);
        dest.writeInt(draws);
        dest.writeInt(goalsScored);
        dest.writeInt(goalsAgainst);
        dest.writeInt(goalDifference);
        dest.writeInt(points);
    }
}
