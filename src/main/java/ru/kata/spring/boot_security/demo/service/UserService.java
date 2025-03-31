package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Transactional
    public List<User> getAllUsers() {
        return em
                .createQuery("select u from User u", User.class)
                .getResultList();
    }

    @Transactional
    public User getUser(int id) {
        return em.find(User.class, id);
    }

    @Transactional
    public void saveUser(User user) {
        em.persist(user);
    }

    @Transactional
    public void updateUser(User user) {
        getUser(user.getId());
        em.merge(user);
    }

    @Transactional
    public void deleteUser(int id) {
        User user = getUser(id);
        em.remove(user);
    }
}
