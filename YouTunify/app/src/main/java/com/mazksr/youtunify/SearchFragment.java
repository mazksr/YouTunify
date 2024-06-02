package com.mazksr.youtunify;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private ApiService apiService;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private List<Item> items;
    private EditText searchField;
    private ImageView cancel;
    private ProgressBar loading, loading2;
    private Runnable runnable;
    private Handler handler = new Handler();
    private Call<UserResponse> call;
    private Future<?> future;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        recyclerView = view.findViewById(R.id.result);
        searchField = view.findViewById(R.id.searchField);
        cancel = view.findViewById(R.id.cancel);
        loading = view.findViewById(R.id.loading);
        loading2 = view.findViewById(R.id.loading2);
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchValue = searchField.getText().toString();
                adapter.setCurrentlyPlayingPosition(-1);
                if (searchField.getText() != null && !searchValue.isEmpty()) {
                    cancel.setVisibility(View.VISIBLE);
                    if (!searchValue.trim().isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);
                        if (future != null && !future.isDone()) {
                            future.cancel(true);
                        }
                        runnable = makeAPICall(searchValue);
                        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                        future = executorService.schedule(runnable, 1, TimeUnit.SECONDS);
                    }
                } else {
                    if (future != null && !future.isDone()) {
                        future.cancel(true);
                    }
                    cancel.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    adapter.clear();
                }
            }
        };
        if (adapter.getCurrentlyPlayingPosition() != -1) {
            String searchValue = savedInstanceState.getString("searchValue");
            items = savedInstanceState.getParcelableArrayList("items");
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
            searchField.setText(searchValue);
            searchField.addTextChangedListener(textWatcher);
        } else {
            searchField.addTextChangedListener(textWatcher);
        }
        cancel.setOnClickListener(v -> {
            searchField.setText(null);
            adapter.clear();
            adapter.notifyDataSetChanged();
        });
    }

    public Runnable makeAPICall(String searchValue) {
        Runnable r = () -> {
            handler.post(() -> {
                cancel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
            });

            call = apiService.search(searchValue, "all", 0, 20);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        items = response.body().getItems();
                        adapter.setItems(items);
                        handler.post(() -> {
                            adapter.notifyDataSetChanged();
                            loading.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    } else {
                        System.out.println(response);
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        System.out.println("Failed");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    System.out.println(t);
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Failed");
                }
            });
        };
        return r;
    }

    Boolean isStopped = false;
    @Override
    public void onPlayClicked(int position) {
        Runnable runnable1 = () -> {
            handler.post(() -> {
                adapter.enablePlayButton(false);
                adapter.notifyDataSetChanged();
            });
            call = apiService.download(items.get(position).getId());

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String url = response.body().getAudioUrl();
                        System.out.println(url);
                        handler.post(() -> {
                            Player.play(url, mp -> {
                                Player.getPlayer().start();
                                Player.songId = items.get(position).getId();
                                loading2.setVisibility(View.GONE);
                                adapter.enablePlayButton(true);
                                adapter.notifyDataSetChanged();
                            }, mp -> {
                                Player.songId = "";
                                Player.getPlayer().reset();
                                onSongCompleted(position);
                            });
                        });
                    } else {
                        loading2.setVisibility(View.GONE);
                        Player.songId = "";
                        adapter.enablePlayButton(true);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    loading2.setVisibility(View.GONE);
                    Player.songId = "";
                    adapter.enablePlayButton(true);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(t);
                }
            });
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (Player.songId.equals(items.get(position).getId())) {
            Player.stop();
            adapter.notifyItemChanged(position);
        } else {
            if (Player.getPlayer().isPlaying()) {
                Player.stop();
            }
            loading2.setVisibility(View.VISIBLE);
            executorService.execute(runnable1);
        }


    }

    public void onSongCompleted(int pos) {
        adapter.notifyItemChanged(pos);
    }

    private byte[] cover;
    @Override
    public void onAddClicked(int position) {
        String title = items.get(position).getName();
        String artist = items.get(position).getArtists().get(0).getName();
        String coverUrl = items.get(position).getAlbum().getCover().get(0).getUrl();
        String songId = items.get(position).getId();
        Picasso.get().load(coverUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                cover = stream.toByteArray();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        DataSource.addSong(getActivity(), new Songs(title, artist, cover, songId));
        Toast.makeText(getActivity(), "Song added to library", Toast.LENGTH_SHORT).show();
    }

}