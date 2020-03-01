FROM amazoncorretto:11.0.4

ADD libs/mood-tracker-service-1.0.0.jar /app/app.jar

ADD application.yml /app/application.yml

ENV TZ Pacific/Auckland

# start the application as an executable
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# if you want to run Elastic APM, set up the proxy (if applicable), install wget to download the elastic-apm-agent
# then start the application
#ENV http_proxy http://hostname:port
#ENV https_proxy http://hostname:port
#RUN yum -y install wget
#RUN wget -O /app/elastic-apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.13.0/elastic-apm-agent-1.13.0.jar
#ENTRYPOINT ["java", \
#"-javaagent:/app/elastic-apm-agent.jar", \
#"-Delastic.apm.service_name=mood-tracker-service", \
#"-Delastic.apm.server_urls=http://apm:8200", \
#"-Delastic.apm.application_packages=nz.co.moodtracker", \
#"-jar", "/app/app.jar"]

EXPOSE 8080
