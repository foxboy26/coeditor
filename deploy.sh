TOMCAT_HOME=/home/apache-tomcat-7.0.41

echo $TOMCAT_HOME

cp -r WebContent/* web/
rm -rf $TOMCAT_HOME/webapps/coeditor

ant clean
ant all
ant install

cp ~/AwsCredentials.properties $TOMCAT_HOME/webapps/coeditor/WEB-INF/classes/
cp ~/AwsCredentials.properties $TOMCAT_HOME/webapps/coeditor/

$TOMCAT_HOME/bin/shutdown.sh
$TOMCAT_HOME/bin/startup.sh

