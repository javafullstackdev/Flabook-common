https://www.digitalocean.com/community/tutorials/how-to-install-apache-kafka-on-ubuntu-18-04


How To Install Apache Kafka on Ubuntu 18.04
UbuntuApacheMessagingJavaUbuntu 18.04
hathy
bsder
By Hathy A and bsder

Last Validated onMay 10, 2019 Originally Published onJuly 31, 2018 188.2kviews
Not using Ubuntu 18.04?
Choose a different version or distribution.
The author selected the Free and Open Source Fund to receive a donation as part of the Write for DOnations program.

Introduction
Apache Kafka is a popular distributed message broker designed to efficiently handle large volumes of real-time data. A Kafka cluster is not only highly scalable and fault-tolerant, but it also has a much higher throughput compared to other message brokers such as ActiveMQ and RabbitMQ. Though it is generally used as a publish/subscribe messaging system, a lot of organizations also use it for log aggregation because it offers persistent storage for published messages.

A publish/subscribe messaging system allows one or more producers to publish messages without considering the number of consumers or how they will process the messages. Subscribed clients are notified automatically about updates and the creation of new messages. This system is more efficient and scalable than systems where clients poll periodically to determine if new messages are available.

In this tutorial, you will install and use Apache Kafka 2.1.1 on Ubuntu 18.04.

Prerequisites
To follow along, you will need:

One Ubuntu 18.04 server and a non-root user with sudo privileges. Follow the steps specified in this guide if you do not have a non-root user set up.
At least 4GB of RAM on the server. Installations without this amount of RAM may cause the Kafka service to fail, with the Java virtual machine (JVM) throwing an “Out Of Memory” exception during startup.
OpenJDK 8 installed on your server. To install this version, follow these instructions on installing specific versions of OpenJDK. Kafka is written in Java, so it requires a JVM; however, its startup shell script has a version detection bug that causes it to fail to start with JVM versions above 8.


## Step 1 — Creating a User for Kafka
Since Kafka can handle requests over a network, you should create a dedicated user for it. This minimizes damage to your Ubuntu machine should the Kafka server be compromised. We will create a dedicated kafka user in this step, but you should create a different non-root user to perform other tasks on this server once you have finished setting up Kafka.

Logged in as your non-root sudo user, create a user called kafka with the useradd command:

```
sudo useradd kafka -m
```

The -m flag ensures that a home directory will be created for the user. This home directory, /home/kafka, will act as our workspace directory for executing commands in the sections below.

Set the password using passwd:
```
sudo passwd kafka
```

Add the kafka user to the sudo group with the adduser command, so that it has the privileges required to install Kafka’s dependencies:
```
sudo adduser kafka sudo
```
Your kafka user is now ready. Log into this account using su:
```
su -l kafka
```
Now that we’ve created the Kafka-specific user, we can move on to downloading and extracting the Kafka binaries.

## Step 2 — Downloading and Extracting the Kafka Binaries
Let’s download and extract the Kafka binaries into dedicated folders in our kafka user’s home directory.

To start, create a directory in /home/kafka called Downloads to store your downloads:
```
mkdir ~/Downloads
```
Use curl to download the Kafka binaries:
```
curl "https://www.apache.org/dist/kafka/2.1.1/kafka_2.11-2.1.1.tgz" -o ~/Downloads/kafka.tgz
```
Create a directory called kafka and change to this directory. This will be the base directory of the Kafka installation:
```
mkdir ~/kafka && cd ~/kafka
```
Extract the archive you downloaded using the tar command:
```
tar -xvzf ~/Downloads/kafka.tgz --strip 1
```
We specify the --strip 1 flag to ensure that the archive’s contents are extracted in ~/kafka/ itself and not in another directory (such as ~/kafka/kafka_2.11-2.1.1/) inside of it.

Now that we’ve downloaded and extracted the binaries successfully, we can move on configuring to Kafka to allow for topic deletion.

## Step 3 — Configuring the Kafka Server
Kafka’s default behavior will not allow us to delete a topic, the category, group, or feed name to which messages can be published. To modify this, let’s edit the configuration file.

Kafka’s configuration options are specified in server.properties. Open this file with nano or your favorite editor:
```
nano ~/kafka/config/server.properties
```
Let’s add a setting that will allow us to delete Kafka topics. Add the following to the bottom of the file:
```
~/kafka/config/server.properties
delete.topic.enable = true
```
Save the file, and exit nano. Now that we’ve configured Kafka, we can move on to creating systemd unit files for running and enabling it on startup.

## Step 4 — Creating Systemd Unit Files and Starting the Kafka Server
In this section, we will create systemd unit files for the Kafka service. This will help us perform common service actions such as starting, stopping, and restarting Kafka in a manner consistent with other Linux services.

Zookeeper is a service that Kafka uses to manage its cluster state and configurations. It is commonly used in many distributed systems as an integral component. If you would like to know more about it, visit the official Zookeeper docs.

Create the unit file for zookeeper:
```
sudo nano /etc/systemd/system/zookeeper.service
```

Enter the following unit definition into the file:

```
/etc/systemd/system/zookeeper.service
[Unit]
Requires=network.target remote-fs.target
After=network.target remote-fs.target

[Service]
Type=simple
User=kafka
ExecStart=/home/kafka/kafka/bin/zookeeper-server-start.sh /home/kafka/kafka/config/zookeeper.properties
ExecStop=/home/kafka/kafka/bin/zookeeper-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
```

The [Unit] section specifies that Zookeeper requires networking and the filesystem to be ready before it can start.

The [Service] section specifies that systemd should use the zookeeper-server-start.sh and zookeeper-server-stop.sh shell files for starting and stopping the service. It also specifies that Zookeeper should be restarted automatically if it exits abnormally.

Next, create the systemd service file for kafka:
```
sudo nano /etc/systemd/system/kafka.service
```
Enter the following unit definition into the file:

```
/etc/systemd/system/kafka.service
[Unit]
Requires=zookeeper.service
After=zookeeper.service

[Service]
Type=simple
User=kafka
ExecStart=/bin/sh -c '/home/kafka/kafka/bin/kafka-server-start.sh /home/kafka/kafka/config/server.properties > /home/kafka/kafka/kafka.log 2>&1'
ExecStop=/home/kafka/kafka/bin/kafka-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
```
The [Unit] section specifies that this unit file depends on zookeeper.service. This will ensure that zookeeper gets started automatically when the kafka service starts.

The [Service] section specifies that systemd should use the kafka-server-start.sh and kafka-server-stop.sh shell files for starting and stopping the service. It also specifies that Kafka should be restarted automatically if it exits abnormally.

Now that the units have been defined, start Kafka with the following command:
```
sudo systemctl start kafka
```
To ensure that the server has started successfully, check the journal logs for the kafka unit:
```
sudo journalctl -u kafka
```
You should see output similar to the following:

Output
Jul 17 18:38:59 kafka-ubuntu systemd[1]: Started kafka.service.
You now have a Kafka server listening on port 9092.

While we have started the kafka service, if we were to reboot our server, it would not be started automatically. To enable kafka on server boot, run:
```
sudo systemctl enable kafka
```
Now that we’ve started and enabled the services, let’s check the installation.

