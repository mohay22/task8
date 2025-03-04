package com.example.task8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;

public class UploadFragment extends Fragment {
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final String PREFS_NAME = "VideoPrefs";
    private static final String VIDEO_URIS_KEY = "videoUris";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        Button uploadButton = view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> openVideoPicker());

        return view;
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            saveVideoUri(videoUri);
            Toast.makeText(getContext(), "Video uploaded successfully", Toast.LENGTH_SHORT).show();
            // Notify DisplayFragment via MainActivity
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).notifyVideoAdded();
            }
        }
    }

    private void saveVideoUri(Uri videoUri) {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Set<String> videoUris = prefs.getStringSet(VIDEO_URIS_KEY, new HashSet<>());
        Set<String> updatedUris = new HashSet<>(videoUris);
        updatedUris.add(videoUri.toString());
        prefs.edit().putStringSet(VIDEO_URIS_KEY, updatedUris).apply();
    }
}