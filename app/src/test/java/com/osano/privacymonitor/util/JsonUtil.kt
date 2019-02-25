package com.osano.privacymonitor.util

import java.io.File

class JsonUtil {

    companion object {

        fun getJSON(path: String): String {
            // Load the JSON response
            val uri = this.javaClass.classLoader.getResource(path)
            val file = File(uri.path)
            return String(file.readBytes())
        }
    }
}