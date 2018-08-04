package Helpers;

import java.io.PrintWriter;


public class Logger {
    private PrintWriter writer;

    public void updateLog(String markdown, double[] info) {
        if (writer != null) {
            writer.print(markdown);
            for (int i = 0; i < info.length; ++i) {
                writer.printf(" %f", info[i]);
            }
            writer.println();
        }
    }

    public void close() {
        this.writer.close();
    }

    public Logger(String log_filename) {
        try {
            this.writer = new PrintWriter(log_filename, "UTF-8");
        } catch (Exception e) {
            this.writer = null;
            System.out.println("Logging disabled: cannot open file " + log_filename);
        }
    }
}
