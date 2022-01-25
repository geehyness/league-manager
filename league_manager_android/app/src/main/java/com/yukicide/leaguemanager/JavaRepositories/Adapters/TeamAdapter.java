package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.CommentViewHolder> {
    private ArrayList<TeamModel> teamList;
    private TeamAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(TeamAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView teamIcon;
        public TextView txtTeamName;
        public ImageView btnMore;

        public CommentViewHolder(View itemView, final TeamAdapter.OnItemClickListener listener) {
            super(itemView);
            teamIcon = itemView.findViewById(R.id.teamIcon);
            txtTeamName = itemView.findViewById(R.id.txtTeamName);
            btnMore = itemView.findViewById(R.id.more);

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMoreClick(getAdapterPosition());
                }
            });

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

    public TeamAdapter(ArrayList<TeamModel> exampleList) {
        teamList = exampleList;
    }

    @NonNull
    @Override
    public TeamAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_team, viewGroup, false);
        TeamAdapter.CommentViewHolder evh = new TeamAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdapter.CommentViewHolder commentViewHolder, int i) {
        TeamModel currentItem = teamList.get(i);

        //commentViewHolder.teamIcon.setImageResource(R.drawable.ic_sell);
        commentViewHolder.txtTeamName.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

}
