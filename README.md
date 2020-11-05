# NextBasin
Task manager with a distributed role system - Employee, Manager, Admin. 
 
#### Functionality:
- Manager can attach and detach employees and assign tasks to them in a certain time interval, also manager can update and confirm tasks. 
- Employee has a task sheet in his workspace, employee can mark the task as completed and wait for the manager's confirmation.  
- Admin has crud functionality (update employee and manager profile, delete employees and managers, registration new manager and, 
has access to all information about employees, managers and tasks assigned to them or by them)
- Password recovery function implemented
- Implemented other information functions (List of managers for employees, list of administration for all users (including those who are not authorized))  

#### Technologies:
- Data Base - postrgeSQL
- Spring Boot (Security, MVC, AOP, Data, Email)
- Hibernate 
- Lombok
- View - Thymeleaf, Bootstrap, JS
- Test - JUnit, Mockito
