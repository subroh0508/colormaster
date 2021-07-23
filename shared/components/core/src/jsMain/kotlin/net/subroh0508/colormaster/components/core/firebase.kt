package net.subroh0508.colormaster.components.core

import kotlinext.js.jsObject
import net.subroh0508.colormaster.api.jsfirebaseauth.firebase

fun initializeApp() {
    val config: firebase.Options = jsObject {
        apiKey = "AIzaSyAa9XK8rRymTOsfNpb2Fejn6aAo7A0c13o"
        authDomain = "imas-colormaster.firebaseapp.com"
        databaseURL = "https://imas-colormaster.firebaseio.com"
        projectId = "imas-colormaster"
        storageBucket = "imas-colormaster.appspot.com"
        messagingSenderId = "1084041594481"
        appId = "1:1084041594481:web:d21eb204c9ebae39a1bdd0"
        measurementId = "G-XV1LEWMH6K"
    }

    firebase.initializeApp(config)
}
