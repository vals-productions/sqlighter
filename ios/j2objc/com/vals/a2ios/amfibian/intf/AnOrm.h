//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../android//com/vals/a2ios/amfibian/intf/AnOrm.java
//

#ifndef _ComValsA2iosAmfibianIntfAnOrm_H_
#define _ComValsA2iosAmfibianIntfAnOrm_H_

#include "J2ObjC_header.h"
#include "com/vals/a2ios/amfibian/intf/AnSql.h"

@class JavaLangLong;
@protocol JavaUtilCollection;
@protocol SQLighterDb;

@protocol AnOrm < AnSql, NSObject, JavaObject >

- (id<JavaUtilCollection>)getRecords;

- (id<JavaUtilCollection>)getRecordsWithJavaUtilCollection:(id<JavaUtilCollection>)collectionToUse;

- (id<JavaUtilCollection>)getJSONObjectRecordsWithJavaUtilCollection:(id<JavaUtilCollection>)collectionToUse;

- (id<JavaUtilCollection>)getJSONObjectRecords;

- (id)getSingleResult;

- (id)getFirstResultOrNull;

- (JavaLangLong *)apply;

- (void)setSqlighterDbWithSQLighterDb:(id<SQLighterDb>)sqlighterDb;

- (id<SQLighterDb>)getSqlighterDb;

@end

J2OBJC_EMPTY_STATIC_INIT(AnOrm)

J2OBJC_TYPE_LITERAL_HEADER(AnOrm)

#define ComValsA2iosAmfibianIntfAnOrm AnOrm

#endif // _ComValsA2iosAmfibianIntfAnOrm_H_
