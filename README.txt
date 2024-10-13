Improvements

I modified my app so that it allows the taxi to take the client to any shop, the client specifies their desired stop when they requesrt a taxi.
The app is also modified so that taxis are not only stationed at shops, taxis can also be stationed at any of the QnQ pick up spots. 
In the sample input there is a line that shows the position of each taxi.



Input format:
<number of nodes><newline>
{<source node number> {<destination node number> <weight>}*<newline>}*
<number of shops><newline>
{<shop node number>}*<newline>
<number of taxis><newline>
{<taxi node number>}*<newline>
<number of clients><newline>
{<client node number><desired destination shop><newline>}*

Here's an example of the request 
5
0 4 15
1 0 14 2 7 3 23
2 0 7
3 1 23 4 16
4 2 15 3 9
2
0 3
2
4 3
3
1 0
4 0
2 3

NB: The taxi node number has to be an existing vertex. 
After the line "0 3" that represents the shops that are in the graph, we have the line that represents the number of taxis available,
the line following that one represents the positions of the respective taxis.
After the line that has "3" which represents the number of taxi requests, the following lines are the individual clients and their desired stop <client node> <desired stop> 