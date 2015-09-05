# sqlighter

SQLIte implementation that works with j2objc on both - Android and iOS platforms

# Overview

This implementation is based on standard Android's SQlite implementation that is being
used on Android devices, and matching implementation on iOS device.

This library does not attempt to replicate Android's implementation completely. The 
goal is to provide ability to execute pretty much any SQL statements at either of 
the platforms with single and simple interface without dependencies on existing
platform specific implementations.
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
that are result or SQLighterDb.java and SQLighterRs.java j2objc converion into 
corresponding Objective C classes (actually, protocols).

# j2objc

You do not need to use j2objc tools to use this library as part of your j2objc project,
because it includes classes\modules that are already j2objc'd. 

So you can save your time on conversion setup and skip to the Project configuration.

But just in case... SQLighterDb.java and SQLighterRs.java normally are to be converted
into iOS to become  SQLighterDb.h, SQLighterDb.m and SQLighterRs.h, SQLighterRs.m.

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

So, you should include SQLighterDb.h, SQLighterDb.m, SQLighterRs.h and SQLighterRs.m in 
your Objective C project whether you generate or copy them from this repository.

### Project configuration

First, you should configure your project for j2objc according to information at
j2objc.org. Add path to j2objc include directory, add j2objc libraries. Make sure
your code compiles and j2objc conversion works. In xcode add libsqlite3.dylib to 
your project libraries.

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

Include contents of /ios/impl/ directory SQLighterDbImpl and SQLighterRsImpl .h and *.m files
into your iOS project. Right click at the project's source files folder, pick "Add files to "prj name", locate
the place where you cloned sqlighter repository, and add those files.

Add path to the content of ios/j2objc to your project's include's search path. I.e. you should be
able to use the following in your code, because these files are under ios/j2objc and xcode has ios/j2objc
in include search path
```
#import "com/vals/a2ios/sqlighter/intf/SQLighterDb.h"
#import "com/vals/a2ios/sqlighter/intf/SQLighterRs.h"
```

Make sure your project compiles.

#### Database file

In both cases - Android and iOS your initial SQLite database file should be part of your
project. It can have some predefined DB schema or you can create tables on the fly - it's
all in developer's control.

On Android's project the location of the file is typically 'assets' directory.

On iOS you have to right click on the project, pick "Add files to...", locate your file
on the file system and add it to the project this way.

SQLighterDb.setDbName(String name) specifies file name. 

SQLighterDb.setDbPath(String path) specifies path to the file on the device. This is
different between Android and iOS. For android it is "/data/data/<<YOUR PROJECT path>>/databases/",
for iOS this method has empty implementation as the library knows the location relative
to project's location.

On application instantiation SQLighterDb.copyDbOnce() will copy the database file from the
project into the device. According to method name it will only copy the file one time even
if called more than once.

Method SQLighterDb.setOverwriteDb(boolean) will let you overwrite target device database
file if one exists. This is helpful for development but in general you shouldn't use it
in production environment.

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
    [db setDbNameWithNSString: @"sqlite.sqlite"];
    [db setReplaceDatabase:FALSE];
    [db copyDbOnce];
    [db openIfClosed];
    [b setDbWithSQLighterDb:db];
    return YES;
}
```
One important feature od this implementation is - you get uniform and platform 
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
        Number pk = rs.getLong(0);
        String e = rs.getString(1);
        byte[] d1 = rs.getBlob(3);
        ....
}
rs.close();
```
c) once your statement is executed, bound parameters are cleaned up, so you can use
 addParam* methods again to be bound/used with your next statement.

<< more to be added to this section>>, for now please see the next section that has
 some pretty straightforward examples.

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

The above code gives identical output after being converted into iOS using j2objc. Therefore, you can implement your database related logics in java language amd just convert/reuse it in iOS.

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
