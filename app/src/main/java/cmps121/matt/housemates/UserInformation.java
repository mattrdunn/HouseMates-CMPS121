package cmps121.matt.housemates;

public class UserInformation
{
    public String name;
    public String email;

    public UserInformation(String name, String email)
    {
        this.name = name;
        this.email = email;
    }

    //Returns the first name of user
    public String getName()
    {
        return name;
    }

    //Return user email
    public String getEmail()
    {
        return email;
    }
}
