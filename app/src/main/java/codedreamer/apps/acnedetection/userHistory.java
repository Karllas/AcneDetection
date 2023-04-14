package codedreamer.apps.acnedetection;

public class userHistory
{
    String acneType, area, age, sleepingHours, gender, skinType, spicyFood, advice, medication, date, time;

    public userHistory() {}

    public userHistory(String acneType, String area, String age, String sleepingHours, String gender, String skinType, String spicyFood, String advice, String medication, String date, String time)
    {
        this.acneType = acneType;
        this.area = area;
        this.age = age;
        this.sleepingHours = sleepingHours;
        this.gender = gender;
        this.skinType = skinType;
        this.spicyFood = spicyFood;
        this.advice = advice;
        this.medication = medication;
        this.date = date;
        this.time = time;
    }

    public String getAcneType()
    {
        return acneType;
    }

    public void setAcneType(String acneType)
    {
        this.acneType = acneType;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getSleepingHours()
    {
        return sleepingHours;
    }

    public void setSleepingHours(String sleepingHours)
    {
        this.sleepingHours = sleepingHours;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getSkinType()
    {
        return skinType;
    }

    public void setSkinType(String skinType)
    {
        this.skinType = skinType;
    }

    public String getSpicyFood()
    {
        return spicyFood;
    }

    public void setSpicyFood(String spicyFood)
    {
        this.spicyFood = spicyFood;
    }

    public String getAdvice()
    {
        return advice;
    }

    public void setAdvice(String advice)
    {
        this.advice = advice;
    }

    public String getMedication()
    {
        return medication;
    }

    public void setMedication(String medication)
    {
        this.medication = medication;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
}
