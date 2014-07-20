# Step 14 - Build and Bake <a href="" target="_blank">Eureka</a>

Eureka is a mid-tier service discovery server, optmized for the cloud. 
Using Eureka we can register our services and, almost as importantly as the service, be able to call registered services directly without incurring the cost of going through a Load Balancer. 
Changes to instances take effect very rapidly, unlike DNS.
It is designed for resiliency in the likely occurance of major catastrophies, further making it a better choice than DNS or Load Balancer when applicable.

## Bake Eureka

    cd ~/zerotocloud
    ./gradlew :eureka:buildDeb
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs eureka/build/distributions/eureka_1.0.0_all.deb

By now, you might be noticing a pattern, we build a DEB and bake it. This has become very routine and easily automated.

## Deploy Eureka.

Once again, we're going to repeat the Asgard deployment but for Eureka. 
Return to [Step 13](AsgardStandalone.md) and perform the "Create Application", "Create an ELB", "Create Auto Scaling Group", and "View instance" pieces.
Everything from the Health Check URL to the port numbers can stay the same, just use the name "eureka".

1. Naviate to Asgard. This can be done by finding the DNS Name from the end of [Step 13](AsgardStandalone.md) or finding the Asgard ELB and using the DNS Name.
2. Follow "Create Application", using the name "eureka" instead of "asgard".
3. Follow "Create an ELB", using the name "eureka" instead of "asgard". And instead of HTTP:7001/healthcheck for the Health Check URL, use "HTTP:7001/api/v2/view/instances;_limit=1". The protocol and port are the same, but the path is different.
4. Follow "Create Auto Scaling Group" using the name "eureka" instead of "asgard".
5. Follow "View instance" to get the DNS Name for eureka's ELB, i.e. _eureka--frontend_. It can Eureka quite a few minutes to start up, because it is trying find other instances.
6. Using that DNS Name, visit _http://<ELB DNS name>/_. ![](images/Eureka.png)

Review <a href="https://github.com/Netflix/eureka/wiki/Eureka-REST-operations" target="_blank">Eureka's REST API</a> for further documentation.

## Querying Eureka

When it's initially started, nothing has registered with it.