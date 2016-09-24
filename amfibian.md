# AmfibiaN

# Table of content
* [Overview] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#vverview)
* [Going by example] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#going-by-example)
* [Association fetching] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#association-fetching)
* [Class diagram] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#class-diagram)
* [Database versioning] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#database-versioning)
* [JSON definitions] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#json-definitions)
* [Install] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#install)

## Overview

AmfibiaN ORM is called after amphibians,- inhabitants that inhabit in a variety of habitats.

Inhabitants of modern software systems have to be able to transition between their native
state on different platforms, database persistent state and various text representations
such as JSON format. AmfibiaN is here to help with these scenarios.

AmfibiaN is a lightweight java framework that stands between native objects, their JSON
repesentations (to communicate with the server if necessary), and CRUD database operations
with elements of ORM. It supports association fetching. It is executing only one query per
association (no N + 1). Association fetching also solves absence of outer join suport by
SQLite.

AmfibiaN's code is J2ObjC compatible. You would be able to execute your code in Android
and iOS. You can use it in conjunction with SQLighter to implement local database storage
and relevant business logics based on SQLite database.

AmfibiaN supports single inheritance.

AnUpgrade is a utility that helps to manage schema changes with new application version 
deployments.

*Dependencies:* `org.json` package. Currently `JSONObject` and `JSONArray` are being referenced directly.

AmfibiaN objects are not thread safe, so instantiate an instance of what you need per thread.

AmfibiaN does not introduce yet another query language. It generates SQLs and maps object properties to SQL statements. If you need to specify WHERE clause conditions or tweak queries, you do this in SQL.

## Going by example

Detailed and up to date example with extensive comments can be found at [Demo.java] 
(https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) 
class' amfibianOperations() method.

Let's assume the following scenario: we would like to send/receive business objects to/from our server
in JSON representation, convert them into native objects, use as such, also, perform some CRUD database
operations.

We are dealing with some sort of appointment setup system.

We will use two objects. The Entity, that is common superclass for our business objects:

```java
public class Entity {
    public Integer id;
...
```
and the Appointment that extends the Entity

```java
public class Appointment extends Entity {
    private String name;
    private Integer isProcessed;
...
```

We might've received the following JSON string representation of the Appointment as a result of our mobile app's data exchange with our server:


```json
"{"id": "234", "name": "Meet AmfibiaN!", "isProcessed": "0"}"
```

Let's assume our goal is to convert this appointment definition JSON string into a native Appointment business object, do some operations with the object, save it in the database and  send transformed business object back to the server as JSON string.

First, let's tell AmfibiaN about our business entities and their properties we would like to manage. We do not have to manage all of them, just those we care of. Essentially, this is our opportunity to map our native properties to their JSON and database column properties. If you are lucky to control their names, it could be just simply one name for all, but if you are dealing with some legacy system and names do not match, then you have flexibility to deal with such situation.

JSON file with definitions is a preferred way of providing definitions. Here's demo project's defintion file with
extensive comments: 

After we've done with mappings, we can get native object from json string so that we could manipulate it in native way:

```java
Appointment appointment234 = anOrm.asNativeObject(
	"{id: "234", name: "Meet AmfibiaN!", isProcessed: "0"}");
```

Let's store our ```appointment234``` native object in the database. Since we do not have the table for this entity in our database yet, we can ask AmfibiaN to give us database create table statement for or object:

```java
anOrm.startSqlCreate();
anOrm.apply();
```

statements above will execute the following SQL:

```
create table appointment(
  name TEXT,
  id INTEGER,
  ....

```

Note how database column names relate to object attributes.

Now, since the table for Appointment objects has been created, lets persist our object in there. The following two statements will prepare the query, (implicitly) bind parameters and execute the statement against the database:

```java
anOrm.startSqlInsert(appointment234);
anOrm.apply();
```

Imagine we've done some appointment processing and would like to update our appointment record in the database:

```java
appointment234.setIsProcessed(1);

anOrm.startSqlUpdate(appointment234);
anOrm.addWhere("id = ?", appointment234.getId());
anOrm.apply();
```

Next, we are going to retrieve our info from the database and
transform back to JSON to be able to send it back to the server
over the network.

```java
anOrm.startSqlSelect();
anOrm.addWhere("id = ?", 234);

List<Appointment> list = anOrm.getRecords();
if (list.size() == 1) { // just making sure we've got the result
	// get native object
	Appointment meetAmfibianAppointment = list.get(0);
	// get JSON
	String jsonString = anOrm.asJsonString(meetAmfibianAppointment);
}
```    

jsonString above is ready to be sent back to the server.

### Association fetching

In order to be able to deal with association fetching you should define each assiciation in JSON definition file.

Here's one example:

``` java 
public class User {
    private Long userId;
    private Collection<Appointment> appointments;
...

public class Appointment {
    private Long createUserId;
    private User createUser;
...
```

We would like to be able to fetch user.getAppointments() (appointments created by the user) and appointment.getCreateUser().

```
    /* json file fragment */
    
    /* Appointment */
        ...
        { "attribName":"createUserId"},
        { "attribName":"createUser,null,createUser", /* null means we don't need matching db table column for createUser. 
                                                        createUserId serves this purpose */
       ...
            "associations": [
                {
                    "name": "createUser", /* association name */
                    "srcAttribName": "createUserId", /* createUserId is matching column at the (source) Appointment object */
                    "trgAttribName": "id", /* User.id is matching column at the (target) User object */
                    "object": "package.name.User" /* full class name of the associated User object */
                }
            }
        }
        ...
    
    /* User */
        ...
        { "attribName": "id",
          "attribName": "appointments,null,appointments", /* null means we do not need a matching db table column for appointments. */
          ...
            "associations": [
                {
                     "name": "appointments", /* association name */
                     "srcAttribName": "id", /* id is matching column at the (source) User object */
                     "trgAttribName": "createUserId", /* Appointment.createUserId is matching column at the (target) Appointment object */
                     "object":"package.name.Appointment" /* full class name of the associated Appointment object */
                }
            ]
        }
```
So as you see in addition to createUser object (which is not mapped to the database) we also need to keep createUserId - the actual
database column name that identifies the create User in the Appointment object.

After this we'll be able to fetch associations just like that:

``` java
userOrm.fetch(user, "appointments", "and isProcessed = 0");
```

The call above will select all user appointments that are not processed yet in a single query, and assign to supplied user object.

Or it could be done the following way. The next call will select all appointments for users in userList collection in a single query
and assign to respective user.appointments collections if found. As you can see this is also working like outer join substitute
as SQLite does not support one.

``` java
userOrm.fetch(userList, "appointments", "and isProcessed = 1");
```

and this is also working:

``` java
appointmentOrm.fetch(appointmentList, "createUser");
appointmentOrm.fetch(appointment, "createUser");
```

## Class diagram

```
   AmfibiaN
             n
AnObject o---- AnAttrib
 ^
 |
AnSql          AnUpgrade
 ^             AnAdaptor
 |             
AnOrm    ----  AnIncubator

```

### AnAttrib

**AnAttrib** defines a single property of AnObject. In the example above it is not referenced anywhere in the code. They are being created implicitly by **AnObject** based on column mappings provided. In complicated scenarios you might need to work with them directly to be able to gain more control.

### AnObject

**AnObject** holds a collection of attribute definitions. As you work with your objects, it may process JSON <---> Native transformations for you.

If you do not deal with database, this might be all you need.

### AnSql

**AnSql** extends **AnObject** and gives you database query generation capabilities on top of what **AnObject** provides. You can execute the queries through whatever Database implementation you have, not only SQLite.

### AnOrm

**AnOrm** adds object mapping capabilities on top of what **AnSql** implements. You can perform CRUD operations on your objects, or, collections of objects.

**AnOrm** makes it much easier for you to manipulate and convert objects in most cases. Your mobile database is typically much simple than database server database and does not need complex joins as it represents a subset of data for a single customer. In case you need something complex, you can always use the full power of direct SQL. 

For the cases when you do need to do an outer join and retrieve hierarchical result set, you can always use Sqlighter directly and go as far as you want.

### AnAdapter

Adapters are needed in case some object's attribute's type is different in native, json and/or db presentation. If you control all presentations you might not need this feature, but often you deal with third party systems and have to deal with discrepancies. 

For example, you receive JSON dates from third party systems in the format of milliseconds, but would like to assign it to a Date attribute in your native object and then save it as formatted date string in the database. Then you'd need adapters for this scenario.

Adapters are AnAttrib'ute centric.

JsonGetAdapters will get the value from Native Object's attribute, convert it into the format required by JSON representation. In the situation above, it would take a Date and convert it into milliseconds.

JsonSetAdapters would get the value from JSON serialized anbject's date (long value in the example above), convert in into a Date that would be suitable to assign to a native object's date attribute.

Json adapters are defined at AnObject level.

DbGetAdapters and DbSetadapters are similar and work for Native Object\Database conversions. They are defined at AnSql level as this is the entry level for DB operations.

AnAttrib has SetAdapters and GetAdapters. If set at this level you have to make them flexible to understand what type of conversion is requested - Json or database type.

If multiple adapters are defined, then the AmfibiaN will preffer:

* Attribute level converter first
* Object level converter if no attribute level converted defined.

If no adapters are supplied, straight assignment will be attempted.

AmfibiaN has some sample adapters turned on (see AnObject initadapters() method). They should be replaced/customized per your needs as you develop and get into your project specifics.

### AnIncubator

AnIncubator consumes JSON defintion file, parses it, and produces AnOrm instancs upon request. It can be used instead of configurng AnOrm instances in programmatic way.

```java
incubator.load(jsonString);
AnOrm<Appointment> orm = incubator.make(Appointment.class);

```

Demo project contains expamples of AmfibiaN usage.

### JSON file definitions.
Here's 
[sample json definition file from Demo project] (https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/assets/an_objects.json) with comments that help to understand its format.

This section is to be updated with more content.

## Database versioning

### AnUpdate

***AnUpdate*** is helping you with applying changes to your database schema to update particular 
device's database structure to the most current version. It handles situations where your customer's
mobile application is several versions behind. 

It might happen that incremental upgrade fails due to unpredicted sequence of updates, and 
**AnUpgrade** lets you specify database recovery scenario to
its most up to date state. See Demo.java for an example of such technique.

**AnUpdate** can work with low level SQL DDL statements, or, use some extended AmfibiaN 
features where instead of developing ```CREATE TABLE ... ``` statements, you pass a list 
of ```AnObject <....>``` instances, and AmfibiaN will take care of the rest and will 
generate respective statements and execute them. Other than just obvious convenience, there 
will be less chance to execute ```CREATE``` statements that are out of date with your latest
object model. Less things to maintain.

Check out [Demo.java] 
(https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) 
for AnUpgrade in action steps.

## JSON definitions

For now the sample Demo project json file: [url] serves as documentation. The file contains extensive comments
and I hope its content is easy to understand.

### Associations

Association JSON section is explained here:
[Association fetching] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#association-fetching)

## Installation

AmfibiaN ORM is part of SQLighter repository. Follow SQLighter installation instructions.
