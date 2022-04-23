package com.company;

import java.io.*;
import java.util.*;
public class Main {
    static String code = "";
    public static HashMap<String, String> dictionary = new HashMap<String, String>();
    public static String compress;
    public static String decompress;

    //CHECK IF KEY IN THE DICTIONARY OR NOT
    public static String search(String sub) {
        if (dictionary.containsKey(sub)) {
            return dictionary.get(sub);
        }
        else {
            return "-1";
        }
    }
    public static double getEntropy(char c){
        return getprob(c)*(Math.log(1/getprob(c))/Math.log(2));
    }

    public static int counter(char ch) {
        int count = 0;
        for (int j = 0; j < compress.length(); j++) {
            if (compress.charAt(j) == ch) {
                count++;
            }
        }
        return count;
    }

    public static double getprob(char ch) {
        double count = 0.0;
        double length = compress.length();
        for (int j = 0; j < compress.length(); j++) {
            if (compress.charAt(j) == ch) {
                count++;
            }
        }
        return count / length;

    }

    public static HashMap<String, String> min(HashMap<String, Double> mp) {
        HashMap<String, String> P = new HashMap<>();
        Double min = 1000000000.0;
        String s1 = "", s2 = "";
        for (String s : mp.keySet()) {
            if (mp.get(s) < min) {
                s1 = s;
                min = mp.get(s);
            }
        }
        min = 1000000000.0;
        for (String s : mp.keySet()) {
            if (mp.get(s) < min && mp.get(s) >= mp.get(s1) && s != s1) {
                s2 = s;
                min = mp.get(s);

            }
        }
        P.put(s1, s2);


        return P;
    }

    public static void main(String[] args) throws IOException {
        // file reader to read each char in a file and passing it to compress String
        File file = new File(System.getProperty("user.home") + "/Desktop/input.txt");

        Scanner myReader = new Scanner(file);
        compress = "";

        while (myReader.hasNextLine()) {
            compress += myReader.nextLine();

        }
        // map to store each elem and its probability
        HashMap<Character, Double> propmap = new HashMap<Character, Double>();
        // map for doing operations on it
        HashMap<String, Double> opMap = new HashMap<String, Double>();
        // tree map to store values parent and its children
        HashMap<String, Vector<String>> treemap = new HashMap<String, Vector<String>>();
        // map to have elements with its code
        HashMap<String, String> dic = new HashMap<>();
        for (int i = 0; i < compress.length(); i++) {
            propmap.put(compress.charAt(i), getprob(compress.charAt(i)));
            opMap.put(String.valueOf(compress.charAt(i)), getprob(compress.charAt(i)));
        }
        while (opMap.size() > 2) {
            // printing minimum 2 elements
            for (String s : min(opMap).keySet()) {
                String newKey = s;
                newKey += min(opMap).get(s);
                Double newVal = opMap.get(s) + opMap.get(min(opMap).get(s));
                opMap.put(newKey, newVal);
                Vector<String> v = new Vector<>();
                v.add(s);
                v.add(min(opMap).get(s));
                treemap.put(newKey, v);
                opMap.remove(min(opMap).get(s));
                opMap.remove(s);
            }


        }
        // puts initial values of the last 2 elements in the opMap
        String m = "0";
        for (String s : opMap.keySet()) {
            dic.put(s, m);
            m = "1";
        }
        // putting a code to every element in the treeMap
        int l = 0;
        while (l < treemap.size()) {
            for (String treeS : treemap.keySet()) {
                if (dic.get(treeS) != null) {
                    dic.put(treemap.get(treeS).elementAt(0), dic.get(treeS) + "1");
                    dic.put(treemap.get(treeS).elementAt(1), dic.get(treeS) + "0");

                }
            }
            l++;
        }
        int cnt = 0;
        BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/dic.txt"));
        writer.write("{");
        decompress = "";
        double decompressSum=0;
        double sum=0;
        // outputing dictionary
        for (String s : dic.keySet()) {
            if (s.length() == 1) {
                writer.write(s + "=");
                writer.write(dic.get(s));
                    writer.write(", ");

                System.out.println(s+" = "+dic.get(s));
                System.out.println(s+" = "+counter(s.charAt(0)));
                decompressSum+=counter(s.charAt(0))*dic.get(s).length();
                sum+=getEntropy(s.charAt(0));
            }

        }
        System.out.println(dic);
        //System.out.println("Entropy = "+ sum*compress.length());
        writer.write("}");
        writer.close();
        // write in file
        writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/out.txt"));
        for (int j = 0; j < compress.length(); j++) {
            decompress += dic.get(String.valueOf((compress.charAt(j))));

        }
        writer.write(decompress);
        writer.close();
        //OPENS DICTIONARY FILE
        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Desktop/dic.txt"));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        //READS DICTIONARY FILE
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        //CLOSES DICTIONARY FILE
        br.close();
        //DELETE THE DICTIONARY BRACKETS, AND SPACES FROM THE TEXT FILE
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '{' || stringBuilder.charAt(i) == '}') {
                stringBuilder.deleteCharAt(i);
            } else if ((stringBuilder.charAt(i - 1) == ',') && (stringBuilder.charAt(i) == ' ')) {
                stringBuilder.deleteCharAt(i);
            }
        }
        line = stringBuilder.toString();
        String[] values = {""};
        values = line.split("[=,]");
        //STORE TEXT FILE INTO A STRING ARRAY
        int j = 1;
        for (int i = 0; i < values.length - 1; i += 2) {
            dictionary.put(values[j], values[i]);
            j += 2;
        }

        //OPENS CODE FILE
        BufferedReader br2 = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Desktop/out.txt"));
        line = null;
        StringBuilder stringBuilder2 = new StringBuilder();
        //READS CODE FILE
        while ((line = br2.readLine()) != null) {
            stringBuilder2.append(line);
        }
        //CLOSES CODE FILE
        br2.close();
        //STORE FILE TEXT TO A STRING
        code = stringBuilder2.toString();
        String x = "";

        //FOR LOOP OVER THE ZEROS&ONES INPUT, TEST EVERY NUMBER IF IT IN THE DICTIONARY -> PRINT HIS CHARACTER
        //    IF NOT -> TAKE ONE MORE NUMBER AND RESEARCH AND SO ON.
        String searchedChars = "";
        for (int i = 0; i < code.length(); i++) {
            x = "";
            x += code.charAt(i);
            if (search(x) != "-1") {
                searchedChars += search(x);
            } else {
                for (int w = i + 1; w < code.length(); w++) {
                    x += code.charAt(w);
                    if (search(x) != "-1") {
                        searchedChars += search(x);
                        i += x.length() - 1;
                        break;
                    }
                }
            }
        }

        //COMPRESSION RATIO
        double originalSize;
        originalSize= compress.length() *((int) (1+(Math.log(dic.size())/Math.log(2))));

        String decompressionFile = "\nOriginal String Size is : " + originalSize + " Bits.";

        System.out.println(decompressionFile);

        System.out.println("Compressed String Size= "+decompressSum+" Bits.");

        System.out.println("Compression ratio = "+originalSize/decompressSum);
        System.out.println();
        File myObj = new File("Decompression Result.txt");
        try {
            FileWriter myWriter = new FileWriter(System.getProperty("user.home") + "/Desktop/Decompression Result.txt");
            myWriter.write(searchedChars);
            myWriter.close();
            System.out.println("Successfully wrote the Result to the \"" + myObj.getName() + "\" file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}


    