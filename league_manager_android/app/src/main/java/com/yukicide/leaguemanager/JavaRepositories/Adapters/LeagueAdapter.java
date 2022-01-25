package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.CommentViewHolder> {
    private ArrayList<LeagueModel> leagueList;
    private OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView catIconView;
        public TextView catNameView;

        public CommentViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            catIconView = itemView.findViewById(R.id.imgLeagueIcon);
            catNameView = itemView.findViewById(R.id.txtLeagueName);

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

    public LeagueAdapter(ArrayList<LeagueModel> exampleList) {
        leagueList = exampleList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_league, viewGroup, false);
        CommentViewHolder evh = new CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        LeagueModel currentItem = leagueList.get(i);

        //commentViewHolder.catIconView.setImageResource(R.drawable.ic_sell);
        commentViewHolder.catNameView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return leagueList.size();
    }

}
