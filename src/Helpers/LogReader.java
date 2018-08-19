package Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LogReader {
    private BufferedReader reader;

    public String readLine(ArrayList<Double> info) {
        try {
            String s = reader.readLine();
            if (s == null) {
                return null;
            }
            String[] strings = s.split(" ");
            //System.out.println(strings.length);
            String markdown = strings[0];
            info.clear();
            //System.out.println("A");
            for (int i = 1; i < strings.length; ++i) {
                //System.out.println(strings[i]);
                //System.out.println(Double.valueOf(strings[i].replace(',', '.')));
                info.add(Double.valueOf(strings[i].replace(',', '.')));
            }
            return markdown;
        } catch (Exception e) {
            System.out.println(e);
            //System.out.printf("%f\n", 27.0000);
            //System.out.println("Reader: error while reading");
            System.exit(1);
        }
        return "";
    }

    public void close() {
        try {
            this.reader.close();
        } catch (Exception e) {
            System.out.println("Reader: error while closing file");
            System.exit(1);
        }
    }

    public LogReader(String log_filename) {
        try {
            this.reader = new BufferedReader(new FileReader(log_filename));
        } catch (Exception e) {
            this.reader = null;
            System.out.println("Reader: cannot open file " + log_filename);
            System.exit(1);
        }
    }
}
