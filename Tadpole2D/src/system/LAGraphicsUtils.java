package system;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import tadpole2d.game.LAGraphics;
import tadpole2d.game.LAImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


public class LAGraphicsUtils {
    private static final Map<String, LAImage> cacheImages = new HashMap<String, LAImage>(100);

    public static final LAImage loadLAImage(final String innerFileName) {
        if (innerFileName == null) {
            return null;
        }
        String innerName = replaceIgnoreCase(innerFileName, "\\", "/");
        String keyName = innerName.toLowerCase();
        LAImage image = cacheImages.get(keyName);
        if (image == null) {
            InputStream in = null;
            try {
                in = LAGraphicsUtils.class.getResourceAsStream(innerFileName);
                image = new LAImage(BitmapFactory.decodeStream(in));
                cacheImages.put(keyName, image);
            } catch (Exception e) {
                System.err.println(innerFileName + "not found!");
            } finally {
                try {
                    if (in != null) {
                        in.close();
                        in = null;
                    }
                } catch (IOException e) {
                }
            }
        }
        if (image == null) {
            System.err.println(("File not found.( " + innerFileName + " )").intern());
        }
        return image;
    }

    public static final String replaceIgnoreCase(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            char line2[] = line.toCharArray();
            char newString2[] = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j;
            for (j = i; (i = lcLine.indexOf(lcOldString, i)) > 0; j = i) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
            }

            buf.append(line2, j, line2.length - j);
            return buf.toString();
        } else {
            return line;
        }
    }

    public static LAImage resizeImage(LAImage image, int w, int h) {
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(image.getBitmap(), 0, 0, width, height, matrix, true);
        return new LAImage(resizedBitmap);
    }


    public static LAImage[] getSplitImages(String fileName, int width, int height) {
        return getSplitImages(LAGraphicsUtils.loadLAImage(fileName), width, height);
    }

    public static LAImage[] getSplitImages(LAImage image, int row, int col) {
        int frame = 0;
        int x, y = 0;
        int wlength = image.getWidth() / row;
        int hlength = image.getHeight() / col;
        int total = wlength * hlength;
        LAImage[] images = new LAImage[total];
        for (y = 0; y < hlength; y++) {
            for (x = 0; x < wlength; x++) {
                images[frame] = new LAImage(row, col);
                LAGraphics g = images[frame].getLAGraphics();
                g.drawImage(image, 0, 0, row, col, (x * row), (y * col), row + (x * row), col + (y * col));
                g.dispose();
                frame++;
            }
        }
        return images;
    }

    public static void destroyImages() {
        cacheImages.clear();
    }

}