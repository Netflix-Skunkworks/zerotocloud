# <a href="https://github.com/Netflix/SimianArmy/wiki" target="_blank">Simian Army</a>

The Simian Army is a suite of tools for keeping your cloud operating in top form. 
Chaos Monkey, the first member, is a resiliency tool that helps ensure that your applications can tolerate random instance failures.
Janitor Monkey will monitor for unused resources and then clean them up.
Conformity Monkey looks for instances that are not conforming to predefined rules.

![](images/SimianArmy.png)

## Bake Simian Army

The Simian Army is actually a single application that can run all the monkeys.

    cd ~/zerotocloud
    ./gradlew :simian-army:buildDeb
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs simian-army/build/distributions/simian-army_1.0.0_all.deb

## Deploy

Return to [Step 13](AsgardStandalone.md) and perform the "Create Application", "Create an ELB", "Create Auto Scaling Group", and "View instance" pieces.
The difference will be be that we're going to use a different "Health Check" URL, because the monkies don't provide a healthcheck endpoint. 

1. Naviate to Asgard. This can be done by finding the DNS Name from the end of [Step 13](AsgardStandalone.md) or finding the Asgard ELB and using the DNS Name.
2. Follow "Create Application", using the name "simianarmy" instead of "asgard".
3. Follow "Create an ELB", using the name "simianarmy" instead of "asgard". And instead of HTTP:7001/healthcheck for the Health Check URL, use "HTTP:7001/api/v1/chaos". The protocol and port are the same, but the path is different.
4. Follow "Create Auto Scaling Group" using the name "simianarmy" instead of "asgard".
5. Follow "View instance" to get the DNS Name for simianarmy's ELB, i.e. _simianarmy--frontend_. 
6. Using that DNS Name, visit _http://<ELB DNS name>/api/v1/chaos

## Running

When starting up, the Simian Army will create a SimpleDB domain named SIMIAN_ARMY for storing historical records.

The Simian Army has no UI, and it's work is primarily recognized as emails to the owners of instances. 
By default it will come up in "leashed" mode, where it will only report. 
You can edit the _simian-army/build.gradle_ file to change the options before the module is built. 
An example change would be to target a specific ASG, change the frequency of chaos, or unleash the monkies. 

