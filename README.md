# multiplier-generator
This is a multiplier-generator written in chisel

## Environment Setup

### Install JDK

#### Download
[JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
Version: `jdk-8u261-linux-x64.tar.gz`

#### Environment
Upload jdk-8u261-linux-x64.tar.gz to /your/directory/
```
tar jdk-8u261-linux-x64.tar.gz
export JAVA_HOME=/your/directory/jdk1.8.0_261
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH:$HOME/bin
```

### Install sbt
Take Ubuntu 18.04 as an example, other OS please refer to [here](https://www.scala-sbt.org/release/docs/Setup.html)
```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
```

### Sbt test




