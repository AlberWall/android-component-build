package open.gy.androidtools.componentbuild

import org.gradle.api.Project

class ComponentDependencyHandler {
    final Project currentComponent

    private Set<ComponentDependency> componentDependencies = new HashSet<>()

    ComponentDependencyHandler(Project proj) {
        currentComponent = proj
    }

    def compile(Project project) {
        makeDependenciesCompile(project)
    }

    def provided(Project project) {
        makeDependenciesProvided(project)
    }

    def project(String name) {

        return currentComponent.project(name)
    }

    def dependencies() {
        componentDependencies
    }

    def dependencyChain(boolean isDebug) {

        Set<Project> dependsOnList = getDependentComponents(currentComponent)
        Map<String, ComponentDependency> evaluationMap = evaluateComponents()

        List<Project> evaluatedList = getSortedComponents(evaluationMap)
        if (isDebug) {
            println " evaluated=${evaluatedList}"
        }

        List<Project> resList = new ArrayList<>()
        for (Project pj in evaluatedList) {
            if (dependsOnList.contains(pj)) {
                resList.add(pj)
            }
        }

        if (isDebug) {
            println "dependencies[${currentComponent.name}]=${dependsOnList}," +
                    "\ndependencyChain[${currentComponent.name}]=${resList}"
        }
        return resList

    }

    private Map<String, ComponentDependency> evaluateComponents() {
        HashMap<String, ComponentDependency> evaluationMap = new HashMap<>()
        evaluateComponentsInternal(evaluationMap, currentComponent)

        return evaluationMap
    }

    private List<Project> getSortedComponents(Map<String, ComponentDependency> evaluationMap) {
        if (null == evaluationMap) return new ArrayList<Project>(0)

        ArrayList<ComponentDependency> sortedList = new ArrayList<>(evaluationMap.values())
        Collections.sort(sortedList)

        List<Project> resList = new ArrayList<>()
        for (ComponentDependency pd in sortedList) {
            resList.add(pd.target)
        }

        return resList
    }


    private void evaluateComponentsInternal(Map<String, ComponentDependency> evaluationMap, Project curComponent) {
        Set<ComponentDependency> theDeps = curComponent.extensions.componentConfigurations.dependencies()
        for (ComponentDependency cmptDep in theDeps) {

            switch (cmptDep.type) {
                case DependencyType.Compile:

                    evaluateCompile(evaluationMap, curComponent, cmptDep.target)
                    break
                case DependencyType.Provided:

                    evaluateProvided(evaluationMap, curComponent, cmptDep.target)
                    break
            }

            evaluateComponentsInternal(evaluationMap, cmptDep.target)
        }
    }

    private void makeDependenciesCompile(Project projectB) {
        ComponentDependency cmptDep = new ComponentDependency(projectB)
        cmptDep.type = DependencyType.Compile
        componentDependencies.add(cmptDep)
    }

    private void makeDependenciesProvided(Project projectB) {
        ComponentDependency cmptDep = new ComponentDependency(projectB)
        cmptDep.type = DependencyType.Provided
        componentDependencies.add(cmptDep)
    }

    private void evaluateCompile(Map<String, ComponentDependency> evaluationMap, Project project, Project dependsOnProject) {
        ComponentDependency pd = evaluationMap.get(project.name)
        if (null == pd) {
            pd = new ComponentDependency(project)
            evaluationMap.put(project.name, pd)
        }

        if (null != dependsOnProject) {
            ComponentDependency pd2 = evaluationMap.get(dependsOnProject.name)
            if (null == pd2) {
                pd2 = new ComponentDependency(dependsOnProject)
                evaluationMap.put(dependsOnProject.name, pd2)
            }

            if (pd.level + 1 > pd2.level) {
                pd2.level = pd.level + 1
            }

            pd2.numOfDependency++
        }
    }

    private void evaluateProvided(Map<String, ComponentDependency> evaluationMap, Project project, Project dependsOnProject) {
        ComponentDependency pd = evaluationMap.get(project.name)
        if (null == pd) {
            pd = new ComponentDependency(project)
            evaluationMap.put(project.name, pd)
        }

        if (null != dependsOnProject) {
            ComponentDependency pd2 = evaluationMap.get(dependsOnProject.name)
            if (null == pd2) {
                pd2 = new ComponentDependency(dependsOnProject)
                evaluationMap.put(dependsOnProject.name, pd2)
            }

            if (pd.level + 1 > pd2.level) {
                pd2.level = pd.level + 1
            }

            pd2.numOfDependency++
        }
    }

    Set<Project> getDependentComponents(Project target) {
        Set<Project> resList = new HashSet<>()

        getDependentComponentsInternal(resList, target)

        return resList
    }

    private void getDependentComponentsInternal(Set<Project> res, Project curComponent) {
        Set<ComponentDependency> tmp = curComponent.extensions.componentConfigurations.dependencies()
        if (null == tmp) return

        for (ComponentDependency pd in tmp) {
            res.add(pd.target)
            getDependentComponentsInternal(res, pd.target)
        }
    }
}