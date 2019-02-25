package com.osano.privacymonitor.util

import com.google.common.net.InternetDomainName
import java.net.URI

fun String.rootDomain(): String? {
    return try {
        val uri = URI(this)
        return InternetDomainName.from(uri.host ?: this).topDomainUnderRegistrySuffix().toString()
    }
    catch (e: Throwable) {
        null
    }
}

