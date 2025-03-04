package com.example.task8;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<String> videoUris;

    public VideoAdapter(List<String> videoUris) {
        this.videoUris = videoUris;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String videoUri = videoUris.get(position);
        holder.videoView.setVideoURI(Uri.parse(videoUri));

        // Reset video state
        holder.videoView.seekTo(0);
        holder.playButton.setVisibility(View.VISIBLE);

        // Play button logic
        holder.playButton.setOnClickListener(v -> {
            if (holder.videoView.isPlaying()) {
                holder.videoView.pause();
                holder.playButton.setBackgroundResource(android.R.drawable.ic_media_play);
            } else {
                holder.videoView.start();
                holder.playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoUris.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        Button playButton;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoItem);
            playButton = itemView.findViewById(R.id.playButton);
        }
    }
}