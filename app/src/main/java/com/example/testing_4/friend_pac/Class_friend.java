package com.example.testing_4.friend_pac;

public class Class_friend
{
    private String name;
    private String age;
    private String email;
//    private String data_images;
    private String phone_number ;

    // Required empty constructor (for Firebase)
    public Class_friend() {
    }

    public Class_friend(String name, String age, String email , String phone_number) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone_number = phone_number;
//        this.data_images = data_images;
    }

    // Getters and Setters (required for Firebase)
    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

//    public String getData_images() {return data_images;}
//
//    public void setData_images(String data_images) {
//        this.data_images = data_images;
//    }

    // Override toString() for easy debugging
    @Override
    public String toString() {
        return "User{" +
                "Name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone_number ='" + phone_number + '\'' +
                '}';
    }
}
