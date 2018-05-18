package cmps121.matt.housemates;

public class HouseInformation
{
    public String houseName;
    public String housePassword;

    // this is needed for firebase**
    public HouseInformation()
    {

    }

    public HouseInformation(String houseName, String housePassword)
    {
        this.houseName = houseName;
        this.housePassword = housePassword;
    }

    public String getHouseName()
    {
        return houseName;
    }

    // Reason why this is commented is b/c it made an extra field for the password in the DB
//    public String getHousePW()
//    {
//        return housePassword;
//    }


}
