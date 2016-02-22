# AmfibiaN

# Table of content
* [Overview] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#vverview)
* [Going by example] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#going-by-example)
* [Class diagram] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#class-diagram)
* [Database versioning] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#database-versioning)
* [Install] (https://github.com/vals-productions/sqlighter/blob/master/amfibian.md#install)

## Overview

AmfibiaN is called after amphibians,- inhabitants that inhabit in a variety of habitats.

Inhabitants of modern software systems have to be able to transition between their native state on different platforms, database persistent state and various text representations such as JSON format. AmfibiaN is here to help with these scenarios.

AmfibiaN is a lightweight java framework that stands between native objects, their JSON repesentations, and CRUD database operations with elements of ORM. 

AmfibiaN's code is J2ObjC compatible. You would be able to execute your code in Android and iOS. You can use it in conjunction with SQLighter to implement local database storage and relevant business logics based on SQLite database.

AmfibiaN supports single inheritance.

AnUpgrade is a utility that helps to manage schema changes with new application version deployments.

*Dependencies:* `org.json` package. Currently `JSONObject` and `JSONArray` are being referenced directly.

## Going by example

Detailed and up to date example with extensive comments can be found at [Demo.java] 
(https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) class' amfibianOperations() method.

Let's assume the following scenario: we would like to send/receive business objects to/from our server in JSON representation, convert them into native objects, use as such, also, perform some CRUD database operations.

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
"{id: "234", name: "Meet AmfibiaN!", isProcessed: "0"}"
```

Let's assume our goal is to convert this appointment definition JSON string into a native Appointment business object, do some operations with the object, save it in the database and  send transformed business object back to the server as JSON string.

First, let's tell AmfibiaN about our business entities and their properties we would like to manage. We do not have to manage all of them, just those we care of. Essentially, this is our opportunity to map our native properties to their JSON and database column properties. If you are lucky to control their
names, it could be just simply one name for all, but if you are dealing with some legacy system and names do not match, then you have flexibility to deal with such situation.

```java
AnObject<Entity> anEntity = 
	new AnObject( Entity.class, new String[]{"id"});
   
AnOrm<Appointment> anOrm = new AnOrm<>(
	/* reference sqlighter database management object */
	sqlighterDb,
	/*database table name*/
	"appointment",
	/*provide the class info for java reflection*/
    Appointment.class, 
	/* Provide column mappings, here isProcessed 
	attribute is mapped to  "is_processed" database
	column in "amfibian" database table, while the
	"name" is named identically everywhere */
    new String[]{"name", "isProcessed,is_processed"},
    anEntity);
```
After we've done with mappings, we can get native object from json string so that we could manipulate it in native way:

```java
Appointment appointment234 = anOrm.asNativeObject(
	"{id: "234", name: "Meet AmfibiaN!", isProcessed: "0"}");
```

Let's store our ```appointment234``` native object in the database. Since we do not have the table for this entity in our database yet, we can ask AmfibiaN to give us database create table statement for or object:

```java
String createAppointmentTableSql =
	anOrm.startSqlCreate().getQueryString();
```

the variable above will contain:

```
create table appointment(
  name TEXT,
  id INTEGER,
  is_processed INTEGER )
```

Note how database column names relate to object attributes. Lets execute the query:

```java
sqlighterDb.executeChange(createAppointmentTableSql);
```

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

## Class diagram


```
   AmfibiaN
             n
AnObject o---- AnAttrib
 ^
 |
AnSql          AnUpgrade
 ^
 |
AnOrm

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

## Database versioning

### AnUpdate

***AnUpdate*** is helping you with applying changes to your database schema to update particular device's database structure to the most current version. It handles situations where your customer's mobile application is several versions behind. Also, it might happen that incremental upgrade fail due to unpredicted sequence of updates, and **AnUpgrade** lets you specify database recovery scenario to its most up to date state. See Demo.java for an example of such technique.

**AnUpdate** can work with low level SQL DDL statements, or, use some extended AmfibiaN features where instead of specifying ```CREATE TABLE ... ``` statements, you pass a list of ```AnObject <....>``` instances, and AmfibiaN will take care of the rest and will generate respective statements and execute them. Other than obvious convenience, there will be less chance to execute ```CREATE``` statement that are out of date with your latest object model. Less things to maintain.

<b>AnUpdate is under current development, new features are being added, some may be not backward compatible. Evaluate it, but use with caution until this notice is removed.</b>

## Installation

AmfibiaN is part of SQLighter repository. Follow SQLighter installation instructions.

<b>AmfibiaN is a part of library based distribution since v 2.0.0. </b>
