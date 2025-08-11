package com.hcl.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name="branch_address")
    private String branchAddress;

    @Column(name="mobile_number", unique = true, nullable = false)
    private String mobileNumber;

    public Account() {
        super();
    }

    public Account(int id, String accountNumber, String bankName, String ifscCode, String branchAddress, String mobileNumber) {
        super();
        this.id = id;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.branchAddress = branchAddress;
        this.mobileNumber = mobileNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", accountNumber=" + accountNumber + ", bankName=" + bankName + ", ifscCode="
                + ifscCode + ", branchAddress=" + branchAddress + ", mobileNumber=" + mobileNumber + "]";
    }
}