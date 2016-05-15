package jisuto.drawerapp.model.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Xml;

import com.android.volley.RequestQueue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.BitmapCache;
import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.LoadListener;
import jisuto.drawerapp.utils.SingletonCarrier;

public class InternetImageLoader extends com.android.volley.toolbox.ImageLoader
        implements ImageLoader {

    private class NetworkTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(stream, null);
                    String imgUrl;
                    List<String> imgUrls = new ArrayList<>();
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

        private String processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String prefix = parser.getPrefix();
                // Starts by looking for the entry tag
                if ("f".equals(prefix) && "img".equals(name)) {
                    if (parser.getAttributeValue(null, "size").equals("XXS")) {
                        return parser.getAttributeValue(null, "href");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> xml) {
            urls = xml;
            listener.countAcquired();
        }
    }

    private List<String> urls;
    private LoadListener listener;

    public InternetImageLoader(RequestQueue queue) {
        super(queue, SingletonCarrier.getInstance().getCommonCache());
        urls = Collections.emptyList();
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
        String url = urls.get(position);
        ImageContainer volleyContainer = get(url, holder);
        InternetImageContainer container = new InternetImageContainer(volleyContainer);
        holder.setContainer(container);
    }

    @Override
    public int total() {
        return urls == null ? 0 : urls.size();
    }

    @Override
    public void acquireCount() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z/'");
        new NetworkTask().execute("http://api-fotki.yandex.ru/api/podhistory/poddate;"+ sdf.format(new Date()));
    }

    @Override
    public void onAcquireCount(LoadListener eventListener) {
        this.listener = eventListener;
    }

    @Override
    public Bitmap getBitmap(Object id) throws IOException {
        //TODO
        Resources res = SingletonCarrier.getInstance().getContext().getResources();
        return ImageScaler.getPlaceholder(res);
    }

}
