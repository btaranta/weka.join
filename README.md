# weka.join
This is an implementation of `weka.core.Instances` that supports joining datasets together by key, update or union them. This make it possible to easily perform SQL-like operations on Weka internal data representation. 

### Quick start
Assume that you have two datasets (Instances) with different number of columns and rows. The final result should have all rows (Instance) from the first dataset, matched rows from the second and columns (Attributes) from both. Key column in both sets is called "Id". Corresponding SQL query:

```SQL
CREATE TABLE Result AS
SELECT  T1.*, T2.Field1, T2.Field2 ...
FROM    T1 LEFT JOIN T2 ON T1.ID = T2.ID
```
Same in Java using ```weka.join.Instances```
```java
import weka.join.Instances;
...
Instances table1 = new Instances(...);
Instances table2 = new Instances(...);
table1.makeIndex(table1.attribute("Id");
table2.makeIndex(table2.attribute("Id");
Instances result = table1.leftJoin(table2);
```

# Author
Bogdan Taranta ([LinkedIn](http://linkedin.com/in/taranta))

# License
GNU General Public license (GPL 2.0) 
