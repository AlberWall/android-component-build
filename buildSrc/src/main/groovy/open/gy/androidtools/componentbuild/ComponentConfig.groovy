package open.gy.androidtools.componentbuild

import org.gradle.api.Project

/**
 * Created by ws on 17/9/14.
 */

class ComponentConfig {
    public static final String DEFAULT_JAR_DIR = 'build/component-output/jarLibs'
    public static final String DEFAULT_AAR_DIR = 'build/component-output/aarLibs'
    public static final String DEFAULT_JNI_DIR = 'build/component-output/jniLibs'

    public static final String OUTPUT_JAR = 'jar'
    public static final String OUTPUT_AAR = 'aar'

    ComponentDependencyHandler dependencyHandler

    final Project currentComponent

    /** lib output, need be 'jar', 'aar', that all is ignored case */
    String output
    Project sdkProject

    String jarDir = DEFAULT_JAR_DIR
    String aarDir = DEFAULT_AAR_DIR
    String jniDir = DEFAULT_JNI_DIR

    String appendix = ''
    String baseName
    String version = '1.0'

    String vendor = ''

    ComponentConfig(Project project) {
        currentComponent = project

        baseName = currentComponent.name
    }

    def getLibName() {
        if (appendix.isEmpty() || appendix.trim().isEmpty()) {
            return baseName + "-" + version + ".jar"
        } else {
            return baseName + "-" + appendix + "-" + version + ".jar"
        }
    }

    def getLibJarDirectory() {
        if (null == sdkProject) return new File(currentComponent.projectDir.getAbsolutePath(), jarDir)
        return new File(sdkProject.projectDir.getAbsolutePath(), jarDir)
    }

    def getLibAarDirectory() {
        if (null == sdkProject) return new File(currentComponent.projectDir.getAbsolutePath(), aarDir)
        return new File(sdkProject.projectDir.getAbsolutePath(), aarDir)
    }

    def getLibPath() {
        libDir.absolutePath
    }

    def dependencies(Closure cl) {
        cl.delegate = dependencyHandler
        cl.resolveStrategy = Closure.DELEGATE_ONLY
        cl()
    }

    def dependencies() {
        dependencyHandler.dependencies()
    }


    @Override
    public String toString() {
        return """\
ComponentConfig{
    dependencyHandler=$dependencyHandler,
    sdkProject=$sdkProject,
    jarDir='$jarDir',
    aarDir='$aarDir',
    jniDir='$jniDir',
    appendix='$appendix',
    baseName='$baseName',
    version='$version',
    vendor='$vendor'
}"""
    }
}
