//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: ../../../../demo/andr-demo-prj/app/src/main/java/com/prod/vals/andr_demo_prj/DemoIntGetAdapter.java
//

#include "J2ObjC_header.h"

#pragma push_macro("INCLUDE_ALL_ComProdValsAndr_demo_prjDemoIntGetAdapter")
#ifdef RESTRICT_ComProdValsAndr_demo_prjDemoIntGetAdapter
#define INCLUDE_ALL_ComProdValsAndr_demo_prjDemoIntGetAdapter 0
#else
#define INCLUDE_ALL_ComProdValsAndr_demo_prjDemoIntGetAdapter 1
#endif
#undef RESTRICT_ComProdValsAndr_demo_prjDemoIntGetAdapter

#if !defined (DemoIntGetAdapter_) && (INCLUDE_ALL_ComProdValsAndr_demo_prjDemoIntGetAdapter || defined(INCLUDE_DemoIntGetAdapter))
#define DemoIntGetAdapter_

#define RESTRICT_ComValsA2iosAmfibianIntfAnAdapter 1
#define INCLUDE_AnAdapter 1
#include "com/vals/a2ios/amfibian/intf/AnAdapter.h"

@class IOSClass;
@protocol AnAttrib;

@interface DemoIntGetAdapter : NSObject < AnAdapter >

#pragma mark Public

- (instancetype)init;

- (id)convertWithAnAttrib:(id<AnAttrib>)attrib
                   withId:(id)value;

- (void)onWarningWithIOSClass:(IOSClass *)cluss
                 withNSString:(NSString *)attribName
                       withId:(id)value;

@end

J2OBJC_EMPTY_STATIC_INIT(DemoIntGetAdapter)

FOUNDATION_EXPORT void DemoIntGetAdapter_init(DemoIntGetAdapter *self);

FOUNDATION_EXPORT DemoIntGetAdapter *new_DemoIntGetAdapter_init() NS_RETURNS_RETAINED;

FOUNDATION_EXPORT DemoIntGetAdapter *create_DemoIntGetAdapter_init();

J2OBJC_TYPE_LITERAL_HEADER(DemoIntGetAdapter)

@compatibility_alias ComProdValsAndr_demo_prjDemoIntGetAdapter DemoIntGetAdapter;

#endif

#pragma pop_macro("INCLUDE_ALL_ComProdValsAndr_demo_prjDemoIntGetAdapter")
