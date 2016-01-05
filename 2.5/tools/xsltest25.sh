# Transform PSI-MI XML file to compact, expanded and HTML
# Pass in ID of XML file in ../sampleData
# Example: ./xsltest25.sh 10523676

export tmp=/tmp/psi
mkdir -p $tmp
cp ../sampleData/$1.xml $tmp

xslt='java -jar ../../xml/lib/saxon.jar '

echo "Generating $1.html..."
$xslt $tmp/$1.xml         MIF25_view.xsl    > $tmp/$1.html

echo "Generating $1-compact.xml..."
$xslt $tmp/$1.xml         MIF25_compact.xsl > $tmp/$1-compact.xml

echo "Generating $1-expand.xml..."
$xslt $tmp/$1-compact.xml MIF25_expand.xsl  > $tmp/$1-expand.xml

echo "See:"
ls -lh  $tmp/$1*