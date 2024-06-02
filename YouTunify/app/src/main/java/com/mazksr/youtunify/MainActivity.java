package com.mazksr.youtunify;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private LyricsFragment lyricsFragment;
    private LibraryFragment libraryFragment;
    private SearchFragment searchFragment;
    private CircleImageView songControl;
    private FragmentManager fragmentManager;

    final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == 101) {
                    recreateLibrary();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        libraryFragment = new LibraryFragment();
        lyricsFragment = new LyricsFragment();
        searchFragment = new SearchFragment();
        bottomNavigationView = findViewById(R.id.botnavbar);
        songControl = findViewById(R.id.player_control);

        songControl.setOnClickListener(v -> resultLauncher.launch(new Intent(this, SongActivity.class)));

        Fragment fragment = fragmentManager.findFragmentByTag(LibraryFragment.class.getSimpleName());
        Player.getPlayer();

        if (!(fragment instanceof LibraryFragment)){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_view, libraryFragment)
                    .commit();
        }



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected (@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID==R.id.menu_library) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_view, libraryFragment)
                            .commit();
                    return true;
                } else if (itemID==R.id.menu_search) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_view, searchFragment)
                            .commit();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer player = Player.getPlayer();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        }
    }

    private void recreateLibrary() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_view, libraryFragment)
                .commit();
    }
}