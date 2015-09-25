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

@implementation SQLighterRsImpl

@synthesize db, stmt;

-(BOOL) hasNext {
    int code = sqlite3_step(stmt);
    [db analyzeReturnCodeForErrors: code];
    bool hasIt = (code == SQLITE_ROW);
    return hasIt;
}

-(BOOL) isNullWithInt: (int) idx {
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

-(NSDate*) getDateAtIndex: (int) idx {
    NSNumber *d = [self getDoubleWithInt: idx];
    if(d == nil) {
        return nil;
    }
    return [NSDate dateWithTimeIntervalSince1970: [d doubleValue]];
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

- (void)close {
    [db closeStmt: stmt];
}

@end