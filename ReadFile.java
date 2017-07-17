/**
 * Created by Lily Bhattacharjee on 7/12/17.
 */


/*
Creates a .txt file containing ranked json data of most frequently used words in facebook chats from archived file downloaded.
 */

import java.io.*;
import java.util.ArrayList;

public class ReadFile {

    private String path; // path of file to be read and parsed
    private ArrayList<String> uniqueItems = new ArrayList<String>(); // contains unique tokens from file

    // constructor assigning path instance variable
    public ReadFile(String filePath) {
        path = filePath;
    }

    // returns an ArrayList of all applicable message lines in file split into individual 'word' elements
    ArrayList<String> openFile() throws IOException {

        FileReader fr = new FileReader(path);
        BufferedReader textReader = new BufferedReader(fr);

        int numberOfLines = readLines();
        String currentLine;
        boolean willAdd;
        ArrayList<String> textData = new ArrayList<String>();

        // iterates through each line, breaking it according to position of spaces
        for (int i = 0; i < numberOfLines; i++) {

            willAdd = true;
            currentLine = textReader.readLine();

            for (String s : currentLine.split(" ")) {

                // checks that the line isn't a date or address
                if (s.equals("PDT") || s.equals("PST") || s.contains("@facebook.com")) {
                    willAdd = false;
                }

            }

            if (willAdd) {
                for (String s : currentLine.split(" ")) {
                    textData.add(s);
                }
            }
        }

        textReader.close();
        return textData;

    }

    // counts number of lines in file to help loops determine where the file ends
    int readLines() throws IOException {

        FileReader fileToRead = new FileReader(path);
        BufferedReader bf = new BufferedReader(fileToRead);

        String aLine;
        int numberOfLines = 0;

        while (( aLine = bf.readLine()) != null) {
            numberOfLines++;
        }

        bf.close();
        return numberOfLines;

    }

    // writes json to a file with words below ranking limit (e.g. 50th most common) and calculated size based on ranking
    public void countFrequencies() throws IOException {

        ArrayList<String> totalLines = new ArrayList<String>();

        try {
            totalLines = openFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        ArrayList<Integer> counts = processList(totalLines);
        int maximumIndex;
        String newInput;
        BufferedWriter writer = new BufferedWriter(new FileWriter("ranks.txt"));

        // begins file with standard json format for arrays i.e. [
        writer.write("[\n");

        int rankingLimit = counts.size();
        String uniqueItemsString;
        int originalCount = counts.get(findListMaximum(counts));
        String ranksString = "";
        int size;

        // iteratively creates array of json objects with 2 attributes -- "text" for the word and "size" for the ranking decreased to manageable size
        for (int rank = 1; rank <= rankingLimit; rank++) {

            maximumIndex = findListMaximum(counts);
            uniqueItemsString = uniqueItems.get(maximumIndex);
            size = (int) (Math.floor(counts.get(maximumIndex)) / (originalCount / 2000));

            if (size > 0) {
                uniqueItemsString = uniqueItemsString.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\r", "\\r")
                        .replace("\n", "\\n")
                        .replace("\u2028", "\\u2028")
                        .replace("\u2029", "\\u2029");
                newInput = "[" + "{\"text\": \"" + uniqueItemsString + "\"},{" + " \"size\": \"" + (size) + "\"}], \n";
                ranksString += newInput;
            }

            counts.remove(counts.get(maximumIndex));
            uniqueItems.remove(uniqueItems.get(maximumIndex));

        }

        ranksString = ranksString.substring(0, ranksString.lastIndexOf(',')) + ranksString.substring(ranksString.lastIndexOf(',') + 1, ranksString.length());
        writer.write(ranksString);
        writer.write("]");
        writer.close();

    }

    // writes corresponding list for uniqueitems, counting the number of times a word is mentioned e.g. 50th most common
    ArrayList<Integer> processList(ArrayList<String> givenList) {

        ArrayList<Integer> uniqueCounts = new ArrayList<Integer>();
        int presentIndex;
        int currentValue;

        for (String item : givenList) {

            presentIndex = uniqueItems.indexOf(item.toLowerCase());

            if (presentIndex == -1) {
                uniqueItems.add(item);
                uniqueCounts.add(1);
            } else {
                currentValue = uniqueCounts.get(presentIndex) + 1;
                uniqueCounts.set(presentIndex, currentValue);
            }

        }

        return uniqueCounts;
    }

    // finds index of word with maximum count in both uniqueitems and uniquecounts
    int findListMaximum(ArrayList<Integer> givenList) {
        int maxIndex = 0;
        for (int i = 0; i < givenList.size(); i++) {
            if (givenList.get(i) > givenList.get(maxIndex)) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
