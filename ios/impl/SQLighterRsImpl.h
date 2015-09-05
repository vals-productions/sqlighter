//
//  SQLighterRsImpl.h
//  mi
//
//  Created by Vlad Sayenko on 8/22/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

#import "com/vals/a2ios/sqlighter/intf/SQLighterRs.h"
#import "SQLighterDbImpl.h"

@interface SQLighterRsImpl : NSObject<SQLighterRs> {
}

@property sqlite3_stmt *stmt;
@property (nonatomic, retain) SQLighterDbImpl *db;

- (BOOL)hasNext;

- (NSNumber*)getDoubleWithInt:(int)index;

- (NSNumber*)getLongWithInt:(int)index;

- (NSString*)getStringWithInt:(int)index;

- (IOSByteArray*)getBlobWithInt:(int)index;

- (NSData*) getBlobAtIndex: (int) index;

- (NSNumber*)getIntWithInt:(int)index;

- (void)close;

@end
