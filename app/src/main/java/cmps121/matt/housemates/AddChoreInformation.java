package cmps121.matt.housemates;

public class AddChoreInformation {
    public String choreName;
    public String choreDescription;
    public String assignee;

    //Firebase constructor
    public AddChoreInformation(){

    }

    public AddChoreInformation (String choreName, String choreDescription, String assignee) {
        this.choreName = choreName;
        this.choreDescription = choreDescription;
        this.assignee = assignee;
    }
}
