#!/bin/sh
java -Xms500M -Xmx500M -Djava.ext.dirs=libs -classpath dist/xmlMakerFlattener.jar psidev.psi.mi.filemakers.xmlMaker.XmlMaker
