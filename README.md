# weka.join
This is an implementation of `weka.core.Instances` that supports joining datasets together by key, update or union them. This make it possible to easily perform SQL-like operations on Weka internal dataset representation. 

### Quick start
Assume that you have two datasets (Instances) with different number of columns and rows. The final result should have all rows from the first dataset, matched rows from the second and columns (Attributes) from both. 

***table1***:

Country_ID | Continent_ID | Country
---|---|---
1|1|Poland
2|1|Germany
3|1|France
4|2|USA
5|3|Senegal

***table2***:

Continent_ID | Continent
---|---
1|Europe
2|North America
4|Asia

***result***:

Country_ID | Continent_ID | Country | Continent
---|---|---|---
1|1|Poland|Europe
2|1|Germany|Europe
3|1|France|Europe
4|2|USA|North America
5|3|Senegal|?

SQL query could look like that:

```SQL
CREATE  TABLE result AS
SELECT  table1.Country_ID, table1.Continent_ID, table1.Country, table2.Continent
FROM    table1 LEFT JOIN table2 ON table1.Continent_ID = table2.Continent_ID
```
Same in Java using ```weka.join.Instances```:
```java
table1.makeIndex(table1.attribute("Continent_ID");
table2.makeIndex(table2.attribute("Continent_ID");
Instances result = table1.leftJoin(table2);
```
Full example can be found in [```QuickStart.java```](/src/weka/join/examples/QuickStart.java)

# Author
Bogdan Taranta ([LinkedIn](http://linkedin.com/in/taranta))

# License
GNU General Public license (GPL 2.0) 
