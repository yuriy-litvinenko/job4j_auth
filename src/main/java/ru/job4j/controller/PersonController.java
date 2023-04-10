package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final PersonService personService;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public PersonController(final PersonService personService, BCryptPasswordEncoder encoder,
                            ObjectMapper objectMapper) {
        this.personService = personService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return personService.getAll();
    }

    @GetMapping("/find/all")
    public ResponseEntity<String> findAll2() {
        var body = personService.getAll().toString();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Job4jPersonHeader", "job4j")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(body.length())
                .body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return new ResponseEntity<>(this.personService.getById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Account is not found. Please, check requisites."
        )), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Person> findById2(@PathVariable int id) {
        return ResponseEntity.of(this.personService.getById(id));
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
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("The password must be at least 6 characters long");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        personService.save(person);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<Person> patchDTO(@RequestBody PersonDTO personDTO, @PathVariable int id) {
        Person person = this.personService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        person.setPassword(personDTO.getPassword());
        return new ResponseEntity<>(
                this.personService.save(person),
                HttpStatus.OK
        );
    }

    @PatchMapping("/patch")
    public ResponseEntity<Person> patch(@RequestBody Person person) throws InvocationTargetException,
            IllegalAccessException {
        var current = personService.getById(person.getId());
        if (current.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var methods = current.get().getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method : methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Impossible invoke set method from object : " + current + ", Check set and get pairs.");
                }
                var newValue = getMethod.invoke(person);
                if (newValue != null) {
                    setMethod.invoke(current.get(), newValue);
                }
            }
        }
        return new ResponseEntity<>(
                this.personService.save(current.get()),
                HttpStatus.OK
        );
    }
}
