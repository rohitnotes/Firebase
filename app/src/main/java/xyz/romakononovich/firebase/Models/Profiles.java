package xyz.romakononovich.firebase.Models;

import android.net.Uri;

import java.net.URI;

/**
 * Created by rkononovich on 22.06.2017.
 */

public class Profiles {
    private String name;
    private String phone;
    private String id;
    private Uri uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "Profiles{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
                ", uri=" + uri +
                '}';
    }
}
