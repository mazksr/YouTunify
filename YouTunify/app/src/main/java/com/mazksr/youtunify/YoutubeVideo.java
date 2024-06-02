package com.mazksr.youtunify;

import java.util.List;

public class YoutubeVideo {
    private List<Audio> audio;

    public List<Audio> getAudio() {
        return audio;
    }

    public void setAudio(List<Audio> audio) {
        this.audio = audio;
    }
}

class Audio {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
