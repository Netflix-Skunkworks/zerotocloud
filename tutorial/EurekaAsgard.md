# <a href="https://github.com/Netflix/asgard/wiki/Eureka-Integration" target="_blank">Eureka/Asgard Integration</a>

If Asgard knows about Eureka it will enhance many of the pages with Eureka information. 
The link above goes into more specific of how the integratin works.
The following steps are to enable that integregation in a way that is compatible with the rest of this tutorial.

## Build/Bake

If you don't have the eureka--frontend ELB's DNS Name handy, go back to Asgard and find it.
Make sure to use that DNS name below instead of just copying like you have in previous steps.

    EUREKA_ELB=*ELB DNS NAME from Step 15*
    cd ~/zerotocloud
    ./gradlew :asgard:buildDeb
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs asgard/build/distributions/asgard_1.0.0_all.deb

## Deploy

FYI, this can not be done as a Rolling Push, since Asgard will disable creation of new instances while killing itself.