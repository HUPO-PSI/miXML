#!/bin/sh

# author: Samuel Kerrien
# version $Id: console.sh,v 1.1 2006/01/18 16:44:54 skerrien Exp $

CLASSPATH=`echo lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo lib/jaxb/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo lib/jaxp/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo lib/jaxp/endorsed/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo lib/jwsdp-shared/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo lib/obo/*.jar | tr ' ' ':'`:$CLASSPATH

CLASSPATH=classes/:$CLASSPATH

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
    $JAVA_HOME/bin/java -classpath $CLASSPATH psidev.psi.mi.validator.client.console.MI25ConsoleClient $*
else
    echo Please set JAVA_HOME for this script to execute
fi

# end
