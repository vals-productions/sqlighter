//
//  SQLighterRsImpl.m
//  mi
//
//  Created by Vlad Sayenko on 8/22/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "SQLighterRsImpl.h"
#import "IOSPrimitiveArray.h"
#import "java/lang/Double.h"
#import "java/lang/Long.h"
#import "java/lang/Integer.h"
#import "java/lang/Exception.h"
#import "java/util/Date.h"

@implementation SQLighterRsImpl

@synthesize db, stmt;

-(BOOL) hasNext {
    int code = sqlite3_step(stmt);
    [db analyzeReturnCodeForErrors: code];
    bool hasIt = (code == SQLITE_ROW);
    return hasIt;
}

-(jboolean) isNullWithInt: (int) idx {
    const char *buffer = (char*)sqlite3_column_text(stmt, idx);
    int retVal = buffer == nil;
    return retVal;
}

-(NSString*) getStringWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    const char *buffer = (char*)sqlite3_column_text(stmt, idx);
    NSString *str = [NSString stringWithUTF8String: buffer];
    return str;
}

-(char) getCharWithInt: (int) idx {
    char c;
    NSString *str = [self getStringWithInt:idx];
    c = [str characterAtIndex:0];
    return c;
}

-(JavaLangInteger*) getIntWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    int v = sqlite3_column_int(stmt, idx);
    return  [JavaLangInteger valueOfWithInt: v];
}

-(JavaLangLong*) getLongWithInt: (int) idx {
    return [JavaLangLong valueOfWithLong: [[self getIntWithInt:idx] longValue]];
}

-(JavaLangDouble*) getDoubleWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    double d = sqlite3_column_double(stmt, idx++);
    return [JavaLangDouble valueOfWithDouble:d];
}

-(NSNumber*) getNumberWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    double d = sqlite3_column_double(stmt, idx++);
    return [NSNumber numberWithDouble:d];
}

- (IOSByteArray *)getBlobWithInt:(jint)index {
    NSData *d = [self getBlobAtIndex:index];
    if(d == nil) {
        return nil;
    }
    IOSByteArray *ba = [IOSByteArray arrayWithBytes: [ d bytes] count:(int)[d length]];
    return ba;
}

-(NSData*) getBlobAtIndex: (int) index {
    int length = sqlite3_column_bytes(stmt, index);
    if(length == 0) {
        return nil;
    }
    NSData *data = [NSData dataWithBytes:sqlite3_column_blob(stmt, index) length:length];
    return data;
}

- (id)getObjectWithInt:(jint)index {
    int type = [self getColumnTypeWithInt: index];
    if (type == SQLITE_NULL) {
        return nil;
    } else if(type == SQLITE_INTEGER) {
        return [self getIntWithInt: index];
    } else if (type == SQLITE_FLOAT) {
        return [self getDoubleWithInt: index];
    } else if (type == SQLITE_BLOB) {
        return [self getBlobAtIndex: index];
    } else if (type == SQLITE_TEXT) {
        NSString *colName = [self getColumnNameWithInt:index];
        if (db.isDateNamedColumn == YES && colName != nil && db.dateColumnHint != nil &&
            [[colName lowercaseString] containsString: [db.dateColumnHint lowercaseString]]) {
            return [self getDateWithInt:index];
        }
        return [self getStringWithInt: index];
    }
    return nil;
}

- (JavaUtilDate *)getDateWithInt:(jint)index {
    NSDate *date = [self getNSDateWithInt:index];
    JavaUtilDate *jud = [[JavaUtilDate alloc] initWithLong: [date timeIntervalSince1970] * 1000];
    return jud;
}

- (NSDate*)getNSDateWithInt:(jint)index {
    NSString *dateStr = [self getStringWithInt:index];
    NSDate *date = [db.dateFormatter dateFromString:dateStr];
    return date;
}

- (jint)getColumnTypeWithInt:(jint)index {
    return sqlite3_column_type(stmt, index);
}

- (NSString *)getColumnNameWithInt:(jint)index {
    const char *buffer = (char*)sqlite3_column_name(stmt, index);
    NSString *str = [NSString stringWithUTF8String: buffer];
    return str;
}

- (void)close {
    [db closeStmt: stmt];
}

@end