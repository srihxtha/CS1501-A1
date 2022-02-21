import java.io.*;
import java.util.*;

public class Crossword 
{

	private DictInterface D;
	private char [][] theBoard;
	private int size;


	Scanner inScan = new Scanner(System.in);
	Scanner fReader;
	File fName;
	String fString = "";



	public static void main(String [] args) throws IOException
	{
		new Crossword();
	}

	public Crossword() throws IOException 
	{
		//Read the dictionary
		Scanner fileScan = new Scanner(new FileInputStream("dict8.txt"));
		String st;
		D = new MyDictionary();

		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}
		fileScan.close();

		// Make sure the file name for the test file is valid
		while (true)
		{
			try
			{
				System.out.println("Please enter test file filename:");
				fString = inScan.nextLine();
				fName = new File(fString);
				fReader = new Scanner(fName);

				break;
			}
			catch (IOException e)
			{
				System.out.println("Problem: " + e);
			}
		}

		//create the board

		size = Integer.parseInt(fReader.nextLine());

		theBoard = new char[size][size];

		for (int i = 0; i < size; i++)
		{
			String rowString = fReader.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}
		fReader.close();


		StringBuilder[] rowStr =new StringBuilder[size];
			for (int i = 0; i < rowStr.length; i++) 
			{
    			rowStr[i] = new StringBuilder("");
			}

		StringBuilder[] colStr =new StringBuilder[size];
			for (int i = 0; i < colStr.length; i++) 
			{
    			colStr[i] = new StringBuilder("");
			}

		int[] lastMinusPosRow = new int [size];
			for (int i = 0; i < lastMinusPosRow.length; i++)
			{
				lastMinusPosRow[i] = -1;
			}

		int[] lastMinusPosCol = new int [size];
		for (int i = 0; i < lastMinusPosCol.length; i++)
		{
			lastMinusPosCol[i] = -1;
		}

		solve(0, 0, rowStr, colStr,theBoard, lastMinusPosRow, lastMinusPosCol);


	}

	//solve method 
	void solve(int r, int c, StringBuilder[] rowStr, StringBuilder[] colStr, char[][] theBoard, int [] lastMinusPosRow, int [] lastMinusPosCol)
{


	if (theBoard[r][c] == '+') {
		
		for (char ch = 'a'; ch <= 'z'; ch++)
		{
			if (isValid(r, c, rowStr, colStr, ch,lastMinusPosRow, lastMinusPosCol)) {
				rowStr[r].append(ch);
				colStr[c].append(ch);

				if (r == size -1 && c == size -1){
					// if its the end of the board, stop, because were done
					printMethod(rowStr, colStr);
					System.exit(0);
				} else {
					// try solving the next column over
					if (c < size - 1){
						solve(r, c+1, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
					} else {
						// solve with r+1
						solve(r+1, 0, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
					}
					// then we know it was not valid
					// delete the characters on rowStr and colStr
					rowStr[r].deleteCharAt(rowStr[r].length()-1);
					colStr[c].deleteCharAt(colStr[c].length()-1);
				}
			}
		}

	} else if (theBoard[r][c] == '-'){
		int prevLastMinusPosRow = lastMinusPosRow[r];
		int prevLastMinusPosCol = lastMinusPosCol[c];
		lastMinusPosRow[r] = c;
		lastMinusPosCol[c] = r;
		rowStr[r].append(" ");
		colStr[c].append(" ");
		if (r == size -1 && c == size -1){
			// if its the end of the board, stop, because were done
			printMethod(rowStr, colStr);
			System.exit(0);
		} else {
			// try solving the next column over
			if (c < size - 1){
				solve(r, c+1, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
			} else {
				// solve with r+1
				solve(r+1, 0, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
			}
			// then we know it was not valid
			// delete the characters on rowStr and colStr
			rowStr[r].deleteCharAt(rowStr[r].length()-1);
			colStr[c].deleteCharAt(colStr[c].length()-1);

			//reset the last minus when it is not found
			lastMinusPosRow[r] = prevLastMinusPosRow;
			lastMinusPosCol[c] = prevLastMinusPosCol;
		}


	} else{
		char ch = theBoard[r][c];
		if (isValid(r, c, rowStr, colStr, ch, lastMinusPosRow, lastMinusPosCol)) {
			rowStr[r].append(ch);
			colStr[c].append(ch);

			if (r == size -1 && c == size -1){
				// if its the end of the board, stop, because were done
				printMethod(rowStr, colStr);
				System.exit(0);
			} else {
				// try solving the next column over
				if (c < size - 1){
					solve(r, c+1, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
				} else {
					// solve with r+1
					solve(r+1, 0, rowStr, colStr, theBoard, lastMinusPosRow, lastMinusPosCol);
				}
				// then we know it was not valid
				// delete the characters on rowStr and colStr
				rowStr[r].deleteCharAt(rowStr[r].length()-1);
				colStr[c].deleteCharAt(colStr[c].length()-1);
			}
		}
	}
}

	private boolean isValid(int r, int c, StringBuilder[] rowStr, StringBuilder[] colStr, char ch, int [] lastMinusPosRow, int [] lastMinusPosCol)
	{
		StringBuilder currentRowWord = rowStr[r];
		currentRowWord = new StringBuilder(currentRowWord).append(ch);
		int resultRow;
		if (lastMinusPosRow[r] > -1) {
			resultRow = D.searchPrefix(currentRowWord, lastMinusPosRow[r] + 1, currentRowWord.length() - 1);
		}else {
			resultRow = D.searchPrefix(currentRowWord);
		}


		if (resultRow == 0) {
			return false;
		}   
		

		// same thing for column		

		StringBuilder currentColWord = colStr[c];
		currentColWord = new StringBuilder(currentColWord).append(ch);

		int resultCol;
		if (lastMinusPosCol[c] > -1) {
			resultCol = D.searchPrefix(currentColWord, lastMinusPosCol[c] + 1, currentColWord.length() - 1);
		}else {
			resultCol = D.searchPrefix(currentColWord);
		}


		if (resultCol == 0) {
			return false;
		}   

		// if last row, column should be a valid word
		if(r == size - 1 && !(resultCol == 2 || resultCol == 3)){
			return false;
		}

		// if last column, row should be a valid word
		if (c == size -1 && !(resultRow == 2 || resultRow == 3)){
			return false;
		}

		return true;

	}

	private void printMethod(StringBuilder[] rowStr, StringBuilder [] colStr)
	{
		for (int i =0; i < rowStr.length; i++){
			System.out.println(rowStr[i]);
		}
	}

	




}
