# AHAndroid
Android app extension for the home automation repo https://github.com/ehmurray8/AutoHome.

Android app that utilizes the AutoHome infrastructure to allow the user to use the Android app to control the home. The app sends messages on the same ably channels that AutoHome uses, and thus a Raspberry Pi running the AutoHome nodejs script can handle these messages. The app also requires an Ably key to be able to post messages.
