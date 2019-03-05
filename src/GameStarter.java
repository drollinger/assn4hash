import java.io.*;
import java.util.Scanner;

public class GameStarter {

    private String name;
    private QuadraticProbingHashTable<WordInfo> HashTable;

    class WordInfo {

        String word;
        int count;

        WordInfo(String newWord) {
            this.word = newWord;
        }

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

        @Override
        public int hashCode() {
            return this.word.hashCode();
        }
    }

    public GameStarter(){
        HashTable = new QuadraticProbingHashTable<WordInfo>();
    }

    public int computeScore(WordInfo w) {
        int[] letterValue = {1, 3, 3, 2, 1,  4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8,  4, 10};
        int letterScore = 0;
        for (int i=0; i < w.word.length(); i++)
            letterScore += letterValue[w.word.toLowerCase().charAt(i)-'a'];

        int lengthValue = w.word.length() < 8 ? w.word.length() - 2 : 6;
        if (lengthValue < 0) lengthValue = 0;

        int bonus = w.count < 15 ? 5 - (int)Math.ceil((double)w.count/5) : 1;

        String spacing = new String(new char[20 - w.word.length()]).replace("\0", " ");
        System.out.println(w.word + spacing + lengthValue + "\t\t\t...\t\t\t" + letterScore + "\t\t\t...\t\t\t" + bonus);
        return letterScore * lengthValue * bonus;
    }

    public void playGame(String filename){

        int totalScore = 0;

        try {
            File file = new File(filename);
            Scanner fileScan = new Scanner(file);
            while (fileScan.hasNext()) {

                WordInfo newWord = new WordInfo(fileScan.next());

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

            System.out.println("====================================================================\n\n" +
                    filename + ": " + totalScore + "\n\n" +
                    "====================================================================\n\n\n\n");

            fileScan.close();
        } catch (FileNotFoundException e) {
            System.out.println("There was and error scanning the file: " + filename);
        }
    }

    public String toString() {
        int LIMIT = 20;
        return name+ "\n"+ HashTable.toString(LIMIT);
    }

    public static void main( String [ ] args ) {
        try {
            GameStarter g0 = new GameStarter(  );
            g0.playGame("game0.txt" );
            //System.out.println(g0);
            GameStarter g1 = new GameStarter(  );
            g1.playGame("game1.txt" );
            //System.out.println(g1);
            GameStarter g2 = new GameStarter(  );
            g2.playGame("game2.txt" );
            //System.out.println(g2);
            GameStarter g3 = new GameStarter(  );
            g3.playGame("game3.txt" );
            //System.out.println(g3);
            GameStarter g4 = new GameStarter(  );
            g4.playGame("game4.txt" );
            //System.out.println(g4);
        }
        catch(Exception e){
           e.printStackTrace();
        }


    }

}
