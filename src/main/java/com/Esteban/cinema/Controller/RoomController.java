package com.Esteban.cinema.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Esteban.cinema.Model.Rooms;
import com.Esteban.cinema.Repository.RoomRepository;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping()
    public List<Rooms> getAllRooms() {
        return roomRepository.findAll();
    }
}
