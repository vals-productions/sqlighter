# AmfibiaN

*This document is under construction.*

AmfibiaN is a lightweight java framework that stands between native objects, their JSON repesentations, and CRUD database operations with elements of ORM. 

AmfibiaN's code is J2ObjC compatible. You would be able to execute your code in Android and iOS. You can use it in conjunction with SQLighter to implement local database storage and relevant business logics based on SQLite database.

Unlike SQLighter, which has platform specific implementatin and is pre-built for both - Android and iOS platforms, AmfibiaN as of now does not have iOS specific code.

*Dependencies:* `org.json` package. Currently `JSONObject` and `JSONArray` are being referenced directly.

## Going by example

Detailed and up to date example with extensive comments can
be found at [Demo.java] 
(https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java) class' [amfibianOperations()] (https://github.com/vals-productions/sqlighter/blob/master/demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/Demo.java#L261) method.

We would like to send/receive business objects to/from our server in JSON representation, convert them into native objects, use as such, also, perform some CRUD database operations.

We will use two objects. The Entity:

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
We might've received the following JSON string representation of the Appointment as a result of our mobile app's data exchange with the server. Let's assume our goal is to convert this JSON string into a native business object, do some operations with the object, save it in the database and  send transformed business object back to the server as JSON string.

```json
"{id: "234", name: "Meet AmfibiaN!", isProcessed: "0"}"
```
First, let's tell AmfibiaN about our business entities and their properties we would like to manage. We do not have to manage all of them, just those we care of.

```java
AnObject<Entity> anEntity = 
	new AnObject( Entity.class, new String[]{"id"});
   
AnOrm<Appointment> anOrm = new AnOrm<>(
	sqlighterDb, // reference to sqlighter database management object
	"appointment", // table name
    Appointment.class, // will
    new String[]{"name", "isProcessed,is_processed"},
    anEntity);
```
After we've done this, we can get native object from json string so that we could manipulate it in native way:

```java
Appointment appointment234 = anOrm.asNativeObject(
	"{id: "234", name: "Meet AmfibiaN!", isProcessed: "0"}");
```

Let's decide to store our ```appointment234``` native object in the database. Since we do not have the table for this entity in our database yet, we can ask AmfibiaN to give us database create table statement for or object:

```java
String createAppointmentTableSql = anOrm.startSqlCreate().getQueryString();
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

Now, since the table for Appointment objects has been created, lets store our object in there. The following two statements will prepare the query, bind parameters and execute the statement against the database:

```java
anOrm.startSqlInsert(appointment234);
anOrm.apply();
```

Imagine we've done some appointment processing and would like to update
our record in the database:

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
	Appointment meetAmfibianAppointment = list.get(0);
	String jsonString = anOrm.asJsonString(meetAmfibianAppointment);
}
```    

```jsonString``` above is ready to be sent back to the server.

## Class diagram

```
   AmfibiaN
             n
AnObject o---- AnAttrib
 ^
 |
AnSql
 ^
 |
AnOrm

```
### AnAttrib

AnAttrib defines a single property of AnObject. 

### AnObject

**AnObject** holds a collection of attribute definitions. As you work with your objects, it may process JSON <---> Native transformations for you.

### AnSql

**AnSql** extends an object and gives you database query generation capabilities on top of what AnObject provides. You can execute the queries through whatever Database implementation you have.

### AnOrm

**AnOrm** adds object mapping capabilities. You canperform CRUD operations on your objects, or, collections of objects.

**AnOrm** does not cover all possible scenarios. The assumption is that the most complex and costly database operations occur at your server, and your mobile client received a simplified and limited subset of data.

For the cases when you do need to do an outer join and retrieve hierarchical result set, you can always use Sqlighter directly and go as far as you want.


