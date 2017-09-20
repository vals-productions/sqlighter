//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../android//com/vals/a2ios/amfibian/impl/AnOrmImpl.java
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "J2ObjC_source.h"
#include "com/vals/a2ios/amfibian/impl/AnOrmImpl.h"
#include "com/vals/a2ios/amfibian/impl/AnSqlImpl.h"
#include "com/vals/a2ios/amfibian/intf/AnAdapter.h"
#include "com/vals/a2ios/amfibian/intf/AnAttrib.h"
#include "com/vals/a2ios/amfibian/intf/AnIncubator.h"
#include "com/vals/a2ios/amfibian/intf/AnObject.h"
#include "com/vals/a2ios/amfibian/intf/AnOrm.h"
#include "com/vals/a2ios/sqlighter/intf/SQLighterDb.h"
#include "com/vals/a2ios/sqlighter/intf/SQLighterRs.h"
#include "java/lang/Exception.h"
#include "java/lang/Long.h"
#include "java/lang/reflect/Method.h"
#include "java/util/Collection.h"
#include "java/util/Iterator.h"
#include "java/util/LinkedHashMap.h"
#include "java/util/LinkedList.h"
#include "java/util/List.h"
#include "java/util/Map.h"
#include "org/json/JSONObject.h"

@interface AnOrmImpl () {
 @public
  id<AnIncubator> incubator_;
}

@end

J2OBJC_FIELD_SETTER(AnOrmImpl, incubator_, id<AnIncubator>)

__attribute__((unused)) static id<SQLighterDb> AnOrmImpl_getDbEngine(AnOrmImpl *self);

__attribute__((unused)) static id<AnIncubator> AnOrmImpl_getIncubator(AnOrmImpl *self);

__attribute__((unused)) static void AnOrmImpl_applyParameters(AnOrmImpl *self);

__attribute__((unused)) static void AnOrmImpl_assignWithJavaUtilCollection_withJavaUtilMap_withNSString_withNSString_withAnOrm_(AnOrmImpl *self, id<JavaUtilCollection> entities, id<JavaUtilMap> associationMap, NSString *assiciationSrcJoinAttribName, NSString *assiciationSrcAttribName, id<AnOrm> sourceOrm);

__attribute__((unused)) static id<JavaUtilMap> AnOrmImpl_mapAssociationsWithJavaUtilCollection_withAnOrm_withNSString_(AnOrmImpl *self, id<JavaUtilCollection> associations, id<AnOrm> associateOrm, NSString *associationTrgJoinAttribName);

__attribute__((unused)) static jboolean AnOrmImpl_isCollectionWithAnAttrib_(AnOrmImpl *self, id<AnAttrib> attrib);

__attribute__((unused)) static id<JavaUtilCollection> AnOrmImpl_fetchAssociationsWithNSString_withJavaUtilCollection_withNSString_withNSString_withAnOrm_withAnOrm_withNSString_(AnOrmImpl *self, NSString *associationClassName, id<JavaUtilCollection> entities, NSString *assiciationSrcJoinAttribName, NSString *associationTrgJoinAttribName, id<AnOrm> sourceOrm, id<AnOrm> associateOrm, NSString *extraSql);

@implementation AnOrmImpl

J2OBJC_IGNORE_DESIGNATED_BEGIN
- (instancetype)init {
  AnOrmImpl_init(self);
  return self;
}
J2OBJC_IGNORE_DESIGNATED_END

- (instancetype)initWithSQLighterDb:(id<SQLighterDb>)sqLighterDb
                       withNSString:(NSString *)tableName
                       withIOSClass:(IOSClass *)anObjClass
                  withAnAttribArray:(IOSObjectArray *)attribList
                       withAnObject:(id<AnObject>)parentAnObject {
  AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_(self, sqLighterDb, tableName, anObjClass, attribList, parentAnObject);
  return self;
}

- (instancetype)initWithSQLighterDb:(id<SQLighterDb>)sqLighterDb
                       withNSString:(NSString *)tableName
                       withIOSClass:(IOSClass *)anObjClass
                  withNSStringArray:(IOSObjectArray *)attribColumnList
                       withAnObject:(id<AnObject>)parentAnObject {
  AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_(self, sqLighterDb, tableName, anObjClass, attribColumnList, parentAnObject);
  return self;
}

- (id<JavaUtilCollection>)getRecords {
  return [self getRecordsWithJavaUtilCollection:nil];
}

- (void)setIncubatorWithAnIncubator:(id<AnIncubator>)incubator {
  self->incubator_ = incubator;
}

- (id<JavaUtilCollection>)getRecordsWithJavaUtilCollection:(id<JavaUtilCollection>)collectionToUse {
  NSString *queryStr = [self getQueryString];
  if (collectionToUse == nil) {
    collectionToUse = new_JavaUtilLinkedList_init();
  }
  AnOrmImpl_applyParameters(self);
  id<SQLighterRs> rs = [((id<SQLighterDb>) nil_chk(AnOrmImpl_getDbEngine(self))) executeSelectWithNSString:queryStr];
  while ([((id<SQLighterRs>) nil_chk(rs)) hasNext]) {
    [self resetNativeObject];
    jint columnIndex = 0;
    for (NSString * __strong attribName in nil_chk([self getAttribNameList])) {
      if (![self isSkipAttrWithNSString:attribName]) {
        id columnValue = [rs getObjectWithInt:columnIndex++];
        if (columnValue != nil) {
          id<AnAttrib> attrib = [self getAttribWithNSString:attribName];
          [self setValueWithAnAdapter:[((id<AnAttrib>) nil_chk(attrib)) getDbSetAdapter] withAnAdapter:[self getDbSetAdapter] withAnAttrib:attrib withId:columnValue];
        }
      }
    }
    id objectValue = [self getNativeObject];
    id ov = objectValue;
    [collectionToUse addWithId:ov];
  }
  [rs close];
  return collectionToUse;
}

- (id<JavaUtilCollection>)getJSONObjectRecords {
  return [self getJSONObjectRecordsWithJavaUtilCollection:nil];
}

- (id<JavaUtilCollection>)getJSONObjectRecordsWithJavaUtilCollection:(id<JavaUtilCollection>)collectionToUse {
  id<JavaUtilCollection> rc = [self getRecordsWithJavaUtilCollection:collectionToUse];
  id<JavaUtilCollection> joc = new_JavaUtilLinkedList_init();
  for (id __strong c in nil_chk(rc)) {
    [joc addWithId:[self asJSONObjectWithId:c]];
  }
  return joc;
}

- (id)getSingleResult {
  id<JavaUtilCollection> l = [self getRecordsWithJavaUtilCollection:nil];
  if (l == nil || [l size] != 1) {
    return nil;
  }
  return [((id<JavaUtilIterator>) nil_chk([l iterator])) next];
}

- (id)getFirstResultOrNull {
  id<JavaUtilCollection> l = [self getRecordsWithJavaUtilCollection:nil];
  if (l == nil || [l size] == 0) {
    return nil;
  }
  id<JavaUtilIterator> i = [l iterator];
  return [((id<JavaUtilIterator>) nil_chk(i)) next];
}

- (JavaLangLong *)apply {
  if ([self getType] == AnSqlImpl_TYPE_INSERT || [self getType] == AnSqlImpl_TYPE_UPDATE || [self getType] == AnSqlImpl_TYPE_DELETE) {
    NSString *q = [self getQueryString];
    AnOrmImpl_applyParameters(self);
    JavaLangLong *updateInfo = [((id<SQLighterDb>) nil_chk(AnOrmImpl_getDbEngine(self))) executeChangeWithNSString:q];
    return updateInfo;
  }
  else if ([self getType] == AnSqlImpl_TYPE_CREATE) {
    NSString *q = [self getQueryString];
    JavaLangLong *updateInfo = [((id<SQLighterDb>) nil_chk(AnOrmImpl_getDbEngine(self))) executeChangeWithNSString:q];
    return updateInfo;
  }
  return nil;
}

- (void)setSqlighterDbWithSQLighterDb:(id<SQLighterDb>)sqlighterDb {
  self->sqlighterDb_ = sqlighterDb;
}

- (id<SQLighterDb>)getSqlighterDb {
  return sqlighterDb_;
}

- (void)fetchWithId:(id)entity
       withNSString:(NSString *)attribName {
  [self fetchWithId:entity withNSString:attribName withNSString:nil];
}

- (void)fetchWithJavaUtilCollection:(id<JavaUtilCollection>)entities
                       withNSString:(NSString *)attribName {
  [self fetchWithJavaUtilCollection:entities withNSString:attribName withNSString:nil];
}

- (void)fetchWithId:(id)entity
       withNSString:(NSString *)attribName
       withNSString:(NSString *)extraSql {
  id<JavaUtilCollection> c = new_JavaUtilLinkedList_init();
  [c addWithId:entity];
  [self fetchWithJavaUtilCollection:c withNSString:attribName withNSString:extraSql];
}

- (void)fetchWithJavaUtilCollection:(id<JavaUtilCollection>)entities
                       withNSString:(NSString *)attribName
                       withNSString:(NSString *)extraSql {
  if (sqlighterDb_ == nil) {
    @throw new_JavaLangException_initWithNSString_(@"SQlighterDb is not set");
  }
  if (entities == nil || [entities isEmpty] || attribName == nil || [((NSString *) nil_chk([@"" java_trim])) isEqual:attribName]) {
    @throw new_JavaLangException_initWithNSString_(@"Incorrect parameters.");
  }
  IOSClass *cluss = [self getNativeClass];
  id<AnOrm> sourceOrm = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) makeWithIOSClass:cluss];
  if (sourceOrm == nil) {
    @throw new_JavaLangException_initWithNSString_(JreStrcat("$$", @"No definition found for ", [((IOSClass *) nil_chk(cluss)) getName]));
  }
  [sourceOrm setSqlighterDbWithSQLighterDb:sqlighterDb_];
  id<AnAttrib> attrib = [sourceOrm getAttribWithNSString:attribName];
  if (attrib == nil) {
    @throw new_JavaLangException_initWithNSString_(JreStrcat("$$$", @"Attribute ", attribName, @" is not defined"));
  }
  NSString *associationClassName = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) getAssociationTrgClassNameWithIOSClass:cluss withAnAttrib:attrib];
  NSString *associationTrgJoinAttribName = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) getAssociationTrgJoinAttribNameWithIOSClass:cluss withAnAttrib:attrib];
  NSString *assiciationSrcJoinAttribName = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) getAssociationSrcJoinAttribNameWithIOSClass:cluss withAnAttrib:attrib];
  NSString *associationSrcAttribName = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) getAssociationSrcAttribNameWithIOSClass:cluss withAnAttrib:attrib];
  if (associationClassName == nil || associationTrgJoinAttribName == nil || assiciationSrcJoinAttribName == nil || associationSrcAttribName == nil) {
    @throw new_JavaLangException_initWithNSString_(@"Association definition is not complete.");
  }
  id<AnOrm> associateOrm = [((id<AnIncubator>) nil_chk(AnOrmImpl_getIncubator(self))) makeWithNSString:associationClassName];
  if (associateOrm == nil) {
    @throw new_JavaLangException_initWithNSString_(JreStrcat("$$", @"No definition found for: ", associationClassName));
  }
  [associateOrm setSqlighterDbWithSQLighterDb:sqlighterDb_];
  id<JavaUtilCollection> associations = AnOrmImpl_fetchAssociationsWithNSString_withJavaUtilCollection_withNSString_withNSString_withAnOrm_withAnOrm_withNSString_(self, associationClassName, entities, assiciationSrcJoinAttribName, associationTrgJoinAttribName, sourceOrm, associateOrm, extraSql);
  id<JavaUtilMap> associationMap = AnOrmImpl_mapAssociationsWithJavaUtilCollection_withAnOrm_withNSString_(self, associations, associateOrm, associationTrgJoinAttribName);
  AnOrmImpl_assignWithJavaUtilCollection_withJavaUtilMap_withNSString_withNSString_withAnOrm_(self, entities, associationMap, assiciationSrcJoinAttribName, associationSrcAttribName, sourceOrm);
}

@end

void AnOrmImpl_init(AnOrmImpl *self) {
  AnSqlImpl_init(self);
}

AnOrmImpl *new_AnOrmImpl_init() {
  J2OBJC_NEW_IMPL(AnOrmImpl, init)
}

AnOrmImpl *create_AnOrmImpl_init() {
  J2OBJC_CREATE_IMPL(AnOrmImpl, init)
}

void AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_(AnOrmImpl *self, id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject) {
  AnSqlImpl_initWithNSString_withIOSClass_withAnAttribArray_withAnObject_(self, tableName, anObjClass, attribList, parentAnObject);
  self->sqlighterDb_ = sqLighterDb;
}

AnOrmImpl *new_AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_(id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject) {
  J2OBJC_NEW_IMPL(AnOrmImpl, initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_, sqLighterDb, tableName, anObjClass, attribList, parentAnObject)
}

AnOrmImpl *create_AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_(id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribList, id<AnObject> parentAnObject) {
  J2OBJC_CREATE_IMPL(AnOrmImpl, initWithSQLighterDb_withNSString_withIOSClass_withAnAttribArray_withAnObject_, sqLighterDb, tableName, anObjClass, attribList, parentAnObject)
}

void AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_(AnOrmImpl *self, id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject) {
  AnSqlImpl_initWithNSString_withIOSClass_withNSStringArray_withAnObject_(self, tableName, anObjClass, attribColumnList, parentAnObject);
  self->sqlighterDb_ = sqLighterDb;
}

AnOrmImpl *new_AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_(id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject) {
  J2OBJC_NEW_IMPL(AnOrmImpl, initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_, sqLighterDb, tableName, anObjClass, attribColumnList, parentAnObject)
}

AnOrmImpl *create_AnOrmImpl_initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_(id<SQLighterDb> sqLighterDb, NSString *tableName, IOSClass *anObjClass, IOSObjectArray *attribColumnList, id<AnObject> parentAnObject) {
  J2OBJC_CREATE_IMPL(AnOrmImpl, initWithSQLighterDb_withNSString_withIOSClass_withNSStringArray_withAnObject_, sqLighterDb, tableName, anObjClass, attribColumnList, parentAnObject)
}

id<SQLighterDb> AnOrmImpl_getDbEngine(AnOrmImpl *self) {
  if (self->sqlighterDb_ == nil) {
    @throw new_JavaLangException_initWithNSString_(@"DB engine is not set.");
  }
  return self->sqlighterDb_;
}

id<AnIncubator> AnOrmImpl_getIncubator(AnOrmImpl *self) {
  if (self->incubator_ == nil) {
    @throw new_JavaLangException_initWithNSString_(@"Incubator is not set");
  }
  return self->incubator_;
}

void AnOrmImpl_applyParameters(AnOrmImpl *self) {
  id<JavaUtilList> parameters = [self getParameters];
  for (id __strong par in nil_chk(parameters)) {
    [((id<SQLighterDb>) nil_chk(AnOrmImpl_getDbEngine(self))) addParamObjWithId:par];
  }
}

void AnOrmImpl_assignWithJavaUtilCollection_withJavaUtilMap_withNSString_withNSString_withAnOrm_(AnOrmImpl *self, id<JavaUtilCollection> entities, id<JavaUtilMap> associationMap, NSString *assiciationSrcJoinAttribName, NSString *assiciationSrcAttribName, id<AnOrm> sourceOrm) {
  for (id __strong entity in nil_chk(entities)) {
    [((id<AnOrm>) nil_chk(sourceOrm)) setNativeObjectWithId:entity];
    id<AnAttrib> srcAttrib = [sourceOrm getAttribWithNSString:assiciationSrcJoinAttribName];
    id assiciationKeyValue = [((id<AnAttrib>) nil_chk(srcAttrib)) getValue];
    id<AnAttrib> attrib = [sourceOrm getAttribWithNSString:assiciationSrcAttribName];
    if (attrib == nil) {
      @throw new_JavaLangException_initWithNSString_(JreStrcat("$$$", @"Attribute ", assiciationSrcAttribName, @" is not defined."));
    }
    id<JavaUtilCollection> associations = [((id<JavaUtilMap>) nil_chk(associationMap)) getWithId:assiciationKeyValue];
    if (AnOrmImpl_isCollectionWithAnAttrib_(self, attrib)) {
      [attrib setValueWithId:associations];
    }
    else if (associations != nil && [associations size] == 1) {
      [attrib setValueWithId:[((id<JavaUtilIterator>) nil_chk([associations iterator])) next]];
    }
    else {
      [attrib setValueWithId:nil];
    }
  }
}

id<JavaUtilMap> AnOrmImpl_mapAssociationsWithJavaUtilCollection_withAnOrm_withNSString_(AnOrmImpl *self, id<JavaUtilCollection> associations, id<AnOrm> associateOrm, NSString *associationTrgJoinAttribName) {
  id<JavaUtilMap> associationMap = new_JavaUtilLinkedHashMap_init();
  for (id __strong association in nil_chk(associations)) {
    [((id<AnOrm>) nil_chk(associateOrm)) setNativeObjectWithId:association];
    id associationColumnValue = [((id<AnAttrib>) nil_chk([associateOrm getAttribWithNSString:associationTrgJoinAttribName])) getValue];
    id<JavaUtilCollection> items = [associationMap getWithId:associationColumnValue];
    if (items == nil) {
      items = new_JavaUtilLinkedList_init();
      (void) [associationMap putWithId:associationColumnValue withId:items];
    }
    [items addWithId:association];
  }
  return associationMap;
}

jboolean AnOrmImpl_isCollectionWithAnAttrib_(AnOrmImpl *self, id<AnAttrib> attrib) {
  JavaLangReflectMethod *m = [((id<AnAttrib>) nil_chk(attrib)) getGetter];
  if (m != nil) {
    IOSClass *rtc = [m getReturnType];
    if (rtc != nil) {
      return [JavaUtilCollection_class_() isAssignableFrom:rtc];
    }
  }
  return false;
}

id<JavaUtilCollection> AnOrmImpl_fetchAssociationsWithNSString_withJavaUtilCollection_withNSString_withNSString_withAnOrm_withAnOrm_withNSString_(AnOrmImpl *self, NSString *associationClassName, id<JavaUtilCollection> entities, NSString *assiciationSrcJoinAttribName, NSString *associationTrgJoinAttribName, id<AnOrm> sourceOrm, id<AnOrm> associateOrm, NSString *extraSql) {
  if (associationClassName == nil || associationTrgJoinAttribName == nil || assiciationSrcJoinAttribName == nil) {
    return nil;
  }
  [((id<AnOrm>) nil_chk(associateOrm)) startSqlSelect];
  jint size = [((id<JavaUtilCollection>) nil_chk(entities)) size];
  jint idx = 0;
  id<AnAttrib> trgAttr = [associateOrm getAttribWithNSString:associationTrgJoinAttribName];
  if (trgAttr == nil || [trgAttr getColumnName] == nil) {
    @throw new_JavaLangException_initWithNSString_(JreStrcat("$$$", @"Target attribute ", associationTrgJoinAttribName, @" is not defined."));
  }
  for (id __strong entity in entities) {
    [((id<AnOrm>) nil_chk(sourceOrm)) setNativeObjectWithId:entity];
    id<AnAttrib> scrAttr = [sourceOrm getAttribWithNSString:assiciationSrcJoinAttribName];
    if (scrAttr == nil) {
      @throw new_JavaLangException_initWithNSString_(JreStrcat("$$$", @"Source attribute ", associationTrgJoinAttribName, @" is not defined."));
    }
    id parameter = [scrAttr getValue];
    if (idx == 0 && idx == size - 1) {
      [associateOrm addWhereWithNSString:JreStrcat("$$$", @"and ", [trgAttr getColumnName], @" = ?") withId:parameter];
    }
    else if (idx == 0) {
      [associateOrm addWhereWithNSString:JreStrcat("$$$", @"and ", [trgAttr getColumnName], @" in(?") withId:parameter];
    }
    else if (idx == size - 1) {
      [associateOrm addWhereWithNSString:@",?)" withId:parameter];
    }
    else {
      [associateOrm addWhereWithNSString:@",?" withId:parameter];
    }
    idx++;
  }
  if (extraSql != nil) {
    [associateOrm addSqlWithNSString:extraSql];
  }
  id<JavaUtilCollection> associations = [associateOrm getRecords];
  return associations;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(AnOrmImpl)
