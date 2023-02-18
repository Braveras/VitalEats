package com.vitaleats.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe implements Parcelable {

    private String recipeTitle;
    private String recipeIngredients;
    private String recipeElaboration;
    private String tvRecipeServings;
    private String tvRecipeTime;
    private String selectedRecipeType;
    private List<String> tags;
    private List<String> images;
    private String creatorUid;
    private String creatorUsername;
    private float rating;
    private int voteCounter;
    private Date createdAt;
    private Boolean isPublished;
    private DocumentReference Ref;

    public Recipe(String recipeTitle, String recipeIngredients, String recipeElaboration, String tvRecipeServings,
                  String tvRecipeTime, String selectedRecipeType, List<String> tags, List<String> images, String creatorUid,
                  String creatorUsername, float rating, int voteCounter, Date createdAt, Boolean isPublished) {

        this.recipeTitle = recipeTitle;
        this.recipeIngredients = recipeIngredients;
        this.recipeElaboration = recipeElaboration;
        this.tvRecipeServings = tvRecipeServings;
        this.tvRecipeTime = tvRecipeTime;
        this.selectedRecipeType = selectedRecipeType;
        this.tags = tags;
        this.images = images;
        this.creatorUid = creatorUid;
        this.creatorUsername = creatorUsername;
        this.rating = rating;
        this.createdAt = createdAt;
        this.isPublished = isPublished;
        this.voteCounter = voteCounter;
    }

    public Recipe() {

    }

    protected Recipe(Parcel in) {
        recipeTitle = in.readString();
        recipeIngredients = in.readString();
        recipeElaboration = in.readString();
        tvRecipeServings = in.readString();
        tvRecipeTime = in.readString();
        selectedRecipeType = in.readString();
        tags = in.createStringArrayList();
        images = in.createStringArrayList();
        creatorUid = in.readString();
        creatorUsername = in.readString();
        rating = in.readFloat();
        voteCounter = in.readInt();
        createdAt = new Date(in.readLong());
        isPublished = in.readByte() != 0;
        String refPath = in.readString();
        if (refPath != null) {
            Ref = FirebaseFirestore.getInstance().document(refPath);
        }
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeTitle);
        dest.writeString(recipeIngredients);
        dest.writeString(recipeElaboration);
        dest.writeString(tvRecipeServings);
        dest.writeString(tvRecipeTime);
        dest.writeString(selectedRecipeType);
        dest.writeStringList(tags);
        dest.writeStringList(images);
        dest.writeString(creatorUid);
        dest.writeString(creatorUsername);
        dest.writeFloat(rating);
        dest.writeInt(voteCounter);
        dest.writeLong(createdAt.getTime());
        dest.writeByte((byte) (isPublished ? 1 : 0));
        if (Ref != null) {
            dest.writeString(Ref.getPath());
        } else {
            dest.writeString(null);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public String getRecipeElaboration() {
        return recipeElaboration;
    }

    public String getTvRecipeServings() {
        return tvRecipeServings;
    }

    public String getTvRecipeTime() {
        return tvRecipeTime;
    }

    public String getSelectedRecipeType() {
        return selectedRecipeType;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public float getRating() {
        return rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setRef(DocumentReference documentReference) {
        this.Ref = documentReference;
    }

    public DocumentReference getRef() {
        return Ref;
    }

    public int getVoteCounter() {
        return voteCounter;
    }

}
