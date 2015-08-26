# sqlighter

SQLIte implementation that works with j2objc on both - Android and iOS platforms

# Overview

This implementation is based on standard Android's SQlite implementation that is being used on Android devices, and matching implementation on iOS device.

```
   Android                            iOS
   
              Interface Definition
              
                   j2objc
SQLighterDb.java ----------> SQLighterDb.h, SQLighterDb.m
                   j2objc
SQLighterRs.java ----------> SQLighterRs.h, SQLighterRs.m

               Implementation
   
SQLighterDbImpl.java         SQLighterDbImpl.h
                             SQLighterDbImpl.m

                             SQLighterRsImpl.h
                             SQLighterRsImpl.m


```

Both implementations conform to SQLighterDb (core database methods) and SQLighterRs (ResultSet processing) interfaces. Android implementation for these is SQLighterDbImpl.java that is included. iOS implementation is a set of ios/impl *Impl.h and *Impl.m files. They implement same interfaces but after SQLighterDb.java and SQLighterRs.java get j2objc'd into Objective C classes with the use of --prefixes <file with prefix configs> option to prevent adding java package prefix to class names. Sample file is below.
```
<file with prefix configs>
...
com.vals.a2ios.sqlighter=
...
```

So, you should get the content of ios/j2objc/ (provided as an example) in your Objective C project, and add those files to your project.

This library does not attempt to replicate Android's implementation completely. The goal is to provide ability to execute pretty much any SQL statements at either of the platforms with single interface without dependencies on existing platform specific implementations.

### Project configuration

#### Android

Include content of sqlighter/android in your Android project. Include com/vals/a2ios/sqlighter/*.java interfaces into j2objc conversion processes. I recommend to exclude package name prefix generation for the package so that class names look shorter and simpler.

#### iOS

Include /ios/impl/ *.h and *.m files into your iOS project.

ios/j2objc/ content does not need to be included anywhere. The files are j2objc conversions of com/vals/a2ios/sqlighter/ interfaces and included as examples.

#### Instantiation example

Here I will show you how to inject platform specific implementations on application/activity initialization. 

One of the ways this might be done is by using singleton pattern. Code below is for Android, which could/should be converted to iOS with j2objc.
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
    db.copyDbOnce(); // will copy DB file from your project files into device's DB location if it's not there yet.
    db.openIfClosed();
  } catch (Exception e) {
  ...
  }
...
```
And your iOS' app delegate initialization method may look like this. Note that Bootstrap is just a j2objc clone of Android's Bootstrap class.
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
Once that is done you can use SQLite in the followig manner.

# Going by example

### Pre requisites
``` sql
CREATE TABLE "user" (
	`name`	TEXT,
	`email`	TEXT,
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`data`	BLOB,
	`height`	REAL
);
```

With the following initial records:
``` sql
INSERT INTO user(name, email, data, height) values ('user 1', 'user1@email.com', null, 1.4);
INSERT INTO user(name, email, data, height) values ('user 2', 'user2@email.com', null, null);
INSERT INTO user(name, email, data, height) values ('user 3', 'user3@email.com', null, 4.89);
INSERT INTO user(name, email, data, height) values ('user 4', null, null, null);
```

### Android code

First let's just output what is initially in the table:

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
Then, add another record with some blob:
``` java
String dataStr = "This is blob string example";
byte[] data = dataStr.getBytes();
db.addParam("user name 5");
db.addParam("qw@er.ty1");
db.addParam(data);
db.addParam(5.67);
db.executeChange("insert into user( name, email, data, height) values (?, ?, ?, ?)");
```
And let's also requery what we just inserted
``` java
db.addParam("qw@er.ty1");
System.out.println("check if the record was inserted");
rs = db.executeSelect("select id, email, name, data, height from user where email = ?");
while (rs.hasNext()) {
  print(rs);
}
rs.close();
```
Which this should result in:
```
check if the record was inserted
pk: 5, email: qw@er.ty1, name: user name 5, blob data: This is blob string example, height: 5.67
```
Then, let's do some update
``` java
db.addParam("user@email.com");
db.addParam("qw@er.ty1");
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
The above code gives identical output after being converted into iOS using j2objc. Therefore, you can implement your database related logics in java language amd just convert/reuse it in iOS.

If for whatever reason you have to code some SQLITE in iOS without j2objc, it doesn't look bad either:
``` objc
id<SQLighterDb> db = [[Bootstrap getInstance] getDb];
[db addParamWithNSString: @"user@email.com"];
id<SQLighterRs> rs = [db executeWithNSString: @"select name, email from user where email = ?"];
while([rs hasNext]) {
	NSLog(@"email:  %@, name: %@", [rs getStringWithInt:1], [rs getStringWithInt:0]);
}
[rs close];

```
