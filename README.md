[<img width="250" height="119" src="https://github.com/dock2dock/dock2dock-android/assets/20273969/f7ea7c93-59c1-45cb-875d-957e7d400c3f"/>](https://dock2dock.io)

# Dock2Dock Android SDK

The Dock2Dock Android SDK makes it quick and easy to integrate crossdocking in your Android app. We provide UI elements that can be used out-of-the-box to display and print crossdock labels.

Table of contents
=================

<!--ts-->
   * [Releases](#releases)
   * [Installation](#installation)
      * [Requirements](#requirements)
      * [Setup Dock2Dock](#setup-dock2dock)
   * [Getting Started](#getting-started)
      * [Configuration](#configuration)
      * [Features](#features)
<!--te-->

## Releases

## Installation

### Requirements

- Android 6.0 (API level 23) and above
* [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin) 7.4.2
* [Gradle](https://gradle.org/releases/) 7.5+

### Setup Dock2Dock

Add `dock2dock-android` to your `build.gradle` dependencies.

```
dependencies {
    // ...
    
    // Dock2Dock Android SDK
    implementation 'io.dock2dock:dock2dock-android:$latest'
}
```

## Getting Started

The examples shown below are using Kotlin.

### Configuration

```
import io.dock2dock.android.fragments.*
import io.dock2dock.android.configuration.*

class MainActivity : AppCompatActivity() 
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dock2DockConfiguration.init(this, "{API_KEY}")
    }
}
```

### Features

We provide, **CrossdockLabelsFragment** a prebuilt UI that combines all the tasks required to crossdock - displaying label details, print and delete unused labels - into a single fragment that can be displayed in your app.

```
private fun setupFragment() 
{
  fragAdapter = PagerAdapter(supportFragmentManager)
  val fragments = arrayListOf(
    FragmentModel(CrossdockLabelsFragment("{SALES_ORDER_NO}"), "Crossdock")
  )

  fragAdapter.fragments = fragments
  binding.viewPager.adapter = fragAdapter

  binding.tabs.setupWithViewPager(binding.viewPager)
}
```
![](https://github.com/dock2dock/dock2dock-android/assets/20273969/eaa526cd-3459-43c7-a2b6-641008089cac)



### Examples

Our Jetpack Compose implementation comes with its own example app, which you can play with.

- The [Example project](https://github.com/dock2dock/dock2dock-android/tree/master/example) demonstrates how to integrate and use our prebuilt ui.



