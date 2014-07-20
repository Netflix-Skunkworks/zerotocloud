# Step 10 - Setup Credentials

We're going to use two, and potentially three, tools on the jumphost that need the user credentials created in [Step 6](CreateUser.md).
AWS provided it as a csv file, but the tools we're using need it in different formats.
Aminator will rely on Boto's ~/.boto file, Asgard needs a ~/.asgard/Config.groovy file, and the AWS Cli uses a ~/.aws/config file.
To aid in the generation of these files this tutorial provides scripts to create them.
 
Additionally, there are recipes that we're going to be using, they're all in the same git repositoriy on github. 
We're going to clone it onto the jumphost.

    cd ~
    git clone `[https://github.com/Netflix-Skunkworks/zerotocloud.git](https://github.com/Netflix-Skunkworks/zerotocloud.git)
    cd zerotocloud
    eval $(baseami/root/usr/local/bin/metadatavars)
    set | grep EC2 # Show what just happened
    ./gradlew writeConfig
    
The fourth line above calls the ["metadata" service](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AESDG-chapter-instancedata.html), which is a REST endpoint only available from your instance.
We use it to extra certain variables specifc to this instance and your account. Feel free to look at the script.

The last line uses [Gradle](http://gradle.org) to run our script. Future scripts will also be using  
