#!/bin/sh

#for i in {0..53125};do
#for i in {0..53125};do
#for i in {53126..60646};do
#for i in {60647..68165};do
for i in {68166..88000};do
    ((d=i/1000))
    echo "mogrify ${i}"
    mogrify -resize 120x150  -pointsize 20 -verbose -draw " fill white text 6,144 'jipinshu.com' fill white text 7,145 'jipinshu.com' " /usr/local/tomcat/webapps/ROOT/cover/${d}/${i}/${i}*
done
