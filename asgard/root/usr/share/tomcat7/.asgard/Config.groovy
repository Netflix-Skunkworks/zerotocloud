cloud {
    accountName="demo"
    publicResourceAccounts=["netflixoss"]
    defaultKeyName = 'zerotocloud'
    defaultSecurityGroups = ['elb-http-public']
    launchConfig {
        ebsVolumes {
            instanceTypeNeeds = { String instanceType ->
                false // instanceType.startsWith('m3.')
            }
        }
    }
}