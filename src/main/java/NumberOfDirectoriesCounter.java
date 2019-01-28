import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class NumberOfDirectoriesCounter extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 2;

    private final File[] subDirectories;

    public NumberOfDirectoriesCounter(File... subDirectories) {
        this.subDirectories = subDirectories;
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\esula";

        NumberOfDirectoriesCounter numberOfDirectoriesCounter = new NumberOfDirectoriesCounter(getChildNodes(new File(path)));

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
            return getNumberOfFiles(subDirectories);
        }
    }

    private Collection<NumberOfDirectoriesCounter> createSubtasks() {
        List<NumberOfDirectoriesCounter> dividedTasks = new ArrayList<>();

        dividedTasks.add(new NumberOfDirectoriesCounter(
                Arrays.copyOfRange(subDirectories, 0, subDirectories.length / 2)
        ));

        dividedTasks.add(new NumberOfDirectoriesCounter(
                Arrays.copyOfRange(subDirectories, subDirectories.length / 2, subDirectories.length)
        ));

        return dividedTasks;
    }

    private static int getNumberOfFiles(File[] directories) {
        int numberOfFiles = 0;

        for (File node : directories) {
            if (node.isDirectory()) {
                for (File childNode : getChildNodes(node)) {
                    numberOfFiles += getNumberOfFiles(childNode);
                }
            } else {
                numberOfFiles++;
            }
        }

        return numberOfFiles;
    }

    private static int getNumberOfFiles(File node) {
        int numberOfFiles = 0;

        if (node.isDirectory()) {
            for (File childNode : getChildNodes(node)) {
                numberOfFiles += getNumberOfFiles(childNode);
            }
        } else {
            numberOfFiles++;
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