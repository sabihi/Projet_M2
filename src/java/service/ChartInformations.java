/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.ArrayList;
import java.util.List;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.StringLogger;

/**
 *
 * @author Naoufal
 */
public class ChartInformations {

    private static GraphDatabaseService graphDb;
    private static final String DB_PATH = "/Users/Naoufal/Desktop/Neo4jDB_7/";
    private TestNeo4J1 neo4J1 = new TestNeo4J1();

    public List<Integer> getnbrServices() {
        GraphDatabaseFactory generator = new GraphDatabaseFactory();
        graphDb = generator.newEmbeddedDatabase(DB_PATH);
        registerShutdownHook(graphDb);
        List<Integer> list=new ArrayList<>();
        Transaction tx = graphDb.beginTx();
        try {
            ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
            ExecutionResult ws = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='ws' RETURN count(n)");//cette requete retourne l'ensemble de services web qui existent dans notre base de données neo4j
            ExecutionResult users = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='user' RETURN count(n)");//cette requete retourne l'ensemble de services web qui existent dans notre base de données neo4j
            ExecutionResult clusters = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='Cluster' RETURN count(n)");//cette requete retourne l'ensemble de services web qui existent dans notre base de données neo4j
            ExecutionResult categories = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='category' RETURN count(n)");//cette requete retourne l'ensemble de services web qui existent dans notre base de données neo4j
            if (ws.hasNext()) {
                list.add(Integer.parseInt(("" + ws.next()).split("-> ")[1].split("\\)")[0]));
            }else{
                list.add(0);
            }
            if (users.hasNext()) {
                list.add(Integer.parseInt(("" + users.next()).split("-> ")[1].split("\\)")[0]));
            }else{
                list.add(0);
            }
             if (categories.hasNext()) {
                list.add(Integer.parseInt(("" + categories.next()).split("-> ")[1].split("\\)")[0]));
            }else{
                list.add(0);
            }
            if (clusters.hasNext()) {
                list.add(Integer.parseInt(("" + clusters.next()).split("-> ")[1].split("\\)")[0]));
            }else{
                list.add(0);
            }
           
            tx.success();
        } finally {
            tx.finish();
            graphDb.shutdown();
        }
        return list;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        }
        );
    }
}
