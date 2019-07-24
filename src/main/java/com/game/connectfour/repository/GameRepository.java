package com.game.connectfour.repository;


import com.game.connectfour.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends MongoRepository<Game, UUID> {



}