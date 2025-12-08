package com.gl.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gl.model.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PersonRepository {

    private final Map<String, Person> personMap = new HashMap<>();

    public Mono<Person> findById(String id) {
        return Mono.justOrEmpty(personMap.get(id));
    }

    public Flux<Person> findAll() {
        return Flux.fromIterable(personMap.values());
    }

    public Mono<Person> save(Person person) {
        personMap.put(person.getId(), person);
        return Mono.just(person);
    }

    public Mono<Person> update(String id, Person person) {
        if (personMap.containsKey(id)) {
            personMap.put(id, person);
            return Mono.just(person);
        }
        return Mono.empty();
    }

    public Mono<Void> delete(String id) {
        personMap.remove(id);
        return Mono.empty();
    }
}

