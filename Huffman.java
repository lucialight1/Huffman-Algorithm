
import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Comparator;


class HuffmanNode implements Serializable{

    int data;
    char c;
    HuffmanNode left;
    HuffmanNode right;
}


class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y)
    {

        return x.data - y.data;
    }
}

public class Huffman {

    private static ArrayList<String> CharacterCode = new ArrayList<String>();



    //This method prints out the code of each character in the Huffman Tree
    //If we want to obtain the code of a character, we have to look through the tree

    public static void printCode(HuffmanNode root, String s) {

        if (root.left == null && root.right == null) {
            System.out.println(root.c + ":" + s);
            CharacterCode.add(root.c + s);
            return;
        }

        printCode(root.left, s + "0");
        printCode(root.right, s + "1");
    }

    //This method converts the string of the text file into a byte array
    //This method will make it easier to write to the compressed file (binary file)


    public static byte[] getBinary(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        s = sBuilder.toString();

        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        } return data;
    }


    //This method converts the byte array into a string
    //This is for the decompression of the compressed file
    //It will get the byte array from the compressed file and convert it into a string, so it's easier to write to the decompressed file (text file)

    public static String GetString(byte[] bytes){
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }



    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("");
        Scanner in = new Scanner(file);
        String string = "";

        while(in.hasNextLine()){
            string = string+in.nextLine();
        }
        in.close();

        char[] charArray = string.toCharArray();
        ArrayList<Character> charContent=new ArrayList<>();
        ArrayList<Integer> frequencies=new ArrayList<>();

        for(Character character:charArray){
            if(!charContent.contains(character)){
                charContent.add(character);
            }
        }

        for(Character charCont: charContent){
            int counter = 0;
            for(Character charArr: charArray){
                if(charArr==charCont){
                    counter++;
                }
            }
            frequencies.add(counter);
        }

        int n=charContent.size();

        PriorityQueue<HuffmanNode> priorityqueue = new PriorityQueue<HuffmanNode>(n, new MyComparator());

        for (int i = 0; i < n; i++) {


            HuffmanNode huffman = new HuffmanNode();

            huffman.c = charContent.get(i);
            huffman.data = frequencies.get(i);

            huffman.left = null;
            huffman.right = null;

            priorityqueue.add(huffman);
        }

        HuffmanNode root = null;

        while (priorityqueue.size() > 1) {

            HuffmanNode x = priorityqueue.peek();
            priorityqueue.poll();

            HuffmanNode y = priorityqueue.peek();
            priorityqueue.poll();

            HuffmanNode huffmannode = new HuffmanNode();

            huffmannode.data = (x.data + y.data);
            huffmannode.c = '-';

            huffmannode.left = x;

            huffmannode.right = y;

            root = huffmannode;

            priorityqueue.add(huffmannode);
        }

        if (root != null) {
            printCode(root, "");
        }


        File file1 = new File("");
        Scanner in1 = new Scanner(file1);
        String string1 = "";

        while(in1.hasNextLine()){
            string1 = string1+in1.nextLine();
        }
        in1.close();

        char[] charArray1 = string1.toCharArray();


        File fileOfBytes = new File("compressed.bin");
        FileOutputStream fos = new FileOutputStream(fileOfBytes);

        String s = "";

        for (Character charac : charArray1) {
            for (String code : CharacterCode) {
                if (code.charAt(0)==charac) {
                    s = s+code.substring(1);
                }
            }
        }
        byte[] binaryString = getBinary(s);
        try {
            fos.write(binaryString);
            fos.close();
        } catch (IOException e) {
            System.out.println("Couldn't make file");
        }



        try {


            FileInputStream fis = new FileInputStream("compressed.bin");
            FileOutputStream fis1 = new FileOutputStream(new File("decompressed.txt"));
            byte[] allBytes = fis.readAllBytes();
            String stringOfBits = GetString(allBytes);

            HuffmanNode newRoot = root;
            for(Character character: stringOfBits.toCharArray()){
                if(character=='0'){
                    newRoot = newRoot.left;
                }
                else if(character=='1') {
                    newRoot = newRoot.right;
                }
                if(newRoot.left==null && newRoot.right==null){
                    fis1.write(newRoot.c);
                    newRoot = root;
                }
            }

            fis1.close();
            fis.close();

        }catch(IOException e){
            System.out.println("Couldn't write to file");
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("huffmantree.ser"));
            out.writeObject(root);
            out.close();

        }catch(IOException e){
            System.out.println(e.toString());
            System.out.println("Cannot print out tree");

        }



    }
}
