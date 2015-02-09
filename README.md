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

# Reference

1. ```this.innerJoin(Instances source, Attribute key)```
  - only rows matched by indexed field on ```source``` and ```key``` attribute on ```this```
  - all attributes from ```this```, new attributes from ```source```
2. ```this.innerJoin(Instances source)```
  - same, but indexed field is used for ```this``` instead of ```key```
3. ```this.leftJoin(Instances source, Attribute key)```
  - all rows from ```this```
  - only matched rows from ```source``` using indexed field on ```source``` and ```key``` attribute on ```this```
  - all attributes from ```this```, new attributes from ```source```
4. ```this.leftJoin(Instances source)```
  - same, but indexed field is used for ```this``` instead of ```key```
5. ```this.fullJoin(Instances source, Attribute key)```
  - all rows from both datasets matched by indexed field on ```source``` and ```key``` attribute on ```this```
  - all attributes from ```this```, new attributes from ```source```
6. ```this.fullJoin(Instances source)```
  - same, but indexed field is used for ```this``` instead of ```key```
7. ```this.update(Instances source, Attribute key)```
  - all rows and attributes from ```this```
  - values matched by indexed field on ```source``` and ```key``` attribute on ```this``` are updated:
    - same attribute name in ```this``` and ```source```
    - same attribute type, only `NUMERIC`, `NOMINAL`, `STRING` and `DATE` types are supported
    - `NOMINAL` and `STRING` attributes are *rebuild* to match levels from both sets
8. ```this.update(Instances source)```
  - same, but indexed field is used for ```this``` instead of ```key```
9. ```this.union(Instances source)```
  - all rows from both datasets
  - all attributes from ```this```
  - all attributes from ```source```, but non-conflicting with ```this```
  - `NOMINAL` and `STRING` attributes are *rebuild* to match levels from both sets
10. ```this.makeIndex(Attribute attr)```
  - build index on attribute `attr`

Please check the examples for details of usage:
- [```JoinsCircus.java```](/src/weka/join/examples/JoinsCircus.java)
- [```Union.java```](/src/weka/join/examples/Union.java) 
- [```Update.java```](/src/weka/join/examples/Update.java) 
 
# Performance
Implementation is not seriously optimized and using default Java collections leads to high memory consumption. Indexes are implemented as `HashMap<Double,HashSet<Integer>>` matching values with rows. Index is required always on `Instances source`, using index on `this` is optional. In most cases full scan through `this` is better, only `innerJoin` between big and very small datasets justifies using two indexes. 

[```PerformanceCheck.java```](/src/weka/join/examples/PerformanceCheck.java) gives a quick way to experiment and compare different settings.

# Author
Bogdan Taranta ([LinkedIn](http://linkedin.com/in/taranta))

# License
GNU General Public license (GPL 2.0) 
