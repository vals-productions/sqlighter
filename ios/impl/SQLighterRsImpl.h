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
#import "SQLighterDbImpl.h"

@interface SQLighterRsImpl : NSObject<SQLighterRs> {
//    sqlite3_stmt *stmt;
}

@property sqlite3_stmt *stmt;
@property (nonatomic, retain) SQLighterDbImpl *db;

-(void) setStatement: (sqlite3_stmt *) statement;

- (BOOL)hasNext;

- (NSNumber*)getDoubleWithInt:(int)index;

- (NSNumber*)getLongWithInt:(int)index;

- (NSString*)getStringWithInt:(int)index;

- (IOSByteArray*)getBlobWithInt:(int)index;

- (NSData*) getBlobAtIndex: (int) index;

- (NSNumber*)getIntWithInt:(int)index;

- (void)close;

@end
