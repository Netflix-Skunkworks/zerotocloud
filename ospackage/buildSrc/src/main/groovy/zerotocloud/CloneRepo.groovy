package zerotocloud

import org.ajoberstar.grgit.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class CloneRepo extends DefaultTask {
    @Input
    String repository

    @Input
    String branch = 'master'

    @OutputDirectory
    File gitDir = new File(project.buildDir, "${project.name}-git")

    @TaskAction
    def clone() {
        Grgit.clone(dir: gitDir, uri: repository, refToCheckout: branch)
    }
}
