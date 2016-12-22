package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Custom Constructor
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books   A List of Book objects to display in a list
     */
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Set the book title in the book_title TextView
        TextView bookTitleView = (TextView) listItemView.findViewById(R.id.book_title);
        String bookTitle = currentBook.getTitle();
        bookTitleView.setText(bookTitle);

        // Set the book subtitle (if available) in the book_title TextView
        TextView bookSubTitleView = (TextView) listItemView.findViewById(R.id.book_subtitle);
        String bookSubTitle = currentBook.getSubTitle();
        bookSubTitleView.setText(bookSubTitle);

        // Display the list of authors in the book_authors TextView
        TextView authorsTextView = (TextView) listItemView.findViewById(R.id.book_authors);
        ArrayList<String> authors = currentBook.getAuthors();
        String authorsList = convertAuthorsListToString(authors);
        authorsTextView.setText(authorsList);

        // Set the book publisher in the book_publisher TextView
        TextView publisherTextView = (TextView) listItemView.findViewById(R.id.book_publisher);
        String bookPublisher = currentBook.getPublisher();
        publisherTextView.setText(bookPublisher);

        // Set the book publishing date in the book_published_date TextView
        TextView publishedDateTextView =
                (TextView) listItemView.findViewById(R.id.book_published_date);
        String publishedDate = currentBook.getPublishedDate();
        String formattedPublishedDate = formatDate(publishedDate);
        publishedDateTextView.setText(formattedPublishedDate);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    private String convertAuthorsListToString(ArrayList<String> authors) {
        // Parse ArrayList of authors into a StringBuilder object
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(authors.get(i));
        }
        // Return the final String obtained from the StringBuilder object
        return sb.toString();
    }

    private String formatDate(String date) {
        if (date.length() == 4) {
            Date dateObject = new Date();
            DateFormat df = new SimpleDateFormat("yyyy");
            try {
                dateObject = df.parse(date);
            } catch (java.text.ParseException e) {
                System.out.println("Error formatting date.");
            }
            return df.format(dateObject);
        } else if (date.length() == 7 || date.length() == 10) {
            String dateParts[] = date.split("-");

            Date dateObject = new Date();

            DateFormat dfYear = new SimpleDateFormat("yyyy");
            try {
                dateObject = dfYear.parse(dateParts[0]);
            } catch (java.text.ParseException e) {
                System.out.println("Error formatting date year.");
            }
            String year = dfYear.format(dateObject);

            DateFormat dfMonth = new SimpleDateFormat("LLL");
            DateFormat dfTempMonth = new SimpleDateFormat("LL");
            try {
                dateObject = dfTempMonth.parse(dateParts[1]);
            } catch (java.text.ParseException e) {
                System.out.println("Error formatting date month.");
            }
            String month = dfMonth.format(dateObject);
            StringBuilder sb = new StringBuilder();
            String finalDate = (sb.append(month).append(", ").append(year)).toString();
            return finalDate;
        } else {
            return date;
        }
    }

}
