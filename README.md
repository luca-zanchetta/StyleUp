# StyleUp

**Presented by:**
- Luca Zanchetta, 1848878
- Camilla Iorio, 1852512

## Index
- [Introduction](#introduction)
- [The application](#the-application)
  - [Shirt Try On](#shirt-try-on)
  - [Social Interactions](#social-interactions)
  - [Maps Location](#maps-location)
- [Prerequisites](#prerequisites)
- [Installation and Launch](#installation-and-launch)

## Introduction

Welcome to **StyleUP**! This is a mobile application implementing a simple social network; here, you'll find the possibility to connect with your friends, by posting pictures of you while trying on shirts with Augmented Reality (AR) techniques. You just need an Android smartphone, an account and an Internet connection!

The architecture of the application is composed of:
- A **frontend node**, that is a mobile application coded in Kotlin;
- A **backend node**, that is a RESTful web service developed in python, through the Flask framework;
- A **database node**, that is a simple MySQL DBMS.

The backend node and the database node run on the [PythonAnywhere](https://www.pythonanywhere.com/) web hosting platform; this allows the user to run only the frontend node from its Android smartphone. See [Installation and Launch](#installation-and-launch) section for further details.

## The application

**StyleUp** is a mobile application implementing a simple social network. Once you have installed and opened the application (see [Installation and Launch](#installation-and-launch) and [Prerequisites](#prerequisites) sections for further details), you have to create an account in order to have access to all the provided features. Once you have created an account, you'll have to login into the application. 

Once the login is performed, you'll be presented the homepage of the application: this screen contains all the shirts that are available in the system for being tried on. On the top part of the screen, there is a notification icon, through which the user will be able to see its notifications (for instance, a notification will appear upon the receival of a friendship request or a like on a post). On the bottom part of the screen, there is a navigation bar, through which the user will be able to navigate throughout the application. The second icon of this navigation bar is the default one; i.e., selecting this icon will redirect the user to the homepage of the application.

The first icon of the navigation bar is the users icon, and upon the click of such an icon, the user will be redirected to a screen showing all the users that are currently signed up to the system. The user will be able also to search for a specific user, given its username. By clicking on one username among all those shown, the user will be able to visit the profile of the selected user.

The thirt icon allows the user to visit his/her own profile; here, the user will be able to see and delete his/her own posts. Moreover, by selecting the settings icon in this screen, the user will be able to logout, to modify his/her own account and/or to delete his/her own account.

The fourth icon allows the user to see its current location, based on a Google Maps API. 

### Shirt Try On

The **Shirt Try On** feature is the most important feature of our application. It is based on a pre-trained [MoveNet](https://www.tensorflow.org/hub/tutorials/movenet) network, which is able to perform pose-estimation in real-time. Basically, when the user selects a shirt in the homepage, the application will open the camera, asking this pre-trained model to perform pose-estimation in real-time using the camera stream. The pre-trained model is able to return a list of Points of Interest (POI) of the detected human body; therefore, the image of the shirt is overlayed on specific points of interest, thus creating a rect bounded by the detected shoulders and the detected hips. You're encouraged to see the code available in the *CameraFragment.kt* file for further details.

### Social Interactions

The **Social Interaction** feature is the core of the application; without this feature, the whole application wouldn't make any sense to exist. While the user tries on a selected shirt, (s)he is able to take a picture of her/himself: this picture can be either posted or not. If the user decides to post a picture, then we are in the scope of a social network. Each post can be liked, and the user can see how many likes each of his/her posts received. 

Obviously, for receiving likes, the user must have friends. Therefore, when the user visits the profile of another user, (s)he could decide to send to the other user a friendship request; the other user can decide either to accept or refuse such a friendship request. Only friends can see and like each other's posts.

### Maps Location

The **Map** feature allows the user to see its current location, based on a [Google Map API](https://developers.google.com/maps/documentation/android-sdk). This is useful, because in a real use-case scenario, the user would be able to try on shirts before purchasing them, directly from home; after that, by the means of the map feature, the user would be able to find the shop that has provided the shirt the user has tried on.

## Prerequisites

In order to correctly launch the application, the user must have:
- An Android smartphone with Android version 7.0 or higher;
- An Internet connection;
- At least 100MB of free space.

## Installation and Launch

The application can be downloaded by clicking this [link](https://github.com/luca-zanchetta/StyleUp/blob/main/styleup.apk). Once you have downloaded and installed the application, ensure you're in the following setting **before executing it**:
- Ensure that your smartphone is in **light mode**;
- Ensure to have your current location turned on;
- Ensure you have an active Internet connection.

Enjoy the application! :)
