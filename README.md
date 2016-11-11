
StreamHub-Android-SDK
=====================

Make Android apps powered by Livefyre StreamHub

Read the docs: http://livefyre.github.com/StreamHub-Android-SDK/

Install Android Studio [Here](https://developer.android.com/studio/index.html?gclid=Cj0KEQiA9ZXBBRC29cPdu7yuvrQBEiQAhyQZ9FALExzWUoc2Zj-G4AG3HzhEha0pg6B274OR9jAOalsaApnX8P8HAQ).

Get the fresh StreamHub-Android-SDK from Github.

**Steps to run streamHub-android-sdk and sample apps**

1. Open Android Studio. 

2. Select 'Open an Android Studio project'.

3. Browse and select StreamHub-Android-SDK -> select Ok.

**Steps to use streamHub-android-sdk in your Android Studio project:**

1.  Start with an existing project or create a new one.

2.	Select File -> New -> Import Module.

3.  Select the browse option(...) next to the 'Source Directory'. 

4.  Select streamHub-android-sdk in StreamHub-Android-SDK (.../StreamHub-Android-SDK/streamHub-android-sdk).

5.  Add the dependency by going to project folder -> "app"(Your Module Name) folder -> "build.gradle" file under dependencies as follows:

```
dependencies {
  compile project(':streamhub-android-sdk')
}
```
6. Make sure that the following line is in your project folder -> settings.gradle file

```
include ':streamhub-android-sdk'
```

Note: You can customize configurations from within   [LivefyreConfig.java](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/streamhub-android-sdk/src/main/java/com/livefyre/streamhub_android_sdk/util/LivefyreConfig.java).

# Sample App

* Reviews Demonstrative Example: https://github.com/Livefyre/StreamHub-Android-SDK/tree/master/streamhub-reviews

* Comments Demonstrative Example: https://github.com/Livefyre/StreamHub-Android-SDK/tree/master/streamhub-comments

# SDK Client Classes

The StreamHub Android SDK exposes several Client classes that can be used to request StreamHub APIs.

* [`AdminClient`](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/streamhub-android-sdk/src/main/java/com/livefyre/streamhub_android_sdk/network/AdminClient.java) - Exchange a user authentication token for user information, keys, and other metadata

* [`BootstrapClient`](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/streamhub-android-sdk/src/main/java/com/livefyre/streamhub_android_sdk/network/BootstrapClient.java) - Get recent Content and metadata about a particular Collection

* [`StreamClient`](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/streamhub-android-sdk/src/main/java/com/livefyre/streamhub_android_sdk/network/StreamClient.java) - Poll a stream for a collection to retrieve new, updated, and deleted content

* [`WriteClient`](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/streamhub-android-sdk/src/main/java/com/livefyre/streamhub_android_sdk/network/WriteClient.java) - Post content, flag content, like content in a collection

# License

Copyright (c) 2015 Livefyre, Inc.

Licensed under the MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
