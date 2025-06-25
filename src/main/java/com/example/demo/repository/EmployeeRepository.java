package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    // Trouver par prénom
    Employee findByFirstName(String firstName);

    // Trouver tous par nom
    List<Employee> findByName(String name);

    // Trouver tous qui contiennent "Andry" dans le nom
    List<Employee> findByNameContaining(String partName);

    // Trouver tous par email ET nom
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    // Trouver tous dont l'email finit par "@mail.com"
    List<Employee> findByFirstNameEndingWith(String suffix);

    @Query("SELECT e FROM Employee e WHERE e.firstName LIKE %:prenom")
    List<Employee> findByPrenom(@Param("prenom") String prenom);

    // Pour du SQL natif :
    @Query(value = "SELECT * FROM employee WHERE name LIKE %?1%", nativeQuery = true)
    List<Employee> searchByName(String partName);
}

// Résumé des méthodes courantes
// Méthode Repository	    Usage
// findAll()	            Lire tous
// findById(id)	            Lire par id
// save(entity)	            Créer / mettre à jour
// deleteById(id)	        Supprimer par id
// findByXxx(...)	        Lire selon un champ
// findByXxxAndYyy(...)	    Lire selon plusieurs champs
// findByXxxContaining()	LIKE
// findAll(Pageable)	    Lecture paginée

// List<Employee> findByCreatedAtAfter(LocalDateTime dt);
// List<Employee> findByCreatedAtBefore(LocalDateTime dt);
// List<Employee> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

// Récupérer tous les employés nés après le 1er janvier 2000
// List<Employee> jeunes = employeeRepository.findByDateOfBirthAfter(LocalDate.of(2000, 1, 1));

// Récupérer tous les employés créés aujourd'hui
// List<Employee> today = employeeRepository.findByCreatedAtBetween(
//     LocalDateTime.now().with(LocalTime.MIN),
//     LocalDateTime.now().with(LocalTime.MAX)
// );


// Résumé des expressions avec dates
// Méthode Repository	                    Résultat
// findByDateOfBirthAfter(LocalDate d)	    Après la date
// findByDateOfBirthBefore(LocalDate d)	    Avant la date
// findByDateOfBirthBetween(d1, d2)	        Entre deux dates
// findByCreatedAtBetween(dt1, dt2)	        Entre deux timestamps
// findByDateOfBirth(LocalDate d)	        Exactement à la date

