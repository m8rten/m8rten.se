FROM java:8u40-jdk
 
# Install GVM
RUN curl -s get.gvmtool.net | bash
 
# Install Groovy
RUN ["/bin/bash", "-c", "source /root/.gvm/bin/gvm-init.sh"]
RUN echo "gvm_suggestive_selfupdate=false" >> /root/.gvm/etc/config
RUN ["/bin/bash", "-c", "-l", "gvm install groovy"]
 
# Fix path
ENV GROOVY_HOME /root/.gvm/groovy/current
ENV PATH $GROOVY_HOME/bin:$PATH

# Adds application
ADD public /public
ADD m8rten-se.groovy /
ADD ratpack.properties /

CMD ["groovy", "m8rten-se.groovy"]

EXPOSE 5050