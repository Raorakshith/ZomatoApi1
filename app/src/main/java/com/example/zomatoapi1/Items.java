package com.example.zomatoapi1;

public class Items {
    private String name;
    private String photos_url;
    private String location;

    public Items() {
    }

    public Items(String name, String photos_url, String location) {
        this.name = name;
        this.photos_url = photos_url;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotos_url() {
        return photos_url;
    }

    public void setPhotos_url(String photos_url) {
        this.photos_url = photos_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
