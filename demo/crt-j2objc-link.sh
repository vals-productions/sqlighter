#!/bin/sh

# create a link to your j2objc distribution
# ln -s <paht to j2objc> j2objc

if [ "$#" -ne 1 ]; then
	echo 'Usage  : > ./crt-j2objc-link.sh [path to your j2objc installation]'
	echo '(Example)> ./crt-j2objc-link.sh ../../m/j2objc-0.9.7'
fi

# example
# ln -s ../../m/j2objc-0.9.7 j2objc 
if [ "$#" -eq 1 ]; then
	echo 'Creating link'
	ln -s $1 j2objc
fi	

# unlink j2objc
