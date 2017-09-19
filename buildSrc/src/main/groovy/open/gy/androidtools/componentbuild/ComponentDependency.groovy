package open.gy.androidtools.componentbuild

import org.gradle.api.Project

class ComponentDependency implements Comparable<ComponentDependency> {
    private static final int LEVEL_INTERVAL = 100;

    public final Project target;

    int level = 0;
    int numOfDependency = 0;
    DependencyType type;

    ComponentDependency(Project proj) {
        target = proj
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ComponentDependency that = (ComponentDependency) o

        return target.name == that.target.name
    }

    int hashCode() {
        return (target.name != null ? target.name.hashCode() : 0)
    }

    @Override
    int compareTo(ComponentDependency o) {
        if (o == null) return 1

        return (o.level * LEVEL_INTERVAL + o.numOfDependency) - (level * LEVEL_INTERVAL) + numOfDependency
    }

    @Override
    public String toString() {
        return "{${target.name}: " +
                "level=" + level +
                ", numOfDeps=" + numOfDependency +
                '}';
    }

}