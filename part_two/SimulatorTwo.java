
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.*;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public GraphException(String name) {
        super(name);
    }
}

// Represents an edge in the graph.
class Edge {
    public Vertex dest; // Second vertex in Edge
    public double cost; // Edge cost

    public Edge(Vertex d, double c) {
        dest = d;
        cost = c;
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.
class Path implements Comparable<Path> {
    public Vertex dest; // w
    public double cost; // d(w)
    public List<Vertex> pathNodes; // List of vertices in the path

    public Path(Vertex d, double c) {
        dest = d;
        cost = c;
    }

    public int compareTo(Path rhs) {
        double otherCost = rhs.cost;

        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }
}

// Represents a vertex in the graph.
class Vertex {
    public String name; // Vertex name
    public List<Edge> adj; // Adjacent vertices
    public double dist; // Cost
    public Vertex prev; // Previous vertex on shortest path
    public int scratch;// Extra variable used in algorithm
    public int paths;

    public Vertex(String nm) {
        name = nm;
        adj = new LinkedList<Edge>();
        reset();
        paths = 0;
    }

    public void reset()
    {
        dist = SimulatorTwo.INFINITY;
        prev = null;
        scratch = 0;
    }


}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
// --> Add additional edge
// void printPath( String w ) --> Print path after alg is run
// void dijkstra( String s ) --> Single-source weighted
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm. Exceptions are thrown if errors are detected.

public class SimulatorTwo {
    public static final double INFINITY = Double.MAX_VALUE;
    private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();

    /**
     * Add a new edge to the graph.
     */
    public void addEdge(String sourceName, String destName, double cost) {
        Vertex v = getVertex(sourceName);
        Vertex w = getVertex(destName);
        v.adj.add(new Edge(w, cost));
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */
    public void printPath(String destName) {
    
        Vertex w = vertexMap.get(destName);
        if (w == null) {
            throw new NoSuchElementException("Destination vertex not found");
            }
        else if (w.dist == INFINITY)
            System.out.println(destName + " is unreachable");
        else {
            System.out.print("");
            printPath(w);
            System.out.println();
        }
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private Vertex getVertex(String vertexName) {
        Vertex v = vertexMap.get(vertexName);
        if (v == null) {
            v = new Vertex(vertexName);
            vertexMap.put(vertexName, v);
        }
        return v;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
     */
    private void printPath(Vertex dest) {
        if (dest.prev != null) {
            printPath(dest.prev);
            System.out.print(" ");
        }
        System.out.print(dest.name);
    }

    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll() {
        for (Vertex v : vertexMap.values())
            v.reset();
    }
    
    /**
     * Single-source weighted shortest-path algorithm. (Dijkstra)
     * using priority queues based on the binary heap
     */
    public void dijkstra(String startName) {
        PriorityQueue<Path> pq = new PriorityQueue<Path>();
       

        Vertex start = vertexMap.get(startName);
        if (start == null) {
            throw new NoSuchElementException("Start vertex not found");
        }
        
        clearAll();
        pq.add(new Path(start, 0));   /// changedd 
        start.dist = 0;
        start.paths = 1;

        int nodesSeen = 0;
        while (!pq.isEmpty() && nodesSeen < vertexMap.size()) {
            Path vrec = pq.remove();
            Vertex v = vrec.dest;
            if (v.scratch != 0) // already processed v
                continue;

            v.scratch = 1;
            nodesSeen++;

            for (Edge e : v.adj) {
                Vertex w = e.dest;
                double cvw = e.cost;

                if (cvw < 0)
                    throw new GraphException("Graph has negative edges");

                if (w.dist > v.dist + cvw) {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    pq.add(new Path(w, w.dist));
                    w.paths = v.paths;               
                }
                else if (w.dist == v.dist + cvw) {
                     w.paths += v.paths;
                }
            }
        }
        
    }
    
    
    /**
     * Calculate the distance between two nodes using Dijkstra's algorithm.
     * 
     * @param sourceName The name of the source node.
     * @param destName   The name of the destination node.
     * @return The distance between the source and destination nodes.
     */
    public double[] getDistance(String startName, String destName) {
        dijkstra(startName); 
        double result[] = new double[2];
        result[0] = vertexMap.get(destName).paths;
        Vertex dest = vertexMap.get(destName); // Get the destination vertex
        if (dest == null) {
            throw new NoSuchElementException("Destination vertex not found");
        }
        result[1] = dest.dist; // Distance from source to destination
         
        
        return result;
    }
    

    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest(String client, String endShop, SimulatorTwo g, List<String> taxis) {
        try {
                   
            double minFromShop = Double.MAX_VALUE;             // Initialize the minimum distance from the shop
            double minToShop = Double.MAX_VALUE;              // Initialize the minimum distance to the shop
            String startShop = "";                           // Initialize the starting shop
            List<String> usableTaxis = new ArrayList<>();   // List of usable taxis
            
            
            // Find the nearest taxi to the client
            for (String taxi: taxis) {
               double distFromShop = g.getDistance(taxi, client)[1];      // Distance from the taxi to the client
               double distToShop = g.getDistance(client, endShop)[1];    // Distance from the client to the shop
               
               // Update the nearest taxi to the client
               if (distFromShop < minFromShop && distFromShop != g.INFINITY) {
                  minFromShop = distFromShop;
                  startShop = taxi;
                  
                  usableTaxis.clear();               // Clear the list of usable taxis
                  usableTaxis.add(taxi);            // Add the current taxi as the only usable taxi
               }
               else if (distFromShop == minFromShop && distFromShop != g.INFINITY) {
                  usableTaxis.add(taxi);     // Add another usable taxi with the same distance to the client
               }
            }
            
            // If no usable taxis are found, throw an exception
            if (usableTaxis.size() == 0) {
               throw new NoSuchElementException("Destination vertex not found");
             }
            System.out.println("client " +client);
            
            // Print the shortest paths for each usable taxi to the client
            for (String taxi: usableTaxis) {
               System.out.println("taxi " +taxi);
               g.dijkstra(taxi);
               double costs[] = g.getDistance(taxi, client);    // Get the costs for the path from the taxi to the client
               if (costs[0] > 1) {
                  System.out.println("multiple solutions cost "+Double.valueOf(costs[1]).intValue());
               }
               else { 
                  g.printPath(client);
               }
            }
            
               g.dijkstra(client);
               System.out.println("shop "+endShop);
               double costs[] = g.getDistance(client, endShop);
               if (costs[0] > 1) {
                  System.out.println("multiple solutions cost "+Double.valueOf(costs[1]).intValue());
               }
               else { 
                  g.printPath(endShop);
               }
        
        // Handle the case where the destination vertex is not found 
        } catch (NoSuchElementException e) {
            System.out.println("client " +client);
            System.err.println("cannot be helped");
            return false;
        } catch (GraphException e) {
            System.err.println(e);
        }
        return true;
    }

    /**
     * A main routine that:
     * 1. Reads a file containing edges (supplied as a command-line parameter);
     * 2. Forms the graph;
     * 3. Repeatedly prompts for two vertices and
     * runs the shortest path algorithm.
     * The data file is a sequence of lines of the format
     * source destination cost
     */
    public static void main(String[] args) {
        SimulatorTwo g = new SimulatorTwo();
        int countNodes = 0;          // Initialize the count of nodes
        int countClients = -1;      // Initialize the count of clients
        int countShops = -1;       // Initialize the count of shops
        int countTaxis = -1;      // Initialize the count of taxis
        Map<String, String> requests = new HashMap<String, String>();     // Map to store client requests
        List<String> shops = new ArrayList<>();                          // List to store shops
        List<String> taxis = new ArrayList<>();                         // List to store taxis
        
      
        Scanner keyboard = new Scanner(System.in);
        try {
              countNodes = Integer.parseInt(keyboard.nextLine());     // Read the count of nodes from user input
              
              // Read the edges and add them to the graph
              for (int i = 0; i <countNodes; i++) {
                 String line = keyboard.nextLine();
                 StringTokenizer st = new StringTokenizer(line);
               
                 String source = st.nextToken();     // Get the source node             
                 while (st.hasMoreTokens()) {
                    String dest = st.nextToken();     // Get the destination node
                    int cost = Integer.parseInt(st.nextToken());   // Get the cost
                    g.addEdge(source, dest, cost);       // Add the edge to the graph
                 }
              }

              countShops = Integer.parseInt(keyboard.nextLine());        // Read the count of shops from user input
              shops = new ArrayList<>(countShops);
              StringTokenizer st = new StringTokenizer(keyboard.nextLine());
              while (st.hasMoreTokens()) {
                 shops.add(st.nextToken());
              }  
              countTaxis = Integer.parseInt(keyboard.nextLine());       // Read the count of taxis from user input
              taxis = new ArrayList<>(countTaxis);
              st = new StringTokenizer(keyboard.nextLine());
              while (st.hasMoreTokens()) {
                  taxis.add(st.nextToken());
              }
              
              countClients = Integer.parseInt(keyboard.nextLine());      // Read the count of clients from user input
              requests = new HashMap<>(countClients);                   // Initialize the map of client requests
              for (int i =0; i < countClients;i++) {     
                  st = new StringTokenizer(keyboard.nextLine());                   
                  requests.put(st.nextToken(), st.nextToken());      // Add each client request to the map
              }
              keyboard.close();
              } catch (NumberFormatException e) {
                 System.out.println("Incorrect input was entered, please try again");
              
              }        
        
         // Process each client request
        for (String client: requests.keySet()) {
            processRequest(client,requests.get(client), g, taxis);
        }
    }
}
