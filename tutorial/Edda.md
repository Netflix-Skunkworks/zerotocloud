# Step 14 - Build and Bake <a href="https://github.com/Netflix/edda/wiki/" target="_blank">Edda</a>

Operating "in the cloud" has its challenges, and one of those challenges is that nothing is static. Virtual host instances are constantly coming and going, IP addresses can get reused by different applications, and firewalls suddenly appear as security configurations are updated. At Netflix we needed something to help us keep track of our ever-shifting environment within Amazon Web Services (AWS). Our solution is Edda.

Using Edda we learn about our buckets, images, load balancers, tags, volumes, instances, etc all over time with a rich matrix-style API.
## Bake Edda

    cd ~/zerotocloud
    ./gradlew :edda:buildDeb
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs edda/build/distributions/edda_1.0.0_all.deb

## Deploy Edda

We're going to repeat the Asgard deploy but for Edda. 
This will have us returning to [Step 13](AsgardStandalone.md) to perform the "Create Application", "Create an ELB", "Create Auto Scaling Group", and "View instance" pieces.
The difference will be be that we're going to use a different "Health Check" URL, because Edda doesn't provide a healthcheck endpoint. 
Instead we're going to pick a safe endpoint that will return a HTTP response of 200 when Edda is up.

1. Naviate to Asgard. This can be done by finding the DNS Name from the end of [Step 13](AsgardStandalone.md) or finding the Asgard ELB and using the DNS Name.
2. Follow "Create Application", using the name "edda" instead of "asgard".
3. Follow "Create an ELB", using the name "edda" instead of "asgard". And instead of HTTP:7001/healthcheck for the Health Check URL, use "HTTP:7001/api/v2/view/instances;_limit=1". The protocol and port are the same, but the path is different.
4. Follow "Create Auto Scaling Group" using the name "edda" instead of "asgard".
5. Follow "View instance" to get the DNS Name for edda's ELB, i.e. _edda--frontend_. 
6. Using that DNS Name, visit _http://*ELB DNS name*/api/v2/view/instances;_pp_

Review <a href="https://github.com/Netflix/edda/wiki/REST" target="_blank">Edda's REST API</a> for further documentation.