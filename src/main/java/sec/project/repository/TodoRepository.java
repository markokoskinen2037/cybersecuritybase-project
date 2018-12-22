package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
