package in.playground.model;

import java.util.List;

/**
 * this is L&D for employee in a company.
 */
public record L_AND_D(String empId, Employee employee, List<Course> course) {
}
