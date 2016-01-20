#!/bin/sh

#  ios-demo-prj
#
#  Created by Vlad Sayenko on 8/3/15.
#  Copyright (c) 2015 Vlad Sayenko. All rights reserved.

# It is possible to use the following scripts to generate SQlighter Db and Rs
# protocols, but you do not have to. Just include what is already provided
# with SQlighter

ROOT_DIR=../../../..
# echo $ROOT_DIR

#
# Java interfaces to Objc Protocols conversion
#

#
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/sqlighter/intf/SQLighterDb.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/sqlighter/intf/SQLighterRs.java

./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/intf/AnAttrib.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/intf/AnObject.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/intf/AnSql.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/intf/AnOrm.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/intf/AnUpgrade.java

./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/impl/AnAttribImpl.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/impl/AnObjectImpl.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/impl/AnSqlImpl.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/impl/AnOrmImpl.java
./j2objc.sh $ROOT_DIR/ios/j2objc/ $ROOT_DIR/android/ com/vals/a2ios/amfibian/impl/AnUpgradeImpl.java


./j2objc-w-refl.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/mobilighter/android com/vals/a2ios/mobilighter/intf/Mobilighter.java
./j2objc.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/mobilighter/android com/vals/a2ios/mobilighter/intf/MobilAction.java

#

#
# Business functionality conversion
#

# since Android proj. is including sqlighter files through gradle config file
# we need to tmp bring these files directly into j2objc conversion
cp -r $ROOT_DIR/android/com/vals $ROOT_DIR/demo/andr-demo-prj/app/src/main/java/com
# also temporarily bring mibilighter in
cp -r $ROOT_DIR/demo/mobilighter/android/com/vals $ROOT_DIR/demo/andr-demo-prj/app/src/main/java/com

# ./j2objc-w-refl.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/mobilighter/android com/vals/a2ios/mobilighter/intf/Mobilighter.java
# ./j2objc.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/mobilighter/android com/vals/a2ios/mobilighter/intf/MobilAction.java

./j2objc-w-refl.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Entity.java
./j2objc-w-refl.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Appointment.java

./j2objc.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Demo.java
./j2objc.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/DemoBase.java
./j2objc.sh $ROOT_DIR/demo/ios-demo-prj/ios-demo-prj $ROOT_DIR/demo/andr-demo-prj/app/src/main/java com/prod/vals/andr_demo_prj/Bootstrap.java

# and then remove temporarily brought sqlighter files
rm -r $ROOT_DIR/demo/andr-demo-prj/app/src/main/java/com/vals

# end