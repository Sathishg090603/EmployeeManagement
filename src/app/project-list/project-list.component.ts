import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, NgModule } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { ProjectService } from '../services/project.service';
import { project } from '../models/project/project.model';
import { MessageComponent } from '../message/message.component';
import { Employee } from '../models/employee/employee.model';
import { EmployeeService } from '../services/employee.service';
import { ProjectAssignModelComponent } from '../project-assign-model/project-assign-model.component';
import { AssignedListModelComponent } from '../assigned-list-model/assigned-list-model.component';
import { ConfirmationModalComponent } from '../confirmation-modal/confirmation-modal.component';
import { HeaderComponent } from '../shared/header/header.component';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { FormsModule } from '@angular/forms';

declare var bootstrap: any;


@Component({
  selector: 'app-project-list',
  imports: [CommonModule, RouterLink, MessageComponent, ProjectAssignModelComponent, AssignedListModelComponent, ConfirmationModalComponent, HeaderComponent, FormsModule],
  standalone: true,
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent {
  private readonly USER_KEY = 'loggedInUsername';
  loggedInUsername: string | null = null; // Type it as string | null


  allProjects: project[] = [];
  completedProjects: project[] = [];
  filteredProjects: project[] = [];
  expiringProjects: project[] = []; //to store & show expiring projects to take action
  isLoading: boolean = true;

  showToast: boolean = false;  //notification toast
  showBell: boolean = true;
  showCompletedProjects: boolean = false; //to show only completed projects
  showExpiredProject:boolean=false;

  filterType: string = 'Name';
  searchTerm: string = ''; //to serach project name



  selectedProject: any = null; //both are used for pass inputs to the employee-assign model
  showSelectedProject: any = null;
  allEmployeesUnAssigned: Employee[] = [];
  Employees: Employee[] = [];


  ToBeAssignedEmployees: Employee[] = []; //unassignedEmployees 

  EmployeeToDeallocate!: Employee;
  projectToDelete!: project;

  AllocatedEmployees: Employee[] = []; //To show the assigned employees of project

  showConfirmationModal: boolean = false;
  confirmationMessage: string = '';

  showModal = false; //for assign model
  showAssignedModal = false; //for showing assigned employees

  //message modal view variable
  message: string | null = null;

  constructor(
    private router: Router,
    private projectService: ProjectService,
    private employeeService: EmployeeService
  ) { }

  ngOnInit(): void {
    // if (!sessionStorage.getItem(this.USER_KEY)) {
    //   this.router.navigate(['/login']);
    // }
    this.loadProjects();
    this.loggedInUsername = sessionStorage.getItem(this.USER_KEY);
    this.loadEmployees();
    console.log(this.filterType)
  }

  LogOut(): void {
    sessionStorage.removeItem(this.USER_KEY);
    console.log('Logging out (placeholder action)');
    this.router.navigate(['/login']);
  }
  loadProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (data: project[]) => {
        console.log('Projects:', data);
        this.allProjects = data.filter(project => {
          if (project.endDate) {
            const end = new Date(project.endDate)
            const today = new Date();
            return end > today;
          }
          return false;
        }
        )
        this.completedProjects = data.filter(project => {
          if (project.endDate) {
            const end = new Date(project.endDate)
            const today = new Date();
            return end<today;
          }
          return false;
        }
        )
        console.log(this.completedProjects)
        this.checkExpiring(); //after loading projects it will store the expiring projects into the arrray of expiring projects
        this.isLoading = false;
        console.log('completed',this.completedProjects)
      },
      error: (err) => {
        console.error('Error loading employees:', err);
      }
    });
  }
  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe({
      next: (data: Employee[]) => {
        console.log('All employees:', data);
        this.Employees = data;
        this.allEmployeesUnAssigned = data.filter(emp => emp['project'] == null);
      }
    })
  }
  openAssignModal(project: any) {
    this.selectedProject = project;
    this.showModal = true;
  }
  openViewModal(project: any) {
    this.selectedProject = project;
    this.loadAssignedEmployees(this.selectedProject);
  }
  loadAssignedEmployees(project: any) {
    this.employeeService.getAllEmployees().subscribe({
      next: (data: Employee[]) => {
        console.log('All employees:', data);
        this.AllocatedEmployees = data.filter(emp => emp['project'] && emp['project'].id === project.id);
        this.showAssignedModal = true
      }
    })
  }
  handleEmployeeAssignment(event: any) {
    const { employee, project } = event;  //this is assigning multiple object at same line like below

    // const employee1=event.employee
    //const project1=event.project;

    this.projectService.AssignProjects(employee.id, project.id).subscribe({
      next: () => {
        this.loadEmployees();
        this.showModal = false;
        this.showSuccess(`Assigned ${employee.name} to ${project.name}`);
      },
      error: (err) => {
        this.showModal = false;
        this.showError(err.message);
      }
    })
  }
  confirmDeallocateEmployee(event: { employee: any, project: any }) {
    this.showAssignedModal = false;
    this.EmployeeToDeallocate = event.employee;
    this.confirmationMessage = `Are you sure want to Deallocate  ${event.employee.name}?! from ${event.project.name}`;
    this.showConfirmationModal = true;
  }
  handleConfirmationResponse(confirmed: boolean) {
    if (confirmed) {
      if (this.EmployeeToDeallocate) {
        this.AfterDeallocateConfirm();
      }
      if (this.projectToDelete) {
        this.AfterDeleteConfirm();
      }
    }
    this.showConfirmationModal = false;

  }

  confirmDeleteProject(pro: project) {
    this.projectToDelete = pro;
    this.confirmationMessage = `Are you sure you want to delete project '${pro.name}'? This action cannot be undone.`;
    this.showConfirmationModal = true; // Show the confirmation modal

  }
  AfterDeallocateConfirm() {

    const employee = { ...this.EmployeeToDeallocate, project: null };
    this.employeeService.updateEmployee(employee).subscribe({
      next: () => {
        this.showConfirmationModal = false;
        this.loadEmployees();
        this.showSuccess(`${employee.name} Deallocated successfully`);
        this.EmployeeToDeallocate = undefined!;


      },
      error: (err) => {
        console.error('Error Deallocating  Employee:', err);
        this.showError(err.message)
      }
    });
  }
  AfterDeleteConfirm() {
    this.projectService.deleteProject(this.projectToDelete.id).subscribe({
      next: () => {
        this.showConfirmationModal = false;
        this.showSuccess(`${this.projectToDelete.name} Deleted Successfully`);
        this.loadProjects();
        this.projectToDelete = undefined!;
      },
      error: (error) => {
        console.log(error);
        this.showError(error.error.message);
      }
    })
  }

  showSuccess(msg: string): void {
    this.message = msg;
  }

  showError(msg: string): void {
    this.message = msg;

  }

  AddNewProject() {
    this.router.navigate(['/project-form'])
  }
  getDaysUntilExpiry(endDate: string): number | null {
    const today = new Date();
    const end = new Date(endDate);
    const diffTime = end.getTime() - today.getTime();
    const daysLeft = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    if (daysLeft == 10 || daysLeft < 10)
      return daysLeft;

    else
      return null;
  }
  checkExpiring() {
    this.expiringProjects = this.allProjects.filter(project => {
      if (project.endDate) {
        const end = new Date(project.endDate)
        const today = new Date();
        const DaysLeft = this.getDaysUntilExpiry(project.endDate);
        return (end > today && (DaysLeft != null && DaysLeft < 11))
      }
      return false;
    });
  }
  OnFilterChange(event: Event) {
    this.filterType = (event.target as HTMLSelectElement).value
    console.log(this.filterType);
    this.onSearchChange()
  }
  onSearchChange() {
    this.showSelectedProject = null;
    if (this.searchTerm.trim().length < 3) {
      this.filteredProjects = [];
    }
    else {
      if (this.filterType == 'Name') {
        this.filteredProjects = this.allProjects.filter(project => project.name.toLowerCase().includes(this.searchTerm.toLowerCase())).slice(0, 5);
        console.log(this.filteredProjects);
      }
      else {
        this.filteredProjects = this.allProjects.filter(project => project.client.toLowerCase().includes(this.searchTerm.toLowerCase())).slice(0, 5);
        console.log(this.filteredProjects);
      }
    }

    this.loadProjects();

  }
  toggleExpiringProjectsView() {
    this.showToast = true;
  }
  showExpired() {
    
    this.showToast = false;
    this.showBell=false;
    this.showExpiredProject=true;
    this.allProjects = this.expiringProjects;
  }

  clearSearch() {
    this.searchTerm = ''
    this.showSelectedProject = null;
    this.filteredProjects = [];
    this.loadProjects();
    this.showExpiredProject=false;
  }
  selectProject(project: project) {
    this.showSelectedProject = [project]; //wrappiing project into array;
    this.allProjects = this.showSelectedProject;
  }
  onBellClick() {
    this.showToast = true;
    this.showBell = false;
  }
  savePdf() {
    console.log("saving pdf");
    if (this.allProjects.length === 0) {
      this.showError('No Projects to download.');
      return;
    }
    const doc = new jsPDF('landscape');
    const head = [['ProjectName', 'client', 'Description', 'RequiredSkill', 'startDate', 'endDate', 'EmployeeCount']];
    console.log(this.Employees);
    console.log(this.Employees);
    console.log(this.Employees.map(emp => emp.project?.id));

    const body = this.allProjects.map(project => {
      const employeeCount = this.Employees.filter(emp => emp.project?.id === project.id).length;
      return [
        project.name ?? '',
        project.client ?? '',
        project.description ?? '',
        project.requiredSkill ?? '',
        project.startDate ?? '',
        project.endDate ?? '',
        employeeCount.toString(),
      ];
    });


    doc.setFontSize(18);
    doc.text('Project List', 14, 22);
    doc.setFontSize(11);
    doc.text(`Generated on: ${new Date().toLocaleDateString()}`, 14, 30);


    autoTable(doc, {
      head: head,
      body: body,
      startY: 40,
      theme: 'striped',
      headStyles: {
        fillColor: [40, 53, 147]
      },
      styles: {
        cellPadding: 3,
        fontSize: 6,
        valign: 'middle',
        halign: 'center'
      },
    });

    doc.save('project-list.pdf');
    this.showSuccess('Project list downloaded as PDF!');

  }
  downloadExcel() {
    console.log('downloading excel')
    if (this.allProjects.length === 0) {
      this.showError('No projects to download.');
      return;
    }

    const dataForExcel = this.allProjects.map(project => {
      const EmployeeCount = this.Employees.filter(emp => emp.project?.id === project.id).length;
      return {
        'Project Name': project.name,
        'Client Name': project.client,
        'Description': project.description,
        'Required Skills': project.requiredSkill,
        'Project Start Date': project.startDate,
        'Project End Date': project.endDate,
        'Count of Employees': EmployeeCount,
      };
    });

    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(dataForExcel);

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Project List');

    XLSX.writeFile(wb, 'project-list.xlsx');

    this.showSuccess('project list downloaded as Excel!');
  }
}
