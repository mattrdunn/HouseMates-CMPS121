package cmps121.matt.housemates;

public class UserInformation
{
    public String firstName;
    public String lastName;
    public String studentId;
    public String password;
    public String email;

    public UserInformation(String firstName, String lastName, String studentId, String email)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email;
    }

    //Returns the first name of user
    public String getFirstName()
    {
        return firstName;
    }

    //Returns the last name of the user
    public String getLastName()
    {
        return lastName;
    }

    //Returns the student ID of the student
    public String getStudentId()
    {
        return studentId;
    }

    //Return user email
    public String getEmail()
    {
        return email;
    }
}
