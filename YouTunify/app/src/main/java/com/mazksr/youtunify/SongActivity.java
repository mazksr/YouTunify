package com.mazksr.youtunify;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongActivity extends AppCompatActivity {

    private List<Songs> items = DataSource.songs;
    private ImageView cover, playpause, play, pause, next, prev;
    private TextView title, artist;
    private ProgressBar loading2;
    private Handler handler = new Handler();
    private Call<UserResponse> call;
    private ApiService apiService;
    private LibraryAdapter libraryAdapter;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cover = findViewById(R.id.cover);
        playpause = findViewById(R.id.play_container);
        next = findViewById(R.id.next_container);
        prev = findViewById(R.id.prev_container);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        loading2 = findViewById(R.id.loading2);

        if (Player.libraryPos>=0) {
            byte[] blob = DataSource.songs.get(Player.libraryPos).getCover();
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            cover.setImageBitmap(bitmap);
            title.setText(DataSource.songs.get(Player.libraryPos).getTitle());
            artist.setText(DataSource.songs.get(Player.libraryPos).getArtist());
        } else {
            cover.setImageResource(R.color.white);
            title.setText("-");
            artist.setText("-");
        }



        playpause.setOnClickListener(v -> playPause());
        next.setOnClickListener(v -> next());
        prev.setOnClickListener(v -> prev());

        if (!Player.getPlayer().isPlaying()) {
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        } else {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    public void playPause() {
        if (Player.getPlayer().isPlaying()) {
            Player.getPlayer().pause();
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        } else {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            Player.getPlayer().start();
        }
    }

    public void playSong() {
        Player.stop();
        int position = Player.libraryPos;
        Runnable runnable1 = () -> {
            handler.post(() -> {
                loading2.setVisibility(View.VISIBLE);
                cover.setImageResource(R.color.white);
                title.setText("-");
                artist.setText("-");
                next.setEnabled(false);
                prev.setEnabled(false);
            });
            call = apiService.download(items.get(position).getSongId());

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String url = response.body().getAudioUrl();
                        System.out.println(url);
                        handler.post(() -> {
                            try {
                                Player.play(url, mp -> {
                                    Player.getPlayer().start();
                                    byte[] blob = DataSource.songs.get(Player.libraryPos).getCover();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                                    cover.setImageBitmap(bitmap);
                                    title.setText(DataSource.songs.get(Player.libraryPos).getTitle());
                                    artist.setText(DataSource.songs.get(Player.libraryPos).getArtist());
                                    if (!Player.getPlayer().isPlaying()) {
                                        play.setVisibility(View.VISIBLE);
                                        pause.setVisibility(View.GONE);
                                    } else {
                                        play.setVisibility(View.GONE);
                                        pause.setVisibility(View.VISIBLE);
                                    }
                                    loading2.setVisibility(View.GONE);
                                    next.setEnabled(true);
                                    prev.setEnabled(true);
                                }, mp -> {
                                    next();
                                });
                            } catch (Exception e) {
                                next.setEnabled(true);
                                prev.setEnabled(true);
                                Toast.makeText(SongActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        loading2.setVisibility(View.GONE);
                        next.setEnabled(true);
                        prev.setEnabled(true);
                        Toast.makeText(SongActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    loading2.setVisibility(View.GONE);
                    next.setEnabled(true);
                    prev.setEnabled(true);
                    Toast.makeText(SongActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(t);
                }
            });
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable1);
    }

    public void next() {
        if (Player.libraryPos < items.size() - 1 && Player.libraryPos > -1) {
            Player.stop();
            loading2.setVisibility(View.VISIBLE);
            Player.libraryPos++;
            playSong();
        }
    }

    public void prev() {
        if (Player.libraryPos > 0) {
            Player.stop();
            loading2.setVisibility(View.VISIBLE);
            Player.libraryPos--;
            playSong();
        }
    }

}