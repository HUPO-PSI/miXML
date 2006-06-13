#!/bin/sh

# author: Samuel Kerrien
# version $Id: expand.sh,v 1.1 2006/06/13 16:42:13 skerrien Exp $

echo "Id you are using Java 5, please read http://www.stylusstudio.com/xsllist/200503/post21110.html"


CLASSPATH=lib/jaxp/endorsed/xalan.jar

echo "CLASSPATH=$CLASSPATH"

#if cygwin used (ie on a Windows machine), make sure the paths
#are converted from Unix to run correctly with the windows JVM
cygwin=false;

case "`uname`" in

CYGWIN*) cygwin=true
         echo "running in a Windows JVM (from cygwin).." ;;
*) echo "running in a Unix JVM..." ;;

esac

if $cygwin; then
CLASSPATH=`cygpath --path --windows "$CLASSPATH"`

fi

if [ "$JAVA_HOME" ]; then
    $JAVA_HOME/bin/java -classpath $CLASSPATH org.apache.xalan.xslt.Process \
                                             -xsl xslt/MIF25_expand.xsl \
					     -in $1 \
					     -out $2
    echo "done"				     
else
    echo Please set JAVA_HOME for this script to execute
fi

# end
