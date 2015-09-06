### Hello, sqlighter demo projects

These examples demonstrate how sqlighter could be of use to code SQLite related code in
java, and run it on iOS. 

Demo.java class' dbOperations() static method performs some database activity, retrieves
and returns "Hello sqlighter!" blob value (as String) from database's user table.

Demo class is converted into its Objective-C counterpart, as well as Bootstrap that
handles SQlite initialization and gives identical access to Database access interface\protocol
in both platforms.

Java to Objective C conversion is done by shell scripts that are checked in in iOS 
project. You have to go into sqlighter/demo/ios-demo-prj/ios-demo-prj/script directory
and run
```
sqlighter/demo/ios-demo-prj/ios-demo-prj/script> ./convert.sh
```
if you make changes to the Demo class in your Android project and would like to propagate
the changes to iOS.

Then both, Android and iOS projects display the greeting at the screen.

## iOS

First, look into to crt-j2objc-link.sh for an example on how to create a link
to j2objc distribution into the demo directory. iOS project refers
this location for includes and lib paths. This is much easier than readjusting all
path related properties one by one. So you are supposed to have 
```
sqlighter
  demo
    ios-demo-prj
    andr-demo-prj
    j2objc -> link to the actual j2objc installation dir.
      bin
      include
      lib
      ....
```

## Android

Make sure Android IDE at your computer has Android SDK 5.0 (21) and corresponding build tools 
installed. It is not a requirement, it's just the way the project is configured, unless you
want to reconfigure the project to the SDK you have.
