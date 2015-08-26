//
//  SQLighterRsImpl.m
//  mi
//
//  Created by Vlad Sayenko on 8/22/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "SQLighterRsImpl.h"
#import "IOSPrimitiveArray.h"

@implementation SQLighterRsImpl

-(BOOL) hasNext {
    bool hasIt = sqlite3_step(self.stmt) == SQLITE_ROW;
    return hasIt;
}

-(BOOL) isNullWithInt: (int) idx {
    const char *buffer = (char*)sqlite3_column_text(self.stmt, idx);
    int retVal = buffer == nil;
    return retVal;
}

-(NSString*) getStringWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    const char *buffer = (char*)sqlite3_column_text(self.stmt, idx);
    NSString *str = [NSString stringWithUTF8String: buffer];
    return str;
}

-(char) getCharWithInt: (int) idx {
    char c;
    NSString *str = [self getStringWithInt:idx];
    c = [str characterAtIndex:0];
    return c;
}

-(NSNumber*) getIntWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    int v = sqlite3_column_int(self.stmt, idx);
    return  [NSNumber numberWithInt: v];
}

-(NSNumber*) getLongWithInt: (int) idx {
    return [self getIntWithInt:idx];
}

-(NSNumber*) getDoubleWithInt: (int) idx {
    if ([self isNullWithInt:idx]) {
        return nil;
    }
    double d = sqlite3_column_double(self.stmt, idx++);
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
    IOSByteArray *ba = [IOSByteArray arrayWithBytes: [ d bytes] count:(int)[d length]];
    return ba;
}

-(NSData*) getBlobAtIndex: (int) index {
    int length = sqlite3_column_bytes(self.stmt, index);
    NSData *data = [NSData dataWithBytes:sqlite3_column_blob(self.stmt, index) length:length];
    return data;
}

- (void)close {
    if (sqlite3_finalize(self.stmt) == SQLITE_ERROR) {
        @throw [NSException
            exceptionWithName: @"Database finalize statement error"
            reason: [NSString stringWithFormat: @"Service: Database Error: '%s'.",
            sqlite3_errmsg(self.database)]
            userInfo: nil];
    }
}

@end