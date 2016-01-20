//
//  MobilighterImpl.m
//  mi
//
//  Created by Vlad Sayenko on 8/17/15.
//  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
//

#import "com/vals/a2ios/mobilighter/impl/MobilighterImpl.h"
#import "com/vals/a2ios/mobilighter/intf/MobilAction.h"
#import <UIKit/UIAlertView.h>
#import <UIKit/UILabel.h>
#import <UIKit/UIButton.h>
#import <UIKit/UIAlertController.h>
#import <UIKit/UIKit.h>
#import "java/util/Date.h"

@implementation MobilighterImpl

@synthesize contextController, dateFormatter; //, dateTimeFormatter; //, buttonListenerArray;

- (id) init {
    self = [super init];
    if ( self ) {
        self.dateFormatter = [[NSDateFormatter alloc] init];
    }
    return self;
}

- (void)notifyTableDataRefreshWithBaseListCtrl:(id) ctrl withId:(id)tableObject {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([tableObject isKindOfClass: [UITableView class]]) {
            UITableView *tv = (UITableView*)tableObject;
            [tv reloadData];
        }
    });
}

- (NSString *)dateToStringWithId:(id)date withNSString: (NSString*) pattern {
    if (date != nil && [date isKindOfClass: [NSDate class]]) {
         NSDate *d = (NSDate*) date;
        [self.dateFormatter setDateFormat:pattern];
        return [self.dateFormatter stringFromDate: d];
    } else if (date != nil && [date isKindOfClass: [JavaUtilDate class]]) {
        JavaUtilDate *d = (JavaUtilDate*) date;
        NSDate *nsd = [self nsDate:d];
        [self.dateFormatter setDateFormat:pattern];
        return [self.dateFormatter stringFromDate: nsd];
    }
    return @"";
}
                                                
-(NSDate*) nsDate: (JavaUtilDate*) jud {
    NSDate *nsd = [NSDate dateWithTimeIntervalSince1970: [jud getTime] / 1000];
    return nsd;
}

- (void)showOkDialogWithNSString: (NSString *)title withNSString:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alert =   [UIAlertController
                                      alertControllerWithTitle: title
                                      message: message
                                      preferredStyle: UIAlertControllerStyleAlert];
        UIAlertAction *ok = [UIAlertAction
                             actionWithTitle: @"Ok"
                             style: UIAlertActionStyleDefault
                             handler: nil];
        [alert addAction:ok];
        
        [contextController presentViewController:alert animated:YES completion:nil];
    });
}

- (void)showOkDialogWithNSString: (NSString *)title withNSString:(NSString *)message
                      withMobilAction:(id<MobilAction>)okAction {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alert =   [UIAlertController
                                      alertControllerWithTitle: title
                                      message: message
                                      preferredStyle: UIAlertControllerStyleAlert];
        UIAlertAction *ok = [UIAlertAction
                             actionWithTitle: @"Ok"
                             style: UIAlertActionStyleDefault
                             handler:^(UIAlertAction * action) {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                 [okAction onActionWithId:nil];
                             }];
        [alert addAction:ok];
        [contextController presentViewController:alert animated:YES completion:nil];
    });
}

- (void)showConfirmDialogWithNSString: (NSString *)title
                      withNSString:(NSString *)message
                      withMobilAction:(id<MobilAction>)yesAction
                      withMobilAction:(id<MobilAction>)noAction {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alert =   [UIAlertController
                                      alertControllerWithTitle: title
                                      message: message
                                      preferredStyle: UIAlertControllerStyleAlert];
        UIAlertAction *ok = [UIAlertAction
                             actionWithTitle: @"Ok"
                             style: UIAlertActionStyleDefault
                             handler:^(UIAlertAction * action) {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                 [yesAction onActionWithId:nil];
                             }];
        UIAlertAction *no = [UIAlertAction
                             actionWithTitle: @"Ok"
                             style: UIAlertActionStyleDefault
                             handler:^(UIAlertAction * action) {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                 [noAction onActionWithId:nil];
                             }];
        [alert addAction:ok];
        [alert addAction:no];
        [contextController presentViewController:alert animated:YES completion:nil];
    });
}

- (void)setContextWithId:(id)context {
    self.contextController = (UIViewController*)context;
}

- (void)setTextWithId:(id)textWidget withNSString:(NSString *)text {
    if ([textWidget isKindOfClass: [UITextField class]]) {
        UITextField *tf = (UITextField*)textWidget;
        tf.text = text;
    } else if ([textWidget isKindOfClass: [UILabel class]]) {
        UILabel *l = (UILabel*)textWidget;
        l.text = text;
    } else if ([textWidget isKindOfClass: [UIButton class]]) {
        UIButton *l = (UIButton*)textWidget;
        [l setTitle: text forState:UIControlStateNormal];
    } else if ([textWidget isKindOfClass: [UITextView class]]) {
        UITextView *l = (UITextView*) textWidget;
        [l setText: text];
    }
}

- (NSString *)getTextWithId:(id)textWidget {
    if ([textWidget isKindOfClass: [UITextField class]]) {
        UITextField *tf = (UITextField*)textWidget;
        return tf.text;
    }
    return nil;
}

-(void) addActionListenerWithId: (id) widget withMobilAction: (id<MobilAction>)command {
        if ([widget isKindOfClass: [UIButton class]]) {
            UIButton *b = (UIButton*)widget;
            [b addTarget:command action:@selector(onActionWithId:) forControlEvents:UIControlEventTouchDown];
        } else if ([widget isKindOfClass: [UIBarButtonItem class]]) {
            UIBarButtonItem *b = (UIBarButtonItem*)widget;
            b.target = command;
            b.action = @selector(onActionWithId:);
        }
}

- (void)setPlaceholderWithId:(id)textWidget withNSString:(NSString *)text {
    if ([textWidget isKindOfClass: [UITextField class]]) {
        UITextField *tf = (UITextField*)textWidget;
        tf.placeholder = text;
    }
}

- (void)hideWithId:(id)widget {
     if ([widget isKindOfClass: [UIView class]]) {
         UIView *o = (UIView*)widget;
         o.hidden = TRUE;
     }
}

- (void)showWithId:(id)widget {
    if ([widget isKindOfClass: [UIView class]]) {
        UIView *o = (UIView*)widget;
        o.hidden = FALSE;
    }
}

@end
