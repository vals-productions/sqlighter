//
//  SQLighterDb.m
//  mi
//
//  Created by Vlad Sayenko on 8/21/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "SQLighterDbImpl.h"
#import "SQLighterRsImpl.h"
#import "IOSPrimitiveArray.h"

@implementation SQLighterDbImpl

@synthesize dbName, replaceDatabase, database, /*stmt,*/ parameterArray;

-(id) init {
    if( self = [super init]) {
        parameterArray = [NSMutableArray arrayWithCapacity:0];
        isOpen = NO;
        isCopied = NO;
    }
    return self;
}

- (void)setDbPathWithNSString:(NSString *)path {
    // not used in iOS
}

- (void)setDbNameWithNSString:(NSString *)name {
    self.dbName = name;
}

- (void)setOverwriteDbWithBoolean:(jboolean)isOverwrite {
    self.replaceDatabase = isOverwrite;
}

-(sqlite3_stmt *) prepareStatementWithSql: (NSString *) sqlString  {
    sqlite3_stmt *statement;
    if (sqlite3_prepare_v2(database, [sqlString  UTF8String], -1, &statement, NULL) != SQLITE_OK) {
        @throw [NSException exceptionWithName: @"Database prepare statement error"
                            reason: [NSString stringWithFormat:
                                                            @"Service: Database SQL Error: '%s'.",
                                                            sqlite3_errmsg(database)]
                userInfo: nil];
    }
    self.lastPreparedStmt = statement;
    return statement;
}

- (id<SQLighterRs>)executeSelectWithNSString:(NSString *)selectQuery {
    sqlite3_stmt *statement = [self prepareStatementWithSql: selectQuery];
    [self bindParameters: parameterArray];
    SQLighterRsImpl *rs = [[SQLighterRsImpl alloc] init];
    rs.stmt = statement;
    rs.db = self;
    [parameterArray removeAllObjects];
    return rs;
}

- (void)executeChangeWithNSString:(NSString *) makeCmangeQuery {
    sqlite3_stmt *statement = [self prepareStatementWithSql: makeCmangeQuery];
    [self bindParameters: parameterArray];
    code = sqlite3_step(statement);
    [self closeStmt: statement];
}

-(void) closeStmt: (sqlite3_stmt *) statement {
    if (sqlite3_finalize(statement) == SQLITE_ERROR) {
        @throw [NSException exceptionWithName: @"Database finalize statement error"
                                                   reason: [NSString stringWithFormat:
                                                            @"Service: Database Error: '%s'.",
                                                            sqlite3_errmsg(database)] userInfo: nil];
    }
}

-(void) bindParameters: (NSMutableArray*) parameters {
    if (parameters == nil) {
        return;
    }
    for (int par = 1; par <= [parameters count]; par++) {
        id o = [parameters objectAtIndex: par - 1];
        if (o == nil || [o isKindOfClass: [NSNull class]]) {
            [self bindNullAtIndex: par];
        } else if([o isKindOfClass: [NSString class]]) {
            [self bindString: o atIndex: par];
        } else if ([o isKindOfClass: [NSNumber class]]) {
            NSNumber *num = [parameters objectAtIndex: par - 1];
            if (strcmp([num objCType], @encode(int)) == 0 ||
                strcmp([num objCType], @encode(long)) == 0 ||
                strcmp([num objCType], @encode(unsigned int)) == 0 ||
                strcmp([num objCType], @encode(unsigned long)) == 0 ||
                strcmp([num objCType], @encode(unsigned long long)) == 0 ||
                strcmp([num objCType], @encode(unsigned short)) == 0 ||
                strcmp([num objCType], @encode(short)) == 0 ||
                strcmp([num objCType], @encode(long long)) == 0
                ) {
                [self bindInt:[num intValue] atIndex: par];
            } else if (strcmp([num objCType], @encode(float)) == 0 ||
                       strcmp([num objCType], @encode(double)) == 0) {
                [self bindDouble: [num doubleValue] atIndex: par];
            }
        } else if ([o isKindOfClass: [NSDate class]]) {
            [self bindDate:o atIndex: par];
        } else if ([o isKindOfClass: [NSData class]]) {
            [self bindBlob: o atIndex: par];
        } else {
            [self bindNullAtIndex: par];
        }
    }
    [parameters removeAllObjects];
}

- (void)setContextWithId:(id)context {
    // not used in iOS.
}

-(void) copyDbOnce {
    if (!isCopied) {
        isCopied = YES;
    } else {
        return;
    }
    BOOL success;
    NSError *error;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    /**
     * Documents directory under application's directory
     */
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentDirectory = [paths objectAtIndex:0];
    /**
     * Full path\file to DB file
     */
    NSString *writableDbPath = [documentDirectory stringByAppendingPathComponent: dbName];
    if (replaceDatabase == YES) {
        NSLog(@"Service: Attempt to replace existing DB.");
        if (![fileManager removeItemAtPath:writableDbPath error:&error]) {
            NSLog(@"Service: Error deleting file: %@", [error localizedDescription]);
        }
    }
    success = [fileManager fileExistsAtPath:writableDbPath];
    if (!success) {
        /**
         * file not found - was deleted, or didn't exist before.
         */
        /**
         * Path to project's resources
         */
        NSString *defaultDbPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent: dbName];
        NSLog(@"\nSource db path: \n%@\nDest db path: \n%@", defaultDbPath, writableDbPath);
        /**
         * Copy DB file from project's resource path to application document's directory.
         */
        success = [fileManager copyItemAtPath:defaultDbPath toPath:writableDbPath error:&error];
        if (!success) {
            NSLog(@"Could not copy database");
            @throw [NSException exceptionWithName: @"Could not copy database"
                                reason: [NSString stringWithFormat: @"Failed to create database with message: '%@'", [error localizedDescription]]
                                userInfo: nil];
        }
    }
}

- (void)openIfClosed {
    if(!isOpen) {
        isOpen = YES;
    } else {
        return;
    }
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *path = [documentsDirectory stringByAppendingPathComponent: dbName];
    // Open the database. The database was prepared outside the application.
    if (sqlite3_open([path UTF8String], &database) != SQLITE_OK) {
        // Even though the open failed, call close to properly clean up resources.
        //[paths release];
        sqlite3_close(database);
        @throw [NSException exceptionWithName: @"Could not open database"
                                       reason:  [NSString stringWithFormat:
                                                 @"Service: Database Error: '%s'.",
                                                 sqlite3_errmsg(database)]
                                     userInfo: nil];
    }
}
-(void) addParamWithNSString: (NSString*) str {
    [parameterArray addObject: str];
}
-(void) addParamWithDouble: (double) par {
    [parameterArray addObject: [NSNumber numberWithDouble: par]];
}
-(void) addParamWithNull {
    [parameterArray addObject: [NSNull null]];
}
-(void) addParamWithInt: (int) par {
    [parameterArray addObject: [NSNumber numberWithInt:par]];
}
-(void) addParamWithLong: (long) par {
    [parameterArray addObject: [NSNumber numberWithLong: par ]];
}
-(void) addParamWithDate: (NSDate*) date atIndex: (int) idx {
    [self addParamWithDouble: [date timeIntervalSince1970]];
}
-(void) addParamWithBlob: (NSData*) data {
    [parameterArray addObject: data];
}
- (void)addParamWithByteArray:(IOSByteArray *)blob {
    NSData *d = [NSData dataWithBytes:[blob buffer] length:[blob length]];
    [self addParamWithBlob: d];
}
- (void)addParamNull {
    [self addParamWithNull];
}
-(void) bindString: (NSString*) str  atIndex: (int) paramIdx {
        sqlite3_bind_text(self.lastPreparedStmt, paramIdx, [str UTF8String], -1, SQLITE_TRANSIENT);
}
-(void) bindDouble: (double) d atIndex: (int) paramIdx{
        sqlite3_bind_double(self.lastPreparedStmt, paramIdx, d );
}
-(void) bindNullAtIndex: (int) paramIdx {
        sqlite3_bind_null(self.lastPreparedStmt, paramIdx);
}
-(void) bindInt: (int) par atIndex: (int) paramIdx  {
        sqlite3_bind_int(self.lastPreparedStmt, paramIdx, par);
}
-(void) bindLong: (long) par  atIndex: (int) paramIdx{
        sqlite3_bind_int(self.lastPreparedStmt, paramIdx, (int)par);
}
-(void) bindDate: (NSDate*) date atIndex: (int) idx {
        [self bindDouble: [date timeIntervalSince1970] atIndex: idx];
}
-(void) bindBlob: (NSData*) data  atIndex: (int) paramIdx {
        sqlite3_bind_blob(self.lastPreparedStmt, paramIdx, [data bytes], (int)[data length], SQLITE_TRANSIENT);
}
-(BOOL) isNullAtIndex: (int) idx {
    const char *buffer = (char*)sqlite3_column_text(self.lastPreparedStmt, idx);
    int retVal = buffer == nil;
    return retVal;
}
@end
