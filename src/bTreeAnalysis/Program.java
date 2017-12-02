package bTreeAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Program {

	/**
	 * Use our BTree program to iterate through different BTree structures and sizes
	 * to compare # of comparisons and tree height for search. Output will be to a pipe
	 * delimited file for easy Excel analysis.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Random rand = new Random();
		
		List<String> linesComparison = new ArrayList<String>();
		List<String> linesHeight = new ArrayList<String>();
		
		//output paths
		Path file = Paths.get("C:/Users/dunca/Google Drive/BU_MET/MET_CS_566_Final_Project/output/btree_analysis__comparison.txt");
		Path file2 = Paths.get("C:/Users/dunca/Google Drive/BU_MET/MET_CS_566_Final_Project/output/btree_analysis__height.txt");
		
		
		//Iterate through different array sizes for our BTree comparisons
		for (int i = 10; i <= 10000000; i *= 10){
			
			StringBuilder lineComp = new StringBuilder(i + "|");
			StringBuilder lineHeight = new StringBuilder(i + "|");
			
			//Create and populate our test array with values between 1 and 10,000,000
			int testArray[] = new int[i];
			for (int j = 0; j < i; j++){
				testArray[j] = rand.nextInt(10000000) + 1;
			}
			
			//Test out average number of comparisons and height for differently structured BTrees
			int treeSize = 2;
			while (treeSize <= 100){
				//create and populate our BTree
				BTree<Integer> tree = new BTree<Integer>(treeSize);
				for (int a : testArray){
					tree.add(a);//this is the bulk of program run time
				}
			
				//Test number of comparisons needed to find random number in tree 1000 times
				double comparisonAverage = 0;
				for (int x = 0; x < 1000; x++){
					int tester = rand.nextInt(i);
					comparisonAverage += tree.getNodeComparisons(testArray[tester]);
				}
				comparisonAverage /= 1000;
				
				//check level of max value of tree, this is the height of our tree
				int max = testArray[0];
				int height;
				for (int val : testArray){
					if (val > max){
						max = val;
					}
				}
				height = tree.getNodeDepth(max);
				
				//add data gathered to our output
				lineComp.append(comparisonAverage + "|");
				lineHeight.append(height + "|");
				
				if (treeSize == 2){
					treeSize = 10;
				}else{
					treeSize += 10;
				}
			}
			linesComparison.add(lineComp.toString());
			linesHeight.add(lineHeight.toString());
		}
		
		//write output data to files
		Files.write(file, linesComparison, Charset.forName("UTF-8"));
		Files.write(file2, linesHeight, Charset.forName("UTF-8"));
		
	}

}
