package com.call.colorscreen.ledflash.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.Nullable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class FileUtil {
    companion object {
        fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
            return if (contentUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createCopyAndReturnRealPath(context,contentUri)
                    //getFilePathForN(context, contentUri)
                } else {
                    var cursor: Cursor? = null
                    try {
                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
                        if (cursor != null) {
                            val column_index =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            cursor.moveToFirst()
                            cursor.getString(column_index)
                        } else {
                            ""
                        }
                    } finally {
                        cursor?.close()
                    }
                }
            } else ""
        }

        @Nullable
        fun createCopyAndReturnRealPath(
            context: Context, uri: Uri
        ): String? {
            val contentResolver = context.contentResolver ?: return null

            // Create file path inside app's data dir
            val filePath = (context.filesDir.absolutePath + File.separator
                    + System.currentTimeMillis())
            val file = File(filePath)
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return null
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                outputStream.close()
                inputStream.close()
            } catch (ignore: IOException) {
                return null
            }
            Log.e("TAN", "createCopyAndReturnRealPath: "+file.absolutePath)
            return file.absolutePath
        }
        private fun getFilePathForN(context: Context, uri: Uri): String {
            val file: File
            var returnCursor: Cursor? = null
            try {
                returnCursor = context.contentResolver.query(uri, null, null, null, null)
                /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
                Log.e("TAN", "getFilePathForN:1 ")

                val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                val name = returnCursor.getString(nameIndex)
                file = File(context.filesDir, name)
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1024 * 1024
                val bytesAvailable = inputStream!!.available()
                Log.e("TAN", "getFilePathForN:11 ")

                //int bufferSize = 1024;
                val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
                Log.e("TAN", "getFilePathForN:111 ")

            } catch (e: Exception) {
                Log.e("Exception", e.message!!)
            } finally {
                returnCursor?.close()
            }
            Log.e("TAN", "getFilePathForN:2 ")
            return ""
        }
        fun saveBitmap(path: String?, bitmap: Bitmap) {
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(path)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}