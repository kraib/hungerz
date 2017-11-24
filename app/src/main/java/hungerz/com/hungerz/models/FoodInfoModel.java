package hungerz.com.hungerz.models;

/**
 * Created by sokool on 11/24/2017.
 */

public class FoodInfoModel {
    String name;
    String FoodType;
    String NumberOfPeople;
    String TimeLimit;
    String Wish;
    String latitude;
    String longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodType() {
        return FoodType;
    }

    public void setFoodType(String foodType) {
        FoodType = foodType;
    }

    public String getNumberOfPeople() {
        return NumberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        NumberOfPeople = numberOfPeople;
    }

    public String getTimeLimit() {
        return TimeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        TimeLimit = timeLimit;
    }

    public String getWish() {
        return Wish;
    }

    public void setWish(String wish) {
        Wish = wish;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
