#!/bin/sh -x
# 
XSLT='java -jar saxon.jar '
$XSLT $1.xml        MIF_view.xsl   > $1.html
$XSLT $1.xml        MIF_fold.xsl   > $1_fold.xml
$XSLT $1_fold.xml   MIF_view.xsl   > $1_fold.html
$XSLT $1.xml        MIF_unfold.xsl > $1_unfold.xml
$XSLT $1_unfold.xml MIF_view.xsl   > $1_unfold.html

# end