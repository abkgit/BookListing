package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookActivity.class.getName();

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Segments of the URL for retrieving book data from the Google Books API
     */
    private static final String GBOOKS_REQUEST_URL_PART1 =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private String mbookQueryText = null;
    private static final String GBOOKS_REQUEST_URL_PART2 = "&maxResults=10";

    /**
     * URL for the book query based on user input
     */
    private String mGBooksRequestUrl = null;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        //Hide the progress indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(GONE);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);
        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.hello);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(BookActivity.this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Handle device rotation
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            // Get the user typed text from the EditText view
            loaderManager.initLoader(BOOK_LOADER_ID, null, BookActivity.this);
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Find a reference to the {@link EditText} in the layout
        EditText bookQuery = (EditText) findViewById(R.id.book_search_text);

        // Update the list when user inputs the query and presses "Enter" on keyboard
        bookQuery.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "Enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    onQueryEventFunctionality();

                    return true;
                }
                return false;
            }
        });

        // Find a reference to the Search Button {@link ImageView} in the layout
        ImageView searchButton = (ImageView) findViewById(R.id.search_button);

        // Set an click listener on the search button ImageView, which initiates the search
        // for the book title entered by the user.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onQueryEventFunctionality();
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getBookPreviewUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    // Method to get user input from the EditText, build a request URL and perform network and
    // list update operations via a Loader provided a working internet connection is available.
    private void onQueryEventFunctionality() {
        hideKeypad();
        // Find a reference to the {@link EditText} in the layout
        EditText bookQuery = (EditText) findViewById(R.id.book_search_text);
        // Get the user typed text from the EditText view
        mbookQueryText = bookQuery.getText().toString();
        // Make the final URL, based on user input, to be used to fetch data from the server
        mGBooksRequestUrl = makeUrlFromInput(mbookQueryText);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        mEmptyStateTextView.setVisibility(GONE);

        //Show the progress indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(VISIBLE);

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URL
        return new BookLoader(this, mGBooksRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        //Hide the progress indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(GONE);

        // Exclude app startup message
        if (mEmptyStateTextView.getText().toString() != getApplicationContext().getString(R.string.hello)) {
            // Set empty state text to display "No books found."
            mEmptyStateTextView.setText(R.string.no_books);
        }

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        // Clear the adapter of previous book data
        mAdapter.clear();
    }

    // Method to make a final URL combining the different URL parts and the user input
    private String makeUrlFromInput(String bookQueryText) {

        // Replace white spaces with a + symbol to make it compatible to be used in the JSON
        // request URL
        bookQueryText.replaceAll(" ", "+");

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder = urlBuilder.append(GBOOKS_REQUEST_URL_PART1)
                .append(bookQueryText)
                .append(GBOOKS_REQUEST_URL_PART2);

        // First encode into UTF-8, then back to a form easily processed by the API
        // This is mainly to avoid issues with spaces and other special characters in the URL
        String finalUrl = Uri.encode(urlBuilder.toString()).replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%3A", ":")
                .replaceAll("\\%2F", "/")
                .replaceAll("\\%3F", "?")
                .replaceAll("\\%26", "&")
                .replaceAll("\\%3D", "=")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%20", "\\+")
                .replaceAll("\\%7E", "~");
        return finalUrl;
    }

    // Method to hide the soft-keyboard used in events where it tends to pop-up automatically,
    // for example, when the screen-orientation changes
    private void hideKeypad() {
        EditText editTextView = (EditText) findViewById(R.id.book_search_text);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }

}

