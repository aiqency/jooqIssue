package com.example.app.endpoints

import com.example.app.configurations.GraphQlQuery
import com.example.app.service.UsersService
import org.springframework.beans.factory.annotation.Autowired

@GraphQlQuery
class UsersGraphQl @Autowired constructor(
        val userService: UsersService,
) {

    fun getUsersWithRoles() = userService.getUsersWithRoles()

    fun getUserPersonalRoles() = userService.getUserPersonalRoles()
}
