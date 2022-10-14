package xyz.haff.quoteapi.data.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import xyz.haff.quoteapi.data.entity.UserEntity

interface UserRepository : ReactiveMongoRepository<UserEntity, String>