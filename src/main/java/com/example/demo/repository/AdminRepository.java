package com.example.demo.repository;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.Admin;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    Admin findByUsernameAndPassword(String username, String password);
}
