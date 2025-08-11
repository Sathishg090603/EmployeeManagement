	package com.hcl.model;
	
	import javax.persistence.*;
	
	
	@Entity
	@Table(name = "employees")
	public class Employee {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	
	    @Column(name="name")
	    private String name;
	
	    @Column(name="dob")
	    private String dob;
	
	    @Column(name="gender")
	    private String gender;
	
	    @Column(name="email", unique = true, nullable = false)
	    private String email;
	
	    @Column(name="skills")
	    private String skills;
	    
	    @Column(name="doj")
	    private String doj;
	
	    @Column(name="department")
	    private String department;
	
	    @OneToOne
	    @JoinColumn(name = "mobile_number", referencedColumnName = "mobile_number", unique = true, nullable = false)
	    private Account account;	
	    // ----------------------------------------
	    
	    @ManyToOne
	    @JoinColumn(name="project_id",referencedColumnName="id",nullable=true)
	    Project project;
	
	    public Employee() {
	        super();
	    }
	
	    public Employee(int id, String name, String dob, String gender, String email, String skills,String doj, String department, Account account,Project project) {
	        super();
	        this.id = id;
	        this.name = name;
	        this.dob = dob;
	        this.gender = gender;
	        this.email = email;
	        this.skills = skills;
	        this.department = department;
	        this.account = account;
	        this.project=project;	    }
	
	    public String getDoj() {
			return doj;
		}
	 

		public void setDoj(String doj) {
			this.doj = doj;
		}

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
	
	    public String getDob() {
	        return dob;
	    }
	
	    public void setDob(String dob) {
	        this.dob = dob;
	    }
	
	    public String getGender() {
	        return gender;
	    }
	
	    public void setGender(String gender) {
	        this.gender = gender;
	    }
	
	    public String getSkills() {
	        return skills;
	    }
	
	    public void setSkills(String skills) {
	        this.skills = skills;
	    }
	
	    public String getEmail() {
	        return email;
	    }
	
	    public void setEmail(String email) {
	        this.email = email;
	    }
	
	    public String getDepartment() {
	        return department;
	    }
	
	    public void setDepartment(String department) {
	        this.department = department;
	    }
	
	    public Account getAccount() {
	        return account;
	    }
	
	    public void setAccount(Account account) {
	        this.account = account;
	    }

		public Project getProject() {
			return project;
		}

		public void setProject(Project project) {
			this.project = project;
		}
	    
	}