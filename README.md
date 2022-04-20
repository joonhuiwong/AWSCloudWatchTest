# AWS CloudWatch Sample Project

## *WARNING: This project WILL NOT WORK OUT OF THE BOX. YOU WILL HAVE TO DEFINE YOUR AWS CREDENTIALS*

This is a sample project which implements AWS CloudWatch for logging purposes.

I'll list down the components to take note below. Think of it like a checklist.

---

## Setup

### AndroidManifest.xml

```<uses-permission android:name="android.permission.INTERNET" />```

### build.gradle (App)

```implementation "com.amazonaws:aws-android-sdk-logs:2.44.0"```

---

## AWS Credential

You would require AWS Credentials that has appropriate access level for Cloudwatch (not exactly sure what permissions). Check with your AWS administrator.

You would need an ACCESS KEY and SECRET KEY for Basic Authentication (there are other types of Authentication but this example will only use BasicAWSCredentials).

Once you get your keys, you must decide how to put it in your application. For this sample, we will just be saving it as a gradle property in (gradle.properties).
It's arguably less secure but this is for ease of use and for different credentials just swap out the values in gradle.properties and recompile/rebuild the apk (think of it as unique settings bundled into the apk).

Refer to the following notes on how to use gradle.properties to have values you can use in your application.

### gradle.properties

Refer to the new *User Defined Properties* section. We add the properties in a simple key=value format.

### build.gradle (App)

Refer to the *buildConfigField* usage under defaultConfig. This is where we define and add the properties to BuildConfig object.

### Re/Build the application

After rebuilding, you should be able to access your properties as *BuildConfig.AWS_ACCESS_KEY* and *BuildConfig.AWS_SECRET_KEY*.

---

## Implementation in Code

You can probably see how it is implemented in my code. Here's the rough idea:

### CloudWatchLogger.java
- A separate class to handle the CloudWatch logging. Has only one basic function, log a specified message to the specified log group and log stream.

### MainApplication.java
- Holds a single persistent CloudWatchLogger object in the MainApplication so we can reuse it.
- In Application object so it is not randomly killed or paused like Activity objects.
- Made a log() method just to make the call A BIT shorter (still long)
  - ```MainApplication.getInstance().log("TestGroup", "TestStream", "Test Log"); ```
  - ```MainApplication.getInstance().getCloudWatchLogger().log("TestGroup", "TestStream", "Test Log");```

 ### MainActivity.java
- Just call from anywhere to log anything.

---

## Use Cases:
- Error Logging.
- API Logging (Request/Response in nice format, can add in local variables as well unlike API Gateway).
- Activity Logging.
- Debug Logging.
- Specific Logs. Like record every transaction from a terminal or every scan on a daily basis.

Basically you have to design the logs very specifically and manually (unlike integrated solutions like Bugsnag). How good it'll be for your project is also up to you.
