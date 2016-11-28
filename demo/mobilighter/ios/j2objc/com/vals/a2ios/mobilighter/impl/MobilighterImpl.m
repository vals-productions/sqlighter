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
        // [self.dateFormatter setDateFormat: Mobilighter_DATE_FORMAT_STR_];
    }
    return self;
}

void MobilighterImpl_init(MobilighterImpl *self) {
    (void) NSObject_init(self);
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
    } else if([textWidget isKindOfClass: [UIBarButtonItem class]]) {
        UIBarButtonItem *l = (UIBarButtonItem*) textWidget;
        [l setTitle:text];
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

- (id)getContext {
    return self.contextController;
}

- (jboolean)isOnWithId:(id)toggleButton {
    if ([toggleButton isKindOfClass: [UISwitch class]]) {
        UISwitch *o = (UISwitch*)toggleButton;
        return o.isOn;
    }
    return NO;
}

- (void)setOnWithId:(id)toggleButton
        withBoolean:(jboolean)isOn {
    if ([toggleButton isKindOfClass: [UISwitch class]]) {
        UISwitch *o = (UISwitch*)toggleButton;
        [o setOn:isOn animated: YES];
    }
}

- (NSString *)readFileWithNSString:(NSString *)fileName {
    NSString *jsonFileName = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent: fileName];
    NSError* error = nil;
    NSString *jsonString = [NSString stringWithContentsOfFile:jsonFileName encoding:NSUTF8StringEncoding error:&error];
    if (!error) {
        return jsonString;
    }
    return nil;
}

- (void)showWaitPopupWithNSString:(NSString *)title
                     withNSString:(NSString *)message {
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:@"Please wait\n\n\n"
                                                                preferredStyle:UIAlertControllerStyleAlert];
    
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    spinner.center = CGPointMake(130.5, 65.5);
    spinner.color = [UIColor blackColor];
    [spinner startAnimating];
    [alert.view addSubview:spinner];
    
    UIViewController *c = (UIViewController*)self.contextController;
    
    [c presentViewController:alert animated:NO completion:nil];
}

- (void)hideWaitPopup {
    UIViewController *c = (UIViewController*)self.contextController;
    [c dismissViewControllerAnimated:YES completion:nil];
}

- (void)runOnUiThreadWithMobilAction:(id<MobilAction>)action {
    dispatch_async(dispatch_get_main_queue(), ^{
        [action onActionWithId:nil];
    });
}

- (void)setEnabledWithId:(id)widget
             withBoolean:(jboolean)isEnabled {
    if ([widget isKindOfClass: [UIButton class]]) {
        UIButton *w = (UIButton*)widget;
        w.enabled = isEnabled;
    }
}

- (void)setFontWithId:(id)widget
               withId:(id)font
               withId:(id)size {
    if ([widget isKindOfClass: [UIButton class]]) {
        UIButton *w = (UIButton*)widget;
        UIFont *f = (UIFont*)font;
        UIFont *fnt = [UIFont fontWithName:f.fontName size: f.pointSize];
        w.titleLabel.font = fnt;
    } else if ([widget isKindOfClass: [UILabel class]]) {
        UILabel *w = (UILabel*)widget;
        UIFont *f = (UIFont*)font;
        UIFont *fnt = [UIFont fontWithName:f.fontName size: f.pointSize];
        w.font = (UIFont*)fnt;
    }
   
}

- (void)setTextColorWithId:(id)widget
                 withFloat:(jfloat)r
                 withFloat:(jfloat)g
                 withFloat:(jfloat)b
                 withFloat:(jfloat)a {
    if ([widget isKindOfClass: [UILabel class]]) {
        UILabel *w = (UILabel*)widget;
        [w setTextColor:[UIColor colorWithRed:r green:g blue:b alpha:a]];
    }
}

@end
