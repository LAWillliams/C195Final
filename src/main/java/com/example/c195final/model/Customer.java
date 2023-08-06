package com.example.c195final.model;

public class Customer {

    private int Customer_ID;
    private String Customer_Name;
    private String Address;
    private String Postal_Code;
    private String Phone;
    private int Division_ID;
    private String Create_Date;
    private String Created_By;
    private String Last_Update;
    private String Last_Updated_By;

    public Customer(int Customer_ID, String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID, String Create_Date, String Created_By, String Last_Update, String Last_Updated_By){

        this.Customer_ID = Customer_ID;
        this.Customer_Name = Customer_Name;
        this.Address = Address;
        this.Postal_Code = Postal_Code;
        this.Phone = Phone;
        this.Create_Date = Create_Date;
        this.Created_By = Created_By;
        this.Last_Update = Last_Update;
        this.Last_Updated_By = Last_Updated_By;
        this.Division_ID = Division_ID;

    }

    public Customer(int customerId, String name, String address, String postalCode, String phone, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int divisionId) {

        this.Customer_ID = customerId;
        this.Customer_Name = name;
        this.Address = address;
        this.Postal_Code = postalCode;
        this.Phone = phone;
        this.Create_Date = createDate;
        this.Created_By = createdBy;
        this.Last_Update = lastUpdate;
        this.Last_Updated_By = lastUpdatedBy;
        this.Division_ID = divisionId;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getPostal_Code() {
        return Postal_Code;
    }

    public String getPhone() {
        return Phone;
    }

    public int getDivision_ID() {
        return Division_ID;
    }
    public String getCreate_Date(){
        return Create_Date;
    }
    public String getCreated_By(){
        return Created_By;
    }
    public String getLast_Update(){
        return Last_Update;
    }
    public String getLast_Updated_By(){
        return Last_Updated_By;
    }
}
