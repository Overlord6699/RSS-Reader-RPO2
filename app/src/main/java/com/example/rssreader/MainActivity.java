package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> titles;
    ArrayList<String> links;

    private final String  WEBSITE = "https://tech.onliner.by/feed", TEXT_FORMAT ="UTF_8";
    private final String PARSE_ITEM="item",PARSE_LINK="link", PARSE_TITLE="title";

    private void FindElems()
    {
        listView = findViewById(R.id.lv_rss);
    }

    private void Subscribe()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra(ContextContainer.LOAD_DATA_WAY, links.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FindElems();

        titles = new ArrayList<String>();
        links = new ArrayList<String>();

        Subscribe();

        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception exception = null;

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading news...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try {
                URL url = new URL(WEBSITE);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), TEXT_FORMAT);

                boolean insideItem = false;
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase(PARSE_ITEM)) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase(PARSE_TITLE)) {
                            if (insideItem) {
                                titles.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase(PARSE_LINK)) {
                            if (insideItem) {
                                links.add(xpp.nextText());
                            }
                        }
                        //Check EOF or closed item tag
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase(PARSE_ITEM)) {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }

            return exception;
        }
    }
}