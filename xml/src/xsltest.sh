#!/bin/sh -x
# 
echo "Transforming files"
XSLT='java -jar saxon.jar '
$XSLT $1.xml        MIF_view.xsl   > $1.html
$XSLT $1.xml        MIF_compact.xsl   > $1_compact.xml
$XSLT $1_compact.xml   MIF_view.xsl   > $1.html
$XSLT $1.xml        MIF_expand.xsl > $1_expand.xml

# end