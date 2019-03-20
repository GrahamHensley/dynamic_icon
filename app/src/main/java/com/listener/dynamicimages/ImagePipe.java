package com.listener.dynamicimages;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Content providers have issues sending streams that are more then 64KB.
 * This pipe facilitates streaming large content through a provider.
 *
 * Hat tip:
 * http://stackoverflow.com/questions/2148301/custom-contentprovider-openinputstream-openoutputstream/14734310#14734310
 */
public class ImagePipe extends Thread {
    private InputStream in;
    private OutputStream out;

    /**
     * Creates a new ImagePipe.
     * @throws NullPointerException if either stream supplied to the constructor is null.
     */
    public ImagePipe(InputStream in, OutputStream out) {
        if (in == null || out == null) {
            throw new NullPointerException();
        }

        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        byte[] buf = new byte[8192];
        int len;

        try {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Caught IOException transferring file", e);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Caught IOException closing the input stream", e);
            }
            try {
                out.flush();
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Caught IOException flushing the output stream", e);
            }
            try {
                out.close();
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Caught IOException closing the output stream", e);
            }
        }
    }
}