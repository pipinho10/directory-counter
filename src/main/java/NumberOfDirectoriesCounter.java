import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.util.Arrays.*;

public class NumberOfDirectoriesCounter extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 10;

    private final File[] subDirectories;

    private NumberOfDirectoriesCounter(File... subDirectories) {
        this.subDirectories = subDirectories;
    }

    public static void main(String[] args) {
        NumberOfDirectoriesCounter numberOfDirectoriesCounter = new NumberOfDirectoriesCounter(getChildNodes(new File(args[0])));

        System.out.println(numberOfDirectoriesCounter.compute());
    }

    @Override
    public Integer compute() {
        if (subDirectories.length > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            return calculateNumberOfFiles(subDirectories);
        }
    }

    private Collection<NumberOfDirectoriesCounter> createSubtasks() {
        List<NumberOfDirectoriesCounter> dividedTasks = new ArrayList<>();

        dividedTasks.add(new NumberOfDirectoriesCounter(
                copyOfRange(subDirectories, 0, subDirectories.length / 2)
        ));

        dividedTasks.add(new NumberOfDirectoriesCounter(
                copyOfRange(subDirectories, subDirectories.length / 2, subDirectories.length)
        ));

        return dividedTasks;
    }

    private int calculateNumberOfFiles(File[] nodes) {
        int numberOfFiles = 0;

        for (File node : nodes) {
            if (node.isDirectory()) {
                numberOfFiles += calculateNumberOfFiles(getChildNodes(node));
            } else {
                numberOfFiles++;
            }
        }

        return numberOfFiles;
    }

    private static File[] getChildNodes(File node) {
        File[] childNodes = node.listFiles();
        if (childNodes == null) {
            return new File[0];
        }
        return childNodes;
    }
}