package com.company;
import java.util.Scanner;
import java.util.Vector;
public class Main {
    static String s;
    public static Vector search(int j,String sub){
        int cnt=0;
        int maxCnt=0;
        int index=0;
        String newS="";
        for(int i=0;i<j;i++){
            newS+=s.charAt(i);
            for(int k=0;k<sub.length();k++){
                if (i<j&&s.charAt(i)==sub.charAt(k)){cnt++;i++; }
                else break;
            }
            if (maxCnt<cnt){maxCnt=cnt;index=i-cnt;}
            cnt=0;
        }

        Vector<Integer>v=new Vector<Integer>();
        v.add(j-index);
        v.add(maxCnt);
        return v;
    }
    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);
        s=input.next();
        Vector<Integer> v1=new Vector <Integer>();
        Vector<Integer> v2=new Vector <Integer>();
        Vector<Character> v3=new Vector <Character>();
        String x;
        for(int i=0;i<s.length();i++){
            x="";
            x+=s.charAt(i);
            Vector<Integer> cnt=search(i,x);
            if (i==s.length()-1){
                v1.add(cnt.get(0));
                v2.add(cnt.get(1));
                v3.add('0');
            }
            if (cnt.get(1)==0){
                v1.add(0);
                v2.add(0);
                v3.add(s.charAt(i));

            }
            else {
                for(int j=i+1;j<s.length();j++) {
                    x+=s.charAt(j);
                    cnt=search(i,x);

                    if ((cnt.get(1)==x.length())&&(j==s.length()-1)){

                        v1.add(cnt.get(0));
                        v2.add(cnt.get(1));
                        v3.add('0');
                        i+=x.length()-1;
                        break;
                    }
                    else if (cnt.get(1) == x.length() - 1){
                        v1.add(cnt.get(0));
                        v2.add(cnt.get(1));
                        v3.add(s.charAt(j));
                        i+=x.length()-1;
                        break;

                    }
                }
            }
        }
        System.out.println("Tags After Compression");
        for(int f=0;f<v1.size();f++){
            System.out.println("<"+v1.get(f)+", "+v2.get(f)+", "+v3.get(f)+">");
        }
        System.out.println("Decompress");
        String w="";
        for(int i=0;i<v1.size();i++){

            for(int j=0;j<v2.get(i);j++){
                w+=w.charAt(w.length()-v1.get(i));
            }
            if (v3.get(i)!='0')
                w+=v3.get(i);
        }
        System.out.println(w);
    }
}
