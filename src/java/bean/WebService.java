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
public class WebService {
    static final String type="type";
    static final String wsname="wsname";
    static final String summury="summury";
    static final String categoryid="categoryid";
    static final String tags="tags";
    static final String protocols="protocols";
    static final String dataformats="dataformats";
    static final String apihome="apihome";
    static final String description="description";
    static final String provider="provider";
    static final String signupRequire="signupRequire";
    static final String developerKey="developerKey";
    static final String id="id";
    static final String updated="updated";
    static final String wsdl="wsdl";
    static final String rating="rating";
    private final Node refNode;

    public WebService(Node myNode) {
        this.refNode = myNode;
    }

    public void create(String wsname,String summury,String categoryid,String tags,String protocols,String dataformats,String APIhome,String Description,String provider,String signupRequire,String DeveloperKey,String id,String updated,String wsdl,String rating){
        Node myNode=this.refNode;
        myNode.setProperty(this.wsname, wsname);
        myNode.setProperty(this.summury, summury);
        myNode.setProperty(this.categoryid, categoryid);
        myNode.setProperty(this.tags, tags);
        myNode.setProperty(this.protocols, protocols);
        myNode.setProperty(this.dataformats, dataformats);
        myNode.setProperty(this.apihome, APIhome);
        myNode.setProperty(this.description, Description);
        myNode.setProperty(this.provider, provider);
        myNode.setProperty(this.signupRequire, signupRequire);
        myNode.setProperty(this.developerKey, DeveloperKey);
        myNode.setProperty(this.id, id);
        myNode.setProperty(this.updated, updated);
        myNode.setProperty(this.wsdl, wsdl);
        myNode.setProperty(this.rating, rating);
        myNode.setProperty(this.type, "ws");
    }
    
    public String getType() {
        return (String) refNode.getProperty(type) ;
    }

    public String getWsname() {
        return (String) refNode.getProperty(wsname);
    }

    public String getSummury() {
        return (String) refNode.getProperty(summury);
    }

    public String getCategoryid() {
        return (String) refNode.getProperty(categoryid);
    }

    public String getTags() {
        return (String) refNode.getProperty(tags);
    }

    public String getProtocols() {
        return (String) refNode.getProperty(protocols);
    }

    public String getDataformats() {
        return (String) refNode.getProperty(dataformats);
    }

    public String getApihome() {
        return (String) refNode.getProperty(apihome);
    }

    public String getDescription() {
        return (String) refNode.getProperty(description);
    }

    public String getProvider() {
        return (String) refNode.getProperty(provider);
    }

    public String getSignupRequire() {
        return (String) refNode.getProperty(signupRequire);
    }

    public String getDeveloperKey() {
        return (String) refNode.getProperty(developerKey);
    }

    public String getId() {
        return (String) refNode.getProperty(id);
    }

    public String getUpdated() {
        return (String) refNode.getProperty(updated);
    }

    public String getWsdl() {
        return (String) refNode.getProperty(wsdl);
    }

    public String getRating() {
        return (String) refNode.getProperty(rating);
    }

    public Node getMyNode() {
        return refNode;
    }
    
    
    private static enum RelTypes implements RelationshipType {
        SIMILAIRE,APPARTIENT
    }
    
    public void add_similare_ws(WebService newWebService,double seuil){
        Node node=newWebService.getMyNode();
        Relationship relation=refNode.createRelationshipTo(node,RelTypes.SIMILAIRE);
        relation.setProperty("seuil", seuil);
        System.out.println("------->"+relation);
    }
    
    public void add_category_ws(WebService newWebService){
        Node node=newWebService.getMyNode();
        Relationship relation=refNode.createRelationshipTo(node,RelTypes.APPARTIENT);
        System.out.println(RelTypes.APPARTIENT+"------->"+relation);
    }
    
    
    public void showAll(){
        System.out.println("Name : "+refNode.getProperty(wsname)+" ");
        System.out.println("type : "+refNode.getProperty(type)+" ");
        System.out.println("description : "+refNode.getProperty(description)+" ");
    }
    
}
