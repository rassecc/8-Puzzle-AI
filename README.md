# 8-Puzzle-AI

This is a program that solves the 8 Puzzle problem using the Manhattan heurtistic and the Misplaced Tile Heuristic. The program allows you to enter a puzzle manually, use one of the files provided that have 200 test cases at different levels, or a puzzle is randomly created to solve. 

xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Enter a choice
1) Randomly create puzzles
2) Enter a puzzle manually
3) Read in test case files
4) Exit the program

Your choice: 
xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

Enter a 1, 2, 3, or 4 to do any of the commands (nothing other than the number. ex: 3)

1) Randomly create puzzles
	- This prompts you how many random puzzles you want to create and solve.
	- Solves all puzzles and displays it in a chart in the console.
	- Information that is diplayed is depth, search costs (both h1 and h2), and run times (both h1 and h2)
	- At the end, an average of depth, search costs, and runtime is printed.

2) Enter a puzzle manually
	- Asks you to input a puzzle that is [0 - 8] and has as space between each number
	- An example input is shown to you each time (ex: 1 2 3 6 8 4 7 5 0)
	- Will solve the puzzle using h1 and will print each step leading upto the goal state
	- At the end, the depth of the solution is printed along with runtime for both h1 and h2

3) Read in test case files
	- Tells the user to enter an even number from 2-20 for the depth of the puzzles to be tested and the program reads the corresponding file.
	- Program will go through all 200 tests at the specified depth and print out a piece of the final table from my report displaying
	  a table with the depth you selected, the search cost for h1 and h2, and the runtime for h1 and h2

4) Exit the program
	- Terminates the program
