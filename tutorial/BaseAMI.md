# Step 11 - Build and Bake AMI

In continuing with the layers that we'll use for our instances, we build a "Base AMI" that is used by every instance.
With this, we can install packages and configuration we believe all instances will need.
Ideally this leaves the baking of the actual application to just the application, keeping the bake quick and small.

# Install aminator

<a href="https://github.com/Netflix/aminator" target="_blank">Aminator</a> is a Python-based open sourced tool from Netflix to "Bake" Amazon images.
It can use system packages, like RPMs or DEBs, or via a system of plugins common configuration management tools, like Chef, Puppet or Ansible, to build a machine image.
It also supports various base environments, we'll be using Ubuntu but other RPM based OS's like CentOS are supported.

    sudo pip install git+https://github.com/Netflix/aminator.git@2.1.52-dev#egg=aminator
    
# Build a Debian

To minimize complexity and to mimic how Netflix bakes, we're going to be building DEBs for baking. 
Once again we're using Gradle during this tutorial, but you can create your packages however you like. 
As stated above, you could use Chef or Puppet to instead. 

    cd ~/zerotocloud
    ./gradlew :baseami:buildDeb

In Gradle, we're using the ospackage plugin to build our packages. 
It's lets us build RPM and DEB files with a single syntax declarative syntax, in Java.
E.g. this is what we're using to build the Base AMI:

    ospackage {
        requires('default-jdk')
        requires('tomcat7')

        from(file('root')) {
            into('/')
        }

        postInstall('rm -fr /var/lib/tomcat7/webapps/ROOT')
        postInstall("perl -p -i -e 's/port=\"8080\"/port=\"7001\"/gi' /var/lib/tomcat7/conf/server.xml")
    }

# Bake

We can now "bake".

    sudo aminate -e ec2_aptitude_linux -b ubuntu-foundation -n ubuntu-base-ami baseami/build/distributions/baseami_1.0.0_all.deb

The _-e_ argument tells Aminator when environment it is in, in this case we're tell it to us Aptitude.
The _-b_ argument is name of the AMI to start with, this is the one we created in [Step 8](FoundationAMI.d).
The _-n_ argument says to use a "named" image, which is easier to keep track of. There will still be a ami-1234567 like name associated the resulting AMI.
One note, aminate will append a ‘-ebs’ suffix to the name. 
The baking process take a few minutes, you can add _--debug_ to the command line to get more details of what it is doing.
