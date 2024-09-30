#!/bin/sh

    echo "|Number of Rows | FileName | Description |" 
for i in `ls *.lragr`
do
    echo "|" `cat $i |grep -v "^#" |wc -l` "|" $i " | " `cat $i.def` "|"
done
