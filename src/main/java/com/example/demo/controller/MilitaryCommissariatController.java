package com.example.demo.controller;

import com.example.demo.model.MilitaryCommissariat;
import com.example.demo.service.MilitaryCommissariatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissariats")
public class MilitaryCommissariatController {

    @Autowired
    MilitaryCommissariatService commissariatService;

    @GetMapping
    public ResponseEntity<List<MilitaryCommissariat>> getAllCommissariats() {
        List<MilitaryCommissariat> commissariats = commissariatService.getAllCommissariats();
        return ResponseEntity.ok(commissariats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MilitaryCommissariat> getCommissariatById(@PathVariable Long id) {
        MilitaryCommissariat commissariat = commissariatService.getCommissariatById(id)
                .orElseThrow();
        return ResponseEntity.ok(commissariat);
    }

    @PostMapping
    public ResponseEntity<MilitaryCommissariat> createCommissariat(@RequestBody MilitaryCommissariat commissariat) {
        commissariatService.saveCommissariat(commissariat);
        return ResponseEntity.ok(commissariat);
    }

    @PostMapping("/{id}")
    public ResponseEntity<MilitaryCommissariat> changeCommissariat(@PathVariable Long id, @RequestBody MilitaryCommissariat commissariat) {
        commissariat.setId(id);
        commissariatService.saveCommissariat(commissariat);
        return ResponseEntity.ok(commissariat);
    }

    @DeleteMapping("/{id}")
    public void deleteCommissariat(@PathVariable Long id) {
        commissariatService.deleteCommissariat(id);
    }
}
