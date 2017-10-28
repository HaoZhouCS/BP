package pers.zhouhao.util;

import pers.zhouhao.bp.BPMain;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by lenovo on 2017/10/8.
 */
public class GenTrainData {

    private static String fileName = "bp.train";
    private static FileWriter fw;
    private static BufferedWriter bw;

    private static int dataNum = 0;
    private static final int maxNum = 30000;
    private static Data[] trainData = new Data[maxNum];

    public static void main(String[] args) {

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);

            genAllType(-3.1, -0.1, 8.2, 4.2, 100, true);
            genAllType(3, 2.5, 2, 1.5, 70, false);
            genAllType(0, 0, 2, 2, 40, false);
            genAllType(-20.5, -20.5, 40, 30, 30, true);

            randomSort();

            for(int i = 0;i < dataNum;i ++) {
                Data data = trainData[i];
                bw.write(data.x + " " + data.y + " " + data.type + "\n");
            }

            bw.close();
            fw.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

    }

    private static void genAllType(double begin_x, double begin_y, double length, double width, int SLOT, boolean mode) throws IOException {
        double x = begin_x, y = begin_y;
        double x_interval = length / SLOT;
        double y_interval = width / SLOT;

        for(int i = 0;i < SLOT;i ++) {
            x +=  x_interval;
            y = begin_y;
            for(int j = 0;j < SLOT;j ++) {
                y += y_interval;
                trainData[dataNum] = new Data();
                trainData[dataNum].x = x;
                trainData[dataNum].y = y;
                if(isTypeFirst(x, y)) {
                    //bw.write(x + " " + y + " " + 1 + "\n");
                    trainData[dataNum ++].type = 1;
                } else if(isTypeSecond(x, y)) {
                    //bw.write(x + " " + y + " " + 2 + "\n");
                    trainData[dataNum ++].type = 2;
                } else if(isTypeThird(x, y)) {
                    //bw.write(x + " " + y + " " + 3 + "\n");
                    trainData[dataNum ++].type = 3;
                } else if(mode) {
                    //bw.write(x + " " + y + " " + 4 + "\n");
                    trainData[dataNum ++].type = 4;
                }
            }
        }
    }

    private static void randomSort() {
        randomSort(trainData);
    }

    public static void randomSort(Data[] trainData) {
        int n = dataNum;
        Random random = new Random();
        while(n >= 1) {
            int k = random.nextInt(n);
            Data temp = trainData[k];
            trainData[k] = trainData[n - 1];
            trainData[n - 1] = temp;
            n --;
        }
    }

    private static boolean isTypeFirst(double x, double y) {
        return x >= 3 && x <= 5 && y >= 2.5 && y <= 4 && (y - 4 < -(x - 3) * 3 / 4);
    }

    private static boolean isTypeSecond(double x, double y) {
        return x > 0 && x <= 2 && y > 0 && y <= 2;
    }

    private static boolean isTypeThird(double x, double y) {
        return x >= -3 && x <= 0 && y >= 0 && y <= 4 && (y  < x / 2 + 4);
    }

    static public class Data {
        public double x;
        public double y;
        public int type;
        public int pred;
    }
}
