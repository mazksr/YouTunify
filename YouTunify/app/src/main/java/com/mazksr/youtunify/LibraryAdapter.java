package com.mazksr.youtunify;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private List<Songs> libraryList;
    private Context context;
    private boolean isItemClickEnabled = true;
    private LibraryInterface libraryInterface;

    public LibraryAdapter(LibraryInterface libraryInterface, Context context, List<Songs> libraryList) {
        this.libraryInterface = libraryInterface;
        this.libraryList = libraryList;
        this.context = context;
    }

    public void enableItemClick(boolean b) {
        isItemClickEnabled = b;
    }

    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.library_item, parent, false);
        return new LibraryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ViewHolder holder, int position) {
        Songs song = libraryList.get(position);
        if (Player.libraryPos != -1 && DataSource.songs.get(position).getSongId().equals(DataSource.songs.get(Player.libraryPos).getSongId())) {
            holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.rectangle));
        } else {
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        holder.title.setText(song.getTitle());
        Paint paint = new Paint();
        paint.setTextSize(holder.title.getTextSize());
        float textWidth = paint.measureText(holder.title.getText().toString());
        if (textWidth > holder.title.getMaxWidth()) {
            holder.title.setSelected(true);
        }
        holder.artist.setText(song.getArtist());
        byte[] blob = song.getCover();
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        holder.cover.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return libraryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cover;
        TextView title, artist;
        ImageButton more;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.track_cover);
            title = itemView.findViewById(R.id.track_name);
            artist = itemView.findViewById(R.id.track_artist);
            more = itemView.findViewById(R.id.more_button);
            view = itemView.findViewById(R.id.main_view);
            view.setOnClickListener(v -> {
                if (libraryInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        libraryInterface.onItemClicked(pos);
                    }
                }
            });

            more.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(v.getContext(), R.style.PopupMenuStyle), more);
                popupMenu.inflate(R.menu.library_options_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete song from library?")
                                .setPositiveButton("Ya", (dialog, which) -> {
                                    DataSource.removeSong(context, libraryList.get(getAdapterPosition()).getId());
                                    notifyItemRemoved(getAdapterPosition());
                                })
                                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    } else {return false;}
                });
                popupMenu.show();
            });

        }
    }
}
