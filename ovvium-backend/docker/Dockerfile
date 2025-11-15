FROM openjdk:15-alpine

# define enviroment variable: timezone
ENV TZ=Europe/Madrid \
    ARTIFACT_NAME="${project.build.finalName}.jar" \
    JAVA_OPTS="-Xms128m -Xmx512m --enable-preview"
    
# Install packages
RUN apk update && apk --no-cache add curl
    
COPY ./${ARTIFACT_NAME} /opt/app/${ARTIFACT_NAME}

EXPOSE 8080
# Hazelcast PORT
EXPOSE 5701

# Execute artifact by java
CMD java ${JAVA_OPTS} -jar /opt/app/${ARTIFACT_NAME}
