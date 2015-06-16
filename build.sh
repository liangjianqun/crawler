#!/bin/sh
mvn clean;
mvn assembly:assembly

mkdir -p run/
cp target/crawler-0.0.1-SNAPSHOT-dist.zip run/
cd run/
unzip -o -d crawler crawler-0.0.1-SNAPSHOT-dist.zip
cd ../

echo -n "classpath=" > run/run.sh
for f in `ls run/crawler/lib`;do echo -n ":./crawler/lib/${f}";done>> run/run.sh
echo "">>run/run.sh
echo "exec java -Xms512m -Xmx512m -ea -cp \${classpath} crawler.core.Job $@ 1>std.out 2>std.err" >> run/run.sh
