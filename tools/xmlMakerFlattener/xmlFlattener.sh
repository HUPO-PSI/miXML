#!/bin/csh
java  -Xms500M -Xmx500M  -classpath dist/xmlMakerFlattener.jar:libs/cocoon.jar:libs/commons-lang-2.0.jar:libs/xercesImpl.jar:libs/castor-0.9.5-xml.jar:libs/xml-apis.jar:libs/xmlParserAPIs.jar mint.filemakers.xmlFlattener.XmlFlattenerGui
