/******************************************************************************
 * This is a stock price picker. Users enter a stock quote ticker symbol, then
 * receive relevant price information about that stock.
 *
 * Name:   Wilson Ding
 * ID:     wxd130130
 * Class:  CS 4301
 ******************************************************************************/

package com.wilsonding.stockprices_wilsonding;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.net.*;
import java.io.*;


public class MainActivity extends AppCompatActivity {

    private DownloadTickerDataTask downloadTask;


    /****************************************************************************
     * Hides progress bar and list view until needed. Sets onclick listener for search button
     * Written By: Wilson Ding
     ****************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);  // set progressbar to be invisible until button press

        ListView listView = (ListView) findViewById(R.id.listView);

        listView.setVisibility(View.INVISIBLE); // hide list view until successful retrieval of data

        Button searchButton = (Button) findViewById(R.id.button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.editText);

                String symbol = editText.getText().toString();

                if (!symbol.isEmpty()) {
                    if (downloadTask != null) {
                        downloadTask.cancel(true);
                    }

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

                    progressBar.setVisibility(View.VISIBLE);    // show progress bar

                    downloadTask = new DownloadTickerDataTask();
                    downloadTask.execute(symbol);
                }
            }
        });
    }

    /****************************************************************************
     * Creates an alert with the given message
     * Written By: Wilson Ding
     ****************************************************************************/
    public void sendErrorAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Error");
        alertDialog.setMessage("The symbol was not found or took too long to load. Please try again.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /****************************************************************************
     * Public class that downloads ticker data, after taking in a ticker from editText
     * Written By: Wilson Ding
     ****************************************************************************/
    private class DownloadTickerDataTask extends AsyncTask<String, Void, String> {

        /****************************************************************************
         * Takes in a ticker from the editText, then asynchronously gets ticker info
         * Written By: Wilson Ding
         ****************************************************************************/
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://utdallas.edu/~John.Cole/2017Spring/" + params[0] + ".txt");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);    // 2 second connection timeout
                connection.setReadTimeout(45000);   // 45 second read timeout

                connection.connect();

                // was able to successfully download ticker data
                if (connection.getResponseCode() == 200) {
                    InputStream responseInputStream = connection.getInputStream();

                    InputStreamReader isr = new InputStreamReader(responseInputStream, "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder builder = new StringBuilder();

                    for (String line = null; (line = reader.readLine()) != null;) {
                        builder.append(line).append("\n");
                    }

                    reader.close();
                    isr.close();

                    return builder.toString();
                // failed to download ticker data
                } else {
                    return null;
                }
            } catch(IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /****************************************************************************
         * Updates listView with given ticker data csv
         * Written By: Wilson Ding
         ****************************************************************************/
        @Override
        protected void onPostExecute(String result) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            progressBar.setVisibility(View.INVISIBLE);  // hide progress bar

            if (result != null) {
                ListView listView = (ListView) findViewById(R.id.listView);

                listView.setVisibility(View.VISIBLE);   // set listView to be visible

                String[] lines = result.split("\\r?\\n");   // split result by newline into array

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, lines);

                listView.setAdapter(adapter);
            } else {
                ListView listView = (ListView) findViewById(R.id.listView);

                listView.setVisibility(View.INVISIBLE); // hide list view until successful retrieval of data

                sendErrorAlert();
            }
        }
    }
}
