//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../android//com/vals/a2ios/amfibian/impl/AnObject.java
//

#ifndef _ComValsA2iosAmfibianImplAnObject_H_
#define _ComValsA2iosAmfibianImplAnObject_H_

#include "J2ObjC_header.h"

@class ComValsA2iosAmfibianImplAnAttrib;
@class IOSClass;
@class IOSObjectArray;
@class OrgJsonJSONObject;
@protocol JavaUtilList;
@protocol JavaUtilMap;

@interface ComValsA2iosAmfibianImplAnObject : NSObject {
 @public
  IOSClass *nativeClass_;
}

#pragma mark Public

- (instancetype)init;

- (instancetype)initWithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnAttribArray:(IOSObjectArray *)propertyMappers;

- (instancetype)initWithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnAttribArray:(IOSObjectArray *)propertyMappers
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper;

- (instancetype)initWithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper;

- (instancetype)initWithIOSClass:(IOSClass *)anObjClass
               withNSStringArray:(IOSObjectArray *)propertyNames;

- (instancetype)initWithIOSClass:(IOSClass *)anObjClass
               withNSStringArray:(IOSObjectArray *)propertyNames
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper;

- (void)addAttribWithComValsA2iosAmfibianImplAnAttrib:(ComValsA2iosAmfibianImplAnAttrib *)anAttribMapper;

- (OrgJsonJSONObject *)asJSONObjectWithId:(id)nativeObject;

- (NSString *)asJsonStringWithId:(id)nativeObject;

- (id<JavaUtilList>)asListWithNSString:(NSString *)jsonArrayString;

- (id<JavaUtilMap>)asMapWithId:(id)nativeObject;

- (id)asNativeObjectWithOrgJsonJSONObject:(OrgJsonJSONObject *)jsonObject;

- (id)asNativeObjectWithNSString:(NSString *)jsonString;

- (ComValsA2iosAmfibianImplAnAttrib *)getAttribWithNSString:(NSString *)propertyName;

- (id<JavaUtilMap>)getAttribList;

- (IOSClass *)getNativeClass;

- (void)resetNativeObject;

#pragma mark Protected

- (id<JavaUtilMap>)getJsonMap;

- (id)getNativeObject;

- (void)init__WithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnAttribArray:(IOSObjectArray *)propertyMappers OBJC_METHOD_FAMILY_NONE;

- (void)init__WithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnAttribArray:(IOSObjectArray *)propertyMappers
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper OBJC_METHOD_FAMILY_NONE;

- (void)init__WithIOSClass:(IOSClass *)anObjClass
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper OBJC_METHOD_FAMILY_NONE;

- (void)init__WithIOSClass:(IOSClass *)anObjClass
         withNSStringArray:(IOSObjectArray *)propertyNames OBJC_METHOD_FAMILY_NONE;

- (void)init__WithIOSClass:(IOSClass *)anObjClass
         withNSStringArray:(IOSObjectArray *)propertyNames
withComValsA2iosAmfibianImplAnObject:(ComValsA2iosAmfibianImplAnObject *)parentMapper OBJC_METHOD_FAMILY_NONE;

- (void)setNativeClassWithIOSClass:(IOSClass *)anObjClass;

- (void)setNativeObjectWithId:(id)o;

@end

J2OBJC_EMPTY_STATIC_INIT(ComValsA2iosAmfibianImplAnObject)

J2OBJC_FIELD_SETTER(ComValsA2iosAmfibianImplAnObject, nativeClass_, IOSClass *)

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_init(ComValsA2iosAmfibianImplAnObject *self);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_init() NS_RETURNS_RETAINED;

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnObject_(ComValsA2iosAmfibianImplAnObject *self, IOSClass *anObjClass, ComValsA2iosAmfibianImplAnObject *parentMapper);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnObject_(IOSClass *anObjClass, ComValsA2iosAmfibianImplAnObject *parentMapper) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnAttribArray_withComValsA2iosAmfibianImplAnObject_(ComValsA2iosAmfibianImplAnObject *self, IOSClass *anObjClass, IOSObjectArray *propertyMappers, ComValsA2iosAmfibianImplAnObject *parentMapper);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnAttribArray_withComValsA2iosAmfibianImplAnObject_(IOSClass *anObjClass, IOSObjectArray *propertyMappers, ComValsA2iosAmfibianImplAnObject *parentMapper) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnAttribArray_(ComValsA2iosAmfibianImplAnObject *self, IOSClass *anObjClass, IOSObjectArray *propertyMappers);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withComValsA2iosAmfibianImplAnAttribArray_(IOSClass *anObjClass, IOSObjectArray *propertyMappers) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withNSStringArray_withComValsA2iosAmfibianImplAnObject_(ComValsA2iosAmfibianImplAnObject *self, IOSClass *anObjClass, IOSObjectArray *propertyNames, ComValsA2iosAmfibianImplAnObject *parentMapper);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withNSStringArray_withComValsA2iosAmfibianImplAnObject_(IOSClass *anObjClass, IOSObjectArray *propertyNames, ComValsA2iosAmfibianImplAnObject *parentMapper) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT void ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withNSStringArray_(ComValsA2iosAmfibianImplAnObject *self, IOSClass *anObjClass, IOSObjectArray *propertyNames);

FOUNDATION_EXPORT ComValsA2iosAmfibianImplAnObject *new_ComValsA2iosAmfibianImplAnObject_initWithIOSClass_withNSStringArray_(IOSClass *anObjClass, IOSObjectArray *propertyNames) NS_RETURNS_RETAINED;

J2OBJC_TYPE_LITERAL_HEADER(ComValsA2iosAmfibianImplAnObject)

#endif // _ComValsA2iosAmfibianImplAnObject_H_
