# CapstoneProject 

The Capstone project provides the opportunity for me to take the skills that I've learned across my Android Nanodegree journey and apply it to an app idea of my own.

I have decide to create a stock count app which helps user to keep record of their stock taking on a
mobile phone.

# StoCount

[![ScreenShot1](https://raw.githubusercontent.com/theeheng/CapstoneProject/master/demo1.gif)](https://www.youtube.com/watch?v=CwHSMMj_XHY)
[![ScreenShot2](https://raw.githubusercontent.com/theeheng/CapstoneProject/master/demo2.gif)](https://www.youtube.com/watch?v=CwHSMMj_XHY)
[![ScreenShot3](https://raw.githubusercontent.com/theeheng/CapstoneProject/master/demo3.gif)](https://www.youtube.com/watch?v=CwHSMMj_XHY)

StoCount is perfect for independent shopkeeper, small restaurant or home user which interested
to keep track of store inventory electronically on a portable device such as mobile phone ,
tablet , as well as android wear device.

[![ScreenShot4](https://raw.githubusercontent.com/theeheng/CapstoneProject/master/demo4.gif)](https://www.youtube.com/watch?v=PMgvn8dESqk)

Features
---------

- Keep a list of product use for stock taking
- Allow to do stock count with barcode scanning
- Able to do stock count offline
- Connected to Tesco API, Walmart API, Amazon API, provide wide range of household product information (include barcode info)
- Integrated with Google+ login

In this project I will demonstrate the ability to create and app that meet the following requirement :

Core Platform Development
--------------------------

- App integrates a library
- App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash.
- App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts
- App provides a widget to provide relevant information to the user on the home screen.

	
Google Play Services
---------------------

- App integrates two or more Google services
- Each service imported in the build.gradle is used in the app.
- If Location is used, the app customizes the user’s experience by using their location.
- If Admob is used, the app displays test ads. If admob was not used, student meets specifications.
- If Analytics is used, the app creates only one analytics instance. If analytics was not used, student meets specifications.
- If Maps is used, the map provides relevant information to the user. If maps was not used, student meets specifications.
- If Identity is used, the user’s identity influences some portion of the app. If identity was not used, student meets specifications.

	
Material Design
----------------

- App theme extends AppCompat.
- App uses an app bar and associated toolbars.
- App uses standard and simple transitions between activities.

	
Building
---------

- App builds from a clean repository checkout with no additional configuration.
- App builds and deploys using the installRelease Gradle task.
- App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
- All app dependencies are managed by Gradle.

	
Data Persistence
-----------------

- App implements a ContentProvider to access locally stored data.
- Must implement at least one of the three
- If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter.
OR
- If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so.
OR
- It it performs short duration, on-demand requests(such as search), app uses an AsyncTask.
- App uses a Loader to move its data to its views.

