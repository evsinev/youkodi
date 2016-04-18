package com.payneteasy.youkodi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    KodiRemoteService kodiRemoteService = new KodiRemoteService();

    TextView labelUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        labelUrl = (TextView) findViewById(R.id.label_url);
        setTitle("Main Activity");
        updateTitle("https://youtu.be/dQw4w9WgXcQ");
    }

    private void updateTitle(final String aUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final String title = kodiRemoteService.getMediaInfo(aUrl).title;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        labelUrl.setText( title );
                    }
                });
            }
        });
        thread.start();
    }

}
