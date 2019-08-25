package com.example.david.booklist;

public class Book {
    int image;
    int title;
    int rating;
    String stitle;
    String sauther;

    private static final int NOT_FOUND = -1;

    public Book() {
        this.image = NOT_FOUND;
        this.rating = NOT_FOUND;
        this.title = NOT_FOUND;
    }

    public Book(int image, int title, int author) {
        this.image = image;
        this.title = title;
        this.rating = author;
    }

    public Book(int image, String title, String author) {
        this.image = image;
        this.stitle = title;
        this.sauther = author;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getImage() {
        return image;
    }

    public int getTitle() {
        return title;
    }

    public int getRating() {
        return rating;
    }

    public String getSauther() {
        return sauther;
    }

    public String getStitle() {
        return stitle;
    }
}
