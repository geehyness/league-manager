package com.yukicide.leaguemanager.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.TeamAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.fixtureCRUD.ViewFixturesActivity;
import com.yukicide.leaguemanager.UI.leagueCRUD.LogTableActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.ViewTeamsActivity;
import com.yukicide.leaguemanager.UI.venuesCRUD.ViewVenuesActivity;

import java.util.ArrayList;
import java.util.Objects;

public class FavouriteFragment extends Fragment {
    private TeamAdapter teamAdapter;
    private ArrayList<TeamModel> teamList = new ArrayList<>();
    private ArrayList<FixtureModel> fixturesList = new ArrayList<>();

    private TeamModel selectedTeam;

    LeagueModel leagueModel;
    LeagueManagerAPI leagueManagerAPI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);

        Intent i = Objects.requireNonNull(getActivity()).getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        TextView txtLeague = v.findViewById(R.id.txtTitle);
        txtLeague.setText(leagueModel.getName());

        CardView teams = v.findViewById(R.id.teams);
        CardView fixtures = v.findViewById(R.id.fixtures);
        CardView venues = v.findViewById(R.id.venues);
        CardView log = v.findViewById(R.id.log);

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamFixtures teamFixtures = new TeamFixtures();
                teamFixtures.setFixturesList(fixturesList);
                teamFixtures.setTeamList(teamList);

                startActivity(new Intent(getContext(), ViewTeamsActivity.class)
                        .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures))
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        fixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamFixtures teamFixtures = new TeamFixtures();
                teamFixtures.setFixturesList(fixturesList);
                teamFixtures.setTeamList(teamList);

                startActivity(new Intent(getContext(), ViewFixturesActivity.class)
                        .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures))
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        venues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ViewVenuesActivity.class)
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LogTableActivity.class)
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        return v;
    }
}