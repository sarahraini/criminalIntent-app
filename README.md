# CriminalIntent – Android Mobile Application

A companion app intended for a Private Investigating Superhero, to capture, record and document acts of petty crime.

# 📱 Overview
CriminalIntent is an Android application developed as part of the Secure Mobile App Development module at the University. 
The application is designed as a companion tool for a private investigator to record, manage, and document incidents of petty crime. It demonstrates the development of a multi-screen Android application using Java, Android Fragments, RecyclerView, SQLite, implicit intents, device contacts, phone functionality, and camera integration.
  
# 🎯 Project Objectives

The main objectives of this project were to:

* Develop a functional Android mobile application using Java.
* Build user interfaces using Android XML layouts.
* Understand Activities and Fragments.
* Manage multiple crime records.
* Implement RecyclerView for displaying lists.
* Store and retrieve application data using SQLite.
* Implement CRUD operations for crime records.
* Use implicit intents to interact with other Android applications.
* Access device contacts.
* Implement phone calling functionality.
* Capture and store photographs using the device camera.
* Handle different screen orientations, including landscape layouts.
* Implement swipe-based navigation between crime records.
 
 
# 🛠️ Technologies & Tools

* Java
* Android Studio
* Android SDK
* XML
* SQLite
* RecyclerView
* Fragments
* Activities
* ViewPager
* Implicit Intents
* FileProvider
* Android Camera
* Android Contacts
* Minimum API Level: 27 Package Name "uk.ac.wlv.criminalintent"
 
 
# ✨ Features

Crime Management

* Create and manage crime records.
* Add crime titles and dates.
* Mark crimes as solved or unsolved.
* Assign suspects to crimes.
* Delete crime records.
* Persist crime information using SQLite.

Crime List

* Display multiple crime records using RecyclerView.
* Display:
    * Crime title
    * Crime date
    * Solved status
* Select an individual crime to view and edit its details.
* Automatically refresh the crime list when records are updated.

Crime Details

Each crime can contain:

* Crime title
* Crime date
* Solved status
* Suspect
* Crime report
* Crime photograph

Date Picker

A date picker dialog allows users to select and modify the date associated with a crime.
Crime Reports
The application generates a formatted crime report containing relevant crime information.
Users can share the report through other messaging applications using Android’s implicit intent system.

Contact Integration

Users can select a suspect from the device’s contacts.
The selected contact’s name is then associated with the crime record.
Phone Calling
The application includes functionality for initiating a reporting phone call using Android’s telephone intent.

Camera Integration

Users can:

* Launch the device camera.
* Capture photographs related to a crime.
* Store photographs using FileProvider.
* Display a thumbnail of the captured photograph.
* View the full-size crime photograph.

Swipe Navigation

Users can swipe between different crime records using a ViewPager, allowing them to navigate directly between crimes without returning to the crime list.

Orientation Support

The application includes a separate landscape layout to provide an improved user interface when the device is rotated.

 
# 🏗️ Application Architecture

The project follows a basic separation between the application’s model, controller, and database components.

Model Layer

The model layer manages crime information.

* Crime
* CrimeLab

Controller Layer

The controller layer manages the application’s Activities and Fragments.

* CrimeActivity
* CrimeFragment
* CrimeListActivity
* CrimeListFragment
* CrimePagerActivity
* DatePickerFragment

Database Layer

The database package manages persistent SQLite storage.

* CrimeDbSchema
* CrimeBaseHelper
* CrimeCursorWrapper
 

 
# 💾 Database

SQLite is used to persist crime records locally on the Android device.

The database stores information such as:

* Crime ID
* Crime title
* Crime date
* Solved status
* Suspect

The application uses SQLiteOpenHelper to create and manage the database.

Database operations include:

* Creating crime records
* Reading crime records
* Updating crime records
* Deleting crime records
 

 
# 🔗 Android Intents

The application demonstrates the use of implicit intents to communicate with other Android applications and system components.

Examples include:

* Sharing crime reports through messaging applications.
* Selecting a suspect from Contacts.
* Initiating a telephone call.
* Launching the device camera.
 

 
# 📸 Camera & FileProvider

The camera functionality uses Android’s ACTION_IMAGE_CAPTURE intent.

A FileProvider is used to securely provide the camera application with access to the location where the photograph should be stored.

The application also generates a unique image filename based on the crime ID.
 
