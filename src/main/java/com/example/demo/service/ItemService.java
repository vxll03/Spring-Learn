package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }

    public List<Item> findItemsByName(String name) {
        return itemRepository.findByTitle(name);
    }

    public List<Item> searchItemsByName(String namePart) {
        return itemRepository.findByTitleContainingIgnoreCase(namePart);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
