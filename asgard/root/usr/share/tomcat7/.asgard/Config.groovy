def accountNumber = '@ACCOUNT_NUMBER@'
grails {
    awsAccounts = [accountNumber]
    awsAccountNames = [(accountNumber): 'prod']
}
cloud {
    accountName="prod"
    publicResourceAccounts=[]
    defaultKeyName = 'zerotocloud'
    defaultSecurityGroups = []
    launchConfig {
        ebsVolumes {
            instanceTypeNeeds = { String instanceType ->
                false // instanceType.startsWith('m3.')
            }
        }
    }
}
import com.netflix.asgard.Region