package com.example.projectprm392.Domain;

public class FeedbackDomain {
    private String comment;
    private String id;
    private float ranking; // Changed to 'ranking' to match your structure
    private String title;
    private String userId;

    public FeedbackDomain(String comment) {
        this.comment = comment;
    }

    public FeedbackDomain(String comment, String id, float ranking, String title, String userId) {
        this.comment = comment;
        this.id = id;
        this.ranking = ranking;
        this.title = title;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getRanking() {
        return ranking;
    }

    public void setRanking(float ranking) {
        this.ranking = ranking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
