package com.example.demo.service;

import com.example.demo.model.MilitaryCommissariat;
import com.example.demo.repository.MilitaryCommissariatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MilitaryCommissariatService {

    @Autowired
    MilitaryCommissariatRepository repository;


    public List<MilitaryCommissariat> getAllCommissariats() {
        return repository.findAll();
    }

    public Optional<MilitaryCommissariat> getCommissariatById(Long id) {
        return repository.findById(id);
    }

    public void saveCommissariat(MilitaryCommissariat commissariat) {
        repository.save(commissariat);
    }

    public void deleteCommissariat(Long id) {
        repository.deleteById(id);
    }
}
