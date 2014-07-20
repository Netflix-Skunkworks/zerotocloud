# Step 13 - Stand alone Asgard

To responsibly stand up an instance, we want it in an ASG, with a security group and behind a load balancer. 
In AWS, this also requires a Launch Configuration to define the ASG.
We could stand this up with the [AWS CLI](http://aws.amazon.com/cli/) tool or the AWS Console, but it's quite tedious. 
An example is provided at the end of this page.

The general flow for any new application will be to register the Application, create an ELB (if needed), create the first ASG, wait for the first instance to come up, and finally visit it via the DNS Name. 
We're going to run through those steps here for Asgard, but other steps in the tutorial will have you coming back here. 
When they do, follow the "Create Application", "Create an ELB", "

## Run Asgard

Instead we're going to use Asgard to standup Asgard. 
This will require us to run Asgard on the jumphost for a little while.

    wget https://github.com/Netflix/asgard/releases/download/asgard-1.5/asgard-standalone.jar
    java -DonlyRegions=us-west-2 -Xmx2048m -jar asgard-standalone.jar

## Create Application

In your local browser, navigate to [http://localhost:8080/](http://localhost:8080/us-west-2). 
You should be viewing us-west-2, if not use the pull down at the top of page to change your region.

1. Navigate to _App | Application_
2. Click "Create New Application"
3. Enter "asgard" as Name
4. Fill in a the _Description_, _Owner_ and _Email_ fields.
5. Click "Create New Security Group" button.
6. If you see "vpc…" in the VPC field, make sure to click the checkbox.
7. Click "Create New Security Group". It is very likely to get an error message saying _"Could not create Security Group: java.lang.NullPointerException"_. Just click again the button again and you should get another message that says “Security Group 'asgard' already exists.”, which confirms that it was created.
8. After creation, click "Edit Security Group"
9. Check the "Open" checkbox next to elb-http-public
10. Click "Update Security Group"
11. Ensure this new permission is added to "Ingress Permissions" row.

## Create an ELB

1. Navigate to _ELB | Elastic Load Balancer_
2. Click "Create New Load Balancer"
3. Choose "asgard" as the Application

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


# AWS CLI

If we had to do this with the aws cli, it would look like this:

    aws ec2 create-security-group --group-name asgard-sg
    aws ec2 authorize-security-group-ingress --group-id sg-123456 --protocol tcp --port 22 --cidr 0.0.0.0/0
    aws elb create-load-balancer --load-balancer-name asgard-lb --listeners Protocol=string,LoadBalancerPort=80,InstanceProtocol=http,InstancePort=7001
    aws autoscaling create-launch-configuration --launch-configuration-name asgard-lc --image-id ami-XXX --key-name zerotocloud --security-groups asgard-sg --instance-type m3.medium --iam-instance-profile jumphost
    aws autoscaling create-auto-scaling-group --auto-scaling-group-name asgard-v000 --launch-configuration-name asgard-lc --min-size 1 --max-size 1 --desired-capacity 1 --availability-zones us-west-2b --load-balancer-names asgard-lb --tags ResourceId=string,ResourceType=string,Key=string,Value=string,PropagateAtLaunch=boolean

You'll notice that the output of one line become the input for the next one.
For example the security group created in the first line is used in the second line.
Since this is a unique name, it is very error prone.