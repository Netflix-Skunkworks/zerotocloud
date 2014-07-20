# Step 12 - Build and Bake Asgard

Very similiar to how we built the Base AMI, we're going to build one AMI to build another. 
In this case we're putting Asgard on top of the BaseAMI.
 
    cd ~/zerotocloud
    ./gradlew :asgard:buildDeb -Pasgard.password=th0r
    sudo aminate -e ec2_aptitude_linux -b ubuntu-base-ami-ebs  asgard/build/distributions/asgard_1.0.0_all.deb`

In the second line, the argument is optional and can be changed. If left out, it'll default to "th0r".

# Troubleshooting

If bake fails, the buildDeb task has to be re-run. This is because Aminator moves the .deb to a different directory, so a second call will fail when it unable to find the .deb file.

