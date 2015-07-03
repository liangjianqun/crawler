#!/bin/sh

#for i in {1281..1566};do
#for i in {1567..4885};do
#for i in {7406..48648};do
#for i in {4886..7405};do
#for i in {48648..53125};do
#for i in {53126..53126};do
#for i in {53127..60646};do
#for i in {60647..68165};do
#for i in {68166..79300};do
for i in {79301..79301};do
    ((d=i/1000))
    echo "process /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt"
    #sed -i '1,6d' /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&nbsp;/ /g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&ldquo;/\"/g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&rdquo;/\"/g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&hellip;/.../g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&mdash;/--/g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&middot;/./g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&lsquo;/'/g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
    sed  -i "s/&rsquo;/'/g" /usr/local/tomcat/webapps/ROOT/txt/${d}/${i}/*.txt
done
