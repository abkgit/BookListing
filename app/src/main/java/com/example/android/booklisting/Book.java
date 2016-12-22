package com.example.android.booklisting;

import java.util.ArrayList;

public class Book {

    private String mTitle;

    private String mSubTitle;

    private ArrayList<String> mAuthors;

    private String mPublisher;

    private String mPublishedDate;

    private String mBookPreviewUrl;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title          is the book's title.
     * @param subtitle       is the extended title for the book.
     * @param authors        is the list of authors.
     * @param publisher      is the publisher name.
     * @param publishedDate  is the publishing date.
     * @param bookPreviewUrl is the Google Books preview URL.
     */
    public Book(String title, String subtitle, ArrayList<String> authors, String publisher,
                String publishedDate, String bookPreviewUrl) {
        mTitle = title;
        mSubTitle = subtitle;
        mAuthors = authors;
        mPublisher = publisher;
        mPublishedDate = publishedDate;
        mBookPreviewUrl = bookPreviewUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getBookPreviewUrl() {
        return mBookPreviewUrl;
    }

}