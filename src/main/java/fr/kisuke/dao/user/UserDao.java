package fr.kisuke.dao.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import fr.kisuke.dao.Dao;
import fr.kisuke.entity.Users;


public interface UserDao extends Dao<Users, Long>, UserDetailsService
{

	Users findByName(String name);

}