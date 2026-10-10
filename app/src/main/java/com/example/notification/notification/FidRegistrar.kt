package com.example.notification.notification

import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object FidRegistrar {
    // Đổi thành IP máy tính chạy server (emulator dùng 10.0.2.2)
    private const val SERVER_URL = "http://192.168.1.17:8000"
    private const val TAG = "TokenRegistrar"
    private const val TIMEOUT_MILLIS = 5_000

    fun register(fid: String) {
        // Android cấm gọi mạng trên luồng chính (NetworkOnMainThreadException)
        thread {
            val connection = URL("$SERVER_URL/register").openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = TIMEOUT_MILLIS
                connection.readTimeout = TIMEOUT_MILLIS
                connection.doOutput = true
                connection.outputStream.use { output ->
                    output.write(JSONObject().put("fid", fid).toString().toByteArray())
                }
                Log.d(TAG, "FID $fid: HTTP ${connection.responseCode}")
            } catch (error: IOException) {
                Log.w(TAG, "Không kết nối được server", error)
            } finally {
                connection.disconnect()
            }
        }
    }
}