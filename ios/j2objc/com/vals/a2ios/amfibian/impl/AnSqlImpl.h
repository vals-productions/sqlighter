//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../android//com/vals/a2ios/amfibian/impl/AnSqlImpl.java
//

#include "J2ObjC_header.h"

#pragma push_macro("INCLUDE_ALL_ComValsA2iosAmfibianImplAnSqlImpl")
#ifdef RESTRICT_ComValsA2iosAmfibianImplAnSqlImpl
#define INCLUDE_ALL_ComValsA2iosAmfibianImplAnSqlImpl 0
#else
#define INCLUDE_ALL_ComValsA2iosAmfibianImplAnSqlImpl 1
#endif
#undef RESTRICT_ComValsA2iosAmfibianImplAnSqlImpl

#if !defined (AnSqlImpl_) && (INCLUDE_ALL_ComValsA2iosAmfibianImplAnSqlImpl || defined(INCLUDE_AnSqlImpl))
#define AnSqlImpl_

#define RESTRICT_ComValsA2iosAmfibianImplAnObjectImpl 1
#define INCLUDE_AnObjectImpl 1
#include "com/vals/a2ios/amfibian/impl/AnObjectImpl.h"

#define RESTRICT_ComValsA2iosAmfibianIntfAnSql 1
#define INCLUDE_AnSql 1
#include "com/vals/a2ios/amfibian/intf/AnSql.h"

@class IOSClass;
@class IOSObjectArray;
@class JavaLangInteger;
@protocol AnAdapter;
@protocol AnAttrib;
@protocol AnObject;
@protocol JavaUtilList;
@protocol JavaUtilSet;

@interface AnSqlImpl : AnObjectImpl < AnSql > {
 @public
  NSString *tableName_;
}

#pragma mark Public

- (instancetype)initWithNSString:(NSString *)tableName
                    withAnObject:(id<AnObject>)anAllDefinedObject;

- (instancetype)initWithNSString:(NSString *)tableName
                    withIOSClass:(IOSClass *)anObjClass
               withAnAttribArray:(IOSObjectArray *)attribList
                    withAnObject:(id<AnObject>)parentAnObject;

- (instancetype)initWithNSString:(NSString *)tableName
                    withIOSClass:(IOSClass *)anObjClass
               withNSStringArray:(IOSObjectArray *)attribColumnList
                    withAnObject:(id<AnObject>)parentAnObject;

- (void)addInclAttribsWithNSStringArray:(IOSObjectArray *)names;

- (void)addLimitOffsetWithJavaLangInteger:(JavaLangInteger *)limit
                      withJavaLangInteger:(JavaLangInteger *)offset;

- (void)addSkipAttribsWithNSStringArray:(IOSObjectArray *)names;

- (void)addSqlWithNSString:(NSString *)sql;

- (void)addWhereWithNSString:(NSString *)condition;

- (void)addWhereWithNSString:(NSString *)condition
                      withId:(id)param;

- (NSString *)getAliasedColumnWithNSString:(NSString *)columnName;

- (id<JavaUtilList>)getAttribNameList;

- (id<AnAdapter>)getDbGetAdapter;

- (id<AnAdapter>)getDbSetAdapter;

- (id<JavaUtilList>)getParameters;

- (NSString *)getQueryString;

- (id<JavaUtilSet>)getSkipAttrNameList;

- (NSString *)getTableName;

- (jint)getType;

- (void)resetSkipInclAttrNameList;

- (void)setDbGetAdapterWithAnAdapter:(id<AnAdapter>)dbGetAdapter;

- (void)setDbSetAdapterWithAnAdapter:(id<AnAdapter>)dbSetAdapter;

- (void)setTableNameWithNSString:(NSString *)tableName;

- (void)setTypeWithInt:(jint)type;

- (id<AnSql>)startSqlCreate;

- (void)startSqlDelete;

- (void)startSqlInsertWithId:(id)objectToInsert;

- (void)startSqlSelect;

- (void)startSqlUpdateWithId:(id)objectToUpdate;

#pragma mark Protected

- (instancetype)init;

- (NSString *)getSqlColumnDefinitionWithAnAttrib:(id<AnAttrib>)attr;

- (jboolean)isSkipAttrWithNSString:(NSString *)propertyName;

// Disallowed inherited constructors, do not use.

- (instancetype)initWithIOSClass:(IOSClass *)arg0
               withAnAttribArray:(IOSObjectArray *)arg1 NS_UNAVAILABLE;

- (instancetype)initWithIOSClass:(IOSClass *)arg0
               withAnAttribArray:(IOSObjectArray *)arg1
                    withAnObject:(id<AnObject>)arg2 NS_UNAVAILABLE;

- (instancetype)initWithIOSClass:(IOSClass *)arg0
                    withAnObject:(id<AnObject>)arg1 NS_UNAVAILABLE;

- (instancetype)initWithIOSClass:(IOSClass *)arg0
               withNSStringArray:(IOSObjectArray *)arg1 NS_UNAVAILABLE;

- (instancetype)initWithIOSClass:(IOSClass *)arg0
               withNSStringArray:(IOSObjectArray *)arg1
                    withAnObject:(id<AnObject>)arg2 NS_UNAVAILABLE;

@end

J2OBJC_EMPTY_STATIC_INIT(AnSqlImpl)

J2OBJC_FIELD_SETTER(AnSqlImpl, tableName_, NSString *)

inline jint AnSqlImpl_get_TYPE_SELECT();
#define AnSqlImpl_TYPE_SELECT 1
J2OBJC_STATIC_FIELD_CONSTANT(AnSqlImpl, TYPE_SELECT, jint)

inline jint AnSqlImpl_get_TYPE_UPDATE();
#define AnSqlImpl_TYPE_UPDATE 2
J2OBJC_STATIC_FIELD_CONSTANT(AnSqlImpl, TYPE_UPDATE, jint)

inline jint AnSqlImpl_get_TYPE_INSERT();
#define AnSqlImpl_TYPE_INSERT 3
J2OBJC_STATIC_FIELD_CONSTANT(AnSqlImpl, TYPE_INSERT, jint)

inline jint AnSqlImpl_get_TYPE_CREATE();
#define AnSqlImpl_TYPE_CREATE 4
J2OBJC_STATIC_FIELD_CONSTANT(AnSqlImpl, TYPE_CREATE, jint)

inline jint AnSqlImpl_get_TYPE_DELETE();
#define AnSqlImpl_TYPE_DELETE 5
J2OBJC_STATIC_FIELD_CONSTANT(AnSqlImpl, TYPE_DELETE, jint)

FOUNDATION_EXPORT void AnSqlImpl_initWithNSString_withIOSClass_withAnAttribArray_withAnObject_(AnSqlImpl *self, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject);

FOUNDATION_EXPORT AnSqlImpl *new_AnSqlImpl_initWithNSString_withIOSClass_withAnAttribArray_withAnObject_(NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT AnSqlImpl *create_AnSqlImpl_initWithNSString_withIOSClass_withAnAttribArray_withAnObject_(NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject);

FOUNDATION_EXPORT void AnSqlImpl_initWithNSString_withIOSClass_withNSStringArray_withAnObject_(AnSqlImpl *self, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject);

FOUNDATION_EXPORT AnSqlImpl *new_AnSqlImpl_initWithNSString_withIOSClass_withNSStringArray_withAnObject_(NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT AnSqlImpl *create_AnSqlImpl_initWithNSString_withIOSClass_withNSStringArray_withAnObject_(NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject);

FOUNDATION_EXPORT void AnSqlImpl_initWithNSString_withAnObject_(AnSqlImpl *self, NSString *tableName, id<AnObject> anAllDefinedObject);

FOUNDATION_EXPORT AnSqlImpl *new_AnSqlImpl_initWithNSString_withAnObject_(NSString *tableName, id<AnObject> anAllDefinedObject) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT AnSqlImpl *create_AnSqlImpl_initWithNSString_withAnObject_(NSString *tableName, id<AnObject> anAllDefinedObject);

FOUNDATION_EXPORT void AnSqlImpl_init(AnSqlImpl *self);

FOUNDATION_EXPORT AnSqlImpl *new_AnSqlImpl_init() NS_RETURNS_RETAINED;

FOUNDATION_EXPORT AnSqlImpl *create_AnSqlImpl_init();

J2OBJC_TYPE_LITERAL_HEADER(AnSqlImpl)

@compatibility_alias ComValsA2iosAmfibianImplAnSqlImpl AnSqlImpl;

#endif

#pragma pop_macro("INCLUDE_ALL_ComValsA2iosAmfibianImplAnSqlImpl")
