package com.example.app.dao

import com.example.app.generated.jooq.Tables.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class IdName(val id: Int, val name: String)
data class UserRoles(val id: Int, val name: String, val elements: List<UserElement>?)
data class UserElement(val id: Int, val name: String)
data class UserAction(val id: Int, val name: String)

@Service
class UserDao @Autowired constructor(
        private val ctx: DSLContext
) {

    private val log = LoggerFactory.getLogger(javaClass)


    /**
     * @return [UserRoles] The list of all users with their roles (user 1-* elements 1-* actions)
     */
    fun findAllUserRoles(condition: Condition? = null): List<UserRoles> {
        val userRoles = ctx.select(
                USERS.ID.`as`("id"),
                USERS.USERNAME.`as`("name"),
                field(
                        select(
                                jsonArrayAgg(
                                        jsonObject(
                                                jsonEntry("id", USER_ELEMENT.ID),
                                                jsonEntry("name", USER_ELEMENT.ELEMENTS),
//                                                jsonEntry("actions",
//                                                        field(select(jsonArrayAgg(jsonObject(
//                                                                jsonEntry("id", USER_ACTION.ID),
//                                                                jsonEntry("name", USER_ACTION.ACTIONS)
//                                                        ))).from(USER_ACTION)
//                                                                .where(USER_ACTION.ID
//                                                                        .`in`(
//                                                                                select(USER_ACTION.ID)
//                                                                                        .from(USER_ELEMENT_ACTION)
//                                                                                        .where(USER_ELEMENT_ACTION.USER_ID.eq(USERS.ID))
//                                                                                        .and(USER_ELEMENT_ACTION.USER_ELEMENT_ID.eq(USER_ELEMENT.ID))
//                                                                        )
//                                                                )
//                                                        )
//                                                )
                                        )
                                )
                        ).from(USER_ELEMENT).where(USER_ELEMENT.ID
                                .`in`(
                                        select(USER_ELEMENT_ACTION.USER_ELEMENT_ID)
                                                .from(USER_ELEMENT_ACTION)
                                                .where(USER_ELEMENT_ACTION.USER_ID.eq(USERS.ID))
                                )
                        )
                ).`as`("elements")
        ).from(USERS)
                .apply { if (condition != null) where(condition) }
                .fetchInto(UserRoles::class.java)

        // TODO here is the issue
        userRoles.forEach { userRole ->
            log.info("userRole $userRole")
            userRole.elements!!.forEach { element ->
                log.info("element $element")
            }
        }

        return userRoles
    }
}
