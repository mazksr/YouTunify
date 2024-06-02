package com.mazksr.youtunify;

import android.content.Context;
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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment implements LibraryInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    private RecyclerView recyclerView;
    private LibraryAdapter adapter;
    private Handler handler = new Handler();
    private Call<UserResponse> call;
    private ApiService apiService;
    private List<Songs> items = DataSource.songs;
    private List<Songs> searchResult = DataSource.searchResult;
    private ProgressBar loading2;
    private EditText searchField;
    private ImageView cancel;
    private LibraryInterface c = this;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchField = view.findViewById(R.id.searchField);
        loading2 = view.findViewById(R.id.loading2);
        recyclerView = view.findViewById(R.id.recycler_view);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        cancel = view.findViewById(R.id.cancel);

        DataSource.getData(getContext());
        adapter = new LibraryAdapter(this,getActivity(), items);
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
                String searchValue = searchField.getText().toString().trim();
                if (searchField.getText() != null && !searchValue.isEmpty()) {
                    cancel.setVisibility(View.VISIBLE);
                    Runnable runnable = () -> {
                        DataSource.searchData(getContext(), searchValue);
                    };
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(runnable);

                    adapter = new LibraryAdapter(c, getContext(), DataSource.searchResult);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter = new LibraryAdapter(c, getContext(), items);
                    recyclerView.setAdapter(adapter);
                }


            }
        };
        searchField.addTextChangedListener(textWatcher);
        cancel.setOnClickListener(v -> {
            searchField.setText(null);
            adapter = new LibraryAdapter(c, getContext(), items);
            recyclerView.setAdapter(adapter);
        });
    }

    public void play() {
        int position = Player.libraryPos;
        Runnable runnable1 = () -> {
            handler.post(() -> {
                adapter.enableItemClick(false);
                adapter.notifyDataSetChanged();
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

                                    loading2.setVisibility(View.GONE);
                                    adapter.enableItemClick(true);
                                    adapter.notifyDataSetChanged();
                                }, mp -> {
                                    onSongCompleted();
                                });
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        loading2.setVisibility(View.GONE);
                        adapter.enableItemClick(true);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    loading2.setVisibility(View.GONE);
                    Player.songId = "";
                    adapter.enableItemClick(true);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(t);
                }
            });
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable1);
    }

    public void onSongCompleted() {
        if (Player.libraryPos < items.size() - 1) {
            loading2.setVisibility(View.VISIBLE);
            Player.libraryPos++;
            adapter.notifyItemChanged(Player.libraryPos);
            play();
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (position != Player.libraryPos) {
            adapter.notifyItemChanged(position);
            Player.stop();
            loading2.setVisibility(View.VISIBLE);
            Player.libraryPos = position;
            play();
        }
    }
}