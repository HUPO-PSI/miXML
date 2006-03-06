#!/bin/sh -x
#
echo "Transforming files"
mkdir -p tmp
XSLT='java -jar lib/saxon.jar '
cp sampleData/$1.xml tmp
$XSLT tmp/$1.xml         tools/MIF25_view.xsl    > tmp/$1.html
$XSLT tmp/$1.xml         tools/MIF25_compact.xsl > tmp/$1-compact.xml
$XSLT tmp/$1-compact.xml tools/MIF25_expand.xsl  > tmp/$1-expand.xml

# end