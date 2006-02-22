#!/bin/sh -x
#
echo "Transforming files"
mkdir -p tmp
XSLT='java -jar ../lib/saxon.jar '
cp ../sampleData/$1.xml tmp
$XSLT tmp/$1.xml         MIF_view.xsl    > tmp/$1.html
$XSLT tmp/$1.xml         MIF_compact.xsl > tmp/$1-compact.xml
$XSLT tmp/$1-compact.xml MIF_expand.xsl  > tmp/$1-expand.xml

# end