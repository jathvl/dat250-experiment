# DAT250 Experiment 5

In this experiment, I've played with the MongoDB database.
I opted to use the built-in IntelliJ client instead of `mongosh` for better text editing support,
and more comprehensive screenshots.

## Screenshots

### 0. Validate package hash
I installed mongodb through homebrew. The homebrew package manager automatically checks hashes during installations.

Still, I validated it manually. Obtaining the expected hash of the tarball was difficult,
the best source I found was the one defined in the homebrew formula:
https://github.com/mongodb/homebrew-brew/blob/master/Formula/mongodb-community.rb#L12

I could not find the expected hash on any official mongodb website.

I manually checked the hash correctness like this (diff exiting with code 0 means they are equal):

![img_1.png](ex5-images/img_1.png)

### 1.1. Insert documents

![img.png](ex5-images/crud/img.png)

### 1.2. Query documents

![img_2.png](ex5-images/crud/img_2.png)

![img_3.png](ex5-images/crud/img_3.png)

### 1.3. Update documents

![img_4.png](ex5-images/crud/img_4.png)

![img_6.png](ex5-images/crud/img_6.png)

![img_5.png](ex5-images/crud/img_5.png)

### 1.4. Delete documents

![img_7.png](ex5-images/crud/img_7.png)

### 1.5. Bulk operations

![img_8.png](ex5-images/crud/img_8.png)

I couldn't get the new bulk-write across collections method to work in the IntelliJ client,
so here that is in `monogsh`:

![img_9.png](ex5-images/crud/img_9.png)

### 2.1. Aggregation, filter and subset

![img.png](ex5-images/aggregations/img.png)

...

![img_1.png](ex5-images/aggregations/img_1.png)

### 2.2. Aggregation, group and total

![img_3.png](ex5-images/aggregations/img_3.png)

...

![img_2.png](ex5-images/aggregations/img_2.png)

### 2.3. Aggregation, unwind Arrays and Group Data

![img_4.png](ex5-images/aggregations/img_4.png)

...

![img_5.png](ex5-images/aggregations/img_5.png)

### 2.4. Aggregation, One-to-One join

![img_6.png](ex5-images/aggregations/img_6.png)

...

![img_7.png](ex5-images/aggregations/img_7.png)

...

![img_8.png](ex5-images/aggregations/img_8.png)

### 2.5. Aggregation, Multi-Field join

![img_9.png](ex5-images/aggregations/img_9.png)

...

![img_10.png](ex5-images/aggregations/img_10.png)

...

![img_11.png](ex5-images/aggregations/img_11.png)

![img_13.png](ex5-images/aggregations/img_13.png)

![img_14.png](ex5-images/aggregations/img_14.png)

## Reflections on mongodb aggregation pipelines vs SQL

I think the mongodb pipelines way of doing aggregations might be more user-friendly than SQL.
The fact that it is imperative, breaking down the desired operations step by step, might be easier to 
understand if you're not intimately familiar with SQL.

The programming model of interacting with the database through functions and objects in your programming language
also allows for cleaner code and composition, compared to using a DSL in strings.
MongoDB might lend itself better to complex aggregation pipelines (for example with array unwinding) 
than SQL, where the alternative would likely include writing multiple CTEs.

Still, unless you're doing operations that lend themselves very well to a distributed map/reduce, allowing for the
aggregation to be run on a database cluster, I think the SQL way is more efficient, because you're declaring your goal,
not the means to get there.
There's been lots of work put into the development and optimization of SQL query planners and executors.
And the fact that most SQL databases use a schema likely allows for more efficient disk reads for the database engine.
Along with the well-established benefits of data integrity guarantees.

## Pending issues

I think the assignment text might be out-of-date compared to the actual documentation live on MongoDB.
For example in the lack of available hashes for the downloaded tarball,
and no references to a ZIP-code dataset where we can run map/reduce operations.

Still, I think I've implemented the assignment without problems according to the current mongodb docs.
