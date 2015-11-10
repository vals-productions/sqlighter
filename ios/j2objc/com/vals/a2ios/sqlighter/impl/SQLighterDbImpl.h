//
//  SQLighterDb.h
//  mi
//
//  Created by Vlad Sayenko on 8/21/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "com/vals/a2ios/sqlighter/intf/SQLighterDb.h"
#import <sqlite3.h>

@interface SQLighterDbImpl : NSObject<SQLighterDb> {
    int code;
    BOOL isOpen;
    BOOL isCopied;
}

@property (nonatomic, retain) NSString *dbName;
@property (nonatomic, retain) NSMutableArray *parameterArray;
@property BOOL replaceDatabase;
@property BOOL isDateNamedColumn;
@property sqlite3 *database;
@property sqlite3_stmt *lastPreparedStmt;
@property NSDateFormatter *dateFormatter;

- (void) copyDbOnce;
- (void) setContextWithId:(id)context;
- (void) addParamWithNSString:(NSString *)s;
- (void) addParamWithDouble:(double)s;
- (void) addParamWithLong:(long)s;
- (void) addParamWithInt:(int)s;
- (id<SQLighterRs>)executeSelectWithNSString:(NSString *)selectQuery;
- (void) executeChangeWithNSString:(NSString *) makeCmangeQuery;

- (void) closeStmt: (sqlite3_stmt *) statement;

-(void) analyzeReturnCodeForErrors: (int) code;

@end
