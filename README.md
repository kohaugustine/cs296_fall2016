# Final Project Submission for CS225-Honors/CS296 Fall 2016 Functional Data Structures

This project contains my Clojure implementation of the lab described in 
section 2.12 of the book **Coding the Matrix: Linear Algebra through Computer Science
Applications** by Philip N. Klein.

## Lab: Comparing Voting Records Using Dot-Product

In this lab, we will represent a US senator’s voting record as a vector over R, and will use dot-products to compare voting records. For this lab, we will just use a list to represent a vector.

All data required is found in the `US_Senate_voting_data_109.txt` file, which contains the voting record for each senator in the 109th Congress.

The data was obtained from the [webpage associated with the book](http://resources.codingthematrix.com/).

### Running the code

To run this code, ensure that [Lein](http://leiningen.org/) is installed in your
system. After cloning this repository, `cd` into the repository and launch the
_Lein REPL_ with `lein repl`, and execute

`(load-file "voting_records_analysis.clj")`

This loads in all the analysis functions contained in the file, into your current
REPL session's namespace, ready for your immediate use.


### Example usage

```
; By default, loading in the source file immediately executes a `slurp` command that
; reads in the entire file string into your current namespace as the `data` var.
; Generate a hashmap containing senator names as the keys, and their associated voting
; record vector as the value.
user=> (def votemap (create-voting-dict data))]
#'user/votemap

user=> votemap
{"Isakson" [1 1 1 1 1 1 1 1 1 -1 1 -1 1 1 1 1 1 1 1 1 1 1 1 .........

; Select Obama's record from the votemap
user=> (def obama (select-keys votemap ["Obama"]))
#'user/obama

user=> obama
{"Obama" [1 -1 1 1 1 -1 -1 -1 1 1 1 1 1 1 -1 1 1 1 1 1 1 1 1 1 -1 1 -1 -1 1 1 1 1 1 1 1 1 1 -1 1 1 1 1 -1 1 1 -1]}

; We find that Kerry had the most similar voting record to Obama 
user=> (most-similar obama votemap)
(["Kerry" 42.0])

; Compute the vector of average vote patterns for all Democrats
user=> (def avg_d (hash-map "avgd" (find-average-record 
				   (get-same-party "D" data) votemap)))
#'user/avg_d

user=> avg_d
{"avgd" [-0.16279069767441862 -0.23255813953488372 1.0 0.8372093023255814 0.9767441860465116 -0.13953488372093023 -0.9534883720930233 0.813953488372093 0.9767441860465116 0.9767441860465116 0.9069767441860465 0.7674418604651163 0.6744186046511628 0.9767441860465116 -0.5116279069767442 0.9302325581395349 0.9534883720930233 0.9767441860465116 -0.3953488372093023 0.9767441860465116 1.0 1.0 1.0 0.9534883720930233 -0.4883720930232558 1.0 -0.32558139534883723 -0.06976744186046512 0.9767441860465116 0.8604651162790697 0.9767441860465116 0.9767441860465116 1.0 1.0 0.9767441860465116 -0.3488372093023256 0.9767441860465116 -0.4883720930232558 0.23255813953488372 0.8837209302325582 0.4418604651162791 0.9069767441860465 -0.9069767441860465 1.0 0.9069767441860465 -0.3023255813953488]}

; We find that Biden's voting record is the most similar to the average voting record
; for all Democrats
user=>  (most-similar avg_d votemap) 
(["Biden" 34.86046500504017])
```

Have fun, and feel free to announce any new discoveries you make about your favourite
senators!


Descriptions about the lab contents above were taken from **Coding the Matrix: Linear Algebra through Computer Science
Applications**.
