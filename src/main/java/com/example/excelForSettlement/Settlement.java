package com.example.excelForSettlement;

import org.springframework.stereotype.Component;

@Component
public class Settlement {
    double value;
    String account;
    String name;
    String divadeInformation;

    public Settlement() {
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivadeInformation() {
        return divadeInformation;
    }

    public void setDivadeInformation(String divadeInformation) {
        this.divadeInformation = divadeInformation;
    }
}

