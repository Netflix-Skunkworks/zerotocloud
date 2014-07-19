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

1. [Sign up for AWS](tutorial/Step1.md)
2. [Log into AWS Console](tutorial/Step2.md)
3. Create keypair

    4. Browse to [https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#KeyPairs:](https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#KeyPairs:)

    5. Call it "zerotocloud" (tell them about a separate one for instances). 

    6. It’ll be downloaded to your Downloads directory automatically.

4. Start Ubuntu instance as a bastion/jumphost. Relevant when dealing with internal VPC and for not distributing keys to all your users.

    7. Find your AMI "trusty amd64 us-west-2 ebs-ssd" on [http://cloud-images.ubuntu.com/locator/ec2/](http://cloud-images.ubuntu.com/locator/ec2/).  The AMI Id will look like “[ami-ddaed3ed](https://console.aws.amazon.com/ec2/home?region=us-west-2#launchAmi=ami-ddaed3ed)”. Do not select the HVM version. And ebs version would work, but choosing an ssh ebs volume for speed)

    8. The AMI Id should be a hyperlink, click it. It’ll bring you to an AWS page.

    9. Select "m3.xlarge" ‘s checkbox. Click “Review and Launch”

    10. Click Launch. There’ll be a warning about Security. This can be changed now or later, in a security group which defaults to something like launch-wizard-1. As a reminder, it’s just SSH and you have the only PEM file. Might also see a message about not being in the free tier.

    11. A dialog will appear to ask about the key pair. Used the one created in Step 3 called zerotocloud. Check the "I acknowledge…" checkbox.  Click “Launch Instances”

    12. On the following page, there will be a "The following instance launch has been initiated:" section, immediately after that is your instance id. Click that link.

    13. The page should show your instance selected. It’ll start in "Pending", once started look in the “Description” tab below, there is a field called “Public DNS” on the right hand side, save this.

5. Create a user, mimics how the pem files work

    14. View [Users](https://console.aws.amazon.com/iam/home?#users) page. Which can also be accessed from the Services | IAM | Users.

    15. Select "Create New Users".

    16. Enter a single user called jumphost and keep the "Generate an access key for each User" selected.

    17. Click "Create". In the resulting dialog, do not immediately close the window. Click “Download credentials”, then you can close the window.

    18. Select created user.

    19. Select "Permissions" tab in the lower section, click “Attach User Policy”

    20. Select "Administrator Access". (Relatively arbitrary but it’ll work for our tutorial use-cases. Though you should familiar yourself with a complex set of roles)    

    21. Click "Apply Policy".

6. Create a role for instances. Which can also be accessed from the Services | IAM | Users.

    22. View [Roles](https://console.aws.amazon.com/iam/home?region=us-west-2#roles) page.

    23. Click "Create New Role", name it “jumphost”, click “Continue”.

    24. Click "Select" next to the “Amazon EC2” service role type

    25. Click "Select" next to the “Administrator Access”  policy template

    26. Click "Continue"

    27. Click "Create Role"

7. Create Security Group for ELBs.

    28. View [Security Groups](https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#SecurityGroups:) page.  Which can also be accessed from the Services | EC2 | Security Groups.

    29. Click "Create Security Group"

    30. Set "Security group name" to “elb-http-public”

    31. Set "Description" to “Public HTTP for ELBs”

    32. Leave VPC alone

    33. Click "Add Rule"

        1. select "HTTP" under the Type column

        2. ensure "Source" shows Anywhere

    34. Click "Create"

8. Foundation

    35. View [Instances](https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#Instances:) page. Which can also be accessed from the Services | EC2 | Instances.

    36. With your instance selected, pull down from the "Actions" menu bar and select “Create Image”. (This will create a snapshot. If Canonical provided snapshots, we wouldn’t have to do this)

    37. In the dialog that comes up, Give it an "Image Name" of “ubuntu-foundation”.

    38. Click "Create Image".

    39. On following dialog, you can just "Close" it.

9. Setup bastion (On Windows use WinSCP or pscp.exe from the Putty package.)

    40. Start at a terminal on your laptop

    41. `cd ~/Downloads`

    42. `chmod 0600 zerotocloud.pem`

    43. `export JUMPHOST=ec2-54-191-135-15.us-west-2.compute.amazonaws.com (from Step 4g)`

    44. `scp -i zerotocloud.pem credentials.csv ubuntu@$JUMPHOST:credentials.csv`

        3. When prompted about the RSA fingerprint, type "yes".

    45. `scp -i zerotocloud.pem zerotocloud.pem ubuntu@$JUMPHOST:zerotocloud.pem`

    46. `ssh -i zerotocloud.pem -L 8080:localhost:8080 ubuntu@$JUMPHOST`

    47. `sudo apt-get update`

    48. `sudo apt-get install python-pip python-dev git default-jdk`

        4. When prompted to continue, hit Return.

    49. `chmod 0600 zerotocloud.pem # might be superfluous`

10. Setup credentials

    50. `cd ~`

    51. `git clone `[https://github.com/Netflix-Skunkworks/zerotocloud.git](https://github.com/Netflix-Skunkworks/zerotocloud.git)

    52. `cd zerotocloud`

    53. `eval $(baseami/root/usr/local/bin/metadatavars)`

    54. `set | grep EC2 # to show what just happened`

    55. `./gradlew writeConfig `(writes ~/.aws/config and ~/.boto from credentials.csv) `(49 seconds) (Explain Gradle, since this is the first time they’re running)`

11. Build and bake BaseAMI

    56. `sudo pip install git+`[https://github.com/Netflix/aminator.git@2.1.52-dev#egg=aminator](https://github.com/Netflix/aminator.git#egg=aminator)

    57. `cd ~/zerotocloud`

    58. `./gradlew :baseami:buildDeb (3 seconds) `

    59. `sudo aminate -e ec2_aptitude_linux -b ubuntu-foundation -n ubuntu-base-ami baseami/build/distributions/baseami_1.0.0_all.deb`

        5.  (Using named image so that they don’t have to keep track of the base ami id) *(Note that aminate will append a ‘-ebs’ suffix to the name (-n) provided on the command line.) (Good time to talk about immutable infrastructure)*

12. Build and Bake Asgard Package

    60. `cd ~/zerotocloud`

    61. `./gradlew :asgard:buildDeb -Pasgard.password=XYZ` (defaults to th0r otherwise)`(real   1m1.879s)`

    62. `sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs  asgard/build/distributions/asgard_1.0.0_all.deb`

        6. If bake fails, the buildDeb task has to be re-run

13. Standup asgard using asgard

    63. `wget `[https://github.com/Netflix/asgard/releases/download/asgard-1.5/asgard-standalone.jar](https://github.com/Netflix/asgard/releases/download/asgard-1.5/asgard-standalone.jar)

    64. `java -DonlyRegions=us-west-2 -Xmx2048m -jar asgard-standalone.jar`

    65. Use browser to [http://localhost:8080](http://localhost:8080/us-west-2)/ (Should be going to us-west-2, once started)

    66. Navigate to App -> Application

        7. Click "Create New Application"

        8. Enter "asgard" as Name

        9. Type in a Description, Owner and Email

        10. Click "Create New Security Group" button

        11. If you see "vpc…" in the VPC field, make sure to click the checkbox.

        12. Click "Create New Security Group"

            1. Very likely to get "Could not create Security Group: java.lang.NullPointerException", Just click again and get a “Security Group 'asgard' already exists.” message.

        13. After creation, click "Edit Security Group"

        14. Check the "Open" checkbox next to elb-http-public

        15. Click "Update Security Group"

        16. Ensure this new permission is added to "Ingress Permissions" row.

    67. Navigate to ELB | Elastic Load Balancer

        17. Click "Create New Load Balancer"

        18. Choose "asgard" as the Application

        19. Type (or select) elb-http-public in "Security Group"

        20. Change "Health Check"’s Healthy Threshold to “5”

        21. Click "Create New Load Balancer"

        22. It’ll be named "asgard--frontend"

    68. Navigate Cluster, via Cluster | Auto Scaling Groups

        23. Click "Create New Auto Scaling Group"

        24. Select "asgard" as the Application

        25. Set "Min", “Max” and “Desired Capacity” to 1

        26. Type "asgard--frontend" in Load Balancer

        27. In "AMI Image ID", start to type asgard. Select the baked version of Asgard

        28. Ensure "SSH Key" is zerotocloud

        29. Set "Security Group" to “asgard”

        30. Set "IAM Instance Profile" to “jumphost” (Could be customized)

        31. Click "Create New Auto Scaling Group"

        32. Launch Config will implicitly be created, and an instance will start booting. "Launch Config 'asgard-20140718181745' has been created. Auto Scaling Group 'asgard' has been created."

    69. Visit baked instance

        33. Navigate to [http://localhost:8080/us-west-2/loadBalancer/show/asgard--frontend](http://localhost:8080/us-west-2/loadBalancer/show/asgard--frontend)

        34. Ensure instance is InService under "ELB State". It will start in OutOfService with a description of “Instance registration is still in progress.”. Keep refreshing.

        35. Visit "DNS Name" in your browser, e.g. asgard--frontend-1362846407.us-west-2.elb.amazonaws.com (Record this)

        36. Troubleshooting

            2. If you want to SSH to the instance, add ssh from Anywhere to the Asgard security group in the console. Then you can run ssh -i zerotocloud.pem ubuntu@<DNS Name>

        37. When successful, Ctrl-C out of the java -jar line

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

Steps avoided

    125. aws ec2 create-security-group --group-name asgard-sg

    126. aws ec2 authorize-security-group-ingress --group-id sg-123456 --protocol tcp --port 22 --cidr 0.0.0.0/0

    127. aws elb create-load-balancer --load-balancer-name asgard-lb --listeners Protocol=string,LoadBalancerPort=80,InstanceProtocol=http,InstancePort=7001

    128. aws autoscaling create-launch-configuration --launch-configuration-name asgard-lc --image-id ami-XXX --key-name zerotocloud --security-groups asgard-sg --instance-type m3.medium --iam-instance-profile jumphost

    129. `aws autoscaling create-auto-scaling-group --auto-scaling-group-name asgard-v000 --launch-configuration-name asgard-lc --min-size 1 --max-size 1 --desired-capacity 1 --availability-zones us-west-2b --load-balancer-names asgard-lb --tags ResourceId=string,ResourceType=string,Key=string,Value=string,PropagateAtLaunch=boolean`

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


