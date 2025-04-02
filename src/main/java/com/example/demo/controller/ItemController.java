package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.service.ItemService;
import com.example.demo.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private RateLimitService rateLimit;

    @GetMapping
    public ResponseEntity<?> getAllItems() {
        String key = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!rateLimit.tryConsume(key)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Слишком много запросов, попробуйте позже");
        }

        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/exact")
    public ResponseEntity<List<Item>> searchExact(@RequestParam String title) {
        List<Item> items = itemService.findItemsByName(title);
        return items.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> search(@RequestParam String title) {
        List<Item> items = itemService.searchItemsByName(title);
        return items.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        Collection<? extends GrantedAuthority> roles = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities();

        if (!roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission");
        }

        return ResponseEntity.ok(itemService.saveItem(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> changeItem(@PathVariable Long id, @RequestBody Item item) {
        if (!itemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        item.setId(id);
        return ResponseEntity.ok(itemService.saveItem(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        if (!itemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemService.deleteItem(id);
        return ResponseEntity.ok("Товар удален");
    }
}
