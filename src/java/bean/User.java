/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Naoufal
 */
public class User {
    static final String type="type";
    static final String id="id";
    static final String title="title";
    static final String longitude="longitude";
    static final String latitude="latitude";
    static final String location="location";
    static final String country="country";
    static final String updated="updated";
    static final String password="password";
    
    private final Node refNode;

    public User(Node myNode) {
        this.refNode = myNode;
    }

    public void create(String id,String title,String longitude,String latitude,String location,String country,String updated,String password){
        Node myNode=this.refNode;
        myNode.setProperty(this.id, id);
        myNode.setProperty(this.title, title);
        myNode.setProperty(this.longitude, longitude);
        myNode.setProperty(this.latitude, latitude);
        myNode.setProperty(this.location, location);
        myNode.setProperty(this.country, country);
        myNode.setProperty(this.updated, updated);
        myNode.setProperty(this.password, password);
        myNode.setProperty(this.id, id);
        myNode.setProperty(this.updated, updated);
        myNode.setProperty(this.type, "user");
    }
    
    public String getType() {
        return (String) refNode.getProperty(type) ;
    }

    public Node getMyNode() {
        return refNode;
    }

    public static String getId() {
        return id;
    }

    public static String getTitle() {
        return title;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static String getLocation() {
        return location;
    }

    public static String getCountry() {
        return country;
    }

    public static String getUpdated() {
        return updated;
    }

    public static String getPassword() {
        return password;
    }

    public Node getRefNode() {
        return refNode;
    }
    
    
    
    private static enum RelTypes implements RelationshipType {
        UTILISE
    }
    
    public void add_ws(User myUser){
        Node node=myUser.getMyNode();
        Relationship relation=refNode.createRelationshipTo(node,RelTypes.UTILISE);
        System.out.println("------->"+relation);
    }
    

    
    public void showAll(){
        System.out.println("Name : "+refNode.getProperty(id)+" ");
        System.out.println("type : "+refNode.getProperty(type)+" ");
        System.out.println("description : "+refNode.getProperty(title)+" ");
    }
    
}
