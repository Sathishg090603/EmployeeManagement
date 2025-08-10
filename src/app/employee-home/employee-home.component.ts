import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderComponent } from '../shared/header/header.component';

@Component({
  selector: 'app-employee-home',
  imports: [HeaderComponent],
  standalone:true,
  templateUrl: './employee-home.component.html',
  styleUrl: './employee-home.component.css'
})
export class EmployeeHomeComponent implements OnInit {

  username:string | null='';
  constructor(private router:Router)
  {

  }
  ngOnInit()
  {
    if(!sessionStorage.getItem("USER_KEY"))
    {
      this.router.navigate(['/login'])
    }
    else
    {
     this.username=sessionStorage.getItem("USER_KEY");
    }
  }
}
