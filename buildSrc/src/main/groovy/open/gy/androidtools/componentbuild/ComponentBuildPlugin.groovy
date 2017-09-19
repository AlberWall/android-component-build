package open.gy.androidtools.componentbuild

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class ComponentBuildPlugin implements Plugin<Project> {
    // Task Name
    public static final String TN_ASSEMBLE_COMPONENT = "assembleComponent"
    public static final String TN_BUILD_COMPONENT = "buildComponent"
    public static final String TN_BUILD_JAR = "buildLibJar"
    public static final String TN_BUILD_AAR = "buildLibAAR"
    public static final String TN_MAKE_DEPENDENCY_CHAIN = 'makeComponentDependencyChain'

    // Extension Name
    public static final String EXTENSION_COMPONENT = 'componentConfigurations'

    @Override
    void apply(Project project) {

        ComponentConfig cfg = project.extensions.create(EXTENSION_COMPONENT, ComponentConfig, project)
        cfg.dependencyHandler = new ComponentDependencyHandler(project)

        project.task('cleanLibJar') {
            doLast {
                println "clean lib output of ${project.name}"

                def libName = cfg.getLibName()
                def dir = cfg.getLibJarDirectory();
                def jarPath = new File(dir, libName).absolutePath
                project.delete jarPath
            }
        }

        project.task(TN_ASSEMBLE_COMPONENT) {
            doLast {
                List<Project> dependencyChain = cfg.dependencyHandler.dependencyChain(false)
                for (Project pj in dependencyChain) {

                    println "compile project:${pj.name}"
                    execBuildComponent(pj)
                }

                // execute current project
                execBuildComponent(project)
            }
        }

        def taskBuildAar = project.task(dependsOn: ['assembleRelease'], TN_BUILD_AAR) {

            doLast {
                project.copy {
                    from 'build/outputs/aar'
                    include project.name + "-release.aar"
                    into cfg.getLibAarDirectory().absolutePath
                    rename(project.name + "-release.aar", cfg.baseName + "-" + cfg.appendix + "-" + cfg.version + "-release.aar")
                }
            }
        }

        def taskBuildJar = project.task(type: Jar, dependsOn: ['assembleRelease', 'cleanLibJar'], TN_BUILD_JAR) {

            conventionMapping.destinationDir = { cfg.getLibJarDirectory() }

            conventionMapping.appendix = { cfg.appendix }
            conventionMapping.baseName = { cfg.baseName }
            conventionMapping.version = { cfg.version }

            def curManifest = manifest
            conventionMapping.manifest = { // manifest信息
                def map = ['Version': cfg.version,
                           'Gradle' : project.gradle.gradleVersion,
                           'Vendor' : cfg.vendor,
                           'Date'   : new Date().getDateTimeString()
                ]
                curManifest.attributes(map)

                return curManifest
            }

            from('build/intermediates/classes/release/')
            exclude('**/BuildConfig.class')
            exclude('**/BuildConfig\$*.class')
            exclude('**/R.class')
            exclude('**/R\$*.class')
            include('**/*.class')

        }

        def taskBuildComponent = project.task(TN_BUILD_COMPONENT) {
            doLast {
                println "finish build lib: ${project.name}"
            }
        }

        project.task(TN_MAKE_DEPENDENCY_CHAIN) {

            doLast {
                cfg.dependencyHandler.dependencyChain(true)
            }
        }

        project.afterEvaluate {

            if (null == cfg.output) {
                throw new GradleException("'output' must be configured for component[${project.name}]")
            }

            if (null == cfg.sdkProject) {
                throw new GradleException("'sdkProject' must be configured for component[${project.name}]")
            }

            if (ComponentConfig.OUTPUT_AAR.equalsIgnoreCase(cfg.output)) {
                taskBuildComponent.dependsOn(taskBuildAar)
            } else {
                taskBuildComponent.dependsOn(taskBuildJar)
            }

        }
    }

    private void execBuildComponent(Project project) {

        if (isWindows()) {
            execBuildComponentInWin(project)
        } else {
            execBuildComponentInLinux(project)
        }
    }

    private void execBuildComponentInWin(Project project) {
        project.exec {
            workingDir project.projectDir
            commandLine '../gradlew.bat', TN_BUILD_COMPONENT
        }
    }

    private void execBuildComponentInLinux(Project project) {

        project.exec {
            workingDir project.projectDir
            commandLine '../gradlew', TN_BUILD_COMPONENT
        }
    }

    private boolean isWindows() {
        return org.gradle.internal.os.OperatingSystem.current().isWindows()
    }

//    private void createCleanDependenciesForSdk(ComponentConfig cfg) {
//        try {
//            cfg.sdkProject.tasks.getByName(TN_CLEAN_DEPENDENCIES)
//        }
//        catch (UnknownTaskException ex) {
//            println 'create task cleanComponentDependencies for sdkproject'
//            cfg.sdkProject.task(TN_CLEAN_DEPENDENCIES) {
//
//                doLast {
//                    println "clean components dependencies"
//                    DependencyManager.instance.clean()
//                }
//            }
//        }
//    }

}
