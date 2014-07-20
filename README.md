# Zero To Cloud With @NetflixOSS

Netflix has released a multitude of tools and applications to help in using the Cloud. Being infrastructure, they are
more difficult to setup that just consuming a client library like a JAR in Central. In general, each application
can run independently, but they work better together. This tutorial is focused on bringing up the @NetflixOSS stack
 on a fresh AWS account, in a similar style to how Netflix does it internally. 

We are actively tuning the steps and will post the specific steps here for OSCON 2014.

# Assumptions

* Working in US West (Oregon) aka us-west-2. You’re flexible to do another region, but "Keep it Local" (We’re in Portland after all)
* We’re performing non destructive operations, so if you have an existing AWS account setup, that will be fine and they won’t conflict. But it might be easier to find instances, etc if using a new region.
* In the case of existing infrastructure, like keys, please follow the instructions closely and do not re-use existing provisioned items. I wouldn’t want to be responsible for opening up a security hole in existing infrastructure.
* There are plenty of opportunities to lock down these applications at the network layer or the application layer. Or restrict what the instances can do. References will be made to additional security precautions, but they have not all been integrated into this tutorial.
* This is not a developing for the cloud tutorial, that makes for a great followup. Given enough time, we can talk about it.
* There will costs.

# Tutorial

1. [Sign up for AWS](tutorial/Signup.md)
2. [Log into AWS Console](tutorial/Login.md)
3. [Create keypair](tutorial/Keypair.md)
4. [Create Jumphost](tutorial/Jumphost.md)
5. [Create a role](tutorial/CreateRole.md)
6. [Create an user](tutorial/CreateUser.md)
7. [Create Security Group for ELBs](tutorial/SecurityGroups.md)
8. [Create Foundation AMI](tutorial/FoundationAMI.md)
9. [Setup Jumphost](tutorial/SshJumphost.md)
10. [Setup Credentials](tutorial/Credentials.md)
11. [Build and bake BaseAMI](tutorial/BaseAMI.md)
12. [Build and Bake Asgard](tutorial/AsgardBake.md)
13. [Standup asgard using asgard](tutorial/AsgardStandalone.md)

14. Build Edda Package

    70. `cd ~/zerotocloud`

    71. `./gradlew :edda:buildDeb`

    72. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs edda/build/distributions/edda_1.0.0_all.deb`

15. Deploy Edda

    73. Use browser to DNS Name from Step 13

    74. Follow 13 d-i but for Edda, and for health check use  HTTP:7001/api/v2/view/instances;_limit=1

    75. Using the edda--frontend DNS Name: `http://<ELB DNS name>/api/v2/view/instances;_pp`

    76. Review [https://github.com/Netflix/edda/wiki/REST](https://github.com/Netflix/edda/wiki/REST) for further documentation

16. Build and Bake Eureka

    77. `cd ~/zerotocloud`

    78. `./gradlew :eureka:buildDeb`

    79. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs eureka/build/distributions/eureka_1.0.0_all.deb`

17. Deploy Eureka

    80. Use browser to DNS Name from Step 13

    81. Follow 13 e-i but use eureka

    82. Using the eureka--frontend DNS Name, navigate to  `http://<ELB DNS name>/`

18. (Extra) Killing an instance, see it come back up

19. (Extra) Build/Bake/Deploy Simian Army

    83. `cd ~/zerotocloud`

    84. `./gradlew :simian-army:buildDeb`

    85. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs simian-army/build/distributions/simian-army_1.0.0_all.deb`

        38. Might have to create SimpleDB Domain named SIMIAN_ARMY in asgard if not automatically created

    86. Deploy Simian Army by following 13 d-i except that healthcheck url is `http://<ELB DNS name>/api/v1/chaos`

20. (Extra) Red/Black deploy Edda

    87. Re-build and re-bake edda from Step 14.

    88. Use browser to DNS Name from Step 13.

    89. Navigate to Cluster -> Clusters

    90. Click "edda" link in the Cluster column.

    91. Look at the "Create Next Group" box

        39. In "AMI Image ID", select the image which was just baked. (It might not be the selected by default. Normally would reflect the version number.)

        40. Click "Create Next Group edda-v000"

        41. Watch the task log, click the "Return to Cluster" link at the bottom

    92. Look at the edda--frontend page to see both instances.

    93. Click "Disable" on the first ASG edda. See edda--frontend not showing instance anymore.

    94. Click "Delete" on first ASG edda.

21. (Extra) Ops-y

    95. `sudo pip install awscli`

        42. Uses ~/.aws/config

    96. How to find instances: aws ec2 describe-instances

    97. How to ssh into them: ssh -i jumphost.pem <dns name>

22. (Extra) Gradle 101

    98. Show how we’re making the packages

23. (Extra) [Karyon](https://github.com/Netflix/Karyon) example

    99. `cd ~/zerotocloud`

    100. `./gradlew :karyon:buildDeb`

    101. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs karyon/build/distributions/karyon_1.0.0_all.deb`

    102. Use browser to DNS Name from Step 13

    103. Follow 13 e-i but use karyon

        43. use 8077,8888 as the Security Group port

        44. 8888 as the Listener

        45. Health should be HTTP:8077/admin

        46. No need for an IAM Instance Profile

    104. Using the eureka--frontend DNS Name, navigate to  `http://<ELB DNS name>/`

24. Red/Black Deploy of Karyon

    105. We’re going to modify Karyon to register with eureka

    106. `cd ~/zerotocloud`

    107. `KARYON_OPTS="-Deureka.serviceUrl.default=http://<ELB DNS NAME from 17>/v2/" ./gradlew :karyon:buildDeb`

        47. **`E.g.** `**http://**eureka--frontend-1507600573.us-west-2.elb.amazonaws.com/v2/

    108. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs karyon/build/distributions/karyon_1.0.0_all.deb`

    109. Go to Eureka Security group and allow 7001 from karyon

    110. Go to Cluster | Clusters

    111. Select AMI Image Id, select most recently built.

    112. Click "Create Next Group karyon-v000"

    113. Can go to Eureka’s DNS Name to see HELLO-NETFLIX-OSS

25. (Extra) Developing for the cloud

26. (Extra) Turbine (needs an app with Hystrix running)

27. (Extra) Build/Bake/Deploy ICE

    114. open up two tabs in the AWS console

    115. tab1: Enable Billing Reports on [https://console.aws.amazon.com/billing/home?#/preferences](https://console.aws.amazon.com/billing/home?#/preferences)

    116. tab2: Create S3 Bucket name to something like zerotocloud.billing [https://console.aws.amazon.com/s3/home?region=us-west-2](https://console.aws.amazon.com/s3/home?region=us-west-2)

    117. tab1: enter bucket name (don’t click verify yet),

    118. tab1: click sample policy, copy all of the json text to your clipboard.

    119. tab2: add bucket policy under bucket permissions

    120. tab2: paste json from clipboard and save

    121. tab1: verify

28. (Extra) Build/Bake/Deploy Genie

29. Clean up

    122. In Asgard, delete all resources (to save money)

        48. AutoScaling Groups

        49. Load Balancers

        50. Security Groups

        51. Applications

    123. On the EC2 page, go to AMI and de-register all of the AMIs you created

    124. On the Snapshots section, delete all of the snapshots

Nice to haves:

* Tomcat-users.xml Password protection for Asgard

* server.xml to use port 7001, to reduce number fields that have to changed.

* UDF variables from Asgard aren’t in the ubuntu user’s env

* Speed up edda build, it’s taking an exorbitant time.

* Have versions of modules match what is being wrapped

* Stop tomcat on karyon instance

* ElasticSearch for Edda

* Disable mongod pre-allocated shards

* Tomcat7 log rotations

* Format and mount xvdb xvdc on startup

Pre-talk

* Pictures of AWS and Asgard screens

* Learn more about beanstalk, OpsWorks

* Test with new account, that isn’t EC2 Classic

* Link Shortener for the steps, if there’s few enough.

* Windows Instructions

Questions

* What should we do about VPC? Can a new account have public instances?

Interesting Links

* [http://blog.melnicki.com/2014/03/20/Set-up-public-and-private-subnets-using-AWS-VPC/](http://blog.melnicki.com/2014/03/20/Set-up-public-and-private-subnets-using-AWS-VPC/)

* [https://github.com/Netflix/edda/wiki/Configuration](https://github.com/Netflix/edda/wiki/Configuration)

Testing

`cp baseami/build/distributions/baseami_1.0.0_all.deb .; sudo aminate --interactive --debug -e ec2_aptitude_linux -b ubuntu-foundation -n ubuntu-base-ami ~/zerotocloud/baseami_1.0.0_all.deb`


