def accountNumber = '@ACCOUNT_NUMBER@'
grails {
    awsAccounts = [accountNumber]
    awsAccountNames = [(accountNumber): 'prod']
}
cloud {
    accountName="prod"
    publicResourceAccounts=[]
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