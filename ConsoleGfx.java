import java.io.File;
import java.io.FileInputStream;

public class ConsoleGfx
{
    public static final String defaultTop = "═";
    public static final String defaultUpLeft = "╔";
    public static final String defaultUpRight = "╗";
    public static final String defaultStart = "║";
    public static final String defaultEnd = "║";
    public static final String defaultBottom = "═";
    public static final String defaultLowLeft = "╚";
    public static final String defaultLowRight = "╝";

    // Reset
    private static final String COLOR_RESET = "\033[0m";  // Text Reset
    private static String[] fgPalette, emPalette, ulPalette, bgPalette;

    private static final byte   BLACK = 0, RED = 1, DARK_GREEN = 2, GOLD = 3,
                                BLUE = 4, GARNETT = 5, ORANGE = 6, LIGHT_GRAY = 7,
                                GRAY = 8, PEACH = 9, GREEN = 10, BRIGHT_GOLD = 11,
                                CYAN = 12, MAGENTA = 13, BRIGHT_ORANGE = 14, WHITE = 15;

    private static final byte CLEAR = MAGENTA;
    private static final byte TRANS_DISPLAY = BLACK;

    public static byte[] testRainbow =
    {
        16, 2,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
    };

    public static byte[] testImage =
    {
        14, 6,
        CLEAR, CLEAR, GREEN, GREEN, GREEN, CLEAR, CLEAR, CLEAR, CLEAR, CLEAR, CLEAR, GREEN, GREEN, CLEAR,
        CLEAR, GREEN, WHITE, BLACK, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, DARK_GREEN, GREEN, GREEN,
        GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, CLEAR,
        GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, BLACK, BLACK, BLACK, GREEN, CLEAR,
        GREEN, GREEN, GREEN, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, GREEN, GREEN, GREEN, CLEAR, CLEAR,
            CLEAR, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, GREEN, CLEAR, CLEAR, CLEAR, CLEAR, CLEAR
    };

    static
    {
        fgPalette = new String[16];
        emPalette = new String[16];
        ulPalette = new String[16];
        bgPalette = new String[16];

        for (int index = 0; index < 8; index++)
        {
            fgPalette[index] = "\033[3" + index + "m";
            fgPalette[index+8] = "\033[9" + index + "m";
            emPalette[index] = "\033[1;3" + index + "m";
            emPalette[index+8] = "\033[1;9" + index + "m";
            ulPalette[index] = "\033[4;3" + index + "m";
            ulPalette[index+8] = "\033[4;9" + index + "m";
            bgPalette[index] = "\033[4" + index + "m";
            bgPalette[index+8] = "\033[10" + index + "m";
        }
    }

    public static void displayImage(byte[] imageData)
    {
        displayImage(imageData, defaultTop, defaultUpLeft, defaultUpRight, defaultStart, defaultEnd, defaultBottom, defaultLowLeft, defaultLowRight);
    }

    public static void displayImage(byte[] imageData, String top, String upLeft, String upRight, String start, String end, String bottom, String lowLeft, String lowRight)
    {
        displayImage(imageData, false, top, upLeft, upRight, start, end, bottom, lowLeft, lowRight);
    }

    public static void displayImage(byte[] imageData, boolean compressed, String top, String upLeft, String upRight, String start, String end, String bottom, String lowLeft, String lowRight)
    {
        // If the image is compressed, uncompress it.
        if (compressed)
            imageData = decompressImage(imageData);

        int width = imageData[0];
        int height = imageData[1];
        int dataIndex = 2;

        System.out.print(upLeft);
        for (int xIndex = 0; xIndex < width; xIndex += 1)
            System.out.print(top);
        System.out.println(upRight);

        for (int yIndex = 0; yIndex < height; yIndex += 2)
        {
            String outputString = start;
            for (int xIndex = 0; xIndex < width; xIndex += 1)
            {
                int outputColor = imageData[dataIndex];
                outputString += fgPalette[outputColor == CLEAR ? TRANS_DISPLAY : outputColor];
                outputColor = yIndex + 1 < height ? imageData[dataIndex + width] : CLEAR;
                outputString += bgPalette[outputColor == CLEAR ? TRANS_DISPLAY : outputColor];
                outputString += "▀";
                dataIndex++;
            }
            dataIndex += width;
            System.out.println(outputString + COLOR_RESET + end);
        }

        System.out.print(lowLeft);
        for (int xIndex = 0; xIndex < width; xIndex += 1)
            System.out.print(bottom);
        System.out.println(lowRight);
    }

    public static byte[] decompressImage(byte[] imageData)
    {
        int dataLength = 0;

        // First, reformat the data, cramming two data points together.
        byte[] reformattedData = new byte[imageData.length / 2 + 1];

        // Mash the data together and count the length
        for (int index = 0; index < reformattedData.length; index++)
        {
            byte length = (byte) (0x0F & (imageData[index*2]));
            byte color = (byte) (0xF0 & (imageData[index*2+1] << 4));
            reformattedData[index] = (byte) (color | length);
            dataLength += length;
        }

        byte[] uncompressedData = new byte[dataLength];
        dataLength = 0;

        // Populate and return uncompressed data
        for (int index = 0; index < reformattedData.length; index++)
        {
            byte color = (byte) (0x0F & (reformattedData[index] >> 4));
            byte length = (byte) (0x0F & (reformattedData[index]));

            for (int runLength = 0; runLength < length; runLength++)
                uncompressedData[dataLength + runLength] = color;

            dataLength += length;
        }

        return uncompressedData;
    }

    public static byte[] loadFile(String filename)
    {
        byte[] fileData = null;
        try
        {
            File myFile = new File(filename);
            fileData = new byte[(int) myFile.length()];
            FileInputStream fileStream = new FileInputStream(myFile);

            int bytesRead = 0;
            int result = 0;

            while (bytesRead < fileData.length && result != -1)
            {
                result = fileStream.read(fileData, bytesRead, fileData.length - bytesRead);
                if (result > 0) bytesRead += result;
            }
            fileStream.close();
        }
        catch (Exception e) { }

        return fileData;
    }
}
