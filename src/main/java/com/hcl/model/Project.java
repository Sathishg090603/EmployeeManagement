package com.hcl.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="projects")
public class Project {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="description")
    private String description;
    
    @Column(name="required_skills")
    private String requiredSkill;
    
    @Column(name="start_date")
    private String startDate;
    
    @Column(name="end_date")
    private String endDate;   
    
    @Column(name="client")
    private String client;
    
   
	@OneToMany(mappedBy="project" ,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Employee>employees;

    // Constructors
    public Project() {}

    public Project(int id, String name, String description, String requiredSkill, String startDate, String endDate,String client) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredSkill = requiredSkill;
        this.startDate = startDate;
        this.endDate = endDate;
        this.client=client;
        }
    

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getrequiredSkill() {
        return requiredSkill;
    }

    public void setrequiredSkill(String requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
	
}
