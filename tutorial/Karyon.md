# [Karyon](https://github.com/Netflix/Karyon)

So far, we've brought up what we're call infrastructure. This exercize starts to bring a real application and explore how it's deployed over time.

Karyon is a framework and library that essentially contains the blueprint of what it means to implement a cloud ready web service. 
All the other fine grained web services and applications that form our SOA graph can essentially be thought as being cloned from this basic blueprint.
It combines many of the library @netflixoss projects:

* Bootstrapping , dependency and Lifecycle Management (via Governator)
* Runtime Insights and Diagnostics (via karyon-admin-web module)
* Configuration Management (via Archaius)
* Service discovery (via karyon)
* Powerful transport module (via RxNetty)

## Build and Bake

    cd ~/zerotocloud`
    ./gradlew :karyon:buildDeb`
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs karyon/build/distributions/karyon_1.0.0_all.deb

## Deploy

Since Karyon is meant to be a middle-tier service, there's no need for a load balancer but it will still live in an ASG. 
The default 
Return to [Step 13](AsgardStandalone.md) and perform the "Create Application", "Create Auto Scaling Group", and "View instance" pieces.
Without load balancer, we won't have to enter it on the ASG page.

1. Naviate to Asgard. This can be done by finding the DNS Name from the end of [Step 13](AsgardStandalone.md) or finding the Asgard ELB and using the DNS Name.
2. Follow "Create Application", using the name "karyon" instead of "asgard".
4. Follow "Create Auto Scaling Group" using the name "karyon" instead of "asgard". Leave the Load Balancer section blank. Also leave the IAM Profile section blank, since Karyon doesn't require any access to the AWS APIs.
5. Follow "View instance" to get the DNS Name the instance. 
6. Using that DNS Name, visit _http://<ELB DNS name>/_

Review <a href="https://github.com/Netflix/karyon/wiki/karyon-REST-operations" target="_blank">karyon's REST API</a> for further documentation.
    102. Use browser to DNS Name from Step 13

    103. Follow 13 e-i but use karyon

        43. use 8077,8888 as the Security Group port

        44. 8888 as the Listener

        45. Health should be HTTP:8077/admin

        46. No need for an IAM Instance Profile

    104. Using the karyon--frontend DNS Name, navigate to  `http://<ELB DNS name>/`

24. Red/Black Deploy of Karyon

    105. We’re going to modify Karyon to register with karyon

    106. `cd ~/zerotocloud`

    107. `KARYON_OPTS="-Dkaryon.serviceUrl.default=http://<ELB DNS NAME from 17>/v2/" ./gradlew :karyon:buildDeb`

        47. **`E.g.** `**http://**karyon--frontend-1507600573.us-west-2.elb.amazonaws.com/v2/

    108. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs karyon/build/distributions/karyon_1.0.0_all.deb`

    109. Go to karyon Security group and allow 7001 from karyon

    110. Go to Cluster | Clusters

    111. Select AMI Image Id, select most recently built.

    112. Click "Create Next Group karyon-v000"

    113. Can go to karyon’s DNS Name to see HELLO-NETFLIX-OSS
