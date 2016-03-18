/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
import bean.WebService;
import dao.ConnectMSSQLServer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.impl.util.StringLogger;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Naoufal
 */
public class TestNeo4J {

    private static GraphDatabaseService graphDb;
    private static final String DB_PATH = "/Users/Naoufal/Desktop/Neo4jDB_3/";
    private Jaccard jaccard = new Jaccard();
    private final static double seuil = 0.4;
    private final static double seuil_Cluster = 0.35;

    public static void main(String[] args) throws SQLException {
        GraphDatabaseFactory generator = new GraphDatabaseFactory();
        graphDb = generator.newEmbeddedDatabase(DB_PATH);
        TestNeo4J testNeo4J = new TestNeo4J();
        registerShutdownHook(graphDb);

        Transaction tx = graphDb.beginTx();
        try {
            testNeo4J.createServicesGraph();//Création du graphe de services
            testNeo4J.createCategorie();//Création des catégories à partir de la table 'ws' 
            testNeo4J.setWebServiceRelations();//Création des relations service-service/ service-catégorie
            testNeo4J.createUsersGraph();//Création du graphe d'utilisateurs
            //testNeo4J.setUserRelations();
            tx.success();
        } finally {
            tx.finish();
            graphDb.shutdown();
        }

//        ExecutionEngine engine=new ExecutionEngine(graphDb, StringLogger.SYSTEM);
//        Map<String,Object> parms=new HashMap<String, Object>();
//        parms.put("id", 2);
//        ExecutionResult result=engine.execute("start n=node({id}) return n",parms);
//        scala.collection.Iterator<Node> iterator= result.columnAs("n");
//        
//        while(iterator.hasNext()){
//            Object un_noeud=iterator.next();
//            System.out.println("---->"+un_noeud.toString());
//        }

        /*ExecutionEngine engine=new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result=engine.execute("start n=node(*) return n");
        scala.collection.Iterator<Node> iterator= result.columnAs("n");
        
        while(iterator.hasNext()){
            Node un_noeud=iterator.next();
            for (String propertyKey : un_noeud.getPropertyKeys()) {
                System.out.println("\t"+propertyKey+" : "+un_noeud.getProperty(propertyKey));
            }
            System.out.println();
        }*/
    }

    public void createServicesGraph() throws SQLException {  //WHERE id>=6000 and id<=6600 //WHERE id>=6583 and id<=6599
        ResultSet resultSet = ConnectMSSQLServer.select("SELECT * FROM [dbo].[ws] WHERE categoryid='Recommendations' or categoryid='Social'");
        while (resultSet.next()) {
            Node firstNode = graphDb.createNode();
            WebService webService = new WebService(firstNode);
            webService.create(resultSet.getString("wsname"), resultSet.getString("summary"), resultSet.getString("categoryid"), resultSet.getString("tags"), resultSet.getString("protocols"), resultSet.getString("dataformats"), resultSet.getString("APIhome"), resultSet.getString("Description"), resultSet.getString("provider"), resultSet.getString("signupRequire"), resultSet.getString("DeveloperKeyRequire"), resultSet.getString("id"), resultSet.getString("updated"), resultSet.getString("wsdl"), resultSet.getString("rating"));
            webService.showAll();
        }

        Iterable<Node> tous = GlobalGraphOperations.at(graphDb).getAllNodes();
        for (Node noeud : tous) {
            if (noeud.hasProperty("type")) {
                Object le_type_du_noeud = noeud.getProperty("type");
                String nom_du_type = le_type_du_noeud.toString();

                if (nom_du_type.equals("ws") == true) {
                    IndexManager gestionnaire_index = graphDb.index();
                    Index<Node> mon_index = gestionnaire_index.forNodes(nom_du_type);
                    mon_index.add(noeud, "Index_" + nom_du_type, le_type_du_noeud);
                }
            }
        }
    }

    public void createUsersGraph() throws SQLException {//WHERE id>=2250 and id<=2260 //WHERE id>=2000 and id<=3000
        ResultSet resultSet = ConnectMSSQLServer.select("SELECT * FROM [dbo].[userinfo] WHERE id>=2250 and id<=2260");
        System.out.println("********************Création d'utitisateurs***********************");
        while (resultSet.next()) {
            Node firstNode = graphDb.createNode();
            User user = new User(firstNode);
            user.create(resultSet.getString("id"), resultSet.getString("title"), resultSet.getString("longitude"), resultSet.getString("latitude"), resultSet.getString("location"), resultSet.getString("country"), resultSet.getString("updated"), "");

            List<Integer> ServicesID = getServicesIdRelations(resultSet.getString("title"));
            System.out.println("\nUtilisateur N° : " + resultSet.getString("id"));
            for (Integer integer : ServicesID) {
                if (getServiceNodeByID(integer + "") != null) {
                    System.out.println("Utilise----service_Id : " + integer + " -->" + getServiceNodeByID(integer + ""));
                    firstNode.createRelationshipTo(getServiceNodeByID(integer + ""), RelTypes.UTILISE);
                }
            }

        }

        Iterable<Node> tous = GlobalGraphOperations.at(graphDb).getAllNodes();
        for (Node noeud : tous) {
            if (noeud.hasProperty("type")) {
                Object le_type_du_noeud = noeud.getProperty("type");
                String nom_du_type = le_type_du_noeud.toString();

                if (nom_du_type.equals("user") == true) {
                    IndexManager gestionnaire_index = graphDb.index();
                    Index<Node> mon_index = gestionnaire_index.forNodes(nom_du_type);
                    mon_index.add(noeud, "Index_" + nom_du_type, le_type_du_noeud);
                }
            }
        }
    }

    public List<Integer> getServicesIdRelations(String idUser) throws SQLException {
        ResultSet resultSet = ConnectMSSQLServer.select("SELECT id FROM [dbo].[watchlist] WHERE [user] LIKE '" + idUser + "'");
        List<Integer> idServices = new ArrayList<>();
        while (resultSet.next()) {
            //System.out.println("--->"+resultSet.getInt("id"));
            idServices.add(resultSet.getInt("id"));
        }
        return idServices;
    }

    public Node getServiceNodeByID(String idService) throws SQLException {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("start n=node(*) match n where has(n.id) and n.id='" + idService + "' return n");
        scala.collection.Iterator<Node> iterator = result.columnAs("n");
        Node un_noeud = null;
        while (iterator.hasNext()) {
            un_noeud = iterator.next();
        }
        return un_noeud;
    }

    //Cette fonction permet de créer les liens entre services en utilisant la matrice de similarité
    public void setWebServiceRelations() {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='ws' RETURN n");//cette requete retourne l'ensemble de services web qui existent dans notre base de données neo4j
        scala.collection.Iterator<Node> iterator = result.columnAs("n");

        List<List<String>> services = new ArrayList<>();//Matrice auxiliaire, Contient les propriétés de tous les services web qui existent dans la base de données
        List<List<String>> services1 = new ArrayList<>();//Matrice auxiliaire, Contient les propriétés de tous les services web qui existent dans la base de données
        List<List<Double>> servicesSimilarity = new ArrayList<>();//Marice de similarité

        while (iterator.hasNext()) {//Insertion des lignes (Propriétés de chaque service ) dans la matrice auxiliaire 'services'

            Node un_noeud = iterator.next();
            List<String> proprieties = new ArrayList<>();
            List<String> proprieties1 = new ArrayList<>();

            for (String propertyKey : un_noeud.getPropertyKeys()) {
                //System.out.print("prop"+propertyKey);
                proprieties.add("" + un_noeud.getProperty(propertyKey));
                if (propertyKey.equals("summury") || propertyKey.equals("wsname") || propertyKey.equals("tags")) {
                    proprieties1.add("" + un_noeud.getProperty(propertyKey));
                }
            }
            services1.add(proprieties1);
            services.add(proprieties);
        }

        for (int i = 0; i < services1.size(); i++) {
            // List<String> myService = services.get(i);
            List<String> myService1 = services1.get(i);
            List<String> myService = services.get(i);
            //**********************************Création de relations service-catégorie*************************************//
            ExecutionResult result2 = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='category' RETURN n");
            scala.collection.Iterator<Node> categories = result2.columnAs("n");
            while (categories.hasNext()) {
                Node un_noeud = categories.next();
                if (un_noeud.getProperty("name").equals(services.get(i).get(2))) {
                    graphDb.getNodeById(i + 1).createRelationshipTo(getCategoryFromNodeById((int) un_noeud.getProperty("id")), RelTypes.APPARTIENT);
                }
            }
            //**************************************************************************************************************//

            //*****Calcul de la similarité entre deux service et création de la matrice de similarité********//
            List<Double> similarity_Two_Rows = new ArrayList<>();

            for (int j = 0; j < services1.size(); j++) {
                if (i != j) {
                    List<String> otherService1 = services1.get(j);
                    similarity_Two_Rows.add(jaccard.jaccard_coeffecient(myService1, otherService1));
                } else {
                    similarity_Two_Rows.add((double) 1);
                }
            }
            servicesSimilarity.add(similarity_Two_Rows);
        }
        //*****************************************************************************************************//

        //*********************************Relation (service-service)*****************************************//
        for (int i = 0; i < servicesSimilarity.size(); i++) {
            WebService webService1 = new WebService(getNodeById(i + 1));
            for (int j = i + 1; j < servicesSimilarity.get(0).size(); j++) {
                if (servicesSimilarity.get(i).get(j) > seuil && servicesSimilarity.get(i).get(j) != 1) {
                    WebService webService2 = new WebService(getNodeById(j + 1));
                    webService1.add_similare_ws(webService2, servicesSimilarity.get(i).get(j));
                    System.out.println(getNodeById(i + 1).getProperty("wsname") + " >--< " + getNodeById(i + 1) + " <---" + servicesSimilarity.get(i).get(j) + "----> " + getNodeById(j + 1) + " >--< " + getNodeById(j + 1).getProperty("wsname"));
                }
            }
        }
        //******************************************************************************************************//       
        clustering(servicesSimilarity);
    }

    public void clustering(List<List<Double>> servicesSimilarity) {
        for (int i = 0; i < servicesSimilarity.size(); i++) {
            //List<Node> nodes = new ArrayList<>();
            List<Integer> idservices = new ArrayList<>();
            List<String> set1 = new ArrayList<String>();
            List<Node> nodes = new ArrayList<>();
            nodes.add(getNodeById(i+1));
            String[] s = null;
            for (int j = i + 1; j < servicesSimilarity.get(0).size() - 1; j++) {
                if ( servicesSimilarity.get(i).get(j) > seuil_Cluster && servicesSimilarity.get(i).get(j) != 1) {
                    idservices.add(j + 1);
                    nodes.add(getNodeById(j + 1));
                    s = ((String) getNodeById(j + 1).getProperty("tags")).split(",");
                    for (String string : s) {
                        if (string != null) {
                            set1.add(string);
                        }
                    }
                }
            }

//            Arrays.stream(set1).collect(Collectors.groupingBy(sa -> sa))
//      .forEach((k, v) -> System.out.println(k+" "+v.size()));
            int y;
            for (int k = 0; k < idservices.size(); k++) {
                for (int x = 0; x < servicesSimilarity.size(); x++) {
                    y = idservices.get(k);
                    if (servicesSimilarity.get(y).get(x) > seuil_Cluster && servicesSimilarity.get(y).get(x) != 1) {
                        nodes.add(getNodeById(x + 1));
                        s = ((String) getNodeById(x + 1).getProperty("tags")).split(",");
                        for (String string : s) {
                            if (string != null) {
                                set1.add(string);
                            }
                        }
                    }
                }

            }

            //Création du cluster 
            if (nodes.size() > 1) {
                int max = 0;
                int curr = 0;
                String currKey = null;
                Set<String> unique = new HashSet<String>(set1);

                //Cette boucle pour calculer le nombre d'apparaition d'une chaine de caractères dans la liste des tags.
                //Comme sortie -> la chaine de caractères la plus utilisée 'currKey'
                for (String key : unique) {
                    curr = Collections.frequency(set1, key);
                    if (max < curr) {
                        max = curr;
                        currKey = key;
                    }
                }
                System.out.println("*************Le tag " + currKey + " est utilisé " + max + " fois**************");

                //Création du cluster
                Node node_C = graphDb.createNode();
                node_C.setProperty("name", currKey);
                node_C.setProperty("type", "Cluster");
                for (Node node : nodes) {
                    node.createRelationshipTo(node_C, RelTypes.APPARTIENT_C);
                }
            }
        }
    }

    public Node getNodeById(int id) {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("start n=node(" + id + ") return n");
        scala.collection.Iterator<Node> iterator = result.columnAs("n");
        Node un_noeud = null;
        while (iterator.hasNext()) {
            un_noeud = iterator.next();
        }
        return un_noeud;
    }

    public void createCategorie() {
        System.out.println("------------Création des noeuds catégorie---------------");
        int comp = 999;
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("Start n=node(*) match n where has(n.categoryid)  return distinct (n.categoryid)");
        while (result.hasNext()) {
            String s = "" + result.next();
            Node node = graphDb.createNode();
            node.setProperty("id", comp);
            node.setProperty("name", s.split("-> ")[1].split("\\)")[0]);
            node.setProperty("type", "category");
            comp++;
        }
    }

    public scala.collection.Iterator<Node> getAllCategories() {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='category' RETURN n");
        scala.collection.Iterator<Node> iterator = result.columnAs("n");
        while (iterator.hasNext()) {
            Object un_noeud = iterator.next();
        }
        return iterator;
    }

    public Node getCategoryFromNodeById(int id) {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("start n=node(*) match n where has( n.type) and n.type='category' and n.id=" + id + " return n");
        scala.collection.Iterator<Node> iterator = result.columnAs("n");
        Node un_noeud = null;
        while (iterator.hasNext()) {
            un_noeud = iterator.next();
        }
        return un_noeud;
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

    private static enum RelTypes implements RelationshipType {
        SIMILAIRE, APPARTIENT, UTILISE, APPARTIENT_C
    }

    //****************************************************************************************************//
    public List<Integer> getIdServices() {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("Start n=node(*) match n WHERE has(n.type) AND n.type='ws'  return (n.id)");
        List<Integer> allIdServices = new ArrayList<>();
        while (result.hasNext()) {
            String s = "" + result.next();
            allIdServices.add(Integer.parseInt(s.split("-> ")[1].split("\\)")[0]));
        }
        return allIdServices;
    }

    public List<Integer> getIdServicesByUser(String idUser) {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("Start n=node(*) match n-[:UTILISE]->(a) where has(n.title) and n.title='" + idUser + "' return a.id");
        List<Integer> idsServices = new ArrayList<>();
        while (result.hasNext()) {
            String s = "" + result.next();
            //System.out.println(s + " ----> " + Integer.parseInt(s.split("-> ")[1].split("\\)")[0]));
            idsServices.add(Integer.parseInt(s.split("-> ")[1].split("\\)")[0]));
        }
        //System.out.println("------------>" + idUser + "utilise " + idsServices);
        return idsServices;
    }

    public void setUserRelations() throws SQLException {
        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
        ExecutionResult result = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='user' RETURN n");
        scala.collection.Iterator<Node> users = result.columnAs("n");

        List<List<String>> services = new ArrayList<List<String>>();
        List<Integer> idAllServices = getIdServices();//les ids de tous les services
        List<List<Integer>> matrixServices = new ArrayList<List<Integer>>();
        while (users.hasNext()) {
            Node un_noeud = users.next();
            List<Integer> idServicesUser = getIdServicesByUser("" + un_noeud.getProperty("title"));//les ids de services utilisés par un utilisateur
            matrixServices.add(idServicesUser);
        }
        if (!matrixServices.isEmpty()) {
            System.out.println("Matrix--------->" + matrixServices);
            for (int i = 0; i < matrixServices.size(); i++) {
                List<Integer> line1 = matrixServices.get(i);
                if (!line1.isEmpty()) {
//                    System.out.println("Line1--" + line1);
                    for (int j = 0; j < line1.size(); j++) {
                        System.out.println("--->j=" + j);
                        if (i != j) {
                            List<Integer> line2 = matrixServices.get(j);
//                                System.out.println("Line2--" + line2);
                            if (!line2.isEmpty()) {
                                for (int k = 0; k < line2.size(); k++) {
                                    if (Objects.equals(line1.get(j), line2.get(k))) {
                                        System.out.println("User" + i + " <-> User" + j);

                                    } else {
                                        System.out.println("---------Rien--------");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//    public void setUserRelations() throws SQLException {
//        ExecutionEngine engine = new ExecutionEngine(graphDb, StringLogger.SYSTEM);
//        ExecutionResult result = engine.execute("START n=node(*) WHERE has(n.type) AND n.type='user' RETURN n");
//        scala.collection.Iterator<Node> users = result.columnAs("n");
//
//        List<List<String>> services = new ArrayList<List<String>>();
//        List<Integer> idAllServices = getIdServices();//les ids de tous les services
//        while (users.hasNext()) {
//            List<List<Integer>> matrixServices = new ArrayList<List<Integer>>();
//            Node un_noeud = users.next();
//            List<Integer> idServicesUser = getIdServicesByUser("" + un_noeud.getProperty("title"));//les ids de services utilisés par un utilisateur
//            for (int i = 0; i < idAllServices.size(); i++) {
//                List<Integer> line = new ArrayList<>();
//                for (int j = 0; j < users.size(); j++) {
//                    if (Objects.equals(idAllServices.get(i), idServicesUser.get(j))) {
//                        line.add(1);
//                    } else {
//                        line.add(0);
//                    }
//                }
//                matrixServices.add(line);
//            }
//
//            for (int i = 0; i < matrixServices.size(); i++) {
//                for (int j = 0; j < matrixServices.get(0).size(); j++) {
//                    System.out.print(matrixServices.get(i).get(j) + " ");
//                }
//            }
//
//        }
//
//    }

    public static GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    public static void setGraphDb(GraphDatabaseService graphDb) {
        TestNeo4J.graphDb = graphDb;
    }
    
    
}
