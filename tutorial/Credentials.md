# Step 10 - Setup Credentials

We're going to use two, and potentially three, tools on the jumphost that need the user credentials created in [Step 6](CreateUser.md).
AWS provided it as a csv file, but the tools we're using need it in different formats.
Aminator will rely on Boto's ~/.boto file, Asgard needs a ~/.asgard/Config.groovy file, and the AWS Cli uses a ~/.aws/config file.
To aid in the generation of these files, this tutorial provides a script to create them.
 
In addition to that script, we have other recipes that we need later in the tutorial. 
So, we're going to pull them from github onto the jumphost.

    cd ~
    git clone https://github.com/Netflix-Skunkworks/zerotocloud.git
    cd zerotocloud
    eval $(baseami/root/usr/local/bin/metadatavars)
    set | grep EC2 # Show what just happened
    ./gradlew writeConfig
    
Above, in the fourth line, the script calls the <a href="http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AESDG-chapter-instancedata.html" target="_blank">"metadata" service</a>, which is a REST endpoint only available from your instance.
We use it to load up a few variables about our environment, like your AWS Account ID. 
Feel free to look at the script, to ensure there's no funny business going on here.

The last line uses <a href="http://gradle.org" target="_blank">Gradle</a> to run our script. 
We'll be using Gradle as the backbone of the other scripts in the tutorial.
Reviewing the Gradle scripts is an exercise left to the user.  
