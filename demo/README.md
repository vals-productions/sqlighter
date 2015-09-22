### Hello, sqlighter demo projects

These examples demonstrate how sqlighter could be of use to code SQLite related code in
java, and run it on iOS. These projects are setup by including actual sources from
sqligher library rather than libraries. They demonstrate this more complex approach.
Also, they are used as sort of test environment for sqlighter.

[Demo.java] (https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) 
class' dbOperations() static method performs some database activity, saves, retrieves
and returns "Hello sqlighter!" blob value (as String) from database's user table.

Demo class is converted into its Objective-C counterpart, as well as Bootstrap that
handles SQlite initialization and gives identical access to Database access 
interface\protocol in both platforms.

Java to Objective C conversion is done by shell scripts that are checked-in in iOS 
project.
```
sqlighter/demo/ios-demo-prj/ios-demo-prj/script> ./convert.sh
```
You have to go into sqlighter/demo/ios-demo-prj/ios-demo-prj/script directory
and run the script if you make changes to the Demo class in your Android project and
would like to propagate the changes to iOS.

There's one manual step you have to do in order for xCode project to work. It is dependent
on J2ObjC installation (*.h and libraries) and, since J2ObjC location at your computer 
is different from mine, the easiest way is to create a link to your J2ObjC installation
in a predefined place. This is explained below.

## iOS

First, look into crt-j2objc-link.sh for an example on how to create a link
to J2ObjC distribution into the demo directory. iOS project refers
this location for includes and lib paths. This is much easier than readjusting all
path related properties one by one to your actual J2ObjC location. 
So you are supposed to have 
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

You should see include and lib directories here, and they should actually contain
include and lib files.

If suggestion above does not work
1) just go into Build Settings\Search Paths and update Header and Library Search path
to your locations, then 
2) go to Build Phases\Link Binary With Libraries and replace entries there with entires
in your locations.

#### xCode project and github

Do not make any changes to the iOS project settings once cloned, please, otherwise you
might get weird errors.

This has to do something with how github stores iOS project settings 
(file encodings or so). There are issues reported at stackoverflow with no solution 
like this and others: http://stackoverflow.com/questions/22106307/cyclic-dependency-in-module-corefoundation-corefoundation-spritekit-cor

The way to bypass it - make a clean clone of the project, and build xcode project as is, 
with no any changes, like changing the target or anything else. Since this is just a 
demo project this should be sufficient.

Myself I have this issue if I use github, but never if I use my own non github git 
repositories.

My wild guess is - xcode does not recognize/handle properly its own project settings 
file and, instead of modifying existing preferences, it adds new ones, like instead of 
changing the target from iphone 6 to iphone 5s it adds both to the project file and 
this causes concurrency lockups and cyclic dependency issues.

## Android

Make sure Android IDE at your computer has Android SDK 5.0 (21) and corresponding build
tools installed. It is not a requirement, it's just the way the project is configured,
unless you want to reconfigure the project to the SDK you have.
