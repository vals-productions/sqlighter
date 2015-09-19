### Hello, sqlighter demo projects

These examples demonstrate how sqlighter could be of use to code SQLite related code in
java, and run it on iOS. 

[Demo.java] (https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) class' dbOperations() static method performs some database activity, retrieves
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

Please look into the next two sections in case you need to make any adjustments
before running projects.

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
So.

1. You have to be in this directory: ...sqlighter/demo
<pre>
> pwd
...sqlighter/demo
</pre>
2. create a link while in the directory above:
<pre>
...sqlighter/demo> ln -s [path to j2objc installation] j2objc
</pre>
3. Verify the link actually worked:
<pre>
... sqlighter/demo> ls -l
... README.md
... andr-demo-prj
... crt-j2objc-link.sh
... ios-demo-prj
... j2objc -> ../../m/j2objc-0.9.7 /* your unique path to be displayed to the left */
</pre>
Make sure the link is setup correctly:
<pre>
> cd j2objc
> ls
cycle_finder		j2objc			j2objc_protoc_plugin	lib
include			j2objc_protoc		j2objcc			man
</pre>

You should see include nad lib directories here, and they should actually contain include and lib files.

If suggestion above does not work
1) just go into Build Settings\Search Paths and update Header and Library Search path to your locations, then 
2) go to Build Phases\Link Binary With Libraries and replace entries there with entires in your locations.


## Android

Make sure Android IDE at your computer has Android SDK 5.0 (21) and corresponding build tools 
installed. It is not a requirement, it's just the way the project is configured, unless you
want to reconfigure the project to the SDK you have.
