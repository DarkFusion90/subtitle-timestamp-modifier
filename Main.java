
/**
 * Program Description: 
 *  This program accepts a subtitle file as a command line argument and modifies the 
 *  timestamps according to the input of the user. 
 *  (Adds or subtracts a specific number of milliseconds in each timestamp)
 *  As of now, only positive time values are accepted.
 * 
 * Started: 16th October 2018 10:00 IST
 * Completed: 16th October 2018 14:30 IST
 * 
 * @author Gaurav Adhikari
 * @version 1.0
 */

import java.util.regex.*;
import java.io.*;
import java.util.*;

class Main {

  static Scanner scanner;

  public static void main(String[] args) {
    FileWriter outputFile;
    try {
      outputFile = new FileWriter("changedSubs.srt");
      operate(outputFile, args[0]);
    } catch (IOException ioe) {
      System.out.println("Exception occured while creating new file: " + ioe.getMessage());
    }
  }

  /**
   * Function performs all the operations of the program
   *
   * @param outputFile    The FileWriter instance for the file to write the modified subtitles
   * @param inputFileName The name (path) of the input file (original subtitle
   *                      file)
   */
  static void operate(FileWriter outputFile, String inputFileName) {
    scanner = new Scanner(System.in);
    
    String pat = "([0-9]){2}:([0-9]){2}:([0-9]){2},([0-9]){1,}";
    Pattern pattern = Pattern.compile(pat);
    Matcher matcher;
    String originalFile = "";

    System.out.println("Mention delay (in ms): ");
    long delay = scanner.nextInt();

    try (FileReader reader = new FileReader(inputFileName)) {
      originalFile = getContents(reader);
    } catch (IOException io) {
      System.out.println("Exception while reading to string: " + io.getCause());
    }
    scanner = new Scanner(originalFile);

    while (scanner.hasNext()) {
      String token = scanner.next(), modifiedtoken;
      matcher = pattern.matcher(token);

      if (matcher.matches()) {
        modifiedtoken = modifyTimestamp(token, delay);
        originalFile = originalFile.replaceAll(token, modifiedtoken);
      }
    }

    try {
      outputFile.write(originalFile);
    } catch (IOException io) {
      System.out.println("Exception while writing to string: " + io.getCause());
    }

    try {
      scanner.close();
      outputFile.close();
    } catch (Exception e) {
      System.out.println("Exception while closing " + e.getCause());
    }

  }

  /**
   * Function modifies a timestamp by subtracting or adding the amount of milliseconds represented by @param delay
   * 
   * @param original The original timestamp token to modify [Sample: "00:54:47,123"]
   * @param delay    The amount of delay required in the timestamp
   * @return The modified timestamp
   */
  static String modifyTimestamp(String original, long delay) {
    StringBuffer modified = new StringBuffer();

    String[] splitStr = original.split(":");
    long hour = Long.parseLong(splitStr[0]);
    long min = Long.parseLong(splitStr[1]);

    String[] sec_millis = splitStr[2].split(",");
    long sec = Long.parseLong(sec_millis[0]);
    long millis = Long.parseLong(sec_millis[1]);

    long[] timestamp = fragmentTime(delay);

    hour += timestamp[0];
    min += timestamp[1];
    sec += timestamp[2];
    millis += timestamp[3];

    Formatter fmt = new Formatter();
    String str;

    str = fmt.format("%02d", hour).toString();
    modified.append(str).append(":");
    fmt.close();
    fmt = new Formatter();

    str = fmt.format("%02d", min).toString();
    modified.append(str).append(":");
    fmt.close();
    fmt = new Formatter();

    str = fmt.format("%02d", sec).toString();
    modified.append(str).append(",");
    fmt.close();
    fmt = new Formatter();

    str = fmt.format("%03d", millis).toString();
    modified.append(str);
    fmt.close();

    return modified.toString();
  }

  /**
   * Function fragments a time into hours, minutes, seconds and milliseconds
   * Example: 64560 (milliseconds) equals [0 hour, 1 minute, 4 seconds and 560 milliseconds
   * 
   * @param time The time value to fragment
   * @return the array of fragmented time
   */
  static long[] fragmentTime(long time) {
    long[] result = new long[4];
    long HOUR = 3600000L, MIN = 60000L, SEC = 1000L;
    long hour = 0, min = 0, sec = 0, millis = 0;

    while (time > 0) {
      if (time > HOUR) {
        hour = time / HOUR;
        time = time % HOUR;
      } else if (time > MIN) {
        min = time / MIN;
        time = time % MIN;
      } else if (time > SEC) {
        sec = time / SEC;
        time = time % SEC;
      } else if (time < SEC) {
        millis = time;
        time = time / SEC;
      }
    }

    result[0] = hour;
    result[1] = min;
    result[2] = sec;
    result[3] = millis;

    return result;
  }

  /**
   * Function copies contents of a file into a String object
   * 
   * @param reader The FileReader instance of the input file
   * @return The contents of the input file wrapped into a String object
   */

  static String getContents(FileReader reader) {
    StringBuffer response = new StringBuffer();

    try {
      int c;
      while ((c = reader.read()) != -1) {
        response.append((char) c);
      }
    } catch (IOException io) {
      System.out.println("Exception while reading to string: " + io.getCause());
    }

    return response.toString();
  }

}
