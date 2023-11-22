package com.example.petlink.Model;

public class Transaction {
    String id;
    String animalId;
    String user;
    String date;

    public Transaction() {
    }

    public Transaction(String id, String animalId, String user, String date) {
        this.id = id;
        this.animalId = animalId;
        this.user = user;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
