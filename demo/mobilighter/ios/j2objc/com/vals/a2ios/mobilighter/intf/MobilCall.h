//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ..//android/com/vals/a2ios/mobilighter/intf/MobilCall.java
//

#ifndef _ComValsA2iosMobilighterIntfMobilCall_H_
#define _ComValsA2iosMobilighterIntfMobilCall_H_

#include "J2ObjC_header.h"

@protocol JavaUtilList;
@protocol JavaUtilMap;
@protocol MobilCallBack;

@protocol MobilCall < NSObject, JavaObject >

- (void)setBaseUrlWithNSString:(NSString *)baseUrl;

- (void)setRelativeUrlWithNSString:(NSString *)relativeUrl;

- (void)setCallBackWithMobilCallBack:(id<MobilCallBack>)callBack;

- (void)addHeaderWithNSString:(NSString *)name
                 withNSString:(NSString *)value;

- (void)setNameWithNSString:(NSString *)name;

- (id<MobilCallBack>)getCallBack;

- (void)setCookieHeaderWithNSString:(NSString *)c;

- (jint)getResponseStatus;

- (void)setMethodWithNSString:(NSString *)method;

- (void)remoteCallMakeOnNewThread;

- (void)remoteCallMakeWithBoolean:(jboolean)isBlocking;

- (void)remoteCallMakeAndWait;

- (void)addUrlParameterWithNSString:(NSString *)name
                             withId:(id)value;

- (void)setJsonPayloadWithNSString:(NSString *)json;

- (void)addParameterWithNSString:(NSString *)name
                          withId:(id)value;

- (void)addParameterWithNSString:(NSString *)name
                          withId:(id)value
                 withJavaUtilMap:(id<JavaUtilMap>)map;

- (void)setParamMapWithJavaUtilMap:(id<JavaUtilMap>)paramMap;

- (id<JavaUtilList>)getThrowableList;

- (void)clearThrowableList;

@end

J2OBJC_EMPTY_STATIC_INIT(MobilCall)

FOUNDATION_EXPORT NSString *MobilCall_METHOD_POST_;
J2OBJC_STATIC_FIELD_GETTER(MobilCall, METHOD_POST_, NSString *)

FOUNDATION_EXPORT NSString *MobilCall_METHOD_GET_;
J2OBJC_STATIC_FIELD_GETTER(MobilCall, METHOD_GET_, NSString *)

J2OBJC_TYPE_LITERAL_HEADER(MobilCall)

#define ComValsA2iosMobilighterIntfMobilCall MobilCall

#endif // _ComValsA2iosMobilighterIntfMobilCall_H_