# <a href="http://aws.amazon.com/cli/" target="_blank">AWS CLI</a>

Amazon cosolidated all its command line tools into a single Python-based simply called _aws_.

## Install
   
We can use _pip_ on the jumphost to install it.

    sudo pip install awscli


## Running

It uses ~/.aws/config for credentials, which we created in [Step 10](Credentials.md). It can also use an IAM Role if it's available on the instance.

How to find instances: 

    aws ec2 describe-instances

