//
//  MobilighterImpl.h
//  mi
//
//  Created by Vlad Sayenko on 8/17/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import <Foundation/Foundation.h>
// #import "com/vals/a2ios/mobilighter/impl/MobilighterImpl.h"
#import "com/vals/a2ios/mobilighter/intf/Mobilighter.h"
#import <UIKit/UIAlertView.h>

@interface MobilighterImpl : NSObject<Mobilighter, UIAlertViewDelegate> {
}

@property UIViewController *contextController;
@property NSDateFormatter *dateFormatter;

@end
