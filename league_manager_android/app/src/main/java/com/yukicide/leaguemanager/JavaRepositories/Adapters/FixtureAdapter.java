package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.CommentViewHolder> {
    private ArrayList<FixtureModel> fixtureList;
    TeamFixtures teamFixtures;
    ArrayList<VenueModel> venues;
    private FixtureAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(FixtureAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView fixtureDetails;

        public CommentViewHolder(View itemView, final FixtureAdapter.OnItemClickListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.txtTime);
            fixtureDetails = itemView.findViewById(R.id.txtFixtureDetails);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            try {
                                listener.onItemClick(position);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    public FixtureAdapter(ArrayList<FixtureModel> exampleList, TeamFixtures teamFixtures, ArrayList<VenueModel> venues) {
        fixtureList = exampleList;
        this.teamFixtures = teamFixtures;
        this.venues = venues;
    }

    @NonNull
    @Override
    public FixtureAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fixture_item, viewGroup, false);
        FixtureAdapter.CommentViewHolder evh = new FixtureAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull FixtureAdapter.CommentViewHolder commentViewHolder, int i) {
        FixtureModel currentItem = fixtureList.get(i);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d = new Date(currentItem.getTime());
        //commentViewHolder.catIconView.setImageResource(R.drawable.ic_sell);
        commentViewHolder.time.setText(formatter.format(d));

        TeamModel Team1 = null, Team2 = null;
        for (TeamModel t : teamFixtures.getTeamList()) {
            if (t.get_id().equals(currentItem.getTeam1Id())) {
                Team1 = t;
            }
            if (t.get_id().equals(currentItem.getTeam2Id())) {
                Team2 = t;
            }
        }

        for (VenueModel v : venues) {
            if (v.get_id().equals(currentItem.getVenueId())) {
                commentViewHolder.time.append(" - " + v.getName());
                break;
            }
        }

        commentViewHolder.fixtureDetails.setText(Team1.getName() + " vs " + Team2.getName());
    }

    @Override
    public int getItemCount() {
        return fixtureList.size();
    }

}