# --input inputdir --output outputdir --name AppName --class package.ClassName
jpackager create-image \
    --input build/libs \
    --main-jar myjar.jar \
    --add-modules java.base,java.datatransfer,java.desktop,java.scripting,java.xml,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.naming,java.sql,jdk.charsets \
    --output dist \
    --jvm-args '-XstartOnFirstThread' \
    --name GoodGov --class AppKt \