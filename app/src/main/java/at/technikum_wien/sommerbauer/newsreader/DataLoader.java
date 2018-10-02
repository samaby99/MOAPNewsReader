package at.technikum_wien.sommerbauer.newsreader;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.Html;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import at.technikum_wien.sommerbauer.newsreader.data.NewsItem;

public class DataLoader extends AsyncTaskLoader<List<NewsItem>> {
    private static final String LOG_TAG = DataLoader.class.getCanonicalName();
    public static final String ARG_URL = "URL";
    private Bundle mArgs;
    private List<NewsItem> mResult;

    DataLoader(Context context, Bundle args) {
        super(context);
        mArgs = args;
    }

    @Override
    protected void onStartLoading() {
        if (mArgs == null)
            return;
        if (mResult != null)
            deliverResult(mResult);
        else
            forceLoad();
    }

    @Override
    public void deliverResult(@Nullable List<NewsItem> data) {
        mResult = data;
        super.deliverResult(data);
    }

    @Nullable
    @Override
    public List<NewsItem> loadInBackground() {
        final List<NewsItem> newsItems = new LinkedList<>();
        HttpsURLConnection urlConnection = null;
        InputStream is = null;

        try {
            URL url = new URL(mArgs.getString(ARG_URL));
            urlConnection = (HttpsURLConnection) url.openConnection();
            is = urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = parserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (SAXException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        DefaultHandler handler = new DefaultHandler(){
            boolean mIsInsideItem = false;
            boolean mIsInsideSubTag = false;
            private String mCurrentTag = "";
            private String mCurrentString = "";
            private NewsItem mNewsItem = new NewsItem();
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if ( mIsInsideItem ) {
                    mCurrentTag = localName;
                    mIsInsideSubTag = true;
                } else if ( localName.equals("item") ) {
                    mIsInsideItem = true;
                    mNewsItem = new NewsItem();
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if ( localName.equals("item") ) {
                    mIsInsideItem = false;
                    newsItems.add(mNewsItem);
                }
                if ( mIsInsideSubTag ) {
                    switch (mCurrentTag) {
                        case "title":
                            mNewsItem.setTitle(mCurrentString);
                            break;

                        case "link":
                            try {
                                mNewsItem.setLink(new URL(mCurrentString));
                            } catch (MalformedURLException e) {
                                Log.w(LOG_TAG, "broken url: " + mCurrentString);
                                mNewsItem.setLink(null);
                            }
                            break;

                        case "description":
                            // Manually parse image URL and description
                            int imageURLBegin = mCurrentString.indexOf("src=\"");
                            int imageURLEnd = mCurrentString.indexOf("\"", imageURLBegin + 6);
                            int imageTagEnd = mCurrentString.indexOf(">", imageURLEnd);
                            String imageURL = mCurrentString.substring(imageURLBegin + 5, imageURLEnd);
                            String description = mCurrentString.substring(imageTagEnd + 1).trim();

                            // unescape HTML entities
                            if (Build.VERSION.SDK_INT >= 24)
                            {
                                description = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY).toString();
                            }
                            else
                            {
                                description = Html.fromHtml(description).toString();
                            }

                            try {
                                mNewsItem.setImageURL(new URL(imageURL));
                            } catch (MalformedURLException e) {
                                Log.w(LOG_TAG, "broken url: " + imageURL);
//                                mNewsItem.setImageURL(null);
                            }
                            mNewsItem.setDescription(description);
                            break;

                        case "identifier":
                            mNewsItem.setUniqueID(mCurrentString);
                            break;

                        case "creator":
                            mNewsItem.setCreator(mCurrentString);
                            break;

                        case "pubDate":
                            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

                            try {
                                mNewsItem.setPublicationDate(format.parse(mCurrentString));
                            } catch (ParseException e) {
                                Log.w(LOG_TAG, "could not format date: " + mCurrentString);
                                //                                mNewsItem.setPublicationDate(null);
                            }
                            break;

                        case "category":
                            mNewsItem.addKeyword(mCurrentString);

                        default:
                            // ignore and continue
                    }
                    mIsInsideSubTag = false;
                    mCurrentString = "";
                }

            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if ( mIsInsideItem && mIsInsideSubTag ) {
                    mCurrentString += new String(ch, start, length);
                 }
            }
        };

        if ( is == null && parser == null ) {
            return null;
        } else {
            try {
                parser.parse(is, handler);
            } catch (SAXException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return newsItems;
        }
    }
}
