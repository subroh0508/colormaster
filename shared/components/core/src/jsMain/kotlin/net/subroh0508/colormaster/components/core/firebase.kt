package net.subroh0508.colormaster.components.core

import kotlinext.js.jsObject
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

fun initializeApp(options: FirebaseOptions) {
    val config: firebase.Options = jsObject {
        apiKey = options.apiKey
        authDomain = options.authDomain
        databaseURL = options.databaseURL
        projectId = options.projectId
        storageBucket = options.storageBucket
        messagingSenderId = options.messagingSenderId
        appId = options.appId
        measurementId = options.measurementId
    }

    firebase.initializeApp(config)
}
