package com.professional.b07legendaryproject2026.data;

import java.util.ArrayList;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private boolean isAdmin;
    private ArrayList<String> savedArtifactIds;
    private ArrayList<String> likedArtifactIds;

    //Default Constructor
    public User() {
        this.isAdmin = false;
        this.savedArtifactIds = new ArrayList<>();
        this.likedArtifactIds = new ArrayList<>();
    }

    //Constructor with Parameters
    public User(String id, String email, String username, String password, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.savedArtifactIds = new ArrayList<>();
        this.likedArtifactIds = new ArrayList<>();
    }

    //Get and Set methods
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String  getUsername() {return username;}
    public void  setUsername(String username) {this.username = username;}

    public boolean checkAdmin() {return isAdmin;}
    public void setAdmin(boolean admin) {this.isAdmin = admin;}

    public ArrayList<String> getSavedArtifactsIds() {return savedArtifactIds;}
    public void setSavedArtifactIds(ArrayList<String> savedArtifactIds) {
        if (savedArtifactIds == null) {this.savedArtifactIds = new ArrayList<String>();}

        else {this.savedArtifactIds = savedArtifactIds;}
    }

    public ArrayList<String>  getLikedArtifactIds() {return likedArtifactIds;}
    public void setLikedArtifactIds(ArrayList<String> likedArtifactIds) {
        if (likedArtifactIds == null) {this.likedArtifactIds = new ArrayList<String>();}

        else {this.likedArtifactIds = likedArtifactIds;}
    }

    //Methods for saved and liked artifacts
    public void addToSavedArtifacts(String artifactId) {
        addToList(savedArtifactIds, artifactId);
    }
    public void addToLikedArtifacts(String artifactId) {
        addToList(likedArtifactIds, artifactId);
    }

    public void removeFromSavedArtifacts(String artifactId) {
        removeFromList(savedArtifactIds, artifactId);
    }
    public void removeFromLikedArtifacts(String artifactId) {
        removeFromList(likedArtifactIds, artifactId);
    }

    public boolean isSaved(String artifactId) {
        return savedArtifactIds.contains(artifactId);
    }

    public boolean isLiked(String artifactId) {
        return likedArtifactIds.contains(artifactId);
    }

    //Private helper methods
    private void addToList(ArrayList<String> list,  String artifactId) {
        if (artifactId != null && !list.contains(artifactId)) {
            list.add(artifactId);
        }
    }

    private void removeFromList(ArrayList<String> list,  String artifactId) {
        if (artifactId != null) {
            list.remove(artifactId);
        }
    }
}
