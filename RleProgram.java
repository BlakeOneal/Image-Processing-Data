import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public class RleProgram {

    // This is a comment for my Lab 6 :)
// The following method prints out the program's menu
    public static void printMenu() {
        System.out.println("\nRLE Menu");
        System.out.println("--------");
        System.out.println("0. Exit");
        System.out.println("1. Load File");
        System.out.println("2. Load Test Image");
        System.out.println("3. Read RLE String");
        System.out.println("4. Read RLE Hex String");
        System.out.println("5. Read Data Hex String");
        System.out.println("6. Display Image");
        System.out.println("7. Display RLE String");
        System.out.println("8. Display Hex RLE Data");
        System.out.println("9. Display Hex Flat Data\n");
        System.out.print("Select a Menu Option: ");
    }
// The following method loads a file utilizing string filename from user input
    public static byte[] loadFile(String fileName) {
        byte [] data = ConsoleGfx.loadFile(fileName);
        return data;
    }
// The following method converts a byte of data into a hex string
    public static String toHexString(byte[] data) {
        String dataString = "";
        String dataStringHolder;
        int i;
        for (i = 0; i < data.length; i++) {
            // Changes data at [i] to a hexadecimal value
            dataStringHolder = Integer.toHexString(data[i]);
            dataString = dataString + dataStringHolder;
        }
        return dataString;
    }
// The following method counts the total number of "runs" in a byte of flat data
    public static int countRuns(byte[] flatData) {
        int runCount = 1;
        // consecutivenumbers serves as an instance to aid in overflow issues if the run goes past 15
        int consecutiveNumbers = 1;
        int i;
        for (i = 0; i < flatData.length - 1; i++) {
            if (flatData[i] != flatData[i + 1]) {
                runCount ++;
                consecutiveNumbers = 1;
            }
            if (flatData[i] == flatData[i + 1]) {
                consecutiveNumbers++;
                if (consecutiveNumbers > 15){
                    runCount++;
                    consecutiveNumbers = 1;
                }
            }
        }
        return runCount;
    }
// The following method serves to encode a byte[] array of flat data into a byte of rle
    public static byte[] encodeRle(byte[] flatData) {
        // Sets the size of the array
        int sizeOfArray = 2 * countRuns(flatData);
        byte[] encodedRle = new byte[sizeOfArray];
        int i;
        int j;
        int numberRepeated;
        int numberOfRepeats = 1;
        // Placeholder values used to gain access to variables i and j outside of the while loop
        int jPlaceHolder = 0;
        int iPlaceHolder = 0;
        int numberOfRepeatsRemaining;
        // Loop will go until i is less than flatdata.length - 1, used to limit array out of bounds exceptions
        for (i = iPlaceHolder; i < flatData.length - 1; i++) {
            iPlaceHolder++;
            numberRepeated = flatData[i];
            while (flatData[i] == flatData[i + 1]) {
                numberOfRepeats++;
                break;
            }
            // Special cases where "i" would go out of bounds, while loop using iplaceholder serves to mitigate the chance of an exception occuring
            while (flatData[i] != flatData[i + 1] || (i == flatData.length - 2)) {
// IplaceHolder used to track i value as i is one behind in the array length
                if (iPlaceHolder == flatData.length - 1 || iPlaceHolder == flatData.length) {
                    jPlaceHolder = sizeOfArray - 4;
                    numberRepeated = flatData[iPlaceHolder - 1];
                    while (numberOfRepeats > 15) {
                        numberOfRepeatsRemaining = numberOfRepeats - 15;
                        encodedRle[jPlaceHolder] = (byte) 15;
                        encodedRle[jPlaceHolder + 1] = (byte) numberRepeated;
                        numberOfRepeats = numberOfRepeatsRemaining;
                        jPlaceHolder += 2;
                        break;
                    }
                    // Special instance where runs goes over 15
                    if (numberOfRepeats <= 15) {
                        encodedRle[jPlaceHolder] = (byte) numberOfRepeats;
                        encodedRle[jPlaceHolder + 1] = (byte) numberRepeated;
                        numberOfRepeats = 1;
                        jPlaceHolder += 2;
                    }
                    // Special instance where the last element in an array has a numberOfRepeats value of 1
                    while ((iPlaceHolder == flatData.length - 1) && (numberOfRepeats == 1)) {
                        numberRepeated = flatData[iPlaceHolder];
                        encodedRle[jPlaceHolder] = (byte) 1;
                        encodedRle[jPlaceHolder + 1] = (byte) numberRepeated;
                        break;
                    }
                    iPlaceHolder++;
                }
                // Regular assignment of values if special circumstances are not met
                for (j = jPlaceHolder; j < encodedRle.length - 1; j++) {
                    if (numberOfRepeats > 15) {
                        // Special circumstance where repeats exceed 15
                        while (numberOfRepeats > 15) {
                            numberOfRepeatsRemaining = numberOfRepeats - 15;
                            encodedRle[j] = (byte) 15;
                            encodedRle[j + 1] = (byte) numberRepeated;
                            jPlaceHolder += 2;
                            j = jPlaceHolder;
                            numberOfRepeats = numberOfRepeatsRemaining;
                        }
                        // End of special case, consider repeats below or equal to 15
                    } else {
                        j = jPlaceHolder;
                        encodedRle[j] = (byte) numberOfRepeats;
                        encodedRle[j + 1] = (byte) numberRepeated;
                        numberOfRepeats = 1;
                        jPlaceHolder += 2;
                        break;
                    }
                }
                break;
            }
        }
        return encodedRle;
    }
    // The following method provides the length of the decoded byte holding rleData
    public static int getDecodedLength(byte[] rleData) {
        int i;
        int decompressedSizeRLEData = 0;
        int sizeRLEData;
        // Enumerates through the array, incrementing length by 1
        for (i = 0; i < rleData.length - 1; i = i + 2) {
            sizeRLEData = rleData[i];
            decompressedSizeRLEData = decompressedSizeRLEData + sizeRLEData;
        }
        return decompressedSizeRLEData;
    }

    // The following method decodes a byte holding rleData and transfers that data into a byte holding string Data
    public static byte[] decodeRle(byte[] rleData) {
        // Calls on getDecodedLength to get the size of the array
        int size = getDecodedLength(rleData);
        int i;
        // Array size passed into array declaration
        byte[] decodedRle = new byte[size];
        int index = 0;
        // Works through the array, enumerating the amount of times a value appears by 1 consecutively, and keeping track of the value itself
        for (i = 0; i < rleData.length - 1; i += 2) {
            int value = rleData[i + 1];
            int times = rleData[i];
            for (int j = 0; j < times; j++) {
                // Increases the index so data is not overwritten
                decodedRle[index] = (byte) value;
                index++;
            }
        }
        return decodedRle;
    }
// The following method converts a dataString into a byte holding string Data
    public static byte[] stringToData(String dataString) {
        // Length given by the length of the data String
        byte[] stringData = new byte[dataString.length()];
        int stringLength = dataString.length();
        int i;
        for (i = 0; i < stringLength; i++) {
            // Hex values provided by parsing the index value of the array
            stringData[i] = Byte.parseByte(String.valueOf(dataString.charAt(i)), 16);
        }
        System.out.println(stringData);
        return stringData;
    }
// The following method converts a byte holding rle data into a string holding rle data
    public static String toRleString(byte[] rleData) {
        // Declare an empty string to fill
        String rleString = "";
        String rleStringHolder;
        int i;
        int j = 1;
        int iPlaceHolderTwo;
        // Length given by rleData array
        byte[] stringToHex = new byte[rleData.length];
        String hexValues;
        for (i = 1; i < rleData.length; i+=2) {
            int iPlaceHolder = i;
            if (iPlaceHolder > 1) {
                // Encodes hex values for every other value in the array to ensure rleData is provided
                iPlaceHolder -= 1;
                stringToHex[iPlaceHolder - 1] = rleData[i];
            }
            else {
                stringToHex[i - 1] = rleData[i];
            }
        }
        // Converts values to hex
        hexValues = toHexString(stringToHex);
        for (i = 0; i < rleData.length; i+=2) {
            Arrays.toString(rleData);
            iPlaceHolderTwo = i;
            // Ensures that there are two cases considered: One where two values appear before the :, and one where three values appear before the :. For example, "13f:64"
            if (iPlaceHolderTwo == 0) {
                j = i;
            }
            else if (iPlaceHolderTwo >= 2) {
                j = i - 1;
            }
            // Parses the string value of the integer to add to the string
            rleStringHolder = String.valueOf(rleData[i]) + hexValues.charAt(j);
            rleString = rleString + rleStringHolder;
            if (i < rleData.length - 2) {
                // Adds the delimiter between values
                rleString = rleString + ":";
            }
        }
        return rleString;
    }
// This method converts a String of rleData into a byte array of rleData
    public static byte[] stringToRle(String rleString) {
        // Rids the string of all instances of colons to remove the delimiter
        String[] elementsToDecode = rleString.split(":");
        int i;
        int j;
        int count = 0;
        int countHolder = count;
        int iCounter = 0;
        // 2 * length to take into account number of elements, element
        byte[] stringToRle = new byte[2 * elementsToDecode.length];
        for (i = 0; i < elementsToDecode.length; i++) {

            for (j = 0; j < elementsToDecode[i].length(); j++) {
                count++;
                countHolder = count;
            }
            // Takes into account the two cases mentioned before, where three values appeared before the delimiter
            if (countHolder == 3) {

                stringToRle[iCounter] = Byte.parseByte(elementsToDecode[i].substring(0,2));
                stringToRle[iCounter + 1] = Byte.parseByte(elementsToDecode[i].substring(2) + "", 16);
                countHolder = 0;
                count = countHolder;
                iCounter += 2;
            }
            // Takes into account the two cases mentioned before, where two values appeared before the delimiter
            if (countHolder == 2) {

                stringToRle[iCounter] = Byte.parseByte(elementsToDecode[i].substring(0,1));
                stringToRle[iCounter + 1] = Byte.parseByte(elementsToDecode[i].substring(1) + "", 16);
                countHolder = 0;
                count = countHolder;
                iCounter += 2;

            }
        }
        return stringToRle;
    }
// The following serves as the main method of the encoder
    public static void main(String[] args) {
        System.out.println("Welcome to the RLE image encoder!\n");
        System.out.println("Displaying Spectrum Image:");
        // Prints the testRainbow color spectrum
        ConsoleGfx.displayImage(ConsoleGfx.testRainbow);
        System.out.println("\n");
        // Calls on print menu to print the menu
        printMenu();
        int userOption;
        Scanner Scanner = new Scanner(System.in);
        userOption = Scanner.nextInt();
        // Variables/Bytes declared to keep track of data when printing out values
        byte[] imageData = new byte[0];
        byte[] rleByteData = new byte[0];
        byte[] flatByteData = new byte[0];
        byte[] dataHolder = new byte[0];
        String rleDataHolderDecimal;
        String rleHexString = "";
        String rleFlatString = "";
        byte[] rleData = new byte[0];
        String rleDataHex;
        String flatString;
        String rleString = "";
        // Threshold to provide error message
        if (userOption < 0 || userOption > 9) {
            System.out.println("Error! Invalid input.");
            printMenu();
            userOption = Scanner.nextInt();
        }
        // While loop used to implement error message
        while (userOption <= 9999 && userOption >= -9999) {
            if (userOption == 0) {
                System.exit(0);
            }
// Option one loads a file through file name
            if (userOption == 1) {
                System.out.print("Enter name of file to load: ");
                String fileName;
                fileName = Scanner.next();
                // All possible data values assigned to imageData to aid in printing out results of data, calls on methods to gain results
                imageData = loadFile(fileName);
                rleByteData = encodeRle(imageData);
                rleString = toRleString(rleByteData);
                rleHexString = toHexString(rleByteData);
                flatByteData = decodeRle(rleByteData);
                rleFlatString = toHexString(flatByteData);
                printMenu();
                userOption = Scanner.nextInt();
            }
// Option two loads testImage data into the function
            if (userOption == 2) {
                imageData = ConsoleGfx.testImage;
                // All possible data values assigned to imageData to aid in printing out results of data, calls on methods to gain results
                rleByteData = encodeRle(imageData);
                rleString = toRleString(rleByteData);
                rleHexString = toHexString(rleByteData);
                flatByteData = decodeRle(rleByteData);
                rleFlatString = toHexString(flatByteData);
                System.out.print("Test image data loaded.\n");
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Method 3 calls for the user to enter an RLE string to become decoded
            if (userOption == 3) {
                System.out.print("Enter an RLE string to be decoded: ");
                rleDataHolderDecimal = Scanner.next();
                // All possible data values assigned to rleData to aid in printing out results of data, calls on methods to gain results
                rleByteData = stringToRle(rleDataHolderDecimal);
                flatByteData = decodeRle(rleByteData);
                rleString = toRleString(rleByteData);
                rleHexString = toHexString(rleByteData);
                rleFlatString = toHexString(flatByteData);
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 4 calls for the user to enter a hex string holding RLE data to become decoded
            if (userOption == 4) {
                System.out.print("Enter the hex string holding RLE data: ");
                rleDataHex = Scanner.next();
                // All possible data values assigned to hexDataRLE to aid in printing out results of data, calls on methods to gain results
                dataHolder = stringToData(rleDataHex);
                rleString = toRleString(dataHolder);
                rleByteData = stringToRle(rleString);
                flatByteData = decodeRle(rleByteData);
                rleHexString = toHexString(rleByteData);
                rleFlatString = toHexString(flatByteData);
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 5 calls for the user to enter a hex string holding flat data to become decoded
            if (userOption == 5) {
                System.out.print("Enter the hex string holding flat data: ");
                flatString = Scanner.next();
                // All possible data values assigned to hexDataFlat to aid in printing out results of data, calls on methods to gain results
                flatByteData = stringToData(flatString);
                rleByteData = encodeRle(flatByteData);
                rleString = toRleString(rleByteData);
                rleHexString = toHexString(rleByteData);
                rleFlatString = toHexString(flatByteData);

                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 6 calls on display image function, displays image from loaded data
            if (userOption == 6) {
                // Error message if no data is loaded
                System.out.println("Displaying image...");
                if (imageData.length == 0) {
                    System.out.print("(No Data)\n");
                }
                else  {
                    // Calls on ConsoleGfx.displayImage method to display image
                    ConsoleGfx.displayImage(imageData);
                }
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 7 displays RLE representation of data currently held within the program
            if (userOption == 7) {
                // Error message if no data is loaded
                if (rleString.length() == 0) {
                    System.out.print("(No Data)\n");
                }
                // Prints data to screen
                else {
                    System.out.println("RLE representation: " + rleString);
                }
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 8 displays RLE hex values of data currently held within the program
            if (userOption == 8) {
                // Error message if no data is loaded
                if (rleHexString.length() == 0) {
                    System.out.println("(No Data)\n");
                }
                // Prints data to screen
                else {
                    System.out.println("RLE hex Values: " + rleHexString);
                }
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Option 9 displays flat hex values of data currently held within the program
            if (userOption == 9) {
                // Error message if no data is loaded
                if (rleFlatString.length() == 0) {
                    System.out.println("(No Data)\n");
                }
                else {
                    System.out.println("Flat hex values: " + rleFlatString);
                }
                printMenu();
                userOption = Scanner.nextInt();
            }
            // Threshold to provide error message
            if (userOption < 0 || userOption > 9) {
                System.out.println("Error! Invalid input.");
                printMenu();
                userOption = Scanner.nextInt();
            }
        }
    }
}