package imgRGP;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

class GetPixels {
    // height , width of the original image ..... sizeh,sizew are the dimensions of the small matrices
    static int height, width, sizeh, sizew;
    static String vectorWidth = "";
    static String vectorHeight = "";
    static String codeBookSize = "";
    static Vector<Vector<Vector<Integer>>> reconstructedMatrix1 = new Vector<>();
    // vector contains 2D vectors that the original matrix of pixels divided into it
    static Vector<Vector<Vector<Integer>>> dividedMatrix = new Vector<Vector<Vector<Integer>>>();
    // this hashmap contains the blocks with its nearest codebook
    static HashMap<Vector<Vector<Double>>, Vector<Vector<Vector<Integer>>>> hashMap;
    // contains 2D vectors we get after the splitting operations
    static Vector<Vector<Vector<Double>>> splitVector = new Vector<Vector<Vector<Double>>>();
    //same as "splitVector" but with int not double
    static Vector<Vector<Vector<Integer>>> intSplitVector = new Vector<Vector<Vector<Integer>>>();
    // vector contains binary codes of the vector
    static Vector<String> compressedImage = new Vector<>();
    // hashMap contains 2D vectors that will be replaced with the original block in the image and its binary code
    static HashMap<String, Vector<Vector<Integer>>> codeBook = new HashMap<>();

    // method to divide the original matrix into blocks
    public static void divide(int[][] matrix, int sizeh, int sizew) throws IOException {
        Vector<Vector<Integer>> v = new Vector<Vector<Integer>>();
        Vector<Vector<Vector<Integer>>> newMatrix = new Vector<Vector<Vector<Integer>>>();
        int i, j, x, y, z;
        i = j = x = y = z = 0;
        while (i < height) {
            while (j < width) {
                for (int k = i; k < sizeh + i; k++) {
                    //takes the rows vectors
                    Vector<Integer> subV = new Vector<>();
                    for (int m = j; m < sizew + j; m++) {
                        subV.add(matrix[k][m]);
                        z++;

                    }
                    z = 0;
                    //takes columns also
                    v.add(subV);
                    y++;

                }

                j += sizew;
                //takes the new matrix
                newMatrix.add(v);
                y = 0;
                x++;
                v = new Vector<Vector<Integer>>();

            }
            j = 0;
            i += sizeh;
        }

        dividedMatrix = newMatrix;

    }

    // method to get the average
    public static Vector<Vector<Double>> getavg(Vector<Vector<Vector<Integer>>> newMatrix, int sizeh, int sizew) {
        Vector<Vector<Double>> avgVector = new Vector<>();
        Double sum = 0.0;
        Double ans = 0.0;
        for (int j = 0; j < sizeh; j++) {
            Vector<Double> v = new Vector<>();
            for (int k = 0; k < sizew; k++) {
                for (int i = 0; i < newMatrix.size(); i++) {
                    sum += newMatrix.get(i).get(j).get(k);

                }
                ans = sum / newMatrix.size();

                v.add(ans);
                sum = 0.0;

            }
            avgVector.add(v);
        }
        return avgVector;
    }

    // method to return the index of the nearest vector to specified block in the image
    public static int getnearestVector(Vector<Vector<Integer>> vector, Vector<Vector<Vector<Double>>> containSpliters) {
        int mindistance = 0;
        int min = 1000000000;
        int index = 0;
        for (int k = 0; k < containSpliters.size(); k++) {
            for (int i = 0; i < vector.size(); i++) {
                for (int j = 0; j < vector.get(i).size(); j++) {
                    mindistance += (int) Math.pow(vector.get(i).get(j) - containSpliters.get(k).get(i).get(j), 2.0);


                }
            }
            min = Math.min(min, mindistance);
            if (min == mindistance) index = k;
            mindistance = 0;

        }

        return index;
    }

    // method to split the average of some blocks into 2  2D vectors
    public static Vector<Vector<Vector<Double>>> split(Vector<Vector<Vector<Integer>>> dividedMatrix) {
        Vector<Vector<Double>> v1 = new Vector<Vector<Double>>();
        Vector<Vector<Double>> v2 = new Vector<Vector<Double>>();
        Vector<Vector<Double>> avgResult;

        avgResult = getavg(dividedMatrix, sizeh, sizew);

        for (int i = 0; i < avgResult.size(); i++) {
            Vector<Double> temp1 = new Vector<>();
            Vector<Double> temp2 = new Vector<>();
            for (int j = 0; j < avgResult.get(i).size(); j++) {
                // 6.0   6.0
                if (Math.floor(avgResult.get(i).get(j)) == avgResult.get(i).get(j)) {
                    temp1.add((avgResult.get(i).get(j) - 1));
                    temp2.add((avgResult.get(i).get(j) + 1));

                } else {
                    //6.9   6.0
                    temp1.add(Math.floor(avgResult.get(i).get(j)));
                    temp2.add(Math.floor(avgResult.get(i).get(j) + 1));

                }
            }

            v1.add(temp1);
            v2.add(temp2);


        }

        Vector<Vector<Vector<Double>>> containSpliter = new Vector<>();
        containSpliter.add(v1);
        containSpliter.add(v2);
        return containSpliter;


    }

    // method to group some blocks with their nearest block
    public static HashMap<Vector<Vector<Double>>, Vector<Vector<Vector<Integer>>>> hashing(Vector<Vector<Vector<Double>>> parents) {
        HashMap<Vector<Vector<Double>>, Vector<Vector<Vector<Integer>>>> splitersGroups = new HashMap<>();
        for (int j = 0; j < parents.size(); j++) {
            splitersGroups.put(parents.get(j), new Vector<Vector<Vector<Integer>>>());
        }

        for (int i = 0; i < dividedMatrix.size(); i++) {
            int index;
            index = getnearestVector(dividedMatrix.get(i), parents);
            //adding 2d vector to the nearest parent after splitting
            splitersGroups.get(parents.get(index)).add(dividedMatrix.get(i));
        }
        //  System.out.println(splitersGroups);
        return splitersGroups;
    }

    //
    public static void finalMethod(Vector<Vector<Vector<Integer>>> newMatrix, int Vectorsize) {
        if (Vectorsize == 1) {

            return;
        }
        Vector<Vector<Vector<Double>>> splitVector1;
        splitVector1 = split(newMatrix);
        hashMap = hashing(splitVector1);

        splitVector.add(splitVector1.get(0));
        splitVector.add(splitVector1.get(1));


        finalMethod(hashing(splitVector1).get(splitVector1.get(0)), Vectorsize / 2);
        finalMethod(hashing(splitVector1).get(splitVector1.get(1)), Vectorsize / 2);


    }

    // method to get the average of some blocks until no change in the average happens
    public static void untilnoChange(HashMap<Vector<Vector<Double>>, Vector<Vector<Vector<Integer>>>> hashMap) {
        int it = 5;
        while (it > 0) {
            Vector<Vector<Vector<Double>>> splitVector1 = new Vector<Vector<Vector<Double>>>();
            HashMap<Vector<Vector<Double>>, Vector<Vector<Vector<Integer>>>> hashMap1 = hashing(splitVector);
            for (int i = 0; i < hashMap.size(); i++) {
                Vector<Vector<Double>> v1 = new Vector<>();
                Vector<Vector<Double>> vector = getavg(hashMap1.get(splitVector.get(i)), sizeh, sizew);
                int size = vector.size();
                for (int j = 0; j < size; j++) {
                    Vector<Double> v2 = new Vector<>();
                    int size1 = vector.get(j).size();
                    for (int m = 0; m < size1; m++) {
                        double x = Math.floor(vector.get(j).get(m));
                        v2.add(x);
                    }
                    v1.add(v2);
                }
                splitVector1.add(v1);

            }


            if (splitVector.equals(splitVector1)) {
                break;
            }

            Collections.copy(splitVector, splitVector1);


            it--;

        }
        for (int i = 0; i < splitVector.size(); i++) {
            Vector<Vector<Integer>> v1 = new Vector<>();
            for (int j = 0; j < splitVector.get(i).size(); j++) {
                Vector<Integer> v = new Vector<>();
                for (int k = 0; k < splitVector.get(i).get(j).size(); k++) {
                    v.add((int) Math.round(splitVector.get(i).get(j).get(k)));
                }
                v1.add(v);
            }
            intSplitVector.add(v1);
        }

    }

    // return hashMap
    public static HashMap<String, Vector<Vector<Integer>>> imageEncoding(Vector<Vector<Vector<Integer>>> intSplitVector) {
        HashMap<String, Vector<Vector<Integer>>> binaryCodeBook = new HashMap<>();
        int i = 0;
        for (Vector<Vector<Integer>> v : intSplitVector) {
            if (Integer.toBinaryString(i).length() != intSplitVector.size() / 2) {
                String binary = "";
                int size = (intSplitVector.size() / 2) - Integer.toBinaryString(i).length();
                for (int j = 0; j < size; j++) {
                    binary += '0';
                }
                binaryCodeBook.put(binary + Integer.toBinaryString(i), v);

            } else {
                binaryCodeBook.put(Integer.toBinaryString(i), v);
            }
            i++;
        }
        codeBook = binaryCodeBook;
        return binaryCodeBook;

    }

    // replace the block with its nearest one
    public static Vector<Vector<Vector<Integer>>> reconsrtuctedImage() {
        Vector<Vector<Vector<Integer>>> reconstructedMatrix2 = dividedMatrix;


        for (Vector<Vector<Integer>> v : dividedMatrix) {
            for (int i = 0; i < hashMap.size(); i++) {
                int size = hashMap.get(splitVector.get(i)).size();
                for (int j = 0; j < size; j++) {
                    if (v.equals(hashMap.get(splitVector.get(i)).get(j))) {

                        reconstructedMatrix2.set(dividedMatrix.indexOf(v), intSplitVector.get(i));

                    }
                }
            }
        }
        reconstructedMatrix1 = reconstructedMatrix2;
        return reconstructedMatrix2;
    }

    // turn the blocks into binary ones
    public static void compressedImage() {
        Vector<String> Compressedvector = new Vector<>();

        for (Vector<Vector<Integer>> v : reconstructedMatrix1) {
            for (String binary : codeBook.keySet()) {
                if (codeBook.get(binary).equals(v)) {
                    Compressedvector.add(binary);
                }
            }
        }
        compressedImage = Compressedvector;
    }

    // function to do the compression operation
    public static void compress(int[][] matrix, int sizeH, int sizeW, int codeBookSize) throws IOException {
        if (height % sizeH != 0) {
            int height1 = (((height / sizeH)) * sizeH);
            height = height1;

        }
        if (width % sizeW != 0) {
            int width1;
            width1 = (((width / sizeW)) * sizeW);
            width = width1;
        }
        sizeh = sizeH;
        sizew = sizeW;
        // divide 2d matrix into small 2d matrices based on sizeh ,sizew
        System.out.println("divide method");
        divide(matrix, sizeh, sizew);
        // method to get average of the matrix and split the matrix into number of matrices based on vector code book size
        finalMethod(dividedMatrix, codeBookSize);
        //for loop to remove useless code book vectors
        for (int i = 0; i <= splitVector.size() - codeBookSize; i++) {
            splitVector.remove(0);
        }
        System.out.println("split vector..");
        //method to get the average until no change happens
        System.out.println("until no change..");
        hashMap = hashing(splitVector);
        untilnoChange(hashMap);
        // System.out.println(intSplitVector);
        System.out.println("hashing..");
        //System.out.println(hashing(splitVector));
        hashMap = hashing(splitVector);
        System.out.println("image encoding..");
        // System.out.println(imageEncoding(splitVector));
        reconsrtuctedImage();
        System.out.println("compressed image..");
        compressedImage();


    }

    // fun to do the De compression operation
    public static void DeCompress(Vector<String> compressedImage, HashMap<String, Vector<Vector<Integer>>> codeBook) {
        Vector<Vector<Vector<Integer>>> decompressed = new Vector<>();
        for (int i = 0; i < compressedImage.size(); i++) {
            for (String s : codeBook.keySet()) {
                if (s.equals(compressedImage.get(i))) {
                    decompressed.add(codeBook.get(s));
                }
            }

        }
        System.out.println("Decompression");
        System.out.println("re construct");
        reconsrtuctedImage();
        int[][] reconsruct2 = new int[height][width];
        int[] reconsruct = new int[height * width];
        int x = 0;
        for (int j = 0; j < sizeh; j++) {
            for (int i = 0; i < reconstructedMatrix1.size(); i++) {
                for (int k = 0; k < sizew; k++) {
                    reconsruct[x] = reconstructedMatrix1.get(i).get(j).get(k);
                    x++;
                }
            }
        }
        int y = 0;
        int m = 0;
        int t = 1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                reconsruct2[y][j] = reconsruct[m];
                m++;

            }
            y += sizeh;
            if (y >= height) {
                y = t;
                t++;
            }
        }
        System.out.println("writing  the pixels..");
        String path=System.getProperty("user.dir")+"/decompression.jpg";

        writeImage(reconsruct2, width, height, path);


    }

    public static BufferedImage getBufferedImage(int[][] imagePixels, int height, int width) {
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            int s;
            if(y == 199)
                s = 13;
            for (int x = 0; x < width; x++) {
                int value = -1 << 24;
                value = 0xff000000 | (imagePixels[y][x] << 16) | (imagePixels[y][x] << 8) | (imagePixels[y][x]);
                image.setRGB(x, y, value);
            }
        }
        return image;
    }

    public static void writeImage(int[][] imagePixels, int height, int width, String outPath) {
        BufferedImage image = getBufferedImage(imagePixels, height, width);
        File ImageFile = new File(outPath);
        try {
            ImageIO.write(image, "jpg", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {

        FileWriter writer = new FileWriter("rgb.txt");
      /*  int  matrix[][]= new int[6][6];
            matrix[0][0]=1;
            matrix[0][1]=2;
            matrix[0][2]=7;
            matrix[0][3]=9;
            matrix[0][4]=4;
            matrix[0][5]=11;
            matrix[1][0]=3;
            matrix[1][1]=4;
            matrix[1][2]=6;
            matrix[1][3]=6;
            matrix[1][4]=12;
            matrix[1][5]=12;
            matrix[2][0]=4;
            matrix[2][1]=9;
            matrix[2][2]=15;
            matrix[2][3]=14;
            matrix[2][4]=9;
            matrix[2][5]=9;
            matrix[3][0]=10;
            matrix[3][1]=10;
            matrix[3][2]=20;
            matrix[3][3]=18;
            matrix[3][4]=8;
            matrix[3][5]=8;
            matrix[4][0]=4;
            matrix[4][1]=3;
            matrix[4][2]=17;
            matrix[4][3]=16;
            matrix[4][4]=1;
            matrix[4][5]=4;
            matrix[5][0]=4;
            matrix[5][1]=5;
            matrix[5][2]=18;
            matrix[5][3]=18;
            matrix[5][4]=5;
            matrix[5][5]=6;*/

       //Reading the image
       String path=System.getProperty("user.dir")+"/sadTom.jpg";
       File file = new File(path);
        BufferedImage img = ImageIO.read(file);
        // matrix to store the pixels
        int  matrix[][]= new int[img.getHeight()][img.getWidth()];
        System.out.println("Height: " + img.getHeight());
        System.out.println("Width: " + img.getWidth());

        height= img.getHeight();
        width=img.getWidth();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //Retrieving contents of a pixel
                int pixel = img.getRGB(x, y);
                //Creating a Color object from pixel value
                Color color = new Color(pixel, true);
                //Retrieving the R G B values
                int red = color.getRed();
                writer.append(red + " ");
                matrix[y][x]=red;
           //     writer.append("\n");
                writer.flush();
            }
        }
        writer.close();
        System.out.println("RGB values at each pixel are stored in the specified file");
        // dividing matrix into vector of vectors
        sizeh=3;
        sizew=3;
        compress(matrix,3,3,8);
        DeCompress(compressedImage, codeBook);



    }
}

