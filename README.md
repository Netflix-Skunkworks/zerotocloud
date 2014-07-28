# Zero To Cloud With @NetflixOSS

Netflix has released a multitude of tools and applications to help in using the Cloud. Being infrastructure, they are
more difficult to setup that just consuming a client library like a JAR in Central. In general, each application
can run independently, but they work better together. This tutorial is focused on bringing up the @NetflixOSS stack
 on a fresh AWS account, in a similar style to how Netflix does it internally. 

We are actively tuning the steps and will post the specific steps here for OSCON 2014.

# Assumptions

* Working in US West (Oregon) aka us-west-2. You’re flexible to do another region, but "Keep it Local" (We’re in Portland after all)
* We’re performing non destructive operations, so if you have an existing AWS account setup, that will be fine and they won’t conflict. But it might be easier to find instances, etc if using a new VPC.  Creating a new VPC isn't required, but we will call out the points to pay attention to if you do.
* In the case of existing infrastructure, like keys, please follow the instructions closely and do not re-use existing provisioned items. I wouldn’t want to be responsible for opening up a security hole in existing infrastructure.
* There are plenty of opportunities to lock down these applications at the network layer or the application layer. Or restrict what the instances can do. References will be made to additional security precautions, but they have not all been integrated into this tutorial.
* This is not a developing for the cloud tutorial, that makes for a great followup. Given enough time, we can talk about it.
* This will cost money. Not much, but some.

# Tutorial

1. [Sign up for AWS](tutorial/Signup.md)
2. [Log into AWS Console](tutorial/Login.md)
3. [Create a VPC (Optional)](tutorial/VPCBuild.md)
4. [Create Key Pair](tutorial/Keypair.md)
5. [Create Jumphost](tutorial/Jumphost.md)
6. [Create a role](tutorial/CreateRole.md)
7. [Create a user](tutorial/CreateUser.md)
8. [Create Security Group for ELBs](tutorial/SecurityGroups.md)
9. [Create Foundation AMI](tutorial/FoundationAMI.md)
10. [Setup Jumphost](tutorial/SshJumphost.md)
11. [Setup Credentials](tutorial/Credentials.md)
12. [Build and Bake BaseAMI](tutorial/BaseAMI.md)
13. [Build and Bake Asgard](tutorial/AsgardBake.md)
14. [Standup Asgard using Asgard](tutorial/AsgardStandalone.md)
15. [Build and Bake Edda](tutorial/Edda.md)
16. [Build and Bake Eureka](tutorial/Eureka.md)

When all done, ilrelevant of how far you get make sure to read the Clean up instructions below, so that you don't get charged for resources that you're not using.

# Extras

Setting up infrastructure can be frought with problems, so if you've made this far in the allocated time, congratulate yourself.
Here are some additional exercises which can help expand your knowledge of the Netflix stack or the AWS in general.

* [Karyon](tutorial/Karyon.md)
* [Simian Army](tutorial/SimianArmy.md)
* [Enable Eureka Integration in Asgard](tutorial/EurekaAsgard.md)
* (TBD) [AWS CLI](tutorial/AwsCli.md)
* (TBD) [Turbine](tutorial/Turbine.md)
* (TBD) [ICE](tutorial/ICE.md)

# Clean up

The actions taken in this tutorial have create resources which AWS will charge you for. 
A couple of hours of usage won't add up to much.
If you accidentally leave our instances running your volumes allocated, the costs can add up fast. 

1. In Asgard, Delete all AutoScaling Groups
2. In Asgard, Delete all Load Balancers
3. In Asgard, Delete all Security Groups
4. In Asgard, Delete all Applications (these are stored in SimpleDB and miniscule in size)
5. In the EC2 Console, go to the AMI page and de-register all of the AMIs you created
6. In the EC2 Console, go to the snapshots section, delete all of the snapshots
7. In the VPC Console, delete the VPC

FYI, items 5 and 6 would normally be cleaned up by Janitor monkey.

# TODO

* UDF variables from Asgard aren’t in the ubuntu user’s env
* Have versions of modules match what is being wrapped
* Stop tomcat on karyon instance
* Multiple Eureka instances in different zones.
