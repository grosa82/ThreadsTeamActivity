package com.toughland.ricardo.threadsteamactivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends ActionBarActivity {

    /**
     * Used to show action chosen
     */
    public enum actionType{
        create,
        load,
        clean
    }

    /**
     * Used to populate the ListView
     */
    ArrayAdapter<String> adapter;

    public class FileManipulation extends AsyncTask<actionType, Integer, actionType> {

        /**
         * Create/Write a file
         * @param data text to write down
         */
        private void writeToFile(String data) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read file
         * @return content of the file
         */
        private String readFromFile() {

            String ret = "";

            try {
                InputStream inputStream = openFileInput("data.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        @Override
        protected actionType doInBackground(actionType... actions) {
            try {
                if (actions[0] == actionType.create) {
                    // create action

                    writeToFile("1-2-3-4-5-6-7-8-9-10");

                    // taking a nap and publishing the progress bar
                    for (int i = 0; i <= 100; i += 10) {
                        Thread.sleep(250);
                        publishProgress(i);
                    }
                }
                else if (actions[0] == actionType.load) {
                    // load action

                    String content = readFromFile();
                    String[] array = content.split("-");

                    adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, array );

                    // taking a nap and publishing the progress bar
                    for (int i = 0; i <= 100; i += 10) {
                        Thread.sleep(250);
                        publishProgress(i);
                    }
                } else {
                    // clear action

                    publishProgress(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return actions[0];
        }

        @Override
        protected void onPostExecute(actionType action) {
            // do some final stuff according to the action chosen
            if (action == actionType.create)
                MainActivity.this.showAlert("File Created");
            else if (action == actionType.load) {
                ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
                MainActivity.this.showAlert("File Loaded");
            } else {
                adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, new String[0] );
                ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
                MainActivity.this.showAlert("Everything is now clean");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Event handler for create file
     * @param view
     */
    public void create(View view) {
        new FileManipulation().execute(actionType.create);
    }

    /**
     * Event handler for load the file
     * @param view
     */
    public void load(View view) {
        new FileManipulation().execute(actionType.load);
    }

    /**
     * Event handler for clear the list view
     * @param view
     */
    public void clear(View view) {
        new FileManipulation().execute(actionType.clean);
    }

    /**
     * Set the value of the progress bar
     * @param percentage number between 0 and 100
     */
    public void setProgressPercent(Integer percentage) {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setProgress(percentage);
    }

    /**
     * Display a temporary message on screen
     * @param message message to display
     */
    private void showAlert(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
