package zindach.neuralnettest.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import zindach.mathlib.algebra.Vector;

public class MNISTLoader {

    public static double[][] importData(String fileName) {
        try {
            System.out.println("\n---Importing MNIST data---\nfile: " + fileName);
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(fileName));
            byte[] magic = new byte[4];
            gzip.read(magic);
            int magicNum = bytesToInt(magic);
            System.out.println("magic num: " + magicNum);
            switch (magicNum) {
                case 2049:
                    return importLabelFile(gzip);
                case 2051:
                    return importImageFile(gzip);
                default:
                    System.err.println("This is not a valid file. magic num: " + magicNum);
                    break;
            }
        } catch (IOException ex) {
            System.err.println("Error while reading file:\n" + ex);
        }
        return new double[0][];
    }

    private static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF));
    }

    private static double[][] importLabelFile(GZIPInputStream gzip) throws IOException {
        byte[] itemCountBytes = new byte[4];
        gzip.read(itemCountBytes);
        int itemCount = bytesToInt(itemCountBytes);
        System.out.println("item count: " + itemCount);
        double[][] data = new double[10][itemCount];
        for (int i = 0; i < itemCount; i++) {
            data[gzip.read()][i] = 1.0;;
        }
        System.out.println("finished");
        return data;
    }

    private static double[][] importImageFile(GZIPInputStream gzip) throws IOException {
        byte[] infoBytes = new byte[4];
        gzip.read(infoBytes);
        int itemCount = bytesToInt(infoBytes);
        gzip.read(infoBytes);
        int rowCount = bytesToInt(infoBytes);
        gzip.read(infoBytes);
        int colCount = bytesToInt(infoBytes);
        System.out.println("item count: " + itemCount);
        System.out.println("row count: " + rowCount);
        System.out.println("col count: " + colCount);
        Vector[] data = new Vector[itemCount];
        int pixelCount = rowCount * colCount;
        for (int i = 0; i < itemCount; i++) {
            double[] vec = new double[pixelCount];
            for (int j = 0; j < pixelCount; j++) {
                vec[j] = gzip.read() / 255.0;
            }
            data[i] = new Vector(vec);
        }
        System.out.println("finished");
        return data;
    }
}
