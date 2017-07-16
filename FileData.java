/**
 * Created by Lily Bhattacharjee on 7/12/17.
 */

/*
The control center -- determines the file that should be read and how many words should be displayed in the word cloud
 */

import java.io.IOException;

public class FileData {

    private String fileName;

    public FileData(String fileName) {
        this.fileName = fileName;
    }

    // creates readfile instance if file can be found and calls countfrequencies method to create json file of data
    public void createRankFile() throws IOException {

        try {

            ReadFile file = new ReadFile(fileName);
            file.countFrequencies();

        } catch(IOException e) {

            System.out.println(e.getMessage());

        }

    }

    public static void main(String[] args) throws IOException {

        try {

            FileData messages = new FileData("src/messages.txt");
            messages.createRankFile();

        } catch (IOException e) {

            System.out.println(e.getMessage());

        }

    }

}
