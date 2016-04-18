package com.payneteasy.youkodi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.payneteasy.youkodi.model.MediaInfo;

import java.net.URL;

public class YoutubeActivity extends AppCompatActivity {


    KodiRemoteService kodiRemoteService = new KodiRemoteService();
    TextView labelUrl;
    TextView labelResponse;
    TextView labelTitle;
    ImageView imageThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        setTitle("youtube kodi");

        labelUrl = (TextView) findViewById(R.id.label_url);
        labelTitle = (TextView) findViewById(R.id.label_title);
        labelResponse = (TextView) findViewById(R.id.label_response);
        imageThumbnail = (ImageView) findViewById(R.id.image_thumbnail);

        Intent intent = getIntent();
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        final String url = extras.getString(Intent.EXTRA_TEXT);

        labelUrl.setText(url);

        updateTitle(url);

        labelResponse.setText( kodiRemoteService.getVideoId(url) + " ...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = kodiRemoteService.playYoutube(url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        labelResponse.setText( response );
                    }
                });
            }
        });
        thread.start();
    }

    private void updateTitle(final String aUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                MediaInfo mediaInfo = kodiRemoteService.getMediaInfo(aUrl);
                if(mediaInfo.imageUrl != null) {
                    updateImage(mediaInfo.imageUrl);
                }
                final String title = mediaInfo.title;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        labelTitle.setText( title );
                    }
                });
            }
        });
        thread.start();
    }

    private void updateImage(final String aImageUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(aImageUrl);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageThumbnail.setImageBitmap(bmp);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("new intent");

        handleIntent(intent);
    }


}
