package de.uni_due.paluno.sse.elephant.execution;

/**
 * Created by Ole Meyer
 */
public class Diff<T,E> {
    private T target;
    private E diffValue;

    public Diff(T target, E diffValue) {
        this.target = target;
        this.diffValue = diffValue;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public E getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(E diffValue) {
        this.diffValue = diffValue;
    }

    @Override
    public String toString() {
        return "Diff{" +
                "target=" + target +
                ", diffValue=" + diffValue +
                '}';
    }
}
