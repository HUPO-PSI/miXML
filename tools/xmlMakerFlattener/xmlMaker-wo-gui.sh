#!/bin/csh
java -Xms500M -Xmx500M -Djava.ext.dirs=libs -classpath dist/xmlMakerFlattener.jar mint.filemakers.xmlMaker.XmlMaker
