import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.util.Arrays.*;

public class FilesCounter extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 10;

    private final File[] subDirectories;

    private FilesCounter(File... subDirectories) {
        this.subDirectories = subDirectories;
    }

    public static void main(String[] args) {
        long start = System.nanoTime();

        FilesCounter filesCounter = new FilesCounter(getChildNodes(new File(args[0])));

        System.out.println(filesCounter.compute());

        long end = System.nanoTime();

        System.out.println((end - start) / 1000000);
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

    private Collection<FilesCounter> createSubtasks() {
        List<FilesCounter> dividedTasks = new ArrayList<>();

        dividedTasks.add(new FilesCounter(
                copyOfRange(subDirectories, 0, subDirectories.length / 2)
        ));

        dividedTasks.add(new FilesCounter(
                copyOfRange(subDirectories, subDirectories.length / 2, subDirectories.length)
        ));

        return dividedTasks;
    }

    private int calculateNumberOfFiles(File[] nodes) {
        int numberOfFiles = 0;

        for (File node : nodes) {
            if (node.isDirectory()) {
                numberOfFiles += new FilesCounter(getChildNodes(node)).compute(); // In case of directory create separate thread
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