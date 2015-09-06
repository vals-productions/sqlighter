### Demo projects

## iOS

First, look into to crt-j2objc-link.sh for an example on how to create a link
to j2objc distribution into the demo directory. iOS project refers
this location for includes and lib paths. This is much easier than readjusting all
path related properties one by one. So you are supposed to have 
```
sqlighter
  demo
    ios-demo-prj
    andr-demo-prj
    j2objc -> link to the actual j2objc installation dir.
      bin
      include
      lib
      ....
```

## Android

Android demo is under construction. You can look at proj. structure and code samples
but I do not guarantee it will compile and run out of the box on your computer
due to path configurations.
