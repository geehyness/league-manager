package com.yukicide.leaguemanager.JavaRepositories.Models;

public class VenueModel {
    String _id;
    String name;

    public VenueModel() {
    }

    public VenueModel(String name) {
        this.name = name;
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
}
