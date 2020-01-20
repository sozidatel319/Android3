package com.example.myapplication.user.list;

import java.util.Objects;

public final class User {
    long id;
    String name;
    String avatarUrl;
    String weblink;

    public User(long id, String name, String avatarUrl, String weblink) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.weblink = weblink;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getWeblink() {
        return weblink;
    }

    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(avatarUrl, user.avatarUrl) &&
                Objects.equals(weblink, user.weblink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, avatarUrl, weblink);
    }
}
