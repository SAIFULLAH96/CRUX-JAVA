package com.codingblocks.Lecture_21;

import java.util.*;

public class AdjMapGraphWeighted<T> {

    private HashMap<T, Vertex> vertexHashMap = new HashMap<>();

    public void addVertex(T value){
        vertexHashMap.put(value, new Vertex(value));
    }

    public void addEdge(T first, T second, int weight){
        Vertex f_vertex = vertexHashMap.get(first);
        Vertex s_vertex = vertexHashMap.get(second);

        if(f_vertex != null && s_vertex != null){
            f_vertex.addNeighbour(s_vertex,weight);
            s_vertex.addNeighbour(f_vertex,weight);
        }
    }

    public void dft(T start){
        Vertex v_start = vertexHashMap.get(start);
        if(v_start == null){
            return;
        }
        HashSet<Vertex> set = new HashSet<>();
        Stack<Vertex> stack = new Stack<>();

        set.add(v_start);
        stack.push(v_start);

        while (!stack.empty()){
            Vertex top = stack.pop();
            System.out.println(top.value);

            for (Vertex padoci: top.neighbours.values()){
                if(!set.contains(padoci)){
                    set.add(padoci);
                    stack.push(padoci);
                }
            }
        }
    }

    public void bft(T start){
        Vertex v_start = vertexHashMap.get(start);
        if(v_start == null){
            return;
        }
        HashSet<Vertex> set = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();

        set.add(v_start);
        queue.add(v_start);

        while (!queue.isEmpty()){
            Vertex top = queue.remove();
            System.out.println(top.value);

            for (Vertex padoci: top.neighbours.values()){
                if(!set.contains(padoci)){
                    set.add(padoci);
                    queue.add(padoci);
                }
            }
        }
    }

    public LinkedList<LinkedList<T>> connectedComponent(){
        LinkedList<LinkedList<T>> components = new LinkedList<>();
        HashSet<Vertex> set = new HashSet<>();
        Stack<Vertex> stack = new Stack<>();

        for (Vertex v_start: vertexHashMap.values()) {

            if(set.contains(v_start)){
                continue;
            }
            set.add(v_start);
            stack.push(v_start);

            LinkedList<T> comp = new LinkedList<>();
            while (!stack.empty()){
                Vertex top = stack.pop();
                comp.add(top.value);

                for (Vertex padoci: top.neighbours.values()){
                    if(!set.contains(padoci)){
                        set.add(padoci);
                        stack.push(padoci);
                    }
                }

            }
            components.add(comp);

        }

        return components;

    }


    public int bfs(T start, T end){
        Vertex v_start = vertexHashMap.get(start);
        Vertex v_end = vertexHashMap.get(end);

        if(v_start == null || v_end == null){
            return -1;
        }
        int level = 0;
        HashSet<Vertex> set = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();

        set.add(v_start);
        queue.add(v_start);
        queue.add(null);

        while (queue.size() > 1){
            Vertex top = queue.remove();

            if(top == null){
                queue.add(null);
                level++;
                continue;
            }
            if (top.value.equals(end)){
                return level;
            }

            for (Vertex padoci: top.neighbours.values()){
                if(!set.contains(padoci)){
                    set.add(padoci);
                    queue.add(padoci);
                }
            }
        }
        return -1;
    }

    private Map<Vertex, Vertex> generateParents(){
        HashMap<Vertex, Vertex> parents = new HashMap<>();
        for (Vertex vertex: vertexHashMap.values()) {
            parents.put(vertex,null);
        }
        return parents;
    }

    private Vertex find(Vertex vertex, Map<Vertex, Vertex> parents){
        while (parents.get(vertex) != null){
            vertex = parents.get(vertex);
        }

        return vertex;
    }

    private boolean union(Vertex first, Vertex second, Map<Vertex, Vertex> parents){
        Vertex p_first = find(first, parents);
        Vertex p_second = find(second, parents);

        if(p_first == p_second){
            return false;
        }
        parents.put(p_first, p_second);
        return true;
    }

    //criskal method
    public int mst(){
        ArrayList<Edge> edges = new ArrayList<>();

        for (Vertex vertex: vertexHashMap.values()) {
            for (Vertex padoci:vertex.neighbours.values()) {
                int weight = vertex.weights.get(padoci.value);
                Edge e = new Edge(vertex,padoci,weight);
            }
        }
        Collections.sort(edges);

        int mst_weight = 0;
        Map<Vertex, Vertex> parents = generateParents();

        for (Edge edge: edges) {
            if(union(edge.first,edge.second,parents)){
                mst_weight += edge.weight;
            }
        }
        return mst_weight;
    }

    public AdjMapGraphWeighted<T> mstGraph(){
        AdjMapGraphWeighted<T> graph = new AdjMapGraphWeighted<>();
        for (T value: vertexHashMap.keySet()) {
            graph.addVertex(value);
        }
        ArrayList<Edge> edges = new ArrayList<>();

        for (Vertex vertex : vertexHashMap.values()) {
            for (Vertex padoci : vertex.neighbours.values()) {
                int weight = vertex.weights.get(padoci.value);
                Edge e = new Edge(vertex,padoci,weight);
            }
        }

        Collections.sort(edges);

        Map<Vertex, Vertex> parents = generateParents();

        for (Edge edge: edges) {
            if(union(edge.first,edge.second,parents)){
                graph.addEdge(edge.first.value,edge.second.value,edge.weight);
            }
        }
        return graph;
    }

    //prims method of mst
    public int prims(){
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        Set<Vertex> visited = new HashSet<>();

        //first item into s_vertex
        Vertex s_vertex = vertexHashMap.values().iterator().next();
        visited.add(s_vertex);

        for (Vertex padosi: s_vertex.neighbours.values()) {
            Edge edge = new Edge(s_vertex, padosi,s_vertex.weights.get(padosi.value));
            queue.add(edge);
        }
        int mst = 0;
        while (!queue.isEmpty()){
            Edge front = queue.remove();
            if(visited.contains(front.first) && visited.contains(front.second)){
                continue;
            }

            mst += front.weight;
            Vertex unvisited = null;
            unvisited = visited.contains(front.first) ? front.second : front.first;

            for (Vertex padosi: unvisited.neighbours.values()) {
                visited.add(padosi);
                Edge edge = new Edge(unvisited, padosi, unvisited.weights.get(padosi.value));
                queue.add(edge);
            }
        }
        return mst;
    }

    private class Vertex{
        private T value;
        private HashMap<T, Vertex> neighbours;
        private HashMap<T, Integer> weights;


        public Vertex(T value) {
            this.value = value;
            neighbours = new HashMap<>();
        }

        public void addNeighbour(Vertex padosi, int weight){
            neighbours.put(padosi.value, padosi);
            weights.put(padosi.value,weight);
        }
    }

    private class Edge implements Comparable<Edge>{
        private Vertex first;
        private Vertex second;
        private int weight;

        public Edge(Vertex first, Vertex second, int weight) {
            this.first = first;
            this.second = second;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge o) {
            return this.weight - o.weight;
        }
    }
}
