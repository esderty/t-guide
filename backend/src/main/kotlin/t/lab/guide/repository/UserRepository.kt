package t.lab.guide.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import t.lab.guide.domain.User
import t.lab.guide.repository.view.user.AdminUserShortView
import t.lab.guide.repository.view.user.LoginUserView
import t.lab.guide.repository.view.user.ProfileUserView

interface UserRepository : CrudRepository<User, Long> {
    @Query(
        """
        select u.id, u.username, p.password_hash, u.role, u.is_active
        from "user" u
        join password p on p.user_id = u.id
        where u.username = :username
    """,
    )
    fun findLoginViewByUsername(username: String): LoginUserView?

    @Query(
        """
        select u.id, u.username, p.password_hash, u.role, u.is_active
        from "user" u
        join password p on p.user_id = u.id
        where u.id = :userId
    """,
    )
    fun findLoginViewByUserId(userId: Long): LoginUserView?

    @Query(
        """
        select u.id, u.username, u.email, u.name, u.lang, u.role
        from "user" u
        where u.id = :userId
    """,
    )
    fun findUserProfileById(userId: Long): ProfileUserView?

    @Query(
        """
      select u.id, u.username, u.email, u.role, u.is_active, u.created_at
      from "user" u
      where (:search is null or u.username ilike '%' || :search || '%'
                            or u.email    ilike '%' || :search || '%')
      """,
    )
    fun findUsersPage(
        @Param("search") search: String?,
        pageable: Pageable,
    ): List<AdminUserShortView>

    @Query(
        """
      select count(*) from "user" u
      where (:search is null or u.username ilike '%' || :search || '%'
                            or u.email    ilike '%' || :search || '%')
      """,
    )
    fun countUsersInPage(
        @Param("search") search: String?,
    ): Long

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
