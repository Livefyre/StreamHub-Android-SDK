StreamHub-Android-SDK
=====================

Make Android apps powered by Livefyre StreamHub

Read the docs: http://livefyre.github.com/StreamHub-Android-SDK/

# Usage

Clone the repo and drop the StreamHub-Android-SDK.jar file into your project's libs folder

Or, close the repo and add the streamhub_android_sdk as an Android dependency

## Packages

At the time of writing, the StreamHub Android SDK exposes classes for requesting and sending to the StreamHub APIs. It make no assumptions about how to view the content.

### Clients

The StreamHub Android SDK exposes several Client classes that can be used to request StreamHub APIs.

* [`AdminClient`](http://livefyre.github.com/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/AdminClient.html) - Exchange a user authentication token for user information, keys, and other metadata

* [`BootstrapClient`](http://livefyre.github.com/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/BootstrapClient.html) - Get recent Content and metadata about a particular Collection

* [`PublicAPIClient`](http://livefyre.github.com/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/PublicAPIClient.html) - Request the Hottest Collections in a Network or get recent Content from a specific user

* [`StreamClient`](http://livefyre.github.io/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/StreamClient.html) - Poll a stream for a collection to retrieve new, updated, and deleted content

* [`WriteClient`](http://livefyre.github.io/StreamHub-Android-SDK/com/livefyre/streamhub_android_sdk/WriteClient.html) - Post content, flag content, like content in a collection

# Deving

Clone the project, run the tests, and notice a few undocumented classes. Kindly treat the project as alpha code.

# License

Copyright 2013 Livefyre Inc.

Licensed under the MIT License
