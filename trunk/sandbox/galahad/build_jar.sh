GALAHAD_PROJECT_DIR=`pwd`
LANCELOT_PROJECT_DIR=~/dev/nr_workspace/lancelot

# Lancelot's dependencies
DEPENDENCIES=`find $LANCELOT_PROJECT_DIR $GALAHAD_PROJECT_DIR/lib -name "*.jar"`
CLASSPATH=$CLASSPATH:.:./jython/jython.jar:`echo $DEPENDENCIES | tr ' ' ':'`

echo "Compiling Galahad's java files..."
find galahadjava -name "*.java" | xargs javac -cp $CLASSPATH

rm -fr build_workdir
mkdir build_workdir
cd build_workdir

for DEP in $DEPENDENCIES; do
    echo "Extracting $DEP..."
    jar xf $DEP
    rm -fr META-INF
    for f in  `find . -maxdepth 1 ! -type d`; do
        rm $f;
    done    
done

echo "Extracting jython.jar..."
cp ../jython/jython.jar .
jar xf jython.jar

echo "Extracting Jython's libraries..."
cp ../jython/jython-lib.jar .
jar xf jython-lib.jar
mv jython2.5-Lib Lib

echo "Copying Lancelot's classes..."
cp -R $LANCELOT_PROJECT_DIR/bin/no no

echo "Copying Lancelot's resources..."
cp -R $LANCELOT_PROJECT_DIR/resources/ lancelot_resources

echo "Adding galahad's python files..."
cp -R ../src/* Lib

echo "Adding galahad's Java files..."
cp -R ../galahadjava/ .

echo "Removing .svn noise..."
find . -name "*.svn" | xargs rm -fr

echo "Removing .jar noise..."
rm *.jar

echo "Packaging output .jar file..."
jar -cfe lancelot-galahad.jar galahadjava.Main *

cd ..
echo
echo "ALL DONE"
du -sh build_workdir/lancelot-galahad.jar
