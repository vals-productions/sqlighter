//
//  SQLighterDb.m
//
//  Created by Vlad Sayenko on 8/21/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "SQLighterDbImpl.h"
#import "SQLighterRsImpl.h"
#import "IOSPrimitiveArray.h"
#import "java/lang/Exception.h"
#import "java/util/Date.h"
#import "java/lang/Long.h"

@implementation SQLighterDbImpl

@synthesize dbName, replaceDatabase, database, parameterDictionary, isDateNamedColumn, dateColumnHint;

-(id) init {
    if( self = [super init]) {
        self.parameterDictionary = [NSMutableDictionary dictionary];
        isOpen = NO;
        isDeployed = NO;
        stmtOpenCnt = 0;
        stmtCloseCnt = 0;
        self.isDateNamedColumn = YES;
        self.dateFormatter = [[NSDateFormatter alloc] init];
        [self.dateFormatter setDateFormat:SQLighterDb_DATE_FORMAT_];
        self.dateColumnHint = SQLighterDb_DATE_HINT_;
    }
    return self;
}

-(int) threadsafe {
    return sqlite3_threadsafe();
}

-(void) analyzeReturnCodeForErrors: (int) returnCode {
    if (returnCode == SQLITE_OK ||
        returnCode == SQLITE_DONE ||
        returnCode == SQLITE_ROW ||
        returnCode == SQLITE_WARNING) {
        /**
         * These codes are, basically, positive execution diagnistics
         * SQLITE_WARNING - tbd...
         */
        return;
    }
    @throw [[JavaLangException alloc]
            initWithNSString:[NSString
            stringWithFormat: @"Database returned Error code: '%d'.", returnCode]];
}

- (jboolean)isDbFileDeployed {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentDirectory = [paths objectAtIndex:0];
    NSString *writableDbPath = [documentDirectory stringByAppendingPathComponent: dbName];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    return [fileManager fileExistsAtPath:writableDbPath];
}

- (void)setDbNameWithNSString:(NSString *)name {
    self.dbName = name;
}

- (void)setOverwriteDbWithBoolean:(jboolean)isOverwrite {
    self.replaceDatabase = isOverwrite;
}

-(sqlite3_stmt *) prepareStatementWithSql: (NSString *) sqlString  {
    @synchronized(self) {
        sqlite3_stmt *statement;
        if ((code = sqlite3_prepare_v2(database, [sqlString  UTF8String], -1, &statement, NULL)) != SQLITE_OK) {
            [self clearParameterArray];
            @throw [[JavaLangException alloc]
                    initWithNSString:[NSString stringWithFormat:
                       @"Database SQL Error: '%s'.", sqlite3_errmsg(database)]];
        }
        self.lastPreparedStmt = statement;
        stmtOpenCnt++;
        return statement;
    }
}

- (id<SQLighterRs>)executeSelectWithNSString:(NSString *)selectQuery {
    @synchronized(self) {
        @try {
            sqlite3_stmt *statement = [self prepareStatementWithSql: selectQuery];
            [self bindParameters: [self parameterArray]];
            SQLighterRsImpl *rs = [[SQLighterRsImpl alloc] init];
            rs.stmt = statement;
            rs.db = self;
            [self clearParameterArray];
            return rs;
        } @catch (JavaLangException *exception) {
            @throw exception;
        } @finally {
        }
    }
}

- (JavaLangLong *)executeChangeWithNSString:(NSString *) makeChangeQuery {
    @synchronized(self) {
        @try {
            JavaLangLong *resultInfo = nil;
            sqlite3_stmt *statement = [self prepareStatementWithSql: makeChangeQuery];
            [self bindParameters: [self parameterArray]];
            code = sqlite3_step(statement);
            NSString *alteredQuery = [self substringWithoutLeadingWhitespace: [makeChangeQuery lowercaseString]];
            if ([alteredQuery hasPrefix:@"insert"]) {
                sqlite_int64 rows = sqlite3_last_insert_rowid(database);
                resultInfo = [[JavaLangLong alloc]initWithLong: rows];
            } else if ([alteredQuery hasPrefix:@"update"] || [alteredQuery hasPrefix:@"delete"]) {
                int rows = sqlite3_changes(database);
                resultInfo = [[JavaLangLong alloc]initWithLong: (long)rows];
            } else {
                int rows = sqlite3_changes(database);
                resultInfo = [[JavaLangLong alloc]initWithLong: (long)rows];
            }
            [self closeStmt: statement];
            return resultInfo;
        } @catch (JavaLangException *exception) {
            @throw exception;
        } @finally {
        }
    }
}

-(void) closeStmt: (sqlite3_stmt *) statement {
    @synchronized(self) {
        stmtCloseCnt++;
        if((code = sqlite3_reset(statement)) != SQLITE_OK) {
            @throw [[JavaLangException alloc] initWithNSString:
                    [NSString stringWithFormat: @"Database SQL Error on reset stmt: '%s'.", sqlite3_errmsg(database)]];
        }
        if ((code = sqlite3_finalize(statement)) != SQLITE_OK) {
            @throw [[JavaLangException alloc] initWithNSString:
                    [NSString stringWithFormat: @"Database SQL Error on fin stmt: '%s'.", sqlite3_errmsg(database)]];
        }
    }
}

-(void) bindParameters: (NSMutableArray*) parameters {
    @synchronized(self) {
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
                    strcmp([num objCType], @encode(unsigned int)) == 0 ||
                    strcmp([num objCType], @encode(unsigned short)) == 0 ||
                    strcmp([num objCType], @encode(short)) == 0) {
                    [self bindInt:[num intValue] atIndex: par];
                }
                else if (strcmp([num objCType], @encode(long)) == 0 ||
                    strcmp([num objCType], @encode(long long)) == 0 ||
                    strcmp([num objCType], @encode(unsigned long)) == 0 ||
                    strcmp([num objCType], @encode(unsigned long long)) == 0) {
                    [self bindLong:[num longLongValue] atIndex: par];
                }
                else if (strcmp([num objCType], @encode(float)) == 0 ||
                           strcmp([num objCType], @encode(double)) == 0) {
                    [self bindDouble: [num doubleValue] atIndex: par];
                }
            } else if ([o isKindOfClass: [JavaUtilDate class]]) {
                [self bindJavaUtilDate:o atIndex: par];
            } else if ([o isKindOfClass: [NSData class]]) {
                [self bindBlob: o atIndex: par];
            } else {
                [self bindNullAtIndex: par];
            }
        }
        [self clearParameterArray];
    }
}

- (void)setIsDateNamedColumnWithBoolean:(jboolean)isDateNamedCol {
    self.isDateNamedColumn = isDateNamedCol;
}

- (void)setContextWithId:(id)context {
    // not used in iOS.
}

- (jboolean)deleteDBFile {
    NSError *error;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *dbFile = [self getFilePath];
    isDeployed = NO;
    return [fileManager removeItemAtPath:dbFile error:&error];
}

-(NSString*) getFilePath {
    /**
     * Documents directory under application's directory
     */
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentDirectory = [paths objectAtIndex:0];
    /**
     * Full path\file to DB file
     */
    NSString *writableDbPath = [documentDirectory stringByAppendingPathComponent: dbName];
    return writableDbPath;
}

-(void) copyDbOnce {
    [self deployDbOnce];
}

-(void) deployDbOnce {
    @synchronized(self) {
        if (!isDeployed) {
            isDeployed = YES;
        } else {
            return;
        }
        BOOL success;
        NSError *error;
        NSFileManager *fileManager = [NSFileManager defaultManager];
        
        NSString *writableDbPath = [self getFilePath];
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
                @throw [[JavaLangException alloc] initWithNSString:
                        [NSString stringWithFormat: @"Failed to create database with message: '%@'", [error localizedDescription]]];
            }
        }
    }
}

- (void)openIfClosed {
    @synchronized(self) {
        if(!isOpen) {
            NSString *path = [self getFilePath]; // [documentsDirectory stringByAppendingPathComponent: dbName];
            // Open the database.
            if (sqlite3_open([path UTF8String], &database) != SQLITE_OK) {
                // Even though the open failed, call close to properly clean up resources.
                // [paths release];
                sqlite3_close(database);
                @throw [[JavaLangException alloc] initWithNSString:
                        [NSString stringWithFormat: @"Database Error: '%s'.", sqlite3_errmsg(database)]];
            }
            isOpen = YES;
        }
    }
}

- (void)addParamWithJavaUtilDate:(JavaUtilDate *)date {
    if(date == nil) {
        [self addParamWithNull];
        return;
    }
    [[self parameterArray] addObject: date];
}

-(void) bindJavaUtilDate: (JavaUtilDate *) date atIndex: (int) paramIdx {
    jlong t = [date getTime];
    NSDate *dt = [NSDate dateWithTimeIntervalSince1970:t/1000];
    NSString *dateStr = [self.dateFormatter stringFromDate:dt];
    [self bindString:dateStr atIndex: paramIdx];
}

- (void)addParamObjWithId:(id)o {
    if(o == nil) {
        [self addParamWithNull];
        return;
    }
    [[self parameterArray] addObject: o];
}

-(void) addParamWithNSString: (NSString*) str {
    if(str == nil) {
        [self addParamWithNull];
        return;
    }
    [[self parameterArray] addObject: str];
}

-(void) addParamWithDouble: (double) par {
    [[self parameterArray] addObject: [NSNumber numberWithDouble: par]];
}

-(void) addParamWithNull {
    [[self parameterArray] addObject: [NSNull null]];
}

-(void) addParamWithInt: (int) par {
    [[self parameterArray] addObject: [NSNumber numberWithInt:par]];
}

-(void) addParamWithLong: (jlong) par {
    [[self parameterArray] addObject: [NSNumber numberWithLong: (long)par]];
}

-(void) addParamWithBlob: (NSData*) data {
    if(data == nil) {
        [self addParamWithNull];
        return;
    }
    [[self parameterArray] addObject: data];
}

- (void)addParamWithByteArray:(IOSByteArray *)blob {
    if(blob == nil) {
        [self addParamWithNull];
        return;
    }
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
        sqlite3_bind_int64(self.lastPreparedStmt, paramIdx, (sqlite3_int64)par);
}

-(void) bindBlob: (NSData*) data  atIndex: (int) paramIdx {
        sqlite3_bind_blob(self.lastPreparedStmt, paramIdx, [data bytes], (int)[data length], SQLITE_TRANSIENT);
}

-(jboolean) isNullAtIndex: (int) idx {
    const char *buffer = (char*)sqlite3_column_text(self.lastPreparedStmt, idx);
    int retVal = buffer == nil;
    return retVal;
}

- (void)beginTransaction {
    [self executeChangeWithNSString:@"begin transaction"];
}

- (void)commitTransaction {
    [self executeChangeWithNSString:@"commit"];
}

- (void)rollbackTransaction {
    [self executeChangeWithNSString:@"rollback"];
}

- (void)close {
    @synchronized(self) {
        if(isOpen == YES) {
            int rc = sqlite3_close(database);
            [self analyzeReturnCodeForErrors:rc];
            isOpen = NO;
        }
    }
}

-(NSString *) substringWithoutLeadingWhitespace: (NSString *) string {
    const char *cStringValue = [string UTF8String];
    int i = 0;
    for (; cStringValue[i] != '\0' && isspace(cStringValue[i]); i++);
    return [string substringFromIndex:i];
}

-(NSString *) threadId {
    NSString *strId = [NSString stringWithFormat:@"%@", [NSThread currentThread]];
    return strId;
}

-(NSMutableArray*) parameterArray {
    @synchronized(self) {
        NSString *threadId = [self threadId];
        NSMutableArray *arr = [self.parameterDictionary objectForKey: threadId];
        if(arr == nil) {
            arr = [NSMutableArray arrayWithCapacity:3];
            [self.parameterDictionary setObject:arr forKey:threadId];
        }
        return arr;
    }
}

- (void) clearParameterArray {
    @synchronized(self) {
        NSString *threadId = [self threadId];
        NSMutableArray* pd = [parameterDictionary objectForKey: threadId];
        if(pd != nil) {
            [self.parameterDictionary removeObjectForKey: threadId];
        }
    }
}

- (JavaUtilDate *)getDateWithoutMillisWithJavaUtilDate:(JavaUtilDate *)date {
    if(date != nil) {
        [date setTimeWithLong:[date getTime] / 1000];
        [date setTimeWithLong:[date getTime] * 1000];
        return date;
    }
    return nil;
}

-(jlong) getStatementBalance {
    return stmtOpenCnt - stmtCloseCnt;
}

- (void)setDbPathWithNSString:(NSString *)path {
}

- (void)setDateColumnNameHintWithNSString:(NSString *)hint {
    self.dateColumnHint = hint;
}

- (void)setTimeZoneWithNSString:(NSString *)timeZoneName {
    self.dateFormatter.timeZone = [NSTimeZone timeZoneWithName: timeZoneName];
}

- (void)setDateFormatStringWithNSString:(NSString *)dateFormatString {
    self.dateFormatter.dateFormat = dateFormatString;
}

@end
