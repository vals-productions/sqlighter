# sqlighter

SQLIte implementation that works with j2objc on both - Android and iOS platforms

Note: I'm in the progress of initial repository creation / documenting.

# Overview

This implementation is based on standard Android's SQlite implementation that is being used on Android devices, and matching implementation on iOS device.

Both implementations conform to SQLighterDb (core database methods) and SQLighterRs (ResultSet processing) interfaces. 

This library does not attempt to replicate Android's implementation completely. The goal is to provide ability to execute pretty much any SQL statements at either of the platforms with single interface without dependencies on existing platform specific implementations.

# Going by example

### Pre requisites
```
CREATE TABLE "user" (
	`name`	TEXT,
	`email`	TEXT,
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
	`data`	BLOB,
	`height`	REAL
);
```

With the following initial records:
```
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
The above code gives identical output after being converted into iOS using j2objc.

