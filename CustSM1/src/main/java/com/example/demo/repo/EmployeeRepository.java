package com.example.demo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Employee;

/**
 * EmployeeRepository Interface
 * This interface is a repository for managing Employee entities.
 * 
 * @author Manjunath.AS, Nitisha. S, Srihari .P
 */

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
	
	Employee findByEmployeeFirstName(String employeeFirstName);
	
	Optional<Employee> findByEmployeeEmail(String username);
	
	Employee findByEmployeePhNo(String employeePhNo);
	
	List<Employee> findByEmployeeManagerId(int employeeManagerId);
	
	List<Employee> findByEmployeeDesignation(String employeeDesignation);
	
	List<Employee> findByEmployeeDept(String employeeDept);

	long countByEmployeeEmailAndEmployeePassword(String employeeEmail, String employeePassword);
	
	// Called in EmployeeRepository for filtering employees by their departments
	@Query("SELECT e.employeeId FROM Employee e WHERE e.employeeDept = :department")
	List<Integer> findEmployeeIdsByDepartment(@Param("department") String department);

	
	@Query("SELECT e.employeeId FROM Employee e WHERE e.employeeDept = ?1 ORDER BY e.employeeId ASC")
    List<Integer> findEmployeesByDomain(String domain);
	
	// Custom query to count all employees
    @Query("SELECT COUNT(e) FROM Employee e where employeeDesignation='employee'")
    long countAllEmployees();
 
    // Custom query to count managers (employees with a non-null manager ID)
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employeeDesignation='manager'")
    long countManagers();
 
    // Custom query to count admins (employees with the designation "Admin")
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employeeDesignation = 'Admin'")
    long countAdmins();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employeeManagerId = :managerId")
    long countEmployeesUnderManager(@Param("managerId") Integer managerId);
 
 
}
