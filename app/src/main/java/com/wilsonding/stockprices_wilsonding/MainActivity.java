/******************************************************************************
 * This is a stock price picker. Users enter a stock quote ticker symbol, then
 * receive relevant price information about that stock.
 *
 * Name:   Wilson Ding
 * ID:     wxd130130
 * Class:  CS 4301
 ******************************************************************************/

package com.wilsonding.stockprices_wilsonding;

import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import java.net.*;
import java.io.*;


public class MainActivity extends AppCompatActivity {

    private DownloadTickerDataTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (downloadTask != null) {
                    downloadTask.cancel(true);
                }

                downloadTask = new DownloadTickerDataTask();
                downloadTask.execute("INTC");
            }
        });
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

                    System.out.println(builder.toString());

                    return builder.toString();
                // failed to download ticker data
                } else {
                    System.out.println("Fail");
                }
            } catch(IOException e) {
                e.printStackTrace();
                return "";
            }

            return "";
        }

        /****************************************************************************
         * Updates UI with given ticker data csv
         * Written By: Wilson Ding
         ****************************************************************************/
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // do stuff
        }
    }
}
