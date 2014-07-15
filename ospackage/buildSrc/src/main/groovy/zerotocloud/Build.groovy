package zerotocloud

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.initialization.layout.BuildLayout
import org.gradle.initialization.layout.BuildLayoutFactory
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.wrapper.WrapperExecutor

class Build extends DefaultTask {

    @Input
    File moduleDir

    @Input
    List<String> arguments = []

    @OutputDirectory
    def File getDistributionDir() {
        return new File(moduleDir, 'build/libs')
    }

    @TaskAction
    def build() {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(moduleDir);

        ProjectConnection connection = connector.connect();
        try {
            BuildLauncher launcher = connection.newBuild();
            String[] argumentArray = new String[arguments.size()];
            arguments.toArray(argumentArray);
            launcher.withArguments(argumentArray);
            launcher.run()
        } finally {
            if(connection) {
                connection.close()
            }
        }
    }
}
