#!/bin/sh

for i in {1..11};do
    echo "process /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt"
#    sed -i '1,6d' /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&nbsp;/ /g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&ldquo;/\"/g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&rdquo;/\"/g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&hellip;/.../g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&mdash;/--/g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&middot;/./g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&lsquo;/'/g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
    sed  -i "s/&rsquo;/'/g" /usr/local/tomcat/webapps/ROOT/txt/0/${i}/*.txt
done
