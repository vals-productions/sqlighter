#!/bin/sh

#  j2objc.sh
#  prj01-ios
#
#  Created by Vlad Sayenko on 8/3/15.
#  Copyright (c) 2015 Vlad Sayenko. All rights reserved.
# ls -l $1
#
# --strip-reflection
#

# Used by convert.sh

 ../../../j2objc/j2objc -use-arc --strip-reflection --prefixes pckgs.sh -d $1 -sourcepath $2 $3
