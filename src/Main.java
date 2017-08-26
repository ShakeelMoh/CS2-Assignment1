
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author shakeel
 */
//sampleinputfile.txt 3 outputfile.txt
public class Main {

    static int[] point;
    static double[] data;
    static double[] outputData;

    static double[] subset;
    static int filterSize;

    static String outputFileName;

    static int numLines;

    static long startTime = 0;

    //ForkJoin Framework
    static final ForkJoinPool fjPool = new ForkJoinPool();

    private static void tick() {
        startTime = System.currentTimeMillis();
    }

    private static float toc() {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    static double invokeParallel(double[] arr) {
        return fjPool.invoke(new FilterThread(arr, 0, arr.length));
    }

    //PSVM populates two arrays with points and their data
    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        String line = sc.nextLine();
        String[] info = line.split(" ");

        String fileName = info[0];
        filterSize = Integer.parseInt(info[1]);
        outputFileName = info[2];

        //System.out.println(fileName + " " + filterSize + " " + outputFileName);
        File file = new File(fileName);

        //Reads file
        Scanner fileReader;
        try {
            fileReader = new Scanner(file);

            numLines = fileReader.nextInt();

            point = new int[numLines];
            data = new double[numLines];

            fileReader.nextLine();
            //System.out.println(numLines);

            //Output array
            outputData = new double[numLines];

            for (int i = 0; i < numLines; i++) {

                //Gets current line
                String currLine = fileReader.nextLine();

                String[] currLineArr = currLine.split(" ");

                point[i] = Integer.parseInt(currLineArr[0]);
                data[i] = Double.parseDouble(currLineArr[1]);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Garbage collection
        System.gc();

        //Setting boundaries
        outputData[0] = data[0];
        outputData[numLines - 1] = data[numLines - 1];

        //------------------------------------------------------------------------------------------------------
        tick();
        //MedianFilter(data);
        invokeParallel(data);
        //ParallelFilter(data);

        float time = toc();

        System.out.println("Run took " + time + " seconds");

        printToFile(outputFileName);
        //------------------------------------------------------------------------------------------------------

    }

    public static void MedianFilter(double[] in) {

        for (int j = 1; j < numLines - 1; j++) {

            subset = new double[filterSize];
            //System.out.println(j);
            subset = Arrays.copyOfRange(in, j - ((filterSize - 1) / 2), j + ((filterSize + 1) / 2));

            Arrays.sort(subset);

            double median = subset[((filterSize + 1) / 2) - 1];
            outputData[j] = median;
            //System.out.println(median);
            //System.out.println(Arrays.toString(subset));

        }

    }

    public static void ParallelFilter(double[] in, int lo, int hi) {

        //invokeParallel(in);
        if (lo == 0) {
            lo += 1;
        }
        if (hi == in.length) {
            hi -= 1;
        }

        for (int j = lo; j < hi; j++) {

            subset = new double[filterSize];
            //System.out.println(j);
            subset = Arrays.copyOfRange(in, j - ((filterSize - 1) / 2), j + ((filterSize + 1) / 2));

            Arrays.sort(subset);

            double median = subset[((filterSize + 1) / 2) - 1];

            outputData[j] = median;
            //System.out.println(median);
            //System.out.println(Arrays.toString(subset));

        }

    }

    public static void printToFile(String file) {

        PrintWriter writer;
        try {
            writer = new PrintWriter(file, "UTF-8");

            writer.println(numLines);

            for (int i = 0; i < outputData.length; i++) {

                writer.append((i + 1) + " " + outputData[i] + "\n");

            }

            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
