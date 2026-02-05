package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
}
