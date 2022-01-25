package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.GoalModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.CommentViewHolder> {
    private ArrayList<GoalModel> goalList;
    private ArrayList<PlayerModel> playerList;
    private GoalAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(GoalAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtGoals;
        public ImageView btnDelete;

        public CommentViewHolder(View itemView, final GoalAdapter.OnItemClickListener listener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtVenueName);
            txtGoals = itemView.findViewById(R.id.txtGoals);
            btnDelete = itemView.findViewById(R.id.btnDelete);

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

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onMoreClick(position);
                        }
                    }
                }
            });
        }
    }

    public GoalAdapter(ArrayList<GoalModel> exampleList, ArrayList<PlayerModel> teamPlayers) {
        goalList = exampleList;
        playerList = teamPlayers;
    }

    @NonNull
    @Override
    public GoalAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal, viewGroup, false);
        GoalAdapter.CommentViewHolder evh = new GoalAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.CommentViewHolder commentViewHolder, int i) {
        GoalModel currentItem = goalList.get(i);

        //commentViewHolder.teamIcon.setImageResource(R.drawable.ic_sell);
        for (PlayerModel p : playerList) {
            if (p.get_id().equals(currentItem.getPlayerId())) {
                commentViewHolder.txtName.setText(p.getName());
                break;
            }
        }
        commentViewHolder.txtGoals.setText(currentItem.getNumGoals() + " goals");
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

}