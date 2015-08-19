
StreamHub-Android-SDK
=====================

Make Android apps powered by Livefyre StreamHub

Read the docs: http://livefyre.github.com/StreamHub-Android-SDK/

This StreamHub-Android-SDK can be used for both Eclipse and Android Studio

**Steps to use in Eclipse:**

Get the fresh StreamHub-Android-SDK from Github

1.  Start with an existing project or create a new one

1. 	To import StreamHub-Android-SDK into your workspace go to: File > Import Project > General > Existing Project into Workspace
2. 	Browse and select StreamHub-Android-SDK; it should now show in the package explorer

3.	Right click on your project and select properties then select Android tab

4.	Under the Library section, select Add button then select StreamHub-Android-SDK from the list of libraries

5.  Click on Apply and OK.

____________

**Steps to use in Android Studio:**

Get the fresh StreamHub-Android-SDK from Github

1.  Start with an existing project or create a new one

1.	Right click on your project and select 'Open Module Settings'

2.	Select the **'+'** button on the top left corner of window

3.	Select "Import Existing Project"  (In new version of Android studio you can find "Import Existing Project" option under "More Modules")

4.  Browse and select StreamHub-Android-SDK
 * Android Studio may request to convert the SDK to gradle version; if this occurs, select next and finish

5.  Add the following dependency by going to project folder -> "app" folder -> "build.gradle" file under dependencies as follows:

```
dependencies {
  compile project(':streamHubAndroidSDK')
}
```
**Make sure that the following line is in your project folder -> settings.gradle file**

```
include ':streamHubAndroidSDK'
```

Note: You can customize configurations from within   [Config.java](https://github.com/Livefyre/StreamHub-Android-SDK/blob/master/src/livefyre/streamhub/Config.java).

# Sample App

* Reviews Demonstrative Example: https://github.com/Livefyre/StreamHub-Android-Reviews-App

* Comments Demonstrative Example: https://github.com/Livefyre/StreamHub-Android-Comments-App

# SDK Client Classes

The StreamHub Android SDK exposes several Client classes that can be used to request StreamHub APIs.

* [`AdminClient`](http://livefyre.github.com/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/AdminClient.html) - Exchange a user authentication token for user information, keys, and other metadata

* [`BootstrapClient`](http://livefyre.github.com/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/BootstrapClient.html) - Get recent Content and metadata about a particular Collection

* [`StreamClient`](http://livefyre.github.io/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/StreamClient.html) - Poll a stream for a collection to retrieve new, updated, and deleted content

* [`WriteClient`](http://livefyre.github.io/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/WriteClient.html) - Post content, flag content, like content in a collection

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
