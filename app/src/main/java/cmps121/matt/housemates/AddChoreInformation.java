package cmps121.matt.housemates;

public class AddChoreInformation {
    public String choreName;
    public String choreDescription;
    public String assignee;
    public String dateCreated;
    public String dueDate;

    //Firebase constructor
    public AddChoreInformation(){

    }

    public AddChoreInformation (String choreName, String choreDescription, String assignee, String dateCreated, String dueDate) {
        this.choreName = choreName;
        this.choreDescription = choreDescription;
        this.assignee = assignee;
        this.dateCreated = dateCreated;
        this.dueDate = dueDate;
    }
}
