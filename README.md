# sqlighter

SQLIte implementation that works with J2ObjC on both - Android and iOS platforms

Note: I've just added library distribution to the project, and, as part of that 
change I had to relocate the content of sqlighter/ios/impl/ into
sqlighter/ios/j2objc/com/vals/a2ios/sqlighter/impl/ , so if you referenced these
files directly, please update your references. Or, better yet, switch to libraries
instead of sources.

# Overview

This implementation is based on standard Android's SQlite implementation that is being
used on Android devices, and matching implementation on iOS device.

This library does not attempt to replicate Android's implementation completely. The 
goal is to provide ability to execute pretty much any SQL statements at either of 
the platforms with single and simple interface without dependencies on existing
platform specific implementations.

It is up to you how to setup J2ObjC conversions for business logics conversions of your
project. You do it in whichever way works for you. There is no dependency here. Most
likely you already have something setup by now. You just have to include provided classes
and interfaces into your Android project on one side, and iOS / Objective C modules/protocols 
at the other side, or, (better) use provided jar file for Android and lib for iOS.

Then see guidelines and usage examples provided in the documentation.

You can also check Android and iOS demo projects that are part of this repository.

```
   Android                            iOS
   
              Interface Definition
              
  /*provided*/      j2objc     /*generated \ provided*/
SQLighterDb.java ----------> SQLighterDb.h, SQLighterDb.m
  /*provided*/      j2objc     /*generated \ provided*/
SQLighterRs.java ----------> SQLighterRs.h, SQLighterRs.m

               Implementation
  /*provided*/                 /*provided*/ 
SQLighterDbImpl.java         SQLighterDbImpl.h
                             SQLighterDbImpl.m
                               /*provided*/ 
                             SQLighterRsImpl.h
                             SQLighterRsImpl.m
```
Both implementations conform to SQLighterDb (core database methods) and
SQLighterRs (ResultSet processing) interfaces. Android implementation for these is
SQLighterDbImpl.java that is included. iOS implementation is a set of ios/impl *Impl.h 
and *Impl.m files (see the diagram above). They implement, in essence, same interfaces, 
that are result or SQLighterDb.java and SQLighterRs.java J2ObjC conversion into 
corresponding Objective C classes (actually, protocols).

# J2ObjC

You do not need to use J2ObjC tools to use this library as part of your project,
because it includes classes\modules that are already j2objc'd.

So you can save your time on conversion setup and skip to the Project configuration.

But just in case you decide to do so... SQLighterDb.java and SQLighterRs.java are 
to be converted into Objective C to become SQLighterDb.h, SQLighterDb.m 
and SQLighterRs.h, SQLighterRs.m.

Conversion should be done with the use of --prefixes <file with prefix configs> j2objc 
switch to prevent adding java package prefix to class names. Sample file is below.
```
<file with prefix configs>
...
com.vals.a2ios.sqlighter=
...
```
This makes code look cleaner in case you'd like to use sqlighter for some iOS 
functionality that is not matching your Android counterpart. 

## Project configuration

### Using provided libraries

Check out the contend of sqlighter/distr directory. It contains versioned libraries. 

For your Android project just include the sqlighter.jar file into your project. Consult
with Android Studio how to do it.

For your xCode project, add sqlighter/distr/<VERSION>/ios/libsqlighter-all-lib.a into 
your project. Consult xCode docs on how to do it. Add path to 
sqlighter/distr/<VERSION>/ios directory to your project's Header Search path.

This should make it.

### Using source files

It is actually pretty simple (but using libs is easier) - include java files to Android, 
include *.h and *.m file to your xCode, make sure everything compiles. If something does
not work, check doc for more detailed explainations.

#### Sqlighter at Android

Include content of sqlighter/android in your Android project. I.e. 
you should have these packages/files in your sources:
```
com.vals.a2ios.sqlighter.impl.SQLighterDbImpl.java
com.vals.a2ios.sqlighter.intf.SQLighterDb.java
com.vals.a2ios.sqlighter.intf.SQLighterRs.java
```
You can manually copy them into your project, or, edit gradle.build and point to the
location of files, 
```
android {
....
    sourceSets {
        main.java.srcDirs += 'src/main/../../../../../android/'
    }
}
```
or whatever other method of including sources in the project
you know.

#### Sqlighter at iOS

In xcode add libsqlite3.dylib to your project libraries.

Include contents of sqlighter/ios/j2objc/com/vals/a2ios/sqlighter/impl/ directory - 
SQLighterDbImpl.m and SQLighterRsImpl.m files into your iOS project. Right click at the
project's source files folder, pick "Add files to "prj name"", locate
the place where you cloned sqlighter repository, and add those files.

Add path to the sqlighter/ios/j2objc to your project's include's search path. I.e. you
should be able to use the following in your code, because these files are under 
sqlighter/ios/j2objc and xCode has sqlighter/ios/j2objc in include search path:
```
#import "com/vals/a2ios/sqlighter/intf/SQLighterDb.h"
#import "com/vals/a2ios/sqlighter/intf/SQLighterRs.h"
```
J2ObjC toolkit also has to be part of the project since some specific classes from the
toolkit are referenced from sqlighter. You include this according to J2ObjC guidelines. If
you need sqlighter project, I assume, you've already familiarized yourself with this
routine.

Make sure your project compiles.

#### Database file

In either way, both Android and iOS projects should contain your initial SQLite database
file. It should be part of your project. It can have some predefined DB schema or you
can create tables on the fly - it's all in developer's control.

On Android's project the location of the file is typically 'assets' directory.

On iOS you have to right click on the project, pick "Add files to...", locate your file
on the file system and add it to the project this way.

SQLighterDb.setDbName(String name) specifies file name. 

SQLighterDb.setDbPath(String path) specifies path to the file on the device. This is
different between Android and iOS. For android it is "/data/data/<<YOUR PROJECT path>>/databases/",
for iOS this method is missing as the library knows the location relative
to project's location.

##### SQLighterDb.copyDbOnce()

Your initial sqlite database file is stored somewhere within your project structure.
This file could contain no tables/information whatsoever, or, contain some
initial database structure/data of your project. This is all in developer's hands.
Let's call this file initial database file.

The location of the initial database file in the project and on the device (emulator or
real device) should be different. Among other things this will prevent database from being
overwritten during sequential application upgrades by the content of the initial
database file.

So, there's a task of copying the file from the project into designated device's
location. This task should be done once since otherwise you'll keep overwriting
user data in the real application.

Whenever the user starts your application the very first time, the database file
should be copied from its project location into designated device's location.

This, also, should be done before you start using the database. Basically #copyDbOnce
takes the complexity of various checks out of your hands.

copyDbOnce works only once per SQLighterDb instance per application startup. This is 
just to prevent erroneous database overrides during application runs.

Once invoked, first, copyDbOnce checks the destination location of the database file.
If it's already there, it will skip copying the database file.

If the file is not there, the copyDbOnce will copy the file.

There's also SQLighterDb.setOverwriteDb method, that lets you override default behavior
SQLighterDb.copyDbOnce. If called with true, it will let copyDbOnce override the 
destination database file even if the file is there. Normally, this is necessary for your
development process where you would like to roll database back and start fresh until you
develop and test some particular. Normally, you do not want to call #setOverwriteDb with
"true" in your production environment unless you want the user to start fresh every time.

#### Instantiation example

Here I will show you how to inject platform specific implementations on application/activity
initialization and initialize the database file.

One of the ways this might be done is by using singleton pattern. Code below is for 
Android, which could/should be converted to iOS with j2objc.
``` java
public class Bootstrap {
    private static Bootstrap instance;
    private SQLighterDb db;
    public SQLighterDb getDb() { return db;}
    public void setDb(SQLighterDb db) { this.db = db;}
    private Bootstrap() {
    }
    public static Bootstrap getInstance() {
...
}
```
Then your activity's onCreate method might look like this:
``` java
protected void onCreate(Bundle savedInstanceState) {
  SQLighterDbImpl db = new SQLighterDbImpl();
  // platform specific initialization
  db.setDbPath("/data/data/<<YOUR PROJECT path>>/databases/");
  db.setDbName("sqlite.sqlite");
  db.setOverwriteDb(false); // will not replace device's DB file already exists
  Bootstrap.getInstance().setDb(db);
  // important at Android as database open method is called from the Context
  db.setContext(this);
  try {
    /* 
     Will copy DB file from your project files into device's DB location
     if it's not there yet. If the file is already on the device, will proceed
     according to db.setOverwriteDb(boolean) method.
     */
    db.copyDbOnce(); 
    db.openIfClosed();
  } catch (Exception e) {
  ...
  }
...
```
And your iOS' app delegate initialization method may look like this. Note that 
Bootstrap is just a j2objc clone of Android's Bootstrap class.
``` objc
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    Bootstrap *b = [Bootstrap getInstance];
    SQLighterDbImpl *db = [[SQLighterDbImpl alloc] init];
    // platform specific initialization
    [db setDbNameWithNSString: @"sqlite.sqlite"];
    [db setOverwriteDbWithBoolean: false];
    [db copyDbOnce];
    [db openIfClosed];
    [b setDbWithSQLighterDb:db];
    return YES;
}
```
One important feature of this implementation is - you get uniform and platform 
independent way to get access to your DB implementation interface in your shared 
between platforms code as this
``` java
SQLighterDb db = Bootstrap.getInstance().getDb();
```
gets translated into this
``` objc
id<SQLighterDb> db = [[Bootstrap getInstance] getDb];
```
Once that is done you can use SQLite in the followig manner.

# Usage

Usage is very straightforward.

Positional parameters are supported. No naming parameter support.

a) Use addParam* methods to bind positional parameters to your statements. Parameter 
position is determined by the order in which it was added. Parameters are optional 
as some statements may be parameterless.

b) Execute the statement by calling SQLighterDb.executeSelect(String sqlString) 
(for Select...) or SQLighterDb.executeChange(String sqlString) 
(for INSERT/UPDATE/CREATE/DELETE/...). executeSelect returns the SQLighterRs result 
set interface that lets you iterate through returned records and retrieve row columns by 
executing corresponding getters. The SQLighterRs should be closed once you are done with 
the result set. executeChange closes underlying statements implicitly.

ResultSet has getters to retrieve positional select clause parameters like this:
``` java
SQLighterRs rs = db.executeSelect("select id, email, name, data, height from user");
while (rs.hasNext()) {
        Long pk = rs.getLong(0);
        String e = rs.getString(1);
        byte[] d1 = rs.getBlob(3);
        ....
}
rs.close();
```
c) once your statement is executed, bound parameters are cleaned up, so you can use
 addParam* methods again to be bound/used with your next statement.

Please see the next section that has some pretty straightforward examples.

# Going by example

### Pre requisites

Let's create some sqlite file using sqlite command line or one of the existing UI tools
and create a table user in it.

``` sql
CREATE TABLE "user" (
	`name`	TEXT,
	`email`	TEXT,
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`data`	BLOB,
	`height`	REAL
);
```
Let's insert the following initial records:
``` sql
INSERT INTO user(name, email, data, height) values ('user 1', 'user1@email.com', null, 1.4);
INSERT INTO user(name, email, data, height) values ('user 2', 'user2@email.com', null, null);
INSERT INTO user(name, email, data, height) values ('user 3', 'user3@email.com', null, 4.89);
INSERT INTO user(name, email, data, height) values ('user 4', null, null, null);
```

### Android code

First let's just output what is initially in the table by executing SQL select statement
with no parameters: 

``` java
SQLighterDb db = Bootstrap.getInstance().getDb();
SQLighterRs rs = db.executeSelect("select id, email, name, data, height from user");
System.out.println("initial state ");
while (rs.hasNext()) {
  print(rs);
}
rs.close();
```
And this should result in:
```
initial state 
pk: 1, email: user1@email.com, name: user 1, blob data: , height: 1.4
pk: 2, email: user2@email.com, name: user 2, blob data: , height: null
pk: 3, email: user3@email.com, name: user 3, blob data: , height: 4.89
pk: 4, email: null, name: user 4, blob data: , height: null
```

print function looks just like this and is used in all examples: 
``` java
private void print(SQLighterRs rs) {
	Number pk = rs.getLong(0);
    String e = rs.getString(1);
    String n = rs.getString(2);
    byte[] dataBytes = rs.getBlob(3);
    String dataString = null;
    if (dataBytes != null) {
    	dataString = new String(dataBytes);
    }
    Number h = rs.getDouble(4);
    System.out.println("pk: " + pk + ", email: " + e + ", name: " + n + 
    						", blob data: " + dataString + ", height: " + h );
}
```
Then, add another record with some blob value:
``` java
String dataStr = "This is blob string example";
byte[] data = dataStr.getBytes();
db.addParam("user name 5"); // bind too the first insert value (name)
db.addParam("qw@er.ty1"); // bind to the second one (email)
db.addParam(data); // bind to the data column
db.addParam(5.67); // bind to the height column
db.executeChange("insert into user( name, email, data, height) values (?, ?, ?, ?)");
```
And let's also requery what we just inserted
``` java
db.addParam("qw@er.ty1"); // bind to the where email filter condition
System.out.println("check if the record was inserted");
rs = db.executeSelect("select id, email, name, data, height from user where email = ?");
while (rs.hasNext()) {
  print(rs);
}
rs.close();
```
Which should result in:
```
check if the record was inserted
pk: 5, email: qw@er.ty1, name: user name 5, blob data: This is blob string example, height: 5.67
```
Then, let's do some update
``` java
db.addParam("user@email.com"); // bind to the set email = ? 
db.addParam("qw@er.ty1"); // bind to where email = ?
db.executeChange("update user set email = ? where email is null or email = ?");
```
... and verify the output with above select all code
```
after update state
pk: 1, email: user1@email.com, name: user 1, blob data: , height: 1.4
pk: 2, email: user2@email.com, name: user 2, blob data: , height: null
pk: 3, email: user3@email.com, name: user 3, blob data: , height: 4.89
pk: 4, email: user@email.com, name: user 4, blob data: , height: null
pk: 5, email: user@email.com, name: user name 5, blob data: This is blob string example, height: 5.67
```
Let's delete something
``` java
db.addParam("user@email.com");
db.executeChange("delete from user where email = ?");
```
and see what we get:
```
after delete state
pk: 1, email: user1@email.com, name: user 1, blob data: , height: 1.4
pk: 2, email: user2@email.com, name: user 2, blob data: , height: null
pk: 3, email: user3@email.com, name: user 3, blob data: , height: 4.89
```
And finally, let's create another table, populate with data and run the query with join
``` java
db.executeChange("create table address(id integer primary key autoincrement unique, name text, user_id integer)");
db.addParam("123 main str, walnut creek, ca");
db.addParam(1);
db.executeChange("insert into address(name, user_id) values(?, ?)");

System.out.println("after address creation/population");
rs = db.executeSelect("select a.user_id, u.email, u.name, u.data, u.height, a.name from user u, address a "
                      + "where a.user_id = u.id");
while (rs.hasNext()) {
  print(rs);
  System.out.println(" address: " + rs.getString(5));
}
rs.close();
```
the output:
```
after address creation/population
pk: 1, email: user1@email.com, name: user 1, blob data: , height: 1.4
 address: 123 main str, walnut creek, ca
```

And, in case you'd like to bind some NULL parameter, it can be done this way:
``` java
db.addParamNull(); // bind first param to update as null
db.addParam("qw@er.ty1"); // bind second param as where filter condition
db.executeChange("update user set email = ? where email = ?");
```
will result in:
``` sql
update user set email = null where email = 'qw@er.ty1';
```

The above code gives identical output after being converted into iOS using j2objc.
Therefore, you can implement your database related logics in java language amd just
convert/reuse it in iOS.

### iOS code

Normally you shouldn't need to do SQLite related coding in your iOS implementation, 'cause
the whole goal of this library is to code in java and convert to Objective C.

But if for whatever reason you have to code some SQLITE in iOS without j2objc, it doesn't
look bad either:
``` objc
id<SQLighterDb> db = [[Bootstrap getInstance] getDb];
[db addParamWithNSString: @"user@email.com"];
id<SQLighterRs> rs = [db executeWithNSString: @"select name, email from user where email = ?"];
while([rs hasNext]) {
	NSLog(@"email:  %@, name: %@", [rs getStringWithInt:1], [rs getStringWithInt:0]);
}
[rs close];

```
