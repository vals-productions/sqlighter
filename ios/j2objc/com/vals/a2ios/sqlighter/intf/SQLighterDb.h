//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../android//com/vals/a2ios/sqlighter/intf/SQLighterDb.java
//

#include "J2ObjC_header.h"

#pragma push_macro("INCLUDE_ALL_ComValsA2iosSqlighterIntfSQLighterDb")
#ifdef RESTRICT_ComValsA2iosSqlighterIntfSQLighterDb
#define INCLUDE_ALL_ComValsA2iosSqlighterIntfSQLighterDb 0
#else
#define INCLUDE_ALL_ComValsA2iosSqlighterIntfSQLighterDb 1
#endif
#undef RESTRICT_ComValsA2iosSqlighterIntfSQLighterDb

#if !defined (SQLighterDb_) && (INCLUDE_ALL_ComValsA2iosSqlighterIntfSQLighterDb || defined(INCLUDE_SQLighterDb))
#define SQLighterDb_

@class IOSByteArray;
@class JavaLangLong;
@class JavaUtilDate;
@protocol SQLighterRs;

@protocol SQLighterDb < JavaObject >

- (jboolean)isDbFileDeployed;

- (void)setDbNameWithNSString:(NSString *)name;

- (void)setDbPathWithNSString:(NSString *)path;

- (void)setContextWithId:(id)context;

- (void)setOverwriteDbWithBoolean:(jboolean)isOverwrite;

- (void)openIfClosed;

- (void)copyDbOnce OBJC_METHOD_FAMILY_NONE;

- (void)deployDbOnce;

- (void)addParamWithNSString:(NSString *)s;

- (void)addParamWithDouble:(jdouble)d;

- (void)addParamWithLong:(jlong)l;

- (void)addParamNull;

- (void)addParamWithByteArray:(IOSByteArray *)blob;

- (void)addParamWithJavaUtilDate:(JavaUtilDate *)date;

- (void)addParamObjWithId:(id)o;

- (id<SQLighterRs>)executeSelectWithNSString:(NSString *)selectQuery;

- (JavaLangLong *)executeChangeWithNSString:(NSString *)statementString;

- (void)beginTransaction;

- (void)commitTransaction;

- (void)rollbackTransaction;

- (void)close;

- (jboolean)deleteDBFile;

- (void)setIsDateNamedColumnWithBoolean:(jboolean)isDateNamedColumn;

- (JavaUtilDate *)getDateWithoutMillisWithJavaUtilDate:(JavaUtilDate *)date;

- (jlong)getStatementBalance;

- (void)setDateColumnNameHintWithNSString:(NSString *)hint;

- (void)setTimeZoneWithNSString:(NSString *)timeZone;

- (void)setDateFormatStringWithNSString:(NSString *)dateFormatString;

- (void)setDefaultIntegerColumnTypeWithInt:(jint)defaultIntegerColumnType;

@end

J2OBJC_EMPTY_STATIC_INIT(SQLighterDb)

inline NSString *SQLighterDb_get_DATE_HINT();
/*! INTERNAL ONLY - Use accessor function from above. */
FOUNDATION_EXPORT NSString *SQLighterDb_DATE_HINT;
J2OBJC_STATIC_FIELD_OBJ_FINAL(SQLighterDb, DATE_HINT, NSString *)

inline NSString *SQLighterDb_get_DATE_FORMAT();
/*! INTERNAL ONLY - Use accessor function from above. */
FOUNDATION_EXPORT NSString *SQLighterDb_DATE_FORMAT;
J2OBJC_STATIC_FIELD_OBJ_FINAL(SQLighterDb, DATE_FORMAT, NSString *)

inline jint SQLighterDb_get_DEFAULT_INTEGER_COLUMN_CLASS_INTEGER();
#define SQLighterDb_DEFAULT_INTEGER_COLUMN_CLASS_INTEGER 0
J2OBJC_STATIC_FIELD_CONSTANT(SQLighterDb, DEFAULT_INTEGER_COLUMN_CLASS_INTEGER, jint)

inline jint SQLighterDb_get_DEFAULT_INTEGER_COLUMN_CLASS_LONG();
#define SQLighterDb_DEFAULT_INTEGER_COLUMN_CLASS_LONG 1
J2OBJC_STATIC_FIELD_CONSTANT(SQLighterDb, DEFAULT_INTEGER_COLUMN_CLASS_LONG, jint)

J2OBJC_TYPE_LITERAL_HEADER(SQLighterDb)

#define ComValsA2iosSqlighterIntfSQLighterDb SQLighterDb

#endif

#pragma pop_macro("INCLUDE_ALL_ComValsA2iosSqlighterIntfSQLighterDb")
