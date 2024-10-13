
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
    // { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }
    {
        dist = SimulatorOne.INFINITY;
        prev = null;
        scratch = 0;
    }


    // public PairingHeap.Position<Path> pos; // Used for dijkstra2 (Chapter 23)
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
// --> Add additional edge
// void dijkstra( String s ) --> Single-source weighted
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm. Exceptions are thrown if errors are detected.

public class SimulatorOne {
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
            //System.out.println("In Print Path");
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
            //System.out.println("In Djisktra");
            throw new NoSuchElementException("Start vertex not found");
        }
        
        clearAll();
        pq.add(new Path(start, 0));    
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
            //System.out.println("In Get Distance");
            throw new NoSuchElementException("Destination vertex not found");
        }
        result[1] = dest.dist; // Distance from source to destination
         
        
        return result;
    }
   
    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest(String client, SimulatorOne g, List<String> shops) {
        try {
                   
            double minFromShop = Double.MAX_VALUE;
            double minToShop = Double.MAX_VALUE;
            String startShop = "";
            String endShop = "";
            List<String> taxis = new ArrayList<>();
            List<String> dropOffs = new ArrayList<>();
            
            for (String shop: shops) {
               double distFromShop = g.getDistance(shop, client)[1];
               double distToShop = g.getDistance(client, shop)[1];
               if (distFromShop < minFromShop && distFromShop != g.INFINITY) {
                  minFromShop = distFromShop;
                  startShop = shop;
                  
                  taxis.clear();
                  taxis.add(shop);
               }
               else if (distFromShop == minFromShop && distFromShop != g.INFINITY) {
                  taxis.add(shop);
               }
               
               if (distToShop < minToShop && distToShop != g.INFINITY) {
                  minToShop = distToShop;
                  endShop = shop;
                  dropOffs.clear();
                  dropOffs.add(shop);
               }
               else if (distToShop == minToShop && distToShop != g.INFINITY) {
                  dropOffs.add(shop);
               }

            }
            if (taxis.size() == 0 || dropOffs.size() == 0) {
               throw new NoSuchElementException("Destination vertex not found");
             }
            System.out.println("client " +client);
            
            for (String taxi: taxis) {
               System.out.println("taxi " +taxi);
               g.dijkstra(taxi);
               double costs[] = g.getDistance(taxi, client);
               if (costs[0] > 1) {
                  System.out.println("multiple solutions cost "+Double.valueOf(costs[1]).intValue());
               }
               else { 
                  g.printPath(client);
               }
            }

            
            for (String dropOff: dropOffs) {
               
               g.dijkstra(client);
               System.out.println("shop "+dropOff);
               double costs[] = g.getDistance(client, dropOff);
               if (costs[0] > 1) {
                  System.out.println("multiple solutions cost "+Double.valueOf(costs[1]).intValue());
               }
               else { 
                  g.printPath(dropOff);
               }
            }

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
        SimulatorOne g = new SimulatorOne();
        int countNodes = 0;
        int countClients = -1;
        int countShops = -1;
        List<String> clients = new ArrayList<>();
        List<String> shops = new ArrayList<>();
        
      
        Scanner keyboard = new Scanner(System.in);
       
        countNodes = Integer.parseInt(keyboard.nextLine());
      
        for (int i = 0; i <countNodes; i++) {
           String line = keyboard.nextLine();
           StringTokenizer st = new StringTokenizer(line);
         
           String source = st.nextToken();                
           while (st.hasMoreTokens()) {
              String dest = st.nextToken();
              int cost = Integer.parseInt(st.nextToken());
              g.addEdge(source, dest, cost);
           }
        }

        countShops = Integer.parseInt(keyboard.nextLine());
        shops = new ArrayList<>(countShops);
        StringTokenizer st = new StringTokenizer(keyboard.nextLine());
        while (st.hasMoreTokens()) {
           shops.add(st.nextToken());
        }   
        countClients = Integer.parseInt(keyboard.nextLine());
        clients = new ArrayList<>(countClients);               
        st = new StringTokenizer(keyboard.nextLine());            
        while (st.hasMoreTokens()) {
           clients.add(st.nextToken());
        }
        keyboard.close();         

        for (String client: clients) {
            processRequest(client, g,shops);
        }
    }
}
