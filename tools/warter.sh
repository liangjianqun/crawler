#!/bin/sh

for i in {0..53125};do
    ((d=i/1000))
    mogrify  -pointsize 20 -verbose -draw " fill white text 6,144 'jipinshu.com' fill white text 7,145 'jipinshu.com' " /usr/local/tomcat/webapps/ROOT/cover/${d}/${i}/${i}*
done
