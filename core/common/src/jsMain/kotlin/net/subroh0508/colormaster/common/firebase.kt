package net.subroh0508.colormaster.common

import kotlin.js.json

fun initializeApp(
    apiKey: String,
    authDomain: String,
    databaseUrl: String,
    projectId: String,
    storageBucket: String,
    messagingSenderId: String,
    appId: String,
    measurementId: String,
) {
    dev.gitlive.firebase.externals.initializeApp(json(
        "apiKey" to apiKey,
        "authDomain" to authDomain,
        "databaseURL" to databaseUrl,
        "projectId" to projectId,
        "storageBucket" to storageBucket,
        "messagingSenderId" to messagingSenderId,
        "appId" to appId,
        "measurementId" to measurementId,
    ))
}
