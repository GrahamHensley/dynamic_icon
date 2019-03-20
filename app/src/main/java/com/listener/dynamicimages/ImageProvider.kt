package com.listener.dynamicimages

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.graphics.Bitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import android.graphics.BitmapFactory
import android.util.Log
import android.R.attr.start
import android.R.attr.bitmap




class ImageProvider : ContentProvider() {
    // Uri matcher to decode incoming URIs.
    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(ImageContract.AUTHORITY, ImageContract.PATH_LOGO, 100)
        uriMatcher.addURI(ImageContract.AUTHORITY, "${ImageContract.PATH_LOGO}/*", 101)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Throws(UnsupportedOperationException::class)
    override fun getType(uri: Uri): String? {
        val match = uriMatcher.match(uri)
        return when(match) {
            100, 101 -> "vnd.android.cursor.item/vnd.logo"
            else -> throw UnsupportedOperationException("Unknown URI " + uri)
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val match = uriMatcher.match(uri)

        var requestedBitmap: Bitmap? = when(match) {
            100 -> {
                val cal = Calendar.getInstance()
                cal.time =  Date()
                val month = cal.get(Calendar.MONTH)

                generateLogo(month)
            }
            101 -> {

                generateLogo(ImageContract.Logo.getMonth(uri))
            }
            else -> throw UnsupportedOperationException("Unknown URI " + uri)

        }

        return send(requestedBitmap)
    }

    private fun generateLogo(monthInt: Int?): Bitmap? {

        context?.apply {
            val size = 100

            return when (monthInt) {
                0 -> sampleAndScaleImage(this.resources, R.drawable.fallout_snoo, size.toInt(), size.toInt())
                1 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt())
                2 -> sampleAndScaleImage(this.resources, R.drawable.stp_snoo, size.toInt(), size.toInt()) //st patrick
                3 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt()) //easter
                4 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt())
                5 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt())
                6 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt())//4th
                7 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt())
                8 -> sampleAndScaleImage(this.resources, R.drawable.fall_snoo, size.toInt(), size.toInt())
                9 -> sampleAndScaleImage(this.resources, R.drawable.halloween_snoo, size.toInt(), size.toInt())//halloween
                10 -> sampleAndScaleImage(this.resources, R.drawable.default_snoo, size.toInt(), size.toInt()) //turkey
                11 -> sampleAndScaleImage(this.resources, R.drawable.snoo_xmess, size.toInt(), size.toInt()) // xmas
                else -> throw UnsupportedOperationException("Unknown month " + monthInt)
            }
        }

        return null
    }

    private fun send(bitmap: Bitmap?): ParcelFileDescriptor? {
        //compress the final bitmap into an input stream
        val bytesOutStream =  ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, bytesOutStream)


        //transfer pipe
        var filePipe: Array<ParcelFileDescriptor>?

        //stream our bitmap to the client through our file pipe
        try {
            filePipe = ParcelFileDescriptor.createPipe()

            ImagePipe(ByteArrayInputStream(bytesOutStream.toByteArray()),
                    ParcelFileDescriptor.AutoCloseOutputStream(filePipe[1])).start()


        } catch (e: IOException) {

            throw FileNotFoundException("Could not open pipe")
        }

        return (filePipe?.get(0))

    }

        /**
     * Create a subsampled image from resource
     * @param res - image file to make int a thumbnail
     * @param imageResource - cache key to use for completed thumbnail
     * @param width - thumbnail width
     * @param height - thumbnail height
     * @return Bitmap of a Bitmap thumbnail
     * @throws IOException
     */
    fun sampleAndScaleImage(res: Resources, imageResource: Int, width: Int, height: Int) : Bitmap {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inDither = true //dither bitmap
        options.inScaled = false //we are doing our own scaling

        BitmapFactory.decodeResource(res, imageResource, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inScaled = false

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeResource(res, imageResource, options)

    }

    /**
     * determine the sub-sample size of an image to thumbnail
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize;
    }
}