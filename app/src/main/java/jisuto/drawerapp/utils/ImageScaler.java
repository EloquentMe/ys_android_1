package jisuto.drawerapp.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import jisuto.drawerapp.R;

public class ImageScaler {

    private static SoftReference<Bitmap> placeholderRef;

    static {
        placeholderRef = new SoftReference<>(null);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {
        Point p = getScreenDimensions();
        p.x *= SingletonCarrier.SCALE_FACTOR;
        p.y *= SingletonCarrier.SCALE_FACTOR;
        return decodeSampledBitmapFromResource(res, resId, p.x, p.y);
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream in) throws IOException {
        Point p = getScreenDimensions();
        int reqWidth = p.x * SingletonCarrier.SCALE_FACTOR;
        int reqHeight = p.y * SingletonCarrier.SCALE_FACTOR;
        BufferedInputStream buf = new BufferedInputStream(in);
        buf.mark(0);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(in, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        buf.reset();
        return BitmapFactory.decodeStream(in, null, options);
    }

    public static Bitmap getPlaceholder(Resources res) {
        Bitmap placeholder = placeholderRef.get();
        if (placeholder == null) {
            placeholder = BitmapFactory.decodeResource(res, R.drawable.placeholder);
            placeholderRef = new SoftReference<>(placeholder);
        }
        return placeholder;
    }

    private static Point getScreenDimensions() {
        Point p = new Point();
        ((WindowManager) SingletonCarrier.getInstance().getContext()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getSize(p);
        return p;
    }
}
