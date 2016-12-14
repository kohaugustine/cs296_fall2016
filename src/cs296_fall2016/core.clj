
;; Splitting up the data in line by line, and storing the whole collection
;; into a list. First I must "require" the use of the string module
(ns cs296_fall2016.core
    (:require [clojure.string :as str]))

;; Reading a txt data file, and storing the whole string of file
;; into a var named data
(def data (slurp "/data/US_Senate_voting_data_109.txt"))

;; split the data into distinct strings, each line in the original data file
;; that represented one senator is now a distinct string in the seq that is referred
;; to by dl
(def dl (str/split data #"\n")) 

; Now we want to construct a hashmap, make each Senator's name remaining as 
;; a string that serves as the key, and his sequence of votes to be a into a vector
;; that is the value associated with that key
;; suppose that we are just working with the very first line in the original data
;; file for simplicity, focussing on the senator "Akaka"
;; First split each distinct line into four parts, with them being the last name, 
;; party affiliation (D stands for democrat, R for republican), state that 
;; they represent (in short form, for example, MA is Massachusettes), and 
;; lastly the sequence of votes. Next, parse out the name and vote vector accordingly.
(def lname (first (str/split (first dl) #" " 4))) 
(def votes (last (str/split (first dl) #" " 4))  

;; function that converts a string of votes into a vector of ints
(defn create-vote-vec
  [string]
  (vec (map #(Integer/parseInt %)
        (str/split string #" "))))  

;; Create a var, the vote vector 
(def vote_vec (create-vote-vec votes))
 
;; Construct the hashmap
(def hm {lname vote_vec} )

(defn split-each-line
  [line]
  (str/split line #" " 4))  ;; another quote to end the confusion "

;; Check with a map, and indeed it works to parse each line in the data!
(def vectorized_data (map split-each-line dl))

;; Function that can be applied to vectorized_data using the map function to
;; extract out the last name of each senator
(defn get-first-each-elem
  [data]
  (first data))
;; test it out and I verified that it works the way I want
(map get-first-each-elem vectorized_data)

;; function that extracts out the voting record of each senator
(defn get-last-each-elem
  [data]
  (last data))
;; test it and verified it
(map get-last-each-elem vectorized_data)

;; Finally figured out the beautiful one line command needed to construct the 
;; hashmap that I need (and boy, having the Clojure cheatsheet PDF file has been 
;; extremely helpful! Stored it in a var called voting_map
(def voting_map 
  (zipmap 
    (map get-first-each-elem vectorized_data)
    (map create-vote-vec 
      (map get-last-each-elem vectorized_data)))
;; By this point, I've completed task 2.12.1!
;; Paused here for the night, on Wednesday, 7 December 2016, to sleep!
#######################################

########################################
;; Resumed work here on Thursday started in morning....
;; Next task 2.12.2 is to write the policy-compare function (by this point
;; I'm realzing that the clojure culture is to use - to connect the words in a 
;; function name with multiple words, instead of _ that is used in python
(defn policy-compare
  [n1 n2 vhmap]    ;; n1 and n2 each represent the last name of senators, and vhmap is the hashmap of lastnames to voting vectors
  (reduce +
    (mapv (fn [v1 v2] (* v1 v2))
      (vhmap n1) (vhmap n2))))
      
;; Decided I want to break down this function to having a dot product function
;; as a separate function that it calls, since the dot product is so useful
(defn dot-pdt
  [v1 v2]
  (reduce + (mapv (fn [a b] (* a b)) v1 v2)))
  
;; So policy-compare can be redefined as follows
(defn policy-compare
  [n1 n2 vhmap]    
  (dot-pdt (vhmap n1) (vhmap n2))))

;; The dot-pdt function is a useful primitive for all other functions in this lab

;; Now working on function most-similar.... I really wanted to avoid loops
;; but I really couldn't think of any other way, so I'm failling back on that 
;; for now.... trying to solve the problem by parts....

;; A loop that iteratively computes the dot product between the voting vector
;; of a given senator with the vector of every other senator in the entire 
;; voting record hasmap, except for the same senator himself (the dot product
;; of a senator's voting record on his own will give the highest agreement, which
;; we want to exclude from this result)
(for [i (keys voting_map) :when (not= i "Harkin")] 
      {i (dot-pdt (voting_map i) 
          (voting_map "Harkin"))})
          
;; This list comprehension generated a list of hash maps, so next I found out
;; how to merge all these hashmaps to form one giant hashmap
(reduce 
  merge (for [i (keys voting_map) :when (not= i "Harkin")] 
    {i (dot-pdt (voting_map i) 
       (voting_map "Harkin"))}))
       
;; Then I inverted this hashmap, so that the dot-product became the key, then
;; sorted based on this key, and picked out the value of the very first key, which
;; is the senator that has the vector with the largest dot-product value with the
;; senator that we passed in as the argument

;; Before using the map-invert function, I had to require the use of the set
;; into my namespace, by running 
(require '[clojure.set :as set])

;; Then running the function worked!
(set/map-invert 
  (reduce merge (for [i (keys voting_map) :when (not= i "Harkin")] 
    {i (dot-pdt (voting_map i) 
      (voting_map "Harkin"))})))

;; Suppose I store I wrap it into a function, and apply a sort to it
;; Remember, similarity is just the 
;; this function takes in the name of a particular senator, sen, and then 
;; computes, for every other senator in the main hashmap, the dot product between
;; the sen vote vector and the senator's vote vector, and it returns a compiled 
;; hashmap where the dot pdt, ie the similarity score, is assigned to each 
;; corresponding senator
(defn similarity-map
  [sen vhmap]
  (into (sorted-map)
  (set/map-invert 
  (reduce merge (for [i (keys vhmap) :when (not= i sen)] 
    {i (dot-pdt (vhmap i) 
      (vhmap sen))})))))

;; Now the two functions for tasks 2.12.3 and 2.12.4 are just a simply calling
;; this similarity-map function and then indexing the end and start of the maps
;; to extract out the senator's name

;; sorted-map sorted the hashmap in ascending order, so the smallest dot product
;; becomes the first element while the largest dot product is the last element, so
;; most-similar needs to index the last element while least-similar indexes the first
(defn most-similar
  [sen vhmap]
  (val (last (similarity-map sen vhmap))))

(defn least-similar
  [sen vhmap]
  (val (first (similarity-map sen vhmap))))

;; Tasks 2.12.5-6 are just about calling these functions, so I'm skipping them for
;; now....

;; Moving to task 2.12.7, which also requires I construct a map of democrat only
;; votes 

;; First, need to write a function that acts on each senate to get its second 
;; element, the party affliation coupled with the vote records
;; TODO: I think before I push my code for submission, consider rewriting
;; the above parsing functions like get-first-each-elem and get-last-each-elem
;; into some more specific name, such as get-last-name and get-vote-record
(defn get-party-affl-vote
  [data]
  [(second data) (last data)])

;; WARNING: this function below is not working!!!!! Do not use it!!!!!
(def voting_map_democrats
  (zipmap 
    (filter (fn [party] 
      (= "D" party)) 
      (map first (map get-party-affl vectorized_data)))
    (filter (fn [party] 
      (= "D" party)) 
      (map second (map get-party-affl vectorized_data))) )))

;; Was trying very hard to work with the resulting vector for a while, to construct
;; a hashmap, but just found that no matter how hard I tried I just couldn't 
;; get things to work out.... see the remains of the above function. I was trying
;; to do filtering on each level.... when I filtered for democrats I would lose 
;; the links to the vote vector.... then I decided to look deeper into how to do
;; a smart thing with the filtering function, and found that this line of 
;; code worked beautifully:
(filter (fn [sen] (= "D" (second sen))) vectorized_data)
;; What it does is to apply a lambda function that evaluates whether or not 
;; the senator's party is "D" or not. 
;; Generate a hashmap from this guy that contains only democrat senators...
;; By now since this action could be quite frequent, I've wrapped into its own
;; function...
(defn generate-name-vote-rec-map 
  [vectorized_data]
  (zipmap 
    (map get-first-each-elem vectorized_data)
    (map create-vote-vec 
      (map get-last-each-elem vectorized_data))))
;; Store the democrat only vote vector into a var, and pass it into this function
(def democrat_only
  (filter (fn [sen] (= "D" (second sen))) vectorized_data))
;; Now I get the hashmap with only democrats, assign it to the d_sen variable
(def d_sen (generate-name-vote-rec-map democrat_only))
;; TODO: Next steps are to construct the find-average-similarity function of task
;; 2.12.7, and pass in the d_sen hashmap in for processing...
;; PAUSED here on night of Thursday, 8 Dec 2016
#################################################33






