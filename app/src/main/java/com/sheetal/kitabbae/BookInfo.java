package com.sheetal.kitabbae;

public class BookInfo {
    private String bookName;

    // string variable for storing
    // employee contact number
    private String bookAuthor;

    // string variable for storing
    // employee address.
    private String bookPublisher;

    private String publishYear;

    private String bookCategory;

    private String currentUId;
    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public BookInfo() {

    }

    // created getter and setter methods
    // for all our variables.
    public String getbookName() {
        return bookName;
    }

    public void setbookName(String bookName) {
        this.bookName = bookName;
    }

    public String getbookAuthor() {
        return bookAuthor;
    }

    public void setbookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getbookPublisher() {
        return bookPublisher;
    }

    public void setbookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }
    public String getbookPublishYear() {
        return publishYear;
    }

    public void setbookPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public String setbookCategory(String category) {
        return bookCategory;
    }

    public void getbookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String get(String currentUId) {
        return currentUId;
    }
    public void set(String currentUId) {
        this.currentUId=currentUId;
    }
}
