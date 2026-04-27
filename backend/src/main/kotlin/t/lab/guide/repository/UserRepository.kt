package t.lab.guide.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.User
import t.lab.guide.dto.admin.user.AdminUserShortItem
import t.lab.guide.repository.view.LoginUserView
import t.lab.guide.repository.view.ProfileUserView

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
      order by
          case when :sortBy = 'username'  then u.username  end,
          case when :sortBy = 'email'     then u.email     end,
          case when :sortBy = 'createdAt' then u.created_at end,
          u.id
      offset :offset limit :limit
      """,
    )
    fun findUsersPage(
        search: String?,
        sortBy: String,
        offset: Long,
        limit: Int,
    ): List<AdminUserShortItem>

    @Query(
        """
      select count(*) from "user" u
      where (:search is null or u.username ilike '%' || :search || '%'
                            or u.email    ilike '%' || :search || '%'
                            or u.name     ilike '%' || :search || '%')
      """,
    )
    fun countUsers(search: String?): Long

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
