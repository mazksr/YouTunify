package com.mazksr.youtunify;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Item> items;
    private SearchInterface searchInterface;
    private int currentlyPlayingPosition = -1;
    private boolean isPlayButtonEnabled = true;

    public SearchAdapter(SearchInterface searchInterface) {
        this.searchInterface = searchInterface;
        items = new ArrayList<>();
    }

    public void enablePlayButton(boolean bool) {
        isPlayButtonEnabled = bool;
    }

    public int getCurrentlyPlayingPosition() {
        return currentlyPlayingPosition;
    }

    public void setCurrentlyPlayingPosition(int position) {
        int previousPosition = currentlyPlayingPosition;
        currentlyPlayingPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(currentlyPlayingPosition);
    }


    public void clear() {
        items.clear();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.playButton.setEnabled(isPlayButtonEnabled);
        Picasso.get().load(item.getAlbum().getCover().get(0).getUrl()).into(holder.coverImage);
        holder.title.setText(item.getName());
        holder.artist.setText(item.getArtists().get(0).getName());
        Paint paint = new Paint();
        paint.setTextSize(holder.title.getTextSize());
        float textWidth = paint.measureText(holder.title.getText().toString());
        if (textWidth > holder.title.getMaxWidth()) {
            holder.title.setSelected(true);
        }
        if (Player.songId.equals(item.getId())) {
            holder.playButton.setImageResource(R.drawable.stop_24px);
        } else {
            holder.playButton.setImageResource(R.drawable.baseline_play_arrow_black_24);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    CircleImageView coverImage;
    ImageButton playButton, downloadButton;
    TextView title, artist;
    ProgressBar loading;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.track_cover);
            title = itemView.findViewById(R.id.track_name);
            artist = itemView.findViewById(R.id.track_artist);
            playButton = itemView.findViewById(R.id.play_button);
            loading = itemView.findViewById(R.id.loading);
            downloadButton = itemView.findViewById(R.id.download_button);
            playButton.setOnClickListener(v -> {
                if (searchInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        searchInterface.onPlayClicked(pos);
                    }
                }
            });
            downloadButton.setOnClickListener(v -> {
                if (searchInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        searchInterface.onAddClicked(pos);
                    }
                }
            });
        }
    }
}
