#!/bin/sh

#  j2objc-pars.sh
#  prj01-ios
#
#  Created by Vlad Sayenko on 8/3/15.
#  Copyright (c) 2015 Vlad Sayenko. All rights reserved.


# It is possible to use the following scripts to generate SQlighter Db and Rs
# protocols, but you do not have to. Just include what is already provided
# with SQlighter

./j2objc.sh ../../ios-demo-prj ../../../../android/ com/vals/a2ios/sqlighter/intf/SQLighterDb.java
./j2objc.sh ../../ios-demo-prj ../../../../android/ com/vals/a2ios/sqlighter/intf/SQLighterRs.java


# Business functionality conversion

cp -r ../../../../android/com/vals ../../../andr-demo-prj/app/src/main/java/com

./j2objc.sh ../../ios-demo-prj ../../../andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Demo.java
./j2objc.sh ../../ios-demo-prj ../../../andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Bootstrap.java

rm -r ../../../andr-demo-prj/app/src/main/java/com/vals