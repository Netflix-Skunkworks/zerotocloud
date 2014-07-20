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
11. [Build and Bake BaseAMI](tutorial/BaseAMI.md)
12. [Build and Bake Asgard](tutorial/AsgardBake.md)
13. [Standup asgard using asgard](tutorial/AsgardStandalone.md)
14. [Build and Bake Edda](tutorial/Edda.md)
15. [Build and Bake Eureka](tutorial/Eureka.md)

When all done, ilrelevant of how far you get make sure to read the Clean up instructions below, so that you don't get charged for resources that you're not using.

# Extras

Settings up infrastructure can be frought with problems, so if you've made this far in the allocated time, congratulate yourself.
Here are some additional exercises which can help expand your knowledge of the Netflix stack or the AWS in general.

* [Simian Army](tutorial/SimianArmy.md)
* [Karyon](tutorial/Karyon.md)


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

18. [Killing an instance](tutorial/ASG)


# Clean up

    122. In Asgard, delete all resources (to save money)

        48. AutoScaling Groups

        49. Load Balancers

        50. Security Groups

        51. Applications

    123. On the EC2 page, go to AMI and de-register all of the AMIs you created

    124. On the Snapshots section, delete all of the snapshots

Nice to haves this tutorial:

* Tomcat-users.xml Password protection for Asgard
* UDF variables from Asgard aren’t in the ubuntu user’s env
* Have versions of modules match what is being wrapped
* Stop tomcat on karyon instance
* Tomcat7 log rotations
* Multiple Eureka instances in different zones.

Interesting Links

* [http://blog.melnicki.com/2014/03/20/Set-up-public-and-private-subnets-using-AWS-VPC/](http://blog.melnicki.com/2014/03/20/Set-up-public-and-private-subnets-using-AWS-VPC/)

* [https://github.com/Netflix/edda/wiki/Configuration](https://github.com/Netflix/edda/wiki/Configuration)

Testing

`cp baseami/build/distributions/baseami_1.0.0_all.deb .; sudo aminate --interactive --debug -e ec2_aptitude_linux -b ubuntu-foundation -n ubuntu-base-ami ~/zerotocloud/baseami_1.0.0_all.deb`


