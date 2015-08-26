//
//  SQLighterRsImpl.h
//  mi
//
//  Created by Vlad Sayenko on 8/22/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

#import "SQLighterRs.h"

@interface SQLighterRsImpl : NSObject<SQLighterRs> {
//    sqlite3_stmt *stmt;
//    sqlite3 *database;
}
@property sqlite3 *database;
@property sqlite3_stmt *stmt;

- (BOOL)hasNext;

- (NSNumber*)getDoubleWithInt:(jint)index;

- (NSNumber*)getLongWithInt:(jint)index;

- (NSString*)getStringWithInt:(jint)index;

- (IOSByteArray*)getBlobWithInt:(jint)index;

- (NSNumber*)getIntWithInt:(jint)index;

- (void)close;

@end
