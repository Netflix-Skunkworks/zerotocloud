cloud {
    launchConfig {
        ebsVolumes {
            instanceTypeNeeds = { String instanceType ->
                false // instanceType.startsWith('m3.')
            }
        }
    }
}