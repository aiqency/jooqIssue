package com.example.app.service

import com.example.app.dao.UserDao
import com.example.app.generated.jooq.Tables
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UsersService @Autowired constructor(
        val userDao: UserDao,
) {

    /**
     * @return All the users with their roles excepting the user requesting it.
     */
    fun getUsersWithRoles() = userDao.findAllUserRoles(Tables.USERS.USERNAME.notEqual("admin"))

    /**
     * @return Current user roles
     */
    fun getUserPersonalRoles() = userDao.findAllUserRoles(Tables.USERS.USERNAME.eq("admin")).firstOrNull()

}
