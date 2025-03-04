package com.example.task8;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DisplayFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private static final String PREFS_NAME = "VideoPrefs";
    private static final String VIDEO_URIS_KEY = "videoUris";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadVideos();

        return view;
    }

    private void loadVideos() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Set<String> videoUris = prefs.getStringSet(VIDEO_URIS_KEY, new HashSet<>());
        videoAdapter = new VideoAdapter(new ArrayList<>(videoUris));
        recyclerView.setAdapter(videoAdapter);
    }

    public void refreshVideoList() {
        if (videoAdapter != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
            Set<String> videoUris = prefs.getStringSet(VIDEO_URIS_KEY, new HashSet<>());
            videoAdapter = new VideoAdapter(new ArrayList<>(videoUris));
            recyclerView.setAdapter(videoAdapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            VideoView videoView = child.findViewById(R.id.videoItem);
            if (videoView != null && videoView.isPlaying()) {
                videoView.pause();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshVideoList();  // Refresh on resume too
    }
}