(require '[clojure.string :as str]
         '[clojure.set :as set]   )


;; Lab Part 2.12.2: Reading in the file BEGIN

; Reading a txt data file containing the voting records, and storing 
; the whole string of data in the file into a var named data.
(def data (slurp "./US_Senate_voting_data_109.txt"))

; Format of the data files is that each line comprises information for 
; each senator, in the following order, last name, party affiliation (
; D stands for democrat, R for republican), state that they represent (in 
; short form, for example, MA is Massachusettes), and lastly the sequence 
; of votes.

; Split the data into distinct strings, each line in the original data 
; file that represented one senator is now a distinct string in the 
; seq that is referred to by dl.
(def dl (str/split data #"\n"))
  
; Helper function for split-senators that splits each line into 4 
; separate strings, each string representing each field.
(defn split-each-line
  [line]
  (str/split line #" " 4))  ;; another quote to end the confusion "


; Helper function for create-voting-dict
; Split the large string of data into a seq of vectors. Each vector 
; contains all the 4 information fields of a senator, split into 4 
; distinct strings.
(defn split-info
    [data]
    (map split-each-line (str/split data #"\n")))

; Helper function for create-voting-dict
; Converts the string of voting numbers into a vector of ints
(defn create-vote-vector
  [info] 
  (vec (map #(Integer/parseInt %)
        (str/split (last info) #" "))))

; Task 2.12.1: create-voting-dict function 
(defn create-voting-dict
    [dataset]
    (zipmap
        (map first (split-info dataset))
        (map create-vote-vector (split-info data))))

;; Lab Part 2.12.2: Reading in the file COMPLETED

;; Lab Part 2.12.4: Policy comparison BEGIN

; Dot Product defined as its own function, serving as a useful primitive
; for all other functions in the lab.
(defn dot-pdt
  [v1 v2]
  (reduce + (mapv (fn [a b] (* (float a) (float b))) v1 v2)))

; Task 2.12.2: policy-compare function
; votemap is the hashmap that is generated from create-voting-dict function
; n1 and n2 are the names of the two senators that you want to compare
(defn policy-compare
  [n1 n2 votemap]    
  (dot-pdt (votemap n1) (votemap n2)))


;; Helper functions for tasks 2.12.3 and 2.12.4 

; Creates a hashmap containing each senator's name as the key with the 
; value being the dot product between each senator's voting records and
; that of the particular sen being passed in.
; Interface to function only accepts the sen argument as being a hashmap 
; with a single key value pair, with the key being the name of the
; senator, the value his vote vector. This is generalized to accept sen even if 
; it does not exist in the votemap argument being passed in.
(defn compute-voting-similarity
    [sen votemap]
       (reduce 
            ; iteratively compute dot product between sen and each senator in votemap
            ; and always excluding the sen himself so that we avoid dotting the sen
            ; votes to its ownself
            merge (for [i (keys votemap) :when (not= i (key (first sen)))] 
                {i (dot-pdt (votemap i) (val (first sen)))})))
 
       
; Sorts a voting similarity hashmap with the votes similarity arranged
; in descending order. The senator with the most similar vote pattern is 
; the first key, while the senator with the least similar vote is the last
; key.
(defn sort-similarity-map
    [simmap]
    (let [result simmap]   ; result is like a local variable
        (into (sorted-map-by (fn [key1 key2]
            (compare [(get result key2) key2]
                     [(get result key1) key1])))
                        result)))

;; In the following task functions, I make use of filter to handle cases 
;; where there is more than one most or least similar voting records for
;; the given senator that is passed into the function.

; Task 2.12.3: most-similar function
(defn most-similar
  [sen votemap]
  (let [simmap (sort-similarity-map (compute-voting-similarity 
                                            sen votemap))]
    (filter (fn [sen] (= (val sen) (val (first simmap))))
        simmap)))

; Task 2.12.4: least-similar function
(defn least-similar
  [sen votemap]
  (let [simmap (sort-similarity-map (compute-voting-similarity 
                                            sen votemap))]
    (filter (fn [sen] (= (val sen) (val (last simmap))))
        simmap)))

;; Lab Part 2.12.4: Policy comparison COMPLETED

;; Lab Part 2.12.5: Not your average Democrat BEGIN

; Helper function, following the lab suggestion to construct a sen_set 
; that has only all Democrat senators. This function is parameterized to 
; do the job for whatever desired party, passed in as a string argument.
; Accepts a string indicating the senator's party affliation, and the
; original unparsed data read in from the slurp.
(defn get-same-party
    [p data]
    (map first
        (filter (fn [vec] (= p (second vec))) (split-info data))))

; Helper function that computes the mean of a given sequence of numbers
(defn mean
    [scores]
        (/ (reduce + scores) (float (count scores))))

; Task 2.12.7: find-average-similarity function
(defn find-average-similarity
    [sen senset votemap]
    ; filtering step to remove the senator passed in as sen in case he is
    ; also present in sen_set, and use a local var sens for holding this filtered seq
    (let [sens (filter (fn [name] (not= sen name)) senset)]
     (mean (vals 
            ; Compute the similarity score of sen with each senator in votemap, and then
            ; select using select-keys, only those senators that belong in sens
            ; TODO: Redundant computation; figure a more efficient way
            (select-keys 
                    (compute-voting-similarity sen votemap) sens)))))

; Task 2.12.8: find-average-record function
(defn find-average-record
    [senset votemap]
    (let [voteset (select-keys votemap senset)]
       ; divide the result of the vector sum by the number of elements to get average
       (mapv (fn [sum] (/ sum (float (count voteset))))
            ; compute sum of all the vote vectors
            (reduce (fn [v1 v2] (mapv + v1 v2)) (vals voteset)))))

;; Lab Part 2.12.5: Not your average Democrat COMPLETED


