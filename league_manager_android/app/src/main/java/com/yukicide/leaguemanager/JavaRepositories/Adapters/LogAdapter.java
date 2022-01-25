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

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.CommentViewHolder> {
    private ArrayList<TeamModel> teamList;
    private LogAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(LogAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView teamIcon;
        public TextView txtNum;
        public TextView txtTeamName;
        public TextView txtPoints;

        public CommentViewHolder(View itemView, final LogAdapter.OnItemClickListener listener) {
            super(itemView);
            teamIcon = itemView.findViewById(R.id.teamIcon);
            txtNum = itemView.findViewById(R.id.txtNum);
            txtTeamName = itemView.findViewById(R.id.txtTeamName);
            txtPoints = itemView.findViewById(R.id.txtPoints);

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

    public LogAdapter(ArrayList<TeamModel> exampleList) {
        teamList = exampleList;
    }

    @NonNull
    @Override
    public LogAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_log, viewGroup, false);
        LogAdapter.CommentViewHolder evh = new LogAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.CommentViewHolder commentViewHolder, int i) {
        TeamModel currentItem = teamList.get(i);

        //commentViewHolder.teamIcon.setImageResource(R.drawable.ic_sell);
        if (teamList.indexOf(currentItem) + 1 < 10)
            commentViewHolder.txtNum.setText("0" + (teamList.indexOf(currentItem) + 1));
        else
            commentViewHolder.txtNum.setText("" + (teamList.indexOf(currentItem) + 1));

        commentViewHolder.txtTeamName.setText(currentItem.getName());
        commentViewHolder.txtPoints.setText(currentItem.getPoints() + " Point(s)");
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

}
