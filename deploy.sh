cp -r WebContent/* web/
ant clean
ant all
sudo rm -rf /home/apache-tomcat-7.0.41/webapps/coeditor
ant install
sudo cp ~/AwsCredentials.properties /home/apache-tomcat-7.0.41/webapps/coeditor/WEB-INF/classes/
sudo cp ~/AwsCredentials.properties /home/apache-tomcat-7.0.41/webapps/coeditor/
sudo /home/apache-tomcat-7.0.41/bin/shutdown.sh
sudo /home/apache-tomcat-7.0.41/bin/startup.sh

