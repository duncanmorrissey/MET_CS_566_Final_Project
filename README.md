# MET_CS_566_Final_Project

Understanding B-Tree Search and its Application to Databases

Duncan Morrissey

Graduate Student

BU MET College of Computer Science

CS566 – Belyaev

Fall 2017

## Abstract
_Accessing data on disk efficiently is a primary requirement for databases and file systems, and presents a different set of problems than traversing a data structure in memory. B-Trees present a tidy solution to disk access minimization. With a sufficient number of keys per node, the height of a B-Tree will decrease significantly and lower the number of reads required. The intention of this research is to clearly examine and highlight the benefit of differently structured B-Trees with regard to the unique problems that databases and disk access present._

## Introduction
Although whether B-Tree is actually short for “Balanced Tree” is a source of debate, the data structure’s importance to computer systems over the last 50 years is universally accepted. The B-Tree was first envisioned by Rudolf Bayer and Ed McCreight in the 1960’s while working to optimize data retrieval [5].  Data structures like binary trees, stacks and lists are traditionally more applicable to accessing or organizing data that is already in main memory. Accessing data that is stored on disk however is a different problem. Disk access is orders of magnitude slower than accessing data already in main memory and thus, requires a different solution [3]. 

The B-Tree data structure was created to address this problem. A B-Tree is a generalization of binary trees or search trees. Each node of a B-Tree can have a variable number of keys and children, and the keys are stored in non-decreasing order [3]. Each node of the tree has a minimum number of keys allowed, and the children of that node fall into ranges between those keys. In order to access children, you can simply compare what key you are looking for to the keys of the current node, and then iterate down the tree structure [7].

This structure largely addresses the problem of slow disk access. Rather than optimize traversal of the data structure itself as we would in a search structure in memory, the primary goal is to minimize data retrieval time from disk, and that means retrieving the data in as few disk reads as [2]. A disk read occurs when we need to retrieve data from a child node, as that child nodes data will live on a different disk page. Therefore, in order to minimize disk reads, the ideal structure of a B-Tree is one with a very low height and large number of keys in each node, as there will be fewer child node iterations required [6]. However, even though disk read minimization is the primary focus, tree traversal of a B-Tree can still be completed in O(nlogn) time [4]. This height minimization is particularly applicable today to databases and file systems.

These benefits manifest themselves in today’s most popular database systems. A database is inherently a structure with a significant quantity of data and the disk read minimization issue is paramount [2]. When accessing different pages for data, the low height B-Tree structure appropriately minimizes I/O and facilitates transfer of significant quantities of data. Microsoft SQL Server, Oracle MySQL and PostgresSQL all implement variations of B-Trees with the number of keys in each node maximized.

## The B-Tree Data Structure
A B-Tree is a generalization of a search tree structure with several specific rules [1]. It is first initialized with a minimum integer value (t) and then is bound to the following:
The root node can have as few as one element within it, but all other nodes have at least the minimum (t)

1. The max number of keys in a node is twice the minimum
2. The elements of a node are sorted in ascending order
3. The number of children in a non-leaf node is always one greater than the number of keys
4. An element at index i is greater than all elements in subtree number i of the node, and less than all elements in subtree i + 1 
5. For example, if we initialize our B-Tree with a minimum value of 1 and the below “values” array, we could get a B-Tree with the following:

Values: 2, 4, 5, 6, 9, 10, 11, 14, 15, 24

<img width="409" alt="Screen Shot 2022-09-04 at 8 51 28 AM" src="https://user-images.githubusercontent.com/23343931/188314847-89e39129-b0a8-4df0-9a42-2f127b03b025.png">

A more applicable generalization of this diagram to databases would be 100+ key trees similar to the below. Rather than represent value in these nodes, the numbers represent the number of keys within them [2]:

<img width="434" alt="Screen Shot 2022-09-04 at 8 52 26 AM" src="https://user-images.githubusercontent.com/23343931/188314850-0a866f49-ffb3-4d99-8f63-01cf64ee1f47.png">

The larger number of keys per node will decrease the height, and the height of the tree will dictate how many disk reads are required when accessing data in our database. Given a minimum (t) ≥ 2, our height will be dictated by [2]:

<img width="98" alt="Screen Shot 2022-09-04 at 8 53 08 AM" src="https://user-images.githubusercontent.com/23343931/188314861-9c5f17c1-d553-4d2d-9dea-54c78d28d1fa.png">

## B-Tree Search
Searching for an element within a B-Tree follows the same basic idea as searching for an element within a binary tree, just augmented for the additional number of keys. The search process ends when we’ve found our node in question or reached a leaf node with no key equal to our value. We begin at the root node and check whether the key in question is within the root. If the key is not, we then perform two initial comparisons: whether the search value is less than the smallest key in our node, or greater than the largest key. This can result in several scenarios:

1. The search value is less than the smallest key. We then iterate down the smallest child node value and repeat.
2. The search value is greater than the largest key. We then iterate down the largest child node value and repeat.
3. The value is between our smallest and largest key within the node. We then search through all keys between our smallest and largest values for the node itself or child node needed to iterate down. Since the keys within a node are essentially a sorted list, we can use binary search within nodes to speed up our search time.
4. The root node is null (value is not found)
5. A leaf node is reached and our value isn’t present

Even with nodes with 1000+ keys, search on B-Trees will still run in O(logn) time. If we encounter scenario 1 or 2, our search is bound by the height of the tree, and scenario 3 will run in binary search time. Both of these scenarios are O(logn) [1]. 

## Program Design

A console application was designed to highlight the benefits of B-Trees for different data input sizes and different tree structures in light of the set of problems that disk access introduces. The purpose was twofold: First, to demonstrate as node size of a tree increases, the number of comparisons to find an element still performs in O(logn) time. Second, even with millions of elements, the height of a B-Tree will still be small, minimizing disk reads required. 

Arrays of size 10 – 10,000,000 were populated with random values between 0 and 10,000,000. For each of these arrays, B-Trees with node size of 2 – 100 were created. The number of comparisons required to find a random value in the B-Tree were averaged over 1,000 repetitions, and the height of each tree was measured. 

## Source Code

Below is the Main class for the java application, created using a Dell XPS Laptop with the Eclipse IDE. Excluded from this printout are modifications to the B-Tree class used, specifically the `getNodeComparisons` and `getNodeDepth` methods created. The full source code can be found at https://github.com/dmorrissey14/MET_CS_566_Final_Project/tree/master/src/bTreeAnalysis

```java
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
```

## Results
Below we have two tables and corresponding graphs demonstrating the output from our console application. The y-axis is the number of keys within a node, the x-axis is the size of our input array. 

<img width="521" alt="Screen Shot 2022-09-04 at 8 55 30 AM" src="https://user-images.githubusercontent.com/23343931/188314930-788002ce-2ee6-40da-a259-0fc71e39446f.png">

As you can see, even as the input array creates a B-Tree with 10,000,000 elements, our height is still only four if the number of keys per node is sufficiently high. This stands in comparison to a B-Tree with two keys per node, as that tree would require a height of 12. Considering disk access, this lower height saves us significant amounts of time.

<img width="505" alt="Screen Shot 2022-09-04 at 8 56 03 AM" src="https://user-images.githubusercontent.com/23343931/188314934-d779727f-13e9-437f-8dd8-de55f6ffb5d3.png">

In Fig. 1, the y-axis shows the number of comparisons, the x-axis is the number of keys per node in the tree, and individual lines are differently input sizes. 

<img width="590" alt="Screen Shot 2022-09-04 at 8 56 36 AM" src="https://user-images.githubusercontent.com/23343931/188314944-ff821a29-5e5e-4799-ab72-3966c9a47a6a.png">

This demonstrates a relatively small difference between the number of comparisons between differently structured B-Trees, even as the number of keys is increased. Our B-Tree with the largest number of keys is performing only about 300 more comparisons for our largest input.

Each tree individually is also still searching in logarithmic time, as shown by the isolated number of comparisons of our 100 node B-Tree (Fig. 2):

<img width="607" alt="Screen Shot 2022-09-04 at 8 57 01 AM" src="https://user-images.githubusercontent.com/23343931/188314952-f09a1017-f3ad-40c9-ad9d-3cfce33be4ee.png">

As our input size progresses from 10 to 10,000,000 , our number of comparisons match a logarithmic best-fit curve with an R^2 = 0.9753. Clearly, even our B-Tree with the highest number of keys is still performing search extremely quickly.

Ultimately, looking at the sets of data, the B-Trees with a large number of keys are performing as expected. Given an input size of 10,000,000, a B-Tree with 2 keys per node will only take about 12 comparisons to find a search value, while a B-Tree with 100 keys will take closer to 300-400. However, that B-Tree with 2 keys will take about 8 more disk reads to access all of that data. Considering a disk read takes 10-50ms, while in memory operations are measured in nanoseconds, the disk reads saved by having a lower height B-Tree more than offset the 300 nanoseconds lost to less efficient tree traversal. Finally, all of these trees are searching extremely efficiently in log(n) time.

## Conclusion

The core ideas validated here are still relevant today, even as research has gone beyond the basic B-Tree structure. A more standard implementation today for databases are B+ Trees, which only store data in their leaf nodes, which facilitates a transfer of larger amounts of data. Database Tables are structured as B-Trees themselves, and often have supplemental indexes on them structured as B-Trees, allowing for optimizing search of fields besides the primary field. 

Even as NoSQL and Hadoop backed data stores gain popularity, the backbone B-Tree structure of relational databases remains core to the technology industry.  Today cutting edge research is performed on scalable and distributed B-Trees by Microsoft and HP. Although the concept may be 40 years old now, its brilliant initial implementation remains relevant and essential to today’s programs. 

## References and Links
```
[1] Main, M. (2012). Data structures & other objects using Java. 4th ed. Boston, Mass: Pearson Addison-Wesley, pp.556-581.
[2] Cormen, T., Leiserson, C., Rivest, R. and Stein, C. (n.d.). Introduction to algorithms. 3rd ed. Cambridge, Massachusetts: The MIT Press, pp.484-499.
[3] Bluerwhite.org. (2017). B-Trees. [online] Available at: https://www.bluerwhite.org/btree/ [Accessed 10 Nov. 2017].
[4] GeeksforGeeks. (2017). B-Tree | Set 1 (Introduction) - GeeksforGeeks. [online] Available at: http://www.geeksforgeeks.org/b-tree-set-1-introduction-2/ [Accessed 17 Nov. 2017].
[5] Perforce.com. (2017). A Short History of the BTree | Perforce. [online] Available at: https://www.perforce.com/blog/short-history-btree [Accessed 10 Dec. 2017].
[6]  Web.csulb.edu. (2017). B+-Tree Indexes. [online] Available at: http://web.csulb.edu/~amonge/classes/common/db/B+TreeIndexes.html [Accessed 1 Dec. 2017].
[7] Opendatastructures.org. (2017). 14.2 B-Trees. [online] Available at: http://opendatastructures.org/versions/edition-0.1g/ods-python/14_2_B_Trees.html [Accessed 23 Nov. 2017].
```
