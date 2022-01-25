package com.yukicide.leaguemanager.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.CommentViewHolder> {
    private ArrayList<VenueModel> venueList;
    private VenueAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(VenueAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;

        public CommentViewHolder(View itemView, final VenueAdapter.OnItemClickListener listener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtVenueName);

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

    public VenueAdapter(ArrayList<VenueModel> exampleList) {
        venueList = exampleList;
    }

    @NonNull
    @Override
    public VenueAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_venue, viewGroup, false);
        VenueAdapter.CommentViewHolder evh = new VenueAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull VenueAdapter.CommentViewHolder commentViewHolder, int i) {
        VenueModel currentItem = venueList.get(i);

        //commentViewHolder.teamIcon.setImageResource(R.drawable.ic_sell);
        commentViewHolder.txtName.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

}