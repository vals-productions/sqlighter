//
//  AppDelegate.m
//  ios-demo-prj
//
//  Created by Vlad Sayenko on 8/27/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "AppDelegate.h"
#import "Bootstrap.h"
#import "com/vals/a2ios/sqlighter/impl/SQLighterDbImpl.h"
#import "com/vals/a2ios/sqlighter/intf/SQLighterDb.h"

#import "com/vals/a2ios/mobilighter/intf/Mobilighter.h"
#import "com/vals/a2ios/mobilighter/impl/MobilighterImpl.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    /**
     * Database initialization steps. Similar
     * to Android with minor differences.
     */
    Bootstrap *b = [Bootstrap getInstance];
    SQLighterDbImpl *db = [[SQLighterDbImpl alloc] init];
    [db setDbNameWithNSString: @"sqlite.sqlite"];
    
    if (![db isDbFileDeployed]) {
        NSLog(@"DB file is not deployed");
    } else {
        NSLog(@"DB file is deployed");
    }
    
    [db setOverwriteDbWithBoolean:true];
    [db deployDbOnce];
    [db openIfClosed];
    [b setSqLighterDbWithSQLighterDb:db];
    
    MobilighterImpl *m = [[MobilighterImpl alloc] init];
    
    [b setMobilighterWithMobilighter: m];
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
