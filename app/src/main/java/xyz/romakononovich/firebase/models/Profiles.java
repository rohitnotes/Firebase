package xyz.romakononovich.firebase.models;

import android.net.Uri;

/**
 * Created by rkononovich on 22.06.2017.
 */

public class Profiles {
    private  String avatarPath;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    public String toString() {
        return "Profiles{" +
                "avatarPath='" + avatarPath + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
