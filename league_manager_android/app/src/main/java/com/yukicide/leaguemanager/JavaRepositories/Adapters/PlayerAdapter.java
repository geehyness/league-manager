package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.CommentViewHolder> {
    private ArrayList<PlayerModel> teamList;
    private PlayerAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(PlayerAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtGoals;

        public CommentViewHolder(View itemView, final PlayerAdapter.OnItemClickListener listener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtVenueName);
            txtGoals = itemView.findViewById(R.id.txtGoals);

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

    public PlayerAdapter(ArrayList<PlayerModel> exampleList) {
        teamList = exampleList;
    }

    @NonNull
    @Override
    public PlayerAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_player, viewGroup, false);
        PlayerAdapter.CommentViewHolder evh = new PlayerAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapter.CommentViewHolder commentViewHolder, int i) {
        PlayerModel currentItem = teamList.get(i);

        //commentViewHolder.teamIcon.setImageResource(R.drawable.ic_sell);
        commentViewHolder.txtName.setText(currentItem.getName());
        commentViewHolder.txtGoals.setText(currentItem.getGoals() + " Goal(s)");
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

}
