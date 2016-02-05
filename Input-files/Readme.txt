Input format:
You will be given an input file. 
Read the input file name from the command line.

The first line of the input will be the number of queries (n). 
Following n lines will be the queries, one per line. 
For each of them, you have to determine whether it can be proved form the knowledge base or not.
Next line of the input will contain the number of clauses in the knowledge base (m).
Following, there will be m lines each containing a statement in the knowledge base. Each clause is in one of these two formats:
1- p1 ∧ p2 ∧ ... ∧ pn => q
2- facts: which are atomic sentences. Such as p or ~p
All the p s and also q are either a literal such as HasPermission(Google,Contacts) or negative of a literal such as ~HasPermission(Google,Contacts).
