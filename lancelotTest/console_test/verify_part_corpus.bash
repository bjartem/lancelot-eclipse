APPLICATIONS=(    
    "ANTLR 2.7.6"
    "ASM 2.2.3"
    "AXIS 1.4"
    "Ant 1.7.0"
    "ArgoUML 0.24"
    "Avalon 4.1.5"
    "BSF 2.4.0"
    "Batik 1.6"
    "BlueJ 2.1.3"
    "C3P0 0.9.1"
    "CGLib 2.1.03"
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
