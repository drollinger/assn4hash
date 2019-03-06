/***********************************************************
 * Name: Dallin Drollinger
 * A#: A01984170
 *
 * Description: GameStarter.java totals up word scores from .txt files
 *      included in the directory. It also prints out stats of the
 *      Double Hashing Hash Table we use in the process.
 *
 ***********************************************************/
import java.io.*;
import java.util.Scanner;

public class GameStarter {

    //Private variables included in each game
    private DoubleHashingHashTable<WordInfo> HashTable;
    private String name;
    private int totalScore;

    //Constructor
    public GameStarter(){
        HashTable = new DoubleHashingHashTable<>();
    }

    /********************************************************
     * This internal private class is used to interact with
     * our hashtable in order to track word count.
     ********************************************************/
    private class WordInfo {
        String word;
        int count;

        //Constructor
        WordInfo(String newWord) {
            this.word = newWord;
        }

        //equals is needed for comparing only word values
        @Override
        public boolean equals(Object wordTwo){
            if (wordTwo==this){
                return true;
            }
            if (!(wordTwo instanceof WordInfo)){
                return false;
            }
            else {
                WordInfo w = (WordInfo)wordTwo;
                return (this.word.compareTo(w.word) == 0);
            }
        }

        //hash code only operates on word
        @Override
        public int hashCode() {
            return this.word.hashCode();
        }

        //Our toString uses custom spacing in outputting the word and count
        @Override
        public String toString() {
            return word + new String(new char[Math.abs(16 - word.length()) + 1]).replace("\0", " ") + count;
        }
    }

    /********************************************************
     * computeScore takes a WordInfo object as input and computes
     * the score of that word according to the guidelines specified
     ********************************************************/
    public int computeScore(WordInfo w) {

        int[] letterValue = {1, 3, 3, 2, 1,  4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8,  4, 10};
        int letterScore = 0;
        for (int i=0; i < w.word.length(); i++)
            letterScore += letterValue[w.word.toLowerCase().charAt(i)-'a'];

        int lengthValue = w.word.length() < 8 ? w.word.length() - 2 : 6;
        if (lengthValue < 0) lengthValue = 0;

        int bonus = w.count <= 15 ? 5 - (int)Math.ceil((double)w.count/5) : 1;

        return letterScore * lengthValue * bonus;
    }

    /********************************************************
     * playGame takes the filename String as an input and opens that
     * files to calculate the total score. The HashTable is involved
     * in tracking calculations.
     ********************************************************/
    public void playGame(String filename){

        this.name = filename;
        int totalScore = 0;

        try {
            File file = new File(filename);
            Scanner fileScan = new Scanner(file);
            while (fileScan.hasNext()) {

                WordInfo newWord = new WordInfo(fileScan.next().toLowerCase());

                WordInfo oldWord = HashTable.find(newWord);

                if (oldWord == null) {
                    HashTable.insert(newWord);
                    totalScore += computeScore(newWord);
                }
                else {
                    oldWord.count++;
                    totalScore += computeScore(oldWord);
                }
            }

            this.totalScore = totalScore;

            fileScan.close();
        } catch (FileNotFoundException e) {
            System.out.println("There was and error scanning the file: " + filename);
        }
    }

    /********************************************************
     * The overrided toString function prints out a table of
     * the total score and all the associated hashtable stats
     ********************************************************/
    @Override
    public String toString() {

        int LIMIT = 20;
        StringBuilder sb = new StringBuilder();
        sb.append("==========================\n        " + name +
                "\n==========================\n");
        sb.append("Total Score: " + totalScore + "\n");
        sb.append("# of finds: " + HashTable.toString("finds"));
        sb.append("# of probs: " + HashTable.toString("probs"));
        sb.append("# of items: " + HashTable.toString("items"));
        sb.append("Table Length: " + HashTable.toString("length"));
        sb.append("\n-----First " + LIMIT + " Entries" + "-----\n");
        sb.append(HashTable.toString("entries", LIMIT));
        sb.append("--------------------------\n\n");

        return sb.toString();
    }

    /********************************************************
     * The main function to play and print out all 5 games.
     * NOTE: These text files must be in the appropriate directory.
     ********************************************************/
    public static void main(String [ ] args) {
        try {
            GameStarter g0 = new GameStarter();
            g0.playGame("game0.txt");
            System.out.println(g0);

            GameStarter g1 = new GameStarter();
            g1.playGame("game1.txt");
            System.out.println(g1);

            GameStarter g2 = new GameStarter();
            g2.playGame("game2.txt");
            System.out.println(g2);

            GameStarter g3 = new GameStarter();
            g3.playGame("game3.txt");
            System.out.println(g3);

            GameStarter g4 = new GameStarter();
            g4.playGame("game4.txt");
            System.out.println(g4);
        }
        catch(Exception e){
           e.printStackTrace();
        }
    }
}