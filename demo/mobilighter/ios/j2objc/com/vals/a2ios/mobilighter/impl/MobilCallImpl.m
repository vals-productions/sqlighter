//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ..//android/com/vals/a2ios/mobilighter/impl/MobilCallImpl.java
//

#include "IOSClass.h"
#include "IOSPrimitiveArray.h"
#include "J2ObjC_source.h"
#include "com/vals/a2ios/mobilighter/impl/MobilCallImpl.h"
#include "com/vals/a2ios/mobilighter/intf/MobilCallBack.h"
#include "java/io/BufferedReader.h"
#include "java/io/IOException.h"
#include "java/io/InputStream.h"
#include "java/io/InputStreamReader.h"
#include "java/io/OutputStream.h"
#include "java/io/UnsupportedEncodingException.h"
#include "java/lang/Exception.h"
#include "java/lang/Integer.h"
#include "java/lang/StringBuilder.h"
#include "java/lang/Thread.h"
#include "java/lang/Throwable.h"
#include "java/net/HttpURLConnection.h"
#include "java/net/URL.h"
#include "java/net/URLConnection.h"
#include "java/net/URLEncoder.h"
#include "java/util/HashMap.h"
#include "java/util/List.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@interface MobilCallImpl ()

- (void)specHeaders;

- (NSString *)readStreamWithJavaIoInputStream:(JavaIoInputStream *)stream;

- (void)writePostBodyWithNSString:(NSString *)bodyStr;

- (NSString *)buildUrlParamsWithJavaUtilMap:(id<JavaUtilMap>)paramMap;

@end

__attribute__((unused)) static void MobilCallImpl_specHeaders(MobilCallImpl *self);

__attribute__((unused)) static NSString *MobilCallImpl_readStreamWithJavaIoInputStream_(MobilCallImpl *self, JavaIoInputStream *stream);

__attribute__((unused)) static void MobilCallImpl_writePostBodyWithNSString_(MobilCallImpl *self, NSString *bodyStr);

__attribute__((unused)) static NSString *MobilCallImpl_buildUrlParamsWithJavaUtilMap_(MobilCallImpl *self, id<JavaUtilMap> paramMap);

@interface MobilCallImpl_MobilCallThreadImpl () {
 @public
  id<MobilCallBack> callback_;
  MobilCallImpl *netCall_;
  jint tryCount_;
}

@end

J2OBJC_FIELD_SETTER(MobilCallImpl_MobilCallThreadImpl, callback_, id<MobilCallBack>)
J2OBJC_FIELD_SETTER(MobilCallImpl_MobilCallThreadImpl, netCall_, MobilCallImpl *)

@implementation MobilCallImpl

- (void)setBaseUrlWithNSString:(NSString *)baseUrl {
  self->baseUrl_ = baseUrl;
}

- (void)setRelativeUrlWithNSString:(NSString *)relativeUrl {
  self->relativeUrl_ = relativeUrl;
}

- (void)setCallBackWithMobilCallBack:(id<MobilCallBack>)callBack {
  self->callBack_ = callBack;
}

- (void)addHeaderWithNSString:(NSString *)name
                 withNSString:(NSString *)value {
  (void) [((id<JavaUtilMap>) nil_chk(httpHeaders_)) putWithId:name withId:value];
}

- (void)setNameWithNSString:(NSString *)name {
  self->name_ = name;
}

- (void)setHeaderWithNSString:(NSString *)name
                 withNSString:(NSString *)value {
  if (name != nil && value != nil) {
    [((JavaNetHttpURLConnection *) nil_chk(connection_)) setRequestPropertyWithNSString:name withNSString:value];
  }
}

- (void)specHeaders {
  MobilCallImpl_specHeaders(self);
}

+ (jboolean)isEmptyWithNSString:(NSString *)s1 {
  return MobilCallImpl_isEmptyWithNSString_(s1);
}

- (id<MobilCallBack>)getCallBack {
  return callBack_;
}

- (void)clearCookie {
  cookie_ = nil;
}

- (void)setCookieHeaderWithNSString:(NSString *)c {
  [self setHeaderWithNSString:@"Cookie" withNSString:c];
}

- (jint)getResponseStatus {
  return responseStatus_;
}

- (void)setMethodWithNSString:(NSString *)method {
  self->method_ = [((NSString *) nil_chk(method)) uppercaseString];
}

- (void)connect {
  [((JavaNetHttpURLConnection *) nil_chk(connection_)) connect];
}

- (jboolean)isErrorWithInt:(jint)responseStatus {
  return responseStatus >= 400 && responseStatus < 600;
}

- (void)consumeResponse {
  @try {
    responseStatus_ = [((JavaNetHttpURLConnection *) nil_chk(connection_)) getResponseCode];
    if ([self isErrorWithInt:responseStatus_]) {
      JavaIoInputStream *errorStream = [connection_ getErrorStream];
      responseContent_ = MobilCallImpl_readStreamWithJavaIoInputStream_(self, errorStream);
    }
    else {
      responseContent_ = MobilCallImpl_readStreamWithJavaIoInputStream_(self, [connection_ getInputStream]);
    }
    [self readResponseCookieWithJavaNetHttpURLConnection:connection_];
  }
  @catch (JavaLangThrowable *t) {
    [((id<JavaUtilList>) nil_chk(throwableList_)) addWithId:t];
  }
}

- (void)readResponseCookieWithJavaNetHttpURLConnection:(JavaNetHttpURLConnection *)connection {
  if (connection != nil) {
    NSString *headerName = nil;
    NSString *headerValue = nil;
    for (jint i = 0; (headerName = [connection getHeaderFieldKeyWithInt:i]) != nil; i++) {
      if ([@"Set-Cookie" isEqual:headerName]) {
        headerValue = [connection getHeaderFieldWithInt:i];
      }
    }
    if (headerValue != nil) {
      cookie_ = headerValue;
    }
  }
}

- (void)addParameterWithNSString:(NSString *)name
                          withId:(id)value
                 withJavaUtilMap:(id<JavaUtilMap>)map {
  if (value != nil && [value isKindOfClass:[NSString class]]) {
    @try {
      value = JavaNetURLEncoder_encodeWithNSString_withNSString_((NSString *) check_class_cast(value, [NSString class]), charset_);
    }
    @catch (JavaIoUnsupportedEncodingException *e) {
      return;
    }
  }
  (void) [((id<JavaUtilMap>) nil_chk(map)) putWithId:name withId:value];
}

- (void)addUrlParameterWithNSString:(NSString *)name
                             withId:(id)value {
  [self addParameterWithNSString:name withId:value withJavaUtilMap:urlParamMap_];
}

- (void)addParameterWithNSString:(NSString *)name
                          withId:(id)value {
  [self addParameterWithNSString:name withId:value withJavaUtilMap:paramMap_];
}

- (NSString *)readStreamWithJavaIoInputStream:(JavaIoInputStream *)stream {
  return MobilCallImpl_readStreamWithJavaIoInputStream_(self, stream);
}

- (void)setJsonPayloadWithNSString:(NSString *)json {
  self->jsonPayload_ = json;
}

- (void)writePostBodyWithNSString:(NSString *)bodyStr {
  MobilCallImpl_writePostBodyWithNSString_(self, bodyStr);
}

- (void)setParamMapWithJavaUtilMap:(id<JavaUtilMap>)paramMap {
  self->paramMap_ = paramMap;
}

- (NSString *)buildUrlParamsWithJavaUtilMap:(id<JavaUtilMap>)paramMap {
  return MobilCallImpl_buildUrlParamsWithJavaUtilMap_(self, paramMap);
}

+ (NSString *)getParametersStrWithJavaUtilMap:(id<JavaUtilMap>)paramMap {
  return MobilCallImpl_getParametersStrWithJavaUtilMap_(paramMap);
}

- (jboolean)processServerResponseWithBoolean:(jboolean)isLastCall {
  if ([self isErrorWithInt:responseStatus_]) {
    if (isLastCall) {
      [self finishWithErrorWithJavaLangException:nil];
    }
    return false;
  }
  else {
    [self finishWithSuccess];
    return true;
  }
}

- (void)finishWithErrorWithJavaLangException:(JavaLangException *)e {
  [((id<MobilCallBack>) nil_chk(callBack_)) onErrorWithJavaLangThrowable:e withJavaLangInteger:JavaLangInteger_valueOfWithInt_(responseStatus_) withNSString:responseContent_];
}

- (void)finishWithSuccess {
  [((id<MobilCallBack>) nil_chk(callBack_)) onCallBackWithNSString:responseContent_];
}

+ (NSString *)actualUrlWithNSString:(NSString *)url
                       withNSString:(NSString *)paramString {
  return MobilCallImpl_actualUrlWithNSString_withNSString_(url, paramString);
}

+ (NSString *)concatUrlWithNSString:(NSString *)url1
                       withNSString:(NSString *)url2 {
  return MobilCallImpl_concatUrlWithNSString_withNSString_(url1, url2);
}

- (void)beforeConnect {
}

- (void)prepareAndConnect {
  urlParamString_ = @"";
  NSString *rootUrl = MobilCallImpl_concatUrlWithNSString_withNSString_(baseUrl_, relativeUrl_);
  if ([@"get" equalsIgnoreCase:method_]) {
    urlParamString_ = MobilCallImpl_buildUrlParamsWithJavaUtilMap_(self, urlParamMap_);
    url_ = new_JavaNetURL_initWithNSString_(MobilCallImpl_actualUrlWithNSString_withNSString_(rootUrl, urlParamString_));
    connection_ = (JavaNetHttpURLConnection *) check_class_cast([url_ openConnection], [JavaNetHttpURLConnection class]);
    [((JavaNetHttpURLConnection *) nil_chk(connection_)) setRequestMethodWithNSString:method_];
    [connection_ setRequestPropertyWithNSString:@"Accept-Charset" withNSString:charset_];
    MobilCallImpl_specHeaders(self);
    [self beforeConnect];
    [self connect];
  }
  else if ([@"post" equalsIgnoreCase:method_]) {
    urlParamString_ = MobilCallImpl_buildUrlParamsWithJavaUtilMap_(self, urlParamMap_);
    url_ = new_JavaNetURL_initWithNSString_(MobilCallImpl_actualUrlWithNSString_withNSString_(rootUrl, urlParamString_));
    connection_ = (JavaNetHttpURLConnection *) check_class_cast([url_ openConnection], [JavaNetHttpURLConnection class]);
    [((JavaNetHttpURLConnection *) nil_chk(connection_)) setRequestMethodWithNSString:method_];
    [connection_ setDoOutputWithBoolean:true];
    [connection_ setRequestPropertyWithNSString:@"Accept-Charset" withNSString:charset_];
    [self setHeaderWithNSString:@"Cookie" withNSString:cookie_];
    MobilCallImpl_specHeaders(self);
    if (jsonPayload_ != nil) {
      [connection_ setRequestPropertyWithNSString:@"Content-Type" withNSString:@"application/json"];
      [connection_ setRequestPropertyWithNSString:@"Accept" withNSString:@"application/json"];
      [self beforeConnect];
      MobilCallImpl_writePostBodyWithNSString_(self, jsonPayload_);
    }
    else {
      [connection_ setRequestPropertyWithNSString:@"Content-Type" withNSString:@"application/x-www-form-urlencoded"];
      [self beforeConnect];
      MobilCallImpl_writePostBodyWithNSString_(self, MobilCallImpl_buildUrlParamsWithJavaUtilMap_(self, paramMap_));
    }
  }
}

- (void)remoteCallMakeWithBoolean:(jboolean)isBlocking {
  if (isBlocking) {
    [self remoteCallMakeAndWait];
  }
  else {
    [self remoteCallMakeOnNewThread];
  }
}

- (void)remoteCallMakeAndWait {
  [self prepareAndConnect];
  [self consumeResponse];
  [self processServerResponseWithBoolean:true];
}

- (void)remoteCallMakeOnNewThread {
  MobilCallImpl_MobilCallThreadImpl *at = new_MobilCallImpl_MobilCallThreadImpl_initWithMobilCallBack_withMobilCallImpl_(callBack_, self);
  [at start];
}

- (id<JavaUtilList>)getThrowableList {
  return throwableList_;
}

- (void)clearThrowableList {
  [((id<JavaUtilList>) nil_chk(throwableList_)) clear];
}

J2OBJC_IGNORE_DESIGNATED_BEGIN
- (instancetype)init {
  MobilCallImpl_init(self);
  return self;
}
J2OBJC_IGNORE_DESIGNATED_END

@end

void MobilCallImpl_specHeaders(MobilCallImpl *self) {
  if ([((id<JavaUtilMap>) nil_chk(self->httpHeaders_)) size] > 0) {
    id<JavaUtilSet> set = [self->httpHeaders_ keySet];
    for (NSString * __strong hname in nil_chk(set)) {
      NSString *value = [self->httpHeaders_ getWithId:hname];
      if (!MobilCallImpl_isEmptyWithNSString_(value)) {
        [self setHeaderWithNSString:hname withNSString:value];
      }
    }
  }
}

jboolean MobilCallImpl_isEmptyWithNSString_(NSString *s1) {
  MobilCallImpl_initialize();
  if (s1 == nil || [@"" isEqual:[s1 trim]]) {
    return true;
  }
  return false;
}

NSString *MobilCallImpl_readStreamWithJavaIoInputStream_(MobilCallImpl *self, JavaIoInputStream *stream) {
  JavaIoInputStreamReader *isr = new_JavaIoInputStreamReader_initWithJavaIoInputStream_(stream);
  JavaIoBufferedReader *reader = new_JavaIoBufferedReader_initWithJavaIoReader_(isr);
  self->stringBuilder_ = new_JavaLangStringBuilder_init();
  NSString *line = nil;
  while ((line = [reader readLine]) != nil) {
    (void) [self->stringBuilder_ appendWithNSString:line];
    (void) [self->stringBuilder_ appendWithNSString:@"\n"];
  }
  [reader close];
  return [self->stringBuilder_ description];
}

void MobilCallImpl_writePostBodyWithNSString_(MobilCallImpl *self, NSString *bodyStr) {
  if ([((NSString *) nil_chk(self->method_)) equalsIgnoreCase:@"POST"]) {
    self->outputStream_ = [((JavaNetHttpURLConnection *) nil_chk(self->connection_)) getOutputStream];
    [((JavaIoOutputStream *) nil_chk(self->outputStream_)) writeWithByteArray:[((NSString *) nil_chk(bodyStr)) getBytes]];
    [self->outputStream_ flush];
    [self->outputStream_ close];
  }
}

NSString *MobilCallImpl_buildUrlParamsWithJavaUtilMap_(MobilCallImpl *self, id<JavaUtilMap> paramMap) {
  return MobilCallImpl_getParametersStrWithJavaUtilMap_(paramMap);
}

NSString *MobilCallImpl_getParametersStrWithJavaUtilMap_(id<JavaUtilMap> paramMap) {
  MobilCallImpl_initialize();
  if ([((id<JavaUtilMap>) nil_chk(paramMap)) size] > 0) {
    JavaLangStringBuilder *buf = new_JavaLangStringBuilder_init();
    id<JavaUtilSet> keys = [paramMap keySet];
    for (NSString * __strong key in nil_chk(keys)) {
      (void) [buf appendWithNSString:@"&"];
      (void) [((JavaLangStringBuilder *) nil_chk([((JavaLangStringBuilder *) nil_chk([buf appendWithNSString:key])) appendWithNSString:@"="])) appendWithId:[paramMap getWithId:key]];
    }
    return [buf description];
  }
  return @"";
}

NSString *MobilCallImpl_actualUrlWithNSString_withNSString_(NSString *url, NSString *paramString) {
  MobilCallImpl_initialize();
  JavaLangStringBuilder *sb = new_JavaLangStringBuilder_initWithNSString_(url);
  if (!MobilCallImpl_isEmptyWithNSString_(paramString)) {
    (void) [sb appendWithChar:'?'];
    if ([((NSString *) nil_chk(paramString)) hasPrefix:@"&"]) {
      (void) [sb appendWithNSString:[paramString substring:1]];
    }
    else {
      (void) [sb appendWithNSString:paramString];
    }
  }
  return [sb description];
}

NSString *MobilCallImpl_concatUrlWithNSString_withNSString_(NSString *url1, NSString *url2) {
  MobilCallImpl_initialize();
  if ([((NSString *) nil_chk(url1)) hasSuffix:@"/"]) {
    url1 = [url1 substring:0 endIndex:((jint) [url1 length]) - 1];
  }
  if ([((NSString *) nil_chk(url2)) hasPrefix:@"/"]) {
    url2 = [url2 substring:1];
  }
  return JreStrcat("$C$", url1, '/', url2);
}

void MobilCallImpl_init(MobilCallImpl *self) {
  (void) NSObject_init(self);
  self->baseUrl_ = @"";
  self->relativeUrl_ = @"";
  self->method_ = @"GET";
  self->name_ = @"default";
  self->paramMap_ = new_JavaUtilHashMap_init();
  self->urlParamMap_ = new_JavaUtilHashMap_init();
  self->charset_ = @"UTF-8";
  self->cookie_ = nil;
  self->httpHeaders_ = new_JavaUtilHashMap_init();
}

MobilCallImpl *new_MobilCallImpl_init() {
  MobilCallImpl *self = [MobilCallImpl alloc];
  MobilCallImpl_init(self);
  return self;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(MobilCallImpl)

@implementation MobilCallImpl_MobilCallThreadImpl

- (instancetype)initWithMobilCallBack:(id<MobilCallBack>)callback
                    withMobilCallImpl:(MobilCallImpl *)netCall {
  MobilCallImpl_MobilCallThreadImpl_initWithMobilCallBack_withMobilCallImpl_(self, callback, netCall);
  return self;
}

- (void)run {
  @try {
    [((MobilCallImpl *) nil_chk(netCall_)) remoteCallMakeAndWait];
  }
  @catch (JavaLangThrowable *t) {
    [((JavaLangThrowable *) nil_chk(t)) printStackTrace];
    if (callback_ != nil) {
      [callback_ onErrorWithJavaLangThrowable:t withJavaLangInteger:nil withNSString:nil];
    }
  }
}

@end

void MobilCallImpl_MobilCallThreadImpl_initWithMobilCallBack_withMobilCallImpl_(MobilCallImpl_MobilCallThreadImpl *self, id<MobilCallBack> callback, MobilCallImpl *netCall) {
  (void) JavaLangThread_init(self);
  self->tryCount_ = 0;
  self->callback_ = callback;
  self->netCall_ = netCall;
}

MobilCallImpl_MobilCallThreadImpl *new_MobilCallImpl_MobilCallThreadImpl_initWithMobilCallBack_withMobilCallImpl_(id<MobilCallBack> callback, MobilCallImpl *netCall) {
  MobilCallImpl_MobilCallThreadImpl *self = [MobilCallImpl_MobilCallThreadImpl alloc];
  MobilCallImpl_MobilCallThreadImpl_initWithMobilCallBack_withMobilCallImpl_(self, callback, netCall);
  return self;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(MobilCallImpl_MobilCallThreadImpl)