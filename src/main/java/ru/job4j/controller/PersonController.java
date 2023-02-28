package ru.job4j.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final BCryptPasswordEncoder encoder;

    public PersonController(final PersonService personService, BCryptPasswordEncoder encoder) {
        this.personService = personService;
        this.encoder = encoder;
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return personService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.personService.getById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return new ResponseEntity<>(
                this.personService.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        this.personService.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.personService.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        personService.save(person);
        return ResponseEntity.ok().build();
    }
}
