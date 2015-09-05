//
//  ios_demo_prjTests.m
//  ios-demo-prjTests
//
//  Created by Vlad Sayenko on 8/27/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

@interface ios_demo_prjTests : XCTestCase

@end

@implementation ios_demo_prjTests

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    // This is an example of a functional test case.
    XCTAssert(YES, @"Pass");
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
