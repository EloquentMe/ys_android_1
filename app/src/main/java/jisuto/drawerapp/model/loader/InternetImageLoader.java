package jisuto.drawerapp.model.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Xml;

import com.android.volley.RequestQueue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.LoadListener;

public class InternetImageLoader extends com.android.volley.toolbox.ImageLoader implements ImageLoader {

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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
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
                    for (int i = 0; i<parser.getAttributeCount(); i++) {
                        if ("height".equals(parser.getAttributeName(i))) {
                            if ("75".equals(parser.getAttributeValue(i))) {
                                return parser.getAttributeValue(i+1);
                            }
                        }
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

    List<String> urls = Collections.emptyList();
    LoadListener listener;

    public InternetImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
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

        /*@Override
        public Bitmap getFullSizeBitmap(int position) {
            get(urls.get(position), new ImageListener() {
                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    pic =  response.getBitmap();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            return null;
        }*/
    }

    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        holder.setContainer(new InternetImageContainer(get(urls.get(position), holder)));
    }

    @Override
    public int total() {
        return urls.size();
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

}