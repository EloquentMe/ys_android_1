package jisuto.drawerapp.model.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.Xml;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.LoadListener;
import jisuto.drawerapp.utils.SingletonCarrier;

public class InternetImageLoader extends com.android.volley.toolbox.ImageLoader
        implements ImageLoader {

    private class NetworkTask extends AsyncTask<String, Void, List<InternetImageInfo>> {

        @Override
        protected List<InternetImageInfo> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(stream, null);
                    InternetImageInfo imgUrl;
                    List<InternetImageInfo> imgUrls = new ArrayList<>();
                    while ((imgUrl = processEntry(parser)) != null) {
                        imgUrls.add(imgUrl);
                    }
                    return imgUrls;
                }
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        private InternetImageInfo processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
            InternetImageInfo info = new InternetImageInfo();
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String prefix = parser.getPrefix();
                // Starts by looking for the entry tag
                if ("f".equals(prefix) && "img".equals(name)) {
                    if (parser.getAttributeValue(null, "size").equals("XXS"))  {
                        info.thumbUrl = parser.getAttributeValue(null, "href");
                    }
                    if (parser.getAttributeValue(null, "size").equals("L")) {
                        info.fullUrl = parser.getAttributeValue(null, "href");
                        return info;
                    }
                }
                else if (name.equals("title")) {
                    info.title = parser.nextText();
                }
                else if (name.equals("name")) {
                    info.author = parser.nextText();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<InternetImageInfo> xml) {
            imgInfos = xml;
            listener.countAcquired();
        }
    }

    private List<InternetImageInfo> imgInfos;
    private LoadListener listener;

    public InternetImageLoader(RequestQueue queue) {
        super(queue, SingletonCarrier.getInstance().getCommonCache());
        imgInfos = Collections.emptyList();
    }

    public class InternetImageContainer implements ImageHolder.ImageContainer {

        private ImageContainer trueContainer;

        InternetImageContainer(ImageContainer trueContainer) {
            this.trueContainer = trueContainer;
        }

        @Override
        public void cancelRequest() {
            trueContainer.cancelRequest();
        }

        @Override
        public ImageSource getSource() {
            return ImageSource.INTERNET;
        }

    }

    /*
     * Caching is done inside volley
     */
    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        InternetImageInfo info = imgInfos.get(position);
        ImageContainer volleyContainer = get(info.thumbUrl, holder);
        InternetImageContainer container = new InternetImageContainer(volleyContainer);
        holder.setContainer(container);
    }

    @Override
    public int total() {
        return imgInfos == null ? 0 : imgInfos.size();
    }

    @Override
    public void acquireCount() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z/'", Locale.UK);
        new NetworkTask().execute("http://api-fotki.yandex.ru/api/podhistory/poddate;"+ sdf.format(new Date()));
    }

    @Override
    public void onAcquireCount(LoadListener eventListener) {
        this.listener = eventListener;
    }

    @Override
    @WorkerThread
    public Bitmap getBitmap(int position) throws IOException {
        String fullUrl = imgInfos.get(position).fullUrl;
        ImageCache cache = SingletonCarrier.getInstance().getCommonCache();
        Bitmap b = cache.getBitmap(fullUrl);
        if (b == null) {
            URL url = new URL(fullUrl);
            URLConnection conn = url.openConnection();
            b = BitmapFactory.decodeStream(conn.getInputStream());
            cache.putBitmap(fullUrl, b);
        }
        return b;
    }

    @Override
    public String getAuthor(int position) {
        return imgInfos.get(position).author;
    }

    @Override
    public String getTitle(int position) {
        return imgInfos.get(position).title;
    }

    private class InternetImageInfo implements Serializable {
        private String thumbUrl;
        private String fullUrl;
        private String author;
        private String title;

    }

}
