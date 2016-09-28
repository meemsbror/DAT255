package gruppn.kasslr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alle on 2016-09-22.
 * Vocabulare is a collection of words and pictures used to play Swedish learning games on an application
 */

public class Vocabulary {

    public String vocabularyName = "ettKonstigtNamn";

    //String #1 is a Swedish word
    //String #2 is a path to an image connected to the word.
    public Map<String, String> wordPairs = new HashMap<>();

    public String getVocabularyName() {
        return vocabularyName;
    }

    public void setVocabularyName(String vocabularyName) {
        this.vocabularyName = vocabularyName;
    }

    public Vocabulary(String name){
        name = vocabularyName;
    }

    //Todo
    /*public Vocabulary(File file){
        load(jsson fil);
    }*/

    //Returns all the wordpairs
    private Map<String, String> getWordpairs() {
        return wordPairs;
    }

    //Returns all the image paths in an ArrayList
    public ArrayList<String> getPictures(){
        return new ArrayList<String>(wordPairs.values());
    }

    //Returns all the words in an ArrayList
    public ArrayList<String> getWords(){
        return new ArrayList<String>(wordPairs.keySet());
    }

    //Adds a wordPair (same function that removes a word pair)
    public void addWordPair(String word, String imgPath){
        wordPairs.put(word, imgPath);
    }

    //Changes image path
    public void changeImagePath(String word, String imgPath){
        if(wordPairs.containsKey(word)){
            addWordPair(word, imgPath);
        }else{
            //Todo add exception
        }
    }

    //Remove wordPair
    public void annihilateWordpair(String word){
        wordPairs.remove(word);
    }

    //Save vocabulary
    public void save() throws  JSONException {

        JSONArray outPut = new JSONArray();

        //Saves the name in the top
        //out.println(vocabularyName);

        //Saves all the words and paths after eachother
        for (Map.Entry<String, String> wordpair : wordPairs.entrySet())
        {
            JSONObject temp = new JSONObject();
            temp.put("Word", wordpair.getKey());
            temp.put("Picture",wordpair.getValue());
            System.out.println(temp.toString());
            outPut.put(temp);
        }
        System.out.println(outPut.toString());
        //Todo ska den sparas i en fil?
    }
/*
    //Load vocabulary
    private void load(File file) {
        //The vocabulary is always saved in a certain way so it is easy to extract information
        if(file.canRead()) {
            String fileTemp[] = file.toString().split("\\r?\\n");
            setVocabularyName(fileTemp[0]);
            setWordPairs(pairWords(fileTemp));
        }else{
            //Todo add exeption
        }

    }

    //Pairs words from a string array format
    private Map<String, String> pairWords(String [] file){
        Map<String, String> temp = new HashMap<>();
        for(int i = 1; i <= file.length; i += 2){
            temp.put(file[i], file[i+1]);
        }
        return temp;
    }

    //Sets current wordPairs, only to be done when a new file is loaded
    private void setWordPairs(Map<String, String> wordPairs) {
        this.wordPairs = wordPairs;
    }
    */
}