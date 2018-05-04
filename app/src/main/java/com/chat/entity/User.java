package com.chat.entity;

/**
 * Created by m on 22.09.2017.
 */

public class User {

    private String objectId;
    private String name;
    private String password;
    private String token;
    private int countNewPost;
    private long lastUpdate;

    public User() {
    }

    public User(String name, String token, long lastUpdate) {
        this.name = name;
        this.token = token;
        this.lastUpdate = lastUpdate;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public int getCountNewPost() {
        return countNewPost;
    }

    public void setCountNewPost(int countNewPost) {
        this.countNewPost = countNewPost;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean myEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        return password != null ? password.equals(user.password) : user.password == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
