package com.mazksr.youtunify;

import java.util.List;

public class UserResponse {
    private Tracks tracks;
    private YoutubeVideo youtubeVideo;

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public List<Item> getItems() {
        return tracks.getItems();
    }

    public YoutubeVideo getYoutubeVideo() {
        return youtubeVideo;
    }

    public void setYoutubeVideo(YoutubeVideo youtubeVideo) {
        this.youtubeVideo = youtubeVideo;
    }

    public String getAudioUrl() {
        return  youtubeVideo.getAudio().get(0).getUrl();
    }
}
