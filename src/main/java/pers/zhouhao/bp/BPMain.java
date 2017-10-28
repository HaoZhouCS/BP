package pers.zhouhao.bp;

import pers.zhouhao.util.GenTrainData;
import pers.zhouhao.util.GenTrainData.Data;

import java.io.*;
import java.util.Scanner;

/**
 * Created by lenovo on 2017/10/8.
 */
public class BPMain {

    private String trainFileName = "bp.train";
    private String testFileName = "bp.test";
    private String predictFileName = "bp.predict";

    private final int maxTrainNum = 30000;
    private Data[] trainData = new Data[maxTrainNum];

    private final int maxTestNum = 3000;
    private Data[] testData = new Data[maxTestNum];

    private int trainDataLen = 0;
    private int testDataLen = 0;

    public static void main(String[] args) {

        BPMain bpMain = new BPMain();

        bpMain.readTrainFile();

        BackPropagation BP = new BackPropagation(3, 9, 0.1);

        boolean success = false;
        double maxError = 0.99;
        int times = 0;
        int right = 0;

        while(!success && times < 500) {

            right = 0;
            success = true;

            GenTrainData.randomSort(bpMain.trainData);

            for(int i = 0;i < bpMain.trainDataLen;i ++) {
                BP.study(bpMain.trainData[i].x, bpMain.trainData[i].y, bpMain.trainData[i].type);
                double error = BP.getError();
                if(error > maxError) {
                    success = false;
                } else {
                    right ++;
                }
            }

            System.out.println("right = " + right);

            times ++;
        }

        System.out.println("train times = " + times);

        bpMain.readTestFile();

        for(int i = 0;i < bpMain.testDataLen;i ++) {
            bpMain.testData[i].pred = BP.predict(bpMain.testData[i].x, bpMain.testData[i].y);
        }

        bpMain.writePredictFile();

        int accurate = 0;
        for(int i = 0;i < bpMain.testDataLen;i ++) {
            if(bpMain.testData[i].type == bpMain.testData[i].pred) {
                accurate ++;
            }
        }

        double accuracy = (double)accurate / bpMain.testDataLen;

        System.out.println("accuracy = " + accuracy);

    }

    private void readTrainFile() {
        try {

            Scanner scanner = new Scanner(new FileInputStream(trainFileName));
            while(scanner.hasNextDouble()) {
                trainData[trainDataLen] = new Data();
                trainData[trainDataLen].x = scanner.nextDouble();
                 trainData[trainDataLen].y = scanner.nextDouble();
                trainData[trainDataLen].type = scanner.nextInt();
                trainDataLen ++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readTestFile() {
        try {

            Scanner scanner = new Scanner(new FileInputStream(testFileName));
            while(scanner.hasNextDouble()) {
                testData[testDataLen] = new Data();
                testData[testDataLen].x = scanner.nextDouble();
                testData[testDataLen].y = scanner.nextDouble();
                testData[testDataLen].type = scanner.nextInt();
                testDataLen ++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePredictFile() {
        try {
            FileWriter fw = new FileWriter(predictFileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for(int i = 0;i < testDataLen;i ++) {
                bw.write(testData[i].x + " " + testData[i].y + " " + testData[i].type + " " + testData[i].pred + "\n");
            }

            bw.close();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    class Data {
        public double x;
        public double y;
        public int type;
        public int pred;
    }
*/
}
