# Step 4 - Create Jumphost

We're creating a "jumphost" for two purposes. 
First to limit the impact of the network during the conference. 
Second, it's how we do things at Netflix (we call it our bastion).
It acts as a choke point to accessing instances in the cloud, since we require people to use it for SSHing to instances (the SSH keys live on this box). 
It has the benefit of being able to track usages and for making changes in a cosolidated area.
In some VPC scenarios where instances do not have public IPs, a jumphost is almost a requirement.

1. Find your AMI "trusty amd64 us-west-2 ebs-ssd" on <a href="http://cloud-images.ubuntu.com/locator/ec2/" target="_blank">http://cloud-images.ubuntu.com/locator/ec2/</a>.  The AMI Id will look like “[ami-ddaed3ed](https://console.aws.amazon.com/ec2/home?region=us-west-2#launchAmi=ami-ddaed3ed)”. Do not select the HVM version. And ebs version would work, but choosing an ssh ebs volume for speed)
2. The AMI Id should be a hyperlink, click it. It’ll bring you to an AWS page.
3. Select "m3.xlarge" ‘s checkbox. Click “Review and Launch”
4. Click Launch. There’ll be a warning about Security. This can be changed now or later, in a security group which defaults to something like launch-wizard-1. As a reminder, it’s just SSH and you have the only PEM file. Might also see a message about not being in the free tier.
5. A dialog will appear to ask about the key pair. Used the one created in Step 3 called zerotocloud. Check the "I acknowledge…" checkbox.  Click “Launch Instances”
6. On the following page, there will be a "The following instance launch has been initiated:" section, immediately after that is your instance id. Click that link.
7. The page should show your instance selected. It’ll start in "Pending", once started look in the “Description” tab below, there is a field called “Public DNS” on the right hand side, save this.


# Production

There's a lot that can be done on the jumphost to make it better for day-to-day usage. Here are some suggestions:

* Protect ssh keys so that _sudo_ is required
* Snapshot or bake these instances, for when they go away
* Have multiple jumphosts for redundancy.