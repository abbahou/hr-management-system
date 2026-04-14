import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {

  // Menu State
  isEmployeeMenuOpen = false;
  isPayrollMenuOpen = false;

  toggleEmployeeMenu() {
    this.isEmployeeMenuOpen = !this.isEmployeeMenuOpen;
  }

  togglePayrollMenu() {
    this.isPayrollMenuOpen = !this.isPayrollMenuOpen;
  }

  // Dashboard Metrics
  metrics = [
    { label: 'Total Employees', value: '1,452', change: '+24 This month', icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z' },
    { label: 'On Leave Today', value: '38', change: '-5 From yesterday', icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z' },
    { label: 'Open Roles', value: '14', change: '3 New this week', icon: 'M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z' },
    { label: 'Pending Approvals', value: '8', change: 'Needs action', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4' }
  ];

  // Recent Activity Feed
  activities = [
    { user: 'Sarah Jenkins', action: 'submitted a new leave request', time: '10 mins ago', initial: 'SJ', bgColor: 'bg-blue-100', textColor: 'text-blue-600' },
    { user: 'Michael Chen', action: 'updated his banking details', time: '1 hour ago', initial: 'MC', bgColor: 'bg-[#00de9a]/20', textColor: 'text-[#00de9a]' },
    { user: 'HR System', action: 'generated mid-month payroll report', time: '3 hours ago', initial: 'HR', bgColor: 'bg-purple-100', textColor: 'text-purple-600' },
    { user: 'Amanda Garcia', action: 'accepted the offer letter', time: 'Yesterday', initial: 'AG', bgColor: 'bg-amber-100', textColor: 'text-amber-600' },
    { user: 'David Smith', action: 'completed security onboarding', time: 'Yesterday', initial: 'DS', bgColor: 'bg-gray-100', textColor: 'text-gray-600' }
  ];

  constructor(private router: Router) { }

  logout() {
    this.router.navigate(['/login']);
  }
}
