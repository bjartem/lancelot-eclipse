APPLICATIONS=(    
    "ANTLR 2.7.6"
    "ASM 2.2.3"
    "AXIS 1.4"
    "Ant 1.7.0"
    "ArgoUML 0.24"
    "AspectJ 1.5.3"
    "Avalon 4.1.5"
    "BSF 2.4.0"
    "Batik 1.6"
    "BeanShell 2.0 Beta"
    "BlueJ 2.1.3"
    "BluePrints UI 1.4"
    "C3P0 0.9.1"
    "CGLib 2.1.03"
    "Cactus 1.7.2"
    "Castor 1.1"
    "Checkstyle 4.3"
    "Cobertura 1.8"
    "Commons Codec 1.3"
    "Commons Collections 3.2"
    "Commons DBCP 1.2.1"
    "Commons Digester 1.8"
    "Commons Discovery 0.4"
    "Commons EL 1.0"
    "Commons FileUpload 1.2"
    "Commons HttpClient 3.0.1"
    "Commons IO 1.3.1"
    "Commons Lang 2.3"
    "Commons Modeler 2.0"
    "Commons Net 1.4.1"
    "Commons Pool 1.3"
    "Commons Validator 1.3.1"
    "CruiseControl 2.6"
    "DB Derby 10.2.2.0"
    "Dom4J 1.6.1"
    "Eclipse 3.2.1"
    "Emma 2.0.5312"
    "FitNesse"
    "Ganymed ssh build 209"
    "Genericra"
    "Geronimo 1.1.1"
    "Google WT 1.3.3"
    "Groovy 1.0"
    "HOWL 1.0.2"
    "HSQLDB"
    "Hibernate 3.2.1"
    "J5EE SDK"
    "JBoss 4.0.5"
    "JDOM 1.0"
    "JEdit 4.3"
    "JGroups 2.2.8"
    "JOnAS 4.8.4"
    "JRuby 0.9.2"
    "JUnit 4.2"
    "JXTA 2.4.1"
    "JacORB 2.3.0"
    "James 2.3.0"
    "Jar Jar Links 0.7"
    "JavaCC 4.0"
    "Javassist 3.4"
    "Jetty 6.1.1"
    "Jini 2.1"
    "Jython 2.2b1"
    "Kawa 1.9.1"
    "Livewire 4.12.11"
    "Log4J 1.2.14"
    "MJC 1.3.2"
    "MOF"
    "MX4J 3.0.2"
    "Maven 2.0.4"
    "Mule 1.3.3"
    "NetBeans 5.5"
    "OGNL 2.6.9"
    "OpenJMS 0.7.7 Alpha"
    "OpenSAML 1.0.1"
    "Piccolo 1.04"
    "PicoContainer 1.3"
    "Polyglot 2.1.0"
    "Poseidon CE 5.0.1"
    "Rhino 1.6r5"
    "Saxon 8.8"
    "Shale Remoting 1.0.3"
    "Spring 2.0.2"
    "Struts 2.0.1"
    "Sun Wireless Toolkit 2.5"
    "Tapestry 4.0.2"
    "Tomcat 6.0.7 Beta"
    "TranQL 1.3"
    "Trove 1.1b4"
    "Velocity 1.4"
    "WSDL4J 1.6.2"
    "XBean 2.0.0"
    "XML Security 1.3.0"
    "XOM 1.1"
    "XPP 1.1.3.4"
    "XStream 1.2.1"
    "Xalan-J 2.7.0"
    "Xerces-J 2.9.0"
    "azplugins"
    "azrating"
    "azupdater"
)

RESOURCES_DIR="$( cd "$( dirname "$0" )" && pwd )/../../lancelot/resources"

function verify {
    CORPUS_DIR=$1
    TMP_DIR=~/.lancelotConsoleTemp/
    TSUM=0

    mkdir $TMP_DIR

    IFS=$'\n' 

    n=0
    for app in "${APPLICATIONS[@]}"
    do
        JARS=`find $CORPUS_DIR | grep $app | egrep ".*jar$"`

        pushd . > /dev/null
        cd $TMP_DIR
        rm -fr x*
        for jar in $JARS
        do
            JAR_UNPACK_DIR="x$n"
            mkdir $JAR_UNPACK_DIR
            cd $JAR_UNPACK_DIR
            cp $jar .
            jar xf $jar > /dev/null
            cd ..
            n=$((n+1))
        done    
        popd > /dev/null

        echo $app
        TPRE=`date +%s`
        bash run_lancelot_script.bash lancelot_application_verifier.rhino.js \
                            $RESOURCES_DIR `find $TMP_DIR -name "*.class"`
        TSUM=$((TSUM + `date +%s` - TPRE))
        echo
    done    

    rm -fr $TMP_DIR
    echo "Analysis took $TSUM seconds."
}


if [ $# -ne 1 ]
then
    echo "Usage: bash `basename $0` {corpus directory}"
    exit 1
else
    verify $1
fi
