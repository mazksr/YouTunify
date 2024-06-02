package com.mazksr.youtunify;

public class Songs {
    private String title, artist, songId;
    private int id;
    private byte[] cover;

    public Songs(int id, String title, String artist, byte[] cover, String songId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.cover = cover;
        this.songId = songId;
    }

    public Songs(String title, String artist, byte[] cover, String songId) {
        this.title = title;
        this.artist = artist;
        this.cover = cover;
        this.songId = songId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }
}
