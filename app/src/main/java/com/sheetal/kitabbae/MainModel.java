package com.sheetal.kitabbae;

public class MainModel {

    String author,book_cover_image,category,currentuserId,phoneno,publish_year,publisher,title;

    MainModel(){

    }

    public MainModel(String author, String book_cover_image, String category, String currentuserId, String phoneno, String publish_year, String publisher, String title) {
        this.author = author;
        this.book_cover_image = book_cover_image;
        this.category = category;
        this.currentuserId = currentuserId;
        this.phoneno = phoneno;
        this.publish_year = publish_year;
        this.publisher = publisher;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook_cover_image() {
        return book_cover_image;
    }

    public void setBook_cover_image(String book_cover_image) {
        this.book_cover_image = book_cover_image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrentuserId() {
        return currentuserId;
    }

    public void setCurrentuserId(String currentuserId) {
        this.currentuserId = currentuserId;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getPublish_year() {
        return publish_year;
    }

    public void setPublish_year(String publish_year) {
        this.publish_year = publish_year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
