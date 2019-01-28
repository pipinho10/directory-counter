import java.io.File;

public class NumberOfDirectoriesCounter {
    private static final String PATH = "C:\\Users\\esula\\Desktop\\New folder";

    public static void main(String[] args) {
        System.out.println(getNumberOfFiles(new File(PATH)));
    }

    private static int getNumberOfFiles(File node){
        int numberOfFiles = 0;

        if (node.isDirectory()) {
            for (String childNode : getChildNodes(node)) {
                numberOfFiles += getNumberOfFiles(new File(node, childNode));
            }
        } else {
            numberOfFiles++;
        }

        return numberOfFiles;
    }

    private static String[] getChildNodes(File node) {
        String[] childNodes = node.list();
        if (childNodes == null) {
            return new String[0];
        }
        return childNodes;
    }
}