package jisuto.drawerapp.tab;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jisuto.drawerapp.R;
import jisuto.drawerapp.model.InternetImageAdapter;


public class InternetImagesFragment extends Fragment {

    private int columnCount = 4;
    private GridLayoutManager lLayout;
    private RecyclerView rView;

    public InternetImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_images, container, false);
        //List<LocalItemObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(getActivity().getApplicationContext(), columnCount);

        rView = (RecyclerView) x.findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        new NetworkTask().execute("http://api-fotki.yandex.ru/api/podhistory/poddate;2012-04-01T12:00:00Z/");
        return x;
    }

    public static LocalImagesFragment newInstance(int cols) {
        LocalImagesFragment newFragment = new LocalImagesFragment();
        Bundle args = new Bundle();
        args.putInt("columnCount", cols);
        newFragment.setArguments(args);

        return newFragment;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int cols) {
        if (columnCount == cols)
            return;

        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        LocalImagesFragment newFragment = newInstance(cols);
        ft.replace(R.id.tabs, newFragment); //tabs???
        ft.commit();
    }

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
            //rView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
            rView.setAdapter(new InternetImageAdapter(xml));
        }
    }
}
